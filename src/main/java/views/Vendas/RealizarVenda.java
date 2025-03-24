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
import javax.swing.text.*;

import controllers.Cliente.ClienteController;
import dao.ItemVenda.ItemVendaDAO;
import main.ConexaoBD;
import models.Cliente.Cliente;
import models.ItemVenda.ItemVenda;
import models.Medicamento.Medicamento;
import models.Produto.Produto;
import views.BarrasSuperiores.PainelSuperior;
import views.Clientes.CadastrarCliente;

public class RealizarVenda extends JPanel {

    private final Connection conn;
    private Map<String, Component[]> itensMap = new HashMap<>();
    private int ordemItem = 1;

    private JLabel itemLabel, lblCodigoProduto, lblQuantidade, lblPrecoUnitario, lblDesconto, lblPrecoTotal;
    JLabel lblNomeCliente, lblCpfCliente, lblAtendente;
    private JTextField txtItem, txtCodigoProduto, txtQuantidade, txtPrecoUnitario, txtDesconto, txtPrecoTotal, txtTotal;
    private JPanel painelItem;
    private JPopupMenu popupMenu;
    private Timer searchTimer;
    private ResumoDaVenda painelDireito;
    private JButton btnRemoverItem, btnConfirmarVenda, btnCancelarVenda;

    private static final Color BACKGROUND_COLOR = new Color(240, 236, 236);
    private static final Color BORDER_COLOR = new Color(173, 216, 230);
    private static final Color INPUT_BG_COLOR = new Color(24, 39, 55);
    private static final Color INPUT_FG_COLOR = Color.WHITE;
    private static final Color BUTTON_CONFIRM_COLOR = new Color(0, 133, 0);
    private static final int ITEM_HEIGHT = 40;

    public RealizarVenda(Connection conn) {
        this.conn = conn;
        initializeComponents();
        setupLayout();
        addResizeListener();
        atualizarEstadoBotoes();
    }

    private void initializeComponents() {
        lblNomeCliente = new JLabel("Nome do Consumidor: Não Identificado");
        lblCpfCliente = new JLabel("CPF do Consumidor: Não Identificado");
        lblAtendente = new JLabel("Atendente: " + (PainelSuperior.getNomeFuncionarioAtual() != null
                ? PainelSuperior.getNomeFuncionarioAtual()
                : "Não identificado"));
    }

    public void atualizarAtendente(String nomeFuncionario) {
        lblAtendente.setText("Atendente: " + (nomeFuncionario != null ? nomeFuncionario : "Não identificado"));
    }

