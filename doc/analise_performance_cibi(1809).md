Analisei o projeto que você enviou, olhando principalmente para `Controller`, `Service`, `Repository`, navegação, FXML, CSS e o fluxo JDBC. A conclusão é bem clara: **o JavaFX em si não parece estar sobrecarregado no estado atual; o maior risco de “travadas” está em operações síncronas de banco executadas na própria thread gráfica do JavaFX.**

## 1. O principal gargalo: banco rodando na JavaFX Application Thread

Esse é, disparado, o ponto que eu atacaria primeiro.

Hoje existem chamadas assim:


```
Clique/ação da interface
        ↓
Controller
        ↓
Service
        ↓
Repository
        ↓
DriverManager.getConnection()
        ↓
MySQL
```

Tudo isso acontece na mesma thread que precisa desenhar a interface.

### Login

Em `LoginController.java`, o método:


```
ColaboradorService.RealizarLogin(usuario, senha);
```

é executado diretamente pelo evento do botão.

Depois:


```
RealizarLogin
 → buscarPorUsuarioESenha
   → ConnectionFactory.getConnection()
   → executeQuery()
```

Ou seja, enquanto o MySQL responde, a interface não pode continuar processando os pulsos normalmente.

Isso pode acontecer mesmo com uma consulta extremamente simples. Um atraso de 50 ms, 200 ms, 500 ms ou alguns segundos no banco vira atraso visual.

### Empréstimos: esse caso é ainda mais crítico

Esse trecho me chamou bastante atenção:


```
if (newValue.length() == 15 && !executado) {
    executado = true;

    BuscarAlunoPorMatricula();

    Platform.runLater(() -> input_matricula.clear());
}
```

E `BuscarAlunoPorMatricula()` acessa o banco imediatamente.

Isso significa que o evento do campo de texto chega ao 15º caractere e, **dentro do próprio listener de** **`textProperty()`**, você faz:


```
digitação
   ↓
listener
   ↓
JDBC
   ↓
MySQL
   ↓
interface esperando
```

Para um sistema que provavelmente recebe matrícula de leitor de código de barras, isso é exatamente o tipo de arquitetura que pode dar a sensação de:

> “A tela fica congelada logo depois da leitura.”

Esse seria meu **suspeito nº 1 para uma travada perceptível no uso real**.

---

# 2. O mesmo problema acontece no cadastro de usuário

Em `UsuariosController.java`:


```
ColaboradorService.inserirColabrador(colaborador);
```

também é chamado diretamente pelo evento do botão.

Internamente:


```
Controller
 → Service
   → Repository
     → getConnection()
     → executeUpdate()
```

Novamente, a interface fica dependente do tempo de resposta do MySQL.

O cadastro talvez não seja acionado frequentemente, então o impacto é menor que no empréstimo/login, mas a causa é a mesma.

---

# 3. Configurações também fazem I/O bloqueante

Na inicialização:


```
Map<String, String> v = service.carregarTodas();
```

E isso chega até:


```
repository.buscarTodas()
```

que abre conexão e executa SQL.

Então ao entrar em Configurações, você tem uma operação de banco acontecendo durante a construção/entrada da tela.

Além disso:


```
service.salvar(valores);
```

e:


```
service.restaurarPadroes();
```

também são síncronos.

Nesse caso o impacto é menor, porque são ações pontuais, mas você pode sentir aquela pequena “engasgada” ao clicar em salvar/restaurar.

---

# 4. Abrir uma tela pela primeira vez também pode gerar uma pequena travada

Seu `NavigationManager` faz algo interessante:


```
if (screenCache.containsKey(name)) {
    root = screenCache.get(name);
} else {
    FXMLLoader loader = new FXMLLoader(resource);
    root = loader.load();
    screenCache.put(name, root);
}
```

Isso é uma boa decisão para navegação repetida.

A primeira abertura faz:


```
ler FXML
→ construir árvore de Scene Graph
→ instanciar Controller
→ executar initialize()
→ aplicar CSS
→ fazer layout
→ renderizar
```

