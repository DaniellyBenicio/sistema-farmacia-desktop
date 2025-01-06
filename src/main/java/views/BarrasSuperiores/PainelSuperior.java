package views.BarrasSuperiores;

import javax.swing.*;

import dao.Funcionario.FuncionarioDAO;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Calendar;

import main.ConexaoBD;

public class PainelSuperior extends JPanel {
    private JLabel labelTitulo, labelFuncionario, labelData;
    private JButton[] botoesMenu;
    private CardLayout layoutAlternativo;;
    private JPanel painelDeVisualizacao;
    private String cargoFuncionario;
    private static int idFuncionarioAtual;

    public PainelSuperior(CardLayout layoutAlternativo, JPanel painelDeVisualizacao) {
        this.layoutAlternativo = layoutAlternativo;
        this.painelDeVisualizacao = painelDeVisualizacao;

        setLayout(new BorderLayout());
        inicializarMenuSuperior();
        inicializarMenuOpcoes();

        SwingUtilities.invokeLater(this::abrirDialogoIdentificacaoFuncionario);
    }

    private void inicializarMenuSuperior() {
        JPanel menuSuperior = criarMenuSuperior();
        menuSuperior.add(criarLabelTitulo(), BorderLayout.WEST);
        menuSuperior.add(criarLabelFuncionario(), BorderLayout.CENTER);
        menuSuperior.add(criarPainelData(), BorderLayout.EAST);
        add(menuSuperior, BorderLayout.NORTH);
    }

