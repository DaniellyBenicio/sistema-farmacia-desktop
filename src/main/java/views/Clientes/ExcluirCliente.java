package views.Clientes;

import javax.swing.*;
import java.sql.Connection;
import java.sql.SQLException;

import dao.Cliente.ClienteDAO;
import models.Cliente.Cliente;
import main.ConexaoBD;

public class ExcluirCliente {

    public static void excluirCliente(int clienteId) {
        if (clienteId <= 0) {
            JOptionPane.showMessageDialog(null, "ID do cliente inválido!", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Cliente cliente;

        try (Connection conn = ConexaoBD.getConnection()) {
            cliente = ClienteDAO.clientePorID(conn, clienteId);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao recuperar cliente.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (cliente == null) {
            JOptionPane.showMessageDialog(null, "Cliente não encontrado!", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String mensagemConfirmacao = "Você realmente deseja excluir o cliente \"" + cliente.getNome() + "\"?";

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
                ClienteDAO.deletarCliente(conn, cliente);
                JOptionPane.showMessageDialog(null, "Cliente excluído com sucesso!", "Sucesso",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Erro ao excluir cliente.", "Erro",
                        JOptionPane.ERROR_MESSAGE);
            }
        } else {
            System.out.println("Exclusão cancelada.");
        }
    }
}