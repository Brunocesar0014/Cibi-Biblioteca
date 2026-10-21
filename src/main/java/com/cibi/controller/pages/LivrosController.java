package com.cibi.controller.pages;

import com.cibi.Service.LivroService;
import com.cibi.model.Livro;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.stage.FileChooser;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

public class LivrosController {
    private static final int TAMANHO_PAGINA = 5;
    private static final String INDISPONIVEL = "—";

    @FXML private Label lblTotal;
    @FXML private HBox paginasNumeradas;
    @FXML private Label lblResultados;
    @FXML private Label lblMensagem;
    @FXML private TextField txtBusca;
    @FXML private ComboBox<String> filtroCategoria;
    @FXML private ComboBox<String> filtroStatus;
    @FXML private ComboBox<String> filtroAno;
    @FXML private ComboBox<String> comboOrdenar;
    @FXML private TableView<Livro> tabelaLivros;
    @FXML private TableColumn<Livro, Number> colId;
    @FXML private TableColumn<Livro, String> colTitulo;
    @FXML private TableColumn<Livro, String> colAutor;
    @FXML private TableColumn<Livro, String> colCategoria;
    @FXML private TableColumn<Livro, String> colIsbn;
    @FXML private TableColumn<Livro, String> colAno;
    @FXML private TableColumn<Livro, String> colQuantidade;
    @FXML private TableColumn<Livro, String> colDisponiveis;
    @FXML private TableColumn<Livro, String> colStatus;
    @FXML private TableColumn<Livro, Livro> colAcoes;
    @FXML private Button btnAnterior;
    @FXML private Button btnProximo;

    private final LivroService service = new LivroService();
    private List<Livro> livros = List.of();
    private List<Livro> filtrados = List.of();
    private int pagina = 0;

