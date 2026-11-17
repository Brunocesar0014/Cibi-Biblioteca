# Prompt para análise e compreensão do projeto CIBI

Copie o texto abaixo para a IA que fará a análise:

---

Atue como analista de software e leia o repositório CIBI para construir uma compreensão técnica verificável do projeto. O projeto é uma aplicação desktop Java/JavaFX para controle de biblioteca. Comece pelo arquivo `doc/GUIA_ESTRATEGICO_PROJETO.md`, mas trate-o como contexto inicial a ser validado, não como verdade imutável.

## Objetivos da análise

1. Explicar o propósito do sistema, seus usuários e os fluxos que existem hoje.
2. Mapear arquitetura, dependências, telas, controllers, services, repositories, models, banco e recursos FXML/CSS.
3. Identificar quais capacidades estão implementadas, parciais, provisórias ou apenas planejadas.
4. Explicar as últimas mudanças no código comparando o estado de trabalho com `HEAD`, sem atribuir mudanças locais a commits.
5. Destacar inconsistências entre código, schema SQL e documentação antiga, sobretudo no fluxo de livros.
6. Entregar uma visão que permita a outro desenvolvedor continuar o projeto sem confundir intenção, implementação e pendências.

## Roteiro de inspeção

1. Consulte `git status --short`, `git log -5 --oneline` e os diffs de fontes. Registre qual é o commit-base e se há mudanças locais. Confira também arquivos novos não rastreados; eles não aparecem em `git diff` padrão.
2. Leia `pom.xml`, `Readme.md` e a documentação principal em `doc/`: `contexto.md`, `arquitetura.md`, `decisoes.md`, `roadmap.md`, `tarefas.md`, `erros.md` e `log.md`.
3. Mapeie os arquivos de `src/main/java`, `src/main/resources` e `BD`. Siga os fluxos desde a inicialização/login e navegação até os controllers, services, repositories e banco.
4. Para cada tela, confira o FXML, o `fx:controller`, os `fx:id`, os handlers, os recursos CSS/imagens e o controller correspondente. Não presuma que um botão ou uma tela parcial implementa o fluxo inteiro.
5. Compare cada model e repository com as tabelas e colunas dos scripts SQL realmente usados. Identifique campos sem persistência e diferenças entre dumps atuais e legados.
6. Analise especificamente `Livro`, `LivroService`, `LivroRepository`, `LivroTemporarioRepository`, `CategoriaLivroService`, controllers/FXML de livros e categorias. Confirme se os dados sobrevivem ao encerramento do app e como funciona o CSV.
7. Analise separadamente empréstimos: determine se existe criação/devolução persistida ou somente busca de aluno/interface parcial.
8. Trate `target/` como saída compilada; use os arquivos-fonte como autoridade para comportamento. Não divulgue credenciais ou valores secretos.

## Formato da resposta

Entregue um relatório em português, com estas seções:

1. **Resumo executivo** — o que é o CIBI e qual é seu estado real.
2. **Arquitetura e execução** — tecnologias, inicialização, navegação e camadas, com caminhos relevantes.
3. **Mapa de funcionalidades** — tabela com funcionalidade, evidência no código, estado (implementada/parcial/provisória/planejada) e limitações.
4. **Últimas alterações** — separar claramente alterações já commitadas de mudanças locais não commitadas; agrupar por objetivo e citar arquivos.
5. **Dados e persistência** — módulos conectados ao MySQL, módulos em memória, schema utilizado e incompatibilidades.
6. **Descompassos documentais** — afirmações nos Markdown antigos que não correspondem ao código atual, indicando também quando a divergência pode ser uma escolha provisória.
7. **Riscos e lacunas** — ordenar por impacto e explicar a evidência, sem exagerar riscos hipotéticos.
8. **Próximos passos sugeridos** — ações pequenas e ordenadas, distinguindo decisão de produto de tarefa técnica.
9. **Perguntas em aberto** — somente perguntas cuja resposta não possa ser obtida dos arquivos.

Para cada conclusão importante, cite caminhos de arquivos e, se disponível, nomes de classes/métodos ou linhas. Marque cada conclusão como **observado**, **inferido** ou **não confirmado** quando isso evitar ambiguidade. Se um ponto não puder ser verificado, diga o que faltou.

## Limites

Esta é uma análise, não uma autorização para mudar o projeto. Não edite nem apague arquivos, não rode migrações, não altere dados e não faça commits. Não execute testes ou builds; apenas recomende comandos de verificação se forem úteis. Preserve todo o estado de trabalho, inclusive arquivos não rastreados e artefatos do usuário.

---
