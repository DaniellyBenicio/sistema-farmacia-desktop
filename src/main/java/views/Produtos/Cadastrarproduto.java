package views.Produtos;

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
import java.util.List;
import java.util.Locale;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import javax.swing.text.MaskFormatter;
import javax.swing.text.NumberFormatter;

import controllers.Produto.ProdutoController;
import main.ConexaoBD;
import models.Produto.Produto;
import models.Categoria.Categoria;
import models.Fornecedor.Fornecedor;
import models.Funcionario.Funcionario;
import views.BarrasSuperiores.PainelSuperior;

public class CadastrarProduto extends JPanel {
    private JTextField nomeField;
    private JTextField descricaoField;
    private JFormattedTextField valorUnitarioField;
    private JFormattedTextField dataValidadeField;
    private JFormattedTextField dataFabricacaoField;
    private JTextField qntMedidaField;
    private JTextField embalagemField;
    private JComboBox<String> categoriaComboBox;
    private JComboBox<String> fornecedorComboBox;
    private JButton salvarButton;
    private JButton cancelarButton;

    private static final String ERRO_TITULO = "Erro";
    private static final String SUCESSO_TITULO = "Sucesso";
    private static final String ERRO_NOME_VAZIO = "O nome do produto não pode ser vazio.";
    private static final String ERRO_DESCRICAO_VAZIA = "A descrição não pode ser vazia.";
    private static final String ERRO_CATEGORIA_NAO_SELECIONADA = "Por favor, selecione uma categoria.";
    private static final String ERRO_FORNECEDOR_NAO_SELECIONADO = "Por favor, selecione um fornecedor.";
    private static final String ERRO_VALOR_VAZIO = "O valor unitário não pode ser vazio.";
    private static final String ERRO_DATA_VALIDADE_VAZIA = "A data de validade não pode ser vazia.";
    private static final String ERRO_DATA_FABRICACAO_VAZIA = "A data de fabricação não pode ser vazia.";
    private static final String ERRO_QNT_MEDIDA_VAZIA = "A quantidade de medida não pode ser vazia.";
    private static final String ERRO_EMBALAGEM_VAZIA = "A embalagem não pode ser vazia.";
    private static final String ERRO_VALOR_INVALIDO = "Valor unitário inválido.";
    private static final String ERRO_VALOR_MENOR_OU_IGUAL_ZERO = "O valor unitário deve ser maior que zero.";
    private static final String ERRO_DATA_VALIDADE_INVALIDA = "Data de validade inválida. Use o formato MM/yyyy.";
    private static final String ERRO_DATA_FABRICACAO_INVALIDA = "Data de fabricação inválida. Use o formato MM/yyyy.";

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

        // Campo Nome
        JLabel nomeLabel = new JLabel("Nome");
        nomeLabel.setFont(labelFont);
        gbc.gridx = 0;
        gbc.gridy = 0;
        camposPanel.add(nomeLabel, gbc);

        nomeField = new JTextField();
        nomeField.setPreferredSize(new Dimension(400, 40));
        estilizarCamposFormulario(nomeField, fieldFont);
        gbc.gridx = 0;
        gbc.gridy = 1;
        camposPanel.add(nomeField, gbc);

        // Campo Descrição
        JLabel descricaoLabel = new JLabel("Descrição");
        descricaoLabel.setFont(labelFont);
        gbc.gridx = 0;
        gbc.gridy = 2;
        camposPanel.add(descricaoLabel, gbc);

        descricaoField = new JTextField();
        descricaoField.setPreferredSize(new Dimension(400, 40));
        estilizarCamposFormulario(descricaoField, fieldFont);
        gbc.gridx = 0;
        gbc.gridy = 3;
        camposPanel.add(descricaoField, gbc);

        // Campo Valor Unitário
        JLabel valorUnitarioLabel = new JLabel("Valor Unitário (R$)");
        valorUnitarioLabel.setFont(labelFont);
        gbc.gridx = 1;
        gbc.gridy = 0;
        camposPanel.add(valorUnitarioLabel, gbc);

