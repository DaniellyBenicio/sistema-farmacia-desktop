package dao.Relatorio;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RelatorioVendasDAO {
    private Connection conn;

    public RelatorioVendasDAO(Connection conn) {
        this.conn = conn;
    }

    public static class VendaRelatorio {
        private String dataVenda;
        private String horario;
        private String vendedor;
        private String valorTotal;
        private String formasPagamento;
        private String detalhes;

        public VendaRelatorio(String dataVenda, String horario, String vendedor, 
                              String valorTotal, String formasPagamento, String detalhes) {
            this.dataVenda = dataVenda;
            this.horario = horario;
            this.vendedor = vendedor;
            this.valorTotal = valorTotal;
            this.formasPagamento = formasPagamento;
            this.detalhes = detalhes;
        }

        public String getDataVenda() { return dataVenda; }
        public String getHorario() { return horario; }
        public String getVendedor() { return vendedor; }
        public String getValorTotal() { return valorTotal; }
        public String getFormasPagamento() { return formasPagamento; }
        public String getDetalhes() { return detalhes; }
    }

    public List<VendaRelatorio> buscarRelatorioVendas(String dataFiltro, String vendedorFiltro, 
                                                      String pagamentoFiltro, String dataInicioPersonalizada, 
                                                      String dataFimPersonalizada) throws SQLException {
        List<VendaRelatorio> vendas = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT DISTINCT v.id, DATE_FORMAT(v.data, '%d/%m/%Y') as data_venda, " +
            "f.nome as vendedor, v.valorTotal, " +
            "GROUP_CONCAT(DISTINCT p.formaPagamento SEPARATOR ', ') as formasPagamento, " +
            "TIME(v.data) as horario " +
            "FROM venda v " +
            "JOIN funcionario f ON v.funcionario_id = f.id " +
            "LEFT JOIN pagamento p ON v.id = p.venda_id " +
            "LEFT JOIN itemVenda iv ON iv.venda_id = v.id " +
            "LEFT JOIN medicamento m ON iv.medicamento_id = m.id " +
            "LEFT JOIN produto pr ON iv.produto_id = pr.id " +
            "WHERE f.status = true ");

        if (!dataFiltro.equals("Selecione")) {
            sql.append(aplicarFiltroData(dataFiltro, dataInicioPersonalizada, dataFimPersonalizada));
        }
        if (!vendedorFiltro.equals("Selecione") && !vendedorFiltro.equals("Todos")) {
            if (vendedorFiltro.trim().contains(" ")) {
                sql.append(" AND LOWER(f.nome) = LOWER(?)");
            } else {
                sql.append(" AND LOWER(f.nome) LIKE LOWER(?)");
            }
        }
        if (pagamentoFiltro != null && !pagamentoFiltro.equals("Selecione") && !pagamentoFiltro.equals("Todos")) {
            sql.append(" AND p.formaPagamento = ?");
        }

        sql.append(" GROUP BY v.id, data_venda, vendedor, valorTotal, horario ");
        sql.append(" ORDER BY v.data DESC, v.id DESC");

        try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            int paramIndex = 1;

            if (!vendedorFiltro.equals("Selecione") && !vendedorFiltro.equals("Todos")) {
                if (vendedorFiltro.trim().contains(" ")) {
                    stmt.setString(paramIndex++, vendedorFiltro.trim());
                } else {
                    stmt.setString(paramIndex++, "%" + vendedorFiltro.trim() + "%");
                }
            }
            if (pagamentoFiltro != null && !pagamentoFiltro.equals("Selecione") && !pagamentoFiltro.equals("Todos")) {
                stmt.setString(paramIndex++, pagamentoFiltro);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int vendaId = rs.getInt("id");
                    String formasPagamento = rs.getString("formasPagamento");
                    String formasFormatadas = formatarFormasPagamento(formasPagamento);
                    String valorFormatado = String.format("R$ %.2f", rs.getDouble("valorTotal")).replace(".", ",");
                    String detalhes = obterDetalhesVenda(vendaId);

                    vendas.add(new VendaRelatorio(
                        rs.getString("data_venda"),
                        rs.getString("horario"),
                        rs.getString("vendedor"),
                        valorFormatado,
                        formasFormatadas,
                        detalhes
                    ));
                }
            }
        }

        return vendas;
    }

    private String aplicarFiltroData(String dataFiltro, String dataInicioPersonalizada, String dataFimPersonalizada) {
        SimpleDateFormat sdfDB = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdfInput = new SimpleDateFormat("dd/MM/yyyy");
        sdfInput.setLenient(false);

        switch (dataFiltro) {
            case "Hoje": return " AND DATE(v.data) = CURDATE()";
            case "Ontem": return " AND DATE(v.data) = DATE_SUB(CURDATE(), INTERVAL 1 DAY)";
            case "Esta semana": return " AND YEARWEEK(v.data, 1) = YEARWEEK(CURDATE(), 1)";
            case "Este mês": return " AND MONTH(v.data) = MONTH(CURRENT_DATE()) AND YEAR(v.data) = YEAR(CURRENT_DATE())";
            case "Personalizado":
                try {
                    if (dataInicioPersonalizada != null && dataFimPersonalizada != null) {
                        Date dataInicio = sdfInput.parse(dataInicioPersonalizada);
                        Date dataFim = sdfInput.parse(dataFimPersonalizada);
                        return " AND DATE(v.data) BETWEEN '" + sdfDB.format(dataInicio) + 
                               "' AND '" + sdfDB.format(dataFim) + "'";
                    }
                } catch (Exception e) {
                    throw new IllegalArgumentException("Formato de data inválido no filtro personalizado");
                }
                return "";
            default: return "";
        }
    }

    private String obterDetalhesVenda(int vendaId) throws SQLException {
        StringBuilder detalhes = new StringBuilder();

        String sqlProdutos = "SELECT p.nome, iv.qnt, iv.precoUnit, iv.desconto " +
                            "FROM itemVenda iv " +
                            "LEFT JOIN produto p ON iv.produto_id = p.id " +
                            "WHERE iv.venda_id = ?";
        try (PreparedStatement stmtProdutos = conn.prepareStatement(sqlProdutos)) {
            stmtProdutos.setInt(1, vendaId);
            try (ResultSet rsProdutos = stmtProdutos.executeQuery()) {
                while (rsProdutos.next()) {
                    String nomeProduto = rsProdutos.getString("nome");
                    int quantidade = rsProdutos.getInt("qnt");
                    double precoUnit = rsProdutos.getDouble("precoUnit");
                    double desconto = rsProdutos.getDouble("desconto");

                    if (nomeProduto != null) {
                        detalhes.append("Produto: ").append(nomeProduto)
                                .append(" | Qtd: ").append(quantidade)
                                .append(" | Preço Unit.: R$ ").append(String.format("%.2f", precoUnit))
                                .append(" | Desconto: R$ ").append(String.format("%.2f", desconto))
                                .append("\n");
                    }
                }
            }
        }

        String sqlMedicamentos = "SELECT m.nome, iv.qnt, iv.precoUnit, iv.desconto, m.tipo " +
                                "FROM itemVenda iv " +
                                "LEFT JOIN medicamento m ON iv.medicamento_id = m.id " +
                                "WHERE iv.venda_id = ?";
        try (PreparedStatement stmtMedicamentos = conn.prepareStatement(sqlMedicamentos)) {
            stmtMedicamentos.setInt(1, vendaId);
            try (ResultSet rsMedicamentos = stmtMedicamentos.executeQuery()) {
                while (rsMedicamentos.next()) {
                    String nomeMedicamento = rsMedicamentos.getString("nome");
                    int quantidade = rsMedicamentos.getInt("qnt");
                    double precoUnit = rsMedicamentos.getDouble("precoUnit");
                    double desconto = rsMedicamentos.getDouble("desconto");
                    String tipo = rsMedicamentos.getString("tipo");

                    if (nomeMedicamento != null) {
                        detalhes.append("Medicamento: ").append(nomeMedicamento)
                                .append(" (").append(tipo).append(")")
                                .append(" | Qtd: ").append(quantidade)
                                .append(" | Preço Unit.: R$ ").append(String.format("%.2f", precoUnit))
                                .append(" | Desconto: R$ ").append(String.format("%.2f", desconto))
                                .append("\n");
                    }
                }
            }
        }

        return detalhes.length() > 0 ? detalhes.toString() : "Nenhum item encontrado para esta venda.";
    }

    private String formatarFormasPagamento(String formasPagamento) {
        if (formasPagamento == null || formasPagamento.isEmpty()) {
            return "Não informado";
        }
        String[] pagamentos = formasPagamento.split(", ");
        StringBuilder result = new StringBuilder();
        for (String pagamento : pagamentos) {
            if (result.length() > 0) {
                result.append(" / ");
            }
            result.append(formatarFormaPagamento(pagamento));
        }
        return result.toString();
    }

    private String formatarFormaPagamento(String formaPagamento) {
        if (formaPagamento == null) return "Não informado";
        switch (formaPagamento) {
            case "DINHEIRO": return "Dinheiro";
            case "CARTAO_CREDITO": return "Cartão de Crédito";
            case "CARTAO_DEBITO": return "Cartão de Débito";
            case "PIX": return "PIX";
            default: return formaPagamento;
        }
    }
}