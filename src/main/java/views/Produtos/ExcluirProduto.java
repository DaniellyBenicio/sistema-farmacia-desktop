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

        try (Connection conn = ConexaoBD.getConnection()) {
            Produto produto = ProdutoDAO.buscarPorId(conn, produtoId);
            
            if (produto == null) {
                JOptionPane.showMessageDialog(null, "Produto não encontrado!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String mensagemConfirmacao = "Você realmente deseja excluir o produto \"" + produto.getNome() + "\"?";
            int resposta = JOptionPane.showConfirmDialog(null, mensagemConfirmacao, "Confirmar Exclusão", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (resposta == JOptionPane.YES_OPTION) {
                ProdutoController.excluirProduto(conn, produtoId);
                JOptionPane.showMessageDialog(null, "Produto excluído com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao excluir produto: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
