package views.Medicamentos;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.text.MaskFormatter;

import main.ConexaoBD;

public class EditarMedicamento extends JPanel {
    private JTextField nomeField;
    private JComboBox<String> categoriaComboBox;
    private JTextField fabricanteField;
    private JTextField dosagemField;
    private JComboBox<String> tipoComboBox;
    private JComboBox<String> fornecedorComboBox;
    private JFormattedTextField dataFabricacaoField;
    private JFormattedTextField dataValidadeField;
    private JTextField estoqueField;
    private JTextField valorUnitarioField;
    private int medicamentoId;

    public EditarMedicamento(int medicamentoId) {
        this.medicamentoId = medicamentoId;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(new Color(0, 0, 0, 0));

        JLabel titulo = new JLabel("EDITAR MEDICAMENTO");
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(Box.createRigidArea(new Dimension(0, 80)));
        add(titulo);

        JPanel camposPanel = criarCamposPanel();
        add(camposPanel);

        JPanel botoesPanel = criarBotoesPanel();
        add(botoesPanel);
        add(Box.createRigidArea(new Dimension(0, 150)));

        carregarDados();
    }

    private JPanel criarCamposPanel() {
        JPanel camposPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        camposPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        nomeField = new JTextField();
        categoriaComboBox = new JComboBox<>(new String[]{"Selecione", "Analgesico", "Antibiotico", "Outros"});
        fabricanteField = new JTextField();
        dosagemField = new JTextField();
        tipoComboBox = new JComboBox<>(new String[]{"Selecione", "Ético", "Genérico", "Similar"});
        fornecedorComboBox = new JComboBox<>(new String[]{"Selecione", "Fornecedor A", "Fornecedor B"});
        estoqueField = new JTextField();
        valorUnitarioField = new JTextField();
        
        try {
            MaskFormatter dataFormatter = new MaskFormatter("##/####");
            dataFabricacaoField = new JFormattedTextField(dataFormatter);
            dataValidadeField = new JFormattedTextField(dataFormatter);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        camposPanel.add(new JLabel("Nome:"));
        camposPanel.add(nomeField);
        camposPanel.add(new JLabel("Categoria:"));
        camposPanel.add(categoriaComboBox);
        camposPanel.add(new JLabel("Fabricante:"));
        camposPanel.add(fabricanteField);
        camposPanel.add(new JLabel("Dosagem:"));
        camposPanel.add(dosagemField);
        camposPanel.add(new JLabel("Tipo:"));
        camposPanel.add(tipoComboBox);
        camposPanel.add(new JLabel("Fornecedor:"));
        camposPanel.add(fornecedorComboBox);
        camposPanel.add(new JLabel("Estoque:"));
        camposPanel.add(estoqueField);
        camposPanel.add(new JLabel("Valor Unitário:"));
        camposPanel.add(valorUnitarioField);
        camposPanel.add(new JLabel("Fabricação:"));
        camposPanel.add(dataFabricacaoField);
        camposPanel.add(new JLabel("Validade:"));
        camposPanel.add(dataValidadeField);
        
        return camposPanel;
    }

    private JPanel criarBotoesPanel() {
        JPanel botoesPanel = new JPanel(new FlowLayout());
        
        JButton cancelarButton = new JButton("CANCELAR");
        cancelarButton.addActionListener(e -> SwingUtilities.getWindowAncestor(this).dispose());
        
        JButton salvarButton = new JButton("SALVAR");
        salvarButton.addActionListener(e -> atualizarMedicamento());
        
        botoesPanel.add(cancelarButton);
        botoesPanel.add(salvarButton);
        return botoesPanel;
    }

    private void carregarDados() {
        try (Connection conn = ConexaoBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM medicamentos WHERE id = ?")) {
            stmt.setInt(1, medicamentoId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                nomeField.setText(rs.getString("nome"));
                categoriaComboBox.setSelectedItem(rs.getString("categoria"));
                fabricanteField.setText(rs.getString("fabricante"));
                dosagemField.setText(rs.getString("dosagem"));
                tipoComboBox.setSelectedItem(rs.getString("tipo"));
                fornecedorComboBox.setSelectedItem(rs.getString("fornecedor"));
                estoqueField.setText(rs.getString("estoque"));
                valorUnitarioField.setText(rs.getString("valor_unitario"));
                dataFabricacaoField.setText(rs.getString("data_fabricacao"));
                dataValidadeField.setText(rs.getString("data_validade"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao carregar os dados.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void atualizarMedicamento() {
        try (Connection conn = ConexaoBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement("UPDATE medicamentos SET nome=?, categoria=?, fabricante=?, dosagem=?, tipo=?, fornecedor=?, estoque=?, valor_unitario=?, data_fabricacao=?, data_validade=? WHERE id=?")) {
            stmt.setString(1, nomeField.getText());
            stmt.setString(2, (String) categoriaComboBox.getSelectedItem());
            stmt.setString(3, fabricanteField.getText());
            stmt.setString(4, dosagemField.getText());
            stmt.setString(5, (String) tipoComboBox.getSelectedItem());
            stmt.setString(6, (String) fornecedorComboBox.getSelectedItem());
            stmt.setString(7, estoqueField.getText());
            stmt.setString(8, valorUnitarioField.getText());
            stmt.setString(9, dataFabricacaoField.getText());
            stmt.setString(10, dataValidadeField.getText());
            stmt.setInt(11, medicamentoId);
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Medicamento atualizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao atualizar medicamento.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao atualizar o medicamento.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
