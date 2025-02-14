package views.Estoque;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import dao.Medicamento.MedicamentoDAO;
import models.Medicamento.Medicamento;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
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

    public EstoqueMedicamento(Connection conn, PrincipalEstoque principalEstoque, CardLayout layoutCartao,
            JPanel painelCentral) {
        this.conn = conn;
        this.principalEstoque = principalEstoque;
        this.layoutCartao = layoutCartao;
        this.painelCentral = painelCentral;

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        linhasSelecionadas = new ArrayList<>();

        try {
            medicamentos = MedicamentoDAO.listarTodos(this.conn);
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
    }

    private void inicializarLinhasSelecionadas() {
        linhasSelecionadas.clear();
        for (int i = 0; i < medicamentos.size(); i++) {
            linhasSelecionadas.add(false);
        }
    }

    private JPanel criarTituloEBusca() {
        JPanel painelSuperior = new JPanel(new BorderLayout());

        JPanel painelTitulo = new JPanel();
        painelTitulo.setLayout(new BoxLayout(painelTitulo, BoxLayout.Y_AXIS));
        painelTitulo.setBorder(BorderFactory.createEmptyBorder(50, 0, 35, 0));

        JLabel titulo = new JLabel("ESTOQUE DE MEDICAMENTOS");
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        painelTitulo.add(titulo);

        JPanel painelBuscarTitulo = new JPanel();
        painelBuscarTitulo.setLayout(new BoxLayout(painelBuscarTitulo, BoxLayout.X_AXIS));
        painelBuscarTitulo.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 50));
        JLabel buscarTitulo = new JLabel("Buscar Medicamento");
        buscarTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        painelBuscarTitulo.add(buscarTitulo);

        JPanel painelBuscaBotao = new JPanel();
        painelBuscaBotao.setLayout(new BoxLayout(painelBuscaBotao, BoxLayout.X_AXIS));
        painelBuscaBotao.setBorder(BorderFactory.createEmptyBorder(0, 30, 30, 50));

        JTextField campoBusca = new JTextField();
        campoBusca.setFont(new Font("Arial", Font.PLAIN, 14));
        campoBusca.setPreferredSize(new Dimension(600, 30));
        campoBusca.setMaximumSize(new Dimension(600, 30));
        campoBusca.setText("Buscar");
        campoBusca.setForeground(Color.GRAY);

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
                filtrarMedicamentos(campoBusca.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filtrarMedicamentos(campoBusca.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filtrarMedicamentos(campoBusca.getText());
            }
        });

        JButton baixoEstoque = new JButton("Baixo Estoque");
        baixoEstoque.setFont(new Font("Arial", Font.BOLD, 16));
        baixoEstoque.setBackground(new Color(24, 39, 55));
        baixoEstoque.setForeground(Color.WHITE);
        baixoEstoque.setFocusPainted(false);
        baixoEstoque.setPreferredSize(new Dimension(150, 30));

        JButton voltar = new JButton("Voltar");
        voltar.setFont(new Font("Arial", Font.BOLD, 16));
        voltar.setBackground(new Color(24, 39, 55));
        voltar.setForeground(Color.WHITE);
        voltar.setFocusPainted(false);
        voltar.setPreferredSize(new Dimension(150, 30));

        voltar.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> {
                layoutCartao.show(painelCentral, "GerenciamentoDeEstoque");
            });
        });

        painelBuscaBotao.add(campoBusca);
        painelBuscaBotao.add(Box.createHorizontalGlue());
        painelBuscaBotao.add(baixoEstoque);
        painelBuscaBotao.add(Box.createHorizontalStrut(10));
        painelBuscaBotao.add(voltar);

        painelSuperior.add(painelTitulo, BorderLayout.NORTH);
        painelSuperior.add(painelBuscarTitulo, BorderLayout.CENTER);
        painelSuperior.add(painelBuscaBotao, BorderLayout.SOUTH);

        return painelSuperior;
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
                new JanelaImpressaoPedido(null, medicamentosSelecionados).setVisible(true);
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

    public void atualizarTabela() {
        try {
            medicamentos = MedicamentoDAO.listarTodos(this.conn);
            inicializarLinhasSelecionadas();
            carregarDados();
            atualizarEstadoBotaoPedido();
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
        String[] colunas = { "Selecionar", "Nome", "Categoria", "Medida", "Validade", "Preço Unitário", "Quantidade" };

        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public Class<?> getColumnClass(int column) {
                return column == 0 ? Boolean.class : Object.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0;
            }
        };

        carregarDados();

        tabela = new JTable(modeloTabela) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (linhasSelecionadas.size() > row && linhasSelecionadas.get(row)) {
                    c.setBackground(new Color(173, 216, 230));
                } else {
                    c.setBackground(Color.WHITE);
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

        tabela.getColumnModel().getColumn(0).setPreferredWidth(30);
        tabela.getColumnModel().getColumn(1).setPreferredWidth(170);
        tabela.getColumnModel().getColumn(2).setPreferredWidth(100);
        tabela.getColumnModel().getColumn(3).setPreferredWidth(80);
        tabela.getColumnModel().getColumn(4).setPreferredWidth(80);
        tabela.getColumnModel().getColumn(5).setPreferredWidth(80);
        tabela.getColumnModel().getColumn(6).setPreferredWidth(80);

        tabela.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = tabela.rowAtPoint(evt.getPoint());
                int column = tabela.columnAtPoint(evt.getPoint());
                if (column == 0) {
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

    private void filtrarMedicamentos(String filtro) {
        List<Boolean> linhasSelecionadasAnterior = new ArrayList<>(linhasSelecionadas);
        List<Medicamento> medicamentosFiltrados;

        if (filtro.isEmpty() || filtro.equals("Buscar")) {
            try {
                medicamentosFiltrados = MedicamentoDAO.listarTodos(this.conn);
            } catch (SQLException e) {
                e.printStackTrace();
                return;
            }
        } else {
            try {
                medicamentosFiltrados = MedicamentoDAO.listarTodos(this.conn).stream()
                        .filter(medicamento -> medicamento.getNome().toLowerCase().contains(filtro.toLowerCase()))
                        .collect(Collectors.toList());
            } catch (SQLException e) {
                e.printStackTrace();
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

    private void carregarDados() {
        modeloTabela.setRowCount(0);

        if (medicamentos == null || medicamentos.isEmpty()) {
            modeloTabela.addRow(new Object[] { false, "Medicamento não encontrado.", "", "", "", "", "" });
        } else {
            NumberFormat numberFormat = NumberFormat.getInstance();
            for (int i = 0; i < medicamentos.size(); i++) {
                Medicamento medicamento = medicamentos.get(i);
                Object[] rowData = new Object[7];
                rowData[0] = linhasSelecionadas.get(i);
                rowData[1] = medicamento.getNome();
                rowData[2] = medicamento.getCategoria().getNome();
                rowData[3] = medicamento.getDosagem();
                rowData[4] = formatarData(medicamento.getDataValidade());
                rowData[5] = numberFormat.format(medicamento.getValorUnit());
                rowData[6] = medicamento.getQnt();

                modeloTabela.addRow(rowData);
            }
        }
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

    private void atualizarEstadoBotaoPedido() {
        boolean algumSelecionado = linhasSelecionadas.contains(true);
        realizarPedidoButton.setEnabled(algumSelecionado);
    }
}

class JanelaImpressaoPedido extends JDialog {
    private List<Medicamento> medicamentos;
    private String dataHoraCriacao;
    private List<JTextField> quantidadeFields;

    public JanelaImpressaoPedido(JFrame parent, List<Medicamento> medicamentos) {
        super(parent, "Lista de Pedidos", true);
        this.medicamentos = medicamentos;
        this.dataHoraCriacao = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());
        this.quantidadeFields = new ArrayList<>();


        int width = 800;
        int height = 600;
        setSize(width, height);
        setPreferredSize(new Dimension(width, height));
        setMinimumSize(new Dimension(width, height));
        setMaximumSize(new Dimension(width, height));

        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Lista de Pedidos de Medicamentos", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(new Color(24, 39, 55));
        topPanel.add(titleLabel, BorderLayout.NORTH);

        JLabel dataLabel = new JLabel("Lista criada em: " + dataHoraCriacao);
        dataLabel.setFont(new Font("Arial", Font.ITALIC, 10));
        dataLabel.setForeground(Color.DARK_GRAY);
        topPanel.add(dataLabel, BorderLayout.WEST);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        JPanel pedidoPanel = criarPedidoPanel();
        JScrollPane scrollPane = new JScrollPane(pedidoPanel);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JButton realizarPedidoButton = new JButton("Finalizar Pedido");
        realizarPedidoButton.setFont(new Font("Arial", Font.BOLD, 14));
        realizarPedidoButton.setBackground(new Color(24, 39, 55));
        realizarPedidoButton.setForeground(Color.WHITE);
        realizarPedidoButton.addActionListener(e -> imprimirLista());

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.add(realizarPedidoButton);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
        pack();
    }

    private JPanel criarPedidoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 2, 2, 2); 
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0; 

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(criarHeaderPanel(), gbc);

        for (int i = 0; i < medicamentos.size(); i++) {
            Medicamento medicamento = medicamentos.get(i);
            gbc.gridy = i + 1;
            panel.add(criarMedicamentoPanel(medicamento), gbc);
        }

        gbc.gridy = medicamentos.size() + 1;
        gbc.weighty = 1.0; 
        panel.add(new JPanel(), gbc); 

        return panel;
    }

    private JPanel criarHeaderPanel() {
        JPanel headerPanel = new JPanel(new GridLayout(1, 5));
        headerPanel.setBackground(Color.LIGHT_GRAY);
        headerPanel.setPreferredSize(new Dimension(750, 25));
        headerPanel.setMaximumSize(new Dimension(750, 25));

        headerPanel.add(criarLabel("Nome", Font.BOLD, 14));
        headerPanel.add(criarLabel("Categoria", Font.BOLD, 14));
        headerPanel.add(criarLabel("Medida", Font.BOLD, 14));
        headerPanel.add(criarLabel("Preço", Font.BOLD, 14));
        headerPanel.add(criarLabel("Qtd. Solicitada", Font.BOLD, 14));

        return headerPanel;
    }

    private JPanel criarMedicamentoPanel(Medicamento medicamento) {
        JPanel medicamentoPanel = new JPanel(new GridLayout(1, 5));
        medicamentoPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));
        medicamentoPanel.setPreferredSize(new Dimension(750, 25));
        medicamentoPanel.setMaximumSize(new Dimension(750, 25));

        medicamentoPanel.add(criarLabel(medicamento.getNome(), Font.PLAIN, 12));
        medicamentoPanel.add(criarLabel(medicamento.getCategoria().getNome(), Font.PLAIN, 12));
        medicamentoPanel.add(criarLabel(medicamento.getDosagem(), Font.PLAIN, 12));
        medicamentoPanel.add(criarLabel(String.format("R$ %.2f", medicamento.getValorUnit()), Font.PLAIN, 12));

        JTextField quantidadeField = new JTextField(3);
        quantidadeField.setPreferredSize(new Dimension(20, 20));
        quantidadeField.setMaximumSize(new Dimension(20, 20));
        quantidadeFields.add(quantidadeField);
        medicamentoPanel.add(quantidadeField);

        return medicamentoPanel;
    }

    private JLabel criarLabel(String texto, int estiloFonte, int tamanhoFonte) {
        JLabel label = new JLabel(texto, SwingConstants.CENTER);
        label.setFont(new Font("Arial", estiloFonte, tamanhoFonte));
        label.setVerticalAlignment(SwingConstants.CENTER);
        return label;
    }

    private void imprimirLista() {
        List<String> informacoesParaImprimir = new ArrayList<>();
        for (int i = 0; i < medicamentos.size(); i++) {
            String text = quantidadeFields.get(i).getText();
            try {
                int quantidade = Integer.parseInt(text);
                Medicamento medicamento = medicamentos.get(i);
                informacoesParaImprimir.add(String.format("Medicamento: %s, Categoria: %s, Dosagem: %s, Preço: R$ %.2f, Quantidade: %d",
                        medicamento.getNome(), medicamento.getCategoria().getNome(), medicamento.getDosagem(), medicamento.getValorUnit(), quantidade));
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Digite uma quantidade válida para todos os medicamentos.", "Erro",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintable((graphics, pageFormat, pageIndex) -> {
            if (pageIndex > 0) {
                return Printable.NO_SUCH_PAGE;
            }

            Graphics2D g2d = (Graphics2D) graphics;
            g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

            int y = 20;
            for (String info : informacoesParaImprimir) {
                g2d.drawString(info, 10, y);
                y += 15;
            }

            return Printable.PAGE_EXISTS;
        });

        boolean doPrint = job.printDialog();
        if (doPrint) {
            try {
                job.print();
            } catch (PrinterException e) {
                JOptionPane.showMessageDialog(null, "Erro ao imprimir: " + e.getMessage(), "Erro",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}