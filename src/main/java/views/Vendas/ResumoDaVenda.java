package views.Vendas;

import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.swing.*;

public class ResumoDaVenda extends JPanel {

    private JLabel lblNomeCliente, lblCpfCliente;

    public ResumoDaVenda(JLabel lblNomeCliente, JLabel lblCpfCliente) {
        this.lblNomeCliente = lblNomeCliente;
        this.lblCpfCliente = lblCpfCliente;

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
        JLabel lblAtendente = new JLabel("Atendente: Não Identificado");
        painelClienteInfo.add(lblAtendente, gbc);

        gbc.gridy++;
        JLabel lblEspaco = new JLabel("");
        painelClienteInfo.add(lblEspaco, gbc);

        JPanel painelItens = new JPanel(new BorderLayout());
        painelItens.setBorder(BorderFactory.createTitledBorder("Resumo da Venda"));
        JPanel painelConteudoItens = new JPanel(new GridBagLayout());
        GridBagConstraints gbcItens = new GridBagConstraints();

        gbcItens.gridy = 0;
        gbcItens.anchor = GridBagConstraints.WEST;
        gbcItens.fill = GridBagConstraints.HORIZONTAL;
        gbcItens.insets = new Insets(5, 0, 5, 0);

        JLabel lblItem = new JLabel("Item");
        JLabel lblCodigo = new JLabel("Código");
        JLabel lblDescricao = new JLabel("Descrição");
        JLabel lblQnt = new JLabel("Quantidade");
        JLabel lblValorUni = new JLabel("Valor Unitário");
        JLabel lblSubtotal = new JLabel("Subtotal");

        GridBagConstraints gbcItem = createGridBagConstraints(0.5);
        GridBagConstraints gbcCodigo = createGridBagConstraints(0.6);
        GridBagConstraints gbcDescricao = createGridBagConstraints(4.2);
        GridBagConstraints gbcQnt = createGridBagConstraints(1.5);
        GridBagConstraints gbcValorUni = createGridBagConstraints(1.0);
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
        painelConteudoItens.add(lblSubtotal, gbcSubtotal);

        painelItens.add(painelConteudoItens, BorderLayout.NORTH);

        // Painel de detalhes dos itens
        JPanel painelDetalhesItens = new JPanel(new GridBagLayout());
        painelDetalhesItens.setBackground(Color.LIGHT_GRAY);
        painelDetalhesItens.setPreferredSize(new Dimension(painelItens.getWidth(), 150));

        addItemDetailsToPanel(painelDetalhesItens);

        JScrollPane scrollPane = new JScrollPane(painelDetalhesItens);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setPreferredSize(new Dimension(painelItens.getWidth(), 100));
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

        JLabel lblSubtotalValor = new JLabel("65.00");
        JLabel lblDescontoValor = new JLabel("5.00");
        JLabel lblTotalValor = new JLabel("60.00");

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
    }

    // Método auxiliar para criar GridBagConstraints com weightx configurado
    private GridBagConstraints createGridBagConstraints(double weightx) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 5, 0);
        gbc.weightx = weightx;
        return gbc;
    }

    private void addItemDetailsToPanel(JPanel painelDetalhesItens) {
        String[][] itens = {
                { "1", "123", "Descrição do Item 1", "1", "10.00", "10.00" },
                { "2", "124", "Descrição do Item 2", "2", "20.00", "40.00" },
                { "3", "125", "Descrição do Item 3", "1", "15.00", "15.00" }
        };

        for (int i = 0; i < itens.length; i++) {
            GridBagConstraints gbcItemDetalhe = createGridBagConstraints(0.55);
            GridBagConstraints gbcCodigoDetalhe = createGridBagConstraints(0.6);
            GridBagConstraints gbcDescricaoDetalhe = createGridBagConstraints(2.3);
            GridBagConstraints gbcQntDetalhe = createGridBagConstraints(1.3);
            GridBagConstraints gbcValorUniDetalhe = createGridBagConstraints(1.0);
            GridBagConstraints gbcSubtotalDetalhe = createGridBagConstraints(0.5);

            gbcItemDetalhe.gridy = i + 1;
            gbcItemDetalhe.gridx = 0;
            painelDetalhesItens.add(new JLabel(itens[i][0]), gbcItemDetalhe);

            gbcCodigoDetalhe.gridy = i + 1;
            gbcCodigoDetalhe.gridx = 1;
            painelDetalhesItens.add(new JLabel(itens[i][1]), gbcCodigoDetalhe);

            gbcDescricaoDetalhe.gridy = i + 1;
            gbcDescricaoDetalhe.gridx = 2;
            painelDetalhesItens.add(new JLabel(itens[i][2]), gbcDescricaoDetalhe);

            gbcQntDetalhe.gridy = i + 1;
            gbcQntDetalhe.gridx = 3;
            painelDetalhesItens.add(new JLabel(itens[i][3]), gbcQntDetalhe);

            gbcValorUniDetalhe.gridy = i + 1;
            gbcValorUniDetalhe.gridx = 4;
            painelDetalhesItens.add(new JLabel(itens[i][4]), gbcValorUniDetalhe);

            gbcSubtotalDetalhe.gridy = i + 1;
            gbcSubtotalDetalhe.gridx = 5;
            painelDetalhesItens.add(new JLabel(itens[i][5]), gbcSubtotalDetalhe);
        }
    }
}