package com.cibi.controller.pages;

import com.cibi.Service.ConfiguracoesService;
import com.cibi.Service.ConfiguracoesService.ConfiguracaoInvalidaException;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.net.URL;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controller da tela de Configurações do CIBI.
 *
 * Responsabilidades:
 *  - montar os componentes (spinners, combo, grupos de seleção);
 *  - preencher a tela com os valores vindos do {@link ConfiguracoesService};
 *  - alternar entre as abas;
 *  - marcar o formulário como "alterado" e habilitar o botão Salvar;
 *  - devolver o mapa chave/valor para o service persistir.
 */
public class ConfiguracoesController implements Initializable {

    /* ====================== FXML ====================== */

    @FXML private VBox root;
    @FXML private ScrollPane scroll;
    @FXML private HBox barraAbas;

    @FXML private ToggleGroup grupoAbas;
    @FXML private ToggleButton abaAparencia;
    @FXML private ToggleButton abaEmprestimos;
    @FXML private ToggleButton abaNotificacoes;
    @FXML private ToggleButton abaSistema;

    @FXML private VBox painelAparencia;
    @FXML private VBox painelEmprestimos;
    @FXML private VBox painelNotificacoes;
    @FXML private VBox painelSistema;

    // Aparência
    @FXML private ToggleGroup grupoTema;
    @FXML private ToggleButton temaEscuro;
    @FXML private ToggleButton temaSistema;
    @FXML private ToggleButton temaClaro;
    @FXML private Label lblTemaEscuro;
    @FXML private Label lblTemaSistema;
    @FXML private Label lblTemaClaro;

    @FXML private ToggleGroup grupoCor;
    @FXML private ToggleGroup grupoFonte;
    @FXML private CheckBox swSidebarCompacta;

    // Empréstimos
    @FXML private Spinner<Integer> spPrazoDias;
    @FXML private Spinner<Integer> spLimiteLivros;
    @FXML private Spinner<Integer> spRenovacoes;
    @FXML private TextField txtMultaDia;
    @FXML private CheckBox swCobrarMulta;
    @FXML private CheckBox swBloquearAtraso;

    // Notificações
    @FXML private CheckBox swAlertaDevolucao;
    @FXML private CheckBox swAlertaAtraso;
    @FXML private CheckBox swAlertaEstoque;
    @FXML private Spinner<Integer> spAntecedencia;

    // Sistema
    @FXML private TextField txtNomeBiblioteca;
    @FXML private ComboBox<Integer> cbItensPagina;
    @FXML private TextField txtPastaBackup;
    @FXML private Button btnEscolherPasta;
    @FXML private CheckBox swBackupAutomatico;

    // Rodapé
    @FXML private Label lblStatus;
    @FXML private Button btnSalvar;
    @FXML private Button btnCancelar;

    /* ====================== ESTADO ====================== */

    private final ConfiguracoesService service = new ConfiguracoesService();

    /** Evita marcar o formulário como alterado enquanto ele está sendo preenchido. */
    private boolean carregando = false;
    private boolean alterado = false;

    /* ====================== CICLO DE VIDA ====================== */

    @Override
    public void initialize(URL url, ResourceBundle bundle) {
        configurarAbas();
        configurarCampos();
        configurarSelecaoObrigatoria(grupoTema);
        configurarSelecaoObrigatoria(grupoCor);
        configurarSelecaoObrigatoria(grupoFonte);

        carregarValores();
        registrarListeners();

        marcarSalvo(null);
    }

    /* ====================== MONTAGEM ====================== */

    private void configurarAbas() {
        grupoAbas.selectedToggleProperty().addListener((obs, anterior, atual) -> {
            if (atual == null) {
                if (anterior != null) {
                    anterior.setSelected(true);
                }
                return;
            }
            mostrarPainel(String.valueOf(atual.getUserData()));
        });
        mostrarPainel("aparencia");
    }

    private void mostrarPainel(String chave) {
        exibir(painelAparencia, "aparencia".equals(chave));
        exibir(painelEmprestimos, "emprestimos".equals(chave));
        exibir(painelNotificacoes, "notificacoes".equals(chave));
        exibir(painelSistema, "sistema".equals(chave));
        if (scroll != null) {
            scroll.setVvalue(0);
        }
    }

    private void exibir(Node no, boolean visivel) {
        no.setVisible(visivel);
        no.setManaged(visivel);
    }

