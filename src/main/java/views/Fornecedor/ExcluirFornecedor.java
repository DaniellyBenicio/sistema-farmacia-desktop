package views.Fornecedor;

import javax.swing.*;
import java.sql.Connection;
import java.sql.SQLException;

import dao.Fornecedor.FornecedorDAO;
import models.Fornecedor.Fornecedor;
import main.ConexaoBD;

public class ExcluirFornecedor {

    public static void excluirFornecedor(int idFornecedor) {
        if (idFornecedor <= 0) {
            JOptionPane.showMessageDialog(null, "ID do fornecedor inválido!", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Fornecedor fornecedor;
        try (Connection conn = ConexaoBD.getConnection()) {
            fornecedor = FornecedorDAO.fornecedorPorId(conn, idFornecedor);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao recuperar fornecedor.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (fornecedor == null) {
            JOptionPane.showMessageDialog(null, "Fornecedor não encontrado!", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String mensagemConfirmacao = "Você realmente deseja excluir o fornecedor \"" + fornecedor.getNome() + "\"?";

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
                FornecedorDAO.deletarFornecedor(conn, fornecedor);
                JOptionPane.showMessageDialog(null, "Fornecedor excluído com sucesso!", "Sucesso",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Erro ao excluir fornecedor.", "Erro",
                        JOptionPane.ERROR_MESSAGE);
            }
        } else {
            System.out.println("Exclusão cancelada.");
        }
    }
}