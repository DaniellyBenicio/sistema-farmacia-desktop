package views.Medicamentos;

import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.swing.*;
import javax.swing.text.MaskFormatter;

import controllers.Fabricante.FabricanteController;
import controllers.Fornecedor.FornecedorController;
import controllers.Medicamento.MedicamentoController;

import dao.Fornecedor.FornecedorDAO;
import dao.Funcionario.FuncionarioDAO;

import main.ConexaoBD;
import models.Categoria.Categoria;
import models.Fabricante.Fabricante;
import models.Fornecedor.Fornecedor;
import models.Funcionario.Funcionario;
import models.Medicamento.Medicamento;
import models.Medicamento.Medicamento.Tipo;
import models.Medicamento.Medicamento.TipoReceita;
import views.BarrasSuperiores.PainelSuperior;
import views.Fornecedor.CadastrarFornecedor;

public class CadastrarMedicamento extends JPanel {

    private JTextField nomedoMedicamentoField;
    private JComboBox<String> tipoComboBox;
    private JComboBox<String> embalagemComboBox;
    private JTextField quantidadeEmbalagemField;
    private JComboBox<String> categoriaComboBox;
    private JComboBox<String> fornecedorComboBox;
    private JComboBox<String> formaFarmaceuticaComboBox;
    private JComboBox<String> receitaComboBox;
    private JTextField dosagemField;
    private JTextField estoqueField;
    private JComboBox<String> fabricanteComboBox;
    private JFormattedTextField dataFabricacaoField;
    private JFormattedTextField dataValidadeField;
    private JTextField valorUnitarioField;

    private JTextField categoriaField;
    private JTextField embalagemField;
    private JTextField formaFarmaceuticaField;
    private JTextField fabricanteField;

    public CadastrarMedicamento() {
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
        JLabel titulo = new JLabel("CADASTRO DE MEDICAMENTO");
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        return titulo;
    }

