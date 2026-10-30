package com.cibi.controller.pages;

import com.cibi.Service.CategoriaLivroService;
import com.cibi.Service.CategoriaLivroService.Item;
import com.cibi.Service.CategoriaLivroService.Resultado;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.control.*;
import java.util.function.Consumer;

public class GerenciarCategoriasController {
    @FXML private VBox modalRoot;
    @FXML private ListView<Item> listaCategorias;
    @FXML private TextField nomeCategoria;
    @FXML private Label lblAjuda, lblMensagem;
    @FXML private Button btnRenomear, btnExcluir;
    private CategoriaLivroService.Edicao edicao;
    private Consumer<Resultado> aoSalvar;
    private Runnable aoCancelar;
    private boolean nova;
    private boolean atualizandoLista;

    @FXML
    private void initialize() {
        modalRoot.setFocusTraversable(true);
        modalRoot.addEventFilter(MouseEvent.MOUSE_PRESSED, evento -> {
            if (nomeCategoria.isFocused() && !dentroDoCampo(evento.getTarget())) {
                modalRoot.requestFocus();
            }
        });
        listaCategorias.setCellFactory(lista -> new ListCell<>() {
            @Override protected void updateItem(Item item, boolean vazio) {
                super.updateItem(item, vazio);
                setText(vazio || item == null ? null : item.nome());
                setTooltip(vazio || item == null ? null : new Tooltip(item.nome()));
            }
        });
        listaCategorias.getSelectionModel().selectedItemProperty().addListener((o, a, item) -> {
            if (!atualizandoLista) mostrar(item);
        });
    }

    private boolean dentroDoCampo(Object alvo) {
        if (!(alvo instanceof Node node)) return false;
        for (Node atual = node; atual != null; atual = atual.getParent()) {
            if (atual == nomeCategoria) return true;
        }
        return false;
    }

    public void configurar(String selecionada, Consumer<Resultado> salvar, Runnable cancelar) {
        edicao = new CategoriaLivroService().iniciarEdicao();
        aoSalvar = salvar;
        aoCancelar = cancelar;
        Item inicial = edicao.listar().stream().filter(i -> i.nome().equalsIgnoreCase(selecionada == null ? "" : selecionada.strip()))
                .findFirst().orElse(edicao.listar().isEmpty() ? null : edicao.listar().getFirst());
        atualizarLista(inicial);
        if (inicial == null) novaCategoria();
    }

    private void atualizarLista(Item selecionada) {
        atualizandoLista = true;
        listaCategorias.getItems().setAll(edicao.listar());
        listaCategorias.getSelectionModel().select(selecionada);
        atualizandoLista = false;
        mostrar(selecionada);
    }

    private void mostrar(Item item) {
        nova = item == null;
        nomeCategoria.setText(item == null ? "" : item.nome());
        btnRenomear.setText(nova ? "Adicionar" : "Renomear");
        btnExcluir.setDisable(item == null);
        lblAjuda.setText(nova ? "Informe o nome da nova categoria."
                : "Renomear também atualiza os livros desta categoria.");
        limparMensagem();
    }

    @FXML
    private void novaCategoria() {
        atualizandoLista = true;
        listaCategorias.getSelectionModel().clearSelection();
        atualizandoLista = false;
        mostrar(null);
        nomeCategoria.requestFocus();
    }

    private Item aplicarNome() {
        Item selecionada = listaCategorias.getSelectionModel().getSelectedItem();
        Item atualizada = nova ? edicao.adicionar(nomeCategoria.getText())
                : edicao.renomear(selecionada, nomeCategoria.getText());
        atualizarLista(atualizada);
        return atualizada;
    }

    @FXML
    private void renomear() {
        try {
            boolean adicionando = nova;
            aplicarNome();
            mensagem(adicionando ? "Categoria adicionada à lista. Clique em Salvar para confirmar."
                    : "Categoria renomeada na lista. Clique em Salvar para confirmar.", false);
        } catch (IllegalArgumentException erro) {
            mensagem(erro.getMessage(), true);
            nomeCategoria.requestFocus();
        }
    }

    @FXML
    private void excluir() {
        try {
            Item item = listaCategorias.getSelectionModel().getSelectedItem();
            if (item == null) return;
            if (edicao.emUso(item)) {
                mensagem("Esta categoria possui livros vinculados e não pode ser excluída. Você pode renomeá-la.", true);
                return;
            }
            if (!ConfirmacaoCategorias.excluir(modalRoot, item.nome())) return;
            int indice = listaCategorias.getSelectionModel().getSelectedIndex();
            edicao.excluir(item);
            Item proxima = edicao.listar().isEmpty() ? null : edicao.listar().get(Math.min(indice, edicao.listar().size() - 1));
            atualizarLista(proxima);
            mensagem("Categoria removida da lista. Clique em Salvar para confirmar.", false);
        } catch (IllegalArgumentException erro) {
            mensagem(erro.getMessage(), true);
        }
    }

    @FXML
    private void salvar() {
        try {
            Item selecionada = listaCategorias.getSelectionModel().getSelectedItem();
            if (nova && !nomeCategoria.getText().isBlank()
                    || selecionada != null && !nomeCategoria.getText().equals(selecionada.nome())) {
                selecionada = aplicarNome();
            }
            if (!ConfirmacaoCategorias.salvar(modalRoot)) return;
            aoSalvar.accept(edicao.salvar(selecionada));
        } catch (IllegalArgumentException | IllegalStateException erro) {
            mensagem(erro.getMessage(), true);
            nomeCategoria.requestFocus();
        }
    }

    @FXML private void cancelar() { aoCancelar.run(); }

    private void limparMensagem() {
        lblMensagem.setText("");
        lblMensagem.setVisible(false);
        lblMensagem.setManaged(false);
    }

    private void mensagem(String texto, boolean erro) {
        lblMensagem.setText(texto);
        lblMensagem.getStyleClass().removeAll("category-message-error", "category-message-success");
        lblMensagem.getStyleClass().add(erro ? "category-message-error" : "category-message-success");
        lblMensagem.setVisible(true);
        lblMensagem.setManaged(true);
    }
}
