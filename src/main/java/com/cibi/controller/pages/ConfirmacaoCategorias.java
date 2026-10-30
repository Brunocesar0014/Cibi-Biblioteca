package com.cibi.controller.pages;

import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.Effect;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.StageStyle;

/** Confirmações restritas ao gerenciamento de categorias de livros. */
final class ConfirmacaoCategorias {
    private ConfirmacaoCategorias() {}

    static boolean excluir(VBox gerenciador, String categoria) {
        Text nome = new Text("“" + categoria + "”");
        nome.getStyleClass().add("category-confirm-name");
        return mostrar(gerenciador, true, "Excluir categoria",
                new TextFlow(new Text("Tem certeza que deseja excluir a categoria "), nome, new Text("?")),
                "A categoria será removida da lista. A exclusão será aplicada ao salvar as alterações.",
                "Excluir categoria");
    }

    static boolean salvar(VBox gerenciador) {
        return mostrar(gerenciador, false, "Salvar alterações",
                new TextFlow(new Text("Deseja salvar as alterações nas categorias?")),
                "As alterações serão aplicadas nesta sessão. Renomeações também atualizarão os livros vinculados.",
                "Salvar alterações");
    }

    private static boolean mostrar(VBox gerenciador, boolean exclusao, String titulo,
                                   TextFlow pergunta, String explicacao, String acao) {
        Dialog<ButtonType> dialogo = new Dialog<>();
        java.util.concurrent.atomic.AtomicBoolean confirmado = new java.util.concurrent.atomic.AtomicBoolean();
        dialogo.setTitle(titulo);
        dialogo.initOwner(gerenciador.getScene().getWindow());
        dialogo.initStyle(StageStyle.TRANSPARENT);
        DialogPane painel = dialogo.getDialogPane();
        painel.getStyleClass().add("book-category-dialog");
        painel.getStylesheets().add(ConfirmacaoCategorias.class.getResource("/com/cibi/css/pages/Livros.css").toExternalForm());
        painel.getButtonTypes().add(ButtonType.CANCEL);
        Node cancelarNativo = painel.lookupButton(ButtonType.CANCEL);
        cancelarNativo.setVisible(false);
        cancelarNativo.setManaged(false);
        Node barraNativa = painel.lookup(".button-bar");
        if (barraNativa != null) {
            barraNativa.setVisible(false);
            barraNativa.setManaged(false);
        }

        Label cabecalho = new Label(titulo);
        cabecalho.getStyleClass().add("category-confirm-title");
        Region espaco = new Region();
        HBox.setHgrow(espaco, Priority.ALWAYS);
        Button fechar = new Button("×");
        fechar.setId("btnFecharConfirmacao");
        fechar.setAccessibleText("Cancelar confirmação");
        fechar.getStyleClass().add("category-modal-close");
        HBox topo = new HBox(cabecalho, espaco, fechar);
        topo.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        Region desenho = new Region();
        desenho.getStyleClass().addAll("category-confirm-icon", exclusao ? "bi-trash-fill" : "category-confirm-check");
        StackPane circulo = new StackPane(desenho);
        circulo.getStyleClass().addAll("category-confirm-icon-circle",
                exclusao ? "category-confirm-icon-danger" : "category-confirm-icon-save");
        pergunta.getStyleClass().add("category-confirm-question");
        pergunta.setMinWidth(0);
        Label detalhes = new Label(explicacao);
        detalhes.setWrapText(true);
        detalhes.setMaxWidth(Double.MAX_VALUE);
        detalhes.getStyleClass().add("category-confirm-details");
        VBox textos = new VBox(7, pergunta, detalhes);
        textos.setMinWidth(0);
        HBox.setHgrow(textos, Priority.ALWAYS);
        HBox corpo = new HBox(18, circulo, textos);
        corpo.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        Button cancelar = new Button("Cancelar");
        cancelar.setId("btnCancelarConfirmacao");
        cancelar.getStyleClass().add("category-outline-button");
        cancelar.setCancelButton(true);
        Button confirmar = new Button(acao);
        confirmar.setId("btnConfirmarCategoria");
        confirmar.getStyleClass().add(exclusao ? "category-confirm-danger" : "category-primary-button");
        HBox acoes = new HBox(12, cancelar, confirmar);
        acoes.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
        VBox conteudo = new VBox(20, topo, corpo, acoes);
        conteudo.setId("confirmacaoCategorias");
        conteudo.getStyleClass().addAll("book-category-modal", "book-category-confirmation");
        conteudo.setPrefWidth(Math.max(500, Math.min(680, gerenciador.getScene().getWindow().getWidth() - 60)));
        painel.setContent(conteudo);
        Runnable desistir = dialogo::close;
        fechar.setOnAction(e -> desistir.run());
        cancelar.setOnAction(e -> desistir.run());
        confirmar.setOnAction(e -> { confirmado.set(true); dialogo.close(); });
        painel.addEventFilter(KeyEvent.KEY_PRESSED, evento -> {
            if (evento.getCode() == KeyCode.ESCAPE) {
                evento.consume();
                desistir.run();
            }
        });

        Effect anterior = gerenciador.getEffect();
        GaussianBlur desfoque = new GaussianBlur(2);
        desfoque.setInput(anterior);
        ColorAdjust sombrear = new ColorAdjust();
        sombrear.setBrightness(-0.12);
        sombrear.setInput(desfoque);
        dialogo.setOnShown(e -> {
            painel.getScene().setFill(Color.TRANSPARENT);
            gerenciador.setEffect(sombrear);
            cancelar.requestFocus();
        });
        try {
            dialogo.showAndWait();
            return confirmado.get();
        } finally {
            gerenciador.setEffect(anterior);
        }
    }
}
