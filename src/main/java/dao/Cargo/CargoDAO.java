package dao.Cargo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import models.Cargo.Cargo;


public class CargoDAO {
    
    public static int criarCargo(Connection conn, Cargo cargo) throws SQLException {
        String sqlVerificar = "SELECT id FROM cargo WHERE nome = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sqlVerificar)) {
            pstmt.setString(1, cargo.getNome());
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("id"); 
            } else {
                return inserirCargo(conn, cargo);
            }
        }
    }

    private static int inserirCargo(Connection conn, Cargo cargo) throws SQLException {
        String sqlInserir = "INSERT INTO cargo(nome) VALUES (?)";
        try (PreparedStatement pstmtInserir = conn.prepareStatement(sqlInserir, PreparedStatement.RETURN_GENERATED_KEYS)) {
            pstmtInserir.setString(1, cargo.getNome());
            pstmtInserir.executeUpdate();

            try (ResultSet rsInserir = pstmtInserir.getGeneratedKeys()) {
                if (rsInserir.next()) {
                    return rsInserir.getInt(1);
                } else {
                    throw new SQLException("Falha ao obter o id do cargo .");
                }
            }
        }
    }

    public static int CargoPorNome(Connection conn, String nome) throws SQLException {
        String sql = "SELECT id FROM cargo WHERE nome = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nome);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
            return 0; 
        } catch (SQLException e) {
            System.err.println("Erro ao buscar cargo: " + e.getMessage());
            throw e;
        }
    }

    public static ArrayList<Cargo> listarTodosCargos(Connection conn) throws SQLException {
        String sql = "SELECT nome FROM cargo ORDER BY nome ASC";

        ArrayList<Cargo> cargos = new ArrayList<>();

        try (PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Cargo cargo = new Cargo();
                cargo.setNome(rs.getString("nome")); 

                cargos.add(cargo);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar cargos: " + e.getMessage());
            throw e;
        }
        return cargos; 
    }

}