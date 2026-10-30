package com.cibi.controller.pages;

import com.cibi.Service.LivroService;
import com.cibi.Service.CategoriaLivroService;
import com.cibi.model.Livro;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.Effect;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.paint.Color;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.StageStyle;
import java.io.IOException;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.time.Year;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class LivroFormularioController {
    private static final PseudoClass INVALIDO = PseudoClass.getPseudoClass("invalid");
    @FXML private VBox formRoot;
    @FXML private ScrollPane rolagem;
    @FXML private GridPane gradePrincipal, gradeBibliografica, gradeAcervo;
    @FXML private Label lblTituloFormulario, lblSubtituloFormulario, lblEtapa, lblFeedback;
    @FXML private TextField titulo, subtitulo, autor, editora, isbn, ano, edicao, volume, paginas;
    @FXML private TextField localPublicacao, assuntos, quantidadeTotal, quantidadeDisponivel, localizacao, numeroChamada;
    @FXML private TextArea sinopse;
    @FXML private ComboBox<String> categoria, idioma, status;
    @FXML private Button btnSalvar, btnRascunho;
    @FXML private ToggleButton alternarPrincipal, alternarBibliografica, alternarAcervo;
    @FXML private VBox corpoPrincipal, corpoBibliografica, corpoAcervo;
    private final CategoriaLivroService categorias = new CategoriaLivroService();
    private Consumer<CategoriaLivroService.Resultado> aoAtualizarCategorias = resultado -> {};
    private Map<String, TextInputControl> textos;
    private Map<String, String> estadoInicial;
    private Map<String, String> estadoSalvo;
    private Consumer<Livro> aoSalvar;
    private Consumer<Map<String, String>> aoSalvarRascunho;
    private Runnable aoCancelar;
    private int id;

    @FXML
    private void initialize() {
        textos = new LinkedHashMap<>();
        textos.put("titulo", titulo); textos.put("subtitulo", subtitulo); textos.put("autor", autor);
        textos.put("editora", editora); textos.put("isbn", isbn); textos.put("sinopse", sinopse);
        textos.put("ano", ano); textos.put("edicao", edicao); textos.put("volume", volume);
        textos.put("paginas", paginas); textos.put("localPublicacao", localPublicacao);
        textos.put("assuntos", assuntos); textos.put("quantidadeTotal", quantidadeTotal);
        textos.put("quantidadeDisponivel", quantidadeDisponivel); textos.put("localizacao", localizacao);
        textos.put("numeroChamada", numeroChamada);
        atualizarCategorias("");
        vincularSecao(alternarPrincipal, corpoPrincipal, "Informações principais");
        vincularSecao(alternarBibliografica, corpoBibliografica, "Dados bibliográficos");
        vincularSecao(alternarAcervo, corpoAcervo, "Controle de acervo");
        idioma.getItems().setAll("Português", "Inglês", "Espanhol", "Francês", "Alemão", "Italiano", "Outros");
        status.getItems().setAll(LivroService.STATUS);
        textos.values().forEach(campo -> campo.textProperty().addListener((o, a, n) -> campo.pseudoClassStateChanged(INVALIDO, false)));
        quantidadeDisponivel.textProperty().addListener((o, a, n) -> {
            if (status.getValue() == null || List.of("Disponível", "Indisponível").contains(status.getValue())) {
                if (n.matches("[0-9]+")) {
                    status.setValue(n.matches("0+") ? "Indisponível" : "Disponível");
                }
            }
        });
        formRoot.widthProperty().addListener((o, a, n) -> ajustarGrades(n.doubleValue()));
        ajustarGrades(1100);
    }

    public void configurar(Livro existente, Map<String, String> rascunho, Consumer<Livro> salvar,
                           Consumer<Map<String, String>> salvarRascunho, Runnable cancelar) {
        aoSalvar = salvar;
        aoSalvarRascunho = salvarRascunho;
        aoCancelar = cancelar;
        id = existente == null ? 0 : existente.getId();
        if (existente == null) {
            preencher(Map.of("quantidadeTotal", "1", "quantidadeDisponivel", "1", "idioma", "Português", "status", "Disponível"));
        } else {
            lblTituloFormulario.setText("Editar Livro");
            lblSubtituloFormulario.setText("Atualize as informações do acervo");
            lblEtapa.setText("Editar");
            btnSalvar.setText("Salvar Alterações");
            btnRascunho.setVisible(false);
            btnRascunho.setManaged(false);
            Map<String, String> dados = new LinkedHashMap<>();
            dados.put("titulo", texto(existente.getTitulo())); dados.put("subtitulo", texto(existente.getSubtitulo()));
            dados.put("autor", texto(existente.getAutor())); dados.put("editora", texto(existente.getEditora()));
            dados.put("categoria", texto(existente.getCategoria())); dados.put("isbn", texto(existente.getIsbn()));
            dados.put("sinopse", texto(existente.getSinopse())); dados.put("ano", texto(existente.getAnoPublicacao()));
            dados.put("edicao", texto(existente.getEdicao())); dados.put("volume", texto(existente.getVolume()));
            dados.put("paginas", texto(existente.getNumeroPaginas())); dados.put("idioma", texto(existente.getIdioma()));
            dados.put("localPublicacao", texto(existente.getLocalPublicacao())); dados.put("assuntos", texto(existente.getAssuntos()));
            dados.put("quantidadeTotal", texto(existente.getQuantidadeTotal()));
            dados.put("quantidadeDisponivel", texto(existente.getQuantidadeDisponivel()));
            dados.put("localizacao", texto(existente.getLocalizacao())); dados.put("numeroChamada", texto(existente.getNumeroChamada()));
            dados.put("status", texto(existente.getStatus()));
            preencher(dados);
        }
        estadoInicial = capturar();
        if (existente == null && rascunho != null) {
            preencher(rascunho);
            mensagem("Rascunho restaurado. Continue o cadastro e clique em Salvar Livro.", false);
        }
        estadoSalvo = capturar();
    }


    private void vincularSecao(ToggleButton botao, VBox corpo, String nome) {
        corpo.visibleProperty().bind(botao.selectedProperty());
        corpo.managedProperty().bind(botao.selectedProperty());
        HBox cabecalho = (HBox) botao.getGraphic();
        cabecalho.prefWidthProperty().bind(botao.widthProperty().subtract(2));
        botao.setAccessibleText(nome);
        botao.setTooltip(new Tooltip("Expandir ou recolher " + nome.toLowerCase(java.util.Locale.ROOT)));
    }

    private void atualizarCategorias(String selecionada) {
        categoria.getItems().setAll(categorias.listar());
        categoria.setValue(selecionada);
        categoria.getEditor().setText(selecionada);
    }

    public void configurarAtualizacaoCategorias(Consumer<CategoriaLivroService.Resultado> atualizar) {
        aoAtualizarCategorias = atualizar;
    }

    @FXML
    private void gerenciarCategorias() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/cibi/fxml/pages/GerenciarCategorias.fxml"));
            Parent conteudo = loader.load();
            GerenciarCategoriasController controller = loader.getController();
            Dialog<Void> dialogo = new Dialog<>();
            dialogo.setTitle("Gerenciar categorias");
            dialogo.initOwner(formRoot.getScene().getWindow());
            dialogo.initStyle(StageStyle.TRANSPARENT);
            DialogPane painel = dialogo.getDialogPane();
            painel.getStyleClass().add("book-category-dialog");
            painel.getStylesheets().add(getClass().getResource("/com/cibi/css/pages/Livros.css").toExternalForm());
            painel.setContent(conteudo);
            painel.addEventFilter(KeyEvent.KEY_PRESSED, evento -> {
                if (evento.getCode() == KeyCode.ESCAPE) {
                    evento.consume();
                    dialogo.close();
                }
            });
            painel.getButtonTypes().add(ButtonType.CANCEL);
            Node cancelarNativo = painel.lookupButton(ButtonType.CANCEL);
            cancelarNativo.setVisible(false);
            cancelarNativo.setManaged(false);
            Node barraNativa = painel.lookup(".button-bar");
            if (barraNativa != null) {
                barraNativa.setVisible(false);
                barraNativa.setManaged(false);
            }
            if (conteudo instanceof VBox modal) {
                modal.setPrefWidth(Math.max(560, Math.min(820, formRoot.getScene().getWindow().getWidth() - 80)));
                modal.setPrefHeight(Math.max(400, Math.min(500, formRoot.getScene().getWindow().getHeight() - 100)));
            }
            controller.configurar(categoria.getEditor().getText(), resultado -> {
                estadoInicial = atualizarCategoriaNoEstado(estadoInicial, resultado);
                estadoSalvo = atualizarCategoriaNoEstado(estadoSalvo, resultado);
                aoAtualizarCategorias.accept(resultado);
                atualizarCategorias(resultado.selecionada());
                dialogo.close();
                mensagem("Categorias atualizadas nesta sessão.", false);
            }, dialogo::close);
            Effect efeitoAnterior = formRoot.getEffect();
            GaussianBlur desfoque = new GaussianBlur(2);
            desfoque.setInput(efeitoAnterior);
            ColorAdjust escurecer = new ColorAdjust();
            escurecer.setBrightness(-0.18);
            escurecer.setInput(desfoque);
            dialogo.setOnShown(e -> {
                painel.getScene().setFill(Color.TRANSPARENT);
                formRoot.setEffect(escurecer);
                conteudo.lookup("#nomeCategoria").requestFocus();
            });
            dialogo.setOnHidden(e -> formRoot.setEffect(efeitoAnterior));
            dialogo.showAndWait();
        } catch (IOException erro) {
            mensagem("Não foi possível abrir o gerenciamento de categorias.", true);
        }
    }

    private Map<String, String> atualizarCategoriaNoEstado(Map<String, String> estado, CategoriaLivroService.Resultado resultado) {
        Map<String, String> atualizado = new LinkedHashMap<>(estado);
        atualizado.put("categoria", resultado.atualizarNome(estado.get("categoria")));
        return Map.copyOf(atualizado);
    }

    private void expandirCampo(Node campo) {
        for (Node atual = campo; atual != null; atual = atual.getParent()) {
            if (atual == corpoPrincipal) alternarPrincipal.setSelected(true);
            else if (atual == corpoBibliografica) alternarBibliografica.setSelected(true);
            else if (atual == corpoAcervo) alternarAcervo.setSelected(true);
        }
        formRoot.applyCss();
        formRoot.layout();
    }

    private void ajustarGrades(double largura) {
        organizar(gradePrincipal, largura < 650 ? 1 : 2);
        organizar(gradeBibliografica, largura < 650 ? 1 : largura < 1050 ? 2 : 4);
        organizar(gradeAcervo, largura < 650 ? 1 : largura < 1050 ? 2 : 4);
    }

    private void organizar(GridPane grade, int colunas) {
        grade.getColumnConstraints().clear();
        for (int i = 0; i < colunas; i++) {
            ColumnConstraints coluna = new ColumnConstraints();
            coluna.setPercentWidth(100.0 / colunas);
            coluna.setHgrow(Priority.ALWAYS);
            coluna.setMinWidth(0);
            grade.getColumnConstraints().add(coluna);
        }
        for (int i = 0; i < grade.getChildren().size(); i++) {
            Node campo = grade.getChildren().get(i);
            GridPane.setColumnIndex(campo, i % colunas);
            GridPane.setRowIndex(campo, i / colunas);
            GridPane.setColumnSpan(campo, i == grade.getChildren().size() - 1
                    ? Math.min(2, colunas - i % colunas) : 1);
        }
    }

    private Map<String, String> capturar() {
        Map<String, String> dados = new LinkedHashMap<>();
        textos.forEach((nome, campo) -> dados.put(nome, campo.getText()));
        dados.put("categoria", texto(categoria.getEditor().getText()));
        dados.put("idioma", texto(idioma.getEditor().getText()));
        dados.put("status", texto(status.getValue()));
        return Map.copyOf(dados);
    }

    private void preencher(Map<String, String> dados) {
        textos.forEach((nome, campo) -> campo.setText(dados.getOrDefault(nome, "")));
        categoria.setValue(dados.getOrDefault("categoria", ""));
        categoria.getEditor().setText(dados.getOrDefault("categoria", ""));
        idioma.setValue(dados.getOrDefault("idioma", ""));
        idioma.getEditor().setText(dados.getOrDefault("idioma", ""));
        status.setValue(dados.getOrDefault("status", "Disponível"));
    }

    @FXML
    private void salvar() {
        textos.values().forEach(campo -> campo.pseudoClassStateChanged(INVALIDO, false));
        try {
            exigir(!titulo.getText().isBlank(), titulo, "Informe o título do livro.");
            exigir(titulo.getText().strip().length() <= 100, titulo, "O título deve ter até 100 caracteres.");
            exigir(autor.getText().strip().length() <= 100, autor, "O autor deve ter até 100 caracteres.");
            exigir(LivroService.isbnValido(isbn.getText()), isbn, "Informe um ISBN-10 ou ISBN-13 válido, ou deixe o campo vazio.");
            Integer anoInformado = numero(ano, "Ano de publicação", false);
            exigir(anoInformado == null || (anoInformado > 0 && anoInformado <= Year.now().getValue() + 1),
                    ano, "Informe um ano de publicação entre 1 e " + (Year.now().getValue() + 1) + ".");
            Integer paginasInformadas = numero(paginas, "Número de páginas", false);
            exigir(paginasInformadas == null || paginasInformadas > 0, paginas, "O número de páginas deve ser maior que zero.");
            int total = numero(quantidadeTotal, "Quantidade total", true);
            int disponivel = numero(quantidadeDisponivel, "Quantidade disponível", true);
            exigir(total >= 1, quantidadeTotal, "Informe pelo menos 1 exemplar na quantidade total.");
            exigir(disponivel >= 0 && disponivel <= total, quantidadeDisponivel,
                    "A quantidade disponível deve estar entre zero e a quantidade total.");
            Livro livro = new Livro(titulo.getText().strip(), autor.getText().strip(), sinopse.getText().strip());
            livro.setId(id);
            livro.setSubtitulo(subtitulo.getText().strip()); livro.setEditora(editora.getText().strip());
            livro.setCategoria(categoria.getEditor().getText().strip()); livro.setIsbn(isbn.getText().strip());
            livro.setAnoPublicacao(anoInformado); livro.setEdicao(edicao.getText().strip());
            livro.setVolume(volume.getText().strip()); livro.setNumeroPaginas(paginasInformadas);
            livro.setIdioma(idioma.getEditor().getText().strip()); livro.setLocalPublicacao(localPublicacao.getText().strip());
            livro.setAssuntos(assuntos.getText().strip()); livro.setNumeroChamada(numeroChamada.getText().strip());
            livro.setLocalizacao(localizacao.getText().strip()); livro.setQuantidadeTotal(total);
            livro.setQuantidadeDisponivel(disponivel); livro.setStatus(status.getValue());
            LivroService.validar(livro);
            aoSalvar.accept(livro);
        } catch (IllegalArgumentException | IllegalStateException erro) {
            mensagem(erro.getMessage(), true);
        }
    }

    private Integer numero(TextField campo, String nome, boolean obrigatorio) {
        String valor = campo.getText().strip();
        if (valor.isEmpty() && !obrigatorio) return null;
        exigir(valor.matches("[0-9]+"), campo, nome + ": informe um número inteiro sem sinais ou decimais.");
        try {
            return Integer.valueOf(valor);
        } catch (NumberFormatException erro) {
            exigir(false, campo, nome + ": o número informado é muito grande.");
            return null;
        }
    }

    private void exigir(boolean condicao, Control campo, String mensagem) {
        if (condicao) return;
        expandirCampo(campo);
        campo.pseudoClassStateChanged(INVALIDO, true);
        campo.requestFocus();
        // Garante que um campo inválido fora da área visível seja revelado.
        double altura = rolagem.getContent().getBoundsInLocal().getHeight();
        double viewport = rolagem.getViewportBounds().getHeight();
        double y = rolagem.getContent().sceneToLocal(campo.localToScene(campo.getBoundsInLocal())).getMinY();
        if (altura > viewport) rolagem.setVvalue(Math.max(0, Math.min(1, (y - 60) / (altura - viewport))));
        throw new IllegalArgumentException(mensagem);
    }

    @FXML
    private void salvarRascunho() {
        estadoSalvo = capturar();
        aoSalvarRascunho.accept(estadoSalvo);
        mensagem("Rascunho salvo nesta sessão. Ele será restaurado ao abrir Novo Livro.", false);
    }

    @FXML
    private void limpar() {
        if (!capturar().equals(estadoInicial) && !confirmar("Limpar formulário?", "Os campos serão limpos. Na edição, os valores iniciais serão restaurados.")) return;
        preencher(estadoInicial);
        textos.values().forEach(campo -> campo.pseudoClassStateChanged(INVALIDO, false));
        if (id == 0) {
            aoSalvarRascunho.accept(null);
            estadoSalvo = capturar();
        }
        mensagem(id == 0 ? "Formulário limpo." : "Valores originais restaurados.", false);
        rolagem.setVvalue(0);
        titulo.requestFocus();
    }

    @FXML
    private void cancelar() {
        if (!capturar().equals(estadoSalvo) && !confirmar("Descartar alterações?", "As alterações desde o último salvamento serão descartadas.")) return;
        aoCancelar.run();
    }

    private boolean confirmar(String cabecalho, String mensagem) {
        ButtonType descartar = new ButtonType("Descartar", ButtonBar.ButtonData.OK_DONE);
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION, mensagem, ButtonType.CANCEL, descartar);
        alerta.initOwner(formRoot.getScene().getWindow());
        alerta.setTitle("Cadastro de livros");
        alerta.setHeaderText(cabecalho);
        return alerta.showAndWait().orElse(ButtonType.CANCEL) == descartar;
    }

    private void mensagem(String texto, boolean erro) {
        lblFeedback.setText(texto);
        lblFeedback.getStyleClass().removeAll("book-feedback-error", "book-feedback-success");
        lblFeedback.getStyleClass().add(erro ? "book-feedback-error" : "book-feedback-success");
        lblFeedback.setVisible(true);
        lblFeedback.setManaged(true);
    }

    private static String texto(Object valor) { return valor == null ? "" : valor.toString(); }
}
