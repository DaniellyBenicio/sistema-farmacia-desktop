package views.Vendas;

import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GerarRelatorio extends JPanel {
    private Connection conn;
    private JComboBox<String> comboData, comboPagamento;
    private JComboBox<String> comboVendedor, comboCategoria, comboTipoMedicamento, comboFornecedor;
    private JTextField campoVendedorCustom;
    private JFormattedTextField txtDataInicio, txtDataFim;
    private JPanel painelDatasPersonalizadas;

    public GerarRelatorio(Connection conn) {
        this.conn = conn;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

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
        painelTitulo.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        painelTitulo.setBackground(Color.WHITE);

        JLabel titulo = new JLabel("Gerar Relatório");
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        painelTitulo.add(titulo);

        return painelTitulo;
    }

    private JPanel criarFiltros() {
        JPanel painelFiltros = new JPanel(new GridBagLayout());
        painelFiltros.setBorder(BorderFactory.createEmptyBorder(0, 80, 0, 80));
        painelFiltros.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);
        
        // Linha 0 - Labels
        JLabel lblData = new JLabel("Data da venda");
        lblData.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 0;
        painelFiltros.add(lblData, gbc);

        JLabel lblVendedor = new JLabel("Vendedor");
        lblVendedor.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.gridy = 0;
        painelFiltros.add(lblVendedor, gbc);

        JLabel lblPagamento = new JLabel("Forma de Pagamento");
        lblPagamento.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 2;
        gbc.gridy = 0;
        painelFiltros.add(lblPagamento, gbc);

        // Linha 1 - Combos superiores
        comboData = new JComboBox<>(new String[]{"Selecione", "Hoje", "Ontem", "Esta semana", "Este mês", "Personalizado"});
        comboData.setFont(new Font("Arial", Font.PLAIN, 14));
        comboData.setPreferredSize(new Dimension(150, 30));
        gbc.gridx = 0;
        gbc.gridy = 1;
        painelFiltros.add(comboData, gbc);

        // Painel de datas personalizadas
        painelDatasPersonalizadas = new JPanel(new GridLayout(1, 4, 5, 5));
        painelDatasPersonalizadas.setBackground(Color.WHITE);
        painelDatasPersonalizadas.setVisible(false);
        
        JLabel lblDe = new JLabel("De:");
        lblDe.setFont(new Font("Arial", Font.PLAIN, 12));
        painelDatasPersonalizadas.add(lblDe);
        
        try {
            MaskFormatter mascaraData = new MaskFormatter("##/##/####");
            mascaraData.setPlaceholderCharacter('_');
            txtDataInicio = new JFormattedTextField(mascaraData);
        } catch (ParseException e) {
            txtDataInicio = new JFormattedTextField();
        }
        txtDataInicio.setFont(new Font("Arial", Font.PLAIN, 12));
        txtDataInicio.setColumns(8);
        txtDataInicio.setToolTipText("DD/MM/AAAA");
        painelDatasPersonalizadas.add(txtDataInicio);
        
        JLabel lblAte = new JLabel("Até:");
        lblAte.setFont(new Font("Arial", Font.PLAIN, 12));
        painelDatasPersonalizadas.add(lblAte);
        
        try {
            MaskFormatter mascaraData = new MaskFormatter("##/##/####");
            mascaraData.setPlaceholderCharacter('_');
            txtDataFim = new JFormattedTextField(mascaraData);
        } catch (ParseException e) {
            txtDataFim = new JFormattedTextField();
        }
        txtDataFim.setFont(new Font("Arial", Font.PLAIN, 12));
        txtDataFim.setColumns(8);
        txtDataFim.setToolTipText("DD/MM/AAAA");
        painelDatasPersonalizadas.add(txtDataFim);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        painelFiltros.add(painelDatasPersonalizadas, gbc);

        // Listener para mostrar/ocultar datas personalizadas
        comboData.addActionListener(e -> {
            boolean mostrarDatas = "Personalizado".equals(comboData.getSelectedItem());
            painelDatasPersonalizadas.setVisible(mostrarDatas);
            painelFiltros.revalidate();
            painelFiltros.repaint();
            
            if (mostrarDatas) {
                txtDataInicio.requestFocusInWindow();
            }
        });

        // Combo vendedor
        comboVendedor = new JComboBox<>();
        comboVendedor.setFont(new Font("Arial", Font.PLAIN, 14));
        comboVendedor.setPreferredSize(new Dimension(150, 30));
        comboVendedor.addItem("Selecione");
        comboVendedor.addItem("Todos");
        preencherVendedores();
        comboVendedor.addItem("Outros");
        
        campoVendedorCustom = new JTextField();
        campoVendedorCustom.setFont(new Font("Arial", Font.PLAIN, 14));
        campoVendedorCustom.setPreferredSize(new Dimension(150, 30));
        campoVendedorCustom.setVisible(false);
        
        JPanel painelVendedor = new JPanel();
        painelVendedor.setLayout(new BoxLayout(painelVendedor, BoxLayout.Y_AXIS));
        painelVendedor.setBackground(Color.WHITE);
        painelVendedor.add(comboVendedor);
        painelVendedor.add(campoVendedorCustom);
        
        gbc.gridx = 1;
        gbc.gridy = 1;
        painelFiltros.add(painelVendedor, gbc);

        comboVendedor.addActionListener(e -> {
            boolean mostrarCampo = "Outros".equals(comboVendedor.getSelectedItem());
            campoVendedorCustom.setVisible(mostrarCampo);
            painelFiltros.revalidate();
            painelFiltros.repaint();
        });

        comboPagamento = new JComboBox<>(new String[]{"Selecione", "Todos", "Dinheiro", "Cartão de Débito", "Cartão de Crédito", "PIX"});
        comboPagamento.setFont(new Font("Arial", Font.PLAIN, 14));
        comboPagamento.setPreferredSize(new Dimension(150, 30));
        gbc.gridx = 2;
        gbc.gridy = 1;
        painelFiltros.add(comboPagamento, gbc);

        // Linha 2 - Labels dos novos filtros
        JLabel lblCategoria = new JLabel("Categoria");
        lblCategoria.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 3;
        painelFiltros.add(lblCategoria, gbc);

        JLabel lblTipoMedicamento = new JLabel("Tipo de Medicamento");
        lblTipoMedicamento.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.gridy = 3;
        painelFiltros.add(lblTipoMedicamento, gbc);

        JLabel lblFornecedor = new JLabel("Fornecedor");
        lblFornecedor.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 2;
        gbc.gridy = 3;
        painelFiltros.add(lblFornecedor, gbc);

        // Linha 3 - Novos combos
        comboCategoria = new JComboBox<>();
        comboCategoria.setFont(new Font("Arial", Font.PLAIN, 14));
        comboCategoria.setPreferredSize(new Dimension(150, 30));
        comboCategoria.addItem("Selecione");
        comboCategoria.addItem("Todos");
        preencherCategorias();
        gbc.gridx = 0;
        gbc.gridy = 4;
        painelFiltros.add(comboCategoria, gbc);

        comboTipoMedicamento = new JComboBox<>(new String[]{"Selecione", "Todos", "Ético", "Genérico", "Similar"});
        comboTipoMedicamento.setFont(new Font("Arial", Font.PLAIN, 14));
        comboTipoMedicamento.setPreferredSize(new Dimension(150, 30));
        gbc.gridx = 1;
        gbc.gridy = 4;
        painelFiltros.add(comboTipoMedicamento, gbc);

        comboFornecedor = new JComboBox<>();
        comboFornecedor.setFont(new Font("Arial", Font.PLAIN, 14));
        comboFornecedor.setPreferredSize(new Dimension(150, 30));
        comboFornecedor.addItem("Selecione");
        comboFornecedor.addItem("Todos");
        preencherFornecedores();
        gbc.gridx = 2;
        gbc.gridy = 4;
        painelFiltros.add(comboFornecedor, gbc);

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

    private void preencherCategorias() {
        try {
            String sql = "SELECT nome FROM categoria ORDER BY nome";
            try (var stmt = conn.createStatement();
                 var rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    comboCategoria.addItem(rs.getString("nome"));
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao carregar categorias: " + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void preencherFornecedores() {
        try {
            String sql = "SELECT nome FROM fornecedor ORDER BY nome";
            try (var stmt = conn.createStatement();
                 var rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    comboFornecedor.addItem(rs.getString("nome"));
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao carregar fornecedores: " + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel criarBotoes() {
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        painelBotoes.setBorder(BorderFactory.createEmptyBorder(80, 0, 30, 0));
        painelBotoes.setBackground(Color.WHITE);

        JButton btnLimpar = new JButton("Limpar filtro");
        btnLimpar.setFont(new Font("Arial", Font.BOLD, 14));
        btnLimpar.setBackground(new Color(220, 53, 53));
        btnLimpar.setForeground(Color.WHITE);
        btnLimpar.setFocusPainted(false);
        btnLimpar.setPreferredSize(new Dimension(150, 40));
        btnLimpar.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        btnLimpar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLimpar.addActionListener(e -> limparFiltros());

        JButton btnGerar = new JButton("Gerar Relatório");
        btnGerar.setFont(new Font("Arial", Font.BOLD, 14));
        btnGerar.setBackground(new Color(24, 39, 72));
        btnGerar.setForeground(Color.WHITE);
        btnGerar.setFocusPainted(false);
        btnGerar.setPreferredSize(new Dimension(150, 40));
        btnGerar.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        btnGerar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGerar.addActionListener(e -> gerarRelatorio());

        painelBotoes.add(btnLimpar);
        painelBotoes.add(btnGerar);

        return painelBotoes;
    }

    private void limparFiltros() {
        comboData.setSelectedIndex(0);
        comboVendedor.setSelectedIndex(0);
        comboPagamento.setSelectedIndex(0);
        comboCategoria.setSelectedIndex(0);
        comboTipoMedicamento.setSelectedIndex(0);
        comboFornecedor.setSelectedIndex(0);
        campoVendedorCustom.setText("");
        campoVendedorCustom.setVisible(false);
        painelDatasPersonalizadas.setVisible(false);
        txtDataInicio.setValue(null);
        txtDataFim.setValue(null);
    }

    private void gerarRelatorio() {
        String data = (String) comboData.getSelectedItem();
        String pagamento = (String) comboPagamento.getSelectedItem();
        String categoria = (String) comboCategoria.getSelectedItem();
        String tipoMedicamento = (String) comboTipoMedicamento.getSelectedItem();
        String fornecedor = (String) comboFornecedor.getSelectedItem();
        
        // Processamento do vendedor
        String vendedorSelecionado = (String) comboVendedor.getSelectedItem();
        String vendedor = vendedorSelecionado;
        
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

        // Validação de datas personalizadas
        if ("Personalizado".equals(data)) {
            String dataInicioStr = txtDataInicio.getText().replace("_", "").trim();
            String dataFimStr = txtDataFim.getText().replace("_", "").trim();
            
            if (dataInicioStr.isEmpty() || dataFimStr.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Preencha ambas as datas para o período personalizado",
                        "Aviso",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                sdf.setLenient(false);
                Date dataInicio = sdf.parse(dataInicioStr);
                Date dataFim = sdf.parse(dataFimStr);
                
                if (dataInicio.after(dataFim)) {
                    JOptionPane.showMessageDialog(this,
                            "A data inicial não pode ser posterior à data final",
                            "Aviso",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Formato de data inválido. Use DD/MM/AAAA",
                        "Erro",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        // Verifica se pelo menos um filtro foi selecionado
        if (data.equals("Selecione") && vendedorSelecionado.equals("Selecione") && 
            pagamento.equals("Selecione") && categoria.equals("Selecione") && 
            tipoMedicamento.equals("Selecione") && fornecedor.equals("Selecione")) {
            JOptionPane.showMessageDialog(this,
                    "Selecione pelo menos um filtro para gerar o relatório.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Processamento da forma de pagamento
        String formaPagamento = null;
        if (!pagamento.equals("Selecione") && !pagamento.equals("Todos")) {
            switch (pagamento) {
                case "Dinheiro": formaPagamento = "DINHEIRO"; break;
                case "Cartão de Débito": formaPagamento = "CARTAO_DEBITO"; break;
                case "Cartão de Crédito": formaPagamento = "CARTAO_CREDITO"; break;
                case "PIX": formaPagamento = "PIX"; break;
            }
        }

        // Processamento do tipo de medicamento
        String tipoMedicamentoDB = null;
        if (!tipoMedicamento.equals("Selecione") && !tipoMedicamento.equals("Todos")) {
            switch (tipoMedicamento) {
                case "Ético": tipoMedicamentoDB = "ÉTICO"; break;
                case "Genérico": tipoMedicamentoDB = "GENÉRICO"; break;
                case "Similar": tipoMedicamentoDB = "SIMILAR"; break;
            }
        }

        // Abrir tela de relatório com todos os parâmetros
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        frame.getContentPane().removeAll();
        frame.getContentPane().add(new RelatorioVendas(
            conn, 
            data, 
            vendedor, 
            formaPagamento, 
            txtDataInicio.getText(), 
            txtDataFim.getText(),
            categoria.equals("Todos") ? null : categoria,
            tipoMedicamentoDB,
            fornecedor.equals("Todos") ? null : fornecedor
        ));
        frame.revalidate();
        frame.repaint();
    }
}