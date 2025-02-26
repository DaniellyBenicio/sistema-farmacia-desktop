package views.Produtos;

import javax.swing.*;
import javax.swing.text.MaskFormatter;
import javax.swing.text.NumberFormatter;

import java.awt.*;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import controllers.Fabricante.FabricanteController;
import controllers.Fornecedor.FornecedorController;
import controllers.Produto.ProdutoController;
import dao.Fornecedor.FornecedorDAO;
import dao.Funcionario.FuncionarioDAO;
import dao.Produto.ProdutoDAO;
import main.ConexaoBD;
import models.Categoria.Categoria;
import models.Fabricante.Fabricante;
import models.Fornecedor.Fornecedor;
import models.Funcionario.Funcionario;
import models.Produto.Produto;
import views.BarrasSuperiores.PainelSuperior;
import views.Fornecedor.CadastrarFornecedor;

public class CadastrarProduto extends JPanel {
    private JTextField nomeProdutoField;
    private JComboBox<String> embalagemComboBox;
    private JTextField embalagemField;
    private JTextField qntMedidaField;
    private JTextField fabricanteField;
    private JComboBox<String> fabricanteComboBox;
    private JComboBox<String> fornecedorComboBox;
    private JFormattedTextField dataFabricacaoField;
    private JFormattedTextField dataValidadeField;
    private JTextField estoqueField;
    private JFormattedTextField valorUnitarioField;
    private JComboBox<String> categoriaComboBox;
    private JTextField quantidadeEmbalagemField;

    public CadastrarProduto() {
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
        JLabel titulo = new JLabel("CADASTRO DE PRODUTO");
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        return titulo;
    }

    private JPanel criarCamposPanel() {
        JPanel camposPanel = new JPanel();
        camposPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 20, 0, 10);
        gbc.anchor = GridBagConstraints.WEST;

        Font labelFont = new Font("Arial", Font.BOLD, 15);
        Font fieldFont = new Font("Arial", Font.PLAIN, 14);

        JLabel nomeLabel = new JLabel("Nome");
        nomeLabel.setFont(labelFont);
        gbc.gridx = 0;
        gbc.gridy = 0;
        camposPanel.add(nomeLabel, gbc);

        nomeProdutoField = new JTextField();
        nomeProdutoField.setPreferredSize(new Dimension(280, 40));
        estilizarCamposFormulario(nomeProdutoField, fieldFont);
        gbc.gridx = 0;
        gbc.gridy = 1;
        camposPanel.add(nomeProdutoField, gbc);

        JLabel embalagemLabel = new JLabel("Embalagem");
        embalagemLabel.setFont(labelFont);
        gbc.gridx = 1;
        gbc.gridy = 0;
        camposPanel.add(embalagemLabel, gbc);

        String[] tiposdeEmbalagem = { "Selecione", "Bisnaga", "Caixa", "Frasco", "Garrafa", "Lata", "Pacote", "Pote",
                "Refil", "Rolo", "Spray", "Tubo", "Vidro", "Outros" };
        embalagemComboBox = new JComboBox<>(tiposdeEmbalagem);
        embalagemComboBox.setPreferredSize(new Dimension(150, 40));
        estilizarComboBox(embalagemComboBox, fieldFont);
        gbc.gridx = 1;
        gbc.gridy = 1;
        camposPanel.add(embalagemComboBox, gbc);

        embalagemField = new JTextField();
        embalagemField.setPreferredSize(new Dimension(150, 40));
        estilizarCamposFormulario(embalagemField, fieldFont);
        embalagemField.setVisible(false);
        gbc.gridx = 1;
        gbc.gridy = 1;
        camposPanel.add(embalagemField, gbc);

        embalagemComboBox.addActionListener(e -> {
            if ("Outros".equals(embalagemComboBox.getSelectedItem())) {
                embalagemComboBox.setVisible(false);
                embalagemField.setVisible(true);
                embalagemField.requestFocus();
            } else {
                embalagemField.setText("");
                embalagemField.setVisible(false);
                embalagemComboBox.setVisible(true);
            }
        });

