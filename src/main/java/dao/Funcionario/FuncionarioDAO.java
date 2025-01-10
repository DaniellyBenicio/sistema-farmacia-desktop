package dao.Funcionario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dao.Cargo.CargoDAO;
import models.Cargo.Cargo;
import models.Funcionario.Funcionario;

public class FuncionarioDAO {
    public static void cadastrarFuncionario(Connection conn, Funcionario f) throws SQLException {
        if (funcionarioExiste(conn, f.getNome(), f.getEmail(), f.getTelefone())) {
            throw new SQLException("Já existe um funcionário com um dos dados informados no banco de dados:\n(nome, e-mail ou telefone)\nVerifique novamente!");
        }
        int cargoId = CargoDAO.criarCargo(conn, f.getCargo());

        String sql = "INSERT INTO funcionario(nome, telefone, email, cargo_id, status) VALUES (?,?,?,?,?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, f.getNome());
            pstmt.setString(2, f.getTelefone().replaceAll("[^0-9]", ""));
            pstmt.setString(3, f.getEmail());
            pstmt.setInt(4, cargoId);
            pstmt.setBoolean(5, f.isStatus());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao cadastrar funcionário: " + e.getMessage());
            throw e;
        }
    }

    public static void atualizarFuncionario(Connection conn, Funcionario f) throws SQLException {
        if (f.getCargo() == null || f.getCargo().getNome() == null || f.getCargo().getNome().isEmpty()) {
            throw new SQLException("Erro: Cargo não pode ser nulo ou vazio.");
        }

        int cargoId = CargoDAO.criarCargo(conn, f.getCargo());
        f.getCargo().setId(cargoId);

        String sql = "UPDATE funcionario SET nome = ?, telefone = ?, email = ?, cargo_id = ?, status = ? WHERE id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, f.getNome());
            pstmt.setString(2, f.getTelefone());
            pstmt.setString(3, f.getEmail());
            pstmt.setInt(4, cargoId);
            pstmt.setBoolean(5, true);
            pstmt.setInt(6, f.getId());
            pstmt.executeUpdate();
            System.out.println("Dados atualizados com sucesso!");
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar o funcionário: " + e.getMessage());
            throw e;
        }
    }

    public String verificarFuncionarioPorCodigo(Connection conn, int codigo) throws SQLException {
        String query = "SELECT f.nome, c.nome AS cargo_nome,  f.status FROM funcionario f " +
                "JOIN cargo c ON f.cargo_id = c.id WHERE f.id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, codigo);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return "Código: " + codigo + ", Nome: " + rs.getString("nome") +
                        ", Cargo: " + rs.getString("cargo_nome") +
                        ", Status: " + (rs.getBoolean("status") ? "Ativo" : "Inativo");
            } else {
                return "Funcionário não encontrado.";
            }
        } catch (SQLException e) {
            System.err.println("Erro ao consultar o banco de dados: " + e.getMessage());
            throw e;
        }
    }

    public static Funcionario funcionarioPorId(Connection conn, int id) throws SQLException {
        String sql = "SELECT f.id, f.nome, f.email, f.telefone, c.nome AS cargo, f.status " +
                "FROM funcionario f " +
                "JOIN cargo c ON f.cargo_id = c.id WHERE f.id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Funcionario f = new Funcionario();
                    f.setId(rs.getInt("id"));
                    f.setNome(rs.getString("nome"));
                    f.setEmail(rs.getString("email"));
                    f.setTelefone(rs.getString("telefone"));

                    Cargo cargo = new Cargo();
                    cargo.setNome(rs.getString("cargo"));
                    f.setCargo(cargo);

                    f.setStatus(rs.getBoolean("status"));

                    return f;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar funcionário por ID: " + e.getMessage());
            throw e;
        }

        return null;
    }

    public static List<Funcionario> listarTodosFuncionarios(Connection conn) throws SQLException {
        String sql = "SELECT f.id, f.nome, f.email, f.telefone, c.nome AS cargo, f.status " +
                "FROM funcionario f " +
                "JOIN cargo c ON f.cargo_id = c.id " +
                "ORDER BY f.status DESC, f.nome ASC";

        List<Funcionario> funcionarios = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (!rs.isBeforeFirst()) {
                System.out.println("Nenhum funcionário encontrado.");
            }
            while (rs.next()) {
                Funcionario f = new Funcionario();
                f.setId(rs.getInt("id"));
                f.setNome(rs.getString("nome"));
                f.setEmail(rs.getString("email"));
                f.setTelefone(rs.getString("telefone"));

                Cargo cargo = new Cargo();
                cargo.setNome(rs.getString("cargo"));
                f.setCargo(cargo);

                f.setStatus(rs.getBoolean("status"));

                funcionarios.add(f);
            }
        }

        return funcionarios;
    }

    public static void deletarFuncionario(Connection conn, Funcionario f) throws SQLException {
        String sqlCargo = "SELECT c.nome FROM cargo c JOIN funcionario f ON f.cargo_id = c.id WHERE f.id = ?";
        try (PreparedStatement pstmtCargo = conn.prepareStatement(sqlCargo)) {
            pstmtCargo.setInt(1, f.getId());

            try (ResultSet rs = pstmtCargo.executeQuery()) {
                if (rs.next()) {
                    String cargoNome = rs.getString("nome");

                    if ("Gerente".equalsIgnoreCase(cargoNome)) {
                        String updateStatusSQL = "UPDATE funcionario SET status = ? WHERE id = ?";
                        try (PreparedStatement pstmtUpdate = conn.prepareStatement(updateStatusSQL)) {
                            pstmtUpdate.setBoolean(1, false);
                            pstmtUpdate.setInt(2, f.getId());
                            pstmtUpdate.executeUpdate();
                            System.out.println(
                                    "Funcionário do tipo Gerente não pode ser excluído, seu status foi alterado para inativo.");
                        }
                    } else {
                        String deleteSQL = "DELETE FROM funcionario WHERE id = ?";
                        try (PreparedStatement pstmtDelete = conn.prepareStatement(deleteSQL)) {
                            pstmtDelete.setInt(1, f.getId());
                            int rowsAffected = pstmtDelete.executeUpdate();

                            if (rowsAffected > 0) {
                                System.out.println("Funcionário excluído com sucesso!");
                            } else {
                                System.out.println("Nenhum funcionário encontrado para o ID " + f.getId());
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao verificar cargo do funcionário ou ao tentar excluir: " + e.getMessage());
            throw e;
        }
    }

    public static void desativarGerente(Connection conn, Funcionario funcionario) throws SQLException {
        String sql = "UPDATE funcionario SET status = 0 WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, funcionario.getId());
            stmt.executeUpdate();
        }
    }

    public static void ativarGerente(Connection conn, Funcionario funcionario) throws SQLException {
        String sql = "UPDATE funcionario SET status = 1 WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, funcionario.getId());
            stmt.executeUpdate();
        }
    }

    public static boolean funcionarioExiste(Connection conn, String nome, String telefone, String email) throws SQLException {
        String sqlVerificarDuplicidade = "SELECT COUNT(*) FROM funcionario WHERE nome = ? OR telefone = ? OR email = ? ";
    
        try (PreparedStatement pstmt = conn.prepareStatement(sqlVerificarDuplicidade)) {
            pstmt.setString(1, nome);
            pstmt.setString(2, telefone.replaceAll("[^0-9]", ""));
            pstmt.setString(3, email);
    
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao verificar duplicidade de funcionário: " + e.getMessage());
            throw e;
        }
    
        return false; 
    }
}