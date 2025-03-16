package dao.ItemVenda;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import models.ItemVenda.ItemVenda;
import models.Medicamento.Medicamento;
import models.Produto.Produto;

public class ItemVendaDAO {
    public void inserirItemVenda(Connection conn, ItemVenda iv) throws SQLException {
        String sql = "insert into itemVenda (venda_id, produto_id, medicamento_id, qnt, precoUnit, subtotal, desconto) VALUES (?, ?, ?, ?, ?, ?, ?)";
    
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, iv.getVendaId());
            pstmt.setObject(2, (iv.getProduto() != null) ? iv.getProduto().getId() : null);
            pstmt.setObject(3, (iv.getMedicamento() != null) ? iv.getMedicamento().getId() : null);
            pstmt.setInt(4, iv.getQnt());
            pstmt.setBigDecimal(5, iv.getPrecoUnit());
            pstmt.setBigDecimal(6, iv.getSubtotal());
            pstmt.setBigDecimal(7, iv.getDesconto());
            
    
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir ItemVenda no banco de dados.", e);
        }
    }
    
    public void atualizarItemVenda(Connection conn, ItemVenda iv) throws SQLException {
        String sql = "update itemVenda set venda_id = ?, produto_id = ?, medicamento_id = ?, qnt = ?, precoUnit = ?, subtotal = ?, desconto = ? WHERE id = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, iv.getVendaId());
            pstmt.setObject(2, (iv.getProduto() != null) ? iv.getProduto().getId() : null);
            pstmt.setObject(3, (iv.getMedicamento() != null) ? iv.getMedicamento().getId() : null);
            pstmt.setInt(4, iv.getQnt());
            pstmt.setBigDecimal(5, iv.getPrecoUnit());
            pstmt.setBigDecimal(6, iv.getSubtotal());
            pstmt.setBigDecimal(7, iv.getDesconto());
            pstmt.setInt(8, iv.getId());
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar ItemVenda no banco de dados.", e);
        }
    }

    public void excluirItemVenda(Connection conn, int id) throws SQLException {
        String sql = "delete from itemVenda where id = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao excluir ItemVenda no banco de dados.", e);
        }
    }

    public ItemVenda buscarItemVendaPorId(Connection conn, int id) throws SQLException {
        String sql = "SELECT iv.qnt, iv.precoUnit, iv.subtotal, iv.desconto, " +
                    "COALESCE(p.nome, m.nome) AS nome, " +
                    "COALESCE(p.valor, m.valorUnit) AS preco " +
                    "FROM itemVenda iv " +
                    "LEFT JOIN produto p ON iv.produto_id = p.id " +
                    "LEFT JOIN medicamento m ON iv.medicamento_id = m.id " +
                    "WHERE iv.id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    ItemVenda iv = new ItemVenda();
                    iv.setId(rs.getInt("id"));
                    iv.setQnt(rs.getInt("qnt"));
                    iv.setPrecoUnit(rs.getBigDecimal("precoUnit"));
                    iv.setSubtotal(rs.getBigDecimal("subtotal"));
                    iv.setDesconto(rs.getBigDecimal("desconto"));
    
                    String nome = rs.getString("nome");
                    BigDecimal preco = rs.getBigDecimal("preco");     
                    if (rs.getObject("produto_id") != null) {
                        Produto produto = new Produto();
                        produto.setNome(nome);
                        produto.setValor(preco);
                        iv.setProduto(produto);
                    }
                    
                    if (rs.getObject("medicamento_id") != null) {
                        Medicamento medicamento = new Medicamento();
                        medicamento.setNome(nome);
                        medicamento.setValorUnit(preco);
                        iv.setMedicamento(medicamento);
                    }
    
                    return iv;  
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar ItemVenda por ID no banco de dados.", e);
        }
    
        return null; 
    }

    public List<ItemVenda> buscarTodosItemVendas(Connection conn) throws SQLException {
        List<ItemVenda> itens = new ArrayList<>();
        String sql = "SELECT iv.qnt, iv.precoUnit, iv.subtotal, iv.desconto, " +
                    "COALESCE(p.nome, m.nome) AS nome, " +
                    "COALESCE(p.valor, m.valorUnit) AS preco " +
                    "FROM itemVenda iv " +
                    "LEFT JOIN produto p ON iv.produto_id = p.id " +
                    "LEFT JOIN medicamento m ON iv.medicamento_id = m.id";

        try (PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                ItemVenda iv = new ItemVenda();
                iv.setQnt(rs.getInt("qnt"));
                iv.setPrecoUnit(rs.getBigDecimal("precoUnit"));
                iv.setSubtotal(rs.getBigDecimal("subtotal"));
                iv.setDesconto(rs.getBigDecimal("desconto"));

                String nome = rs.getString("nome");
                BigDecimal preco = rs.getBigDecimal("preco");

                if (nome != null) {
                    Produto produto = new Produto();
                    produto.setNome(nome);
                    produto.setValor(preco);
                    iv.setProduto(produto);
                }

                if (nome != null) {
                    Medicamento medicamento = new Medicamento();
                    medicamento.setNome(nome);
                    medicamento.setValorUnit(preco);
                    iv.setMedicamento(medicamento);
                }

                itens.add(iv);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar todos os itens de venda.", e);
        }

        return itens;
    }

    public List<ItemVenda> buscarItensPorVenda(Connection conn, int vendaId) throws SQLException {
        List<ItemVenda> itens = new ArrayList<>();
        String sql = "SELECT iv.qnt, iv.precoUnit, iv.subtotal, iv.desconto, " +
                     "COALESCE(p.nome, m.nome) AS nome, " +
                     "COALESCE(p.valor, m.valorUnit) AS preco " +
                     "FROM itemVenda iv " +
                     "LEFT JOIN produto p ON iv.produto_id = p.id " +
                     "LEFT JOIN medicamento m ON iv.medicamento_id = m.id " +
                     "WHERE iv.venda_id = ?";
    
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, vendaId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ItemVenda iv = new ItemVenda();
                    iv.setQnt(rs.getInt("qnt"));
                    iv.setPrecoUnit(rs.getBigDecimal("precoUnit"));
                    iv.setSubtotal(rs.getBigDecimal("subtotal"));
                    iv.setDesconto(rs.getBigDecimal("desconto"));
    
                    String nome = rs.getString("nome");
                    BigDecimal preco = rs.getBigDecimal("preco");
    
                    if (nome != null) {
                        Produto produto = new Produto();
                        produto.setNome(nome);
                        produto.setValor(preco);
                        iv.setProduto(produto);
                    }
    
                    if (nome != null) {
                        Medicamento medicamento = new Medicamento();
                        medicamento.setNome(nome);
                        medicamento.setValorUnit(preco);
                        iv.setMedicamento(medicamento);
                    }
    
                    itens.add(iv);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar itens de venda por ID de venda.", e);
        }
    
        return itens;
    } 

    public BigDecimal calcularTotalVenda(Connection conn, int vendaId) throws SQLException {
        String sql = "select sum(subtotal) as total from itemVenda WHERE venda_id = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, vendaId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("total");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao calcular total da venda.", e);
        }
        
        return BigDecimal.ZERO; 
    }
}    
