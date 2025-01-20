package dao.Cliente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import utils.Criptografia;
import models.Cliente.Cliente;

public class ClienteDAO {
    public static void cadastrarCliente(Connection conn, Cliente c) throws SQLException {
        String cpfCriptografado = null;
    
        try {
            cpfCriptografado = Criptografia.criptografar(c.getCpf());
        } catch (Exception e) {
            System.err.println("Erro ao criptografar CPF: " + e.getMessage());
            throw new SQLException("Erro ao criptografar CPF.", e); 
        }
    
        try {
            if (verificarCpfExistente(conn, cpfCriptografado, c.getId())) {
                throw new IllegalArgumentException("Cliente com o CPF " + c.getCpf() + " já cadastrado.");
            }
        } catch (SQLException e) {
            System.err.println("Erro ao verificar existência do cliente: " + e.getMessage());
            throw e; 
        }
    
        String sql = "INSERT INTO cliente(nome, cpf, telefone, rua, numCasa, bairro, cidade, estado, pontoReferencia, funcionario_id) VALUES (?,?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, c.getNome());
            pstmt.setString(2, cpfCriptografado); 
            pstmt.setString(3, c.getTelefone().replaceAll("[^0-9]", "")); 
            pstmt.setString(4, c.getRua());
            pstmt.setString(5, c.getNumCasa());
            pstmt.setString(6, c.getBairro());
            pstmt.setString(7, c.getCidade());
            pstmt.setString(8, c.getEstado());
            pstmt.setString(9, c.getPontoReferencia());
            pstmt.setInt(10, c.getFuncionario().getId());            
    
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
        } catch (Exception e) {
            System.err.println("Erro inesperado ao cadastrar cliente: " + e.getMessage());
            throw new SQLException("Erro inesperado ao cadastrar cliente.", e); 
        }
    }

    public static void atualizarCliente(Connection conn, Cliente c) throws SQLException {
        String cpfCriptografadoNovo = null;

        try {
            cpfCriptografadoNovo = Criptografia.criptografar(c.getCpf());
        } catch (Exception e) {
            System.err.println("Erro ao criptografar CPF: " + e.getMessage());
            throw new SQLException("Erro ao criptografar CPF.", e);
        }

        try {
            if (verificarCpfExistente(conn, cpfCriptografadoNovo, c.getId())) {
                throw new IllegalArgumentException("Já existe um cliente com o CPF " + c.getCpf());
            }
        } catch (SQLException e) {
            System.err.println("Erro ao verificar existência do CPF: " + e.getMessage());
            throw e;
        }

    String sql = "UPDATE cliente SET nome = ?, cpf = ?, telefone = ?, rua = ?, numCasa = ?, bairro = ?, cidade = ?, estado = ?, pontoReferencia = ? WHERE id = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, c.getNome());
            pstmt.setString(2, cpfCriptografadoNovo);
            pstmt.setString(3, c.getTelefone().replaceAll("[^0-9]", ""));
            pstmt.setString(4, c.getRua());
            pstmt.setString(5, c.getNumCasa());
            pstmt.setString(6, c.getBairro());
            pstmt.setString(7, c.getCidade());
            pstmt.setString(8, c.getEstado());
            pstmt.setString(9, c.getPontoReferencia());
            pstmt.setInt(10, c.getId());
    
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar cliente: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.err.println("Erro inesperado ao atualizar cliente: " + e.getMessage());
            throw new SQLException("Erro inesperado ao atualizar cliente ID " + c.getId(), e);
        }
    }

    public static Cliente clientePorID(Connection conn, int id) throws SQLException {
        String sql = "SELECT c.id, c.nome, c.cpf, c.telefone, c.rua, c.numCasa, c.bairro, c.cidade, c.estado, c.pontoReferencia " +
                 "FROM cliente c " +
                 "WHERE c.id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Cliente cliente = new Cliente();
                    cliente.setId(rs.getInt("id"));
                    cliente.setNome(rs.getString("nome"));
                    String cpfCriptografado = rs.getString("cpf");
                    try {
                        String cpfDescriptografado = Criptografia.descriptografar(cpfCriptografado);
                        cliente.setCpf(cpfDescriptografado);
                    } catch (Exception e) {
                        System.err.println("Erro ao descriptografar CPF para o cliente ID " + id + ": " + e.getMessage());
                        throw new SQLException("Erro ao descriptografar CPF do cliente ID " + id, e);
                    }
                    cliente.setTelefone(rs.getString("telefone"));
                    cliente.setRua(rs.getString("rua"));
                    cliente.setNumCasa(rs.getString("numCasa"));
                    cliente.setBairro(rs.getString("bairro"));
                    cliente.setCidade(rs.getString("cidade"));
                    cliente.setEstado(rs.getString("estado"));
                    cliente.setPontoReferencia(rs.getString("pontoReferencia"));

                    return cliente;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar cliente por ID: " + e.getMessage());
            throw e;
        }

        return null;
    }

    public static Cliente clientePorCpf(Connection conn, String cpf) throws SQLException {
        String cpfCriptografado = null;
        try {
            cpfCriptografado = Criptografia.criptografar(cpf.replaceAll("[^0-9]", ""));
        } catch (Exception e) {
            System.err.println("Erro ao criptografar CPF: " + e.getMessage());
            throw new SQLException("Erro ao criptografar CPF para busca de cliente", e); 
        }
    
        String sql = "SELECT c.id, c.nome, c.cpf, c.telefone, c.rua, c.numCasa, c.bairro, c.cidade, c.estado, c.pontoReferencia " +
                     "FROM cliente c " +
                     "WHERE c.cpf = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cpfCriptografado);
        
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Cliente cliente = new Cliente();
                    cliente.setId(rs.getInt("id"));
                    cliente.setNome(rs.getString("nome"));
                    
                    String cpfDescriptografado = null;
                    try {
                        cpfDescriptografado = Criptografia.descriptografar(rs.getString("cpf"));
                    } catch (Exception e) {
                        System.err.println("Erro ao descriptografar CPF: " + e.getMessage());
                        throw new SQLException("Erro ao descriptografar CPF do cliente", e); 
                    }
                    cliente.setCpf(cpfDescriptografado);
                    cliente.setTelefone(rs.getString("telefone"));
                    cliente.setRua(rs.getString("rua"));
                    cliente.setNumCasa(rs.getString("numCasa"));
                    cliente.setBairro(rs.getString("bairro"));
                    cliente.setCidade(rs.getString("cidade"));
                    cliente.setEstado(rs.getString("estado"));
                    cliente.setPontoReferencia(rs.getString("pontoReferencia"));
        
                    return cliente;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar cliente por CPF: " + e.getMessage());
            throw e; 
        } catch (Exception e) {
            System.err.println("Erro inesperado ao buscar cliente por CPF: " + e.getMessage());
            throw new SQLException("Erro inesperado ao buscar cliente por CPF", e);
        }
        
        return null;  
    }
    
    
    public static ArrayList<Cliente> listarClientesSemCpf(Connection conn) throws SQLException {
        String sql = "SELECT c.id, c.nome, c.telefone, c.rua, c.numCasa, c.bairro, c.cidade, c.estado, c.pontoReferencia " +
                     "FROM cliente c " +
                     "ORDER BY c.nome ASC";
    
        ArrayList<Cliente> clientes = new ArrayList<>();
    
        try (PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Cliente cliente = new Cliente();
                cliente.setId(rs.getInt("id"));
                cliente.setNome(rs.getString("nome"));
                cliente.setTelefone(rs.getString("telefone"));
                cliente.setRua(rs.getString("rua"));
                cliente.setNumCasa(rs.getString("numCasa"));
                cliente.setBairro(rs.getString("bairro"));
                cliente.setCidade(rs.getString("cidade"));
                cliente.setEstado(rs.getString("estado"));
                cliente.setPontoReferencia(rs.getString("pontoReferencia"));
    
                clientes.add(cliente);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar clientes: " + e.getMessage());
            throw e;
        }
        return clientes;
    }    

    public static void deletarCliente(Connection conn, Cliente c) throws SQLException {
        String sql = "DELETE FROM cliente WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, c.getId());
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Cliente excluído com sucesso!");
            } else {
                System.out.println("Nenhum cliente encontrado para o ID " + c.getId());
            }
        } catch (SQLException e) {
            System.err.println("Erro ao excluir cliente: " + e.getMessage());
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

    public static boolean verificarCpfExistente(Connection conn, String cpfCriptografado, int idCliente) throws SQLException {
        String sql = "SELECT COUNT(*) FROM cliente WHERE cpf = ? AND id != ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, cpfCriptografado);
            pstmt.setInt(2, idCliente);  
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
}
