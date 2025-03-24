package views.Vendas;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
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

        // Painel de Título
        JPanel painelTitulo = new JPanel();
        painelTitulo.setLayout(new BoxLayout(painelTitulo, BoxLayout.Y_AXIS));
        painelTitulo.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        painelTitulo.setBackground(Color.WHITE);

        JLabel titulo = new JLabel("ÚLTIMAS VENDAS REALIZADAS");
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        painelTitulo.add(titulo);

        // Painel de Busca
        JPanel painelBusca = new JPanel(new BorderLayout());
        painelBusca.setBackground(Color.WHITE);
        painelBusca.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));

        JPanel painelEsquerda = new JPanel(new BorderLayout());
        painelEsquerda.setBackground(Color.WHITE);
       
        JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        labelPanel.setBackground(Color.WHITE);
       
        JLabel lblBuscarVenda = new JLabel("Buscar venda");
        lblBuscarVenda.setFont(new Font("Arial", Font.BOLD, 14));
        labelPanel.add(lblBuscarVenda);
       
        painelEsquerda.add(labelPanel, BorderLayout.NORTH);

        campoBusca = new JFormattedTextField();
        campoBusca.setFont(new Font("Arial", Font.PLAIN, 14));
        campoBusca.setPreferredSize(new Dimension(400, 40));
        campoBusca.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        campoBusca.setEditable(false);
        campoBusca.setToolTipText("Selecione um filtro para buscar");
        painelEsquerda.add(campoBusca, BorderLayout.CENTER);

        painelBusca.add(painelEsquerda, BorderLayout.CENTER);

        JPanel painelDireita = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        painelDireita.setBackground(Color.WHITE);

        JButton btnFiltrarVenda = new JButton("FILTRAR VENDA");
        btnFiltrarVenda.setFont(new Font("Arial", Font.BOLD, 14));
        btnFiltrarVenda.setBackground(new Color(26, 35, 126));
        btnFiltrarVenda.setForeground(Color.WHITE);
        btnFiltrarVenda.setFocusPainted(false);
        btnFiltrarVenda.setPreferredSize(new Dimension(160, 40));
        btnFiltrarVenda.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JButton btnShowMenu = new JButton("▼");
        btnShowMenu.setFont(new Font("Arial", Font.BOLD, 14));
        btnShowMenu.setBackground(new Color(26, 35, 126));
        btnShowMenu.setForeground(Color.WHITE);
        btnShowMenu.setFocusPainted(false);
        btnShowMenu.setPreferredSize(new Dimension(40, 40));
        btnShowMenu.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem itemVendedor = new JMenuItem("Vendedor");
        JMenuItem itemData = new JMenuItem("Data");
        JMenuItem itemLimparFiltro = new JMenuItem("Limpar Filtro");

        itemVendedor.addActionListener(e -> {
            tipoFiltroAtual = "Vendedor";
            btnFiltro = btnFiltrarVenda;
            btnFiltrarVenda.setText("FILTRAR POR VENDEDOR");
            campoBusca.setValue(null);
            campoBusca.setEditable(true);
            campoBusca.setToolTipText("Digite o nome do vendedor");
            campoBusca.setFormatterFactory(null);
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
        });

        itemLimparFiltro.addActionListener(e -> {
            tipoFiltroAtual = null;
            btnFiltro = btnFiltrarVenda;
            btnFiltrarVenda.setText("FILTRAR VENDA");
            campoBusca.setValue(null);
            campoBusca.setText("");
            campoBusca.setEditable(false);
            campoBusca.setToolTipText("Selecione um filtro para buscar");
            campoBusca.setFormatterFactory(null);
            atualizarTabela("", null);
        });

        popupMenu.add(itemVendedor);
        popupMenu.add(itemData);
        popupMenu.add(itemLimparFiltro);

        btnShowMenu.addActionListener(e -> {
            popupMenu.show(btnShowMenu, 0, btnShowMenu.getHeight());
        });

        btnFiltrarVenda.addActionListener(e -> {
            String termoBusca = campoBusca.getText().trim();
           
            if (tipoFiltroAtual != null && tipoFiltroAtual.equals("Data")) {
                if (!termoBusca.matches("\\d{2}-\\d{2}-\\d{4}")) {
                    JOptionPane.showMessageDialog(this, "Formato de data inválido. Use DD-MM-YYYY.", "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try {
                    SimpleDateFormat formatoEntrada = new SimpleDateFormat("dd-MM-yyyy");
                    SimpleDateFormat formatoSaida = new SimpleDateFormat("yyyy-MM-dd");
                    Date data = formatoEntrada.parse(termoBusca);
                    termoBusca = formatoSaida.format(data);
                } catch (ParseException ex) {
                    JOptionPane.showMessageDialog(this, "Erro ao converter a data.", "Erro", JOptionPane.ERROR_MESSAGE);
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

    private JPanel criarTabela() {
        JPanel painelTabela = new JPanel(new BorderLayout());
        painelTabela.setBackground(Color.WHITE);
        painelTabela.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
       
        String[] colunas = {"DATA", "HORÁRIO", "VENDEDOR", "VALOR TOTAL", "FORMA DE PAGAMENTO", "AÇÕES"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5; 
            }
        };
        
        tabelaVendas = new JTable(modeloTabela);
        tabelaVendas.setFillsViewportHeight(true);
        tabelaVendas.setRowHeight(35);
        tabelaVendas.setFont(new Font("Arial", Font.PLAIN, 12));
        tabelaVendas.getTableHeader().setFont(new Font("Arial", Font.BOLD, 15));
        tabelaVendas.getTableHeader().setReorderingAllowed(false);
        tabelaVendas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaVendas.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        
        for (int i = 0; i < colunas.length - 1; i++) {
            tabelaVendas.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
        tabelaVendas.getColumnModel().getColumn(5).setCellRenderer(new RenderizadorBotoes());
        tabelaVendas.getColumnModel().getColumn(5).setCellEditor(new EditorBotoes(new JTextField()));
        
        tabelaVendas.getColumnModel().getColumn(0).setPreferredWidth(180);
        tabelaVendas.getColumnModel().getColumn(1).setPreferredWidth(100); 
        tabelaVendas.getColumnModel().getColumn(2).setPreferredWidth(180); 
        tabelaVendas.getColumnModel().getColumn(3).setPreferredWidth(100); 
        tabelaVendas.getColumnModel().getColumn(4).setPreferredWidth(150); 
        tabelaVendas.getColumnModel().getColumn(5).setPreferredWidth(130); 
        
        tabelaVendas.setCellSelectionEnabled(false);
        tabelaVendas.setRowSelectionAllowed(false);
        tabelaVendas.setColumnSelectionAllowed(false);
        
        JScrollPane scrollPane = new JScrollPane(tabelaVendas);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 30, 57, 30));
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
                        sql = "SELECT v.id, v.data, v.horario, f.nome AS vendedor, v.valorTotal, v.formaPagamento " +
                              "FROM venda v " +
                              "LEFT JOIN funcionario f ON v.funcionario_id = f.id " +
                              "WHERE f.nome LIKE ? " +
                              "ORDER BY v.data DESC, v.horario DESC";
                        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                            stmt.setString(1, "%" + termoBusca + "%");
                            try (ResultSet rs = stmt.executeQuery()) {
                                processarResultados(rs);
                            }
                        }
                        break;
    
                    case "Data":
                        sql = "SELECT v.id, v.data, v.horario, f.nome AS vendedor, v.valorTotal, v.formaPagamento " +
                              "FROM venda v " +
                              "LEFT JOIN funcionario f ON v.funcionario_id = f.id " +
                              "WHERE DATE(v.data) = ? " +
                              "ORDER BY v.data DESC, v.horario DESC";
                        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                            stmt.setString(1, termoBusca);
                            try (ResultSet rs = stmt.executeQuery()) {
                                processarResultados(rs);
                            }
                        }
                        break;
    
                    default:
                        sql = "SELECT v.id, v.data, v.horario, f.nome AS vendedor, v.valorTotal, v.formaPagamento " +
                              "FROM venda v " +
                              "LEFT JOIN funcionario f ON v.funcionario_id = f.id " +
                              "ORDER BY v.data DESC, v.horario DESC";
                        try (PreparedStatement stmt = conn.prepareStatement(sql);
                             ResultSet rs = stmt.executeQuery()) {
                            processarResultados(rs);
                        }
                        break;
                }
            } else {
                sql = "SELECT v.id, v.data, v.horario, f.nome AS vendedor, v.valorTotal, v.formaPagamento " +
                      "FROM venda v " +
                      "LEFT JOIN funcionario f ON v.funcionario_id = f.id " +
                      "ORDER BY v.data DESC, v.horario DESC";
                try (PreparedStatement stmt = conn.prepareStatement(sql);
                     ResultSet rs = stmt.executeQuery()) {
                    processarResultados(rs);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar vendas: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void processarResultados(ResultSet rs) throws SQLException {
        int rowIndex = 0;
        while (rs.next()) {
            int vendaId = rs.getInt("id");
           
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
            String dataFormatada = dateFormat.format(rs.getDate("data"));
            String horaFormatada = timeFormat.format(rs.getTime("horario"));
           
            String valorFormatado = String.format("R$ %.2f", rs.getDouble("valorTotal"))
                .replace(".", ",");
    
            String detalhes = obterDetalhesVenda(vendaId);
            detalhesVendaMap.put(rowIndex, detalhes);

            Object[] row = {
                dataFormatada,
                horaFormatada,
                rs.getString("vendedor"),
                valorFormatado,
                rs.getString("formaPagamento"),
                "Detalhes" 
            };
            modeloTabela.addRow(row);
            rowIndex++;
        }
    }

    private String obterDetalhesVenda(int vendaId) {
        StringBuilder detalhes = new StringBuilder();

        try {
            // Busca produtos da venda
            String sqlProdutos = "SELECT p.nome, iv.qnt " +
                               "FROM itemVenda iv " +
                               "LEFT JOIN produto p ON iv.produto_id = p.id " +
                               "WHERE iv.venda_id = ?";
            try (PreparedStatement stmtProdutos = conn.prepareStatement(sqlProdutos)) {
                stmtProdutos.setInt(1, vendaId);
                try (ResultSet rsProdutos = stmtProdutos.executeQuery()) {
                    while (rsProdutos.next()) {
                        String nomeProduto = rsProdutos.getString("nome");
                        int quantidade = rsProdutos.getInt("qnt");
                        detalhes.append(nomeProduto).append(" (").append(quantidade).append("), ");
                    }
                }
            }

            // Busca medicamentos da venda
            String sqlMedicamentos = "SELECT m.nome, iv.qnt " +
                                   "FROM itemVenda iv " +
                                   "LEFT JOIN medicamento m ON iv.medicamento_id = m.id " +
                                   "WHERE iv.venda_id = ?";
            try (PreparedStatement stmtMedicamentos = conn.prepareStatement(sqlMedicamentos)) {
                stmtMedicamentos.setInt(1, vendaId);
                try (ResultSet rsMedicamentos = stmtMedicamentos.executeQuery()) {
                    while (rsMedicamentos.next()) {
                        String nomeMedicamento = rsMedicamentos.getString("nome");
                        int quantidade = rsMedicamentos.getInt("qnt");
                        detalhes.append(nomeMedicamento).append(" (").append(quantidade).append("), ");
                    }
                }
            }

            if (detalhes.length() > 0) {
                detalhes.setLength(detalhes.length() - 2); 
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao buscar detalhes da venda: " + e.getMessage(), 
                                        "Erro", JOptionPane.ERROR_MESSAGE);
        }

        return detalhes.toString();
    }

    
    class RenderizadorBotoes extends DefaultTableCellRenderer {
        private JButton button;

        public RenderizadorBotoes() {
            button = new JButton("Detalhes");
            button.setFont(new Font("Arial", Font.BOLD, 11)); 
            button.setBackground(new Color(56, 39, 55));
            button.setForeground(Color.WHITE);
            button.setFocusPainted(false);
            button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            button.setPreferredSize(new Dimension(90, 25)); 
            button.setMargin(new Insets(2, 5, 2, 5)); 
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return button;
        }
    }

    class EditorBotoes extends DefaultCellEditor {
        protected JButton button;
        private String label;
        private boolean isPushed;
        private int row, col;

        public EditorBotoes(JTextField textField) {
            super(textField);
            button = new JButton();
            button.setOpaque(true);
            button.setFont(new Font("Arial", Font.BOLD, 11));
            button.setBackground(new Color(56, 39, 55));
            button.setForeground(Color.WHITE);
            button.setFocusPainted(false);
            button.setPreferredSize(new Dimension(90, 25));
            button.setMargin(new Insets(2, 5, 2, 5)); 
            
            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, 
                                                   boolean isSelected, int row, int column) {
            label = (value == null) ? "Detalhes" : value.toString();
            button.setText(label);
            this.row = row;
            this.col = column;
            isPushed = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                mostrarDetalhesVenda(row);
            }
            isPushed = false;
            return label;
        }

        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
    }

    public void mostrarDetalhesVenda(int row) {
        String detalhes = detalhesVendaMap.get(row);
        if (detalhes != null && !detalhes.isEmpty()) {
            JDialog dialog = new JDialog();
            dialog.setTitle("Detalhes da Venda");
            dialog.setModal(true);
            dialog.setSize(500, 300);
            dialog.setLocationRelativeTo(this);
            
            JPanel panel = new JPanel(new BorderLayout());
            panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            
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
            
            JScrollPane scrollPane = new JScrollPane(textArea);
            panel.add(scrollPane, BorderLayout.CENTER);
            
            JButton closeButton = new JButton("Fechar");
            closeButton.setFont(new Font("Arial", Font.BOLD, 14));
            closeButton.setBackground(new Color(56, 39, 55));
            closeButton.setForeground(Color.WHITE);
            closeButton.setFocusPainted(false);
            closeButton.addActionListener(e -> dialog.dispose());
            
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonPanel.add(closeButton);
            panel.add(buttonPanel, BorderLayout.SOUTH);
            
            dialog.add(panel);
            dialog.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Nenhum detalhe disponível para está venda.", 
                                        "Informação", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}