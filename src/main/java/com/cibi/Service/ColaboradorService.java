package com.cibi.Service;

import com.cibi.model.Colaborador;
import com.cibi.repository.ColaboradorRepository;

public class ColaboradorService {

    public static boolean RealizarLogin(String usuario, String senha) {
        if (usuario == null || usuario.isBlank()) {
            throw new IllegalArgumentException("Usuário obrigatório");
        }

        if (senha == null || senha.isBlank()) {
            throw new IllegalArgumentException("Senha obrigatória");
        }

        Colaborador colaborador = ColaboradorRepository.buscarPorUsuarioESenha(usuario, senha);

        if (colaborador == null) {
            throw new IllegalStateException("Usuário ou senha inválidos");
        }

        return true;
    }

    public static void inserirColabrador(Colaborador colaborador) {
        if (colaborador == null) {
            throw new IllegalArgumentException("colaborador obrigatório");
        }
        if (colaborador.getNome() == null || colaborador.getNome().isBlank()) {
            throw new IllegalArgumentException("nome inválido");
        }
        if (    colaborador.getTelefone() == null
                || colaborador.getTelefone().isBlank()
                || colaborador.getTelefone().length() < 10
                || colaborador.getTelefone().length() > 11)
        {
            throw new IllegalArgumentException("telefone inválido");
        }
        if (colaborador.getMatricula() <= 0) {
            throw new IllegalArgumentException("matricula inválida");
        }
        if (colaborador.getTurma() == null || colaborador.getTurma().isBlank()) {
            throw new IllegalArgumentException("turma inválida");
        }
        if (colaborador.getSenha() == null || colaborador.getSenha().isBlank()) {
            throw new IllegalArgumentException("senha inválida");
        }

        try {
            ColaboradorRepository.inserir(colaborador);
        } catch (RuntimeException e) {
            throw new RuntimeException("Erro ao inserir colaborador", e);
        }
    }
}
