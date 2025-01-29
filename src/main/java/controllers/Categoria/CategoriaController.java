package controllers.Categoria;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import dao.Categoria.CategoriaDAO;
import models.Categoria.Categoria;

public class CategoriaController {

    public static void cadastrarCategoria(Connection conn, Categoria categoria) throws SQLException {
        try {
            CategoriaDAO.criarCategoria(conn, categoria);
        } catch (SQLException e) {
            throw new SQLException("Erro ao cadastrar categoria.", e);
        }
    }

    public static Categoria buscarCategoriaPorNome(Connection conn, String nome) throws SQLException {
        if (nome == null || nome.isEmpty()) {
            throw new IllegalArgumentException("Nome inválido.");
        }

        try {
            int id = CategoriaDAO.buscarCategoriaPorNome(conn, nome);
            if (id == 0) {
                System.out.println("Categoria não encontrada.");
                return null;
            }
            Categoria categoria = new Categoria();
            categoria.setId(id);
            categoria.setNome(nome);
            return categoria;
        } catch (SQLException e) {
            throw new SQLException("Erro ao buscar categoria.", e);
        }
    }

    public static ArrayList<String> listarTodasCategorias(Connection conn) throws SQLException {
        try {
            return CategoriaDAO.listarTodasCategorias(conn);
        } catch (SQLException e) {
            throw new SQLException("Erro ao listar categorias.", e);
        }
    }
}
