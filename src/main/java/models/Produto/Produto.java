package models.Produto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

import models.Funcionario.Funcionario;
import models.Fornecedor.Fornecedor;
import models.Fabricante.Fabricante;

public class Produto {
    private int id;
    private String nome;
    private BigDecimal valor;
    private int qntEstoque;
    private LocalDate dataValidade;
    private LocalDate dataFabricacao;
    private String qntMedida;
    private String embalagem;
    private Funcionario funcionario;
    private Fabricante fabricante;
    private Fornecedor fornecedor;

    public Produto(int id, String nome, BigDecimal valor, int qntEstoque, LocalDate dataValidade, LocalDate dataFabricacao, String qntMedida, String embalagem, Funcionario funcionario, Fabricante fabricante, Fornecedor fornecedor){
        this.id = id;
        this.nome = nome;
        this.valor = valor;
        this.qntEstoque = qntEstoque;
        this.dataValidade = dataValidade;
        this.dataFabricacao = dataFabricacao;
        this.qntMedida = qntMedida;
        this.embalagem = embalagem;
        this.funcionario = funcionario;
        this.fabricante = fabricante;
        this.fornecedor = fornecedor;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor deve ser maior que zero.");
        }
        this.valor = valor;
    }

    public int getQntEstoque() {
        return qntEstoque;
    }

    public void setQntEstoque(int qntEstoque) {
        if (qntEstoque >= 0) {
            this.qntEstoque = qntEstoque;
        } else {
            throw new IllegalArgumentException("Quantidade não pode ser negativa");
        }
    }

    public LocalDate getDataValidade() {
        return dataValidade;
    }

    public void setDataValidade(YearMonth dataValidade) {
        LocalDate validade = dataValidade.atDay(28);
        LocalDate hoje = LocalDate.now();

        if (validade.isBefore(hoje)) {
            throw new IllegalArgumentException("Data inválida! A data de validade não pode ser anterior à data atual.");
        }

        if (dataFabricacao != null && validade.isBefore(dataFabricacao)) {
            throw new IllegalArgumentException("Data inválida! A data de validade não pode ser anterior à data de fabricação.");
        }
        this.dataValidade = validade;
    }

    public LocalDate getDataFabricacao() {
        return dataFabricacao;
    }

    public void setDataFabricacao(YearMonth dataFabricacao) {
        LocalDate fabricacao = dataFabricacao.atDay(28); 
        LocalDate hoje = LocalDate.now();

        if (fabricacao.isAfter(hoje)) {
            throw new IllegalArgumentException("Data inválida! A data de fabricação não pode ser posterior à data atual.");
        }
        if (dataValidade != null && fabricacao.isAfter(dataValidade)) {
            throw new IllegalArgumentException("Data inválida! A data de fabricação não pode ser posterior à data de validade.");
        }

        this.dataFabricacao = fabricacao;
    }

    public String getQntMedida() {
        return qntMedida;
    }

    public void setQntMedida(String qntMedida) {
        if (qntMedida == null || qntMedida.trim().isEmpty()) {
            throw new IllegalArgumentException("A quantidade de medida não pode ser nula ou vazia.");
        }
    
        if (!qntMedida.matches("^\\d+(\\.\\d+)?(mL|L|g|kg|un)$")) {
            throw new IllegalArgumentException("Formato inválido para a quantidade de medida. Use valores como '500mL', '2kg', '1L', '1un'.");
        }
    
        double valor = Double.parseDouble(qntMedida.replaceAll("[^0-9.]", ""));
        if (valor <= 0) {
            throw new IllegalArgumentException("A quantidade de medida deve ser maior que zero.");
        }
    
        this.qntMedida = qntMedida;
    }    

    public String getEmbalagem() {
        return embalagem;
    }

    public void setEmbalagem(String embalagem) {
        if (embalagem == null || embalagem.trim().isEmpty()) {
            throw new IllegalArgumentException("A forma farmacêutica não pode ser vazia.");
        }
        this.embalagem = embalagem;
    }
    //exs: lata, frasco, pacote, caixa, bisnaga, tubo, pote, garrafa, vidro, rolo, spray, refil, pacote

    public Funcionario getFuncionario() {
        return funcionario;
    }

    public void setFuncionario(Funcionario funcionario) {
        if (funcionario == null) {
            throw new IllegalArgumentException("O funcionário não pode ser nulo.");
        }
        this.funcionario = funcionario;
    }

    public Fabricante getFabricante() {
        return fabricante;
    }

    public void setFabricante(Fabricante fabricante) {
        if(fabricante == null){
            throw new IllegalArgumentException("O fabricante não pode ser nulo");
        }
        this.fabricante = fabricante;
    }

    public Fornecedor getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(Fornecedor fornecedor) {
        if(fornecedor == null){
            throw new IllegalArgumentException("O fornecedor não pode ser nulo");
        }
        this.fornecedor = fornecedor;
    }

}