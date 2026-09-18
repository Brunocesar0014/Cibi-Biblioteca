package com.cibi.Repository;

import com.cibi.util.ConexaoBD; // ajuste para o nome real da sua classe de conexão

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Acesso a dados das configurações do CIBI.
 *
 * As configurações são gravadas em um modelo chave/valor, o que evita ter que
 * alterar o schema toda vez que uma nova opção for adicionada à tela.
 *
 * <pre>
 * CREATE TABLE IF NOT EXISTS configuracoes (
 *     chave        VARCHAR(80)  NOT NULL,
 *     valor        VARCHAR(255) NOT NULL,
 *     atualizado_em TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP
 *                                  ON UPDATE CURRENT_TIMESTAMP,
 *     PRIMARY KEY (chave)
 * ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;
 * </pre>
 */
public class ConfiguracoesRepository {

    private static final String SQL_SELECT_TODAS =
            "SELECT chave, valor FROM configuracoes";

    private static final String SQL_UPSERT =
            "INSERT INTO configuracoes (chave, valor) VALUES (?, ?) " +
            "ON DUPLICATE KEY UPDATE valor = VALUES(valor)";

    private static final String SQL_DELETE_TODAS =
            "DELETE FROM configuracoes";

    /**
     * Carrega todas as configurações persistidas.
     *
     * @return mapa chave/valor (vazio quando a tabela ainda não tem registros)
     */
    public Map<String, String> buscarTodas() throws SQLException {
        Map<String, String> valores = new LinkedHashMap<>();

        try (Connection conexao = ConexaoBD.getConnection();
             Statement statement = conexao.createStatement();
             ResultSet rs = statement.executeQuery(SQL_SELECT_TODAS)) {

            while (rs.next()) {
                valores.put(rs.getString("chave"), rs.getString("valor"));
            }
        }
        return valores;
    }

    /**
     * Grava o lote de configurações em uma única transação: ou tudo é salvo,
     * ou nada é alterado.
     */
    public void salvarTodas(Map<String, String> valores) throws SQLException {
        if (valores == null || valores.isEmpty()) {
            return;
        }

        Connection conexao = null;
        try {
            conexao = ConexaoBD.getConnection();
            conexao.setAutoCommit(false);

            try (PreparedStatement ps = conexao.prepareStatement(SQL_UPSERT)) {
                for (Map.Entry<String, String> entrada : valores.entrySet()) {
                    ps.setString(1, entrada.getKey());
                    ps.setString(2, entrada.getValue());
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            conexao.commit();

        } catch (SQLException e) {
            if (conexao != null) {
                try {
                    conexao.rollback();
                } catch (SQLException ignorada) {
                    // mantém a exceção original
                }
            }
            throw e;

        } finally {
            if (conexao != null) {
                try {
                    conexao.setAutoCommit(true);
                    conexao.close();
                } catch (SQLException ignorada) {
                    // nada a fazer no fechamento
                }
            }
        }
    }

    /**
     * Apaga todos os registros — usado pelo "restaurar padrões", que em seguida
     * regrava os valores originais.
     */
    public void limpar() throws SQLException {
        try (Connection conexao = ConexaoBD.getConnection();
             Statement statement = conexao.createStatement()) {
            statement.executeUpdate(SQL_DELETE_TODAS);
        }
    }
}
