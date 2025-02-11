package views.Produtos;

import javax.swing.*;
import controllers.Categoria.CategoriaController;
import controllers.Produto.ProdutoController;
import main.ConexaoBD;
import models.Categoria.Categoria;
import models.Produto.Produto;
import models.Fabricante.Fabricante;
import models.Fornecedor.Fornecedor;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class EditarProduto extends JPanel {
    private JTextField nomeField;
    private JComboBox<String> categoriaComboBox;
    private JComboBox<String> fornecedorComboBox;
    private JTextField validadeField;
    private JTextField fabricacaoField;
    private JTextField estoqueField;
    private JTextField qntMedidaField;
    private JTextField embalagemField;
    private JTextField valorUnitarioField;
    private JButton salvarButton;
    private JButton cancelarButton;

    public EditarProduto(int produtoId) {
        setLayout(new BorderLayout());

        JPanel camposPanel = new JPanel();
        camposPanel.setLayout(new GridLayout(8, 2, 10, 10));

        camposPanel.add(new JLabel("Nome:"));
        nomeField = new JTextField();
        camposPanel.add(nomeField);

        camposPanel.add(new JLabel("Categoria:"));
        categoriaComboBox = new JComboBox<>(obterCategorias());
        camposPanel.add(categoriaComboBox);

        camposPanel.add(new JLabel("Fornecedor:"));
        fornecedorComboBox = new JComboBox<>(obterFornecedores());
        camposPanel.add(fornecedorComboBox);

        camposPanel.add(new JLabel("Validade (MM/yyyy):"));
        validadeField = new JTextField();
        camposPanel.add(validadeField);

        camposPanel.add(new JLabel("Fabricação (MM/yyyy):"));
        fabricacaoField = new JTextField();
        camposPanel.add(fabricacaoField);

        camposPanel.add(new JLabel("Estoque:"));
        estoqueField = new JTextField();
        camposPanel.add(estoqueField);

        camposPanel.add(new JLabel("Quantidade Medida:"));
        qntMedidaField = new JTextField();
        camposPanel.add(qntMedidaField);

        camposPanel.add(new JLabel("Embalagem:"));
        embalagemField = new JTextField();
        camposPanel.add(embalagemField);

        camposPanel.add(new JLabel("Valor Unitário:"));
        valorUnitarioField = new JTextField();
        camposPanel.add(valorUnitarioField);

        JPanel botoesPanel = criarBotoesPanel(produtoId);

        add(camposPanel, BorderLayout.CENTER);
        add(botoesPanel, BorderLayout.SOUTH);

        carregarDadosProduto(produtoId);
    }

    private JPanel criarBotoesPanel(int produtoId) {
        JPanel botoesPanel = new JPanel();
        botoesPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 40, 20));

        cancelarButton = new JButton("CANCELAR");
        cancelarButton.addActionListener(e -> SwingUtilities.getWindowAncestor(this).dispose());
        botoesPanel.add(cancelarButton);

        salvarButton = new JButton("SALVAR");
        salvarButton.addActionListener(e -> salvarProduto(produtoId));
        botoesPanel.add(salvarButton);

        return botoesPanel;
    }

    private void carregarDadosProduto(int produtoId) {
        try (Connection conn = ConexaoBD.getConnection()) {
            Produto produto = ProdutoController.buscarProdutoPorId(conn, produtoId);
            if (produto != null) {
                nomeField.setText(produto.getNome());
                categoriaComboBox.setSelectedItem(produto.getCategorias().get(0).getNome());
                fornecedorComboBox.setSelectedItem(produto.getFornecedor().getNome());
                validadeField.setText(produto.getDataValidade().format(DateTimeFormatter.ofPattern("MM/yyyy")));
                fabricacaoField.setText(produto.getDataFabricacao().format(DateTimeFormatter.ofPattern("MM/yyyy")));
                estoqueField.setText(String.valueOf(produto.getQntEstoque()));
                qntMedidaField.setText(produto.getQntMedida());
                embalagemField.setText(produto.getEmbalagem());
                valorUnitarioField.setText(produto.getValor().toString());
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar dados do Produto.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String[] obterCategorias() {
        try (Connection conn = ConexaoBD.getConnection()) {
            ArrayList<String> categorias = CategoriaController.listarTodasCategorias(conn);
            categorias.add(0, "Selecione");
            return categorias.toArray(new String[0]);
        } catch (SQLException e) {
            return new String[] { "Selecione" };
        }
    }

    private String[] obterFornecedores() {
        return new String[]{"Selecione", "Fornecedor 1", "Fornecedor 2", "Fornecedor 3"};
    }
}