    private void configurarCampos() {
        spPrazoDias.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 90, 14));
        spLimiteLivros.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, 3));
        spRenovacoes.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 5, 1));
        spAntecedencia.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 30, 2));

        spPrazoDias.setEditable(true);
        spLimiteLivros.setEditable(true);
        spRenovacoes.setEditable(true);
        spAntecedencia.setEditable(true);

        cbItensPagina.setItems(FXCollections.observableArrayList(25, 50, 100, 200));

        // aceita apenas dígitos, vírgula e ponto no campo de multa
        txtMultaDia.textProperty().addListener((obs, anterior, atual) -> {
            if (atual != null && !atual.matches("[0-9]*[.,]?[0-9]{0,2}")) {
                txtMultaDia.setText(anterior);
            }
        });
    }

    /** Impede que o usuário desmarque um cartão e fique sem nenhuma opção selecionada. */
    private void configurarSelecaoObrigatoria(ToggleGroup grupo) {
        grupo.selectedToggleProperty().addListener((obs, anterior, atual) -> {
            if (atual == null && anterior != null) {
                anterior.setSelected(true);
            }
        });
    }

    /* ====================== CARGA ====================== */

    private void carregarValores() {
        carregando = true;
        Map<String, String> v = service.carregarTodas();

        selecionarPorUserData(grupoTema, v.get(ConfiguracoesService.TEMA));
        selecionarPorUserData(grupoCor, v.get(ConfiguracoesService.COR_DESTAQUE));
        selecionarPorUserData(grupoFonte, v.get(ConfiguracoesService.TAMANHO_FONTE));
        swSidebarCompacta.setSelected(Boolean.parseBoolean(v.get(ConfiguracoesService.SIDEBAR_COMPACTA)));

        spPrazoDias.getValueFactory().setValue(service.getInt(ConfiguracoesService.PRAZO_DIAS));
        spLimiteLivros.getValueFactory().setValue(service.getInt(ConfiguracoesService.LIMITE_LIVROS));
        spRenovacoes.getValueFactory().setValue(service.getInt(ConfiguracoesService.RENOVACOES));
        txtMultaDia.setText(String.format("%.2f", service.getDouble(ConfiguracoesService.MULTA_DIA))
                .replace('.', ','));
        swCobrarMulta.setSelected(service.getBoolean(ConfiguracoesService.COBRAR_MULTA));
        swBloquearAtraso.setSelected(service.getBoolean(ConfiguracoesService.BLOQUEAR_ATRASO));

        swAlertaDevolucao.setSelected(service.getBoolean(ConfiguracoesService.ALERTA_DEVOLUCAO));
        swAlertaAtraso.setSelected(service.getBoolean(ConfiguracoesService.ALERTA_ATRASO));
        swAlertaEstoque.setSelected(service.getBoolean(ConfiguracoesService.ALERTA_ESTOQUE));
        spAntecedencia.getValueFactory().setValue(service.getInt(ConfiguracoesService.ANTECEDENCIA_DIAS));

        txtNomeBiblioteca.setText(v.get(ConfiguracoesService.NOME_BIBLIOTECA));
        cbItensPagina.setValue(service.getInt(ConfiguracoesService.ITENS_PAGINA));
        txtPastaBackup.setText(v.get(ConfiguracoesService.PASTA_BACKUP));
        swBackupAutomatico.setSelected(service.getBoolean(ConfiguracoesService.BACKUP_AUTOMATICO));

        aplicarPreviaVisual();
        carregando = false;
    }

    private void selecionarPorUserData(ToggleGroup grupo, String valor) {
        if (valor == null) {
            return;
        }
        for (Toggle toggle : grupo.getToggles()) {
            if (valor.equalsIgnoreCase(String.valueOf(toggle.getUserData()))) {
                toggle.setSelected(true);
                return;
            }
        }
    }

    /* ====================== LISTENERS ====================== */

    private void registrarListeners() {
        ChangeListener<Object> aoMudar = (obs, anterior, atual) -> marcarAlterado();

        grupoTema.selectedToggleProperty().addListener(aoMudar);
        grupoCor.selectedToggleProperty().addListener(aoMudar);
        grupoFonte.selectedToggleProperty().addListener(aoMudar);
        swSidebarCompacta.selectedProperty().addListener(aoMudar);

        spPrazoDias.valueProperty().addListener(aoMudar);
        spLimiteLivros.valueProperty().addListener(aoMudar);
        spRenovacoes.valueProperty().addListener(aoMudar);
        spAntecedencia.valueProperty().addListener(aoMudar);
        txtMultaDia.textProperty().addListener(aoMudar);
        swCobrarMulta.selectedProperty().addListener(aoMudar);
        swBloquearAtraso.selectedProperty().addListener(aoMudar);

        swAlertaDevolucao.selectedProperty().addListener(aoMudar);
        swAlertaAtraso.selectedProperty().addListener(aoMudar);
        swAlertaEstoque.selectedProperty().addListener(aoMudar);

        txtNomeBiblioteca.textProperty().addListener(aoMudar);
        cbItensPagina.valueProperty().addListener(aoMudar);
        txtPastaBackup.textProperty().addListener(aoMudar);
        swBackupAutomatico.selectedProperty().addListener(aoMudar);

        // prévia imediata de tema, cor e legenda dos cartões
        grupoTema.selectedToggleProperty().addListener((o, a, b) -> aplicarPreviaVisual());
        grupoCor.selectedToggleProperty().addListener((o, a, b) -> aplicarPreviaVisual());
    }

    private void marcarAlterado() {
        if (carregando) {
            return;
        }
        alterado = true;
        btnSalvar.setDisable(false);
        definirStatus("Há alterações não salvas.", "status-alterado");
    }

    private void marcarSalvo(String mensagem) {
        alterado = false;
        btnSalvar.setDisable(true);
        definirStatus(mensagem, mensagem == null ? null : "status-ok");
    }

    /* ====================== PRÉVIA VISUAL ====================== */

    /**
     * Aplica a cor de destaque escolhida ao próprio painel (a cor é uma
     * looked-up color do CSS, então basta redefini-la no nó raiz) e destaca a
     * legenda do tema selecionado, como na referência.
     */
    private void aplicarPreviaVisual() {
        Toggle cor = grupoCor.getSelectedToggle();
        if (cor != null) {
            String hex = String.valueOf(cor.getUserData());
            root.setStyle("-cibi-destaque: " + hex + "; -cibi-destaque-hover: derive(" + hex + ", -12%);");
        }

        atualizarLegenda(lblTemaEscuro, temaEscuro);
        atualizarLegenda(lblTemaSistema, temaSistema);
        atualizarLegenda(lblTemaClaro, temaClaro);
    }

    private void atualizarLegenda(Label legenda, ToggleButton cartao) {
        legenda.getStyleClass().remove("selecionado");
        if (cartao.isSelected()) {
            legenda.getStyleClass().add("selecionado");
        }
    }

    /* ====================== AÇÕES ====================== */

    @FXML
    private void onSalvar() {
        Map<String, String> valores = coletarValores();
        try {
            service.salvar(valores);
            marcarSalvo("Configurações salvas com sucesso.");
            aplicarPreviaVisual();

        } catch (ConfiguracaoInvalidaException e) {
            definirStatus(e.getMessage(), "status-erro");
            alerta(Alert.AlertType.WARNING, "Configuração inválida", e.getMessage());

        } catch (SQLException e) {
            definirStatus("Não foi possível salvar no banco de dados.", "status-erro");
            alerta(Alert.AlertType.ERROR, "Erro ao salvar",
                    "Não foi possível gravar as configurações.\n\nDetalhe: " + e.getMessage());
        }
    }

    @FXML
    private void onDescartar() {
        if (!alterado) {
            return;
        }
        carregarValores();
        marcarSalvo("Alterações descartadas.");
    }

    @FXML
    private void onRestaurarPadroes() {
        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION,
                "Todas as configurações voltarão aos valores originais do CIBI. Deseja continuar?",
                ButtonType.CANCEL, ButtonType.OK);
        confirmacao.setTitle("Restaurar padrões");
        confirmacao.setHeaderText("Restaurar configurações padrão");
        estilizar(confirmacao);

        Optional<ButtonType> resposta = confirmacao.showAndWait();
        if (resposta.isEmpty() || resposta.get() != ButtonType.OK) {
            return;
        }

        try {
            service.restaurarPadroes();
            carregarValores();
            marcarSalvo("Configurações restauradas aos valores padrão.");
        } catch (SQLException e) {
            definirStatus("Não foi possível restaurar os padrões.", "status-erro");
            alerta(Alert.AlertType.ERROR, "Erro ao restaurar", e.getMessage());
        }
    }

    @FXML
    private void onEscolherPasta() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Selecione a pasta de backup");

        String atual = txtPastaBackup.getText();
        if (atual != null && !atual.isBlank()) {
            File pasta = new File(atual);
            if (pasta.isDirectory()) {
                chooser.setInitialDirectory(pasta);
            }
        }

        File escolhida = chooser.showDialog(root.getScene() != null ? root.getScene().getWindow() : null);
        if (escolhida != null) {
            txtPastaBackup.setText(escolhida.getAbsolutePath());
        }
    }

    /* ====================== APOIO ====================== */

    private Map<String, String> coletarValores() {
        Map<String, String> v = new LinkedHashMap<>();

        v.put(ConfiguracoesService.TEMA, userData(grupoTema));
        v.put(ConfiguracoesService.COR_DESTAQUE, userData(grupoCor));
        v.put(ConfiguracoesService.TAMANHO_FONTE, userData(grupoFonte));
        v.put(ConfiguracoesService.SIDEBAR_COMPACTA, String.valueOf(swSidebarCompacta.isSelected()));

        v.put(ConfiguracoesService.PRAZO_DIAS, String.valueOf(spPrazoDias.getValue()));
        v.put(ConfiguracoesService.LIMITE_LIVROS, String.valueOf(spLimiteLivros.getValue()));
        v.put(ConfiguracoesService.RENOVACOES, String.valueOf(spRenovacoes.getValue()));
        v.put(ConfiguracoesService.MULTA_DIA, txtMultaDia.getText().replace(',', '.'));
        v.put(ConfiguracoesService.COBRAR_MULTA, String.valueOf(swCobrarMulta.isSelected()));
        v.put(ConfiguracoesService.BLOQUEAR_ATRASO, String.valueOf(swBloquearAtraso.isSelected()));

        v.put(ConfiguracoesService.ALERTA_DEVOLUCAO, String.valueOf(swAlertaDevolucao.isSelected()));
        v.put(ConfiguracoesService.ALERTA_ATRASO, String.valueOf(swAlertaAtraso.isSelected()));
        v.put(ConfiguracoesService.ALERTA_ESTOQUE, String.valueOf(swAlertaEstoque.isSelected()));
        v.put(ConfiguracoesService.ANTECEDENCIA_DIAS, String.valueOf(spAntecedencia.getValue()));

        v.put(ConfiguracoesService.NOME_BIBLIOTECA, txtNomeBiblioteca.getText().trim());
        v.put(ConfiguracoesService.ITENS_PAGINA, String.valueOf(cbItensPagina.getValue()));
        v.put(ConfiguracoesService.PASTA_BACKUP, txtPastaBackup.getText().trim());
        v.put(ConfiguracoesService.BACKUP_AUTOMATICO, String.valueOf(swBackupAutomatico.isSelected()));

        return v;
    }

    private String userData(ToggleGroup grupo) {
        Toggle selecionado = grupo.getSelectedToggle();
        return selecionado != null ? String.valueOf(selecionado.getUserData()) : "";
    }

    private void definirStatus(String mensagem, String classe) {
        lblStatus.getStyleClass().removeAll("status-ok", "status-erro", "status-alterado");
        lblStatus.setText(mensagem == null ? "" : mensagem);
        if (classe != null) {
            lblStatus.getStyleClass().add(classe);
        }
    }

    private void alerta(Alert.AlertType tipo, String titulo, String mensagem) {
        Alert alerta = new Alert(tipo, mensagem, ButtonType.OK);
        alerta.setTitle(titulo);
        alerta.setHeaderText(titulo);
        estilizar(alerta);
        alerta.showAndWait();
    }

    private void estilizar(Alert alerta) {
        if (root.getScene() != null) {
            alerta.initOwner(root.getScene().getWindow());
            alerta.getDialogPane().getStylesheets().addAll(root.getScene().getStylesheets());
        }
        alerta.getDialogPane().getStyleClass().add("dialog-cibi");
    }

    /** Permite que a tela principal pergunte se há alterações pendentes antes de trocar de página. */
    public boolean temAlteracoesPendentes() {
        return alterado;
    }
}
