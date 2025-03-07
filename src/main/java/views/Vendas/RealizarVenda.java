package views.Vendas;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;

public class RealizarVenda extends JPanel {
    private JLabel lblItem, lblCodigoProduto, lblQuantidade, lblPrecoUnitario, lblDesconto, lblPrecoTotal;
    private JTextField txtItem, txtCodigoProduto, txtQuantidade, txtPrecoUnitario, txtDesconto, txtPrecoTotal;
    private CardLayout layoutCartao;
    private JPanel painelCentralParam;
    private Connection conn;
    private TelaInicialVendas telaInicialVendas;

    public RealizarVenda(Connection conn, TelaInicialVendas telaInicialVendas, CardLayout layoutCartao,
            JPanel painelCentralParam) {
        this.conn = conn;
        this.telaInicialVendas = telaInicialVendas;
        this.layoutCartao = layoutCartao;
        this.painelCentralParam = painelCentralParam;
        setLayout(new BorderLayout());
        // add(criarTituloEBusca(), BorderLayout.NORTH);

        JPanel localPainelCentral = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 10, 5, 10);
        localPainelCentral.add(createItemPanel(), gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 20, 10);
        localPainelCentral.add(createOutrosCamposItensVendas(), gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 0, 10);
        localPainelCentral.add(createBotoesVenda(), gbc);
        add(localPainelCentral, BorderLayout.CENTER);
    }

    /*
     * private JPanel criarTituloEBusca() {
     * JPanel painelSuperior = new JPanel(new BorderLayout());
     * JPanel painelVoltar = new JPanel();
     * painelVoltar.setLayout(new FlowLayout(FlowLayout.LEFT));
     * 
     * JButton voltar = new JButton("Voltar");
     * voltar.setFont(new Font("Arial", Font.PLAIN, 17));
     * voltar.setBorder(null);
     * voltar.setContentAreaFilled(false);
     * voltar.setFocusPainted(false);
     * voltar.setPreferredSize(new Dimension(90, 30));
     * voltar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
     * 
     * voltar.addMouseListener(new java.awt.event.MouseAdapter() {
     * 
     * @Override
     * public void mouseEntered(java.awt.event.MouseEvent evt) {
     * voltar.setForeground((new Color(50, 100, 150)));
     * }
     * 
     * @Override
     * public void mouseExited(java.awt.event.MouseEvent evt) {
     * voltar.setForeground(Color.BLACK);
     * }
     * });
     * 
     * voltar.addActionListener(e -> {
     * SwingUtilities.invokeLater(() -> {
     * layoutCartao.show(painelCentralParam, "TelaInicialVendas");
     * });
     * });
     * 
     * painelVoltar.add(voltar);
     * painelSuperior.add(painelVoltar, BorderLayout.WEST);
     * 
     * return painelSuperior;
     * }
     */
    private JPanel createItemPanel() {
        JPanel painelItem = new JPanel();
        painelItem.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.anchor = GridBagConstraints.WEST;

        lblItem = new JLabel("Produto");
        lblItem.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        painelItem.add(lblItem, gbc);

        txtItem = createTextFieldItem();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 0, 0);
        painelItem.add(txtItem, gbc);

        return painelItem;
    }

    private JPanel createOutrosCamposItensVendas() {
        JPanel painelPrincipal = new JPanel(new BorderLayout());
        JPanel painelCampos = new JPanel();
        painelCampos.setLayout(new GridBagLayout());
        painelCampos.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 50));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 10, 0);
        gbc.anchor = GridBagConstraints.WEST;

        lblCodigoProduto = new JLabel("Código do Produto");
        lblCodigoProduto.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        painelCampos.add(lblCodigoProduto, gbc);

        txtCodigoProduto = createTextFieldOutrosCampos();
        gbc.gridx = 0;
        gbc.gridy = 1;
        painelCampos.add(txtCodigoProduto, gbc);

        lblQuantidade = new JLabel("Quantidade");
        lblQuantidade.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 2;
        painelCampos.add(lblQuantidade, gbc);

        txtQuantidade = createTextFieldOutrosCampos();
        gbc.gridx = 0;
        gbc.gridy = 3;
        painelCampos.add(txtQuantidade, gbc);

        lblPrecoUnitario = new JLabel("Preço Unitário");
        lblPrecoUnitario.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 4;
        painelCampos.add(lblPrecoUnitario, gbc);

        txtPrecoUnitario = createTextFieldOutrosCampos();
        gbc.gridx = 0;
        gbc.gridy = 5;
        painelCampos.add(txtPrecoUnitario, gbc);

        lblDesconto = new JLabel("Desconto");
        lblDesconto.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 6;
        painelCampos.add(lblDesconto, gbc);

        txtDesconto = createTextFieldOutrosCampos();
        gbc.gridx = 0;
        gbc.gridy = 7;
        painelCampos.add(txtDesconto, gbc);

        lblPrecoTotal = new JLabel("Preço Total");
        lblPrecoTotal.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 8;
        painelCampos.add(lblPrecoTotal, gbc);

        txtPrecoTotal = createTextFieldOutrosCampos();
        gbc.gridx = 0;
        gbc.gridy = 9;
        painelCampos.add(txtPrecoTotal, gbc);

        painelPrincipal.add(painelCampos, BorderLayout.WEST);

        JPanel painelResumo = new JPanel();
        painelResumo.setBackground(Color.WHITE);
        painelResumo.setLayout(new BorderLayout());
        painelResumo.setPreferredSize(new Dimension(400, 440));
        painelResumo.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

        JLabel lblResumo = new JLabel("Resumo da Venda", SwingConstants.CENTER);
        lblResumo.setFont(new Font("Arial", Font.BOLD, 20));
        lblResumo.setForeground(Color.BLACK);

        painelResumo.add(lblResumo, BorderLayout.CENTER);
        painelPrincipal.add(painelResumo, BorderLayout.CENTER);

        return painelPrincipal;
    }

    private JPanel createBotoesVenda() {
        JPanel botoesVenda = new JPanel();
        botoesVenda.setLayout(new BoxLayout(botoesVenda, BoxLayout.X_AXIS));

        JButton btnIdentificarCliente = new JButton("Identificar Cliente");
        btnIdentificarCliente.setFont(new Font("Arial", Font.BOLD, 18));
        btnIdentificarCliente.setBackground(new Color(24, 39, 55));
        btnIdentificarCliente.setForeground(Color.WHITE);
        btnIdentificarCliente.setFocusPainted(false);
        btnIdentificarCliente.setPreferredSize(new Dimension(200, 45));
        botoesVenda.add(btnIdentificarCliente);

        botoesVenda.add(Box.createRigidArea(new Dimension(30, 0)));

        JButton btnCancelarVenda = new JButton("Cancelar Venda");
        btnCancelarVenda.setFont(new Font("Arial", Font.BOLD, 18));
        btnCancelarVenda.setBackground(Color.RED);
        btnCancelarVenda.setForeground(Color.WHITE);
        btnCancelarVenda.setFocusPainted(false);
        btnCancelarVenda.setPreferredSize(new Dimension(200, 40));
        botoesVenda.add(btnCancelarVenda);

        botoesVenda.add(Box.createRigidArea(new Dimension(30, 0)));

        JButton btnConfirmarVenda = new JButton("Confirmar Venda");
        btnConfirmarVenda.setFont(new Font("Arial", Font.BOLD, 18));
        btnConfirmarVenda.setBackground(new Color(0, 133, 0));
        btnConfirmarVenda.setForeground(Color.WHITE);
        btnConfirmarVenda.setFocusPainted(false);
        btnConfirmarVenda.setPreferredSize(new Dimension(200, 40));
        botoesVenda.add(btnConfirmarVenda);

        JPanel painelTotal = new JPanel();
        painelTotal.setLayout(new FlowLayout(FlowLayout.RIGHT));

        JLabel lblTotal = new JLabel("Total: ");
        lblTotal.setFont(new Font("Arial", Font.PLAIN, 20));
        painelTotal.add(lblTotal);

        JTextField txtTotal = new JTextField(15);
        txtTotal.setFont(new Font("Arial", Font.PLAIN, 20));
        txtTotal.setBackground(new Color(24, 39, 55));
        txtTotal.setForeground(Color.WHITE);
        txtTotal.setOpaque(true);
        txtTotal.setPreferredSize(new Dimension(20, 40));
        painelTotal.add(txtTotal);

        botoesVenda.add(painelTotal);

        return botoesVenda;
    }

    private JTextField createTextFieldItem() {
        JTextField textField = new JTextField();
        textField.setBackground(new Color(24, 39, 55));
        textField.setForeground(Color.WHITE);
        textField.setOpaque(true);
        textField.setFont(new Font("Arial", Font.PLAIN, 20));
        textField.setPreferredSize(new Dimension(1440, 45));
        return textField;
    }

    private JTextField createTextFieldOutrosCampos() {
        JTextField textField = new JTextField();
        textField.setBackground(Color.WHITE);
        textField.setForeground(Color.BLACK);
        textField.setOpaque(true);
        textField.setFont(new Font("Arial", Font.PLAIN, 20));
        textField.setPreferredSize(new Dimension(350, 45));
        textField.setHorizontalAlignment(SwingConstants.LEFT);
        return textField;
    }
}