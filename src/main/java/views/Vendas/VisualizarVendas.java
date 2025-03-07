package views.Vendas;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.sql.Connection;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class VisualizarVendas extends JPanel {

    private Connection conn;
    private CardLayout layoutCartao;
    private JPanel painelCentral;
    private JPanel painelCentralParam;

    public VisualizarVendas(Connection conn, TelaInicialVendas telaInicialVendas, CardLayout layoutCartao,
            JPanel painelCentral) {
        this.conn = conn;
        this.layoutCartao = layoutCartao;
        this.painelCentral = painelCentral;
        this.painelCentralParam = painelCentral;

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JPanel painelSuperior = criarTituloEBusca();
        add(painelSuperior, BorderLayout.NORTH);
    }

    private JPanel criarTituloEBusca() {
        JPanel painelSuperior = new JPanel(new BorderLayout());
        JPanel painelVoltar = new JPanel();
        painelVoltar.setLayout(new FlowLayout(FlowLayout.LEFT));

        JButton voltar = new JButton("Voltar");
        voltar.setFont(new Font("Arial", Font.PLAIN, 17));
        voltar.setBorder(null);
        voltar.setContentAreaFilled(false);
        voltar.setFocusPainted(false);
        voltar.setPreferredSize(new Dimension(90, 30));
        voltar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        voltar.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                voltar.setForeground(new Color(50, 100, 150));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                voltar.setForeground(Color.BLACK);
            }
        });

        voltar.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> {
                layoutCartao.show(painelCentralParam, "TelaInicialVendas");
            });
        });

        painelVoltar.add(voltar);
        painelSuperior.add(painelVoltar, BorderLayout.WEST);

        return painelSuperior;
    }
}
