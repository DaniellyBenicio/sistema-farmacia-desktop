package controllers.Medicamento;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import dao.Medicamento.MedicamentoDAO;
import models.Medicamento.Medicamento;


public class MedicamentoController {
    public static boolean medicamentoExiste(Connection conn, Medicamento m) throws SQLException {
        if (m == null) {
            throw new IllegalArgumentException("Medicamento não pode ser nulo.");
        }
        try {
            return MedicamentoDAO.medicamentoExiste(conn, m);
        } catch (SQLException e) {
            throw new SQLException("Erro ao verificar a existência do medicamento.", e);
        }
    }

    public static void cadastrarMedicamento(Connection conn, Medicamento m) throws SQLException {
        try {
            MedicamentoDAO.cadastrarMedicamento(conn, m);
        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate entry")) {
                throw new SQLException("Medicamento já cadastrado na base de dados.");
            } else {
                throw new SQLException("Erro ao cadastrar medicamento.", e);
            }
        }
    }

    public static void atualizarMedicamento(Connection conn, Medicamento m) throws SQLException {
        if (m == null || m.getId() <= 0) {
            throw new IllegalArgumentException("Medicamento inválido.");
        }
        try {
            MedicamentoDAO.atualizarMedicamento(conn, m);
        } catch (SQLException e) {
            throw new SQLException("Erro ao atualizar medicamento.", e);
        }
    }

    public static List<Medicamento> listarMedicamentos(Connection conn) throws SQLException {
        try {
            return MedicamentoDAO.listarTodos(conn);
        } catch (SQLException e) {
            throw new SQLException("Erro ao listar medicamentos.", e);
        }
    }

    public static Medicamento buscarMedicamentoPorId(Connection conn, int id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("ID inválido.");
        }
        try {
            return MedicamentoDAO.buscarPorId(conn, id);
        } catch (SQLException e) {
            throw new SQLException("Erro ao buscar medicamento pelo ID.", e);
        }
    }

    public static void excluirMedicamento(Connection conn, Medicamento m) throws SQLException {
        if (m == null || m.getId() <= 0) {
            throw new IllegalArgumentException("ID inválido.");
        }

        try {
            MedicamentoDAO.deletarMedicamento(conn, m);
        } catch (SQLException e) {
            System.err.println("Erro ao excluir medicamento: " + e.getMessage());
        }
    }

    public static List<String> listarFormasFarmaceuticas(Connection conn) throws SQLException {
        try {
            return MedicamentoDAO.listarFormasFarmaceuticas(conn);
        } catch (SQLException e) {
            throw new SQLException("Erro ao listar formas farmacêuticas.", e);
        }
    }
    
    public static List<String> listarTiposDeMedicamentos(Connection conn) throws SQLException {
        try {
            return MedicamentoDAO.listarTiposDeMedicamentos(conn);
        } catch (SQLException e) {
            throw new SQLException("Erro ao listar tipos de medicamentos.", e);
        }
    }

    public static List<String> TiposDeReceitas(Connection conn) throws SQLException {
        try {
            return MedicamentoDAO.listarTiposDeReceitas(conn);
        } catch (SQLException e) {
            throw new SQLException("Erro ao listar tipos de receitas.", e);
        }
    }

    public static List<Medicamento> listarEstoqueMedicamento(Connection conn) throws SQLException {
        try {
            return MedicamentoDAO.listarEstoqueMedicamentos(conn);
        } catch (SQLException e) {
            throw new SQLException("Erro ao listar o estoque dos medicamentos.", e);
        }
    } 

}
