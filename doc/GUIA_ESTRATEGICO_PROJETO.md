# Guia estratégico do projeto CIBI

**Atualizado em:** 10/11/2026  
**Repositório analisado:** `cibi codex`  
**Objetivo deste arquivo:** dar a uma IA ou pessoa desenvolvedora contexto rápido, confiável e verificável sobre o projeto e sobre as alterações recentes no diretório de trabalho.

## 1. Visão do projeto

CIBI significa **Controle Interno de Biblioteca**. É uma aplicação desktop para apoiar a operação de uma biblioteca escolar/institucional: autenticação de colaboradores, gestão de usuários, consulta e gestão do acervo e, como objetivo de produto, empréstimos e devoluções.

O projeto é um sistema JavaFX, organizado em telas FXML/CSS e código Java. O repositório também contém scripts SQL e documentação de apoio ao TCC.

## 2. Tecnologias e execução

- **Java 22** e **JavaFX 22**, com interface declarada em FXML e estilos em CSS.
- **Maven** como ferramenta de build. O `pom.xml` declara `com.cibi.application.MainApp` como entrada e o plugin JavaFX.
- **MySQL via JDBC** (`mysql-connector-j`), sem ORM.
- Inicialização descrita no `Readme.md`: configurar JDK/Maven e executar `mvn javafx:run` na raiz do projeto. Para fluxos dependentes do banco, configurar a conexão pelos parâmetros aceitos em `src/main/java/com/cibi/database/DatabaseConfig.java` e preparar o schema compatível com os repositories.

Não conclua que todos os módulos já persistem no MySQL: a seção 5 descreve uma exceção importante no módulo de livros.

## 3. Mapa de código

Raiz Java: `src/main/java/com/cibi/`  
Recursos da interface: `src/main/resources/com/cibi/`  
Scripts e snapshots SQL: `BD/`  
Documentação e material de pesquisa: `doc/`

| Área | Papel principal |
|---|---|
| `application/MainApp.java` | inicializa JavaFX, login e console de desenvolvimento |
| `controller/` | login e janela principal com menu e navegação |
| `controller/pages/` | controllers de dashboard, livros, empréstimos, usuários, configurações e categorias |
| `Service/` | validações e coordenação de regras de negócio; o `S` maiúsculo é uma convenção legada do projeto |
| `repository/` | persistência JDBC e, no fluxo novo de livros, armazenamento temporário em memória |
| `model/` | modelos de domínio simples; `Livro` recebeu campos adicionais de catalogação |
| `database/` | configuração JDBC e fábrica de conexões |
| `util/` | navegação, ferramentas de desenvolvimento, janela e animação de modais |
| `fxml/`, `css/` | telas e apresentação JavaFX |

A arquitetura documentada historicamente é Controller → Service → Repository → Database, com Model como objeto de domínio. Confira a implementação atual antes de assumir que toda tela segue essa separação: algumas partes antigas podem divergir e o módulo de livros foi recentemente alterado.

## 4. Fluxo da aplicação

`MainApp` abre a tela de login ou, sob configuração de desenvolvimento, o console/bypass. Após o login, `MainController` monta a navegação lateral e utiliza `NavigationManager` para carregar as telas internas. A tela inicial configurada é Dashboard. Os botões levam a Dashboard, Livros, Empréstimos, Usuários e Configurações.

O código oferece funcionalidades reais de login e acesso a dados para alguns módulos. A existência de uma tela ou de um botão, por si só, não comprova que todo o fluxo de empréstimos, relatórios ou persistência esteja completo.

## 5. Estado atual do módulo de livros — leia com atenção

As alterações locais recentes implementam uma experiência de gestão de livros mais ampla:

- Listagem tabular com busca, filtros por categoria/status/ano, ordenação, paginação e indicadores resumidos.
- Formulário para cadastrar e editar dados principais, bibliográficos e de acervo; valida campos, ISBN, ano, páginas e quantidades.
- Rascunho de formulário, ações de editar/excluir e exportação CSV.
- Gerenciamento de categorias com adição, renomeação e exclusão, usando confirmação e atualização dos livros quando uma categoria é renomeada.
- Animações para modais, com escurecimento/desfoque do fundo e restauração do estado visual.

**Limite de persistência observado no código:** `LivroService` usa `LivroTemporarioRepository`, cujo mapa estático mantém registros apenas durante a execução atual. `CategoriaLivroService` também guarda a lista de categorias em memória. O próprio FXML informa que o cadastro é provisório. Os campos extras adicionados a `Livro` estão comentados como ainda fora do schema SQL. Portanto, não descreva a nova interface como persistência durável: os livros, categorias e rascunhos não sobrevivem ao encerramento do processo. O CSV é uma exportação explícita, não uma substituição da integração com o banco.

Pontos de entrada para entender esse fluxo:

