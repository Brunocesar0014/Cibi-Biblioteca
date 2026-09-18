package com.cibi.repository;

import com.cibi.database.ConnectionFactory;
import com.cibi.model.Colaborador;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ColaboradorRepository {
    
    //cria uma conta e a insere no banco
    public static void inserir(Colaborador colaborador) {
        String sql = "INSERT INTO colaboradores (matricula, nome, senha, telefone, turma) VALUES (?, ?, ?, ?, ?)";

        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setLong(1, colaborador.getMatricula());
            stmt.setString(2, colaborador.getNome());
            stmt.setString(3, colaborador.getSenha());
            stmt.setString(4, colaborador.getTelefone());
            stmt.setString(5, colaborador.getTurma());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir colaborador", e);
        }
    }
    
    // procura usuario a partir de usuario e senha
    public static Colaborador buscarPorUsuarioESenha(String nome, String senha) {
        String sql = "SELECT matricula, nome, telefone, turma FROM colaboradores WHERE nome = ? AND senha = ?";

        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, nome);
            stmt.setString(2, senha);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Colaborador colaborador = new Colaborador();
                    colaborador.setMatricula(rs.getLong("matricula"));
                    colaborador.setNome(rs.getString("nome"));
                    colaborador.setTelefone(rs.getString("telefone"));
                    colaborador.setTurma(rs.getString("turma"));
                    return colaborador;
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao autenticar usuário", e);
        }

        return null;
    }
    
    // criar ArreyList com todos os dados de usuario do banco
    public List<Colaborador> listar() {
        List<Colaborador> lista = new ArrayList<>();
        String sql = "SELECT matricula, nome, telefone, turma FROM colaboradores";

        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Colaborador colaborador = new Colaborador();
                colaborador.setMatricula(rs.getLong("matricula"));
                colaborador.setNome(rs.getString("nome"));
                colaborador.setTelefone(rs.getString("telefone"));
                colaborador.setTurma(rs.getString("turma"));
                lista.add(colaborador);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar colaboradores", e);
        }

        return lista;
    }
    
    // excluir usuario
    public void excluir(long matricula) {
        String sql = "DELETE FROM colaboradores WHERE matricula = ?";

        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setLong(1, matricula);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao excluir colaborador", e);
        }
    }
}
