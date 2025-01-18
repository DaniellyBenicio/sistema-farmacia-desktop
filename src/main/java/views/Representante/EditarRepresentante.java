package views.Representante;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.Connection;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.MaskFormatter;

import controllers.Represetante.RepresentanteController;
import main.ConexaoBD;
import models.Representante.Representante;

public class EditarRepresentante extends JDialog {
    private JTextField representanteField;
    private JFormattedTextField numeroField;

    public EditarRepresentante(java.awt.Frame parent, Representante representante, Connection conn) {
        super(parent, "Editar Representante", true);
        setSize(580, 300);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 0, 10, 10);

        JLabel fornecedorLabel = new JLabel("Nome do Fornecedor:");
        JTextField fornecedorField = new JTextField(20);
        if (representante.getFornecedor() != null) {
            fornecedorField.setText(representante.getFornecedor().getNome());
        } else {
            fornecedorField.setText("Fornecedor não disponível");
        }
        fornecedorField.setEditable(false);

        JLabel representanteLabel = new JLabel("Nome do Representante:");
        representanteField = new JTextField(20);
        representanteField.setText(representante.getNome());

        JLabel numeroLabel = new JLabel("Telefone:");
        MaskFormatter numeroFormatter;
        try {
            numeroFormatter = new MaskFormatter("(##) #####-####");
            numeroFormatter.setValidCharacters("0123456789");
        } catch (java.text.ParseException e) {
            e.printStackTrace();
            return;
        }

        numeroField = new JFormattedTextField(numeroFormatter);
        numeroField.setText(representante.getTelefone());
        numeroField.setColumns(15);

        Font font = new Font("Arial", Font.PLAIN, 16);
        fornecedorField.setFont(font);
        representanteField.setFont(font);
        numeroField.setFont(font);

        for (JTextField field : new JTextField[] { fornecedorField, representanteField, numeroField }) {
            field.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            field.setPreferredSize(new Dimension(300, 30));
        }

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(fornecedorLabel, gbc);
        gbc.gridx = 1;
        add(fornecedorField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(representanteLabel, gbc);
        gbc.gridx = 1;
        add(representanteField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        add(numeroLabel, gbc);
        gbc.gridx = 1;
        add(numeroField, gbc);

        JButton salvarButton = new JButton("SALVAR");
        JButton cancelarButton = new JButton("CANCELAR");

        Font botaoFont = new Font("Arial", Font.BOLD, 14);
        cancelarButton.setFont(botaoFont);
        cancelarButton.setBackground(Color.RED);
        cancelarButton.setForeground(Color.WHITE);
        cancelarButton.setPreferredSize(new Dimension(120, 35));
        cancelarButton.setFocusPainted(false);
        cancelarButton.addActionListener(e -> dispose());

        salvarButton.setFont(botaoFont);
        salvarButton.setBackground(new Color(24, 39, 55));
        salvarButton.setForeground(Color.WHITE);
        salvarButton.setFocusPainted(false);
        salvarButton.setPreferredSize(new Dimension(120, 35));

        salvarButton.addActionListener(e -> {
            String nomeRepresentante = representanteField.getText().trim();
            String telefoneOriginal = numeroField.getText().trim();
            String telefoneFormatado = telefoneOriginal.replaceAll("[^0-9]", "");

            StringBuilder mensagensErro = new StringBuilder();

            if (nomeRepresentante.isEmpty() && telefoneOriginal.isEmpty()) {
                mensagensErro.append("Por favor, informe o nome e telefone do representante.\n");
            } else if (nomeRepresentante.isEmpty()) {
                mensagensErro.append("Por favor, informe o nome do representante.\n");
            } else if (!nomeRepresentante.matches("^[a-zA-ZÀ-ÿ\\s]+$")) {
                mensagensErro.append(
                        "Nome do representante contém caracteres inválidos. Apenas letras e espaços são permitidos.\n");
            } else if (telefoneOriginal.isEmpty()) {
                mensagensErro
                        .append("Por favor, informe o telefone. Certifique-se de que contém exatamente 11 dígitos.\n");
            }

            if (mensagensErro.length() > 0) {
                JOptionPane.showMessageDialog(this, mensagensErro.toString(), "Erro", JOptionPane.ERROR_MESSAGE);
            } else {
                try (Connection connection = ConexaoBD.getConnection()) {
                    Representante representanteAtualizado = new Representante();
                    representanteAtualizado.setNome(nomeRepresentante);
                    representanteAtualizado.setTelefone(telefoneFormatado);
                    representanteAtualizado.setFornecedor(representante.getFornecedor());

                    RepresentanteController.atualizarRepresentante(connection, representanteAtualizado);
                    JOptionPane.showMessageDialog(this, "Representante editado com sucesso!", "Sucesso",
                            JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } catch (SQLException ex) {
                    String message = ex.getMessage();
                    if (message.contains("telefone")) {
                        JOptionPane.showMessageDialog(null, "O telefone informado já está associado a um representante existente. Por favor, utilize outro número.", "Erro",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        JPanel botoesPanel = new JPanel();
        botoesPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 30, 0));
        botoesPanel.add(cancelarButton);
        botoesPanel.add(salvarButton);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(30, 0, 10, 10);
        add(botoesPanel, gbc);

        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }
}
