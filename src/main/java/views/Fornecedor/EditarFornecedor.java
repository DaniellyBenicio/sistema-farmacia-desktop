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
import java.util.List;
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
import dao.Representante.RepresentanteDAO;
import main.ConexaoBD;
import models.Fornecedor.Fornecedor;
import models.Representante.Representante;
import views.Representante.CadastroRepresentante;
import views.Representante.EditarRepresentante;

public class EditarFornecedor extends JPanel {

    private JTextField nomeField;
    private JTextField cnpjField;
    private JTextField emailField;
    private JTextField telefoneField;
    private int fornecedorId;

    public EditarFornecedor(int fornecedorId) {
        this.fornecedorId = fornecedorId;

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

        carregarDadosFornecedor();
    }

    private JLabel criarTitulo() {
        JLabel titulo = new JLabel("EDIÇÃO DE FORNECEDOR");
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
        salvarButton.addActionListener(e -> salvarFornecedor(fornecedorId));
        botoesPanel.add(salvarButton);

        JButton representanteButton = new JButton("REPRESENTANTE");
        representanteButton.setFont(new Font("Arial", Font.BOLD, 17));
        representanteButton.setBackground(new Color(0x3E5B94));
        representanteButton.setForeground(Color.WHITE);
        representanteButton.setEnabled(true);
        representanteButton.setFocusPainted(false);
        representanteButton.setPreferredSize(new Dimension(180, 35));
        botoesPanel.add(representanteButton);

        representanteButton.addActionListener(e -> {
            try (Connection conn = ConexaoBD.getConnection()) {
                List<Representante> representantes = RepresentanteDAO.listarRepresentantesPorFornecedorId(conn,
                        fornecedorId);
                String nomeFornecedor = FornecedorDAO.getNomeFornecedorPorId(conn, fornecedorId);

                if (representantes == null || representantes.isEmpty()) {
                    CadastroRepresentante cadastroRepresentante = new CadastroRepresentante(null, nomeFornecedor,
                            fornecedorId);
                    cadastroRepresentante.setVisible(false);
                } else {
                    Representante representante = representantes.get(0);
                    EditarRepresentante editarRepresentante = new EditarRepresentante(null, representante, conn);
                    editarRepresentante.setVisible(true);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Erro ao buscar dados do representante.", "Erro",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        return botoesPanel;
    }

    private void carregarDadosFornecedor() {
        try (Connection conn = ConexaoBD.getConnection()) {
            Fornecedor fornecedor = FornecedorDAO.fornecedorPorId(conn, fornecedorId);
            if (fornecedor != null) {
                nomeField.setText(fornecedor.getNome());
                cnpjField.setText(fornecedor.getCnpj());
                emailField.setText(fornecedor.getEmail());
                telefoneField.setText(fornecedor.getTelefone());
            } else {
                JOptionPane.showMessageDialog(null, "Fornecedor não encontrado!", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao carregar dados do fornecedor.", "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void salvarFornecedor(int idFornecedor) {
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
        if (cnpj.trim().isEmpty() && cnpjLimpo.trim().isEmpty() && cnpjLimpo.length() != 14) {
            errorMessage.append("- CNPJ deve ser preenchido.\n");
            hasError = true;
        } else if (cnpjLimpo.length() != 14) {
            errorMessage.append("- CNPJ inválido (certifique-se de que contém 14 dígitos numéricos).\n");
            hasError = true;
        }

        if (email.isEmpty() || !validarEmail(email)) {
            errorMessage.append("- E-mail deve ser preenchido e válido.\n");
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
            Fornecedor fornecedorExistente = FornecedorDAO.fornecedorPorId(conn, idFornecedor);
            if (fornecedorExistente == null) {
                JOptionPane.showMessageDialog(null, "Fornecedor não encontrado.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            fornecedorExistente.setNome(nome);
            fornecedorExistente.setCnpj(cnpjLimpo);
            fornecedorExistente.setEmail(email);
            fornecedorExistente.setTelefone(telefoneLimpo);

            FornecedorDAO.atualizarFornecedor(conn, fornecedorExistente);
            JOptionPane.showMessageDialog(null, "Fornecedor atualizado com sucesso!", "Sucesso",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao atualizar fornecedor: " + e.getMessage(), "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean validarEmail(String email) {
        String emailRegex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        return Pattern.matches(emailRegex, email);
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