package com.cibi.controller.pages;

import com.cibi.model.Livro;
import com.cibi.repository.LivroRepository;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class LivrosController {
    @FXML
    private TextField txt_id_livro;
    
    @FXML
    private void onBuscarLivroPorId(ActionEvent event) {
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

    @FXML
    public void ExibirLivro(Livro livro) {
        System.out.println(livro.getId());
        System.out.println(livro.getTitulo());
        System.out.println(livro.getAutor());
        System.out.println(livro.getSinopse());
    }
}
