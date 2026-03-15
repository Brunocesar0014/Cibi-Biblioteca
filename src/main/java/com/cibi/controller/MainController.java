package com.cibi.controller;

import com.cibi.util.NavigationManager;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;

public class MainController implements Initializable {

    @FXML private Button btnDashboard;

    @FXML private Button btnLivros;

    @FXML private Button btnEmprestimos;

    @FXML private Button btnUsuarios;

    @FXML private Button btnSobre;

    @FXML
    private VBox sidebar;

    @FXML private StackPane contentArea;

    private NavigationManager navigation;

    private boolean menuExpandido = true;

    private void minimizarBotao(Button btn) {
        btn.setText("");
        btn.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
    }

    private void expandirBotao(Button btn, String texto) {
        btn.setText(texto);
        btn.setContentDisplay(ContentDisplay.LEFT);
    }

    @FXML
    private void toggleMenu() {

        double larguraAtual = sidebar.getPrefWidth();
        double novaLargura;

        if (menuExpandido) {
            novaLargura = 60;
            
            minimizarBotao(btnDashboard);
            minimizarBotao(btnLivros);
            minimizarBotao(btnEmprestimos);
            minimizarBotao(btnUsuarios);
            minimizarBotao(btnSobre);
        } else {
            novaLargura = 180;
            
            expandirBotao(btnDashboard, " Dashboard");
            expandirBotao(btnLivros, " Livros");
            expandirBotao(btnEmprestimos, " Empréstimos");
            expandirBotao(btnUsuarios, " Usuários");
            expandirBotao(btnSobre, " Sobre");
        }

        Timeline timeline = new Timeline();

        KeyValue keyValue = new KeyValue(
                sidebar.prefWidthProperty(),
                novaLargura
        );

        KeyFrame keyFrame = new KeyFrame(
                javafx.util.Duration.millis(300),
                keyValue
        );

        timeline.getKeyFrames().add(keyFrame);
        timeline.play();

        menuExpandido = !menuExpandido;

    }

    @FXML
    private void abrirDashboard() {

        navigation.loadScreen(
                "dashboard",
                "/com/cibi/fxml/pages/Dashboard.fxml"
        );
    }

    @FXML
    private void abrirLivros() {

        navigation.loadScreen(
                "livros",
                "/com/cibi/fxml/pages/Livros.fxml"
        );
    }

    @FXML
    private void abrirEmprestimos() {

        navigation.loadScreen(
                "emprestimos",
                "/com/cibi/fxml/pages/Emprestimos.fxml"
        );
    }

    @FXML
    private void abrirUsuarios() {

        navigation.loadScreen(
                "usuarios",
                "/com/cibi/fxml/pages/Usuarios.fxml"
        );
    }

    @FXML
    private void abrirSobre() {

        navigation.loadScreen(
                "sobre",
                "/com/cibi/fxml/pages/Sobre.fxml"
        );
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        navigation = new NavigationManager(contentArea);

        navigation.loadScreen(
                "dashboard",
                "/com/cibi/fxml/pages/Dashboard.fxml"
        );
    }
}
