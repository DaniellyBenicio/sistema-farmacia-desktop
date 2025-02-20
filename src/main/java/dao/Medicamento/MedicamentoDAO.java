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

import java.sql.Date;
import java.time.YearMonth;
import models.Medicamento.Medicamento;
import models.Categoria.Categoria;
import models.Fabricante.Fabricante;
import models.Fornecedor.Fornecedor;
import models.Funcionario.Funcionario;

public class MedicamentoDAO {

    public static boolean medicamentoExiste(Connection conn, Medicamento m) throws SQLException {
        String sql = "SELECT count(*) FROM medicamento WHERE id = ?"; 
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, m.getId()); 
    
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

        String sql = "INSERT INTO medicamento (nome, dosagem, formaFarmaceutica, valorUnit, dataValidade, dataFabricacao, tipoReceita, qnt, tipo, embalagem, qntEmbalagem, categoria_id, funcionario_id, fabricante_id, fornecedor_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, m.getNome());
            pstmt.setString(2, m.getDosagem());
            pstmt.setString(3, m.getFormaFarmaceutica());
            pstmt.setBigDecimal(4, m.getValorUnit());
            pstmt.setDate(5, Date.valueOf(m.getDataValidade()));
            pstmt.setDate(6, Date.valueOf(m.getDataFabricacao()));
            pstmt.setString(7, m.getTipoReceita().name());
            pstmt.setInt(8, m.getQnt());
            pstmt.setString(9, m.getTipo().name());
            pstmt.setString(10, m.getEmbalagem());       
            pstmt.setInt(11, m.getQntEmbalagem());
            pstmt.setInt(12, categoriaId);
            pstmt.setInt(13, m.getFuncionario().getId());
            pstmt.setInt(14, fabricanteId);
            pstmt.setInt(15, fornecedor.getId());

            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    m.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Erro ao obter ID do medicamento.");
                }
            }
        } catch (SQLException e) {
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
    
        String sql = "UPDATE medicamento SET nome = ?, dosagem = ?, formaFarmaceutica = ?, valorUnit = ?, dataValidade = ?, dataFabricacao = ?, tipoReceita = ?, qnt = ?, tipo = ?, embalagem = ?, qntEmbalagem = ?, categoria_id = ?, fabricante_id = ?, fornecedor_id = ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, m.getNome());
            pstmt.setString(2, m.getDosagem());
            pstmt.setString(3, m.getFormaFarmaceutica());
            pstmt.setBigDecimal(4, m.getValorUnit());
            pstmt.setDate(5, Date.valueOf(m.getDataValidade()));
            pstmt.setDate(6, Date.valueOf(m.getDataFabricacao()));
            pstmt.setString(7, m.getTipoReceita().name());
            pstmt.setInt(8, m.getQnt());
            pstmt.setString(9, m.getTipo().name());
            pstmt.setString(10, m.getEmbalagem());       
            pstmt.setInt(11, m.getQntEmbalagem());
            pstmt.setInt(12, categoriaId);
            pstmt.setInt(13, fabricanteId);
            pstmt.setInt(14, fornecedor.getId());
            pstmt.setInt(15, m.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar medicamento: " + e.getMessage());
            throw e;
        }
    }    

    public static Medicamento buscarPorId(Connection conn, int id) throws SQLException {
        String sql = "SELECT m.*, c.nome AS categoria_nome, f.nome AS funcionario_nome, "
                   + "fa.nome AS fabricante_nome, fo.nome AS fornecedor_nome "
                   + "FROM medicamento m "
                   + "JOIN categoria c ON m.categoria_id = c.id "
                   + "JOIN funcionario f ON m.funcionario_id = f.id "
                   + "JOIN fabricante fa ON m.fabricante_id = fa.id "
                   + "JOIN fornecedor fo ON m.fornecedor_id = fo.id "
                   + "WHERE m.id = ?";
        
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
                    medicamento.setValorUnit(rs.getBigDecimal("valorUnit"));
                    medicamento.setDataValidade(YearMonth.from(rs.getDate("dataValidade").toLocalDate()));
                    medicamento.setDataFabricacao(YearMonth.from(rs.getDate("dataFabricacao").toLocalDate()));
                    medicamento.setTipoReceita(Medicamento.TipoReceita.valueOf(rs.getString("tipoReceita")));
                    medicamento.setQnt(rs.getInt("qnt"));
                    medicamento.setTipo(Medicamento.Tipo.valueOf(rs.getString("tipo")));
                    medicamento.setEmbalagem(rs.getString("embalagem"));
                    medicamento.setQntEmbalagem(rs.getInt("QntEmbalagem"));
                    
                    // Categoria
                    Categoria categoria = new Categoria();
                    categoria.setId(rs.getInt("categoria_id"));
                    categoria.setNome(rs.getString("categoria_nome"));
                    medicamento.setCategoria(categoria);
    
                    // Funcionario
                    Funcionario funcionario = new Funcionario();
                    funcionario.setId(rs.getInt("funcionario_id"));
                    funcionario.setNome(rs.getString("funcionario_nome"));
                    medicamento.setFuncionario(funcionario);
    
                    // Fabricante
                    Fabricante fabricante = new Fabricante();
                    fabricante.setId(rs.getInt("fabricante_id"));
                    fabricante.setNome(rs.getString("fabricante_nome"));
                    medicamento.setFabricante(fabricante);
    
                    // Fornecedor
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
        String sql = "SELECT m.id, m.nome, m.dosagem, m.formaFarmaceutica, m.valorUnit, m.dataValidade, m.dataFabricacao, m.tipoReceita, m.qnt, m.tipo, m.embalagem, m.qntEmbalagem, m.categoria_id, m.funcionario_id, m.fabricante_id, m.fornecedor_id, c.nome as categoria_nome, f.nome as funcionario_nome, fa.nome as fabricante_nome, fo.nome as fornecedor_nome "
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
                med.setValorUnit(rs.getBigDecimal("valorUnit"));
                med.setDataValidade(YearMonth.from(rs.getDate("dataValidade").toLocalDate()));
                med.setDataFabricacao(YearMonth.from(rs.getDate("dataFabricacao").toLocalDate()));
                med.setTipoReceita(Medicamento.TipoReceita.valueOf(rs.getString("tipoReceita")));
                med.setQnt(rs.getInt("qnt"));
                med.setTipo(Medicamento.Tipo.valueOf(rs.getString("tipo")));
                med.setEmbalagem(rs.getString("embalagem"));
                med.setQntEmbalagem(rs.getInt("QntEmbalagem"));
    
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
    
    public static List<Medicamento> buscarPorCategoriaOuNome(Connection conn, String termo) throws SQLException {
        List<Medicamento> medicamentos = new ArrayList<>();
        String sql = "SELECT m.nome, m.dosagem, m.formaFarmaceutica, m.valorUnit, m.dataValidade, " +
                     "m.dataFabricacao, m.tipoReceita, m.qnt, m.tipo, m.embalagem, m.qntEmbalagem, " +
                     "c.nome AS categoria_nome, " +
                     "f.nome AS funcionario_nome, " +
                     "fa.nome AS fabricante_nome, " +
                     "fo.nome AS fornecedor_nome " +
                     "FROM medicamento m " +
                     "JOIN categoria c ON m.categoria_id = c.id " +
                     "JOIN funcionario f ON m.funcionario_id = f.id " +
                     "JOIN fabricante fa ON m.fabricante_id = fa.id " +
                     "JOIN fornecedor fo ON m.fornecedor_id = fo.id " +
                     "WHERE c.nome LIKE ? OR m.nome LIKE ? " +
                     "ORDER BY m.nome ASC";
    
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + termo + "%");
            pstmt.setString(2, "%" + termo + "%");
    
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Medicamento med = new Medicamento();
                    med.setNome(rs.getString("nome"));
                    med.setDosagem(rs.getString("dosagem"));
                    med.setFormaFarmaceutica(rs.getString("formaFarmaceutica"));
                    med.setValorUnit(rs.getBigDecimal("valorUnit"));
                    med.setDataValidade(YearMonth.from(rs.getDate("dataValidade").toLocalDate()));
                    med.setDataFabricacao(YearMonth.from(rs.getDate("dataFabricacao").toLocalDate()));
                    med.setTipoReceita(Medicamento.TipoReceita.valueOf(rs.getString("tipoReceita")));
                    med.setQnt(rs.getInt("qnt"));
                    med.setTipo(Medicamento.Tipo.valueOf(rs.getString("tipo")));
                    med.setEmbalagem(rs.getString("embalagem"));
                    med.setQntEmbalagem(rs.getInt("QntEmbalagem"));
    
                    Categoria cat = new Categoria();
                    cat.setNome(rs.getString("categoria_nome"));
                    med.setCategoria(cat);
    
                    Funcionario fun = new Funcionario();
                    fun.setNome(rs.getString("funcionario_nome"));
                    med.setFuncionario(fun);
    
                    Fabricante fabricante = new Fabricante();
                    fabricante.setNome(rs.getString("fabricante_nome"));
                    med.setFabricante(fabricante);
    
                    Fornecedor fornecedor = new Fornecedor();
                    fornecedor.setNome(rs.getString("fornecedor_nome"));
                    med.setFornecedor(fornecedor);
    
                    medicamentos.add(med);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar medicamentos por categoria ou nome: " + e.getMessage());
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

    public static ArrayList<String> listarFormasFarmaceuticas(Connection conn) throws SQLException {
        String sql = "SELECT formaFarmaceutica FROM medicamento ORDER BY formaFarmaceutica ASC";
        ArrayList<String> formas = new ArrayList<>();
    
        try (PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                formas.add(rs.getString("formaFarmaceutica"));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar formas farmacêuticas: " + e.getMessage());
            throw e;
        }
        return formas;
    }

    public static ArrayList<String> listarTiposDeMedicamentos(Connection conn) throws SQLException {
        String sql = "SELECT tipo FROM medicamento ORDER BY tipo ASC";
        ArrayList<String> formas = new ArrayList<>();
    
        try (PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                formas.add(rs.getString("tipo"));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar os tipos de medicamentos: " + e.getMessage());
            throw e;
        }
        return formas;
    }

    public static ArrayList<String> listarTiposDeReceitas(Connection conn) throws SQLException {
        String sql = "select tipoReceita from medicamento order by tipoReceita asc";
        ArrayList<String> receitas = new ArrayList<>();

        try (PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                receitas.add(rs.getString("tipoReceita"));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar os tipos de receitas: " + e.getMessage());
            throw e;
        }
        return receitas;
    }

    public static List<Medicamento> listarEstoqueMedicamentos(Connection conn) throws SQLException {
        List<Medicamento> medicamentosEstoque = new ArrayList<>();
        String sql = "SELECT m.nome, m.dosagem, m.formaFarmaceutica, m.valorUnit, m.qnt, m.dataValidade, m.embalagem, m.qntEmbalagem, "
                   + "c.nome AS categoria_nome, "
                   + "fo.nome AS fornecedor_nome "
                   + "FROM medicamento m "
                   + "JOIN categoria c ON m.categoria_id = c.id "
                   + "JOIN fornecedor fo ON m.fornecedor_id = fo.id "
                   + "ORDER BY m.dataValidade ASC, m.qnt ASC";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql); 
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Medicamento medicamento = new Medicamento();
                medicamento.setNome(rs.getString("nome"));
                medicamento.setDosagem(rs.getString("dosagem"));
                medicamento.setFormaFarmaceutica(rs.getString("formaFarmaceutica"));
                medicamento.setValorUnit(rs.getBigDecimal("valorUnit"));
                medicamento.setQnt(rs.getInt("qnt"));
                medicamento.setDataValidade(YearMonth.from(rs.getDate("dataValidade").toLocalDate()));
                medicamento.setEmbalagem(rs.getString("embalagem"));
                medicamento.setQntEmbalagem(rs.getInt("QntEmbalagem"));
                
                Categoria categoria = new Categoria();
                categoria.setNome(rs.getString("categoria_nome"));
                medicamento.setCategoria(categoria);
                
                Fornecedor fornecedor = new Fornecedor();
                fornecedor.setNome(rs.getString("fornecedor_nome"));
                medicamento.setFornecedor(fornecedor);
                
                medicamentosEstoque.add(medicamento);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar estoque dos medicamentos: " + e.getMessage());
            throw e;
        }
        
        return medicamentosEstoque;
    }

    public static List<Medicamento> listarBaixoEstoque(Connection conn) throws SQLException {
        List<Medicamento> baixoEstoque = new ArrayList<>();
        String sql = "SELECT m.nome, m.dosagem, m.formaFarmaceutica, m.valorUnit, c.nome AS categoria_nome, m.dataValidade, m.embalagem, " +
                     "f.nome AS fornecedor_nome, m.qnt " +
                     "FROM medicamento m " +
                     "JOIN categoria c ON m.categoria_id = c.id " +
                     "JOIN fornecedor f ON m.fornecedor_id = f.id " +
                     "WHERE " +
                     "(" +
                     "    (" +
                     "        c.nome IN ('Antihipertensivo', 'Analgésico', 'Antitérmico', 'Antibiótico', 'Anti-inflamatório', " +
                     "                   'Antidepressivo', 'Antipsicótico', 'Ansiolítico', 'Antidiabético', 'Antialérgico') " +
                     "        AND m.qnt < 30 " +
                     "        AND (" +
                     "            (m.tipo = 'Genérico' OR m.tipo = 'Similar') AND m.valorUnit <= 40 " +
                     "            OR m.tipo = 'Ético' AND m.valorUnit <= 50 " +
                     "        )" +
                     "    ) " +
                     "    OR (" +
                     "        c.nome NOT IN ('Antihipertensivo', 'Analgésico', 'Antitérmico', 'Antibiótico', 'Anti-inflamatório', " +
                     "                     'Antidepressivo', 'Antipsicótico', 'Ansiolítico', 'Antidiabético', 'Antialérgico') " +
                     "        AND m.qnt <= 15 " +
                     "    )" +
                     ")" +
                     "ORDER BY m.qnt ASC, m.dataValidade ASC";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql); 
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Medicamento medicamento = new Medicamento();
                medicamento.setNome(rs.getString("nome"));
                medicamento.setValorUnit(rs.getBigDecimal("valorUnit"));
                medicamento.setFormaFarmaceutica(rs.getString("formaFarmaceutica"));
                medicamento.setDosagem(rs.getString("dosagem"));
                medicamento.setQnt(rs.getInt("qnt"));
                medicamento.setDataValidade(YearMonth.from(rs.getDate("dataValidade").toLocalDate()));
                medicamento.setEmbalagem(rs.getString("embalagem"));
               // medicamento.setQntEmbalagem(rs.getInt("QntEmbalagem"));
                
                Categoria categoria = new Categoria();
                categoria.setNome(rs.getString("categoria_nome"));
                medicamento.setCategoria(categoria);
                
                Fornecedor fornecedor = new Fornecedor();
                fornecedor.setNome(rs.getString("fornecedor_nome"));
                medicamento.setFornecedor(fornecedor);
                
                baixoEstoque.add(medicamento);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar baixo estoque: " + e.getMessage());
            throw e;
        }
        
        return baixoEstoque;
    }

    
    public static List<String> medicamentoCategoria(Connection conn) throws SQLException {
        String sql = "SELECT DISTINCT c.nome AS categoria " +
                     "FROM medicamento m " +
                     "INNER JOIN categoria c ON m.categoria_id = c.id " +
                     "ORDER BY c.nome ASC";
        List<String> medCategoria = new ArrayList<>();
    
        try (PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                medCategoria.add(rs.getString("categoria"));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar categoria dos medicamentos: " + e.getMessage());
            throw e;
        }
        return medCategoria;
    }

    //embalagem: scaixa, envelope

    
    
}
