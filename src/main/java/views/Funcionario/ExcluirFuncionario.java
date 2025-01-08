package views.Funcionario;

import dao.Funcionario.FuncionarioDAO;
import models.Funcionario.Funcionario;

import javax.swing.*;
import java.sql.Connection;
import java.sql.SQLException;

public class ExcluirFuncionario {
    private Connection conn;

    public ExcluirFuncionario(Connection conn) {
        this.conn = conn;
    }

    public void excluirFuncionario(int idFuncionario, Runnable onSuccess) {
        if (idFuncionario <= 0) {
            JOptionPane.showMessageDialog(null, "ID do funcionário inválido!", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Funcionario funcionario;
        try {
            funcionario = FuncionarioDAO.funcionarioPorId(conn, idFuncionario);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao recuperar funcionário.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (funcionario == null) {
            JOptionPane.showMessageDialog(null, "Funcionário não encontrado!", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String mensagemConfirmacao;
        if (funcionario.getCargo() != null && "Gerente".equalsIgnoreCase(funcionario.getCargo().getNome())) {
            if (funcionario.isStatus()) {
                mensagemConfirmacao = "Deseja desativar o gerente \"" + funcionario.getNome() + "\"?";
            } else {
                mensagemConfirmacao = "Deseja ativar o gerente \"" + funcionario.getNome() + "\"?";
            }
        } else {
            mensagemConfirmacao = "Você realmente deseja excluir o funcionário \"" + funcionario.getNome() + "\"?";
        }

        Object[] opcoes = { "Sim", "Não" };
        int resposta = JOptionPane.showOptionDialog(null,
                mensagemConfirmacao,
                "Confirmar Exclusão",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE,
                null,
                opcoes,
                opcoes[0]);

        if (resposta == JOptionPane.YES_OPTION) {
            try {
                if (funcionario.getCargo() != null && "Gerente".equalsIgnoreCase(funcionario.getCargo().getNome())) {
                    if (funcionario.isStatus()) {
                        FuncionarioDAO.desativarGerente(conn, funcionario);
                        JOptionPane.showMessageDialog(null, "Gerente desativado com sucesso!", "Sucesso",
                                JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        FuncionarioDAO.ativarGerente(conn, funcionario);
                        JOptionPane.showMessageDialog(null, "Gerente ativado com sucesso!", "Sucesso",
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {
                    FuncionarioDAO.deletarFuncionario(conn, funcionario);
                    JOptionPane.showMessageDialog(null, "Funcionário excluído com sucesso!", "Sucesso",
                            JOptionPane.INFORMATION_MESSAGE);
                }

                // Executa a operação de sucesso, se fornecida
                if (onSuccess != null) {
                    onSuccess.run();
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Erro ao processar a operação.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } else if (resposta == 1) {
            System.out.println("Operação cancelada.");
        } else {
            System.out.println("Diálogo fechado sem seleção.");
        }
    }
}