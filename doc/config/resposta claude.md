Vou montar os cinco arquivos seguindo o layout da referência (header + abas + seções com divisórias) mas com a sua paleta atual: sidebar escura, área de conteúdo clara e azul como cor de destaque.

Montei a tela seguindo a estrutura da referência (título → abas → seções separadas por divisórias → rodapé de ações), mas com a paleta que você já usa: fundo 
#F4F6FA, superfície branca, texto 
#1B2432 e azul 
#2563EB como destaque, além do 
#141C2B da sidebar reaproveitado nas miniaturas de tema.

O que foi implementado

Quatro abas dentro da mesma página: Aparência (tema com os 3 cartões de preview, 5 bolinhas de cor de destaque, switch e os 4 cartões "Aa"), Empréstimos (prazo, limite de livros, renovações, multa por dia, bloqueio por atraso), Notificações (alertas + antecedência do aviso) e Sistema (nome da biblioteca, itens por página, pasta de backup com DirectoryChooser, restaurar padrões).

O rodapé fixo mostra o estado do formulário e só habilita "Salvar alterações" quando algo muda. As bolinhas de cor mudam a aparência ao vivo: -cibi-destaque é uma looked-up color declarada em .config-root, então o controller só redefine ela via setStyle no nó raiz e tudo que usa a cor acompanha.

Pontos de atenção antes de compilar

ConfiguracoesRepository importa com.cibi.util.ConexaoBD — troque pelo nome real da sua classe de conexão.
Crie a tabela (o SQL está no Javadoc do repository): é um modelo chave/valor, então adicionar uma opção nova na tela não exige mexer no schema.
No seu MainController, ao carregar o FXML, adicione a folha de estilo:
java
   Parent page = FXMLLoader.load(getClass().getResource("/com/cibi/fxml/pages/Configuracoes.fxml"));
   page.getStylesheets().add(getClass().getResource("/com/cibi/css/pages/Configuracoes.css").toExternalForm());
O switch é um CheckBox com a classe switch — ele só fica com cara de switch com o CSS carregado; no SceneBuilder vai aparecer como checkbox comum.

Uma coisa que ficou pendente de propósito: salvar o tema/cor persiste o valor, mas aplicar de fato nas outras telas depende de um ponto central (trocar o stylesheet da Scene). Se quiser, eu escrevo um TemaManager que lê o service na inicialização e aplica em toda a aplicação.