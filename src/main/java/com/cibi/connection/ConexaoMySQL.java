package com.cibi.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoMySQL {
    
    private static final String URL = "jdbc:mysql://localhost:3306/tcc";
    private static final String USUARIO = "root";
    private static final String SENHA = "bc487180";

    // Método utilizado pelos DAOs
    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USUARIO, SENHA);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao conectar ao banco de dados", e);
        }
    }

    // Teste opcional
    public static void main(String[] args) {
        try (Connection conexao = getConnection()) {
            System.out.println("Conexao bem-sucedida!");
        } catch (Exception e) {
            System.out.println("Erro ao testar conexão: " + e.getMessage());
        }
    }


}
