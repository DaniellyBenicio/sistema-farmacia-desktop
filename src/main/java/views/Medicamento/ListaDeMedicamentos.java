package views.Medicamento;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import dao.Medicamento.MedicamentoDAO;
import models.Medicamento.Medicamento;

public class ListaDeMedicamentos extends JPanel {

    private JTable tabela;
    private DefaultTableModel modeloTabela;
    private List<Medicamento> medicamentos;
    private List<Object[]> medicamentosFiltrados;
    private JScrollPane tabelaScrollPane;
    @SuppressWarnings("unused")
    private Connection conn;

    public ListaDeMedicamentos(Connection conn) throws SQLException {
        this.conn = conn;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(new Color(0, 0, 0, 0));

        medicamentosFiltrados = new ArrayList<>();

        medicamentos = MedicamentoDAO.ListaDeMedicamentos(conn);
        atualizarMedicamentosFiltrados(medicamentos);

        JPanel painelSuperior = criarTituloEBusca();
        add(painelSuperior);

        tabelaScrollPane = criarTabela();
        add(tabelaScrollPane);
    }

    private void atualizarMedicamentosFiltrados(List<Medicamento> medicamentos) {
        medicamentosFiltrados.clear();

        for (Medicamento medicamento : medicamentos) {
            Object[] dadosMedicamento = new Object[6];
            dadosMedicamento[0] = medicamento.getNome();
            dadosMedicamento[1] = medicamento.getCategoria();
            dadosMedicamento[2] = medicamento.getFormaFarmaceutica();
            dadosMedicamento[3] = medicamento.getDosagem();
            dadosMedicamento[4] = medicamento.getDataValidade();
            dadosMedicamento[5] = String.format("R$ %.2f", medicamento.getValorUnit());
            
            medicamentosFiltrados.add(dadosMedicamento);
        }
    }

    private JPanel criarTituloEBusca() {
        JPanel painelSuperior = new JPanel(new BorderLayout());
        JPanel painelTitulo = new JPanel();
        painelTitulo.setLayout(new BoxLayout(painelTitulo, BoxLayout.Y_AXIS));
        painelTitulo.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        JLabel titulo = new JLabel("LISTA DE MEDICAMENTOS");
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        painelTitulo.add(titulo);

        JPanel painelBusca = new JPanel();
        painelBusca.setLayout(new FlowLayout());

        JTextField campoBusca = new JTextField(30);
        campoBusca.setFont(new Font("Arial", Font.PLAIN, 14));
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

        painelBusca.add(campoBusca);
        painelSuperior.add(painelTitulo, BorderLayout.NORTH);
        painelSuperior.add(painelBusca, BorderLayout.CENTER);

        return painelSuperior;
    }

    private JScrollPane criarTabela() {
        String[] colunas = { "Nome", "Categoria", "Forma Farmacêutica", "Dosagem", "Validade", "Valor Unitário" };
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        carregarDados();

        tabela = new JTable(modeloTabela);
        tabela.setFillsViewportHeight(true);
        tabela.setRowHeight(30);
        tabela.setFont(new Font("Arial", Font.PLAIN, 12));
        tabela.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        for (int i = 0; i < colunas.length; i++) {
            tabela.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(tabela);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        return scrollPane;
    }

    private void filtrarMedicamentos(String filtro) {
        if (filtro.isEmpty() || filtro.equals("Buscar")) {
            atualizarMedicamentosFiltrados(medicamentos);
        } else {
            medicamentosFiltrados = medicamentos.stream()
                    .filter(medicamento -> medicamento.getNome().toLowerCase().startsWith(filtro.toLowerCase()))
                    .map(medicamento -> new Object[] {
                            medicamento.getNome(),
                            medicamento.getCategoria(),
                            medicamento.getFormaFarmaceutica(),
                            medicamento.getDosagem(),
                            medicamento.getDataValidade(),
                            String.format("R$ %.2f", medicamento.getValorUnit()),
                    })
                    .collect(Collectors.toList());
        }
        carregarDados();
    }

    private void carregarDados() {
        modeloTabela.setRowCount(0);
        for (Object[] medicamento : medicamentosFiltrados) {
            modeloTabela.addRow(medicamento);
        }
    }
}