    @FXML
    private void initialize() {
        tabelaLivros.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        tabelaLivros.setFixedCellSize(53);
        colId.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getId()));
        colTitulo.setCellValueFactory(c -> new ReadOnlyStringWrapper(texto(c.getValue().getTitulo())));
        colAutor.setCellValueFactory(c -> new ReadOnlyStringWrapper(texto(c.getValue().getAutor())));
        colTitulo.setStyle("-fx-font-weight: bold;");
        for (TableColumn<Livro, String> coluna : List.of(colCategoria, colIsbn, colAno, colQuantidade, colDisponiveis, colStatus)) {
            coluna.setCellValueFactory(c -> new ReadOnlyStringWrapper(INDISPONIVEL));
        }
        colAcoes.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue()));
        colAcoes.setCellFactory(c -> new TableCell<>() {
            private final Button editar = acao("bi-pencil-fill", "Editar", "edit-button");
            private final Button excluir = acao("bi-trash-fill", "Excluir", "delete-button");
            private final HBox botoes = new HBox(7, editar, excluir);
            {
                getStyleClass().add("book-actions-cell");
                setAlignment(Pos.CENTER);
                botoes.setAlignment(Pos.CENTER);
                editar.setOnAction(e -> editarLivro(getItem()));
                excluir.setOnAction(e -> excluirLivro(getItem()));
            }
            @Override protected void updateItem(Livro livro, boolean vazio) {
                super.updateItem(livro, vazio);
                setGraphic(vazio ? null : botoes);
            }
        });
        String semDados = "O cadastro atual não contém este campo.";
        filtroCategoria.setTooltip(new Tooltip(semDados));
        filtroStatus.setTooltip(new Tooltip(semDados));
        filtroAno.setTooltip(new Tooltip(semDados));
        comboOrdenar.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(String valor, boolean vazio) {
                super.updateItem(valor, vazio);
                setText(vazio || valor == null ? "Ordenar por: ID" : "Ordenar por: " + valor);
            }
        });
        comboOrdenar.getItems().setAll("ID", "Título", "Autor");
        comboOrdenar.setValue("ID");
        btnAnterior.managedProperty().bind(btnAnterior.visibleProperty());
        btnProximo.managedProperty().bind(btnProximo.visibleProperty());
        comboOrdenar.valueProperty().addListener((obs, antigo, novo) -> aplicarFiltros());
        txtBusca.textProperty().addListener((obs, antigo, novo) -> aplicarFiltros());
        atualizarPaginacao();
    }

    public void atualizar() {
        lblResultados.setText("Carregando livros...");
        lblMensagem.setText("");
        Task<List<Livro>> tarefa = new Task<>() {
            @Override protected List<Livro> call() { return service.listarLivros(); }
        };
        executar(tarefa, resultado -> {
            livros = resultado;
            lblTotal.setText(NumberFormat.getIntegerInstance(Locale.forLanguageTag("pt-BR")).format(livros.size()));
            aplicarFiltros();
        }, "Não foi possível carregar os livros.");
    }

    private void aplicarFiltros() {
        String busca = txtBusca.getText() == null ? "" : txtBusca.getText().strip().toLowerCase(Locale.ROOT);
        List<Livro> resultado = new ArrayList<>();
        for (Livro livro : livros) {
            if (busca.isEmpty() || texto(livro.getTitulo()).toLowerCase(Locale.ROOT).contains(busca)
                    || texto(livro.getAutor()).toLowerCase(Locale.ROOT).contains(busca)) {
                resultado.add(livro);
            }
        }
        Comparator<Livro> ordem = switch (comboOrdenar.getValue()) {
            case "Título" -> Comparator.comparing(l -> texto(l.getTitulo()), String.CASE_INSENSITIVE_ORDER);
            case "Autor" -> Comparator.comparing(l -> texto(l.getAutor()), String.CASE_INSENSITIVE_ORDER);
            default -> Comparator.comparingInt(Livro::getId);
        };
        resultado.sort(ordem.thenComparingInt(Livro::getId));
        filtrados = resultado;
        pagina = 0;
        atualizarPaginacao();
    }

    private void atualizarPaginacao() {
        int paginas = Math.max(1, (filtrados.size() + TAMANHO_PAGINA - 1) / TAMANHO_PAGINA);
        pagina = Math.min(pagina, paginas - 1);
        int inicio = pagina * TAMANHO_PAGINA;
        int fim = Math.min(inicio + TAMANHO_PAGINA, filtrados.size());
        tabelaLivros.setItems(FXCollections.observableArrayList(filtrados.subList(inicio, fim)));
        atualizarNumeros(paginas);
        btnAnterior.setVisible(pagina > 0);
        btnProximo.setVisible(pagina < paginas - 1);
        lblResultados.setText(filtrados.isEmpty() ? "0 resultados"
                : (inicio + 1) + " – " + fim + " de " + filtrados.size() + " resultados");
    }

    private void atualizarNumeros(int paginas) {
        paginasNumeradas.getChildren().clear();
        if (paginas <= 7) {
            for (int indice = 0; indice < paginas; indice++) adicionarNumero(indice);
            return;
        }
        int primeiro = Math.max(1, Math.min(pagina - 1, paginas - 4));
        int ultimo = Math.min(paginas - 2, primeiro + 2);
        primeiro = Math.max(1, ultimo - 2);
        adicionarNumero(0);
        if (primeiro > 1) adicionarReticencias();
        for (int indice = primeiro; indice <= ultimo; indice++) adicionarNumero(indice);
        if (ultimo < paginas - 2) adicionarReticencias();
        adicionarNumero(paginas - 1);
    }

    private void adicionarNumero(int indice) {
        Button numero = new Button(Integer.toString(indice + 1));
        numero.getStyleClass().add("page-number");
        if (indice == pagina) numero.getStyleClass().add("page-number-current");
        numero.setOnAction(evento -> { pagina = indice; atualizarPaginacao(); });
        paginasNumeradas.getChildren().add(numero);
    }

    private void adicionarReticencias() {
        Label reticencias = new Label("…");
        reticencias.getStyleClass().add("page-ellipsis");
        paginasNumeradas.getChildren().add(reticencias);
    }

    @FXML private void paginaAnterior() { if (pagina > 0) { pagina--; atualizarPaginacao(); } }
    @FXML private void proximaPagina() { if ((pagina + 1) * TAMANHO_PAGINA < filtrados.size()) { pagina++; atualizarPaginacao(); } }

    @FXML private void novoLivro() { abrirFormulario(null); }
    private void editarLivro(Livro livro) { if (livro != null) abrirFormulario(livro); }

    private void abrirFormulario(Livro existente) {
        Dialog<Livro> dialogo = new Dialog<>();
        dialogo.setTitle(existente == null ? "Novo Livro" : "Editar Livro");
        dialogo.setHeaderText(existente == null ? "Cadastrar livro" : "Editar livro cadastrado");
        ButtonType salvar = new ButtonType("Salvar", javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
        dialogo.getDialogPane().getButtonTypes().addAll(salvar, ButtonType.CANCEL);
        TextField titulo = new TextField(existente == null ? "" : texto(existente.getTitulo()));
        TextField autor = new TextField(existente == null ? "" : texto(existente.getAutor()));
        TextArea sinopse = new TextArea(existente == null ? "" : texto(existente.getSinopse()));
        sinopse.setPrefRowCount(4);
        GridPane campos = new GridPane();
        campos.setHgap(10);
        campos.setVgap(10);
        campos.setPadding(new Insets(12));
        campos.addRow(0, new Label("Título *"), titulo);
        campos.addRow(1, new Label("Autor"), autor);
        campos.addRow(2, new Label("Sinopse"), sinopse);
        dialogo.getDialogPane().setContent(campos);
        Button botaoSalvar = (Button) dialogo.getDialogPane().lookupButton(salvar);
        botaoSalvar.disableProperty().bind(Bindings.createBooleanBinding(
                () -> titulo.getText().isBlank() || titulo.getText().length() > 100
                        || autor.getText().length() > 100,
                titulo.textProperty(), autor.textProperty()));
        dialogo.setResultConverter(botao -> {
            if (botao != salvar) return null;
            Livro livro = new Livro(titulo.getText().strip(), autor.getText().strip(), sinopse.getText().strip());
            if (existente != null) livro.setId(existente.getId());
            return livro;
        });
        dialogo.showAndWait().ifPresent(livro -> {
            Task<Void> tarefa = new Task<>() {
                @Override protected Void call() {
                    if (existente == null) service.inserirLivro(livro);
                    else service.atualizarLivro(livro);
                    return null;
                }
            };
            executar(tarefa, ignorado -> atualizar(), "Não foi possível salvar o livro.");
        });
    }

    private void excluirLivro(Livro livro) {
        if (livro == null) return;
        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION,
                "Excluir “" + texto(livro.getTitulo()) + "” do acervo?", ButtonType.CANCEL, ButtonType.OK);
        confirmacao.setHeaderText("Confirmar exclusão");
        if (confirmacao.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) return;
        Task<Void> tarefa = new Task<>() {
            @Override protected Void call() { service.excluirLivro(livro.getId()); return null; }
        };
        executar(tarefa, ignorado -> atualizar(), "Não foi possível excluir o livro.");
    }

    @FXML
    private void exportar() {
        FileChooser seletor = new FileChooser();
        seletor.setTitle("Exportar livros");
        seletor.setInitialFileName("livros.csv");
        seletor.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV", "*.csv"));
        File destino = seletor.showSaveDialog(tabelaLivros.getScene().getWindow());
        if (destino == null) return;
        List<Livro> paraExportar = List.copyOf(filtrados);
        Task<Void> tarefa = new Task<>() {
            @Override protected Void call() throws Exception {
                StringBuilder csv = new StringBuilder("ID;Título;Autor;Sinopse\r\n");
                for (Livro livro : paraExportar) {
                    csv.append(livro.getId()).append(';').append(csv(livro.getTitulo())).append(';')
                            .append(csv(livro.getAutor())).append(';').append(csv(livro.getSinopse())).append("\r\n");
                }
                Files.writeString(destino.toPath(), csv.toString(), StandardCharsets.UTF_8);
                return null;
            }
        };
        executar(tarefa, ignorado -> lblMensagem.setText("Arquivo exportado com sucesso."),
                "Não foi possível exportar os livros.");
    }

    private <T> void executar(Task<T> tarefa, Consumer<T> sucesso, String mensagemErro) {
        tarefa.setOnSucceeded(e -> sucesso.accept(tarefa.getValue()));
        tarefa.setOnFailed(e -> lblMensagem.setText(mensagemErro));
        Thread worker = new Thread(tarefa, "cibi-livros");
        worker.setDaemon(true);
        worker.start();
    }

    private static Button acao(String icone, String dica, String classe) {
        Button botao = new Button();
        Region grafico = new Region();
        grafico.getStyleClass().addAll("bi-icon", "book-action-icon", icone);
        botao.setGraphic(grafico);
        botao.getStyleClass().addAll("action-button", classe);
        botao.setTooltip(new Tooltip(dica));
        return botao;
    }

    private static String texto(String valor) { return valor == null ? "" : valor; }
    private static String csv(String valor) { return "\"" + texto(valor).replace("\"", "\"\"") + "\""; }
}
