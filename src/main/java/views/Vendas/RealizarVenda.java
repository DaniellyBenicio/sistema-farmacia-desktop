package views.Vendas;

import javax.swing.*;
import java.awt.*;

public class RealizarVenda extends JPanel {
    private JLabel lblProduto, lblCodigo, lblQuantidade, lblPrecoUnitario, lblDesconto, lblPrecoTotal;
    private JTextField txtProduto, txtCodigo, txtQuantidade, txtPrecoUnitario, txtDesconto, txtPrecoTotal;

    public RealizarVenda() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        lblProduto = new JLabel("Produto");
        lblProduto.setFont(new Font("Arial", Font.BOLD, 18));
        lblProduto.setForeground(new Color(24, 39, 55));
        lblProduto.setAlignmentX(0.0f);
        add(lblProduto);

        txtProduto = new JTextField();
        txtProduto.setFont(new Font("Arial", Font.PLAIN, 25));
        txtProduto.setMaximumSize(new Dimension(2900, 50));
        txtProduto.setBackground(new Color(24, 39, 55));
        txtProduto.setForeground(Color.WHITE);
        txtProduto.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(24, 39, 55), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        txtProduto.setCaretColor(Color.WHITE);
        txtProduto.setAlignmentX(0.0f);
        add(txtProduto);

        lblCodigo = new JLabel("Código do Produto");
        lblCodigo.setFont(new Font("Arial", Font.BOLD, 18));
        lblCodigo.setForeground(new Color(24, 39, 55));
        lblCodigo.setAlignmentX(0.0f);
        lblCodigo.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        add(lblCodigo);

        txtCodigo = new JTextField();
        txtCodigo.setFont(new Font("Arial", Font.PLAIN, 25));
        txtCodigo.setMaximumSize(new Dimension(300, 50));
        txtCodigo.setBackground(Color.WHITE);
        txtCodigo.setForeground(Color.BLACK);

        txtCodigo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(24, 39, 55), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        txtCodigo.setCaretColor(Color.BLACK);
        txtCodigo.setAlignmentX(0.0f);
        add(txtCodigo);

        lblQuantidade = new JLabel("Quantidade");
        lblQuantidade.setFont(new Font("Arial", Font.BOLD, 18));
        lblQuantidade.setForeground(new Color(24, 39, 55));
        lblQuantidade.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblQuantidade.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        add(lblQuantidade);

        txtQuantidade = new JTextField();
        txtQuantidade.setFont(new Font("Arial", Font.PLAIN, 25));
        txtQuantidade.setMaximumSize(new Dimension(300, 50));
        txtQuantidade.setBackground(Color.WHITE);
        txtQuantidade.setForeground(Color.BLACK);
        txtQuantidade.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(24, 39, 55), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        txtQuantidade.setCaretColor(Color.BLACK);
        txtQuantidade.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(txtQuantidade);

        lblPrecoUnitario = new JLabel("Preço Unitário");
        lblPrecoUnitario.setFont(new Font("Arial", Font.BOLD, 18));
        lblPrecoUnitario.setForeground(new Color(24, 39, 55));
        lblPrecoUnitario.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblPrecoUnitario.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        add(lblPrecoUnitario);

        txtPrecoUnitario = new JTextField();
        txtPrecoUnitario.setFont(new Font("Arial", Font.PLAIN, 25));
        txtPrecoUnitario.setMaximumSize(new Dimension(300, 50));
        txtPrecoUnitario.setBackground(Color.WHITE);
        txtPrecoUnitario.setForeground(Color.BLACK);
        txtPrecoUnitario.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(24, 39, 55), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        txtPrecoUnitario.setCaretColor(Color.BLACK);
        txtPrecoUnitario.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(txtPrecoUnitario);

        lblDesconto = new JLabel("Desconto");
        lblDesconto.setFont(new Font("Arial", Font.BOLD, 18));
        lblDesconto.setForeground(new Color(24, 39, 55));
        lblDesconto.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblDesconto.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        add(lblDesconto);

        txtDesconto = new JTextField();
        txtDesconto.setFont(new Font("Arial", Font.PLAIN, 25));
        txtDesconto.setMaximumSize(new Dimension(300, 50));
        txtDesconto.setBackground(Color.WHITE);
        txtDesconto.setForeground(Color.BLACK);
        txtDesconto.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(24, 39, 55), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        txtDesconto.setCaretColor(Color.BLACK);
        txtDesconto.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(txtDesconto);

        lblPrecoTotal = new JLabel("Preço Total");
        lblPrecoTotal.setFont(new Font("Arial", Font.BOLD, 18));
        lblPrecoTotal.setForeground(new Color(24, 39, 55));
        lblPrecoTotal.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblPrecoTotal.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        add(lblPrecoTotal);

        txtPrecoTotal = new JTextField();
        txtPrecoTotal.setFont(new Font("Arial", Font.PLAIN, 25));
        txtPrecoTotal.setMaximumSize(new Dimension(300, 50));
        txtPrecoTotal.setBackground(Color.WHITE);
        txtPrecoTotal.setForeground(Color.BLACK);
        txtPrecoTotal.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(24, 39, 55), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        txtPrecoTotal.setCaretColor(Color.BLACK);
        txtPrecoTotal.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(txtPrecoTotal);
    }
}