package dao.Relatorio;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import utils.Criptografia;

public class RelatorioVendasDAO {
    private Connection conn;

    public RelatorioVendasDAO(Connection conn) {
        this.conn = conn;
    }

    public static class VendaRelatorio {
        private String dataVenda;
        private String horario;
        private String vendedor;
        private String cpfCliente;
        private String valorTotal;
        private String formasPagamento;
        private String detalhes;

        public VendaRelatorio(String dataVenda, String horario, String vendedor,
                String cpfCliente,
                String valorTotal, String formasPagamento, String detalhes) {
            this.dataVenda = dataVenda;
            this.horario = horario;
            this.vendedor = vendedor;
            this.cpfCliente = cpfCliente;
            this.valorTotal = valorTotal;
            this.formasPagamento = formasPagamento;
            this.detalhes = detalhes;
        }

        public String getDataVenda() {
            return dataVenda;
        }

        public String getHorario() {
            return horario;
        }

        public String getVendedor() {
            return vendedor;
        }

        public String getCpfCliente() {
            return cpfCliente;
        }

        public String getValorTotal() {
            return valorTotal;
        }

        public String getFormasPagamento() {
            return formasPagamento;
        }

        public String getDetalhes() {
            return detalhes;
        }
    }

    public List<VendaRelatorio> buscarRelatorioVendas(String dataFiltro, String vendedorFiltro,
            String pagamentoFiltro, String dataInicioPersonalizada,
            String dataFimPersonalizada) throws SQLException {
        List<VendaRelatorio> vendas = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT DISTINCT v.id, DATE_FORMAT(v.data, '%d/%m/%Y') as data_venda, "
                        + "f.nome as vendedor, c.cpf as cpf_cliente, v.valorTotal, "
                        + "GROUP_CONCAT(DISTINCT p.formaPagamento SEPARATOR ', ') as formasPagamento, "
                        + "TIME(v.data) as horario "
                        + "FROM venda v "
                        + "JOIN funcionario f ON v.funcionario_id = f.id "
                        + "LEFT JOIN cliente c ON v.cliente_id = c.id "
                        + "LEFT JOIN pagamento p ON v.id = p.venda_id "
                        + "WHERE f.status = true ");

        // Adiciona filtros
        // ...

        sql.append(" GROUP BY v.id, data_venda, vendedor, cpf_cliente, valorTotal, horario ");
        sql.append(" ORDER BY v.data DESC, v.id DESC");

        try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int vendaId = rs.getInt("id");
                    String formasPagamento = rs.getString("formasPagamento");
                    String formasFormatadas = formatarFormasPagamento(formasPagamento);
                    String valorFormatado = String.format("R$ %.2f", rs.getDouble("valorTotal")).replace(".", ",");
                    String cpfCliente = rs.getString("cpf_cliente");

                    String detalhes = obterDetalhesVenda(vendaId);

