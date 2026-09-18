package com.cibi.controller.pages;

import com.cibi.Service.ColaboradorService;
import com.cibi.model.Colaborador;
import java.util.function.UnaryOperator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

public class UsuariosController {

    private static final int MAX_DIGITOS_TELEFONE = 11;
    
    //inputs
    @FXML private TextField input_nome;
    @FXML private TextField input_telefone;
    @FXML private TextField input_matricula;
    @FXML private ComboBox<String> input_turma;
    @FXML private TextField input_senha;
    @FXML private TextField input_confirmarSenha;

    @FXML
    private void cadastrarColaborador(ActionEvent event) {
        String nome = "", telefone = "", turma = "", senha = "", confirmar_senha = "";
        long matricula = 0;

        try {
            nome = input_nome.getText();
            telefone = extrairSomenteDigitos(input_telefone.getText());
            matricula = Long.parseLong(input_matricula.getText());
            turma = input_turma.getValue();
            senha = input_senha.getText();
            confirmar_senha = input_confirmarSenha.getText();
        } catch (Exception e) {
            mostrarErro("Erro ao ler os dados: " + e.getMessage());
            return;
        }

        if (!senha.equals(confirmar_senha)) {
            mostrarErro("As senhas são diferentes.");
            return;
        }

        try {
            Colaborador colaborador = new Colaborador(nome, senha, turma, telefone, matricula);

            ColaboradorService.inserirColabrador(colaborador);

            limparCampos();
            mostrarMensagem("Colaborador cadastrado com sucesso.", "Sucesso!");
        } catch (Exception e) {
            mostrarErro("Erro ao cadastrar colaborador: " + e.getMessage());
        }
    }//fecha Cadatrar colaborador

    @FXML
    private void limparCampos() {
        input_nome.clear();
        input_telefone.clear();
        input_matricula.clear();
        input_turma.setValue(null);
        input_senha.clear();
        input_confirmarSenha.clear();
    }

    private void mostrarErro(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
    
    private void mostrarMensagem(String mensagem, String titulo) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    private void configurarCampoTelefone() {
        UnaryOperator<TextFormatter.Change> filtroTelefone = change -> {
            String novoTextoCompleto = change.getControlNewText();

            // Remove tudo que não for número
            String somenteDigitos = extrairSomenteDigitos(novoTextoCompleto);

            // Bloqueia se passar do limite
            if (somenteDigitos.length() > MAX_DIGITOS_TELEFONE) {
                return null;
            }

            // Aplica a máscara
            String textoFormatado = formatarTelefone(somenteDigitos);

            /*
             * Em vez de deixar o JavaFX aplicar a alteração "crua",
             * nós substituímos o conteúdo inteiro do campo pela versão já formatada.
             */
            change.setRange(0, change.getControlText().length());
            change.setText(textoFormatado);

            // Mantém o cursor no fim do texto
            change.selectRange(textoFormatado.length(), textoFormatado.length());

            return change;
        };

        input_telefone.setTextFormatter(new TextFormatter<>(filtroTelefone));
    }

    private String formatarTelefone(String digitos) {
        int tamanho = digitos.length();

        if (tamanho == 0) {
            return "";
        }

        // Até 2 dígitos: abre parêntese e mostra o DDD parcialmente
        if (tamanho <= 2) {
            return "(" + digitos;
        }

        // 3 a 6 dígitos totais: já fecha o DDD e começa o número
        if (tamanho <= 6) {
            return "(" + digitos.substring(0, 2) + ") " + digitos.substring(2);
        }

        // 7 a 10 dígitos totais: formato de telefone com 8 dígitos no número
        if (tamanho <= 10) {
            return "(" + digitos.substring(0, 2) + ") "
                    + digitos.substring(2, 6) + "-"
                    + digitos.substring(6);
        }

        // 11 dígitos totais: formato de celular com 9 dígitos no número
        return "(" + digitos.substring(0, 2) + ") "
                + digitos.substring(2, 7) + "-"
                + digitos.substring(7);
    }
    
    private String extrairSomenteDigitos(String texto) {
        return texto.replaceAll("\\D", "");
    }
    
    @FXML
    private void initialize() {
        input_turma.getItems().addAll(
            "INFO 3001", "INFO 2002", "INFO 2001", "INFO 1001",
            "ADM 1001", "ADM 1002", "ADM 2001", "ADM 3001", "ADM 3002",
            "EDI 1001", "EDI 2001", "EDI 3001", "EDI 3002"
        );
        
        configurarCampoTelefone();
    }
}//fecha class
