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
    public static void inserirItemVenda(Connection conn, ItemVenda iv) throws SQLException {
        String sql = "insert into itemVenda (venda_id, produto_id, medicamento_id, qnt, precoUnit, desconto) VALUES (?, ?, ?, ?, ?, ?)";
    
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, iv.getVendaId());
            pstmt.setObject(2, (iv.getProduto() != null) ? iv.getProduto().getId() : null);
            pstmt.setObject(3, (iv.getMedicamento() != null) ? iv.getMedicamento().getId() : null);
            pstmt.setInt(4, iv.getQnt());
            pstmt.setBigDecimal(5, iv.getPrecoUnit());
            pstmt.setBigDecimal(6, iv.getDesconto());
            
    
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir ItemVenda no banco de dados.", e);
        }
    }
    
    public static void atualizarItemVenda(Connection conn, ItemVenda iv) throws SQLException {
        String sql = "update itemVenda set venda_id = ?, produto_id = ?, medicamento_id = ?, qnt = ?, precoUnit = ?, desconto = ? WHERE id = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, iv.getVendaId());
            pstmt.setObject(2, (iv.getProduto() != null) ? iv.getProduto().getId() : null);
            pstmt.setObject(3, (iv.getMedicamento() != null) ? iv.getMedicamento().getId() : null);
            pstmt.setInt(4, iv.getQnt());
            pstmt.setBigDecimal(5, iv.getPrecoUnit());
            pstmt.setBigDecimal(6, iv.getDesconto());
            pstmt.setInt(7, iv.getId());
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar ItemVenda no banco de dados.", e);
        }
    }

    public static void excluirItemVenda(Connection conn, int id) throws SQLException {
        String sql = "delete from itemVenda where id = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao excluir ItemVenda no banco de dados.", e);
        }
    }

    public static ItemVenda buscarItemVendaPorId(Connection conn, int id) throws SQLException {
        String sql = "SELECT iv.*, p.nome AS produto_nome, p.valor AS produto_preco, " +
                    "m.nome AS medicamento_nome, m.valorUnit AS medicamento_preco " +
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
                    iv.setVendaId(rs.getInt("venda_id"));
                    iv.setQnt(rs.getInt("qnt"));
                    iv.setPrecoUnit(rs.getBigDecimal("precoUnit"));
                    iv.setSubtotal(rs.getBigDecimal("subtotal"));
                    iv.setDesconto(rs.getBigDecimal("desconto"));
    
                    Integer produtoId = rs.getObject("produto_id", Integer.class);
                    if (produtoId != null) {
                        Produto produto = new Produto();
                        produto.setId(produtoId);
                        produto.setNome(rs.getString("produto_nome"));
                        produto.setValor(rs.getBigDecimal("produto_preco"));
                        iv.setProduto(produto);
                    } else {
                        Integer medicamentoId = rs.getObject("medicamento_id", Integer.class);
                        if (medicamentoId != null) {
                            Medicamento medicamento = new Medicamento();
                            medicamento.setId(medicamentoId);
                            medicamento.setNome(rs.getString("medicamento_nome"));
                            medicamento.setValorUnit(rs.getBigDecimal("medicamento_preco"));
                            iv.setMedicamento(medicamento);
                        }
                    }
                    return iv;
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Erro ao buscar ItemVenda por ID: " + e.getMessage(), e);
        }
        return null;
    }
    
    public static List<ItemVenda> buscarTodosItemVendas(Connection conn) throws SQLException {
        List<ItemVenda> itens = new ArrayList<>();
        String sql = "SELECT iv.*, p.nome AS produto_nome, p.valor AS produto_preco, " +
                    "m.nome AS medicamento_nome, m.valorUnit AS medicamento_preco " +
                    "FROM itemVenda iv " +
                    "LEFT JOIN produto p ON iv.produto_id = p.id " +
                    "LEFT JOIN medicamento m ON iv.medicamento_id = m.id";
    
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                ItemVenda iv = new ItemVenda();
                iv.setId(rs.getInt("id"));
                iv.setVendaId(rs.getInt("venda_id"));
                iv.setQnt(rs.getInt("qnt"));
                iv.setPrecoUnit(rs.getBigDecimal("precoUnit"));
                iv.setSubtotal(rs.getBigDecimal("subtotal"));
                iv.setDesconto(rs.getBigDecimal("desconto"));
    
                Integer produtoId = rs.getObject("produto_id", Integer.class);
                if (produtoId != null) {
                    Produto produto = new Produto();
                    produto.setId(produtoId);
                    produto.setNome(rs.getString("produto_nome"));
                    produto.setValor(rs.getBigDecimal("produto_preco"));
                    iv.setProduto(produto);
                } else {
                    Integer medicamentoId = rs.getObject("medicamento_id", Integer.class);
                    if (medicamentoId != null) {
                        Medicamento medicamento = new Medicamento();
                        medicamento.setId(medicamentoId);
                        medicamento.setNome(rs.getString("medicamento_nome"));
                        medicamento.setValorUnit(rs.getBigDecimal("medicamento_preco"));
                        iv.setMedicamento(medicamento);
                    }
                }
                itens.add(iv);
            }
        } catch (SQLException e) {
            throw new SQLException("Erro ao buscar todos os itens de venda: " + e.getMessage(), e);
        }
        return itens;
    }
    
    public static List<ItemVenda> buscarItensPorVenda(Connection conn, int vendaId) throws SQLException {
        List<ItemVenda> itens = new ArrayList<>();
        String sql = "SELECT iv.*, p.nome AS produto_nome, p.valor AS produto_preco, " +
                    "m.nome AS medicamento_nome, m.valorUnit AS medicamento_preco " +
                    "FROM itemVenda iv " +
                    "LEFT JOIN produto p ON iv.produto_id = p.id " +
                    "LEFT JOIN medicamento m ON iv.medicamento_id = m.id " +
                    "WHERE iv.venda_id = ?";
    
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, vendaId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ItemVenda iv = new ItemVenda();
                    iv.setId(rs.getInt("id"));
                    iv.setVendaId(rs.getInt("venda_id"));
                    iv.setQnt(rs.getInt("qnt"));
                    iv.setPrecoUnit(rs.getBigDecimal("precoUnit"));
                    iv.setSubtotal(rs.getBigDecimal("subtotal"));
                    iv.setDesconto(rs.getBigDecimal("desconto"));
    
                    Integer produtoId = rs.getObject("produto_id", Integer.class);
                    if (produtoId != null) {
                        Produto produto = new Produto();
                        produto.setId(produtoId);
                        produto.setNome(rs.getString("produto_nome"));
                        produto.setValor(rs.getBigDecimal("produto_preco"));
                        iv.setProduto(produto);
                    } else {
                        Integer medicamentoId = rs.getObject("medicamento_id", Integer.class);
                        if (medicamentoId != null) {
                            Medicamento medicamento = new Medicamento();
                            medicamento.setId(medicamentoId);
                            medicamento.setNome(rs.getString("medicamento_nome"));
                            medicamento.setValorUnit(rs.getBigDecimal("medicamento_preco"));
                            iv.setMedicamento(medicamento);
                        }
                    }
                    itens.add(iv);
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Erro ao buscar itens por venda: " + e.getMessage(), e);
        }
        return itens;
    }

    public static BigDecimal calcularTotalVenda(Connection conn, int vendaId) throws SQLException {
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

    public static List<Object> buscarTodosItensDisponiveis(Connection conn, String termoBusca) throws SQLException {
        List<Object> itens = new ArrayList<>();
        String termoBuscaLike = "%" + (termoBusca != null ? termoBusca.toLowerCase() : "") + "%";


        String sqlProdutos = "SELECT id, nome, valor, embalagem, qntEmbalagem, qntMedida " +
                            "FROM produto " +
                            "WHERE qntEstoque > 0 AND LOWER(nome) LIKE ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sqlProdutos)) {
            pstmt.setString(1, termoBuscaLike);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Produto produto = new Produto();
                    produto.setId(rs.getInt("id"));
                    produto.setNome(rs.getString("nome"));
                    produto.setValor(rs.getBigDecimal("valor"));
                    produto.setEmbalagem(rs.getString("embalagem"));
                    produto.setQntEmbalagem(rs.getInt("qntEmbalagem"));
                    produto.setQntMedida(rs.getString("qntMedida"));
                    itens.add(produto);
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Erro ao buscar produtos disponíveis: " + e.getMessage(), e);
        }

        String sqlMedicamentos = "SELECT id, nome, dosagem, formaFarmaceutica, valorUnit, " +
                                "FROM medicamento " +
                                "WHERE qnt > 0 AND LOWER(nome) LIKE ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sqlMedicamentos)) {
            pstmt.setString(1, termoBuscaLike);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Medicamento medicamento = new Medicamento();
                    medicamento.setId(rs.getInt("id"));
                    medicamento.setNome(rs.getString("nome"));
                    medicamento.setDosagem(rs.getString("dosagem"));
                    medicamento.setFormaFarmaceutica(rs.getString("formaFarmaceutica"));
                    medicamento.setValorUnit(rs.getBigDecimal("valorUnit"));
                    itens.add(medicamento);
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Erro ao buscar medicamentos disponíveis: " + e.getMessage(), e);
        }

        return itens;
    }
}