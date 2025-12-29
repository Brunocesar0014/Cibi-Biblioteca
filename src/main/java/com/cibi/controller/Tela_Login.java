package com.cibi.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

//animação textField
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

public class Tela_Login {

    @FXML
    private Button butao_login;

    @FXML
    private void RealizarLogin(ActionEvent event) {
        System.out.println("teste");
    }
    
    @FXML
    private Pane containerPrincipal; // Use o tipo do seu container pai

    @FXML
    private void cliqueFora() {
        // Quando clicar no fundo, o container solicita o foco, 
        // tirando o foco do TextField e disparando o Listener de volta ao normal.
        containerPrincipal.requestFocus();
    }

    //animação textField
    @FXML
    private TextField txtInput;
    @FXML
    private Label lblFloating;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private Label lblPassword;

    @FXML
    private void initialize() {
        // Monitorar o foco do campo de texto
        txtInput.focusedProperty().addListener((obs, oldVal, newVal) -> {
            updateLabel(newVal);
        });

        // Listener para o campo de senha
        txtPassword.focusedProperty().addListener((obs, oldVal, newVal) -> {
            animateLabel(txtPassword, lblPassword, newVal);
        });

    }

    private void updateLabel(boolean isFocused) {
        TranslateTransition translate = new TranslateTransition(Duration.millis(200), lblFloating);
        ScaleTransition scale = new ScaleTransition(Duration.millis(200), lblFloating);

        // Se ganhar foco ou se o campo não estiver vazio, o label sobe
        if (isFocused || !txtInput.getText().isEmpty()) {
            translate.setToY(-15);
            scale.setToX(0.85);
            scale.setToY(0.85);
            lblFloating.setStyle("-fx-text-fill: #1e272e; -fx-font-weight: bold;");
        } else {
            // Se perder o foco e estiver vazio, volta para o centro
            translate.setToY(0);
            scale.setToX(1.0);
            scale.setToY(1.0);
            lblFloating.setStyle("-fx-text-fill: #666666; -fx-font-weight: normal;");
        }

        ParallelTransition anim = new ParallelTransition(translate, scale);
        anim.play();
    }

    private void animateLabel(PasswordField field, Label label, boolean isFocused) {
        TranslateTransition translate = new TranslateTransition(Duration.millis(200), label);
        ScaleTransition scale = new ScaleTransition(Duration.millis(200), label);

        // Verifica se está focado ou se tem texto (mesmo que sejam os pontos da senha)
        if (isFocused || !field.getText().isEmpty()) {
            translate.setToY(-15); 
            scale.setToX(0.8);
            scale.setToY(0.8);
            label.setStyle("-fx-text-fill: #1e272e; -fx-font-weight: bold;");
        } else {
            translate.setToY(0);
            scale.setToX(1.0);
            scale.setToY(1.0);
            label.setStyle("-fx-text-fill: #666666; -fx-font-weight: normal;");
        }

        ParallelTransition anim = new ParallelTransition(translate, scale);
        anim.play();
    }
}
