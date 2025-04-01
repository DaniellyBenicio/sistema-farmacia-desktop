package views.Vendas;

import dao.Relatorio.RelatorioVendasDAO;
import dao.Relatorio.RelatorioVendasDAO.VendaRelatorio;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class RelatorioVendas extends JPanel {
    private JTable tabelaRelatorio;
    private DefaultTableModel modeloTabela;
    private Connection conn;
    private String dataFiltro, vendedorFiltro, pagamentoFiltro;
    private String dataInicioPersonalizada, dataFimPersonalizada;
    private RelatorioVendasDAO dao;
    private List<VendaRelatorio> vendas; // Armazena os dados retornados pelo DAO

    // Construtores
    public RelatorioVendas(Connection conn, String dataFiltro, String vendedorFiltro, String pagamentoFiltro) {
        this(conn, dataFiltro, vendedorFiltro, pagamentoFiltro, null, null);
    }

    public RelatorioVendas(Connection conn, String dataFiltro, String vendedorFiltro, 
                           String pagamentoFiltro, String dataInicioPersonalizada, String dataFimPersonalizada) {
        this.conn = conn;
        this.dataFiltro = dataFiltro;
        this.vendedorFiltro = vendedorFiltro;
        this.pagamentoFiltro = pagamentoFiltro;
        this.dataInicioPersonalizada = dataInicioPersonalizada;
        this.dataFimPersonalizada = dataFimPersonalizada;
        this.dao = new RelatorioVendasDAO(conn);

        initComponents();
        carregarDadosRelatorio();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JPanel painelVoltar = criarBotaoVoltar();
        add(painelVoltar, BorderLayout.NORTH);

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
        frame.getContentPane().add(new GerarRelatorio(conn)); // Assumindo que GerarRelatorio existe
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
                return column == 5; // Apenas a coluna de ações é editável
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

        try {
            vendas = dao.buscarRelatorioVendas(dataFiltro, vendedorFiltro, pagamentoFiltro, 
                                               dataInicioPersonalizada, dataFimPersonalizada);

            for (VendaRelatorio venda : vendas) {
                Object[] row = {
                    venda.getDataVenda(),
                    venda.getHorario(),
                    venda.getVendedor(),
                    venda.getValorTotal(),
                    venda.getFormasPagamento(),
                    "Detalhes"
                };
                modeloTabela.addRow(row);
            }

            if (vendas.isEmpty()) {
                modeloTabela.addRow(new Object[]{"Nenhuma venda encontrada", "", "", "", "", ""});
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Erro ao carregar relatório: " + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
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
            if (vendas != null && row >= 0 && row < vendas.size()) {
                String detalhes = vendas.get(row).getDetalhes();
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
            } else {
                JOptionPane.showMessageDialog(RelatorioVendas.this,
                        "Erro ao exibir detalhes: venda não encontrada.",
                        "Erro",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}