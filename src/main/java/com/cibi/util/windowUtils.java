package com.cibi.util;

import java.util.stream.Collectors;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
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

        return mostrarConfirmacao(stage.getScene().getRoot(), "Sair", "Deseja realmente fechar o programa?",
                "A aplicação será encerrada.", botaoCancelar, botaoOk) == botaoOk;
    }

    public static ButtonType mostrarConfirmacao(Parent origem, String tituloJanela, String pergunta,
                                                String mensagem, ButtonType cancelar, ButtonType confirmar,
                                                ButtonType... alternativas) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initOwner(origem.getScene().getWindow());
        TemaVisual.herdar(alert.getDialogPane(), origem);
        alert.initStyle(StageStyle.TRANSPARENT);

        alert.setTitle(tituloJanela);
        alert.setHeaderText(null);
        alert.setGraphic(null);
        alert.setContentText(null);
        alert.getButtonTypes().setAll(cancelar);
        alert.getButtonTypes().addAll(alternativas);
        alert.getButtonTypes().add(confirmar);

        Label titulo = new Label(pergunta);
        titulo.getStyleClass().add("titulo-confirmacao");
        titulo.setWrapText(true);
        boolean temAlternativas = alternativas.length > 0;
        if (temAlternativas) {
            titulo.setMaxWidth(Double.MAX_VALUE);
            alert.getDialogPane().getStyleClass().add("alert-confirmacao-com-alternativas");
        }

        Button botaoFechar = new Button("\u2715"); // ×
        botaoFechar.getStyleClass().add("botao-fechar-x");
        botaoFechar.setAccessibleText(cancelar.getText());
        botaoFechar.setOnAction(e -> alert.setResult(cancelar));

        HBox linhaTopo = new HBox(titulo, botaoFechar);
        linhaTopo.setAlignment(Pos.TOP_LEFT);
        HBox.setHgrow(titulo, Priority.ALWAYS);

        Label subtitulo = new Label(mensagem);
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

        for (ButtonType acao : alert.getButtonTypes()) {
            Button botao = (Button) alert.getDialogPane().lookupButton(acao);
            botao.getStyleClass().add(acao == confirmar ? "botao-confirmar" : "botao-cancelar");
            botao.setDefaultButton(acao == confirmar);
            if (temAlternativas) {
                ButtonBar.setButtonUniformSize(botao, false);
                botao.setMinWidth(Region.USE_PREF_SIZE);
            }
        }
        if (temAlternativas && alert.getDialogPane().lookup(".button-bar") instanceof ButtonBar barra) {
            String ordem = alert.getButtonTypes().stream().map(tipo -> tipo.getButtonData().getTypeCode())
                    .collect(Collectors.joining());
            barra.setButtonOrder(ordem);
        }

        try (var animacao = AnimacaoModal.configurar(alert, origem.getScene().getRoot(),
                AnimacaoModal.PADRAO, resultado -> resultado != confirmar)) {
            return alert.showAndWait().orElse(cancelar);
        }
    }
}
