package com.cibi.controller;

import com.cibi.Service.FuncionarioService;
import javafx.beans.binding.Bindings;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class PrimeiroAcessoController {

    @FXML private VBox formulario;
    @FXML private VBox confirmacao;
    @FXML private GridPane campos;
    @FXML private TextField nome;
    @FXML private TextField email;
    @FXML private TextField telefone;
    @FXML private TextField usuario;
    @FXML private Label labelSenha;
    @FXML private PasswordField senha;
    @FXML private TextField senhaVisivel;
    @FXML private ToggleButton mostrarSenha;
    @FXML private Label labelRepetirSenha;
    @FXML private PasswordField repetirSenha;
    @FXML private TextField repetirSenhaVisivel;
    @FXML private ToggleButton mostrarRepetirSenha;
    @FXML private Label mensagem;
    @FXML private Hyperlink loginExistente;
    @FXML private Button botaoCriarConta;
    @FXML private Button botaoLogin;

    private Runnable aoAbrirLogin;

    @FXML
    private void initialize() {
        configurarVisibilidade(labelSenha, senha, senhaVisivel, mostrarSenha, "senha");
        configurarVisibilidade(labelRepetirSenha, repetirSenha, repetirSenhaVisivel,
                mostrarRepetirSenha, "confirmação de senha");
        formulario.managedProperty().bind(formulario.visibleProperty());
        confirmacao.managedProperty().bind(confirmacao.visibleProperty());
        mensagem.managedProperty().bind(mensagem.textProperty().isNotEmpty());
        loginExistente.managedProperty().bind(loginExistente.visibleProperty());
    }

    public void setAoAbrirLogin(Runnable aoAbrirLogin) {
        this.aoAbrirLogin = aoAbrirLogin;
    }

    @FXML
    private void criarConta() {
        String nomeInformado = nome.getText();
        String emailInformado = email.getText();
        String telefoneInformado = telefone.getText();
        String usuarioInformado = usuario.getText();
        String senhaInformada = senha.getText();
        String confirmacaoInformada = repetirSenha.getText();

        Task<Boolean> cadastro = new Task<>() {
            @Override
            protected Boolean call() {
                return FuncionarioService.cadastrarPrimeiro(nomeInformado, emailInformado,
                        telefoneInformado, usuarioInformado, senhaInformada, confirmacaoInformada);
            }
        };
        cadastro.setOnSucceeded(evento -> {
            definirEmAndamento(false);
            if (cadastro.getValue()) {
                senha.clear();
                repetirSenha.clear();
                formulario.setVisible(false);
                confirmacao.setVisible(true);
                botaoCriarConta.setDefaultButton(false);
                botaoLogin.setDefaultButton(true);
                botaoLogin.requestFocus();
            } else {
                mensagem.setText("O primeiro funcionário já foi cadastrado.");
                loginExistente.setVisible(true);
                botaoCriarConta.setDisable(true);
            }
        });
        cadastro.setOnFailed(evento -> {
            definirEmAndamento(false);
            Throwable erro = cadastro.getException();
            if (erro instanceof IllegalArgumentException) {
                mensagem.setText(erro.getMessage());
            } else {
                System.getLogger(getClass().getName()).log(System.Logger.Level.ERROR, "Erro no primeiro acesso", erro);
                mensagem.setText("Não foi possível salvar a conta. Verifique a conexão e tente novamente.");
            }
        });

        mensagem.setText("");
        definirEmAndamento(true);
        Thread.ofVirtual().start(cadastro);
    }

    @FXML
    private void irParaLogin() {
        aoAbrirLogin.run();
    }

    private void definirEmAndamento(boolean emAndamento) {
        campos.setDisable(emAndamento);
        botaoCriarConta.setDisable(emAndamento);
        botaoCriarConta.setText(emAndamento ? "Criando conta…" : "Criar conta");
    }

    private void configurarVisibilidade(Label label, PasswordField oculto, TextField visivel,
            ToggleButton alternar, String campo) {
        visivel.textProperty().bindBidirectional(oculto.textProperty());
        visivel.visibleProperty().bind(alternar.selectedProperty());
        oculto.visibleProperty().bind(alternar.selectedProperty().not());
        visivel.managedProperty().bind(visivel.visibleProperty());
        oculto.managedProperty().bind(oculto.visibleProperty());
        label.setLabelFor(oculto);

        var descricao = Bindings.when(alternar.selectedProperty())
                .then("Ocultar " + campo).otherwise("Mostrar " + campo);
        alternar.accessibleTextProperty().bind(descricao);
        Tooltip dica = new Tooltip();
        dica.textProperty().bind(descricao);
        alternar.setTooltip(dica);
        alternar.setOnAction(evento -> {
            TextField destino = alternar.isSelected() ? visivel : oculto;
            label.setLabelFor(destino);
            destino.requestFocus();
            destino.end();
        });
    }
}
