package com.cibi.util;

import java.util.Locale;
import javafx.application.ColorScheme;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.WeakChangeListener;
import javafx.css.PseudoClass;
import javafx.scene.Parent;
import javafx.scene.control.Dialog;

/** Aplica a paleta do CIBI e acompanha o esquema do sistema no modo automático. */
public final class TemaVisual {

    public enum Opcao {
        ESCURO, CLARO, SISTEMA;

        public static Opcao de(String valor) {
            if (valor != null) {
                String nome = valor.strip().toUpperCase(Locale.ROOT);
                for (Opcao opcao : values()) {
                    if (opcao.name().equals(nome)) {
                        return opcao;
                    }
                }
            }
            return ESCURO;
        }
    }

    private static final String CLASSE = "cibi-tema";
    private static final PseudoClass ESCURO = PseudoClass.getPseudoClass("tema-escuro");
    private static final String CSS =
            TemaVisual.class.getResource("/com/cibi/css/temas.css").toExternalForm();

    private final Parent raiz;
    private Opcao opcao = Opcao.ESCURO;
    private final ChangeListener<ColorScheme> aoMudarSistema = (obs, anterior, atual) -> {
        if (opcao == Opcao.SISTEMA) {
            atualizar(atual);
        }
    };

    public TemaVisual(Parent raiz) {
        this.raiz = raiz;
        preparar(raiz);
        Platform.getPreferences().colorSchemeProperty().addListener(new WeakChangeListener<>(aoMudarSistema));
    }

    public void aplicar(Opcao opcao) {
        this.opcao = opcao;
        atualizar(Platform.getPreferences().getColorScheme());
    }

    private void atualizar(ColorScheme sistema) {
        raiz.pseudoClassStateChanged(ESCURO,
                opcao == Opcao.ESCURO || (opcao == Opcao.SISTEMA && sistema == ColorScheme.DARK));
    }

    /** Pop-ups e diálogos têm raízes próprias e recebem a paleta da janela que os abriu. */
    public static void herdar(Parent destino, Parent origem) {
        if (origem.getStyleClass().contains(CLASSE)) {
            preparar(destino);
            destino.pseudoClassStateChanged(ESCURO, origem.getPseudoClassStates().contains(ESCURO));
        }
    }

    public static void herdar(Dialog<?> dialogo) {
        if (dialogo.getOwner() != null && dialogo.getOwner().getScene() != null) {
            herdar(dialogo.getDialogPane(), dialogo.getOwner().getScene().getRoot());
        }
    }

    private static void preparar(Parent raiz) {
        if (!raiz.getStyleClass().contains(CLASSE)) {
            raiz.getStyleClass().add(CLASSE);
            raiz.getStylesheets().add(CSS);
        }
    }
}
