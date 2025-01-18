package views.Funcionario;

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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.text.MaskFormatter;

import dao.Cargo.CargoDAO;
import dao.Funcionario.FuncionarioDAO;
import main.ConexaoBD;
import models.Cargo.Cargo;
import models.Funcionario.Funcionario;

public class EditarFuncionario extends JPanel {

    private JTextField nomeField;
    private JFormattedTextField telefoneField;
    private JTextField emailField;
    private JTextField cargoField;
    private JComboBox<String> cargoComboBox;

    private int funcionarioId;

    public EditarFuncionario(int funcionarioId) {
        this.funcionarioId = funcionarioId;

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

        carregarDadosFuncionario();
    }

    private JLabel criarTitulo() {
        JLabel titulo = new JLabel("EDIÇÃO DE FUNCIONÁRIO");
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
        Dimension fieldSize = new Dimension(230, 40);

        JLabel nomeLabel = new JLabel("Nome");
        nomeLabel.setFont(labelFont);
        gbc.gridx = 0;
        gbc.gridy = 0;
        camposPanel.add(nomeLabel, gbc);

        nomeField = new JTextField();
        nomeField.setPreferredSize(new Dimension(500, 40));
        estilizarCampo(nomeField, fieldFont);
        gbc.gridx = 0;
        gbc.gridy = 1;
        camposPanel.add(nomeField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        camposPanel.add(Box.createRigidArea(new Dimension(0, 30)), gbc);

        JLabel telefoneLabel = new JLabel("Telefone");
        telefoneLabel.setFont(labelFont);
        gbc.gridx = 1;
        gbc.gridy = 0;
        camposPanel.add(telefoneLabel, gbc);

        telefoneField = criarCampoFormatado("(##) #####-####");
        telefoneField.setPreferredSize(fieldSize);
        estilizarCampo(telefoneField, fieldFont);
        gbc.gridx = 1;
        gbc.gridy = 1;
        camposPanel.add(telefoneField, gbc);

        JLabel emailLabel = new JLabel("E-mail");
        emailLabel.setFont(labelFont);
        gbc.gridx = 0;
        gbc.gridy = 3;
        camposPanel.add(emailLabel, gbc);

        emailField = new JTextField();
        emailField.setPreferredSize(new Dimension(500, 40));
        estilizarCampo(emailField, fieldFont);
        gbc.gridx = 0;
        gbc.gridy = 4;
        camposPanel.add(emailField, gbc);

        List<String> cargos = obterCargos();

        JLabel cargoLabel = new JLabel("Cargo");
        cargoLabel.setFont(labelFont);
        gbc.gridx = 1;
        gbc.gridy = 3;
        camposPanel.add(cargoLabel, gbc);

        cargoComboBox = new JComboBox<>(obterCargos().toArray(new String[0]));
        cargoComboBox.setPreferredSize(fieldSize);
        cargoComboBox.setFont(fieldFont);
        cargoComboBox.setFocusable(false);

        gbc.gridx = 1;
        gbc.gridy = 4;
        camposPanel.add(cargoComboBox, gbc);

        cargoField = new JTextField();
        cargoField.setPreferredSize(fieldSize);
        estilizarCampo(cargoField, fieldFont);
        cargoField.setVisible(false);
        gbc.gridx = 1;
        gbc.gridy = 4;
        camposPanel.add(cargoField, gbc);

        cargoComboBox.addActionListener(e -> {
            if ("Outros".equals(cargoComboBox.getSelectedItem())) {
                cargoComboBox.setVisible(false);
                cargoField.setVisible(true);
            } else {
                cargoField.setText("");
                cargoComboBox.setVisible(true);
                cargoField.setVisible(false);
            }
        });

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
        cancelarButton.addActionListener(e -> {
            SwingUtilities.getWindowAncestor(this).dispose();
        });
        botoesPanel.add(cancelarButton);

        JButton salvarButton = new JButton("SALVAR");
        salvarButton.setFont(new Font("Arial", Font.BOLD, 17));
        salvarButton.setBackground(new Color(24, 39, 55));
        salvarButton.setForeground(Color.WHITE);
        salvarButton.setFocusPainted(false);
        salvarButton.setPreferredSize(new Dimension(150, 40));
        salvarButton.addActionListener(e -> salvarFuncionario(funcionarioId));
        botoesPanel.add(salvarButton);

        return botoesPanel;
    }

    private void carregarDadosFuncionario() {
        try (Connection conn = ConexaoBD.getConnection()) {
            Funcionario funcionario = FuncionarioDAO.funcionarioPorId(conn, funcionarioId);
            if (funcionario != null) {
                nomeField.setText(funcionario.getNome());
                telefoneField.setText(funcionario.getTelefone());
                emailField.setText(funcionario.getEmail());
                if (funcionario.getCargo() != null) {
                    cargoComboBox.setSelectedItem(funcionario.getCargo().getNome());
                }

            } else {
                JOptionPane.showMessageDialog(null, "Funcionário não encontrado!", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao carregar dados do funcionário.", "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private List<String> obterCargos() {
        List<String> cargos = new ArrayList<>();

        cargos.add("Assistente Administrativo");
        cargos.add("Atendente");
        cargos.add("Caixa");
        cargos.add("Estoquista");
        cargos.add("Farmacêutico");
        cargos.add("Gerente");
        cargos.add("Técnico de Enfermagem");
        cargos.add("Técnico de Farmácia");

        try (Connection conn = ConexaoBD.getConnection()) {
            ArrayList<Cargo> cargosDB = CargoDAO.listarTodosCargos(conn);
            for (Cargo cargo : cargosDB) {
                String nomeCargo = cargo.getNome();
                if (!cargos.contains(nomeCargo)) {
                    cargos.add(nomeCargo);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        List<String> cargosParaOrdenar = new ArrayList<>(cargos);
        Collections.sort(cargosParaOrdenar, String.CASE_INSENSITIVE_ORDER);
        cargosParaOrdenar.add("Outros");

        return cargosParaOrdenar;
    }

    private void salvarFuncionario(int idFuncionario) {
        String nome = nomeField.getText().trim();
        String telefone = telefoneField.getText().trim();
        String email = emailField.getText().trim();
        String cargoNome = (String) cargoComboBox.getSelectedItem();

        if ("Outros".equals(cargoNome)) {
            cargoNome = cargoField.getText().trim();
        }

        StringBuilder errorMessage = new StringBuilder("Por favor, corrija os seguintes erros: \n");
        boolean hasError = false;

        if (nome.isEmpty()) {
            errorMessage.append("- Nome deve ser preenchido.\n");
            hasError = true;
        } else {
            if (!nome.matches("^[\\p{L}\\s]*$")) {
                errorMessage.append("- Nome inválido (apenas letras e espaços são permitidos).\n");
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

        if (email.isEmpty()) {
            errorMessage.append("- E-mail deve ser preenchido.\n");
            hasError = true;
        } else {
            if (!validarEmail(email)) {
                errorMessage.append("- E-mail inválido.\n");
                hasError = true;
            }
        }

        if (cargoNome == null || cargoNome.isEmpty() || "Selecione".equals(cargoNome)) {
            errorMessage.append("- Cargo deve ser preenchido.\n");
            hasError = true;
        } else {
            if (!cargoNome.matches("^[\\p{L}\\s]*$")) {
                errorMessage.append("- Cargo inválido (apenas letras e espaços são permitidos).\n");
                hasError = true;
            }
        }

        if (hasError) {
            JOptionPane.showMessageDialog(null, errorMessage.toString(), "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = ConexaoBD.getConnection()) {

            if ("Outros".equals(cargoComboBox.getSelectedItem())) {
                ArrayList<Cargo> cargosExistentes = CargoDAO.listarTodosCargos(conn);
                for (Cargo c : cargosExistentes) {
                    if (c.getNome().equalsIgnoreCase(cargoNome)) {
                        JOptionPane.showMessageDialog(null,
                                "O cargo informado já existe no banco de dados.\n" +
                                        "Selecione esse cargo na lista de cargos.",
                                "Cargo Existente",
                                JOptionPane.INFORMATION_MESSAGE);
                        cargoField.setText("");
                        cargoComboBox.setVisible(true);
                        cargoField.setVisible(false);
                        cargoComboBox.setSelectedItem("");
                        return;
                    }
                }
            }

            Funcionario funcionarioAtualizado = new Funcionario();

            funcionarioAtualizado.setId(idFuncionario);
            funcionarioAtualizado.setNome(nome);
            funcionarioAtualizado.setTelefone(telefone.replaceAll("[^0-9]", ""));
            funcionarioAtualizado.setEmail(email);

            Cargo cargo = new Cargo();
            cargo.setNome(cargoNome);

            if (cargo.getNome() == null || cargo.getNome().trim().isEmpty()) {
                throw new IllegalArgumentException("Cargo deve ter um nome válido.");
            }

            funcionarioAtualizado.setCargo(cargo);

            FuncionarioDAO.atualizarFuncionario(conn, funcionarioAtualizado);
            JOptionPane.showMessageDialog(null, "Funcionário atualizado com sucesso!", "Sucesso",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            String message = e.getMessage();
            if (message.contains("email")) {
                JOptionPane.showMessageDialog(null, "O e-mail informado já está associado a um funcionário existente. Por favor, utilize outro e-mail.", "Erro",
                        JOptionPane.ERROR_MESSAGE);
            }
            if (message.contains("telefone")) {
                JOptionPane.showMessageDialog(null, "O telefone informado já está associado a um funcionário existente. Por favor, informe outro número.", "Erro",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
        cargoComboBox.setVisible(true);
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