package com.mycompany.farmacia.views;

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
import controllers.RepresentanteController;
import main.ConexaoBD;
import models.Representante;

public class EditarRepresentante extends JDialog {
    private JTextField representanteField;
    private JFormattedTextField numeroField;
    private Representante representante;

    public EditarRepresentante(java.awt.Frame parent, Representante representante, Connection conn) {
        super(parent, "Editar Representante", true);
        this.representante = representante;

        setSize(500, 300);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 0, 10, 10);

        // Label e campo para o nome do fornecedor
        JLabel fornecedorLabel = new JLabel("Nome do Fornecedor:");
        JTextField fornecedorField = new JTextField(20);
        if (representante.getFornecedor() != null) {
            fornecedorField.setText(representante.getFornecedor().getNome());
        } else {
            fornecedorField.setText("Fornecedor não disponível"); 
        }
        fornecedorField.setEditable(false);
        
        // Label e campo para o nome do representante
        JLabel representanteLabel = new JLabel("Nome do Representante:");
        representanteField = new JTextField(20);
        representanteField.setText(representante.getNome());

        // Label e campo para o telefone
        JLabel numeroLabel = new JLabel("Telefone:");
        MaskFormatter numeroFormatter;
        try {
            numeroFormatter = new MaskFormatter("(##) #####-####");
            numeroFormatter.setValidCharacters("0123456789");
        } catch (java.text.ParseException e) {
            e.printStackTrace();
            return; // Saia se a máscara não puder ser criada
        }

        numeroField = new JFormattedTextField(numeroFormatter);
        numeroField.setText(representante.getTelefone());
        numeroField.setColumns(15);

        Font font = new Font("Arial", Font.PLAIN, 16);
        fornecedorField.setFont(font);
        representanteField.setFont(font);
        numeroField.setFont(font);
        
        // Configurando os campos
        for (JTextField field : new JTextField[]{fornecedorField, representanteField, numeroField}) {
            field.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            field.setPreferredSize(new Dimension(230, 30));
        }

        // Adiciona componentes ao layout
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

        // Botões
        JButton salvarButton = new JButton("SALVAR");
        JButton cancelarButton = new JButton("CANCELAR");

        Font botaoFont = new Font("Arial", Font.BOLD, 16);

        // Configuração do botão cancelar
        cancelarButton.setFont(botaoFont);
        cancelarButton.setBackground(Color.RED);
        cancelarButton.setForeground(Color.WHITE);
        cancelarButton.setPreferredSize(new Dimension(150, 40));
        cancelarButton.setFocusPainted(false);
        cancelarButton.addActionListener(e -> dispose()); // Fechar o dialog

        // Configuração do botão salvar
        salvarButton.setFont(botaoFont);
        salvarButton.setBackground(new Color(24, 39, 55));
        salvarButton.setForeground(Color.WHITE);
        salvarButton.setFocusPainted(false);
        salvarButton.setPreferredSize(new Dimension(150, 40));
        
        salvarButton.addActionListener(e -> {
            String nomeRepresentante = representanteField.getText().trim();
            String telefoneFormatado = numeroField.getText().replaceAll("[^0-9]", "");

            // Validação dos campos
            if (nomeRepresentante.isEmpty() || telefoneFormatado.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor, preencha todos os campos!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Valida telefone
            if (telefoneFormatado.length() != 11) {
                JOptionPane.showMessageDialog(this, "Telefone inválido. Certifique-se de que contém 11 dígitos.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Atualiza o representante
            try (Connection connection = ConexaoBD.getConnection()) {
                Representante representanteAtualizado = new Representante();
                representanteAtualizado.setNome(nomeRepresentante);
                representanteAtualizado.setTelefone(telefoneFormatado);
                representanteAtualizado.setFornecedor(representante.getFornecedor());

                // Atualizar no banco de dados
                RepresentanteController.atualizarRepresentante(connection, representanteAtualizado);
                JOptionPane.showMessageDialog(this, "Representante editado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                
                dispose(); // Fecha o JDialog
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Erro ao editar representante: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Layout do painel de botões
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
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE); // Certifique-se que o fechamento comporte-se corretamente
    }
}