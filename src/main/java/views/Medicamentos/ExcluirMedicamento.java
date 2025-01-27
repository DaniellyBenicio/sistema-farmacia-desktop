package views.Medicamentos;

import javax.swing.*;
import java.sql.Connection;
import java.sql.SQLException;

import dao.Medicamento.MedicamentoDAO;
import models.Medicamento.Medicamento;
import main.ConexaoBD;

public class ExcluirMedicamento {

    public static void excluirMedicamento(int medicamentoId) {
        if (medicamentoId <= 0) {
            JOptionPane.showMessageDialog(null, "ID do medicamento inválido!", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Medicamento medicamento;

        try (Connection conn = ConexaoBD.getConnection()) {
            medicamento = MedicamentoDAO.buscarPorId(conn, medicamentoId); 
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao recuperar medicamento.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (medicamento == null) {
            JOptionPane.showMessageDialog(null, "Medicamento não encontrado!", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String mensagemConfirmacao = "Você realmente deseja excluir o medicamento \"" + medicamento.getNome() + "\"?";

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
                MedicamentoDAO.deletarMedicamento(conn, medicamento); 
                JOptionPane.showMessageDialog(null, "Medicamento excluído com sucesso!", "Sucesso",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Erro ao excluir medicamento.", "Erro",
                        JOptionPane.ERROR_MESSAGE);
            }
        } else {
            System.out.println("Exclusão cancelada.");
        }
    }
}
