package com.cibi.model.dao;

//classes Java
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.List;
import java.util.ArrayList;

//classes proprias
import com.cibi.connection.ConexaoMySQL;
import com.cibi.model.bean.Livro;

// JOptionPane ("para depuração")
import javax.swing.JOptionPane;

public class LivroDAO {

    JOptionPane jop = new JOptionPane();

    // lista livros em um ArreyList
    public List<Livro> listarLivros() {
        List<Livro> lista = new ArrayList<>(); // Cria a lista de livros

        String sql = "SELECT id, titulo, autor, sinopse FROM livros"; // Ajuste conforme seu banco

        // try-with-resources fecha automaticamente os recursos ao final
        try (Connection con = ConexaoMySQL.getConnection(); PreparedStatement stmt = con.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) { // Percorre os resultados
                Livro livro = new Livro();
                livro.setId(rs.getInt("id"));
                livro.setTitulo(rs.getString("titulo"));
                livro.setAutor(rs.getString("autor"));
                livro.setSinopse(rs.getString("sinopse"));

                lista.add(livro); // Adiciona à lista
            }

        } catch (SQLException e) {
            e.printStackTrace();
            // Aqui você pode lançar uma exceção personalizada ou registrar em log
        }

        return lista; // Retorna a lista com todos os livros
    }

    // insere livros no BD
    public void inserir(Livro livro) {
        String sql = "INSERT INTO livros (titulo, autor, sinopse) VALUES (?, ?, ?)";

        try (Connection conexao = ConexaoMySQL.getConnection(); PreparedStatement stmt = conexao.prepareStatement(sql)) {

            // Substitui os ? pelos valores do objeto
            stmt.setString(1, livro.getTitulo());
            stmt.setString(2, livro.getAutor());
            stmt.setString(3, livro.getSinopse());

            stmt.executeUpdate(); // Executa o INSERT

            jop.showMessageDialog(null, "Livro inserido com sucesso!");

        } catch (SQLException e) {
            jop.showMessageDialog(null, "Erro ao inserir Livro: " + e.getMessage());
        }
    }

    // exclui livros do BD
    public void excluir(Livro livro) {
        String sql = "DELETE FROM livros WHERE id = ?";

        try (Connection conexao = ConexaoMySQL.getConnection(); PreparedStatement stmt = conexao.prepareStatement(sql)) {

            // Substitui os ? pelos valores do objeto
            stmt.setInt(1, livro.getId());

            stmt.executeUpdate(); // Executa o INSERT

            jop.showMessageDialog(null, "Livro excluido com sucesso!");

        } catch (SQLException e) {
            jop.showMessageDialog(null, "Erro ao excluir Livro: " + e.getMessage());
        }
    }

    // busca livros por id
    public Livro BuscarPorId(String id) {
        String sql = "SELECT * FROM livros WHERE id = ?";

        try (Connection conexao = ConexaoMySQL.getConnection(); PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, id);

            ResultSet rs = stmt.executeQuery();

            // Se encontrou o usuário:
            if (rs.next()) {
                Livro livro = new Livro();
                livro.setId(rs.getInt("id"));
                livro.setTitulo(rs.getString("titulo"));
                livro.setAutor(rs.getString("autor"));
                livro.setSinopse(rs.getString("sinopse"));
            }

        } catch (SQLException e) {
            jop.showMessageDialog(null, "Erro ao buscar livro: " + e.getMessage());
        }

        return null; // Retorna null se não encontrar
    }

}
