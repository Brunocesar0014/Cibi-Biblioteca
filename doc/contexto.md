# Contexto — CIBI (Controle Interno de Biblioteca)

## O que é o projeto

CIBI é uma aplicação **desktop** para controle de uma biblioteca escolar/institucional. Permite login de colaboradores, cadastro de usuários (colaboradores/alunos), busca de livros e um fluxo de "empréstimo" iniciado pela busca de um aluno por matrícula (pensado para funcionar com leitor de código QR de carteirinha).

## Objetivo

Centralizar em um único sistema:
- Autenticação de colaboradores da biblioteca.
- Cadastro de usuários e colaboradores do sistema.
- Cadastro e consulta de livros do acervo.
- Consulta rápida de alunos por matrícula, como base para o fluxo de empréstimos.
- Melhorar o acesso e a organização das informações relacionadas ao acervo bibliográfico.
- Garantir maior segurança e integridade no armazenamento de dados e geração de relatórios
- Facilitar o trabalho dos gestores por meio de uma interface intuitiva e funcional.

## Tecnologias

| Camada | Tecnologia |
|---|---|
| Linguagem | Java 22 |
| UI | JavaFX 22 (FXML + CSS) |
| Build | Maven (`javafx-maven-plugin`, `maven-compiler-plugin`) |
| Banco de dados | MySQL 8 (via `mysql-connector-j` 9.3.0, JDBC puro) |
| IDE de desenvolvimento | NetBeans (há `nbactions.xml` no repositório) |
| Versionamento | Git |

Não há framework de injeção de dependência, ORM, logging, nem testes automatizados configurados — é uma aplicação Java "pura", camada por camada, escrita manualmente.

## Funcionalidades

### Implementadas (ao menos parcialmente)
- **Login** (`Tela_Login.fxml` + `LoginController`) com animação de floating label.
- **Tela principal** com sidebar retrátil e navegação entre páginas (`MainController` + `NavigationManager`).
- **Cadastro de usuários/colaboradores** (`Usuarios.fxml` + `UsuariosController`), incluindo máscara de telefone.
- **Busca de livro por ID** existe no controller/service/repository, mas `Livros.fxml` ainda é um stub e não expõe o campo/ação na interface.
- **Busca de aluno por matrícula** com gatilho automático ao atingir 15 dígitos, simulando leitura de carteirinha (`EmprestimosController`).

### Ainda não implementadas (stubs vazios)
- Dashboard (`DashboardController` vazio).
- Configurações (`ConfiguracoesController` vazio).
- Fluxo de empréstimo em si (hoje só existe a busca do aluno; não há registro de empréstimo/devolução).
- Listagem/edição/exclusão de livros e usuários pela UI (os métodos existem no repository, mas não estão ligados a nenhuma tela).

## Regras de negócio identificadas no código

- Delegação de funções:

| Processo             | Colaborador | Gestora |
| -------------------  | ----------: | ------: |
| Realizar empréstimo  |           ✓ |       ✓ |
| Realizar devolução   |           ✓ |       ✓ |
| Renovar empréstimo   |           ✓ |       ✓ |
| Cadastrar livro      |           ✗ |       ✓ |
| Consultar acervo     |           ✓ |       ✓ |
| Relatórios           |           ? |       ✓ |
| Cadastrar Colaborador|           ✗ |       ✓ |
| Configurações        |           ✗ |       ✓ |


- Um colaborador só pode ser inserido se tiver nome, telefone (10–11 dígitos), matrícula positiva, turma e senha preenchidos (`ColaboradorService.inserirColabrador`).
- Login exige usuário e senha não vazios, e falha lançando exceção se a combinação não for encontrada (`ColaboradorService.RealizarLogin`).
- Busca de aluno por matrícula rejeita matrícula não positiva e lança erro se o aluno não existir (`AlunoService.BuscarAlunoPorMaticula`).
- Busca de livro rejeita objeto nulo e ID não positivo (`LivroService.BuscarLivro`).
- Cadastro de livro exige objeto e título não nulos/não vazios (`LivroService.InserirLivro`).
- Login e cadastro usam a tabela `colaboradores` definida em `BD/Query.txt`; os dumps com `usuarios` estão explicitamente desatualizados.
- Configuração JDBC: `CIBI_DB_URL`, `CIBI_DB_USER` e `CIBI_DB_PASSWORD` (ou propriedades `cibi.db.url`, `cibi.db.user`, `cibi.db.password`). URL e usuário possuem defaults locais; senha não possui segredo padrão.

> Nota: o build foi validado em 01/08/2026, mas os fluxos JDBC não foram testados contra uma instância MySQL real nesta revisão.
