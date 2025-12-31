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

//objetos
import com.cibi.model.bean.Conta;
import com.cibi.model.dao.ContaDAO;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Tela_Login {

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
    private Button butao_login;

    @FXML
    private Label label_depuracao;

    @FXML
    private void RealizarLogin(ActionEvent event) {
        Conta c = new Conta();
        String usuario = txtInput.getText();
        String senha = txtPassword.getText();

        if (senha.equals("") || usuario.equals("")) {
            label_depuracao.setText("Preencha todos os capos corretamente");
            label_depuracao.setStyle("-fx-text-fill: #ED2100;");
        } else {
            label_depuracao.setStyle("-fx-text-fill: transparent;");

            ContaDAO dao = new ContaDAO();
            c = dao.buscarPorLogin(usuario, senha);

            if (c != null) {
                try{
                    // 1. Carrega o arquivo FXML da nova janela
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/cibi/fxml/Tela_Principal.fxml"));
                    Parent root = loader.load();

                    // 2. Cria o novo Stage (Janela)
                    Stage novaStage = new Stage();
                    novaStage.setScene(new Scene(root));
                    novaStage.setTitle("Nova Janela");
                    novaStage.show();
                    
                    Stage stageAtual = (Stage) lblFloating.getScene().getWindow();
                    stageAtual.close();
                }catch(IOException e){
                    System.out.println("erro ao mudar janela");
                }
            } else {
                label_depuracao.setText("Usuario ou senha inválidos");
                label_depuracao.setStyle("-fx-text-fill: #ED2100;");
            }
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
