package com.cibi.repository;

import com.cibi.database.ConnectionFactory;
import com.cibi.model.Conta;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ContaRepository {
    
    //cria uma conta e a insere no banco
    public void inserir(Conta conta) {
        String sql = "INSERT INTO usuarios (usuario, senha, perfil) VALUES (?, ?, ?)";

        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, conta.getUsuario());
            stmt.setString(2, conta.getSenha());
            stmt.setString(3, conta.getPerfil());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir conta", e);
        }
    }
    
    // procura usuario a partir de usuario e senha
    public static Conta buscarPorUsuarioESenha(String usuario, String senha) {
        String sql = "SELECT id, usuario, perfil FROM usuarios WHERE usuario = ? AND senha = ?";

        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, usuario);
            stmt.setString(2, senha);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Conta conta = new Conta();
                    conta.setId(rs.getInt("id"));
                    conta.setUsuario(rs.getString("usuario"));
                    conta.setPerfil(rs.getString("perfil"));
                    return conta;
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao autenticar usuário", e);
        }

        return null;
    }
    
    // criar ArreyList com todos os dados de usuario do banco
    public List<Conta> listar() {
        List<Conta> lista = new ArrayList<>();
        String sql = "SELECT id, usuario, perfil FROM usuarios";

        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Conta conta = new Conta();
                conta.setId(rs.getInt("id"));
                conta.setUsuario(rs.getString("usuario"));
                conta.setPerfil(rs.getString("perfil"));
                lista.add(conta);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar contas", e);
        }

        return lista;
    }
    
    // excluir usuario
    public void excluir(int id) {
        String sql = "DELETE FROM usuarios WHERE id = ?";

        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao excluir conta", e);
        }
    }
}
