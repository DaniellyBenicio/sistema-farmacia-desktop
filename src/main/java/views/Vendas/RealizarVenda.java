package views.Vendas;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.sql.Connection;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

import controllers.Cliente.ClienteController;
import main.ConexaoBD;
import models.Cliente.Cliente;
import views.Clientes.CadastrarCliente;

public class RealizarVenda extends JPanel {

    private final Connection conn;

    private JLabel itemLabel;
    private JLabel lblCodigoProduto;
    private JLabel lblQuantidade;
    private JLabel lblPrecoUnitario;
    private JLabel lblDesconto;
    private JLabel lblPrecoTotal;
    private JLabel lblNomeCliente;
    private JLabel lblCpfCliente;

    private JTextField txtItem;
    private JTextField txtCodigoProduto;
    private JTextField txtQuantidade;
    private JTextField txtPrecoUnitario;
    private JTextField txtDesconto;
    private JTextField txtPrecoTotal;

    private JPanel painelDireito;

    private static final Color BACKGROUND_COLOR = new Color(240, 236, 236);
    private static final Color BORDER_COLOR = new Color(173, 216, 230);
    private static final Color INPUT_BG_COLOR = new Color(24, 39, 55);
    private static final Color INPUT_FG_COLOR = Color.WHITE;
    private static final Color BUTTON_CONFIRM_COLOR = new Color(0, 133, 0);

    public RealizarVenda(Connection conn) {
        this.conn = conn;

        initializeComponents();
        setupLayout();
        addResizeListener();
    }

    private void initializeComponents() {
        lblNomeCliente = new JLabel("Nome do Consumidor: Não Identificado");
        lblCpfCliente = new JLabel("CPF do Consumidor: Não Identificado");
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
        painelMeio.add(createPainelEsquerdo(), gbcMeio);

        gbcMeio.gridx = 1;
        gbcMeio.weightx = 0.9;
        painelDireito = createPainelDireito();
        painelMeio.add(painelDireito, gbcMeio);

        JScrollPane scrollPane = new JScrollPane(painelMeio);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);

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
        JPanel painelItem = new JPanel(new GridBagLayout());
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

