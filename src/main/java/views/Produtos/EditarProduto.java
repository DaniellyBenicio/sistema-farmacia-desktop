package views.Produtos;

import javax.swing.*;
import javax.swing.text.MaskFormatter;
import javax.swing.text.NumberFormatter;

import org.w3c.dom.events.MouseEvent;

import java.awt.*;
import java.awt.event.MouseAdapter;
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
import java.util.List;
import java.util.Locale;

import controllers.Fabricante.FabricanteController;
import controllers.Fornecedor.FornecedorController;
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

public class EditarProduto extends JPanel {
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

    private Produto produto;

    public EditarProduto(Produto produto) {
        this.produto = produto;
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

        preencherCamposComDadosDoProduto();
    }

    private JLabel criarTitulo() {
        JLabel titulo = new JLabel("EDIÇÃO DE PRODUTO");
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

        // Nome
        JLabel nomeLabel = new JLabel("Nome");
        nomeLabel.setFont(labelFont);
        gbc.gridx = 0;
        gbc.gridy = 0;
        camposPanel.add(nomeLabel, gbc);

        nomeProdutoField = new JTextField();
        nomeProdutoField.setPreferredSize(new Dimension(400, 40));
        estilizarCamposFormulario(nomeProdutoField, fieldFont);
        gbc.gridx = 0;
        gbc.gridy = 1;
        camposPanel.add(nomeProdutoField, gbc);

        // Embalagem
        JLabel embalagemLabel = new JLabel("Embalagem");
        embalagemLabel.setFont(labelFont);
        gbc.gridx = 1;
        gbc.gridy = 0;
        camposPanel.add(embalagemLabel, gbc);

        String[] tiposdeEmbalagem = { "Selecione", "Bisnaga", "Caixa", "Frasco", "Garrafa", "Lata", "Pacote", "Pote",
                "Refil", "Rolo", "Spray", "Tubo", "Vidro" };
        embalagemComboBox = new JComboBox<>(tiposdeEmbalagem);
        embalagemComboBox.setPreferredSize(new Dimension(180, 40));
        estilizarComboBox(embalagemComboBox, fieldFont);
        gbc.gridx = 1;
        gbc.gridy = 1;
        camposPanel.add(embalagemComboBox, gbc);

        embalagemField = new JTextField();
        embalagemField.setPreferredSize(new Dimension(180, 40));
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

        // Categoria
        JLabel categoriaLabel = new JLabel("Categoria");
        categoriaLabel.setFont(labelFont);
        gbc.gridx = 2;
        gbc.gridy = 0;
        camposPanel.add(categoriaLabel, gbc);

        String[] tiposDeCategoria = { 
            "Selecione", "Acessórios e Utilidades Pessoais", "Alimentícios", "Cosméticos e Perfumaria", "Dermocosméticos", 
            "Fraldas e Acessórios", "Higiene e Cuidado Pessoal", "Materiais Hospitalares", "Ortopedia", 
            "Saúde Sexual e Reprodutiva", "Suplementos e Vitaminas" };
        categoriaComboBox = new JComboBox<>(tiposDeCategoria);
        categoriaComboBox.setPreferredSize(new Dimension(235, 40));
        estilizarComboBox(categoriaComboBox, fieldFont);
        gbc.gridx = 2;
        gbc.gridy = 1;
        camposPanel.add(categoriaComboBox, gbc);

        JLabel fabricanteLabel = new JLabel("Fabricante");
        fabricanteLabel.setFont(labelFont);
        gbc.gridx = 0;
        gbc.gridy = 2;
        camposPanel.add(fabricanteLabel, gbc);

        fabricanteComboBox = new JComboBox<>(obterFabricantes());
        fabricanteComboBox.setPreferredSize(new Dimension(400, 40));
        estilizarComboBox(fabricanteComboBox, fieldFont);
        gbc.gridx = 0;
        gbc.gridy = 3;
        camposPanel.add(fabricanteComboBox, gbc);

        fabricanteField = new JTextField();
        fabricanteField.setPreferredSize(new Dimension(400, 40));
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

        // Medida
        JLabel medidaLabel = new JLabel("Medida");
        medidaLabel.setFont(labelFont);
        gbc.gridx = 1;
        gbc.gridy = 2;
        camposPanel.add(medidaLabel, gbc);

        qntMedidaField = new JTextField();
        qntMedidaField.setPreferredSize(new Dimension(180, 40));
        estilizarCamposFormulario(qntMedidaField, fieldFont);
        qntMedidaField.setText("Medidas: mL, kg, L, un");
        qntMedidaField.setForeground(Color.GRAY);
        qntMedidaField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (qntMedidaField.getText().equals("Medidas: mL, kg, L, un")) {
                    qntMedidaField.setText("");
                    qntMedidaField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (qntMedidaField.getText().isEmpty()) {
                    qntMedidaField.setText("Medidas: mL, kg, L, un");
                    qntMedidaField.setForeground(Color.GRAY);
                }
            }
        });
        gbc.gridx = 1;
        gbc.gridy = 3;
        camposPanel.add(qntMedidaField, gbc);

        // Estoque
        JLabel estoqueLabel = new JLabel("Estoque");
        estoqueLabel.setFont(labelFont);
        gbc.gridx = 2;
        gbc.gridy = 2;
        camposPanel.add(estoqueLabel, gbc);

        estoqueField = new JTextField();
        estoqueField.setPreferredSize(new Dimension(170, 40));
        estilizarCamposFormulario(estoqueField, fieldFont);
        gbc.gridx = 2;
        gbc.gridy = 3;
        camposPanel.add(estoqueField, gbc);

        // Fornecedor
        JLabel fornecedorLabel = new JLabel("Fornecedor");
        fornecedorLabel.setFont(labelFont);
        gbc.gridx = 0;
        gbc.gridy = 4;
        camposPanel.add(fornecedorLabel, gbc);

        fornecedorComboBox = new JComboBox<>(obterFornecedores());
        fornecedorComboBox.setPreferredSize(new Dimension(400, 40));
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

        // Data de Fabricação
        JLabel dataFabricacaoLabel = new JLabel("Fabricação (Mês/Ano)");
        dataFabricacaoLabel.setFont(labelFont);
        gbc.gridx = 1;
        gbc.gridy = 4;
        camposPanel.add(dataFabricacaoLabel, gbc);

        try {
            MaskFormatter dataFormatter = new MaskFormatter("##/####");
            dataFormatter.setPlaceholderCharacter('_'); // Define o caractere de preenchimento opcional
            dataFabricacaoField = new JFormattedTextField(dataFormatter);
            dataFabricacaoField.setPreferredSize(new Dimension(170, 40));
            estilizarCamposFormulario(dataFabricacaoField, fieldFont);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        gbc.gridx = 1;
        gbc.gridy = 5;
        camposPanel.add(dataFabricacaoField, gbc);

        // Data de Validade
        JLabel dataValidadeLabel = new JLabel("Validade (Mês/Ano)");
        dataValidadeLabel.setFont(labelFont);
        gbc.gridx = 2;
        gbc.gridy = 4;
        camposPanel.add(dataValidadeLabel, gbc);

        try {
            MaskFormatter dataFormatter = new MaskFormatter("##/####");
            dataFormatter.setPlaceholderCharacter('_'); 
            dataValidadeField = new JFormattedTextField(dataFormatter);
            dataValidadeField.setPreferredSize(new Dimension(170, 40));
            estilizarCamposFormulario(dataValidadeField, fieldFont);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        gbc.gridx = 2;
        gbc.gridy = 5;
        camposPanel.add(dataValidadeField, gbc);

        // Valor Unitário
        JLabel valorUnitarioLabel = new JLabel("Valor Unitário (R$)");
        valorUnitarioLabel.setFont(labelFont);
        gbc.gridx = 0;
        gbc.gridy = 6;
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
                return super.stringToValue(text);
            }
        };
        formatter.setAllowsInvalid(false);
        formatter.setOverwriteMode(false);
        formatter.setMinimum(0.0);
        formatter.setMaximum(999999.99);

        valorUnitarioField = new JFormattedTextField(formatter);
        valorUnitarioField.setPreferredSize(new Dimension(120, 40));
        estilizarCamposFormulario(valorUnitarioField, fieldFont);
        gbc.gridx = 0;
        gbc.gridy = 7;
        camposPanel.add(valorUnitarioField, gbc);

        return camposPanel;
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

        JButton salvarButton = new JButton("SALVAR");
        salvarButton.setFont(new Font("Arial", Font.BOLD, 17));
        salvarButton.setBackground(new Color(24, 39, 55));
        salvarButton.setForeground(Color.WHITE);
        salvarButton.setFocusPainted(false);
        salvarButton.setPreferredSize(new Dimension(140, 35));
        botoesPanel.add(salvarButton);

        cancelarButton.addActionListener(e -> {
            SwingUtilities.getWindowAncestor(this).dispose();
        });

        salvarButton.addActionListener(e -> salvarProduto());

        return botoesPanel;
    }

    private void salvarProduto() {
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

        StringBuilder errorMessage = new StringBuilder("Por favor, corrija os seguintes erros: \n");
        boolean hasError = false;

        if (nomeProduto.isEmpty()) {
            errorMessage.append("Nome deve ser preenchido.\n");
            hasError = true;
        } else {
            if (!nomeProduto.matches("^[\\p{L}\\d\\s]*$")) {
                errorMessage.append("Nome inválido (apenas letras, números e espaços são permitidos).\n");
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

        if (categoriaProduto.isEmpty()) {
            errorMessage.append("Pelo menos uma categoria deve ser selecionada.\n");
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

        if (qntMedida.isEmpty()) {
            errorMessage.append("Medida deve ser informada.\n");
            hasError = true;
        } else if (!qntMedida.toLowerCase().matches("\\d+(\\.\\d+)?(ml|l|g|kg|un)")) {
            errorMessage.append("Informe a medida com as seguintes unidades válidas:\n" +
                    "(mL, kg, L, un).\n" +
                    "Exemplo: 500mL, 10kg\n");
            hasError = true;
        } else {
            double qntMedidaValor = Double.parseDouble(qntMedida.replaceAll("[^\\d.]", ""));
            if (qntMedidaValor <= 0) {
                errorMessage.append("A medida deve ser um valor positivo.\n");
                hasError = true;
            }
        }

        Integer estoque = Integer.parseInt(estoqueField.getText().trim());
        if (estoque < 0) {
            errorMessage.append("A quantidade informada para estoque não pode ser negativa.\n");
            hasError = true;
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
        if (fabricacaoData.equals("/") || validadeData.equals("/")) {
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

        if (valorUnitarioTexto.isEmpty()) {
            errorMessage.append("O valor unitário deve ser informado.\n");
            hasError = true;
        }

        if (hasError) {
            JOptionPane.showMessageDialog(this, errorMessage.toString(), "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        BigDecimal valorUnitario;

        if (valorUnitarioTexto.isEmpty()) {
            JOptionPane.showMessageDialog(this, "O valor unitário deve ser informado.", "Erro",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!valorUnitarioTexto.matches("\\d+(\\.\\d{1,2})?")) {
            JOptionPane.showMessageDialog(this, "Valor unitário deve ser um número válido.", "Erro",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        valorUnitario = new BigDecimal(valorUnitarioTexto);

        if (valorUnitario.compareTo(BigDecimal.ZERO) <= 0) {
            JOptionPane.showMessageDialog(this, "O valor unitário deve ser maior que zero.", "Erro",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        Categoria categoria = new Categoria();
        categoria.setNome(categoriaProduto);

        Fabricante fabricante = new Fabricante();
        fabricante.setNome(fabricanteNome);

        produto.setNome(nomeProduto);
        produto.setValor(valorUnitario);
        produto.setDataFabricacao(dataFabricacao);
        produto.setDataValidade(dataValidade);
        produto.setQntMedida(qntMedida);
        produto.setEmbalagem(embalagem);
        produto.setFabricante(fabricante);
        produto.setFornecedor(fornecedor);
        produto.setCategoria(categoria);

        try (Connection conn = ConexaoBD.getConnection()) {
            ProdutoDAO.atualizarProduto(conn, produto);
            JOptionPane.showMessageDialog(null, "Produto atualizado com sucesso!", "Sucesso",
                    JOptionPane.INFORMATION_MESSAGE);

            SwingUtilities.getWindowAncestor(this).dispose();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao atualizar produto: " + ex.getMessage(), "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void preencherCamposComDadosDoProduto() {
        nomeProdutoField.setText(produto.getNome());
        categoriaComboBox.setSelectedItem(produto.getCategoria().getNome());
        embalagemComboBox.setSelectedItem(produto.getEmbalagem());
        qntMedidaField.setText(produto.getQntMedida());
        fabricanteComboBox.setSelectedItem(produto.getFabricante().getNome());
        fornecedorComboBox.setSelectedItem(produto.getFornecedor().getNome());
        dataFabricacaoField.setText(produto.getDataFabricacao().format(DateTimeFormatter.ofPattern("MM/yyyy")));
        dataValidadeField.setText(produto.getDataValidade().format(DateTimeFormatter.ofPattern("MM/yyyy")));
        valorUnitarioField.setText(produto.getValor().toString());
        estoqueField.setText(String.valueOf(produto.getQntEstoque()));
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