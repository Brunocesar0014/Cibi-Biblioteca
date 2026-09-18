Sim, é válido, mas para o CIBI eu não recomendaria chegar ao ponto de criar um CSS para cada elemento individual. Isso tende a gerar fragmentação excessiva.

O ideal é separar o CSS por responsabilidade, não necessariamente por elemento.

Uma estrutura que considero adequada para o CIBI
src/main/resources/
└── com/cibi/
    ├── css/
    │   ├── global.css
    │   ├── components/
    │   │   ├── buttons.css
    │   │   ├── textfields.css
    │   │   ├── tables.css
    │   │   ├── alerts.css
    │   │   └── dialogs.css
    │   │
    │   └── pages/
    │       ├── login.css
    │       ├── usuarios.css
    │       ├── alunos.css
    │       └── livros.css
    │
    ├── fxml/
    └── imagen/

Isso cria uma distinção importante:

global.css → identidade geral do CIBI
components/ → elementos reutilizáveis
pages/ → aparência específica de cada tela
Por que eu evitaria css/elements/

Imagine que você tenha:

elements/
├── label.css
├── button.css
├── textfield.css
├── checkbox.css
├── table.css
├── imageview.css
├── pane.css
└── ...

Parece organizado inicialmente, mas rapidamente surge um problema:

Um elemento raramente possui apenas uma aparência.

Você provavelmente terá:

botao_login
botao_cancelar
botao_excluir
botao_salvar
botao_confirmar

Todos são Button, mas não necessariamente devem ter a mesma aparência.

O mesmo acontece com Label:

titulo
subtitulo
label_depuracao
label_erro
label_campo
label_obrigatorio

Criar um arquivo para cada tipo de elemento acaba transferindo a organização para dentro dos próprios arquivos CSS.

Eu usaria classes reutilizáveis

Por exemplo, no buttons.css:

.botao-principal {
    -fx-background-color: #1e272e;
    -fx-text-fill: white;
    -fx-background-radius: 5px;
}

.botao-principal:hover {
    -fx-background-color: #34444d;
}

.botao-cancelar {
    -fx-background-color: #dddddd;
    -fx-text-fill: #1e272e;
}

.botao-perigo {
    -fx-background-color: #c0392b;
    -fx-text-fill: white;
}

Então no FXML:

<Button
    text="Salvar"
    styleClass="botao-principal"/>

Outro botão pode reutilizar:

<Button
    text="Confirmar"
    styleClass="botao-principal"/>

E um botão de exclusão:

<Button
    text="Excluir"
    styleClass="botao-perigo"/>

Você ganha reutilização sem perder controle.

E o global.css?

Eu colocaria nele coisas que representam a identidade visual geral:

.root {
    -fx-font-family: "Arial";
    -fx-background-color: #F8F5FB;
}

.label {
    -fx-text-fill: #1e272e;
}

.text-field {
    -fx-background-radius: 5px;
}

Mas cuidado com regras muito genéricas.

Por exemplo:

.button {
    -fx-background-color: #1e272e;
}

pode parecer conveniente, mas depois você pode querer um botão vermelho para excluir algo, um botão cinza para cancelar etc.

Por isso prefiro:

.botao-principal { ... }
.botao-cancelar { ... }
.botao-perigo { ... }
E seus IDs?

Você já está utilizando algo como:

#painel_esquerda {
    -fx-background-color: #1e272e;
}

#painel_login {
    -fx-background-color: #F8F5FB;
}

Isso é perfeitamente válido.

Eu faria uma distinção:

id

Use quando o elemento é específico daquela tela:

#painel_login
#painel_esquerda
#painel_cabecalho
styleClass

Use quando a aparência pode ser reutilizada:

.botao-principal
.botao-cancelar
.titulo
.subtitulo
.campo-entrada

Essa distinção é bastante útil em projetos JavaFX maiores.

Para o seu Alert

Nesse caso eu faria:

css/
├── global.css
├── components/
│   ├── buttons.css
│   ├── inputs.css
│   └── alerts.css
└── pages/
    ├── login.css
    ├── usuarios.css
    └── ...

O alerts.css poderia conter:

.alert-confirmacao {
    -fx-background-color: #F8F5FB;
}

.alert-confirmacao .header-panel {
    -fx-background-color: #1e272e;
}

.alert-confirmacao .botao-confirmar {
    -fx-background-color: #1e272e;
    -fx-text-fill: white;
}

E você carrega especificamente esse CSS no Alert.

Um ponto importante no seu projeto

Como o CIBI é um projeto de TCC, eu não tentaria criar uma arquitetura de CSS excessivamente sofisticada agora.

Uma estrutura como:

css/
├── global.css
├── components/
│   ├── buttons.css
│   ├── alerts.css
│   ├── forms.css
│   └── tables.css
└── pages/
    ├── login.css
    ├── usuarios.css
    ├── alunos.css
    └── livros.css

já é mais do que suficiente e demonstra uma boa preocupação com manutenção e separação de responsabilidades.

Se o projeto crescer, você pode evoluir naturalmente.

Eu evitaria com.cibi.css.elements especificamente. css não é um pacote Java; é um diretório de recursos. Portanto, conceitualmente, eu deixaria:

com/cibi/
    css/
        components/
        pages/

em vez de:

com/cibi/css/elements/

Isso também combina melhor com a forma como você está estruturando o restante do CIBI (controller, service, database, util etc.).