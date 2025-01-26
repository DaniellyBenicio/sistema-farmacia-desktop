package main;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.sql.Connection;
import java.sql.SQLException;

import views.BarrasSuperiores.PainelSuperior;
import views.Clientes.ListaDeClientes;
import views.Fornecedor.CadastrarFornecedor;
import views.Fornecedor.ListaDeFornecedores;
import views.Funcionario.CadastrarFuncionario;
import views.Funcionario.ListaDeFuncionarios;
import views.Medicamentos.ListaDeMedicamentos;
import views.Vendas.RealizarVenda;

public class Farmacia extends JFrame {

    private CardLayout layoutCartao;
    private JPanel painelCentral;

    public Farmacia() {
        setTitle("Farmácia");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        layoutCartao = new CardLayout();
        painelCentral = new JPanel(layoutCartao);
        painelCentral.setBackground(Color.WHITE);

        Connection conexão = null;
        try {
            conexão = ConexaoBD.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro de conexão com o banco de dados", "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }

        // Adicionando painéis
        painelCentral.add(new RealizarVenda(), "Vendas");
        painelCentral.add(new CadastrarFornecedor(), "CadastrarFornecedor");
        painelCentral.add(new ListaDeFornecedores(conexão), "ListaDeFornecedores");
        painelCentral.add(new CadastrarFuncionario(), "CadastrarFuncionário");
        painelCentral.add(new ListaDeFuncionarios(conexão), "ListaDeFuncionarios");
        painelCentral.add(new ListaDeClientes(conexão), "ListaDeClientes");
        painelCentral.add(new ListaDeMedicamentos(conexão), "ListaDeMedicamentos");

        PainelSuperior painelSuperior = new PainelSuperior(layoutCartao, painelCentral);
        add(painelSuperior, BorderLayout.NORTH);
        add(painelCentral, BorderLayout.CENTER);

        layoutCartao.show(painelCentral, "Vendas");

        // Ajustando a responsividade
        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent evt) {
                ajusteLayout();
            }
        });
    }

    private void ajusteLayout() {
        // Método para ajustar o layout ou o tamanho dos componentes conforme necessário
        for (Component comp : painelCentral.getComponents()) {
            if (comp instanceof JComponent) {
                ((JComponent) comp).revalidate(); // Solicitar a revalidação do layout
                ((JComponent) comp).repaint(); // Repaint para atualização visual
            }
        }
        painelCentral.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Farmacia janela = new Farmacia();
            janela.setVisible(true);
        });
    }
}