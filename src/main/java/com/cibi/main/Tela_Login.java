package com.cibi.main;

import javafx.application.Application; // Classe base para apps JavaFX
import javafx.fxml.FXMLLoader;           // Carrega arquivos FXML criados no Scene Builder
import javafx.scene.Parent;              // Tipo genérico para a raiz carregada do FXML
import javafx.scene.Scene;               // Contêiner visual principal (janela interna)
import javafx.stage.Stage;               // Representa a janela do sistema

public class Tela_Login extends Application {  // Ponto de entrada JavaFX — extende Application
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Carrega o arquivo FXML da pasta resources; mantém separação view/logica
        Parent root = FXMLLoader.load(getClass().getResource("/com/cibi/fxml/Tela_Login.fxml"));
        
        // Cria cena com a raiz; você pode especificar largura/altura iniciais aqui
        Scene scene = new Scene(root, 900, 500);
        
        // Adiciona um arquivo CSS para temas/estilo (opcional, mas recomendado)
        scene.getStylesheets().add(getClass().getResource("/com/cibi/css/style.css").toExternalForm());
        
        // Define o título da janela (visível ao usuário)
        primaryStage.setTitle("Curso JavaFX - Módulo 1");
        // Associa cena à Stage
        primaryStage.setScene(scene);
        // Mostra a janela no desktop
        primaryStage.show();
    }

    public static void main(String[] args) {
        // Lança a aplicação JavaFX; obrigatório quando start é sobrescrito
        launch(args);
    }
}