package com.cibi.controller;

import com.cibi.model.Livro;
import com.cibi.repository.LivroRepository;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

public class PrincipalController {
    
    @FXML private TextField txt_id_livro;
    
    @FXML
    private void onBuscarLivro(ActionEvent event) {
        int id = 0;
        
        try {
            id = Integer.parseInt(txt_id_livro.getText());
        } catch (Exception e) {
            throw new IllegalArgumentException("Digite um valor válido");
        }
        
        try {
            Livro livro = new Livro();

            livro = LivroRepository.buscarPorId(id);
            
            if (livro.getTitulo() != null) {
                ExibirLivro(livro);
            }
            
        } catch (Exception e) {
            System.out.println("erro ao buscar livro");
        }
    }
    
    @FXML private BorderPane BorderPane_fundo;
    
    @FXML
    public void ExibirLivro(Livro livro){
        System.out.println(livro.getId());
        System.out.println(livro.getTitulo());
        System.out.println(livro.getAutor());
        System.out.println(livro.getSinopse());
    }
    
    @FXML
    private void initialize() {
        // Imagem/painel mantém proporção 16:9
    BorderPane_fundo.prefHeightProperty().bind(
        BorderPane_fundo.widthProperty().multiply(9.0 / 16.0)
    );

    /* Fonte escala com a janela
    myLabel.styleProperty().bind(
        Bindings.concat("-fx-font-size: ",
            rootPane.widthProperty().multiply(0.02), "px;")
    );
    */
    }
}
