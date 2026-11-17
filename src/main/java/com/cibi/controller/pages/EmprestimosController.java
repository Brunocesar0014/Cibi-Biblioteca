package com.cibi.controller.pages;

import com.cibi.Service.AlunoService;
import com.cibi.Service.EmprestimoService;
import com.cibi.model.Emprestimo;
import com.cibi.model.Emprestimo.Status;
import com.cibi.util.TemaVisual;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.concurrent.Task;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class EmprestimosController {
    private static final int ITENS_POR_PAGINA = 5;
    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final String TODOS_STATUS = "Todos";
    private static final PseudoClass MENU_ABERTO = PseudoClass.getPseudoClass("showing");
    private static final FiltroColaborador TODOS_COLABORADORES = new FiltroColaborador(null);

    private record FiltroColaborador(String nome) {
        @Override public String toString() { return nome == null ? "Todos" : nome; }
    }

    private enum Periodo {
        TRINTA("Últimos 30 dias", 30), NOVENTA("Últimos 90 dias", 90), TODOS("Todo o período", 0);

        private final String rotulo;
        private final int dias;

        Periodo(String rotulo, int dias) {
            this.rotulo = rotulo;
            this.dias = dias;
        }

        @Override public String toString() { return rotulo; }
    }

    @FXML private StackPane raiz;
    @FXML private VBox listagem;
    @FXML private ScrollPane formulario;
    @FXML private NovoEmprestimoController formularioController;
    @FXML private TextField busca;
    @FXML private ComboBox<String> filtroStatus;
    @FXML private ComboBox<FiltroColaborador> filtroColaborador;
    @FXML private ComboBox<Periodo> filtroPeriodo;
    @FXML private TableView<Emprestimo> tabela;
    @FXML private TableColumn<Emprestimo, Emprestimo> colAluno;
    @FXML private TableColumn<Emprestimo, Emprestimo> colLivro;
    @FXML private TableColumn<Emprestimo, Emprestimo> colRetirada;
    @FXML private TableColumn<Emprestimo, Emprestimo> colPrevisao;
    @FXML private TableColumn<Emprestimo, Emprestimo> colStatus;
    @FXML private TableColumn<Emprestimo, Emprestimo> colAcoes;
    @FXML private Button anterior;
    @FXML private Button proximo;
    @FXML private HBox paginas;
    @FXML private Label resultados;
    private final EmprestimoService service = new EmprestimoService();
    private final ObservableMap<Long, Image> fotos = FXCollections.observableHashMap();
    private Set<Long> matriculasDaPagina = Set.of();
    private Task<Map<Long, Image>> consultaFotos;
    private List<Emprestimo> emprestimos = List.of();
    private String colaborador;
    private int pagina;
    private LocalDate hoje;

    @FXML
    private void initialize() {
        raiz.getChildren().setAll(listagem);
        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        configurarColuna(colAluno, this::celulaAluno);
        configurarColuna(colLivro, e -> duasLinhas(e.livro(), e.autor()));
        configurarColuna(colRetirada, e -> new Label(formatarData(e.retirada())));
        configurarColuna(colPrevisao, e -> e.devolvidoEm() == null
                ? new Label(formatarData(e.devolucaoPrevista()))
                : duasLinhas(formatarData(e.devolucaoPrevista()), "Devolvido em " + formatarData(e.devolvidoEm())));
        configurarColuna(colStatus, this::celulaStatus);
        configurarColuna(colAcoes, this::celulaAcoes);
        filtroStatus.getItems().add(TODOS_STATUS);
        Arrays.stream(Status.values()).map(Status::toString).forEach(filtroStatus.getItems()::add);
        filtroStatus.getSelectionModel().selectFirst();
        filtroPeriodo.getItems().setAll(Periodo.values());
        filtroPeriodo.setValue(Periodo.TRINTA);
        busca.textProperty().addListener((obs, antes, depois) -> filtrar());
        filtroStatus.valueProperty().addListener((obs, antes, depois) -> filtrar());
        filtroColaborador.valueProperty().addListener((obs, antes, depois) -> filtrar());
        filtroPeriodo.valueProperty().addListener((obs, antes, depois) -> filtrar());
    }

    public void atualizar(String colaborador) {
        this.colaborador = colaborador;
        carregarFotos(Set.of());
        recarregar();
    }

    private void recarregar() {
        emprestimos = service.listar();
        FiltroColaborador selecionado = filtroColaborador.getValue();
        List<FiltroColaborador> opcoes = new ArrayList<>();
        opcoes.add(TODOS_COLABORADORES);
        emprestimos.stream().map(Emprestimo::colaborador).distinct().sorted()
                .map(FiltroColaborador::new).forEach(opcoes::add);
        if (!filtroColaborador.getItems().equals(opcoes)) {
            filtroColaborador.getItems().setAll(opcoes);
            filtroColaborador.setValue(opcoes.contains(selecionado) ? selecionado : TODOS_COLABORADORES);
        }
        renderizarPagina();
    }

    private void filtrar() {
        pagina = 0;
        renderizarPagina();
    }

    private void renderizarPagina() {
        hoje = LocalDate.now();
        String termo = EmprestimoService.normalizar(busca.getText().strip());
        String status = filtroStatus.getValue();
        FiltroColaborador responsavel = filtroColaborador.getValue();
        Periodo periodo = filtroPeriodo.getValue();
        LocalDate inicio = hoje.minusDays(periodo.dias - 1L);
        List<Emprestimo> filtrados = emprestimos.stream()
                .filter(e -> EmprestimoService.normalizar(e.aluno()).contains(termo)
                        || Long.toString(e.matriculaAluno()).contains(termo)
                        || EmprestimoService.normalizar(e.livro()).contains(termo))
                .filter(e -> TODOS_STATUS.equals(status) || e.statusEm(hoje).toString().equals(status))
                .filter(e -> responsavel == null || TODOS_COLABORADORES.equals(responsavel)
                        || e.colaborador().equals(responsavel.nome()))
                .filter(e -> periodo == Periodo.TODOS || !e.retirada().isBefore(inicio))
                .toList();

        int total = filtrados.size();
        int totalPaginas = Math.max(1, (total + ITENS_POR_PAGINA - 1) / ITENS_POR_PAGINA);
        pagina = Math.clamp(pagina, 0, totalPaginas - 1);
        int de = pagina * ITENS_POR_PAGINA;
        int ate = Math.min(de + ITENS_POR_PAGINA, total);
        List<Emprestimo> emprestimosVisiveis = filtrados.subList(de, ate);
        tabela.getItems().setAll(emprestimosVisiveis);
        tabela.refresh();
        carregarFotos(emprestimosVisiveis.stream().map(Emprestimo::matriculaAluno).collect(Collectors.toSet()));
        resultados.setText(total == 0 ? "0 empréstimos" : "Mostrando %d–%d de %d %s"
                .formatted(de + 1, ate, total, total == 1 ? "empréstimo" : "empréstimos"));

        anterior.setDisable(pagina == 0);
        proximo.setDisable(pagina >= totalPaginas - 1);
        paginas.getChildren().clear();
        int primeira = Math.max(0, Math.min(pagina - 2, totalPaginas - 5));
        for (int i = primeira; i < Math.min(totalPaginas, primeira + 5); i++) {
            int destino = i;
            Button numero = new Button(Integer.toString(i + 1));
            numero.setAccessibleText("Página " + (i + 1));
            numero.getStyleClass().add(i == pagina ? "loan-page-current" : "loan-page-button");
            numero.setOnAction(evento -> {
                pagina = destino;
                renderizarPagina();
            });
            paginas.getChildren().add(numero);
        }
    }

    private void carregarFotos(Set<Long> matriculas) {
        if (matriculasDaPagina.equals(matriculas)) return;
        matriculasDaPagina = matriculas;
        if (consultaFotos != null) consultaFotos.cancel();
        consultaFotos = null;
        fotos.keySet().retainAll(matriculas);
        if (matriculas.isEmpty()) return;

        Task<Map<Long, Image>> tarefa = new Task<>() {
            @Override protected Map<Long, Image> call() {
                Map<Long, Image> imagens = new HashMap<>();
                AlunoService.buscarFotosCida(List.copyOf(matriculas)).forEach((matricula, jpeg) -> {
                    Image foto = FotoAluno.decodificar(jpeg);
                    if (foto != null) imagens.put(matricula, foto);
                });
                return imagens;
            }
        };
        consultaFotos = tarefa;
        tarefa.setOnSucceeded(evento -> {
            if (consultaFotos != tarefa) return;
            fotos.keySet().retainAll(tarefa.getValue().keySet());
            fotos.putAll(tarefa.getValue());
            consultaFotos = null;
        });
        tarefa.setOnFailed(evento -> {
            if (consultaFotos == tarefa) consultaFotos = null;
        });
        Thread worker = new Thread(tarefa, "cibi-fotos-emprestimos");
        worker.setDaemon(true);
        worker.start();
    }

    @FXML private void paginaAnterior() { pagina--; renderizarPagina(); }
    @FXML private void paginaProxima() { pagina++; renderizarPagina(); }

    @FXML
    private void novoEmprestimo() {
        raiz.getChildren().setAll(formulario);
        formularioController.abrir(colaborador, this::voltar, () -> {
            busca.clear();
            filtroStatus.getSelectionModel().selectFirst();
            filtroColaborador.getSelectionModel().selectFirst();
            pagina = 0;
            voltar();
        });
    }

    private void voltar() {
        raiz.getChildren().setAll(listagem);
        recarregar();
    }

    private void verDetalhes(Emprestimo emprestimo) {
        String texto = """
                Aluno: %s (%d)
                Livro: %s
                Colaborador: %s
                Retirada: %s
                Devolução prevista: %s
                Devolvido em: %s
                Status: %s
                """.formatted(emprestimo.aluno(), emprestimo.matriculaAluno(), emprestimo.livro(),
                emprestimo.colaborador(), formatarData(emprestimo.retirada()), formatarData(emprestimo.devolucaoPrevista()),
                formatarData(emprestimo.devolvidoEm()), emprestimo.statusEm(LocalDate.now()));
        Alert detalhes = alerta(Alert.AlertType.INFORMATION, "Detalhes do empréstimo", texto, ButtonType.OK);
        detalhes.showAndWait();
    }

    private void registrarDevolucao(Emprestimo emprestimo) {
        ButtonType registrar = new ButtonType("Registrar devolução", ButtonBar.ButtonData.OK_DONE);
        Alert confirmacao = alerta(Alert.AlertType.CONFIRMATION, "Registrar devolução",
                "Confirmar a devolução de “%s” por %s?".formatted(emprestimo.livro(), emprestimo.aluno()),
                ButtonType.CANCEL, registrar);
        if (confirmacao.showAndWait().orElse(ButtonType.CANCEL) != registrar) return;
        try {
            service.devolver(emprestimo.id());
            recarregar();
        } catch (IllegalStateException erro) {
            recarregar();
            alerta(Alert.AlertType.ERROR, "Falha ao registrar devolução", erro.getMessage(), ButtonType.OK).showAndWait();
        }
    }

    private Alert alerta(Alert.AlertType tipo, String titulo, String texto, ButtonType... botoes) {
        Alert alert = new Alert(tipo, texto, botoes);
        alert.initOwner(raiz.getScene().getWindow());
        TemaVisual.herdar(alert);
        alert.setTitle(titulo);
        alert.setHeaderText(titulo);
        return alert;
    }

    static String formatarData(LocalDate data) {
        return data == null ? "—" : data.format(DATA);
    }

    private VBox duasLinhas(String principal, String secundario) {
        Label titulo = new Label(principal);
        titulo.getStyleClass().add("loan-cell-title");
        Label subtitulo = new Label(secundario);
        subtitulo.getStyleClass().add("loan-muted");
        VBox textos = new VBox(2, titulo, subtitulo);
        textos.setAlignment(Pos.CENTER_LEFT);
        return textos;
    }

    private HBox celulaAluno(Emprestimo emprestimo) {
        String iniciais = Arrays.stream(emprestimo.aluno().strip().split("\\s+"))
                .filter(p -> !p.isEmpty()).limit(2).map(p -> p.substring(0, 1).toUpperCase(Locale.ROOT))
                .collect(Collectors.joining());
        Label alternativa = new Label(iniciais);
        alternativa.getStyleClass().add("loan-cell-title");
        ImageView foto = new ImageView();
        FotoAluno.configurar(foto, alternativa, 36);
        foto.imageProperty().bind(Bindings.valueAt(fotos, emprestimo.matriculaAluno()));
        StackPane avatar = new StackPane(alternativa, foto);
        avatar.getStyleClass().add("loan-avatar");
        HBox celula = new HBox(10, avatar, duasLinhas(emprestimo.aluno(), Long.toString(emprestimo.matriculaAluno())));
        celula.setAlignment(Pos.CENTER_LEFT);
        return celula;
    }

    private Label celulaStatus(Emprestimo emprestimo) {
        Status status = emprestimo.statusEm(hoje);
        Label rotulo = new Label("●  " + status);
        rotulo.getStyleClass().addAll("loan-badge", "loan-" + status.classe());
        return rotulo;
    }

    private Button celulaAcoes(Emprestimo emprestimo) {
        MenuItem detalhes = new MenuItem("Ver detalhes", iconeAcao("loan-icon-details"));
        detalhes.setOnAction(evento -> verDetalhes(emprestimo));
        ContextMenu menu = new ContextMenu(detalhes);
        menu.getStyleClass().add("loan-actions-menu");
        menu.getScene().getStylesheets().addAll(raiz.getStylesheets());
        if (emprestimo.devolvidoEm() == null) {
            MenuItem devolver = new MenuItem("Registrar devolução", iconeAcao("loan-icon-return"));
            devolver.setOnAction(evento -> registrarDevolucao(emprestimo));
            menu.getItems().add(devolver);
        }
        Button botao = new Button("", iconeAcao("loan-icon-ellipsis"));
        botao.getStyleClass().add("loan-row-menu");
        botao.setAccessibleText("Ações do empréstimo de " + emprestimo.livro());
        botao.setOnAction(evento -> {
            TemaVisual.herdar(menu.getScene().getRoot(), raiz.getScene().getRoot());
            menu.show(botao, Side.BOTTOM, 0, 5);
        });
        menu.showingProperty().addListener((obs, antes, aberto) -> botao.pseudoClassStateChanged(MENU_ABERTO, aberto));
        return botao;
    }

    private Region iconeAcao(String classe) {
        Region icone = new Region();
        icone.getStyleClass().add(classe);
        return icone;
    }

    private void configurarColuna(TableColumn<Emprestimo, Emprestimo> coluna,
                                  Function<Emprestimo, Node> conteudo) {
        coluna.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue()));
        coluna.setCellFactory(c -> new TableCell<>() {
            @Override
            protected void updateItem(Emprestimo emprestimo, boolean vazio) {
                super.updateItem(emprestimo, vazio);
                setGraphic(vazio || emprestimo == null ? null : conteudo.apply(emprestimo));
            }
        });
    }
}
