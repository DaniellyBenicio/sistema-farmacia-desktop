package views.Vendas;

import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;

public class ResumoDaVenda extends JPanel {

    JLabel lblNomeCliente;
    JLabel lblCpfCliente;
    JLabel lblAtendente;
    private JPanel painelDetalhesItens;
    private JLabel lblSubtotalValor, lblDescontoValor, lblTotalValor;
    private int linhaAtual = 0;
    private BigDecimal subtotalTotal = BigDecimal.ZERO;
    private BigDecimal descontoTotal = BigDecimal.ZERO;
    private BigDecimal totalGeral = BigDecimal.ZERO;
    Map<String, Component[]> itensMap = new HashMap<>();

    public ResumoDaVenda(JLabel lblNomeCliente, JLabel lblCpfCliente, JLabel lblAtendente) {
        this.lblNomeCliente = lblNomeCliente;
        this.lblCpfCliente = lblCpfCliente;
        this.lblAtendente = lblAtendente;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(10, 5, 5, 17));

        JPanel painelInternoDireito = new JPanel(new BorderLayout());
        painelInternoDireito.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

        JPanel painelClienteInfo = new JPanel(new GridBagLayout());
        painelClienteInfo.setBorder(BorderFactory.createEmptyBorder(2, 5, 10, 5));
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(5, 0, 0, 0);

        JLabel lblFarmacia = new JLabel("Farmácia", SwingConstants.CENTER);
        lblFarmacia.setFont(new Font("Arial", Font.BOLD, 16));
        lblFarmacia.setForeground(Color.BLACK);
        painelClienteInfo.add(lblFarmacia, gbc);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        JLabel lblData = new JLabel("Data: " + LocalDate.now().format(formatter));
        gbc.gridy++;
        painelClienteInfo.add(lblData, gbc);

        gbc.gridy++;
        painelClienteInfo.add(lblCpfCliente, gbc);

        gbc.gridy++;
        painelClienteInfo.add(lblNomeCliente, gbc);

        gbc.gridy++;
        painelClienteInfo.add(lblAtendente, gbc);

        gbc.gridy++;
        JLabel lblEspaco = new JLabel("");
        painelClienteInfo.add(lblEspaco, gbc);

        JPanel painelItens = new JPanel(new BorderLayout());
        painelItens.setBorder(BorderFactory.createTitledBorder("Resumo da Venda"));
        JPanel painelConteudoItens = new JPanel(new GridBagLayout());
        GridBagConstraints gbcItens = new GridBagConstraints();

        gbcItens.gridy = 0;
        gbcItens.anchor = GridBagConstraints.LINE_START;
        gbcItens.fill = GridBagConstraints.HORIZONTAL;
        gbcItens.insets = new Insets(5, 5, 5, 5);

        JLabel lblItem = new JLabel("Item", SwingConstants.LEFT);
        JLabel lblCodigo = new JLabel("Código", SwingConstants.LEFT);
        JLabel lblDescricao = new JLabel("Descrição", SwingConstants.LEFT);
        JLabel lblQnt = new JLabel("Quantidade", SwingConstants.LEFT);
        JLabel lblValorUni = new JLabel("Valor Unitário", SwingConstants.LEFT);
        JLabel lblDesconto = new JLabel("Desconto", SwingConstants.LEFT);
        JLabel lblSubtotal = new JLabel("Subtotal", SwingConstants.LEFT);

        GridBagConstraints gbcItem = createGridBagConstraints(0.4);
        GridBagConstraints gbcCodigo = createGridBagConstraints(0.6);
        GridBagConstraints gbcDescricao = createGridBagConstraints(6.3);
        GridBagConstraints gbcQnt = createGridBagConstraints(1.0);
        GridBagConstraints gbcValorUni = createGridBagConstraints(1.0);
        GridBagConstraints gbcDesconto = createGridBagConstraints(1.1);
        GridBagConstraints gbcSubtotal = createGridBagConstraints(1.0);

        gbcItens.gridx = 0;
        painelConteudoItens.add(lblItem, gbcItem);

        gbcItens.gridx = 1;
        painelConteudoItens.add(lblCodigo, gbcCodigo);

        gbcItens.gridx = 2;
        painelConteudoItens.add(lblDescricao, gbcDescricao);

        gbcItens.gridx = 3;
        painelConteudoItens.add(lblQnt, gbcQnt);

        gbcItens.gridx = 4;
        painelConteudoItens.add(lblValorUni, gbcValorUni);

        gbcItens.gridx = 5;
        painelConteudoItens.add(lblDesconto, gbcDesconto);

