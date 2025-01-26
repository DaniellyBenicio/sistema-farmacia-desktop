package models.Medicamento;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.List;

import models.Funcionario.Funcionario;
import models.Fornecedor.Fornecedor;
import models.Categoria.Categoria;
import models.Fabricante.Fabricante;

public class Medicamento {
    private int id;
    private String nome;
    private String dosagem;
    private String formaFarmaceutica;
    private double valorUnit;
    private LocalDate dataValidade;
    private LocalDate dataFabricacao;
    private TipoReceita tipoReceita;
    private int qnt;
    private Tipo tipo;
    private Categoria categoria;
    private Fabricante fabricante;
    private Fornecedor fornecedor;
    private Funcionario funcionario;

    public Medicamento(){};

    public Medicamento(String nome, String dosagem, String formaFarmaceutica, double valorUnit, LocalDate dataValidade, LocalDate dataFabricacao, TipoReceita tipoReceita, int qnt, Tipo tipo, Categoria categoria, Fabricante fabricante, Fornecedor fornecedor, Funcionario funcionario) {
        this.nome = nome;
        this.dosagem = dosagem;
        this.formaFarmaceutica = formaFarmaceutica;
        this.valorUnit = valorUnit;
        this.dataValidade = dataValidade;
        this.dataFabricacao = dataFabricacao;
        this.tipoReceita = tipoReceita;
        this.qnt = qnt;
        this.tipo = tipo;
        this.categoria = categoria;
        this.fabricante = fabricante;
        this.fornecedor = fornecedor;
        this.funcionario = funcionario;
    }
    
    public enum TipoReceita {
        SIMPLES, ESPECIAL;
    }

    public enum Tipo {
        ETICO, GENERICO, SIMILAR
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

    public String getDosagem() {
        return dosagem;
    }

    public void setDosagem(String dosagem) {
        if (dosagem == null || dosagem.trim().isEmpty()) {
            throw new IllegalArgumentException("A dosagem não pode ser vazia.");
        }
        if (!dosagem.matches("\\d+(\\.\\d+)?(mg|g|mcg|ml|l)")) {
            throw new IllegalArgumentException("Informe a unidade válida (mg, g, mcg, ml, l).");
        }
        double dosagemValor = Double.parseDouble(dosagem.replaceAll("[^\\d.]", ""));
        if (dosagemValor <= 0) {
            throw new IllegalArgumentException("A dosagem deve ser um valor positivo.");
        }
        this.dosagem = dosagem;
    }
    

    public String getFormaFarmaceutica() {
        return formaFarmaceutica;
    }

    public void setFormaFarmaceutica(String formaFarmaceutica) {
        if (formaFarmaceutica == null || formaFarmaceutica.trim().isEmpty()) {
            throw new IllegalArgumentException("A forma farmacêutica não pode ser vazia.");
        }

        List<String> formasValidas = Arrays.asList(
            "comprimido", "creme", "pomada", "injecao", "xarope", "solucao", "spray", 
            "capsula", "gel", "locao", "gelatina", "supositorio", "pó", "emulsao", 
            "colirio", "gotejamento", "aerossol", "spray nasal", "pastilha", 
            "suspensao", "pasta", "sache"
        );

        if (!formasValidas.contains(formaFarmaceutica.trim().toLowerCase())) {
            throw new IllegalArgumentException("Forma farmacêutica inválida.");
        }
        this.formaFarmaceutica = formaFarmaceutica;
    }

    public double getValorUnit() {
        return valorUnit;
    }

    public void setValorUnit(double valorUnit) {
        if (valorUnit <= 0) {
            throw new IllegalArgumentException("O valor unitário deve ser maior que zero.");
        }
        this.valorUnit = valorUnit;
    }

    public LocalDate getDataValidade() {
        return dataValidade;
    }

    public void setDataValidade(YearMonth dataValidade) {
        LocalDate validade = dataValidade.atDay(1);
        if (validade.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Data inválida! Não pode ser anterior à data atual.");
        }
        if (dataFabricacao != null && validade.isBefore(dataFabricacao)) {
            throw new IllegalArgumentException("Data inválida! Não pode ser anterior à data de fabricação.");
        }

        this.dataValidade = validade;

    }    

    public LocalDate getDataFabricacao() {
        return dataFabricacao;
    }

   public void setDataFabricacao(YearMonth dataFabricacao) {
        LocalDate fabricacao = dataFabricacao.atDay(1); 
        if (fabricacao.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Data inválida! Não pode ser posterior à data atual.");
        }
        if (dataValidade != null && fabricacao.isAfter(dataValidade)) {
            throw new IllegalArgumentException("Data inválida! Não pode ser posterior à data de validade.");
        }

        this.dataFabricacao = fabricacao;
    }

    public boolean isValido() {
        if (dataValidade == null || dataFabricacao == null) {
            return false; 
        }
   
        if (dataValidade.isBefore(LocalDate.now())) {
            return false; 
        }
    
        if (dataFabricacao.isAfter(dataValidade)) {
            return false; 
        }
    
        return true; 
    }
    
    public TipoReceita getTipoReceita() {
        return tipoReceita;
    }

    public void setTipoReceita(TipoReceita tipoReceita) {
        this.tipoReceita = tipoReceita;
    }

    public int getQnt() {
        return qnt;
    }

    public void setQnt(int qnt) {
        if (qnt >= 0) {
            this.qnt = qnt;
        } else {
            throw new IllegalArgumentException("Quantidade não pode ser negativa");
        }
    }

    public Tipo getTipo() {
        return tipo;
    }

    public void setTipo(Tipo tipo) {
        this.tipo = tipo;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        if (categoria == null) {
            throw new IllegalArgumentException("A categoria não pode ser nula.");
        }
        this.categoria = categoria;
    }

    public Fabricante getFabricante() {
        return fabricante;
    }
    
    public void setFabricante(Fabricante fabricante) {
        if (fabricante == null) {
            throw new IllegalArgumentException("O fabricante não pode ser nulo.");
        }
        this.fabricante = fabricante;
    }

    public Fornecedor getFornecedor() {
        return fornecedor;
    }
    
    public void setFornecedor(Fornecedor fornecedor) {
        if (fornecedor == null) {
            throw new IllegalArgumentException("O fornecedor não pode ser nulo.");
        }
        this.fornecedor = fornecedor;
    }
    
    public Funcionario getFuncionario() {
        return funcionario;
    }
    
    public void setFuncionario(Funcionario funcionario) {
        if (funcionario == null) {
            throw new IllegalArgumentException("O funcionário não pode ser nulo.");
        }
        this.funcionario = funcionario;
    }
}
