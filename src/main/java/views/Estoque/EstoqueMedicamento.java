package views.Estoque;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import controllers.Medicamento.MedicamentoController;
import dao.Medicamento.MedicamentoDAO;
import models.Medicamento.Medicamento;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.time.format.DateTimeFormatter;

public class EstoqueMedicamento extends JPanel {
    private JTable tabela;
    private DefaultTableModel modeloTabela;
    private List<Medicamento> medicamentos;
    private JScrollPane tabelaScrollPane;
    private Connection conn;
    private PrincipalEstoque principalEstoque;
    private CardLayout layoutCartao;
    private JPanel painelCentral;
    private List<Boolean> linhasSelecionadas;
    private JButton realizarPedidoButton;
    private boolean baixoEstoqueSelecionado = false;
    private Timer timer;
    private JTextField campoBusca;

    public EstoqueMedicamento(Connection conn, PrincipalEstoque principalEstoque, CardLayout layoutCartao,
            JPanel painelCentral) {
        this.conn = conn;
        this.principalEstoque = principalEstoque;
        this.layoutCartao = layoutCartao;
        this.painelCentral = painelCentral;
        campoBusca = new JTextField();

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        linhasSelecionadas = new ArrayList<>();

        try {
            medicamentos = MedicamentoController.listarEstoqueMedicamento(this.conn);
            inicializarLinhasSelecionadas();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao carregar medicamentos.", "Erro", JOptionPane.ERROR_MESSAGE);
        }

        JPanel painelSuperior = criarTituloEBusca();
        add(painelSuperior, BorderLayout.NORTH);

        tabelaScrollPane = criarTabela();
        add(tabelaScrollPane, BorderLayout.CENTER);

        realizarPedidoButton = criarBotaoRealizarPedido();
        JPanel buttonPanel = criarPainelBotaoRealizarPedido(realizarPedidoButton);
        add(buttonPanel, BorderLayout.SOUTH);

        carregarDados();

        atualizarEstadoBotaoPedido();
        iniciarTimer();
    }

    private void iniciarTimer() {
        timer = new Timer(3000, e -> {
            if (!baixoEstoqueSelecionado && !isCampoBuscaPreenchido()) {
                atualizarTabela();
            }
        });
        timer.start();
    }

    private boolean isCampoBuscaPreenchido() {
        return campoBusca.getText() != null && !campoBusca.getText().trim().isEmpty()
                && !campoBusca.getText().equals("Buscar");
    }

    private void inicializarLinhasSelecionadas() {
        linhasSelecionadas.clear();
        for (int i = 0; i < medicamentos.size(); i++) {
            linhasSelecionadas.add(false);
        }
    }

