# Roadmap — CIBI

Planejamento de médio/longo prazo. Para o que já está pronto para ser feito agora, ver `tarefas.md`.

## Fase 0 — Estabilização (pré-requisito para tudo mais)

Objetivo: fazer o que já existe funcionar de ponta a ponta contra o banco real.

- [x] Corrigir a cadeia de compilação/construtores de `Colaborador`/`Aluno` e alinhar repositories ao schema `colaboradores`.
- [x] Validar CSS/imagens/FXML e garantir que `mvn package` builda a partir dos fontes atuais.
- [x] Retirar a senha do banco do código atual e exigir configuração externa.
- [ ] Rotacionar a senha anteriormente versionada e validar login/cadastro contra uma instância MySQL provisionada por um schema versionado.

> Enquanto a Fase 0 não estiver concluída, novas funcionalidades tendem a ser construídas sobre uma base instável.

## Fase 1 — Completar o núcleo de gestão de acervo

- Tela de listagem de livros (usar `LivroRepository.listar()`, hoje sem UI).
- Cadastro de livro pela UI (a validação de título já existe em `LivroService.InserirLivro`; falta o formulário).
- Edição e exclusão de livro pela UI.
- Dashboard com indicadores básicos (nº de livros, nº de alunos cadastrados, empréstimos ativos).

## Fase 2 — Fluxo de empréstimos completo

- Modelar entidade `Emprestimo` (aluno, livro, data de retirada, data prevista de devolução, data real de devolução, status).
- Registrar empréstimo após a busca do aluno (hoje o fluxo para na exibição do aluno encontrado).
- Registrar devolução.
- Alertas de atraso.

## Fase 3 — Gestão de usuários/colaboradores

- Listagem de colaboradores cadastrados (usar `ColaboradorRepository.listar()`, hoje sem UI).
- Edição e exclusão de colaborador pela UI.
- Perfis de acesso (exigem modelagem e migração, pois o schema atual de `colaboradores` não possui coluna de perfil).

## Fase 4 — Segurança e robustez

- Hash de senha (BCrypt/Argon2) em vez de texto plano.
- [x] Configuração de conexão via propriedades/variáveis de ambiente, fora do versionamento.
- Substituir `System.out.println` por logging estruturado.
- Cobertura de testes automatizados para `Service` e `Repository` (hoje inexistente).
- [x] Ocultar e bloquear o botão “Pular Login” fora de ambiente de desenvolvimento explicitamente habilitado.

## Fase 5 — Polimento de UX

- Página "Configurações" (`ConfiguracoesController`, hoje vazia) — definir o que ela deve configurar (tema? dados da instituição? backup do banco?).
- Padronizar visualmente as páginas que ainda usam `Label` simples (`Dashboard`, `Livros`, `Configuracoes`) no mesmo nível visual da tela de `Usuarios`.
- Revisar consistência de nomenclatura/idioma nas telas (mistura de português nos textos e inglês/português nos identificadores de código).

## Ideias em aberto (não priorizadas)

- Exportação de relatórios (livros emprestados, atrasos, acervo total) em PDF/planilha.
- Histórico de empréstimos por aluno.
- Backup automático do banco.
- Criação de um aplicativo voltado para os alunos.

## Como usar este roadmap

- Itens aqui são **objetivos**, não tarefas prontas para execução — ao decidir começar uma fase, quebrar em itens acionáveis dentro de `tarefas.md`.
- Reordenar fases é esperado conforme prioridades mudarem; quando isso acontecer, registrar o motivo em `decisoes.md`.
