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
   
        if (existeClientePorCpf(conn, c.getCpf())) {
        throw new IllegalArgumentException("Cliente com o CPF " + c.getCpf() + " já cadastrado.");
    } 
        String sql = "INSERT INTO cliente(nome, cpf, telefone, rua, numCasa, bairro, cidade, estado, pontoReferencia, funcionario_id) VALUES (?,?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, c.getNome());
            pstmt.setString(2, Criptografia.criptografar(c.getCpf().replaceAll("[^0-9]", "")));
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
        }
    }

    public static void atualizarCliente(Connection conn, Cliente c) throws SQLException {
        if (c.getId() <= 0) {
            throw new IllegalArgumentException("ID do cliente inválido para atualização.");
        }

        String sql = "UPDATE cliente SET nome = ?, cpf = ?, email = ?, telefone = ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, c.getNome());
            pstmt.setString(2, c.getCpf().replaceAll("[^0-9]", ""));
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
                    cliente.setCpf(rs.getString("cpf"));
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

    public static Cliente clientePorNome(Connection conn, String nome) throws SQLException {
        String sql = "SELECT c.id, c.nome, c.cpf, c.telefone, c.rua, c.numCasa, c.bairro, c.cidade, c.estado, c.pontoReferencia " +
                     "FROM cliente c " +
                     "WHERE c.nome = ?";
    
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nome);  
    
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Cliente cliente = new Cliente();
                    cliente.setId(rs.getInt("id"));
                    cliente.setNome(rs.getString("nome"));
                    cliente.setCpf(rs.getString("cpf"));
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
            System.err.println("Erro ao buscar cliente por nome: " + e.getMessage());
            throw e;
        }
    
        return null;  
    }

    public static Cliente clientePorCpf(Connection conn, String cpf) throws SQLException {
        String sql = "SELECT c.id, c.nome, c.cpf, c.telefone, c.rua, c.numCasa, c.bairro, c.cidade, c.estado, c.pontoReferencia " +
                     "FROM cliente c " +
                     "WHERE c.cpf = ?";
    
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cpf); 
    
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Cliente cliente = new Cliente();
                    cliente.setId(rs.getInt("id"));
                    cliente.setNome(rs.getString("nome"));
                    cliente.setCpf(rs.getString("cpf"));
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
        }
    
        return null;  
    }
    
    public static ArrayList<Cliente> listarClientes(Connection conn) throws SQLException {
        String sql = "SELECT c.id, c.nome, c.cpf, c.telefone, c.rua, c.numCasa, c.bairro, c.cidade, c.estado, c.pontoReferencia " +
             "FROM cliente c " +
             "ORDER BY c.nome ASC";

        ArrayList<Cliente> clientes = new ArrayList<>();

        try (PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Cliente cliente = new Cliente();
                cliente.setId(rs.getInt("id"));
                cliente.setNome(rs.getString("nome"));
                cliente.setCpf(rs.getString("cpf"));
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
}
