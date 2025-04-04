package views.Produtos;

import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import dao.Produto.ProdutoDAO;
import models.Produto.Produto;

public class ListaDeProdutos extends JPanel {

    private JTable tabela;
    private DefaultTableModel modeloTabela;
    private List<Produto> produtos;
    private List<Object[]> produtosFiltrados;
    private JScrollPane tabelaScrollPane;
    private Connection conn;

    public ListaDeProdutos(Connection conn) {
        this.conn = conn;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(new Color(0, 0, 0, 0));

        produtosFiltrados = new ArrayList<>();
        try {
            produtos = ProdutoDAO.listarTodos(conn);
            atualizarProdutosFiltrados(produtos);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao carregar produtos.", "Erro", JOptionPane.ERROR_MESSAGE);
        }

        JPanel painelSuperior = criarTituloEBusca();
        add(painelSuperior);

        tabelaScrollPane = criarTabela();
        add(tabelaScrollPane);
    }

    public void atualizarTabela() {
        try {
            produtos = ProdutoDAO.listarTodos(conn);
            atualizarProdutosFiltrados(produtos);
            carregarDados();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao carregar produtos.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String formatarData(LocalDate data) {
        if (data != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yyyy");
            return data.format(formatter);
        }
        return "";
    }

    private String formatarEstoque(int estoque) {
        return String.format("%,d", estoque);
    }

    private void atualizarProdutosFiltrados(List<Produto> produtos) {
        produtosFiltrados.clear();

        for (Produto produto : produtos) {
            Object[] dadosProduto = new Object[9];
            dadosProduto[0] = produto.getNome();
            dadosProduto[1] = produto.getCategoria().getNome();
            dadosProduto[2] = formatarData(produto.getDataValidade());
            dadosProduto[3] = produto.getValor();
            dadosProduto[4] = formatarEstoque(produto.getQntEstoque());
            dadosProduto[5] = produto.getEmbalagem();
            dadosProduto[6] = produto.getQntEmbalagem();
            dadosProduto[7] = produto.getQntMedida();
            dadosProduto[8] = "";

            produtosFiltrados.add(dadosProduto);
        }
    }

    private JPanel criarTituloEBusca() {
        JPanel painelSuperior = new JPanel(new BorderLayout());

        JPanel painelTitulo = new JPanel();
        painelTitulo.setLayout(new BoxLayout(painelTitulo, BoxLayout.Y_AXIS));
        painelTitulo.setBorder(BorderFactory.createEmptyBorder(50, 0, 35, 0));

        JLabel titulo = new JLabel("LISTA DE PRODUTOS");
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        painelTitulo.add(titulo);

        JPanel painelBuscarTitulo = new JPanel();
        painelBuscarTitulo.setLayout(new BoxLayout(painelBuscarTitulo, BoxLayout.X_AXIS));
        painelBuscarTitulo.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 50));
        JLabel buscarTitulo = new JLabel("Buscar Produto");
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
                filtrarProdutos(campoBusca.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filtrarProdutos(campoBusca.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filtrarProdutos(campoBusca.getText());
            }
        });

        painelBuscaBotao.add(campoBusca);
        painelBuscaBotao.add(Box.createHorizontalGlue());

        JButton cadastrarButton = new JButton("Cadastrar Produto");
        cadastrarButton.setFont(new Font("Arial", Font.BOLD, 15));
        cadastrarButton.setBackground(new Color(24, 39, 55));
        cadastrarButton.setForeground(Color.WHITE);
        cadastrarButton.setFocusPainted(false);
        cadastrarButton.setPreferredSize(new Dimension(180, 30));
        painelBuscaBotao.add(cadastrarButton);

