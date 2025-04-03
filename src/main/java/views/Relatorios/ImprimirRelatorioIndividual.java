package views.Relatorios;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.print.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.text.DecimalFormat;

public class ImprimirRelatorioIndividual implements Printable {

    private String detalhesVenda;
    private DefaultTableModel itensTableModel;
    private String informacoesVenda;

    public ImprimirRelatorioIndividual(String detalhesVenda, DefaultTableModel itensTableModel,
            String informacoesVenda) {
        this.detalhesVenda = detalhesVenda;
        this.itensTableModel = itensTableModel;
        this.informacoesVenda = informacoesVenda;
    }

    public void imprimir() {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintable(this);

        if (job.printDialog()) {
            try {
                job.print();
            } catch (PrinterException e) {
                JOptionPane.showMessageDialog(null, "Erro ao imprimir relatório: " + e.getMessage() + "\n"
                        + java.util.Arrays.toString(e.getStackTrace()), "Erro", JOptionPane.ERROR_MESSAGE);
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
        Font boldFont = new Font("Arial", Font.BOLD, 12);
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

        int yInformacoes = startY + (headerLines.length + 2) * rowHeight + 5;
        String[] vendaDetails = detalhesVenda.split("\n");

        for (String detail : vendaDetails) {
            if (detail.startsWith("Código da Venda:") || detail.startsWith("Data:") ||
                    detail.startsWith("Horário:") || detail.startsWith("Vendedor:") ||
                    detail.startsWith("CPF do Cliente:") || detail.startsWith("Valor Total:")) {

                String[] keyValue = detail.split(":", 2);
                if (keyValue.length == 2) {
                    String label = keyValue[0] + ":";
                    String value = keyValue[1].trim();

                    g.setFont(boldFont);
                    g.drawString(label, startX, yInformacoes);
                    g.setFont(regularFont);
                    g.drawString(value, startX + g.getFontMetrics(boldFont).stringWidth(label) + 5, yInformacoes);
                    yInformacoes += rowHeight;
                }
            } else if (detail.startsWith("Formas de Pagamento:")) {
                String[] keyValue = detail.split(":", 2);
                if (keyValue.length == 2) {
                    String label = keyValue[0] + ":";
                    String[] formasPagamento = keyValue[1].split(",");
                    StringBuilder formasFormatadas = new StringBuilder();

                    for (String forma : formasPagamento) {
                        String formaTrimmed = forma.trim();
                        if (formaTrimmed.equals("DINHEIRO")) {
                            formasFormatadas.append("Dinheiro, ");
                        } else if (formaTrimmed.equals("CARTAO_CREDITO")) {
                            formasFormatadas.append("Cartão de Crédito, ");
                        } else if (formaTrimmed.equals("CARTAO_DEBITO")) {
                            formasFormatadas.append("Cartão de Débito, ");
                        } else {
                            formasFormatadas.append(formaTrimmed).append(", ");
                        }
                    }

                    if (formasFormatadas.length() > 2) {
                        formasFormatadas.delete(formasFormatadas.length() - 2, formasFormatadas.length());
                    }

                    g.setFont(boldFont);
                    g.drawString(label, startX, yInformacoes);
                    g.setFont(regularFont);
                    g.drawString(formasFormatadas.toString(),
                            startX + g.getFontMetrics(boldFont).stringWidth(label) + 5, yInformacoes);
                    yInformacoes += rowHeight;
                }
            } else if (!detail.startsWith("Código do Medicamento") && !detail.startsWith("Código do Produto")) {
                g.drawString(detail, startX, yInformacoes);
                yInformacoes += rowHeight;
            }
        }

        int yTabela = yInformacoes + rowHeight + 10;

        g.setFont(headerFont);
        FontMetrics fmHeader = g.getFontMetrics();
        int tableColumnCount = itensTableModel.getColumnCount();

        int[] colWidths = new int[tableColumnCount];
        int totalTableWidth = 0;
        int descricaoColumnIndex = 1;
        colWidths[descricaoColumnIndex] = pageWidth / 3;

        for (int i = 0; i < tableColumnCount; i++) {
            if (i != descricaoColumnIndex) {
                String columnName = itensTableModel.getColumnName(i);
                if (columnName.equals("Quantidade")) {
                    columnName = "Qnt.";
                } else if (columnName.equals("Valor Uni.")) {
                    columnName = "Valor Uni.";
                }
                colWidths[i] = fmHeader.stringWidth(columnName) + 20;
            }
            totalTableWidth += colWidths[i];
        }

        int remainingSpace = pageWidth - totalTableWidth - 40;
        int extraSpace = remainingSpace / (tableColumnCount - 1);

        startX = (int) pf.getImageableX() + 20;
        for (int i = 0; i < tableColumnCount; i++) {
            String columnName = itensTableModel.getColumnName(i);
            if (columnName.equals("Quantidade")) {
                columnName = "Qnt.";
            } else if (columnName.equals("Valor Uni.")) {
                columnName = "Valor Uni.";
            }
            g.drawString(columnName, startX, yTabela);
            startX += colWidths[i];
            if (i != descricaoColumnIndex) {
                colWidths[i] += extraSpace;
            }
        }
        yTabela += rowHeight;

        startX = (int) pf.getImageableX() + 20;
        g.drawLine(startX, yTabela - 17, startX + getTotalColumnWidth(colWidths), yTabela - 17);

        g.setFont(regularFont);
        yTabela += 5;

        for (int i = 0; i < itensTableModel.getRowCount(); i++) {
            startX = (int) pf.getImageableX() + 20;
            int initialY = yTabela;
            int initialStartX = startX;
            int maxDescricaoY = yTabela;

            for (int j = 0; j < tableColumnCount; j++) {
                String value = itensTableModel.getValueAt(i, j).toString();
                if (j == descricaoColumnIndex) {
                    int maxLineWidth = colWidths[j];
                    String[] words = value.toUpperCase().split(" ");
                    String currentLine = "";
                    for (String word : words) {
                        if (fmRegular.stringWidth(currentLine + word) < maxLineWidth) {
                            currentLine += word + " ";
                        } else {
                            g.drawString(currentLine, startX, yTabela);
                            yTabela += rowHeight;
                            currentLine = word + " ";
                        }
                    }
                    g.drawString(currentLine, startX, yTabela);
                    if (yTabela > maxDescricaoY) {
                        maxDescricaoY = yTabela;
                    }
                    startX += colWidths[j];
                    yTabela = initialY;
                } else {
                    int valueWidth = fmRegular.stringWidth(value);
                    int x = startX + (colWidths[j] - valueWidth) / 2;
                    g.drawString(value, x, yTabela);
                    startX += colWidths[j];
                }
            }
            yTabela = maxDescricaoY + rowHeight;
            g.drawLine((int) pf.getImageableX() + 20, yTabela - 15,
                    (int) pf.getImageableX() + 20 + getTotalColumnWidth(colWidths), yTabela - 15);
            startX = initialStartX;
        }

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