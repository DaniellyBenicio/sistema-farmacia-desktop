package views.Estoque;

import models.Produto.Produto;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

import java.awt.*;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

class RealizarPedidoProduto extends JDialog {

    private List<Produto> produtos;
    private String dataHoraCriacao;
    private JTable tabela;
    private DefaultTableModel modeloTabela;
    private NumberFormat formatadorNumero;
    private DecimalFormat formatadorDecimal;
    private final int[] larguraColunas = { 150, 120, 70, 80, 40, 120, 80 };

    public RealizarPedidoProduto(JFrame parent, List<Produto> produtos) {
        super(parent, "Realizar Pedido de Produtos", true);
        this.produtos = produtos;
        this.dataHoraCriacao = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());
        this.formatadorNumero = NumberFormat.getNumberInstance(Locale.getDefault());
        this.formatadorDecimal = new DecimalFormat("#,###");

        configurarJanela();
        adicionarComponentes();

        pack();
    }

    private void configurarJanela() {
        int largura = 1200;
        int altura = 700;
        setSize(largura, altura);
        setPreferredSize(new Dimension(largura, altura));
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setResizable(false);
    }

    private void adicionarComponentes() {
        JPanel painelPrincipal = new JPanel(new BorderLayout());
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel painelSuperior = criarPainelSuperior();
        painelPrincipal.add(painelSuperior, BorderLayout.NORTH);

        criarTabela();

        JScrollPane scrollPane = new JScrollPane(tabela);
        painelPrincipal.add(scrollPane, BorderLayout.CENTER);

        JButton botaoConfirmarPedido = criarBotaoConfirmarPedido();
        JPanel painelInferior = new JPanel(new FlowLayout(FlowLayout.CENTER));
        painelInferior.add(botaoConfirmarPedido);
        painelPrincipal.add(painelInferior, BorderLayout.SOUTH);

        add(painelPrincipal);
    }

    private JPanel criarPainelSuperior() {
        JPanel painelSuperior = new JPanel(new BorderLayout());

        JLabel labelFarmacia = new JLabel("Farmácia", SwingConstants.CENTER);
        labelFarmacia.setFont(new Font("Arial", Font.BOLD, 18));
        labelFarmacia.setForeground(new Color(24, 39, 55));
        painelSuperior.add(labelFarmacia, BorderLayout.NORTH);

        JLabel labelTitulo = new JLabel("Lista de Pedidos de Produtos", SwingConstants.CENTER);
        labelTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        labelTitulo.setForeground(new Color(24, 39, 55));
        painelSuperior.add(labelTitulo, BorderLayout.CENTER);

        JLabel labelData = new JLabel("Lista criada em: " + dataHoraCriacao);
        labelData.setFont(new Font("Arial", Font.ITALIC, 10));
        labelData.setForeground(Color.DARK_GRAY);
        painelSuperior.add(labelData, BorderLayout.SOUTH);

        return painelSuperior;
    }

    private void criarTabela() {
        String[] nomeColunas = {
                "Nome",
                "Categoria",
                "Embalagem",
                "Qnt. Embalagem",
                "Medida",
                "Fornecedor",
                "Preço Unitário",
                "Qnt. Solicitada"
        };

        modeloTabela = new DefaultTableModel(nomeColunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 7;
            }
        };

        tabela = new JTable(modeloTabela);
        tabela.setRowHeight(30);

        for (int i = 0; i < larguraColunas.length; i++) {
            tabela.getColumnModel().getColumn(i).setPreferredWidth(larguraColunas[i]);
        }

        tabela.getTableHeader().setFont(new Font("Arial", Font.BOLD, 16));
        adicionarDadosTabela();
        configurarEditorColunaQuantidade();
        alinharColunaQuantidade();
    }

    private void adicionarDadosTabela() {
        for (Produto produto : produtos) {
            Object[] dadosLinha = {
                    produto.getNome(),
                    produto.getCategoria().getNome(),
                    produto.getEmbalagem(),
                    produto.getQntEmbalagem(),
                    produto.getQntMedida(),
                    produto.getFornecedor().getNome(),
                    String.format("R$ %.2f", produto.getValor()),
                    ""
            };
            modeloTabela.addRow(dadosLinha);
        }
    }

    private void configurarEditorColunaQuantidade() {
        JTextField campoQuantidade = new JTextField();
        ((AbstractDocument) campoQuantidade.getDocument())
                .setDocumentFilter(new FiltroFormatoNumerico(formatadorDecimal, this));
        DefaultCellEditor editorQuantidade = new DefaultCellEditor(campoQuantidade);
        tabela.getColumnModel().getColumn(7).setCellEditor(editorQuantidade);
    }

    private void alinharColunaQuantidade() {
        DefaultTableCellRenderer alinhamentoDireita = new DefaultTableCellRenderer();
        alinhamentoDireita.setHorizontalAlignment(SwingConstants.CENTER);
        tabela.getColumnModel().getColumn(7).setCellRenderer(alinhamentoDireita);
    }

    private JButton criarBotaoConfirmarPedido() {
        JButton botaoConfirmarPedido = new JButton("Confirmar Pedido");
        botaoConfirmarPedido.setFont(new Font("Arial", Font.BOLD, 14));
        botaoConfirmarPedido.setBackground(new Color(24, 39, 55));
        botaoConfirmarPedido.setForeground(Color.WHITE);
        botaoConfirmarPedido.setFocusPainted(false);

        botaoConfirmarPedido.addActionListener(e -> imprimirLista());

        return botaoConfirmarPedido;
    }

    private void imprimirLista() {
        List<ItemPedido> itensPedido = new ArrayList<>();
        boolean valido = true;
        boolean campoVazioEncontrado = false;

        for (int i = 0; i < tabela.getRowCount(); i++) {
            Object valorQuantidade = tabela.getValueAt(i, 7);
            int quantidade = 0;

            if (valorQuantidade instanceof Number) {
                quantidade = ((Number) valorQuantidade).intValue();
            } else if (valorQuantidade instanceof String) {
                String quantidadeString = (String) valorQuantidade;
                if (quantidadeString.isEmpty()) {
                    campoVazioEncontrado = true;
                    break;
                } else {
                    try {
                        quantidade = formatadorNumero.parse(quantidadeString).intValue();
                    } catch (ParseException e) {
                        exibirMensagemErro("Digite uma quantidade válida para todos os produtos.", "Erro");
                        valido = false;
                        break;
                    }
                }
            } else {
                quantidade = 0;
            }

            if (!campoVazioEncontrado && quantidade == 0) {
                exibirMensagemErro("A quantidade deve ser maior que zero para todos os produtos.", "Erro");
                valido = false;
                break;
            }

            if (quantidade < 0) {
                exibirMensagemErro("A quantidade não pode ser negativa.", "Erro");
                valido = false;
                break;
            }

            Produto produto = produtos.get(i);
            itensPedido.add(new ItemPedido(produto, quantidade));
        }

        if (campoVazioEncontrado) {
            exibirMensagemErro("Por favor, informe a quantidade solicitada para todos os produtos.", "Erro");
            valido = false;
        }

        if (valido) {
            PrinterJob job = PrinterJob.getPrinterJob();
            PageFormat formatoPagina = job.defaultPage();
            formatoPagina.setOrientation(PageFormat.LANDSCAPE);

            job.setPrintable(
                    new ImpressaoPedido(
                            "Farmácia",
                            "Lista de Pedidos de Produtos",
                            dataHoraCriacao,
                            itensPedido,
                            formatadorNumero,
                            formatadorDecimal),
                    formatoPagina);

            if (job.printDialog()) {
                try {
                    job.print();
                } catch (PrinterException e) {
                    exibirMensagemErro("Erro ao imprimir: " + e.getMessage(), "Erro");
                }
            }
        }
    }

    private void exibirMensagemErro(String mensagem, String titulo) {
        JOptionPane.showMessageDialog(this, mensagem, titulo, JOptionPane.ERROR_MESSAGE);
    }

    private class ItemPedido {
        Produto produto;
        int quantidade;

        public ItemPedido(Produto produto, int quantidade) {
            this.produto = produto;
            this.quantidade = quantidade;
        }
    }

    private class FiltroFormatoNumerico extends DocumentFilter {

        private DecimalFormat formatadorDecimal;
        private RealizarPedidoProduto janela;

        public FiltroFormatoNumerico(DecimalFormat formatador, RealizarPedidoProduto janela) {
            this.formatadorDecimal = formatador;
            this.janela = janela;
        }

        @Override
        public void insertString(
                FilterBypass fb, int offset, String string, AttributeSet attr)
                throws BadLocationException {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < string.length(); i++) {
                if (Character.isDigit(string.charAt(i))) {
                    sb.append(string.charAt(i));
                }
            }
            aplicarFormato(fb, offset, 0, sb.toString(), attr);
        }

        @Override
        public void replace(
                FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                throws BadLocationException {
            StringBuilder sb = new StringBuilder();
            if (text != null) {
                for (int i = 0; i < text.length(); i++) {
                    if (Character.isDigit(text.charAt(i))) {
                        sb.append(text.charAt(i));
                    }
                }
            }
            aplicarFormato(fb, offset, length, sb.toString(), attrs);
        }

        private void aplicarFormato(
                FilterBypass fb, int offset, int length, String string, AttributeSet attr)
                throws BadLocationException {
            StringBuilder textoAtual = new StringBuilder(fb.getDocument().getText(0, fb.getDocument().getLength()));
            textoAtual.replace(offset, offset + length, string);
            String texto = textoAtual.toString().replaceAll("[^0-9]", "");

            if (texto.isEmpty()) {
                fb.replace(0, fb.getDocument().getLength(), "", attr);
                return;
            }

            try {
                long numero = Long.parseLong(texto);
                String numeroFormatado = formatadorDecimal.format(numero);
                fb.replace(0, fb.getDocument().getLength(), numeroFormatado, attr);
            } catch (NumberFormatException e) {
                if (!texto.isEmpty()) {
                    JOptionPane.showMessageDialog(
                            janela, "Digite apenas números.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
                fb.replace(0, fb.getDocument().getLength(), "", attr);
            }
        }
    }

    public class ImpressaoPedido implements Printable {

        private String tituloFarmacia;
        private String titulo;
        private String data;
        private List<ItemPedido> itensPedido;
        private NumberFormat formatadorNumero;
        private DecimalFormat formatadorDecimal;
        private final int[] larguraColunas = { 140, 130, 100, 60, 110, 80, 60 };
        private final String[] cabecalhoColunas = {
                "Nome",
                "Categoria",
                "Embalagem",
                "Medida",
                "Fornecedor",
                "Preço Unit.",
                "Qtd."
        };

        public ImpressaoPedido(
                String tituloFarmacia,
                String titulo,
                String data,
                List<ItemPedido> itensPedido,
                NumberFormat formatadorNumero,
                DecimalFormat formatadorDecimal) {
            this.tituloFarmacia = tituloFarmacia;
            this.titulo = titulo;
            this.data = data;
            this.itensPedido = itensPedido;
            this.formatadorNumero = formatadorNumero;
            this.formatadorDecimal = formatadorDecimal;
        }

        @Override
        public int print(Graphics graphics, PageFormat formatoPagina, int indicePagina)
                throws PrinterException {
            if (indicePagina > 0) {
                return NO_SUCH_PAGE;
            }

            Graphics2D g2d = (Graphics2D) graphics;
            g2d.translate(formatoPagina.getImageableX(), formatoPagina.getImageableY());

            int y = 20;
            int x = 10;
            int larguraPagina = (int) formatoPagina.getImageableWidth();

            g2d.setColor(Color.BLACK);
            Font fonteTitulo = new Font("Arial", Font.BOLD, 14);
            g2d.setFont(fonteTitulo);
            FontMetrics fm = g2d.getFontMetrics();

            int larguraTituloFarmacia = fm.stringWidth(tituloFarmacia);
            g2d.drawString(tituloFarmacia, (larguraPagina - larguraTituloFarmacia) / 2, y);
            y += fm.getHeight() + 5;

            int larguraTitulo = fm.stringWidth(titulo);
            g2d.drawString(titulo, (larguraPagina - larguraTitulo) / 2, y);
            y += fm.getHeight() + 10;

            g2d.setFont(new Font("Arial", Font.ITALIC, 12));
            g2d.drawString("Lista criada em: " + data, x, y);
            y += fm.getHeight() + 5;

            y = imprimirCabecalhoColunas(g2d, x, y, larguraPagina);
            Font fonteDados = new Font("Arial", Font.PLAIN, 10);
            g2d.setFont(fonteDados);
            fm = g2d.getFontMetrics();
            for (ItemPedido item : itensPedido) {
                y = imprimirLinha(g2d, x, y, item, fm, larguraPagina);
                if (y > formatoPagina.getImageableHeight() - 20) {
                    return PAGE_EXISTS;
                }
            }

            return PAGE_EXISTS;
        }

        private int imprimirCabecalhoColunas(Graphics2D g2d, int x, int y, int larguraPagina) {
            Font fonteCabecalho = new Font("Arial", Font.BOLD, 12);
            g2d.setFont(fonteCabecalho);
            FontMetrics fm = g2d.getFontMetrics();
            int posicaoXCabecalho = x;

            y += fm.getAscent() - 2;

            for (int i = 0; i < cabecalhoColunas.length; i++) {
                g2d.drawString(cabecalhoColunas[i], posicaoXCabecalho + 5, y);
                posicaoXCabecalho += larguraColunas[i];
            }

            g2d.drawLine(10, y + 2, larguraPagina - 20, y + 2);

            y += fm.getHeight() + 5;

            return y;
        }

        private int imprimirLinha(Graphics2D g2d, int x, int y, ItemPedido item, FontMetrics fm, int larguraPagina) {
            int posicaoXLinha = x;
            int alturaMaximaLinha = y;

            int yNome = imprimirTextoQuebrado(g2d, posicaoXLinha + 5, y, item.produto.getNome(), larguraColunas[0], fm);
            alturaMaximaLinha = Math.max(alturaMaximaLinha, yNome);
            posicaoXLinha += larguraColunas[0];

            int yCategoria = imprimirTextoQuebrado(g2d, posicaoXLinha + 5, y, item.produto.getCategoria().getNome(),
                    larguraColunas[1], fm);
            alturaMaximaLinha = Math.max(alturaMaximaLinha, yCategoria);
            posicaoXLinha += larguraColunas[1];

            imprimirTextoSemQuebra(g2d, posicaoXLinha + 5, y, item.produto.getEmbalagem(), larguraColunas[2], fm);
            posicaoXLinha += larguraColunas[2];

            imprimirTextoSemQuebra(g2d, posicaoXLinha + 5, y, item.produto.getQntMedida(), larguraColunas[3], fm);
            posicaoXLinha += larguraColunas[3];

            int yFornecedor = imprimirTextoQuebrado(g2d, posicaoXLinha + 5, y, item.produto.getFornecedor().getNome(),
                    larguraColunas[4], fm);
            alturaMaximaLinha = Math.max(alturaMaximaLinha, yFornecedor);
            posicaoXLinha += larguraColunas[4];

            String valorUnitarioFormatado = String.format("R$ %.2f", item.produto.getValor());
            imprimirTextoSemQuebra(g2d, posicaoXLinha + 5, y, valorUnitarioFormatado, larguraColunas[5], fm);
            posicaoXLinha += larguraColunas[5];

            String quantidadeFormatada = formatadorDecimal.format(item.quantidade);
            imprimirTextoSemQuebra(g2d, posicaoXLinha + 5, y, quantidadeFormatada, larguraColunas[6], fm);
            posicaoXLinha += larguraColunas[6];

            g2d.setStroke(new BasicStroke(1.2f));
            g2d.drawLine(10, alturaMaximaLinha - 8, larguraPagina - 20, alturaMaximaLinha - 8);

            return alturaMaximaLinha + fm.getHeight() + 5;
        }

        private int imprimirTextoQuebrado(Graphics2D g2d, int x, int y, String texto, int larguraColuna,
                FontMetrics fm) {
            if (texto == null || texto.isEmpty()) {
                return y;
            }

            String[] palavras = texto.split(" ");
            StringBuilder linhaAtual = new StringBuilder();
            int yAtual = y;
            int alturaLinha = fm.getHeight();

            for (String palavra : palavras) {
                String testeLinha = linhaAtual.length() == 0 ? palavra : linhaAtual + " " + palavra;
                if (fm.stringWidth(testeLinha) <= larguraColuna) {
                    linhaAtual.append(linhaAtual.length() == 0 ? palavra : " " + palavra);
                } else {
                    g2d.drawString(linhaAtual.toString(), x, yAtual);
                    yAtual += alturaLinha;
                    linhaAtual = new StringBuilder(palavra);
                }
            }

            if (linhaAtual.length() > 0) {
                g2d.drawString(linhaAtual.toString(), x, yAtual);
                yAtual += alturaLinha;
            }
            return yAtual;
        }

        private void imprimirTextoSemQuebra(Graphics2D g2d, int x, int y, String texto, int larguraColuna,
                FontMetrics fm) {
            if (texto == null || texto.isEmpty()) {
                return;
            }
            g2d.drawString(texto, x, y);
        }

    }
}