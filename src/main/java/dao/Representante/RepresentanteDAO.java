package dao.Representante;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dao.Fornecedor.FornecedorDAO;
import models.Fornecedor.Fornecedor;
import models.Represetante.Representante;

public class RepresentanteDAO {
    public static void cadastrarRepresentante(Connection conn, String nome, String telefone, int fornecedorId) throws SQLException{
		if (representanteExiste(conn, nome, telefone, fornecedorId)) {
            throw new SQLException("Já existe um representante com o número de telefone informado!\nTente novamente!");
        }
        String sql = "INSERT INTO representante(nome, telefone, fornecedor_id) VALUES (?,?,?)";
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nome);
            pstmt.setString(2, telefone);
            pstmt.setInt(3, fornecedorId);
            pstmt.executeUpdate();
            System.out.println("Representante cadastrado com sucesso.");
        } catch (SQLException e) {
			System.err.println("Erro ao cadastrar representante: " + e.getMessage());
            throw e;
		}
	}

    public static void atualizarRepresentante(Connection conn, Representante r) throws SQLException {
        if (representanteExiste(conn, r.getNome(), r.getTelefone(), r.getFornecedor().getId())) {
            throw new SQLException("Já existe um representante com o telefone informado. Verifique novamente!");
        }
        String sql = "UPDATE representante SET nome = ?, telefone = ? WHERE fornecedor_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, r.getNome());
            pstmt.setString(2, r.getTelefone());
            pstmt.setInt(3, r.getFornecedor().getId());
    
            int linhasAfetadas = pstmt.executeUpdate();
            if (linhasAfetadas == 0) {
                System.out.println("Nenhum representante encontrado para atualizar.");
            } else {
                System.out.println("Representante atualizado com sucesso.");
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                throw new SQLException("Já existe um representante com o telefone informado. Tente novamente!");
            } else {
                System.err.println("Erro ao atualizar representante: " + e.getMessage());
                throw e;  
            }
        }
    }

    public static Representante representantePornome(Connection conn, Representante r) throws SQLException {
        String sql = "SELECT r.nome AS representante_nome, r.telefone AS representante_telefone, " +
                     "f.id AS fornecedor_id, f.nome AS fornecedor_nome " +
                     "FROM representante r " +
                     "INNER JOIN fornecedor f ON r.fornecedor_id = f.id " +
                     "WHERE r.nome = ?";
    
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, r.getNome()); 
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    r.setNome(rs.getString("representante_nome"));
                    r.setTelefone(rs.getString("representante_telefone")); 
    
                    Fornecedor fornecedor = new Fornecedor();
                    fornecedor.setId(rs.getInt("fornecedor_id")); 
                    fornecedor.setNome(rs.getString("fornecedor_nome"));
                    
                    r.setFornecedor(fornecedor);
    
                    return r;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar representante por nome: " + e.getMessage());
            throw e;
        }
    
        return null; 
    }
    
    
    public static List<Representante> listarRepresentantes(Connection conn) throws SQLException {
        String sql = "SELECT r.nome, r.telefone, f.nome AS fornecedor_nome "
                   + "FROM representante r "
                   + "JOIN fornecedor f ON r.fornecedor_id = f.id";
    
        List<Representante> representantes = new ArrayList<>();
    
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Representante representante = new Representante();
                representante.setNome(rs.getString("nome"));
                representante.setTelefone(rs.getString("telefone"));
    
                Fornecedor fornecedor = new Fornecedor();
                fornecedor.setNome(rs.getString("fornecedor_nome"));
                representante.setFornecedor(fornecedor);
    
                representantes.add(representante);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar representantes: " + e.getMessage());
            throw e;
        }
    
        return representantes;
    }

    public static List<Representante> listarRepresentantesPorFornecedorId(Connection conn, int fornecedorId) throws SQLException {
        String sql = "SELECT r.nome, r.telefone, r.fornecedor_id " +
                     "FROM representante r " +
                     "WHERE r.fornecedor_id = ?";
    
        List<Representante> representantes = new ArrayList<>();
    
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, fornecedorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Representante representante = new Representante();
                    representante.setNome(rs.getString("nome"));
                    representante.setTelefone(rs.getString("telefone"));
    
                    Fornecedor fornecedor = FornecedorDAO.fornecedorPorId(conn, rs.getInt("fornecedor_id"));
                    representante.setFornecedor(fornecedor);
    
                    representantes.add(representante);
                }
            }
        }
        return representantes;
    }
    
    public static void deletarRepresentante(Connection conn, Representante r) throws SQLException {
        if (r == null || r.getFornecedor() == null) {
            throw new IllegalArgumentException("O representante ou o fornecedor não pode ser nulo.");
        }
    
        String sql = "DELETE FROM representante WHERE nome = ? AND fornecedor_id = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, r.getNome()); 
            pstmt.setInt(2, r.getFornecedor().getId()); 
    
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Representante excluído com sucesso!");
            } else {
                System.out.println("Nenhum representante encontrado com o nome: " + r.getNome() + 
                                   " para o fornecedor com ID: " + r.getFornecedor().getId());
            }
        } catch (SQLException e) {
            System.err.println("Erro ao excluir representante: " + e.getMessage());
            throw e; 
        }
    }

    public static boolean representanteExiste(Connection conn, String nome, String telefone, int fornecedorId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM representante WHERE nome = ? AND telefone = ? AND fornecedor_id = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nome);
            ps.setString(2, telefone);
            ps.setInt(3, fornecedorId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0; 
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao verificar existência de representante: " + e.getMessage());
            throw e;
        }
        
        return false;  
    }
    
}
