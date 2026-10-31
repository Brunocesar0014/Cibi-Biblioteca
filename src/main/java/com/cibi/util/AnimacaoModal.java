package com.cibi.util;

import java.util.Objects;
import java.util.function.Predicate;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogEvent;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.Effect;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

/**
 * Fade de um Dialog/Alert com desfoque e escurecimento do conteúdo atrás dele.
 * Configure uma vez, depois dos demais handlers e antes de show/showAndWait.
 * Use StageStyle.TRANSPARENT para que a decoração da janela não fique visível.
 */
public final class AnimacaoModal<R> implements AutoCloseable {
    // Ajustes globais. Durações em milissegundos; zero desativa a transição.
    public static final Duration DURACAO_ENTRADA_PADRAO = Duration.millis(150);
    public static final Duration DURACAO_SAIDA_PADRAO = Duration.millis(150);
    public static final double RAIO_DESFOQUE_PADRAO = 2;
    // De 0 (sem escurecer) a 1 (preto).
    public static final double ESCURECIMENTO_PADRAO = 0.18;
    public static final Interpolator INTERPOLACAO_PADRAO = Interpolator.EASE_BOTH;
    public static final Configuracao PADRAO = new Configuracao(
            DURACAO_ENTRADA_PADRAO, DURACAO_SAIDA_PADRAO,
            RAIO_DESFOQUE_PADRAO, ESCURECIMENTO_PADRAO, INTERPOLACAO_PADRAO);

    /** Valores próprios de um modal, sem alterar os padrões dos demais. */
    public record Configuracao(Duration entrada, Duration saida, double raioDesfoque,
                               double escurecimento, Interpolator interpolacao) {
        public Configuracao {
            validarDuracao(entrada);
            validarDuracao(saida);
            Objects.requireNonNull(interpolacao, "interpolacao");
            if (!Double.isFinite(raioDesfoque) || raioDesfoque < 0 || raioDesfoque > 63) {
                throw new IllegalArgumentException("Raio do desfoque deve estar entre 0 e 63.");
            }
            if (!Double.isFinite(escurecimento) || escurecimento < 0 || escurecimento > 1) {
                throw new IllegalArgumentException("Escurecimento deve estar entre 0 e 1.");
            }
        }

        private static void validarDuracao(Duration duracao) {
            Objects.requireNonNull(duracao, "duracao");
            if (!Double.isFinite(duracao.toMillis()) || duracao.lessThan(Duration.ZERO)) {
                throw new IllegalArgumentException("Duração deve ser finita e não negativa.");
            }
        }
    }

    private final Dialog<R> dialogo;
    private final Node fundo;
    private final Configuracao configuracao;
    private final Predicate<? super R> animarSaida;
    private final GaussianBlur desfoque = new GaussianBlur(0);
    private final ColorAdjust escurecer = new ColorAdjust();
    private Timeline transicao;
    private Effect efeitoAnterior;
    private double opacidadeAnterior;
    private boolean desabilitadoAnterior;
    private boolean preparado;
    private boolean saindo;
    private boolean concluirFechamento;
    private R resultadoSaida;
    private Window janela;
    private Scene cenaObservada;
    private final ChangeListener<Window> aoVincularJanela =
            (observavel, anterior, atual) -> prepararJanela(atual);
    private final EventHandler<WindowEvent> fecharJanela;

    private AnimacaoModal(Dialog<R> dialogo, Node fundo, Configuracao configuracao,
                          Predicate<? super R> animarSaida) {
        this.dialogo = Objects.requireNonNull(dialogo, "dialogo");
        this.fundo = Objects.requireNonNull(fundo, "fundo");
        this.configuracao = Objects.requireNonNull(configuracao, "configuracao");
        this.animarSaida = Objects.requireNonNull(animarSaida, "animarSaida");
        fecharJanela = evento -> {
            evento.consume();
            dialogo.close();
        };
        escurecer.setInput(desfoque);
        // Um Dialog pode criar ou reconectar sua cena somente durante show().
        // Prepara a transparência assim que ela é vinculada, antes da exibição.
        dialogo.getDialogPane().sceneProperty().addListener((obs, anterior, atual) -> {
            if (preparado) observarCena(atual);
        });
        dialogo.setOnShowing(encadear(dialogo.getOnShowing(), e -> preparar()));
        // Dialog emite onShown antes de terminar a criação da janela nativa.
        // Aguarda essa montagem para não gastar o fade carregando CSS/layout.
        dialogo.setOnShown(encadear(dialogo.getOnShown(), e -> Platform.runLater(() -> {
            if (!preparado || !dialogo.isShowing()) return;
            if (!saindo) entrar();
        })));
        EventHandler<DialogEvent> aoSolicitarFechamento = dialogo.getOnCloseRequest();
        dialogo.setOnCloseRequest(evento -> {
            // Executa a validação existente uma vez por solicitação; o término
            // do fade e cliques repetidos não devem repetir seus efeitos.
            if (!saindo && !concluirFechamento && aoSolicitarFechamento != null) {
                aoSolicitarFechamento.handle(evento);
            }
            if (!evento.isConsumed()) solicitarSaida(evento);
        });
        // Restaura os efeitos antes de notificar quem já observava o fechamento.
        EventHandler<DialogEvent> anterior = dialogo.getOnHidden();
        dialogo.setOnHidden(e -> {
            close();
            if (anterior != null) anterior.handle(e);
        });
    }

