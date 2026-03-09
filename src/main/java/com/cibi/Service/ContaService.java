package com.cibi.Service;

import com.cibi.model.Conta;
import com.cibi.repository.ContaRepository;

public class ContaService {

    public static boolean RealizarLogin(String usuario, String senha) {
        if (usuario == null) {
            throw new IllegalArgumentException("Usuário obrigatório");
        }

        if (senha == null) {
            throw new IllegalArgumentException("senha obrigatória");
        }

        Conta conta = new Conta();
        ContaRepository dao = new ContaRepository();

        conta = dao.buscarPorUsuarioESenha(usuario, senha);

        if (conta == null) {
            throw new IllegalStateException("Usuário ou senha inválidos");
        }

        return true;
    }

}
