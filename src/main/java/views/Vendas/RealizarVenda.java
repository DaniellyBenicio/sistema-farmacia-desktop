package views.Vendas;

import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import dao.ItemVenda.ItemVendaDAO;
import main.ConexaoBD;
import models.ItemVenda.ItemVenda;
import models.Medicamento.Medicamento;
import models.Produto.Produto;
import views.BarrasSuperiores.PainelSuperior;

public class RealizarVenda extends JPanel {

    private static final Color BACKGROUND_COLOR = new Color(240, 236, 236);
    private static final Color BORDER_COLOR = new Color(173, 216, 230);
    private static final Color INPUT_BG_COLOR = new Color(24, 39, 55);
    private static final Color INPUT_FG_COLOR = Color.WHITE;
    private static final Color BUTTON_CONFIRM_COLOR = new Color(0, 133, 0);
    private static final int ITEM_HEIGHT = 40;

    private final Connection conn;
    private Map<String, Component[]> itensMap = new HashMap<>();
    private int ordemItem = 1;

    private JLabel itemLabel, lblCodigoProduto, lblQuantidade, lblPrecoUnitario, lblDesconto, lblPrecoTotal;
    private JLabel lblNomeCliente, lblCpfCliente, lblAtendente;
    private JTextField txtItem, txtCodigoProduto, txtQuantidade, txtPrecoUnitario, txtDesconto, txtPrecoTotal;
    private JPanel painelItem;
    private JPopupMenu popupMenu;
    private Timer searchTimer;
    private ResumoDaVenda painelDireito;
    private PainelInferiorVenda painelInferior;

    public RealizarVenda(Connection conn) {
        this.conn = conn;
        initializeComponents();
        setupLayout();
        addResizeListener();
    }

    private void initializeComponents() {
        lblNomeCliente = new JLabel("Nome do Consumidor: Não Identificado");
        lblCpfCliente = new JLabel("CPF do Consumidor: Não Identificado");
        lblAtendente = new JLabel("Atendente: " + (PainelSuperior.getNomeFuncionarioAtual() != null
                ? PainelSuperior.getNomeFuncionarioAtual()
                : "Não identificado"));
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(10, 50, 0, 50));

        JPanel painelTopo = new JPanel(new BorderLayout());
        painelTopo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        painelTopo.add(createItemPanel(), BorderLayout.CENTER);
        add(painelTopo, BorderLayout.NORTH);

        JPanel painelMeio = new JPanel(new GridBagLayout());
        GridBagConstraints gbcMeio = new GridBagConstraints();
        gbcMeio.fill = GridBagConstraints.BOTH;
        gbcMeio.weighty = 1.0;

        gbcMeio.gridx = 0;
        gbcMeio.gridy = 0;
        gbcMeio.weightx = 0.1;
        JPanel painelEsquerdo = createPainelEsquerdo();
        painelEsquerdo.setPreferredSize(new Dimension(400, 500));
        painelMeio.add(painelEsquerdo, gbcMeio);

        gbcMeio.gridx = 1;
        gbcMeio.weightx = 0.9;
        painelDireito = createPainelDireito();
        painelDireito.setPreferredSize(new Dimension(600, 500));
        painelMeio.add(painelDireito, gbcMeio);
        add(painelMeio, BorderLayout.CENTER);