Depois a tela é reutilizada.

### O ponto interessante no seu projeto

A `Configuracoes.fxml` é bem maior que as outras telas, com aproximadamente **157 componentes/nós relevantes**.

Então eu esperaria um pequeno pico na primeira abertura de Configurações, mas **não considero que seja o principal causador das travadas**.

As outras telas são bem menores.

---

# 5. O cache de telas está correto, mas existe um trade-off

O cache:


```
private final Map<String, Parent> screenCache = new HashMap<>();
```

é bom para responsividade porque evita:

-  novo parsing do FXML; 
-  nova criação de controles; 
-  novo `initialize()`; 
-  novo processamento CSS. 

Então eu **não removeria isso**.

Mas ele mantém as telas vivas.

Hoje isso não é um problema relevante porque o sistema é pequeno.

Porém, no futuro, imagine:


```
Dashboard
Livros com tabela grande
Usuários com tabela grande
Empréstimos
Relatórios
Estatísticas
Gráficos
...
```

Se cada tela ficar armazenada inteira na memória, o consumo cresce.

Minha recomendação para o futuro seria:

> continuar usando cache para telas pequenas/estáveis e avaliar descarregamento para telas muito pesadas.

No estágio atual, eu deixaria como está.

---

# 6. Seu banco não parece estar fazendo uma busca absurda

Isso é importante.

A busca do aluno:


```
SELECT matricula, nome, turma
FROM alunos
WHERE matricula = ?
```

usa:


```
PRIMARY KEY (matricula)
```

Então a consulta é adequada.

A busca do colaborador:


```
WHERE nome = ? AND senha = ?
```

também está razoavelmente protegida porque no schema de referência `nome` é `UNIQUE`.

Portanto:

### Não vejo a consulta em si como o grande vilão.

Vejo mais isto:


```
DriverManager.getConnection(...)
```

e toda a espera de rede/driver/banco acontecendo **na thread da interface**.

---

# 7. Abrir uma nova conexão para cada operação também merece atenção

Seu `ConnectionFactory` faz:


```
DriverManager.getConnection(...)
```

toda vez.

Ou seja, uma sequência como:


```
login
→ abre conexão
→ fecha conexão

busca aluno
→ abre conexão
→ fecha conexão

cadastro
→ abre conexão
→ fecha conexão
```

Isso funciona, mas existe overhead.

Hoje:


```
ConnectionFactory
      ↓
DriverManager
      ↓
nova conexão
```

Uma evolução natural seria:


```
Connection Pool
      ↓
conexão reutilizável
      ↓
Repository
```

Uma biblioteca muito usada para esse tipo de cenário é HikariCP.

### Mas atenção

Eu colocaria isso **depois** de tirar o JDBC da UI thread.

Porque:


```
JDBC síncrono + conexão rápida
```

ainda pode travar a tela.

Enquanto:


```
JDBC em background + conexão não ideal
```

mantém a UI responsiva.

---

# 8. A estrutura atual do sistema é pequena do ponto de vista gráfico

Eu contei aproximadamente:

-  Login: poucos componentes 
-  Principal: poucos componentes 
-  Empréstimos: pouquíssimos 
-  Livros: pouquíssimos 
-  Usuários: ~38 componentes relevantes 
-  Configurações: ~157 componentes relevantes 

Isso é muito pequeno para JavaFX.

Então eu **não começaria** tentando:

> “reduzir VBox”,
>
> “trocar BorderPane”,
>
> “remover StackPane”,
>
> “evitar Label”.

Essas otimizações seriam praticamente irrelevantes comparadas ao JDBC bloqueante.

---

# 9. CSS não está exageradamente pesado

O CSS possui bastante código, mas a maior parte é estilização tradicional.

Tem alguns `dropshadow`, principalmente em:


```
.theme-card:selected
.swatch:hover
.switch > .box > .mark
.text-campo:focused
```

e no alerta:


