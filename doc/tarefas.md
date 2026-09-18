# Tarefas — CIBI

Backlog organizado por status. Ao mover uma tarefa de coluna, registrar a data. Itens `[erros.md #N]` referenciam o número do problema em `erros.md`.

## 🔴 Bloqueadores (impedem o app de funcionar corretamente)

- [ ] Validar login e cadastro contra uma instância MySQL criada a partir de um schema versionado.
- [ ] Rotacionar a senha do banco anteriormente exposta no histórico Git.

## 🟠 Em andamento

_(nenhuma tarefa em andamento registrada — atualizar aqui conforme o trabalho avançar)_

## 📋 Pendentes — funcionalidades

- [ ] Implementar `DashboardController` (hoje é um stub vazio)
- [ ] Implementar `ConfiguracoesController` (hoje é um stub vazio)
- [ ] Implementar o fluxo de empréstimo em si (hoje só existe a busca do aluno; falta registrar empréstimo/devolução, prazos, etc.)
- [ ] Ligar `LivroRepository.listar()`/`excluir()` a uma tela (hoje só existem no repository, sem UI)
- [ ] Ligar `ColaboradorRepository.listar()`/`excluir()` a uma tela de gestão de usuários
- [ ] Expor o cadastro/busca de livros em `Livros.fxml` (a validação de título já existe na Service)

## 📋 Pendentes — correções de qualidade

- [ ] Adicionar hash de senha (Argon2id/BCrypt) com migração do schema — `[erros.md #1]`
- [ ] Tirar JDBC da JavaFX Application Thread — `[erros.md #2]`
- [ ] Criar testes automatizados básicos (Service e Repository) — `[erros.md #3]`
- [ ] Renomear pacote `Service` → `service` e padronizar métodos/repositories — `[erros.md #4]`
- [ ] Consolidar scripts de banco em migrações versionadas — `[erros.md #5]`
- [ ] Introduzir logging estruturado e exceções de aplicação — `[erros.md #7]`

## ✅ Concluídas

- [x] Tela de login funcional (UI + animações)
- [x] Estrutura em camadas Controller/Service/Repository
- [x] Sidebar retrátil animada na tela principal
- [x] `NavigationManager` com cache de telas
- [x] Máscara de telefone na tela de usuários
- [x] Busca de livro por ID nas camadas Java (a ligação com `Livros.fxml` permanece pendente)
- [x] Busca de aluno por matrícula com gatilho automático (leitor de carteirinha)
- [x] Análise técnica completa do código atual (22/07/2026) — ver `arquitetura.md`, `erros.md`, `decisoes.md`
- [x] Corrigir os cinco erros de compilação dos models/controllers (01/08/2026)
- [x] Alinhar login/listagem de colaboradores a `BD/Query.txt` (01/08/2026)
- [x] Validar campos obrigatórios no cadastro de livros (01/08/2026)
- [x] Externalizar configuração JDBC e restringir bypass de login (01/08/2026)
- [x] Validar `mvn test`/`mvn package`, FXML e recursos (01/08/2026; zero testes automatizados)

## Convenções desta lista

- Funcionalidades dependentes de persistência só vão para “Concluídas” após teste contra o banco real; tarefas puramente estruturais podem ser concluídas pelo build/verificações estáticas correspondentes.
- Bloqueadores têm prioridade sobre qualquer nova funcionalidade.
