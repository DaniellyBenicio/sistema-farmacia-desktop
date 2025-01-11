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
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.text.MaskFormatter;

import dao.Cliente.ClienteDAO;
import main.ConexaoBD;
import models.Cliente.Cliente;

public class EditarCliente extends JPanel {

    private JTextField nomeField;
    private JFormattedTextField cpfField;
    private JFormattedTextField telefoneField;
    private JTextField ruaField;
    private JTextField numeroField;
    private JTextField bairroField;
    private JTextField cidadeField;
    private JComboBox<String> estadoComboBox;
    private JTextField pontodereferenciaField;
    private int clienteId;

    public EditarCliente(int clienteId) {
        this.clienteId = clienteId;

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

        carregarDadosCliente();
    }

    private JLabel criarTitulo() {
        JLabel titulo = new JLabel("EDIÇÃO DE CLIENTE");
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
        cidadeField.setPreferredSize(new Dimension(480, 40));
        estilizarCamposFormulario(cidadeField, fieldFont);
        gbc.gridx = 0;
        gbc.gridy = 5;
        camposPanel.add(cidadeField, gbc);

        List<String> estadosValidos = Arrays.asList("AC", "AL", "AM", "BA", "CE", "DF", "ES", "GO",
                "MA", "MT", "MS", "MG", "PA", "PB", "PR", "PE",
                "PI", "RJ", "RN", "RS", "RO", "RR", "SC", "SP",
                "SE", "TO");
        String[] estadosArray = estadosValidos.toArray(new String[0]);

        JLabel estadoLabel = new JLabel("Estado");
        estadoLabel.setFont(labelFont);
        gbc.gridx = 1;
        gbc.gridy = 4;
        camposPanel.add(estadoLabel, gbc);

        estadoComboBox = new JComboBox<>(estadosArray);
        estadoComboBox.setPreferredSize(new Dimension(200, 40));
        estadoComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (isSelected) {
                    component.setBackground(new Color(24, 39, 55));
                    component.setForeground(Color.WHITE);
                } else {
                    component.setBackground(Color.WHITE);
                    component.setForeground(Color.BLACK);
                }
                return component;
            }
        });
        estadoComboBox.setFont(fieldFont);
        gbc.gridx = 1;
        gbc.gridy = 5;
        camposPanel.add(estadoComboBox, gbc);

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
        cancelarButton.setPreferredSize(new Dimension(130, 35));
        cancelarButton.addActionListener(e -> {
            SwingUtilities.getWindowAncestor(this).dispose();
        });
        botoesPanel.add(cancelarButton);

        JButton salvarButton = new JButton("SALVAR");
        salvarButton.setFont(new Font("Arial", Font.BOLD, 17));
        salvarButton.setBackground(new Color(24, 39, 55));
        salvarButton.setForeground(Color.WHITE);
        salvarButton.setFocusPainted(false);
        salvarButton.setPreferredSize(new Dimension(130, 35));
        salvarButton.addActionListener(e -> salvarCliente(clienteId));
        botoesPanel.add(salvarButton);

        return botoesPanel;
    }

    private void carregarDadosCliente() {
        try (Connection conn = ConexaoBD.getConnection()) {
            Cliente cliente = ClienteDAO.clientePorID(conn, clienteId);
            if (cliente != null) {
                nomeField.setText(cliente.getNome());
                cpfField.setText(cliente.getCpf());
                telefoneField.setText(cliente.getTelefone());
                ruaField.setText(cliente.getRua());
                numeroField.setText(cliente.getNumCasa());
                bairroField.setText(cliente.getBairro());
                cidadeField.setText(cliente.getCidade());
                estadoComboBox.setToolTipText(cliente.getEstado());
            } else {
                JOptionPane.showMessageDialog(null, "Cliente não encontrado!", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao carregar dados do cliente.", "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void salvarCliente(int idCliente) {
        String nome = nomeField.getText().trim();
        String cpf = cpfField.getText().trim();
     //   String email = emailField.getText().trim();
        String telefone = telefoneField.getText().trim();

        boolean hasError = false;
        StringBuilder errorMessage = new StringBuilder("Por favor, corrija os seguintes erros: \n");

        if (nome.isEmpty()) {
            errorMessage.append("- Nome deve ser preenchido.\n");
            hasError = true;
        }

        String cpfLimpo = cpf.replaceAll("[^0-9]", "");
        if (cpfLimpo.isEmpty() || cpfLimpo.length() != 11) {
            errorMessage.append("- CPF deve ser preenchido e conter exatamente 11 dígitos numéricos.\n");
            hasError = true;
        }

       
        String telefoneLimpo = telefone.replaceAll("[^0-9]", "");
        if (telefoneLimpo.isEmpty() || telefoneLimpo.length() != 11) {
            errorMessage.append("- Telefone deve conter 11 dígitos numéricos.\n");
            hasError = true;
        }

        if (hasError) {
            JOptionPane.showMessageDialog(null, errorMessage.toString(), "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = ConexaoBD.getConnection()) {
            Cliente clienteExistente = ClienteDAO.clientePorID(conn, idCliente);
            if (clienteExistente == null) {
                JOptionPane.showMessageDialog(null, "Cliente não encontrado.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            clienteExistente.setNome(nome);
            clienteExistente.setCpf(cpfLimpo);
            
            clienteExistente.setTelefone(telefoneLimpo);

            ClienteDAO.atualizarCliente(conn, clienteExistente);
            JOptionPane.showMessageDialog(null, "Cliente atualizado com sucesso!", "Sucesso",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao atualizar cliente: " + e.getMessage(), "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
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