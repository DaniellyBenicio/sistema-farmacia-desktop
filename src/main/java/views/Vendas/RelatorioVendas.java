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
    private List<VendaRelatorio> vendas;
    private CardLayout layoutCartao;
    private JPanel painelCentral;

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

    public void setLayoutDetails(CardLayout layoutCartao, JPanel painelCentral) {
        this.layoutCartao = layoutCartao;
        this.painelCentral = painelCentral;
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
        btnVoltar.setFont(new Font("Arial", Font.PLAIN, 17));
        btnVoltar.setFocusPainted(false);
        btnVoltar.setBorderPainted(false);
        btnVoltar.setContentAreaFilled(false);
        btnVoltar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnVoltar.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnVoltar.setForeground(new Color(50, 100, 150));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnVoltar.setForeground(Color.BLACK);
            }
        });

        btnVoltar.addActionListener(e -> voltarTelaAnterior());

        painelVoltar.add(btnVoltar);
        return painelVoltar;
    }

    private void voltarTelaAnterior() {
        if (painelCentral instanceof JPanel) {
            for (Component component : painelCentral.getComponents()) {
                if (component instanceof GerarRelatorio) {
                    ((GerarRelatorio) component).limparFiltros();
                    break;
                }
            }
        }
        layoutCartao.show(painelCentral, "GerarRelatorio");
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

        String[] colunas = { "Data", "Horário", "Vendedor", "Valor Total", "Forma de Pagamento", "Ações" };
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5;
            }
        };

        tabelaRelatorio = new JTable(modeloTabela);
        tabelaRelatorio.setFillsViewportHeight(true);
        tabelaRelatorio.setRowHeight(45);
        tabelaRelatorio.setFont(new Font("Arial", Font.PLAIN, 12));
        tabelaRelatorio.getTableHeader().setFont(new Font("Arial", Font.BOLD, 15));
        tabelaRelatorio.getTableHeader().setReorderingAllowed(false);
        tabelaRelatorio.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaRelatorio.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        for (int i = 0; i < colunas.length - 1; i++) {
            tabelaRelatorio.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        tabelaRelatorio.getColumnModel().getColumn(5).setCellRenderer(new ButtonRenderer());
        tabelaRelatorio.getColumnModel().getColumn(5).setCellEditor(new ButtonEditor(new JTextField()));

        tabelaRelatorio.getColumnModel().getColumn(0).setPreferredWidth(150);
        tabelaRelatorio.getColumnModel().getColumn(1).setPreferredWidth(100);
        tabelaRelatorio.getColumnModel().getColumn(2).setPreferredWidth(60);
        tabelaRelatorio.getColumnModel().getColumn(3).setPreferredWidth(60);
        tabelaRelatorio.getColumnModel().getColumn(4).setPreferredWidth(80);
        tabelaRelatorio.getColumnModel().getColumn(5).setPreferredWidth(60);

        tabelaRelatorio.setCellSelectionEnabled(false);
        tabelaRelatorio.setRowSelectionAllowed(false);
        tabelaRelatorio.setColumnSelectionAllowed(false);

        JScrollPane scrollPane = new JScrollPane(tabelaRelatorio);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 30, 57, 30));

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
            vendas = dao.buscarRelatorioVendas(
                    dataFiltro,
                    vendedorFiltro,
                    pagamentoFiltro,
                    dataInicioPersonalizada,
                    dataFimPersonalizada);

            for (VendaRelatorio venda : vendas) {
                Object[] row = {
                        venda.getDataVenda(),
                        venda.getHorario(),
                        venda.getVendedor(),
                        venda.getValorTotal(),
                        formatarFormaPagamento(venda.getFormasPagamento()),
                        "Detalhes"
                };
                modeloTabela.addRow(row);
            }

            if (vendas.isEmpty()) {
                modeloTabela.addRow(new Object[] { "Nenhuma venda encontrada", "", "", "", "", "" });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Erro ao carregar relatório: " + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private String formatarFormaPagamento(String formasPagamento) {
        if (formasPagamento == null || formasPagamento.isEmpty()) {
            return "Não informado";
        }
    
        String[] pagamentos = formasPagamento.split(", ");
        StringBuilder resultado = new StringBuilder();
    
        for (int i = 0; i < pagamentos.length; i++) {
            String pagamento = pagamentos[i].trim();
            switch (pagamento) {
                case "DINHEIRO":
                    resultado.append("Dinheiro");
                    break;
                case "CARTAO_CREDITO":
                    resultado.append("Cartão de Crédito");
                    break;
                case "CARTAO_DEBITO":
                    resultado.append("Cartão de Débito");
                    break;
                case "PIX":
                    resultado.append("PIX");
                    break;
                default:
                    resultado.append(pagamento);
                    break;
            }
            if (i < pagamentos.length - 1) {
                resultado.append(", ");
            }
        }
        return resultado.toString();
    }

    private void exportarRelatorio() {
        ImprimirRelatorioGeral relatorioPrinter = new ImprimirRelatorioGeral(tabelaRelatorio);
        relatorioPrinter.imprimir();
    }

    class ButtonRenderer extends JPanel implements TableCellRenderer {
        private final JButton button;

        public ButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 0, 10));
            setBackground(Color.WHITE);

            button = criarBotao("DETALHES", new Color(24, 39, 55), Color.WHITE);

            add(button);
        }

        private JButton criarBotao(String texto, Color corFundo, Color corTexto) {
            JButton botao = new JButton(texto);
            botao.setBackground(corFundo);
            botao.setForeground(corTexto);
            botao.setBorderPainted(false);
            botao.setFocusPainted(false);
            botao.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            return botao;
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
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
            panel.setBackground(Color.WHITE);

            button = criarBotao("DETALHES", new Color(24, 39, 55), Color.WHITE);

            button.addActionListener(e -> fireEditingStopped());
            panel.add(button);
        }

        private JButton criarBotao(String texto, Color corFundo, Color corTexto) {
            JButton botao = new JButton(texto);
            botao.setBackground(corFundo);
            botao.setForeground(corTexto);
            botao.setBorderPainted(false);
            botao.setFocusPainted(false);
            botao.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            return botao;
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            this.row = row;
            JPanel painel = new JPanel();
            painel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
            painel.setBackground(Color.WHITE);
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            mostrarDetalhesVenda(row);
            return "";
        }

        private void mostrarDetalhesVenda(int row) {
            if (vendas != null && row >= 0 && row < vendas.size()) {
                VendaRelatorio venda = vendas.get(row);
                String detalhes = venda.getDetalhes();

                if (detalhes.length() > 0) {
                    JDialog dialog = new JDialog();
                    dialog.setTitle("Detalhes da Venda");
                    dialog.setModal(true);
                    dialog.setSize(1100, 600);
                    dialog.setLocationRelativeTo(RelatorioVendas.this);

                    JPanel panel = new JPanel(new BorderLayout());
                    panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
                    panel.setBackground(Color.WHITE);

                    JLabel titleLabel = new JLabel("Informações Gerais");
                    titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
                    titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
                    panel.add(titleLabel, BorderLayout.NORTH);

                    JTextArea textArea = new JTextArea(detalhes.toString());
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