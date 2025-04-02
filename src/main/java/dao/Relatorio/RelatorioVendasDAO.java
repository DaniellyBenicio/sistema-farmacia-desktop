package dao.Relatorio;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

    public List<VendaRelatorio> buscarRelatorioVendas(String tipoDataFiltro, String vendedorFiltro,
            String pagamentoFiltro, String dataInicioPersonalizada,
            String dataFimPersonalizada) throws SQLException {
        List<VendaRelatorio> vendas = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
                "SELECT DISTINCT v.id, DATE_FORMAT(v.data, '%d/%m/%Y') AS data_venda, "
                        + "f.nome AS vendedor, c.cpf AS cpf_cliente, v.valorTotal, "
                        + "GROUP_CONCAT(DISTINCT p.formaPagamento SEPARATOR ', ') AS formasPagamento, "
                        + "TIME(v.data) AS horario "
                        + "FROM venda v "
                        + "JOIN funcionario f ON v.funcionario_id = f.id "
                        + "LEFT JOIN cliente c ON v.cliente_id = c.id "
                        + "LEFT JOIN pagamento p ON v.id = p.venda_id "
                        + "WHERE f.status = true ");

        List<Object> params = new ArrayList<>();

        if (tipoDataFiltro != null) {
            switch (tipoDataFiltro) {
                case "Hoje":
                    sql.append(" AND DATE(v.data) = CURDATE()");
                    break;
                case "Ontem":
                    sql.append(" AND DATE(v.data) = CURDATE() - INTERVAL 1 DAY");
                    break;
                case "Últimos 15 dias":
                    sql.append(" AND v.data >= CURDATE() - INTERVAL 15 DAY");
                    break;
                case "Este mês":
                    sql.append(" AND v.data >= DATE_FORMAT(CURDATE(), '%Y-%m-01')");
                    break;
                case "Personalizado":
                    if (dataInicioPersonalizada != null && !dataInicioPersonalizada.isEmpty() &&
                            dataFimPersonalizada != null && !dataFimPersonalizada.isEmpty()) {
                        java.util.Date startDate = parseDate(dataInicioPersonalizada);
                        java.util.Date endDate = parseDate(dataFimPersonalizada);

                        if (startDate != null && endDate != null) {
                            sql.append(" AND v.data BETWEEN ? AND ?");
                            params.add(new java.sql.Date(startDate.getTime()));
                            params.add(new java.sql.Date(endDate.getTime()));
                        } else {
                            throw new SQLException("Datas personalizadas são inválidas.");
                        }
                    }
                    break;
                default:
                    break;
            }
        }

        if (vendedorFiltro != null && !vendedorFiltro.isEmpty() && !"Selecione".equals(vendedorFiltro)
                && !"Todos".equals(vendedorFiltro)) {
            sql.append(" AND f.nome = ?");
            params.add(vendedorFiltro);
        }

        if (pagamentoFiltro != null && !pagamentoFiltro.isEmpty() && !pagamentoFiltro.equals("Selecione")
                && !pagamentoFiltro.equals("Todos")) {
            sql.append(" AND p.formaPagamento = ?");
            params.add(pagamentoFiltro);
        }

        sql.append(" GROUP BY v.id, data_venda, vendedor, cpf_cliente, valorTotal, horario ");
        sql.append(" ORDER BY v.data DESC, v.id DESC");

        System.out.println("Consulta SQL: " + sql.toString());
        System.out.println("Parâmetros: " + params);

        try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    vendas.add(new VendaRelatorio(
                            rs.getString("data_venda"),
                            rs.getString("horario"),
                            rs.getString("vendedor"),
                            rs.getString("cpf_cliente"),
                            String.format("R$ %.2f", rs.getDouble("valorTotal")).replace(".", ","),
                            rs.getString("formasPagamento"),
                            obterDetalhesVenda(rs.getInt("id"))));
                }
            }
        }

        System.out.println("Vendas encontradas: " + vendas.size());
        return vendas;
    }

    private java.util.Date parseDate(String dateString) {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        format.setLenient(false);
        try {
            return format.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String obterDetalhesVenda(int vendaId) {
        StringBuilder detalhes = new StringBuilder();

        try {
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

                        detalhes.append("Código da Venda: ").append(idVenda).append("\n")
                                .append("Data: ").append(dataVenda).append("\n")
                                .append("Horário: ").append(horarioVenda).append("\n")
                                .append("Vendedor: ").append(nomeVendedor).append("\n");

                        if (cpfCliente != null && !cpfCliente.isEmpty()) {
                            try {
                                String cpfDescriptografado = Criptografia.descriptografar(cpfCliente);
                                String cpfFormatado = formatarCpf(cpfDescriptografado);
                                detalhes.append("CPF do Cliente: ").append(cpfFormatado).append("\n");
                            } catch (Exception e) {
                                detalhes.append("CPF do Cliente: Erro ao descriptografar\n");
                            }
                        } else {
                            detalhes.append("CPF do Cliente: Não identificado\n");
                        }

                        detalhes.append("Valor Total: R$ ").append(String.format("%.2f", valorTotal)).append("\n")
                                .append("Formas de Pagamento: ")
                                .append(formasPagamento != null ? formasPagamento : "Não informado").append("\n\n");
                    }
                }
            }

            String sqlMedicamentos = "SELECT m.id, m.nome, m.formaFarmaceutica, m.dosagem, "
                    + "m.embalagem, m.qntEmbalagem, iv.qnt, iv.precoUnit, iv.desconto "
                    + "FROM itemVenda iv "
                    + "JOIN medicamento m ON iv.medicamento_id = m.id "
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

    private String formatarCpf(String cpf) {
        if (cpf == null || cpf.length() != 11) {
            return cpf;
        }
        return "***." + cpf.substring(3, 6) + "." + cpf.substring(6, 9) + "-**";
    }
}