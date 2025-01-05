package com.mycompany.farmacia.views;

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

import dao.FornecedorDAO;
import main.ConexaoBD;
import models.Fornecedor;

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

        try {
            fornecedores = FornecedorDAO.listarFornecedores(conn); 
            atualizarFornecedoresFiltrados(fornecedores); 
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao carregar fornecedores: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
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
            JOptionPane.showMessageDialog(this, "Erro ao carregar fornecedores: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void atualizarFornecedoresFiltrados(List<Fornecedor> fornecedores) {
        fornecedoresFiltrados.clear(); 

        for (Fornecedor fornecedor : fornecedores) {
            Object[] dadosFornecedor = new Object[7];
            dadosFornecedor[0] = fornecedor.getId();
            dadosFornecedor[1] = fornecedor.getNome();
            dadosFornecedor[2] = formatarCNPJ(fornecedor.getCnpj());
            dadosFornecedor[3] = fornecedor.getEmail();
            dadosFornecedor[4] = formatarTelefone(fornecedor.getTelefone());
            String representante = fornecedor.getNomeRepresentante();
            dadosFornecedor[5] = (representante != null && !representante.isEmpty()) ? representante : "Sem Representante";
            dadosFornecedor[6] = ""; 

            fornecedoresFiltrados.add(dadosFornecedor);
        }
    }

    private String formatarCNPJ(String cnpj) {
        // Remove caracteres não numéricos
        String numero = cnpj.replaceAll("\\D", "");
        if (numero.length() == 14) { // Verifica se o CNPJ possui 14 dígitos
            return String.format("%s.%s.%s/%s-%s",
                    numero.substring(0, 2),
                    numero.substring(2, 5),
                    numero.substring(5, 8),
                    numero.substring(8, 12),
                    numero.substring(12, 14));
        }
        return cnpj; // Retorna sem formatação se não for válido
    }

    private String formatarTelefone(String telefone) {
        // Remove caracteres não numéricos
        String numero = telefone.replaceAll("\\D", "");
        if (numero.length() == 11) { // Verifica se o telefone possui 11 dígitos
            return String.format("(%s) %s-%s",
                    numero.substring(0, 2),
                    numero.substring(2, 7),
                    numero.substring(7));
        } else if (numero.length() == 10) { // Verifica se o telefone possui 10 dígitos
            return String.format("(%s) %s-%s",
                    numero.substring(0, 2),
                    numero.substring(2, 6),
                    numero.substring(6));
        }
        return telefone; // Retorna sem formatação se não for válido
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
        String[] colunas = {"Código", "Nome", "CNPJ", "E-mail", "Telefone", "Representante", "Ações"};
        
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
    
        tabela.getColumnModel().getColumn(6).setCellRenderer(new ButtonRenderer());
        tabela.getColumnModel().getColumn(6).setCellEditor(new ButtonEditor(new JTextField()));        
    
        tabela.getColumnModel().getColumn(0).setPreferredWidth(40);
        tabela.getColumnModel().getColumn(1).setPreferredWidth(300);
        tabela.getColumnModel().getColumn(2).setPreferredWidth(100);
        tabela.getColumnModel().getColumn(3).setPreferredWidth(200);
        tabela.getColumnModel().getColumn(4).setPreferredWidth(100);
        tabela.getColumnModel().getColumn(5).setPreferredWidth(150);
        tabela.getColumnModel().getColumn(6).setPreferredWidth(150);
        
        tabela.setCellSelectionEnabled(false);  
        tabela.setRowSelectionAllowed(false);   
        tabela.setColumnSelectionAllowed(false); 
    
        for (int i = 0; i < tabela.getColumnModel().getColumnCount(); i++) {
            tabela.getColumnModel().getColumn(i).setResizable(false); 
        }
    
        JScrollPane scrollPane = new JScrollPane(tabela);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 30, 53, 30));
    
        return scrollPane;
    }
    
    private void filtrarFornecedores(String filtro) {
        if (filtro.isEmpty() || filtro.equals("Buscar")) {
            fornecedoresFiltrados = fornecedores.stream()
                    .map(fornecedor -> new Object[]{
                        fornecedor.getId(),
                        fornecedor.getNome(),
                        formatarCNPJ(fornecedor.getCnpj()),
                        fornecedor.getEmail(),
                        formatarTelefone(fornecedor.getTelefone()),
                        fornecedor.getNomeRepresentante() != null && !fornecedor.getNomeRepresentante().isEmpty() 
                        ? fornecedor.getNomeRepresentante() : "Sem Representante"
                    })
                    .collect(Collectors.toList());
        } else {
            fornecedoresFiltrados = fornecedores.stream()
                    .filter(fornecedor -> fornecedor.getNome().toLowerCase().startsWith(filtro.toLowerCase()))
                    .map(fornecedor -> new Object[]{
                        fornecedor.getId(),
                        fornecedor.getNome(),
                        formatarCNPJ(fornecedor.getCnpj()),
                        fornecedor.getEmail(),
                        formatarTelefone(fornecedor.getTelefone()),
                        fornecedor.getNomeRepresentante() != null && !fornecedor.getNomeRepresentante().isEmpty() 
                        ? fornecedor.getNomeRepresentante() : "Sem Representante"
                    })
                    .collect(Collectors.toList());
        }
        carregarDados();
    }
    
    
    private void carregarDados() {
        modeloTabela.setRowCount(0);

        if (fornecedoresFiltrados.isEmpty()) {
            modeloTabela.addRow(new Object[]{"", "Fornecedor não encontrado.", "", "", "", "", ""});
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
    
            deleteButton = new JButton("EXCLUIR");
            deleteButton.setBackground(Color.RED);
            deleteButton.setForeground(Color.WHITE);
            deleteButton.setBorderPainted(false);
            deleteButton.setFocusPainted(false);
            deleteButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); 
    
            add(editButton);
            add(deleteButton);
        }
    
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {   
            setBackground(Color.WHITE);
            if (fornecedoresFiltrados.isEmpty()) { 
                setBackground(Color.WHITE);               
                return new JPanel();  
            }
            return this;  
        }
    }

    private class ButtonEditor extends DefaultCellEditor {
        private final JButton editButton;
        private final JButton deleteButton;
        private int indiceLinha;

        public ButtonEditor(JTextField textField) {
            super(textField);
            editButton = new JButton("EDITAR");
            deleteButton = new JButton("EXCLUIR");

            editButton.addActionListener(e -> {
                indiceLinha = tabela.getSelectedRow();
                if (indiceLinha >= 0) {
                    int fornecedorId = (Integer) fornecedoresFiltrados.get(indiceLinha)[0];

                    JDialog editarDialog = new JDialog();
                    editarDialog.setTitle("Editar Fornecedor");
                    editarDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                    editarDialog.setSize(1200, 650);
                    editarDialog.setLocationRelativeTo(null); 
                    editarDialog.setModal(true);

                    Point location = editarDialog.getLocation();
                    location.y = 150; 
                    editarDialog.setLocation(location);

                    EditarFornecedor editarPanel = new EditarFornecedor(fornecedorId);
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
                indiceLinha = tabela.getSelectedRow();
                if (indiceLinha >= 0) {
                    int fornecedorId = (Integer) fornecedoresFiltrados.get(indiceLinha)[0];
                    excluirFornecedor(fornecedorId);                    
                }
                fireEditingStopped(); 
            });
        }

        private void excluirFornecedor(int idFornecedor) {
            if (idFornecedor <= 0) {
                JOptionPane.showMessageDialog(null, "ID do fornecedor inválido!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
        
            Fornecedor fornecedor = null;
            try (Connection conn = ConexaoBD.getConnection()) {
                fornecedor = FornecedorDAO.fornecedorPorId(conn, idFornecedor);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Erro ao recuperar fornecedor: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                return; 
            }
        
            if (fornecedor == null) {
                JOptionPane.showMessageDialog(null, "Fornecedor não encontrado!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
        
            String mensagemConfirmacao = "Você realmente deseja excluir o fornecedor \"" + fornecedor.getNome() + "\"?";
        
            Object[] opcoes = {"Sim", "Não"};

            int resposta = JOptionPane.showOptionDialog(null,
                    mensagemConfirmacao,
                    "Confirmar Exclusão",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE,
                    null,
                    opcoes, 
                    opcoes[0]
            );

            if (resposta == 0) { // A opção "Sim" foi escolhida
                try (Connection conn = ConexaoBD.getConnection()) {
                    FornecedorDAO.deletarFornecedor(conn, fornecedor);
                    JOptionPane.showMessageDialog(null, "Fornecedor excluído com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    atualizarTabela();
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(null, "Erro ao excluir fornecedor: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            } else if (resposta == 1) { // A opção "Não" foi escolhida
                System.out.println("Exclusão cancelada."); 
            } else {
                System.out.println("Diálogo fechado sem seleção.");
            }
        }

        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            JPanel panel = new JPanel();
            panel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
        
            if (fornecedoresFiltrados.isEmpty()) {
                return panel; 
            }
        
            editButton.setBackground(new Color(24, 39, 55));
            editButton.setForeground(Color.WHITE);
        
            deleteButton.setBackground(Color.RED);
            deleteButton.setForeground(Color.WHITE);
        
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