package dao.Venda;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import dao.Cliente.ClienteDAO;
import dao.Funcionario.FuncionarioDAO;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import models.Venda.Venda;

public class VendaDAO {
    public static boolean verificarVendaExistente(Connection conn, Venda v) throws SQLException {
        String sqlVerificar = "select id from venda where cliente_id = ? and funcionario_id = ? and data = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sqlVerificar)) {
            if (v.getCliente() != null) {
                pstmt.setInt(1, v.getCliente().getId());
            } else {
                pstmt.setNull(1, Types.INTEGER);
            }
            
            pstmt.setInt(2, v.getFuncionario().getId());
            pstmt.setTimestamp(3, Timestamp.valueOf(v.getData()));
    
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next(); 
            }
        }
    }

    public static void realizarVenda(Connection conn, Venda v) throws SQLException {
        if (v.getCliente().getCpf() != null && !v.getCliente().getCpf().isEmpty()) {
            Integer clienteId = ClienteDAO.buscarClientePorCpfRetornaId(conn, v.getCliente().getCpf());
            if (clienteId != null) {
                v.getCliente().setId(clienteId);
            } else {
                System.out.println("Cliente não encontrado.");
                return; 
            }
        } else {
            v.setCliente(null);
        }    

        if (v.getFuncionario() == null) {
            return;
        }

        Integer funcionarioId = FuncionarioDAO.verificarFuncionarioPorId(conn, v.getFuncionario().getId());
        if (funcionarioId == null) {
            System.out.println("Funcionário não encontrado.");
            return;
        }
        
        if (verificarVendaExistente(conn, v)) {
            System.out.println("Venda já existente para este cliente, funcionário e data!");
            return;
        }

        if (v.getCliente() != null) { 
            BigDecimal descontoCalculado = BigDecimal.ZERO;
            
            if (v.getValorTotal().compareTo(new BigDecimal("60")) <= 0) {
                descontoCalculado = v.getValorTotal().multiply(new BigDecimal("0.05"));
            } else if (v.getValorTotal().compareTo(new BigDecimal("60")) > 0 && v.getValorTotal().compareTo(new BigDecimal("150")) <= 0) {
                descontoCalculado = v.getValorTotal().multiply(new BigDecimal("0.10"));
            } else if (v.getValorTotal().compareTo(new BigDecimal("150")) > 0) {
                descontoCalculado = v.getValorTotal().multiply(new BigDecimal("0.15"));
            }
            if (v.getDesconto() == null || v.getDesconto().compareTo(descontoCalculado) < 0) {
                v.setDesconto(descontoCalculado);
            }
        } else {
            v.setDesconto(BigDecimal.ZERO);
        }        

        String sqlInserir = "insert into venda (cliente_id, funcionario_id, valorTotal, desconto, formaPagamento, data) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sqlInserir)) {
            if (v.getCliente() != null) {
                pstmt.setInt(1, v.getCliente().getId());
            } else {
                pstmt.setNull(1, Types.INTEGER);
            }
            
            pstmt.setInt(2, v.getFuncionario().getId());
            pstmt.setBigDecimal(3, v.getValorTotal());
            pstmt.setBigDecimal(4, v.getDesconto());
            pstmt.setString(5, v.getFormaPagamento().name());
            pstmt.setTimestamp(6, Timestamp.valueOf(v.getData()));

            int linhasAfetadas = pstmt.executeUpdate();
            if (linhasAfetadas > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int idVenda = rs.getInt(1);
                        v.setId(idVenda); 
                        System.out.println("Venda registrada com sucesso! ID: " + idVenda);
                    }
                }
            } else {
                System.out.println("Erro ao registrar a venda.");
            }
        }
    }

    public static Venda buscarVendaPorId(Connection conn, int id) throws SQLException {
        String sql = "select * from Venda where id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Venda v = new Venda();
                    v.setValorTotal(rs.getBigDecimal("valorTotal"));
                    v.setDesconto(rs.getBigDecimal("desconto"));
                    v.setFormaPagamento(Venda.FormaPagamento.valueOf(rs.getString("formaPagamento")));
                    v.setData(rs.getTimestamp("data").toLocalDateTime());
                    return v;
                }
            }
        }
        return null;
    } 
    
    public static List<Venda> listarTodasVendas(Connection conn) throws SQLException {
        String sql = "select * from venda";
        List<Venda> vendas = new ArrayList<>();
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Venda v = new Venda();
                v.setId(rs.getInt("id"));
                v.setValorTotal(rs.getBigDecimal("valorTotal"));
                v.setDesconto(rs.getBigDecimal("desconto"));
                v.setFormaPagamento(Venda.FormaPagamento.valueOf(rs.getString("formaPagamento")));
                v.setData(rs.getTimestamp("data").toLocalDateTime());
                vendas.add(v);
            }
        }
        return vendas;
    }

    public static boolean excluirVenda(Connection conn, int id) throws SQLException {
        String sql = "delete from venda where id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        }
    }

    public static void editarVenda(Connection conn, Venda v) throws SQLException {
        String sql = "update venda set funcionario_id = ?, valorTotal = ?, desconto = ?, formaPagamento = ?, data = ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, v.getFuncionario().getId());
            pstmt.setBigDecimal(2, v.getValorTotal());
            pstmt.setBigDecimal(3, v.getDesconto());
            pstmt.setString(4, v.getFormaPagamento().name());
            pstmt.setTimestamp(5, Timestamp.valueOf(v.getData()));
            pstmt.setInt(6, v.getId());
    
            int linhasAfetadas = pstmt.executeUpdate();
            if (linhasAfetadas == 0) {
                throw new SQLException("Nenhuma venda foi atualizada. ID inexistente: " + v.getId());
            }
        }
    }    
}
