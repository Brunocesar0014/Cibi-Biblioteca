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