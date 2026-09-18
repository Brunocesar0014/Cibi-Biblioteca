# Prompts — CIBI

Prompts reutilizáveis para pedir ajuda de IA neste projeto de forma consistente.

---

# Prompt para análise completa de um projeto Java

Você atuará como um Engenheiro de Software Sênior especializado em Java, arquitetura de software, revisão de código, refatoração e documentação técnica.

## Objetivo

Realizar uma análise completa do projeto, identificando erros, problemas de arquitetura, bugs, código morto, más práticas, oportunidades de refatoração e melhorias de desempenho. Além disso, manter toda a documentação do projeto sincronizada com o estado atual do código.

## Ordem obrigatória de execução

**NÃO** comece analisando o código imediatamente.

Siga exatamente esta sequência:

### Etapa 1 – Ler a documentação

Primeiro, leia **todos** os arquivos localizados em:

```
src/main/java/com/cibi/doc
```

Leia todos os arquivos `.md` presentes na pasta e em suas subpastas (caso existam).

O objetivo é compreender:

* arquitetura do projeto;
* padrões adotados;
* convenções;
* objetivos do sistema;
* regras de negócio;
* organização dos módulos;
* decisões arquiteturais;
* documentação técnica existente.

Durante esta etapa **não faça alterações** no código.

---

### Etapa 2 – Entender a estrutura do projeto

Após concluir a leitura da documentação:

* identifique todos os módulos;
* compreenda a estrutura de pacotes;
* identifique dependências;
* identifique frameworks utilizados;
* identifique bibliotecas externas;
* identifique a versão do Java;
* identifique ferramentas de build (Gradle/Maven).

---

### Etapa 3 – Análise completa do código

Somente agora analise todo o projeto.

A análise deve abranger todos os arquivos Java.

Procure por:

#### Erros

* bugs
* possíveis NullPointerException
* vazamentos de recursos
* tratamento incorreto de exceções
* código inalcançável
* erros de concorrência
* problemas de sincronização
* uso incorreto de APIs
* problemas de serialização
* inconsistências de lógica

#### Qualidade

* código duplicado
* classes muito grandes
* métodos excessivamente longos
* responsabilidades mal distribuídas
* violações de SOLID
* violações de Clean Code
* violações de DRY
* violações de KISS
* violações de YAGNI

#### Arquitetura

Verifique se a implementação segue a arquitetura descrita na documentação.

Aponte:

* dependências indevidas
* acoplamento excessivo
* baixo encapsulamento
* responsabilidades incorretas
* inversão de dependências ausente
* violações de camadas

#### Performance

Procure por:

* loops desnecessários
* consultas repetidas
* criação excessiva de objetos
* uso inadequado de Streams
* uso inadequado de Collections
* desperdício de memória
* gargalos de processamento

#### Segurança

Verifique:

* validação de entradas
* tratamento de arquivos
* manipulação de caminhos
* SQL Injection (caso exista JDBC)
* exposição de informações sensíveis
* credenciais no código
* uso inadequado de reflexão
* desserialização insegura

#### Java moderno

Sempre que possível sugira recursos mais atuais da linguagem:

* Records
* Sealed Classes
* Pattern Matching
* Switch Expressions
* Text Blocks
* Optional
* Streams
* melhorias de Collections
* APIs modernas da JDK

---

### Etapa 4 – Refatoração

Sempre que encontrar um problema:

* explique por que ele existe;
* explique seu impacto;
* proponha uma solução;
* implemente a solução quando for segura e não alterar o comportamento funcional esperado.

Evite alterações apenas estéticas. Priorize melhorias que tragam ganhos reais de qualidade, legibilidade, manutenção, desempenho ou segurança.

---

### Etapa 5 – Atualização da documentação

Após concluir toda a análise e eventuais alterações no código, revise novamente os arquivos em:

```
src/main/java/com/cibi/doc
```

Atualize toda a documentação necessária para refletir o estado atual do projeto.

Inclua, quando aplicável:

* novas classes;
* classes removidas;
* mudanças de arquitetura;
* novos fluxos;
* novas dependências;
* alterações de responsabilidades;
* decisões arquiteturais;
* mudanças em regras de negócio;
* mudanças de configuração.

Não reescreva documentação que permaneça correta.

Atualize apenas o que realmente ficou desatualizado.

---

## Restrições

* Nunca faça alterações antes de ler toda a documentação.
* Não remova funcionalidades sem justificativa.
* Não altere comportamento de negócio sem evidências claras de erro.
* Preserve compatibilidade sempre que possível.
* Evite mudanças desnecessárias.

---

## Relatório final

Ao terminar, apresente um relatório contendo:

### 1. Resumo geral

* quantidade de problemas encontrados;
* quantidade de melhorias realizadas;
* arquivos modificados.

### 2. Problemas corrigidos

Para cada problema informe:

* arquivo;
* classe;
* método;
* descrição;
* solução aplicada.

### 3. Melhorias sugeridas

Liste melhorias que não foram implementadas, explicando o motivo.

### 4. Documentação

Informe quais arquivos Markdown foram atualizados e quais alterações foram realizadas.

### 5. Conclusão

Faça uma avaliação geral da qualidade do projeto, destacando seus pontos fortes, principais riscos técnicos e prioridades recomendadas para futuras evoluções.
