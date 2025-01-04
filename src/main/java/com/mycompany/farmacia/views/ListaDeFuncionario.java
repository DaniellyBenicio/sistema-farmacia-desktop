package com.mycompany.farmacia;

import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import dao.FuncionarioDAO;
import models.Funcionario;

public class ListaDeFuncionario extends JPanel {

    private JTable tabela;
    private DefaultTableModel modeloTabela;
    private int paginaAtual = 0;
    private final int itensPorPagina = 10;
    private List<Funcionario> funcionarios;
    private List<Object[]> funcionariosFiltrados;
    private JPanel painelPaginacao;
    private JScrollPane tabelaScrollPane;
    private Connection conn;

    public ListaDeFuncionario(Connection conn) {
        this.conn = conn;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(new Color(0, 0, 0, 0));

        funcionariosFiltrados = new ArrayList<>();

        try {
            funcionarios = FuncionarioDAO.listarFuncionarios(conn);
            atualizarFuncionariosFiltrados(funcionarios);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao carregar funcionários: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }

        JPanel painelSuperior = criarTituloEBusca();
        add(painelSuperior);

        tabelaScrollPane = criarTabela();
        add(tabelaScrollPane);

        painelPaginacao = criarPaginacao();
        add(painelPaginacao);
    }

    private void atualizarFuncionariosFiltrados(List<Funcionario> funcionarios) {
        funcionariosFiltrados.clear();

        for (Funcionario funcionario : funcionarios) {
            Object[] dadosFuncionario = new Object[6];
            dadosFuncionario[0] = funcionario.getId();
            dadosFuncionario[1] = funcionario.getNome();
            dadosFuncionario[2] = formatarCPF(funcionario.getCpf());
            dadosFuncionario[3] = funcionario.getEmail();
            dadosFuncionario[4] = formatarTelefone(funcionario.getTelefone());
            dadosFuncionario[5] = ""; // Coluna para ações

            funcionariosFiltrados.add(dadosFuncionario);
        }
    }