        cadastrarButton.addActionListener(e -> {
            JDialog cadastroDialog = new JDialog();
            cadastroDialog.setTitle("Cadastrar Produto");
            cadastroDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            cadastroDialog.setSize(1200, 650);
            cadastroDialog.setLocationRelativeTo(this);
            cadastroDialog.setModal(true);

            CadastrarProduto cadastrarProduto = new CadastrarProduto();

            cadastroDialog.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                    atualizarTabela();
                }
            });

            cadastroDialog.add(cadastrarProduto);
            cadastroDialog.setVisible(true);
        });

        painelSuperior.add(painelTitulo, BorderLayout.NORTH);
        painelSuperior.add(painelBuscarTitulo, BorderLayout.CENTER);
        painelSuperior.add(painelBuscaBotao, BorderLayout.SOUTH);

        return painelSuperior;
    }

    private JScrollPane criarTabela() {
        String[] colunas = { "Nome", "Categoria", "Validade", "Valor Unitário", "Estoque", "Embalagem",
                "Qnt. Embalagem", "Medida", "Ações" };

        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 8;
            }
        };

        carregarDados();

        tabela = new JTable(modeloTabela);
        tabela.setFillsViewportHeight(true);
        tabela.setRowHeight(35);
        tabela.setFont(new Font("Arial", Font.PLAIN, 12));
        tabela.getTableHeader().setFont(new Font("Arial", Font.BOLD, 15));
        tabela.getTableHeader().setReorderingAllowed(false);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        for (int i = 0; i < colunas.length - 1; i++) {
            tabela.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        tabela.getColumnModel().getColumn(8).setCellRenderer(new RenderizadorBotoes());
        tabela.getColumnModel().getColumn(8).setCellEditor(new EditorBotoes(new JTextField()));

        tabela.getColumnModel().getColumn(0).setPreferredWidth(180);
        tabela.getColumnModel().getColumn(1).setPreferredWidth(100);
        tabela.getColumnModel().getColumn(2).setPreferredWidth(30);
        tabela.getColumnModel().getColumn(3).setPreferredWidth(40);
        tabela.getColumnModel().getColumn(4).setPreferredWidth(30);
        tabela.getColumnModel().getColumn(5).setPreferredWidth(40);
        tabela.getColumnModel().getColumn(6).setPreferredWidth(55);
        tabela.getColumnModel().getColumn(7).setPreferredWidth(10);
        tabela.getColumnModel().getColumn(8).setPreferredWidth(130);

        tabela.setCellSelectionEnabled(false);
        tabela.setRowSelectionAllowed(false);
        tabela.setColumnSelectionAllowed(false);

        JScrollPane scrollPane = new JScrollPane(tabela);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 30, 57, 30));

        return scrollPane;
    }

    private void filtrarProdutos(String filtro) {
        List<Produto> produtosFiltradosTemp = new ArrayList<>();

        if (filtro.isEmpty() || filtro.equals("Buscar")) {
            produtosFiltradosTemp.addAll(produtos);
        } else {
            produtosFiltradosTemp = produtos.stream()
                    .filter(produto -> produto.getNome().toLowerCase().contains(filtro.toLowerCase()))
                    .collect(Collectors.toList());
        }

        produtosFiltrados = produtosFiltradosTemp.stream()
                .map(produto -> new Object[] {
                        produto.getNome(),
                        produto.getCategoria().getNome(),
                        formatarData(produto.getDataValidade()),
                        produto.getValor(),
                        formatarEstoque(produto.getQntEstoque()),
                        produto.getEmbalagem(),
                        produto.getQntEmbalagem(),
                        produto.getQntMedida()
                })
                .collect(Collectors.toList());

        carregarDados();
    }

    private void carregarDados() {
        modeloTabela.setRowCount(0);

        if (produtosFiltrados.isEmpty()) {
            modeloTabela.addRow(new Object[] { "Produto não encontrado.", "", "", "", "", "", "", "", "" });
        } else {
            for (Object[] produto : produtosFiltrados) {
                modeloTabela.addRow(produto);
            }
        }
    }

    private class RenderizadorBotoes extends JPanel implements TableCellRenderer {
        private final JButton botaoEditar;
        private final JButton botaoExcluir;

        public RenderizadorBotoes() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
            botaoEditar = criarBotao("EDITAR", new Color(24, 39, 55), Color.WHITE);
            botaoExcluir = criarBotao("EXCLUIR", Color.RED, Color.WHITE);
            add(botaoEditar);
            add(botaoExcluir);
        }

        private JButton criarBotao(String texto, Color corFundo, Color corTexto) {
            JButton botao = new JButton(texto);
            botao.setBackground(corFundo);
            botao.setForeground(corTexto);
            botao.setBorderPainted(false);
            botao.setFocusPainted(false);
            botao.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            return botao;
        }

        @Override
        public Component getTableCellRendererComponent(JTable tabela, Object valor, boolean isSelected,
                boolean hasFocus, int linha, int coluna) {
            setBackground(Color.WHITE);
            if (produtosFiltrados.isEmpty()) {
                botaoEditar.setVisible(false);
                botaoExcluir.setVisible(false);
            } else {
                botaoEditar.setVisible(true);
                botaoExcluir.setVisible(true);
            }
            return this;
        }
    }

    private class EditorBotoes extends DefaultCellEditor {
        private final JButton botaoEditar;
        private final JButton botaoExcluir;
        private int indiceLinha;

        public EditorBotoes(JTextField campoTexto) {
            super(campoTexto);
            botaoEditar = criarBotao("EDITAR", new Color(24, 39, 55), Color.WHITE);
            botaoExcluir = criarBotao("EXCLUIR", Color.RED, Color.WHITE);

            configurarAcoes();
        }

        private JButton criarBotao(String texto, Color corFundo, Color corTexto) {
            JButton botao = new JButton(texto);
            botao.setBackground(corFundo);
            botao.setForeground(corTexto);
            botao.setBorderPainted(false);
            botao.setFocusPainted(false);
            botao.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            return botao;
        }

        private void configurarAcoes() {
            botaoEditar.addActionListener(e -> {
                fireEditingStopped();

                if (produtosFiltrados.isEmpty()) {
                    return;
                }

                indiceLinha = tabela.getSelectedRow();
                if (indiceLinha >= 0) {
                    Produto produtoSelecionado = produtos.stream()
                            .filter(produto -> produto.getNome().equals(tabela.getValueAt(indiceLinha, 0)))
                            .findFirst()
                            .orElse(null);

                    if (produtoSelecionado != null) {
                        int produtoId = produtoSelecionado.getId();

                        JDialog dialogoEditar = new JDialog();
                        dialogoEditar.setTitle("Editar Produto");
                        dialogoEditar.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                        dialogoEditar.setSize(1200, 650);
                        dialogoEditar.setLocationRelativeTo(null);
                        dialogoEditar.setModal(true);

                        Point localizacao = dialogoEditar.getLocation();
                        localizacao.y = 150;
                        dialogoEditar.setLocation(localizacao);

                        EditarProduto painelEditar = new EditarProduto(produtoId);
                        dialogoEditar.add(painelEditar);

                        dialogoEditar.addWindowListener(new java.awt.event.WindowAdapter() {
                            @Override
                            public void windowClosed(java.awt.event.WindowEvent evento) {
                                atualizarTabela();
                            }
                        });

                        dialogoEditar.setVisible(true);
                    }
                }
            });

            botaoExcluir.addActionListener(e -> {
                fireEditingStopped();

                if (produtosFiltrados.isEmpty()) {
                    return;
                }

                indiceLinha = tabela.getSelectedRow();
                if (indiceLinha >= 0) {
                    Produto produtoSelecionado = produtos.stream()
                            .filter(produto -> produto.getNome().equals(tabela.getValueAt(indiceLinha, 0)))
                            .findFirst()
                            .orElse(null);

                    if (produtoSelecionado != null) {
                        int produto_id = produtoSelecionado.getId();
                        ExcluirProduto.excluirProduto(produto_id);
                        atualizarTabela();
                    }
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable tabela, Object valor, boolean isSelected, int linha,
                int coluna) {
            JPanel painel = new JPanel();
            painel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
            painel.setBackground(Color.WHITE);
            painel.add(botaoEditar);
            painel.add(botaoExcluir);
            return painel;
        }

        @Override
        public Object getCellEditorValue() {
            return "";
        }
    }
}