        JLabel quantidadeEmbalagemLabel = new JLabel("Qnt. Embalagem");
        quantidadeEmbalagemLabel.setFont(labelFont);
        gbc.gridx = 2;
        gbc.gridy = 0;
        camposPanel.add(quantidadeEmbalagemLabel, gbc);

        quantidadeEmbalagemField = new JTextField();
        quantidadeEmbalagemField.setPreferredSize(new Dimension(140, 40));
        estilizarCamposFormulario(quantidadeEmbalagemField, fieldFont);
        gbc.gridx = 2;
        gbc.gridy = 1;
        camposPanel.add(quantidadeEmbalagemField, gbc);

        JLabel categoriaLabel = new JLabel("Categoria");
        categoriaLabel.setFont(labelFont);
        gbc.gridx = 3;
        gbc.gridy = 0;
        camposPanel.add(categoriaLabel, gbc);

        String[] categoriaProdutos = obterCategoriaProdutos();
        categoriaComboBox = new JComboBox<>(categoriaProdutos);
        categoriaComboBox.setPreferredSize(new Dimension(200, 40));
        estilizarComboBox(categoriaComboBox, fieldFont);
        gbc.gridx = 3;
        gbc.gridy = 1;
        camposPanel.add(categoriaComboBox, gbc);

        JLabel fabricanteLabel = new JLabel("Fabricante");
        fabricanteLabel.setFont(labelFont);
        gbc.gridx = 0;
        gbc.gridy = 2;
        camposPanel.add(fabricanteLabel, gbc);

        fabricanteComboBox = new JComboBox<>(obterFabricantes());
        fabricanteComboBox.setPreferredSize(new Dimension(280, 40));
        estilizarComboBox(fabricanteComboBox, fieldFont);
        gbc.gridx = 0;
        gbc.gridy = 3;
        camposPanel.add(fabricanteComboBox, gbc);

        fabricanteField = new JTextField();
        fabricanteField.setPreferredSize(new Dimension(280, 40));
        estilizarCamposFormulario(fabricanteField, fieldFont);
        fabricanteField.setVisible(false);
        gbc.gridx = 0;
        gbc.gridy = 3;
        camposPanel.add(fabricanteField, gbc);

        fabricanteComboBox.addActionListener(e -> {
            if ("Outros".equals(fabricanteComboBox.getSelectedItem())) {
                fabricanteComboBox.setVisible(false);
                fabricanteField.setVisible(true);
                fabricanteField.requestFocus();
            } else {
                fabricanteField.setText("");
                fabricanteField.setVisible(false);
                fabricanteComboBox.setVisible(true);
            }
        });

        JLabel medidaLabel = new JLabel("Medida");
        medidaLabel.setFont(labelFont);
        gbc.gridx = 1;
        gbc.gridy = 2;
        camposPanel.add(medidaLabel, gbc);

