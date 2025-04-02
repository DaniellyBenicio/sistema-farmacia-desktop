package views.Vendas;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;

public class GerarRelatorio extends JPanel {
    private Connection conn;
    private JComboBox<String> comboData, comboPagamento;
    private JComboBox<String> comboVendedor;
    private JTextField campoVendedorCustom;
    private JFormattedTextField txtDataInicio, txtDataFim;
    private JPanel painelDatasPersonalizadas;
    private CardLayout layoutCartao;
    private JPanel painelCentral;
    private JButton btnGerar;

    public GerarRelatorio(Connection conn, CardLayout layoutCartao, JPanel painelCentral) {
        this.conn = conn;
        this.layoutCartao = layoutCartao;
        this.painelCentral = painelCentral;
        setLayout(new BorderLayout());

        JPanel painelSuperior = criarTitulo();
        add(painelSuperior, BorderLayout.NORTH);

        JPanel painelFiltros = criarFiltros();
        add(painelFiltros, BorderLayout.CENTER);

        JPanel painelBotoes = criarBotoes();
        add(painelBotoes, BorderLayout.SOUTH);
    }

    private JPanel criarTitulo() {
        JPanel painelTitulo = new JPanel();
        painelTitulo.setLayout(new BoxLayout(painelTitulo, BoxLayout.Y_AXIS));
        painelTitulo.setBorder(BorderFactory.createEmptyBorder(40, 30, 20, 30));

        JLabel titulo = new JLabel("Gerar Relatório");
        titulo.setFont(new Font("Arial", Font.BOLD, 25));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        painelTitulo.add(titulo);

        return painelTitulo;
    }

    private JPanel criarFiltros() {
        JPanel painelFiltros = new JPanel(new GridBagLayout());
        painelFiltros.setBorder(BorderFactory.createEmptyBorder(0, 80, 0, 80));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 0, 10);

        JLabel lblData = new JLabel("Data da Venda");
        lblData.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 0;
        painelFiltros.add(lblData, gbc);

        JLabel lblVendedor = new JLabel("Vendedor");
        lblVendedor.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 1;
        gbc.gridy = 0;
        painelFiltros.add(lblVendedor, gbc);

        JLabel lblPagamento = new JLabel("Forma de Pagamento");
        lblPagamento.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 2;
        gbc.gridy = 0;
        painelFiltros.add(lblPagamento, gbc);

        comboData = new JComboBox<>(
                new String[] { "Selecione", "Hoje", "Ontem", "Últimos 15 dias", "Este mês", "Personalizado" });
        estilizarComboBox(comboData, new Font("Arial", Font.PLAIN, 14));
        comboData.setPreferredSize(new Dimension(150, 30));
        gbc.gridx = 0;
        gbc.gridy = 1;
        painelFiltros.add(comboData, gbc);

        painelDatasPersonalizadas = new JPanel(new GridBagLayout());
        painelDatasPersonalizadas.setVisible(false);

        GridBagConstraints gbcDatas = new GridBagConstraints();
        gbcDatas.gridx = 0;
        gbcDatas.gridy = 0;
        gbcDatas.weightx = 0;
        gbcDatas.fill = GridBagConstraints.NONE;
        gbcDatas.insets = new Insets(0, 0, 5, 5);

        JLabel lblDe = new JLabel("De:");
        lblDe.setFont(new Font("Arial", Font.PLAIN, 14));
        painelDatasPersonalizadas.add(lblDe, gbcDatas);

        gbcDatas.gridx = 1;
        gbcDatas.fill = GridBagConstraints.HORIZONTAL;
        gbcDatas.weightx = 1;

        try {
            MaskFormatter mascaraData = new MaskFormatter("##/##/####");
            mascaraData.setPlaceholderCharacter('_');
            txtDataInicio = new JFormattedTextField(mascaraData);
        } catch (ParseException e) {
            txtDataInicio = new JFormattedTextField();
        }
        txtDataInicio.setFont(new Font("Arial", Font.PLAIN, 14));
        txtDataInicio.setColumns(8);
        txtDataInicio.setToolTipText("DD/MM/AAAA");
        painelDatasPersonalizadas.add(txtDataInicio, gbcDatas);

