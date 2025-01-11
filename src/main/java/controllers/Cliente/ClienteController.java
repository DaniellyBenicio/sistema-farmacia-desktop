package controllers.Cliente;

import java.sql.Connection;
import java.sql.SQLException;

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
}
