package dao.Medicamento;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.sql.Date;
import models.Medicamento.Medicamento;
import models.Medicamento.Medicamento.Tipo;
import models.Medicamento.Medicamento.TipoReceita;
import models.Categoria.Categoria;
import models.Cliente.Cliente;
import models.Fabricante.Fabricante;
import models.Fornecedor.Fornecedor;
import models.Funcionario.Funcionario;

public class MedicamentoDAO {
    public static void cadastrarMedicamento(Connection conn, Medicamento m) throws SQLException {
        String sql = "INSERT INTO medicamento (nome, dosagem, formaFarmaceutica, valorUnit, dataValidade, dataFabricacao, tipoReceita, qnt, tipo, categoria_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";       
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, m.getNome()); 
            pstmt.setString(2, m.getDosagem());
            pstmt.setString(3, m.getFormaFarmaceutica()); 
            pstmt.setDouble(4, m.getValorUnit()); 
            pstmt.setDate(5, Date.valueOf(m.getDataValidade())); 
            pstmt.setDate(6, Date.valueOf(m.getDataFabricacao())); 
            pstmt.setString(7, m.getTipoReceita().name());
            pstmt.setInt(8, m.getQnt()); 
            pstmt.setString(9, m.getTipo().name()); 
            pstmt.setInt(10, m.getCategoria().getId()); 

            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao cadastrar medicamento: " + e.getMessage());
            throw e;
        }
    }

    public static void atualizarMedicamento(Connection conn, Medicamento m) throws SQLException {
        String sql = "UPDATE medicamento SET nome = ?, dosagem = ?, formaFarmaceutica = ?, valorUnit = ?, dataValidade = ?, dataFabricacao = ?, tipoReceita = ?, qnt = ?, tipo = ?, categoria_id = ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, m.getNome()); 
            pstmt.setString(2, m.getDosagem());
            pstmt.setString(3, m.getFormaFarmaceutica()); 
            pstmt.setDouble(4, m.getValorUnit()); 
            pstmt.setDate(5, Date.valueOf(m.getDataValidade())); 
            pstmt.setDate(6, Date.valueOf(m.getDataFabricacao())); 
            pstmt.setString(7, m.getTipoReceita().name());
            pstmt.setInt(8, m.getQnt()); 
            pstmt.setString(9, m.getTipo().name()); 
            pstmt.setInt(10, m.getCategoria().getId()); 
            pstmt.setInt(11, m.getId()); 
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar medicamento: " + e.getMessage());
            throw e;
        }
    }

    public static void deletarMedicamento(Connection conn, Medicamento m) throws SQLException {
        String sql = "DELETE FROM medicamento WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, m.getId());
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Medicamento excluído com sucesso!");
            } else {
                System.out.println("Nenhum medicamento encontrado para o ID " + m.getId());
            }
        } catch (SQLException e) {
            System.err.println("Erro ao excluir medicamento: " + e.getMessage());
            throw e;
        }
    }

    public static List<Medicamento> ListaDeMedicamentos(Connection conn) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'ListaDeMedicamentos'");
    }

    public static Medicamento medicamentoPorId(Connection conn, int medicamentoId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'medicamentoPorId'");
    }

    
}

//falta listar e buscar por: categoria, nome, fornecedor, fabricante