                    vendas.add(new VendaRelatorio(
                            rs.getString("data_venda"),
                            rs.getString("horario"),
                            rs.getString("vendedor"),
                            cpfCliente,
                            valorFormatado,
                            formasFormatadas,
                            detalhes));
                }
            }
        }
        return vendas;
    }

    private String obterDetalhesVenda(int vendaId) {
        StringBuilder detalhes = new StringBuilder();

        try {
            // Obtendo informações da venda
            String sqlVenda = "SELECT v.id AS venda_id, DATE_FORMAT(v.data, '%d/%m/%Y') AS data_venda, "
                    + "TIME(v.data) AS horario, f.nome AS vendedor, c.cpf AS cpf_cliente, "
                    + "v.valorTotal, GROUP_CONCAT(DISTINCT p.formaPagamento SEPARATOR ', ') AS formasPagamento "
                    + "FROM venda v "
                    + "JOIN funcionario f ON v.funcionario_id = f.id "
                    + "LEFT JOIN cliente c ON v.cliente_id = c.id "
                    + "LEFT JOIN pagamento p ON v.id = p.venda_id "
                    + "WHERE v.id = ?";

            try (PreparedStatement stmtVenda = conn.prepareStatement(sqlVenda)) {
                stmtVenda.setInt(1, vendaId);
                try (ResultSet rsVenda = stmtVenda.executeQuery()) {
                    if (rsVenda.next()) {
                        int idVenda = rsVenda.getInt("venda_id");
                        String dataVenda = rsVenda.getString("data_venda");
                        String horarioVenda = rsVenda.getString("horario");
                        String nomeVendedor = rsVenda.getString("vendedor");
                        String cpfCliente = rsVenda.getString("cpf_cliente");
                        double valorTotal = rsVenda.getDouble("valorTotal");
                        String formasPagamento = rsVenda.getString("formasPagamento");

                        detalhes.append("ID da Venda: ").append(idVenda).append("\n")
                                .append("Data da Venda: ").append(dataVenda).append("\n")
                                .append("Horário da Venda: ").append(horarioVenda).append("\n")
                                .append("Nome do Vendedor: ").append(nomeVendedor).append("\n");

                        if (cpfCliente != null && !cpfCliente.isEmpty()) {
                            // Descriptografamos o CPF do cliente
                            // String cpfDescriptografado = Criptografia.descriptografar(cpfCliente);
                            // detalhes.append("CPF do Cliente: ").append(cpfDescriptografado).append("\n");
                        } else {
                            detalhes.append("CPF do Cliente: Não identificado\n");
                        }

                        detalhes.append("Valor Total: R$ ").append(String.format("%.2f", valorTotal)).append("\n")
                                .append("Formas de Pagamento: ")
                                .append(formasPagamento != null ? formasPagamento : "Não informado").append("\n\n");
                    }
                }
            }

            // Obtendo medicamentos
            String sqlMedicamentos = "SELECT m.id, m.nome, m.formaFarmaceutica, m.dosagem, "
                    + "m.embalagem, m.qntEmbalagem, iv.qnt, iv.precoUnit, iv.desconto "
                    + "FROM itemVenda iv "
                    + "JOIN medicamento m ON iv.medicamento_id = m.id " // Alterado para JOIN
                    + "WHERE iv.venda_id = ?";

            try (PreparedStatement stmtMedicamentos = conn.prepareStatement(sqlMedicamentos)) {
                stmtMedicamentos.setInt(1, vendaId);
                try (ResultSet rsMedicamentos = stmtMedicamentos.executeQuery()) {
                    while (rsMedicamentos.next()) {
                        int idMedicamento = rsMedicamentos.getInt("id");
                        String nomeMedicamento = rsMedicamentos.getString("nome");
                        String formaFarmaceutica = rsMedicamentos.getString("formaFarmaceutica");
                        String dosagem = rsMedicamentos.getString("dosagem");
                        String embalagem = rsMedicamentos.getString("embalagem");
                        int qntEmbalagem = rsMedicamentos.getInt("qntEmbalagem");
                        int quantidade = rsMedicamentos.getInt("qnt");
                        double precoUnit = rsMedicamentos.getDouble("precoUnit");
                        double desconto = rsMedicamentos.getDouble("desconto");

                        if (nomeMedicamento != null) {
                            detalhes.append("Código do Medicamento: ").append(idMedicamento).append("\n")
                                    .append("Descrição: ").append(nomeMedicamento)
                                    .append(" ").append(formaFarmaceutica)
                                    .append(" ").append(dosagem)
                                    .append(" ").append(embalagem)
                                    .append(" ").append(qntEmbalagem).append(" UN\n");
                            detalhes.append("Quantidade: ").append(quantidade).append("\n")
                                    .append("Valor Unitário: R$ ").append(String.format("%.2f", precoUnit)).append("\n")
                                    .append("Desconto: R$ ").append(String.format("%.2f", desconto)).append("\n")
                                    .append("Valor Total: R$ ")
                                    .append(String.format("%.2f", (precoUnit * quantidade - desconto))).append("\n\n");
                        }
                    }
                }
            }

            // Obtendo produtos
            String sqlProdutos = "SELECT p.id, p.nome, p.embalagem, p.qntMedida, iv.qnt, iv.precoUnit, iv.desconto "
                    + "FROM itemVenda iv "
                    + "JOIN produto p ON iv.produto_id = p.id "
                    + "WHERE iv.venda_id = ?";

            try (PreparedStatement stmtProdutos = conn.prepareStatement(sqlProdutos)) {
                stmtProdutos.setInt(1, vendaId);
                try (ResultSet rsProdutos = stmtProdutos.executeQuery()) {
                    while (rsProdutos.next()) {
                        int idProduto = rsProdutos.getInt("id");
                        String nomeProduto = rsProdutos.getString("nome");
                        String embalagem = rsProdutos.getString("embalagem");
                        String qntMedida = rsProdutos.getString("qntMedida");
                        int quantidade = rsProdutos.getInt("qnt");
                        double precoUnit = rsProdutos.getDouble("precoUnit");
                        double desconto = rsProdutos.getDouble("desconto");

                        if (nomeProduto != null) {
                            detalhes.append("Código do Produto: ").append(idProduto).append("\n")
                                    .append("Descrição: ").append(nomeProduto)
                                    .append(" ").append(embalagem)
                                    .append(" ").append(qntMedida).append("\n");
                            detalhes.append("Quantidade: ").append(quantidade).append("\n")
                                    .append("Valor Unitário: R$ ").append(String.format("%.2f", precoUnit)).append("\n")
                                    .append("Desconto: R$ ").append(String.format("%.2f", desconto)).append("\n")
                                    .append("Valor Total: R$ ")
                                    .append(String.format("%.2f", (precoUnit * quantidade - desconto))).append("\n\n");
                        }
                    }
                }
            }

        } catch (SQLException e) {
            detalhes.append("Erro ao obter detalhes da venda: ").append(e.getMessage());
        }

        if (detalhes.length() == 0) {
            detalhes.append("Nenhum item encontrado para esta venda.");
        }

        return detalhes.toString();
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
        if (formaPagamento == null)
            return "Não informado";
        switch (formaPagamento) {
            case "DINHEIRO":
                return "Dinheiro";
            case "CARTAO_CREDITO":
                return "Cartão de Crédito";
            case "CARTAO_DEBITO":
                return "Cartão de Débito";
            case "PIX":
                return "PIX";
            default:
                return formaPagamento;
        }
    }
}