- `src/main/java/com/cibi/controller/pages/LivrosController.java`
- `src/main/java/com/cibi/controller/pages/LivroFormularioController.java`
- `src/main/java/com/cibi/controller/pages/GerenciarCategoriasController.java`
- `src/main/java/com/cibi/controller/pages/ConfirmacaoCategorias.java`
- `src/main/java/com/cibi/Service/LivroService.java`
- `src/main/java/com/cibi/Service/CategoriaLivroService.java`
- `src/main/java/com/cibi/repository/LivroTemporarioRepository.java`
- `src/main/java/com/cibi/model/Livro.java`
- `src/main/resources/com/cibi/fxml/pages/Livros.fxml`
- `src/main/resources/com/cibi/fxml/pages/LivroFormulario.fxml`
- `src/main/resources/com/cibi/fxml/pages/GerenciarCategorias.fxml`
- `src/main/resources/com/cibi/css/pages/Livros.css`
- `src/main/java/com/cibi/util/AnimacaoModal.java`
- `doc/animacoes-de-modais.md`

## 6. Últimas alterações identificadas

O commit mais recente em `HEAD` na análise foi `8cb41b4` (`Atualizacao icones menu lateral`). **As mudanças abaixo estavam no diretório de trabalho e ainda não estavam registradas nesse commit**; consulte `git status` e `git diff` novamente para confirmar se outra pessoa já as alterou:

1. **Expansão da tela de livros:** substituição do fluxo anterior de listagem/formulários inline pela listagem com filtros, ordenação, paginação, exportação e telas de formulário/gerenciamento de categorias.
2. **Novas regras e modelo de livro:** `Livro` agora contém subtítulo, editora, categoria, ISBN, dados bibliográficos, quantidade, status e localização; `LivroService` valida ISBN, ano, páginas e consistência de estoque.
3. **Armazenamento provisório:** inclusão de `LivroTemporarioRepository` e mudança de `LivroService` para usar essa implementação em memória.
4. **Categorias editáveis em sessão:** inclusão de `CategoriaLivroService`, edição de categorias no modal, confirmação de mudanças e propagação de renomeações para livros associados.
5. **Animação reutilizável de modais:** nova classe `AnimacaoModal` e documentação em `doc/animacoes-de-modais.md`; integração em formulários/confirmações e confirmação de fechamento da janela.
6. **Ajustes de janela e build:** alteração da abertura/fechamento entre login e tela principal, mais mudança do `artifactId` Maven para `cibi_codex` e adição do nome do projeto.

Além dos fontes, o `git status` observado tinha alterações em `target/` e arquivos do estado de compilação Maven. Trate `target/` como artefato de build, não como fonte primária para explicar comportamento. Também havia itens não rastreados, incluindo um relatório PDF de pesquisa em `doc/pesquisas/biblioteca_cg/`; não os apague nem os sobrescreva.

## 7. Documentação existente e descompassos

Consulte `doc/contexto.md`, `doc/arquitetura.md`, `doc/decisoes.md`, `doc/roadmap.md`, `doc/tarefas.md`, `doc/erros.md` e `doc/log.md` como histórico e contexto. Eles foram escritos em revisões anteriores e contêm datas/descrições antigas. Em particular, partes do roadmap/tarefas/contexto ainda tratam a tela de livros como ausente ou stub, o que não reflete a interface encontrada na árvore atual. Ao mesmo tempo, a interface nova é provisória e não resolve a persistência MySQL.

Para entender o estado real, dê prioridade a esta ordem:

1. fontes Java e recursos atuais;
2. `git status`, `git diff` e o commit-base;
3. schema SQL e queries dos repositories;
4. documentação, interpretada como histórico e requisitos, validando-a contra o código.

## 8. Riscos e perguntas que precisam ser respondidas

- Qual deve ser o schema definitivo para os campos novos de livro e para categorias? A tabela atual `livros` e `LivroRepository` devem ser comparados com o modelo novo antes de integrar persistência.
- Rascunho, catálogo e categorias são intencionalmente apenas temporários neste estágio ou devem sobreviver a reinícios?
- Quais módulos estão completos de ponta a ponta contra uma instância MySQL real? Não deduza isso de um build compilável.
- O fluxo de empréstimo/devolução deve ser verificado nos controllers, services, repositories e schema; não confunda busca de aluno com empréstimo implementado.
- A documentação mais antiga deve ser atualizada depois que os requisitos e o estado pretendido forem confirmados.
- Distinguir alterações de código-fonte de alterações em classes compiladas e metadados dentro de `target/`.

## 9. Regras de leitura para a próxima IA

- Use evidências do repositório atual; indique os caminhos dos arquivos e, quando possível, métodos/linhas.
- Separe **fato observado**, **inferência** e **lacuna**. Não converta hipótese ou documentação antiga em fato atual.
- Verifique a árvore de trabalho antes de relatar “últimas alterações”; mudanças locais podem não estar commitadas.
- Não exponha valores de credenciais encontrados no código/configuração. Descreva somente os mecanismos de configuração.
- Preserve arquivos e mudanças locais. Não faça refatorações ou edições durante a análise sem pedido explícito.
