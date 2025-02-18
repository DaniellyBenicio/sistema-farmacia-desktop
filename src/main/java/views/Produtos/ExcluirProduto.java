package views.Produtos;

import javax.swing.*;

import controllers.Produto.ProdutoController;

import java.sql.Connection;
import java.sql.SQLException;
import dao.Produto.ProdutoDAO;
import models.Produto.Produto;
import main.ConexaoBD;

public class ExcluirProduto {

    public static void excluirProduto(int produtoId) {
        if (produtoId <= 0) {
            JOptionPane.showMessageDialog(null, "ID do produto inválido!", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Produto produto;

        try (Connection conn = ConexaoBD.getConnection()) {
            produto = ProdutoDAO.buscarPorId(conn, produtoId);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao recuperar produto.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (produto == null) {
            JOptionPane.showMessageDialog(null, "Produto não encontrado!", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String mensagemConfirmacao = "Você realmente deseja excluir o produto \"" + produto.getNome() + "\"?";

        Object[] opcoes = { "Sim", "Não" };

        int resposta = JOptionPane.showOptionDialog(null,
                mensagemConfirmacao,
                "Confirmar Exclusão",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE,
                null,
                opcoes,
                opcoes[0]);

        if (resposta == 0) {
            try (Connection conn = ConexaoBD.getConnection()) {
                ProdutoController.excluirProduto(conn, produto);
                JOptionPane.showMessageDialog(null, "Produto excluído com sucesso!", "Sucesso",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Erro ao excluir produto.", "Erro",
                        JOptionPane.ERROR_MESSAGE);
            }
        } else {
            System.out.println("Exclusão cancelada.");
        }
    }
}
