package views.Funcionario;

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

import dao.Funcionario.FuncionarioDAO;
import models.Funcionario.Funcionario;

public class ListaDeFuncionarios extends JPanel {

    private JTable tabela;
    private DefaultTableModel modeloTabela;
    private int paginaAtual = 0;
    private final int itensPorPagina = 10;
    private List<Funcionario> funcionarios;
    private List<Object[]> funcionariosFiltrados;
    private JPanel painelPaginacao;
    private JScrollPane tabelaScrollPane;
    private Connection conn;

    public ListaDeFuncionarios(Connection conn) {
        this.conn = conn;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(new Color(0, 0, 0, 0));

        funcionariosFiltrados = new ArrayList<>();

        try {
            funcionarios = FuncionarioDAO.listarTodosFuncionarios(conn);
            atualizarFuncionariosFiltrados(funcionarios);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao carregar funcionários.", "Erro", JOptionPane.ERROR_MESSAGE);
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
            funcionarios = FuncionarioDAO.listarTodosFuncionarios(conn);
            atualizarFuncionariosFiltrados(funcionarios);
            carregarDados();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao carregar fornecedores.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void atualizarFuncionariosFiltrados(List<Funcionario> funcionarios) {
        funcionariosFiltrados.clear();

        for (Funcionario funcionario : funcionarios) {
            Object[] dadosFuncionario = new Object[7];
            dadosFuncionario[0] = funcionario.getId();
            dadosFuncionario[1] = funcionario.getNome();
            dadosFuncionario[2] = formatarTelefone(funcionario.getTelefone());
            dadosFuncionario[3] = funcionario.getEmail();
            dadosFuncionario[4] = funcionario.getCargo() != null
                    ? funcionario.getCargo().getNome()
                    : "Cargo não encontrado";
            dadosFuncionario[5] = funcionario.isStatus() ? "Ativo" : "Inativo";
            dadosFuncionario[6] = "";

            funcionariosFiltrados.add(dadosFuncionario);
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
        cadastrarButton.setPreferredSize(new Dimension(220, 30));
        painelBuscaBotao.add(cadastrarButton);

        cadastrarButton.addActionListener(e -> {
            JDialog cadastroDialog = new JDialog();
            cadastroDialog.setTitle("Cadastrar Funcionário");
            cadastroDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            cadastroDialog.setSize(1200, 650);
            cadastroDialog.setLocationRelativeTo(this);
            cadastroDialog.setModal(true);

            CadastrarFuncionario cadastroPanel = new CadastrarFuncionario();

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
        String[] colunas = { "Código", "Nome", "Telefone", "E-mail", "Cargo", "Status", "Ações" };

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
        tabela.getTableHeader().setFont(new Font("Arial", Font.BOLD, 16));
        tabela.getTableHeader().setReorderingAllowed(false);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        for (int i = 0; i < colunas.length - 1; i++) {
            tabela.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        tabela.getColumnModel().getColumn(5).setCellRenderer(new StatusCellRenderer());
        tabela.getColumnModel().getColumn(6).setCellRenderer(new ButtonRenderer());
        tabela.getColumnModel().getColumn(6).setCellEditor(new ButtonEditor(new JTextField()));

        tabela.getColumnModel().getColumn(0).setPreferredWidth(20);
        tabela.getColumnModel().getColumn(1).setPreferredWidth(270);
        tabela.getColumnModel().getColumn(2).setPreferredWidth(50);
        tabela.getColumnModel().getColumn(3).setPreferredWidth(210);
        tabela.getColumnModel().getColumn(4).setPreferredWidth(90);
        tabela.getColumnModel().getColumn(5).setPreferredWidth(90);
        tabela.getColumnModel().getColumn(6).setPreferredWidth(160);

        tabela.setCellSelectionEnabled(false);
        tabela.setRowSelectionAllowed(false);
        tabela.setColumnSelectionAllowed(false);

        for (int i = 0; i < tabela.getColumnModel().getColumnCount(); i++) {
            tabela.getColumnModel().getColumn(i).setResizable(false);
        }

        JScrollPane scrollPane = new JScrollPane(tabela);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 30, 56, 30));

        return scrollPane;
    }

    private void filtrarFuncionarios(String filtro) {
        if (filtro.isEmpty() || filtro.equals("Buscar")) {
            funcionariosFiltrados = funcionarios.stream()
                    .map(funcionario -> new Object[] {
                            funcionario.getId(),
                            funcionario.getNome(),
                            formatarTelefone(funcionario.getTelefone()),
                            funcionario.getEmail(),
                            funcionario.getCargo() != null ? funcionario.getCargo().getNome() : "Cargo não encontrado",
                            funcionario.isStatus() ? "Ativo" : "Inativo",
                    })
                    .collect(Collectors.toList());
        } else {
            funcionariosFiltrados = funcionarios.stream()
                    .filter(funcionario -> funcionario.getNome().toLowerCase().startsWith(filtro.toLowerCase()))
                    .map(funcionario -> new Object[] {
                            funcionario.getId(),
                            funcionario.getNome(),
                            formatarTelefone(funcionario.getTelefone()),
                            funcionario.getEmail(),
                            funcionario.getCargo() != null ? funcionario.getCargo().getNome() : "Cargo não encontrado",
                            funcionario.isStatus() ? "Ativo" : "Inativo",
                    })
                    .collect(Collectors.toList());
        }

        carregarDados();
    }

    private void carregarDados() {
        modeloTabela.setRowCount(0);

        if (funcionariosFiltrados.isEmpty()) {
            modeloTabela.addRow(new Object[] { "", "Funcionário não encontrado.", "", "", "", "", "" });
        } else {
            int inicio = paginaAtual * itensPorPagina;
            int fim = Math.min(inicio + itensPorPagina, funcionariosFiltrados.size());

            for (int i = inicio; i < fim; i++) {
                modeloTabela.addRow(funcionariosFiltrados.get(i));
            }
        }
    }

    private JPanel criarPaginacao() {
        JPanel painelPaginacao = new JPanel();
        painelPaginacao.setLayout(new FlowLayout(FlowLayout.CENTER));

        JButton botaoAnterior = new JButton("Anterior");
        botaoAnterior.setEnabled(paginaAtual > 0);
        botaoAnterior.addActionListener(e -> {
            if (paginaAtual > 0) {
                paginaAtual--;
                carregarDados();
                atualizarPaginacao();
            }
        });

        JButton botaoProximo = new JButton("Próximo");
        botaoProximo.setEnabled((paginaAtual + 1) * itensPorPagina < funcionariosFiltrados.size());
        botaoProximo.addActionListener(e -> {
            if ((paginaAtual + 1) * itensPorPagina < funcionariosFiltrados.size()) {
                paginaAtual++;
                carregarDados();
                atualizarPaginacao();
            }
        });

        painelPaginacao.add(botaoAnterior);
        painelPaginacao.add(Box.createHorizontalGlue());
        painelPaginacao.add(botaoProximo);

        return painelPaginacao;
    }

    private void atualizarPaginacao() {
        Component[] componentes = painelPaginacao.getComponents();

        for (Component componente : componentes) {
            if (componente instanceof JButton) {
                JButton btn = (JButton) componente;
                if (btn.getText().equals("Anterior")) {
                    btn.setEnabled(paginaAtual > 0);
                } else if (btn.getText().equals("Próximo")) {
                    btn.setEnabled((paginaAtual + 1) * itensPorPagina < funcionariosFiltrados.size());
                }
            }
        }
    }

    private class ButtonRenderer extends JPanel implements TableCellRenderer {
        private final JButton editButton;
        private final JButton deleteButton;

        public ButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));

            editButton = new JButton("EDITAR");
            editButton.setBackground(new Color(24, 39, 55));
            editButton.setForeground(Color.WHITE);
            editButton.setBorderPainted(false);
            editButton.setFocusPainted(false);
            editButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            editButton.setPreferredSize(new Dimension(100, 27));

            deleteButton = new JButton("EXCLUIR");
            deleteButton.setBackground(Color.RED);
            deleteButton.setForeground(Color.WHITE);
            deleteButton.setBorderPainted(false);
            deleteButton.setFocusPainted(false);
            deleteButton.setPreferredSize(new Dimension(100, 27));
            deleteButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            add(editButton);
            add(deleteButton);
        }

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,int row, int column) {
            setBackground(Color.WHITE);

            if (row < 0 || row >= funcionarios.size()) {
                editButton.setVisible(false);
                deleteButton.setVisible(false);
            }

            Funcionario funcionario = funcionarios.get(row); 

            if (funcionario.getCargo() != null && "Gerente".equalsIgnoreCase(funcionario.getCargo().getNome())) {
                editButton.setEnabled(funcionario.isStatus());
            } else {
                deleteButton.setText("EXCLUIR");
                editButton.setEnabled(true);
            }

            if (funcionariosFiltrados.isEmpty()) {
                editButton.setVisible(false);
                deleteButton.setVisible(false);
            } else {
                editButton.setVisible(true);
                deleteButton.setVisible(true);
            }
            return this;
        }
    }