    private JPanel criarMenuSuperior() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBackground(new Color(24, 39, 55));
        painel.setPreferredSize(new Dimension(getWidth(), 60));
        return painel;
    }

    private JLabel criarLabelTitulo() {
        labelTitulo = new JLabel("Farmácia", JLabel.LEFT);
        labelTitulo.setFont(new Font("Arial", Font.BOLD, 24));
        labelTitulo.setForeground(Color.WHITE);
        labelTitulo.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        return labelTitulo;
    }

    private JLabel criarLabelFuncionario() {
        labelFuncionario = new JLabel("Funcionário: Não identificado", JLabel.CENTER);
        labelFuncionario.setFont(new Font("Arial", Font.PLAIN, 14));
        labelFuncionario.setForeground(Color.WHITE);
        labelFuncionario.setCursor(new Cursor(Cursor.HAND_CURSOR));

        labelFuncionario.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                abrirDialogoIdentificacaoFuncionario();
            }
        });

        return labelFuncionario;
    }

    private void abrirDialogoIdentificacaoFuncionario() {
        JDialog dialogo = new JDialog();
        dialogo.setTitle("Identificar Funcionário");
        dialogo.setSize(350, 180);
        dialogo.setLayout(new GridBagLayout());
        dialogo.setModal(true);
        dialogo.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        adicionarComponentesDialogo(dialogo, gbc);
        dialogo.setLocationRelativeTo(null);
        dialogo.setVisible(true);
    }

    private void adicionarComponentesDialogo(JDialog dialogo, GridBagConstraints gbc) {
        JLabel labelCodigo = new JLabel("Código do Funcionário:");
        labelCodigo.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 0;
        dialogo.add(labelCodigo, gbc);

        JTextField campoCodigoFuncionario = new JTextField(10);
        gbc.gridy = 1;
        dialogo.add(campoCodigoFuncionario, gbc);

        JButton botaoBuscar = new JButton("Buscar");
        estilizarBotao(botaoBuscar);
        gbc.gridy = 2;
        dialogo.add(botaoBuscar, gbc);

        botaoBuscar.addActionListener(e -> buscarFuncionario(campoCodigoFuncionario.getText(), dialogo));
        campoCodigoFuncionario.addActionListener(e -> botaoBuscar.doClick());
    }

    private void estilizarBotao(JButton botao) {
        botao.setBackground(new Color(24, 39, 55));
        botao.setForeground(Color.WHITE);
        botao.setFont(new Font("Arial", Font.BOLD, 14));
    }

    public static int getIdFuncionarioAtual() {
        return idFuncionarioAtual;
    }

    private void buscarFuncionario(String codigoFuncionarioDigitado, JDialog dialogo) {

        if (codigoFuncionarioDigitado == null || codigoFuncionarioDigitado.trim().isEmpty()) {
            mostrarMensagemErro(dialogo, "Por favor, insira o código do funcionário.");
            return;
        }

        Connection conexao = null;
        try {
            int codigo = Integer.parseInt(codigoFuncionarioDigitado);
            conexao = ConexaoBD.getConnection();
            String resultado = new FuncionarioDAO().verificarFuncionarioPorCodigo(conexao, codigo);

            if (resultado.startsWith("Código:")) {
                String[] partes = resultado.split(", ");
                String codigoFuncionario = partes[0].replace("Código: ", "");
                String nomeFuncionario = partes[1].replace("Nome: ", "");
                cargoFuncionario = partes[2].replace("Cargo: ", "");

                idFuncionarioAtual = Integer.parseInt(codigoFuncionario);

                labelFuncionario.setText("Funcionário: " + codigoFuncionario + " - " + nomeFuncionario);

                dialogo.dispose();

                atualizarEstadoDosBotoesDeAcordoComCargo();

                if (!"Gerente".equalsIgnoreCase(cargoFuncionario)) {
                    selecionarOpcaoMenu(botoesMenu[0]);
                    mostrarPainel("Vendas");
                }
            } else {
                mostrarMensagemErro(dialogo, "Funcionário não encontrado!");
            }
        } catch (SQLException ex) {
            mostrarMensagemErro(dialogo, "Erro ao verificar o funcionário: ");
        } catch (NumberFormatException ex) {
            mostrarMensagemErro(dialogo, "Código inválido. Por favor, insira um número.");
        } finally {
            if (conexao != null) {
                try {
                    conexao.close();
                } catch (SQLException ignored) {
                }
            }
        }
    }

    private void atualizarEstadoDosBotoesDeAcordoComCargo() {
        boolean isGerente = "Gerente".equalsIgnoreCase(cargoFuncionario);

        botoesMenu[3].setEnabled(isGerente);
        botoesMenu[4].setEnabled(isGerente);

        if (isGerente) {
            botoesMenu[3].setForeground(Color.BLACK);
            botoesMenu[4].setForeground(Color.BLACK);
        } else {
            botoesMenu[3].setForeground(Color.GRAY);
            botoesMenu[4].setForeground(Color.GRAY);
        }
    }

    private void mostrarMensagemErro(JDialog dialogo, String mensagem) {
        JOptionPane.showMessageDialog(dialogo, mensagem, "Erro", JOptionPane.ERROR_MESSAGE);
    }

    private JPanel criarPainelData() {
        JPanel painel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        painel.setBackground(new Color(24, 39, 55));

        labelData = new JLabel(obterDataEHoraAtual(), JLabel.RIGHT);
        labelData.setFont(new Font("Arial", Font.PLAIN, 14));
        labelData.setForeground(Color.WHITE);
        labelData.setBorder(BorderFactory.createEmptyBorder(18, 0, 0, 10));

        Timer timer = new Timer(1000, e -> labelData.setText(obterDataEHoraAtual()));
        timer.start();

        painel.add(labelData);
        return painel;
    }

    private void inicializarMenuOpcoes() {
        JPanel menuOpcoes = criarMenuOpcoes();
        add(menuOpcoes, BorderLayout.SOUTH);
        add(painelDeVisualizacao, BorderLayout.CENTER);
    }

    private JPanel criarMenuOpcoes() {
        JPanel painel = new JPanel(new GridLayout(1, 7));
        painel.setBackground(new Color(200, 200, 200));
        painel.setPreferredSize(new Dimension(getWidth(), 50));

        String[] itensMenu = { "Vendas", "Medicamento", "Produtos", "Funcionário", "Fornecedor", "Cliente", "Estoque" };
        botoesMenu = new JButton[itensMenu.length];

        for (int i = 0; i < itensMenu.length; i++) {
            botoesMenu[i] = criarBotaoMenu(itensMenu[i], i, itensMenu);
            painel.add(botoesMenu[i]);
        }

        selecionarOpcaoMenu(botoesMenu[0]);
        return painel;
    }

    private JButton criarBotaoMenu(String texto, int indice, String[] itensMenu) {
        JButton botao = new JButton(texto);
        botao.setFont(new Font("Arial", Font.PLAIN, 16));
        botao.setBackground(new Color(200, 200, 200));
        botao.setForeground(Color.BLACK);
        botao.setBorderPainted(false);
        botao.setFocusPainted(false);
        botao.setContentAreaFilled(false);
        botao.setFocusable(false);

        botao.addActionListener(e -> {
            selecionarOpcaoMenu(botao);
            mostrarPainel(itensMenu[indice]);
        });
        return botao;
    }

    private void selecionarOpcaoMenu(JButton opcaoSelecionada) {
        for (JButton botao : botoesMenu) {
            botao.setContentAreaFilled(false);
            botao.setOpaque(false);
            botao.setForeground(Color.BLACK);
        }
        opcaoSelecionada.setContentAreaFilled(true);
        opcaoSelecionada.setOpaque(true);
        opcaoSelecionada.setBackground(new Color(150, 150, 150));
        opcaoSelecionada.setForeground(Color.WHITE);
    }

    private String obterDataEHoraAtual() {
        Calendar calendario = Calendar.getInstance();
        return String.format("%02d/%02d/%d %02d:%02d:%02d", calendario.get(Calendar.DAY_OF_MONTH),
                calendario.get(Calendar.MONTH) + 1, calendario.get(Calendar.YEAR),
                calendario.get(Calendar.HOUR_OF_DAY), calendario.get(Calendar.MINUTE), calendario.get(Calendar.SECOND));
    }

    private void mostrarPainel(String itemMenu) {
        switch (itemMenu) {
            case "Vendas":
                layoutAlternativo.show(painelDeVisualizacao, "Vendas");
                break;
            case "Fornecedor":
                layoutAlternativo.show(painelDeVisualizacao, "ListaDeFornecedores");
                break;
            case "Funcionário":
                layoutAlternativo.show(painelDeVisualizacao, "ListaDeFuncionarios");
                break;
        }
    }
}