package com.cibi.controller;

import com.cibi.util.NavigationManager;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    private static final double SIDEBAR_WIDTH_EXPANDED = 260.0;
    private static final double SIDEBAR_WIDTH_COLLAPSED = 72.0;
    private static final Duration MENU_ANIMATION_DURATION = Duration.millis(250);

    private static final String MENU_ITEM_STYLE_CLASS = "sidebar-menu-item";
    private static final String MENU_ITEM_ACTIVE_STYLE_CLASS = "sidebar-menu-item-active";

    @FXML
    private VBox sidebar;

    //@FXML
    //private VBox brandTextBox;

    //@FXML
    //private Label lblMenuSection;

    //@FXML
    //private Button btnToggleMenu;

    @FXML
    private Button btnDashboard;

    @FXML
    private Button btnLivros;

    @FXML
    private Button btnEmprestimos;

    @FXML
    private Button btnUsuarios;

    @FXML
    private Button btnSobre;

    @FXML
    private StackPane contentArea;

    private NavigationManager navigation;
    private boolean menuExpandido = true;

    private List<Button> menuButtons;
    private Map<Button, String> menuTexts;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        navigation = new NavigationManager(contentArea);

        configurarMenuButtons();
        configurarEstadoInicialDaSidebar();
        abrirTelaInicial();
    }

    /*
     * IMPORTANTE: esta lista/mapa é o único lugar do controller que
     * precisa ser atualizado quando uma nova aba é adicionada.
     * Veja docs/como-adicionar-uma-nova-aba.md para o passo a passo completo.
     */
    private void configurarMenuButtons() {
        menuButtons = List.of(
                btnDashboard,
                btnLivros,
                btnEmprestimos,
                btnUsuarios,
                btnSobre
        );

        menuTexts = new LinkedHashMap<>();
        menuTexts.put(btnDashboard, "Dashboard");
        menuTexts.put(btnLivros, "Livros");
        menuTexts.put(btnEmprestimos, "Empréstimos");
        menuTexts.put(btnUsuarios, "Usuários");
        menuTexts.put(btnSobre, "Configurações");

        for (Button button : menuButtons) {
            garantirClasseBase(button);
            button.setMaxWidth(Double.MAX_VALUE);
            button.setContentDisplay(ContentDisplay.LEFT);
        }
    }

    private void configurarEstadoInicialDaSidebar() {
        sidebar.setPrefWidth(SIDEBAR_WIDTH_EXPANDED);
        atualizarVisualDoMenu();
    }

    private void abrirTelaInicial() {
        abrirTela(
                "dashboard",
                "/com/cibi/fxml/pages/Dashboard.fxml",
                btnDashboard
        );
    }

    @FXML
    private void toggleMenu() {
        menuExpandido = !menuExpandido;

        Timeline timeline = new Timeline(
                new KeyFrame(
                        MENU_ANIMATION_DURATION,
                        new KeyValue(
                                sidebar.prefWidthProperty(),
                                menuExpandido ? SIDEBAR_WIDTH_EXPANDED : SIDEBAR_WIDTH_COLLAPSED
                        )
                )
        );

        atualizarVisualDoMenu();
        timeline.play();
    }

    private void atualizarVisualDoMenu() {
        atualizarTextosDosBotoes();
        atualizarVisibilidadeDosElementosTextuais();
        atualizarAlinhamentoDosBotoes();
        atualizarBotaoToggle();
    }

    private void atualizarTextosDosBotoes() {
        for (Map.Entry<Button, String> entry : menuTexts.entrySet()) {
            Button button = entry.getKey();
            String text = entry.getValue();

            if (menuExpandido) {
                button.setText(text);
            } else {
                button.setText("");
            }
        }
    }

    private void atualizarVisibilidadeDosElementosTextuais() {
        //brandTextBox.setManaged(menuExpandido);
        //brandTextBox.setVisible(menuExpandido);

        //lblMenuSection.setManaged(menuExpandido);
        //lblMenuSection.setVisible(menuExpandido);
    }

    private void atualizarAlinhamentoDosBotoes() {
        for (Button button : menuButtons) {
            if (menuExpandido) {
                button.setContentDisplay(ContentDisplay.LEFT);
                button.setGraphicTextGap(12);
                button.setPrefHeight(40);
            } else {
                button.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                button.setGraphicTextGap(0);
                button.setPrefHeight(40);
            }
        }
    }

    private void atualizarBotaoToggle() {
        //btnToggleMenu.setText(menuExpandido ? "☰" : "☰");
    }

    private void abrirTela(String screenKey, String fxmlPath, Button activeButton) {
        navigation.loadScreen(screenKey, fxmlPath);
        definirItemAtivo(activeButton);
    }

    private void definirItemAtivo(Button activeButton) {
        for (Button button : menuButtons) {
            button.getStyleClass().remove(MENU_ITEM_ACTIVE_STYLE_CLASS);
            garantirClasseBase(button);
        }

        if (activeButton != null && !activeButton.getStyleClass().contains(MENU_ITEM_ACTIVE_STYLE_CLASS)) {
            activeButton.getStyleClass().add(MENU_ITEM_ACTIVE_STYLE_CLASS);
        }
    }

    private void garantirClasseBase(Button button) {
        if (!button.getStyleClass().contains(MENU_ITEM_STYLE_CLASS)) {
            button.getStyleClass().add(MENU_ITEM_STYLE_CLASS);
        }
    }

    @FXML
    private void abrirDashboard() {
        abrirTela(
                "dashboard",
                "/com/cibi/fxml/pages/Dashboard.fxml",
                btnDashboard
        );
    }

    @FXML
    private void abrirLivros() {
        abrirTela(
                "livros",
                "/com/cibi/fxml/pages/Livros.fxml",
                btnLivros
        );
    }

    @FXML
    private void abrirEmprestimos() {
        abrirTela(
                "emprestimos",
                "/com/cibi/fxml/pages/Emprestimos.fxml",
                btnEmprestimos
        );
    }

    @FXML
    private void abrirUsuarios() {
        abrirTela(
                "usuarios",
                "/com/cibi/fxml/pages/Usuarios.fxml",
                btnUsuarios
        );
    }

    @FXML
    private void abrirSobre() {
        abrirTela(
                "sobre",
                "/com/cibi/fxml/pages/Configuracoes.fxml",
                btnSobre
        );
    }
}