```
-fx-effect: dropshadow(gaussian, ...)
```

Sombras gaussianas podem custar mais renderização que elementos simples, principalmente se houver muitos elementos simultaneamente.

Mas no seu caso há poucos componentes usando isso.

### Portanto

Eu colocaria:

**Impacto atual: baixo.**

Não vale a pena sair removendo todas as sombras.

---

# 10. O ponto que eu reduziria em animações não é a animação — é layout durante animação

Sua sidebar faz:


```
sidebar.prefWidthProperty()
```

animado de:


```
200 → 72
```

Isso funciona.

Mas animar propriedades relacionadas ao tamanho/layout pode gerar vários recálculos durante a animação.

A cada frame:


```
mudou largura
→ layout
→ filhos recalculados
→ render
```

No seu caso são poucos componentes, então provavelmente ainda está leve.

Mas conceitualmente, quando você quiser deixar a aplicação mais refinada, eu preferiria animações baseadas em:


```
translate
opacity
scale
```

quando a alteração não precisar realmente mudar o layout.

Essas propriedades costumam ser mais adequadas para transições visuais.

---

# 11. Sua animação de login está boa conceitualmente

Você já usa:


```
TranslateTransition
ScaleTransition
ParallelTransition
```

Isso é uma direção boa.

O que eu faria visualmente é continuar nessa linha, mas evitar criar uma animação excessivamente longa.

Para aplicações desktop, eu usaria algo próximo de:


```
120–180 ms
```

para microinterações e aproximadamente:


```
180–280 ms
```

para transições de tela.

250 ms que você usa na sidebar é perfeitamente razoável.

---

# 12. Sugestões de animações para deixar o CIBI mais fluido

Aqui acho que existe bastante espaço para melhorar a percepção de qualidade.

## Entrada de tela

Quando trocar:


```
Dashboard
→ Livros
```

em vez de simplesmente trocar o node:


```
[Dashboard]
      ↓
[Livros]
```

você pode fazer:


```
Dashboard
   ↓ fade out
   ↓ translate pequeno
Livros
   ↑ fade in
   ↑ translate pequeno
```

Uma combinação muito boa:


```
Opacity: 0 → 1
TranslateX: 8 → 0
```

ou:


```
Opacity: 0 → 1
TranslateY: 8 → 0
```

Algo extremamente discreto.

Eu evitaria transições grandes como:


```
tela deslizando 300px
```

porque começa a parecer aplicativo mobile.

---

# 13. Sidebar

Sua sidebar já possui a animação de abertura/fechamento.

Você pode complementar com:

### Expandindo


```
Sidebar abre
      ↓
texto aparece gradualmente
```

### Recolhendo


```
texto desaparece
      ↓
sidebar fecha
```

Uma sequência boa seria:


```
largura começa a abrir
       ↓
texto opacity 0 → 1
       ↓
conteúdo estabiliza
```

Isso visualmente fica muito melhor que simplesmente o texto aparecer/desaparecer.

---

# 14. Hover dos botões

Outra microinteração boa:


```
mouse entra
   ↓
translateX = +2
opacity levemente alterada
```

e ao sair:


```
translateX = 0
```

Muito sutil.

Eu evitaria `Scale = 1.1` ou coisas exageradas na sidebar.

Algo como:


```
1.00 → 1.01
```

já é suficiente.

---

# 15. Botões de ação

Para:


```
Salvar
Cancelar
Limpar
Login
```

uma microanimação de pressionamento pode dar bastante sensação de fluidez:


```
normal
 ↓
scale 0.98
 ↓
scale 1.00
```

Extremamente rápida.

A ideia é o usuário perceber:

> “eu interagi com esse elemento”.

---

# 16. Cards de configuração

Você já possui cards de tema.

Esses componentes seriam excelentes para:


```
hover
→ pequena elevação
→ sombra levemente maior
```

e seleção:


```
borda azul
+ pequena transição de escala
```

Mas eu evitaria exagerar nos `dropshadow`.

