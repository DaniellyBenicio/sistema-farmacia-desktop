package controllers.ItemVenda;

import dao.ItemVenda.ItemVendaDAO;
import models.ItemVenda.ItemVenda;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ItemVendaController {

    public void inserirItemVenda(Connection conn, ItemVenda iv) throws SQLException {
        try {
            ItemVendaDAO.inserirItemVenda(conn, iv);
            System.out.println("Item de venda inserido com sucesso.");
        } catch (SQLException e) {
            System.err.println("Erro ao inserir item de venda: " + e.getMessage());
            throw e;
        }
    }

    public void atualizarItemVenda(Connection conn, ItemVenda iv) throws SQLException {
        try {
            ItemVendaDAO.atualizarItemVenda(conn, iv);
            System.out.println("Item de venda atualizado com sucesso.");
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar item de venda: " + e.getMessage());
            throw e;
        }
    }

    public void excluirItemVenda(Connection conn, int id) throws SQLException {
        try {
            ItemVendaDAO.excluirItemVenda(conn, id);
            System.out.println("Item de venda excluído com sucesso.");
        } catch (SQLException e) {
            System.err.println("Erro ao excluir item de venda: " + e.getMessage());
            throw e;
        }
    }

    public ItemVenda buscarItemVendaPorId(Connection conn, int id) throws SQLException {
        try {
            return ItemVendaDAO.buscarItemVendaPorId(conn, id);
        } catch (SQLException e) {
            System.err.println("Erro ao buscar item de venda por ID: " + e.getMessage());
            throw e;
        }
    }

    public List<ItemVenda> buscarTodosItemVendas(Connection conn) throws SQLException {
        try {
            return ItemVendaDAO.buscarTodosItemVendas(conn);
        } catch (SQLException e) {
            System.err.println("Erro ao buscar todos os itens de venda: " + e.getMessage());
            throw e;
        }
    }

    public List<ItemVenda> buscarItensPorVenda(Connection conn, int vendaId) throws SQLException {
        try {
            return ItemVendaDAO.buscarItensPorVenda(conn, vendaId);
        } catch (SQLException e) {
            System.err.println("Erro ao buscar itens de venda por ID de venda: " + e.getMessage());
            throw e;
        }
    }

    public BigDecimal calcularTotalVenda(Connection conn, int vendaId) throws SQLException {
        try {
            return ItemVendaDAO.calcularTotalVenda(conn, vendaId);
        } catch (SQLException e) {
            System.err.println("Erro ao calcular total da venda: " + e.getMessage());
            throw e;
        }
    }
}