package dao.Cliente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import models.Cliente.Cliente;

public class ClienteDAO {
    public static void cadastrarCliente(Connection conn, Cliente c) throws SQLException {
   
        if (existeClientePorCpf(conn, c.getCpf())) {
        throw new IllegalArgumentException("Cliente com o CPF " + c.getCpf() + " já cadastrado.");
    }
        String sql = "INSERT INTO cliente(nome, cpf, telefone, numCasa, bairro, cidade, estado, pontoReferencia, funcionario_id) VALUES (?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, c.getNome());
            pstmt.setString(2, c.getCpf().replaceAll("[^0-9]", ""));
            pstmt.setString(3, c.getTelefone().replaceAll("[^0-9]", ""));
            pstmt.setString(4, c.getNumCasa());
            pstmt.setString(5, c.getBairro());
            pstmt.setString(6, c.getCidade());
            pstmt.setString(7, c.getEstado());
            pstmt.setString(8, c.getPontoReferencia());
            pstmt.setInt(9, c.getFuncionario().getId());            
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    c.setId(generatedKeys.getInt(1));  
                } else {
                    throw new SQLException("Erro ao obter ID do cliente.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao cadastrar cliente: " + e.getMessage());
            throw e;
        }
    }

    public static boolean existeClientePorCpf(Connection conn, String cpf) throws SQLException {
        String sql = "SELECT COUNT(*) FROM cliente WHERE cpf = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, cpf);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0; 
                }
            }
        }
        return false; 
    }
}
