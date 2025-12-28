package com.cibi.model.dao;

//classes Java
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import com.cibi.model.bean.Conta;
import java.util.ArrayList;

//classes próprias
import com.cibi.connection.ConexaoMySQL;
import java.util.List;

// JOptionPane ("para depuração")
import javax.swing.JOptionPane;

public class ContaDAO {

    JOptionPane jop = new JOptionPane();

    // insere contas no BD
    public void inserir(Conta conta) {
        String sql = "INSERT INTO usuarios (usuario, senha, perfil) VALUES (?, ?, ?)";

        try (Connection conexao = ConexaoMySQL.getConnection(); PreparedStatement stmt = conexao.prepareStatement(sql)) {

            // Substitui os ? pelos valores do objeto
            stmt.setString(1, conta.getUsuario());
            stmt.setString(2, conta.getSenha());
            stmt.setString(3, conta.getPerfil());

            stmt.executeUpdate(); // Executa o INSERT

            jop.showMessageDialog(null, "usuario inserido com sucesso!");

        } catch (SQLException e) {
            jop.showMessageDialog(null, "Erro ao inserir usuario: " + e.getMessage());
        }
    }

    // procura conta por usuario e senha
    public Conta buscarPorLogin(String usuario, String senha) {
        String sql = "SELECT * FROM usuarios WHERE usuario = ? AND senha = ?";

        try (Connection conexao = ConexaoMySQL.getConnection(); PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, usuario);
            stmt.setString(2, senha);

            ResultSet rs = stmt.executeQuery();

            // Se encontrou o usuário:
            if (rs.next()) {
                Conta conta = new Conta();
                conta.setId(rs.getInt("id"));
                conta.setPerfil(rs.getString("perfil"));
                conta.setUsuario(rs.getString("usuario"));
                conta.setSenha(rs.getString("senha"));
                return conta; // Retorna o objeto encontrado
            }

        } catch (SQLException e) {
            jop.showMessageDialog(null, "Erro ao buscar usuário: " + e.getMessage());
        }

        return null; // Retorna null se não encontrar
    }

    // procura conta por usuario
    public Conta BuscarPorUsuario(String usuario) {
        String sql = "SELECT * FROM usuarios WHERE usuario = ?";

        try (Connection conexao = ConexaoMySQL.getConnection(); PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, usuario);

            ResultSet rs = stmt.executeQuery();

            // Se encontrou o usuário:
            if (rs.next()) {
                Conta conta = new Conta();
                conta.setId(rs.getInt("id"));
                conta.setPerfil(rs.getString("perfil"));
                conta.setUsuario(rs.getString("usuario"));
                conta.setSenha(rs.getString("senha"));
                return conta; // Retorna o objeto encontrado
            }

        } catch (SQLException e) {
            jop.showMessageDialog(null, "Erro ao buscar usuário: " + e.getMessage());
        }

        return null; // Retorna null se não encontrar
    }

    // lista contas em um ArreyList
    public List<Conta> listarContas() {
        List<Conta> lista = new ArrayList<>(); // Cria a lista de contas

        String sql = "SELECT id, usuario, perfil FROM usuarios"; // Ajuste conforme seu banco

        // try-with-resources fecha automaticamente os recursos ao final
        try (Connection con = ConexaoMySQL.getConnection(); PreparedStatement stmt = con.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) { // Percorre os resultados
                Conta conta = new Conta();
                conta.setId(rs.getInt("id"));
                conta.setUsuario(rs.getString("usuario"));
                conta.setPerfil(rs.getString("perfil"));

                lista.add(conta); // Adiciona à lista
            }

        } catch (SQLException e) {
            e.printStackTrace();
            // Aqui você pode lançar uma exceção personalizada ou registrar em log
        }

        return lista; // Retorna a lista com todos os livros
    }

    // exclui livros do BD
    public void excluir(Conta conta) {
        String sql = "DELETE FROM usuarios WHERE id = ?";

        try (Connection conexao = ConexaoMySQL.getConnection(); PreparedStatement stmt = conexao.prepareStatement(sql)) {

            // Substitui os ? pelos valores do objeto
            stmt.setInt(1, conta.getId());

            stmt.executeUpdate(); // Executa o INSERT

            System.out.println("Conta excluido com sucesso!");

        } catch (SQLException e) {
            jop.showMessageDialog(null, "Erro ao excluir Conta: " + e.getMessage());
        }
    }
}