        return painelItem;
    }

    private JPanel createPainelEsquerdo() {
        JPanel painelEsquerdo = new JPanel();
        painelEsquerdo.setLayout(new BoxLayout(painelEsquerdo, BoxLayout.Y_AXIS));
        painelEsquerdo.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

        JPanel painelInternoEsquerdo = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 0, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1.0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;

        lblCodigoProduto = new JLabel("Código do Produto");
        lblCodigoProduto.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        painelInternoEsquerdo.add(lblCodigoProduto, gbc);

        txtCodigoProduto = createTextFieldOutrosCampos();
        txtCodigoProduto.setEditable(false);
        txtCodigoProduto.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        gbc.gridx = 0;
        gbc.gridy = 1;
        painelInternoEsquerdo.add(txtCodigoProduto, gbc);

        lblQuantidade = new JLabel("Quantidade");
        lblQuantidade.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 2;
        painelInternoEsquerdo.add(lblQuantidade, gbc);

        txtQuantidade = createTextFieldOutrosCampos();
        txtQuantidade.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        gbc.gridx = 0;
        gbc.gridy = 3;
        painelInternoEsquerdo.add(txtQuantidade, gbc);

        lblPrecoUnitario = new JLabel("Preço Unitário");
        lblPrecoUnitario.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 4;
        painelInternoEsquerdo.add(lblPrecoUnitario, gbc);

        txtPrecoUnitario = createTextFieldOutrosCampos();
        txtPrecoUnitario.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        txtPrecoUnitario.setText("0,00");
        txtPrecoUnitario.setCaretPosition(0);
        txtPrecoUnitario.setEditable(false);
        gbc.gridx = 0;
        gbc.gridy = 5;
        painelInternoEsquerdo.add(txtPrecoUnitario, gbc);

        lblDesconto = new JLabel("Desconto");
        lblDesconto.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 6;
        painelInternoEsquerdo.add(lblDesconto, gbc);

        txtDesconto = createTextFieldOutrosCampos();
        txtDesconto.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        txtDesconto.setText("0,00");
        txtDesconto.setCaretPosition(0);
        gbc.gridx = 0;
        gbc.gridy = 7;
        painelInternoEsquerdo.add(txtDesconto, gbc);

        lblPrecoTotal = new JLabel("Preço Total");
        lblPrecoTotal.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 8;
        painelInternoEsquerdo.add(lblPrecoTotal, gbc);

        txtPrecoTotal = createTextFieldOutrosCampos();
        txtPrecoTotal.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        txtPrecoTotal.setText("0,00");
        txtPrecoTotal.setCaretPosition(0);
        txtPrecoTotal.setEditable(false);
        gbc.gridx = 0;
        gbc.gridy = 9;
        painelInternoEsquerdo.add(txtPrecoTotal, gbc);

        gbc.weighty = 1.0;
        gbc.gridy = 10;
        painelInternoEsquerdo.add(Box.createVerticalGlue(), gbc);

        painelEsquerdo.add(painelInternoEsquerdo);
        return painelEsquerdo;
    }

    private JPanel createPainelDireito() {
        return new ResumoDaVenda(lblNomeCliente, lblCpfCliente);
    }

    private JTextField createTextFieldOutrosCampos() {
        JTextField textField = new JTextField();
        textField.setBackground(Color.WHITE);
        textField.setForeground(Color.BLACK);
        textField.setOpaque(true);
        textField.setFont(new Font("Arial", Font.PLAIN, 20));
        textField.setMinimumSize(new Dimension(450, 45));
        textField.setPreferredSize(new Dimension(450, 45));
        textField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        textField.setHorizontalAlignment(SwingConstants.LEFT);
        return textField;
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

        JButton btnCancelarVenda = new JButton("Cancelar Venda");
        btnCancelarVenda.setFont(new Font("Arial", Font.BOLD, 18));
        btnCancelarVenda.setBackground(Color.RED);
        btnCancelarVenda.setForeground(Color.WHITE);
        btnCancelarVenda.setFocusPainted(false);
        btnCancelarVenda.setMinimumSize(new Dimension(185, 40));
        btnCancelarVenda.setPreferredSize(new Dimension(185, 40));
        botoesVenda.add(btnCancelarVenda);
        botoesVenda.add(Box.createRigidArea(new Dimension(25, 0)));

        JButton btnConfirmarVenda = new JButton("Confirmar Venda");
        btnConfirmarVenda.setFont(new Font("Arial", Font.BOLD, 18));
        btnConfirmarVenda.setBackground(BUTTON_CONFIRM_COLOR);
        btnConfirmarVenda.setForeground(Color.WHITE);
        btnConfirmarVenda.setFocusPainted(false);
        btnConfirmarVenda.setMinimumSize(new Dimension(185, 40));
        btnConfirmarVenda.setPreferredSize(new Dimension(185, 40));
        btnConfirmarVenda.addActionListener(e -> {
            Object[] options = { "Sim", "Não" };
            int resposta = JOptionPane.showOptionDialog(this,
                    "Deseja finalizar a venda?\nVocê será direcionado para a tela de pagamento.",
                    "Confirmar Venda",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]);

            if (resposta == 0) {
                try {
                    abrirDialogoPagamentoVenda();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Erro ao confirmar venda: " + ex.getMessage(), "Erro",
                            JOptionPane.ERROR_MESSAGE);
                }
            } else if (resposta == 1) {
                JOptionPane.showMessageDialog(this, "Operação cancelada.", "Cancelamento",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });
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

        JTextField txtTotal = new JTextField("0,00", 7);
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
        gbc.gridx = 0;
        gbc.gridy = 1;
        dialogo.add(txtCpf, gbc);

        ((AbstractDocument) txtCpf.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                    throws BadLocationException {
                String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
                String newText = currentText.substring(0, offset) + text + currentText.substring(offset + length);

                if (newText.matches("\\d{0,11}")) {
                    super.replace(fb, offset, length, text, attrs);
                }
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
        gbc.gridx = 0;
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
                    int opcao = JOptionPane.showOptionDialog(dialogo,
                            "Cliente não cadastrado. Deseja cadastrar?",
                            "Cadastrar Cliente",
                            JOptionPane.DEFAULT_OPTION,
                            JOptionPane.INFORMATION_MESSAGE,
                            null, options, options[0]);

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

    private void abrirDialogoPagamentoVenda() {
        JDialog dialogoPagamento = new JDialog();
        dialogoPagamento.setTitle("Pagamento da Venda");
        dialogoPagamento.setSize(900, 650);
        dialogoPagamento.setModal(true);
        dialogoPagamento.setLocationRelativeTo(this);

        PagamentoVenda painelPagamento = new PagamentoVenda();
        dialogoPagamento.add(painelPagamento);
        dialogoPagamento.setVisible(true);
    }

    private void atualizaPainelDireito() {
        painelDireito.revalidate();
        painelDireito.repaint();
    }
}