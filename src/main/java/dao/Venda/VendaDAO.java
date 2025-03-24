package dao.Venda;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import dao.Funcionario.FuncionarioDAO;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import models.Venda.Venda;

public class VendaDAO {

    public static boolean verificarVendaExistente(Connection conn, Venda v) throws SQLException {
        String sqlVerificar = "SELECT id FROM venda WHERE cliente_id = ? AND funcionario_id = ? AND data = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sqlVerificar)) {
            if (v.getClienteId() != null) {
                pstmt.setInt(1, v.getClienteId());
            } else {
                pstmt.setNull(1, Types.INTEGER);
            }
            pstmt.setInt(2, v.getFuncionarioId());
            pstmt.setTimestamp(3, Timestamp.valueOf(v.getData()));

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public static int realizarVenda(Connection conn, Venda v) throws SQLException {
        Integer funcionarioId = FuncionarioDAO.verificarFuncionarioPorId(conn, v.getFuncionarioId());
        if (funcionarioId == null) {
            System.out.println("Funcionário não encontrado para o ID: " + v.getFuncionarioId());
            return -1;
        }

        if (verificarVendaExistente(conn, v)) {
            System.out.println("Venda já existente para este cliente, funcionário e data!");
            return -1;
        }

        BigDecimal descontoCalculado = BigDecimal.ZERO;
        if (v.getClienteId() != null) {
            if (v.getValorTotal().compareTo(new BigDecimal("60")) <= 0) {
                descontoCalculado = v.getValorTotal().multiply(new BigDecimal("0.05"));
            } else if (v.getValorTotal().compareTo(new BigDecimal("60")) > 0
                    && v.getValorTotal().compareTo(new BigDecimal("150")) <= 0) {
                descontoCalculado = v.getValorTotal().multiply(new BigDecimal("0.10"));
            } else if (v.getValorTotal().compareTo(new BigDecimal("150")) > 0) {
                descontoCalculado = v.getValorTotal().multiply(new BigDecimal("0.15"));
            }
            if (v.getDesconto() == null || v.getDesconto().compareTo(descontoCalculado) < 0) {
                v.setDesconto(descontoCalculado);
            }
        } else {
            if (v.getDesconto() == null) {
                v.setDesconto(BigDecimal.ZERO);
            }
        }

        String sqlInserir = "INSERT INTO venda (cliente_id, funcionario_id, valorTotal, desconto, data) "
                +
                "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sqlInserir, PreparedStatement.RETURN_GENERATED_KEYS)) {
            if (v.getClienteId() != null) {
                pstmt.setInt(1, v.getClienteId());
            } else {
                pstmt.setNull(1, Types.INTEGER);
            }
            pstmt.setInt(2, v.getFuncionarioId());
            pstmt.setBigDecimal(3, v.getValorTotal());
            pstmt.setBigDecimal(4, v.getDesconto());
            pstmt.setTimestamp(5, Timestamp.valueOf(v.getData()));

            int linhasAfetadas = pstmt.executeUpdate();
            if (linhasAfetadas > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int idVenda = rs.getInt(1);
                        v.setId(idVenda);
                        System.out.println("Venda registrada com sucesso! ID: " + idVenda);
                        return idVenda;
                    }
                }
            }
            System.out.println("Erro ao registrar a venda.");
            return -1;
        }
    }

    public static Venda buscarVendaPorId(Connection conn, int id) throws SQLException {
        String sql = "SELECT * FROM venda WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Venda v = new Venda();
                    v.setId(rs.getInt("id"));
                    v.setClienteId(rs.getObject("cliente_id") != null ? rs.getInt("cliente_id") : null);
                    v.setFuncionarioId(rs.getInt("funcionario_id"));
                    v.setValorTotal(rs.getBigDecimal("valor_total"));
                    v.setDesconto(rs.getBigDecimal("desconto"));
                    v.setData(rs.getTimestamp("data").toLocalDateTime());
                    return v;
                }
            }
        }
        return null;
    }

    public static List<Venda> listarTodasVendas(Connection conn) throws SQLException {
        String sql = "SELECT * FROM venda";
        List<Venda> vendas = new ArrayList<>();
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Venda v = new Venda();
                v.setId(rs.getInt("id"));
                v.setClienteId(rs.getObject("cliente_id") != null ? rs.getInt("cliente_id") : null);
                v.setFuncionarioId(rs.getInt("funcionario_id"));
                v.setValorTotal(rs.getBigDecimal("valor_total"));
                v.setDesconto(rs.getBigDecimal("desconto"));
                v.setData(rs.getTimestamp("data").toLocalDateTime());
                vendas.add(v);
            }
        }
        return vendas;
    }

    public static boolean excluirVenda(Connection conn, int id) throws SQLException {
        String sql = "DELETE FROM venda WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        }
    }

    public static void editarVenda(Connection conn, Venda v) throws SQLException {
        String sql = "UPDATE venda SET cliente_id = ?, funcionario_id = ?, valor_total = ?, desconto = ?, data = ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (v.getClienteId() != null) {
                pstmt.setInt(1, v.getClienteId());
            } else {
                pstmt.setNull(1, Types.INTEGER);
            }
            pstmt.setInt(2, v.getFuncionarioId());
            pstmt.setBigDecimal(3, v.getValorTotal());
            pstmt.setBigDecimal(4, v.getDesconto());
            pstmt.setTimestamp(5, Timestamp.valueOf(v.getData()));
            pstmt.setInt(6, v.getId());

            int linhasAfetadas = pstmt.executeUpdate();
            if (linhasAfetadas == 0) {
                throw new SQLException("Nenhuma venda foi atualizada. ID inexistente: " + v.getId());
            }
        }
    }
}