    private String formatarCPF(String cpf) {
        String numero = cpf.replaceAll("\\D", "");
        if (numero.length() == 11) {
            return String.format("%s.%s.%s-%s",
                    numero.substring(0, 3),
                    numero.substring(3, 6),
                    numero.substring(6, 9),
                    numero.substring(9, 11));
        }
        return cpf;
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

        JLabel titulo = new JLabel("LISTA DE FUNCIONÁRIOS");
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        painelTitulo.add(titulo);

        JPanel painelBuscarTitulo = new JPanel();
        painelBuscarTitulo.setLayout(new BoxLayout(painelBuscarTitulo, BoxLayout.X_AXIS));
        painelBuscarTitulo.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 50));
        JLabel buscarTitulo = new JLabel("Buscar Funcionário");
        buscarTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        painelBuscarTitulo.add(buscarTitulo);

        JPanel painelBuscaBotao = new JPanel();
        painelBuscaBotao.setLayout(new BoxLayout(painelBuscaBotao, BoxLayout.X_AXIS));
        painelBuscaBotao.setBorder(BorderFactory.createEmptyBorder(0, 30, 30, 50));

        JTextField campoBusca = new JTextField();
        campoBusca.setFont(new Font("Arial", Font.PLAIN, 16));
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
                filtrarFuncionarios(campoBusca.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filtrarFuncionarios(campoBusca.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filtrarFuncionarios(campoBusca.getText());
            }
        });

        painelBuscaBotao.add(campoBusca);
        painelBuscaBotao.add(Box.createHorizontalGlue());

        JButton cadastrarButton = new JButton("Cadastrar Funcionário");
        cadastrarButton.setFont(new Font("Arial", Font.BOLD, 15));
        cadastrarButton.setBackground(new Color(24, 39, 55));
        cadastrarButton.setForeground(Color.WHITE);
        cadastrarButton.setFocusPainted(false);
        cadastrarButton.setPreferredSize(new Dimension(200, 30));
        painelBuscaBotao.add(cadastrarButton);

        cadastrarButton.addActionListener(e -> {
            JDialog cadastroDialog = new JDialog();
            cadastroDialog.setTitle("Cadastrar Funcionário");
            cadastroDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            cadastroDialog.setSize(800, 400);
            cadastroDialog.setLocationRelativeTo(this);
            cadastroDialog.setModal(true);

            // Adicione o painel de cadastro aqui, por exemplo:
            // cadastroDialog.add(new CadastrarFuncionario());

            cadastroDialog.setVisible(true);
        });

        painelSuperior.add(painelTitulo, BorderLayout.NORTH);
        painelSuperior.add(painelBuscarTitulo, BorderLayout.CENTER);
        painelSuperior.add(painelBuscaBotao, BorderLayout.SOUTH);

        return painelSuperior;
    }

    private JScrollPane criarTabela() {
        String[] colunas = {"Código", "Nome", "CPF", "E-mail", "Telefone", "Ações"};

        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5;
            }
        };

        carregarDados();

        tabela = new JTable(modeloTabela);
        tabela.setFillsViewportHeight(true);
        tabela.setRowHeight(35);
        tabela.setFont(new Font("Arial", Font.PLAIN, 14));
        tabela.getTableHeader().setFont(new Font("Arial", Font.BOLD, 18));
        tabela.getTableHeader().setReorderingAllowed(false);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        for (int i = 0; i < colunas.length - 1; i++) {
            tabela.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        tabela.getColumnModel().getColumn(5).setCellRenderer(new ButtonRenderer());
        tabela.getColumnModel().getColumn(5).setCellEditor(new ButtonEditor(new JTextField()));

        JScrollPane scrollPane = new JScrollPane(tabela);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 30, 53, 30));

        return scrollPane;
    }

    private void filtrarFuncionarios(String filtro) {
        if (filtro.isEmpty() || filtro.equals("Buscar")) {
            funcionariosFiltrados = funcionarios.stream()
                    .map(funcionario -> new Object[]{
                            funcionario.getId(),
                            funcionario.getNome(),
                            formatarCPF(funcionario.getCpf()),
                            funcionario.getEmail(),
                            formatarTelefone(funcionario.getTelefone())
                    })
                    .collect(Collectors.toList());
        } else {
            funcionariosFiltrados = funcionarios.stream()
                    .filter(funcionario -> funcionario.getNome().toLowerCase().startsWith(filtro.toLowerCase()))
                    .map(funcionario -> new Object[]{
                            funcionario.getId(),
                            funcionario.getNome(),
                            formatarCPF(funcionario.getCpf()),
                            funcionario.getEmail(),
                            formatarTelefone(funcionario.getTelefone())
                    })
                    .collect(Collectors.toList());
        }

        carregarDados();
    }

    private void carregarDados() {
        modeloTabela.setRowCount(0);

        int inicio = paginaAtual * itensPorPagina;
        int fim = Math.min(inicio + itensPorPagina, funcionariosFiltrados.size());

        for (int i = inicio; i < fim; i++) {
            modeloTabela.addRow(funcionariosFiltrados.get(i));
        }

        atualizarBotoesPaginacao();
    }

    private JPanel criarPaginacao() {
        JPanel painelPaginacao = new JPanel(new FlowLayout(FlowLayout.CENTER));
        painelPaginacao.setBackground(new Color(0, 0, 0, 0));

        JButton botaoAnterior = new JButton("Anterior");
        JButton botaoProximo = new JButton("Próximo");

        botaoAnterior.addActionListener(e -> {
            if (paginaAtual > 0) {
                paginaAtual--;
                carregarDados();
            }
        });

        botaoProximo.addActionListener(e -> {
            if ((paginaAtual + 1) * itensPorPagina < funcionariosFiltrados.size()) {
                paginaAtual++;
                carregarDados();
            }
        });

        painelPaginacao.add(botaoAnterior);
        painelPaginacao.add(botaoProximo);

        return painelPaginacao;
    }

    private void atualizarBotoesPaginacao() {
        int totalPaginas = (int) Math.ceil((double) funcionariosFiltrados.size() / itensPorPagina);

       
    }
}
