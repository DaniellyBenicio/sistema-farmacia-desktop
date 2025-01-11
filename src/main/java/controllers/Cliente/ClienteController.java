package controllers.Cliente;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import dao.Cliente.ClienteDAO;
import models.Cliente.Cliente;

public class ClienteController {
    public static void cadastrarCliente(Connection conn, Cliente c) throws SQLException {
        try {
            ClienteDAO.cadastrarCliente(conn, c);
        } catch (SQLException e) {
            throw new SQLException("Erro ao cadastrar cliente.", e);
        }
    }

    public static void atualizarCliente(Connection conn, Cliente c) throws SQLException {
        if (c == null || c.getId() <= 0) {
            throw new IllegalArgumentException("Cliente ou ID inválido.");
        }

        try {
            ClienteDAO.atualizarCliente(conn, c);
            System.out.println("Cliente atualizado com sucesso.");
        } catch (SQLException e) {
            throw new SQLException("Erro ao atualizar cliente.", e);
        }
    }

    public static Cliente buscarClientePorId(Connection conn, int id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("ID inválido.");
        }

        try {
            Cliente c = ClienteDAO.clientePorID(conn, id);
            if (c == null) {
                System.out.println("Cliente não encontrado.");
                return null;
            }
            return c;
        } catch (SQLException e) {
            throw new SQLException("Erro ao buscar cliente.", e);
        }
    }

    public static Cliente buscarClientePorCpf(Connection conn, String cpf) throws SQLException {
        try {
            return ClienteDAO.clientePorCpf(conn, cpf);
        } catch (SQLException e) {
            System.err.println("Erro ao buscar cliente por cpf: " + e.getMessage());
            return null;
        }
    }

    public static ArrayList<Cliente> listarSemCpf(Connection conn) throws SQLException {
        try {
            return ClienteDAO.listarClientesSemCpf(conn);
        } catch (SQLException e) {
            throw new SQLException("Erro ao listar clientes.", e);
        }
    }

    public static void excluirCliente(Connection conn, Cliente c) throws SQLException {
        if (c == null || c.getId() <= 0) {
            throw new IllegalArgumentException("ID inválido.");
        }

        try {
            ClienteDAO.deletarCliente(conn, c);
        } catch (SQLException e) {
            System.err.println("Erro ao excluir cliente: " + e.getMessage());
        }
    }
}
