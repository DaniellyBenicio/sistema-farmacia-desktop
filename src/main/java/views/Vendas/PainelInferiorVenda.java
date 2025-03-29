package views.Vendas;

import java.awt.*;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.text.*;
import controllers.Cliente.ClienteController;
import dao.ItemVenda.ItemVendaDAO;
import main.ConexaoBD;
import models.Cliente.Cliente;
import models.ItemVenda.ItemVenda;
import views.Clientes.CadastrarCliente;

public class PainelInferiorVenda extends JPanel {
    private static final Color INPUT_BG_COLOR = new Color(24, 39, 55);
    private static final Color BUTTON_CONFIRM_COLOR = new Color(0, 133, 0);
    private static final Color BORDER_COLOR = new Color(173, 216, 230);

    private final Connection conn;
    private final RealizarVenda vendaPrincipal;
    private ResumoDaVenda painelDireito;
    private JTextField txtTotal;
    private JButton btnRemoverItem, btnConfirmarVenda, btnCancelarVenda;

    public PainelInferiorVenda(Connection conn, RealizarVenda vendaPrincipal, ResumoDaVenda painelDireito) {
        this.conn = conn;
        this.vendaPrincipal = vendaPrincipal;
        this.painelDireito = painelDireito;
        setupLayout();
    }

    private void setupLayout() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridy = 0;
        gbc.weighty = 1.0;

