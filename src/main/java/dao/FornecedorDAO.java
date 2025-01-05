package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import models.Fornecedor;
import models.Representante;

public class FornecedorDAO {
    public static void cadastrarFornecedor(Connection conn, Fornecedor forn) throws SQLException {
        String sql = "INSERT INTO fornecedor(nome, cnpj, email, telefone, funcionario_id) VALUES (?,?,?,?,?)";
        System.out.println("SQL Query: " + sql);
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) { 
            System.out.println("PreparedStatement criado com sucesso.");
            pstmt.setString(1, forn.getNome());
            pstmt.setString(2, forn.getCnpj().replaceAll("[^0-9]", ""));
            pstmt.setString(3, forn.getEmail());
            pstmt.setString(4, forn.getTelefone().replaceAll("[^0-9]", ""));
            pstmt.setInt(5, forn.getFuncionario().getId());
            pstmt.executeUpdate(); 
    
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    forn.setId(generatedKeys.getInt(1)); 
                } else {
                    throw new SQLException("Erro ao obter ID do fornecedor.");
                }
            }
    
        } catch (SQLException e) {
            System.err.println("Erro ao executar o SQL: " + e.getMessage());
            throw new SQLException("Erro ao cadastrar fornecedor.", e);  
        }
    }  

    public static void atualizarFornecedor(Connection conn, Fornecedor forn) throws SQLException {
        if (forn.getId() <= 0) {
            throw new IllegalArgumentException("ID do fornecedor inválido para atualização.");
        }
    
        String sql = "UPDATE Fornecedor SET nome = ?, cnpj = ?, email = ?, telefone = ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, forn.getNome());
            pstmt.setString(2, forn.getCnpj().replaceAll("[^0-9]", ""));
            pstmt.setString(3, forn.getEmail());
            pstmt.setString(4, forn.getTelefone().replaceAll("[^0-9]", ""));
            pstmt.setInt(5, forn.getId());  
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw e; 
        }
    }

  public static Fornecedor fornecedorPorId(Connection conn, int id) throws SQLException {
    String sql = "SELECT f.id, f.nome,  f.cnpj, f.email, f.telefone " +
                 "FROM fornecedor f " +
                 "WHERE f.id = ?";

    try (PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, id); 

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                Fornecedor fornecedor = new Fornecedor();
                fornecedor.setId(rs.getInt("id"));
                fornecedor.setNome(rs.getString("nome"));
                fornecedor.setEmail(rs.getString("email"));
                fornecedor.setTelefone(rs.getString("telefone"));
                fornecedor.setCnpj(rs.getString("cnpj"));
                
                return fornecedor;
            }
        }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar fornecedor por ID: " + e.getMessage());
            throw e; 
    }

        return null;  
    }

    public static String getNomeFornecedorPorId(Connection conn, int fornecedorId) throws SQLException {
        String nomeFornecedor = null;
        String query = "SELECT nome FROM fornecedor WHERE id = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, fornecedorId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                nomeFornecedor = rs.getString("nome");
            }
        }
        
        return nomeFornecedor;
    }

    public static ArrayList<Fornecedor> listarFornecedores(Connection conn) throws SQLException {
        String sql = "SELECT f.id, f.nome AS nomeFornecedor, f.email, f.telefone, f.cnpj, r.nome AS nomeRepresentante, r.telefone AS telefoneRepresentante " +
                    "FROM fornecedor f " +
                    "LEFT JOIN representante r ON f.id = r.fornecedor_id  " +
                    "ORDER BY f.nome ASC";
        
        ArrayList<Fornecedor> fornecedores = new ArrayList<>();
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Fornecedor fornecedor = new Fornecedor();
                fornecedor.setId(rs.getInt("id"));
                fornecedor.setNome(rs.getString("nomeFornecedor"));
                fornecedor.setEmail(rs.getString("email"));
                fornecedor.setTelefone(rs.getString("telefone"));
                fornecedor.setCnpj(rs.getString("cnpj"));
                
                String nomeRepresentante = rs.getString("nomeRepresentante");
                if (nomeRepresentante != null) {
                    Representante representante = new Representante();
                    representante.setNome(nomeRepresentante);
                    
                    // Aqui, vamos pegar o telefone do representante corretamente
                    String telefoneRepresentante = rs.getString("telefoneRepresentante");
                    if (telefoneRepresentante != null) {
                        representante.setTelefone(telefoneRepresentante);
                    }
                    
                    fornecedor.setRepresentante(representante); 
                }
    
                fornecedores.add(fornecedor);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar fornecedores: " + e.getMessage());
            throw e;
        }
        return fornecedores;
    }
    
    public static void deletarFornecedor(Connection conn, Fornecedor forn) throws SQLException {
        String sql = "DELETE FROM fornecedor WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setInt(1, forn.getId());
        int rowsAffected = pstmt.executeUpdate();
            
                if (rowsAffected > 0) {
                    System.out.println("Fornecedor excluído com sucesso!");
                } else {
                    System.out.println("Nenhum fornecedor encontrado para o ID " + forn.getId());
                }
            } catch (SQLException e) {
                System.err.println("Erro ao excluir fornecedor: " + e.getMessage());
                throw e;  
            }
        }
} 

