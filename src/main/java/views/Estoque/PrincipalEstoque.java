package views.Estoque;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class PrincipalEstoque extends JPanel {

    public PrincipalEstoque() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JPanel painelSuperior = criarTitulo();
        JPanel painelBotoes = criarBotoes();

        add(painelSuperior, BorderLayout.NORTH);
        add(painelBotoes, BorderLayout.CENTER);
    }

    private JPanel criarTitulo() {
        JPanel painelTitulo = new JPanel();
        painelTitulo.setLayout(new BoxLayout(painelTitulo, BoxLayout.Y_AXIS));
        painelTitulo.setBorder(BorderFactory.createEmptyBorder(100, 0, 35, 0));
        painelTitulo.setBackground(Color.WHITE);

        JLabel titulo = new JLabel("GERENCIAMENTO DE ESTOQUE");
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        painelTitulo.add(titulo);
        painelTitulo.add(Box.createVerticalStrut(20));

        return painelTitulo;
    }

    private JPanel criarBotoes() {
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 80, 0));
        painelBotoes.setBorder(BorderFactory.createEmptyBorder(90, 10, 0, 50));
        painelBotoes.setBackground(Color.WHITE);

        JButton btnMedicamento = criarBotao("ESTOQUE DE MEDICAMENTO");
        JButton btnProduto = criarBotao("ESTOQUE DE PRODUTO");

        btnMedicamento.addActionListener(e -> abrirEstoqueMedicamento());
        btnProduto.addActionListener(e -> abrirEstoqueProduto());

        painelBotoes.add(btnMedicamento);
        painelBotoes.add(btnProduto);

        return painelBotoes;
    }

    private JButton criarBotao(String texto) {
        JButton botao = new JButton(texto);

        botao.setFont(new Font("Arial", Font.BOLD, 16));
        botao.setBackground(new Color(24, 39, 55));
        botao.setForeground(Color.WHITE);
        botao.setFocusPainted(false);
        botao.setPreferredSize(new Dimension(280, 40));

        botao.setHorizontalTextPosition(SwingConstants.LEFT);
        botao.setVerticalTextPosition(SwingConstants.CENTER);

        return botao;
    }

    private void abrirEstoqueMedicamento() {
        this.removeAll();

        EstoqueMedicamento estoqueMedicamento = new EstoqueMedicamento();
        this.add(estoqueMedicamento, BorderLayout.CENTER);
        this.revalidate();
        this.repaint();
    }

    private void abrirEstoqueProduto() {
        this.removeAll();

        EstoqueProduto estoqueProduto = new EstoqueProduto();
        this.add(estoqueProduto, BorderLayout.CENTER);
        this.revalidate();
        this.repaint();
    }
}