        NumberFormat format = NumberFormat.getNumberInstance(Locale.US);
        format.setMinimumFractionDigits(2);
        format.setMaximumFractionDigits(2);
        NumberFormatter formatter = new NumberFormatter(format) {
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
        valorUnitarioField.setPreferredSize(new Dimension(180, 40));
        estilizarCamposFormulario(valorUnitarioField, fieldFont);
        gbc.gridx = 1;
        gbc.gridy = 1;
        camposPanel.add(valorUnitarioField, gbc);

        // Campo Data de Validade
        JLabel dataValidadeLabel = new JLabel("Data de Validade (MM/yyyy)");
        dataValidadeLabel.setFont(labelFont);
        gbc.gridx = 0;
        gbc.gridy = 4;
        camposPanel.add(dataValidadeLabel, gbc);

        try {
            MaskFormatter dataFormatter = new MaskFormatter("##/####");
            dataValidadeField = new JFormattedTextField(dataFormatter);
        } catch (Exception e) {
            e.printStackTrace();
        }
        dataValidadeField.setPreferredSize(new Dimension(200, 40));
        estilizarCamposFormulario(dataValidadeField, fieldFont);
        gbc.gridx = 0;
        gbc.gridy = 5;
        camposPanel.add(dataValidadeField, gbc);

        // Campo Data de Fabricação
        JLabel dataFabricacaoLabel = new JLabel("Data de Fabricação (MM/yyyy)");
        dataFabricacaoLabel.setFont(labelFont);
        gbc.gridx = 1;
        gbc.gridy = 4;
        camposPanel.add(dataFabricacaoLabel, gbc);

        try {
            MaskFormatter dataFormatter = new MaskFormatter("##/####");
            dataFabricacaoField = new JFormattedTextField(dataFormatter);
        } catch (Exception e) {
            e.printStackTrace();
        }
        dataFabricacaoField.setPreferredSize(new Dimension(200, 40));
        estilizarCamposFormulario(dataFabricacaoField, fieldFont);
        gbc.gridx = 1;
        gbc.gridy = 5;
        camposPanel.add(dataFabricacaoField, gbc);

        // Campo Quantidade de Medida
        JLabel qntMedidaLabel = new JLabel("Quantidade de Medida (ex: 500mL, 2kg)");
        qntMedidaLabel.setFont(labelFont);
        gbc.gridx = 0;
        gbc.gridy = 6;
        camposPanel.add(qntMedidaLabel, gbc);

        qntMedidaField = new JTextField();
        qntMedidaField.setPreferredSize(new Dimension(200, 40));
        estilizarCamposFormulario(qntMedidaField, fieldFont);
        gbc.gridx = 0;
        gbc.gridy = 7;
        camposPanel.add(qntMedidaField, gbc);

        // Campo Embalagem
        JLabel embalagemLabel = new JLabel("Embalagem (ex: lata, frasco, pacote)");
        embalagemLabel.setFont(labelFont);
        gbc.gridx = 1;
        gbc.gridy = 6;
        camposPanel.add(embalagemLabel, gbc);

        embalagemField = new JTextField();
        embalagemField.setPreferredSize(new Dimension(200, 40));
        estilizarCamposFormulario(embalagemField, fieldFont);
        gbc.gridx = 1;
        gbc.gridy = 7;
        camposPanel.add(embalagemField, gbc);

        // Campo Categoria
        JLabel categoriaLabel = new JLabel("Categoria");
        categoriaLabel.setFont(labelFont);
        gbc.gridx = 0;
        gbc.gridy = 8;
        camposPanel.add(categoriaLabel, gbc);

        categoriaComboBox = new JComboBox<>(obterCategorias());
        categoriaComboBox.setPreferredSize(new Dimension(200, 40));
        estilizarComboBox(categoriaComboBox, fieldFont);
        gbc.gridx = 0;
        gbc.gridy = 9;
        camposPanel.add(categoriaComboBox, gbc);

        // Campo Fornecedor
        JLabel fornecedorLabel = new JLabel("Fornecedor");
        fornecedorLabel.setFont(labelFont);
        gbc.gridx = 1;
        gbc.gridy = 8;
        camposPanel.add(fornecedorLabel, gbc);

        fornecedorComboBox = new JComboBox<>(obterFornecedores());
        fornecedorComboBox.setPreferredSize(new Dimension(200, 40));
        estilizarComboBox(fornecedorComboBox, fieldFont);
        gbc.gridx = 1;
        gbc.gridy = 9;
        camposPanel.add(fornecedorComboBox, gbc);

        return camposPanel;
    }

