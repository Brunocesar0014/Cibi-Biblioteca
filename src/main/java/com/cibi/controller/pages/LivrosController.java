package com.cibi.controller.pages;

import com.cibi.Service.LivroService;
import com.cibi.model.Livro;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import java.io.IOException;
import java.util.Map;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
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

    @FXML private StackPane livrosRoot;
    @FXML private VBox painelListagem;
    @FXML private Label lblTotal;
    @FXML private Label lblDisponiveis;
    @FXML private Label lblExemplares;
    @FXML private Label lblCategorias;
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
    private boolean atualizandoFiltros;
    private Map<String, String> rascunho;

    @FXML
    private void initialize() {
        tabelaLivros.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        tabelaLivros.setFixedCellSize(53);
        colId.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getId()));
        colTitulo.setCellValueFactory(c -> new ReadOnlyStringWrapper(texto(c.getValue().getTitulo())));
        colAutor.setCellValueFactory(c -> new ReadOnlyStringWrapper(texto(c.getValue().getAutor())));
        colTitulo.setStyle("-fx-font-weight: bold;");
        colCategoria.setCellValueFactory(c -> new ReadOnlyStringWrapper(opcional(c.getValue().getCategoria())));
        colIsbn.setCellValueFactory(c -> new ReadOnlyStringWrapper(opcional(c.getValue().getIsbn())));
        colAno.setCellValueFactory(c -> new ReadOnlyStringWrapper(
                c.getValue().getAnoPublicacao() == null ? INDISPONIVEL : c.getValue().getAnoPublicacao().toString()));
        colQuantidade.setCellValueFactory(c -> new ReadOnlyStringWrapper(Integer.toString(c.getValue().getQuantidadeTotal())));
        colDisponiveis.setCellValueFactory(c -> new ReadOnlyStringWrapper(Integer.toString(c.getValue().getQuantidadeDisponivel())));
        colStatus.setCellValueFactory(c -> new ReadOnlyStringWrapper(texto(c.getValue().getStatus())));
        colStatus.setCellFactory(c -> new TableCell<>() {
            private final Label etiqueta = new Label();
            @Override protected void updateItem(String status, boolean vazio) {
                super.updateItem(status, vazio);
                setText(null);
                if (vazio || status == null) { setGraphic(null); return; }
                etiqueta.setText(status);
                etiqueta.getStyleClass().setAll("book-status-pill",
                        "Disponível".equals(status) ? "book-status-available" : "book-status-unavailable");
                setGraphic(etiqueta);
            }
        });
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
        filtroCategoria.setValue("Todas as categorias");
        filtroStatus.getItems().setAll("Todos os status");
        filtroStatus.getItems().addAll(LivroService.STATUS);
        filtroStatus.setValue("Todos os status");
        filtroAno.setValue("Todos os anos");
        filtroCategoria.valueProperty().addListener((o, a, n) -> aplicarFiltros());
        filtroStatus.valueProperty().addListener((o, a, n) -> aplicarFiltros());
        filtroAno.valueProperty().addListener((o, a, n) -> aplicarFiltros());
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
        lblMensagem.managedProperty().bind(lblMensagem.textProperty().isNotEmpty());
        lblMensagem.visibleProperty().bind(lblMensagem.managedProperty());
        configurarBotaoFiltro(filtroCategoria, "Todas as categorias", "Categoria");
        configurarBotaoFiltro(filtroStatus, "Todos os status", "Status");
        configurarBotaoFiltro(filtroAno, "Todos os anos", "Ano");
        atualizarPaginacao();
    }

    private void configurarBotaoFiltro(ComboBox<String> filtro, String todos, String rotulo) {
        filtro.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(String valor, boolean vazio) {
                super.updateItem(valor, vazio);
                setText(vazio || valor == null || todos.equals(valor) ? rotulo : valor);
                setTooltip(valor == null ? null : new Tooltip(valor));
            }
        });
    }

    public void atualizar() {
        livros = service.listarLivros();
        lblTotal.setText(formatar(livros.size()));
        lblExemplares.setText(formatar(livros.stream().mapToLong(Livro::getQuantidadeTotal).sum()));
        lblDisponiveis.setText(formatar(livros.stream().filter(l -> "Disponível".equals(l.getStatus()))
                .mapToLong(Livro::getQuantidadeDisponivel).sum()));
        lblCategorias.setText(formatar(livros.stream().map(l -> texto(l.getCategoria()).strip())
                .filter(c -> !c.isBlank()).map(c -> c.toLowerCase(Locale.ROOT)).distinct().count()));
        atualizandoFiltros = true;
        try {
            preencherFiltro(filtroCategoria, "Todas as categorias", livros.stream().map(Livro::getCategoria)
                    .filter(c -> c != null && !c.isBlank()).distinct().sorted(String.CASE_INSENSITIVE_ORDER).toList());
            preencherFiltro(filtroAno, "Todos os anos", livros.stream().map(Livro::getAnoPublicacao)
                    .filter(java.util.Objects::nonNull).distinct().sorted(Comparator.reverseOrder()).map(Object::toString).toList());
        } finally {
            atualizandoFiltros = false;
        }
        aplicarFiltros();
    }

    private static String formatar(long valor) {
        return NumberFormat.getIntegerInstance(Locale.forLanguageTag("pt-BR")).format(valor);
    }

    private static void preencherFiltro(ComboBox<String> filtro, String todos, List<String> valores) {
        String selecionado = filtro.getValue();
        filtro.getItems().setAll(todos);
        filtro.getItems().addAll(valores);
        filtro.setValue(filtro.getItems().contains(selecionado) ? selecionado : todos);
    }

    private void aplicarFiltros() {
        if (atualizandoFiltros) return;
        String busca = txtBusca.getText() == null ? "" : txtBusca.getText().strip().toLowerCase(Locale.ROOT);
        List<Livro> resultado = new ArrayList<>();
        for (Livro livro : livros) {
            boolean correspondeBusca = busca.isEmpty()
                    || texto(livro.getTitulo()).toLowerCase(Locale.ROOT).contains(busca)
                    || texto(livro.getAutor()).toLowerCase(Locale.ROOT).contains(busca)
                    || texto(livro.getIsbn()).toLowerCase(Locale.ROOT).contains(busca)
                    || texto(livro.getAssuntos()).toLowerCase(Locale.ROOT).contains(busca);
            boolean correspondeCategoria = "Todas as categorias".equals(filtroCategoria.getValue())
                    || java.util.Objects.equals(filtroCategoria.getValue(), livro.getCategoria());
            boolean correspondeStatus = "Todos os status".equals(filtroStatus.getValue())
                    || java.util.Objects.equals(filtroStatus.getValue(), livro.getStatus());
            boolean correspondeAno = "Todos os anos".equals(filtroAno.getValue())
                    || java.util.Objects.equals(filtroAno.getValue(), String.valueOf(livro.getAnoPublicacao()));
            if (correspondeBusca && correspondeCategoria && correspondeStatus && correspondeAno) resultado.add(livro);
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
        lblMensagem.setText("");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/cibi/fxml/pages/LivroFormulario.fxml"));
            Parent formulario = loader.load();
            LivroFormularioController controller = loader.getController();
            controller.configurarAtualizacaoCategorias(resultado -> {
                if (rascunho == null) return;
                Map<String, String> atualizado = new java.util.LinkedHashMap<>(rascunho);
                atualizado.put("categoria", resultado.atualizarNome(rascunho.get("categoria")));
                rascunho = Map.copyOf(atualizado);
            });
            controller.configurar(existente, existente == null ? rascunho : null, livro -> {
                if (existente == null) {
                    service.inserirLivro(livro);
                    rascunho = null;
                } else {
                    service.atualizarLivro(livro);
                }
                // Mostra o registro salvo, mesmo se a lista anterior tinha filtros.
                txtBusca.clear();
                filtroCategoria.setValue("Todas as categorias");
                filtroStatus.setValue("Todos os status");
                filtroAno.setValue("Todos os anos");
                voltarParaLista();
                Livro salvo = livros.stream().filter(l -> l.getId() == livro.getId()).findFirst().orElseThrow();
                pagina = filtrados.indexOf(salvo) / TAMANHO_PAGINA;
                atualizarPaginacao();
                tabelaLivros.getSelectionModel().select(salvo);
                mostrarMensagem(existente == null
                        ? "Livro cadastrado nesta sessão." : "Livro atualizado nesta sessão.", false);
            }, dados -> rascunho = dados, this::voltarParaLista);
            livrosRoot.getChildren().setAll(formulario);
        } catch (IOException erro) {
            mostrarMensagem("Não foi possível abrir o formulário de livros.", true);
        }
    }

    private void voltarParaLista() {
        livrosRoot.getChildren().setAll(painelListagem);
        atualizar();
    }

    private void excluirLivro(Livro livro) {
        if (livro == null) return;
        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION,
                "Excluir “" + texto(livro.getTitulo()) + "” do acervo?", ButtonType.CANCEL, ButtonType.OK);
        confirmacao.setHeaderText("Confirmar exclusão");
        confirmacao.initOwner(livrosRoot.getScene().getWindow());
        if (confirmacao.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) return;
        service.excluirLivro(livro.getId());
        atualizar();
        mostrarMensagem("Livro excluído desta sessão.", false);
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
                Files.writeString(destino.toPath(), gerarCsv(paraExportar), StandardCharsets.UTF_8);
                return null;
            }
        };
        executar(tarefa, ignorado -> mostrarMensagem("Arquivo exportado com sucesso.", false),
                "Não foi possível exportar os livros.");
    }

    static String gerarCsv(List<Livro> livros) {
        StringBuilder csv = new StringBuilder("\uFEFFID;Título;Subtítulo;Autor;Editora;Categoria;ISBN;Ano;Edição;Volume;Páginas;Idioma;Local de publicação;Assuntos;Quantidade total;Quantidade disponível;Localização;Número de chamada;Status;Sinopse\r\n");
        for (Livro livro : livros) {
            Object[] campos = { livro.getId(), livro.getTitulo(), livro.getSubtitulo(), livro.getAutor(),
                    livro.getEditora(), livro.getCategoria(), livro.getIsbn(), livro.getAnoPublicacao(),
                    livro.getEdicao(), livro.getVolume(), livro.getNumeroPaginas(), livro.getIdioma(),
                    livro.getLocalPublicacao(), livro.getAssuntos(), livro.getQuantidadeTotal(),
                    livro.getQuantidadeDisponivel(), livro.getLocalizacao(), livro.getNumeroChamada(),
                    livro.getStatus(), livro.getSinopse() };
            for (int i = 0; i < campos.length; i++) {
                if (i > 0) csv.append(';');
                csv.append(csv(campos[i] == null ? "" : campos[i].toString()));
            }
            csv.append("\r\n");
        }
        return csv.toString();
    }

    private void mostrarMensagem(String mensagem, boolean erro) {
        lblMensagem.setText(mensagem);
        lblMensagem.getStyleClass().removeAll("book-feedback-error", "book-feedback-success");
        lblMensagem.getStyleClass().add(erro ? "book-feedback-error" : "book-feedback-success");
    }

    private <T> void executar(Task<T> tarefa, Consumer<T> sucesso, String mensagemErro) {
        tarefa.setOnSucceeded(e -> sucesso.accept(tarefa.getValue()));
        tarefa.setOnFailed(e -> mostrarMensagem(mensagemErro, true));
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

    private static String opcional(String valor) { return valor == null || valor.isBlank() ? INDISPONIVEL : valor; }
    private static String texto(String valor) { return valor == null ? "" : valor; }
    private static String csv(String valor) {
        String seguro = texto(valor);
        if (seguro.stripLeading().matches("(?s)^[=+@-].*")) seguro = "'" + seguro;
        return "\"" + seguro.replace("\"", "\"\"") + "\"";
    }
}
