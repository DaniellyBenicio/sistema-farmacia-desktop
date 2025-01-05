package com.mycompany.farmacia.views;

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
import java.text.ParseException;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.text.MaskFormatter;

import dao.FuncionarioDAO;
import main.ConexaoBD;
import models.Cargo;
import models.Funcionario;

public class CadastrarFuncionario extends JPanel {

    private JTextField nomeField;
    private JTextField emailField;
    private JFormattedTextField telefoneField;
    private JTextField cargoField;

    public CadastrarFuncionario() {
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
    }

    private JLabel criarTitulo() {
        JLabel titulo = new JLabel("CADASTRO DE FUNCIONÁRIO");
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        return titulo;
    }

    private JPanel criarCamposPanel() {
        JPanel camposPanel = new JPanel();
        camposPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 30, 0, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        Font labelFont = new Font("Arial", Font.BOLD, 18);
        Font fieldFont = new Font("Arial", Font.PLAIN, 18);
        Dimension fieldSize = new Dimension(300, 40);

        // Nome
        JLabel nomeLabel = new JLabel("Nome");
        nomeLabel.setFont(labelFont);
        gbc.gridx = 0;
        gbc.gridy = 0;
        camposPanel.add(nomeLabel, gbc);

        nomeField = new JTextField();
        nomeField.setPreferredSize(new Dimension(600, 40));
        estilizarCampo(nomeField, fieldFont);
        gbc.gridx = 0;
        gbc.gridy = 1;
        camposPanel.add(nomeField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        camposPanel.add(Box.createRigidArea(new Dimension(0, 15)), gbc);  // Espaço entre os campos

        // Telefone
        JLabel telefoneLabel = new JLabel("Telefone");
        telefoneLabel.setFont(labelFont);
        gbc.gridx = 1;  // Segunda coluna
        gbc.gridy = 0;
        camposPanel.add(telefoneLabel, gbc);

        telefoneField = criarCampoFormatado("(##) #####-####");
        telefoneField.setPreferredSize(fieldSize);
        estilizarCampo(telefoneField, fieldFont);
        gbc.gridx = 1;  // Segunda coluna
        gbc.gridy = 1;
        camposPanel.add(telefoneField, gbc);

        // E-mail
        JLabel emailLabel = new JLabel("E-mail");
        emailLabel.setFont(labelFont);
        gbc.gridx = 0;  // Primeira coluna
        gbc.gridy = 3;  // Segunda linha (abaixo do nome)
        camposPanel.add(emailLabel, gbc);

        emailField = new JTextField();
        emailField.setPreferredSize(new Dimension(300, 40));
        estilizarCampo(emailField, fieldFont);
        gbc.gridx = 0;  // Primeira coluna
        gbc.gridy = 4;  // Para o campo E-mail
        camposPanel.add(emailField, gbc);

        // Cargo
        JLabel cargoLabel = new JLabel("Cargo");
        cargoLabel.setFont(labelFont);
        gbc.gridx = 1;  // Segunda coluna (ao lado do E-mail)
        gbc.gridy = 3;  // Mesma linha que E-mail
        camposPanel.add(cargoLabel, gbc);

        cargoField = new JTextField();
        cargoField.setPreferredSize(fieldSize);
        estilizarCampo(cargoField, fieldFont);
        gbc.gridx = 1;  // Segunda coluna (ao lado do E-mail)
        gbc.gridy = 4;  // Mesma linha que o campo E-mail
        camposPanel.add(cargoField, gbc);

        return camposPanel;
    }

    private JPanel criarBotoesPanel() {
        JPanel botoesPanel = new JPanel();
        botoesPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 40, 20));

        JButton cancelarButton = new JButton("CANCELAR");
        cancelarButton.setFont(new Font("Arial", Font.BOLD, 17));
        cancelarButton.setBackground(Color.RED);
        cancelarButton.setForeground(Color.WHITE);
        cancelarButton.setFocusPainted(false);
        cancelarButton.setPreferredSize(new Dimension(150, 40));
        botoesPanel.add(cancelarButton);

        JButton cadastrarButton = new JButton("CADASTRAR");
        cadastrarButton.setFont(new Font("Arial", Font.BOLD, 17));
        cadastrarButton.setBackground(new Color(24, 39, 55));
        cadastrarButton.setForeground(Color.WHITE);
        cadastrarButton.setFocusPainted(false);
        cadastrarButton.setPreferredSize(new Dimension(150, 40));
        botoesPanel.add(cadastrarButton);

        cadastrarButton.addActionListener(e -> {
            String nome = nomeField.getText().trim();
            String telefone = telefoneField.getText().trim();
            String email = emailField.getText().trim();
            String cargoNome = cargoField.getText().trim();

            // Verifica se todos os campos estão vazios
            if (nome.isEmpty() || telefone.isEmpty() || email.isEmpty() || cargoNome.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Por favor, preencha todos os campos!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Validações
            boolean hasError = false;
            StringBuilder errorMessage = new StringBuilder("Por favor, corrija os seguintes erros: \n");

            // Verificação do E-mail
            if (!validarEmail(email)) {
                errorMessage.append("- E-mail inválido.\n");
                hasError = true;
            }

            // Verificação do Telefone
            String telefoneLimpo = telefone.replaceAll("[^0-9]", ""); // Remove a máscara para validação
            if (telefoneLimpo.length() != 11) { // Telefone deve ter 11 dígitos (incluindo DDD e o número)
                errorMessage.append("- Telefone inválido (certifique-se de que contém 11 dígitos numéricos).\n");
                hasError = true;
            }

            // Se houve algum erro, exibe a mensagem de erro acumulada
            if (hasError) {
                JOptionPane.showMessageDialog(null, errorMessage.toString(), "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (Connection conn = ConexaoBD.getConnection()) {
                Cargo cargo = new Cargo();
                cargo.setNome(cargoNome); // Adicione lógica para lidar com Cargos

                Funcionario funcionario = new Funcionario(nome, telefone, email, cargo);
                FuncionarioDAO.cadastrarFuncionario(conn, funcionario);
                
                JOptionPane.showMessageDialog(null, "Funcionário cadastrado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);

                // Limpar os campos após o cadastro
                nomeField.setText("");
                telefoneField.setText("");
                emailField.setText("");
                cargoField.setText("");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Erro ao cadastrar funcionário: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelarButton.addActionListener(e -> {
            SwingUtilities.getWindowAncestor(this).dispose();
        });

        return botoesPanel;
    }

    private boolean validarEmail(String email) {
        String emailRegex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        return Pattern.matches(emailRegex, email);
    }

    private JFormattedTextField criarCampoFormatado(String formato) {
        MaskFormatter formatter = null;
        try {
            formatter = new MaskFormatter(formato);
            formatter.setValidCharacters("0123456789");
            formatter.setPlaceholderCharacter('_');
        } catch (ParseException e) {
            e.printStackTrace();
        }
        
        return new JFormattedTextField(formatter);
    }

    private void estilizarCampo(JComponent campo, Font font) {
        campo.setBackground(Color.WHITE);
        campo.setForeground(Color.BLACK);
        campo.setFont(font);
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 0, 0), 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
    }
}