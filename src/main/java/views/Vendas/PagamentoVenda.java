package views.Vendas;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import dao.Cliente.ClienteDAO;
import dao.ItemVenda.ItemVendaDAO;
import dao.Medicamento.MedicamentoDAO;
import dao.Pagamento.PagamentoDAO;
import dao.Produto.ProdutoDAO;
import dao.Venda.VendaDAO;
import main.ConexaoBD;
import models.ItemVenda.ItemVenda;
import models.Medicamento.Medicamento;
import models.Pagamento.Pagamento;
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
    DefaultTableModel modeloTabela;
    private JPanel camposPanel;
    private Connection conn;
    private JComboBox<String> comboPagamento, comboParcelas;
    private JTextField txtPrecoTotal;
    JTextField txtDesconto;
    private JTextField txtTotal;
    JTextField txtValorPago;
    private JTextField txtValorRestante;
    private JTextField txtTroco;
    private BigDecimal subtotal;
    private int ordemPagamento = 1;
    private ResumoDaVenda resumoDaVenda;
    private RealizarVenda realizarVenda;
    private Integer funcionarioId;
    private String cpfCliente;
    private Integer clienteId;

    public PagamentoVenda(BigDecimal totalGeral, ResumoDaVenda resumoDaVenda, RealizarVenda realizarVenda) {
        this.subtotal = totalGeral;
        this.resumoDaVenda = resumoDaVenda;
        this.realizarVenda = realizarVenda;
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
        String[] colunas = { "Ordem", "Forma de Pagamento", "Parcelas", "Valor Total", "Desconto", "Valor Pago",
                "Troco" };
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
        tabela.getColumnModel().getColumn(4).setPreferredWidth(60);
        tabela.getColumnModel().getColumn(5).setPreferredWidth(60);
        tabela.getColumnModel().getColumn(5).setPreferredWidth(60);

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
            String descontoStr = txtDesconto.getText().replace(",", ".").trim();
            BigDecimal desconto = descontoStr.isEmpty() ? BigDecimal.ZERO : new BigDecimal(descontoStr);

            if (desconto.compareTo(subtotal) > 0) {
                JOptionPane.showMessageDialog(this, "O desconto não pode ultrapassar o subtotal de " +
                        subtotal.setScale(2, RoundingMode.HALF_UP).toString().replace(".", ",") + ".");
                txtDesconto.setText("");
                txtTotal.setText(subtotal.setScale(2, RoundingMode.HALF_UP).toString().replace(".", ","));
            } else {
                BigDecimal total = subtotal.subtract(desconto);
                if (total.compareTo(BigDecimal.ZERO) < 0) {
                    total = BigDecimal.ZERO;
                }
                txtTotal.setText(total.setScale(2, RoundingMode.HALF_UP).toString().replace(".", ","));
            }
            atualizarRestanteETroco();
        } catch (NumberFormatException e) {
            txtTotal.setText(subtotal.setScale(2, RoundingMode.HALF_UP).toString().replace(".", ","));
            atualizarRestanteETroco();
        }
    }

    private void atualizarRestanteETroco() {
        try {
            String valorPagoStr = txtValorPago.getText().replace(",", ".");
            BigDecimal valorPago = valorPagoStr.isEmpty() ? BigDecimal.ZERO : new BigDecimal(valorPagoStr);

            BigDecimal totalPagoTabela = BigDecimal.ZERO;
            for (int i = 0; i < modeloTabela.getRowCount(); i++) {
                String valorTabela = modeloTabela.getValueAt(i, 5).toString().replace(",", ".");
                totalPagoTabela = totalPagoTabela.add(new BigDecimal(valorTabela));
            }

            BigDecimal totalPago = totalPagoTabela.add(valorPago);
            String descontoStr = txtDesconto.getText().replace(",", ".").trim();
            BigDecimal desconto = descontoStr.isEmpty() ? BigDecimal.ZERO : new BigDecimal(descontoStr);
            BigDecimal totalBase = subtotal.subtract(desconto);

            BigDecimal restanteAntes = totalBase.subtract(totalPagoTabela);
            if (restanteAntes.compareTo(BigDecimal.ZERO) < 0) {
                restanteAntes = BigDecimal.ZERO;
            }

            BigDecimal restante = totalBase.subtract(totalPago);
            if (restante.compareTo(BigDecimal.ZERO) < 0) {
                restante = BigDecimal.ZERO;
            }
            txtValorRestante.setText(restante.setScale(2, RoundingMode.HALF_UP).toString().replace(".", ","));

            BigDecimal troco = BigDecimal.ZERO;
            String formaPagamento = (String) comboPagamento.getSelectedItem();
            if (formaPagamento != null && formaPagamento.equalsIgnoreCase("Dinheiro")) {
                if (valorPago.compareTo(restanteAntes) > 0) {
                    troco = valorPago.subtract(restanteAntes);
                }
            }
            txtTroco.setText(troco.setScale(2, RoundingMode.HALF_UP).toString().replace(".", ","));
        } catch (NumberFormatException e) {
            txtValorRestante.setText(subtotal.setScale(2, RoundingMode.HALF_UP).toString().replace(".", ","));
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

            if ("Cartão de Crédito".equals(formaPagamento) && "Selecione".equals(parcelas)) {
                JOptionPane.showMessageDialog(this, "Selecione o número de parcelas para o cartão de crédito.");
                return;
            }

            BigDecimal valorPago = new BigDecimal(valorPagoStr);
            if (valorPago.compareTo(BigDecimal.ZERO) <= 0) {
                JOptionPane.showMessageDialog(this, "O valor pago deve ser maior que zero.");
                return;
            }

            String descontoStr = txtDesconto.getText().replace(",", ".").trim();
            BigDecimal desconto = descontoStr.isEmpty() ? BigDecimal.ZERO : new BigDecimal(descontoStr);
            BigDecimal totalBase = subtotal.subtract(desconto);

            BigDecimal totalPagoTabela = BigDecimal.ZERO;
            for (int i = 0; i < modeloTabela.getRowCount(); i++) {
                String valorTabela = modeloTabela.getValueAt(i, 5).toString().replace(",", ".");
                totalPagoTabela = totalPagoTabela.add(new BigDecimal(valorTabela));
            }

            BigDecimal restanteAntes = totalBase.subtract(totalPagoTabela);
            if (restanteAntes.compareTo(BigDecimal.ZERO) < 0) {
                restanteAntes = BigDecimal.ZERO;
            }

            BigDecimal totalPago = totalPagoTabela.add(valorPago);
            BigDecimal restante = totalBase.subtract(totalPago);
            if (restante.compareTo(BigDecimal.ZERO) < 0) {
                restante = BigDecimal.ZERO;
            }

            BigDecimal troco = BigDecimal.ZERO;
            if (formaPagamento.equalsIgnoreCase("Dinheiro")) {
                if (valorPago.compareTo(restanteAntes) > 0) {
                    troco = valorPago.subtract(restanteAntes);
                }
            }

            String valorTotalTabela;
            if (modeloTabela.getRowCount() == 0) {
                valorTotalTabela = totalBase.setScale(2, RoundingMode.HALF_UP).toString().replace(".", ",");
            } else {
                valorTotalTabela = restanteAntes.setScale(2, RoundingMode.HALF_UP).toString().replace(".", ",");
            }

            modeloTabela.addRow(new Object[] {
                    ordemPagamento++,
                    formaPagamento,
                    "Cartão de Crédito".equals(formaPagamento) ? parcelas : "1",
                    valorTotalTabela,
                    modeloTabela.getRowCount() == 0
                            ? desconto.setScale(2, RoundingMode.HALF_UP).toString().replace(".", ",")
                            : "0,00",
                    valorPago.setScale(2, RoundingMode.HALF_UP).toString().replace(".", ","),
                    troco.setScale(2, RoundingMode.HALF_UP).toString().replace(".", ",")
            });

            if (modeloTabela.getRowCount() == 1) {
                txtDesconto.setEditable(false);
            }

            comboPagamento.setSelectedIndex(0);
            comboParcelas.setSelectedIndex(0);
            txtValorPago.setText("");
            atualizarRestanteETroco();

            if (restante.compareTo(BigDecimal.ZERO) == 0) {
                salvarDados();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Valor pago inválido. Use o formato correto (ex: 30,00)");
            txtValorPago.setText("");
        }
    }

    private void salvarDados() {
        Connection conn = null;
        try {
            conn = ConexaoBD.getConnection();
            conn.setAutoCommit(false);

            String cpfCliente = resumoDaVenda.lblCpfCliente.getText().replace("CPF do Consumidor: ", "").trim();
            int funcionarioId = PainelSuperior.getIdFuncionarioAtual();

            Integer clienteId = null;
            if (!cpfCliente.equals("Não Identificado")) {
                clienteId = ClienteDAO.buscarClientePorCpfRetornaId(conn, cpfCliente);
            }

            String descontoStr = txtDesconto.getText().replace(",", ".").trim();
            BigDecimal desconto = descontoStr.isEmpty() ? BigDecimal.ZERO : new BigDecimal(descontoStr);
            LocalDateTime agora = LocalDateTime.now();

            BigDecimal valorTotalVenda = subtotal.subtract(desconto);

            BigDecimal totalPagoNaTabela = BigDecimal.ZERO;
            for (int i = 0; i < modeloTabela.getRowCount(); i++) {
                String valorStr = modeloTabela.getValueAt(i, 5).toString().replace(",", ".").trim(); // Coluna "Valor
                                                                                                     // Pago"
                BigDecimal valorPago = new BigDecimal(valorStr);
                totalPagoNaTabela = totalPagoNaTabela.add(valorPago);
            }

            if (totalPagoNaTabela.compareTo(valorTotalVenda) < 0) {
                throw new SQLException("O total pago (" + totalPagoNaTabela + ") é menor que o valor total da venda ("
                        + valorTotalVenda + "). Adicione mais pagamentos.");
            }

            Venda venda = new Venda(clienteId, funcionarioId, valorTotalVenda, desconto, agora);
            int vendaId = VendaDAO.realizarVenda(conn, venda);
            if (vendaId == -1) {
                throw new SQLException("Erro ao registrar a venda.");
            }

            for (int i = 0; i < modeloTabela.getRowCount(); i++) {
                String formaPagamentoStr = (String) modeloTabela.getValueAt(i, 1);
                String formaPagamento = converterFormaPagamento(formaPagamentoStr);
                String valorStr = modeloTabela.getValueAt(i, 5).toString().replace(",", ".").trim();
                BigDecimal valorPago = new BigDecimal(valorStr);

                Pagamento pagamento = new Pagamento(vendaId, Pagamento.FormaPagamento.valueOf(formaPagamento),
                        valorPago);
                PagamentoDAO.cadastrarPagamento(conn, pagamento);
            }

            for (String ordem : resumoDaVenda.itensMap.keySet()) {
                String[] dadosItem = resumoDaVenda.getDadosItemPorOrdem(ordem);
                int idItem = Integer.parseInt(dadosItem[1].trim());
                String nomeCompleto = dadosItem[2].trim();
                String nomeBase = nomeCompleto.split(" ")[0];
                int quantidade = Integer.parseInt(dadosItem[3].replace(",", ".").trim());
                BigDecimal precoUnitario = new BigDecimal(dadosItem[4].replace(",", ".").trim());
                BigDecimal descontoItem = new BigDecimal(dadosItem[5].replace(",", ".").trim());
                BigDecimal subtotalItem = precoUnitario.multiply(BigDecimal.valueOf(quantidade));

                ItemVenda itemVenda = new ItemVenda();
                itemVenda.setVendaId(vendaId);
                itemVenda.setDesconto(descontoItem);
                itemVenda.setPrecoUnit(precoUnitario);
                itemVenda.setQnt(quantidade);
                itemVenda.setSubtotal(subtotalItem);

                String tipo = ItemVendaDAO.verificarTipoItem(conn, nomeBase);
                if ("Medicamento".equals(tipo)) {
                    Medicamento medicamento = MedicamentoDAO.buscarPorId(conn, idItem);
                    itemVenda.setMedicamento(medicamento);
                    itemVenda.setProduto(null);
                } else if ("Produto".equals(tipo)) {
                    Produto produto = ProdutoDAO.buscarPorId(conn, idItem);
                    itemVenda.setProduto(produto);
                    itemVenda.setMedicamento(null);
                } else {
                    throw new SQLException("Item '" + nomeBase + "' não identificado como produto ou medicamento.");
                }

                ItemVendaDAO.inserirItemVenda(conn, itemVenda, nomeBase);
            }

            conn.commit();
            JOptionPane.showMessageDialog(this, "Pagamento concluído e venda registrada!");

            NotaFiscal.exibirNotaFiscal(resumoDaVenda, this);
            Window dialog = SwingUtilities.getWindowAncestor(this);
            if (dialog != null) {
                dialog.dispose();
            }
            if (realizarVenda != null) {
                realizarVenda.reiniciarVenda();
            }

        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback();
                    JOptionPane.showMessageDialog(this, "Erro ao processar venda: " + e.getMessage());
                }
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erro ao realizar rollback: " + rollbackEx.getMessage());
            }
            e.printStackTrace();
        } catch (Exception e) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro inesperado: " + e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException closeEx) {
                closeEx.printStackTrace();
            }
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