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
import views.Estoque.EstoqueMedicamento;
import views.Estoque.EstoqueProduto;
import views.Estoque.PrincipalEstoque;
import views.Fornecedor.CadastrarFornecedor;
import views.Fornecedor.ListaDeFornecedores;
import views.Funcionario.CadastrarFuncionario;
import views.Funcionario.ListaDeFuncionarios;
import views.Medicamentos.ListaDeMedicamentos;
import views.Produtos.ListaDeProdutos;
import views.Vendas.RealizarVenda;
import views.Vendas.TelaInicialVendas;
import views.Vendas.VisualizarVendas;

public class Farmacia extends JFrame {

    private CardLayout layoutCartao;
    private JPanel painelCentral;
    private PrincipalEstoque principalEstoque;
    private EstoqueMedicamento estoqueMedicamento;
    private EstoqueProduto estoqueProduto;
    private TelaInicialVendas principalVendas;
    private RealizarVenda realizarVenda;
    private VisualizarVendas visualizarVendas;

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
        principalEstoque = new PrincipalEstoque(conexão, layoutCartao, painelCentral);
        estoqueMedicamento = new EstoqueMedicamento(conexão, principalEstoque, layoutCartao, painelCentral);
        estoqueProduto = new EstoqueProduto(conexão, principalEstoque, layoutCartao, painelCentral);

        principalVendas = new TelaInicialVendas(conexão, layoutCartao, painelCentral);
        realizarVenda = new RealizarVenda(conexão, principalVendas, layoutCartao, painelCentral);
        visualizarVendas = new VisualizarVendas(conexão, principalVendas, layoutCartao, painelCentral);

        painelCentral.add(new CadastrarFornecedor(), "CadastrarFornecedor");
        painelCentral.add(new ListaDeFornecedores(conexão), "ListaDeFornecedores");
        painelCentral.add(new CadastrarFuncionario(), "CadastrarFuncionário");
        painelCentral.add(new ListaDeFuncionarios(conexão), "ListaDeFuncionarios");
        painelCentral.add(new ListaDeClientes(conexão), "ListaDeClientes");
        painelCentral.add(new ListaDeMedicamentos(conexão), "ListaDeMedicamentos");
        painelCentral.add(new ListaDeProdutos(conexão), "ListaDeProdutos");
        painelCentral.add(principalEstoque, "GerenciamentoDeEstoque");
        painelCentral.add(estoqueMedicamento, "EstoqueMedicamento");
        painelCentral.add(estoqueProduto, "EstoqueProduto");
        painelCentral.add(principalVendas, "TelaInicialVendas");
        painelCentral.add(realizarVenda, "RealizarVenda");
        painelCentral.add(visualizarVendas, "VisualizarVendas");

        PainelSuperior painelSuperior = new PainelSuperior(layoutCartao, painelCentral);
        add(painelSuperior, BorderLayout.NORTH);
        add(painelCentral, BorderLayout.CENTER);

        layoutCartao.show(painelCentral, "RealizarVenda");

        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent evt) {
                ajusteLayout();
            }
        });
    }

    private void ajusteLayout() {
        for (Component comp : painelCentral.getComponents()) {
            if (comp instanceof JComponent) {
                ((JComponent) comp).revalidate();
                ((JComponent) comp).repaint();
            }
        }
        painelCentral.repaint();
    }

    public CardLayout getLayoutCartao() {
        return layoutCartao;
    }

    public JPanel getPainelCentral() {
        return painelCentral;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Farmacia janela = new Farmacia();
            janela.setVisible(true);
        });
    }
}