package com.cibi.controller;

import com.cibi.model.Aluno;
import com.cibi.model.Livro;
import com.cibi.Service.AlunoService;
import com.cibi.repository.LivroRepository;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

public class PrincipalController {
    
    @FXML private TextField txt_id_livro;
    
    @FXML private TextField input_matricula;
    
    @FXML
    private void onBuscarAluno(ActionEvent event) {
        long matricula = 0;
        
        try{
            matricula = Long.parseLong(input_matricula.getText());
        }catch(Exception e){
            throw new IllegalArgumentException("Digite um valor válido");
        }
        
        try {
            Aluno aluno = new Aluno();
            
            aluno = AlunoService.BuscarAlunoPorMaticula(matricula);
            
            if (aluno.getNome() != null) {
                ExibirAluno(aluno);
            }
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
    
    @FXML
    public void ExibirAluno(Aluno aluno){
        System.out.println(aluno.getMatricula());
        System.out.println(aluno.getNome());
        System.out.println(aluno.getTurma());
    }
    
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
    
    @FXML
    public void ExibirLivro(Livro livro){
        System.out.println(livro.getId());
        System.out.println(livro.getTitulo());
        System.out.println(livro.getAutor());
        System.out.println(livro.getSinopse());
    }
    
    @FXML private BorderPane BorderPane_fundo;
    
    
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