    private class StatusCellRenderer extends DefaultTableCellRenderer {
        public StatusCellRenderer() {
            setHorizontalAlignment(SwingConstants.CENTER); 
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
                    column);

            if (value != null) {
                String status = value.toString();
                if (status.equals("Ativo")) {
                    label.setForeground(Color.GREEN);
                } else if (status.equals("Inativo")) {
                    label.setForeground(Color.RED);
                } else {
                    label.setForeground(Color.BLACK); 
                }
            }

            return label;
        }
    }

    private class ButtonEditor extends DefaultCellEditor {
        private final JButton editButton;
        private final JButton deleteButton;
        private int indiceLinha;

        public ButtonEditor(JTextField textField) {
            super(textField);
            editButton = new JButton("EDITAR");
            deleteButton = new JButton("");

            editButton.addActionListener(e -> {
                if (funcionariosFiltrados.isEmpty()) {
                    return;
                }

                indiceLinha = tabela.getSelectedRow();
                if (indiceLinha >= 0) {
                    int idFuncionario = (int) modeloTabela.getValueAt(indiceLinha, 0);

                    JDialog editarDialog = new JDialog();
                    editarDialog.setTitle("Editar Funcionário");
                    editarDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                    editarDialog.setSize(1200, 650);
                    editarDialog.setLocationRelativeTo(null);
                    editarDialog.setModal(true);

                    Point location = editarDialog.getLocation();
                    location.y = 150;
                    editarDialog.setLocation(location);

                    EditarFuncionario editarPanel = new EditarFuncionario(idFuncionario);
                    editarDialog.add(editarPanel);

                    editarDialog.addWindowListener(new java.awt.event.WindowAdapter() {
                        @Override
                        public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                            atualizarTabela();
                        }
                    });
                    editarDialog.setVisible(true);
                }
                fireEditingStopped();
            });

            deleteButton.addActionListener(e -> {
                if (funcionariosFiltrados.isEmpty()) {
                    return;
                }

                indiceLinha = tabela.getSelectedRow();
                if (indiceLinha >= 0) {
                    int idFuncionario = (int) modeloTabela.getValueAt(indiceLinha, 0);

                    excluirFuncionario(idFuncionario);
                    atualizarTabela();
                }
                fireEditingStopped();
            });
        }

        private void excluirFuncionario(int idFuncionario) {
            if (idFuncionario <= 0) {
                JOptionPane.showMessageDialog(null, "ID do funcionário inválido!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Funcionario funcionario;
            try {
                funcionario = FuncionarioDAO.funcionarioPorId(conn, idFuncionario);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Erro ao recuperar funcionário.", "Erro",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (funcionario == null) {
                JOptionPane.showMessageDialog(null, "Funcionário não encontrado!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String mensagemConfirmacao;
            if (funcionario.getCargo() != null && "Gerente".equalsIgnoreCase(funcionario.getCargo().getNome())) {
                if (funcionario.isStatus()) {
                    mensagemConfirmacao = "Deseja desativar o gerente \"" + funcionario.getNome() + "\"?";
                } else {
                    mensagemConfirmacao = "Deseja ativar o gerente \"" + funcionario.getNome() + "\"?";
                }
            } else {
                mensagemConfirmacao = "Você realmente deseja excluir o funcionário \"" + funcionario.getNome() + "\"?";
            }

            Object[] opcoes = { "Sim", "Não" };

            int resposta = JOptionPane.showOptionDialog(null,
                    mensagemConfirmacao,
                    "Confirmar Exclusão",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE,
                    null,
                    opcoes,
                    opcoes[0]);

            if (resposta == JOptionPane.YES_OPTION) {
                try {
                    if (funcionario.getCargo() != null
                            && "Gerente".equalsIgnoreCase(funcionario.getCargo().getNome())) {
                        if (funcionario.isStatus()) {
                            // Desativar o gerente
                            FuncionarioDAO.desativarGerente(conn, funcionario);
                            JOptionPane.showMessageDialog(null, "Gerente desativado com sucesso!", "Sucesso",
                                    JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            // Ativar o gerente
                            FuncionarioDAO.ativarGerente(conn, funcionario);
                            JOptionPane.showMessageDialog(null, "Gerente ativado com sucesso!", "Sucesso",
                                    JOptionPane.INFORMATION_MESSAGE);
                        }
                    } else {
                        FuncionarioDAO.deletarFuncionario(conn, funcionario);
                        JOptionPane.showMessageDialog(null, "Funcionário excluído com sucesso!", "Sucesso",
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                    atualizarFuncionariosFiltrados(funcionarios);
                    atualizarTabela();
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(null, "Erro ao processar a operação.", "Erro",
                            JOptionPane.ERROR_MESSAGE);
                }
            } else if (resposta == 1) {
                System.out.println("Operação cancelada.");
            } else {
                System.out.println("Diálogo fechado sem seleção.");
            }
            atualizarTabela();
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
           
            JPanel panel = new JPanel();
            panel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
            panel.setBackground(Color.WHITE); 

            indiceLinha = row;
            Funcionario funcionario = funcionarios.get(row); 

            if (funcionario.getCargo() != null && "Gerente".equalsIgnoreCase(funcionario.getCargo().getNome())) {
                deleteButton.setText("DESATIVAR");
                editButton.setEnabled(funcionario.isStatus());                
            } else {
                deleteButton.setText("EXCLUIR");
                editButton.setEnabled(true); 
            }

            if (funcionariosFiltrados.isEmpty()) {
                editButton.setVisible(false);
                deleteButton.setVisible(false);
            } else {
                editButton.setVisible(true);
                editButton.setBackground(new Color(24, 39, 55));
                editButton.setForeground(Color.WHITE);
                deleteButton.setBackground(Color.RED);
                deleteButton.setForeground(Color.WHITE);
                deleteButton.setVisible(true);
            }

            editButton.setPreferredSize(new Dimension(100, 27)); 
            editButton.setMaximumSize(new Dimension(100, 27));
    
            deleteButton.setPreferredSize(new Dimension(100, 27)); 
            deleteButton.setMaximumSize(new Dimension(100, 27));

            panel.add(editButton);
            panel.add(deleteButton);

            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return "";
        }
    }
}