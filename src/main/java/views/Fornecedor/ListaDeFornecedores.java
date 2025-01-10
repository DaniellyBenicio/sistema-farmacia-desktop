package views.Fornecedor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Point;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import dao.Fornecedor.FornecedorDAO;
import models.Fornecedor.Fornecedor;
import views.BarrasSuperiores.PainelSuperior;

public class ListaDeFornecedores extends JPanel {

    private JTable tabela;
    private DefaultTableModel modeloTabela;
    private int paginaAtual = 0;
    private final int itensPorPagina = 10;
    private List<Fornecedor> fornecedores;
    private List<Object[]> fornecedoresFiltrados;
    private JPanel painelPaginacao;
    private JScrollPane tabelaScrollPane;
    private Connection conn;

    public ListaDeFornecedores(Connection conn) {
        this.conn = conn;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(new Color(0, 0, 0, 0));

        fornecedoresFiltrados = new ArrayList<>();
        fornecedoresIds = new ArrayList<>();

        try {
            fornecedores = FornecedorDAO.listarFornecedores(conn);
            atualizarFornecedoresFiltrados(fornecedores);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao carregar fornecedores.", "Erro", JOptionPane.ERROR_MESSAGE);
        }

        JPanel painelSuperior = criarTituloEBusca();
        add(painelSuperior);

        tabelaScrollPane = criarTabela();
        add(tabelaScrollPane);

        painelPaginacao = criarPaginacao();
        add(painelPaginacao);
    }

    public void atualizarTabela() {
        try {
            fornecedores = FornecedorDAO.listarFornecedores(conn);
            atualizarFornecedoresFiltrados(fornecedores);
            carregarDados();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao carregar fornecedores.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private List<Integer> fornecedoresIds;

    private void atualizarFornecedoresFiltrados(List<Fornecedor> fornecedores) {
        fornecedoresFiltrados.clear();
        fornecedoresIds.clear();

        for (Fornecedor fornecedor : fornecedores) {
            Object[] dadosFornecedor = new Object[7];
            dadosFornecedor[0] = fornecedor.getNome();
            dadosFornecedor[1] = formatarCNPJ(fornecedor.getCnpj());
            dadosFornecedor[2] = fornecedor.getEmail();
            dadosFornecedor[3] = formatarTelefone(fornecedor.getTelefone());
            String representante = fornecedor.getNomeRepresentante();
            dadosFornecedor[4] = (representante != null && !representante.isEmpty()) ? representante
                    : "Sem Representante";
            dadosFornecedor[5] = fornecedor.getTelefoneRepresentante() != null
                    ? formatarTelefone(fornecedor.getTelefoneRepresentante())
                    : "Sem Telefone";
            dadosFornecedor[6] = "";

            fornecedoresFiltrados.add(dadosFornecedor);
            fornecedoresIds.add(fornecedor.getId());
        }
    }

    private String formatarCNPJ(String cnpj) {
        String numero = cnpj.replaceAll("\\D", "");
        if (numero.length() == 14) {
            return String.format("%s.%s.%s/%s-%s",
                    numero.substring(0, 2),
                    numero.substring(2, 5),
                    numero.substring(5, 8),
                    numero.substring(8, 12),
                    numero.substring(12, 14));
        }
        return cnpj;
    }

    private String formatarTelefone(String telefone) {
        String numero = telefone.replaceAll("\\D", "");
        if (numero.length() == 11) {
            return String.format("(%s) %s-%s",
                    numero.substring(0, 2),
                    numero.substring(2, 7),
                    numero.substring(7));
        } else if (numero.length() == 10) {
            return String.format("(%s) %s-%s",
                    numero.substring(0, 2),
                    numero.substring(2, 6),
                    numero.substring(6));
        }
        return telefone;
    }

    private JPanel criarTituloEBusca() {
        JPanel painelSuperior = new JPanel(new BorderLayout());

        JPanel painelTitulo = new JPanel();
        painelTitulo.setLayout(new BoxLayout(painelTitulo, BoxLayout.Y_AXIS));
        painelTitulo.setBorder(BorderFactory.createEmptyBorder(50, 0, 35, 0));

        JLabel titulo = new JLabel("LISTA DE FORNECEDORES");
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        painelTitulo.add(titulo);

        JPanel painelBuscarTitulo = new JPanel();
        painelBuscarTitulo.setLayout(new BoxLayout(painelBuscarTitulo, BoxLayout.X_AXIS));
        painelBuscarTitulo.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 50));
        JLabel buscarTitulo = new JLabel("Buscar Fornecedor");
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
                filtrarFornecedores(campoBusca.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filtrarFornecedores(campoBusca.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filtrarFornecedores(campoBusca.getText());
            }
        });

