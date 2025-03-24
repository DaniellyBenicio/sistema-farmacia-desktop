package controllers.Pagamento;

import dao.Pagamento.PagamentoDAO;
import models.Pagamento.Pagamento;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class PagamentoController {

    public static void cadastrarPagamento(Connection conn, Pagamento p) throws SQLException {
        if (p == null) {
            throw new IllegalArgumentException("Pagamento não pode ser nulo.");
        }
        try {
            PagamentoDAO.cadastrarPagamento(conn, p);
        } catch (SQLException e) {
            throw new SQLException("Erro ao cadastrar pagamento.", e);
        }
    }

    public static void atualizarPagamento(Connection conn, Pagamento p) throws SQLException {
        if (p == null || p.getId() <= 0) {
            throw new IllegalArgumentException("Pagamento inválido.");
        }
        try {
            PagamentoDAO.atualizarPagamento(conn, p);
        } catch (SQLException e) {
            throw new SQLException("Erro ao atualizar pagamento.", e);
        }
    }

    public static Pagamento buscarPagamentoPorId(Connection conn, int id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("ID inválido.");
        }
        try {
            return PagamentoDAO.pagamentoPorId(conn, id);
        } catch (SQLException e) {
            throw new SQLException("Erro ao buscar pagamento pelo ID.", e);
        }
    }

    public static List<Pagamento> listarTodosPagamentos(Connection conn) throws SQLException {
        try {
            return PagamentoDAO.listarTodosPagamentos(conn);
        } catch (SQLException e) {
            throw new SQLException("Erro ao listar pagamentos.", e);
        }
    }

    public static List<Pagamento> listarPagamentosPorVenda(Connection conn, int vendaId) throws SQLException {
        if (vendaId <= 0) {
            throw new IllegalArgumentException("ID da venda inválido.");
        }
        try {
            return PagamentoDAO.listarPagamentosPorVenda(conn, vendaId);
        } catch (SQLException e) {
            throw new SQLException("Erro ao listar pagamentos por venda.", e);
        }
    }

    public static void excluirPagamento(Connection conn, Pagamento p) throws SQLException {
        if (p == null || p.getId() <= 0) {
            throw new IllegalArgumentException("Pagamento inválido.");
        }
        try {
            PagamentoDAO.deletarPagamento(conn, p);
        } catch (SQLException e) {
            throw new SQLException("Erro ao excluir pagamento.", e);
        }
    }
}