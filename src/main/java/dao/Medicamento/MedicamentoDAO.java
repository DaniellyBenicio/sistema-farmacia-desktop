package dao.Medicamento;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dao.Categoria.CategoriaDAO;
import dao.Fabricante.FabricanteDAO;
import dao.Fornecedor.FornecedorDAO;
import dao.Funcionario.FuncionarioDAO;

import java.sql.Date;
import java.time.YearMonth;
import models.Medicamento.Medicamento;
import models.Categoria.Categoria;
import models.Fabricante.Fabricante;
import models.Fornecedor.Fornecedor;
import models.Funcionario.Funcionario;

public class MedicamentoDAO {

    public static boolean medicamentoExiste(Connection conn, Medicamento m) throws SQLException {
        String sql = "Select count(*) FROM medicamento WHERE nome = ? and dosagem = ? and formaFarmaceutica = ? " +
                     "and dataValidade = ? and dataFabricacao = ? and fabricante_id = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, m.getNome());
            pstmt.setString(2, m.getDosagem());
            pstmt.setString(3, m.getFormaFarmaceutica());
            pstmt.setDate(4, Date.valueOf(m.getDataValidade())); 
            pstmt.setDate(5, Date.valueOf(m.getDataFabricacao()));
            pstmt.setInt(6, m.getFabricante().getId());
    
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao verificar a existência do medicamento: " + e.getMessage());
            throw e;
        }
        return false;
    }

    public static void cadastrarMedicamento(Connection conn, Medicamento m) throws SQLException {
        if (medicamentoExiste(conn, m)) {
            System.out.println("O medicamento já existe na base de dados.");
            return; 
        }
        
        int categoriaId = CategoriaDAO.criarCategoria(conn, m.getCategoria());
        int fabricanteId = FabricanteDAO.criarFabricante(conn, m.getFabricante());

        Fornecedor fornecedorExistente = FornecedorDAO.fornecedorPorId(conn, m.getFornecedor().getId());
        if (fornecedorExistente == null) {
            FornecedorDAO.cadastrarFornecedor(conn, m.getFornecedor());
        }

        Fornecedor fornecedor = FornecedorDAO.fornecedorPorId(conn, m.getFornecedor().getId());
    
        
        String sql = "INSERT INTO medicamento (nome, dosagem, formaFarmaceutica, valorUnit, dataValidade, dataFabricacao, tipoReceita, qnt, tipo, categoria_id, funcionario_id, fabricante_id, fornecedor_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";       
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
            pstmt.setInt(10, categoriaId);
            pstmt.setInt(11, m.getFuncionario().getId()); 
            pstmt.setInt(12, fabricanteId);
            pstmt.setInt(13, fornecedor.getId());
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    m.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Erro ao obter ID do medicamento.");
                }
            }
            }catch (SQLException e) {
                System.err.println("Erro ao cadastrar medicamento: " + e.getMessage());
                throw e;
            }
    }

    public static void atualizarMedicamento(Connection conn, Medicamento m) throws SQLException {
        if (!medicamentoExiste(conn, m)) {
            System.out.println("O medicamento não existe na base de dados.");
            return; 
        }

        int categoriaId = CategoriaDAO.criarCategoria(conn, m.getCategoria());
        int fabricanteId = FabricanteDAO.criarFabricante(conn, m.getFabricante());

        Fornecedor fornecedorExistente = FornecedorDAO.fornecedorPorId(conn, m.getFornecedor().getId());
        if (fornecedorExistente == null) {
            FornecedorDAO.cadastrarFornecedor(conn, m.getFornecedor());
        }

        Fornecedor fornecedor = FornecedorDAO.fornecedorPorId(conn, m.getFornecedor().getId());
    
        String sql = "UPDATE medicamento SET nome = ?, dosagem = ?, formaFarmaceutica = ?, valorUnit = ?, dataValidade = ?, dataFabricacao = ?, tipoReceita = ?, qnt = ?, tipo = ?, categoria_id = ?, fabricante_id, fornecedor_id = ? WHERE id = ?";
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
            pstmt.setInt(10, categoriaId);
            pstmt.setInt(11, fabricanteId);
            pstmt.setInt(12, fornecedor.getId());
            pstmt.setInt(13, m.getId()); 
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar medicamento: " + e.getMessage());
            throw e;
        }
    }

    public static Medicamento buscarPorId(Connection conn, int id) throws SQLException {
        String sql = "SELECT * FROM medicamento WHERE id = ?";
        Medicamento medicamento = null;
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id); 
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    medicamento = new Medicamento();
                    medicamento.setId(rs.getInt("id"));
                    medicamento.setNome(rs.getString("nome"));
                    medicamento.setDosagem(rs.getString("dosagem"));
                    medicamento.setFormaFarmaceutica(rs.getString("formaFarmaceutica"));
                    medicamento.setValorUnit(rs.getDouble("valorUnit"));
                    medicamento.setDataValidade(YearMonth.from(rs.getDate("dataValidade").toLocalDate()));
                    medicamento.setDataFabricacao(YearMonth.from(rs.getDate("dataFabricacao").toLocalDate()));
                    medicamento.setTipoReceita(Medicamento.TipoReceita.valueOf(rs.getString("tipoReceita")));
                    medicamento.setQnt(rs.getInt("qnt"));
                    medicamento.setTipo(Medicamento.Tipo.valueOf(rs.getString("tipo")));
                    
                    Categoria categoria = new Categoria();
                    categoria.setId(rs.getInt("categoria_id"));
                    categoria.setNome(rs.getString("categoria_nome"));
                    medicamento.setCategoria(categoria);
                    
                    Funcionario funcionario = new Funcionario();
                    funcionario.setId(rs.getInt("funcionario_id"));
                    funcionario.setNome(rs.getString("funcionario_nome"));
                    medicamento.setFuncionario(funcionario);
                    
                    Fabricante fabricante = new Fabricante();
                    fabricante.setId(rs.getInt("fabricante_id"));
                    fabricante.setNome(rs.getString("fabricante_nome"));
                    medicamento.setFabricante(fabricante);
                    
                    Fornecedor fornecedor = new Fornecedor();
                    fornecedor.setId(rs.getInt("fornecedor_id"));
                    fornecedor.setNome(rs.getString("fornecedor_nome"));
                    medicamento.setFornecedor(fornecedor);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar medicamento por ID: " + e.getMessage());
            throw e;
        }
        
        return medicamento;
    }
    
    public static List<Medicamento> listarTodos(Connection conn) throws SQLException {
        List<Medicamento> medicamentos = new ArrayList<>();
        String sql = "SELECT m.id, m.nome, m.dosagem, m.formaFarmaceutica, m.valorUnit, m.dataValidade, m.dataFabricacao, m.tipoReceita, m.qnt, m.tipo, m.categoria_id, m.funcionario_id, m.fabricante_id, m.fornecedor_id, c.nome as categoria_nome, f.nome as funcionario_nome, fa.nome as fabricante_nome, fo.nome as fornecedor_nome "
                + "FROM medicamento m "
                + "JOIN categoria c ON m.categoria_id = c.id "
                + "JOIN funcionario f ON m.funcionario_id = f.id "
                + "JOIN fabricante fa ON m.fabricante_id = fa.id "
                + "JOIN fornecedor fo ON m.fornecedor_id = fo.id";

        try (PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Medicamento med = new Medicamento();
                med.setId(rs.getInt("id"));
                med.setNome(rs.getString("nome"));
                med.setDosagem(rs.getString("dosagem"));
                med.setFormaFarmaceutica(rs.getString("formaFarmaceutica"));
                med.setValorUnit(rs.getDouble("valorUnit"));
                med.setDataValidade(YearMonth.from(rs.getDate("dataValidade").toLocalDate()));
                med.setDataFabricacao(YearMonth.from(rs.getDate("dataFabricacao").toLocalDate()));             
                med.setTipoReceita(Medicamento.TipoReceita.valueOf(rs.getString("tipoReceita")));
                med.setQnt(rs.getInt("qnt"));
                med.setTipo(Medicamento.Tipo.valueOf(rs.getString("tipo")));
                
                Categoria cat = new Categoria();
                cat.setId(rs.getInt("categoria_id"));
                cat.setNome(rs.getString("categoria_nome"));
                med.setCategoria(cat);
                
                Funcionario fun = new Funcionario();
                fun.setId(rs.getInt("funcionario_id"));
                fun.setNome(rs.getString("funcionario_nome"));
                med.setFuncionario(fun);
                
                Fabricante fabricante = new Fabricante();
                fabricante.setId(rs.getInt("fabricante_id"));
                fabricante.setNome(rs.getString("fabricante_nome"));
                med.setFabricante(fabricante);
                
                Fornecedor fornecedor = new Fornecedor();
                fornecedor.setId(rs.getInt("fornecedor_id"));
                fornecedor.setNome(rs.getString("fornecedor_nome"));
                med.setFornecedor(fornecedor);
                
                medicamentos.add(med);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar medicamentos: " + e.getMessage());
            throw e;
        }
        return medicamentos;
    }

    public static void deletarMedicamento(Connection conn, Medicamento m) throws SQLException {
        String sql = "delete from medicamento where id = ?";
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
    
}

//falta buscar por: categoria, nome, fornecedor, fabricante