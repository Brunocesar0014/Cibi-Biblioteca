package com.cibi.repository;

import com.cibi.database.ConnectionFactory;
import com.cibi.model.DashboardResumo;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DashboardRepository {

    public DashboardResumo buscarResumo() {
        try (Connection connection = ConnectionFactory.getConnection()) {
            return new DashboardResumo(
                    contar(connection, "SELECT COUNT(*) FROM livros"),
                    contar(connection, "SELECT COUNT(*) FROM colaboradores")
            );
        } catch (SQLException e) {
            throw new IllegalStateException("Não foi possível carregar o resumo do painel", e);
        }
    }

    private long contar(Connection connection, String sql) throws SQLException {
        try (Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery(sql)) {
            result.next();
            return result.getLong(1);
        }
    }
}
