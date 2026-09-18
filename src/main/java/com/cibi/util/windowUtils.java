package com.cibi.util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

public class windowUtils {

    /*
    public static void configurarConfirmacaoFechamento(Stage stage) {

        stage.setOnCloseRequest(event -> {

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

            alert.setTitle("Sair");
            alert.setHeaderText("Deseja realmente fechar o programa?");
            alert.setContentText("A aplicação será encerrada.");

            alert.initOwner(stage);

            alert.getDialogPane().getStylesheets().add(
                    windowUtils.class
                            .getResource("/com/cibi/css/components/alerts.css")
                            .toExternalForm()
            );

            if (alert.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
                event.consume();
            }

        });
    }*/

    public static void configurarConfirmacaoFechamento(Stage stage) {

        stage.setOnCloseRequest(event -> {

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

            alert.setTitle("Sair");
            alert.setHeaderText("Deseja realmente fechar o programa?");
            alert.setContentText("A aplicação será encerrada.");

            alert.initOwner(stage);

            var recurso = windowUtils.class
                    .getResource("/com/cibi/css/components/alerts.css");

            if (recurso != null) {

                alert.getDialogPane()
                        .getStylesheets()
                        .add(recurso.toExternalForm());

                alert.getDialogPane()
                        .getStyleClass()
                        .add("alert-confirmacao");
            }

            ButtonType resultado = alert.showAndWait()
                    .orElse(ButtonType.CANCEL);

            if (resultado != ButtonType.OK) {
                event.consume();
            }
        });
    }

}
