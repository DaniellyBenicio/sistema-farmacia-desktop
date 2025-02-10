package dao.Produto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.sql.Date;
import java.time.YearMonth;

import models.Produto.Produto;
import models.Categoria.Categoria;
import models.Fabricante.Fabricante;
import models.Fornecedor.Fornecedor;
import models.Funcionario.Funcionario;
import dao.Fabricante.FabricanteDAO;
import dao.Fornecedor.FornecedorDAO;
import dao.ProdutoCategoria.ProdutoCategoriaDAO;

public class ProdutoDAO {
    public static boolean produtoExiste(Connection conn, Produto p) throws SQLException {
        String sql = "select count(*) from produto where id = ?"; 
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, p.getId()); 
    
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0; 
                }
            }
        } catch (SQLException e) {
            throw e;
        }
        return false;
    }

    public static void cadastrarProduto(Connection conn, Produto p) throws SQLException {
        if (produtoExiste(conn, p)) {
            System.out.println("O produto já existe na base de dados.");
            return;
        }

        int fabricanteId = FabricanteDAO.criarFabricante(conn, p.getFabricante());

        Fornecedor fornecedorExistente = FornecedorDAO.fornecedorPorId(conn, p.getFornecedor().getId());
        if (fornecedorExistente == null) {
            FornecedorDAO.cadastrarFornecedor(conn, p.getFornecedor());
        }

        Fornecedor fornecedor = FornecedorDAO.fornecedorPorId(conn, p.getFornecedor().getId());

        String sql = "insert into produto (nome, valor, qntEstoque, dataValidade, dataFabricacao, qntMedida, embalagem, funcionario_id, fabricante_id, fornecedor_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, p.getNome());
            pstmt.setBigDecimal(2, p.getValor());
            pstmt.setInt(3, p.getQntEstoque());
            pstmt.setDate(4, Date.valueOf(p.getDataValidade()));
            pstmt.setDate(5, Date.valueOf(p.getDataFabricacao()));
            pstmt.setString(6, p.getQntMedida());
            pstmt.setString(7, p.getEmbalagem());
            pstmt.setInt(8, p.getFuncionario().getId());
            pstmt.setInt(9, fabricanteId);
            pstmt.setInt(10, fornecedor.getId());

            pstmt.executeUpdate(); 
    
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int produtoId = generatedKeys.getInt(1);
                    ProdutoCategoriaDAO.associarProdutoACategorias(conn, produtoId, p.getCategorias());
                } else {
                    throw new SQLException("Falha ao obter o ID do produto inserido.");
                }
            }
        }
    }

    public static void atualizarProduto(Connection conn, Produto p) throws SQLException {
        if (!produtoExiste(conn, p)) {
            System.out.println("O produto não existe na base de dados.");
            return;
        }
    
        Fornecedor fornecedorExistente = FornecedorDAO.fornecedorPorId(conn, p.getFornecedor().getId());
        if (fornecedorExistente == null) {
            FornecedorDAO.cadastrarFornecedor(conn, p.getFornecedor());
        }
    
        Fornecedor fornecedor = FornecedorDAO.fornecedorPorId(conn, p.getFornecedor().getId());
    
        String sql = "update produto set nome = ?, valor = ?, qntEstoque = ?, dataValidade = ?, dataFabricacao = ?, qntMedida = ?, embalagem = ?, funcionario_id = ?, fabricante_id = ?, fornecedor_id = ? where id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, p.getNome());
            pstmt.setBigDecimal(2, p.getValor());
            pstmt.setInt(3, p.getQntEstoque());
            pstmt.setDate(4, Date.valueOf(p.getDataValidade()));
            pstmt.setDate(5, Date.valueOf(p.getDataFabricacao()));
            pstmt.setString(6, p.getQntMedida());
            pstmt.setString(7, p.getEmbalagem());
            pstmt.setInt(8, p.getFuncionario().getId());
            pstmt.setInt(9, p.getFabricante().getId());
            pstmt.setInt(10, fornecedor.getId());
            pstmt.setInt(11, p.getId());
    
            pstmt.executeUpdate();
                
            ProdutoCategoriaDAO.desassociarProdutoACategorias(conn, p.getId());
            ProdutoCategoriaDAO.associarProdutoACategorias(conn, p.getId(), p.getCategorias());
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar produto: " + e.getMessage());
            throw e;
        }
    }

    public static List<Produto> listarTodos(Connection conn) throws SQLException {
        Map<Integer, Produto> produtoMap = new HashMap<>();
        String sql = "SELECT p.id, p.nome, p.valor, p.qntEstoque, p.dataValidade, "
                        + "p.dataFabricacao, p.qntMedida, p.embalagem, "
                        + "f.id AS funcionario_id, f.nome AS funcionario_nome, "
                        + "fa.id AS fabricante_id, fa.nome AS fabricante_nome, "
                        + "fo.id AS fornecedor_id, fo.nome AS fornecedor_nome, "
                        + "c.id AS categoria_id, c.nome AS categoria_nome "
                        + "FROM produto p "
                        + "JOIN funcionario f ON p.funcionario_id = f.id "
                        + "JOIN fabricante fa ON p.fabricante_id = fa.id "
                        + "JOIN fornecedor fo ON p.fornecedor_id = fo.id "
                        + "JOIN ProdCategoria pc ON p.id = pc.produto_id "
                        + "JOIN categoria c ON pc.categoria_id = c.id";
    
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                int produtoId = rs.getInt("id");
    
                Produto produto = produtoMap.get(produtoId);
                if (produto == null) {
                    produto = new Produto();
                    produto.setId(produtoId);
                    produto.setNome(rs.getString("nome"));
                    produto.setValor(rs.getBigDecimal("valor"));
                    produto.setQntEstoque(rs.getInt("qntEstoque"));
                    produto.setDataValidade(YearMonth.from(rs.getDate("dataValidade").toLocalDate()));
                    produto.setDataFabricacao(YearMonth.from(rs.getDate("dataFabricacao").toLocalDate()));
                    produto.setQntMedida(rs.getString("qntMedida"));
                    produto.setEmbalagem(rs.getString("embalagem"));
    
                    Funcionario funcionario = new Funcionario();
                    funcionario.setId(rs.getInt("funcionario_id"));
                    funcionario.setNome(rs.getString("funcionario_nome"));
                    produto.setFuncionario(funcionario);
    
                    Fabricante fabricante = new Fabricante();
                    fabricante.setId(rs.getInt("fabricante_id"));
                    fabricante.setNome(rs.getString("fabricante_nome"));
                    produto.setFabricante(fabricante);
    
                    Fornecedor fornecedor = new Fornecedor();
                    fornecedor.setId(rs.getInt("fornecedor_id"));
                    fornecedor.setNome(rs.getString("fornecedor_nome"));
                    produto.setFornecedor(fornecedor);
    
                    produto.setCategorias(new ArrayList<>());
                    produtoMap.put(produtoId, produto);
                }
    
                int categoriaId = rs.getInt("categoria_id");
                if (categoriaId > 0) {
                    Categoria categoria = new Categoria();
                    categoria.setId(categoriaId);
                    categoria.setNome(rs.getString("categoria_nome"));
                    produto.getCategorias().add(categoria);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar produtos: " + e.getMessage());
            throw e;
        }               
        return new ArrayList<>(produtoMap.values());
    }

    public static Produto buscarPorId(Connection conn, int id) throws SQLException {
        String sql = "SELECT p.id, p.nome, p.valor, p.qntEstoque, p.dataValidade, "
               + "p.dataFabricacao, p.qntMedida, p.embalagem, "
               + "f.id AS funcionario_id, f.nome AS funcionario_nome, "
               + "fa.id AS fabricante_id, fa.nome AS fabricante_nome, "
               + "fo.id AS fornecedor_id, fo.nome AS fornecedor_nome, "
               + "c.id AS categoria_id, c.nome AS categoria_nome "
               + "FROM produto p "
               + "JOIN funcionario f ON p.funcionario_id = f.id "
               + "JOIN fabricante fa ON p.fabricante_id = fa.id "
               + "JOIN fornecedor fo ON p.fornecedor_id = fo.id "
               + "JOIN ProdCategoria pc ON p.id = pc.produto_id "
               + "JOIN categoria c ON pc.categoria_id = c.id "
               + "WHERE p.id = ?";

        Produto produto = null;
    
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
    
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    produto = new Produto();
                    produto.setId(rs.getInt("id"));
                    produto.setNome(rs.getString("nome"));
                    produto.setValor(rs.getBigDecimal("valor"));
                    produto.setQntEstoque(rs.getInt("qntEstoque"));
                    produto.setDataValidade(YearMonth.from(rs.getDate("dataValidade").toLocalDate()));
                    produto.setDataFabricacao(YearMonth.from(rs.getDate("dataFabricacao").toLocalDate()));
                    produto.setQntMedida(rs.getString("qntMedida"));
                    produto.setEmbalagem(rs.getString("embalagem"));
    
                    Categoria categoria = new Categoria();
                    categoria.setId(rs.getInt("categoria_id"));
                    categoria.setNome(rs.getString("categoria_nome"));
                    produto.setCategorias(new ArrayList<>());
                    produto.getCategorias().add(categoria);
    
                    Funcionario funcionario = new Funcionario();
                    funcionario.setId(rs.getInt("funcionario_id"));
                    funcionario.setNome(rs.getString("funcionario_nome"));
                    produto.setFuncionario(funcionario);
    
                    Fabricante fabricante = new Fabricante();
                    fabricante.setId(rs.getInt("fabricante_id"));
                    fabricante.setNome(rs.getString("fabricante_nome"));
                    produto.setFabricante(fabricante);
    
                    Fornecedor fornecedor = new Fornecedor();
                    fornecedor.setId(rs.getInt("fornecedor_id"));
                    fornecedor.setNome(rs.getString("fornecedor_nome"));
                    produto.setFornecedor(fornecedor);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar produto por ID: " + e.getMessage());
            throw e;
        }
    
        return produto;
    }
    
    public static void deletarProduto(Connection conn, Produto p) throws SQLException {
        String sql = "delete from produto where id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, p.getId());
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Produto excluído com sucesso!");
            } else {
                System.out.println("Nenhum produto encontrado para o ID " + p.getId());
            }
        } catch (SQLException e) {
            System.err.println("Erro ao excluir produto: " + e.getMessage());
            throw e;
        }
    }
}
