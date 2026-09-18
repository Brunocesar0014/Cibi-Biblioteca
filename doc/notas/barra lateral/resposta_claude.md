Principais mudanças na sidebar, mantendo as mesmas 5 seções:

Ícones em cada item do menu (glifos Unicode simples, sem dependência de biblioteca), aplicados via <graphic> no Button + classe .menu-icon no CSS.
Divisor entre a navegação e o bloco de perfil, como na referência.
Perfil do rodapé agora usa as classes que já existiam no CSS mas não estavam aplicadas no FXML (avatar, avatar-text, profile-name, profile-email, logout-icon) — o avatar vira um círculo colorido de verdade.
Ajustes finos de espaçamento/raio de borda (itens com 40px de altura e background-radius: 8) para aproximar do visual do Flux.
MainController não precisou de mudanças de lógica — só recebeu um comentário apontando pro novo guia.

O MainController.java foi incluído inalterado só para o pacote de arquivos ficar completo; se preferir, pode ignorá-lo e manter o seu.