    private JPanel criarCamposPanel() {
        JPanel camposPanel = new JPanel(new GridBagLayout());
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

        nomedoMedicamentoField = new JTextField();
        nomedoMedicamentoField.setPreferredSize(new Dimension(300, 40));
        estilizarCamposFormulario(nomedoMedicamentoField, fieldFont);
        gbc.gridx = 0;
        gbc.gridy = 1;
        camposPanel.add(nomedoMedicamentoField, gbc);

        JLabel tipoLabel = new JLabel("Tipo de Medicamento");
        tipoLabel.setFont(labelFont);
        gbc.gridx = 1;
        gbc.gridy = 0;
        camposPanel.add(tipoLabel, gbc);

        String[] tipos = { "Selecione", "Ético", "Genérico", "Similar" };
        tipoComboBox = new JComboBox<>(tipos);
        tipoComboBox.setPreferredSize(new Dimension(170, 40));
        estilizarComboBox(tipoComboBox, fieldFont);
        gbc.gridx = 1;
        gbc.gridy = 1;
        camposPanel.add(tipoComboBox, gbc);

        JLabel embalagemLabel = new JLabel("Embalagem");
        embalagemLabel.setFont(labelFont);
        gbc.gridx = 2;
        gbc.gridy = 0;
        camposPanel.add(embalagemLabel, gbc);

        String[] tiposdeEmbalagem = { "Selecione", "Ampola", "Caixa", "Envelope", "Frasco", "Pote", "Outros" };
        embalagemComboBox = new JComboBox<>(tiposdeEmbalagem);
        embalagemComboBox.setPreferredSize(new Dimension(150, 40));
        estilizarComboBox(embalagemComboBox, fieldFont);
        gbc.gridx = 2;
        gbc.gridy = 1;
        camposPanel.add(embalagemComboBox, gbc);

        embalagemField = new JTextField();
        embalagemField.setPreferredSize(new Dimension(150, 40));
        estilizarCamposFormulario(embalagemField, fieldFont);
        embalagemField.setVisible(false);
        gbc.gridx = 2;
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
        gbc.gridx = 3;
        gbc.gridy = 0;
        camposPanel.add(quantidadeEmbalagemLabel, gbc);

        quantidadeEmbalagemField = new JTextField();
        quantidadeEmbalagemField.setPreferredSize(new Dimension(150, 40));
        estilizarCamposFormulario(quantidadeEmbalagemField, fieldFont);
        gbc.gridx = 3;
        gbc.gridy = 1;
        camposPanel.add(quantidadeEmbalagemField, gbc);

        JLabel categoriaLabel = new JLabel("Categoria");
        categoriaLabel.setFont(labelFont);
        gbc.gridx = 4;
        gbc.gridy = 0;
        camposPanel.add(categoriaLabel, gbc);

        String[] categorias = obterCategorias();
        categoriaComboBox = new JComboBox<>(categorias);
        categoriaComboBox.setPreferredSize(new Dimension(200, 40));
        estilizarComboBox(categoriaComboBox, fieldFont);
        categoriaComboBox.setRenderer(new MultiLineComboBoxRenderer());
        gbc.gridx = 4;
        gbc.gridy = 1;
        camposPanel.add(categoriaComboBox, gbc);

        categoriaField = new JTextField();
        categoriaField.setPreferredSize(new Dimension(200, 40));
        estilizarCamposFormulario(categoriaField, fieldFont);
        categoriaField.setVisible(false);
        gbc.gridx = 4;
        gbc.gridy = 1;
        camposPanel.add(categoriaField, gbc);

        categoriaComboBox.addActionListener(e -> {
            if ("Outros".equals(categoriaComboBox.getSelectedItem())) {
                categoriaComboBox.setVisible(false);
                categoriaField.setVisible(true);
                categoriaField.requestFocus();
            } else {
                categoriaField.setText("");
                categoriaField.setVisible(false);
                categoriaComboBox.setVisible(true);
            }
        });

        JLabel fornecedorLabel = new JLabel("Fornecedor");
        fornecedorLabel.setFont(labelFont);
        gbc.gridx = 0;
        gbc.gridy = 2;
        camposPanel.add(fornecedorLabel, gbc);

        fornecedorComboBox = new JComboBox<>(obterFornecedores());
        fornecedorComboBox.setPreferredSize(new Dimension(300, 40));
        estilizarComboBox(fornecedorComboBox, fieldFont);
        gbc.gridx = 0;
        gbc.gridy = 3;
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

        JLabel formaFarmaceuticaLabel = new JLabel("Forma Farmacêutica");
        formaFarmaceuticaLabel.setFont(labelFont);
        gbc.gridx = 1;
        gbc.gridy = 2;
        camposPanel.add(formaFarmaceuticaLabel, gbc);

        String[] formasFarmautecia = obterFormasFarmaceuticas();
        formaFarmaceuticaComboBox = new JComboBox<>(formasFarmautecia);
        formaFarmaceuticaComboBox.setPreferredSize(new Dimension(170, 40));
        estilizarComboBox(formaFarmaceuticaComboBox, fieldFont);
        gbc.gridx = 1;
        gbc.gridy = 3;
        camposPanel.add(formaFarmaceuticaComboBox, gbc);

        formaFarmaceuticaField = new JTextField();
        formaFarmaceuticaField.setPreferredSize(new Dimension(170, 40));
        estilizarCamposFormulario(formaFarmaceuticaField, fieldFont);
        formaFarmaceuticaField.setVisible(false);
        gbc.gridx = 1;
        gbc.gridy = 3;
        camposPanel.add(formaFarmaceuticaField, gbc);

        formaFarmaceuticaComboBox.addActionListener(e -> {
            if ("Outros".equals(formaFarmaceuticaComboBox.getSelectedItem())) {
                formaFarmaceuticaComboBox.setVisible(false);
                formaFarmaceuticaField.setVisible(true);
                formaFarmaceuticaField.requestFocus();
            } else {
                formaFarmaceuticaField.setText("");
                formaFarmaceuticaComboBox.setVisible(true);
                formaFarmaceuticaField.setVisible(false);
            }
        });

        JLabel receitaLabel = new JLabel("Receita");
        receitaLabel.setFont(labelFont);
        gbc.gridx = 2;
        gbc.gridy = 2;
        camposPanel.add(receitaLabel, gbc);

        String[] receitas = { "Selecione", "Simples", "Especial" };
        receitaComboBox = new JComboBox<>(receitas);
        receitaComboBox.setPreferredSize(new Dimension(150, 40));
        estilizarComboBox(receitaComboBox, fieldFont);
        gbc.gridx = 2;
        gbc.gridy = 3;
        camposPanel.add(receitaComboBox, gbc);

        JLabel dosagemLabel = new JLabel("Dosagem");
        dosagemLabel.setFont(labelFont);
        gbc.gridx = 3;
        gbc.gridy = 2;
        camposPanel.add(dosagemLabel, gbc);

        dosagemField = new JTextField();
        dosagemField.setPreferredSize(new Dimension(150, 40));
        estilizarCamposFormulario(dosagemField, fieldFont);
        dosagemField.setText("Ex: mg, g, mcg, ml, l");
        dosagemField.setForeground(Color.GRAY);

        dosagemField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent evt) {
                if (dosagemField.getText().equals("Ex: mg, g, mcg, ml, l")) {
                    dosagemField.setText("");
                    dosagemField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent evt) {
                if (dosagemField.getText().isEmpty()) {
                    dosagemField.setText("Ex: mg, g, mcg, ml, l");
                    dosagemField.setForeground(Color.GRAY);
                }
            }
        });
        gbc.gridx = 3;
        gbc.gridy = 3;
        camposPanel.add(dosagemField, gbc);

        JLabel estoqueLabel = new JLabel("Estoque");
        estoqueLabel.setFont(labelFont);
        gbc.gridx = 4;
        gbc.gridy = 2;
        camposPanel.add(estoqueLabel, gbc);

        estoqueField = new JTextField();
        estoqueField.setPreferredSize(new Dimension(200, 40));
        estilizarCamposFormulario(estoqueField, fieldFont);
        gbc.gridx = 4;
        gbc.gridy = 3;
        camposPanel.add(estoqueField, gbc);

        JLabel fabricanteLabel = new JLabel("Fabricante");
        fabricanteLabel.setFont(labelFont);
        gbc.gridx = 0;
        gbc.gridy = 4;
        camposPanel.add(fabricanteLabel, gbc);

        fabricanteComboBox = new JComboBox<>(obterFabricantes());
        fabricanteComboBox.setPreferredSize(new Dimension(300, 40));
        estilizarComboBox(fabricanteComboBox, fieldFont);
        gbc.gridx = 0;
        gbc.gridy = 5;
        camposPanel.add(fabricanteComboBox, gbc);

        fabricanteField = new JTextField();
        fabricanteField.setPreferredSize(new Dimension(300, 40));
        estilizarCamposFormulario(fabricanteField, fieldFont);
        fabricanteField.setVisible(false);
        gbc.gridx = 0;
        gbc.gridy = 5;
        camposPanel.add(fabricanteField, gbc);

        fabricanteComboBox.addActionListener(e -> {
            String selectedItem = (String) fabricanteComboBox.getSelectedItem();
            if ("Outros".equals(selectedItem)) {
                fabricanteComboBox.setVisible(false);
                fabricanteField.setVisible(true);
                fabricanteField.requestFocus();
            } else {
                fabricanteField.setText("");
                fabricanteComboBox.setVisible(true);
                fabricanteField.setVisible(false);
            }
        });

        JLabel dataFabricacaoLabel = new JLabel("Fabricação (Mês/Ano)");
        dataFabricacaoLabel.setFont(labelFont);
        gbc.gridx = 1;
        gbc.gridy = 4;
        camposPanel.add(dataFabricacaoLabel, gbc);

        try {
            MaskFormatter dataFormatter = new MaskFormatter("##/####");
            dataFabricacaoField = new JFormattedTextField(dataFormatter);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        dataFabricacaoField.setPreferredSize(new Dimension(170, 40));
        estilizarCamposFormulario(dataFabricacaoField, fieldFont);
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
            dataValidadeField = new JFormattedTextField(dataFormatter);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        dataValidadeField.setPreferredSize(new Dimension(150, 40));
        estilizarCamposFormulario(dataValidadeField, fieldFont);
        gbc.gridx = 2;
        gbc.gridy = 5;
        camposPanel.add(dataValidadeField, gbc);

        JLabel valorUnitarioLabel = new JLabel("Valor Unitário (R$)");
        valorUnitarioLabel.setFont(labelFont);
        gbc.gridx = 3;
        gbc.gridy = 4;
        camposPanel.add(valorUnitarioLabel, gbc);

        valorUnitarioField = new JTextField();
        valorUnitarioField.setPreferredSize(new Dimension(150, 40));
        estilizarCamposFormulario(valorUnitarioField, fieldFont);
        gbc.gridx = 3;
        gbc.gridy = 5;
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

    private String[] obterCategorias() {
        List<String> categoriasPreDefinidas = new ArrayList<>(Arrays.asList(
                "Anestésicos Locais", "Analgésicos e Antitérmicos", "Antibióticos", "Antidiabéticos e Insulinas",
                "Antieméticos e Reguladores Digestivos", "Anti-inflamatórios", "Antialérgicos e Anti-histamínicos",
                "Antidepressivos e Estabilizadores de Humor", "Antifúngicos", "Anticonvulsivantes", "Antiparasitários",
                "Antivirais", "Ansiolíticos e Sedativos",
                "Corticosteroides", "Fitoterápicos", "Gastrointestinais", "Hipertensivos", "Hormônios e Endócrinos",
                "Relaxantes Musculares", "Psicotrópicos", "Vitaminas e Suplementos"));

        Set<String> categorias = new LinkedHashSet<>();
        categorias.add("Selecione");
        categorias.addAll(categoriasPreDefinidas);

        try (Connection conn = ConexaoBD.getConnection()) {
            List<String> categoriasDoBanco = MedicamentoController.listarCategoriasMedicamento(conn);
            categorias.addAll(categoriasDoBanco);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        categorias.add("Outros");

        return categorias.toArray(new String[0]);
    }

    private String[] obterFormasFarmaceuticas() {
        List<String> formasPreDefinidas = new ArrayList<>(Arrays.asList(
                "Aerossol", "Cápsula", "Colírio", "Comprimido", "Creme", "Emulsão",
                "Gel", "Gelatina", "Gotejamento", "Injeção", "Loção", "Pastilha",
                "Pasta", "Pomada", "Pó", "Sachê", "Solução", "Spray", "Spray Nasal",
                "Supositório", "Suspensão", "Xarope"));

        Set<String> formasFarmaceuticas = new LinkedHashSet<>();
        formasFarmaceuticas.add("Selecione");
        formasFarmaceuticas.addAll(formasPreDefinidas);

        try (Connection conn = ConexaoBD.getConnection()) {
            List<String> formasDoBanco = MedicamentoController.listarFormasFarmaceuticas(conn);
            formasFarmaceuticas.addAll(formasDoBanco);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        formasFarmaceuticas.add("Outros");

        return formasFarmaceuticas.toArray(new String[0]);
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

    private void atualizarCategorias() {
        String[] categoriasAtualizadas = obterCategorias();
        categoriaComboBox.removeAllItems();
        for (String categoria : categoriasAtualizadas) {
            categoriaComboBox.addItem(categoria);
        }
    }

    private void atualizarFormasFarmaceuticas() {
        String[] formasAtualizadas = obterFormasFarmaceuticas();
        formaFarmaceuticaComboBox.removeAllItems();
        for (String forma : formasAtualizadas) {
            formaFarmaceuticaComboBox.addItem(forma);
        }
    }

    private void atualizarFabricantes() {
        String[] fabricantesAtualizadas = obterFabricantes();
        fabricanteComboBox.removeAllItems();
        for (String fabricante : fabricantesAtualizadas) {
            fabricanteComboBox.addItem(fabricante);
        }
    }

    private void atualizarFornecedores() {
        String[] fornecedoresAtualizados = obterFornecedores();
        fornecedorComboBox.removeAllItems();
        for (String fornecedor : fornecedoresAtualizados) {
            fornecedorComboBox.addItem(fornecedor);
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

        cadastrarButton.addActionListener(e -> {
            try {
                int idFuncionario = PainelSuperior.getIdFuncionarioAtual();

                String nomeMedicamento = nomedoMedicamentoField.getText().trim().toLowerCase();
                String tipoNome = (String) tipoComboBox.getSelectedItem();
                String categoriaNome = (String) categoriaComboBox.getSelectedItem();
                String embalagem = (String) embalagemComboBox.getSelectedItem();
                String qntEmbalagemTexto = quantidadeEmbalagemField.getText().trim();
                String dosagem = dosagemField.getText().trim();
                String fornecedorNome = (String) fornecedorComboBox.getSelectedItem();
                String formaFarmaceuticaNome = (String) formaFarmaceuticaComboBox.getSelectedItem();
                String tipoReceitaNome = (String) receitaComboBox.getSelectedItem();
                String fabricanteNome = fabricanteField.getText().trim();
                String estoqueTexto = estoqueField.getText().trim();
                String dataFabricacaoTexto = dataFabricacaoField.getText().trim();
                String dataValidadeTexto = dataValidadeField.getText().trim();
                String valorTexto = valorUnitarioField.getText().replace("R$", "").trim().replace(",", ".");

                StringBuilder errorMessage = new StringBuilder("Por favor, corrija os seguintes erros: \n");
                boolean hasError = false;

                if (nomeMedicamento.isEmpty()) {
                    errorMessage.append("Nome deve ser preenchido.\n");
                    hasError = true;
                } else {
                    if (!nomeMedicamento.matches("^[\\p{L}\\d][\\p{L}\\d\\s]*$")) {
                        errorMessage.append(
                                "Nome inválido (apenas letras, números e espaços são permitidos, e não pode começar com espaço).\n");
                        hasError = true;
                    }
                }

                if ("Selecione".equals(tipoNome)) {
                    errorMessage.append("Tipo de medicamento deve ser selecionado.\n");
                    hasError = true;
                }

                if (embalagemComboBox.isVisible() && "Selecione".equals(embalagem)) {
                    errorMessage.append("Selecione a embalagem.\n");
                    hasError = true;
                }

                if (!embalagemComboBox.isVisible() && embalagemField.getText().trim().isEmpty()) {
                    errorMessage.append("Preencha a embalagem.\n");
                    hasError = true;
                }

                if (qntEmbalagemTexto.isEmpty()) {
                    errorMessage.append("Preencha a quantidade por embalagem.\n");
                    hasError = true;
                }

                int qntEmbalagem = 0;
                try {
                    qntEmbalagem = Integer.parseInt(qntEmbalagemTexto);
                    if (qntEmbalagem <= 0) {
                        errorMessage.append("A quantidade na embalagem deve ser um número inteiro positivo.\n");
                        hasError = true;
                    }
                } catch (NumberFormatException ex) {
                    errorMessage.append("Quantidade na embalagem inválida. Use apenas números inteiros.\n");
                    hasError = true;
                }

                if ("Outros".equals(categoriaNome)) {
                    categoriaNome = categoriaField.getText().trim();
                    if (categoriaNome.isEmpty()) {
                        errorMessage.append("Categoria deve ser preenchida.\n");
                        hasError = true;
                    }
                } else if ("Selecione".equals(categoriaNome)) {
                    errorMessage.append("Categoria deve ser selecionada.\n");
                    hasError = true;
                }

                if (dosagem.isEmpty()) {
                    errorMessage.append("Dosagem deve ser informada.\n");
                    hasError = true;
                } else if (!dosagem.toLowerCase().matches("\\d+(\\.\\d+)?(mg|g|mcg|ml|l)")) {
                    errorMessage.append("Informe a dosagem com as seguintes unidades válidas:\n" +
                            "(mg, g, mcg, ml, l).\n" +
                            "Exemplo: 500mg, 10g\n");
                    hasError = true;
                } else {
                    double dosagemValor = Double.parseDouble(dosagem.replaceAll("[^\\d.]", ""));
                    if (dosagemValor <= 0) {
                        errorMessage.append("A dosagem deve ser um valor positivo.\n");
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

                if ("Selecione".equals(formaFarmaceuticaNome)) {
                    errorMessage.append("Forma farmacêutica deve ser selecionada.\n");
                    hasError = true;
                } else if ("Outros".equals(formaFarmaceuticaNome)) {
                    formaFarmaceuticaNome = formaFarmaceuticaField.getText().trim();
                    if (formaFarmaceuticaNome.isEmpty()) {
                        errorMessage.append("Forma farmacêutica deve ser preenchida.\n");
                        hasError = true;
                    }
                }

                if ("Selecione".equals(tipoReceitaNome)) {
                    errorMessage.append("Tipo de receita deve ser informado.\n");
                    hasError = true;
                }

                int qntEstoque = 0;
                if (!estoqueTexto.isEmpty()) {
                    try {
                        qntEstoque = Integer.parseInt(estoqueTexto);
                        if (qntEstoque <= 0) {
                            errorMessage.append("A quantidade do estoque deve ser um número inteiro positivo.\n");
                            hasError = true;
                        }
                    } catch (NumberFormatException e1) {
                        errorMessage.append("Quantidade de estoque inválida. Use apenas números inteiros.\n");
                        hasError = true;
                    }
                } else {
                    errorMessage.append("Quantidade no estoque deve ser informada.\n");
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

                LocalDate dataFabricacao = null, dataValidade = null;
                if (dataFabricacaoTexto.equals("/") || dataValidadeTexto.equals("/")) {
                    errorMessage.append("As datas de fabricação e validade devem ser preenchidas corretamente.\n");
                    hasError = true;
                } else {
                    try {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yyyy");
                        YearMonth ymFabricacao = YearMonth.parse(dataFabricacaoTexto, formatter);
                        YearMonth ymValidade = YearMonth.parse(dataValidadeTexto, formatter);
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

                if (valorTexto.isEmpty()) {
                    errorMessage.append("O valor unitário deve ser informado.\n");
                    hasError = true;
                }

                if (hasError) {
                    JOptionPane.showMessageDialog(this, errorMessage.toString(), "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                BigDecimal valorUnitario;

                if (valorTexto.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "O valor unitário deve ser informado.", "Erro",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!valorTexto.matches("\\d+(\\.\\d{1,2})?")) {
                    JOptionPane.showMessageDialog(this, "Valor unitário deve ser um número válido.", "Erro",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                valorUnitario = new BigDecimal(valorTexto);

                if (valorUnitario.compareTo(BigDecimal.ZERO) <= 0) {
                    JOptionPane.showMessageDialog(this, "O valor unitário deve ser maior que zero.", "Erro",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Categoria categoria = new Categoria();
                categoria.setNome(categoriaNome);

                Fabricante fabricante = new Fabricante();
                fabricante.setNome(fabricanteNome);

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

                Tipo tipo = Tipo.valueOf(tipoNome.toUpperCase());
                TipoReceita tipoReceita = TipoReceita.valueOf(tipoReceitaNome.toUpperCase());

                Medicamento medicamento = new Medicamento(
                        nomeMedicamento,
                        dosagem,
                        formaFarmaceuticaNome,
                        valorUnitario,
                        dataValidade,
                        dataFabricacao,
                        tipoReceita,
                        qntEstoque,
                        tipo,
                        embalagem,
                        qntEmbalagem,
                        categoria,
                        fabricante,
                        fornecedor,
                        funcionario);

                try (Connection conn = ConexaoBD.getConnection()) {
                    MedicamentoController.cadastrarMedicamento(conn, medicamento);
                    JOptionPane.showMessageDialog(this, "Medicamento cadastrado com sucesso!", "Sucesso",
                            JOptionPane.INFORMATION_MESSAGE);

                    nomedoMedicamentoField.setText("");
                    tipoComboBox.setSelectedIndex(0);
                    embalagemComboBox.setSelectedIndex(0);
                    quantidadeEmbalagemField.setText("");
                    categoriaComboBox.setSelectedIndex(0);
                    dosagemField.setText("Ex: mg, g, mcg, ml, l");
                    dosagemField.setForeground(Color.GRAY);
                    fornecedorComboBox.setSelectedIndex(0);
                    formaFarmaceuticaComboBox.setSelectedIndex(0);
                    receitaComboBox.setSelectedIndex(0);
                    estoqueField.setText("");
                    fabricanteComboBox.setSelectedIndex(0);
                    dataFabricacaoField.setText("");
                    dataValidadeField.setText("");
                    valorUnitarioField.setText("");

                    categoriaField.setText("");
                    categoriaField.setVisible(false);
                    categoriaComboBox.setVisible(true);

                    embalagemField.setText("");
                    embalagemField.setVisible(false);
                    embalagemComboBox.setVisible(true);

                    formaFarmaceuticaField.setText("");
                    formaFarmaceuticaField.setVisible(false);
                    formaFarmaceuticaComboBox.setVisible(true);

                    fabricanteField.setText("");
                    fabricanteField.setVisible(false);
                    fabricanteComboBox.setVisible(true);

                    atualizarCategorias();
                    atualizarFormasFarmaceuticas();
                    atualizarFabricantes();
                    atualizarFornecedores();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Erro ao cadastrar medicamento: " + ex.getMessage(), "Erro",
                            JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro inesperado: " + ex.getMessage(), "Erro",
                        JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
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

    public class MultiLineComboBoxRenderer extends JLabel implements ListCellRenderer<String> {
        public MultiLineComboBoxRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends String> list, String value, int index,
                boolean isSelected, boolean cellHasFocus) {
            setFont(list.getFont());

            if (isSelected) {
                setBackground(new Color(24, 39, 55));
                setForeground(Color.WHITE);
            } else {
                setBackground(Color.WHITE);
                setForeground(Color.BLACK);
            }

            String[] words = value.split(" ");
            StringBuilder wrappedText = new StringBuilder("<html>");
            for (String word : words) {
                if (getFontMetrics(getFont()).stringWidth(wrappedText.toString() + word) > 200) {
                    wrappedText.append("<br>");
                }
                wrappedText.append(word).append(" ");
            }
            wrappedText.append("</html>");
            setText(wrappedText.toString());

            return this;
        }
    }
}