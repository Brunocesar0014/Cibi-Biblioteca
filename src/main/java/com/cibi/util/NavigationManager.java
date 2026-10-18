package com.cibi.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class NavigationManager {
    
    private final StackPane container;

    private final Map<String, Parent> screenCache = new HashMap<>();
    private final Map<String, Object> controllerCache = new HashMap<>();

    public NavigationManager(StackPane container) {
        this.container = Objects.requireNonNull(container, "container obrigatório");
    }
    
    public Object loadScreen(String name, String fxmlPath) {

        try {

            Parent root;

            if (screenCache.containsKey(name)) {

                root = screenCache.get(name);

            } else {

                URL resource = getClass().getResource(fxmlPath);
                if (resource == null) {
                    throw new IllegalArgumentException("FXML não encontrado: " + fxmlPath);
                }
                FXMLLoader loader = new FXMLLoader(resource);
                root = loader.load();

                screenCache.put(name, root);
                controllerCache.put(name, loader.getController());

            }

            container.getChildren().setAll(root);
            return controllerCache.get(name);

        } catch (IOException e) {
            throw new IllegalStateException("Erro ao carregar tela: " + fxmlPath, e);
        }
    }
    
    
}
