package com.cibi.controller.pages;

import com.cibi.Service.DashboardService;
import com.cibi.model.DashboardResumo;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.text.NumberFormat;
import java.util.Locale;

public class DashboardController {

    @FXML private Label lblLivrosCadastrados;
    @FXML private Label lblUsuariosCadastrados;
    @FXML private Label lblResumoStatus;

    private Runnable navegarLivros;
    private Runnable navegarUsuarios;
    private Runnable navegarEmprestimos;

    @FXML
    private void initialize() {
        Task<DashboardResumo> task = new Task<>() {
            @Override
            protected DashboardResumo call() {
                return new DashboardService().buscarResumo();
            }
        };

        task.setOnSucceeded(event -> {
            DashboardResumo resumo = task.getValue();
            NumberFormat formato = NumberFormat.getIntegerInstance(Locale.forLanguageTag("pt-BR"));
            lblLivrosCadastrados.setText(formato.format(resumo.livrosCadastrados()));
            lblUsuariosCadastrados.setText(formato.format(resumo.usuariosCadastrados()));
            lblResumoStatus.setText("");
        });
        task.setOnFailed(event -> lblResumoStatus.setText("Não foi possível carregar os totais"));

        Thread worker = new Thread(task, "cibi-dashboard-resumo");
        worker.setDaemon(true);
        worker.start();
    }

    public void configurarNavegacao(Runnable livros, Runnable usuarios, Runnable emprestimos) {
        navegarLivros = livros;
        navegarUsuarios = usuarios;
        navegarEmprestimos = emprestimos;
    }

    @FXML private void abrirLivros() { navegarLivros.run(); }
    @FXML private void abrirUsuarios() { navegarUsuarios.run(); }
    @FXML private void abrirEmprestimos() { navegarEmprestimos.run(); }
}