        qntMedidaField = new JTextField();
        qntMedidaField.setPreferredSize(new Dimension(150, 40));
        estilizarCamposFormulario(qntMedidaField, fieldFont);
        qntMedidaField.setText("Medidas: mL, g, L, un");
        qntMedidaField.setForeground(Color.GRAY);
        qntMedidaField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (qntMedidaField.getText().equals("Medidas: mL, g, L, un")) {
                    qntMedidaField.setText("");
                    qntMedidaField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (qntMedidaField.getText().isEmpty()) {
                    qntMedidaField.setText("Medidas: mL, g, L, un");
                    qntMedidaField.setForeground(Color.GRAY);
                }
            }
        });
        gbc.gridx = 1;
        gbc.gridy = 3;
        camposPanel.add(qntMedidaField, gbc);

        JLabel estoqueLabel = new JLabel("Estoque");
        estoqueLabel.setFont(labelFont);
        gbc.gridx = 2;
        gbc.gridy = 2;
        camposPanel.add(estoqueLabel, gbc);

        estoqueField = new JTextField();
        estoqueField.setPreferredSize(new Dimension(140, 40));
        estilizarCamposFormulario(estoqueField, fieldFont);
        gbc.gridx = 2;
        gbc.gridy = 3;
        camposPanel.add(estoqueField, gbc);

        JLabel fornecedorLabel = new JLabel("Fornecedor");
        fornecedorLabel.setFont(labelFont);
        gbc.gridx = 0;
        gbc.gridy = 4;
        camposPanel.add(fornecedorLabel, gbc);

        fornecedorComboBox = new JComboBox<>(obterFornecedores());
        fornecedorComboBox.setPreferredSize(new Dimension(280, 40));
        estilizarComboBox(fornecedorComboBox, fieldFont);
        gbc.gridx = 0;
        gbc.gridy = 5;
        camposPanel.add(fornecedorComboBox, gbc);

        fornecedorComboBox.addActionListener(e -> {
            String selectedItem = (String) fornecedorComboBox.getSelectedItem();
            if ("Outros".equals(selectedItem)) {
                JDialog cadastroDialog = new JDialog();
                cadastroDialog.setTitle("Cadastrar Fornecedor");
                cadastroDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                cadastroDialog.setSize(1200, 650);
                cadastroDialog.setLocationRelativeTo(this);
                cadastroDialog.setModal(true);

                CadastrarFornecedor cadastroPanel = new CadastrarFornecedor();

                cadastroDialog.add(cadastroPanel);
                cadastroDialog.setVisible(true);

                String novoFornecedorNome = cadastroPanel.getNomeFornecedorCadastrado();
                if (novoFornecedorNome != null) {
                    fornecedorComboBox.addItem(novoFornecedorNome);
                    fornecedorComboBox.setSelectedItem(novoFornecedorNome);
                }
            }
        });

        JLabel dataFabricacaoLabel = new JLabel("Fabricação (Mês/Ano)");
        dataFabricacaoLabel.setFont(labelFont);
        gbc.gridx = 1;
        gbc.gridy = 4;
        camposPanel.add(dataFabricacaoLabel, gbc);

        try {
            MaskFormatter dataFormatter = new MaskFormatter("##/####");
            dataFormatter.setPlaceholderCharacter('_');
            dataFabricacaoField = new JFormattedTextField(dataFormatter);
            dataFabricacaoField.setPreferredSize(new Dimension(150, 40));
            estilizarCamposFormulario(dataFabricacaoField, fieldFont);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        gbc.gridx = 1;
        gbc.gridy = 5;
        camposPanel.add(dataFabricacaoField, gbc);

        JLabel dataValidadeLabel = new JLabel("Validade (Mês/Ano)");
        dataValidadeLabel.setFont(labelFont);
        gbc.gridx = 2;
        gbc.gridy = 4;
        camposPanel.add(dataValidadeLabel, gbc);

        try {
            MaskFormatter dataFormatter = new MaskFormatter("##/####");
            dataFormatter.setPlaceholderCharacter('_');
            dataValidadeField = new JFormattedTextField(dataFormatter);
            dataValidadeField.setPreferredSize(new Dimension(140, 40));
            estilizarCamposFormulario(dataValidadeField, fieldFont);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        gbc.gridx = 2;
        gbc.gridy = 5;
        camposPanel.add(dataValidadeField, gbc);

        JLabel valorUnitarioLabel = new JLabel("Valor Unitário (R$)");
        valorUnitarioLabel.setFont(labelFont);
        gbc.gridx = 3;
        gbc.gridy = 2;
        camposPanel.add(valorUnitarioLabel, gbc);

        NumberFormat format = NumberFormat.getNumberInstance(Locale.US);
        format.setMinimumFractionDigits(2);
        format.setMaximumFractionDigits(2);
        NumberFormatter formatter = new NumberFormatter(format) {
            @Override
            public Object stringToValue(String text) throws ParseException {
                if (text == null || text.isEmpty()) {
                    return null;
                }
                try {
                    return super.stringToValue(text);
                } catch (ParseException e) {
                    return null;
                }
            }
        };
        formatter.setAllowsInvalid(false);
        formatter.setOverwriteMode(false);
        formatter.setMinimum(0.0);
        formatter.setMaximum(999999.99);

        valorUnitarioField = new JFormattedTextField(formatter);
        valorUnitarioField.setPreferredSize(new Dimension(200, 40));
        estilizarCamposFormulario(valorUnitarioField, fieldFont);
        gbc.gridx = 3;
        gbc.gridy = 3;
        camposPanel.add(valorUnitarioField, gbc);

        return camposPanel;
    }

    private String[] obterCategoriaProdutos() {
        List<String> categoriasdeProdutoPreDefinidas = new ArrayList<>(Arrays.asList(
                "Acessórios e Utilidades Pessoais", "Alimentícios", "Cosméticos e Perfumaria",
                "Dermocosméticos", "Fraldas e Acessórios", "Higiene e Cuidado Pessoal", "Materiais Hospitalares",
                "Ortopedia", "Saúde Sexual e Reprodutiva", "Suplementos e Vitaminas"));

        Set<String> categoriaProdutos = new LinkedHashSet<>();
        categoriaProdutos.add("Selecione");
        categoriaProdutos.addAll(categoriasdeProdutoPreDefinidas);

        try (Connection conn = ConexaoBD.getConnection()) {
            List<String> categoriaProdutosDoBanco = ProdutoController.listarCategoriasProduto(conn);
            categoriaProdutos.addAll(categoriaProdutosDoBanco);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return categoriaProdutos.toArray(new String[0]);

    }

    private String[] obterFornecedores() {
        try (Connection conn = ConexaoBD.getConnection()) {
            ArrayList<String> fornecedores = FornecedorController.listarFornecedoresPorNome(conn);
            fornecedores.add(0, "Selecione");
            fornecedores.add("Outros");
            return fornecedores.toArray(new String[0]);
        } catch (SQLException e) {
            e.printStackTrace();
            return new String[] { "Selecione" };
        }
    }

    private String[] obterFabricantes() {
        try (Connection conn = ConexaoBD.getConnection()) {
            ArrayList<String> fabricantes = FabricanteController.listarTodosFabricantes(conn);
            fabricantes.add(0, "Selecione");
            fabricantes.add("Outros");
            return fabricantes.toArray(new String[0]);
        } catch (SQLException e) {
            e.printStackTrace();
            return new String[] { "Selecione" };
        }
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

        cancelarButton.addActionListener(e -> {
            SwingUtilities.getWindowAncestor(this).dispose();
        });

        cadastrarButton.addActionListener(e -> cadastrarProduto());

        return botoesPanel;
    }

    private void cadastrarProduto() {
        int idFuncionario = PainelSuperior.getIdFuncionarioAtual();

        String nomeProduto = nomeProdutoField.getText().trim().toLowerCase();
        String categoriaProduto = (String) categoriaComboBox.getSelectedItem();
        String embalagem = (String) embalagemComboBox.getSelectedItem();
        String qntMedida = qntMedidaField.getText().trim();
        String fabricanteNome = fabricanteField.getText().trim();
        String fornecedorNome = (String) fornecedorComboBox.getSelectedItem();
        String fabricacaoData = dataFabricacaoField.getText().trim();
        String validadeData = dataValidadeField.getText().trim();
        String valorUnitarioTexto = valorUnitarioField.getText().trim();
        String qntEmbalagemTexto = quantidadeEmbalagemField.getText().trim();
        String qntestoqueTexto = estoqueField.getText().trim();

        StringBuilder errorMessage = new StringBuilder("Por favor, corrija os seguintes erros: \n");
        boolean hasError = false;

        if (nomeProduto.isEmpty()) {
            errorMessage.append("Nome deve ser preenchido.\n");
            hasError = true;
        } else {
            if (!nomeProduto.matches("^[\\p{L}\\d][\\p{L}\\d\\s]*$")) {
                errorMessage.append(
                        "Nome inválido (apenas letras, números e espaços são permitidos, e não pode começar com espaço).\n");
                hasError = true;
            }
        }

        if ("Outros".equals(embalagem)) {
            embalagem = embalagemField.getText().trim();
            if (embalagem.isEmpty()) {
                errorMessage.append("Embalagem deve ser preenchida.\n");
                hasError = true;
            }
        } else if ("Selecione".equals(embalagem)) {
            errorMessage.append("Embalagem deve ser selecionada.\n");
            hasError = true;
        }

        int qntEmbalagem = 0;
        if (!qntEmbalagemTexto.isEmpty()) {
            try {
                qntEmbalagem = Integer.parseInt(qntEmbalagemTexto);
                if (qntEmbalagem <= 0) {
                    errorMessage.append("A quantidade por embalagem deve ser um número inteiro positivo.\n");
                    hasError = true;
                }
            } catch (NumberFormatException e) {
                errorMessage.append("Quantidade por embalagem inválida. Use apenas números inteiros.\n");
                hasError = true;
            }
        } else {
            errorMessage.append("Quantidade por embalagem deve ser informada.\n");
            hasError = true;
        }

        if ("Selecione".equals(categoriaProduto) || categoriaProduto.isEmpty()) {
            errorMessage.append("Categoria deve ser selecionada.\n");
            hasError = true;
        }

        if (fabricanteField.isVisible()) {
            fabricanteNome = fabricanteField.getText().trim();
            if (fabricanteNome.isEmpty()) {
                errorMessage.append("Fabricante deve ser informado.\n");
                hasError = true;
            }
        } else {
            fabricanteNome = (String) fabricanteComboBox.getSelectedItem();
            if ("Selecione".equals(fabricanteNome) || fabricanteNome.isEmpty()) {
                errorMessage.append("Fabricante deve ser selecionado.\n");
                hasError = true;
            }
        }

        if (qntMedida.isEmpty() || qntMedida.equals("Medidas: mL, kg, L, un")) {
            errorMessage.append("Medida deve ser informada.\n");
            hasError = true;
        } else if (!qntMedida.toLowerCase().matches("^\\d+(\\.\\d+)?(ml|l|g|kg|un)$")) {
            errorMessage.append("Medida inválida. Use um formato como 100ml, 1kg, 2L, 5un\n");
            hasError = true;
        }

        int qntEstoque = 0;
        if (!qntestoqueTexto.isEmpty()) {
            try {
                qntEstoque = Integer.parseInt(qntestoqueTexto);
                if (qntEstoque <= 0) {
                    errorMessage.append("A quantidade do estoque deve ser um número inteiro positivo.\n");
                    hasError = true;
                }
            } catch (NumberFormatException e) {
                errorMessage.append("Quantidade de estoque inválida. Use apenas números inteiros.\n");
                hasError = true;
            }
        } else {
            errorMessage.append("Quantidade no estoque deve ser informada.\n");
            hasError = true;
        }

        BigDecimal valorUnitario = null;
        if (valorUnitarioTexto.isEmpty()) {
            errorMessage.append("O valor unitário deve ser informado.\n");
            hasError = true;
        } else {
            try {
                valorUnitario = new BigDecimal(valorUnitarioTexto.replace(",", "."));
                if (valorUnitario.compareTo(BigDecimal.ZERO) <= 0) {
                    errorMessage.append("O valor unitário deve ser maior que zero.\n");
                    hasError = true;
                }
            } catch (NumberFormatException e) {
                errorMessage.append("Valor unitário inválido. Use um formato numérico válido.\n");
                hasError = true;
            }
        }

        if ("Selecione".equals(fornecedorNome)) {
            errorMessage.append("Fornecedor deve ser selecionado.\n");
            hasError = true;
        }

        Funcionario funcionario;
        Fornecedor fornecedor;

        try (Connection conn = ConexaoBD.getConnection()) {
            fornecedor = FornecedorDAO.buscarFornecedorPorNome(conn, fornecedorNome);
            funcionario = FuncionarioDAO.buscarFuncionarioId(conn, idFuncionario);

            if (funcionario == null) {
                JOptionPane.showMessageDialog(this, "Funcionário não encontrado com ID: " + idFuncionario,
                        "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro na busca: " + ex.getMessage(), "Erro",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        LocalDate dataFabricacao = null, dataValidade = null;
        if (fabricacaoData.equals("") || validadeData.equals("")) {
            errorMessage.append("As datas de fabricação e validade devem ser preenchidas corretamente.\n");
            hasError = true;
        } else {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yyyy");
                YearMonth ymFabricacao = YearMonth.parse(fabricacaoData, formatter);
                YearMonth ymValidade = YearMonth.parse(validadeData, formatter);
                dataFabricacao = ymFabricacao.atDay(28);
                dataValidade = ymValidade.atDay(28);
            } catch (DateTimeParseException ex) {
                errorMessage.append("Formato de data inválido. Use Mês/Ano, ex: 08/2023\n");
                hasError = true;
            }
        }

        if (dataFabricacao != null) {
            LocalDate dataMinima = LocalDate.now().minusYears(5);
            if (dataFabricacao.isBefore(dataMinima)) {
                errorMessage.append("Data de fabricação inválida! Deve ser posterior a " +
                        dataMinima.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + ".\n");
                hasError = true;
            } else if (dataFabricacao.isAfter(LocalDate.now())) {
                errorMessage.append("Data de fabricação inválida! Não pode ser posterior à data atual.\n");
                hasError = true;
            }
        }

        if (dataValidade != null) {
            if (dataValidade.isBefore(dataFabricacao)) {
                errorMessage.append("Data de validade inválida! Não pode ser anterior à data de fabricação.\n");
                hasError = true;
            } else if (dataValidade.isBefore(LocalDate.now())) {
                errorMessage.append("Data de validade inválida! Não pode ser anterior à data atual.\n");
                hasError = true;
            }

            LocalDate dataValidadeMaxima = dataFabricacao != null ? dataFabricacao.plusYears(5) : null;
            if (dataValidadeMaxima != null && dataValidade.isAfter(dataValidadeMaxima)) {
                errorMessage.append(
                        "Data de validade inválida! Não pode ser superior a 5 anos a partir da data de fabricação.\n");
                hasError = true;
            }
        }

        if (hasError) {
            JOptionPane.showMessageDialog(this, errorMessage.toString(), "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Categoria categoria = new Categoria();
        categoria.setNome(categoriaProduto);

        Fabricante fabricante = new Fabricante();
        fabricante.setNome(fabricanteNome);

        Produto produto = new Produto(nomeProduto, valorUnitario, qntEstoque, dataValidade, dataFabricacao,
                qntMedida, embalagem, qntEmbalagem, funcionario, fabricante, fornecedor, categoria);

        try (Connection conn = ConexaoBD.getConnection()) {
            ProdutoDAO.cadastrarProduto(conn, produto);
            JOptionPane.showMessageDialog(null, "Produto cadastrado com sucesso!", "Sucesso",
                    JOptionPane.INFORMATION_MESSAGE);

            nomeProdutoField.setText("");
            embalagemField.setText("");
            fabricanteField.setText("");
            qntMedidaField.setText("");
            estoqueField.setText("");
            dataFabricacaoField.setText("");
            dataValidadeField.setText("");
            valorUnitarioField.setText("");
            quantidadeEmbalagemField.setText("");
            categoriaComboBox.setSelectedIndex(0);
            embalagemComboBox.setSelectedIndex(0);
            fabricanteComboBox.setSelectedIndex(0);
            fornecedorComboBox.setSelectedIndex(0);

            nomeProdutoField.requestFocus();

            atualizarFabricantes();
            atualizarFornecedores();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao cadastrar produto: " + ex.getMessage(), "Erro",
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

    private void estilizarComboBox(JComboBox<String> comboBox, Font font) {
        comboBox.setBackground(Color.WHITE);
        comboBox.setRenderer(new DefaultListCellRenderer() {
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
        comboBox.setFont(font);
        comboBox.setFocusable(false);
        comboBox.setSelectedIndex(0);
    }

    private void atualizarFornecedores() {
        String[] fornecedoresAtualizados = obterFornecedores();
        fornecedorComboBox.removeAllItems();
        for (String fornecedor : fornecedoresAtualizados) {
            fornecedorComboBox.addItem(fornecedor);
        }
    }

    private void atualizarFabricantes() {
        String[] fabricantesAtualizadas = obterFabricantes();
        fabricanteComboBox.removeAllItems();
        for (String fabricante : fabricantesAtualizadas) {
            fabricanteComboBox.addItem(fabricante);
        }
    }
}
