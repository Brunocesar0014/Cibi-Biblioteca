package com.cibi.main;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Tela_Principal extends Application{

    @Override
    public void start(Stage primaryStage) throws Exception {
        Image icon = new Image(getClass().getResourceAsStream("/com/cibi/imagen/icone.png"));
        primaryStage.getIcons().add(icon);

        Parent root = FXMLLoader.load(getClass().getResource("/com/cibi/fxml/Tela_Principal.fxml"));
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("Login");
        scene.getStylesheets().add(getClass().getResource("/com/cibi/css/style.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
