# Relatório de Levantamento de Requisitos
## Biblioteca Popular de Campo Grande

**Data da entrevista:** 20/08/2026  
**Fonte principal:** transcrição da entrevista realizada na Biblioteca Popular de Campo Grande  
**Finalidade:** subsidiar o levantamento de requisitos, a definição do escopo e as decisões de desenvolvimento do sistema de gerenciamento de biblioteca proposto no TCC.

---

## Sumário

1. [Objetivo da pesquisa](#1-objetivo-da-pesquisa)  
2. [Caracterização da pesquisa](#2-caracterização-da-pesquisa)  
3. [Perfil e organização da biblioteca](#3-perfil-e-organização-da-biblioteca)  
4. [Situação atual dos processos](#4-situação-atual-dos-processos)  
   4.1. [Gerenciamento geral](#41-gerenciamento-geral)  \
   4.2. [Cadastro de livros](#42-cadastro-de-livros)  \
   4.3. [Cadastro e identificação dos usuários](#43-cadastro-e-identificação-dos-usuários)  \
   4.4. [Empréstimo](#44-empréstimo)  \
   4.5. [Devolução](#45-devolução)  \
   4.6. [Renovação](#46-renovação)  \
   4.7. [Controle de atrasos](#47-controle-de-atrasos)  \
   4.8. [Livros perdidos ou danificados](#48-livros-perdidos-ou-danificados)  \
   4.9. [Organização e localização do acervo](#49-organização-e-localização-do-acervo)  \
   4.10. [Ferramentas utilizadas](#410-ferramentas-utilizadas)  
5. [Problemas e limitações identificados](#5-problemas-e-limitações-identificados)  
6. [Regras de negócio levantadas](#6-regras-de-negócio-levantadas)  
7. [Atores e permissões](#7-atores-e-permissões)  
8. [Informações, indicadores e relatórios](#8-informações-indicadores-e-relatórios)  
9. [Requisitos funcionais candidatos](#9-requisitos-funcionais-candidatos)  
10. [Requisitos não funcionais candidatos](#10-requisitos-não-funcionais-candidatos)  
11. [Decisões já sustentadas pelos dados coletados](#11-decisões-já-sustentadas-pelos-dados-coletados)  
12. [Questões ainda em aberto](#12-questões-ainda-em-aberto)  
13. [Priorização das funcionalidades](#13-priorização-das-funcionalidades)  
14. [Impactos no desenvolvimento do TCC](#14-impactos-no-desenvolvimento-do-tcc)  
15. [Próximos passos recomendados](#15-próximos-passos-recomendados)  
16. [Conclusão](#16-conclusão)  
17. [Anexo A — Matriz resumida de processos e atores](#anexo-a--matriz-resumida-de-processos-e-atores)

---

# 1. Objetivo da pesquisa

A entrevista teve como objetivo compreender como a Biblioteca Popular de Campo Grande realiza atualmente suas principais atividades de gerenciamento, identificando os procedimentos utilizados, as responsabilidades dos funcionários, as regras aplicadas aos serviços e as dificuldades encontradas no controle das informações.

Os dados coletados serão utilizados como base para o desenvolvimento do sistema de gerenciamento de biblioteca proposto no TCC. Dessa forma, a entrevista não deve ser entendida apenas como uma descrição da rotina da instituição, mas como uma fonte primária para o levantamento de necessidades, regras de negócio, requisitos e decisões de escopo.

O foco deste relatório é transformar os dados coletados em uma visão estruturada do problema, distinguindo três níveis:

1. **Dado coletado:** informação efetivamente apresentada durante a entrevista.
2. **Interpretação:** problema ou necessidade que pode ser extraído desse dado.
3. **Decisão proposta:** encaminhamento de desenvolvimento que pode ser adotado a partir da evidência coletada.

Essa distinção é importante para evitar que funcionalidades sejam incorporadas ao sistema sem justificativa ou que hipóteses de desenvolvimento sejam apresentadas como necessidades já confirmadas pela biblioteca.

# 2. Caracterização da pesquisa

A fonte analisada é uma entrevista aplicada à Biblioteca Popular de Campo Grande em **20 de agosto de 2026**. O roteiro foi organizado em blocos relacionados ao perfil e à rotina da gestora, funcionamento geral da biblioteca, atendimento, empréstimos, devoluções, renovações, atrasos, perdas e danos, organização física do acervo, ferramentas utilizadas, indicadores e permissões de usuários do futuro sistema.

A entrevista evidencia uma instituição que atualmente utiliza uma combinação de **planilhas físicas e Excel** para manter seus registros. Também foi relatado que, anteriormente, parte do controle era realizada manualmente e que o sistema Biblion, utilizado no passado, foi descontinuado.

Portanto, o problema central observado não é a inexistência de qualquer controle, mas a existência de um controle predominantemente manual e pouco automatizado, com dificuldades de consulta, acompanhamento e atualização das informações.

# 3. Perfil e organização da biblioteca

A entrevista concentra-se principalmente na descrição dos processos sob responsabilidade da gestora e dos demais funcionários. Os dados registrados indicam a existência de pelo menos dois perfis de atuação relevantes para o futuro sistema: **Gestora** e **Colaborador/Funcionário autorizado**.

A divisão preliminar de responsabilidades indica que:

- empréstimos podem ser realizados por qualquer funcionário;
- devoluções e renovações também fazem parte das operações do atendimento;
- o cadastro de livros é uma atividade exclusiva da gestora;
- o cadastro de colaboradores é indicado como atividade da gestora;
- configurações do sistema devem permanecer sob responsabilidade da gestora;
- todos os funcionários autorizados utilizariam o sistema no cotidiano.

Ainda existem pontos que precisam ser confirmados, sobretudo o nível de acesso aos relatórios e a extensão exata das permissões de cada perfil.

# 4. Situação atual dos processos

## 4.1. Gerenciamento geral

### Dado coletado

O gerenciamento atual da biblioteca é realizado por uma **planilha do Excel**. Foi relatado que, há pouco tempo, o controle era realizado manualmente. O funcionamento cotidiano depende de atividades como empréstimos, devoluções, cadastro de livros, inscrição dos leitores e monitoramento dos dados.

O controle de livros e pessoas é feito por meio de **planilhas físicas e Excel**. Para saber se um livro está disponível, emprestado ou atrasado, é necessário realizar uma busca manual na planilha.

### Interpretação

A principal limitação é a dependência de consultas manuais e registros distribuídos. Isso aumenta o esforço operacional e dificulta obter rapidamente uma visão confiável do estado atual do acervo e dos empréstimos.

### Necessidade identificada

É necessário centralizar as informações em uma solução que permita consultar e atualizar os dados de maneira estruturada.

### Decisão proposta

O sistema deverá substituir o uso cotidiano das planilhas como mecanismo principal de controle, mantendo uma base de dados centralizada e operações específicas para cada processo.

## 4.2. Cadastro de livros

### Dado coletado

Quando um livro novo chega, seus dados são cadastrados e, posteriormente, o livro é encaminhado diretamente para a estante. O cadastro é realizado exclusivamente pela gestora.

Os campos informados atualmente são:

1. título;
2. autor;
3. assunto;
4. editora;
5. ID/matrícula do livro;
6. numeração da prateleira.

### Interpretação

O processo já possui um conjunto mínimo de informações definido e uma responsabilidade claramente atribuída. O principal ganho esperado de um sistema é garantir consistência, identificação única e possibilidade de consulta dessas informações.

### Necessidade identificada

O sistema deve permitir o cadastro estruturado dos livros e a associação de cada exemplar à sua localização física.

### Decisão proposta

O cadastro de livros deverá ser restrito à gestora, conforme informado na entrevista, e os campos atuais deverão servir como base inicial do modelo de dados.

## 4.3. Cadastro e identificação dos usuários

### Dado coletado

Os usuários da biblioteca são identificados pela **matrícula escolar** ou, no caso de pessoas maiores de idade, pelo **CPF**. As informações registradas incluem nome e matrícula/CPF. Não existe carteirinha.

Durante o atendimento, não há um processo automatizado de identificação: a conferência é feita a partir dos dados registrados na planilha.

### Interpretação

O processo possui uma identificação definida, mas depende da consulta manual à planilha. A ausência de uma identificação automatizada dificulta a rapidez do atendimento e limita mecanismos automáticos de controle.

### Necessidade identificada

É necessário manter um cadastro de usuários com identificador único e permitir sua localização rápida durante as operações.

### Decisão proposta

O sistema deverá utilizar a matrícula ou o CPF como referência de identificação, respeitando as regras estabelecidas pela biblioteca. A definição detalhada de tratamento, armazenamento e exibição do CPF ainda deve ser validada no projeto.

## 4.4. Empréstimo

### Dado coletado

No empréstimo, os dados são registrados na planilha e o livro passa a ser considerado emprestado. O empréstimo pode ser realizado por qualquer funcionário. Não existe atualmente um processo automatizado para verificar a disponibilidade do livro.

Foi informado um limite de **até dois livros simultaneamente por usuário**.

### Interpretação

O procedimento atual depende de registro manual e não possui validação automática da disponibilidade. Isso cria risco de consultas demoradas e dificulta a aplicação automática das regras de empréstimo.

### Necessidade identificada

O sistema deve associar usuário, exemplar e empréstimo, além de verificar a disponibilidade antes da conclusão da operação.

### Decisão proposta

O empréstimo deverá ser uma operação controlada pelo sistema, com validação do limite de livros e atualização automática do status do exemplar.

## 4.5. Devolução

### Dado coletado

Após a devolução, é realizada uma verificação da integridade do livro. Depois disso, o exemplar é encaminhado novamente à estante. A entrevistada informou que a verificação não é profunda e que, em alguns casos, pode não ser possível identificar imediatamente uma página danificada.

Devem ser registrados o livro devolvido, sua integridade e, caso esteja danificado, a ação que será tomada.

### Interpretação

A devolução não representa apenas a mudança do status do empréstimo: também envolve uma avaliação física do exemplar e, em certos casos, uma ocorrência de dano.

### Necessidade identificada

O sistema deve permitir registrar a devolução e, quando necessário, associar uma ocorrência de dano e a medida adotada.

### Decisão proposta

A operação de devolução deve incluir um estado ou registro de integridade do exemplar, sem transformar o sistema em um mecanismo de inspeção detalhada que não faz parte da prática atual.

## 4.6. Renovação

### Dado coletado

É possível renovar um empréstimo, acrescentando mais **30 dias** ao período de posse do livro. Foi informado o limite de **uma renovação**.

### Interpretação

A regra é suficientemente objetiva para ser implementada como validação do sistema.

### Necessidade identificada

O sistema deverá verificar se o empréstimo ainda pode ser renovado e impedir novas renovações após o limite estabelecido.

### Decisão proposta

A renovação será implementada como uma operação vinculada ao empréstimo existente, com extensão do prazo em 30 dias e controle da quantidade de renovações já utilizadas.

## 4.7. Controle de atrasos

### Dado coletado

O acompanhamento de empréstimos atrasados é realizado exclusivamente de forma manual, por meio de buscas na planilha. Atualmente não existe uma forma de contato com a pessoa quando o empréstimo está atrasado.

Não há multa. Também não existe bloqueio, embora tenha sido considerado que um software apropriado poderia oferecer esse recurso. A entrevistada relatou que o controle gera dificuldade porque é um trabalho manual e que, quando uma pessoa atrasa, atualmente não há uma ação efetiva a ser realizada.

### Interpretação

O controle de atrasos é um dos pontos de maior deficiência identificados na entrevista. O problema não é apenas descobrir quais empréstimos estão atrasados, mas também a ausência de mecanismos que permitam reagir a essa situação.

### Necessidade identificada

O sistema precisa identificar automaticamente empréstimos atrasados e apresentar essa informação de maneira clara.

### Decisão proposta

Deve existir um mecanismo automático de identificação de atraso. O bloqueio de novos empréstimos por inadimplência pode ser previsto como funcionalidade, mas sua adoção definitiva deve ser confirmada com a biblioteca antes de ser considerada regra obrigatória.

A criação de multas **não deve ser incorporada ao sistema neste momento**, pois a entrevista informa explicitamente que atualmente não existe multa.

## 4.8. Livros perdidos ou danificados

### Dado coletado

Quando uma pessoa informa que perdeu um livro, deve trazer outro exemplar do mesmo livro ou outro equivalente. Quando um livro é gravemente danificado, a mesma regra é aplicada. Entretanto, se a pessoa simplesmente não devolver o livro e não informar a ocorrência, a biblioteca informou que atualmente não há ação a ser tomada.

### Interpretação

Existe uma regra institucional para substituição de exemplares perdidos ou gravemente danificados, mas o controle dessas ocorrências é limitado pelo processo atual.

### Necessidade identificada

É útil registrar ocorrências relacionadas à perda ou dano e guardar a medida tomada, permitindo histórico e acompanhamento.

### Decisão proposta

O sistema deve permitir registrar a situação do exemplar e a providência adotada. As regras detalhadas para substituição, baixa definitiva ou regularização do cadastro ainda precisam ser especificadas pela biblioteca.

## 4.9. Organização e localização do acervo

### Dado coletado

Os livros são organizados segundo um sistema de classificação por **número de chamada**, utilizando:

- Classificação Decimal de Dewey (CDD);
- código do autor segundo a Tabela Cutter;
- dados complementares, quando aplicáveis.

Na prática, os funcionários frequentemente localizam os livros por memorizarem a organização física do acervo. Não existe uma consulta informatizada da localização, sendo necessário procurar fisicamente o exemplar. A entrevistada considerou que informar a localização física no sistema seria útil por economizar tempo.

### Interpretação

A biblioteca possui uma organização física definida, porém essa informação não está disponível de maneira eficiente no processo de consulta. Parte do conhecimento sobre a localização está concentrada na experiência dos funcionários.

### Necessidade identificada

O sistema deve armazenar e apresentar a localização física do exemplar.

### Decisão proposta

A localização da prateleira deve integrar os dados do livro/exemplar e aparecer nas consultas ao acervo.

## 4.10. Ferramentas utilizadas

### Dado coletado

Atualmente é utilizada uma planilha do **Excel**. Anteriormente, a biblioteca utilizava o **Biblion**, que foi descontinuado.

### Interpretação

O sistema proposto precisa atuar como substituto funcional do controle realizado atualmente pela planilha, levando em consideração que houve uma solução anterior que deixou de estar disponível.

### Necessidade identificada

É necessária uma solução própria, estável e compatível com o fluxo de trabalho atual da biblioteca.

### Decisão proposta

O Excel deve ser tratado como fonte de dados atual e referência de operação durante a transição, e não como parte estrutural do novo sistema.

# 5. Problemas e limitações identificados

Com base na entrevista, os problemas podem ser agrupados em oito categorias principais.

| Problema | Evidência na situação atual | Impacto esperado |
|---|---|---|
| Dependência de planilhas | Excel e planilhas físicas são utilizados no gerenciamento | Maior esforço para registrar e consultar informações |
| Consulta manual do acervo | Disponibilidade e localização dependem de buscas manuais | Atendimento mais lento |
| Ausência de automação | Não há processo automatizado para verificar disponibilidade | Maior risco de inconsistências e operações demoradas |
| Controle manual de atrasos | Empréstimos atrasados são encontrados por busca na planilha | Dificuldade de acompanhamento |
| Ausência de ação para atrasos | Atualmente não há contato nem bloqueio automático | Baixa capacidade de resposta a atrasos |
| Dependência da memória dos funcionários | Localização dos livros é frequentemente conhecida por experiência | Perda de eficiência e dependência de conhecimento individual |
| Controle limitado de danos | A verificação de integridade pode não detectar certos danos | Possibilidade de ocorrências não registradas |
| Descontinuação da ferramenta anterior | O Biblion deixou de ser utilizado | Necessidade de uma solução substituta |

Os problemas mais diretamente relacionados ao escopo do TCC são os que envolvem **controle do acervo, empréstimos, devoluções, atrasos, usuários, localização e indicadores**.

# 6. Regras de negócio levantadas

As seguintes regras foram explicitamente identificadas na entrevista:

| Código | Regra de negócio |
|---|---|
| RN01 | Um usuário pode manter no máximo 2 livros emprestados simultaneamente. |
| RN02 | O prazo informado para o empréstimo é de 30 dias. |
| RN03 | A renovação adiciona 30 dias ao prazo do empréstimo. |
| RN04 | Cada empréstimo pode ser renovado no máximo 1 vez. |
| RN05 | Não existe multa atualmente para atrasos. |
| RN06 | Não existe bloqueio automático atualmente para atrasos. |
| RN07 | Quando um livro é informado como perdido, deve ser providenciado outro exemplar igual ou equivalente. |
| RN08 | Em caso de dano grave, deve ser providenciado outro exemplar igual ou equivalente. |
| RN09 | O cadastro de livros é responsabilidade exclusiva da gestora. |
| RN10 | Empréstimos podem ser realizados por qualquer funcionário autorizado. |
| RN11 | Usuários são identificados por matrícula escolar ou CPF, conforme o caso informado na entrevista. |
| RN12 | A localização física do livro é associada à organização por prateleira. |

### Observação sobre o prazo de empréstimo

A entrevista informa que o prazo atual é de **30 dias**, enquanto anteriormente era de 14 dias. Para o sistema, o valor de 30 dias deve ser considerado a regra vigente no momento do levantamento, podendo ser alterado futuramente por configuração caso a biblioteca confirme essa necessidade.

### Observação sobre "prazo padrão"

Existe uma pequena ambiguidade no roteiro: ao ser perguntada se há um prazo padrão de devolução, foi registrada a resposta "não necessariamente", mas, na pergunta seguinte, foi informado que o prazo é igual para todos e que não depende do tipo de livro. Por esse motivo, a regra de **30 dias para todos os empréstimos** deve ser tratada como a interpretação operacional mais consistente com as respostas, mas vale confirmá-la em uma etapa posterior de validação.

# 7. Atores e permissões

A entrevista permite estabelecer dois atores principais para o sistema: **Gestora** e **Colaborador/Funcionário autorizado**.

## 7.1. Gestora

Responsabilidades claramente indicadas:

- cadastrar livros;
- cadastrar colaboradores;
- realizar configurações;
- realizar empréstimos, devoluções, renovações e consultas, segundo a matriz preliminar;
- acessar relatórios, conforme o entendimento atual da entrevista.

## 7.2. Colaborador

Responsabilidades indicadas:

- realizar empréstimos;
- realizar devoluções;
- realizar renovações;
- consultar o acervo;
- demais operações autorizadas pela gestora.

## 7.3. Pontos que ainda precisam de validação

A entrevista original deixa alguns pontos sem definição explícita. Principalmente:

- se colaboradores podem visualizar todos os relatórios ou apenas parte deles;
- se colaboradores podem cadastrar ou editar usuários;
- se colaboradores podem registrar ocorrências de livros danificados/perdidos;
- quais configurações estarão disponíveis exclusivamente para a gestora;
- se o acesso ao sistema dependerá de contas individuais ou de algum outro mecanismo.

Essas questões devem ser resolvidas antes da definição final dos casos de uso e das permissões do banco de dados.

# 8. Informações, indicadores e relatórios

A entrevista identificou como úteis os seguintes dados e indicadores:

1. quantidade total de livros;
2. quantidade de livros disponíveis;
3. quantidade de livros emprestados;
4. quantidade de empréstimos atrasados;
5. quantidade de usuários cadastrados;
6. livros mais emprestados;
7. períodos com maior quantidade de empréstimos;
8. livros danificados;
9. livros perdidos.

Essas informações podem ser agrupadas em quatro áreas:

### Acervo

- total de livros;
- livros disponíveis;
- livros emprestados;
- livros danificados;
- livros perdidos.

### Usuários

- total de usuários cadastrados.

### Circulação

- quantidade de empréstimos;
- quantidade de atrasos;
- livros mais emprestados;
- períodos de maior circulação.

### Gestão

- indicadores gerais da situação da biblioteca.

A existência desses indicadores justifica a inclusão de um módulo ou área de **relatórios e indicadores** no sistema. Entretanto, o nível de detalhamento, filtros, periodicidade e permissões de acesso ainda devem ser definidos.

# 9. Requisitos funcionais candidatos

Os dados coletados permitem propor os seguintes requisitos funcionais iniciais.

| Código | Requisito funcional candidato | Base da decisão |
|---|---|---|
| RF01 | O sistema deve permitir cadastrar livros. | Processo atual de cadastro; atividade exclusiva da gestora. |
| RF02 | O sistema deve permitir consultar o acervo. | Necessidade de localizar informações sem busca manual. |
| RF03 | O sistema deve informar a situação do livro/exemplar. | Hoje disponibilidade é verificada manualmente. |
| RF04 | O sistema deve registrar usuários. | Usuários são atualmente cadastrados em planilha. |
| RF05 | O sistema deve identificar o usuário por matrícula ou CPF, conforme cadastro. | Processo atual de identificação. |
| RF06 | O sistema deve registrar empréstimos. | Processo principal da biblioteca. |
| RF07 | O sistema deve verificar a disponibilidade do exemplar antes do empréstimo. | Atualmente não existe validação automatizada. |
| RF08 | O sistema deve impedir que um usuário ultrapasse o limite de empréstimos definido. | Limite atual de 2 livros. |
| RF09 | O sistema deve registrar devoluções. | Processo atual de atendimento. |
| RF10 | O sistema deve permitir registrar a integridade/ocorrência de dano na devolução. | Regra informada para inspeção e danos. |
| RF11 | O sistema deve permitir renovar empréstimos. | Processo atual de renovação. |
| RF12 | O sistema deve controlar o limite de uma renovação por empréstimo. | Regra de negócio. |
| RF13 | O sistema deve controlar o prazo do empréstimo. | Regra atual de 30 dias. |
| RF14 | O sistema deve identificar automaticamente empréstimos atrasados. | Controle atualmente manual. |
| RF15 | O sistema deve permitir visualizar os empréstimos atrasados. | Necessidade de acompanhamento. |
| RF16 | O sistema deve registrar livros perdidos ou danificados e a providência adotada. | Processo relatado na entrevista. |
| RF17 | O sistema deve informar a localização física do exemplar. | Necessidade explicitamente considerada útil. |
| RF18 | O sistema deve permitir gerar/consultar indicadores da biblioteca. | Lista de informações úteis informada na entrevista. |
| RF19 | O sistema deve permitir consultar livros mais emprestados. | Indicador solicitado. |
| RF20 | O sistema deve permitir consultar períodos de maior quantidade de empréstimos. | Indicador solicitado. |
| RF21 | O sistema deve controlar permissões de acesso conforme o perfil do usuário. | Existência de gestora e colaboradores com responsabilidades diferentes. |
| RF22 | O sistema deve restringir o cadastro de livros à gestora. | Regra de responsabilidade informada. |

### Requisitos que dependem de validação adicional

Os seguintes requisitos são plausíveis, mas não devem ser considerados definitivamente aprovados apenas com a entrevista atual:

- bloqueio automático de novos empréstimos em caso de atraso;
- envio de notificações aos usuários;
- histórico detalhado de todas as alterações;
- edição de usuários por colaboradores;
- exportação de relatórios;
- importação automática da planilha Excel;
- cadastro de exemplares múltiplos de uma mesma obra;
- baixa definitiva de livros perdidos ou danificados.

# 10. Requisitos não funcionais candidatos

A entrevista não especifica formalmente requisitos não funcionais. Portanto, os itens desta seção devem ser tratados como **necessidades técnicas decorrentes do contexto do projeto**, e não como requisitos expressamente declarados pela entrevistada.

## RNF01 — Usabilidade

O sistema deve permitir que funcionários realizem as operações rotineiras de forma simples e rápida, especialmente consultas, empréstimos e devoluções.

## RNF02 — Controle de acesso

O sistema deve restringir funcionalidades conforme o perfil do usuário, principalmente as operações exclusivas da gestora.

## RNF03 — Integridade dos dados

O sistema deve evitar inconsistências entre usuários, livros, exemplares e empréstimos.

## RNF04 — Disponibilidade das informações

As informações necessárias ao atendimento devem estar acessíveis de forma centralizada, substituindo a dependência de buscas manuais em planilhas.

## RNF05 — Manutenibilidade

A solução deve permitir evolução das regras da biblioteca, como alterações de prazo, limites ou permissões, sem exigir reconstrução completa do sistema.

## RNF06 — Segurança dos dados

Como o cadastro pode conter CPF, o projeto deverá definir mecanismos adequados para proteger dados pessoais e limitar seu acesso conforme a necessidade operacional. A entrevista, entretanto, não detalha políticas de segurança ou requisitos legais, portanto essa parte ainda precisa ser analisada separadamente no TCC.

# 11. Decisões já sustentadas pelos dados coletados

A entrevista fornece evidência suficiente para algumas decisões de escopo e arquitetura funcional.

## 11.1. Centralização dos dados

**Decisão:** utilizar um sistema com banco de dados estruturado como mecanismo principal de armazenamento e consulta.

**Justificativa:** o processo atual depende de Excel, planilhas físicas e buscas manuais.

## 11.2. Automação do controle de circulação

**Decisão:** automatizar empréstimos, devoluções, renovações e identificação de atrasos.

**Justificativa:** essas operações atualmente dependem de registros e consultas manuais.

## 11.3. Controle automático da disponibilidade

**Decisão:** o status do exemplar deve ser atualizado pelo próprio sistema a partir das operações de circulação.

**Justificativa:** atualmente não existe processo automatizado para verificar a disponibilidade.

## 11.4. Controle de localização física

**Decisão:** armazenar a localização do exemplar e exibi-la durante a consulta.

**Justificativa:** a entrevistada declarou que essa informação economizaria tempo.

## 11.5. Controle de permissões

**Decisão:** utilizar pelo menos os perfis Gestora e Colaborador.

**Justificativa:** existem diferenças explícitas de responsabilidade entre os atores.

## 11.6. Relatórios e indicadores

**Decisão:** incluir indicadores básicos de acervo, usuários e circulação.

**Justificativa:** a entrevista fornece uma relação concreta de informações consideradas úteis para acompanhamento.

## 11.7. Ausência de multa como regra inicial

**Decisão:** não implementar cobrança de multa no escopo inicial.

**Justificativa:** a entrevista informa que atualmente não existe multa.

# 12. Questões ainda em aberto

Nem todas as decisões necessárias para o desenvolvimento estão resolvidas pela entrevista. Os seguintes pontos precisam ser validados antes da implementação definitiva.

| Questão | Motivo da pendência | Prioridade |
|---|---|---|
| Quais relatórios cada perfil pode acessar? | A permissão de relatórios não ficou completamente definida. | Alta |
| Colaborador pode cadastrar usuário? | A matriz preliminar indica restrição, mas a entrevista detalhada não confirma toda a regra. | Alta |
| Haverá bloqueio automático por atraso? | Foi considerado útil, mas não é uma regra atual. | Alta |
| Como identificar e tratar um livro não devolvido sem aviso? | A entrevista afirma que atualmente nada é feito. | Média |
| O sistema deverá importar a planilha Excel existente? | Há uso atual do Excel, mas o procedimento de migração não foi discutido. | Alta |
| Haverá histórico das operações? | Não foi explicitamente perguntado. | Média |
| Como serão representadas obras e exemplares? | A entrevista menciona livros e identificadores, mas não detalha o modelo de exemplares. | Alta |
| Quais campos adicionais serão necessários no cadastro? | O levantamento inicial apresenta apenas os campos atuais. | Média |
| Como serão tratadas substituições por livros perdidos/danificados? | A regra geral existe, mas o processo de atualização do acervo não foi detalhado. | Média |
| Como ocorrerá autenticação dos funcionários? | Os mecanismos de login não foram definidos na entrevista. | Alta |

# 13. Priorização das funcionalidades

Uma primeira priorização pode ser estabelecida com base na frequência e no impacto dos problemas identificados.

## Prioridade alta — núcleo do sistema

- cadastro de usuários;
- cadastro de livros;
- consulta ao acervo;
- localização física;
- controle de disponibilidade;
- empréstimo;
- devolução;
- renovação;
- controle de prazos;
- identificação de atrasos;
- autenticação e permissões;
- indicadores básicos.

Essas funcionalidades estão diretamente relacionadas às atividades centrais e aos principais problemas observados.

## Prioridade média — controle e gestão

- registro de danos;
- registro de perdas;
- histórico de ocorrências;
- relatórios mais detalhados;
- filtros avançados de consulta;
- possibilidade de bloqueio por atraso.

## Prioridade baixa ou dependente de decisão

- notificações automáticas;
- integração com serviços externos;
- recursos avançados de análise;
- funcionalidades que não foram apresentadas como necessidade pela biblioteca.

Essa priorização deve ser revista após a validação dos requisitos com a orientadora e, idealmente, com a biblioteca.

# 14. Impactos no desenvolvimento do TCC

Os resultados da entrevista alteram e fortalecem diretamente algumas decisões de modelagem do sistema.

## 14.1. Modelagem de dados

O banco de dados deverá representar, no mínimo, entidades relacionadas a:

- usuários;
- colaboradores;
- livros/obras;
- exemplares;
- empréstimos;
- renovações;
- ocorrências de dano/perda;
- localização física;
- permissões/perfis;
- dados necessários aos indicadores.

A separação entre **obra** e **exemplar** deve ser analisada antes da modelagem definitiva, porque a biblioteca possui identificadores e localização física que podem estar associados especificamente a exemplares.

## 14.2. Casos de uso

A entrevista fornece base para casos de uso como:

- autenticar usuário;
- cadastrar livro;
- cadastrar usuário;
- consultar acervo;
- realizar empréstimo;
- realizar devolução;
- renovar empréstimo;
- consultar atrasos;
- registrar dano/perda;
- consultar indicadores;
- administrar colaboradores;
- administrar configurações.

## 14.3. Regras de validação

O sistema deverá validar regras como:

- máximo de 2 empréstimos ativos por usuário;
- prazo de 30 dias;
- no máximo 1 renovação;
- disponibilidade do exemplar;
- permissões do usuário autenticado.

## 14.4. Interface

A interface deve priorizar consultas rápidas e operações de atendimento. Como a principal dificuldade atual é a dependência de buscas manuais, a tela de consulta do acervo deve ser especialmente eficiente.

## 14.5. Relatórios

Os indicadores levantados devem influenciar tanto o modelo de dados quanto a interface administrativa. É necessário armazenar informações suficientes para responder perguntas como:

- quantos livros estão disponíveis;
- quantos estão emprestados;
- quantos estão atrasados;
- quais livros são mais emprestados;
- em quais períodos a circulação é maior;
- quais livros estão danificados ou perdidos.

# 15. Próximos passos recomendados

A partir deste relatório, a sequência mais segura para o TCC é:

### Etapa 1 — Validação do levantamento

Apresentar os dados consolidados à orientadora e, quando possível, confirmar com a biblioteca os pontos marcados como pendentes.

### Etapa 2 — Fechamento dos requisitos

Transformar os requisitos candidatos confirmados em uma especificação definitiva, atribuindo identificadores, prioridade e critérios de aceitação.

### Etapa 3 — Fechamento dos atores e permissões

Definir formalmente os perfis, suas permissões e todas as exceções.

### Etapa 4 — Modelagem do banco de dados

Construir o modelo conceitual e lógico a partir dos processos realmente necessários, evitando criar estruturas para funcionalidades que ficaram fora do escopo.

### Etapa 5 — Casos de uso e fluxos

Documentar os principais casos de uso e os fluxos alternativos, especialmente empréstimo, devolução, renovação, atraso e registro de ocorrências.

### Etapa 6 — Protótipo de interface

Priorizar as telas de atendimento e consulta do acervo.

### Etapa 7 — Implementação do núcleo

Implementar primeiro as funcionalidades de prioridade alta e validar cada uma contra as regras levantadas na entrevista.

### Etapa 8 — Validação com o usuário real

Demonstrar um protótipo ou versão funcional à biblioteca para verificar se o fluxo projetado corresponde ao funcionamento real.

# 16. Conclusão

A entrevista realizada na Biblioteca Popular de Campo Grande demonstra uma necessidade concreta de melhoria no gerenciamento das informações da biblioteca. O controle atual utiliza Excel e planilhas físicas, e diversos processos dependem de buscas e atualizações manuais. Isso é especialmente evidente na consulta da disponibilidade dos livros, no acompanhamento de atrasos e na localização do acervo.

Ao mesmo tempo, a entrevista mostra que a biblioteca já possui regras e procedimentos definidos. Existem limites para empréstimos, prazos estabelecidos, regras para renovação, critérios para identificação dos usuários, responsabilidades específicas da gestora e uma forma definida de organização física do acervo. Dessa forma, o objetivo do sistema não deve ser apenas digitalizar registros, mas **automatizar e estruturar procedimentos que já existem**, preservando as regras operacionais da instituição.

Os dados coletados sustentam um sistema centrado em seis núcleos principais: **acervo, usuários, circulação, localização, controle de ocorrências e gestão/indicadores**. O controle de permissões também se mostra necessário devido à diferença de responsabilidades entre gestora e colaboradores.

Os maiores ganhos esperados são a redução da dependência de consultas manuais, a melhoria da velocidade de atendimento, a identificação automática da situação dos empréstimos, a disponibilização da localização física do acervo e a geração de informações úteis para acompanhamento da biblioteca.

Por outro lado, alguns pontos ainda não devem ser tratados como decisões definitivas. Destacam-se o bloqueio automático por atraso, os níveis de acesso aos relatórios, o procedimento de migração do Excel, o detalhamento do modelo de exemplares, o tratamento de livros não devolvidos e o mecanismo de autenticação dos funcionários. Esses itens precisam ser validados antes do fechamento definitivo dos requisitos.

Assim, a entrevista cumpre duas funções no desenvolvimento do TCC: **comprovar a existência e a natureza dos problemas que motivam o sistema** e **fornecer uma base concreta para especificar o comportamento da solução**. O próximo passo recomendado é transformar o levantamento deste relatório em uma especificação formal de requisitos, mantendo o vínculo entre cada requisito, a necessidade observada e a decisão de projeto correspondente.

# Anexo A — Matriz resumida de processos e atores

A matriz abaixo consolida a divisão de responsabilidades indicada no levantamento inicial.

| Processo | Colaborador | Gestora | Situação |
|---|:---:|:---:|---|
| Realizar empréstimo | ✓ | ✓ | Confirmado pela entrevista |
| Realizar devolução | ✓ | ✓ | Compatível com os processos descritos |
| Renovar empréstimo | ✓ | ✓ | Compatível com os processos descritos |
| Cadastrar livro | ✗ | ✓ | Explicitamente informado |
| Consultar acervo | ✓ | ✓ | Compatível com o uso cotidiano |
| Relatórios | ? | ✓ | Permissão do colaborador ainda não definida |
| Cadastrar colaborador | ✗ | ✓ | Indicado no levantamento inicial |
| Configurações | ✗ | ✓ | Indicado no levantamento inicial |

## Observação metodológica

Esta matriz deve ser considerada **versão preliminar validável**. Onde a entrevista não forneceu confirmação suficiente, a coluna de situação registra a pendência em vez de transformar uma hipótese em regra definitiva.

---

**Documento elaborado a partir da transcrição da entrevista da Biblioteca Popular de Campo Grande, realizada em 20/08/2026.**
