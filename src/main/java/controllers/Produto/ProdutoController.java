package controllers.Produto;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import dao.Produto.ProdutoDAO;
import models.Produto.Produto;

public class ProdutoController {
    public static boolean produtoExiste(Connection conn, Produto p) throws SQLException {
        if (p == null) {
            throw new IllegalArgumentException("O produto não pode ser nulo.");
        }
        try {
            return ProdutoDAO.produtoExiste(conn, p);
        } catch (SQLException e) {
            throw new SQLException("Erro ao verificar a existência do produto.", e);
        }
    }

    public static void cadastrarProduto(Connection conn, Produto p) throws SQLException{
        if(p == null || p.getId() <=0){
            throw new IllegalArgumentException("O produto não pode ser nulo.");
        }
        try{
            ProdutoDAO.cadastrarProduto(conn, p);
        } catch(SQLException e){
            if (e.getMessage().contains("Duplicate entry")) {
                throw new SQLException("Produto já cadastrado na base de dados.");
            } else {
                throw new SQLException("Erro ao atualizar produto.", e);
            }
        }
    }

    public static void atualizarProduto(Connection conn, Produto p) throws SQLException {
        if (p == null || p.getId() <=0) {
            throw new IllegalArgumentException("O produto não pode ser nulo.");
        }
        try {
            ProdutoDAO.atualizarProduto(conn, p);
        } catch (SQLException e) {
            throw new SQLException("Erro ao atualizar produto.", e);
        }
    }

    public static List<Produto> listarTodos(Connection conn) throws SQLException {
        try {
            return ProdutoDAO.listarTodos(conn);
        } catch (SQLException e) {
            throw new SQLException("Erro ao listar produtos.", e);
        }
    }

    public static Produto buscarProdutoPorId(Connection conn, int id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("ID inválido.");
        }
        try {
            return ProdutoDAO.buscarPorId(conn, id);
        } catch (SQLException e) {
            throw new SQLException("Erro ao buscar produto pelo ID.", e);
        }
    }

    public static List<String> listarCategoriasProduto(Connection conn) throws SQLException {
        try {
            return ProdutoDAO.produtoCategoria(conn);
        } catch (SQLException e) {
            throw new SQLException("Erro ao listar as categorias dos produtos.", e);
        }
    } 
    
    public static List<Produto> listarEstoqueProduto(Connection conn) throws SQLException {
        try {
            return ProdutoDAO.listarEstoqueProdutos(conn);
        } catch (SQLException e) {
            throw new SQLException("Erro ao listar o estoque dos produtos.", e);
        }
    } 

    public static List<Produto> listarBaixoEstoqueProduto(Connection conn) throws SQLException {
        try {
            return ProdutoDAO.listarBaixoEstoqueProdutos(conn);
        } catch (SQLException e) {
            throw new SQLException("Erro ao listar baixo estoque dos produtos.", e);
        }
    } 

    public static void excluirProduto(Connection conn, Produto p) throws SQLException{
        if (p == null || p.getId() <=0) {
            throw new IllegalArgumentException("ID inválido.");
        }
        try{
            ProdutoDAO.deletarProduto(conn, p);
        } catch(SQLException e){
            System.err.println("Erro ao excluir produto: " + e.getMessage());
        }
    }
    
    
}
