package views.Medicamentos;

import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import javax.swing.*;
import javax.swing.text.JTextComponent;

import controllers.Medicamento.MedicamentoController;
import dao.Fornecedor.FornecedorDAO;
import dao.Funcionario.FuncionarioDAO;
import main.ConexaoBD;
import models.Categoria.Categoria;
import models.Fabricante.Fabricante;
import models.Fornecedor.Fornecedor;
import models.Funcionario.Funcionario;
import models.Medicamento.Medicamento;
import models.Medicamento.Medicamento.Tipo;
import models.Medicamento.Medicamento.TipoReceita;
import views.BarrasSuperiores.PainelSuperior;

public class EditarMedicamento extends JPanel {
    private JTextField nomedoMedicamentoField;
    private JComboBox<String> categoriaComboBox;
    private JTextField fabricanteField;
    private JTextField dosagemField;
    private JComboBox<String> tipoComboBox;
    private JComboBox<String> fornecedorComboBox;
    private JFormattedTextField dataFabricacaoField;
    private JFormattedTextField dataValidadeField;
    private JTextField estoqueField;
    private JTextField valorUnitarioField;
    private JTextComponent categoriaField;
    private JComboBox<String> formaFarmaceuticaComboBox;
    private JTextComponent formaFarmaceuticaField;
    private JComboBox<String> receitaComboBox;
    private JComboBox<String> fabricanteComboBox;

