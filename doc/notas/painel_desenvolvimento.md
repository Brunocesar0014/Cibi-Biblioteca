# CIBI — Painel de Desenvolvimento

## Objetivo

Criar uma **segunda janela exclusiva para desenvolvimento e testes**, aberta automaticamente junto com o CIBI quando o **modo desenvolvedor** estiver ativado.

A janela funcionará como um **Development Console**, permitindo configurar o ambiente antes da inicialização e executar ações durante a execução do sistema.

---

## Estrutura da Development Console

### 1. Configurações iniciais

Definir opções **antes de iniciar o CIBI**, como:

* [ ] Ignorar login
* [ ] Selecionar usuário inicial
* [ ] Usar banco de testes
* [ ] Carregar dados fictícios
* [ ] Ativar recursos de debug

Essas configurações serão aplicadas quando o botão **"Iniciar CIBI"** for acionado.

### 2. Comandos durante a execução

Permitir executar ações enquanto o CIBI estiver aberto:

* Simular leitura de matrícula/QR Code
* Criar dados fictícios
* Criar aluno de teste
* Criar livro de teste
* Criar empréstimo de teste
* Recarregar telas
* Abrir determinadas telas
* Limpar dados de teste

Novos comandos poderão ser adicionados conforme surgirem necessidades durante o desenvolvimento.

### 3. Log de desenvolvimento

Exibir informações úteis para acompanhar o funcionamento da aplicação:

```text
Aplicação iniciada
Banco conectado
Usuário de teste selecionado
Matrícula simulada
Aluno encontrado
Livro encontrado
```

A ideia é futuramente utilizar esse mecanismo também para substituir parte dos `System.out.println()`.

---

## Arquitetura planejada

Adicionar um componente central:

```text
util/
├── NavigationManager.java
└── DevTools.java
```

E uma nova tela:

```text
controller/
├── DevConsoleController.java
└── ...

resources/com/cibi/fxml/
├── Tela_Login.fxml
├── Tela_Principal.fxml
└── DevConsole.fxml
```

O `DevConsoleController` não deverá acessar diretamente os repositories.

Preferir:

```text
DevConsoleController
        ↓
     DevTools
        ↓
     Service
        ↓
   Repository
        ↓
     MySQL
```

Dessa forma, a ferramenta de desenvolvimento não quebra a arquitetura principal do CIBI.

---

## Funcionamento das janelas

### Modo normal

```text
MainApp
   ↓
Login
   ↓
CIBI
```

### Modo desenvolvedor

```text
             MainApp
                ↓
       ┌────────┴────────┐
       ↓                 ↓
DevConsole          CIBI principal
       │
       ├── Configurações
       ├── Comandos
       └── Logs
```

A Development Console deverá possuir seu próprio `Stage` e permanecer aberta durante a execução do programa.

---

## Ativação

O modo desenvolvedor deve ser **desativado por padrão** na versão normal/apresentação.

Durante o desenvolvimento, uma configuração simples poderá ativá-lo, por exemplo:

```java
private static final boolean MODO_DESENVOLVEDOR = true;
```

Posteriormente, essa ativação pode continuar sendo controlada pela configuração já existente:

```text
-Dcibi.dev.skipLogin=true
```

ou por uma configuração equivalente.

---

## Primeira versão

Não é necessário implementar tudo imediatamente.

A primeira versão da Development Console deverá conter apenas:

1. **Ignorar login**
2. **Simular matrícula**
3. **Dados fictícios**
4. **Recarregar tela**
5. **Log de desenvolvimento**

Depois, novas ferramentas poderão ser adicionadas conforme a necessidade.

---

## Ideia principal

A Development Console será uma **ferramenta interna do CIBI para acelerar testes**, permitindo alterar configurações e executar cenários de teste sem precisar modificar o código ou manipular manualmente o banco de dados a cada teste.

Ela não fará parte da experiência normal do usuário e deverá ficar disponível somente no **modo de desenvolvimento**.
