package controllers.Represetante;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import dao.Representante.RepresentanteDAO;
import models.Represetante.Representante;

public class RepresentanteController {
    public static void cadastrarRepresentante(Connection conn, String nome, String telefone, int fornecedorId) throws SQLException {
        if (nome == null || nome.isEmpty() || telefone == null || telefone.isEmpty() || fornecedorId <= 0) {
            throw new IllegalArgumentException("Nome, telefone e fornecedor não podem ser nulos ou vazios.");
        }

        try {
            RepresentanteDAO.cadastrarRepresentante(conn, nome, telefone, fornecedorId);
        } catch (SQLException e) {
            System.err.println("Erro ao cadastrar representante: " + e.getMessage());
            throw e;
        }
    }

    public static void atualizarRepresentante(Connection conn, Representante r) throws SQLException {
        if (r == null || r.getNome() == null || r.getNome().isEmpty() || 
            r.getTelefone() == null || r.getTelefone().isEmpty() || 
            r.getFornecedor() == null || r.getFornecedor().getId() <= 0) {
            throw new IllegalArgumentException("Nome, telefone e fornecedor não podem ser nulos ou vazios.");
        }
    
        try {
            // Chamar o método correto de atualizar o representante
            RepresentanteDAO.atualizarRepresentante(conn, r);
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar representante: " + e.getMessage());
            throw e;
        }
    }
    
    public static Representante buscarRepresentantePorNome(Connection conn, Representante r) throws SQLException {
        if (r == null || r.getNome() == null || r.getNome().isEmpty()) {
            throw new IllegalArgumentException("Nome do representante não pode ser nulo ou vazio.");
        }

        try {
            Representante resultado = RepresentanteDAO.representantePornome(conn, r);
            if (resultado == null) {
                System.out.println("Nenhum representante encontrado com o nome: " + r.getNome());
            }
            return resultado;
        } catch (SQLException e) {
            System.err.println("Erro ao buscar representante por nome: " + e.getMessage());
            throw e;
        }
    }

    public static List<Representante> listarRepresentantes(Connection conn) throws SQLException {
        try {
            return RepresentanteDAO.listarRepresentantes(conn);
        } catch (SQLException e) {
            System.err.println("Erro ao listar representantes: " + e.getMessage());
            throw e;
        }
    }

    public static void excluirRepresentante(Connection conn, Representante r) throws SQLException {
        if (r == null || r.getFornecedor() == null || r.getNome() == null || r.getNome().isEmpty()) {
            throw new IllegalArgumentException("Representante ou fornecedor inválido.");
        }

        try {
            RepresentanteDAO.deletarRepresentante(conn, r);
        } catch (SQLException e) {
            System.err.println("Erro ao excluir representante: " + e.getMessage());
            throw e;
        }
    }
}