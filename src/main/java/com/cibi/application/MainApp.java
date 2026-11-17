package com.cibi.application;

import com.cibi.controller.PrimeiroAcessoController;
import com.cibi.repository.FuncionarioRepository;
import com.cibi.util.WindowUtils;
import com.cibi.util.DevTools;
import com.cibi.controller.pages.DevConsoleController;
import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class MainApp extends Application {

    private Stage devStage;

    @Override
    public void start(Stage primaryStage) throws Exception {

        if (DevTools.isModoDesenvolvedor()) {
            abrirDevConsole();
            return;
        }

        boolean existemContas;
        try {
            existemContas = FuncionarioRepository.existemContasDeAcesso();
        } catch (IllegalStateException e) {
            System.getLogger(MainApp.class.getName()).log(System.Logger.Level.WARNING,
                    "Não foi possível verificar o primeiro acesso", e);
            existemContas = true;
        }
        if (existemContas) {
            abrirLogin(primaryStage);
        } else {
            abrirPrimeiroAcesso(primaryStage);
        }
    }

    public void abrirLogin(Stage stage) throws IOException {

        Parent root = FXMLLoader.load(getClass().getResource("/com/cibi/fxml/Tela_Login.fxml"));
        Scene scene = new Scene(root, 520, 400);
        scene.getStylesheets().add(getClass().getResource("/com/cibi/css/principal.css").toExternalForm());
        mostrarTelaDeAcesso(stage, scene, "Login");
    }

    private void abrirPrimeiroAcesso(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/cibi/fxml/Tela_PrimeiroAcesso.fxml"));
        Parent root = loader.load();
        PrimeiroAcessoController controller = loader.getController();
        controller.setAoAbrirLogin(() -> {
            try {
                abrirLogin(stage);
            } catch (IOException e) {
                System.getLogger(MainApp.class.getName()).log(System.Logger.Level.ERROR,
                        "Erro ao abrir o login", e);
                Alert alerta = new Alert(Alert.AlertType.ERROR, "Não foi possível abrir a tela de login.");
                alerta.initOwner(stage);
                alerta.setHeaderText(null);
                alerta.showAndWait();
            }
        });
        mostrarTelaDeAcesso(stage, new Scene(root, 1000, 640), "CIBI");
    }

    private void mostrarTelaDeAcesso(Stage stage, Scene scene, String titulo) {
        if (stage.getIcons().isEmpty()) {
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/com/cibi/imagens/icone.png")));
        }
        stage.setResizable(false);
        stage.setTitle(titulo);
        stage.setScene(scene);
        WindowUtils.configurarConfirmacaoFechamento(stage);
        stage.sizeToScene();
        stage.show();
        stage.centerOnScreen();
    }

    public void abrirDevConsole() throws Exception {

        if (devStage != null && devStage.isShowing()) {
            devStage.toFront();
            return;
        }

        devStage = new Stage();

        Image icon = new Image(getClass().getResourceAsStream("/com/cibi/imagens/icone.png"));
        devStage.getIcons().add(icon);

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource(
                        "/com/cibi/fxml/DevConsole.fxml"
                )
        );

        Parent root = loader.load();

        devStage.setResizable(false);
        devStage.setScene(new Scene(root, 400, 600));
        devStage.setOnCloseRequest(event -> {event.consume();});
        devStage.setTitle("CIBI — Development Console");

        DevConsoleController controller = loader.getController();

        controller.setIniciarAplicacaoCallback(() -> {
            try {
                iniciarAplicacao();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        
        devStage.show();

    }

    public void iniciarAplicacao() throws Exception {

        if (DevTools.isIniciarPrimeiroAcesso()) {
            abrirPrimeiroAcesso(new Stage());
        } else if (DevTools.isIgnorarLogin()) {
            abrirTelaPrincipal();
        } else {
            abrirLogin(new Stage());
        }
    }

    public void abrirTelaPrincipal() throws Exception {

        Parent root = FXMLLoader.load(getClass().getResource("/com/cibi/fxml/Tela_Principal.fxml"));

        Stage principal = new Stage();
        Image icon = new Image(getClass().getResourceAsStream("/com/cibi/imagens/icone.png"));
        principal.getIcons().add(icon);
        principal.setScene(new Scene(root));
        principal.setTitle("Tela Principal");
        principal.setMinWidth(1280);
        principal.setMinHeight(720);
        
        WindowUtils.configurarConfirmacaoFechamento(principal);

        principal.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
