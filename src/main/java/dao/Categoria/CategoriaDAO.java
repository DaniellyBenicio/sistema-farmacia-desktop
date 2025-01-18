package dao.Categoria;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import models.Categoria.Categoria;

public class CategoriaDAO {
    
    public static int criarCategoria(Connection conn, Categoria cat) throws SQLException {
        String sqlVerificar = "select id from categoria where nome = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sqlVerificar)) {
            pstmt.setString(1, cat.getNome());
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("id");
            } else {
                return inserirCategoria(conn, cat);
            }
        }
    }

    private static int inserirCategoria(Connection conn, Categoria cat) throws SQLException {
        String sqlInserir = "insert into categoria (nome) values (?)";
        try (PreparedStatement pstmtInserir = conn.prepareStatement(sqlInserir, PreparedStatement.RETURN_GENERATED_KEYS)) {
            pstmtInserir.setString(1, cat.getNome());
            pstmtInserir.executeUpdate();

            try (ResultSet rsInserir = pstmtInserir.getGeneratedKeys()) {
                if (rsInserir.next()) {
                    return rsInserir.getInt(1);  
                } else {
                    throw new SQLException("Falha ao obter o id da categoria.");
                }
            }
        }
    }

    public static int buscarCategoriaPorNome(Connection conn, String nome) throws SQLException {
        String sql = "select id from categoria where nome = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nome);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");  
            }
            return 0;  
        } catch (SQLException e) {
            System.err.println("Erro ao buscar categoria: " + e.getMessage());
            throw e;
        }
    }

    public static ArrayList<Categoria> listarTodasCategorias(Connection conn) throws SQLException {
        String sql = "SELECT nome from categoria order by nome ASC";

        ArrayList<Categoria> categorias = new ArrayList<>();

        try (PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Categoria categoria = new Categoria();
                categoria.setNome(rs.getString("nome"));
                categorias.add(categoria);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar categorias: " + e.getMessage());
            throw e;
        }
        return categorias;
    }
}
