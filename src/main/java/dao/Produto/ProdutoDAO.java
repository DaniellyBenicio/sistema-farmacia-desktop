package dao.Produto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.sql.Date;
import java.time.YearMonth;

import models.Produto.Produto;
import models.Fabricante.Fabricante;
import models.Fornecedor.Fornecedor;
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
