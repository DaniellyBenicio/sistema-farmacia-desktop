package views.Vendas;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class VisualizarVendas extends JPanel {

    private JTable tabelaVendas;
    private DefaultTableModel modeloTabela;
    private JFormattedTextField campoBusca;
    private Connection conn;
    private JButton btnFiltro;

    public VisualizarVendas(Connection conn) {
        this.conn = conn;

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JPanel painelSuperior = criarTituloEBusca();
        add(painelSuperior, BorderLayout.NORTH);

        JPanel painelTabela = criarTabela();
        add(painelTabela, BorderLayout.CENTER);

        atualizarTabela("", null);
    }

    private JPanel criarTituloEBusca() {
        JPanel painelSuperior = new JPanel(new BorderLayout());
        painelSuperior.setBackground(Color.WHITE);

        // Painel de Título
        JPanel painelTitulo = new JPanel();
        painelTitulo.setLayout(new BoxLayout(painelTitulo, BoxLayout.Y_AXIS));
        painelTitulo.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        painelTitulo.setBackground(Color.WHITE);

        JLabel titulo = new JLabel("ÚLTIMAS VENDAS REALIZADAS");
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        painelTitulo.add(titulo);

        // Painel de Busca
        JPanel painelBusca = new JPanel(new BorderLayout());
        painelBusca.setBackground(Color.WHITE);
        painelBusca.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel painelEsquerda = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelEsquerda.setBackground(Color.WHITE);

        JLabel lblBuscarVenda = new JLabel("Buscar venda:");
        lblBuscarVenda.setFont(new Font("Arial", Font.PLAIN, 14));
        painelEsquerda.add(lblBuscarVenda);

        campoBusca = new JFormattedTextField(); // Campo de busca sem máscara inicial
        campoBusca.setFont(new Font("Arial", Font.PLAIN, 14));
        campoBusca.setPreferredSize(new Dimension(120, 30));
        campoBusca.setEditable(false); // Inicialmente desabilitado
        campoBusca.setToolTipText("Selecione um filtro para buscar"); 
        painelEsquerda.add(campoBusca);

        painelBusca.add(painelEsquerda, BorderLayout.CENTER);

        JPanel painelDireita = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        painelDireita.setBackground(Color.WHITE);

        btnFiltro = new JButton();
        btnFiltro.setIcon(UIManager.getIcon("Table.descendingSortIcon"));
        btnFiltro.setBackground(new Color(24, 39, 55));
        btnFiltro.setForeground(Color.WHITE);
        btnFiltro.setFocusPainted(false);
        btnFiltro.setPreferredSize(new Dimension(30, 30));
        btnFiltro.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem itemVendedor = new JMenuItem("Vendedor");
        JMenuItem itemData = new JMenuItem("Data");
        JMenuItem itemLimparFiltro = new JMenuItem("Limpar Filtro"); 

        itemVendedor.addActionListener(e -> {
            btnFiltro.setText("Vendedor");
            campoBusca.setValue(null);
            campoBusca.setEditable(true);
            campoBusca.setToolTipText("Digite o nome do vendedor");
            campoBusca.setFormatterFactory(null); // Remove a máscara
        });

        itemData.addActionListener(e -> {
            btnFiltro.setText("Data");
            campoBusca.setValue(null);
            campoBusca.setEditable(true);
            campoBusca.setToolTipText("Digite a data no formato DD-MM-YYYY");
            try {
                MaskFormatter mascaraData = new MaskFormatter("##-##-####");
                mascaraData.setPlaceholderCharacter('_');
                campoBusca.setFormatterFactory(new DefaultFormatterFactory(mascaraData)); // Aplica a máscara de data
            } catch (ParseException ex) {
                ex.printStackTrace();
            }
        });

        itemLimparFiltro.addActionListener(e -> {
            btnFiltro.setText("Filtro"); 
            campoBusca.setValue(null); 
            campoBusca.setEditable(false); // Desabilita a edição
            campoBusca.setToolTipText("Selecione um filtro para buscar"); 
            campoBusca.setFormatterFactory(null); // Remove a máscara
            atualizarTabela("", null); // Atualiza a tabela sem filtro
        });

        popupMenu.add(itemVendedor);
        popupMenu.add(itemData);
        popupMenu.add(itemLimparFiltro); 

        btnFiltro.addActionListener(e -> popupMenu.show(btnFiltro, 0, btnFiltro.getHeight()));

        painelDireita.add(btnFiltro);

        JButton btnFiltrar = new JButton("Filtrar Vendas");
        btnFiltrar.setFont(new Font("Arial", Font.BOLD, 14));
        btnFiltrar.setBackground(new Color(24, 39, 55));
        btnFiltrar.setForeground(Color.WHITE);
        btnFiltrar.setFocusPainted(false);
        btnFiltrar.setPreferredSize(new Dimension(150, 30));
        btnFiltrar.addActionListener(e -> {
            String termoBusca = campoBusca.getText().trim();
            String tipoFiltro = btnFiltro.getText();

            if (tipoFiltro.equals("Data")) {
                if (!termoBusca.matches("\\d{2}-\\d{2}-\\d{4}")) {
                    JOptionPane.showMessageDialog(this, "Formato de data inválido. Use DD-MM-YYYY.", "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try {
                    SimpleDateFormat formatoEntrada = new SimpleDateFormat("dd-MM-yyyy");
                    SimpleDateFormat formatoSaida = new SimpleDateFormat("yyyy-MM-dd");
                    Date data = formatoEntrada.parse(termoBusca);
                    termoBusca = formatoSaida.format(data);
                } catch (ParseException ex) {
                    JOptionPane.showMessageDialog(this, "Erro ao converter a data.", "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            atualizarTabela(termoBusca, tipoFiltro);
        });

        painelDireita.add(btnFiltrar);
        painelBusca.add(painelDireita, BorderLayout.EAST);

       
        painelSuperior.add(painelTitulo, BorderLayout.NORTH);
        painelSuperior.add(painelBusca, BorderLayout.CENTER);

        return painelSuperior;
    }

    private JPanel criarTabela() {
        JPanel painelTabela = new JPanel(new BorderLayout());
        painelTabela.setBackground(Color.WHITE);

        String[] colunas = {"DATA", "HORÁRIO", "VENDEDOR", "VALOR TOTAL", "FORMA DE PAGAMENTO", "DETALHES"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabelaVendas = new JTable(modeloTabela);
        tabelaVendas.setFont(new Font("Arial", Font.PLAIN, 14));
        tabelaVendas.setRowHeight(35);
        tabelaVendas.getTableHeader().setFont(new Font("Arial", Font.BOLD, 16));
        tabelaVendas.getTableHeader().setReorderingAllowed(false);
        tabelaVendas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaVendas.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        for (int i = 0; i < colunas.length; i++) {
            tabelaVendas.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(tabelaVendas);
        painelTabela.add(scrollPane, BorderLayout.CENTER);

        return painelTabela;
    }

    private void atualizarTabela(String termoBusca, String tipoFiltro) {
        modeloTabela.setRowCount(0);
        String sql;
        PreparedStatement stmt;

        try {
            if (tipoFiltro != null) {
                switch (tipoFiltro) {
                    case "Vendedor":
                        sql = "SELECT v.id, v.data, v.horario, f.nome AS vendedor, v.valorTotal, v.formaPagamento " +
                              "FROM venda v " +
                              "LEFT JOIN funcionario f ON v.funcionario_id = f.id " +
                              "WHERE f.nome LIKE ? " +
                              "ORDER BY v.data DESC, v.horario DESC";
                        stmt = conn.prepareStatement(sql);
                        stmt.setString(1, "%" + termoBusca + "%");
                        break;

                    case "Data":
                        sql = "SELECT v.id, v.data, v.horario, f.nome AS vendedor, v.valorTotal, v.formaPagamento " +
                              "FROM venda v " +
                              "LEFT JOIN funcionario f ON v.funcionario_id = f.id " +
                              "WHERE DATE(v.data) = ? " +
                              "ORDER BY v.data DESC, v.horario DESC";
                        stmt = conn.prepareStatement(sql);
                        stmt.setString(1, termoBusca);
                        break;

                    default:
                        sql = "SELECT v.id, v.data, v.horario, f.nome AS vendedor, v.valorTotal, v.formaPagamento " +
                              "FROM venda v " +
                              "LEFT JOIN funcionario f ON v.funcionario_id = f.id " +
                              "ORDER BY v.data DESC, v.horario DESC";
                        stmt = conn.prepareStatement(sql);
                        break;
                }
            } else {
                sql = "SELECT v.id, v.data, v.horario, f.nome AS vendedor, v.valorTotal, v.formaPagamento " +
                      "FROM venda v " +
                      "LEFT JOIN funcionario f ON v.funcionario_id = f.id " +
                      "ORDER BY v.data DESC, v.horario DESC";
                stmt = conn.prepareStatement(sql);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int vendaId = rs.getInt("id");

                    String detalhes = obterDetalhesVenda(vendaId);

                    Object[] row = {
                        rs.getDate("data"),
                        rs.getTime("horario"),
                        rs.getString("vendedor"),
                        rs.getDouble("valorTotal"),
                        rs.getString("formaPagamento"),
                        detalhes
                    };
                    modeloTabela.addRow(row);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar vendas: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String obterDetalhesVenda(int vendaId) {
        StringBuilder detalhes = new StringBuilder();

        try {
            String sqlProdutos = "SELECT p.nome, iv.qnt " +
                                 "FROM itemVenda iv " +
                                 "LEFT JOIN produto p ON iv.produto_id = p.id " +
                                 "WHERE iv.venda_id = ?";
            PreparedStatement stmtProdutos = conn.prepareStatement(sqlProdutos);
            stmtProdutos.setInt(1, vendaId);

            try (ResultSet rsProdutos = stmtProdutos.executeQuery()) {
                while (rsProdutos.next()) {
                    String nomeProduto = rsProdutos.getString("nome");
                    int quantidade = rsProdutos.getInt("qnt");
                    detalhes.append(nomeProduto).append(" (").append(quantidade).append("), ");
                }
            }

            String sqlMedicamentos = "SELECT m.nome, iv.qnt " +
                                    "FROM itemVenda iv " +
                                    "LEFT JOIN medicamento m ON iv.medicamento_id = m.id " +
                                    "WHERE iv.venda_id = ?";
            PreparedStatement stmtMedicamentos = conn.prepareStatement(sqlMedicamentos);
            stmtMedicamentos.setInt(1, vendaId);

            try (ResultSet rsMedicamentos = stmtMedicamentos.executeQuery()) {
                while (rsMedicamentos.next()) {
                    String nomeMedicamento = rsMedicamentos.getString("nome");
                    int quantidade = rsMedicamentos.getInt("qnt");
                    detalhes.append(nomeMedicamento).append(" (").append(quantidade).append("), ");
                }
            }

            if (detalhes.length() > 0) {
                detalhes.setLength(detalhes.length() - 2);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao buscar detalhes da venda: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }

        return detalhes.toString();
    }
}