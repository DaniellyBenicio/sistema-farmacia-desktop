package controllers.Produto;

import java.sql.Connection;
import java.sql.SQLException;

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

    public static void atualizarProduto(Connection conn, Produto p) throws SQLException {
        if (p == null || p.getId() <=0) {
            throw new IllegalArgumentException("O produto não pode ser nulo.");
        }
        try {
            ProdutoDAO.atualizarProduto(conn, p);
        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate entry")) {
                throw new SQLException("Produto já cadastrado na base de dados.");
            } else {
                throw new SQLException("Erro ao atualizar produto.", e);
            }
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
