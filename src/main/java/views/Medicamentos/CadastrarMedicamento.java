package views.Medicamentos;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.*;
import javax.swing.text.MaskFormatter;

public class CadastrarMedicamento extends JPanel {
    private JTextField nomeField;
    private JComboBox<String> categoriaComboBox;
    private JTextField fabricanteField;
    private JTextField dosagemField;
    private JComboBox<String> tipoComboBox;
    private JComboBox<String> fornecedorComboBox;
    private JFormattedTextField dataFabricacaoField;
    private JFormattedTextField dataValidadeField;
    private JTextField estoqueField;
    private JTextField valorUnitarioField;

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
        JPanel camposPanel = new JPanel();
        camposPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 30, 0, 10);
        gbc.anchor = GridBagConstraints.WEST;

        Font labelFont = new Font("Arial", Font.BOLD, 15);
        Font fieldFont = new Font("Arial", Font.PLAIN, 14);

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

        JLabel categoriaLabel = new JLabel("Categoria");
        categoriaLabel.setFont(labelFont);
        gbc.gridx = 1;
        gbc.gridy = 0;
        camposPanel.add(categoriaLabel, gbc);

        String[] categorias = { "Selecione", "Categoria 1", "Categoria 2", "Categoria 3" };
        categoriaComboBox = new JComboBox<>(categorias);
        categoriaComboBox.setPreferredSize(new Dimension(200, 40));
        estilizarComboBox(categoriaComboBox, fieldFont);
        gbc.gridx = 1;
        gbc.gridy = 1;
        camposPanel.add(categoriaComboBox, gbc);

        JLabel dosagemLabel = new JLabel("Dosagem");
        dosagemLabel.setFont(labelFont);
        gbc.gridx = 2;
        gbc.gridy = 0;
        camposPanel.add(dosagemLabel, gbc);

        dosagemField = new JTextField();
        dosagemField.setPreferredSize(new Dimension(170, 40));
        estilizarCamposFormulario(dosagemField, fieldFont);
        gbc.gridx = 2;
        gbc.gridy = 1;
        camposPanel.add(dosagemField, gbc);

        JLabel tipoLabel = new JLabel("Tipo");
        tipoLabel.setFont(labelFont);
        gbc.gridx = 3;
        gbc.gridy = 0;
        camposPanel.add(tipoLabel, gbc);

        String[] tipos = { "Selecione", "Tipo 1", "Tipo 2", "Tipo 3" };
        tipoComboBox = new JComboBox<>(tipos);
        tipoComboBox.setPreferredSize(new Dimension(170, 40));
        estilizarComboBox(tipoComboBox, fieldFont);
        gbc.gridx = 3;
        gbc.gridy = 1;
        camposPanel.add(tipoComboBox, gbc);

        JLabel fornecedorLabel = new JLabel("Fornecedor");
        fornecedorLabel.setFont(labelFont);
        gbc.gridx = 0;
        gbc.gridy = 2;
        camposPanel.add(fornecedorLabel, gbc);

        String[] fornecedores = { "Selecione", "Fornecedor 1", "Fornecedor 2", "Fornecedor 3" };
        fornecedorComboBox = new JComboBox<>(fornecedores);
        fornecedorComboBox.setPreferredSize(new Dimension(400, 40));
        estilizarComboBox(fornecedorComboBox, fieldFont);
        gbc.gridx = 0;
        gbc.gridy = 3;
        camposPanel.add(fornecedorComboBox, gbc);

        // Criação do Label para Forma Farmacêutica
        JLabel formaFarmaceuticaLabel = new JLabel("Forma Farmacêutica");
        formaFarmaceuticaLabel.setFont(labelFont);
        gbc.gridx = 1;
        gbc.gridy = 2;
        camposPanel.add(formaFarmaceuticaLabel, gbc);

        // Criação do ComboBox para formas farmacêuticas
        JComboBox<String> formaFarmaceuticaComboBox = new JComboBox<>(obterFormasFarmaceuticas());
        formaFarmaceuticaComboBox.setPreferredSize(new Dimension(200, 40));
        estilizarComboBox(formaFarmaceuticaComboBox, fieldFont);
        gbc.gridx = 1;
        gbc.gridy = 3;
        camposPanel.add(formaFarmaceuticaComboBox, gbc);

        // Criação do JTextField que ficará oculto inicialmente
        JTextField formaFarmaceuticaField = new JTextField();
        formaFarmaceuticaField.setPreferredSize(new Dimension(200, 40));
        estilizarCamposFormulario(formaFarmaceuticaField, fieldFont);
        formaFarmaceuticaField.setVisible(false); // Inicialmente invisível
        gbc.gridx = 1;
        gbc.gridy = 3;
        camposPanel.add(formaFarmaceuticaField, gbc);

        // Ação quando a seleção do JComboBox mudar
        formaFarmaceuticaComboBox.addActionListener(e -> {
            if ("Outros".equals(formaFarmaceuticaComboBox.getSelectedItem())) {
                formaFarmaceuticaComboBox.setVisible(false); // Oculta o ComboBox
                formaFarmaceuticaField.setVisible(true); // Mostra o campo de texto
                formaFarmaceuticaField.requestFocus(); // Set focus para o JTextField
            } else {
                formaFarmaceuticaField.setText(""); // Limpa o JTextField
                formaFarmaceuticaComboBox.setVisible(true); // Mostra novamente o ComboBox
                formaFarmaceuticaField.setVisible(false); // Oculta o JTextField
            }
        });
        JLabel receitaLabel = new JLabel("Receita");
        receitaLabel.setFont(labelFont);
        gbc.gridx = 2;
        gbc.gridy = 2;
        camposPanel.add(receitaLabel, gbc);

        String[] receitas = { "Selecione", "Simples", "Especial" };
        JComboBox<String> receitaComboBox = new JComboBox<>(receitas);
        receitaComboBox.setPreferredSize(new Dimension(170, 40));
        estilizarComboBox(receitaComboBox, fieldFont);
        gbc.gridx = 2;
        gbc.gridy = 3;
        camposPanel.add(receitaComboBox, gbc);

        JLabel estoqueLabel = new JLabel("Estoque");
        estoqueLabel.setFont(labelFont);
        gbc.gridx = 3;
        gbc.gridy = 2;
        camposPanel.add(estoqueLabel, gbc);

        estoqueField = new JTextField();
        estoqueField.setPreferredSize(new Dimension(170, 40));
        estilizarCamposFormulario(estoqueField, fieldFont);
        gbc.gridx = 3;
        gbc.gridy = 3;
        camposPanel.add(estoqueField, gbc);

        JLabel fabricanteLabel = new JLabel("Fabricante");
        fabricanteLabel.setFont(labelFont);
        gbc.gridx = 0;
        gbc.gridy = 4;
        camposPanel.add(fabricanteLabel, gbc);

        fabricanteField = new JTextField();
        fabricanteField.setPreferredSize(new Dimension(400, 40));
        estilizarCamposFormulario(fabricanteField, fieldFont);
        gbc.gridx = 0;
        gbc.gridy = 5;
        camposPanel.add(fabricanteField, gbc);

        JLabel dataFabricacaoLabel = new JLabel("Fabricação (Mês/Ano)");
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

        JLabel dataValidadeLabel = new JLabel("Validade (Mês/Ano)");
        dataValidadeLabel.setFont(labelFont);
        gbc.gridx = 2;
        gbc.gridy = 4;
        camposPanel.add(dataValidadeLabel, gbc);

        try {
            MaskFormatter dataFormatter = new MaskFormatter("##/####");
            dataValidadeField = new JFormattedTextField(dataFormatter);
        } catch (Exception e) {
            e.printStackTrace();
        }
        dataValidadeField.setPreferredSize(new Dimension(170, 40));
        estilizarCamposFormulario(dataValidadeField, fieldFont);
        gbc.gridx = 2;
        gbc.gridy = 5;
        camposPanel.add(dataValidadeField, gbc);

        JLabel valorUnitarioLabel = new JLabel("Valor Unitário (R$)");
        valorUnitarioLabel.setFont(labelFont);
        gbc.gridx = 3;
        gbc.gridy = 4;
        camposPanel.add(valorUnitarioLabel, gbc);

        try {
            MaskFormatter moedaFormatter = new MaskFormatter("R$ ##.##");
            valorUnitarioField = new JFormattedTextField(moedaFormatter);
        } catch (Exception e) {
            e.printStackTrace();
        }
        valorUnitarioField.setPreferredSize(new Dimension(170, 40));
        estilizarCamposFormulario(valorUnitarioField, fieldFont);
        gbc.gridx = 3;
        gbc.gridy = 5;
        camposPanel.add(valorUnitarioField, gbc);

        return camposPanel;
    }

    private String[] obterFormasFarmaceuticas() {
        // Criando uma lista mutável com todas as formas farmacêuticas
        List<String> formas = new ArrayList<>(Arrays.asList(
                "Comprimido", "Creme", "Pomada", "Injeção", "Xarope", "Solução",
                "Spray", "Cápsula", "Gel", "Loção", "Gelatina", "Supositório",
                "Pó", "Emulsão", "Colírio", "Gotejamento", "Aerossol",
                "Spray Nasal", "Pastilha", "Suspensão", "Pasta", "Sachê"));
        
        // Adicionando a opção "Outros" e "Selecione"
        formas.add(0, "Selecione"); // Colocando "Selecione" no início
        formas.add("Outros"); // Adicionando "Outros" ao final
    
        // Ordenando a lista de forma alfabética, excluindo "Selecione" e "Outros"
        List<String> formasOrdenadas = new ArrayList<>(formas.subList(1, formas.size() - 1)); // Exclui "Selecione" e "Outros"
        Collections.sort(formasOrdenadas, String.CASE_INSENSITIVE_ORDER);
    
        // Adicionando "Selecione" e "Outros" de volta à lista
        formasOrdenadas.add(0, "Selecione"); // Adicionando novamente "Selecione" no início
        formasOrdenadas.add("Outros"); // Adicionando "Outros" no final
    
        // Convertendo de volta para um array para usar no JComboBox
        return formasOrdenadas.toArray(new String[0]);
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
}