package views.Estoque;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;

import models.Medicamento.Medicamento;

class RealizarPedido extends JDialog {
    private List<Medicamento> medicamentos;
    private String dataHoraCriacao;
    private List<JTextField> quantidadeFields;

    public RealizarPedido(JFrame parent, List<Medicamento> medicamentos) {
        super(parent, "Lista de Pedidos", true);
        this.medicamentos = medicamentos;
        this.dataHoraCriacao = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());
        this.quantidadeFields = new ArrayList<>();

        int width = 800;
        int height = 600;
        setSize(width, height);
        setPreferredSize(new Dimension(width, height));
        setMinimumSize(new Dimension(width, height));
        setMaximumSize(new Dimension(width, height));

        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Lista de Pedidos de Medicamentos", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(new Color(24, 39, 55));
        topPanel.add(titleLabel, BorderLayout.NORTH);

        JLabel dataLabel = new JLabel("Lista criada em: " + dataHoraCriacao);
        dataLabel.setFont(new Font("Arial", Font.ITALIC, 10));
        dataLabel.setForeground(Color.DARK_GRAY);
        topPanel.add(dataLabel, BorderLayout.WEST);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        JPanel pedidoPanel = criarPedidoPanel();
        JScrollPane scrollPane = new JScrollPane(pedidoPanel);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JButton realizarPedidoButton = new JButton("Finalizar Pedido");
        realizarPedidoButton.setFont(new Font("Arial", Font.BOLD, 14));
        realizarPedidoButton.setBackground(new Color(24, 39, 55));
        realizarPedidoButton.setForeground(Color.WHITE);
        realizarPedidoButton.addActionListener(e -> imprimirLista());

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.add(realizarPedidoButton);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
        pack();
    }

    private JPanel criarPedidoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 2, 2, 2);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(criarHeaderPanel(), gbc);

        for (int i = 0; i < medicamentos.size(); i++) {
            Medicamento medicamento = medicamentos.get(i);
            gbc.gridy = i + 1;
            panel.add(criarMedicamentoPanel(medicamento), gbc);
        }

        gbc.gridy = medicamentos.size() + 1;
        gbc.weighty = 1.0;
        panel.add(new JPanel(), gbc);

        return panel;
    }

    private JPanel criarHeaderPanel() {
        JPanel headerPanel = new JPanel(new GridLayout(1, 5));
        headerPanel.setBackground(Color.LIGHT_GRAY);
        headerPanel.setPreferredSize(new Dimension(750, 25));
        headerPanel.setMaximumSize(new Dimension(750, 25));

        headerPanel.add(criarLabel("Nome", Font.BOLD, 14));
        headerPanel.add(criarLabel("Categoria", Font.BOLD, 14));
        headerPanel.add(criarLabel("Medida", Font.BOLD, 14));
        headerPanel.add(criarLabel("Preço Unitário", Font.BOLD, 14));
        headerPanel.add(criarLabel("Qtd. Solicitada", Font.BOLD, 14));

        return headerPanel;
    }

    private JPanel criarMedicamentoPanel(Medicamento medicamento) {
        JPanel medicamentoPanel = new JPanel(new GridLayout(1, 5));
        medicamentoPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));
        medicamentoPanel.setPreferredSize(new Dimension(750, 25));
        medicamentoPanel.setMaximumSize(new Dimension(750, 25));

        medicamentoPanel.add(criarLabel(medicamento.getNome(), Font.PLAIN, 12));
        medicamentoPanel.add(criarLabel(medicamento.getCategoria().getNome(), Font.PLAIN, 12));
        medicamentoPanel.add(criarLabel(medicamento.getDosagem(), Font.PLAIN, 12));
        medicamentoPanel.add(criarLabel(String.format("R$ %.2f", medicamento.getValorUnit()), Font.PLAIN, 12));

        JTextField quantidadeField = new JTextField(3);
        quantidadeField.setPreferredSize(new Dimension(40, 20));
        quantidadeField.setMaximumSize(new Dimension(40, 20));
        quantidadeFields.add(quantidadeField);
        medicamentoPanel.add(quantidadeField);

        return medicamentoPanel;
    }

    private JLabel criarLabel(String texto, int estiloFonte, int tamanhoFonte) {
        JLabel label = new JLabel(texto, SwingConstants.CENTER);
        label.setFont(new Font("Arial", estiloFonte, tamanhoFonte));
        label.setVerticalAlignment(SwingConstants.CENTER);
        return label;
    }

    private void imprimirLista() {
        List<PedidoItem> pedidoItens = new ArrayList<>();
        boolean valid = true;

        for (int i = 0; i < medicamentos.size(); i++) {
            String text = quantidadeFields.get(i).getText();
            try {
                int quantidade = Integer.parseInt(text);
                if (quantidade < 0) {
                    JOptionPane.showMessageDialog(this, "A quantidade não pode ser negativa.", "Erro",
                            JOptionPane.ERROR_MESSAGE);
                    valid = false;
                    break;
                }
                Medicamento medicamento = medicamentos.get(i);
                pedidoItens.add(new PedidoItem(medicamento, quantidade));

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Digite uma quantidade válida para todos os medicamentos.", "Erro",
                        JOptionPane.ERROR_MESSAGE);
                valid = false;
                break;
            }
        }
        if (valid) {
            PrinterJob job = PrinterJob.getPrinterJob();
            job.setPrintable(new PedidoPrintable("Lista de Pedidos de Medicamentos", dataHoraCriacao, pedidoItens));

            boolean doPrint = job.printDialog();
            if (doPrint) {
                try {
                    job.print();
                } catch (PrinterException e) {
                    JOptionPane.showMessageDialog(null, "Erro ao imprimir: " + e.getMessage(), "Erro",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private class PedidoItem {
        Medicamento medicamento;
        int quantidade;

        public PedidoItem(Medicamento medicamento, int quantidade) {
            this.medicamento = medicamento;
            this.quantidade = quantidade;
        }
    }

    private class PedidoPrintable implements Printable {
        private String title;
        private String date;
        private List<PedidoItem> pedidoItens;

        public PedidoPrintable(String title, String date, List<PedidoItem> pedidoItens) {
            this.title = title;
            this.date = date;
            this.pedidoItens = pedidoItens;
        }

        @Override
        public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
            if (pageIndex > 0) {
                return NO_SUCH_PAGE;
            }

            Graphics2D g2d = (Graphics2D) graphics;
            g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

            int y = 20;
            int x = 10;
            int pageWidth = (int) pageFormat.getImageableWidth();

            g2d.setFont(new Font("Arial", Font.BOLD, 14));
            FontMetrics fm = g2d.getFontMetrics();
            int titleWidth = fm.stringWidth(title);
            int titleX = (pageWidth - titleWidth) / 2;
            g2d.drawString(title, titleX, y);
            y += fm.getHeight() + 10;

            g2d.setFont(new Font("Arial", Font.ITALIC, 10));
            g2d.drawString("Lista criada em: " + date, x, y);
            y += fm.getHeight() + 5;

            g2d.setFont(new Font("Arial", Font.BOLD, 10));
            g2d.drawString("Nome", x, y);
            g2d.drawString("Categoria", x + 130, y);
            g2d.drawString("Medida", x + 250, y);
            g2d.drawString("Preço Unit.", x + 370, y);
            g2d.drawString("Qnt. Solicitada", x + 470, y);
            y += fm.getHeight() + 5;

            g2d.drawLine(x, y - 20, pageWidth - 20, y - 20);

            g2d.setFont(new Font("Arial", Font.PLAIN, 10));
            for (PedidoItem item : pedidoItens) {
                int itemHeight = fm.getHeight();

                g2d.drawString(item.medicamento.getNome(), x, y);
                g2d.drawString(item.medicamento.getCategoria().getNome(), x + 130, y);
                g2d.drawString(item.medicamento.getDosagem(), x + 250, y);
                g2d.drawString(String.format("R$ %.2f", item.medicamento.getValorUnit()), x + 370, y);
                g2d.drawString(String.valueOf(item.quantidade), x + 470, y);

                g2d.drawLine(x, y + 2, pageWidth - 20, y + 2);

                y += itemHeight + 2;
            }

            return PAGE_EXISTS;
        }
    }
}