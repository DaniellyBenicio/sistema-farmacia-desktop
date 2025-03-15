package controllers.Funcionario;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import dao.Cargo.CargoDAO;
import dao.Funcionario.FuncionarioDAO;
import models.Cargo.Cargo;
import models.Funcionario.Funcionario;

public class FuncionarioController {
    public static void cadastrarFuncionario(Connection conn, Funcionario fun) throws SQLException {
        if (fun == null || fun.getCargo() == null) {
            throw new IllegalArgumentException("Funcionário ou cargo não podem ser nulos.");
        }

        try {
            Cargo cargo = fun.getCargo();
            
            int cargoId = CargoDAO.criarCargo(conn, cargo); 
            
            cargo.setId(cargoId);
            fun.setCargo(cargo); 

            FuncionarioDAO.cadastrarFuncionario(conn, fun);
            
            System.out.println("Cadastro do funcionário realizado com sucesso!");
        } catch (SQLException e) {
            System.err.println("Erro ao cadastrar funcionário: " + e.getMessage());
            throw e;  
        }
    }

    public static void atualizarFuncionario(Connection conn, Funcionario fun) throws SQLException {
        if (fun == null || fun.getCargo() == null) {
            throw new IllegalArgumentException("Funcionário ou cargo não podem ser nulos.");
        }

        try {
            int cargoId = CargoDAO.CargoPorNome(conn, fun.getCargo().getNome());
            if (cargoId == 0) {
                cargoId = CargoDAO.criarCargo(conn, fun.getCargo());
            }
            fun.getCargo().setId(cargoId);
            FuncionarioDAO.atualizarFuncionario(conn, fun);
            System.out.println("Funcionário atualizado com sucesso!");
        } catch (SQLException e) {
                System.err.println("Erro ao atualizar funcionário: " + e.getMessage());
                throw e;
        }
    }

    public static Funcionario buscarFuncionarioPorId(Connection conn, int id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("ID inválido. O ID deve ser maior que zero.");
        }
    
        try {
            Funcionario funcionario = FuncionarioDAO.funcionarioPorId(conn, id);
            return funcionario;
        } catch (SQLException e) {
            System.err.println("Erro ao buscar o funcionário: " + e.getMessage());
            throw e;
        }
    }    

    public static List<Funcionario> listarFuncionarios(Connection conn) throws SQLException {
        return FuncionarioDAO.listarTodosFuncionarios(conn);  
    }
    

    public static void excluirFuncionario(Connection conn, Funcionario f) throws SQLException {
        if (f == null || f.getId() <= 0) {
            throw new IllegalArgumentException("Funcionário inválido.");
        }

        try {
            FuncionarioDAO.deletarFuncionario(conn, f);
        } catch (SQLException e) {
            System.err.println("Erro ao excluir funcionário: " + e.getMessage());
            throw e;
        }
    }

    public static int buscarFuncionarioPorNome(Connection conn, String nome) throws SQLException {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do funcionário não pode ser vazio ou nulo.");
        }
        try {
            int id = FuncionarioDAO.buscarPorNome(conn, nome);
            if (id == 0) {
                System.out.println("Nenhum funcionário encontrado com o nome: " + nome);
            } else {
                System.out.println("Funcionário encontrado! ID: " + id);
            }
            return id;
        } catch (SQLException e) {
            throw e;
        }
    }

    public static void verificarFuncionario(Connection conn, int id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("O ID do funcionário deve ser um número positivo.");
        }

        try {
            Integer funcionarioId = FuncionarioDAO.verificarFuncionarioPorId(conn, id);
            if (funcionarioId != null) {
                System.out.println("Funcionário encontrado com ID: " + funcionarioId);
            } else {
                System.out.println("Funcionário não encontrado.");
            }
        } catch (SQLException e) {
            System.err.println("Erro ao verificar funcionário: " + e.getMessage());
            throw e; 
        }
    }

} 