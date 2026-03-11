package com.cibi.controller;

import com.cibi.Service.ContaService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.util.Duration;


public class LoginController {

    @FXML private Pane containerPrincipal; // Use o tipo do seu container pai

    @FXML public Label label_depuracao;
    
    @FXML
    private void cliqueFora() {
        // Quando clicar no fundo, o container solicita o foco, 
        // tirando o foco do TextField e disparando o Listener de volta ao normal.
        containerPrincipal.requestFocus();
    }

    //animação textField
    @FXML private TextField txtInput;
    @FXML private Label lblFloating;
    @FXML private PasswordField txtPassword;
    @FXML private Label lblPassword;

    @FXML
    private void onLogarConta(ActionEvent event) {
        String usuario = txtInput.getText();
        String senha = txtPassword.getText();

        try {
            boolean ok = ContaService.RealizarLogin(usuario, senha);

            if (ok) {
                abrirTelaPrincipalEFecharLogin();
            }

        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            label_depuracao.setText("Usuário ou senha inválidos");
            label_depuracao.setStyle("-fx-text-fill: #e71c1c");
        }

    }
    
    @FXML
    private void onPularLogin(ActionEvent event) {
        try{
            abrirTelaPrincipalEFecharLogin();
        }catch(RuntimeException e){
            System.out.println(e.getMessage());
        }
    }

    private void abrirTelaPrincipalEFecharLogin() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/cibi/fxml/Tela_Principal.fxml"));

            Stage principal = new Stage();
            principal.setScene(new Scene(root));
            principal.setTitle("Tela Principal");
            principal.setMinWidth(800);
            principal.setMinHeight(600);
            principal.show();

            Stage loginStage = (Stage) txtInput.getScene().getWindow();
            loginStage.close();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao abrir a tela principal", e);
        }
    }

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
