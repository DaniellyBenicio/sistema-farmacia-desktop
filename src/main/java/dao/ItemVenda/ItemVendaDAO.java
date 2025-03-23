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
import models.Medicamento.Medicamento.TipoReceita;
import models.Produto.Produto;

public class ItemVendaDAO {
    public static void inserirItemVenda(Connection conn, ItemVenda iv, String nomeItem) throws SQLException {
        String sql = "INSERT INTO itemVenda (venda_id, produto_id, medicamento_id, qnt, precoUnit, desconto) VALUES (?, ?, ?, ?, ?, ?)";
    
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            calcularDescontoAutomatico(conn, iv);
            pstmt.setInt(1, iv.getVendaId());
            pstmt.setObject(2, iv.getProduto() != null ? iv.getProduto().getId() : null);
            pstmt.setObject(3, iv.getMedicamento() != null ? iv.getMedicamento().getId() : null);
            pstmt.setInt(4, iv.getQnt());
            pstmt.setBigDecimal(5, iv.getPrecoUnit());
            pstmt.setBigDecimal(6, iv.getDesconto());
    
            pstmt.executeUpdate();
            System.out.println("Item '" + nomeItem + "' inserido com sucesso na venda ID " + iv.getVendaId());
        } catch (SQLException e) {
            throw new SQLException("Erro ao inserir ItemVenda no banco de dados para o item '" + nomeItem + "': " + e.getMessage(), e);
        }
    }

    public static void calcularDescontoAutomatico(Connection conn, ItemVenda iv) throws SQLException {
        if (iv.getDesconto() == null || iv.getDesconto().compareTo(BigDecimal.ZERO) == 0) {
            BigDecimal descontoCalculado = BigDecimal.ZERO;

            if (iv.getMedicamento() != null && iv.getMedicamento().getId() > 0) {
                String sql = "SELECT tipo FROM medicamento WHERE id = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setInt(1, iv.getMedicamento().getId());
                    try (ResultSet rs = pstmt.executeQuery()) {
                        if (rs.next()) {
                            String tipo = rs.getString("tipo");
                            switch (tipo) {
                                case "GENÉRICO":
                                case "SIMILAR":
                                    descontoCalculado = iv.getPrecoUnit()
                                            .multiply(BigDecimal.valueOf(iv.getQnt()))
                                            .multiply(new BigDecimal("0.15"));
                                    break;
                                case "ÉTICO":
                                    descontoCalculado = iv.getPrecoUnit()
                                        .multiply(BigDecimal.valueOf(iv.getQnt()))
                                        .multiply(new BigDecimal("0.05"));
                                    break;
                                default:
                                    descontoCalculado = BigDecimal.ZERO;
                            }
                        }
                    }
                } catch (SQLException e) {
                    System.err.println("Erro ao calcular desconto: " + e.getMessage());
                    throw e;
                }
            }

            if (descontoCalculado.compareTo(iv.getSubtotal()) > 0) {
                descontoCalculado = iv.getSubtotal();
            }

            iv.setDesconto(descontoCalculado);
            System.out.println("Desconto aplicado com sucesso. Novo subtotal: " + iv.getSubtotal());
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
        String termoBuscaLike = (termoBusca != null ? termoBusca.toLowerCase() : "") + "%";

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
            System.err.println("Erro ao buscar produtos disponíveis: " + e.getMessage());
        }

        String sqlMedicamentos = "SELECT id, nome, dosagem, formaFarmaceutica, embalagem, qntEmbalagem, valorUnit, tipoReceita "
                +
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
                    medicamento.setEmbalagem(rs.getString("embalagem"));
                    medicamento.setQntEmbalagem(rs.getInt("qntEmbalagem"));
                    medicamento.setValorUnit(rs.getBigDecimal("valorUnit"));
                    String tipoReceitaStr = rs.getString("tipoReceita");
                    try {
                        if (tipoReceitaStr != null) {
                            medicamento.setTipoReceita(Medicamento.TipoReceita.valueOf(tipoReceitaStr.toUpperCase()));
                        } else {
                            medicamento.setTipoReceita(null);
                        }
                    } catch (IllegalArgumentException e) {
                        System.err.println("Tipo de receita inválido no banco de dados: " + tipoReceitaStr
                                + ". Definindo como null.");
                        medicamento.setTipoReceita(null);
                    }
                    itens.add(medicamento);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar medicamentos disponíveis: " + e.getMessage());
        }

        return itens;
    }

    public static boolean verificarTipoEEstoque(Connection conn, ItemVenda iv, int quantidade, boolean isRemocao, String nomeItem)
            throws SQLException {
        String sqlProduto = "SELECT id, qntEstoque FROM produto WHERE LOWER(nome) = LOWER(?)";
        String sqlMedicamento = "SELECT id, qnt, tipoReceita FROM medicamento WHERE LOWER(nome) = LOWER(?)";

        int qntDisponivel = -1;
        boolean isProduto = false;

        // Primeiro, tenta encontrar o item como produto
        try (PreparedStatement pstmt = conn.prepareStatement(sqlProduto)) {
            pstmt.setString(1, nomeItem);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    qntDisponivel = rs.getInt("qntEstoque");
                    int idProduto = rs.getInt("id");
                    isProduto = true;
                    System.out.println("Produto encontrado. Nome: " + nomeItem + ", ID: " + idProduto + ", Estoque inicial: " + qntDisponivel);

                    if (isRemocao) {
                        qntDisponivel += quantidade;
                        Produto produto = new Produto();
                        produto.setId(idProduto);
                        iv.setProduto(produto);
                        iv.setQnt(quantidade);
                        atualizarEstoqueProduto(conn, idProduto, qntDisponivel);
                        System.out.println("Estoque restaurado de produto ID " + idProduto + ": " + qntDisponivel);
                        return true;
                    } else if (qntDisponivel >= quantidade) {
                        Produto produto = new Produto();
                        produto.setId(idProduto);
                        iv.setProduto(produto);
                        iv.setQnt(quantidade);
                        qntDisponivel -= quantidade;
                        atualizarEstoqueProduto(conn, idProduto, qntDisponivel);
                        System.out.println("Estoque atualizado de produto ID " + idProduto + ": " + qntDisponivel);
                        return true;
                    } else {
                        System.out.println("Estoque insuficiente para o produto: " + nomeItem);
                        return false;
                    }
                }
            }
        }

        // Se não for produto, tenta encontrar como medicamento
        if (!isProduto) {
            try (PreparedStatement pstmt = conn.prepareStatement(sqlMedicamento)) {
                pstmt.setString(1, nomeItem);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        qntDisponivel = rs.getInt("qnt");
                        int idMedicamento = rs.getInt("id");
                        String tipoReceitaStr = rs.getString("tipoReceita");
                        System.out.println("Medicamento encontrado. Nome: " + nomeItem + ", ID: " + idMedicamento + ", Estoque inicial: " + qntDisponivel);

                        Medicamento medicamento = new Medicamento();
                        medicamento.setId(idMedicamento);
                        if (tipoReceitaStr != null && !tipoReceitaStr.trim().isEmpty()) {
                            try {
                                medicamento.setTipoReceita(TipoReceita.valueOf(tipoReceitaStr.toUpperCase()));
                            } catch (IllegalArgumentException e) {
                                throw new SQLException("Tipo de receita inválido para " + nomeItem + ": " + tipoReceitaStr, e);
                            }
                        }
                        iv.setMedicamento(medicamento);

                        if (isRemocao) {
                            qntDisponivel += quantidade;
                            iv.setQnt(quantidade);
                            atualizarEstoqueMedicamento(conn, idMedicamento, qntDisponivel);
                            System.out.println("Estoque restaurado de medicamento ID " + idMedicamento + ": " + qntDisponivel);
                            return true;
                        } else if (qntDisponivel >= quantidade) {
                            iv.setQnt(quantidade);
                            qntDisponivel -= quantidade;
                            atualizarEstoqueMedicamento(conn, idMedicamento, qntDisponivel);
                            System.out.println("Estoque atualizado de medicamento ID " + idMedicamento + ": " + qntDisponivel);
                            return true;
                        } else {
                            System.out.println("Estoque insuficiente para o medicamento: " + nomeItem);
                            return false;
                        }
                    }
                }
            }
        }

        throw new SQLException("Item '" + nomeItem + "' não encontrado no banco de dados.");
    }

    private static void atualizarEstoqueProduto(Connection conn, int id, int novaQuantidade) throws SQLException {
        String sql = "UPDATE produto SET qntEstoque = ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, novaQuantidade);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        }
    }

    private static void atualizarEstoqueMedicamento(Connection conn, int id, int novaQuantidade) throws SQLException {
        String sql = "UPDATE medicamento SET qnt = ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, novaQuantidade);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        }
    }

    // Outras funções permanecem inalteradas, mas aqui está um exemplo para consistência
    public static String verificarTipoItem(Connection conn, String nomeItem) throws SQLException {
        String sqlMedicamento = "SELECT id FROM medicamento WHERE LOWER(nome) = LOWER(?)";
        String sqlProduto = "SELECT id FROM produto WHERE LOWER(nome) = LOWER(?)";

        try (PreparedStatement stmtMedicamento = conn.prepareStatement(sqlMedicamento)) {
            stmtMedicamento.setString(1, nomeItem);
            try (ResultSet rs = stmtMedicamento.executeQuery()) {
                if (rs.next()) {
                    return "Medicamento";
                }
            }
        }

        try (PreparedStatement stmtProduto = conn.prepareStatement(sqlProduto)) {
            stmtProduto.setString(1, nomeItem);
            try (ResultSet rs = stmtProduto.executeQuery()) {
                if (rs.next()) {
                    return "Produto";
                }
            }
        }

        return "ID não encontrado";
    }

    public static boolean verificarNecessidadeReceita(Connection conn, String nomeItem) throws SQLException {
        String sql = "SELECT tipoReceita FROM medicamento WHERE LOWER(nome) = LOWER(?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nomeItem);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String tipoReceita = rs.getString("tipoReceita");
                    return tipoReceita != null && !tipoReceita.trim().isEmpty();
                }
            }
        }
        return false;
    }
    
}