        gbcDatas.gridx = 0;
        gbcDatas.gridy = 1;
        gbcDatas.weightx = 0;
        gbcDatas.fill = GridBagConstraints.NONE;

        JLabel lblAte = new JLabel("Até:");
        lblAte.setFont(new Font("Arial", Font.PLAIN, 14));
        painelDatasPersonalizadas.add(lblAte, gbcDatas);

        gbcDatas.gridx = 1;
        gbcDatas.fill = GridBagConstraints.HORIZONTAL;
        gbcDatas.weightx = 1;

        try {
            MaskFormatter mascaraData = new MaskFormatter("##/##/####");
            mascaraData.setPlaceholderCharacter('_');
            txtDataFim = new JFormattedTextField(mascaraData);
        } catch (ParseException e) {
            txtDataFim = new JFormattedTextField();
        }
        txtDataFim.setFont(new Font("Arial", Font.PLAIN, 14));
        txtDataFim.setColumns(8);
        txtDataFim.setToolTipText("DD/MM/AAAA");
        painelDatasPersonalizadas.add(txtDataFim, gbcDatas);

        gbc.gridx = 0;
        gbc.gridy = 2;
        painelFiltros.add(painelDatasPersonalizadas, gbc);

        comboData.addActionListener(e -> {
            boolean mostrarDatas = "Personalizado".equals(comboData.getSelectedItem());
            painelDatasPersonalizadas.setVisible(mostrarDatas);
            painelFiltros.revalidate();
            painelFiltros.repaint();
            verificarFiltros();
        });

        comboVendedor = new JComboBox<>();
        comboVendedor.setFont(new Font("Arial", Font.PLAIN, 14));
        comboVendedor.setPreferredSize(new Dimension(150, 30));
        comboVendedor.addItem("Selecione");
        comboVendedor.addItem("Todos");
        preencherVendedores();
        comboVendedor.addItem("Outros");
        estilizarComboBox(comboVendedor, new Font("Arial", Font.PLAIN, 14));

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        painelFiltros.add(comboVendedor, gbc);

        campoVendedorCustom = new JTextField();
        campoVendedorCustom.setFont(new Font("Arial", Font.PLAIN, 14));
        campoVendedorCustom.setPreferredSize(new Dimension(150, 30));
        campoVendedorCustom.setVisible(false);

        gbc.gridx = 1;
        gbc.gridy = 1;
        painelFiltros.add(campoVendedorCustom, gbc);

        comboVendedor.addActionListener(e -> {
            if ("Outros".equals(comboVendedor.getSelectedItem())) {
                comboVendedor.setVisible(false);
                campoVendedorCustom.setVisible(true);
            } else {
                campoVendedorCustom.setVisible(false);
                comboVendedor.setVisible(true);
            }
            painelFiltros.revalidate();
            painelFiltros.repaint();
            verificarFiltros();
        });

