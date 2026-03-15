package com.cibi.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

import java.util.HashMap;
import java.util.Map;

public class NavigationManager {
    
    private StackPane container;

    private Map<String, Parent> screenCache = new HashMap<>();

    public NavigationManager(StackPane container) {
        this.container = container;
    }
    
    public void loadScreen(String name, String fxmlPath) {

        try {

            Parent root;

            if (screenCache.containsKey(name)) {

                root = screenCache.get(name);

            } else {

                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
                root = loader.load();

                screenCache.put(name, root);

            }

            container.getChildren().setAll(root);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
}
