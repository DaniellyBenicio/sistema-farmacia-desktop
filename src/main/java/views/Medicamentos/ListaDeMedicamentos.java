package views.Medicamentos;

import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
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

import dao.Medicamento.MedicamentoDAO;
import main.ConexaoBD;
import models.Medicamento.Medicamento;
import views.Clientes.CadastrarCliente; 

public class ListaDeMedicamentos extends JPanel {

    private JTable tabela;
    private DefaultTableModel modeloTabela;
    private List<Medicamento> medicamentos;
    private List<Object[]> medicamentosFiltrados;
    private JScrollPane tabelaScrollPane;
    private Connection conn;

    public ListaDeMedicamentos(Connection conn) {
        this.conn = conn;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(new Color(0, 0, 0, 0));

        medicamentosFiltrados = new ArrayList<>();
        try {
            medicamentos = MedicamentoDAO.listarTodos(conn);
            atualizarMedicamentosFiltrados(medicamentos);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao carregar medicamentos.", "Erro", JOptionPane.ERROR_MESSAGE);
        }

        JPanel painelSuperior = criarTituloEBusca();
        add(painelSuperior);

        tabelaScrollPane = criarTabela();
        add(tabelaScrollPane);
    }

    public void atualizarTabela() {
        try {
            medicamentos = MedicamentoDAO.listarTodos(conn);
            atualizarMedicamentosFiltrados(medicamentos);
            carregarDados();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao carregar medicamentos.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void atualizarMedicamentosFiltrados(List<Medicamento> medicamentos) {
        medicamentosFiltrados.clear();

        for (Medicamento medicamento : medicamentos) {
            Object[] dadosMedicamento = new Object[8]; 
            dadosMedicamento[0] = medicamento.getNome();
            dadosMedicamento[1] = medicamento.getCategoria().getNome();
            dadosMedicamento[2] = medicamento.getFormaFarmaceutica();
            dadosMedicamento[3] = medicamento.getDosagem();
            dadosMedicamento[4] = medicamento.getDataValidade();
            dadosMedicamento[5] = medicamento.getQnt();
            dadosMedicamento[6] = medicamento.getValorUnit(); 
            dadosMedicamento[7] = ""; 

            medicamentosFiltrados.add(dadosMedicamento);
        }
    }

    private JPanel criarTituloEBusca() {
        JPanel painelSuperior = new JPanel(new BorderLayout());

        JPanel painelTitulo = new JPanel();
        painelTitulo.setLayout(new BoxLayout(painelTitulo, BoxLayout.Y_AXIS));
        painelTitulo.setBorder(BorderFactory.createEmptyBorder(50, 0, 35, 0));

        JLabel titulo = new JLabel("LISTA DE MEDICAMENTOS");
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

        painelBuscaBotao.add(campoBusca);
        painelBuscaBotao.add(Box.createHorizontalGlue());

        JButton cadastrarButton = new JButton("Cadastrar Medicamento");
        cadastrarButton.setFont(new Font("Arial", Font.BOLD, 15));
        cadastrarButton.setBackground(new Color(24, 39, 55));
        cadastrarButton.setForeground(Color.WHITE);
        cadastrarButton.setFocusPainted(false);
        cadastrarButton.setPreferredSize(new Dimension(220, 30));
        painelBuscaBotao.add(cadastrarButton);

        cadastrarButton.addActionListener(e -> {
            JDialog cadastroDialog = new JDialog();
            cadastroDialog.setTitle("Cadastrar Medicamento");
            cadastroDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            cadastroDialog.setSize(1200, 650);
            cadastroDialog.setLocationRelativeTo(this);
            cadastroDialog.setModal(true);

            CadastrarMedicamento cadastrarMedicamento = new CadastrarMedicamento();

            cadastroDialog.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                    atualizarTabela();
                }
            });

            cadastroDialog.add(cadastrarMedicamento);
            cadastroDialog.setVisible(true);
        });


