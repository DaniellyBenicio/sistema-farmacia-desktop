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

import dao.FornecedorDAO;
import dao.FuncionarioDAO;
import main.ConexaoBD;
import models.Fornecedor;
import models.Funcionario;

public class CadastrarFornecedor extends JPanel {

    private JTextField nomeField;
    private JTextField cnpjField;
    private JTextField emailField;
    private JTextField telefoneField;
  
    public CadastrarFornecedor() {
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
        JLabel titulo = new JLabel("CADASTRO DE FORNECEDOR");
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

        JLabel nomeLabel = new JLabel("Nome/Razão Social");
        nomeLabel.setFont(labelFont);
        gbc.gridx = 0;
        gbc.gridy = 0;
        camposPanel.add(nomeLabel, gbc);

        nomeField = new JTextField();  // Usando a variável de classe
        nomeField.setPreferredSize(new Dimension(600, 40));
        estilizarCampo(nomeField, fieldFont);
        gbc.gridx = 0;
        gbc.gridy = 1;
        camposPanel.add(nomeField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        camposPanel.add(Box.createRigidArea(new Dimension(0, 30)), gbc);

        JLabel cnpjLabel = new JLabel("CNPJ");
        cnpjLabel.setFont(labelFont);
        gbc.gridx = 1;
        gbc.gridy = 0;
        camposPanel.add(cnpjLabel, gbc);

        MaskFormatter cnpjFormatter = null;
        try {
            cnpjFormatter = new MaskFormatter("##.###.###/####-##");
            cnpjFormatter.setValidCharacters("0123456789");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        cnpjField = new JFormattedTextField(cnpjFormatter);  // Usando a variável de classe
        cnpjField.setPreferredSize(fieldSize);
        estilizarCampo(cnpjField, fieldFont);
        gbc.gridx = 1;
        gbc.gridy = 1;
        camposPanel.add(cnpjField, gbc);

        JLabel emailLabel = new JLabel("E-mail");
        emailLabel.setFont(labelFont);
        gbc.gridx = 0;
        gbc.gridy = 3;
        camposPanel.add(emailLabel, gbc);

        emailField = new JTextField();  // Usando a variável de classe
        emailField.setPreferredSize(new Dimension(300, 40));
        estilizarCampo(emailField, fieldFont);
        gbc.gridx = 0;
        gbc.gridy = 4;
        camposPanel.add(emailField, gbc);

        JLabel telefoneLabel = new JLabel("Telefone");
        telefoneLabel.setFont(labelFont);
        gbc.gridx = 1;
        gbc.gridy = 3;
        camposPanel.add(telefoneLabel, gbc);

        MaskFormatter telefoneFormatter = null;
        try {
            telefoneFormatter = new MaskFormatter("(##) #####-####");
            telefoneFormatter.setValidCharacters("0123456789");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        telefoneField = new JFormattedTextField(telefoneFormatter);  // Usando a variável de classe
        telefoneField.setPreferredSize(fieldSize);
        estilizarCampo(telefoneField, fieldFont);
        gbc.gridx = 1;
        gbc.gridy = 4;
        camposPanel.add(telefoneField, gbc);

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
    
        JButton representanteButton = new JButton("REPRESENTANTE");
        representanteButton.setFont(new Font("Arial", Font.BOLD, 17));
        representanteButton.setBackground(new Color(0x3E5B94));
        representanteButton.setForeground(Color.WHITE);
        representanteButton.setEnabled(false);  // Inicialmente desabilitado
        representanteButton.setFocusPainted(false);
        representanteButton.setPreferredSize(new Dimension(180, 40));
        botoesPanel.add(representanteButton);

       int idFuncionario = PainelSuperior.getIdFuncionarioAtual();

        cadastrarButton.addActionListener(e -> {
        String nome = nomeField.getText().trim();
        String cnpj = cnpjField.getText().trim();
        String email = emailField.getText().trim();
        String telefone = telefoneField.getText().trim();

        // Verifica se todos os campos estão vazios
        if (nome.isEmpty() && cnpj.isEmpty() && email.isEmpty() && telefone.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Por favor, preencha todos os campos!", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Inicializa uma variável para rastrear se houve erros
        boolean hasError = false;
        StringBuilder errorMessage = new StringBuilder("Por favor, corrija os seguintes erros: \n");

        // Validações
        if (nome.isEmpty()) {
            errorMessage.append("- Nome/Razão Social deve ser preenchido.\n");
            hasError = true;
        }

        // Verificação do CNPJ
        if (cnpj.isEmpty()) {
            errorMessage.append("- CNPJ deve ser preenchido.\n");
            hasError = true;
            System.out.println("1");
            System.out.println(cnpj);
        } else {
            String cnpjLimpo = cnpj.replaceAll("[^0-9]", ""); // Remove a máscara para validação
            System.out.println("2");
            System.out.println(cnpjLimpo);
            if (cnpjLimpo.length() != 14) {
                errorMessage.append("- CNPJ inválido (certifique-se de que contém 14 dígitos numéricos).\n");
                System.out.println("3");
                System.out.println(cnpj);
                hasError = true;
            }
        }

        // Verificação do e-mail
        if (email.isEmpty()) {
            errorMessage.append("- E-mail deve ser preenchido.\n");
            hasError = true;
        } else if (!validarEmail(email)) {
            errorMessage.append("- E-mail inválido.\n");
            hasError = true;
        }

        // Verificação do telefone
        if (telefone.isEmpty()) {
            errorMessage.append("- Telefone deve ser preenchido.\n");
            hasError = true;
        } else {
            String telefoneLimpo = telefone.replaceAll("[^0-9]", ""); // Remove a máscara para validação
            if (telefoneLimpo.length() != 11) { // Telefone deve ter 11 dígitos (incluindo DDD e o número)
                errorMessage.append("- Telefone inválido (certifique-se de que contém 11 dígitos numéricos).\n");
                hasError = true;
            }
        }

        // Se houve algum erro, exibe a mensagem de erro acumulada
        if (hasError) {
            JOptionPane.showMessageDialog(null, errorMessage.toString(), "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = ConexaoBD.getConnection()) {
            Funcionario funcionario = FuncionarioDAO.funcionarioPorId(conn, idFuncionario);

            if (funcionario == null) {
                JOptionPane.showMessageDialog(null, 
                "A identificação do funcionário é obrigatória.\n" +
                "Por favor, identifique-se antes de cadastrar um fornecedor.", 
                "Erro", 
                JOptionPane.ERROR_MESSAGE);
                return; 
            }

            Fornecedor fornecedor = new Fornecedor(nome, cnpj, email, telefone, funcionario);
            System.out.println("CNPJ após cadastro: " + cnpj);
            FornecedorDAO.cadastrarFornecedor(conn, fornecedor);

            int idFornecedor = fornecedor.getId();
            System.out.println("1- ID do Fornecedor após cadastro: " + idFornecedor);

            JOptionPane.showMessageDialog(null, "Fornecedor cadastrado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            representanteButton.setEnabled(true);  // Habilita o botão REPRESENTANTE

            representanteButton.addActionListener(event -> {
                new CadastroRepresentante(null, nome, idFornecedor);
            });

            // Limpar os campos após o cadastro
            nomeField.setText("");
            cnpjField.setText("");
            emailField.setText("");
            telefoneField.setText("");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Erro ao cadastrar fornecedor: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
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

    private void estilizarCampo(JComponent campo, Font font) {
        campo.setBackground(Color.WHITE);
        campo.setForeground(Color.BLACK);
        campo.setFont(font);
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 0, 0), 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
    }
}