Uma alternativa ainda mais barata visualmente seria:


```
borda
+ mudança de fundo
+ translateY(-1)
```

em vez de sombra forte.

---

# 17. Tela de carregamento

Esse é provavelmente o melhor recurso para esconder a percepção de espera do banco.

Por exemplo, quando o sistema estiver consultando o MySQL:


```
┌───────────────────────────────┐
│       Carregando aluno...     │
│                               │
│              ◌               │
└───────────────────────────────┘
```

Ou uma pequena animação:


```
⠋
⠙
⠹
⠸
⠼
...
```

Mas o ponto fundamental é:

**a animação só funciona como UX se a consulta estiver em background.**

Se você deixar:


```
JDBC bloqueando FX Thread
```

nem o loading vai conseguir animar direito.

---

# 18. Skeleton loading

Quando o sistema evoluir para tabelas maiores, eu gosto bastante dessa abordagem.

Em vez de:


```
[ tela vazia ]
```

mostrar:


```
████████████████████
██████████
█████████████████
████████
```

com uma pequena animação de shimmer.

Isso faz a aplicação parecer muito mais rápida, mesmo quando existe uma consulta real acontecendo.

Para JavaFX eu usaria isso com moderação, porque pode acabar sendo mais complexo do que o próprio problema.

---

# 19. O seu `Platform.runLater()` não resolve o problema principal

No empréstimo você tem:


```
Platform.runLater(() -> input_matricula.clear());
```

É importante perceber:


```
Platform.runLater()
```

não significa:

> “execute essa operação em outra thread”.

Ele significa:

> “coloque isso para a JavaFX Application Thread executar”.

Ou seja:


```
BuscarAlunoPorMatricula();
```

continua sendo executado na thread gráfica.

Então o fluxo atual é essencialmente:


```
FX Thread
 ├─ recebe caractere
 ├─ chama JDBC
 ├─ espera MySQL
 ├─ termina consulta
 └─ agenda clear
```

É justamente o que precisa ser evitado.

---

# 20. Pequenas otimizações que existem, mas não atacaria agora

Há algumas microotimizações no código.

Por exemplo:


```
newValue.matches("\\d*")
```

e:


```
replaceAll(...)
```

usados durante digitação.

Regex é relativamente caro comparado a uma simples checagem de caracteres.

Seu código poderia futuramente usar `TextFormatter` ou uma validação mais direta.

Mas eu colocaria isso em:

**Prioridade baixa.**

Não vai transformar uma aplicação travada em uma aplicação fluida.

---

# 21. Outro ponto para o futuro: paginação

Hoje existem repositories como:


```
LivroRepository.listar()
```

que fazem:


```
SELECT id, titulo, autor, sinopse FROM livros
```

sem limite.

E:


```
ColaboradorRepository.listar()
```

faz o mesmo.

Agora isso é tranquilo porque o banco aparentemente é pequeno.

Mas pense em:


```
10 livros       → tranquilo
1.000 livros    → tranquilo/moderado
10.000 livros   → já merece atenção
100.000 livros  → não deveria carregar tudo
```

Para uma biblioteca real, eu projetaria desde cedo:


```
LIMIT
OFFSET
ORDER BY
filtro
busca
```

ou paginação equivalente.

E aqui existe um detalhe importante: `sinopse` é `TEXT`, então carregar milhares de sinopses desnecessariamente pode aumentar bastante o volume de dados transferido.

---

# 22. O maior ganho de arquitetura que eu faria

Eu organizaria a evolução de performance assim:


```
                CIBI
                 │
        ┌────────┴────────┐
        │                 │
      JavaFX           Background
       Thread             Work
        │                 │
        │             ┌───┴────┐
        │             │        │
        │           JDBC     outras tarefas
        │             │
        └─────────────┘
             ↑
        apenas UI update
```

Ou seja:

### Thread JavaFX

Responsável por:


```
• botões
• campos
• layout
• animações
• troca de telas
• atualizar Labels
• atualizar TableView
```