    public boolean hasItems() {
        return !itensMap.isEmpty();
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

        JPanel painelFooter = new JPanel(new GridBagLayout());
        GridBagConstraints gbcFooter = new GridBagConstraints();
        gbcFooter.fill = GridBagConstraints.BOTH;
        gbcFooter.gridy = 0;
        gbcFooter.weighty = 1.0;

        gbcFooter.gridx = 0;
        gbcFooter.weightx = 0.9;
        painelFooter.add(ladoEsquerdoFooter(), gbcFooter);

        gbcFooter.gridx = 1;
        gbcFooter.weightx = 0.1;
        painelFooter.add(ladoDireitoFooter(), gbcFooter);
        add(painelFooter, BorderLayout.SOUTH);
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
        txtItem.setFont(new Font("Arial", Font.PLAIN, 20));
        txtItem.setMinimumSize(new Dimension(150, 45));
        txtItem.setPreferredSize(new Dimension(150, 45));
        txtItem.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        txtItem.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        gbc.gridx = 0;
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

    private JPanel ladoEsquerdoFooter() {
        JPanel ladoEsquerdo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel botoesVenda = new JPanel();
        botoesVenda.setLayout(new BoxLayout(botoesVenda, BoxLayout.X_AXIS));
        botoesVenda.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton btnIdentificarCliente = new JButton("Identificar Cliente");
        btnIdentificarCliente.setFont(new Font("Arial", Font.BOLD, 18));
        btnIdentificarCliente.setBackground(INPUT_BG_COLOR);
        btnIdentificarCliente.setForeground(Color.WHITE);
        btnIdentificarCliente.setFocusPainted(false);
        btnIdentificarCliente.setMinimumSize(new Dimension(185, 45));
        btnIdentificarCliente.setPreferredSize(new Dimension(185, 45));
        btnIdentificarCliente.addActionListener(e -> {
            try {
                abrirDialogoIdentificacaoCliente();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao identificar cliente " + ex.getMessage(), "Erro",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
        botoesVenda.add(btnIdentificarCliente);
        botoesVenda.add(Box.createRigidArea(new Dimension(25, 0)));

        btnCancelarVenda = new JButton("Cancelar Venda");
        btnCancelarVenda.setFont(new Font("Arial", Font.BOLD, 18));
        btnCancelarVenda.setBackground(Color.RED);
        btnCancelarVenda.setForeground(Color.WHITE);
        btnCancelarVenda.setFocusPainted(false);
        btnCancelarVenda.setMinimumSize(new Dimension(185, 40));
        btnCancelarVenda.setPreferredSize(new Dimension(185, 40));
        btnCancelarVenda.setEnabled(false);
        btnCancelarVenda.addActionListener(e -> {
            // Limpar campos de entrada
            limparCampos();

            // Reiniciar variáveis
            ordemItem = 1;
            itensMap.clear();

            // Reiniciar informações do cliente
            lblNomeCliente.setText("Nome do Consumidor: Não Identificado");
            lblCpfCliente.setText("CPF do Consumidor: Não Identificado");

            // Recriar e reintegrar o painel direito
            painelDireito = createPainelDireito();
            JPanel painelMeio = (JPanel) getComponent(1);
            painelMeio.remove(1);
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 1;
            gbc.gridy = 0;
            gbc.weightx = 0.9;
            gbc.fill = GridBagConstraints.BOTH;
            painelMeio.add(painelDireito, gbc);

            // Limpar popup
            popupMenu.setVisible(false);
            popupMenu.removeAll();

            // Atualizar interface
            atualizarTotalFooter();
            atualizarEstadoBotoes();
            revalidate();
            repaint();
        });
        botoesVenda.add(btnCancelarVenda);
        botoesVenda.add(Box.createRigidArea(new Dimension(25, 0)));

        btnRemoverItem = new JButton("Remover Item");
        btnRemoverItem.setFont(new Font("Arial", Font.BOLD, 18));
        btnRemoverItem.setBackground(Color.ORANGE);
        btnRemoverItem.setForeground(Color.WHITE);
        btnRemoverItem.setFocusPainted(false);
        btnRemoverItem.setMinimumSize(new Dimension(185, 40));
        btnRemoverItem.setPreferredSize(new Dimension(185, 40));
        btnRemoverItem.setEnabled(false);
        btnRemoverItem.addActionListener(e -> abrirDialogoRemoverItem());
        botoesVenda.add(btnRemoverItem);
        botoesVenda.add(Box.createRigidArea(new Dimension(25, 0)));

        btnConfirmarVenda = new JButton("Confirmar Venda");
        btnConfirmarVenda.setFont(new Font("Arial", Font.BOLD, 18));
        btnConfirmarVenda.setBackground(BUTTON_CONFIRM_COLOR);
        btnConfirmarVenda.setForeground(Color.WHITE);
        btnConfirmarVenda.setFocusPainted(false);
        btnConfirmarVenda.setMinimumSize(new Dimension(185, 40));
        btnConfirmarVenda.setPreferredSize(new Dimension(185, 40));
        btnConfirmarVenda.setEnabled(false);
        btnConfirmarVenda.addActionListener(e -> confirmarVenda());
        botoesVenda.add(btnConfirmarVenda);

        ladoEsquerdo.add(botoesVenda);
        return ladoEsquerdo;
    }

    private JPanel ladoDireitoFooter() {
        JPanel ladoDireito = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        ladoDireito.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        JLabel lblTotal = new JLabel("Total: ");
        lblTotal.setFont(new Font("Arial", Font.BOLD, 18));
        lblTotal.setForeground(Color.BLACK);

        txtTotal = new JTextField("0,00", 7);
        txtTotal.setFont(new Font("Arial", Font.PLAIN, 20));
        txtTotal.setPreferredSize(new Dimension(70, 35));
        txtTotal.setMinimumSize(new Dimension(70, 35));
        txtTotal.setMaximumSize(new Dimension(70, 35));
        txtTotal.setBackground(INPUT_BG_COLOR);
        txtTotal.setForeground(Color.WHITE);
        txtTotal.setEditable(false);
        txtTotal.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));

        ladoDireito.add(lblTotal);
        ladoDireito.add(txtTotal);
        return ladoDireito;
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

    private void configurarEventosItemPanel() {
        txtItem.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String termo = txtItem.getText().trim();
                if (termo.isEmpty()) {
                    txtCodigoProduto.setText("");
                    txtPrecoUnitario.setText("");
                    txtQuantidade.setEnabled(false);
                    popupMenu.setVisible(false);
                    popupMenu.removeAll();
                } else {
                    searchTimer.restart();
                }
            }
        });

        txtItem.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                SwingUtilities.invokeLater(() -> {
                    if (!popupMenu.isShowing())
                        popupMenu.setVisible(false);
                });
            }

