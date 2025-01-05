package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import models.Cargo;
import models.Funcionario;

public class FuncionarioDAO {
    public static void cadastrarFuncionario(Connection conn, Funcionario f) throws SQLException {
        int cargoId = CargoDAO.criarCargo(conn, f.getCargo());
        
        String sql = "INSERT INTO funcionario(nome, telefone, email, cargo_id) VALUES (?,?,?,?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, f.getNome());
            pstmt.setString(2, f.getTelefone().replaceAll("[^0-9]", ""));
            pstmt.setString(3, f.getEmail());
            pstmt.setInt(4, cargoId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao cadastrar funcionário: " + e.getMessage());
            throw e;
        }  
    }

    public static void atualizarFuncionario(Connection conn, Funcionario f) throws SQLException{
	    String sql = "UPDATE funcionario SET nome = ?, telefone = ?, email = ?, cargo_id = ? WHERE id = ?";
	    try(PreparedStatement pstmt = conn.prepareStatement(sql)){
	        pstmt.setString(1, f.getNome());
	        pstmt.setString(2, f.getTelefone());
            pstmt.setString(3, f.getEmail());
            pstmt.setInt(4, f.getCargo().getId());
            pstmt.setInt(5, f.getId()); 
	        pstmt.executeUpdate();
	        System.out.println("Dados atualizados com sucesso!");
	    }catch (SQLException e) {
            System.err.println("Erro ao atualizar o funcionário: " + e.getMessage());
            throw e;
        }
	}

    public String verificarFuncionarioPorCodigo(Connection conn, int codigo) throws SQLException {
        String query = "SELECT f.nome, c.nome AS cargo_nome FROM funcionario f " +
                       "JOIN cargo c ON f.cargo_id = c.id WHERE f.id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) { 
            pstmt.setInt(1, codigo); 
            ResultSet rs = pstmt.executeQuery(); 
    
            if (rs.next()) {
                return "Código: " + codigo + ", Nome: " + rs.getString("nome") + 
                       ", Cargo: " + rs.getString("cargo_nome"); 
            } else {
                return "Funcionário não encontrado.";
            }
        } catch (SQLException e) {
            System.err.println("Erro ao consultar o banco de dados: " + e.getMessage());
            throw e; 
        }
    }    

    public static Funcionario funcionarioPorId(Connection conn, int id) throws SQLException {
        String sql = "SELECT f.id, f.nome, f.email, f.telefone, c.nome AS cargo " +
                     "FROM funcionario f " +
                     "JOIN cargo c ON f.cargo_id = c.id WHERE f.id = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id); 
    
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Funcionario f = new Funcionario();
                    f.setId(rs.getInt("id"));
                    f.setNome(rs.getString("nome"));
                    f.setEmail(rs.getString("email"));
                    f.setTelefone(rs.getString("telefone"));
    
                    Cargo cargo = new Cargo();
                    cargo.setNome(rs.getString("cargo"));
                    f.setCargo(cargo);
    
                    return f;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar funcionário por ID: " + e.getMessage());
            throw e; 
        }
        
        return null;
    }
    
    public static List<Funcionario> listarTodosFuncionarios(Connection conn) throws SQLException {
        String sql = "SELECT f.id, f.nome, f.email, f.telefone, c.nome AS cargo FROM funcionario f " +
                     "JOIN cargo c ON f.cargo_id = c.id";
        
        List<Funcionario> funcionarios = new ArrayList<>(); 
        
        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (!rs.isBeforeFirst()) {
                System.out.println("Nenhum funcionário encontrado.");
            }
            while (rs.next()) {
                Funcionario f = new Funcionario();
                f.setId(rs.getInt("id"));
                f.setNome(rs.getString("nome"));
                f.setEmail(rs.getString("email"));
                f.setTelefone(rs.getString("telefone"));
                
                Cargo cargo = new Cargo();
                cargo.setNome(rs.getString("cargo"));
                f.setCargo(cargo);
                
                funcionarios.add(f);  
            }
        }
        
        return funcionarios;
    }

    public static void deletarFuncionario(Connection conn, Funcionario f) throws SQLException {
        String sql = "DELETE FROM Funcionario WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, f.getId());
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Funcionário excluído com sucesso!");
            } else {
                System.out.println("Nenhum funcionário encontrado para o ID " + f.getId());
            }
        } catch (SQLException e) {
            System.err.println("Erro ao excluir funcionário: " + e.getMessage());
            throw e;  
        }
    }
    
}