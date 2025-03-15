package controllers.Venda;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import dao.Venda.VendaDAO;
import models.Venda.Venda;

public class VendaController {
    public boolean verificarVendaExistente(Connection conn, Venda v) throws SQLException {
        try {
            return VendaDAO.verificarVendaExistente(conn, v);
        } catch (SQLException e) {
            System.err.println("Erro ao verificar existência da venda: " + e.getMessage());
            throw e;
        }
    }

    public void realizarVenda(Connection conn, Venda v) throws SQLException {
        try {
            VendaDAO.realizarVenda(conn, v);
            System.out.println("Venda realizada com sucesso!");
        } catch (SQLException e) {
            System.err.println("Erro ao realizar a venda: " + e.getMessage());
            throw e;
        }
    }

    public Venda buscarVendaId(Connection conn, int id) throws SQLException {
        try {
            return VendaDAO.buscarVendaPorId(conn, id);
        } catch (SQLException e) {
            System.err.println("Erro ao buscar venda: " + e.getMessage());
            throw e;
        }
    }

    public List<Venda> listarVendas(Connection conn) throws SQLException {
        try {
            return VendaDAO.listarTodasVendas(conn);
        } catch (SQLException e) {
            System.err.println("Erro ao listar vendas: " + e.getMessage());
            throw e;
        }
    }

    public boolean excluirVenda(Connection conn, int id) throws SQLException {
        try {
            return VendaDAO.excluirVenda(conn, id);
        } catch (SQLException e) {
            System.err.println("Erro ao excluir venda: " + e.getMessage());
            throw e;
        }
    }

    public void editarVenda(Connection conn, Venda v) throws SQLException {
        try {
            VendaDAO.editarVenda(conn, v);
            System.out.println("Venda atualizada com sucesso!");
        } catch (SQLException e) {
            System.err.println("Erro ao editar venda: " + e.getMessage());
            throw e;
        }
    }
}
