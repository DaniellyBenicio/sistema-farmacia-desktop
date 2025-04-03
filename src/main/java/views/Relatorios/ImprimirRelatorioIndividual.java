package views.Relatorios;

import javax.print.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.print.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.text.DecimalFormat;
import java.math.BigDecimal;

public class ImprimirRelatorioIndividual implements Printable {

    private String detalhesVenda;
    private DefaultTableModel itensTableModel;

    public ImprimirRelatorioIndividual(String detalhesVenda, DefaultTableModel itensTableModel) {
        this.detalhesVenda = detalhesVenda;
        this.itensTableModel = itensTableModel;
    }

    public void imprimir() {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintable(this);

        if (job.printDialog()) {
            try {
                job.print();
            } catch (PrinterException e) {
                JOptionPane.showMessageDialog(null, "Erro ao imprimir relatório: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    @Override
    public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
        if (pageIndex > 0) {
            return NO_SUCH_PAGE;
        }

        int startX = (int) pf.getImageableX() + 20;
        int startY = (int) pf.getImageableY() + 40;
        int rowHeight = 20;
        int pageWidth = (int) pf.getImageableWidth();

        Font titleFont = new Font("Arial", Font.BOLD, 14);
        Font headerFont = new Font("Arial", Font.BOLD, 12);
        Font regularFont = new Font("Arial", Font.PLAIN, 12);
        DecimalFormat df = new DecimalFormat("#,##0.00");

        g.setFont(titleFont);
        FontMetrics fmTitle = g.getFontMetrics();
        String title = "Relatório Individual de Venda";
        int titleWidth = fmTitle.stringWidth(title);
        g.drawString(title, startX + (pageWidth - titleWidth) / 2, startY);

        g.setFont(regularFont);
        FontMetrics fmRegular = g.getFontMetrics();

        String[] headerLines = {
                "Farmácia XYZ - CNPJ: 12.345.678/0001-99",
                "Rua Exemplo, 123 - Centro, Cidade - UF",
                "Data/Hora: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
        };

        for (int i = 0; i < headerLines.length; i++) {
            String line = headerLines[i];
            int lineWidth = fmRegular.stringWidth(line);
            int x = startX + (pageWidth - lineWidth) / 2;
            g.drawString(line, x, startY + (i + 1) * rowHeight);
        }

        int y = startY + (headerLines.length + 2) * rowHeight + 5;

        g.setFont(headerFont);
        FontMetrics fmHeader = g.getFontMetrics();
        int tableColumnCount = itensTableModel.getColumnCount();

        int[] colWidths = new int[tableColumnCount];
        int totalTableWidth = 0;
        for (int i = 0; i < tableColumnCount; i++) {
            colWidths[i] = fmHeader.stringWidth(itensTableModel.getColumnName(i)) + 20;
            totalTableWidth += colWidths[i];
        }

        int remainingSpace = pageWidth - totalTableWidth - 40;
        int extraSpace = remainingSpace / tableColumnCount;

        startX = (int) pf.getImageableX() + 20;
        for (int i = 0; i < tableColumnCount; i++) {
            g.drawString(itensTableModel.getColumnName(i), startX, y);
            startX += colWidths[i] + extraSpace;
            colWidths[i] += extraSpace;
        }
        y += rowHeight;

        startX = (int) pf.getImageableX() + 20;
        g.drawLine(startX, y - 17, startX + getTotalColumnWidth(colWidths), y - 17);

        g.setFont(regularFont);
        y += 5;

        BigDecimal totalVendas = BigDecimal.ZERO;

        for (int i = 0; i < itensTableModel.getRowCount(); i++) {
            startX = (int) pf.getImageableX() + 20;
            for (int j = 0; j < tableColumnCount; j++) {
                String value = itensTableModel.getValueAt(i, j).toString();
                g.drawString(value, startX, y);
                startX += colWidths[j];
            }

            try {
                String valorString = itensTableModel.getValueAt(i, 5).toString().replaceAll("[^0-9,.]", "").replace(",", ".");
                BigDecimal valorTotal = BigDecimal.valueOf(Double.parseDouble(valorString));
                totalVendas = totalVendas.add(valorTotal);
            } catch (NumberFormatException e) {
                System.err.println("Erro ao converter valor: " + itensTableModel.getValueAt(i, 5));
                e.printStackTrace();
            }
            y += rowHeight;
            g.drawLine((int) pf.getImageableX() + 20, y - 15,
                    (int) pf.getImageableX() + 20 + getTotalColumnWidth(colWidths), y - 15);
        }

        y += rowHeight;
        g.drawString("Total de Vendas: R$ " + df.format(totalVendas.doubleValue()), (int) pf.getImageableX() + 20, y);

        return PAGE_EXISTS;
    }

    private int getTotalColumnWidth(int[] colWidths) {
        int totalWidth = 0;
        for (int width : colWidths) {
            totalWidth += width;
        }
        return totalWidth;
    }
}