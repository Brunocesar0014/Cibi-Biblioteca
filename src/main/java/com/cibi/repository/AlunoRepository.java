package com.cibi.repository;

import com.cibi.database.ConnectionFactory;
import com.cibi.model.Aluno;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlunoRepository {
    public record CadastroCida(long matricula, String nome, String turma, byte[] foto) {}
    
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

    public static byte[] buscarFotoCida(long matricula) {
        return buscarFotosCida(List.of(matricula)).get(matricula);
    }

    public static Map<Long, byte[]> buscarFotosCida(List<Long> matriculas) {
        if (matriculas.isEmpty()) return Map.of();
        String parametros = String.join(", ", Collections.nCopies(matriculas.size(), "?"));
        String sql = "SELECT matricula, foto_cida FROM alunos WHERE foto_cida IS NOT NULL AND matricula IN (" + parametros + ")";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            for (int i = 0; i < matriculas.size(); i++) stmt.setLong(i + 1, matriculas.get(i));
            try (ResultSet rs = stmt.executeQuery()) {
                Map<Long, byte[]> fotos = new HashMap<>();
                while (rs.next()) fotos.put(rs.getLong("matricula"), rs.getBytes("foto_cida"));
                return fotos;
            }
        } catch (SQLException erro) {
            if ("42S22".equals(erro.getSQLState())) return Map.of();
            throw new IllegalStateException("Não foi possível consultar as fotos dos alunos.", erro);
        }
    }

    public static void importarCida(List<CadastroCida> registros) {
        String sql = """
                INSERT INTO alunos (matricula, nome, turma, foto_cida) VALUES (?, ?, ?, ?)
                ON DUPLICATE KEY UPDATE nome = ?, turma = ?, foto_cida = COALESCE(?, foto_cida)
                """;
        var ordenados = registros.stream().sorted(Comparator.comparingLong(CadastroCida::matricula)).toList();
        try (Connection con = ConnectionFactory.getConnection()) {
            con.setAutoCommit(false);
            try {
                try (PreparedStatement stmt = con.prepareStatement(sql)) {
                    for (CadastroCida aluno : ordenados) {
                        stmt.setLong(1, aluno.matricula());
                        stmt.setString(2, aluno.nome());
                        stmt.setString(3, aluno.turma());
                        stmt.setBytes(4, aluno.foto());
                        stmt.setString(5, aluno.nome());
                        stmt.setString(6, aluno.turma());
                        stmt.setBytes(7, aluno.foto());
                        stmt.executeUpdate();
                    }
                }
                con.commit();
            } catch (SQLException | RuntimeException erro) {
                try {
                    con.rollback();
                } catch (SQLException falhaRollback) {
                    erro.addSuppressed(falhaRollback);
                }
                throw erro;
            }
        } catch (SQLException erro) {
            if ("42S22".equals(erro.getSQLState())) {
                throw new IllegalStateException("Aplique BD/migracao_importacao_cida.sql no banco antes de importar.", erro);
            }
            throw new IllegalStateException("Não foi possível concluir a importação no banco de dados.", erro);
        }
    }
}
