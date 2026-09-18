# Decisões — CIBI

Registro de decisões de projeto identificadas no código/histórico do Git, com o motivo (quando inferível) e o status atual. Novas decisões devem ser adicionadas no topo, com data.

---

## [2026-08-01] Configuração externa do banco e bypass de login opt-in

**Decisão:** ler URL, usuário e senha do banco por propriedades de sistema ou variáveis de ambiente; manter apenas defaults não sensíveis para URL/usuário. Ocultar e bloquear “Pular Login” por padrão, liberando-o somente por configuração explícita de desenvolvimento.

**Motivo:** retirar o segredo do código versionado e impedir contorno acidental da autenticação em execução normal, sem eliminar a conveniência de desenvolvimento.

**Status:** ✅ Implementada. Variáveis: `CIBI_DB_URL`, `CIBI_DB_USER`, `CIBI_DB_PASSWORD` e `CIBI_DEV_SKIP_LOGIN`; propriedades equivalentes: `cibi.db.url`, `cibi.db.user`, `cibi.db.password` e `cibi.dev.skipLogin`.

## [Sem data] Refactor `Conta` → `Colaborador`

**Decisão:** renomear a entidade `Conta`/`ContaRepository`/`ContaService` para `Colaborador`/`ColaboradorRepository`/`ColaboradorService`, e `Sobre.fxml` para `Configuracoes.fxml`.

**Motivo:** Simplificar o banco de dados e integrar os ajudantes da biblioteca ao gremio estudantil. E renomear a tela de "Sobre" para "Configurações", Pois a sessão sobre estará no site.

**Status:** ✅ Concluído no código atual. Os construtores atribuem os campos herdados e `ColaboradorRepository` usa `colaboradores`, conforme `BD/Query.txt`. Os dumps antigos com `usuarios` permanecem arquivados em pasta marcada como desatualizada.

---

## [Sem data] `Colaborador extends Aluno`

**Decisão:** modelar `Colaborador` como subclasse de `Aluno`, herdando `matricula`, `nome`, `turma`.

**Motivo:** evitar duplicar esses três campos, já que um colaborador também tem matrícula/nome/turma.

**Considerações:** herança aqui está sendo usada não só para reúso de campos, mas tabém para uma relação real de "é um". **Um `Colaborador` "é um" `Aluno`**.

---

## [Sem data] Persistência via JDBC puro, sem ORM

**Decisão:** usar `PreparedStatement`/`ResultSet` manualmente em vez de JPA/Hibernate ou um micro-ORM.

**Motivo:** simplicidade para um projeto de porte pequeno/didático, controle total sobre as queries, menos dependências.

**Status:** ✅ Mantida e coerente com o tamanho do projeto. Ponto de atenção: sem ORM, a responsabilidade de manter model e schema sincronizados é manual; a divergência corrigida em 01/08/2026 demonstra a necessidade de migrações versionadas.

---

## [Sem data] `NavigationManager` com cache de telas (`HashMap<String, Parent>`)

**Decisão:** carregar cada FXML de página interna uma única vez e reaproveitar o `Parent` já montado nas trocas seguintes de tela.

**Motivo:** performance (evita parsing de FXML repetido) e preservação de estado da tela ao navegar (campos preenchidos não se perdem ao trocar de aba e voltar).

**Status:** ✅ Boa decisão, bem executada. Trade-off aceito: telas que dependem de dados "frescos" (ex.: uma lista que muda com frequência) vão precisar de um mecanismo próprio de refresh, já que o `Parent` cacheado não é recriado.

---

## [Sem data] Gatilho automático de busca ao atingir 15 caracteres (`EmprestimosController`)

**Decisão:** disparar `BuscarAlunoPorMatricula()` automaticamente quando o campo de matrícula atinge exatamente 15 dígitos, em vez de exigir clique/Enter.

**Motivo :** compatibilidade com leitor de código QR de carteirinha estudantil, que "digita" o código completo de uma vez.

**Status:** ✅ Decisão específica e bem justificada pelo contexto de uso (biblioteca escolar).

---

## [Sem data] Credenciais de banco em constantes estáticas (`DatabaseConfig`) — superada

**Decisão:** manter usuário/senha do MySQL como `String` estáticas dentro do próprio código-fonte, versionado no Git.

**Motivo:** simplicidade durante desenvolvimento local.

**Status:** ❌ Revertida em 01/08/2026 pela decisão de configuração externa acima. A senha anteriormente versionada deve ser rotacionada, pois removê-la do arquivo atual não a apaga do histórico Git.

---

## Modelo para novas decisões

```md
## [DATA] Título da decisão

**Decisão:** o que foi decidido.
**Motivo:** por que foi decidido assim (alternativas consideradas, se houver).
**Status:** ✅ mantida / ⚠️ parcial / 🔴 revisar / ❌ revertida.
```
