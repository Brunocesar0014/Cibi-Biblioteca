package com.cibi.controller.pages;

import com.cibi.Service.ImportacaoCidaService;
import com.cibi.Service.ImportacaoCidaService.Previa;
import com.cibi.Service.ImportacaoCidaService.Registro;
import com.cibi.util.TemaVisual;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Window;

final class ImportacaoCidaController {
    private final ImportacaoCidaService service = new ImportacaoCidaService();
    private final Dialog<ButtonType> dialogo = new Dialog<>();
    private final Label nomePlanilha = new Label("Nenhuma planilha selecionada");
    private final Label nomePasta = new Label("Sem pasta de fotos");
    private final Label mensagem = new Label("Escolha a planilha exportada para começar.");
    private final TableView<Registro> tabela = new TableView<>();
    private final Button escolherPlanilha = new Button("Escolher planilha");
    private final Button escolherPasta = new Button("Escolher pasta");
    private final Button removerPasta = new Button("Remover");
    private final Button validar = new Button("Validar arquivos");
    private Button importar;
    private Button fechar;
    private Path planilha;
    private Path pasta;
    private Previa previa;
    private boolean ocupado;

    void abrir(Window janela) {
        dialogo.initOwner(janela);
        TemaVisual.herdar(dialogo);
        dialogo.setTitle("Importar do CIDA");
        dialogo.setHeaderText("Cadastro de alunos");
        dialogo.setResizable(true);

        var formato = new Label("Selecione uma planilha do CIDA e, se disponível, a pasta Fotos Excel.");
        formato.setWrapText(true);
        var entradaPlanilha = new HBox(8, escolherPlanilha, nomePlanilha);
        var entradaFotos = new HBox(8, escolherPasta, removerPasta, nomePasta);
        entradaPlanilha.setAlignment(Pos.CENTER_LEFT);
        entradaFotos.setAlignment(Pos.CENTER_LEFT);
        nomePlanilha.setMinWidth(0);
        nomePasta.setMinWidth(0);

        tabela.getColumns().add(coluna("Linha", 65, registro -> Integer.toString(registro.linha())));
        tabela.getColumns().add(coluna("Matrícula", 125, registro ->
                registro.matricula() == 0 ? "—" : Long.toString(registro.matricula())));
        tabela.getColumns().add(coluna("Aluno", 225, Registro::nome));
        tabela.getColumns().add(coluna("Turma", 90, Registro::turma));
        tabela.getColumns().add(coluna("Validação", 250, Registro::status));
        tabela.setPlaceholder(new Label("Os alunos aparecem aqui depois da validação."));
        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        tabela.setPrefHeight(285);
        VBox.setVgrow(tabela, Priority.ALWAYS);
        mensagem.setWrapText(true);
        mensagem.setMinHeight(42);

        var conteudo = new VBox(10, formato, entradaPlanilha, entradaFotos, validar, tabela, mensagem);
        conteudo.setPadding(new Insets(8, 0, 0, 0));
        conteudo.setPrefWidth(760);
        dialogo.getDialogPane().setContent(conteudo);
        var tipoImportar = new ButtonType("Importar alunos", ButtonBar.ButtonData.OK_DONE);
        var tipoFechar = new ButtonType("Fechar", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialogo.getDialogPane().getButtonTypes().setAll(tipoFechar, tipoImportar);
        importar = (Button) dialogo.getDialogPane().lookupButton(tipoImportar);
        fechar = (Button) dialogo.getDialogPane().lookupButton(tipoFechar);
        importar.addEventFilter(ActionEvent.ACTION, evento -> {
            evento.consume();
            importar();
        });
        dialogo.setOnCloseRequest(evento -> {
            if (ocupado) evento.consume();
        });

        escolherPlanilha.setOnAction(evento -> escolherPlanilha());
        escolherPasta.setOnAction(evento -> escolherPasta());
        removerPasta.setOnAction(evento -> {
            pasta = null;
            atualizarArquivos();
        });
        validar.setOnAction(evento -> validar());
        atualizarBotoes();
        dialogo.showAndWait();
    }

    private void escolherPlanilha() {
        var seletor = new FileChooser();
        seletor.setTitle("Selecionar planilha do CIDA");
        seletor.getExtensionFilters().add(new FileChooser.ExtensionFilter("Planilhas Excel", "*.xlsm", "*.xlsx"));
        var selecionado = seletor.showOpenDialog(dialogo.getDialogPane().getScene().getWindow());
        if (selecionado == null) return;
        planilha = selecionado.toPath();
        Path pastaSugerida = planilha.resolveSibling("Fotos Excel");
        pasta = Files.isDirectory(pastaSugerida) ? pastaSugerida : null;
        atualizarArquivos();
    }

    private void escolherPasta() {
        var seletor = new DirectoryChooser();
        seletor.setTitle("Selecionar pasta de fotos do CIDA");
        if (planilha != null) seletor.setInitialDirectory(planilha.getParent().toFile());
        var selecionada = seletor.showDialog(dialogo.getDialogPane().getScene().getWindow());
        if (selecionada == null) return;
        pasta = selecionada.toPath();
        atualizarArquivos();
    }

    private void atualizarArquivos() {
        nomePlanilha.setText(planilha.getFileName().toString());
        nomePasta.setText(pasta == null ? "Sem pasta de fotos" : pasta.toString());
        previa = null;
        tabela.getItems().clear();
        mensagem.setText("Valide os arquivos para revisar o lote.");
        atualizarBotoes();
    }

    private void validar() {
        Path arquivo = planilha;
        Path fotos = pasta;
        previa = null;
        tabela.getItems().clear();
        executar("Validando a planilha e as fotos…", () -> service.validar(arquivo, fotos), resultado -> {
            previa = resultado;
            tabela.getItems().setAll(resultado.registros());
            String resumo = "%d registro(s) • %d foto(s) vinculada(s) • %d JPEG(s) ignorado(s)."
                    .formatted(resultado.registros().size(), resultado.fotos(), resultado.fotosIgnoradas());
            mensagem.setText(resumo + (resultado.pronta()
                    ? " Revise o lote e confirme para gravar."
                    : " Corrija as " + resultado.erros() + " linha(s) com erro e valide de novo."));
        }, "Não foi possível ler os arquivos. Confira se são uma exportação do CIDA e estão acessíveis.");
    }

    private void importar() {
        Previa lote = previa;
        if (lote == null || !lote.pronta() || ocupado) return;
        executar("Gravando o lote no banco…", () -> {
            service.importar(lote);
            return lote.registros().size();
        }, total -> {
            previa = null;
            mensagem.setText("Importação concluída. %d cadastro(s) gravado(s), com %d foto(s)."
                    .formatted(total, lote.fotos()));
        }, "Não foi possível concluir a importação. Confira a conexão com o banco de dados.");
    }

    private <T> void executar(String andamento, Callable<T> acao, Consumer<T> sucesso, String falha) {
        ocupado = true;
        mensagem.setText(andamento);
        atualizarBotoes();
        Task<T> tarefa = new Task<>() {
            @Override protected T call() throws Exception {
                return acao.call();
            }
        };
        tarefa.setOnSucceeded(evento -> {
            ocupado = false;
            sucesso.accept(tarefa.getValue());
            atualizarBotoes();
        });
        tarefa.setOnFailed(evento -> {
            ocupado = false;
            Throwable erro = tarefa.getException();
            mensagem.setText(erro.getClass() == IOException.class || erro instanceof IllegalStateException
                    ? erro.getMessage() : falha);
            atualizarBotoes();
        });
        Thread worker = new Thread(tarefa, "cibi-importacao-cida");
        worker.setDaemon(true);
        worker.start();
    }

    private void atualizarBotoes() {
        escolherPlanilha.setDisable(ocupado);
        escolherPasta.setDisable(ocupado || planilha == null);
        removerPasta.setDisable(ocupado || pasta == null);
        validar.setDisable(ocupado || planilha == null);
        importar.setDisable(ocupado || previa == null || !previa.pronta());
        fechar.setDisable(ocupado);
    }

    private TableColumn<Registro, String> coluna(String titulo, double largura, Function<Registro, String> valor) {
        var coluna = new TableColumn<Registro, String>(titulo);
        coluna.setPrefWidth(largura);
        coluna.setCellValueFactory(dados -> new ReadOnlyStringWrapper(valor.apply(dados.getValue())));
        return coluna;
    }
}