### Threads de trabalho

Responsáveis por:


```
• MySQL
• arquivos
• backup
• relatórios
• operações grandes
```

Essa separação provavelmente produziria uma diferença perceptível no seu sistema.

---

# 23. Minha classificação dos gargalos encontrados

| Prioridade | Problema | Impacto provável |
| --- | --- | --- |
| 🔴 Muito alta | JDBC na JavaFX Application Thread                      | Travamentos perceptíveis                     |
| 🔴 Muito alta | Busca de aluno chamada dentro do listener de digitação | Muito provável de causar “freeze” em leitura |
| 🟠 Alta | Nova conexão JDBC a cada operação | Latência acumulada |
| 🟠 Média | Salvar/restaurar configurações na UI thread | Pequenos congelamentos |
| 🟡 Média | Primeiro carregamento de FXML | Pequeno pico ao abrir tela |
| 🟡 Média | Animação alterando `prefWidth` | Pode gerar mais layout durante frames |
| 🟢 Baixa | Regex em listeners | Microcusto |
| 🟢 Baixa | CSS/shadows atuais | Pouco provável de ser o gargalo |
| 🟢 Baixa | Cache de telas | Atualmente é mais benefício que problema |
| 🟡 Futuro | `listar()` sem paginação | Problema quando banco crescer |

---

# 24. O que eu faria primeiro, em ordem

Sem alterar o seu código agora, meu roteiro técnico seria:

### Etapa 1 — medir

Antes de otimizar cegamente, medir:


```
login
busca matrícula
cadastro
abrir configurações
troca de telas
```

e descobrir:


```
tempo de FXML
tempo de conexão
tempo de query
tempo de processamento
tempo de renderização
```

### Etapa 2 — separar I/O da UI

Prioridade absoluta:


```
Task / ExecutorService / JavaFX Service
```

para:


```
login
busca aluno
cadastro
configurações
livros
futuras consultas
```

### Etapa 3 — conexão

Depois:


```
Connection Pool
```

### Etapa 4 — UX

Depois:


```
loading
fade
slide
microinterações
```

### Etapa 5 — escala

Quando o sistema crescer:


```
paginação
filtros
lazy loading
cache de dados
```

---

# 25. Uma observação importante sobre o projeto

Existe documentação interna (`doc/erros.md`) que **já identifica o problema do JDBC na JavaFX Application Thread** como um problema de desempenho, inclusive mencionando `Task`/`Service`.

Ou seja, a análise do próprio projeto já chegou à mesma conclusão.

Isso reforça bastante que esse é realmente o ponto central a atacar.

Também notei que parte da documentação parece estar defasada em relação ao código atual — por exemplo, ela ainda descreve algumas telas como stubs apesar de já existir implementação de `ConfiguracoesController`. Isso não afeta diretamente a velocidade, mas pode atrapalhar futuras otimizações porque o estado documentado e o estado real do projeto não estão totalmente sincronizados.

## Minha conclusão

**Eu não tentaria “otimizar JavaFX” primeiro. Eu corrigiria o modelo de execução das operações de banco.**

Hoje o cenário é aproximadamente:


```
Usuário
   ↓
JavaFX
   ↓
evento
   ↓
JDBC
   ↓
espera MySQL
   ↓
JavaFX volta a responder
```

O cenário ideal é:


```
Usuário
   ↓
JavaFX responde imediatamente
   ↓
inicia tarefa em background
   ↓
JDBC / MySQL
   ↓
resultado
   ↓
JavaFX atualiza a tela
```

Isso, combinado com **transições curtas de** **`Fade + Translate`**, feedback de carregamento e microanimações**, provavelmente vai mudar bastante a sensação de fluidez do CIBI.

O ponto mais crítico que encontrei especificamente no seu projeto é a **busca automática do aluno aos 15 caracteres**, porque ela dispara uma consulta ao MySQL dentro do listener do campo. Eu trataria esse fluxo como prioridade máxima.