        gbc.gridx = 0;
        gbc.weightx = 0.9;
        add(ladoEsquerdoFooter(), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.1;
        add(ladoDireitoFooter(), gbc);
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
        btnIdentificarCliente.addActionListener(e -> abrirDialogoIdentificacaoCliente());
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
        btnCancelarVenda.addActionListener(e -> cancelarVendaComConfirmacao());
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
        ladoDireito.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 10));

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
        txtTotal.setHorizontalAlignment(JTextField.RIGHT);

        ladoDireito.add(lblTotal);
        ladoDireito.add(txtTotal);
        return ladoDireito;
    }

    private void abrirDialogoIdentificacaoCliente() {
        JDialog dialogo = new JDialog();
        dialogo.setTitle("Identificar Cliente");
        dialogo.setSize(300, 150);
        dialogo.setModal(true);
        dialogo.setLayout(new GridBagLayout());

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

        JTextField txtCpf = new JTextField(11);
        txtCpf.setFont(new Font("Arial", Font.PLAIN, 18));
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
        btnIdentificar.setBackground(INPUT_BG_COLOR);
        btnIdentificar.setForeground(Color.WHITE);
        gbc.gridy = 2;
        dialogo.add(btnIdentificar, gbc);
        txtCpf.addActionListener(e -> btnIdentificar.doClick());

        btnIdentificar.addActionListener(e -> {
            String cpf = txtCpf.getText().trim();
            if (cpf.isEmpty() || cpf.length() != 11) {
                JOptionPane.showMessageDialog(null, "Por favor, informe um CPF válido com 11 dígitos.", "Erro",
                        JOptionPane.ERROR_MESSAGE);
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
                    JOptionPane.showMessageDialog(null, mensagem, "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    vendaPrincipal.atualizarCliente(cliente.getNome(), cliente.getCpf());
                } else {
                    int opcao = JOptionPane.showOptionDialog(null, "Cliente não cadastrado. Deseja cadastrar?",
                            "Cadastrar Cliente", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null,
                            new Object[] { "Sim", "Não" }, "Sim");

                    if (opcao == JOptionPane.YES_OPTION) {
                        JDialog cadastroDialog = new JDialog(dialogo, "Cadastrar Cliente", true);
                        cadastroDialog.setSize(1200, 650);
                        cadastroDialog.setLocationRelativeTo(dialogo);
                        cadastroDialog.add(new CadastrarCliente());
                        cadastroDialog.setVisible(true);
                        dialogo.dispose();
                    } else {
                        dialogo.dispose();
                    }
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Erro ao buscar cliente: " + ex.getMessage(), "Erro",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        dialogo.setLocationRelativeTo(vendaPrincipal);
        dialogo.setVisible(true);
    }

    private void abrirDialogoRemoverItem() {
        JDialog dialogo = new JDialog();
        dialogo.setTitle("Remover Item");
        dialogo.setSize(350, 180);
        dialogo.setModal(true);
        dialogo.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel lblOrdem = new JLabel("Digite o número do item (ordem):");
        lblOrdem.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        dialogo.add(lblOrdem, gbc);

        JTextField txtOrdem = new JTextField(10);
        txtOrdem.setFont(new Font("Arial", Font.PLAIN, 18));
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

                int resposta = JOptionPane.showOptionDialog(dialogo, mensagemConfirmacao, "Confirmação de Remoção",
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, new Object[] { "Sim", "Não" },
                        "Sim");

                if (resposta == JOptionPane.YES_OPTION) {
                    int quantidadeRemovida = Integer.parseInt(dadosItem[3].replace(",", ".").trim());
                    String nomeCompleto = dadosItem[2].trim();
                    String nomeBase = nomeCompleto.split(" ")[0];
                    ItemVenda itemVenda = new ItemVenda();

                    ItemVendaDAO.verificarTipoEEstoque(conn, itemVenda, quantidadeRemovida, true, nomeBase);

                    painelDireito.removerItem(ordem);
                    atualizarTotalFooter();
                    atualizarEstadoBotoes();
                    dialogo.dispose();
                    JOptionPane.showMessageDialog(vendaPrincipal, "Item removido com sucesso!", "Sucesso",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialogo, "Erro ao remover item: " + ex.getMessage(), "Erro",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        txtOrdem.addActionListener(e -> btnRemover.doClick());
        dialogo.setLocationRelativeTo(vendaPrincipal);
        dialogo.setVisible(true);
    }

    private void confirmarVenda() {
        int resposta = JOptionPane.showOptionDialog(null,
                "Deseja finalizar a venda?\nVocê será direcionado para a tela de pagamento.",
                "Confirmar Venda",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, new Object[] { "Sim", "Não" }, "Sim");

        if (resposta == JOptionPane.YES_OPTION) {
            try {
                abrirDialogoPagamentoVenda();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao confirmar venda: " + ex.getMessage(), "Erro",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
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
        dialogoPagamento.setLocationRelativeTo(vendaPrincipal);

        PagamentoVenda painelPagamento = new PagamentoVenda(painelDireito.getTotalGeral(), painelDireito,
                vendaPrincipal);
        dialogoPagamento.add(painelPagamento);
        dialogoPagamento.setVisible(true);
    }

    private void cancelarVendaComConfirmacao() {
        if (painelDireito.itensMap.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Não há itens para cancelar.", "Aviso",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int resposta = JOptionPane.showOptionDialog(vendaPrincipal,
                "Deseja cancelar a venda? Os itens retornarão ao estoque.",
                "Confirmação", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, new String[] { "Sim", "Não" }, "Sim");

        if (resposta == JOptionPane.YES_OPTION) {
            try {
                boolean sucessoCancelamento = true;

                for (String ordem : painelDireito.itensMap.keySet()) {
                    String[] dadosItem = painelDireito.getDadosItemPorOrdem(ordem);
                    if (dadosItem == null) {
                        JOptionPane.showMessageDialog(vendaPrincipal, "Item com ordem '" + ordem + "' não encontrado.",
                                "Erro", JOptionPane.ERROR_MESSAGE);
                        sucessoCancelamento = false;
                        break;
                    }

                    try {
                        int quantidadeRemovida = Integer.parseInt(dadosItem[3].replace(",", ".").trim());
                        String nomeCompleto = dadosItem[2].trim();
                        String nomeBase = nomeCompleto.split(" ")[0];
                        ItemVenda itemVenda = new ItemVenda();

                        boolean sucesso = ItemVendaDAO.verificarTipoEEstoque(conn, itemVenda, quantidadeRemovida, true,
                                nomeBase);
                        if (!sucesso) {
                            throw new SQLException("Erro ao retornar estoque para o item: " + nomeBase);
                        }
                    } catch (SQLException | NumberFormatException e) {
                        JOptionPane.showMessageDialog(vendaPrincipal,
                                "Erro ao processar item de venda: " + e.getMessage(),
                                "Erro", JOptionPane.ERROR_MESSAGE);
                        sucessoCancelamento = false;
                        break;
                    }
                }

                if (sucessoCancelamento) {
                    vendaPrincipal.reiniciarVenda();
                    atualizarTotalFooter();
                    atualizarEstadoBotoes();
                    JOptionPane.showMessageDialog(vendaPrincipal, "Venda cancelada com sucesso e estoque atualizado!",
                            "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(vendaPrincipal, "Falha no cancelamento da venda.",
                            "Erro", JOptionPane.ERROR_MESSAGE);
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao cancelar venda: " + ex.getMessage(),
                        "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void atualizarTotalFooter() {
        BigDecimal total = painelDireito.getTotalGeral();
        if (total == null || total.compareTo(BigDecimal.ZERO) <= 0 || painelDireito.itensMap.isEmpty()) {
            txtTotal.setText("0,00");
        } else {
            txtTotal.setText(total.setScale(2, BigDecimal.ROUND_HALF_UP).toString().replace(".", ","));
        }
    }

    public void atualizarEstadoBotoes() {
        boolean temItens = !painelDireito.itensMap.isEmpty();
        btnRemoverItem.setEnabled(temItens);
        btnConfirmarVenda.setEnabled(temItens);
        btnCancelarVenda.setEnabled(temItens);
    }

    public void setPainelDireito(ResumoDaVenda novoPainelDireito) {
        this.painelDireito = novoPainelDireito;
        atualizarEstadoBotoes();
        atualizarTotalFooter();
    }
}