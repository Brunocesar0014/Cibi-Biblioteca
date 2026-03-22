package com.cibi.controller.pages;

import com.cibi.Service.AlunoService;
import com.cibi.model.Aluno;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

public class EmprestimosController {

    private boolean executado = false;

    @FXML
    private TextField input_matricula;

    @FXML
    private void BuscarAlunoPorMatricula() {
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
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Aluno não encontrado!");
            alert.setHeaderText(null);
            alert.setContentText("Matricula inválida");
            alert.showAndWait();
        }
    }

    @FXML
    public void ExibirAluno(Aluno aluno) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Aluno encontrado!");
        alert.setHeaderText(null);
        alert.setContentText("Nome: " + aluno.getNome() + "\nMatricula: " + aluno.getMatricula() + "\nTurma: " + aluno.getTurma());
        alert.showAndWait();
    }

    @FXML
    private void initialize() {

        input_matricula.textProperty().addListener((obs, oldValue, newValue) -> {

            if (newValue == null) {
                return;
            }

            // Remove não numéricos
            if (!newValue.matches("\\d*")) {
                Platform.runLater(()
                        -> input_matricula.setText(newValue.replaceAll("[^\\d]", ""))
                );
                return;
            }

            // Limita a 15 caracteres
            if (newValue.length() > 15) {
                Platform.runLater(()
                        -> input_matricula.setText(newValue.substring(0, 15))
                );
                return;
            }

            // Executa ação
            if (newValue.length() == 15 && !executado) {
                executado = true;

                BuscarAlunoPorMatricula();

                Platform.runLater(()
                        -> input_matricula.clear()
                );
            }

            if (newValue.length() < 15) {
                executado = false;
            }

        });
    }
}
