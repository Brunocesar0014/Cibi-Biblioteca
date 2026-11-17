package com.cibi.repository;

import com.cibi.database.ConnectionFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public final class FuncionarioRepository {

    private static final int TABELA_INEXISTENTE = 1146;
    private static final int CHAVE_DUPLICADA = 1062;

    private static final String CRIAR_TABELA = """
            CREATE TABLE IF NOT EXISTS contas_funcionarios (
                id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
                nome VARCHAR(150) NOT NULL,
                email VARCHAR(254) NOT NULL,
                telefone VARCHAR(11),
                usuario VARCHAR(50) NOT NULL UNIQUE,
                senha_hash VARCHAR(80) NOT NULL,
                administrador BOOLEAN NOT NULL DEFAULT FALSE
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
            """;

    private FuncionarioRepository() {
    }

    public static boolean existemContasDeAcesso() {
        try (Connection conexao = ConnectionFactory.getConnection()) {
            try {
                if (existeFuncionario(conexao)) {
                    return true;
                }
            } catch (SQLException e) {
                if (e.getErrorCode() != TABELA_INEXISTENTE) {
                    throw e;
                }
            }
            try (Statement consulta = conexao.createStatement();
                 var resultado = consulta.executeQuery("SELECT 1 FROM colaboradores LIMIT 1")) {
                return resultado.next();
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == TABELA_INEXISTENTE) {
                return false;
            }
            throw new IllegalStateException("Erro ao verificar as contas de acesso", e);
        }
    }

    public static boolean inserirPrimeiro(String nome, String email, String telefone,
            String usuario, String senhaHash) {
        String sql = """
                INSERT INTO contas_funcionarios (id, nome, email, telefone, usuario, senha_hash, administrador)
                VALUES (1, ?, ?, ?, ?, ?, TRUE)
                """;
        try (Connection conexao = ConnectionFactory.getConnection()) {
            try {
                if (existeFuncionario(conexao)) {
                    return false;
                }
            } catch (SQLException e) {
                if (e.getErrorCode() != TABELA_INEXISTENTE) {
                    throw e;
                }
                try (Statement migracao = conexao.createStatement()) {
                    migracao.executeUpdate(CRIAR_TABELA);
                }
            }

            try (PreparedStatement cadastro = conexao.prepareStatement(sql)) {
                cadastro.setString(1, nome);
                cadastro.setString(2, email);
                cadastro.setString(3, telefone);
                cadastro.setString(4, usuario);
                cadastro.setString(5, senhaHash);
                return cadastro.executeUpdate() == 1;
            }
        } catch (SQLException e) {
            // O ID reservado impede dois cadastros iniciais simultâneos.
            if (e.getErrorCode() == CHAVE_DUPLICADA) {
                return false;
            }
            throw new IllegalStateException("Erro ao cadastrar o primeiro funcionário", e);
        }
    }

    public static String buscarSenhaHash(String usuario) {
        String sql = "SELECT senha_hash FROM contas_funcionarios WHERE usuario = ?";
        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement consulta = conexao.prepareStatement(sql)) {
            consulta.setString(1, usuario);
            try (var resultado = consulta.executeQuery()) {
                return resultado.next() ? resultado.getString("senha_hash") : null;
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == TABELA_INEXISTENTE) {
                return null;
            }
            throw new IllegalStateException("Erro ao consultar a conta do funcionário", e);
        }
    }

    private static boolean existeFuncionario(Connection conexao) throws SQLException {
        try (Statement consulta = conexao.createStatement();
             var resultado = consulta.executeQuery("SELECT 1 FROM contas_funcionarios LIMIT 1")) {
            return resultado.next();
        }
    }
}
