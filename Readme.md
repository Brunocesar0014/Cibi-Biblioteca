# área para devs (remover dps)

## tudo que é necessário para rodar o projeto:

 - maven mais recente instalado
 - JDK 22.0.2 **(não serve java SE)**
 - pelo cmd navegar até a pasta do projeto e execotar o comando: "mvn javafx:run"

## tudo que é necessário para editar o projeto:

 - maven mais recente instalado
 - JDK 22.0.2 **(não serve java SE)**
 - ide "recente" (netbeans 22, IntelliJ, Eclipse, etc...)
(**Não recomendo o netbeans 8.2**)
 - Git

## Como Exportar/Importar o banco

### **Importar**

 - 1. Dentro do MySQL Workbench abra sua lacal instance;
 - 2. Na aba server vá em data import;  
 - 3. Crie um banco vazio com o exato nome "cibi";
 - 4. Na primeira opção "Import from dump project folder", selecione o diretorio da pasta "dump yyyy/mm/dd";
 - 5. Clique em start import;
 - 6. Depois, no projeto Java, na classe Connection.java em com.cibi.Connection altere a url (se necessário), o usuario e a senha para se adequarem aos da instalação do seu MySQL.

### **Exportar**

 - 1. Dentro do MySQL Workbench abra sua lacal instance;
 - 2. Na aba server vá em data exmport;
 - 3. Selecione o banco "cibi";
 - 4. Clique em start export;
 - 5. Vá em documentos/dumps;
 - 6. Copie a pasta com o formato "dump(yyyy/mm/dd)";
 - 7. Subistitua pela pasta antiga;
 - 8. Atualize o nome da pasta "BD atual yyyy/mm/dd" para o dia da alteração.

### Instalação nova e primeiro acesso

1. Em uma instância MySQL 8, execute [o esquema inicial](BD/cibi_mysql.sql) para criar um banco novo. Para bancos existentes, preserve os dados e use as instruções de migração no final do script.
2. Configure a conexão pelas variáveis `CIBI_DB_URL`, `CIBI_DB_USER` e `CIBI_DB_PASSWORD` e execute `mvn javafx:run`.
3. Se o banco estiver sem contas de funcionário e sem colaboradores cadastrados, o CIBI abre o cadastro do primeiro administrador. Em instalações com contas, o CIBI abre o login.

Para abrir o console de desenvolvimento, defina a variável de ambiente `CIBI_DEV=true` antes de iniciar o CIBI.

### Importar alunos do CIDA

1. Em um banco antigo cuja tabela `alunos` ainda não tenha a coluna `foto_cida`, execute uma vez o script [de migração](BD/migracao_importacao_cida.sql). O esquema de instalação nova já inclui a coluna.
2. Em **Usuários → Importar do CIDA**, selecione uma planilha `.xlsm` ou `.xlsx` com a aba **Controle** ou **Refeitório** e os cabeçalhos **Matrícula**, **Aluno** e **Turma** nas três primeiras colunas.
3. Use a pasta **Fotos Excel** ao lado da planilha ou selecione outra pasta com os JPEGs no formato `Nome_Matrícula_Turma.jpg`. A pasta é opcional.
4. Clique em **Validar arquivos**, confira a prévia e clique em **Importar alunos**. Corrija as linhas sinalizadas e valide novamente se houver erros.

A importação atualiza nome e turma pela matrícula. Fotos existentes são preservadas quando o lote não traz uma imagem correspondente. Os alunos ausentes da planilha permanecem cadastrados. As fotos importadas ficam no banco como miniaturas e aparecem no resumo de **Novo empréstimo** após identificar o aluno.

---


# CIBI (Controle Interno de Biblioteca)

![Status](https://img.shields.io/badge/STATUS-EM%20DESENVOLVIMENTO-blue?style=for-the-badge)

---

## table of contents

 - [1. Proposito](#1-proposito)
 - [2. Tecnologias Utilizadas](#2-Tecnologias-Utilizadas)
 - [3. Tecnologias Utilizadas](#3-Funcionalidades)

---

## 1. Proposito

teste

---

## 2. Tecnologias Utilizadas

teste

---

## 3. Funcionalidaes

teste
