package dao.ProdutoCategoria;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dao.Categoria.CategoriaDAO;
import models.Categoria.Categoria;

public class ProdutoCategoriaDAO {
    
    public static void associarProdutoACategorias(Connection conn, int produtoId, List<Categoria> categorias) throws SQLException {
        String sql = "insert into prodCategoria (produto_id, categoria_id) VALUES (?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (Categoria cat : categorias) {
                int categoriaId = CategoriaDAO.buscarCategoriaPorNome(conn, cat.getNome());
                if (categoriaId == 0) { 
                    categoriaId = CategoriaDAO.criarCategoria(conn, cat);
                }
                pstmt.setInt(1, produtoId);
                pstmt.setInt(2, categoriaId);
                pstmt.executeUpdate();
            }
        } catch (SQLException ex) {
            System.err.println("Erro ao associar produto às categorias: " + ex.getMessage());
            throw ex; 
        }
    }

    public static void desassociarProdutoACategorias(Connection conn, int produtoId) throws SQLException {
        String sql = "delete from prodCategoria where produto_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, produtoId);
            pstmt.executeUpdate();
        }
    }

    public static List<Integer> buscarCategoriasDoProduto(Connection conn, int produtoId) throws SQLException {
        List<Integer> categorias = new ArrayList<>();
        String sql = "select categoria_id from prodCategoria where produto_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, produtoId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    categorias.add(rs.getInt("categoria_id"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar categorias do produto: " + e.getMessage());
            throw e;
        }
        return categorias;
    }

    public static List<Integer> buscarProdutosDaCategoria(Connection conn, int categoriaId) throws SQLException {
        List<Integer> produtos = new ArrayList<>();
        String sql = "select produto_id from prodCategoria where categoria_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, categoriaId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    produtos.add(rs.getInt("produto_id"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar produtos da categoria: " + e.getMessage());
            throw e;
        }
        return produtos;
    }

    public static void removerTodasCatProd(Connection conn, int produtoId) throws SQLException {
        String sql = "delete from prodCategoria where produto_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, produtoId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao remover todas as categorias do produto: " + e.getMessage());
            throw e;
        }
    }

    public static void removerTodosProdCategoria(Connection conn, int categoriaId) throws SQLException {
        String sql = "delete from prodCategoria where categoria_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, categoriaId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao remover todos os produtos da categoria: " + e.getMessage());
            throw e;
        }
    }

}