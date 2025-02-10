package views.Estoque;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class EstoqueProduto extends JPanel {

    public EstoqueProduto() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JPanel painelSuperior = criarTitulo();
        add(painelSuperior, BorderLayout.NORTH);
    }

    private JPanel criarTitulo() {
        JPanel painelTitulo = new JPanel();
        painelTitulo.setLayout(new BoxLayout(painelTitulo, BoxLayout.Y_AXIS));
        painelTitulo.setBorder(BorderFactory.createEmptyBorder(50, 0, 35, 0));
        painelTitulo.setBackground(Color.WHITE);

        JLabel titulo = new JLabel("ESTOQUE DE PRODUTOS");
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        painelTitulo.add(titulo);
        painelTitulo.add(Box.createVerticalStrut(20));

        return painelTitulo;
    }
}