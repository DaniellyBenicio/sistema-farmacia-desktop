package dao.Fabricante;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import models.Fabricante.Fabricante;

public class FabricanteDAO {
    public static int criarFabricante(Connection conn, Fabricante fab) throws SQLException {
        String sqlVerificar = "select id from fabricante where nome = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sqlVerificar)) {
            pstmt.setString(1, fab.getNome());
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("id");
            } else {
                return inserirFabricante(conn, fab);
            }
        }
    }

    private static int inserirFabricante(Connection conn, Fabricante fab) throws SQLException {
        String sqlInserir = "insert into fabricante (nome) values (?)";
        try (PreparedStatement pstmtInserir = conn.prepareStatement(sqlInserir, PreparedStatement.RETURN_GENERATED_KEYS)) {
            pstmtInserir.setString(1, fab.getNome());
            pstmtInserir.executeUpdate();

            try (ResultSet rsInserir = pstmtInserir.getGeneratedKeys()) {
                if (rsInserir.next()) {
                    return rsInserir.getInt(1);  
                } else {
                    throw new SQLException("Falha ao obter o id do fabricante.");
                }
            }
        }
    }
     
    public static int buscarFabricantePorNome(Connection conn, String nome) throws SQLException {
        String sql = "select id from fabricante where nome = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nome);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");  
            }
            return 0;  
        } catch (SQLException e) {
            System.err.println("Erro ao buscar fabricante: " + e.getMessage());
            throw e;
        }
    }

    public static ArrayList<Fabricante> listarTodosFabricantes(Connection conn) throws SQLException {
        String sql = "SELECT nome from fabricante order by nome ASC";

        ArrayList<Fabricante> fabricantes = new ArrayList<>();

        try (PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Fabricante fab = new Fabricante();
                fab.setNome(rs.getString("nome"));
                fabricantes.add(fab);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar fabricantes: " + e.getMessage());
            throw e;
        }
        return fabricantes;
    }
}
