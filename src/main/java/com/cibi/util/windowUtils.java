package com.cibi.util;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class WindowUtils {

    public static void configurarConfirmacaoFechamento(Stage stage) {
        boolean[] confirmacaoAberta = {false};

        stage.setOnCloseRequest(event -> {
            event.consume();
            if (confirmacaoAberta[0]) {
                return;
            }

            confirmacaoAberta[0] = true;
            try {
                if (confirmarFechamento(stage)) {
                    stage.close();
                }
            } finally {
                confirmacaoAberta[0] = false;
            }
        });
    }

    private static boolean confirmarFechamento(Stage stage) {
        ButtonType botaoCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType botaoOk = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initOwner(stage);
        alert.initStyle(StageStyle.TRANSPARENT);

        alert.setTitle("Sair");
        alert.setHeaderText(null);
        alert.setGraphic(null);
        alert.setContentText(null);
        alert.getButtonTypes().setAll(botaoCancelar, botaoOk);

        // ---- Cabeçalho customizado (título + botão X) ----
        Label titulo = new Label("Deseja realmente fechar o programa?");
        titulo.getStyleClass().add("titulo-confirmacao");
        titulo.setWrapText(true);

        Button botaoFechar = new Button("\u2715"); // ×
        botaoFechar.getStyleClass().add("botao-fechar-x");
        botaoFechar.setOnAction(e -> alert.setResult(botaoCancelar));

        HBox linhaTopo = new HBox(titulo, botaoFechar);
        linhaTopo.setAlignment(Pos.TOP_LEFT);
        HBox.setHgrow(titulo, Priority.ALWAYS);

        Label subtitulo = new Label("A aplicação será encerrada.");
        subtitulo.getStyleClass().add("subtitulo-confirmacao");
        subtitulo.setWrapText(true);

        VBox corpo = new VBox(16, linhaTopo, subtitulo);
        corpo.setPadding(new Insets(28, 28, 8, 28));
        alert.getDialogPane().setContent(corpo);

        var recurso = WindowUtils.class
                .getResource("/com/cibi/css/components/alerts.css");

        if (recurso != null) {
            alert.getDialogPane().getStylesheets().add(recurso.toExternalForm());
            alert.getDialogPane().getStyleClass().add("alert-confirmacao");
        }

        Button btnCancelar = (Button) alert.getDialogPane().lookupButton(botaoCancelar);
        btnCancelar.getStyleClass().add("botao-cancelar");

        Button btnOk = (Button) alert.getDialogPane().lookupButton(botaoOk);
        btnOk.getStyleClass().add("botao-confirmar");
        btnOk.setDefaultButton(true);

        try (var animacao = AnimacaoModal.configurar(alert, stage.getScene().getRoot(),
                AnimacaoModal.PADRAO, resultado -> resultado != botaoOk)) {
            return alert.showAndWait().orElse(botaoCancelar) == botaoOk;
        }
    }
}