        painelSuperior.add(painelTitulo, BorderLayout.NORTH);
        painelSuperior.add(painelBuscarTitulo, BorderLayout.CENTER);
        painelSuperior.add(painelBuscaBotao, BorderLayout.SOUTH);

        return painelSuperior;
    }

    private JScrollPane criarTabela() {
        String[] colunas = { "Nome", "Categoria", "F. Farmacêutica", "Dosagem", "Validade", "Estoque", "Preço Unitário", "Ações" };

        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 7; 
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

        tabela.getColumnModel().getColumn(7).setCellRenderer(new RenderizadorBotoes());
        tabela.getColumnModel().getColumn(7).setCellEditor(new EditorBotoes(new JTextField()));
        
        // Ajustando a largura das colunas
        tabela.getColumnModel().getColumn(0).setPreferredWidth(150);
        tabela.getColumnModel().getColumn(1).setPreferredWidth(100);
        tabela.getColumnModel().getColumn(2).setPreferredWidth(100);
        tabela.getColumnModel().getColumn(3).setPreferredWidth(80);
        tabela.getColumnModel().getColumn(4).setPreferredWidth(80);
        tabela.getColumnModel().getColumn(5).setPreferredWidth(80);
        tabela.getColumnModel().getColumn(6).setPreferredWidth(80);
        tabela.getColumnModel().getColumn(7).setPreferredWidth(150);

        // Desabilita seleções nas células
        tabela.setCellSelectionEnabled(false);
        tabela.setRowSelectionAllowed(false);
        tabela.setColumnSelectionAllowed(false);

        JScrollPane scrollPane = new JScrollPane(tabela);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 30, 57, 30));

        return scrollPane;
    }

    private void filtrarMedicamentos(String filtro) {
        if (filtro.isEmpty() || filtro.equals("Buscar")) {
            atualizarMedicamentosFiltrados(medicamentos);
        } else {
            medicamentosFiltrados = medicamentos.stream()
                    .filter(medicamento -> medicamento.getNome().toLowerCase().contains(filtro.toLowerCase()))
                    .map(medicamento -> new Object[]{
                            medicamento.getNome(),
                            medicamento.getCategoria().getNome(),
                            medicamento.getFormaFarmaceutica(),
                            medicamento.getDosagem(),
                            medicamento.getDataValidade(),
                            medicamento.getQnt(),
                            medicamento.getValorUnit(), 
                    })
                    .collect(Collectors.toList());
        }
        carregarDados();
    }

    private void carregarDados() {
        modeloTabela.setRowCount(0);

        if (medicamentosFiltrados.isEmpty()) {
            modeloTabela.addRow(new Object[]{"Medicamento não encontrado.", "", "", "", "", "", ""});
        } else {
            for (Object[] medicamento : medicamentosFiltrados) {
                modeloTabela.addRow(medicamento);
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
                                                        if (medicamentosFiltrados.isEmpty()) {
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
                
                
                if (medicamentosFiltrados.isEmpty()) {
                    return;
                }
        
              
                indiceLinha = tabela.getSelectedRow();
                if (indiceLinha >= 0) {
                   
                    int medicamentoId = medicamentos.get(indiceLinha).getId();  
        
                    
                    JDialog dialogoEditar = new JDialog();
                    dialogoEditar.setTitle("Editar Medicamento");
                    dialogoEditar.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                    dialogoEditar.setSize(1200, 650);
                    dialogoEditar.setLocationRelativeTo(null);
                    dialogoEditar.setModal(true);
        
              
                    Point localizacao = dialogoEditar.getLocation();
                    localizacao.y = 150;
                    dialogoEditar.setLocation(localizacao);
        
                   
                    EditarMedicamento painelEditar = new EditarMedicamento(medicamentoId);
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
                
               
                if (medicamentosFiltrados.isEmpty()) {
                    return;
                }
        
           
                indiceLinha = tabela.getSelectedRow();
                if (indiceLinha >= 0) {
                 
                    int medicamentoId = medicamentos.get(indiceLinha).getId();
                    ExcluirMedicamento.excluirMedicamento(medicamentoId);
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