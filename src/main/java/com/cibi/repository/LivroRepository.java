package com.cibi.repository;

import com.cibi.database.ConnectionFactory;
import com.cibi.model.Livro;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LivroRepository {

    public List<Livro> listar() {
        List<Livro> livros = new ArrayList<>();
        String sql = "SELECT id, titulo, autor, sinopse FROM livros";

        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Livro livro = new Livro();
                livro.setId(rs.getInt("id"));
                livro.setTitulo(rs.getString("titulo"));
                livro.setAutor(rs.getString("autor"));
                livro.setSinopse(rs.getString("sinopse"));
                livros.add(livro);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar livros", e);
        }

        return livros;
    }

    public void inserir(Livro livro) {
        String sql = "INSERT INTO livros (titulo, autor, sinopse) VALUES (?, ?, ?)";

        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, livro.getTitulo());
            stmt.setString(2, livro.getAutor());
            stmt.setString(3, livro.getSinopse());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir livro", e);
        }
    }

    public Livro buscarPorId(int id) {
        String sql = "SELECT id, titulo, autor, sinopse FROM livros WHERE id = ?";

        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Livro livro = new Livro();
                    livro.setId(rs.getInt("id"));
                    livro.setTitulo(rs.getString("titulo"));
                    livro.setAutor(rs.getString("autor"));
                    livro.setSinopse(rs.getString("sinopse"));
                    return livro;
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar livro", e);
        }

        return null;
    }

    public void excluir(int id) {
        String sql = "DELETE FROM livros WHERE id = ?";

        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao excluir livro", e);
        }
    }
}
