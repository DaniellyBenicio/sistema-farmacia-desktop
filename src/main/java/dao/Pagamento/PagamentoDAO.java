package dao.Pagamento;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dao.Venda.VendaDAO;
import models.Pagamento.Pagamento;
import models.Pagamento.Pagamento.FormaPagamento; 
import models.Venda.Venda;

public class PagamentoDAO {

    public static void cadastrarPagamento(Connection conn, Pagamento p) throws SQLException {
        p.setVendaId(p.getVendaId());
        p.setFormaPagamento(p.getFormaPagamento()); 
        p.setValorPago(p.getValorPago()); 

        Venda venda = VendaDAO.buscarVendaPorId(conn, p.getVendaId());
        if (venda == null) {
            throw new SQLException("A venda associada ao pagamento não existe.");
        }

        BigDecimal totalPagoExistente = calcularTotalPagoPorVenda(conn, p.getVendaId());
        BigDecimal novoTotalPago = totalPagoExistente.add(p.getValorPago());

        if (novoTotalPago.compareTo(venda.getValorTotal()) > 0) {
            throw new SQLException("O total pago excede o valor total da venda.");
        }

        String sql = "insert into pagamento (venda_id, formaPagamento, valorPago) values (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, p.getVendaId());
            pstmt.setString(2, p.getFormaPagamento().name()); 
            pstmt.setBigDecimal(3, p.getValorPago());
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    p.setId(rs.getInt(1));
                }
            }
            System.out.println("Pagamento cadastrado com sucesso!");
        } catch (SQLException e) {
            System.err.println("Erro ao cadastrar pagamento: " + e.getMessage());
            throw e;
        }
    }

    public static void atualizarPagamento(Connection conn, Pagamento p) throws SQLException {
        p.setId(p.getId()); 
        p.setVendaId(p.getVendaId()); 
        p.setFormaPagamento(p.getFormaPagamento()); 
        p.setValorPago(p.getValorPago()); 

        if (pagamentoPorId(conn, p.getId()) == null) {
            throw new SQLException("Pagamento com ID " + p.getId() + " não encontrado.");
        }

        Venda venda = VendaDAO.buscarVendaPorId(conn, p.getVendaId());
        if (venda == null) {
            throw new SQLException("A venda associada ao pagamento não existe.");
        }

        BigDecimal totalPagoExistente = calcularTotalPagoPorVendaExcluindoId(conn, p.getVendaId(), p.getId());
        BigDecimal novoTotalPago = totalPagoExistente.add(p.getValorPago());

        if (novoTotalPago.compareTo(venda.getValorTotal()) > 0) {
            throw new SQLException("O total pago excede o valor total da venda.");
        }

        String sql = "update pagamento set venda_id = ?, formaPagamento = ?, valorPago = ? where id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, p.getVendaId());
            pstmt.setString(2, p.getFormaPagamento().name());
            pstmt.setBigDecimal(3, p.getValorPago());
            pstmt.setInt(4, p.getId());
            pstmt.executeUpdate();
            System.out.println("Pagamento atualizado com sucesso!");
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar pagamento: " + e.getMessage());
            throw e;
        }
    }

    public static Pagamento pagamentoPorId(Connection conn, int id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("O ID deve ser um valor positivo.");
        }

        String sql = "SELECT id, venda_id, formaPagamento, valorPago from pagamento where id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Pagamento p = new Pagamento();
                    p.setId(rs.getInt("id"));
                    p.setVendaId(rs.getInt("venda_id"));
                    p.setFormaPagamento(FormaPagamento.valueOf(rs.getString("formaPagamento")));
                    p.setValorPago(rs.getBigDecimal("valorPago"));
                    return p;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar pagamento por ID: " + e.getMessage());
            throw e;
        }
        return null;
    }

    public static List<Pagamento> listarTodosPagamentos(Connection conn) throws SQLException {
        String sql = "SELECT id, venda_id, formaPagamento, valorPago FROM pagamento ORDER BY id ASC";
        List<Pagamento> pagamentos = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (!rs.isBeforeFirst()) {
                System.out.println("Nenhum pagamento encontrado.");
            }
            while (rs.next()) {
                Pagamento p = new Pagamento();
                p.setId(rs.getInt("id"));
                p.setVendaId(rs.getInt("venda_id"));
                p.setFormaPagamento(FormaPagamento.valueOf(rs.getString("formaPagamento")));
                p.setValorPago(rs.getBigDecimal("valorPago"));
                pagamentos.add(p);
            }
        }
        return pagamentos;
    }

    public static List<Pagamento> listarPagamentosPorVenda(Connection conn, int vendaId) throws SQLException {
        if (vendaId <= 0) {
            throw new IllegalArgumentException("O ID da venda deve ser um valor positivo.");
        }

        String sql = "SELECT id, venda_id, formaPagamento, valorPago from pagamento where venda_id = ?";
        List<Pagamento> pagamentos = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, vendaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Pagamento p = new Pagamento();
                    p.setId(rs.getInt("id"));
                    p.setVendaId(rs.getInt("venda_id"));
                    p.setFormaPagamento(FormaPagamento.valueOf(rs.getString("formaPagamento")));
                    p.setValorPago(rs.getBigDecimal("valorPago"));
                    pagamentos.add(p);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar pagamentos por venda: " + e.getMessage());
            throw e;
        }
        return pagamentos;
    }

    public static void deletarPagamento(Connection conn, Pagamento p) throws SQLException {
        p.setId(p.getId()); 

        Pagamento existente = pagamentoPorId(conn, p.getId());
        if (existente == null) {
            throw new SQLException("Pagamento com ID " + p.getId() + " não encontrado.");
        }

        BigDecimal totalPagoExistente = calcularTotalPagoPorVendaExcluindoId(conn, p.getVendaId(), p.getId());
        Venda venda = VendaDAO.buscarVendaPorId(conn, p.getVendaId());
        if (venda != null && totalPagoExistente.compareTo(venda.getValorTotal()) < 0) {
            throw new SQLException("Não é possível excluir o pagamento: o total pago restante seria menor que o valor total da venda.");
        }

        String sql = "DELETE FROM pagamento WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, p.getId());
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Pagamento excluído com sucesso!");
            } else {
                System.out.println("Nenhum pagamento encontrado para o ID " + p.getId());
            }
        } catch (SQLException e) {
            System.err.println("Erro ao excluir pagamento: " + e.getMessage());
            throw e;
        }
    }

    public static BigDecimal calcularTotalPagoPorVenda(Connection conn, int vendaId) throws SQLException {
        if (vendaId <= 0) {
            throw new IllegalArgumentException("O ID da venda deve ser um valor positivo.");
        }

        String sql = "SELECT SUM(valorPago) as total FROM pagamento WHERE venda_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, vendaId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    BigDecimal total = rs.getBigDecimal("total");
                    return total != null ? total : BigDecimal.ZERO;
                }
            }
        }
        return BigDecimal.ZERO;
    }

    public static BigDecimal calcularTotalPagoPorVendaExcluindoId(Connection conn, int vendaId, int pagamentoId) throws SQLException {
        if (vendaId <= 0) {
            throw new IllegalArgumentException("O ID da venda deve ser um valor positivo.");
        }
        if (pagamentoId <= 0) {
            throw new IllegalArgumentException("O ID do pagamento deve ser um valor positivo.");
        }

        String sql = "SELECT SUM(valorPago) as total FROM pagamento WHERE venda_id = ? AND id != ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, vendaId);
            ps.setInt(2, pagamentoId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    BigDecimal total = rs.getBigDecimal("total");
                    return total != null ? total : BigDecimal.ZERO;
                }
            }
        }
        return BigDecimal.ZERO;
    }
}