package com.cibi.repository;

import com.cibi.database.ConnectionFactory;
import com.cibi.model.Aluno;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AlunoRepository {
    
    public static Aluno buscarPorMatricula(long matricula) {
        String sql = "SELECT matricula, nome, turma FROM alunos WHERE matricula = ?";

        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setLong(1, matricula);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Aluno aluno = new Aluno();
                    aluno.setMatricula(rs.getLong("matricula"));
                    aluno.setNome(rs.getString("nome"));
                    aluno.setTurma(rs.getString("turma"));
                    return aluno;
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar aluno", e);
        }

        return null;
    }
    
}
