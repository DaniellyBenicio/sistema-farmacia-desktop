package controllers.Relatorio;

import dao.Relatorio.RelatorioVendasDAO;
import dao.Relatorio.RelatorioVendasDAO.VendaRelatorio;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class RelatorioVendasController {

    public static List<VendaRelatorio> buscarRelatorioVendas(Connection conn, String dataFiltro, String vendedorFiltro, String pagamentoFiltro, String dataInicioPersonalizada, String dataFimPersonalizada) throws SQLException {
        try {
            RelatorioVendasDAO dao = new RelatorioVendasDAO(conn);
            return dao.buscarRelatorioVendas(dataFiltro, vendedorFiltro, pagamentoFiltro, dataInicioPersonalizada, dataFimPersonalizada);
        } catch (SQLException e) {
            throw new SQLException("Erro ao buscar relatório de vendas.", e);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Erro nos parâmetros do relatório: " + e.getMessage(), e);
        }
    }
}