        painelBuscaBotao.add(campoBusca);
        painelBuscaBotao.add(Box.createHorizontalGlue());

        JButton cadastrarButton = new JButton("Cadastrar Fornecedor");
        cadastrarButton.setFont(new Font("Arial", Font.BOLD, 15));
        cadastrarButton.setBackground(new Color(24, 39, 55));
        cadastrarButton.setForeground(Color.WHITE);
        cadastrarButton.setFocusPainted(false);
        cadastrarButton.setPreferredSize(new Dimension(200, 30));
        painelBuscaBotao.add(cadastrarButton);

        cadastrarButton.addActionListener(e -> {
            JDialog cadastroDialog = new JDialog();
            cadastroDialog.setTitle("Cadastrar Fornecedor");
            cadastroDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            cadastroDialog.setSize(1200, 650);
            cadastroDialog.setLocationRelativeTo(this);
            cadastroDialog.setModal(true);

            CadastrarFornecedor cadastroPanel = new CadastrarFornecedor();

            cadastroDialog.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                    atualizarTabela();
                }
            });

            cadastroDialog.add(cadastroPanel);
            cadastroDialog.setVisible(true);
        });

        painelSuperior.add(painelTitulo, BorderLayout.NORTH);
        painelSuperior.add(painelBuscarTitulo, BorderLayout.CENTER);
        painelSuperior.add(painelBuscaBotao, BorderLayout.SOUTH);

        return painelSuperior;
    }

    private JScrollPane criarTabela() {
        String[] colunas = { "Nome", "CNPJ", "E-mail", "Telefone", "Representante", "Telefone Rep.", "Ações" };

        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6;
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

        tabela.getColumnModel().getColumn(6).setCellRenderer(new RenderizadorBotoes());
        tabela.getColumnModel().getColumn(6).setCellEditor(new EditorBotoes(new JTextField()));

        tabela.getColumnModel().getColumn(0).setPreferredWidth(300);
        tabela.getColumnModel().getColumn(1).setPreferredWidth(80);
        tabela.getColumnModel().getColumn(2).setPreferredWidth(200);
        tabela.getColumnModel().getColumn(3).setPreferredWidth(70);
        tabela.getColumnModel().getColumn(4).setPreferredWidth(200);
        tabela.getColumnModel().getColumn(5).setPreferredWidth(70);
        tabela.getColumnModel().getColumn(6).setPreferredWidth(160);

        tabela.setCellSelectionEnabled(false);
        tabela.setRowSelectionAllowed(false);
        tabela.setColumnSelectionAllowed(false);

        for (int i = 0; i < tabela.getColumnModel().getColumnCount(); i++) {
            tabela.getColumnModel().getColumn(i).setResizable(false);
        }

        JScrollPane scrollPane = new JScrollPane(tabela);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 30, 57, 30));

        return scrollPane;
    }

    private void filtrarFornecedores(String filtro) {
        if (filtro.isEmpty() || filtro.equals("Buscar")) {
            fornecedoresFiltrados = fornecedores.stream()
                    .map(fornecedor -> new Object[] {
                            fornecedor.getNome(),
                            formatarCNPJ(fornecedor.getCnpj()),
                            fornecedor.getEmail(),
                            formatarTelefone(fornecedor.getTelefone()),
                            fornecedor.getNomeRepresentante() != null && !fornecedor.getNomeRepresentante().isEmpty()
                                    ? fornecedor.getNomeRepresentante()
                                    : "Sem Representante",
                            fornecedor.getTelefoneRepresentante() != null
                                    ? formatarTelefone(fornecedor.getTelefoneRepresentante())
                                    : "Sem Telefone",
                    })
                    .collect(Collectors.toList());
        } else {
            fornecedoresFiltrados = fornecedores.stream()
                    .filter(fornecedor -> fornecedor.getNome().toLowerCase().startsWith(filtro.toLowerCase()))
                    .map(fornecedor -> new Object[] {
                            fornecedor.getNome(),
                            formatarCNPJ(fornecedor.getCnpj()),
                            fornecedor.getEmail(),
                            formatarTelefone(fornecedor.getTelefone()),
                            fornecedor.getNomeRepresentante() != null && !fornecedor.getNomeRepresentante().isEmpty()
                                    ? fornecedor.getNomeRepresentante()
                                    : "Sem Representante",
                            fornecedor.getTelefoneRepresentante() != null
                                    ? formatarTelefone(fornecedor.getTelefoneRepresentante())
                                    : "Sem Telefone",
                    })
                    .collect(Collectors.toList());
        }
        carregarDados();
    }

    private void carregarDados() {
        modeloTabela.setRowCount(0);

        if (fornecedoresFiltrados.isEmpty()) {
            modeloTabela.addRow(new Object[] { "Fornecedor não encontrado.", "", "", "", "", "", "" });
        } else {
            int inicio = paginaAtual * itensPorPagina;
            int fim = Math.min(inicio + itensPorPagina, fornecedoresFiltrados.size());

            for (int i = inicio; i < fim; i++) {
                modeloTabela.addRow(fornecedoresFiltrados.get(i));
            }
        }
    }

    private JPanel criarPaginacao() {
        JPanel painelPaginacao = new JPanel();
        painelPaginacao.setLayout(new FlowLayout(FlowLayout.CENTER));

        JButton btnAnterior = new JButton("Anterior");
        btnAnterior.setEnabled(paginaAtual > 0);
        btnAnterior.addActionListener(e -> {
            if (paginaAtual > 0) {
                paginaAtual--;
                carregarDados();
                atualizarPaginacao();
            }
        });

        JButton btnProximo = new JButton("Próximo");
        btnProximo.setEnabled((paginaAtual + 1) * itensPorPagina < fornecedoresFiltrados.size());
        btnProximo.addActionListener(e -> {
            if ((paginaAtual + 1) * itensPorPagina < fornecedoresFiltrados.size()) {
                paginaAtual++;
                carregarDados();
                atualizarPaginacao();
            }
        });

        painelPaginacao.add(btnAnterior);
        painelPaginacao.add(Box.createHorizontalGlue());
        painelPaginacao.add(btnProximo);

        return painelPaginacao;
    }

    private void atualizarPaginacao() {
        if (paginaAtual < 0) {
            paginaAtual = 0;
        }
        Component[] componentes = painelPaginacao.getComponents();
        for (Component componente : componentes) {
            if (componente instanceof JButton) {
                JButton btn = (JButton) componente;
                if (btn.getText().equals("Anterior")) {
                    btn.setEnabled(paginaAtual > 0);
                } else if (btn.getText().equals("Próximo")) {
                    btn.setEnabled((paginaAtual + 1) * itensPorPagina < fornecedoresFiltrados.size());
                }
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
        public Component getTableCellRendererComponent(JTable tabela, Object valor, boolean isSelected, boolean hasFocus,
                                                        int linha, int coluna) {
            setBackground(Color.WHITE);
    
            if ("Gerente".equalsIgnoreCase(PainelSuperior.getCargoFuncionarioAtual())) {
                botaoExcluir.setVisible(false);
            } else {
                botaoExcluir.setVisible(true);
            }
    
            if (fornecedoresFiltrados.isEmpty()) {
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
                if (fornecedoresFiltrados.isEmpty()) {
                    return;
                }
    
                indiceLinha = tabela.getSelectedRow();
                if (indiceLinha >= 0) {
                    int fornecedorId = fornecedoresIds.get(indiceLinha);
    
                    JDialog dialogoEditar = new JDialog();
                    dialogoEditar.setTitle("Editar Fornecedor");
                    dialogoEditar.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                    dialogoEditar.setSize(1200, 650);
                    dialogoEditar.setLocationRelativeTo(null);
                    dialogoEditar.setModal(true);
    
                    Point localizacao = dialogoEditar.getLocation();
                    localizacao.y = 150;
                    dialogoEditar.setLocation(localizacao);
    
                    EditarFornecedor painelEditar = new EditarFornecedor(fornecedorId);
                    dialogoEditar.add(painelEditar);
    
                    dialogoEditar.addWindowListener(new java.awt.event.WindowAdapter() {
                        @Override
                        public void windowClosed(java.awt.event.WindowEvent evento) {
                            atualizarTabela();
                        }
                    });
                    dialogoEditar.setVisible(true);
                }
            });
    
            botaoExcluir.addActionListener(e -> {
                fireEditingStopped();
                if (fornecedoresFiltrados.isEmpty()) {
                    return;
                }
    
                indiceLinha = tabela.getSelectedRow();
                if (indiceLinha >= 0) {
                    int fornecedorId = fornecedoresIds.get(indiceLinha);
                    ExcluirFornecedor.excluirFornecedor(fornecedorId);
                    atualizarTabela();
                }
            });
        }
    
        @Override
        public Component getTableCellEditorComponent(JTable tabela, Object valor, boolean isSelected, int linha, int coluna) {
            JPanel painel = new JPanel();
            painel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
            painel.setBackground(Color.WHITE);
    
            if (fornecedoresFiltrados.isEmpty()) {
                botaoEditar.setVisible(false);
                botaoExcluir.setVisible(false);
            } else {
                botaoEditar.setVisible(true);
                botaoExcluir.setVisible(true);
            }
    
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