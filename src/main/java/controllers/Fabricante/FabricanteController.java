package controllers.Fabricante;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import dao.Fabricante.FabricanteDAO;
import models.Fabricante.Fabricante;

public class FabricanteController {

    public static int criarFabricante(Connection conn, Fabricante fab) throws SQLException {
        if (fab == null || fab.getNome() == null || fab.getNome().isEmpty()) {
            throw new IllegalArgumentException("Fabricante ou nome inválido.");
        }

        try {
            return FabricanteDAO.criarFabricante(conn, fab);
        } catch (SQLException e) {
            throw new SQLException("Erro ao criar fabricante.", e);
        }
    }

    public static int buscarFabricantePorNome(Connection conn, String nome) throws SQLException {
        if (nome == null || nome.isEmpty()) {
            throw new IllegalArgumentException("Nome do fabricante inválido.");
        }

        try {
            return FabricanteDAO.buscarFabricantePorNome(conn, nome);
        } catch (SQLException e) {
            throw new SQLException("Erro ao buscar fabricante por nome.", e);
        }
    }

    public static ArrayList<String> listarTodosFabricantes(Connection conn) throws SQLException {
        try {
            return FabricanteDAO.listarTodosFabricantes(conn);
        } catch (SQLException e) {
            throw new SQLException("Erro ao listar fabricantes.", e);
        }
    }
}