    private JPanel criarTituloEBusca() {
        JPanel painelSuperior = new JPanel(new BorderLayout());

        JPanel painelVoltar = new JPanel();
        JButton voltar = new JButton("Voltar");
        voltar.setFont(new Font("Arial", Font.PLAIN, 17));
        voltar.setBorderPainted(false);
        voltar.setContentAreaFilled(false);
        voltar.setFocusPainted(true);
        voltar.setPreferredSize(new Dimension(90, 30));
        voltar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        voltar.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                voltar.setForeground((new Color(50, 100, 150)));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                voltar.setForeground(Color.BLACK);
            }
        });

        voltar.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> {
                layoutCartao.show(painelCentral, "GerenciamentoDeEstoque");
            });
        });

        painelVoltar.add(voltar);
        painelSuperior.add(painelVoltar, BorderLayout.WEST);

        JPanel painelTituloPrincipal = new JPanel();
        painelTituloPrincipal.setLayout(new BoxLayout(painelTituloPrincipal, BoxLayout.Y_AXIS));
        painelTituloPrincipal.setBorder(BorderFactory.createEmptyBorder(50, 0, 35, 0));

        JLabel tituloPrincipal = new JLabel("ESTOQUE DE MEDICAMENTOS");
        tituloPrincipal.setFont(new Font("Arial", Font.BOLD, 20));
        tituloPrincipal.setAlignmentX(Component.CENTER_ALIGNMENT);
        painelTituloPrincipal.add(tituloPrincipal);
        painelSuperior.add(painelTituloPrincipal, BorderLayout.CENTER);

        JPanel painelBusca = new JPanel();
        painelBusca.setLayout(new BoxLayout(painelBusca, BoxLayout.Y_AXIS));
        painelBusca.setBorder(BorderFactory.createEmptyBorder(10, 30, 30, 50));

        JLabel buscarTitulo = new JLabel("Buscar Medicamento");
        buscarTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        buscarTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        painelBusca.add(buscarTitulo);
        painelBusca.add(Box.createVerticalStrut(5));

        JPanel painelBuscaCampoBotoes = new JPanel();
        painelBuscaCampoBotoes.setLayout(new BoxLayout(painelBuscaCampoBotoes, BoxLayout.X_AXIS));
        painelBuscaCampoBotoes.setAlignmentX(Component.LEFT_ALIGNMENT);

        campoBusca.setFont(new Font("Arial", Font.PLAIN, 14));
        campoBusca.setPreferredSize(new Dimension(600, 30));
        campoBusca.setMaximumSize(new Dimension(600, 30));
        campoBusca.setText("Buscar");
        campoBusca.setForeground(Color.GRAY);

        campoBusca.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String termoBusca = campoBusca.getText();
                buscarMedicamento(termoBusca);
            }
        });

        campoBusca.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (campoBusca.getText().equals("Buscar")) {
                    campoBusca.setText("");
                    campoBusca.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (campoBusca.getText().isEmpty()) {
                    campoBusca.setText("Buscar");
                    campoBusca.setForeground(Color.GRAY);
                }
            }
        });

        campoBusca.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                buscarMedicamento(campoBusca.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                buscarMedicamento(campoBusca.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                buscarMedicamento(campoBusca.getText());
            }
        });

        JButton baixoEstoque = new JButton("Baixo Estoque");
        baixoEstoque.setFont(new Font("Arial", Font.BOLD, 16));
        baixoEstoque.setBackground(new Color(24, 39, 55));
        baixoEstoque.setForeground(Color.WHITE);
        baixoEstoque.setFocusPainted(false);
        baixoEstoque.setPreferredSize(new Dimension(160, 30));

        baixoEstoque.addActionListener(e -> {
            baixoEstoqueSelecionado = !baixoEstoqueSelecionado;

            if (baixoEstoqueSelecionado) {
                baixoEstoque.setBackground(new Color(50, 100, 150));

                try {
                    medicamentos = MedicamentoDAO.listarBaixoEstoque(this.conn);
                    inicializarLinhasSelecionadas();
                    carregarDados();
                    atualizarEstadoBotaoPedido();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Erro ao carregar baixo estoque.", "Erro",
                            JOptionPane.ERROR_MESSAGE);
                }
            } else {
                baixoEstoque.setBackground(new Color(24, 39, 55));
                try {
                    medicamentos = MedicamentoController.listarEstoqueMedicamento(this.conn);
                    inicializarLinhasSelecionadas();
                    carregarDados();
                    atualizarEstadoBotaoPedido();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Erro ao carregar medicamentos.", "Erro",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        painelBuscaCampoBotoes.add(campoBusca);
        painelBuscaCampoBotoes.add(Box.createHorizontalGlue());
        painelBuscaCampoBotoes.add(baixoEstoque);
        painelBuscaCampoBotoes.add(Box.createHorizontalStrut(10));

        painelBusca.add(painelBuscaCampoBotoes);
        painelSuperior.add(painelBusca, BorderLayout.SOUTH);

        return painelSuperior;
    }

    private void buscarMedicamento(String termoBusca) {
        List<Boolean> linhasSelecionadasAnterior = new ArrayList<>(linhasSelecionadas);
        List<Medicamento> medicamentosFiltrados = new ArrayList<>();

        String termo = (termoBusca != null) ? termoBusca.trim().toLowerCase() : "";

        if (termo.isEmpty() || termo.equals("buscar")) {
            try {
                if (baixoEstoqueSelecionado) {
                    medicamentosFiltrados = MedicamentoDAO.listarBaixoEstoque(this.conn);
                } else {
                    medicamentosFiltrados = MedicamentoController.listarEstoqueMedicamento(this.conn);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erro ao carregar medicamentos.", "Erro",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        } else {
            try {
                if (baixoEstoqueSelecionado) {
                    medicamentosFiltrados = medicamentos.stream()
                            .filter(medicamento -> {
                                String nome = medicamento.getNome().toLowerCase();
                                String categoria = medicamento.getCategoria().getNome().toLowerCase();
                                return nome.contains(termo) || categoria.contains(termo);
                            })
                            .collect(Collectors.toList());
                } else {
                    medicamentosFiltrados = MedicamentoDAO.buscarPorCategoriaOuNome(this.conn, termo);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erro ao buscar medicamentos.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        linhasSelecionadas.clear();
        for (Medicamento medicamento : medicamentosFiltrados) {
            boolean selecionado = false;
            for (int i = 0; i < medicamentos.size(); i++) {
                if (medicamentos.get(i).getId() == medicamento.getId() && i < linhasSelecionadasAnterior.size()) {
                    selecionado = linhasSelecionadasAnterior.get(i);
                    break;
                }
            }
            linhasSelecionadas.add(selecionado);
        }

        medicamentos = medicamentosFiltrados;
        carregarDados();
        atualizarEstadoBotaoPedido();
    }

    private JButton criarBotaoRealizarPedido() {
        JButton botao = new JButton("Realizar Pedido");
        botao.setFont(new Font("Arial", Font.BOLD, 16));
        botao.setBackground(new Color(24, 39, 55));
        botao.setForeground(Color.WHITE);
        botao.setFocusPainted(false);
        botao.setPreferredSize(new Dimension(170, 30));
        botao.setEnabled(false);

        botao.addActionListener(e -> {
            List<Medicamento> medicamentosSelecionados = obterMedicamentosSelecionados();
            if (!medicamentosSelecionados.isEmpty()) {
                new RealizarPedidoMedicamento(null, medicamentosSelecionados).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Nenhum medicamento selecionado.", "Aviso",
                        JOptionPane.WARNING_MESSAGE);
            }
        });

        return botao;
    }

    private JPanel criarPainelBotaoRealizarPedido(JButton botao) {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 30, 30, 50));
        buttonPanel.add(botao);

        return buttonPanel;
    }

    private void atualizarTabela() {
        if (baixoEstoqueSelecionado || isCampoBuscaPreenchido()) {
            return;
        }

        try {
            List<Medicamento> novosMedicamentos = MedicamentoController.listarEstoqueMedicamento(this.conn);
            if (!novosMedicamentos.equals(medicamentos)) {
                medicamentos = novosMedicamentos;
                inicializarLinhasSelecionadas();
                carregarDados();
                atualizarEstadoBotaoPedido();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao carregar medicamentos.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String formatarData(LocalDate data) {
        if (data != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yyyy");
            return data.format(formatter);
        }
        return "";
    }

    private JScrollPane criarTabela() {
        String[] colunas = { "Selecionar", "Nome", "Categoria", "Embalagem", "F. Farmacêutica", "Dosagem", "Fornecedor",
                "Validade",
                "Preço Unitário", "Quantidade" };

        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public Class<?> getColumnClass(int column) {
                return column == 0 ? Boolean.class : Object.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0 && baixoEstoqueSelecionado && !medicamentos.isEmpty();
            }
        };

        tabela = new JTable(modeloTabela) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (linhasSelecionadas.size() > row && linhasSelecionadas.get(row)) {
                    c.setBackground(new Color(173, 216, 230));
                } else {
                    c.setBackground(Color.WHITE);
                }

                if (baixoEstoqueSelecionado && column == 9) {
                    c.setForeground(Color.RED);
                } else {
                    c.setForeground(Color.BLACK);
                }

                return c;
            }
        };

        tabela.setFillsViewportHeight(true);
        tabela.setRowHeight(35);
        tabela.setFont(new Font("Arial", Font.PLAIN, 12));
        tabela.getTableHeader().setFont(new Font("Arial", Font.BOLD, 15));
        tabela.getTableHeader().setReorderingAllowed(false);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        for (int i = 1; i < colunas.length; i++) {
            tabela.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        tabela.getColumnModel().getColumn(0).setPreferredWidth(10);
        tabela.getColumnModel().getColumn(1).setPreferredWidth(170);
        tabela.getColumnModel().getColumn(2).setPreferredWidth(110);
        tabela.getColumnModel().getColumn(3).setPreferredWidth(40);
        tabela.getColumnModel().getColumn(4).setPreferredWidth(60);
        tabela.getColumnModel().getColumn(5).setPreferredWidth(10);
        tabela.getColumnModel().getColumn(6).setPreferredWidth(120);
        tabela.getColumnModel().getColumn(7).setPreferredWidth(10);
        tabela.getColumnModel().getColumn(8).setPreferredWidth(30);
        tabela.getColumnModel().getColumn(9).setPreferredWidth(10);

        tabela.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = tabela.rowAtPoint(evt.getPoint());
                int column = tabela.columnAtPoint(evt.getPoint());
                if (column == 0 && baixoEstoqueSelecionado && !medicamentos.isEmpty()) {
                    linhasSelecionadas.set(row, !linhasSelecionadas.get(row));
                    modeloTabela.fireTableRowsUpdated(row, row);
                    atualizarEstadoBotaoPedido();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(tabela);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 30, 0, 30));

        return scrollPane;
    }

    private void carregarDados() {
        modeloTabela.setRowCount(0);

        if (medicamentos == null || medicamentos.isEmpty()) {
            modeloTabela
                    .addRow(new Object[] { false, "Nenhum medicamento encontrado.", "", "", "", "", "", "", "", "" });
        } else {
            for (int i = 0; i < medicamentos.size(); i++) {
                Medicamento medicamento = medicamentos.get(i);
                Object[] rowData = new Object[11];
                rowData[0] = linhasSelecionadas.get(i);
                rowData[1] = medicamento.getNome();
                rowData[2] = medicamento.getCategoria().getNome();
                rowData[3] = medicamento.getEmbalagem();
                rowData[4] = medicamento.getFormaFarmaceutica();
                rowData[5] = medicamento.getDosagem();
                rowData[6] = medicamento.getFornecedor().getNome();
                rowData[7] = formatarData(medicamento.getDataValidade());
                rowData[8] = medicamento.getValorUnit();
                rowData[9] = formatarEstoque(medicamento.getQnt());

                modeloTabela.addRow(rowData);
            }
        }
    }

    private String formatarEstoque(int estoque) {
        return String.format("%,d", estoque);
    }

    private void atualizarEstadoBotaoPedido() {
        boolean algumSelecionado = linhasSelecionadas.contains(true);
        realizarPedidoButton.setEnabled(algumSelecionado && baixoEstoqueSelecionado);
    }

    private List<Medicamento> obterMedicamentosSelecionados() {
        List<Medicamento> selecionados = new ArrayList<>();
        for (int i = 0; i < medicamentos.size(); i++) {
            if (linhasSelecionadas.get(i)) {
                selecionados.add(medicamentos.get(i));
            }
        }
        return selecionados;
    }
}