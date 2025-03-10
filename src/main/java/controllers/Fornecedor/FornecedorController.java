package controllers.Fornecedor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import dao.Fornecedor.FornecedorDAO;
import models.Fornecedor.Fornecedor;

public class FornecedorController {
    public static void cadastrarFornecedor(Connection conn, Fornecedor forn) throws SQLException {
        if (forn == null || forn.getNome() == null || forn.getEmail() == null) {
            throw new IllegalArgumentException("As informações são obrigatórias!");
        }

        try {
            FornecedorDAO.cadastrarFornecedor(conn, forn);
        } catch (SQLException e) {
            System.err.println("Erro ao cadastrar fornecedor: " + e.getMessage());
            throw new SQLException("Erro ao cadastrar fornecedor.", e);
        }
    }

    public static void atualizarFornecedor(Connection conn, Fornecedor forn) throws SQLException {
        if (forn == null || forn.getId() <= 0) {
            throw new IllegalArgumentException("Fornecedor ou ID inválido.");
        }

        try {
            FornecedorDAO.atualizarFornecedor(conn, forn);
            System.out.println("Fornecedor atualizado com sucesso.");
        } catch (SQLException e) {
            throw new SQLException("Erro ao atualizar fornecedor.", e);
        }
    }

    public static Fornecedor buscarFornecedorPorId(Connection conn, int id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("ID inválido.");
        }

        try {
            Fornecedor fornecedor = FornecedorDAO.fornecedorPorId(conn, id);
            if (fornecedor == null) {
                System.out.println("Fornecedor não encontrado.");
                return null;
            }
            return fornecedor;
        } catch (SQLException e) {
            throw new SQLException("Erro ao buscar fornecedor.", e);
        }
    }

    public static ArrayList<String> listarFornecedores(Connection conn) throws SQLException {
        try {
            return FornecedorDAO.listarNomesFornecedores(conn);
        } catch (SQLException e) {
            throw new SQLException("Erro ao listar fornecedores.", e);
        }
    }

    public static ArrayList<String> listarFornecedoresPorNome(Connection conn) throws SQLException {
        try {
            return FornecedorDAO.listarNomesFornecedores(conn);
        } catch (SQLException e) {
            throw new SQLException("Erro ao listar fornecedores por nome.", e);
        }
    }

    public static void excluirFornecedor(Connection conn, Fornecedor forn) throws SQLException {
        if (forn == null || forn.getId() <= 0) {
            throw new IllegalArgumentException("ID inválido.");
        }

        try {
            FornecedorDAO.deletarFornecedor(conn, forn);
        } catch (SQLException e) {
            System.err.println("Erro ao excluir fornecedor: " + e.getMessage());
        }
    }

    public static boolean fornecedorExiste(Connection conn, String nome, String cnpj, String email, String telefone) throws SQLException {
        if (nome == null || cnpj == null || email == null || telefone == null) {
            throw new IllegalArgumentException("Os dados do fornecedor não podem ser nulos.");
        }
        try {
            return FornecedorDAO.fornecedorExiste(conn, nome, cnpj, email, telefone);
        } catch (SQLException e) {
            throw new SQLException("Erro ao verificar a existência do fornecedor.", e);
        }
    }
    
}
