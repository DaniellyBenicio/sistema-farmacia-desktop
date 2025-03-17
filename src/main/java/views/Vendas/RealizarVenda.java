package views.Vendas;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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

    private Connection conn;
    private JLabel itemLabel, lblCodigoProduto, lblQuantidade, lblPrecoUnitario, lblDesconto, lblPrecoTotal,
            lblNomeCliente, lblCpfCliente;
    private JTextField txtItem, txtCodigoProduto, txtQuantidade, txtPrecoUnitario, txtDesconto, txtPrecoTotal;

    private JPanel painelDireito;

    public RealizarVenda(Connection conn) {
        this.conn = conn;

        setLayout(new BorderLayout());
        setBackground(new Color(240, 236, 236));
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
        gbcMeio.gridy = 0;
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
        Color bordaAzulClaro = new Color(173, 216, 230);

        itemLabel = new JLabel("Produto:");
        itemLabel.setFont(new Font("Arial", Font.BOLD, 18));
        itemLabel.setForeground(Color.BLACK);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        painelItem.add(itemLabel, gbc);

        txtItem = new JTextField();
        txtItem.setBackground(new Color(24, 39, 55));
        txtItem.setForeground(Color.WHITE);
        txtItem.setOpaque(true);
        txtItem.setFont(new Font("Arial", Font.PLAIN, 20));
        txtItem.setMinimumSize(new Dimension(150, 45));
        txtItem.setPreferredSize(new Dimension(150, 45));
        txtItem.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        txtItem.setBorder(BorderFactory.createLineBorder(bordaAzulClaro, 1));

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
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

        Color bordaAzulClaro = new Color(173, 216, 230);

        lblCodigoProduto = new JLabel("Código do Produto");
        lblCodigoProduto.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        painelInternoEsquerdo.add(lblCodigoProduto, gbc);

        txtCodigoProduto = createTextFieldOutrosCampos();
        gbc.gridx = 0;
        gbc.gridy = 1;
        txtCodigoProduto.setEditable(false);
        txtCodigoProduto.setBorder(BorderFactory.createLineBorder(bordaAzulClaro, 1));
        painelInternoEsquerdo.add(txtCodigoProduto, gbc);

        lblQuantidade = new JLabel("Quantidade");
        lblQuantidade.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 2;
        painelInternoEsquerdo.add(lblQuantidade, gbc);

        txtQuantidade = createTextFieldOutrosCampos();
        txtQuantidade.setBorder(BorderFactory.createLineBorder(bordaAzulClaro, 1));
        gbc.gridx = 0;
        gbc.gridy = 3;
        painelInternoEsquerdo.add(txtQuantidade, gbc);

        lblPrecoUnitario = new JLabel("Preço Unitário");
        lblPrecoUnitario.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 4;
        painelInternoEsquerdo.add(lblPrecoUnitario, gbc);

        txtPrecoUnitario = createTextFieldOutrosCampos();
        txtPrecoUnitario.setBorder(BorderFactory.createLineBorder(bordaAzulClaro, 1));
        gbc.gridx = 0;
        gbc.gridy = 5;
        txtPrecoUnitario.setText("0,00");
        txtPrecoUnitario.setCaretPosition(0);
        txtPrecoUnitario.setEditable(false);
        painelInternoEsquerdo.add(txtPrecoUnitario, gbc);

        lblDesconto = new JLabel("Desconto");
        lblDesconto.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 6;
        painelInternoEsquerdo.add(lblDesconto, gbc);

        txtDesconto = createTextFieldOutrosCampos();
        txtDesconto.setBorder(BorderFactory.createLineBorder(bordaAzulClaro, 1));
        gbc.gridx = 0;
        gbc.gridy = 7;
        txtDesconto.setText("0,00");
        txtDesconto.setCaretPosition(0);
        painelInternoEsquerdo.add(txtDesconto, gbc);

        lblPrecoTotal = new JLabel("Preço Total");
        lblPrecoTotal.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 8;
        painelInternoEsquerdo.add(lblPrecoTotal, gbc);

        txtPrecoTotal = createTextFieldOutrosCampos();
        txtPrecoTotal.setBorder(BorderFactory.createLineBorder(bordaAzulClaro, 1));
        gbc.gridx = 0;
        gbc.gridy = 9;
        txtPrecoTotal.setText("0,00");
        txtPrecoTotal.setCaretPosition(0);
        txtPrecoTotal.setEditable(false);
        painelInternoEsquerdo.add(txtPrecoTotal, gbc);

        gbc.weighty = 1.0;
        gbc.gridy = 10;
        painelInternoEsquerdo.add(Box.createVerticalGlue(), gbc);

        painelEsquerdo.add(painelInternoEsquerdo);
        return painelEsquerdo;
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

    private JPanel createPainelDireito() {
        JPanel painelDireito = new JPanel();
        painelDireito.setLayout(new BoxLayout(painelDireito, BoxLayout.Y_AXIS));
        painelDireito.setBorder(BorderFactory.createEmptyBorder(10, 5, 5, 5));

        JPanel painelInternoDireito = new JPanel(new BorderLayout());
        painelInternoDireito.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

        JPanel painelClienteInfo = new JPanel(new GridBagLayout());
        painelClienteInfo.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(5, 0, 0, 0);

        JLabel lblFarmacia = new JLabel("Farmácia", SwingConstants.CENTER);
        lblFarmacia.setFont(new Font("Arial", Font.BOLD, 16));
        lblFarmacia.setForeground(Color.BLACK);
        painelClienteInfo.add(lblFarmacia, gbc);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        JLabel lblData = new JLabel("Data: " + LocalDate.now().format(formatter));

        gbc.gridy++;
        painelClienteInfo.add(lblData, gbc);

        gbc.gridy++;
        lblCpfCliente = new JLabel("CPF do Consumidor: Não Identificado");
        painelClienteInfo.add(lblCpfCliente, gbc);

        gbc.gridy++;
        lblNomeCliente = new JLabel("Nome do Consumidor: Não Identificado");
        painelClienteInfo.add(lblNomeCliente, gbc);

        gbc.gridy++;
        JLabel lblAtendente = new JLabel("Atendente: Não Identificado");
        painelClienteInfo.add(lblAtendente, gbc);

        gbc.gridy++;
        JLabel lblEspaco = new JLabel("");
        painelClienteInfo.add(lblEspaco, gbc);

        JPanel painelItens = new JPanel(new GridBagLayout());
        painelItens.setBorder(BorderFactory.createTitledBorder("Itens"));
        GridBagConstraints gbcItens = new GridBagConstraints();
        gbcItens.gridx = 0;
        gbcItens.gridy = 0;
        gbcItens.anchor = GridBagConstraints.WEST;
        gbcItens.fill = GridBagConstraints.HORIZONTAL;
        gbcItens.insets = new Insets(5, 55, 10, 55);

        // Cabeçalho da tabela de itens
        JLabel lblItem = new JLabel("Item");
        JLabel lblCodigo = new JLabel("Código");
        JLabel lblDescricao = new JLabel("Descrição");
        JLabel lblQnt = new JLabel("Qnt");
        JLabel lblValorUni = new JLabel("Valor Uni");
        JLabel lblSubtotal = new JLabel("Subtotal");

        // Adicionar cabeçalho no painel
        painelItens.add(lblItem, gbcItens);
        gbcItens.gridx++;
        painelItens.add(lblCodigo, gbcItens);
        gbcItens.gridx++;
        painelItens.add(lblDescricao, gbcItens);
        gbcItens.gridx++;
        painelItens.add(lblQnt, gbcItens);
        gbcItens.gridx++;
        painelItens.add(lblValorUni, gbcItens);
        gbcItens.gridx++;
        painelItens.add(lblSubtotal, gbcItens);

        painelInternoDireito.add(painelClienteInfo, BorderLayout.NORTH);
        painelInternoDireito.add(painelItens, BorderLayout.CENTER);
        painelDireito.add(painelInternoDireito);

        return painelDireito;
    }

    private JPanel ladoEsquerdoFooter() {
        JPanel ladoEsquerdo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel botoesVenda = new JPanel();
        botoesVenda.setLayout(new BoxLayout(botoesVenda, BoxLayout.X_AXIS));
        botoesVenda.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton btnIdentificarCliente = new JButton("Identificar Cliente");
        btnIdentificarCliente.setFont(new Font("Arial", Font.BOLD, 18));
        btnIdentificarCliente.setBackground(new Color(24, 39, 55));
        btnIdentificarCliente.setForeground(Color.WHITE);
        btnIdentificarCliente.setFocusPainted(false);
        btnIdentificarCliente.setMinimumSize(new Dimension(185, 45));
        btnIdentificarCliente.setPreferredSize(new Dimension(185, 45));
        botoesVenda.add(btnIdentificarCliente);

        btnIdentificarCliente.addActionListener(e -> {
            try {
                abrirDialogoIdentificacaoCliente();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao identificar cliente " + ex.getMessage(), "Erro",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

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
        btnConfirmarVenda.setBackground(new Color(0, 133, 0));
        btnConfirmarVenda.setForeground(Color.WHITE);
        btnConfirmarVenda.setFocusPainted(false);
        btnConfirmarVenda.setMinimumSize(new Dimension(185, 40));
        btnConfirmarVenda.setPreferredSize(new Dimension(185, 40));
        botoesVenda.add(btnConfirmarVenda);

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

        ladoEsquerdo.add(botoesVenda);
        return ladoEsquerdo;
    }

    private JPanel ladoDireitoFooter() {
        JPanel ladoDireito = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        ladoDireito.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        JLabel lblTotal = new JLabel("Total: ");
        lblTotal.setFont(new Font("Arial", Font.BOLD, 18));
        lblTotal.setForeground(Color.BLACK);

        Color bordaAzulClaro = new Color(173, 216, 230);
        JTextField txtTotal = new JTextField("0,00", 7);
        txtTotal.setFont(new Font("Arial", Font.PLAIN, 20));
        txtTotal.setPreferredSize(new Dimension(70, 35));
        txtTotal.setMinimumSize(new Dimension(70, 35));
        txtTotal.setMaximumSize(new Dimension(70, 35));
        txtTotal.setBackground(new Color(24, 39, 55));
        txtTotal.setForeground(Color.WHITE);
        txtTotal.setEditable(false);
        txtTotal.setBorder(BorderFactory.createLineBorder(bordaAzulClaro, 1));

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
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                    throws BadLocationException {
                String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
                String newText = currentText.substring(0, offset) + text + currentText.substring(offset + length);

                if (newText.matches("\\d{0,11}")) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }

            @Override
            public void insertString(FilterBypass fb, int offset, String text, AttributeSet attrs)
                    throws BadLocationException {
                replace(fb, offset, 0, text, attrs);
            }
        });

        JButton btnIdentificar = new JButton("Identificar");
        btnIdentificar.setFont(new Font("Arial", Font.BOLD, 16));
        btnIdentificar.setBackground(new Color(24, 39, 55));
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
                    } else if (opcao == 1) {
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

    private void atualizaPainelDireito() {
        painelDireito.revalidate();
        painelDireito.repaint();
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
}