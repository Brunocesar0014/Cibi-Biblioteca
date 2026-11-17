package com.cibi.controller.pages;

import java.io.ByteArrayInputStream;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;

final class FotoAluno {
    private FotoAluno() {}

    static Image decodificar(byte[] jpeg) {
        if (jpeg == null) return null;
        var foto = new Image(new ByteArrayInputStream(jpeg), 120, 120, true, true);
        return foto.isError() ? null : foto;
    }

    static void configurar(ImageView vista, Node alternativa, double diametro) {
        double raio = diametro / 2;
        vista.setFitWidth(diametro);
        vista.setFitHeight(diametro);
        vista.setPreserveRatio(true);
        vista.setSmooth(true);
        vista.setClip(new Circle(raio, raio, raio));
        vista.visibleProperty().bind(vista.imageProperty().isNotNull());
        alternativa.visibleProperty().bind(vista.imageProperty().isNull());
        vista.imageProperty().addListener((obs, anterior, foto) -> {
            if (foto == null) {
                vista.setViewport(null);
                return;
            }
            double lado = Math.min(foto.getWidth(), foto.getHeight());
            vista.setViewport(new Rectangle2D((foto.getWidth() - lado) / 2, (foto.getHeight() - lado) / 2,
                    lado, lado));
        });
    }
}