        painelInferior = new PainelInferiorVenda(conn, this, painelDireito);
        add(painelInferior, BorderLayout.SOUTH);
    }

    private JPanel createItemPanel() {
        painelItem = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        itemLabel = new JLabel("Produto:");
        itemLabel.setFont(new Font("Arial", Font.BOLD, 18));
        itemLabel.setForeground(Color.BLACK);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        painelItem.add(itemLabel, gbc);

        txtItem = new JTextField();
        txtItem.setBackground(INPUT_BG_COLOR);
        txtItem.setForeground(INPUT_FG_COLOR);
        txtItem.setOpaque(true);
        txtItem.setCaretColor(Color.WHITE);
        txtItem.setFont(new Font("Arial", Font.PLAIN, 20));
        txtItem.setMinimumSize(new Dimension(150, 45));
        txtItem.setPreferredSize(new Dimension(150, 45));
        txtItem.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        txtItem.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));

        String placeholder = "Buscar";
        txtItem.setText(placeholder);
        txtItem.setForeground(Color.GRAY);

        txtItem.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (txtItem.getText().equals(placeholder)) {
                    txtItem.setText("");
                    txtItem.setForeground(INPUT_FG_COLOR);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (txtItem.getText().trim().isEmpty()) {
                    txtItem.setText(placeholder);
                    txtItem.setForeground(Color.GRAY);
                }
            }
        });

        txtItem.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (txtItem.getText().equals("Buscar")) {
                    txtItem.setText("");
                    txtItem.setForeground(INPUT_FG_COLOR);
                }
            }
        });

        gbc.gridy = 1;
        gbc.weightx = 1.0;
        painelItem.add(txtItem, gbc);

        popupMenu = new JPopupMenu();
        searchTimer = new Timer(300, e -> atualizarResultadosBusca(txtItem.getText().trim()));
        searchTimer.setRepeats(false);

        configurarEventosItemPanel();
        return painelItem;
    }

    private JPanel createPainelEsquerdo() {
        JPanel painelEsquerdo = new JPanel();
        painelEsquerdo.setLayout(new BoxLayout(painelEsquerdo, BoxLayout.Y_AXIS));
        painelEsquerdo.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

        JPanel painelInternoEsquerdo = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 8, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1.0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;

        adicionarCamposPainelEsquerdo(painelInternoEsquerdo, gbc);
        configurarEventosPainelEsquerdo();
        painelEsquerdo.add(painelInternoEsquerdo);
        return painelEsquerdo;
    }

    private void adicionarCamposPainelEsquerdo(JPanel painel, GridBagConstraints gbc) {
        lblCodigoProduto = new JLabel("Código do Produto");
        lblCodigoProduto.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        painel.add(lblCodigoProduto, gbc);

        txtCodigoProduto = createTextFieldOutrosCampos();
        txtCodigoProduto.setEditable(false);
        txtCodigoProduto.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        gbc.gridy = 1;
        painel.add(txtCodigoProduto, gbc);

        lblQuantidade = new JLabel("Quantidade");
        lblQuantidade.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridy = 2;
        painel.add(lblQuantidade, gbc);

        txtQuantidade = createTextFieldOutrosCampos();
        txtQuantidade.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        txtQuantidade.setEnabled(false);
        gbc.gridy = 3;
        painel.add(txtQuantidade, gbc);

        lblPrecoUnitario = new JLabel("Preço Unitário");
        lblPrecoUnitario.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridy = 4;
        painel.add(lblPrecoUnitario, gbc);

        txtPrecoUnitario = createTextFieldOutrosCampos();
        txtPrecoUnitario.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        txtPrecoUnitario.setText("0,00");
        txtPrecoUnitario.setCaretPosition(0);
        txtPrecoUnitario.setEditable(false);
        gbc.gridy = 5;
        painel.add(txtPrecoUnitario, gbc);

        lblDesconto = new JLabel("Desconto");
        lblDesconto.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridy = 6;
        painel.add(lblDesconto, gbc);

        txtDesconto = createTextFieldOutrosCampos();
        txtDesconto.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        txtDesconto.setText("0,00");
        txtDesconto.setCaretPosition(0);
        txtDesconto.setEditable(false);
        gbc.gridy = 7;
        painel.add(txtDesconto, gbc);

        lblPrecoTotal = new JLabel("Preço Total");
        lblPrecoTotal.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridy = 8;
        painel.add(lblPrecoTotal, gbc);

        txtPrecoTotal = createTextFieldOutrosCampos();
        txtPrecoTotal.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        txtPrecoTotal.setText("0,00");
        txtPrecoTotal.setCaretPosition(0);
        txtPrecoTotal.setEditable(false);
        gbc.gridy = 9;
        painel.add(txtPrecoTotal, gbc);

        JButton btnAdicionarItem = new JButton("Adicionar Item");
        btnAdicionarItem.setFont(new Font("Arial", Font.BOLD, 16));
        btnAdicionarItem.setBackground(BUTTON_CONFIRM_COLOR);
        btnAdicionarItem.setForeground(Color.WHITE);
        btnAdicionarItem.setFocusPainted(false);
        btnAdicionarItem.addActionListener(e -> confirmarItem());
        gbc.insets = new Insets(10, 5, 5, 5);
        gbc.gridy = 10;
        painel.add(btnAdicionarItem, gbc);
    }

    public ResumoDaVenda createPainelDireito() {
        ResumoDaVenda resumo = new ResumoDaVenda(lblNomeCliente, lblCpfCliente, lblAtendente);
        resumo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));
        return resumo;
    }

    private JTextField createTextFieldOutrosCampos() {
        JTextField textField = new JTextField();
        textField.setBackground(Color.WHITE);
        textField.setForeground(Color.BLACK);
        textField.setOpaque(true);
        textField.setFont(new Font("Arial", Font.PLAIN, 20));
        textField.setMinimumSize(new Dimension(400, 35));
        textField.setPreferredSize(new Dimension(400, 40));
        textField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        textField.setHorizontalAlignment(SwingConstants.LEFT);
        return textField;
    }

    private void addResizeListener() {
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                revalidate();
                repaint();
            }
        });
    }

    private void configurarEventosItemPanel() {
        txtItem.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String termo = txtItem.getText().trim();
                if (termo.isEmpty()) {
                    limparCampos();
                    popupMenu.setVisible(false);
                    popupMenu.removeAll();
                    txtItem.putClientProperty("nomeBase", null);
                } else {
                    searchTimer.restart();
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DOWN && popupMenu.isShowing()) {
                    popupMenu.getComponent(0).requestFocus();
                }
            }
        });

        txtItem.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                SwingUtilities.invokeLater(() -> {
                    if (!popupMenu.isShowing() && !txtItem.hasFocus()) {
                        popupMenu.setVisible(false);
                    }
                });
            }

            @Override
            public void focusGained(FocusEvent e) {
                String termo = txtItem.getText().trim();
                if (!termo.isEmpty()) {
                    searchTimer.restart();
                }
            }
        });

        painelItem.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                popupMenu.setPreferredSize(new Dimension(painelItem.getWidth(), 200));
            }
        });
    }

    private void configurarEventosPainelEsquerdo() {
        txtQuantidade.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                calcularPrecoTotal();
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    confirmarItem();
                }
            }
        });

        txtQuantidade.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                calcularPrecoTotal();
                confirmarItem();
            }
        });

        txtDesconto.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                calcularPrecoTotal();
            }
        });

        txtDesconto.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    confirmarItem();
                }
            }
        });
    }

    private void atualizarResultadosBusca(String termo) {
        popupMenu.removeAll();

        SwingWorker<List<Object>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Object> doInBackground() throws Exception {
                try (Connection conn = ConexaoBD.getConnection()) {
                    return ItemVendaDAO.buscarTodosItensDisponiveis(conn, termo);
                }
            }

            @Override
            protected void done() {
                try {
                    List<Object> itens = get();
                    if (itens == null || itens.isEmpty()) {
                        JMenuItem menuItem = new JMenuItem("Produto indisponível");
                        menuItem.setEnabled(false);
                        popupMenu.add(menuItem);
                    } else {
                        int itemCount = 0;
                        for (Object item : itens) {
                            if (itemCount >= 10)
                                break;

                            String textoExibicao;
                            String nomeBase;
                            int idItem;
                            BigDecimal precoUnitario;

                            ItemVenda itemVenda = new ItemVenda();

                            if (item instanceof Produto) {
                                Produto p = (Produto) item;
                                textoExibicao = String.format("%s %s %s %s UN", p.getNome().toUpperCase(),
                                        p.getEmbalagem().toUpperCase(), p.getQntMedida().toUpperCase(),
                                        p.getQntEmbalagem());
                                nomeBase = p.getNome().toUpperCase();
                                idItem = p.getId();
                                precoUnitario = p.getValor();
                                itemVenda.setProduto(p);
                            } else if (item instanceof Medicamento) {
                                Medicamento m = (Medicamento) item;
                                textoExibicao = String.format("%s %s %s %s %s UN", m.getNome().toUpperCase(),
                                        m.getFormaFarmaceutica().toUpperCase(), m.getDosagem().toUpperCase(),
                                        m.getEmbalagem().toUpperCase(), m.getQntEmbalagem());
                                nomeBase = m.getNome().toUpperCase();
                                idItem = m.getId();
                                precoUnitario = m.getValorUnit();
                                itemVenda.setMedicamento(m);
                            } else {
                                continue;
                            }

                            itemVenda.setPrecoUnit(precoUnitario);
                            itemVenda.setQnt(1);
                            itemVenda.setDesconto(BigDecimal.ZERO);

                            try (Connection conn = ConexaoBD.getConnection()) {
                                ItemVendaDAO.calcularDescontoAutomatico(conn, itemVenda);
                            } catch (SQLException e) {
                                e.printStackTrace();
                                JOptionPane.showMessageDialog(null, "Erro ao calcular desconto: " + e.getMessage(),
                                        "Erro",
                                        JOptionPane.ERROR_MESSAGE);
                                continue;
                            }

                            BigDecimal descontoCalculado = itemVenda.getDesconto();

                            JMenuItem menuItem = new JMenuItem(textoExibicao);
                            menuItem.setBackground(INPUT_BG_COLOR);
                            menuItem.setForeground(Color.WHITE);
                            menuItem.setOpaque(true);
                            menuItem.setFont(new Font("Arial", Font.PLAIN, 16));
                            menuItem.setPreferredSize(new Dimension(painelItem.getWidth(), ITEM_HEIGHT));
                            menuItem.addActionListener(e -> {
                                txtItem.setText(textoExibicao);
                                txtCodigoProduto.setText(String.valueOf(idItem));
                                txtPrecoUnitario.setText(String.format("%.2f", precoUnitario).replace(".", ","));
                                txtDesconto.setText(String.format("%.2f", descontoCalculado).replace(".", ","));
                                txtQuantidade.setText("");
                                txtQuantidade.setEnabled(true);
                                txtItem.putClientProperty("nomeBase", nomeBase);
                                txtItem.putClientProperty("descontoUnitario", descontoCalculado);
                                calcularPrecoTotal();
                                popupMenu.setVisible(false);
                                txtItem.requestFocusInWindow();
                                txtItem.selectAll();
                            });

                            popupMenu.add(menuItem);
                            itemCount++;
                        }
                    }

                    int totalHeight = popupMenu.getComponentCount() * ITEM_HEIGHT;
                    if (totalHeight < ITEM_HEIGHT)
                        totalHeight = ITEM_HEIGHT;

                    popupMenu.setPreferredSize(new Dimension(painelItem.getWidth(), totalHeight));
                    SwingUtilities.invokeLater(() -> {
                        popupMenu.show(txtItem, 0, txtItem.getHeight());
                        txtItem.requestFocusInWindow();
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Erro ao buscar itens: " + e.getMessage(), "Erro",
                            JOptionPane.ERROR_MESSAGE);
                    popupMenu.setVisible(false);
                }
            }
        };
        worker.execute();
    }

    private void calcularPrecoTotal() {
        try {
            if (txtCodigoProduto.getText().trim().isEmpty()) {
                txtPrecoTotal.setText("0,00");
                txtDesconto.setText("0,00");
                return;
            }

            String quantidadeText = txtQuantidade.getText().replace(",", ".").trim();
            if (quantidadeText.isEmpty()) {
                txtPrecoTotal.setText("0,00");
                return;
            }

            int quantidade = Integer.parseInt(quantidadeText);
            if (quantidade <= 0) {
                txtQuantidade.setText("1");
                calcularPrecoTotal();
                return;
            }

            String precoUnitarioText = txtPrecoUnitario.getText().replace(",", ".").trim();
            BigDecimal precoUnitario = new BigDecimal(precoUnitarioText);
            if (precoUnitario.compareTo(BigDecimal.ZERO) < 0) {
                txtPrecoUnitario.setText("0,00");
                calcularPrecoTotal();
                return;
            }

            BigDecimal subtotal = precoUnitario.multiply(new BigDecimal(quantidade));
            BigDecimal descontoTotal = BigDecimal.ZERO;

            BigDecimal descontoUnitario;
            Object descontoUnitarioProp = txtItem.getClientProperty("descontoUnitario");
            if (descontoUnitarioProp instanceof BigDecimal) {
                descontoUnitario = (BigDecimal) descontoUnitarioProp;
            } else {
                String descontoText = txtDesconto.getText().replace(",", ".").trim();
                descontoUnitario = descontoText.isEmpty() ? BigDecimal.ZERO : new BigDecimal(descontoText);
            }

            descontoTotal = descontoUnitario.multiply(new BigDecimal(quantidade));

            if (descontoTotal.compareTo(BigDecimal.ZERO) < 0) {
                descontoTotal = BigDecimal.ZERO;
                txtDesconto.setText("0,00");
            } else if (descontoTotal.compareTo(subtotal) > 0) {
                descontoTotal = subtotal;
                txtDesconto.setText(descontoTotal.setScale(2, RoundingMode.HALF_UP).toString().replace(".", ","));
            } else {
                txtDesconto.setText(descontoTotal.setScale(2, RoundingMode.HALF_UP).toString().replace(".", ","));
            }

            BigDecimal precoTotal = subtotal.subtract(descontoTotal);
            txtPrecoTotal.setText(precoTotal.setScale(2, RoundingMode.HALF_UP).toString().replace(".", ","));
        } catch (NumberFormatException e) {
            txtPrecoTotal.setText("0,00");
        }
    }

    private void confirmarItem() {
        try {
            if (txtCodigoProduto.getText().trim().isEmpty()) {
                return;
            }

            String quantidadeText = txtQuantidade.getText().replace(",", ".").trim();
            if (quantidadeText.isEmpty()) {
                return;
            }

            int quantidade = Integer.parseInt(quantidadeText);
            String precoTotalText = txtPrecoTotal.getText().replace(",", ".").trim();
            if (precoTotalText.equals("0,00") || precoTotalText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Preço total inválido. Verifique os valores.", "Erro",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            String nomeBase = (String) txtItem.getClientProperty("nomeBase");
            if (nomeBase == null) {
                JOptionPane.showMessageDialog(this, "Erro: Nome base do item não encontrado.", "Erro",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            ItemVenda itemVenda = new ItemVenda();
            boolean estoqueSuficiente = ItemVendaDAO.verificarTipoEEstoque(conn, itemVenda, quantidade, false,
                    nomeBase);

            if (!estoqueSuficiente) {
                String tipoItem = (itemVenda.getProduto() != null) ? "produto"
                        : (itemVenda.getMedicamento() != null) ? "medicamento" : "item";
                if (itemVenda.getMedicamento() != null && itemVenda.getMedicamento().getTipoReceita() != null) {
                    tipoItem += " (" + itemVenda.getMedicamento().getTipoReceita().name().toLowerCase() + ")";
                }
                JOptionPane.showMessageDialog(this,
                        "Quantidade solicitada (" + quantidade + ") excede o estoque disponível para o " + tipoItem
                                + ".",
                        "Erro de Estoque", JOptionPane.ERROR_MESSAGE);
                txtQuantidade.requestFocusInWindow();
                return;
            }

            painelDireito.adicionarItem(String.valueOf(ordemItem++), txtCodigoProduto.getText(), txtItem.getText(),
                    txtQuantidade.getText(), txtPrecoUnitario.getText(), txtPrecoTotal.getText(),
                    txtDesconto.getText());
            painelInferior.atualizarTotalFooter();
            limparCampos();
            popupMenu.setVisible(false);
            popupMenu.removeAll();
            painelInferior.atualizarEstadoBotoes();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Por favor, insira valores válidos.", "Erro",
                    JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao verificar estoque: " + e.getMessage(), "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void atualizarCliente(String nome, String cpf) {
        lblNomeCliente.setText("Nome do Cliente: " + nome);
        lblCpfCliente.setText("CPF do Cliente: " + cpf);
        atualizaPainelDireito();
    }

    public void atualizarAtendente(String nomeFuncionario) {
        lblAtendente.setText("Atendente: " + (nomeFuncionario != null ? nomeFuncionario : "Não identificado"));
    }

    public boolean hasItems() {
        return !itensMap.isEmpty();
    }

    public void reiniciarVenda() {
        limparCampos();
        ordemItem = 1;
        itensMap.clear();
        lblNomeCliente.setText("Nome do Consumidor: Não Identificado");
        lblCpfCliente.setText("CPF do Consumidor: Não Identificado");

        painelDireito.itensMap.clear();
        painelDireito = createPainelDireito();
        JPanel painelMeio = (JPanel) getComponent(1);
        painelMeio.remove(1);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.9;
        gbc.fill = GridBagConstraints.BOTH;
        painelMeio.add(painelDireito, gbc);

        painelInferior.setPainelDireito(painelDireito);

        popupMenu.setVisible(false);
        popupMenu.removeAll();

        painelInferior.atualizarTotalFooter();
        painelInferior.atualizarEstadoBotoes();

        revalidate();
        repaint();
    }

    private void limparCampos() {
        txtItem.setText("");
        txtCodigoProduto.setText("");
        txtQuantidade.setText("");
        txtQuantidade.setEnabled(false);
        txtPrecoUnitario.setText("0,00");
        txtDesconto.setText("0,00");
        txtPrecoTotal.setText("0,00");
        txtItem.requestFocusInWindow();
    }

    private void atualizaPainelDireito() {
        painelDireito.revalidate();
        painelDireito.repaint();
    }
}