package views.Vendas;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;

public class RealizarVenda extends JPanel {
    private JLabel lblItem, lblCodigoProduto, lblQuantidade, lblPrecoUnitario, lblDesconto, lblPrecoTotal;
    private JTextField txtItem, txtCodigoProduto, txtQuantidade, txtPrecoUnitario, txtDesconto, txtPrecoTotal;
    private JPanel painelResumoVenda;
    private JTextArea txtResumoVenda;

    public RealizarVenda() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        lblItem = new JLabel("Item");
        lblItem.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 30, 5, 5);
        add(lblItem, gbc);

        txtItem = createTextFieldItem();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(5, 30, 10, 30);
        add(txtItem, gbc);

        lblCodigoProduto = new JLabel("Código do Produto");
        lblCodigoProduto.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 30, 5, 5);
        add(lblCodigoProduto, gbc);

        txtCodigoProduto = createTextFieldOutrosCampos();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.insets = new Insets(5, 30, 10, 30);
        add(txtCodigoProduto, gbc);

        lblQuantidade = new JLabel("Quantidade");
        lblQuantidade.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 30, 5, 5);
        add(lblQuantidade, gbc);

        txtQuantidade = createTextFieldOutrosCampos();
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.insets = new Insets(5, 30, 10, 30);
        add(txtQuantidade, gbc);

        lblPrecoUnitario = new JLabel("Preço Unitário");
        lblPrecoUnitario.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 30, 5, 5);
        add(lblPrecoUnitario, gbc);

        txtPrecoUnitario = createTextFieldOutrosCampos();
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.insets = new Insets(5, 30, 10, 30);
        add(txtPrecoUnitario, gbc);

        lblDesconto = new JLabel("Desconto");
        lblDesconto.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 30, 5, 5);
        add(lblDesconto, gbc);

        txtDesconto = createTextFieldOutrosCampos();
        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.insets = new Insets(5, 30, 10, 30);
        add(txtDesconto, gbc);

        lblPrecoTotal = new JLabel("Preço Total");
        lblPrecoTotal.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 10;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 30, 5, 5);
        add(lblPrecoTotal, gbc);

        txtPrecoTotal = createTextFieldOutrosCampos();
        gbc.gridx = 0;
        gbc.gridy = 11;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.insets = new Insets(5, 30, 10, 30);
        add(txtPrecoTotal, gbc);

        JButton identificarCliente = new JButton("Identificar Cliente");
        identificarCliente.setFont(new Font("Arial", Font.BOLD, 18));
        identificarCliente.setBackground(new Color(24, 39, 55));
        identificarCliente.setForeground(Color.WHITE);
        identificarCliente.setFocusPainted(false);
        identificarCliente.setPreferredSize(new Dimension(200, 40));

        JButton cancelarVenda = new JButton("Cancelar Venda");
        cancelarVenda.setFont(new Font("Arial", Font.BOLD, 18));
        cancelarVenda.setBackground(Color.RED);
        cancelarVenda.setForeground(Color.WHITE);
        cancelarVenda.setFocusPainted(false);
        cancelarVenda.setPreferredSize(new Dimension(200, 40));

        JButton confirmarVenda = new JButton("Confirmar Venda");
        confirmarVenda.setFont(new Font("Arial", Font.BOLD, 18));
        confirmarVenda.setBackground(new Color(0, 133, 0));
        confirmarVenda.setForeground(Color.WHITE);
        confirmarVenda.setFocusPainted(false);
        confirmarVenda.setPreferredSize(new Dimension(200, 40));

        JLabel lblTotal = new JLabel("Total");
        lblTotal.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 11;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 30, 5, 5);
        add(lblTotal, gbc);

        JTextField txtTotal = new JTextField();
        txtTotal.setBackground(Color.WHITE);
        txtTotal.setForeground(Color.BLACK);
        txtTotal.setOpaque(true);
        txtTotal.setFont(new Font("Arial", Font.PLAIN, 20));
        txtTotal.setPreferredSize(new Dimension(200, 40));
        txtTotal.setEditable(false);

        gbc.gridx = 0;
        gbc.gridy = 12;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(5, 30, 5, 30);
        add(txtTotal, gbc);

        JPanel panelBotoes = new JPanel();
        panelBotoes.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 20));

        panelBotoes.add(identificarCliente);
        panelBotoes.add(Box.createRigidArea(new Dimension(30, 0)));
        panelBotoes.add(cancelarVenda);
        panelBotoes.add(Box.createRigidArea(new Dimension(30, 0)));
        panelBotoes.add(confirmarVenda);

        gbc.gridx = 0;
        gbc.gridy = 12;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 30, 10, 30);
        add(panelBotoes, gbc);

        AbstractDocument doc = (AbstractDocument) txtItem.getDocument();
        doc.setDocumentFilter(new UpperCaseDocumentFilter());
    }

    private JTextField createTextFieldItem() {
        JTextField textField = new JTextField();
        textField.setBackground(new Color(24, 39, 55));
        textField.setForeground(Color.WHITE);
        textField.setOpaque(true);
        textField.setFont(new Font("Arial", Font.PLAIN, 20));
        textField.setPreferredSize(new Dimension(300, 45));
        return textField;
    }

    private JTextField createTextFieldOutrosCampos() {
        JTextField textField = new JTextField();
        textField.setBackground(Color.WHITE);
        textField.setForeground(Color.BLACK);
        textField.setOpaque(true);
        textField.setFont(new Font("Arial", Font.PLAIN, 20));
        textField.setPreferredSize(new Dimension(200, 45));

        if (textField == txtQuantidade) {
            AbstractDocument doc = (AbstractDocument) textField.getDocument();
            doc.setDocumentFilter(new IntegerDocumentFilter());
        } else if (textField == txtPrecoUnitario || textField == txtDesconto || textField == txtPrecoTotal) {
            AbstractDocument doc = (AbstractDocument) textField.getDocument();
            doc.setDocumentFilter(new DecimalDocumentFilter());
        }

        return textField;
    }

    private void atualizarResumoVenda() {
        String item = txtItem.getText();
        String codigo = txtCodigoProduto.getText();
        String quantidade = txtQuantidade.getText();
        String precoUnitario = txtPrecoUnitario.getText();
        String desconto = txtDesconto.getText();
        String precoTotal = txtPrecoTotal.getText();

        String linha = String.format("%s - %s | Qtd: %s | Preço Unit: %s | Desconto: %s | Total: %s\n", 
                item, codigo, quantidade, precoUnitario, desconto, precoTotal);

        txtResumoVenda.append(linha);
        txtItem.setText("");
        txtCodigoProduto.setText("");
        txtQuantidade.setText("");
        txtPrecoUnitario.setText("");
        txtDesconto.setText("");
        txtPrecoTotal.setText("");
    }

    private static class DecimalDocumentFilter extends DocumentFilter {
        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            String result = string.replaceAll("[^0-9,]", "");
            super.insertString(fb, offset, result, attr);
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String string, AttributeSet attrs) throws BadLocationException {
            String result = string.replaceAll("[^0-9,]", "");
            super.replace(fb, offset, length, result, attrs);
        }
    }

    private static class IntegerDocumentFilter extends DocumentFilter {
        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            String result = string.replaceAll("[^0-9]", "");
            super.insertString(fb, offset, result, attr);
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String string, AttributeSet attrs) throws BadLocationException {
            String result = string.replaceAll("[^0-9]", "");
            super.replace(fb, offset, length, result, attrs);
        }
    }

    private static class UpperCaseDocumentFilter extends DocumentFilter {
        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            super.insertString(fb, offset, string.toUpperCase(), attr);
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String string, AttributeSet attrs) throws BadLocationException {
            super.replace(fb, offset, length, string.toUpperCase(), attrs);
        }
    }
}
