package com.cibi.application;

import com.cibi.util.windowUtils;
import com.cibi.util.DevTools;
import com.cibi.controller.pages.DevConsoleController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class MainApp extends Application {

    private Stage devStage;

    @Override
    public void start(Stage primaryStage) throws Exception {

        if (DevTools.isModoDesenvolvedor()) {
            abrirDevConsole();
        } else {
            abrirLogin(primaryStage);
        }
    }

    public void abrirLogin(Stage stage) throws Exception {

        Image icon = new Image(getClass().getResourceAsStream("/com/cibi/imagens/icone.png"));
        stage.getIcons().add(icon);

        stage.setResizable(false);
        Parent root = FXMLLoader.load(getClass().getResource("/com/cibi/fxml/Tela_Login.fxml"));
        Scene scene = new Scene(root, 520, 400);
        stage.setTitle("Login");
        scene.getStylesheets().add(getClass().getResource("/com/cibi/css/principal.css").toExternalForm());
        stage.setScene(scene);
        
        windowUtils.configurarConfirmacaoFechamento(stage);
        
        stage.show();

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

        if (DevTools.isIgnorarLogin()) {
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
        
        windowUtils.configurarConfirmacaoFechamento(principal);

        principal.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
