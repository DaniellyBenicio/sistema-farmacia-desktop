package views.Clientes;

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

import models.Cliente.Cliente;
import dao.Cliente.ClienteDAO;

public class ListaDeClientes extends JPanel {

    private JTable tabela;
    private DefaultTableModel modeloTabela;
    private List<Cliente> clientes;
    private List<Object[]> clientesFiltrados;
    private JScrollPane tabelaScrollPane;
    private Connection conn;

    public ListaDeClientes(Connection conn) {
        this.conn = conn;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(new Color(0, 0, 0, 0));

        clientesFiltrados = new ArrayList<>();
        clientesIds = new ArrayList<>();

        try {
            clientes = ClienteDAO.listarClientes(conn);
            atualizarClientesFiltrados(clientes);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao carregar clientes.", "Erro", JOptionPane.ERROR_MESSAGE);
        }

        JPanel painelSuperior = criarTituloEBusca();
        add(painelSuperior);

        tabelaScrollPane = criarTabela();
        add(tabelaScrollPane);
    }

    public void atualizarTabela() {
        try {
            clientes = ClienteDAO.listarClientesSemCpf(conn);
            atualizarClientesFiltrados(clientes);
            carregarDados();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao carregar clientes.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private List<Integer> clientesIds;

    private void atualizarClientesFiltrados(List<Cliente> clientes) {
        clientesFiltrados.clear();
        clientesIds.clear();

        for (Cliente cliente : clientes) {
            Object[] dadosCliente = new Object[7];
            dadosCliente[0] = cliente.getNome();
            dadosCliente[1] = formatarTelefone(cliente.getTelefone());
            dadosCliente[2] = cliente.getRua() + "" + cliente.getNumCasa();
            dadosCliente[3] = cliente.getBairro();
            dadosCliente[4] = cliente.getCidade();
            dadosCliente[5] = cliente.getEstado();
            dadosCliente[6] = "";

            clientesFiltrados.add(dadosCliente);
            clientesIds.add(cliente.getId());
        }
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

        JLabel titulo = new JLabel("LISTA DE CLIENTES");
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        painelTitulo.add(titulo);

        JPanel painelBuscarTitulo = new JPanel();
        painelBuscarTitulo.setLayout(new BoxLayout(painelBuscarTitulo, BoxLayout.X_AXIS));
        painelBuscarTitulo.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 50));
        JLabel buscarTitulo = new JLabel("Buscar Cliente");
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
                filtrarClientes(campoBusca.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filtrarClientes(campoBusca.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filtrarClientes(campoBusca.getText());
            }
        });

        painelBuscaBotao.add(campoBusca);
        painelBuscaBotao.add(Box.createHorizontalGlue());

        JButton cadastrarButton = new JButton("Cadastrar Cliente");
        cadastrarButton.setFont(new Font("Arial", Font.BOLD, 15));
        cadastrarButton.setBackground(new Color(24, 39, 55));
        cadastrarButton.setForeground(Color.WHITE);
        cadastrarButton.setFocusPainted(false);
        cadastrarButton.setPreferredSize(new Dimension(200, 30));
        painelBuscaBotao.add(cadastrarButton);