    public static <R> AnimacaoModal<R> configurar(Dialog<R> dialogo, Node fundo) {
        return configurar(dialogo, fundo, PADRAO);
    }

    public static <R> AnimacaoModal<R> configurar(Dialog<R> dialogo, Node fundo, Configuracao configuracao) {
        return configurar(dialogo, fundo, configuracao, resultado -> true);
    }

    /** O predicado permite, por exemplo, animar somente o cancelamento. */
    public static <R> AnimacaoModal<R> configurar(Dialog<R> dialogo, Node fundo,
                                                Configuracao configuracao, Predicate<? super R> animarSaida) {
        return new AnimacaoModal<>(dialogo, fundo, configuracao, animarSaida);
    }

    private static EventHandler<DialogEvent> encadear(EventHandler<DialogEvent> anterior,
                                                     EventHandler<DialogEvent> atual) {
        return evento -> {
            if (anterior != null) anterior.handle(evento);
            if (!evento.isConsumed()) atual.handle(evento);
        };
    }

    private void preparar() {
        efeitoAnterior = fundo.getEffect();
        opacidadeAnterior = dialogo.getDialogPane().getOpacity();
        desabilitadoAnterior = dialogo.getDialogPane().isDisable();
        preparado = true;
        saindo = false;
        concluirFechamento = false;
        desfoque.setInput(efeitoAnterior);
        desfoque.setRadius(0);
        escurecer.setBrightness(0);
        dialogo.getDialogPane().setOpacity(0);
        fundo.setEffect(escurecer);
        observarCena(dialogo.getDialogPane().getScene());
    }

    private void observarCena(Scene cena) {
        if (cenaObservada != null) {
            cenaObservada.windowProperty().removeListener(aoVincularJanela);
        }
        cenaObservada = cena;
        if (cenaObservada != null) {
            cenaObservada.windowProperty().addListener(aoVincularJanela);
        }
        prepararJanela(cena == null ? null : cena.getWindow());
    }

    private void prepararJanela(Window atual) {
        if (janela != atual) {
            if (janela != null) janela.removeEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, fecharJanela);
            janela = atual;
            // O fechamento nativo precisa aguardar o Dialog, inclusive no Alt+F4.
            if (janela != null) janela.addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, fecharJanela);
        }
        if (cenaObservada != null && janela instanceof Stage stage
                && stage.getStyle() == StageStyle.TRANSPARENT) {
            cenaObservada.setFill(Color.TRANSPARENT);
        }
    }

    private void entrar() {
        animar(configuracao.entrada(), opacidadeAnterior,
                -configuracao.escurecimento(), configuracao.raioDesfoque(), () -> {});
    }

    private void solicitarSaida(DialogEvent evento) {
        if (concluirFechamento || !preparado) return;
        if (saindo) {
            evento.consume();
            dialogo.setResult(resultadoSaida);
            return;
        }
        if (!animarSaida.test(dialogo.getResult())) return;
        evento.consume();
        saindo = true;
        resultadoSaida = dialogo.getResult();
        dialogo.getDialogPane().setDisable(true);
        // Parte dos valores atuais, inclusive se o usuário cancelar durante a entrada.
        animar(configuracao.saida(), 0, 0, 0, () -> {
            concluirFechamento = true;
            dialogo.close();
        });
    }

    private void animar(Duration duracao, double opacidade, double brilho, double raio, Runnable aoTerminar) {
        if (transicao != null) transicao.stop();
        var painel = dialogo.getDialogPane();
        if (duracao.equals(Duration.ZERO)) {
            painel.setOpacity(opacidade);
            escurecer.setBrightness(brilho);
            desfoque.setRadius(raio);
            // Evita fechar novamente dentro do próprio evento de fechamento.
            Platform.runLater(() -> { if (preparado) aoTerminar.run(); });
            return;
        }
        transicao = new Timeline(new KeyFrame(duracao,
                new KeyValue(painel.opacityProperty(), opacidade, configuracao.interpolacao()),
                new KeyValue(escurecer.brightnessProperty(), brilho, configuracao.interpolacao()),
                new KeyValue(desfoque.radiusProperty(), raio, configuracao.interpolacao())));
        transicao.setOnFinished(e -> aoTerminar.run());
        transicao.play();
    }

    /** Interrompe a animação e restaura o estado; também é chamado ao ocultar o modal. */
    @Override public void close() {
        if (!preparado) return;
        if (transicao != null) transicao.stop();
        if (cenaObservada != null) {
            cenaObservada.windowProperty().removeListener(aoVincularJanela);
            cenaObservada = null;
        }
        if (janela != null) {
            janela.removeEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, fecharJanela);
            janela = null;
        }
        fundo.setEffect(efeitoAnterior);
        dialogo.getDialogPane().setOpacity(opacidadeAnterior);
        dialogo.getDialogPane().setDisable(desabilitadoAnterior);
        preparado = false;
        saindo = false;
        resultadoSaida = null;
    }
}
