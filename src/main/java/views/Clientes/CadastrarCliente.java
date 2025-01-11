package views.Clientes;

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
import java.util.Arrays;
import java.util.List;

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

import dao.Cliente.ClienteDAO;
import dao.Funcionario.FuncionarioDAO;
import main.ConexaoBD;
import models.Cliente.Cliente;
import models.Funcionario.Funcionario;
import views.BarrasSuperiores.PainelSuperior;

public class CadastrarCliente extends JPanel {

    private JTextField nomeField;
    private JFormattedTextField cpfField;
    private JFormattedTextField telefoneField;
    private JTextField ruaField;
    private JTextField numeroField;
    private JTextField bairroField;
    private JTextField cidadeField;
    private JTextField estadoField;
    private JTextField pontodereferenciaField;

    public CadastrarCliente() {
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
        JLabel titulo = new JLabel("CADASTRO DE CLIENTE");
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        return titulo;
    }

    private JPanel criarCamposPanel() {
        JPanel camposPanel = new JPanel();
        camposPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 30, 0, 10);
        gbc.anchor = GridBagConstraints.WEST;

        Font labelFont = new Font("Arial", Font.BOLD, 18);
        Font fieldFont = new Font("Arial", Font.PLAIN, 18);

        JLabel nomeLabel = new JLabel("Nome");
        nomeLabel.setFont(labelFont);
        gbc.gridx = 0;
        gbc.gridy = 0;
        camposPanel.add(nomeLabel, gbc);

        nomeField = new JTextField();
        nomeField.setPreferredSize(new Dimension(480, 40));
        estilizarCamposFormulario(nomeField, fieldFont);
        gbc.gridx = 0;
        gbc.gridy = 1;
        camposPanel.add(nomeField, gbc);

        JLabel cpfLabel = new JLabel("CPF");
        cpfLabel.setFont(labelFont);
        gbc.gridx = 1;
        gbc.gridy = 0;
        camposPanel.add(cpfLabel, gbc);

        try {
            MaskFormatter cpfFormatter = new MaskFormatter("###.###.###-##");
            cpfField = new JFormattedTextField(cpfFormatter);
        } catch (Exception e) {
            e.printStackTrace();
        }
        cpfField.setPreferredSize(new Dimension(200, 40));
        estilizarCamposFormulario(cpfField, fieldFont);
        gbc.gridx = 1;
        gbc.gridy = 1;
        camposPanel.add(cpfField, gbc);

        JLabel telefoneLabel = new JLabel("Telefone");
        telefoneLabel.setFont(labelFont);
        gbc.gridx = 2;
        gbc.gridy = 0;
        camposPanel.add(telefoneLabel, gbc);

        try {
            MaskFormatter telefoneFormatter = new MaskFormatter("(##) #####-####");
            telefoneField = new JFormattedTextField(telefoneFormatter);
        } catch (Exception e) {
            e.printStackTrace();
        }
        telefoneField.setPreferredSize(new Dimension(200, 40));
        estilizarCamposFormulario(telefoneField, fieldFont);
        gbc.gridx = 2;
        gbc.gridy = 1;
        camposPanel.add(telefoneField, gbc);

        JLabel ruaLabel = new JLabel("Rua");
        ruaLabel.setFont(labelFont);
        gbc.gridx = 0;
        gbc.gridy = 2;
        camposPanel.add(ruaLabel, gbc);

        ruaField = new JTextField();
        ruaField.setPreferredSize(new Dimension(480, 40));
        estilizarCamposFormulario(ruaField, fieldFont);
        gbc.gridx = 0;
        gbc.gridy = 3;
        camposPanel.add(ruaField, gbc);

        JLabel numeroLabel = new JLabel("Número");
        numeroLabel.setFont(labelFont);
        gbc.gridx = 1;
        gbc.gridy = 2;
        camposPanel.add(numeroLabel, gbc);