            @Override
            public void focusGained(FocusEvent e) {
                if (!txtItem.getText().trim().isEmpty() && popupMenu.getComponentCount() > 0) {
                    SwingUtilities.invokeLater(() -> popupMenu.show(txtItem, 0, txtItem.getHeight()));
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
        txtQuantidade.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                calcularPrecoTotal();
            }
        });

        txtDesconto.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                calcularPrecoTotal();
            }
        });

        txtQuantidade.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER)
                    confirmarItem();
            }
        });

        txtDesconto.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER)
                    confirmarItem();
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
                        popupMenu.setVisible(false);
                        return;
                    }

                    int itemCount = 0;
                    for (Object item : itens) {
                        if (itemCount >= 10)
                            break;

                        String textoExibicao;
                        String nomeBase;
                        int idItem;
                        BigDecimal precoUnitario;

                        ItemVenda itemVenda = new ItemVenda(); // Create ItemVenda object for discount calculation

                        if (item instanceof Produto) {
                            Produto p = (Produto) item;
                            textoExibicao = String.format("%s %s %s %s UN", p.getNome().toUpperCase(),
                                    p.getEmbalagem().toUpperCase(),
                                    p.getQntMedida().toUpperCase(), p.getQntEmbalagem());
                            nomeBase = p.getNome().toUpperCase();
                            idItem = p.getId();
                            precoUnitario = p.getValor();
                            itemVenda.setProduto(p);
                        } else if (item instanceof Medicamento) {
                            Medicamento m = (Medicamento) item;
                            textoExibicao = String.format("%s %s %s %s %s UN", m.getNome().toUpperCase(),
                                    m.getFormaFarmaceutica().toUpperCase(),
                                    m.getDosagem().toUpperCase(), m.getEmbalagem().toUpperCase(), m.getQntEmbalagem());
                            nomeBase = m.getNome().toUpperCase();
                            idItem = m.getId();
                            precoUnitario = m.getValorUnit();
                            itemVenda.setMedicamento(m);
                        } else {
                            continue;
                        }

                        // Set initial values for ItemVenda
                        itemVenda.setPrecoUnit(precoUnitario);
                        itemVenda.setQnt(1); // Default quantity of 1 for initial discount calculation
                        itemVenda.setDesconto(BigDecimal.ZERO); // Initialize discount

                        // Calculate automatic discount
                        try (Connection conn = ConexaoBD.getConnection()) {
                            ItemVendaDAO.calcularDescontoAutomatico(conn, itemVenda);
                        } catch (SQLException e) {
                            e.printStackTrace();
                            JOptionPane.showMessageDialog(null, "Erro ao calcular desconto: " + e.getMessage(), "Erro",
                                    JOptionPane.ERROR_MESSAGE);
                            continue;
                        }

                        BigDecimal descontoCalculado = itemVenda.getDesconto();

                        JMenuItem menuItem = new JMenuItem(textoExibicao);
                        menuItem.setBackground(new Color(24, 39, 55));
                        menuItem.setForeground(Color.WHITE);
                        menuItem.setOpaque(true);
                        menuItem.setFont(new Font("Arial", Font.PLAIN, 16));
                        menuItem.setPreferredSize(new Dimension(painelItem.getWidth(), ITEM_HEIGHT));
                        menuItem.addActionListener(e -> {
                            txtItem.setText(textoExibicao);
                            txtCodigoProduto.setText(String.valueOf(idItem));
                            txtPrecoUnitario.setText(String.format("%.2f", precoUnitario).replace(".", ","));
                            txtDesconto.setText(String.format("%.2f", descontoCalculado).replace(".", ",")); // Set
                                                                                                             // discount
                            txtQuantidade.setText("1"); // Default quantity
                            txtQuantidade.setEnabled(true);
                            txtQuantidade.requestFocusInWindow();
                            popupMenu.setVisible(false);
                            txtItem.putClientProperty("nomeBase", nomeBase);
                            calcularPrecoTotal(); // Recalculate total with initial discount
                        });

                        popupMenu.add(menuItem);
                        itemCount++;
                    }

                    int totalHeight = itemCount * ITEM_HEIGHT;
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
                return;
            }

            String quantidadeText = txtQuantidade.getText().replace(",", ".").trim();
            if (quantidadeText.isEmpty()) {
                txtPrecoTotal.setText("0,00");
                return;
            }
            int quantidade = Integer.parseInt(quantidadeText);

            String precoUnitarioText = txtPrecoUnitario.getText().replace(",", ".").trim();
            BigDecimal precoUnitario = new BigDecimal(precoUnitarioText);

            String descontoText = txtDesconto.getText().replace(",", ".").trim();
            BigDecimal desconto = new BigDecimal(descontoText);

            BigDecimal precoTotal = (precoUnitario.multiply(new BigDecimal(quantidade))).subtract(desconto);
            String precoTotalFormatado = precoTotal.setScale(2, RoundingMode.HALF_UP).toString().replace(".", ",");
            txtPrecoTotal.setText(precoTotalFormatado);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Por favor, insira valores válidos.", "Erro",
                    JOptionPane.ERROR_MESSAGE);
            txtPrecoTotal.setText("0,00");
        }
    }

    private void confirmarItem() {
        try {
            if (txtCodigoProduto.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor, selecione um item antes de informar a quantidade.",
                        "Erro", JOptionPane.ERROR_MESSAGE);
                txtItem.requestFocusInWindow();
                return;
            }

            String quantidadeText = txtQuantidade.getText().replace(",", ".").trim();
            if (quantidadeText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor, insira a quantidade.", "Erro",
                        JOptionPane.ERROR_MESSAGE);
                txtQuantidade.requestFocusInWindow();
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

            Object[] opcoes = { "Sim", "Não" };
            int resposta = JOptionPane.showOptionDialog(this,
                    "Confirmar item?\n" +
                            "Produto: " + txtItem.getText() + "\n" +
                            "Quantidade: " + quantidadeText + "\n" +
                            "Preço Total: " + precoTotalText,
                    "Confirmação de Item",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null, opcoes, opcoes[0]);

            if (resposta == JOptionPane.YES_OPTION) {
                painelDireito.adicionarItem(String.valueOf(ordemItem++), txtCodigoProduto.getText(), txtItem.getText(),
                        txtQuantidade.getText(), txtPrecoUnitario.getText(), txtPrecoTotal.getText(),
                        txtDesconto.getText());
                atualizarTotalFooter();
                limparCampos();
                popupMenu.setVisible(false);
                popupMenu.removeAll();
                atualizarEstadoBotoes();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Por favor, insira valores válidos.", "Erro",
                    JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao verificar estoque: " + e.getMessage(), "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void confirmarVenda() {
        Object[] options = { "Sim", "Não" };
        int resposta = JOptionPane.showOptionDialog(this,
                "Deseja finalizar a venda?\nVocê será direcionado para a tela de pagamento.",
                "Confirmar Venda",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, options, options[0]);

        if (resposta == 0) {
            try {
                abrirDialogoPagamentoVenda();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao confirmar venda: " + ex.getMessage(), "Erro",
                        JOptionPane.ERROR_MESSAGE);
            }
        } else if (resposta == 1) {
            JOptionPane.showMessageDialog(this, "Operação cancelada.", "Cancelamento", JOptionPane.INFORMATION_MESSAGE);
        }
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

    private void atualizarTotalFooter() {
        BigDecimal total = painelDireito.getTotalGeral();
        txtTotal.setText(total.setScale(2, RoundingMode.HALF_UP).toString().replace(".", ","));
    }

    private void atualizarEstadoBotoes() {
        boolean temItens = !painelDireito.itensMap.isEmpty();
        btnRemoverItem.setEnabled(temItens);
        btnConfirmarVenda.setEnabled(temItens);
        btnCancelarVenda.setEnabled(temItens);
    }

    private void atualizaPainelDireito() {
        painelDireito.revalidate();
        painelDireito.repaint();
    }

    private void abrirDialogoIdentificacaoCliente() {
        JDialog dialogo = new JDialog();
        dialogo.setTitle("Identificar Cliente");
        dialogo.setSize(300, 150);
        dialogo.setLayout(new GridBagLayout());
        dialogo.setModal(true);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblCpf = new JLabel("Digite o CPF:");
        lblCpf.setFont(new Font("Arial", Font.BOLD, 16));
        lblCpf.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        dialogo.add(lblCpf, gbc);

        JTextField txtCpf = new JTextField();
        txtCpf.setFont(new Font("Arial", Font.PLAIN, 18));
        txtCpf.setColumns(11);
        gbc.gridy = 1;
        dialogo.add(txtCpf, gbc);

        ((AbstractDocument) txtCpf.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                    throws BadLocationException {
                String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
                String newText = currentText.substring(0, offset) + text + currentText.substring(offset + length);
                if (newText.matches("\\d{0,11}"))
                    super.replace(fb, offset, length, text, attrs);
            }

            @Override
            public void insertString(DocumentFilter.FilterBypass fb, int offset, String text, AttributeSet attrs)
                    throws BadLocationException {
                replace(fb, offset, 0, text, attrs);
            }
        });

        JButton btnIdentificar = new JButton("Identificar");
        btnIdentificar.setFont(new Font("Arial", Font.BOLD, 16));
        btnIdentificar.setBackground(INPUT_BG_COLOR);
        btnIdentificar.setForeground(Color.WHITE);
        gbc.gridy = 2;
        dialogo.add(btnIdentificar, gbc);
        txtCpf.addActionListener(e -> btnIdentificar.doClick());

        btnIdentificar.addActionListener(e -> {
            String cpf = txtCpf.getText().trim();
            if (cpf.isEmpty()) {
                JOptionPane.showMessageDialog(dialogo, "Por favor, informe o CPF.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            cpf = cpf.replaceAll("[^0-9]", "");
            if (cpf.length() != 11) {
                JOptionPane.showMessageDialog(dialogo, "O CPF deve ter 11 dígitos.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (Connection conn = ConexaoBD.getConnection()) {
                Cliente cliente = ClienteController.buscarClientePorCpf(conn, cpf);
                if (cliente != null) {
                    dialogo.dispose();
                    String mensagem = "Cliente identificado com sucesso!\n" +
                            "Nome: " + cliente.getNome() + "\n" +
                            "CPF: " + cliente.getCpf() + "\n" +
                            "Telefone: " + cliente.getTelefone();
                    JOptionPane.showMessageDialog(dialogo, mensagem, "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    lblNomeCliente.setText("Nome do Cliente: " + cliente.getNome());
                    lblCpfCliente.setText("CPF do Cliente: " + cliente.getCpf());
                    atualizaPainelDireito();
                } else {
                    Object[] options = { "Sim", "Não" };
                    int opcao = JOptionPane.showOptionDialog(dialogo, "Cliente não cadastrado. Deseja cadastrar?",
                            "Cadastrar Cliente", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null,
                            options, options[0]);

                    if (opcao == 0) {
                        JDialog cadastroDialog = new JDialog(dialogo, "Cadastrar Cliente", true);
                        cadastroDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                        cadastroDialog.setSize(1200, 650);
                        cadastroDialog.setLocationRelativeTo(dialogo);
                        CadastrarCliente cadastrarCliente = new CadastrarCliente();
                        cadastroDialog.add(cadastrarCliente);
                        cadastroDialog.setVisible(true);
                        dialogo.setVisible(false);
                    } else {
                        JOptionPane.showMessageDialog(dialogo, "Operação cancelada.", "Aviso",
                                JOptionPane.INFORMATION_MESSAGE);
                        dialogo.setVisible(false);
                    }
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialogo, "Erro ao buscar cliente: " + ex.getMessage(), "Erro",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        dialogo.setLocationRelativeTo(null);
        dialogo.setVisible(true);
    }

    private void abrirDialogoRemoverItem() {
        JDialog dialogo = new JDialog();
        dialogo.setTitle("Remover Item");
        dialogo.setSize(350, 180);
        dialogo.setLayout(new GridBagLayout());
        dialogo.setModal(true);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.CENTER;

        JLabel lblOrdem = new JLabel("Digite o número do item (ordem):");
        lblOrdem.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        dialogo.add(lblOrdem, gbc);

        JTextField txtOrdem = new JTextField();
        txtOrdem.setFont(new Font("Arial", Font.PLAIN, 18));
        txtOrdem.setColumns(10);
        gbc.gridy = 1;
        dialogo.add(txtOrdem, gbc);

        JButton btnRemover = new JButton("Remover");
        btnRemover.setFont(new Font("Arial", Font.BOLD, 16));
        btnRemover.setBackground(INPUT_BG_COLOR);
        btnRemover.setForeground(Color.WHITE);
        gbc.gridy = 2;
        dialogo.add(btnRemover, gbc);

        btnRemover.addActionListener(e -> {
            String ordem = txtOrdem.getText().trim();
            if (ordem.isEmpty()) {
                JOptionPane.showMessageDialog(dialogo, "Por favor, informe o número do item.", "Erro",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                String[] dadosItem = painelDireito.getDadosItemPorOrdem(ordem);
                if (dadosItem == null) {
                    JOptionPane.showMessageDialog(dialogo, "Item com ordem '" + ordem + "' não encontrado.", "Erro",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String mensagemConfirmacao = "Deseja remover o item?\n" +
                        "Ordem: " + dadosItem[0] + "\n" +
                        "Produto: " + dadosItem[2] + "\n" +
                        "Quantidade: " + dadosItem[3] + "\n" +
                        "Preço Total: " + dadosItem[6];

                Object[] opcoes = { "Sim", "Não" };
                int resposta = JOptionPane.showOptionDialog(dialogo, mensagemConfirmacao, "Confirmação de Remoção",
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, opcoes, opcoes[0]);

                if (resposta == JOptionPane.YES_OPTION) {
                    int quantidadeRemovida = Integer.parseInt(dadosItem[3].replace(",", ".").trim());
                    String nomeCompleto = dadosItem[2].trim();
                    String nomeBase = nomeCompleto.split(" ")[0]; // Apenas o nome base
                    ItemVenda itemVenda = new ItemVenda();

                    ItemVendaDAO.verificarTipoEEstoque(conn, itemVenda, quantidadeRemovida, true, nomeBase);

                    painelDireito.removerItem(ordem);
                    atualizarTotalFooter();
                    atualizarEstadoBotoes();
                    dialogo.dispose();
                    JOptionPane.showMessageDialog(this, "Item removido com sucesso!", "Sucesso",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialogo, "Erro ao remover item: " + ex.getMessage(), "Erro",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        txtOrdem.addActionListener(e -> btnRemover.doClick());
        dialogo.setLocationRelativeTo(this);
        dialogo.setVisible(true);
    }

    private void abrirDialogoPagamentoVenda() {
        if (painelDireito.itensMap.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nenhum item na venda para processar o pagamento.", "Erro",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        JDialog dialogoPagamento = new JDialog();
        dialogoPagamento.setTitle("Pagamento da Venda");
        dialogoPagamento.setSize(900, 650);
        dialogoPagamento.setModal(true);
        dialogoPagamento.setLocationRelativeTo(this);

        PagamentoVenda painelPagamento = new PagamentoVenda(painelDireito.getTotalGeral(), painelDireito, this); // Passar
                                                                                                                 // this
        dialogoPagamento.add(painelPagamento);
        dialogoPagamento.setVisible(true);
    }

    // Esse aqui é um método público para após realizar pagamento limpar tudo da
    // venda antiga
    public void reiniciarVenda() {
        // Limpar campos de entrada
        limparCampos();

        // Reiniciar variáveis
        ordemItem = 1;
        itensMap.clear();

        // Reiniciar informações do cliente
        lblNomeCliente.setText("Nome do Consumidor: Não Identificado");
        lblCpfCliente.setText("CPF do Consumidor: Não Identificado");

        // Recriar e reintegrar o painel direito (ResumoDaVenda)
        painelDireito = createPainelDireito();
        JPanel painelMeio = (JPanel) getComponent(1);
        painelMeio.remove(1);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.9;
        gbc.fill = GridBagConstraints.BOTH;
        painelMeio.add(painelDireito, gbc);

        // Limpar popup
        popupMenu.setVisible(false);
        popupMenu.removeAll();

        // Atualizar interface
        atualizarTotalFooter();
        atualizarEstadoBotoes();
        revalidate();
        repaint();
    }
}