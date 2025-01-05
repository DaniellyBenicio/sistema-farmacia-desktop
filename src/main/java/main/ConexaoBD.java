package main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoBD {

    private static final String URL = "jdbc:mysql://localhost:3306/farmacia"; 
    private static final String USUARIO = "root"; 
    private static final String SENHA = "aluno123"; 
    
    public static Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(URL, USUARIO, SENHA);
        } catch (SQLException e) {
            throw new SQLException("Erro ao conectar ao banco de dados.", e);
        }
    }

    public static void testConexaoComMySQL() {
        try (Connection connection = getConnection()) {
            if (connection != null) {
                System.out.println("Conexão estabelecida com sucesso!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        testConexaoComMySQL();
    }
}
