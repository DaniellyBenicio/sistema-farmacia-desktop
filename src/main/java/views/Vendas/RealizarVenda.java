package views.Vendas;

import java.awt.*;
import java.sql.Connection;

import javax.swing.*;

public class RealizarVenda extends JPanel {

    private Connection conn;
    private JLabel itemLabel, lblCodigoProduto, lblQuantidade, lblPrecoUnitario, lblDesconto, lblPrecoTotal;
    private JTextField txtItem, txtCodigoProduto, txtQuantidade, txtPrecoUnitario, txtDesconto, txtPrecoTotal;

    public RealizarVenda(Connection conn) {
        this.conn = conn;

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JPanel painelTopo = new JPanel(new BorderLayout());
        painelTopo.setPreferredSize(new Dimension(0, 120));
        painelTopo.setBorder(BorderFactory.createEmptyBorder(20, 150, 5, 150));
        painelTopo.add(createItemPanel(), BorderLayout.CENTER);

        add(painelTopo, BorderLayout.NORTH);

        JPanel painelMeio = new JPanel(new GridLayout(1, 2));

        painelMeio.add(createPainelEsquerdo());
        painelMeio.add(createPainelDireito());

        add(painelMeio, BorderLayout.CENTER);

        JPanel painelFooter = new JPanel();
        painelFooter.setLayout(new GridLayout(1, 2));

        painelFooter.add(ladoEsquerdoFooter());
        painelFooter.add(ladoDireitoFooter());

        add(painelFooter, BorderLayout.SOUTH);
    }

    private JPanel createItemPanel() {
        JPanel painelItem = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 20, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        itemLabel = new JLabel("Produto:");
        itemLabel.setFont(new Font("Arial", Font.BOLD, 18));
        itemLabel.setForeground(Color.BLACK);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        painelItem.add(itemLabel, gbc);

        txtItem = new JTextField();
        txtItem.setBackground(new Color(24, 39, 55));
        txtItem.setForeground(Color.WHITE);
        txtItem.setOpaque(true);
        txtItem.setFont(new Font("Arial", Font.PLAIN, 20));
        txtItem.setPreferredSize(new Dimension(0, 45));

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 20, 5, 10);

        painelItem.add(txtItem, gbc);

