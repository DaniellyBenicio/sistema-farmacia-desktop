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
import java.awt.Frame;
import java.text.ParseException;
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

public class CadastroRepresentante extends JDialog {

    private JTextField representanteField;
    private JFormattedTextField numeroField;

    public CadastroRepresentante(Frame parent, String nomeFornecedor, int idFornecedor) {
        super(parent, "Cadastro de Representante", true);

        setSize(550, 300);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 0, 10, 10);

        JLabel fornecedorLabel = new JLabel("Nome do Fornecedor:");
        JTextField fornecedorField = new JTextField(20);
        fornecedorField.setText(nomeFornecedor);
        fornecedorField.setEditable(false);

        JLabel representanteLabel = new JLabel("Nome do Representante:");
        representanteField = new JTextField(20);

        JLabel numeroLabel = new JLabel("Telefone:");
        MaskFormatter numeroFormatter = null;
        try {
            numeroFormatter = new MaskFormatter("(##) #####-####");
            numeroFormatter.setValidCharacters("0123456789");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        numeroField = new JFormattedTextField(numeroFormatter);
        numeroField.setColumns(15);

        Font font = new Font("Arial", Font.PLAIN, 14);
        fornecedorField.setFont(font);
        fornecedorField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        fornecedorField.setPreferredSize(new Dimension(400, 30));

        representanteField.setFont(font);
        representanteField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        representanteField.setPreferredSize(new Dimension(400, 30));

        numeroField.setFont(font);
        numeroField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        numeroField.setPreferredSize(new Dimension(400, 30));

        JButton cadastrarButton = new JButton("CADASTRAR");
        JButton cancelarButton = new JButton("CANCELAR");

        Font botaoFont = new Font("Arial", Font.BOLD, 14);

        cancelarButton.setFont(botaoFont);
        cancelarButton.setBackground(Color.RED);
        cancelarButton.setForeground(Color.WHITE);
        cancelarButton.setPreferredSize(new Dimension(130, 35));
        cancelarButton.setFocusPainted(false);

        cancelarButton.addActionListener(e -> dispose());

        cadastrarButton.setFont(botaoFont);
        cadastrarButton.setBackground(new Color(24, 39, 55));
        cadastrarButton.setForeground(Color.WHITE);
        cadastrarButton.setFocusPainted(false);
        cadastrarButton.setPreferredSize(new Dimension(130, 35));

        cadastrarButton.addActionListener(e -> {
            String representante = representanteField.getText().trim();
            String telefone = numeroField.getText().replaceAll("[^0-9]", "");

            if (representante.isEmpty() && telefone.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor, preencha todos os campos!", "Erro",
                        JOptionPane.ERROR_MESSAGE);
            } else if (representante.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor, informe o nome do representante.", "Erro",
                        JOptionPane.ERROR_MESSAGE);
            } else if (telefone.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor, informe o telefone.", "Erro",
                        JOptionPane.ERROR_MESSAGE);
            } else if (telefone.length() != 11) {
                JOptionPane.showMessageDialog(this, "Telefone inválido. Certifique-se de que contém 11 dígitos.",
                        "Erro", JOptionPane.ERROR_MESSAGE);
            } else if (idFornecedor <= 0) {
                JOptionPane.showMessageDialog(this, "Erro ao cadastrar fornecedor.", "Erro", JOptionPane.ERROR_MESSAGE);
            } else {
                try (Connection conn = ConexaoBD.getConnection()) {
                    RepresentanteController.cadastrarRepresentante(conn, representante, telefone, idFornecedor);
                    JOptionPane.showMessageDialog(this, "Representante cadastrado com sucesso!", "Sucesso",
                            JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Erro ao cadastrar representante.", "Erro",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

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

        JPanel botoesPanel = new JPanel();
        botoesPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 30, 0));
        botoesPanel.add(cancelarButton);
        botoesPanel.add(cadastrarButton);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(30, 0, 10, 10);
        add(botoesPanel, gbc);

        setLocationRelativeTo(parent);
        setVisible(true);
    }
}