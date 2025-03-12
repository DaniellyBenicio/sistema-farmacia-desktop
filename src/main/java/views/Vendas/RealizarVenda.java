package views.Vendas;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.sql.Connection;
import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

import views.Clientes.CadastrarCliente;

public class RealizarVenda extends JPanel {

    private Connection conn;
    private JLabel itemLabel, lblCodigoProduto, lblQuantidade, lblPrecoUnitario, lblDesconto, lblPrecoTotal;
    private JTextField txtItem, txtCodigoProduto, txtQuantidade, txtPrecoUnitario, txtDesconto, txtPrecoTotal;

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
        painelMeio.add(createPainelDireito(), gbcMeio);

        JScrollPane scrollPane = new JScrollPane(painelMeio);
        scrollPane.setBorder(null);
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
        // Define uma altura máxima específica para este painel
        painelInternoEsquerdo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 500));
        painelInternoEsquerdo.setPreferredSize(new Dimension(450, 500)); // Altura fixa sugerida
    
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
    
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
        txtPrecoTotal.setEditable(false);
        painelInternoEsquerdo.add(txtPrecoTotal, gbc);
    
        // Adiciona o painel interno ao painel esquerdo
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
        painelDireito.setBorder(BorderFactory.createEmptyBorder(10, 10, 35, 10));

        JPanel painelInternoDireito = new JPanel(new BorderLayout());
        painelInternoDireito.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

        JLabel labelResumoVenda = new JLabel("Resumo da Venda");
        labelResumoVenda.setHorizontalAlignment(SwingConstants.CENTER);
        labelResumoVenda.setVerticalAlignment(SwingConstants.CENTER);
        labelResumoVenda.setFont(new Font("Arial", Font.BOLD, 16));
        painelInternoDireito.add(labelResumoVenda, BorderLayout.CENTER);

        painelDireito.add(painelInternoDireito);
        painelDireito.add(Box.createVerticalGlue());
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

            if (verificarCliente(cpf)) {
                dialogo.dispose();
                JOptionPane.showMessageDialog(dialogo, "Cliente identificada com sucesso!", "Sucesso",
                        JOptionPane.INFORMATION_MESSAGE);
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
        });

        dialogo.setLocationRelativeTo(null);
        dialogo.setVisible(true);
    }

    private boolean verificarCliente(String cpf) {
        return "12345678900".equals(cpf);
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