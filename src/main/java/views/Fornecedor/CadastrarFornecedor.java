package views.Fornecedor;

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

import dao.Fornecedor.FornecedorDAO;
import dao.Funcionario.FuncionarioDAO;
import main.ConexaoBD;
import models.Fornecedor.Fornecedor;
import models.Funcionario.Funcionario;
import views.BarrasSuperiores.PainelSuperior;
import views.Representante.CadastroRepresentante;

public class CadastrarFornecedor extends JPanel {

    private JTextField nomeField;
    private JTextField cnpjField;
    private JTextField emailField;
    private JTextField telefoneField;
    private String nomedoFornecedorCadastrado;

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
        Dimension fieldSize = new Dimension(200, 40);

        JLabel nomeLabel = new JLabel("Nome/Razão Social");
        nomeLabel.setFont(labelFont);
        gbc.gridx = 0;
        gbc.gridy = 0;
        camposPanel.add(nomeLabel, gbc);

        nomeField = new JTextField();
        nomeField.setPreferredSize(new Dimension(500, 40));
        estilizarCamposFormulario(nomeField, fieldFont);
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

        cnpjField = new JFormattedTextField(cnpjFormatter);
        cnpjField.setPreferredSize(fieldSize);
        estilizarCamposFormulario(cnpjField, fieldFont);
        gbc.gridx = 1;
        gbc.gridy = 1;
        camposPanel.add(cnpjField, gbc);

        JLabel emailLabel = new JLabel("E-mail");
        emailLabel.setFont(labelFont);
        gbc.gridx = 0;
        gbc.gridy = 3;
        camposPanel.add(emailLabel, gbc);

        emailField = new JTextField();
        emailField.setPreferredSize(new Dimension(500, 40));
        estilizarCamposFormulario(emailField, fieldFont);
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
        telefoneField = new JFormattedTextField(telefoneFormatter);
        telefoneField.setPreferredSize(fieldSize);
        estilizarCamposFormulario(telefoneField, fieldFont);
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
        cancelarButton.setPreferredSize(new Dimension(140, 35));
        botoesPanel.add(cancelarButton);

        JButton cadastrarButton = new JButton("CADASTRAR");
        cadastrarButton.setFont(new Font("Arial", Font.BOLD, 17));
        cadastrarButton.setBackground(new Color(24, 39, 55));
        cadastrarButton.setForeground(Color.WHITE);
        cadastrarButton.setFocusPainted(false);
        cadastrarButton.setPreferredSize(new Dimension(140, 35));
        botoesPanel.add(cadastrarButton);

        JButton representanteButton = new JButton("REPRESENTANTE");
        representanteButton.setFont(new Font("Arial", Font.BOLD, 17));
        representanteButton.setBackground(new Color(0x3E5B94));
        representanteButton.setForeground(Color.WHITE);
        representanteButton.setEnabled(false);
        representanteButton.setFocusPainted(false);
        representanteButton.setPreferredSize(new Dimension(180, 35));
        botoesPanel.add(representanteButton);

        int idFuncionario = PainelSuperior.getIdFuncionarioAtual();

        cadastrarButton.addActionListener(e -> {
            String nome = nomeField.getText().trim();
            String cnpj = cnpjField.getText().trim();
            String email = emailField.getText().trim();
            String telefone = telefoneField.getText().trim();

            boolean hasError = false;
            StringBuilder errorMessage = new StringBuilder("Por favor, corrija os seguintes erros: \n");

            if (nome.isEmpty()) {
                errorMessage.append("- Nome/Razão Social deve ser preenchido.\n");
                hasError = true;
            } else {
                if (!nome.matches("^[\\p{L}\\s&\\-\\.,']+$")) {
                    errorMessage.append(
                            "- Nome/Razão Social deve conter apenas letras, espaços, caracteres acentuados e alguns caracteres especiais permitidos (&, -, ., ').\n");
                    hasError = true;
                }
            }

            String cnpjLimpo = cnpj.replaceAll("[^0-9]", "");
            if (cnpjLimpo.trim().isEmpty() && cnpjLimpo.length() != 14) {
                errorMessage.append("- CNPJ deve ser preenchido.\n");
                hasError = true;
            }

            if (!cnpj.trim().isEmpty() && cnpjLimpo.length() != 14) {
                errorMessage.append(
                        "- CNPJ está incorreto ou faltando dígitos (certifique-se de que contém 14 dígitos numéricos).\n");
                hasError = true;
            }

            if (email.isEmpty()) {
                errorMessage.append("- E-mail deve ser preenchido.\n");
                hasError = true;
            } else {
                if (!validarEmail(email)) {
                    errorMessage.append("- E-mail inválido.\n");
                    hasError = true;
                }
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

            if (hasError) {
                JOptionPane.showMessageDialog(null, errorMessage.toString(), "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (Connection conn = ConexaoBD.getConnection()) {
                Funcionario funcionario = FuncionarioDAO.funcionarioPorId(conn, idFuncionario);

                if (funcionario == null) {
                    JOptionPane.showMessageDialog(null,
                            "A identificação do funcionário é obrigatória.\n" +
                                    "Somente o gerente pode cadastrar os fornecedores.",
                            "Acesso Negado",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                Fornecedor fornecedor = new Fornecedor(nome, cnpj, email, telefone, funcionario);
                FornecedorDAO.cadastrarFornecedor(conn, fornecedor);

                int idFornecedor = fornecedor.getId();
                JOptionPane.showMessageDialog(null, "Fornecedor cadastrado com sucesso!", "Sucesso",
                        JOptionPane.INFORMATION_MESSAGE);
                representanteButton.setEnabled(true);

                nomedoFornecedorCadastrado = nome;

                representanteButton.addActionListener(event -> {
                    new CadastroRepresentante(null, nome, idFornecedor);
                });

                nomeField.setText("");
                cnpjField.setText("");
                emailField.setText("");
                telefoneField.setText("");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Erro ao cadastrar fornecedor: " + ex.getMessage(), "Erro",
                        JOptionPane.ERROR_MESSAGE);
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

    public String getNomeFornecedorCadastrado() {
        return nomedoFornecedorCadastrado;
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