    private void atualizarCamposMedicamento(Medicamento medicamento) {
        nomedoMedicamentoField.setText(medicamento.getNome());
        dosagemField.setText(medicamento.getDosagem());
        estoqueField.setText(String.valueOf(medicamento.getEstoque()));
        valorUnitarioField.setText(String.format("R$ %.2f", medicamento.getValorUnit()));
        dataFabricacaoField.setText(medicamento.getDataFabricacao().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        dataValidadeField.setText(medicamento.getDataValidade().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        tipoComboBox.setSelectedItem(medicamento.getTipo().toString());
        categoriaComboBox.setSelectedItem(medicamento.getCategoria().getNome());
        fornecedorComboBox.setSelectedItem(medicamento.getFornecedor().getNome());
        formaFarmaceuticaComboBox.setSelectedItem(medicamento.getFormaFarmaceutica());
        receitaComboBox.setSelectedItem(medicamento.getTipoReceita().toString());

        if (medicamento.getFabricante() != null) {
            fabricanteField.setText(medicamento.getFabricante().getNome());
        }
    }

    private JButton editarMedicamento() {
        JButton editarButton = new JButton("EDITAR");
        editarButton.setFont(new Font("Arial", Font.BOLD, 17));
        editarButton.setBackground(new Color(24, 39, 55));
        editarButton.setForeground(Color.WHITE);
        editarButton.setFocusPainted(false);
        editarButton.setPreferredSize(new Dimension(140, 35));

        editarButton.addActionListener(e -> {
            try {
                int idFuncionario = PainelSuperior.getIdFuncionarioAtual();

                String nomeMedicamento = nomedoMedicamentoField.getText().trim();
                if (nomeMedicamento.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "O nome do medicamento não pode ser vazio.", "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!nomeMedicamento.matches("^[\\p{L}\\d\\s]*$")) {
                    JOptionPane.showMessageDialog(this,
                            "Nome inválido (apenas letras, números e espaços são permitidos).",
                            "Erro",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String tipoNome = (String) tipoComboBox.getSelectedItem();
                if ("Selecione".equals(tipoNome)) {
                    JOptionPane.showMessageDialog(this, "Por favor, selecione o tipo de medicamento.", "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Tipo tipo = Tipo.valueOf(tipoNome.toUpperCase());

                String categoriaNome = (String) categoriaComboBox.getSelectedItem();
                if ("Outros".equals(categoriaNome)) {
                    categoriaNome = categoriaField.getText().trim();

                    if (categoriaNome.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "A categoria não pode ser vazia.", "Erro", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }

                if ("Selecione".equals(categoriaNome)) {
                    JOptionPane.showMessageDialog(this, "Por favor, selecione uma categoria.", "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String dosagem = dosagemField.getText().trim();
                if (dosagem.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "A dosagem não pode ser vazia.", "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                } else {
                    if (!dosagem.matches("\\d+(\\.\\d+)?(mg|g|mcg|ml|l)")) {
                        JOptionPane.showMessageDialog(this,
                                "Informe a dosagem com as seguintes unidades válidas:\n" +
                                        "(mg, g, mcg, ml, l).\n" +
                                        "Exemplo: 500mg, 10g",
                                "Erro",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    double dosagemValor = Double.parseDouble(dosagem.replaceAll("[^\\d.]", ""));
                    if (dosagemValor <= 0) {
                        JOptionPane.showMessageDialog(this, "A dosagem deve ser um valor positivo.", "Erro",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }

                String fornecedorNome = (String) fornecedorComboBox.getSelectedItem();
                if ("Selecione".equals(fornecedorNome)) {
                    JOptionPane.showMessageDialog(this, "Por favor, selecione um fornecedor.", "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Funcionario funcionario;
                Fornecedor fornecedor;

                try (Connection conn = ConexaoBD.getConnection()) {
                    fornecedor = FornecedorDAO.buscarFornecedorPorNome(conn, fornecedorNome);
                    funcionario = FuncionarioDAO.buscarFuncionarioId(conn, idFuncionario);

                    if (funcionario == null) {
                        JOptionPane.showMessageDialog(this, "Funcionário não encontrado com ID: " + idFuncionario, "Erro", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Erro na busca de dados: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String formaFarmaceuticaNome = (String) formaFarmaceuticaComboBox.getSelectedItem();
                if ("Selecione".equals(formaFarmaceuticaNome)) {
                    JOptionPane.showMessageDialog(this, "Por favor, selecione uma forma farmacêutica.", "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if ("Outros".equals(formaFarmaceuticaNome)) {
                    formaFarmaceuticaNome = formaFarmaceuticaField.getText().trim();
                    if (formaFarmaceuticaNome.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "A forma farmacêutica não pode ser vazia.", "Erro", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }

                String tipoReceitaNome = (String) receitaComboBox.getSelectedItem();
                if ("Selecione".equals(tipoReceitaNome)) {
                    JOptionPane.showMessageDialog(this, "Por favor, selecione o tipo de receita.", "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                TipoReceita tipoReceita = TipoReceita.valueOf(tipoReceitaNome.toUpperCase());

                String estoqueTexto = estoqueField.getText().trim();
                if (estoqueTexto.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "O estoque não pode ser vazio.", "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Integer estoque = Integer.valueOf(estoqueTexto);
                if (estoque < 0) {
                    JOptionPane.showMessageDialog(this, "A quantidade informada para estoque não pode ser negativa", "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String fabricanteNome = fabricanteField.getText().trim();
                if (fabricanteNome.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Por favor, insira o nome do fabricante.", "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String dataFabricacaoTexto = dataFabricacaoField.getText().replace("/", "-");
                String dataValidadeTexto = dataValidadeField.getText().replace("/", "-");
                LocalDate dataFabricacao;
                LocalDate dataValidade;

                try {
                    dataFabricacao = LocalDate.parse(dataFabricacaoTexto, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                    dataValidade = LocalDate.parse(dataValidadeTexto, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                } catch (DateTimeParseException ex) {
                    JOptionPane.showMessageDialog(this, "Formato de data inválido. Use dd/MM/yyyy.", "Erro",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (dataFabricacao.isAfter(LocalDate.now())) {
                    JOptionPane.showMessageDialog(this, "Data inválida! Não pode ser posterior à data atual.", "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (dataValidade.isBefore(dataFabricacao)) {
                    JOptionPane.showMessageDialog(this, "Data inválida! Não pode ser anterior à data de fabricação.", "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (dataValidade.isBefore(LocalDate.now())) {
                    JOptionPane.showMessageDialog(this, "Data inválida! Não pode ser anterior à data atual.", "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                double valorUnitario = Double.parseDouble(valorUnitarioField.getText().replace("R$", "").trim().replace(",", "."));
                if (valorUnitario < 0) {
                    JOptionPane.showMessageDialog(this, "O valor unitário não pode ser negativo.", "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Medicamento medicamento = new Medicamento();
                medicamento.setNome(nomeMedicamento);
                medicamento.setDosagem(dosagem);
                medicamento.setEstoque(estoque);
                medicamento.setValorUnit(valorUnitario);
                medicamento.setDataFabricacao(dataFabricacao);
                medicamento.setDataValidade(dataValidade);
                medicamento.setTipo(tipo);
                medicamento.setCategoria(categoriaNome);
                medicamento.setFornecedor(fornecedor);
                medicamento.setFormaFarmaceutica(formaFarmaceuticaNome);
                medicamento.setTipoReceita(tipoReceita);
                medicamento.setFabricante(new Fabricante(fabricanteNome));

                MedicamentoController controller = new MedicamentoController();
                controller.atualizarMedicamento(medicamento);

                JOptionPane.showMessageDialog(this, "Medicamento editado com sucesso!");
                limparCampos();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Erro ao atualizar medicamento: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        return editarButton;
    }

    private void limparCampos() {
        nomedoMedicamentoField.setText("");
        dosagemField.setText("");
        estoqueField.setText("");
        valorUnitarioField.setText("");
        dataFabricacaoField.setText("");
        dataValidadeField.setText("");
        tipoComboBox.setSelectedIndex(0);
        categoriaComboBox.setSelectedIndex(0);
        fornecedorComboBox.setSelectedIndex(0);
        formaFarmaceuticaComboBox.setSelectedIndex(0);
        receitaComboBox.setSelectedIndex(0);
        fabricanteField.setText("");
    }
}
