package views.Vendas;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RelatorioVendas extends JPanel {
    private JTable tabelaRelatorio;
    private DefaultTableModel modeloTabela;
    private Connection conn;
    private String dataFiltro, vendedorFiltro, pagamentoFiltro;
    private String dataInicioPersonalizada, dataFimPersonalizada;
    private Map<Integer, String> detalhesVendaMap;

    // Construtor principal
    public RelatorioVendas(Connection conn, String dataFiltro, String vendedorFiltro, String pagamentoFiltro) {
        this(conn, dataFiltro, vendedorFiltro, pagamentoFiltro, null, null);
    }

    // Construtor com datas personalizadas
    public RelatorioVendas(Connection conn, String dataFiltro, String vendedorFiltro, 
                         String pagamentoFiltro, String dataInicioPersonalizada, String dataFimPersonalizada) {
        this.conn = conn;
        this.dataFiltro = dataFiltro;
        this.vendedorFiltro = vendedorFiltro;
        this.pagamentoFiltro = pagamentoFiltro;
        this.dataInicioPersonalizada = dataInicioPersonalizada;
        this.dataFimPersonalizada = dataFimPersonalizada;
        this.detalhesVendaMap = new HashMap<>();

        initComponents();
        carregarDadosRelatorio();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Add back button panel at the very top
        JPanel painelVoltar = criarBotaoVoltar();
        add(painelVoltar, BorderLayout.NORTH);

        // Panel to hold the title and table
        JPanel painelConteudo = new JPanel(new BorderLayout());
        painelConteudo.setBackground(Color.WHITE);

        JPanel painelTitulo = criarTitulo();
        painelConteudo.add(painelTitulo, BorderLayout.NORTH);

        JPanel painelTabela = criarTabela();
        painelConteudo.add(painelTabela, BorderLayout.CENTER);
        
        add(painelConteudo, BorderLayout.CENTER);

        JPanel painelExportar = criarBotaoExportar();
        add(painelExportar, BorderLayout.SOUTH);
    }

    private JPanel criarBotaoVoltar() {
        JPanel painelVoltar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelVoltar.setBackground(Color.WHITE);
        painelVoltar.setBorder(BorderFactory.createEmptyBorder(10, 20, 0, 0));

        JButton btnVoltar = new JButton("Voltar");
        btnVoltar.setFont(new Font("Arial", Font.BOLD, 14));
        btnVoltar.setBackground(new Color(24, 39, 72));
        btnVoltar.setForeground(Color.WHITE);
        btnVoltar.setFocusPainted(false);
        btnVoltar.setBorderPainted(false);
        btnVoltar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVoltar.setPreferredSize(new Dimension(100, 30));
        btnVoltar.addActionListener(e -> voltarTelaAnterior());

        painelVoltar.add(btnVoltar);
        return painelVoltar;
    }

    private void voltarTelaAnterior() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        frame.getContentPane().removeAll();
        frame.getContentPane().add(new GerarRelatorio(conn));
        frame.revalidate();
        frame.repaint();
    }

    private JPanel criarTitulo() {
        JPanel painelTitulo = new JPanel();
        painelTitulo.setLayout(new BoxLayout(painelTitulo, BoxLayout.Y_AXIS));
        painelTitulo.setBorder(BorderFactory.createEmptyBorder(10, 30, 20, 30));
        painelTitulo.setBackground(Color.WHITE);

        JLabel titulo = new JLabel("Relatório");
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        painelTitulo.add(titulo);

        return painelTitulo;
    }

    private JPanel criarTabela() {
        JPanel painelTabela = new JPanel(new BorderLayout());
        painelTabela.setBackground(Color.WHITE);
        painelTabela.setBorder(BorderFactory.createEmptyBorder(10, 30, 30, 30));

        String[] colunas = {"DATA", "HORÁRIO", "VENDEDOR", "VALOR TOTAL", "FORMA DE PAGAMENTO", "AÇÕES"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5;
            }
        };

        tabelaRelatorio = new JTable(modeloTabela);
        tabelaRelatorio.setFillsViewportHeight(true);
        tabelaRelatorio.setRowHeight(45);
        tabelaRelatorio.setFont(new Font("Arial", Font.PLAIN, 14));
        tabelaRelatorio.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        tabelaRelatorio.getTableHeader().setBackground(Color.WHITE);
        tabelaRelatorio.getTableHeader().setForeground(Color.BLACK);
        tabelaRelatorio.getTableHeader().setPreferredSize(new Dimension(0, 45));
        
        tabelaRelatorio.setShowGrid(true);
        tabelaRelatorio.setGridColor(Color.BLACK);
        tabelaRelatorio.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        
        ((JComponent)tabelaRelatorio.getTableHeader()).setBorder(BorderFactory.createLineBorder(Color.BLACK));
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        centerRenderer.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        for (int i = 0; i < colunas.length - 1; i++) {
            tabelaRelatorio.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        tabelaRelatorio.getColumnModel().getColumn(5).setCellRenderer(new ButtonRenderer());
        tabelaRelatorio.getColumnModel().getColumn(5).setCellEditor(new ButtonEditor(new JTextField()));

        JScrollPane scrollPane = new JScrollPane(tabelaRelatorio);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        scrollPane.getViewport().setBackground(Color.WHITE);

        painelTabela.add(scrollPane, BorderLayout.CENTER);

        return painelTabela;
    }

    private JPanel criarBotaoExportar() {
        JPanel painelExportar = new JPanel(new FlowLayout(FlowLayout.CENTER));
        painelExportar.setBorder(BorderFactory.createEmptyBorder(20, 0, 30, 0));
        painelExportar.setBackground(Color.WHITE);

        JButton btnExportar = new JButton("Exportar relatório");
        btnExportar.setFont(new Font("Arial", Font.BOLD, 14));
        btnExportar.setBackground(new Color(24, 39, 72));
        btnExportar.setForeground(Color.WHITE);
        btnExportar.setFocusPainted(false);
        btnExportar.setPreferredSize(new Dimension(200, 40));
        btnExportar.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        btnExportar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnExportar.addActionListener(e -> exportarRelatorio());

        painelExportar.add(btnExportar);

        return painelExportar;
    }

    private void carregarDadosRelatorio() {
        modeloTabela.setRowCount(0);
        detalhesVendaMap.clear();
        
        try {
            String sql = "SELECT v.id, DATE_FORMAT(v.data, '%d/%m/%Y') as data_venda, " +
                    "f.nome as vendedor, v.valorTotal, " +
                    "p.formaPagamento, TIME(v.data) as horario " +
                    "FROM venda v " +
                    "JOIN funcionario f ON v.funcionario_id = f.id " +
                    "LEFT JOIN pagamento p ON v.id = p.venda_id " +
                    "WHERE f.status = true ";
            
            if (!dataFiltro.equals("Selecione")) {
                sql += aplicarFiltroData();
            }
            
            if (!vendedorFiltro.equals("Selecione") && !vendedorFiltro.equals("Todos")) {
                if (vendedorFiltro.trim().contains(" ")) {
                    sql += " AND LOWER(f.nome) = LOWER(?)";
                } else {
                    sql += " AND LOWER(f.nome) LIKE LOWER(?)";
                }
            }
            
            if (pagamentoFiltro != null && !pagamentoFiltro.equals("Selecione") && !pagamentoFiltro.equals("Todos")) {
                sql += " AND p.formaPagamento = ?";
            }
            
            sql += " ORDER BY v.data DESC, v.id DESC";
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
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
                    int rowIndex = 0;
                    while (rs.next()) {
                        int vendaId = rs.getInt("id");
                        String formaPagamento = rs.getString("formaPagamento");
                        formaPagamento = formatarFormaPagamento(formaPagamento);

                        String valorFormatado = String.format("R$ %.2f", rs.getDouble("valorTotal"))
                                .replace(".", ",");

                        String detalhes = obterDetalhesVenda(vendaId);
                        detalhesVendaMap.put(rowIndex, detalhes);

                        Object[] row = {
                                rs.getString("data_venda"),
                                rs.getString("horario"),
                                rs.getString("vendedor"),
                                valorFormatado,
                                formaPagamento,
                                "Detalhes"
                        };
                        modeloTabela.addRow(row);
                        rowIndex++;
                    }
                }
            }
            
            if (modeloTabela.getRowCount() == 0) {
                modeloTabela.addRow(new Object[]{"Nenhuma venda encontrada", "", "", "", "", ""});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao carregar relatório: " + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private String aplicarFiltroData() {
        SimpleDateFormat sdfDB = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdfInput = new SimpleDateFormat("dd/MM/yyyy");
        sdfInput.setLenient(false);
        
        switch (dataFiltro) {
            case "Hoje":
                return " AND DATE(v.data) = CURDATE()";
            case "Ontem":
                return " AND DATE(v.data) = DATE_SUB(CURDATE(), INTERVAL 1 DAY)";
            case "Esta semana":
                return " AND YEARWEEK(v.data, 1) = YEARWEEK(CURDATE(), 1)";
            case "Este mês":
                return " AND MONTH(v.data) = MONTH(CURRENT_DATE()) AND YEAR(v.data) = YEAR(CURRENT_DATE())";
            case "Personalizado":
                try {
                    if (dataInicioPersonalizada != null && dataFimPersonalizada != null) {
                        Date dataInicio = sdfInput.parse(dataInicioPersonalizada);
                        Date dataFim = sdfInput.parse(dataFimPersonalizada);
                        return " AND DATE(v.data) BETWEEN '" + sdfDB.format(dataInicio) + 
                               "' AND '" + sdfDB.format(dataFim) + "'";
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this,
                            "Formato de data inválido no filtro personalizado",
                            "Erro",
                            JOptionPane.ERROR_MESSAGE);
                }
                return "";
            default:
                return "";
        }
    }

    private String obterDetalhesVenda(int vendaId) {
        StringBuilder detalhes = new StringBuilder();

        try {
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

            String sqlMedicamentos = "SELECT m.nome, iv.qnt, iv.precoUnit, iv.desconto " +
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

                        if (nomeMedicamento != null) {
                            detalhes.append("Medicamento: ").append(nomeMedicamento)
                                    .append(" | Qtd: ").append(quantidade)
                                    .append(" | Preço Unit.: R$ ").append(String.format("%.2f", precoUnit))
                                    .append(" | Desconto: R$ ").append(String.format("%.2f", desconto))
                                    .append("\n");
                        }
                    }
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao buscar detalhes da venda: " + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }

        return detalhes.length() > 0 ? detalhes.toString() : "Nenhum item encontrado para esta venda.";
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

    private void exportarRelatorio() {
        JOptionPane.showMessageDialog(this,
                "Relatório exportado com sucesso!",
                "Sucesso",
                JOptionPane.INFORMATION_MESSAGE);
    }

    class ButtonRenderer extends JPanel implements TableCellRenderer {
        private final JButton button;

        public ButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 0, 5));
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createLineBorder(Color.BLACK));

            button = new JButton("DETALHES");
            button.setFont(new Font("Arial", Font.BOLD, 12));
            button.setBackground(new Color(24, 39, 72));
            button.setForeground(Color.WHITE);
            button.setBorderPainted(false);
            button.setFocusPainted(false);
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            button.setPreferredSize(new Dimension(100, 30));

            add(button);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                      boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        private final JPanel panel;
        private final JButton button;
        private int row;

        public ButtonEditor(JTextField textField) {
            super(textField);
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 5));
            panel.setBackground(Color.WHITE);
            panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

            button = new JButton("DETALHES");
            button.setFont(new Font("Arial", Font.BOLD, 12));
            button.setBackground(new Color(24, 39, 72));
            button.setForeground(Color.WHITE);
            button.setBorderPainted(false);
            button.setFocusPainted(false);
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            button.setPreferredSize(new Dimension(100, 30));

            button.addActionListener(e -> fireEditingStopped());
            panel.add(button);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            this.row = row;
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            mostrarDetalhesVenda(row);
            return "";
        }

        private void mostrarDetalhesVenda(int row) {
            String detalhes = detalhesVendaMap.get(row);
            if (detalhes != null && !detalhes.isEmpty()) {
                JDialog dialog = new JDialog();
                dialog.setTitle("Detalhes da Venda");
                dialog.setModal(true);
                dialog.setSize(500, 300);
                dialog.setLocationRelativeTo(RelatorioVendas.this);

                JPanel panel = new JPanel(new BorderLayout());
                panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
                panel.setBackground(Color.WHITE);

                JLabel titleLabel = new JLabel("Itens da Venda");
                titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
                titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
                panel.add(titleLabel, BorderLayout.NORTH);

                JTextArea textArea = new JTextArea(detalhes);
                textArea.setFont(new Font("Arial", Font.PLAIN, 14));
                textArea.setEditable(false);
                textArea.setLineWrap(true);
                textArea.setWrapStyleWord(true);
                textArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                textArea.setBackground(Color.WHITE);

                JScrollPane scrollPane = new JScrollPane(textArea);
                scrollPane.setBorder(BorderFactory.createLineBorder(new Color(209, 213, 219)));
                panel.add(scrollPane, BorderLayout.CENTER);

                JButton closeButton = new JButton("FECHAR");
                closeButton.setFont(new Font("Arial", Font.BOLD, 14));
                closeButton.setBackground(new Color(24, 39, 72));
                closeButton.setForeground(Color.WHITE);
                closeButton.setFocusPainted(false);
                closeButton.setBorderPainted(false);
                closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
                closeButton.addActionListener(e -> dialog.dispose());

                JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                buttonPanel.setBackground(Color.WHITE);
                buttonPanel.add(closeButton);
                panel.add(buttonPanel, BorderLayout.SOUTH);

                dialog.add(panel);
                dialog.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(RelatorioVendas.this,
                        "Nenhum detalhe disponível para esta venda.",
                        "Informação",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
}