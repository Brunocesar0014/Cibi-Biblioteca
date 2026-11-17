package com.cibi.controller.pages;

import com.cibi.Service.AlunoService;
import com.cibi.Service.EmprestimoService;
import com.cibi.Service.EmprestimoService.Regras;
import com.cibi.model.Aluno;
import com.cibi.model.Livro;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javafx.animation.PauseTransition;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextInputControl;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class NovoEmprestimoController {
    private static final int DIGITOS_MATRICULA_CRACHA = 15;
    private static final int PAUSA_LEITURA_MS = 300;
    private static final long PRAZO_SUFIXO_NANOS = TimeUnit.SECONDS.toNanos(1);

    @FXML private StackPane painelLeitura;
    @FXML private HBox entradaManual;
    @FXML private TextField matricula;
    @FXML private Label tituloLeitura;
    @FXML private Label instrucaoLeitura;
    @FXML private Label estadoLeitura;
    @FXML private Label dicaIdentificacao;
    @FXML private Button alternarEntrada;
    @FXML private Label erroAluno;
    @FXML private TextField buscaLivro;
    @FXML private ListView<Livro> livros;
    @FXML private Label dicaBusca;
    @FXML private Label resumoAluno;
    @FXML private Region iconeAluno;
    @FXML private ImageView fotoAluno;
    @FXML private Label resumoLivro;
    @FXML private Label resumoDevolucao;
    @FXML private Label erroEmprestimo;
    @FXML private Button confirmar;

    private final EmprestimoService service = new EmprestimoService();
    private final StringBuilder codigoLeitura = new StringBuilder();
    private final PauseTransition pausaLeitura = new PauseTransition(Duration.millis(PAUSA_LEITURA_MS));
    private final EventHandler<KeyEvent> capturaLeitura = this::capturarLeitura;
    private long ultimaTeclaLeitura;
    private boolean leituraEnviada;
    private KeyCode sufixoPressionado;
    private Aluno aluno;
    private Regras regras;
    private Task<Identificacao> consulta;
    private Runnable aoCancelar;
    private Runnable aoConfirmar;
    private String colaborador;

    private record Identificacao(Aluno aluno, Regras regras, Image foto) {}

    @FXML
    private void initialize() {
        FotoAluno.configurar(fotoAluno, iconeAluno, 46);
        entradaManual.managedProperty().bind(entradaManual.visibleProperty());
        livros.managedProperty().bind(livros.visibleProperty());
        erroAluno.managedProperty().bind(erroAluno.visibleProperty());
        erroEmprestimo.managedProperty().bind(erroEmprestimo.visibleProperty());
        matricula.setTextFormatter(matriculaNumerica());
        pausaLeitura.setOnFinished(evento -> {
            if (codigoLeitura.length() >= DIGITOS_MATRICULA_CRACHA) {
                // Um leitor com sufixo ainda pode enviar Enter depois da busca automática.
                leituraEnviada = true;
                buscarAluno(codigoLeitura.toString());
            } else {
                limparLeitura();
            }
        });
        painelLeitura.sceneProperty().addListener((obs, antes, depois) -> {
            if (antes != null) {
                antes.removeEventFilter(KeyEvent.ANY, capturaLeitura);
                limparLeitura();
                sufixoPressionado = null;
            }
            if (depois != null) {
                depois.addEventFilter(KeyEvent.ANY, capturaLeitura);
            }
        });
        buscaLivro.textProperty().addListener((obs, antes, depois) -> atualizarLivros());
        livros.getSelectionModel().selectedItemProperty().addListener((obs, antes, depois) -> atualizarResumo());
        livros.setCellFactory(lista -> new ListCell<>() {
            @Override
            protected void updateItem(Livro livro, boolean vazio) {
                super.updateItem(livro, vazio);
                if (vazio || livro == null) {
                    setText(null);
                } else {
                    String autor = livro.getAutor();
                    setText(livro.getTitulo() + (autor == null || autor.isBlank() ? "" : "  ·  " + autor)
                            + "  ·  Disponíveis: " + livro.getQuantidadeDisponivel());
                }
            }
        });
    }

    public void abrir(String colaborador, Runnable aoCancelar, Runnable aoConfirmar) {
        this.colaborador = colaborador;
        this.aoCancelar = aoCancelar;
        this.aoConfirmar = aoConfirmar;
        limparIdentificacao();
    }

    private void limparIdentificacao() {
        cancelarConsulta();
        limparLeitura();
        aluno = null;
        regras = null;
        fotoAluno.setImage(null);
        matricula.clear();
        entradaManual.setVisible(false);
        erroAluno.setVisible(false);
        tituloLeitura.setText("Leia o QR Code do crachá");
        instrucaoLeitura.setText("Apresente o crachá ao leitor. A identificação é automática.");
        estadoLeitura.setText("●  Aguardando leitura");
        alternarEntrada.setText("Digitar matrícula  ›");
        dicaIdentificacao.setText("Não conseguiu ler o crachá?");
        buscaLivro.clear();
        atualizarLivros();
        atualizarResumo();
    }

    @FXML
    private void alternarEntrada() {
        if (aluno != null) {
            limparIdentificacao();
            return;
        }
        boolean manual = !entradaManual.isVisible();
        entradaManual.setVisible(manual);
        limparLeitura();
        erroAluno.setVisible(false);
        alternarEntrada.setText(manual ? "Usar leitor  ›" : "Digitar matrícula  ›");
        dicaIdentificacao.setText(manual ? "Prefere usar o crachá?" : "Não conseguiu ler o crachá?");
        if (manual) matricula.requestFocus();
    }

    @FXML private void buscarMatricula() { buscarAluno(matricula.getText()); }

    private void capturarLeitura(KeyEvent evento) {
        boolean aguardandoLeitura = aluno == null && consulta == null && !entradaManual.isVisible();
        if (consumirSufixo(evento, aguardandoLeitura)) return;

        if (evento.getEventType() != KeyEvent.KEY_TYPED || evento.isShortcutDown() || evento.isAltDown()) return;
        String caracteres = evento.getCharacter();
        if (caracteres.isEmpty() || caracteres.codePoints().anyMatch(Character::isISOControl)) return;

        long agora = System.nanoTime();
        if (leituraEnviada || agora - ultimaTeclaLeitura >= TimeUnit.MILLISECONDS.toNanos(PAUSA_LEITURA_MS)) {
            limparLeitura();
        }
        if (!aguardandoLeitura && (evento.getTarget() instanceof TextInputControl || !caracteres.matches("[0-9]+"))) {
            limparLeitura();
            return;
        }
        if (codigoLeitura.isEmpty() && caracteres.isBlank()) return;

        codigoLeitura.append(caracteres);
        ultimaTeclaLeitura = agora;
        if (aguardandoLeitura) {
            pausaLeitura.playFromStart();
            evento.consume();
        }
    }

    private boolean consumirSufixo(KeyEvent evento, boolean aguardandoLeitura) {
        long agora = System.nanoTime();
        if (agora - ultimaTeclaLeitura > PRAZO_SUFIXO_NANOS) {
            sufixoPressionado = null;
            return false;
        }
        KeyCode tecla = evento.getCode();
        if (tecla == sufixoPressionado) {
            if (evento.getEventType() == KeyEvent.KEY_RELEASED) sufixoPressionado = null;
            ultimaTeclaLeitura = agora;
            evento.consume();
            return true;
        }
        if (sufixoPressionado != null && evento.getEventType() == KeyEvent.KEY_TYPED
                && ("\r".equals(evento.getCharacter()) || "\n".equals(evento.getCharacter())
                    || "\t".equals(evento.getCharacter()))) {
            evento.consume();
            return true;
        }
        if (evento.getEventType() != KeyEvent.KEY_PRESSED || (tecla != KeyCode.ENTER && tecla != KeyCode.TAB)
                || evento.isShiftDown() || evento.isShortcutDown() || evento.isAltDown()
                || codigoLeitura.isEmpty()
                || (!aguardandoLeitura && codigoLeitura.length() < DIGITOS_MATRICULA_CRACHA)) return false;

        boolean buscar = aguardandoLeitura && !leituraEnviada;
        String texto = codigoLeitura.toString();
        limparLeitura();
        sufixoPressionado = tecla;
        ultimaTeclaLeitura = agora;
        evento.consume();
        if (buscar) buscarAluno(texto);
        return true;
    }

    private void limparLeitura() {
        pausaLeitura.stop();
        codigoLeitura.setLength(0);
        leituraEnviada = false;
    }

    private void buscarAluno(String texto) {
        if (aluno != null || consulta != null) return;
        if (!texto.matches("[0-9]{1,19}")) {
            falhaIdentificacao("Informe uma matrícula válida.");
            return;
        }
        long numero;
        try {
            numero = Long.parseLong(texto);
            if (numero <= 0) throw new NumberFormatException();
        } catch (NumberFormatException erro) {
            falhaIdentificacao("Informe uma matrícula válida.");
            return;
        }

        Task<Identificacao> tarefa = new Task<>() {
            @Override protected Identificacao call() {
                Aluno encontrado = AlunoService.BuscarAlunoPorMaticula(numero);
                return new Identificacao(encontrado, service.carregarRegras(), carregarFoto(encontrado));
            }
        };
        consulta = tarefa;
        erroAluno.setVisible(false);
        estadoLeitura.setText("●  Buscando aluno");
        entradaManual.setDisable(true);
        alternarEntrada.setDisable(true);
        tarefa.setOnSucceeded(evento -> {
            if (consulta != tarefa) return;
            finalizarConsulta();
            aluno = tarefa.getValue().aluno();
            regras = tarefa.getValue().regras();
            fotoAluno.setImage(tarefa.getValue().foto());
            entradaManual.setVisible(false);
            tituloLeitura.setText(aluno.getNome());
            instrucaoLeitura.setText("Matrícula " + aluno.getMatricula() + "  ·  Turma " + aluno.getTurma());
            estadoLeitura.setText("●  Aluno identificado");
            dicaIdentificacao.setText("");
            alternarEntrada.setText("Trocar aluno  ›");
            atualizarLivros();
            atualizarResumo();
            buscaLivro.requestFocus();
        });
        tarefa.setOnFailed(evento -> {
            if (consulta != tarefa) return;
            finalizarConsulta();
            Throwable erro = tarefa.getException();
            falhaIdentificacao(erro.getCause() == null && erro instanceof IllegalStateException
                    ? "Aluno não encontrado. Confira a matrícula."
                    : "Não foi possível consultar o cadastro de alunos.");
        });
        Thread worker = new Thread(tarefa, "cibi-identificar-aluno");
        worker.setDaemon(true);
        worker.start();
    }

    private Image carregarFoto(Aluno encontrado) {
        try {
            return FotoAluno.decodificar(AlunoService.buscarFotoCida(encontrado));
        } catch (IllegalStateException erro) {
            return null;
        }
    }

    private void falhaIdentificacao(String mensagem) {
        erroAluno.setText(mensagem);
        erroAluno.setVisible(true);
        estadoLeitura.setText("●  Aguardando leitura");
        if (entradaManual.isVisible()) {
            matricula.requestFocus();
            matricula.selectAll();
        }
    }

    private void finalizarConsulta() {
        consulta = null;
        entradaManual.setDisable(false);
        alternarEntrada.setDisable(false);
    }

    private void cancelarConsulta() {
        if (consulta != null) {
            consulta.cancel();
            finalizarConsulta();
        }
    }

    private void atualizarLivros() {
        boolean identificado = aluno != null;
        buscaLivro.setDisable(!identificado);
        livros.getSelectionModel().clearSelection();
        livros.getItems().setAll(identificado ? service.buscarLivrosDisponiveis(buscaLivro.getText()) : List.of());
        livros.setVisible(!livros.getItems().isEmpty());
        livros.setPrefHeight(Math.min(4, livros.getItems().size()) * livros.getFixedCellSize() + 2);
        atualizarDicaBusca();
    }

    private void atualizarDicaBusca() {
        dicaBusca.setText(aluno == null ? "Identifique o aluno para liberar a busca."
                : livros.getItems().isEmpty() ? "Nenhum livro disponível para esta busca."
                : livros.getSelectionModel().isEmpty() ? "Selecione um livro disponível para continuar."
                : "Confira o resumo para confirmar o empréstimo.");
    }

    private void atualizarResumo() {
        Livro livro = livros.getSelectionModel().getSelectedItem();
        resumoAluno.setText(aluno == null ? "Aguardando identificação" : aluno.getNome());
        resumoLivro.setText(livro == null ? "Nenhum livro selecionado" : livro.getTitulo());
        resumoDevolucao.setText(livro == null ? "—" : EmprestimosController.formatarData(regras.vencimento(LocalDate.now())));
        confirmar.setDisable(aluno == null || livro == null);
        erroEmprestimo.setVisible(false);
        atualizarDicaBusca();
    }

    @FXML
    private void confirmar() {
        Livro livro = livros.getSelectionModel().getSelectedItem();
        if (aluno == null || livro == null) return;
        try {
            service.registrar(aluno, livro, colaborador, regras);
        } catch (IllegalStateException erro) {
            atualizarLivros();
            erroEmprestimo.setText(erro.getMessage());
            erroEmprestimo.setVisible(true);
            return;
        }
        aoConfirmar.run();
    }

    @FXML
    private void cancelar() {
        cancelarConsulta();
        aoCancelar.run();
    }

    private TextFormatter<String> matriculaNumerica() {
        return new TextFormatter<>(alteracao -> alteracao.getControlNewText().matches("[0-9]{0,19}") ? alteracao : null);
    }
}
