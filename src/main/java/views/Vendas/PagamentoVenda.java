package views.Vendas;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Map;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import dao.Cliente.ClienteDAO;
import dao.ItemVenda.ItemVendaDAO;
import dao.Medicamento.MedicamentoDAO;
import dao.Produto.ProdutoDAO;
import dao.Venda.VendaDAO;
import main.ConexaoBD;
import models.ItemVenda.ItemVenda;
import models.Medicamento.Medicamento;
import models.Produto.Produto;
import models.Venda.Venda;
import views.BarrasSuperiores.PainelSuperior;

import java.awt.*;
import java.awt.event.ItemEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class PagamentoVenda extends JPanel {

    private JTable tabela;
    private DefaultTableModel modeloTabela;
    private JPanel camposPanel;
    private Connection conn;
    private JComboBox<String> comboPagamento, comboParcelas;
    private JTextField txtPrecoTotal, txtDesconto, txtTotal, txtValorPago, txtValorRestante, txtTroco;
    private BigDecimal subtotal;
    private int ordemPagamento = 1;
    private ResumoDaVenda resumoDaVenda;
    private Integer funcionarioId;
    private String cpfCliente;
    private Integer clienteId;

    public PagamentoVenda(BigDecimal totalGeral, ResumoDaVenda resumoDaVenda) {
        this.subtotal = totalGeral;
        this.resumoDaVenda = resumoDaVenda;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(new Color(0, 0, 0, 0));

        JLabel titulo = criarTitulo();
        add(Box.createRigidArea(new Dimension(0, 40)));
        add(titulo);

        camposPanel = criarCamposPanel(totalGeral);
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

    private JPanel criarCamposPanel(BigDecimal totalGeral) {
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

        txtPrecoTotal = new JTextField();
        txtPrecoTotal.setPreferredSize(fieldSize);
        estilizarCampo(txtPrecoTotal, fieldFont);
        gbc.gridx = 0;
        gbc.gridy = 1;
        txtPrecoTotal.setBorder(BorderFactory.createLineBorder(bordaAzulClaro, 1));
        txtPrecoTotal.setText(totalGeral.setScale(2, RoundingMode.HALF_UP).toString().replace(".", ","));
        txtPrecoTotal.setEditable(false);
        camposPanel.add(txtPrecoTotal, gbc);

        JLabel descontoLabel = new JLabel("Desconto");
        descontoLabel.setFont(labelFont);
        gbc.gridx = 2;
        gbc.gridy = 0;
        camposPanel.add(descontoLabel, gbc);

        txtDesconto = new JTextField();
        txtDesconto.setPreferredSize(fieldSize);
        estilizarCampo(txtDesconto, fieldFont);
        gbc.gridx = 2;
        gbc.gridy = 1;
        txtDesconto.setBorder(BorderFactory.createLineBorder(bordaAzulClaro, 1));
        txtDesconto.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                atualizarTotal();
            }

            public void removeUpdate(DocumentEvent e) {
                atualizarTotal();
            }

            public void changedUpdate(DocumentEvent e) {
                atualizarTotal();
            }
        });
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

        txtTotal = new JTextField();
        txtTotal.setPreferredSize(fieldSize);
        estilizarCampo(txtTotal, fieldFont);
        gbc.gridx = 0;
        gbc.gridy = 1;
        txtTotal.setBorder(BorderFactory.createLineBorder(bordaAzulClaro, 1));
        txtTotal.setEditable(false);
        txtTotal.setText(subtotal.setScale(2, RoundingMode.HALF_UP).toString().replace(".", ","));
        totalPanel.add(txtTotal, gbc);

        JLabel valorPagoLabel = new JLabel("Valor Pago");
        valorPagoLabel.setFont(labelFont);
        gbc.gridx = 2;
        gbc.gridy = 0;
        totalPanel.add(valorPagoLabel, gbc);

        txtValorPago = new JTextField();
        txtValorPago.setPreferredSize(fieldSize);
        estilizarCampo(txtValorPago, fieldFont);
        gbc.gridx = 2;
        gbc.gridy = 1;
        txtValorPago.setBorder(BorderFactory.createLineBorder(bordaAzulClaro, 1));
        txtValorPago.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                atualizarRestanteETroco();
            }

            public void removeUpdate(DocumentEvent e) {
                atualizarRestanteETroco();
            }

            public void changedUpdate(DocumentEvent e) {
                atualizarRestanteETroco();
            }
        });
        totalPanel.add(txtValorPago, gbc);

        JLabel valorRestanteLabel = new JLabel("Restante");
        valorRestanteLabel.setFont(labelFont);
        gbc.gridx = 4;
        gbc.gridy = 0;
        totalPanel.add(valorRestanteLabel, gbc);

        txtValorRestante = new JTextField();
        txtValorRestante.setPreferredSize(fieldSize);
        estilizarCampo(txtValorRestante, fieldFont);
        gbc.gridx = 4;
        gbc.gridy = 1;
        txtValorRestante.setBorder(BorderFactory.createLineBorder(bordaAzulClaro, 1));
        txtValorRestante.setEditable(false);
        totalPanel.add(txtValorRestante, gbc);

        JLabel trocoLabel = new JLabel("Troco");
        trocoLabel.setFont(labelFont);
        gbc.gridx = 6;
        gbc.gridy = 0;
        totalPanel.add(trocoLabel, gbc);

        txtTroco = new JTextField();
        txtTroco.setPreferredSize(fieldSize);
        estilizarCampo(txtTroco, fieldFont);
        gbc.gridx = 6;
        gbc.gridy = 1;
        txtTroco.setBorder(BorderFactory.createLineBorder(bordaAzulClaro, 1));
        txtTroco.setEditable(false);
        totalPanel.add(txtTroco, gbc);

        comboPagamento.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                String formaSelecionada = (String) comboPagamento.getSelectedItem();
                comboParcelas.setEnabled("Cartão de Crédito".equals(formaSelecionada));
                if (!comboParcelas.isEnabled()) {
                    comboParcelas.setSelectedIndex(0);
                }
            }
        });

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
        btnConfirmarVenda.addActionListener(e -> confirmarPagamento());
        botoesPanel.add(btnConfirmarVenda);

        return botoesPanel;
    }

    private void atualizarTotal() {
        try {
            String descontoStr = txtDesconto.getText().replace(",", ".");
            BigDecimal desconto = descontoStr.isEmpty() ? BigDecimal.ZERO : new BigDecimal(descontoStr);
            BigDecimal total = subtotal.subtract(desconto);
            if (total.compareTo(BigDecimal.ZERO) < 0) {
                total = BigDecimal.ZERO;
            }
            txtTotal.setText(total.setScale(2, RoundingMode.HALF_UP).toString().replace(".", ","));
            atualizarRestanteETroco();
        } catch (NumberFormatException e) {
            txtTotal.setText(subtotal.setScale(2, RoundingMode.HALF_UP).toString().replace(".", ","));
        }
    }

    private void atualizarRestanteETroco() {
        try {
            String totalStr = txtTotal.getText().replace(",", ".");
            String valorPagoStr = txtValorPago.getText().replace(",", ".");
            BigDecimal total = new BigDecimal(totalStr);
            BigDecimal valorPago = valorPagoStr.isEmpty() ? BigDecimal.ZERO : new BigDecimal(valorPagoStr);

            BigDecimal totalPagoTabela = BigDecimal.ZERO;
            for (int i = 0; i < modeloTabela.getRowCount(); i++) {
                String valorTabela = modeloTabela.getValueAt(i, 3).toString().replace(",", ".");
                totalPagoTabela = totalPagoTabela.add(new BigDecimal(valorTabela));
            }

            BigDecimal totalPago = totalPagoTabela.add(valorPago);
            BigDecimal restante = total.subtract(totalPago);
            if (restante.compareTo(BigDecimal.ZERO) < 0) {
                restante = BigDecimal.ZERO;
            }
            txtValorRestante.setText(restante.setScale(2, RoundingMode.HALF_UP).toString().replace(".", ","));

            BigDecimal troco = totalPago.subtract(total);
            if (troco.compareTo(BigDecimal.ZERO) < 0) {
                troco = BigDecimal.ZERO;
            }
            txtTroco.setText(troco.setScale(2, RoundingMode.HALF_UP).toString().replace(".", ","));
        } catch (NumberFormatException e) {
            txtValorRestante.setText(txtTotal.getText());
            txtTroco.setText("0,00");
        }
    }

    private void confirmarPagamento() {
        try {
            String formaPagamento = (String) comboPagamento.getSelectedItem();
            String parcelas = (String) comboParcelas.getSelectedItem();
            String valorPagoStr = txtValorPago.getText().replace(",", ".");

            if ("Selecione".equals(formaPagamento) || valorPagoStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Selecione a forma de pagamento e informe o valor pago.");
                return;
            }

            BigDecimal valorPago = new BigDecimal(valorPagoStr);
            if (valorPago.compareTo(BigDecimal.ZERO) <= 0) {
                JOptionPane.showMessageDialog(this, "O valor pago deve ser maior que zero.");
                return;
            }

            modeloTabela.addRow(new Object[] {
                    ordemPagamento++,
                    formaPagamento,
                    "Cartão de Crédito".equals(formaPagamento) ? parcelas : "1",
                    valorPago.setScale(2, RoundingMode.HALF_UP).toString().replace(".", ",")
            });

            comboPagamento.setSelectedIndex(0);
            comboParcelas.setSelectedIndex(0);
            txtValorPago.setText("");
            atualizarRestanteETroco();

            String restanteStr = txtValorRestante.getText().replace(",", ".");
            BigDecimal restante = new BigDecimal(restanteStr);
            if (restante.compareTo(BigDecimal.ZERO) == 0) {
                salvarDados();
                JOptionPane.showMessageDialog(this, "Pagamento concluído e venda registrada!");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Valor pago inválido. Use o formato correto (ex: 30,00)");
        }
    }

    private void salvarDados() {
        try (Connection conn = ConexaoBD.getConnection()) {
            conn.setAutoCommit(false);

            String cpfCliente = resumoDaVenda.lblCpfCliente.getText().replace("CPF: ", "").trim();
            int funcionarioId = PainelSuperior.getIdFuncionarioAtual();

            Integer clienteId = null;
            if (!cpfCliente.isEmpty()) {
                clienteId = ClienteDAO.buscarClientePorCpfRetornaId(conn, cpfCliente);
            }

            String descontoStr = txtDesconto.getText().replace(",", ".");
            BigDecimal desconto = descontoStr.isEmpty() ? BigDecimal.ZERO : new BigDecimal(descontoStr);
            LocalDateTime agora = LocalDateTime.now();

            int vendaId = -1;
            for (int i = 0; i < modeloTabela.getRowCount(); i++) {
                String formaPagamentoStr = (String) modeloTabela.getValueAt(i, 1);
                String formaPagamento = converterFormaPagamento(formaPagamentoStr);

                String valorStr = modeloTabela.getValueAt(i, 3).toString().replace(",", ".");
                BigDecimal valorPago = new BigDecimal(valorStr);

                Venda venda = new Venda(clienteId, funcionarioId, valorPago, desconto, formaPagamento, agora);
                int idGerado = VendaDAO.realizarVenda(conn, venda);
                if (i == 0 && idGerado != -1) {
                    vendaId = idGerado;
                } else if (idGerado == -1) {
                    throw new SQLException("Erro ao registrar venda na linha " + i);
                }
            }

            for (String ordem : resumoDaVenda.itensMap.keySet()) {
                String[] dadosItem = resumoDaVenda.getDadosItemPorOrdem(ordem);
                int idItem = Integer.parseInt(dadosItem[1]);
                int quantidade = Integer.parseInt(dadosItem[3].replace(",", ".").trim());
                BigDecimal precoUnitario = new BigDecimal(dadosItem[4].replace(",", "."));
                BigDecimal descontoItem = new BigDecimal(dadosItem[5].replace(",", "."));
                BigDecimal subtotal = precoUnitario.multiply(BigDecimal.valueOf(quantidade));

                ItemVenda itemVenda;
                Produto produto = null;
                Medicamento medicamento = null;

                try {
                    produto = ProdutoDAO.buscarPorId(conn, idItem);
                    itemVenda = new ItemVenda(vendaId, produto, null, quantidade, precoUnitario, subtotal,
                            descontoItem);
                } catch (SQLException e) {
                    medicamento = MedicamentoDAO.buscarPorId(conn, idItem);
                    itemVenda = new ItemVenda(vendaId, null, medicamento, quantidade, precoUnitario, subtotal,
                            descontoItem);
                }

                ItemVendaDAO.inserirItemVenda(conn, itemVenda);
            }

            conn.commit();
            JOptionPane.showMessageDialog(this, "Pagamento concluído e venda registrada!");
            Window dialog = SwingUtilities.getWindowAncestor(this);
            if (dialog != null) {
                dialog.dispose();
            }

        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            JOptionPane.showMessageDialog(this, "Erro ao salvar venda: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String converterFormaPagamento(String forma) {
        switch (forma) {
            case "Dinheiro":
                return "DINHEIRO";
            case "Cartão de Crédito":
                return "CARTAO_CREDITO";
            case "Cartão de Débito":
                return "CARTAO_DEBITO";
            case "PIX":
                return "PIX";
            default:
                throw new IllegalArgumentException("Forma de pagamento inválida: " + forma);
        }
    }
}