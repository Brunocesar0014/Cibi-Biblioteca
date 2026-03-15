package com.cibi.controller;

import com.cibi.model.Aluno;
import com.cibi.model.Livro;
import com.cibi.Service.AlunoService;
import com.cibi.repository.LivroRepository;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class PrincipalController {

    private boolean executado = false;

    @FXML
    private TextField txt_id_livro;

    @FXML
    private TextField input_matricula;

    @FXML
    private void BuscarAluno() {
        long matricula = 0;

        try {
            matricula = Long.parseLong(input_matricula.getText());
        } catch (Exception e) {
            throw new IllegalArgumentException("Digite um valor válido");
        }

        try {
            Aluno aluno = new Aluno();

            aluno = AlunoService.BuscarAlunoPorMaticula(matricula);

            if (aluno.getNome() != null) {
                ExibirAluno(aluno);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    public void ExibirAluno(Aluno aluno) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Aluno encontrado!");
        alert.setHeaderText(null);
        alert.setContentText("Nome: " + aluno.getNome() + "\nMatricula: " + aluno.getMatricula() + "\nTurma: " + aluno.getTurma());
        alert.showAndWait();
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
    public void ExibirLivro(Livro livro) {
        System.out.println(livro.getId());
        System.out.println(livro.getTitulo());
        System.out.println(livro.getAutor());
        System.out.println(livro.getSinopse());
    }

    @FXML
    private BorderPane BorderPane_fundo;

    @FXML
    private void initialize() {
        // Imagem/painel mantém proporção 16:9
        BorderPane_fundo.prefHeightProperty().bind(
                BorderPane_fundo.widthProperty().multiply(9.0 / 16.0)
        );

        /*
        input_matricula.textProperty().addListener((obs, oldValue, newValue) -> {

            if (!newValue.matches("\\d*")) {
                input_matricula.setText(newValue.replaceAll("[^\\d]", ""));
                return;
            }

            if (newValue.length() > 15) {
                input_matricula.setText(newValue.substring(0, 15));
                return;
            }

            if (newValue.length() == 15 && !executado) {
                executado = true;
                //código
                BuscarAluno();
                input_matricula.setText("");
            }

            if (newValue.length() < 15) {
                executado = false;
            }

        });*/

    }

}