        cadastrarButton.addActionListener(e -> {
            JDialog cadastroDialog = new JDialog();
            cadastroDialog.setTitle("Cadastrar Cliente");
            cadastroDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            cadastroDialog.setSize(1200, 650);
            cadastroDialog.setLocationRelativeTo(this);
            cadastroDialog.setModal(true);

            CadastrarCliente cadastrarCliente = new CadastrarCliente();

            cadastroDialog.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                    atualizarTabela();
                }
            });

            cadastroDialog.add(cadastrarCliente);
            cadastroDialog.setVisible(true);
        });

        painelSuperior.add(painelTitulo, BorderLayout.NORTH);
        painelSuperior.add(painelBuscarTitulo, BorderLayout.CENTER);
        painelSuperior.add(painelBuscaBotao, BorderLayout.SOUTH);

        return painelSuperior;
    }

    private JScrollPane criarTabela() {
        String[] colunas = { "Nome", "Telefone", "Rua", "Bairro", "Cidade", "Estado", "Ações" };

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
        tabela.getColumnModel().getColumn(1).setPreferredWidth(30);
        tabela.getColumnModel().getColumn(2).setPreferredWidth(200);
        tabela.getColumnModel().getColumn(3).setPreferredWidth(70);
        tabela.getColumnModel().getColumn(4).setPreferredWidth(150);
        tabela.getColumnModel().getColumn(5).setPreferredWidth(20);
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

    private void filtrarClientes(String filtro) {
        if (filtro.isEmpty() || filtro.equals("Buscar")) {
            clientesFiltrados = clientes.stream()
                    .map(cliente -> new Object[] {
                            cliente.getNome(),
                            formatarTelefone(cliente.getTelefone()),
                            cliente.getRua() + cliente.getNumCasa(),
                            cliente.getBairro(),
                            cliente.getCidade(),
                            cliente.getEstado(),
                    })
                    .collect(Collectors.toList());
        } else {
            clientesFiltrados = clientes.stream()
                    .filter(cliente -> cliente.getNome().toLowerCase().startsWith(filtro.toLowerCase()))
                    .map(cliente -> new Object[] {
                            cliente.getNome(),
                            formatarTelefone(cliente.getTelefone()),
                            cliente.getRua() + cliente.getNumCasa(),
                            cliente.getBairro(),
                            cliente.getCidade(),
                            cliente.getEstado(),
                    })
                    .collect(Collectors.toList());
        }
        carregarDados();
    }

    private void carregarDados() {
        modeloTabela.setRowCount(0);

        if (clientesFiltrados.isEmpty()) {
            modeloTabela.addRow(new Object[] { "Cliente não encontrado.", "", "", "", "", "", "" });
        } else {
            for (Object[] cliente : clientesFiltrados) {
                modeloTabela.addRow(cliente);
            }
        }
    }

    private class RenderizadorBotoes extends JPanel implements TableCellRenderer {
        private final JButton botaoEditar;
        private final JButton botaoExcluir;

        public RenderizadorBotoes() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));

            botaoEditar = criarBotao("DETALHES", new Color(24, 39, 55), Color.WHITE);
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
                boolean hasFocus,
                int linha, int coluna) {
            setBackground(Color.WHITE);

            if (clientesFiltrados.isEmpty()) {
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
            botaoEditar = criarBotao("DETALHES", new Color(24, 39, 55), Color.WHITE);
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
                if (clientesFiltrados.isEmpty()) {
                    return;
                }

                indiceLinha = tabela.getSelectedRow();
                if (indiceLinha >= 0) {
                    int clienteId = clientesIds.get(indiceLinha);

                    JDialog dialogoEditar = new JDialog();
                    dialogoEditar.setTitle("Editar Cliente");
                    dialogoEditar.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                    dialogoEditar.setSize(1200, 650);
                    dialogoEditar.setLocationRelativeTo(null);
                    dialogoEditar.setModal(true);

                    Point localizacao = dialogoEditar.getLocation();
                    localizacao.y = 150;
                    dialogoEditar.setLocation(localizacao);

                    // EditarCliente painelEditar = new EditarCliente(clienteId);
                    // dialogoEditar.add(painelEditar);

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
                if (clientesFiltrados.isEmpty()) {
                    return;
                }

                indiceLinha = tabela.getSelectedRow();
                if (indiceLinha >= 0) {
                    int clienteId = clientesIds.get(indiceLinha);
                    // ExcluirCliente.excluirCliente(clienteId);
                    atualizarTabela();
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable tabela, Object valor, boolean isSelected, int linha,
                int coluna) {
            JPanel painel = new JPanel();
            painel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
            painel.setBackground(Color.WHITE);

            if (clientesFiltrados.isEmpty()) {
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