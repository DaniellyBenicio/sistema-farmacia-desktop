package views.Vendas;

import java.sql.Connection;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class PagamentoVenda extends JPanel {

    private JTable tabela;
    private DefaultTableModel modeloTabela;
    private JPanel camposPanel;
    private Connection conn;
    private JComboBox<String> comboPagamento, comboParcelas;

    public PagamentoVenda() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(new Color(0, 0, 0, 0));

        JLabel titulo = criarTitulo();
        add(Box.createRigidArea(new Dimension(0, 40)));
        add(titulo);

        JPanel camposPanel = criarCamposPanel();
        add(camposPanel);

        JPanel tabelaPanel = createTabelaPagamento();
        add(tabelaPanel);

        JPanel botoesPanel = criarBotoesPanel();
        add(botoesPanel);
        add(Box.createRigidArea(new Dimension(0, 80)));
    }

    private JLabel criarTitulo() {
        JLabel titulo = new JLabel("ÁREA DE PAGAMENTO");
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        return titulo;
    }

    private JPanel criarCamposPanel() {
        camposPanel = new JPanel();
        camposPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 10, 0, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        Color bordaAzulClaro = new Color(173, 216, 230);

        Font labelFont = new Font("Arial", Font.BOLD, 16);
        Font fieldFont = new Font("Arial", Font.PLAIN, 16);
        Dimension fieldSize = new Dimension(160, 35);

        JLabel subtotalLabel = new JLabel("Subtotal");
        subtotalLabel.setFont(labelFont);
        gbc.gridx = 0;
        gbc.gridy = 0;
        camposPanel.add(subtotalLabel, gbc);

        JTextField txtPrecoTotal = new JTextField();
        txtPrecoTotal.setPreferredSize(fieldSize);
        estilizarCampo(txtPrecoTotal, fieldFont);
        gbc.gridx = 0;
        gbc.gridy = 1;
        txtPrecoTotal.setBorder(BorderFactory.createLineBorder(bordaAzulClaro, 1));
        camposPanel.add(txtPrecoTotal, gbc);

        JLabel descontoLabel = new JLabel("Desconto");
        descontoLabel.setFont(labelFont);
        gbc.gridx = 2;
        gbc.gridy = 0;
        camposPanel.add(descontoLabel, gbc);

        JTextField txtDesconto = new JTextField();
        txtDesconto.setPreferredSize(fieldSize);
        estilizarCampo(txtDesconto, fieldFont);
        gbc.gridx = 2;
        gbc.gridy = 1;
        txtDesconto.setBorder(BorderFactory.createLineBorder(bordaAzulClaro, 1));
        camposPanel.add(txtDesconto, gbc);

        JLabel pagamentoLabel = new JLabel("Pagamento");
        pagamentoLabel.setFont(labelFont);
        gbc.gridx = 4;
        gbc.gridy = 0;
        camposPanel.add(pagamentoLabel, gbc);

        comboPagamento = new JComboBox<>(
                new String[] { "Selecione", "Cartão de Crédito", "Cartão de Débito", "Dinheiro", "PIX" });
        comboPagamento.setFont(fieldFont);
        comboPagamento.setPreferredSize(fieldSize);
        estilizarComboBox(comboPagamento, fieldFont);
        gbc.gridx = 4;
        gbc.gridy = 1;
        camposPanel.add(comboPagamento, gbc);

        JLabel parcelasLabel = new JLabel("Parcelas");
        parcelasLabel.setFont(labelFont);
        gbc.gridx = 6;
        gbc.gridy = 0;
        camposPanel.add(parcelasLabel, gbc);

        comboParcelas = new JComboBox<>(new String[] { "Selecione", "1", "2", "3", "4", "5", "6" });
        comboParcelas.setFont(fieldFont);
        comboParcelas.setPreferredSize(fieldSize);
        estilizarComboBox(comboParcelas, fieldFont);
        gbc.gridx = 6;
        gbc.gridy = 1;
        camposPanel.add(comboParcelas, gbc);

        return camposPanel;
    }

    private JPanel createTabelaPagamento() {
        String[] colunas = { "Ordem", "Forma de Pagamento", "Parcelas", "Valor" };

        modeloTabela = new DefaultTableModel(colunas, 0);

        tabela = new JTable(modeloTabela);
        tabela.setFillsViewportHeight(true);
        tabela.setRowHeight(35);
        tabela.setFont(new Font("Arial", Font.PLAIN, 12));
        tabela.getTableHeader().setFont(new Font("Arial", Font.BOLD, 16));
        tabela.getTableHeader().setReorderingAllowed(false);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        for (int i = 0; i < colunas.length; i++) {
            tabela.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        tabela.getColumnModel().getColumn(0).setPreferredWidth(10);
        tabela.getColumnModel().getColumn(1).setPreferredWidth(170);
        tabela.getColumnModel().getColumn(2).setPreferredWidth(30);
        tabela.getColumnModel().getColumn(3).setPreferredWidth(60);

        tabela.setCellSelectionEnabled(false);
        tabela.setRowSelectionAllowed(true);
        tabela.setColumnSelectionAllowed(false);

        for (int i = 0; i < tabela.getColumnModel().getColumnCount(); i++) {
            tabela.getColumnModel().getColumn(i).setResizable(false);
        }

        JScrollPane scrollPane = new JScrollPane(tabela);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 55, 30, 55));

        scrollPane.setPreferredSize(new Dimension(750, 200));

        JPanel painelTabela = new JPanel(new BorderLayout());
        painelTabela.add(scrollPane, BorderLayout.CENTER);

        JPanel totalPanel = createDemaisCampos();

        painelTabela.add(totalPanel, BorderLayout.SOUTH);

        return painelTabela;
    }

    private JPanel createDemaisCampos() {
        JPanel totalPanel = new JPanel();
        totalPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 10, 0, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        Color bordaAzulClaro = new Color(173, 216, 230);

        Font labelFont = new Font("Arial", Font.BOLD, 18);
        Font fieldFont = new Font("Arial", Font.PLAIN, 18);
        Dimension fieldSize = new Dimension(180, 40);

        JLabel totaLabel = new JLabel("Total");
        totaLabel.setFont(labelFont);
        gbc.gridx = 0;
        gbc.gridy = 0;
        totalPanel.add(totaLabel, gbc);

        JTextField txtTotal = new JTextField();
        txtTotal.setPreferredSize(fieldSize);
        estilizarCampo(txtTotal, fieldFont);
        gbc.gridx = 0;
        gbc.gridy = 1;
        txtTotal.setBorder(BorderFactory.createLineBorder(bordaAzulClaro, 1));
        totalPanel.add(txtTotal, gbc);

        JLabel valorPagoLabel = new JLabel("Valor Pago");
        valorPagoLabel.setFont(labelFont);
        gbc.gridx = 2;
        gbc.gridy = 0;
        totalPanel.add(valorPagoLabel, gbc);

        JTextField txtValorPago = new JTextField();
        txtValorPago.setPreferredSize(fieldSize);
        estilizarCampo(txtValorPago, fieldFont);
        gbc.gridx = 2;
        gbc.gridy = 1;
        txtValorPago.setBorder(BorderFactory.createLineBorder(bordaAzulClaro, 1));
        totalPanel.add(txtValorPago, gbc);

        JLabel valorRestanteLabel = new JLabel("Restante");
        valorRestanteLabel.setFont(labelFont);
        gbc.gridx = 4;
        gbc.gridy = 0;
        totalPanel.add(valorRestanteLabel, gbc);

        JTextField txtValorRestante = new JTextField();
        txtValorRestante.setPreferredSize(fieldSize);
        estilizarCampo(txtValorRestante, fieldFont);
        gbc.gridx = 4;
        gbc.gridy = 1;
        txtValorRestante.setBorder(BorderFactory.createLineBorder(bordaAzulClaro, 1));
        totalPanel.add(txtValorRestante, gbc);

        JLabel trocoLabel = new JLabel("Troco");
        trocoLabel.setFont(labelFont);
        gbc.gridx = 6;
        gbc.gridy = 0;
        totalPanel.add(trocoLabel, gbc);

        JTextField txtTroco = new JTextField();
        txtTroco.setPreferredSize(fieldSize);
        estilizarCampo(txtTroco, fieldFont);
        gbc.gridx = 6;
        gbc.gridy = 1;
        txtTroco.setBorder(BorderFactory.createLineBorder(bordaAzulClaro, 1));
        totalPanel.add(txtTroco, gbc);

        return totalPanel;
    }

    private void estilizarCampo(JTextField campo, Font font) {
        campo.setFont(font);
        campo.setPreferredSize(new Dimension(160, 35));
    }

    private void estilizarComboBox(JComboBox<String> comboBox, Font font) {
        comboBox.setBackground(Color.WHITE);
        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (isSelected) {
                    component.setBackground(new Color(24, 39, 55));
                    component.setForeground(Color.WHITE);
                } else {
                    component.setBackground(Color.WHITE);
                    component.setForeground(Color.BLACK);
                }
                return component;
            }
        });
        comboBox.setFont(font);
        comboBox.setFocusable(false);
        comboBox.setSelectedIndex(0);
    }

    private JPanel criarBotoesPanel() {
        JPanel botoesPanel = new JPanel();
        botoesPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        botoesPanel.setBorder(BorderFactory.createEmptyBorder(40, 0, 0, 0));

        JButton btnConfirmarVenda = new JButton("Confirmar Venda");
        btnConfirmarVenda.setFont(new Font("Arial", Font.BOLD, 17));
        btnConfirmarVenda.setBackground(new Color(24, 39, 55));
        btnConfirmarVenda.setForeground(Color.WHITE);
        btnConfirmarVenda.setFocusPainted(false);
        btnConfirmarVenda.setPreferredSize(new Dimension(170, 30));

        botoesPanel.add(btnConfirmarVenda);

        return botoesPanel;
    }
}
