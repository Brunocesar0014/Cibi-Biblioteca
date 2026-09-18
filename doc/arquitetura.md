# Arquitetura — CIBI

## Visão geral do padrão

O projeto segue uma arquitetura em camadas:

```
Controller (FXML)  →  Service  →  Repository  →  Database
      ↑                                              |
      └──────────────── Model (POJO) ←───────────────┘
```

- **Controller**: recebe eventos da UI, faz parsing/validação superficial de input, chama a Service e reflete o resultado na tela (Alert, texto, etc).
- **Service**: validações de negócio (campos obrigatórios, formatos, existência), lança exceções (`IllegalArgumentException`, `IllegalStateException`, `RuntimeException`) quando a regra é violada.
- **Repository**: acesso a dados via JDBC puro (`PreparedStatement`), sem ORM.
- **Model**: POJOs simples (getters/setters), sem anotações, sem builders.
- **Database**: configuração de conexão centralizada.

## Estrutura de pastas (`src/main/java/com/cibi`)

```
application/
  MainApp.java                 → ponto de entrada (extends Application)

controller/
  LoginController.java
  MainController.java          → shell principal (sidebar + navegação)
  pages/
    DashboardController.java
    LivrosController.java
    EmprestimosController.java
    UsuariosController.java
    ConfiguracoesController.java

Service/                       → (nome de pacote com "S" maiúsculo — ver arquitetura > pontos de atenção)
  AlunoService.java
  ColaboradorService.java
  LivroService.java

repository/
  AlunoRepository.java
  ColaboradorRepository.java
  LivroRepository.java

model/
  Aluno.java
  Colaborador.java (extends Aluno)
  Livro.java

database/
  DatabaseConfig.java           → configuração via propriedades/variáveis de ambiente
  ConnectionFactory.java        → fábrica estática de Connection

util/
  NavigationManager.java        → carrega/cacheia FXML de páginas internas
```

## Recursos (`src/main/resources/com/cibi`)

```
fxml/
  Tela_Login.fxml
  Tela_Principal.fxml
  pages/
    Dashboard.fxml
    Livros.fxml
    Emprestimos.fxml
    Usuarios.fxml
    Configuracoes.fxml

css/
  login.css
  principal.css
  pages/Usuarios.css

imagens/
  logo.png, logo_SemLetras.png, icone.png, icone_livro.png

icones/
  ampulheta.png
```

As referências atuais de CSS e imagens foram validadas em 01/08/2026 e apontam para recursos existentes. Os sete FXML usam o namespace do JavaFX 22, em linha com o `pom.xml`.

## Fluxo de execução

1. `MainApp.start()` carrega `Tela_Login.fxml` (520x400, não redimensionável).
2. `LoginController` autentica via `ColaboradorService.RealizarLogin`, consultando `colaboradores.nome`/`senha`. O bypass de desenvolvimento só aparece com `-Dcibi.dev.skipLogin=true` ou `CIBI_DEV_SKIP_LOGIN=true`.
3. Ao autenticar abre `Tela_Principal.fxml` em um novo `Stage` (1280x720 mínimo) e fecha a janela de login.
4. `MainController.initialize()` monta a sidebar, registra os botões de menu e abre a tela inicial (`Dashboard`) via `NavigationManager`.
5. `NavigationManager.loadScreen(nome, caminhoFxml)` carrega o FXML uma única vez por nome de tela e reaproveita o `Parent` já carregado nas próximas trocas (cache em `HashMap<String, Parent>`).
6. Cada página interna tem seu controller. Login, empréstimos e usuários seguem Controller → Service → Repository; `LivrosController` ainda acessa `LivroRepository` diretamente e está registrado como violação pendente em `erros.md`.

## Modelo de dados (classes)

```
Aluno
 ├─ matricula: long
 ├─ nome: String
 └─ turma: String

Colaborador extends Aluno
 ├─ senha: String
 └─ telefone: String

Livro
 ├─ id: int
 ├─ titulo: String
 ├─ autor: String
 └─ sinopse: String
```

## Banco de dados

O schema de referência para o código atual está em `BD/Query.txt`:

```sql
alunos     (matricula PK, nome, turma)
colaboradores (matricula PK, nome UNIQUE, senha, telefone, turma)
livros     (id PK auto_increment, titulo, autor, sinopse)
```

Os dumps em `BD/cibi 2026 03 11 (desatualizado)/` são legados e ainda contêm a tabela `usuarios`; não devem ser usados para validar a versão atual sem uma decisão explícita de migração.

## Responsabilidades por módulo (resumo)

| Módulo | Responsabilidade | Status |
|---|---|---|
| `application` | Bootstrap da aplicação JavaFX | OK |
| `controller` (raiz) | Login e shell principal com sidebar | Funcional; bypass de login desabilitado por padrão |
| `controller.pages` | Telas internas de conteúdo | Parcial — 2 de 5 são stubs vazios |
| `Service` | Regras de negócio e validação | Funcional no escopo atual; título obrigatório validado em livros |
| `repository` | Acesso a dados JDBC | Alinhado ao schema de referência em `BD/Query.txt` |
| `model` | Entidades de domínio | POJOs mutáveis com construtores vazio e completo |
| `database` | Configuração de conexão | URL/usuário/senha externos, com defaults não sensíveis |
| `util` | Navegação entre telas | Cache de FXML e propagação explícita de falhas de carregamento |

## Pontos de atenção arquitetural

- Pacote `Service` com "S" maiúsculo quebra a convenção Java (pacotes em minúsculo).
- Mistura de métodos `static` e de instância dentro da mesma classe de repositório/serviço, sem critério consistente.
- Não há camada de DTO — os models são usados diretamente do banco até a UI.
- Não há testes (`src/test` existe mas está vazio).
- Não há logging estruturado (uso de `System.out.println`).
- Consultas JDBC são executadas na JavaFX Application Thread e podem congelar a interface em conexões lentas.