        return painelItem;
    }

    private JPanel createPainelEsquerdo() {
        JPanel painelEsquerdo = new JPanel();
        painelEsquerdo.setPreferredSize(new Dimension(0, 140));

        painelEsquerdo.setLayout(new BoxLayout(painelEsquerdo, BoxLayout.Y_AXIS));

        JPanel painelInternoEsquerdo = new JPanel(new GridBagLayout());
        painelInternoEsquerdo.setPreferredSize(new Dimension(0, 270));

        painelEsquerdo.add(painelInternoEsquerdo);

        painelEsquerdo.setBorder(BorderFactory.createEmptyBorder(5, 168, 20, 50));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 10, 350);
        gbc.anchor = GridBagConstraints.WEST;

        lblCodigoProduto = new JLabel("Código do Produto");
        lblCodigoProduto.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        painelInternoEsquerdo.add(lblCodigoProduto, gbc);

        txtCodigoProduto = createTextFieldOutrosCampos();
        gbc.gridx = 0;
        gbc.gridy = 1;
        painelInternoEsquerdo.add(txtCodigoProduto, gbc);

        lblQuantidade = new JLabel("Quantidade");
        lblQuantidade.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 2;
        painelInternoEsquerdo.add(lblQuantidade, gbc);

        txtQuantidade = createTextFieldOutrosCampos();
        gbc.gridx = 0;
        gbc.gridy = 3;
        painelInternoEsquerdo.add(txtQuantidade, gbc);

        lblPrecoUnitario = new JLabel("Preço Unitário");
        lblPrecoUnitario.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 4;
        painelInternoEsquerdo.add(lblPrecoUnitario, gbc);

        txtPrecoUnitario = createTextFieldOutrosCampos();
        gbc.gridx = 0;
        gbc.gridy = 5;
        painelInternoEsquerdo.add(txtPrecoUnitario, gbc);

        lblDesconto = new JLabel("Desconto");
        lblDesconto.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 6;
        painelInternoEsquerdo.add(lblDesconto, gbc);

        txtDesconto = createTextFieldOutrosCampos();
        gbc.gridx = 0;
        gbc.gridy = 7;
        painelInternoEsquerdo.add(txtDesconto, gbc);

        lblPrecoTotal = new JLabel("Preço Total");
        lblPrecoTotal.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 8;
        painelInternoEsquerdo.add(lblPrecoTotal, gbc);

        txtPrecoTotal = createTextFieldOutrosCampos();
        gbc.gridx = 0;
        gbc.gridy = 9;
        painelInternoEsquerdo.add(txtPrecoTotal, gbc);

        return painelEsquerdo;
    }

    private JTextField createTextFieldOutrosCampos() {
        JTextField textField = new JTextField();
        textField.setBackground(Color.WHITE);
        textField.setForeground(Color.BLACK);
        textField.setOpaque(true);
        textField.setFont(new Font("Arial", Font.PLAIN, 20));
        textField.setPreferredSize(new Dimension(200, 45));
        textField.setHorizontalAlignment(SwingConstants.LEFT);
        return textField;
    }

    private JPanel createPainelDireito() {
        JPanel painelDireito = new JPanel();
        painelDireito.setPreferredSize(new Dimension(0, 140));

        painelDireito.setBorder(BorderFactory.createEmptyBorder(0, 0, 35, 160));

        painelDireito.setLayout(new BoxLayout(painelDireito, BoxLayout.Y_AXIS));

        JPanel painelInternoDireito = new JPanel();
        painelInternoDireito.setPreferredSize(new Dimension(0, 100));

        painelInternoDireito.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

        JLabel labelResumoVenda = new JLabel("Resumo da Venda");
        labelResumoVenda.setHorizontalAlignment(SwingConstants.CENTER);
        labelResumoVenda.setVerticalAlignment(SwingConstants.CENTER);
        labelResumoVenda.setFont(new Font("Arial", Font.BOLD, 16));

        painelInternoDireito.add(labelResumoVenda);

        painelDireito.add(painelInternoDireito);

        return painelDireito;
    }

    private JPanel ladoEsquerdoFooter() {
        JPanel ladoEsquerdo = new JPanel();
        JPanel botoesVenda = new JPanel();
        botoesVenda.setLayout(new BoxLayout(botoesVenda, BoxLayout.X_AXIS));

        botoesVenda.setBorder(BorderFactory.createEmptyBorder(0, 50, 0, 0));

        JButton btnIdentificarCliente = new JButton("Identificar Cliente");
        btnIdentificarCliente.setFont(new Font("Arial", Font.BOLD, 18));
        btnIdentificarCliente.setBackground(new Color(24, 39, 55));
        btnIdentificarCliente.setForeground(Color.WHITE);
        btnIdentificarCliente.setFocusPainted(false);
        btnIdentificarCliente.setPreferredSize(new Dimension(185, 45));
        botoesVenda.add(btnIdentificarCliente);

        botoesVenda.add(Box.createRigidArea(new Dimension(25, 0)));

        JButton btnCancelarVenda = new JButton("Cancelar Venda");
        btnCancelarVenda.setFont(new Font("Arial", Font.BOLD, 18));
        btnCancelarVenda.setBackground(Color.RED);
        btnCancelarVenda.setForeground(Color.WHITE);
        btnCancelarVenda.setFocusPainted(false);
        btnCancelarVenda.setPreferredSize(new Dimension(185, 40));
        botoesVenda.add(btnCancelarVenda);

        botoesVenda.add(Box.createRigidArea(new Dimension(25, 0)));

        JButton btnConfirmarVenda = new JButton("Confirmar Venda");
        btnConfirmarVenda.setFont(new Font("Arial", Font.BOLD, 18));
        btnConfirmarVenda.setBackground(new Color(0, 133, 0));
        btnConfirmarVenda.setForeground(Color.WHITE);
        btnConfirmarVenda.setFocusPainted(false);
        btnConfirmarVenda.setPreferredSize(new Dimension(185, 40));
        botoesVenda.add(btnConfirmarVenda);

        ladoEsquerdo.setBorder(BorderFactory.createEmptyBorder(0, 120, 0, 0));

        ladoEsquerdo.add(botoesVenda);

        return ladoEsquerdo;
    }

    private JPanel ladoDireitoFooter() {
        JPanel ladoDireito = new JPanel();
        ladoDireito.setBackground(Color.RED);
        ladoDireito.setLayout(new BoxLayout(ladoDireito, BoxLayout.Y_AXIS));
        ladoDireito.setBorder(BorderFactory.createEmptyBorder(50, 0, 0, 160));

        return ladoDireito;
    }
}