    private JPanel criarBotoesPanel() {
        JPanel botoesPanel = new JPanel();
        botoesPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 40, 20));

        salvarButton = new JButton("SALVAR");
        salvarButton.setFont(new Font("Arial", Font.BOLD, 17));
        salvarButton.setBackground(new Color(24, 39, 55));
        salvarButton.setForeground(Color.WHITE);
        salvarButton.setFocusPainted(false);
        salvarButton.setPreferredSize(new Dimension(140, 35));
        botoesPanel.add(salvarButton);

        cancelarButton = new JButton("CANCELAR");
        cancelarButton.setFont(new Font("Arial", Font.BOLD, 17));
        cancelarButton.setBackground(Color.RED);
        cancelarButton.setForeground(Color.WHITE);
        cancelarButton.setFocusPainted(false);
        cancelarButton.setPreferredSize(new Dimension(140, 35));
        botoesPanel.add(cancelarButton);

        salvarButton.addActionListener(e -> salvarProduto());
        cancelarButton.addActionListener(e -> fecharJanela());

        return botoesPanel;
    }

    private void salvarProduto() {
        try {
            int idFuncionario = PainelSuperior.getIdFuncionarioAtual();

            String nome = nomeField.getText().trim();
            String descricao = descricaoField.getText().trim();
            String categoriaNome = (String) categoriaComboBox.getSelectedItem();
            String fornecedorNome = (String) fornecedorComboBox.getSelectedItem();
            String valorTexto = valorUnitarioField.getText().replace("R$", "").trim().replace(",", ".");
            String dataValidadeTexto = dataValidadeField.getText().trim();
            String dataFabricacaoTexto = dataFabricacaoField.getText().trim();
            String qntMedida = qntMedidaField.getText().trim();
            String embalagem = embalagemField.getText().trim();

            if (!validarCampos(nome, descricao, categoriaNome, fornecedorNome, valorTexto, dataValidadeTexto, dataFabricacaoTexto, qntMedida, embalagem)) {
                return;
            }

            BigDecimal valorUnitario = new BigDecimal(valorTexto);
            LocalDate dataValidade = YearMonth.parse(dataValidadeTexto, DateTimeFormatter.ofPattern("MM/yyyy")).atEndOfMonth();
            LocalDate dataFabricacao = YearMonth.parse(dataFabricacaoTexto, DateTimeFormatter.ofPattern("MM/yyyy")).atDay(1);

            if (dataValidade.isBefore(dataFabricacao)) {
                JOptionPane.showMessageDialog(this, "A data de validade não pode ser anterior à data de fabricação.", ERRO_TITULO, JOptionPane.ERROR_MESSAGE);
                return;
            }

            Categoria categoria = new Categoria();
            categoria.setNome(categoriaNome);

            Fornecedor fornecedor = new Fornecedor();
            fornecedor.setNome(fornecedorNome);

            Funcionario funcionario = new Funcionario();
            funcionario.setId(idFuncionario);

            Produto produto = new Produto();
            produto.setNome(nome);
            produto.setDescricao(descricao);
            produto.setValor(valorUnitario);
            produto.setDataValidade(dataValidade);
            produto.setDataFabricacao(dataFabricacao);
            produto.setQntMedida(qntMedida);
            produto.setEmbalagem(embalagem);
            produto.setCategorias(List.of(categoria));
            produto.setFornecedor(fornecedor);
            produto.setFuncionario(funcionario);

            try (Connection conn = ConexaoBD.getConnection()) {
                ProdutoController.cadastrarProduto(conn, produto);
                JOptionPane.showMessageDialog(this, "Produto cadastrado com sucesso!", SUCESSO_TITULO, JOptionPane.INFORMATION_MESSAGE);
                limparCampos();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Erro ao cadastrar produto: " + ex.getMessage(), ERRO_TITULO, JOptionPane.ERROR_MESSAGE);
            }
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, ERRO_DATA_VALIDADE_INVALIDA, ERRO_TITULO, JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, ERRO_VALOR_INVALIDO, ERRO_TITULO, JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro inesperado: " + ex.getMessage(), ERRO_TITULO, JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean validarCampos(String nome, String descricao, String categoria, String fornecedor, String valor, String dataValidade, String dataFabricacao, String qntMedida, String embalagem) {
        if (nome.isEmpty()) {
            JOptionPane.showMessageDialog(this, ERRO_NOME_VAZIO, ERRO_TITULO, JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (descricao.isEmpty()) {
            JOptionPane.showMessageDialog(this, ERRO_DESCRICAO_VAZIA, ERRO_TITULO, JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if ("Selecione".equals(categoria)) {
            JOptionPane.showMessageDialog(this, ERRO_CATEGORIA_NAO_SELECIONADA, ERRO_TITULO, JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if ("Selecione".equals(fornecedor)) {
            JOptionPane.showMessageDialog(this, ERRO_FORNECEDOR_NAO_SELECIONADO, ERRO_TITULO, JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (valor.isEmpty()) {
            JOptionPane.showMessageDialog(this, ERRO_VALOR_VAZIO, ERRO_TITULO, JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (dataValidade.isEmpty()) {
            JOptionPane.showMessageDialog(this, ERRO_DATA_VALIDADE_VAZIA, ERRO_TITULO, JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (dataFabricacao.isEmpty()) {
            JOptionPane.showMessageDialog(this, ERRO_DATA_FABRICACAO_VAZIA, ERRO_TITULO, JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (qntMedida.isEmpty()) {
            JOptionPane.showMessageDialog(this, ERRO_QNT_MEDIDA_VAZIA, ERRO_TITULO, JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (embalagem.isEmpty()) {
            JOptionPane.showMessageDialog(this, ERRO_EMBALAGEM_VAZIA, ERRO_TITULO, JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private void limparCampos() {
        nomeField.setText("");
        descricaoField.setText("");
        valorUnitarioField.setValue(null);
        dataValidadeField.setText("");
        dataFabricacaoField.setText("");
        qntMedidaField.setText("");
        embalagemField.setText("");
        categoriaComboBox.setSelectedIndex(0);
        fornecedorComboBox.setSelectedIndex(0);
    }

    private void fecharJanela() {
        Window janela = SwingUtilities.getWindowAncestor(this);
        if (janela != null) {
            janela.dispose();
        }
    }

    private String[] obterCategorias() {
        return new String[]{"Selecione", "Medicamentos", "Higiene e Beleza", "Suplementos alimentares", "Saúde e Bem-Estar", "Infantis", "Loções"};
    }

    private String[] obterFornecedores() {
        return new String[]{"Selecione", "Fornecedor 1", "Fornecedor 2", "Fornecedor 3"};
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
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
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
}