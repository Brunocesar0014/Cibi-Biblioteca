# Animações de modais

A classe `com.cibi.util.AnimacaoModal` reúne o fade do modal, o escurecimento e o desfoque do conteúdo atrás dele. As transições de entrada e saída acontecem juntas, sem bloquear a interface. Cancelar durante a entrada inicia a saída a partir dos valores atuais.

## Ajustes globais

Edite as constantes no início de `src/main/java/com/cibi/util/AnimacaoModal.java` e recompile o aplicativo:

| Constante | Padrão | Efeito |
| --- | --- | --- |
| `DURACAO_ENTRADA_PADRAO` | `Duration.millis(150)` | Tempo para aparecer. Menor significa mais rápido. |
| `DURACAO_SAIDA_PADRAO` | `Duration.millis(150)` | Tempo para desaparecer. Zero desativa a transição. |
| `RAIO_DESFOQUE_PADRAO` | `2` | Desfoque do fundo, de 0 (nenhum) a 63. |
| `ESCURECIMENTO_PADRAO` | `0.18` | Escurecimento, de 0 (nenhum) a 1 (preto). |
| `INTERPOLACAO_PADRAO` | `Interpolator.EASE_BOTH` | Suavização da velocidade. Use `LINEAR` para velocidade constante. |

## Aplicar em outro modal

Configure o proprietário, os botões, o conteúdo e os handlers primeiro. Em seguida, configure a animação **uma única vez**, antes de abrir o modal:

```java
Dialog<ButtonType> dialogo = new Dialog<>();
dialogo.initOwner(stage);
dialogo.initStyle(StageStyle.TRANSPARENT);
dialogo.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);
// Configure aqui o conteúdo e o CSS do seu modal.

try (var animacao = AnimacaoModal.configurar(dialogo, stage.getScene().getRoot())) {
    var resultado = dialogo.showAndWait();
}
```

Importe `com.cibi.util.AnimacaoModal` e os tipos JavaFX utilizados. Também funciona com `Alert`. O nó passado como segundo argumento é a área que será escurecida e desfocada: use a raiz da cena para afetar toda a janela ou um contêiner para limitar a uma seção.

Os botões precisam permitir o fechamento normal do `Dialog`. Um botão personalizado pode chamar `dialogo.close()` ou `dialogo.setResult(...)`; o fechamento será adiado até o fade terminar. Escape e o fechamento nativo também passam pela animação. Durante a saída, os controles do modal são desabilitados para impedir ações duplicadas.

O efeito anterior do fundo, a opacidade e o estado de habilitação do painel são restaurados ao ocultar o modal. Os handlers já configurados são preservados; não substitua os handlers `onShowing`, `onShown`, `onCloseRequest` ou `onHidden` depois de configurar a animação. O `try` garante a restauração mesmo se a abertura falhar. Para `show()` sem espera, a restauração acontece automaticamente no evento de ocultação.

## Valores próprios de um modal

```java
var configuracao = new AnimacaoModal.Configuracao(
        Duration.millis(120), // Entrada
        Duration.millis(120), // Saída
        3,                   // Raio do desfoque
        0.22,                // Escurecimento
        Interpolator.EASE_BOTH);
AnimacaoModal.configurar(dialogo, stage.getScene().getRoot(), configuracao);
```

## Animar a saída somente em alguns resultados

A sobrecarga com predicado escolhe quais resultados terão animação de saída. A confirmação de encerramento em `WindowUtils` usa:

```java
AnimacaoModal.configurar(alert, stage.getScene().getRoot(),
        AnimacaoModal.PADRAO, resultado -> resultado != botaoOk);
```

Assim, Cancelar, X e Escape fazem o fade de saída; OK confirma o encerramento imediatamente. Nos outros modais, a chamada simples anima todos os fechamentos.