        numeroField = new JTextField();
        numeroField.setPreferredSize(new Dimension(200, 40));
        estilizarCamposFormulario(numeroField, fieldFont);
        gbc.gridx = 1;
        gbc.gridy = 3;
        camposPanel.add(numeroField, gbc);

        JLabel bairroLabel = new JLabel("Bairro");
        bairroLabel.setFont(labelFont);
        gbc.gridx = 2;
        gbc.gridy = 2;
        camposPanel.add(bairroLabel, gbc);

        bairroField = new JTextField();
        bairroField.setPreferredSize(new Dimension(200, 40));
        estilizarCamposFormulario(bairroField, fieldFont);
        gbc.gridx = 2;
        gbc.gridy = 3;
        camposPanel.add(bairroField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4; 
        JLabel cidadeLabel = new JLabel("Cidade");
        cidadeLabel.setFont(labelFont);
        camposPanel.add(cidadeLabel, gbc);

        cidadeField = new JTextField();
        cidadeField.setPreferredSize(new Dimension(180, 40));
        estilizarCamposFormulario(cidadeField, fieldFont);
        gbc.gridx = 0;
        gbc.gridy = 5;
        camposPanel.add(cidadeField, gbc);

        JLabel estadoLabel = new JLabel("Estado");
        estadoLabel.setFont(labelFont);
        gbc.gridx = 1; 
        gbc.gridy = 4; 
        camposPanel.add(estadoLabel, gbc);

        estadoField = new JTextField();
        estadoField.setPreferredSize(new Dimension(100, 40));
        estilizarCamposFormulario(estadoField, fieldFont);
        gbc.gridx = 1;
        gbc.gridy = 5; 
        camposPanel.add(estadoField, gbc);

        gbc.gridx = 2; 
        gbc.gridy = 4; 
        JLabel pontoReferenciaLabel = new JLabel("Ponto de Referência");
        pontoReferenciaLabel.setFont(labelFont);
        camposPanel.add(pontoReferenciaLabel, gbc);

        pontodereferenciaField = new JTextField();
        pontodereferenciaField.setPreferredSize(new Dimension(200, 40)); 
        estilizarCamposFormulario(pontodereferenciaField, fieldFont);
        gbc.gridx = 2;
        gbc.gridy = 5; 
        camposPanel.add(pontodereferenciaField, gbc);

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
        cancelarButton.setPreferredSize(new Dimension(140, 35));
        botoesPanel.add(cancelarButton);

        JButton cadastrarButton = new JButton("CADASTRAR");
        cadastrarButton.setFont(new Font("Arial", Font.BOLD, 17));
        cadastrarButton.setBackground(new Color(24, 39, 55));
        cadastrarButton.setForeground(Color.WHITE);
        cadastrarButton.setFocusPainted(false);
        cadastrarButton.setPreferredSize(new Dimension(140, 35));
        botoesPanel.add(cadastrarButton);

        int idFuncionario = PainelSuperior.getIdFuncionarioAtual();

        cadastrarButton.addActionListener(e -> {
            String nome = nomeField.getText().trim();
            String cpf = cpfField.getText().trim();
            String telefone = telefoneField.getText().trim();
            String rua = ruaField.getText().trim();
            String numero = numeroField.getText().trim();
            String bairro = bairroField.getText().trim();
            String cidade = cidadeField.getText().trim();
            String estado = estadoField.getText().trim();
            String pontodereferencia = pontodereferenciaField.getText().trim();

            boolean hasError = false;
            StringBuilder errorMessage = new StringBuilder("Por favor, corrija os seguintes erros: \n");

            if (nome.isEmpty()) {
                errorMessage.append("- Nome deve ser preenchido.\n");
                hasError = true;
            } else if (!nome.matches("[A-Za-zÀ-ÿ\\s]+")) {  
                errorMessage.append("- Nome não pode conter números ou caracteres especiais.\n");
                hasError = true;
            }
            
            if (cpf.isEmpty()) {
                errorMessage.append("- CPF deve ser preenchido.\n");
                hasError = true;
            }

            String telefoneLimpo = telefone.replaceAll("[^0-9]", "");
            if (telefoneLimpo.isEmpty()) {
                errorMessage.append("- Telefone deve ser preenchido.\n");
                hasError = true;
            } else {
                if (telefoneLimpo.length() != 11) {
                    errorMessage.append("- Telefone inválido (certifique-se de que contém 11 dígitos numéricos).\n");
                    hasError = true;
                }
            }

            if (rua == null || rua.trim().isEmpty()) {
                errorMessage.append("- Rua deve ser preenchida.\n");
                hasError = true;
            } else if (rua.matches("[0-9]+")) { 
                errorMessage.append("- Rua inválida (não pode conter apenas números).\n");
                hasError = true;
            }         

            if (numero.isEmpty()) {
                errorMessage.append("- Número deve ser preenchido.\n");
                hasError = true;
            }

            if (bairro.isEmpty()) {
                errorMessage.append("- Bairro deve ser preenchido.\n");
                hasError = true;
            } else if (bairro.matches(".*\\d.*")) { 
                errorMessage.append("- Bairro inválido (não pode conter números).\n");
                hasError = true;
            }

            if (cidade.isEmpty()) {
                errorMessage.append("- Cidade deve ser preenchida.\n");
                hasError = true;
            } else if (cidade.matches(".*\\d.*")) { 
                errorMessage.append("- Cidade inválida (não pode conter números).\n");
                hasError = true;
            }

            List<String> estadosValidos = Arrays.asList("AC", "AL", "AM", "BA", "CE", "DF", "ES", "GO", "MA", "MT", 
                                            "MS", "MG", "PA", "PB", "PR", "PE", "PI", "RJ", "RN", "RS", 
                                            "RO", "RR", "SC", "SP", "SE", "TO");

            if (estado == null || estado.isEmpty() || !estadosValidos.contains(estado)) {
                errorMessage.append("- Estado deve ser preenchido e válido. Escolha uma sigla de estado válida.\n");
                hasError = true;
            }

            if (pontodereferencia.isEmpty()) {
                errorMessage.append("- Ponto de Referência deve ser preenchido.\n");
                hasError = true;
            }

            if (hasError) {
                JOptionPane.showMessageDialog(null, errorMessage.toString(), "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

              try (Connection conn = ConexaoBD.getConnection()) {
                    Funcionario funcionario = FuncionarioDAO.funcionarioPorId(conn, idFuncionario);

                    if (funcionario == null) {
                        JOptionPane.showMessageDialog(null,
                                "A identificação do funcionário é obrigatória.\n" +
                                        "",
                                "Acesso Negado",
                                JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                Cliente cliente = new Cliente(nome, cpf, telefone, rua, numero, bairro, cidade, estado, pontodereferencia, funcionario);

                ClienteDAO.cadastrarCliente(conn, cliente);

                JOptionPane.showMessageDialog(null, "Cliente cadastrado com sucesso!", "Sucesso",
                        JOptionPane.INFORMATION_MESSAGE);
                nomeField.setText("");
                cpfField.setText("");
                telefoneField.setText("");
                ruaField.setText("");
                numeroField.setText("");
                bairroField.setText("");
                cidadeField.setText("");
                estadoField.setText("");
                pontodereferenciaField.setText("");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Erro ao cadastrar cliente: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelarButton.addActionListener(e -> {
            SwingUtilities.getWindowAncestor(this).dispose();
        });

        return botoesPanel;
    }

    private void estilizarCamposFormulario(JComponent campo, Font font) {
        campo.setBackground(Color.WHITE);
        campo.setForeground(Color.BLACK);
        campo.setFont(font);
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 0, 0), 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
    }
}