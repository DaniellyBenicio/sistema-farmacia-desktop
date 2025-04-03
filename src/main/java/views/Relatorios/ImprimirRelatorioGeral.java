package views.Relatorios;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.math.BigDecimal;
import java.awt.print.Printable;
import java.awt.Graphics;
import java.awt.print.PageFormat;
import java.awt.FontMetrics;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.text.DecimalFormat;
import java.awt.Font;
import javax.swing.JDialog;
import java.awt.Window;
import javax.swing.SwingUtilities;
import java.awt.Frame;
import java.awt.Dialog;
import javax.swing.JLabel;
import java.awt.BorderLayout;
import javax.swing.SwingConstants;

public class ImprimirRelatorioGeral {
    private JTable tabela;

    public ImprimirRelatorioGeral(JTable tabela) {
        this.tabela = tabela;
    }

    public void imprimir() {
        PrinterJob printerJob = PrinterJob.getPrinterJob();
        printerJob.setPrintable(new TabelaPrintable());

        Window owner = SwingUtilities.getWindowAncestor(tabela);
        JDialog aguardeDialog;

        if (owner instanceof Frame) {
            aguardeDialog = new JDialog((Frame) owner, "Imprimindo...", true);
        } else if (owner instanceof Dialog) {
            aguardeDialog = new JDialog((Dialog) owner, "Imprimindo...", true);
        } else {
            aguardeDialog = new JDialog();
            aguardeDialog.setTitle("Imprimindo...");
            aguardeDialog.setModal(true);
        }

        aguardeDialog.setLayout(new BorderLayout());
        aguardeDialog.setSize(300, 150);
        aguardeDialog.setLocationRelativeTo(tabela);
        JLabel mensagem = new JLabel("Aguarde, imprimindo relatório geral...", SwingConstants.CENTER);
        mensagem.setFont(new Font("Arial", Font.BOLD, 14));
        aguardeDialog.add(mensagem, BorderLayout.CENTER);
        aguardeDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

        new Thread(() -> {
            SwingUtilities.invokeLater(() -> aguardeDialog.setVisible(true));
            try {
                Thread.sleep(3000);

                boolean doPrint = printerJob.printDialog();
                if (doPrint) {
                    printerJob.print();
                    SwingUtilities.invokeLater(() -> {
                        aguardeDialog.dispose();
                        JOptionPane.showMessageDialog(tabela, "Relatório pronto!", "Sucesso",
                                JOptionPane.INFORMATION_MESSAGE);
                    });
                } else {
                    SwingUtilities.invokeLater(() -> aguardeDialog.dispose());
                }
            } catch (PrinterException | InterruptedException e) {
                SwingUtilities.invokeLater(() -> {
                    aguardeDialog.dispose();
                    JOptionPane.showMessageDialog(tabela, "Erro ao imprimir o relatório: " + e.getMessage(), "Erro",
                            JOptionPane.ERROR_MESSAGE);
                });
                e.printStackTrace();
            }
        }).start();
    }

    private class TabelaPrintable implements Printable {
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
            DecimalFormat df2 = new DecimalFormat("#.00");

            g.setFont(titleFont);
            FontMetrics fmTitle = g.getFontMetrics();
            String title = "Relatório Geral de Vendas";
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
            int tableColumnCount = tabela.getColumnCount();
            if (tableColumnCount > 0) {
                tableColumnCount = tableColumnCount - 1;
            }

            int[] colWidths = new int[tableColumnCount];
            int totalTableWidth = 0;
            for (int i = 0; i < tableColumnCount; i++) {
                colWidths[i] = fmHeader.stringWidth(tabela.getColumnName(i)) + 20;
                totalTableWidth += colWidths[i];
            }

            int remainingSpace = pageWidth - totalTableWidth - 40;
            int extraSpace = remainingSpace / tableColumnCount;

            startX = (int) pf.getImageableX() + 20;
            for (int i = 0; i < tableColumnCount; i++) {
                g.drawString(tabela.getColumnName(i), startX, y);
                startX += colWidths[i] + extraSpace;
                colWidths[i] += extraSpace;
            }
            y += rowHeight;

            startX = (int) pf.getImageableX() + 20;
            g.drawLine(startX, y - 17, startX + getTotalColumnWidth(colWidths), y - 17);

            g.setFont(regularFont);
            y += 5;

            BigDecimal totalVendas = BigDecimal.ZERO;

            for (int i = 0; i < tabela.getRowCount(); i++) {
                startX = (int) pf.getImageableX() + 20;
                for (int j = 0; j < tableColumnCount; j++) {
                    String value = tabela.getValueAt(i, j).toString();
                    g.drawString(value, startX, y);
                    startX += colWidths[j];
                }

                try {
                    String valorString = tabela.getValueAt(i, 3).toString().replaceAll("[^0-9,.]", "").replace(",",
                            ".");
                    BigDecimal valorTotal = BigDecimal.valueOf(Double.parseDouble(valorString));
                    totalVendas = totalVendas.add(valorTotal);

                } catch (NumberFormatException e) {
                    System.err.println("Erro ao converter valor: " + tabela.getValueAt(i, 3));
                    e.printStackTrace();
                }
                y += rowHeight;
                g.drawLine((int) pf.getImageableX() + 20, y - 15,
                        (int) pf.getImageableX() + 20 + getTotalColumnWidth(colWidths), y - 15);
            }

            y += rowHeight;
            g.drawString("Total de Vendas: R$ " + df.format(totalVendas.doubleValue()), (int) pf.getImageableX() + 20,
                    y);

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
}