# Log — CIBI

Histórico cronológico das sessões de trabalho no projeto (mais recente no topo).

---

## 2026-08-01 — Revisão completa, estabilização e sincronização documental

**Participantes:** Bruno + Codex.

**O que foi feito:**
- Leitura integral da documentação antes da inspeção do código.
- Inventário do módulo Maven, das 20 classes Java, recursos JavaFX e schemas SQL.
- Análise integral do Java e dos vínculos FXML/controller/recurso.
- Correção de cinco erros de compilação ligados aos construtores de `Aluno`/`Colaborador` e à tela de usuários.
- Alinhamento de autenticação/listagem ao schema `colaboradores` de `BD/Query.txt`.
- Validações defensivas, preservação de causas de exceção e propagação de falhas de navegação.
- Externalização da configuração JDBC e restrição do bypass de login ao modo de desenvolvimento explícito.
- Alinhamento dos sete FXML ao JavaFX 22 e ativação do botão “Limpar”.
- Validação com `mvn test`, `mvn package`, parsing XML e checagem de recursos/segredos/queries legadas.

**Resultado:** build e empacotamento concluídos com sucesso. Não existem testes automatizados; os fluxos contra MySQL real não foram executados nesta sessão. Pendências e prioridades estão em `erros.md` e `tarefas.md`.

## 2026-07-22 — Sessão de análise técnica

**Participantes:** Bruno + Claude (assistente)

**O que foi feito:**
- Extração e leitura completa do projeto (`cibi.zip`): todas as classes Java, todos os FXML/CSS, `pom.xml`, histórico do Git e os dumps SQL em `BD atual 20260311/`.
- Análise de estrutura, organização e qualidade do código.
- Cruzamento entre `model/repository` e o schema real do banco, revelando incompatibilidades não visíveis olhando só o código Java isoladamente.
- Criação da documentação de apoio ao projeto: `contexto.md`, `arquitetura.md`, `decisoes.md`, `erros.md`, `tarefas.md`, `roadmap.md`, `prompts.md`, este `log.md`.

**Principais achados:**
- Cadastro de colaboradores provavelmente quebrado (construtores não atribuem campos herdados).
- Login provavelmente quebrado contra o schema atual (colunas/tabela incompatíveis).
- Referências a CSS que só existem em build antiga (`target/classes`), ausentes do código-fonte atual.
- `NullPointerException` potencial em `LivroService.BuscarLivro`.
- Credenciais de banco versionadas em texto plano.

**Estado do projeto ao fim da sessão:** nenhum código foi alterado — apenas análise e documentação. Próximo passo natural: atacar os bloqueadores listados em `tarefas.md`.

---

## Commits anteriores (reconstruído do histórico do Git, sem data exata de sessão)

- `c7d3190` — Adição do estilo do menu de Usuários.
- `5740aac` — Menu lateral animado + busca de aluno por matrícula.
- `41ed924` — Busca de aluno por matrícula (implementação inicial).
- `df91f27` — Busca de livro por ID.
- `729c311` — Reorganização e reestruturação das classes (raiz do refactor `Conta`→`Colaborador`, ainda incompleto — ver `erros.md`).
- `cf937c6` — Correção da pasta do BD (dumps SQL).
- `c0a6c72` — Tela de login funcional.
- `025ce11`, `7a9eb34`, `c3fb8c8`, `736e858`, `f44495a` — Ajustes de Readme.
- `e1f8df0` — Tela de login (versão não funcional, inicial).
- `066d77a`, `878c091` — Commits de teste inicial do repositório.

---

## Modelo para novas entradas

```md
## AAAA-MM-DD — Título curto da sessão

**Participantes:** ...
**O que foi feito:** ...
**Decisões tomadas:** (linkar em decisoes.md, se aplicável)
**Problemas encontrados:** (linkar em erros.md, se aplicável)
**Próximos passos:** (linkar em tarefas.md, se aplicável)
```
