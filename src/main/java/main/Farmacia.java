package main;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.sql.Connection;
import java.sql.SQLException;

import views.BarrasSuperiores.PainelSuperior;
import views.Fornecedor.CadastrarFornecedor;
import views.Fornecedor.ListaDeFornecedores;
import views.Funcionario.CadastrarFuncionario;
import views.Funcionario.ListaDeFuncionarios;
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
            JOptionPane.showMessageDialog(this, "Erro de conexão com o banco de dados", "Erro", JOptionPane.ERROR_MESSAGE);
        }

        painelCentral.add(new RealizarVenda(), "Vendas");
        painelCentral.add(new CadastrarFornecedor(), "CadastrarFornecedor");
        painelCentral.add(new ListaDeFornecedores(conexão), "ListaDeFornecedores");
        painelCentral.add(new CadastrarFuncionario(), "CadastrarFuncionário");
        painelCentral.add(new ListaDeFuncionarios(conexão), "ListaDeFuncionarios");

        PainelSuperior painelSuperior = new PainelSuperior(layoutCartao, painelCentral);
        add(painelSuperior, BorderLayout.NORTH);
        add(painelCentral, BorderLayout.CENTER);

        layoutCartao.show(painelCentral, "Vendas"); 
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Farmacia janela = new Farmacia();
            janela.setVisible(true);
        });
    }
}