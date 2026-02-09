package com.cibi.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class ConnectionFactory {
    
    private ConnectionFactory() {
    }

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(
                    DatabaseConfig.URL,
                    DatabaseConfig.USER,
                    DatabaseConfig.PASSWORD
            );
        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Não foi possível conectar ao banco de dados", e
            );
        }
    }
}
