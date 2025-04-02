package views.Vendas;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.awt.print.Printable;
import java.awt.Graphics;
import java.awt.print.PageFormat;

public class ImprimirRelatorioGeral {
    private JTable tabela;

    public ImprimirRelatorioGeral(JTable tabela) {
        this.tabela = tabela;
    }

    public void imprimir() {
        PrinterJob printerJob = PrinterJob.getPrinterJob();
        printerJob.setPrintable(new TabelaPrintable());

        boolean doPrint = printerJob.printDialog();
        if (doPrint) {
            try {
                printerJob.print();
                JOptionPane.showMessageDialog(null, "Relatório enviado para a impressora com sucesso!", "Sucesso",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (PrinterException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Erro ao imprimir o relatório: " + e.getMessage(), "Erro",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private class TabelaPrintable implements Printable {
        @Override
        public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
            if (pageIndex > 0) {
                return NO_SUCH_PAGE;
            }

            int startX = (int) pf.getImageableX() + 20;
            int startY = (int) pf.getImageableY() + 20;
            int rowHeight = 20;
            int colSpacing = 100;

            g.setFont(g.getFont().deriveFont(12f));

            g.drawString("Relatório de Vendas", startX, startY);
            g.drawString("Data: " + java.time.LocalDate.now(), startX, startY + rowHeight);

            int y = startY + 2 * rowHeight;

            for (int i = 0; i < tabela.getColumnCount(); i++) {
                g.drawString(tabela.getColumnName(i), startX + (i * colSpacing), y);
            }
            y += rowHeight;

            g.drawLine(startX, y - 5, startX + (tabela.getColumnCount() * colSpacing), y - 5);

            for (int i = 0; i < tabela.getRowCount(); i++) {
                for (int j = 0; j < tabela.getColumnCount(); j++) {
                    String value = tabela.getValueAt(i, j).toString();
                    g.drawString(value, startX + (j * colSpacing), y);
                }
                y += rowHeight;
            }

            double totalVendas = calcularTotalVendas();
            y += rowHeight;
            g.drawString("Total de Vendas: R$ " + String.format("%.2f", totalVendas), startX, y);

            return PAGE_EXISTS;
        }

        private double calcularTotalVendas() {
            double total = 0.0;
            for (int i = 0; i < tabela.getRowCount(); i++) {
                try {
                    double valorTotal = Double.parseDouble(tabela.getValueAt(i, 3).toString());
                    total += valorTotal;
                } catch (NumberFormatException e) {
                    // Trate possíveis erros de formatação
                }
            }
            return total;
        }
    }
}