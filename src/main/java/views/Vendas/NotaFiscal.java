package views.Vendas;

import java.awt.*;
import java.awt.print.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class NotaFiscal extends JPanel implements Printable {

    private ResumoDaVenda resumoDaVenda;
    private PagamentoVenda pagamentoVenda;
    private JLabel lblDataHora;
    JLabel lblAtendente;

    public NotaFiscal(ResumoDaVenda resumoDaVenda, PagamentoVenda pagamentoVenda) {
        this.resumoDaVenda = resumoDaVenda;
        this.pagamentoVenda = pagamentoVenda;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        initializeComponents();
    }

    private void initializeComponents() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);

        JPanel headerPanel = new JPanel(new GridBagLayout());
        headerPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 0, 2, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;

        JLabel lblTitulo = new JLabel("NOTA FISCAL", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 24));
        headerPanel.add(lblTitulo, gbc);

        JLabel lblEmpresa = new JLabel("Farmácia XYZ - CNPJ: 12.345.678/0001-99", SwingConstants.CENTER);
        lblEmpresa.setFont(new Font("Arial", Font.PLAIN, 14));
        headerPanel.add(lblEmpresa, gbc);

        JLabel lblEndereco = new JLabel("Rua Exemplo, 123 - Centro, Cidade - UF", SwingConstants.CENTER);
        lblEndereco.setFont(new Font("Arial", Font.PLAIN, 12));
        headerPanel.add(lblEndereco, gbc);

        lblDataHora = new JLabel("Data/Hora: " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()),
                SwingConstants.CENTER);
        lblDataHora.setFont(new Font("Arial", Font.PLAIN, 12));
        headerPanel.add(lblDataHora, gbc);

        lblAtendente = new JLabel(resumoDaVenda.lblAtendente.getText(), SwingConstants.CENTER);
        lblAtendente.setFont(new Font("Arial", Font.PLAIN, 12));
        headerPanel.add(lblAtendente, gbc);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);

        JPanel painelCliente = new JPanel();
        painelCliente.setLayout(new BoxLayout(painelCliente, BoxLayout.Y_AXIS));
        painelCliente.setBackground(Color.WHITE);
        painelCliente.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JLabel lblClienteInfo = new JLabel("Cliente:");
        lblClienteInfo.setFont(new Font("Arial", Font.BOLD, 14));
        painelCliente.add(lblClienteInfo);

        JLabel lblNomeCliente = new JLabel(resumoDaVenda.lblNomeCliente.getText());
        lblNomeCliente.setFont(new Font("Arial", Font.PLAIN, 14));
        painelCliente.add(lblNomeCliente);

        JLabel lblCpfCliente = new JLabel(resumoDaVenda.lblCpfCliente.getText());
        lblCpfCliente.setFont(new Font("Arial", Font.PLAIN, 14));
        painelCliente.add(lblCpfCliente);

        contentPanel.add(painelCliente, BorderLayout.NORTH);

        JTable tabelaItens = criarTabelaItens();
        JScrollPane scrollPane = new JScrollPane(tabelaItens);
        scrollPane.setPreferredSize(new Dimension(600, 200));
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel painelTotais = new JPanel();
        painelTotais.setLayout(new BoxLayout(painelTotais, BoxLayout.Y_AXIS));
        painelTotais.setBackground(Color.WHITE);
        painelTotais.add(criarPainelTotais());

        contentPanel.add(painelTotais, BorderLayout.SOUTH);

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);

        JButton btnImprimir = new JButton("Imprimir");
        btnImprimir.setFont(new Font("Arial", Font.BOLD, 16));
        btnImprimir.setBackground(new Color(24, 39, 55));
        btnImprimir.setForeground(Color.WHITE);
        btnImprimir.addActionListener(e -> imprimirNotaFiscal());
        JPanel painelBotoes = new JPanel();
        painelBotoes.add(btnImprimir);
        add(painelBotoes, BorderLayout.SOUTH);
    }

    private JTable criarTabelaItens() {
        String[] colunas = { "Ordem", "Código", "Descrição", "Quantidade", "Preço Unitário", "Desconto", "Subtotal" };
        DefaultTableModel modelo = new DefaultTableModel(colunas, 0);

        for (String ordem : resumoDaVenda.itensMap.keySet()) {
            String[] dadosItem = resumoDaVenda.getDadosItemPorOrdem(ordem);
            modelo.addRow(new Object[] {
                    dadosItem[0],
                    dadosItem[1],
                    dadosItem[2],
                    dadosItem[3],
                    dadosItem[4],
                    dadosItem[5],
                    dadosItem[6]
            });
        }

        JTable tabela = new JTable(modelo);
        tabela.setFillsViewportHeight(true);
        tabela.setRowHeight(25);
        tabela.setFont(new Font("Arial", Font.PLAIN, 12));
        tabela.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        tabela.setEnabled(false);

        tabela.getColumnModel().getColumn(0).setPreferredWidth(50);
        tabela.getColumnModel().getColumn(1).setPreferredWidth(60);
        tabela.getColumnModel().getColumn(2).setPreferredWidth(250);
        tabela.getColumnModel().getColumn(3).setPreferredWidth(80);
        tabela.getColumnModel().getColumn(4).setPreferredWidth(90);
        tabela.getColumnModel().getColumn(5).setPreferredWidth(80);
        tabela.getColumnModel().getColumn(6).setPreferredWidth(90);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(SwingConstants.LEFT);

        tabela.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        tabela.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        tabela.getColumnModel().getColumn(2).setCellRenderer(leftRenderer);
        tabela.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        tabela.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        tabela.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);
        tabela.getColumnModel().getColumn(6).setCellRenderer(centerRenderer);

        return tabela;
    }

    private JPanel criarPainelTotais() {
        JPanel painelTotais = new JPanel();
        painelTotais.setLayout(new BoxLayout(painelTotais, BoxLayout.Y_AXIS));
        painelTotais.setBackground(Color.WHITE);

        DecimalFormat df = new DecimalFormat("#,##0.00");

        // Subtotal
        JPanel linhaSubtotal = new JPanel();
        linhaSubtotal.setLayout(new BoxLayout(linhaSubtotal, BoxLayout.X_AXIS));
        linhaSubtotal.setBackground(Color.WHITE);
        linhaSubtotal.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel lblSubtotal = new JLabel("Subtotal:");
        lblSubtotal.setFont(new Font("Arial", Font.BOLD, 14));
        JLabel lblSubtotalValor = new JLabel("R$ " + df.format(resumoDaVenda.getTotalGeral()));
        lblSubtotalValor.setFont(new Font("Arial", Font.PLAIN, 14));
        linhaSubtotal.add(lblSubtotal);
        linhaSubtotal.add(Box.createRigidArea(new Dimension(10, 0)));
        linhaSubtotal.add(lblSubtotalValor);
        linhaSubtotal.add(Box.createHorizontalGlue());
        painelTotais.add(linhaSubtotal);

        // Desconto Total nos Itens
        BigDecimal descontoItens = BigDecimal.ZERO;
        for (String ordem : resumoDaVenda.itensMap.keySet()) {
            String[] dadosItem = resumoDaVenda.getDadosItemPorOrdem(ordem);
            String descontoStr = dadosItem[5].replace(",", ".").trim();
            BigDecimal descontoItem = descontoStr.isEmpty() ? BigDecimal.ZERO : new BigDecimal(descontoStr);
            descontoItens = descontoItens.add(descontoItem);
        }
        JPanel linhaDescontoItens = new JPanel();
        linhaDescontoItens.setLayout(new BoxLayout(linhaDescontoItens, BoxLayout.X_AXIS));
        linhaDescontoItens.setBackground(Color.WHITE);
        linhaDescontoItens.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel lblDescontoItens = new JLabel("Desconto Total nos Itens:");
        lblDescontoItens.setFont(new Font("Arial", Font.BOLD, 14));
        JLabel lblDescontoItensValor = new JLabel("R$ " + df.format(descontoItens));
        lblDescontoItensValor.setFont(new Font("Arial", Font.PLAIN, 14));
        linhaDescontoItens.add(lblDescontoItens);
        linhaDescontoItens.add(Box.createRigidArea(new Dimension(10, 0)));
        linhaDescontoItens.add(lblDescontoItensValor);
        linhaDescontoItens.add(Box.createHorizontalGlue());
        painelTotais.add(linhaDescontoItens);

        // Desconto no Total da Venda
        String descontoStr = pagamentoVenda.txtDesconto.getText().replace(",", ".").trim();
        BigDecimal descontoVenda = descontoStr.isEmpty() ? BigDecimal.ZERO : new BigDecimal(descontoStr);
        JPanel linhaDescontoVenda = new JPanel();
        linhaDescontoVenda.setLayout(new BoxLayout(linhaDescontoVenda, BoxLayout.X_AXIS));
        linhaDescontoVenda.setBackground(Color.WHITE);
        linhaDescontoVenda.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel lblDescontoVenda = new JLabel("Desconto no Total da Venda:");
        lblDescontoVenda.setFont(new Font("Arial", Font.BOLD, 14));
        JLabel lblDescontoVendaValor = new JLabel("R$ " + df.format(descontoVenda));
        lblDescontoVendaValor.setFont(new Font("Arial", Font.PLAIN, 14));
        linhaDescontoVenda.add(lblDescontoVenda);
        linhaDescontoVenda.add(Box.createRigidArea(new Dimension(10, 0)));
        linhaDescontoVenda.add(lblDescontoVendaValor);
        linhaDescontoVenda.add(Box.createHorizontalGlue());
        painelTotais.add(linhaDescontoVenda);

        // Valor Pago (soma dos valores pagos na tabela)
        BigDecimal valorPagoTotal = BigDecimal.ZERO;
        for (int i = 0; i < pagamentoVenda.modeloTabela.getRowCount(); i++) {
            String valorPagoStr = pagamentoVenda.modeloTabela.getValueAt(i, 5).toString().replace(",", ".").trim(); // Coluna
                                                                                                                    // "Valor
                                                                                                                    // Pago"
            BigDecimal valorPago = new BigDecimal(valorPagoStr);
            valorPagoTotal = valorPagoTotal.add(valorPago);
        }
        JPanel linhaValorPago = new JPanel();
        linhaValorPago.setLayout(new BoxLayout(linhaValorPago, BoxLayout.X_AXIS));
        linhaValorPago.setBackground(Color.WHITE);
        linhaValorPago.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel lblValorPago = new JLabel("Valor Pago:");
        lblValorPago.setFont(new Font("Arial", Font.BOLD, 14));
        JLabel lblValorPagoValor = new JLabel("R$ " + df.format(valorPagoTotal));
        lblValorPagoValor.setFont(new Font("Arial", Font.PLAIN, 14));
        linhaValorPago.add(lblValorPago);
        linhaValorPago.add(Box.createRigidArea(new Dimension(10, 0)));
        linhaValorPago.add(lblValorPagoValor);
        linhaValorPago.add(Box.createHorizontalGlue());
        painelTotais.add(linhaValorPago);

        painelTotais.add(Box.createRigidArea(new Dimension(0, 20)));

        // Formas de Pagamento
        JPanel painelFormasPagamento = new JPanel();
        painelFormasPagamento.setLayout(new BoxLayout(painelFormasPagamento, BoxLayout.Y_AXIS));
        painelFormasPagamento.setBackground(Color.WHITE);
        painelFormasPagamento.setAlignmentX(Component.LEFT_ALIGNMENT);
        painelFormasPagamento.setBorder(BorderFactory.createTitledBorder("Formas de Pagamento"));

        for (int i = 0; i < pagamentoVenda.modeloTabela.getRowCount(); i++) {
            String formaPagamento = (String) pagamentoVenda.modeloTabela.getValueAt(i, 1);
            String parcelas = (String) pagamentoVenda.modeloTabela.getValueAt(i, 2);
            String valorStr = (String) pagamentoVenda.modeloTabela.getValueAt(i, 5); // Usar "Valor Pago" (coluna 5)
            BigDecimal valor = new BigDecimal(valorStr.replace(",", "."));

            JLabel lblPagamento = new JLabel(
                    formaPagamento + " (" + parcelas + "x) - R$ " + df.format(valor));
            lblPagamento.setFont(new Font("Arial", Font.PLAIN, 14));
            lblPagamento.setAlignmentX(Component.LEFT_ALIGNMENT);
            painelFormasPagamento.add(lblPagamento);

            // Exibir o troco apenas se a forma de pagamento for "Dinheiro"
            if (formaPagamento.equalsIgnoreCase("Dinheiro")) {
                String trocoStr = (String) pagamentoVenda.modeloTabela.getValueAt(i, 6);
                BigDecimal troco = trocoStr != null && !trocoStr.isEmpty() ? new BigDecimal(trocoStr.replace(",", "."))
                        : BigDecimal.ZERO;
                if (troco.compareTo(BigDecimal.ZERO) > 0) {
                    JLabel lblTroco = new JLabel("Troco: R$ " + df.format(troco));
                    lblTroco.setFont(new Font("Arial", Font.PLAIN, 14));
                    lblTroco.setAlignmentX(Component.LEFT_ALIGNMENT);
                    painelFormasPagamento.add(lblTroco);
                }
            }
        }
        painelTotais.add(painelFormasPagamento);

        return painelTotais;
    }

    private void imprimirNotaFiscal() {
        PrinterJob printerJob = PrinterJob.getPrinterJob();
        printerJob.setPrintable(this);

        Window owner = SwingUtilities.getWindowAncestor(this);
        JDialog aguardeDialog;

        if (owner instanceof Frame) {
            aguardeDialog = new JDialog((Frame) owner, "Imprimindo...", true);
        } else if (owner instanceof Dialog) {
            aguardeDialog = new JDialog((Dialog) owner, "Imprimindo...", true);
        } else {
            aguardeDialog = new JDialog();
            aguardeDialog.setTitle("Imprimindo...");
            aguardeDialog.setModal(true);
        }

        aguardeDialog.setLayout(new BorderLayout());
        aguardeDialog.setSize(300, 150);
        aguardeDialog.setLocationRelativeTo(this);
        JLabel mensagem = new JLabel("Aguarde, imprimindo nota fiscal...", SwingConstants.CENTER);
        mensagem.setFont(new Font("Arial", Font.BOLD, 14));
        aguardeDialog.add(mensagem, BorderLayout.CENTER);
        aguardeDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

        // Executar a impressão em uma thread separada para não travar a UI
        new Thread(() -> {
            SwingUtilities.invokeLater(() -> aguardeDialog.setVisible(true));
            try {
                Thread.sleep(3000);

                if (printerJob.printDialog()) {
                    printerJob.print();
                    SwingUtilities.invokeLater(() -> {
                        aguardeDialog.dispose();
                        JOptionPane.showMessageDialog(this, "Nota fiscal enviada para impressão!", "Sucesso",
                                JOptionPane.INFORMATION_MESSAGE);
                    });
                } else {
                    SwingUtilities.invokeLater(() -> aguardeDialog.dispose());
                }
            } catch (PrinterException | InterruptedException e) {
                SwingUtilities.invokeLater(() -> {
                    aguardeDialog.dispose();
                    JOptionPane.showMessageDialog(this,
                            "Erro ao imprimir: Impressora não encontrada ou ocorreu um problema. Verifique a conexão e tente novamente.",
                            "Erro de Impressão",
                            JOptionPane.ERROR_MESSAGE);
                });
            }
        }).start();
    }

    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
        if (pageIndex > 0) {
            return NO_SUCH_PAGE;
        }

        Graphics2D g2d = (Graphics2D) graphics;
        g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

        Font titleFont = new Font("Arial", Font.BOLD, 14);
        Font regularFont = new Font("Arial", Font.PLAIN, 10);
        Font boldFont = new Font("Arial", Font.BOLD, 10);
        DecimalFormat df = new DecimalFormat("#,##0.00");

        int y = 20;
        int lineHeight = 15;
        int pageWidth = 550;
        int centerX = pageWidth / 2;

        g2d.setFont(titleFont);
        FontMetrics fm = g2d.getFontMetrics();
        String title = "NOTA FISCAL";
        int titleWidth = fm.stringWidth(title);
        g2d.drawString(title, centerX - (titleWidth / 2), y);
        y += lineHeight * 2;

        g2d.setFont(regularFont);
        String empresa = "Farmácia XYZ - CNPJ: 12.345.678/0001-99";
        int empresaWidth = fm.stringWidth(empresa);
        g2d.drawString(empresa, centerX - (empresaWidth / 2), y);
        y += lineHeight;

        String endereco = "Rua Exemplo, 123 - Centro, Cidade - UF";
        int enderecoWidth = fm.stringWidth(endereco);
        g2d.drawString(endereco, centerX - (enderecoWidth / 2), y);
        y += lineHeight;

        String dataHora = lblDataHora.getText();
        int dataHoraWidth = fm.stringWidth(dataHora);
        g2d.drawString(dataHora, centerX - (dataHoraWidth / 2), y);
        y += lineHeight;

        String atendente = lblAtendente.getText();
        int atendenteWidth = fm.stringWidth(atendente);
        g2d.drawString(atendente, centerX - (atendenteWidth / 2), y);
        y += lineHeight * 2;

        g2d.setFont(boldFont);
        g2d.drawString("Cliente:", 20, y);
        y += lineHeight;
        g2d.setFont(regularFont);
        g2d.drawString(resumoDaVenda.lblNomeCliente.getText(), 20, y);
        y += lineHeight;
        g2d.drawString(resumoDaVenda.lblCpfCliente.getText(), 20, y);
        y += lineHeight * 2;

        int[] columnWidths = { 30, 40, 230, 40, 60, 50, 60 };
        int[] columnXPositions = { 20, 50, 90, 320, 360, 420, 470 };
        int rightAlignX = columnXPositions[6] + columnWidths[6]; // Align with the right edge of Subtotal column

        g2d.setFont(boldFont);
        g2d.drawString("Ordem", columnXPositions[0] + (columnWidths[0] - fm.stringWidth("Ordem")) / 2, y);
        g2d.drawString("Código", columnXPositions[1] + (columnWidths[1] - fm.stringWidth("Código")) / 2, y);
        g2d.drawString("Descrição", columnXPositions[2], y);
        g2d.drawString("Qtd", columnXPositions[3] + (columnWidths[3] - fm.stringWidth("Qtd")) / 2, y);
        g2d.drawString("Preço Unit.", columnXPositions[4] + (columnWidths[4] - fm.stringWidth("Preço Unit.")) / 2, y);
        g2d.drawString("Desc.", columnXPositions[5] + (columnWidths[5] - fm.stringWidth("Desc.")) / 2, y);
        g2d.drawString("Subtotal", columnXPositions[6] + (columnWidths[6] - fm.stringWidth("Subtotal")) / 2, y);
        y += lineHeight;

        g2d.drawLine(20, y, pageWidth, y);
        y += lineHeight;

        g2d.setFont(regularFont);
        for (String ordem : resumoDaVenda.itensMap.keySet()) {
            String[] dadosItem = resumoDaVenda.getDadosItemPorOrdem(ordem);
            String ordemText = dadosItem[0];
            String codigo = dadosItem[1];
            String descricao = dadosItem[2];
            String qtd = dadosItem[3];
            String precoUnit = dadosItem[4];
            String desconto = dadosItem[5];
            String subtotal = dadosItem[6];

            g2d.drawString(ordemText, columnXPositions[0] + (columnWidths[0] - fm.stringWidth(ordemText)) / 2, y);
            g2d.drawString(codigo, columnXPositions[1] + (columnWidths[1] - fm.stringWidth(codigo)) / 2, y);

            int maxDescriptionWidth = columnWidths[2];
            int currentY = y;
            String[] words = descricao.split(" ");
            StringBuilder line = new StringBuilder();
            for (String word : words) {
                if (fm.stringWidth(line + word) <= maxDescriptionWidth) {
                    line.append(word).append(" ");
                } else {
                    g2d.drawString(line.toString().trim(), columnXPositions[2], currentY);
                    line = new StringBuilder(word + " ");
                    currentY += lineHeight;
                }
            }
            if (!line.toString().isEmpty()) {
                g2d.drawString(line.toString().trim(), columnXPositions[2], currentY);
            }

            g2d.drawString(qtd, columnXPositions[3] + (columnWidths[3] - fm.stringWidth(qtd)) / 2, y);
            g2d.drawString(precoUnit, columnXPositions[4] + (columnWidths[4] - fm.stringWidth(precoUnit)) / 2, y);
            g2d.drawString(desconto, columnXPositions[5] + (columnWidths[5] - fm.stringWidth(desconto)) / 2, y);
            g2d.drawString(subtotal, columnXPositions[6] + (columnWidths[6] - fm.stringWidth(subtotal)) / 2, y);

            y = Math.max(y + lineHeight, currentY + lineHeight);
        }

        g2d.drawLine(20, y, pageWidth, y);
        y += lineHeight * 2;

        g2d.setFont(boldFont);
        String subtotalLabel = "Subtotal:";
        g2d.drawString(subtotalLabel, 20, y);
        g2d.setFont(regularFont);
        String subtotalValue = "R$ " + df.format(resumoDaVenda.getTotalGeral());
        g2d.drawString(subtotalValue, rightAlignX - fm.stringWidth(subtotalValue), y);
        y += lineHeight;

        BigDecimal descontoItens = BigDecimal.ZERO;
        for (String ordem : resumoDaVenda.itensMap.keySet()) {
            String[] dadosItem = resumoDaVenda.getDadosItemPorOrdem(ordem);
            String descontoStr = dadosItem[5].replace(",", ".").trim();
            BigDecimal descontoItem = descontoStr.isEmpty() ? BigDecimal.ZERO : new BigDecimal(descontoStr);
            descontoItens = descontoItens.add(descontoItem);
        }
        g2d.setFont(boldFont);
        String descontoItensLabel = "Desconto Total nos Itens:";
        g2d.drawString(descontoItensLabel, 20, y);
        g2d.setFont(regularFont);
        String descontoItensValue = "R$ " + df.format(descontoItens);
        g2d.drawString(descontoItensValue, rightAlignX - fm.stringWidth(descontoItensValue), y);
        y += lineHeight;

        String descontoStr = pagamentoVenda.txtDesconto.getText().replace(",", ".").trim();
        BigDecimal descontoVenda = descontoStr.isEmpty() ? BigDecimal.ZERO : new BigDecimal(descontoStr);
        g2d.setFont(boldFont);
        String descontoVendaLabel = "Desconto no Total da Venda:";
        g2d.drawString(descontoVendaLabel, 20, y);
        g2d.setFont(regularFont);
        String descontoVendaValue = "R$ " + df.format(descontoVenda);
        g2d.drawString(descontoVendaValue, rightAlignX - fm.stringWidth(descontoVendaValue), y);
        y += lineHeight;

        BigDecimal valorPagoTotal = BigDecimal.ZERO;
        for (int i = 0; i < pagamentoVenda.modeloTabela.getRowCount(); i++) {
            String valorPagoStr = pagamentoVenda.modeloTabela.getValueAt(i, 5).toString().replace(",", ".").trim();
            BigDecimal valorPago = new BigDecimal(valorPagoStr);
            valorPagoTotal = valorPagoTotal.add(valorPago);
        }
        g2d.setFont(boldFont);
        String valorPagoLabel = "Valor Pago:";
        g2d.drawString(valorPagoLabel, 20, y);
        g2d.setFont(regularFont);
        String valorPagoValue = "R$ " + df.format(valorPagoTotal);
        g2d.drawString(valorPagoValue, rightAlignX - fm.stringWidth(valorPagoValue), y);
        y += lineHeight * 3;

        g2d.setFont(boldFont);
        g2d.drawString("Formas de Pagamento:", 20, y);
        y += lineHeight;
        g2d.setFont(regularFont);
        for (int i = 0; i < pagamentoVenda.modeloTabela.getRowCount(); i++) {
            String formaPagamento = (String) pagamentoVenda.modeloTabela.getValueAt(i, 1);
            String parcelas = (String) pagamentoVenda.modeloTabela.getValueAt(i, 2);
            String valorStr = (String) pagamentoVenda.modeloTabela.getValueAt(i, 5);
            BigDecimal valor = new BigDecimal(valorStr.replace(",", "."));
            g2d.drawString(formaPagamento + " (" + parcelas + "x) - R$ " + df.format(valor), 20, y);
            y += lineHeight;

            if (formaPagamento.equalsIgnoreCase("Dinheiro")) {
                String trocoStr = (String) pagamentoVenda.modeloTabela.getValueAt(i, 6);
                BigDecimal troco = trocoStr != null && !trocoStr.isEmpty() ? new BigDecimal(trocoStr.replace(",", "."))
                        : BigDecimal.ZERO;
                if (troco.compareTo(BigDecimal.ZERO) > 0) {
                    g2d.drawString("Troco: R$ " + df.format(troco), 20, y);
                    y += lineHeight;
                }
            }
        }

        g2d.drawLine(20, y, pageWidth, y);
        return PAGE_EXISTS;
    }

    public static void exibirNotaFiscal(ResumoDaVenda resumoDaVenda, PagamentoVenda pagamentoVenda) {
        JDialog dialog = new JDialog();
        dialog.setTitle("Nota Fiscal");
        dialog.setSize(900, 650);
        dialog.setModal(true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.add(new NotaFiscal(resumoDaVenda, pagamentoVenda));
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }
}