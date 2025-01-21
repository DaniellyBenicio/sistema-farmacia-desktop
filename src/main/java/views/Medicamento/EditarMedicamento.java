package views.Medicamento;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.Connection;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import dao.Medicamento.MedicamentoDAO;
import main.ConexaoBD;
import models.Medicamento.Medicamento;

public class EditarMedicamento extends JPanel {

    private JTextField nomeField;
    private JTextField fabricanteField;
    private JTextField precoField;
    private int medicamentoId;

    public EditarMedicamento(int medicamentoId) {
        this.medicamentoId = medicamentoId;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(new Color(0, 0, 0, 0));

        JLabel titulo = criarTitulo();
        add(Box.createRigidArea(new Dimension(0, 80)));
        add(titulo);

        JPanel camposPanel = criarCamposPanel();
        add(camposPanel);

        JPanel botoesPanel = criarBotoesPanel();
        add(botoesPanel);
        add(Box.createRigidArea(new Dimension(0, 150)));

        carregarDadosMedicamento();
    }

    private JLabel criarTitulo() {
        JLabel titulo = new JLabel("EDIÇÃO DE MEDICAMENTO");
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        return titulo;
    }

    private JPanel criarCamposPanel() {
        JPanel camposPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 30, 0, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        Font labelFont = new Font("Arial", Font.BOLD, 18);
        Font fieldFont = new Font("Arial", Font.PLAIN, 18);
        Dimension fieldSize = new Dimension(300, 40);

        JLabel nomeLabel = new JLabel("Nome do Medicamento");
        nomeLabel.setFont(labelFont);
        gbc.gridx = 0;
        gbc.gridy = 0;
        camposPanel.add(nomeLabel, gbc);

        nomeField = new JTextField();
        nomeField.setPreferredSize(fieldSize);
        estilizarCamposFormulario(nomeField, fieldFont);
        gbc.gridx = 0;
        gbc.gridy = 1;
        camposPanel.add(nomeField, gbc);

        JLabel fabricanteLabel = new JLabel("Fabricante");
        fabricanteLabel.setFont(labelFont);
        gbc.gridx = 1;
        gbc.gridy = 0;
        camposPanel.add(fabricanteLabel, gbc);

        fabricanteField = new JTextField();
        fabricanteField.setPreferredSize(fieldSize);
        estilizarCamposFormulario(fabricanteField, fieldFont);
        gbc.gridx = 1;
        gbc.gridy = 1;
        camposPanel.add(fabricanteField, gbc);

        JLabel precoLabel = new JLabel("Preço");
        precoLabel.setFont(labelFont);
        gbc.gridx = 0;
        gbc.gridy = 2;
        camposPanel.add(precoLabel, gbc);

        precoField = new JTextField();
        precoField.setPreferredSize(fieldSize);
        estilizarCamposFormulario(precoField, fieldFont);
        gbc.gridx = 0;
        gbc.gridy = 3;
        camposPanel.add(precoField, gbc);

        return camposPanel;
    }

    private JPanel criarBotoesPanel() {
        JPanel botoesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 20));

        JButton cancelarButton = new JButton("CANCELAR");
        cancelarButton.setFont(new Font("Arial", Font.BOLD, 17));
        cancelarButton.setBackground(Color.RED);
        cancelarButton.setForeground(Color.WHITE);
        cancelarButton.setPreferredSize(new Dimension(130, 35));
        cancelarButton.addActionListener(e -> SwingUtilities.getWindowAncestor(this).dispose());
        botoesPanel.add(cancelarButton);

        JButton salvarButton = new JButton("SALVAR");
        salvarButton.setFont(new Font("Arial", Font.BOLD, 17));
        salvarButton.setBackground(new Color(24, 39, 55));
        salvarButton.setForeground(Color.WHITE);
        salvarButton.setPreferredSize(new Dimension(130, 35));
        salvarButton.addActionListener(e -> salvarMedicamento(medicamentoId));
        botoesPanel.add(salvarButton);

        return botoesPanel;
    }

    private void carregarDadosMedicamento() {
        try (Connection conn = ConexaoBD.getConnection()) {
            Medicamento medicamento = MedicamentoDAO.medicamentoPorId(conn, medicamentoId);
            if (medicamento != null) {
                nomeField.setText(medicamento.getNome());
                fabricanteField.setText(medicamento.getFabricante());
                precoField.setText(String.valueOf(medicamento.getPreco()));
            } else {
                JOptionPane.showMessageDialog(null, "Medicamento não encontrado!", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao carregar dados do medicamento.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void salvarMedicamento(int idMedicamento) {
        String nome = nomeField.getText().trim();
        String fabricante = fabricanteField.getText().trim();
        String precoTexto = precoField.getText().trim();

        if (nome.isEmpty() || fabricante.isEmpty() || precoTexto.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Todos os campos devem ser preenchidos.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double preco;
        try {
            preco = Double.parseDouble(precoTexto);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "O preço deve ser um valor numérico.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = ConexaoBD.getConnection()) {
            Medicamento medicamento = new Medicamento(idMedicamento, nome, fabricante, preco);
            MedicamentoDAO.atualizarMedicamento(conn, medicamento);
            JOptionPane.showMessageDialog(null, "Medicamento atualizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao atualizar medicamento: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void estilizarCamposFormulario(JTextField campo, Font font) {
        campo.setBackground(Color.WHITE);
        campo.setForeground(Color.BLACK);
        campo.setFont(font);
        campo.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
    }
}
