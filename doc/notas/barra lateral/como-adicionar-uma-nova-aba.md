# Como adicionar uma nova aba na sidebar do CIBI

Este guia mostra o passo a passo para adicionar um novo item de menu
(uma nova "aba") na barra lateral da `Tela_Principal.fxml`, seguindo o
mesmo padrão usado por Dashboard, Livros, Empréstimos, Usuários e
Configurações.

No total são **4 passos**: criar a tela, criar o botão, ligar o botão
ao controller e (opcional) escolher um ícone.

---

## 1. Criar a tela (FXML) da nova aba

Crie o FXML da nova página dentro de `com/cibi/fxml/pages/`, por
exemplo:

```
com/cibi/fxml/pages/Relatorios.fxml
```

Se a tela precisar de lógica própria, crie também um controller
dedicado (ex.: `com.cibi.controller.RelatoriosController`), do mesmo
jeito que já existe para outras páginas do sistema (ex.:
`ConfiguracoesController`).

> Não é necessário registrar essa tela em nenhum lugar central: o
> `NavigationManager` carrega o FXML sob demanda e guarda o resultado
> em cache (`screenCache`) na primeira vez que a aba é aberta.

---

## 2. Adicionar o botão no `Tela_Principal.fxml`

Dentro do `menuContainer` (para itens do menu principal) ou logo após
ele, antes do `Region` que empurra o conteúdo para baixo (para itens
fixados no rodapé, como "Configurações"), adicione um novo `Button`
seguindo exatamente o padrão dos existentes:

```xml
<Button fx:id="btnRelatorios" contentDisplay="LEFT" graphicTextGap="12.0"
        mnemonicParsing="false" onAction="#abrirRelatorios"
        prefHeight="40.0" text="Relatórios">
   <graphic>
      <Label styleClass="menu-icon" text="📊" />
   </graphic>
   <styleClass>
      <String fx:value="sidebar-menu-item" />
      <String fx:value="button-custom" />
   </styleClass>
</Button>
```

Pontos importantes:

- `fx:id` precisa ser único e será usado no controller.
- `onAction="#abrirRelatorios"` é o nome do método que você vai criar
  no `MainController` (passo 3).
- O `<graphic>` com `Label styleClass="menu-icon"` é o ícone do item —
  veja a seção **Ícones** abaixo.
- As `styleClass` (`sidebar-menu-item` e `button-custom`) já trazem
  todo o visual (cor, hover, item ativo etc.) definido em
  `principal.css`; não é preciso estilizar nada manualmente.

---

## 3. Ligar o botão ao `MainController`

No `MainController.java`, repita o mesmo padrão usado pelos botões
existentes:

**a) Declare o campo `@FXML`:**

```java
@FXML
private Button btnRelatorios;
```

**b) Adicione o botão em `menuButtons` e `menuTexts`**, dentro de
`configurarMenuButtons()`:

```java
menuButtons = List.of(
        btnDashboard,
        btnLivros,
        btnEmprestimos,
        btnUsuarios,
        btnRelatorios,   // <- novo item
        btnSobre
);
```

```java
menuTexts.put(btnRelatorios, "Relatórios");
```

**c) Crie o método que abre a tela:**

```java
@FXML
private void abrirRelatorios() {
    abrirTela(
            "relatorios",
            "/com/cibi/fxml/pages/Relatorios.fxml",
            btnRelatorios
    );
}
```

O primeiro argumento (`"relatorios"`) é a chave usada pelo
`NavigationManager` para cachear a tela — use um identificador curto e
único, em minúsculas.

Pronto: o `abrirTela(...)` já cuida de carregar o FXML (via
`NavigationManager`) e de marcar o botão como item ativo
(`sidebar-menu-item-active`), da mesma forma que as demais telas.

---

## 4. Escolher o ícone (opcional)

Os ícones atuais são simples glifos Unicode (não dependem de nenhuma
biblioteca como Ikonli/FontAwesome), definidos direto no `text` do
`Label` com `styleClass="menu-icon"`. Alguns exemplos que combinam com
o restante do menu:

| Ícone | Sugestão de uso       |
|-------|------------------------|
| ▦     | Dashboard / visão geral |
| ▤     | Listas / livros         |
| ⇄     | Movimentações / trocas  |
| ☺     | Pessoas / usuários      |
| ⚙     | Configurações           |
| 📊     | Relatórios              |
| 🔔     | Notificações            |

Se no futuro o projeto adotar uma biblioteca de ícones (ex. Ikonli), o
`principal.css` já tem as classes `.ikonli-font-icon` preparadas —
basta trocar o `Label` pelo componente de ícone da biblioteca dentro
do mesmo `<graphic>`.

---

## Resumo rápido (checklist)

- [ ] Criar o FXML da página em `com/cibi/fxml/pages/`
- [ ] (se necessário) Criar o controller da página
- [ ] Adicionar o `Button` no `Tela_Principal.fxml` com `fx:id`, ícone e `onAction`
- [ ] Declarar o `@FXML private Button ...` no `MainController`
- [ ] Adicionar o botão em `menuButtons` e `menuTexts`
- [ ] Criar o método `abrirX()` chamando `abrirTela(chave, caminhoFxml, botao)`
