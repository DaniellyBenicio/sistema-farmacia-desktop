package views.Vendas;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class VisualizarVendas extends JPanel {
    private JTable tabelaVendas;
    private DefaultTableModel modeloTabela;
    private JFormattedTextField campoBusca;
    private Connection conn;
    private JButton btnFiltro;
    private String tipoFiltroAtual;
    private Map<Integer, String> detalhesVendaMap;

    public VisualizarVendas(Connection conn) {
        this.conn = conn;
        this.detalhesVendaMap = new HashMap<>();

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JPanel painelSuperior = criarTituloEBusca();
        add(painelSuperior, BorderLayout.NORTH);

        JPanel painelTabela = criarTabela();
        add(painelTabela, BorderLayout.CENTER);

        atualizarTabela("", null);
    }

    private JPanel criarTituloEBusca() {
        JPanel painelSuperior = new JPanel(new BorderLayout());
        painelSuperior.setBackground(Color.WHITE);
        painelSuperior.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // Painel de Título
        JPanel painelTitulo = new JPanel();
        painelTitulo.setLayout(new BoxLayout(painelTitulo, BoxLayout.Y_AXIS));
        painelTitulo.setBorder(BorderFactory.createEmptyBorder(30, 0, 35, 0));
        painelTitulo.setBackground(Color.WHITE);

        JLabel titulo = new JLabel("ÚLTIMAS VENDAS REALIZADAS");
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        painelTitulo.add(titulo);

        // Painel de Busca
        JPanel painelBusca = new JPanel(new BorderLayout());
        painelBusca.setBackground(Color.WHITE);
        painelBusca.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

        JPanel painelEsquerda = new JPanel(new BorderLayout());
        painelEsquerda.setBackground(Color.WHITE);
        painelEsquerda.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));

        JLabel lblBuscarVenda = new JLabel("Buscar venda");
        lblBuscarVenda.setFont(new Font("Arial", Font.BOLD, 16));
        painelEsquerda.add(lblBuscarVenda, BorderLayout.NORTH);

        campoBusca = new JFormattedTextField();
        campoBusca.setFont(new Font("Arial", Font.PLAIN, 14));
        campoBusca.setPreferredSize(new Dimension(0, 40));
        campoBusca.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(209, 213, 219)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        campoBusca.setEditable(false);
        campoBusca.setText("Buscar");
        campoBusca.setForeground(Color.GRAY);
        campoBusca.setToolTipText("Selecione um filtro para buscar");

        painelEsquerda.add(campoBusca, BorderLayout.CENTER);
        painelBusca.add(painelEsquerda, BorderLayout.CENTER);

        // Painel de Botões
        JPanel painelDireita = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        painelDireita.setBackground(Color.WHITE);

        JButton btnFiltrarVenda = new JButton("FILTRAR VENDA");
        btnFiltrarVenda.setFont(new Font("Arial", Font.BOLD, 14));
        btnFiltrarVenda.setBackground(new Color(24, 39, 55));
        btnFiltrarVenda.setForeground(Color.WHITE);
        btnFiltrarVenda.setFocusPainted(false);
        btnFiltrarVenda.setPreferredSize(new Dimension(160, 40));
        btnFiltrarVenda.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        btnFiltrarVenda.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton btnShowMenu = new JButton("▼");
        btnShowMenu.setFont(new Font("Arial", Font.BOLD, 14));
        btnShowMenu.setBackground(new Color(24, 39, 55));
        btnShowMenu.setForeground(Color.WHITE);
        btnShowMenu.setFocusPainted(false);
        btnShowMenu.setPreferredSize(new Dimension(40, 40));
        btnShowMenu.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        btnShowMenu.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Popup Menu
        JPopupMenu popupMenu = new JPopupMenu();
        popupMenu.setBorder(BorderFactory.createLineBorder(new Color(209, 213, 219)));

        JMenuItem itemVendedor = criarMenuItem("Vendedor");
        JMenuItem itemData = criarMenuItem("Data");
        JMenuItem itemLimparFiltro = criarMenuItem("Limpar Filtro");

        itemVendedor.addActionListener(e -> {
            tipoFiltroAtual = "Vendedor";
            btnFiltro = btnFiltrarVenda;
            btnFiltrarVenda.setText("FILTRAR POR VENDEDOR");
            campoBusca.setValue(null);
            campoBusca.setEditable(true);
            campoBusca.setToolTipText("Digite o nome do vendedor");
            campoBusca.setFormatterFactory(null);
            campoBusca.setText("");
            campoBusca.setForeground(Color.BLACK);
        });

        itemData.addActionListener(e -> {
            tipoFiltroAtual = "Data";
            btnFiltro = btnFiltrarVenda;
            btnFiltrarVenda.setText("FILTRAR POR DATA");
            campoBusca.setValue(null);
            campoBusca.setEditable(true);
            campoBusca.setToolTipText("Digite a data no formato DD-MM-YYYY");
            try {
                MaskFormatter mascaraData = new MaskFormatter("##-##-####");
                mascaraData.setPlaceholderCharacter('_');
                campoBusca.setFormatterFactory(new DefaultFormatterFactory(mascaraData));
            } catch (ParseException ex) {
                ex.printStackTrace();
            }
            campoBusca.setText("");
            campoBusca.setForeground(Color.BLACK);
        });

        itemLimparFiltro.addActionListener(e -> {
            tipoFiltroAtual = null;
            btnFiltro = btnFiltrarVenda;
            btnFiltrarVenda.setText("FILTRAR VENDA");
            campoBusca.setValue(null);
            campoBusca.setText("Buscar");
            campoBusca.setEditable(false);
            campoBusca.setToolTipText("Selecione um filtro para buscar");
            campoBusca.setFormatterFactory(null);
            campoBusca.setForeground(Color.GRAY);
            atualizarTabela("", null);
        });

        popupMenu.add(itemVendedor);
        popupMenu.add(itemData);
        popupMenu.addSeparator();
        popupMenu.add(itemLimparFiltro);

        btnShowMenu.addActionListener(e -> {
            popupMenu.show(btnShowMenu, 0, btnShowMenu.getHeight());
        });

        btnFiltrarVenda.addActionListener(e -> {
            String termoBusca = campoBusca.getText().trim();

            if (termoBusca.equals("Buscar")) {
                JOptionPane.showMessageDialog(this,
                        "Selecione um filtro e digite um termo de busca.",
                        "Aviso",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (tipoFiltroAtual != null && tipoFiltroAtual.equals("Data")) {
                if (!termoBusca.matches("\\d{2}-\\d{2}-\\d{4}")) {
                    JOptionPane.showMessageDialog(this,
                            "Formato de data inválido. Use DD-MM-YYYY.",
                            "Erro",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try {
                    SimpleDateFormat formatoEntrada = new SimpleDateFormat("dd-MM-yyyy");
                    SimpleDateFormat formatoSaida = new SimpleDateFormat("yyyy-MM-dd");
                    Date data = formatoEntrada.parse(termoBusca);
                    termoBusca = formatoSaida.format(data);
                } catch (ParseException ex) {
                    JOptionPane.showMessageDialog(this,
                            "Erro ao converter a data.",
                            "Erro",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            atualizarTabela(termoBusca, tipoFiltroAtual);
        });

        painelDireita.add(btnFiltrarVenda);
        painelDireita.add(btnShowMenu);
        painelBusca.add(painelDireita, BorderLayout.EAST);

        painelSuperior.add(painelTitulo, BorderLayout.NORTH);
        painelSuperior.add(painelBusca, BorderLayout.CENTER);

        return painelSuperior;
    }

    private JMenuItem criarMenuItem(String texto) {
        JMenuItem item = new JMenuItem(texto);
        item.setFont(new Font("Arial", Font.PLAIN, 14));
        item.setBackground(Color.WHITE);
        item.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        return item;
    }

    private JPanel criarTabela() {
        JPanel painelTabela = new JPanel(new BorderLayout());
        painelTabela.setBackground(Color.WHITE);
        painelTabela.setBorder(BorderFactory.createEmptyBorder(0, 30, 30, 30));

        String[] colunas = {"DATA", "HORÁRIO", "VENDEDOR", "VALOR TOTAL", "FORMA DE PAGAMENTO", "AÇÕES"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5;
            }
        };

        tabelaVendas = new JTable(modeloTabela);
        tabelaVendas.setFillsViewportHeight(true);
        tabelaVendas.setRowHeight(45);
        tabelaVendas.setFont(new Font("Arial", Font.PLAIN, 14));
        tabelaVendas.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        tabelaVendas.getTableHeader().setBackground(new Color(249, 250, 251));
        tabelaVendas.getTableHeader().setForeground(new Color(17, 24, 39));
        tabelaVendas.getTableHeader().setPreferredSize(new Dimension(0, 45));
        tabelaVendas.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(229, 231, 235)));
        tabelaVendas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaVendas.setShowGrid(false);
        tabelaVendas.setIntercellSpacing(new Dimension(0, 0));
        tabelaVendas.setCellSelectionEnabled(false);
        tabelaVendas.setRowSelectionAllowed(false);
        tabelaVendas.setColumnSelectionAllowed(false);
        tabelaVendas.setBorder(BorderFactory.createLineBorder(new Color(229, 231, 235)));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        for (int i = 0; i < colunas.length - 1; i++) {
            tabelaVendas.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        tabelaVendas.getColumnModel().getColumn(5).setCellRenderer(new RenderizadorBotoes());
        tabelaVendas.getColumnModel().getColumn(5).setCellEditor(new EditorBotoes(new JTextField()));

        // Ajuste das larguras das colunas
        tabelaVendas.getColumnModel().getColumn(0).setPreferredWidth(150);
        tabelaVendas.getColumnModel().getColumn(1).setPreferredWidth(100);
        tabelaVendas.getColumnModel().getColumn(2).setPreferredWidth(200);
        tabelaVendas.getColumnModel().getColumn(3).setPreferredWidth(120);
        tabelaVendas.getColumnModel().getColumn(4).setPreferredWidth(180);
        tabelaVendas.getColumnModel().getColumn(5).setPreferredWidth(160);

        // Desabilitar redimensionamento de colunas
        for (int i = 0; i < tabelaVendas.getColumnModel().getColumnCount(); i++) {
            tabelaVendas.getColumnModel().getColumn(i).setResizable(false);
        }

        JScrollPane scrollPane = new JScrollPane(tabelaVendas);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);

        painelTabela.add(scrollPane, BorderLayout.CENTER);

        return painelTabela;
    }

    private void atualizarTabela(String termoBusca, String tipoFiltro) {
        modeloTabela.setRowCount(0);
        detalhesVendaMap.clear();
        String sql;

        try {
            if (tipoFiltro != null) {
                switch (tipoFiltro) {
                    case "Vendedor":
                        sql = "SELECT v.id, DATE_FORMAT(v.data, '%d/%m/%Y') as data_venda, " +
                                "c.nome as cliente, f.nome as vendedor, v.valorTotal, " +
                                "p.formaPagamento, TIME(v.data) as horario " +
                                "FROM venda v " +
                                "LEFT JOIN cliente c ON v.cliente_id = c.id " +
                                "JOIN funcionario f ON v.funcionario_id = f.id " +
                                "LEFT JOIN pagamento p ON v.id = p.venda_id " +
                                "WHERE f.nome LIKE ? AND f.status = true " +
                                "ORDER BY v.data DESC, v.id DESC";
                        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                            stmt.setString(1, "%" + termoBusca + "%");
                            try (ResultSet rs = stmt.executeQuery()) {
                                processarResultados(rs);
                            }
                        }
                        break;

                    case "Data":
                        sql = "SELECT v.id, DATE_FORMAT(v.data, '%d/%m/%Y') as data_venda, " +
                                "c.nome as cliente, f.nome as vendedor, v.valorTotal, " +
                                "p.formaPagamento, TIME(v.data) as horario " +
                                "FROM venda v " +
                                "LEFT JOIN cliente c ON v.cliente_id = c.id " +
                                "JOIN funcionario f ON v.funcionario_id = f.id " +
                                "LEFT JOIN pagamento p ON v.id = p.venda_id " +
                                "WHERE DATE(v.data) = ? AND f.status = true " +
                                "ORDER BY v.data DESC, v.id DESC";
                        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                            stmt.setString(1, termoBusca);
                            try (ResultSet rs = stmt.executeQuery()) {
                                processarResultados(rs);
                            }
                        }
                        break;

                    default:
                        sql = "SELECT v.id, DATE_FORMAT(v.data, '%d/%m/%Y') as data_venda, " +
                                "c.nome as cliente, f.nome as vendedor, v.valorTotal, " +
                                "p.formaPagamento, TIME(v.data) as horario " +
                                "FROM venda v " +
                                "LEFT JOIN cliente c ON v.cliente_id = c.id " +
                                "JOIN funcionario f ON v.funcionario_id = f.id " +
                                "LEFT JOIN pagamento p ON v.id = p.venda_id " +
                                "WHERE f.status = true " +
                                "ORDER BY v.data DESC, v.id DESC";
                        try (PreparedStatement stmt = conn.prepareStatement(sql);
                             ResultSet rs = stmt.executeQuery()) {
                            processarResultados(rs);
                        }
                        break;
                }
            } else {
                sql = "SELECT v.id, DATE_FORMAT(v.data, '%d/%m/%Y') as data_venda, " +
                        "c.nome as cliente, f.nome as vendedor, v.valorTotal, " +
                        "p.formaPagamento, TIME(v.data) as horario " +
                        "FROM venda v " +
                        "LEFT JOIN cliente c ON v.cliente_id = c.id " +
                        "JOIN funcionario f ON v.funcionario_id = f.id " +
                        "LEFT JOIN pagamento p ON v.id = p.venda_id " +
                        "WHERE f.status = true " +
                        "ORDER BY v.data DESC, v.id DESC";
                try (PreparedStatement stmt = conn.prepareStatement(sql);
                     ResultSet rs = stmt.executeQuery()) {
                    processarResultados(rs);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao carregar vendas: " + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void processarResultados(ResultSet rs) throws SQLException {
        int rowIndex = 0;
        while (rs.next()) {
            int vendaId = rs.getInt("id");
            String cliente = rs.getString("cliente");
            cliente = (cliente == null) ? "Cliente não cadastrado" : cliente;

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

        if (modeloTabela.getRowCount() == 0) {
            modeloTabela.addRow(new Object[]{"Nenhuma venda encontrada", "", "", "", "", ""});
        }
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

    class RenderizadorBotoes extends JPanel implements TableCellRenderer {
        private final JButton button;

        public RenderizadorBotoes() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
            setBackground(Color.WHITE);

            button = new JButton("DETALHES");
            button.setFont(new Font("Arial", Font.BOLD, 12));
            button.setBackground(new Color(24, 39, 55));
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

    class EditorBotoes extends DefaultCellEditor {
        private final JPanel panel;
        private final JButton button;
        private int row;

        public EditorBotoes(JTextField textField) {
            super(textField);
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
            panel.setBackground(Color.WHITE);

            button = new JButton("DETALHES");
            button.setFont(new Font("Arial", Font.BOLD, 12));
            button.setBackground(new Color(24, 39, 55));
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
    }

    private void mostrarDetalhesVenda(int row) {
        String detalhes = detalhesVendaMap.get(row);
        if (detalhes != null && !detalhes.isEmpty()) {
            JDialog dialog = new JDialog();
            dialog.setTitle("Detalhes da Venda");
            dialog.setModal(true);
            dialog.setSize(500, 300);
            dialog.setLocationRelativeTo(this);

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
            closeButton.setBackground(new Color(24, 39, 55));
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
            JOptionPane.showMessageDialog(this,
                    "Nenhum detalhe disponível para esta venda.",
                    "Informação",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }
}