# Erros — CIBI

Registro técnico consolidado após a revisão de 01/08/2026. Os itens pendentes estão ordenados por risco; correções concluídas aparecem no fim.

## Pendentes

### 1. Senhas de colaboradores permanecem em texto plano — segurança alta

**Onde:** `colaboradores.senha`, `ColaboradorRepository.buscarPorUsuarioESenha`.

**Impacto:** uma leitura indevida do banco revela todas as credenciais. O limite `varchar(20)` também é insuficiente para hashes modernos.

**Solução recomendada:** migrar a coluna para tamanho compatível, aplicar Argon2id ou BCrypt no cadastro e verificar o hash no login. Requer migração coordenada do banco e das senhas existentes; por isso não foi aplicada automaticamente.

### 2. JDBC executado na JavaFX Application Thread — desempenho médio/alto

**Onde:** controllers de login, empréstimos e usuários.

**Impacto:** conexão ou consulta lenta congela a interface.

**Solução recomendada:** executar chamadas de Service em `Task`/`Service` do JavaFX ou executor dedicado e atualizar controles apenas na thread da UI.

### 3. Ausência de testes automatizados — qualidade alta

**Onde:** `src/test/java` vazio e sem dependência de teste no `pom.xml`.

**Impacto:** mudanças em validação, queries e controllers não possuem proteção contra regressão. `mvn test` valida compilação, mas executa zero testes.

**Solução recomendada:** adicionar JUnit 5; injetar repositories nas services para testes unitários; usar Testcontainers ou banco dedicado nos testes JDBC.

### 4. Convenções e dependências inconsistentes — arquitetura média

**Onde:** pacote `com.cibi.Service`, métodos em PascalCase/typos, mistura de operações estáticas e de instância nos repositories.

**Impacto:** aumenta a surpresa da API e dificulta injeção de dependências/testes.

**Solução recomendada:** migrar de forma controlada para `com.cibi.service`, camelCase e dependências por construtor. A mudança toca imports/FXML e deve ser feita separadamente.

### 5. Duas representações incompatíveis do schema — manutenção alta

**Onde:** `BD/Query.txt` define `colaboradores`; `BD/cibi 2026 03 11 (desatualizado)/` contém `usuarios`.

**Impacto:** um desenvolvedor pode provisionar a versão errada e quebrar login/cadastro.

**Solução recomendada:** adotar migrações versionadas (Flyway/Liquibase ou scripts numerados) e arquivar os dumps legados fora do caminho principal.

### 6. Telas e ações ainda incompletas — funcional médio

**Onde:** `Livros.fxml`, `Dashboard.fxml`, `Configuracoes.fxml`, botões de usuários e fluxo de empréstimo.

**Impacto:** há código de busca de livro não acessível pela UI, controllers vazios e botões sem ação. `LivrosController` ainda acessa o repository diretamente, violando a camada Service documentada.

**Solução recomendada:** concluir cada fluxo com requisitos de negócio definidos e mover todo acesso a dados para services.

### 7. Tratamento/logging ainda genérico — observabilidade média

**Onde:** `EmprestimosController`, `LivrosController` e outros catches amplos.

**Impacto:** erros operacionais podem ser confundidos com entrada inválida; `System.out.println` não oferece nível, contexto nem destino configurável.

**Solução recomendada:** exceções de aplicação tipadas, mensagens de UI separadas de detalhes técnicos e logging estruturado (`java.util.logging` ou SLF4J).

## Resolvidos em 01/08/2026

1. **Projeto não compilava (cinco diagnósticos):** restaurado construtor vazio de `Aluno`, corrigido `super(...)` de `Colaborador` e ajustada a chamada no cadastro.
2. **Login consultava colunas inexistentes em `usuarios`:** query alinhada a `colaboradores.nome`/`senha` conforme `BD/Query.txt`.
3. **Listagem misturava tabelas e omitia `turma`:** query corrigida para `colaboradores` com todas as colunas lidas.
4. **Cadastro continuava após falha ao ler matrícula:** controller agora encerra o fluxo imediatamente.
5. **Validação ausente em `LivroService.InserirLivro`:** objeto e título obrigatórios são validados antes do JDBC.
6. **Entradas nulas/não positivas nas services:** adicionadas validações defensivas para colaborador, livro, matrícula e ID.
7. **Causa de falhas JDBC era descartada:** wrappers de Service preservam a exceção original como causa.
8. **`NavigationManager` ocultava falhas com `printStackTrace`:** recursos ausentes e erros de carga agora são propagados com contexto.
9. **Senha do MySQL hardcoded:** configuração movida para propriedades/variáveis de ambiente; a credencial exposta ainda precisa ser rotacionada.
10. **“Pular Login” sempre disponível:** recurso oculto/bloqueado por padrão e habilitado apenas por configuração de desenvolvimento.
11. **FXML declarava JavaFX 25 com runtime 22:** os sete namespaces foram alinhados ao JavaFX 22.
12. **Botão “Limpar” sem ação:** ligado ao método existente e campos também são limpos após cadastro bem-sucedido.

## Resolvido antes desta revisão, confirmado em 01/08/2026

- `LivroService.BuscarLivro` já verificava retorno nulo do repository antes de acessar o título, eliminando o `NullPointerException` documentado anteriormente.