        campoVendedorCustom.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                verificarFiltros();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                verificarFiltros();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                verificarFiltros();
            }
        });

        comboPagamento = new JComboBox<>(
                new String[] { "Selecione", "Todos", "Dinheiro", "Cartão de Débito", "Cartão de Crédito", "PIX" });
        estilizarComboBox(comboPagamento, new Font("Arial", Font.PLAIN, 14));
        comboPagamento.setPreferredSize(new Dimension(150, 30));
        gbc.gridx = 2;
        gbc.gridy = 1;
        painelFiltros.add(comboPagamento, gbc);

        comboPagamento.addActionListener(e -> verificarFiltros());

        return painelFiltros;
    }

    private void preencherVendedores() {
        try {
            String sql = "SELECT nome FROM funcionario WHERE status = true ORDER BY nome";
            try (var stmt = conn.createStatement();
                    var rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    comboVendedor.addItem(rs.getString("nome"));
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao carregar vendedores: " + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel criarBotoes() {
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        painelBotoes.setBorder(BorderFactory.createEmptyBorder(80, 0, 30, 0));

        JButton btnLimpar = new JButton("Limpar Filtros");
        btnLimpar.setFont(new Font("Arial", Font.BOLD, 16));
        btnLimpar.setBackground(new Color(220, 53, 53));
        btnLimpar.setForeground(Color.WHITE);
        btnLimpar.setFocusPainted(false);
        btnLimpar.setPreferredSize(new Dimension(150, 40));
        btnLimpar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLimpar.addActionListener(e -> limparFiltros());

        btnGerar = new JButton("Gerar Relatório");
        btnGerar.setFont(new Font("Arial", Font.BOLD, 16));
        btnGerar.setBackground(new Color(24, 39, 72));
        btnGerar.setForeground(Color.WHITE);
        btnGerar.setFocusPainted(false);
        btnGerar.setPreferredSize(new Dimension(150, 40));
        btnGerar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGerar.addActionListener(e -> gerarRelatorio());

        btnGerar.setEnabled(false);

        painelBotoes.add(btnLimpar);
        painelBotoes.add(btnGerar);

        return painelBotoes;
    }

    public void limparFiltros() {
        comboData.setSelectedIndex(0);
        comboVendedor.setSelectedIndex(0);
        comboPagamento.setSelectedIndex(0);
        campoVendedorCustom.setText("");
        campoVendedorCustom.setVisible(false);
        painelDatasPersonalizadas.setVisible(false);
        txtDataInicio.setValue(null);
        txtDataFim.setValue(null);
        verificarFiltros();
    }

    private void gerarRelatorio() {
        String tipoData = (String) comboData.getSelectedItem();
        String pagamentoSelecionado = (String) comboPagamento.getSelectedItem();
        String vendedorSelecionado = (String) comboVendedor.getSelectedItem();
        String vendedor = vendedorSelecionado;

        System.out.println("Tipo de Data: " + tipoData);
        System.out.println("Pagamento: " + pagamentoSelecionado);
        System.out.println("Vendedor Selecionado: " + vendedorSelecionado);

        if ("Outros".equals(vendedorSelecionado)) {
            vendedor = campoVendedorCustom.getText().trim();
            if (vendedor.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Digite o nome do vendedor para a opção 'Outros'",
                        "Aviso",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        String dataInicioStr = null;
        String dataFimStr = null;
        if ("Personalizado".equals(tipoData)) {
            dataInicioStr = txtDataInicio.getText().replace("_", "").trim();
            dataFimStr = txtDataFim.getText().replace("_", "").trim();

            System.out.println("Data Início: " + dataInicioStr);
            System.out.println("Data Fim: " + dataFimStr);

            if (dataInicioStr.isEmpty() || dataFimStr.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Preencha ambas as datas para o período personalizado",
                        "Aviso",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        String formaPagamento = mapearFormaPagamento(pagamentoSelecionado);

        if (formaPagamento == null) {
            JOptionPane.showMessageDialog(this,
                    "Forma de pagamento inválida.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if ("Selecione".equals(tipoData) && "Selecione".equals(vendedorSelecionado)
                && "Selecione".equals(pagamentoSelecionado) && campoVendedorCustom.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecione pelo menos um filtro para gerar o relatório.", "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        RelatorioVendas relatorioVendas = new RelatorioVendas(
                conn,
                tipoData,
                vendedor,
                formaPagamento,
                dataInicioStr,
                dataFimStr);

        relatorioVendas.setLayoutDetails(layoutCartao, painelCentral);
        painelCentral.add(relatorioVendas, "RelatorioVendas");
        layoutCartao.show(painelCentral, "RelatorioVendas");

        System.out.println("Relatório gerado com sucesso.");
    }

    private String mapearFormaPagamento(String formaPagamento) {
        if (formaPagamento == null) {
            return null;
        }
        switch (formaPagamento) {
            case "Dinheiro":
                return "DINHEIRO";
            case "Cartão de Crédito":
                return "CARTAO_CREDITO";
            case "Cartão de Débito":
                return "CARTAO_DEBITO";
            case "PIX":
                return "PIX";
            case "Todos":
                return "Todos";
            case "Selecione":
                return "";
            default:
                return null;
        }
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

    private void verificarFiltros() {
        boolean algumFiltroSelecionado = !"Selecione".equals(comboData.getSelectedItem()) ||
                !"Selecione".equals(comboVendedor.getSelectedItem()) ||
                !"Selecione".equals(comboPagamento.getSelectedItem()) ||
                !campoVendedorCustom.getText().trim().isEmpty();

        btnGerar.setEnabled(algumFiltroSelecionado);
    }
}