        gbcItens.gridx = 6;
        painelConteudoItens.add(lblSubtotal, gbcSubtotal);

        painelItens.add(painelConteudoItens, BorderLayout.NORTH);

        painelDetalhesItens = new JPanel(new GridBagLayout());
        painelDetalhesItens.setBackground(Color.LIGHT_GRAY);

        JScrollPane scrollPane = new JScrollPane(painelDetalhesItens);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setPreferredSize(new Dimension(0, 150));
        scrollPane.getViewport().setBackground(Color.LIGHT_GRAY);

        painelItens.add(scrollPane, BorderLayout.CENTER);

        painelInternoDireito.add(painelClienteInfo, BorderLayout.NORTH);
        painelInternoDireito.add(painelItens, BorderLayout.CENTER);

        JPanel painelFooter = new JPanel(new GridBagLayout());
        painelFooter.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 10));

        GridBagConstraints gbcFooter = new GridBagConstraints();
        gbcFooter.insets = new Insets(5, 5, 5, 5);

        JLabel lblSubtotalTexto = new JLabel("Subtotal R$");
        JLabel lblDescontoTexto = new JLabel("Desconto R$");
        JLabel lblTotalTexto = new JLabel("Total R$");

        lblSubtotalValor = new JLabel("0,00");
        lblDescontoValor = new JLabel("0,00");
        lblTotalValor = new JLabel("0,00");

        gbcFooter.gridx = 0;
        gbcFooter.gridy = 0;
        gbcFooter.anchor = GridBagConstraints.WEST;
        gbcFooter.fill = GridBagConstraints.NONE;
        gbcFooter.weightx = 0.0;
        painelFooter.add(lblSubtotalTexto, gbcFooter);

        gbcFooter.gridy = 1;
        painelFooter.add(lblDescontoTexto, gbcFooter);

        gbcFooter.gridy = 2;
        painelFooter.add(lblTotalTexto, gbcFooter);

        gbcFooter.gridx = 1;
        gbcFooter.gridy = 0;
        gbcFooter.anchor = GridBagConstraints.EAST;
        gbcFooter.fill = GridBagConstraints.NONE;
        gbcFooter.weightx = 1.0;
        painelFooter.add(lblSubtotalValor, gbcFooter);

        gbcFooter.gridy = 1;
        painelFooter.add(lblDescontoValor, gbcFooter);

        gbcFooter.gridy = 2;
        painelFooter.add(lblTotalValor, gbcFooter);

        painelInternoDireito.add(painelFooter, BorderLayout.SOUTH);
        add(painelInternoDireito);

        GridBagConstraints gbcFiller = new GridBagConstraints();
        gbcFiller.gridx = 0;
        gbcFiller.gridy = 9999;
        gbcFiller.weighty = 1.0;
        gbcFiller.fill = GridBagConstraints.VERTICAL;
        painelDetalhesItens.add(Box.createVerticalGlue(), gbcFiller);
    }

    private GridBagConstraints createGridBagConstraints(double weightx) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.weightx = weightx;
        return gbc;
    }

    public void adicionarItem(String ordem, String codigo, String descricao,
            String quantidade, String valorUnitario, String subtotal, String desconto) {
        GridBagConstraints gbcItemDetalhe = createGridBagConstraints(0.4);
        GridBagConstraints gbcCodigoDetalhe = createGridBagConstraints(0.6);
        GridBagConstraints gbcDescricaoDetalhe = createGridBagConstraints(4.2);
        GridBagConstraints gbcQntDetalhe = createGridBagConstraints(1.0);
        GridBagConstraints gbcValorUniDetalhe = createGridBagConstraints(1.4);
        GridBagConstraints gbcDescontoDetalhe = createGridBagConstraints(1.0);
        GridBagConstraints gbcSubtotalDetalhe = createGridBagConstraints(1.0);

        JLabel lblOrdem = new JLabel(ordem, SwingConstants.CENTER);
        JLabel lblCodigo = new JLabel(codigo, SwingConstants.CENTER);

        JTextArea lblDescricao = new JTextArea(descricao);
        lblDescricao.setLineWrap(true);
        lblDescricao.setWrapStyleWord(true);
        lblDescricao.setEditable(false);
        lblDescricao.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 2));
        lblDescricao.setRows(2);
        lblDescricao.setPreferredSize(new Dimension(20, 40));
        lblDescricao.setBackground(Color.LIGHT_GRAY);

        JLabel lblQuantidade = new JLabel(quantidade, SwingConstants.CENTER);
        JLabel lblValorUnitario = new JLabel(valorUnitario, SwingConstants.CENTER);
        JLabel lblDescontoItem = new JLabel(desconto, SwingConstants.CENTER);
        JLabel lblSubtotal = new JLabel(subtotal, SwingConstants.CENTER);

        gbcItemDetalhe.gridy = linhaAtual;
        gbcItemDetalhe.gridx = 0;
        painelDetalhesItens.add(lblOrdem, gbcItemDetalhe);

        gbcCodigoDetalhe.gridy = linhaAtual;
        gbcCodigoDetalhe.gridx = 1;
        painelDetalhesItens.add(lblCodigo, gbcCodigoDetalhe);

        gbcDescricaoDetalhe.gridy = linhaAtual;
        gbcDescricaoDetalhe.gridx = 2;
        painelDetalhesItens.add(lblDescricao, gbcDescricaoDetalhe);

        gbcQntDetalhe.gridy = linhaAtual;
        gbcQntDetalhe.gridx = 3;
        painelDetalhesItens.add(lblQuantidade, gbcQntDetalhe);

        gbcValorUniDetalhe.gridy = linhaAtual;
        gbcValorUniDetalhe.gridx = 4;
        painelDetalhesItens.add(lblValorUnitario, gbcValorUniDetalhe);

        gbcDescontoDetalhe.gridy = linhaAtual;
        gbcDescontoDetalhe.gridx = 5;
        painelDetalhesItens.add(lblDescontoItem, gbcDescontoDetalhe);

        gbcSubtotalDetalhe.gridy = linhaAtual;
        gbcSubtotalDetalhe.gridx = 6;
        painelDetalhesItens.add(lblSubtotal, gbcSubtotalDetalhe);

        itensMap.put(ordem, new Component[] { lblOrdem, lblCodigo, lblDescricao,
                lblQuantidade, lblValorUnitario, lblDescontoItem, lblSubtotal });

        linhaAtual++;

        BigDecimal subtotalItem = new BigDecimal(subtotal.replace(",", "."));
        BigDecimal descontoItem = new BigDecimal(desconto.replace(",", "."));
        subtotalTotal = subtotalTotal.add(subtotalItem);
        descontoTotal = descontoTotal.add(descontoItem);
        totalGeral = subtotalTotal.subtract(descontoTotal);

        atualizarTotais();

        SwingUtilities.invokeLater(() -> {
            JScrollPane scrollPane = (JScrollPane) painelDetalhesItens.getParent().getParent();
            JScrollBar verticalBar = scrollPane.getVerticalScrollBar();
            if (linhaAtual == 1) {
                verticalBar.setValue(verticalBar.getMinimum());
            } else {
                verticalBar.setValue(verticalBar.getMaximum());
            }
        });
    }

    public void removerItem(String ordem) {
        Component[] componentes = itensMap.get(ordem);
        if (componentes != null) {
            for (Component comp : componentes) {
                painelDetalhesItens.remove(comp);
            }

            String subtotalStr = ((JLabel) componentes[6]).getText();
            String descontoStr = ((JLabel) componentes[5]).getText();
            BigDecimal subtotalItem = new BigDecimal(subtotalStr.replace(",", "."));
            BigDecimal descontoItem = new BigDecimal(descontoStr.replace(",", "."));

            subtotalTotal = subtotalTotal.subtract(subtotalItem);
            descontoTotal = descontoTotal.subtract(descontoItem);
            totalGeral = subtotalTotal.subtract(descontoTotal);

            itensMap.remove(ordem);

            revalidate();
            repaint();
            atualizarTotais();
        }
    }

    private void atualizarTotais() {
        lblSubtotalValor.setText(subtotalTotal.setScale(2, RoundingMode.HALF_UP)
                .toString().replace(".", ","));
        lblDescontoValor.setText(descontoTotal.setScale(2, RoundingMode.HALF_UP)
                .toString().replace(".", ","));
        lblTotalValor.setText(totalGeral.setScale(2, RoundingMode.HALF_UP)
                .toString().replace(".", ","));
        revalidate();
        repaint();
    }

    public String[] getDadosItemPorOrdem(String ordem) {
        Component[] componentes = itensMap.get(ordem);
        if (componentes != null) {
            return new String[] {
                    ((JLabel) componentes[0]).getText(),
                    ((JLabel) componentes[1]).getText(),
                    ((JTextArea) componentes[2]).getText(),
                    ((JLabel) componentes[3]).getText(),
                    ((JLabel) componentes[4]).getText(),
                    ((JLabel) componentes[5]).getText(),
                    ((JLabel) componentes[6]).getText()
            };
        }
        return null;
    }

    public BigDecimal getTotalGeral() {
        return totalGeral;
    }
}