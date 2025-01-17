package models.Medicamento;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import models.Funcionario.Funcionario;
import models.Fornecedor.Fornecedor;
import models.Categoria.Categoria;

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
    private Funcionario funcionario;
    private Fornecedor fornecedor;
    private Categoria categoria;

    public Medicamento(){};

    public Medicamento(String nome, String dosagem, String formaFarmaceutica, double valorUnit, LocalDate dataValidade, LocalDate dataFabricacao, TipoReceita tipoReceita, int qnt, Tipo tipo, Funcionario funcionario, Fornecedor fornecedor, Categoria categoria){
        this.nome = nome;
        this.dosagem = dosagem;
        this.formaFarmaceutica = formaFarmaceutica;
        this.valorUnit = valorUnit;
        this.dataValidade = dataValidade;
        this.dataFabricacao = dataFabricacao;
        this.tipoReceita = tipoReceita;
        this.qnt = qnt;
        this.tipo = tipo;
        this.funcionario = funcionario;
        this.fornecedor = fornecedor;
        this.categoria = categoria;
    };
    
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
            "comprimido", "creme", "pomada", "injeção", "xarope", "solução", "spray", 
            "capsula", "gel", "loção", "gelatina", "supositório", "pó", "emulsão", 
            "colírio", "gotejamento", "aerossol", "spray nasal", "pastilha", 
            "suspensão", "solução oral", "pasta", "sachê"
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

    public void setDataValidade(LocalDate dataValidade) {
        if (dataValidade.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("A data de validade não pode ser anterior à data atual.");
        }
        if (dataFabricacao != null && dataValidade.isBefore(dataFabricacao)) {
            throw new IllegalArgumentException("A data de validade não pode ser anterior à data de fabricação.");
        }

        this.dataValidade = dataValidade;
    }

    public LocalDate getDataFabricacao() {
        return dataFabricacao;
    }

    public void setDataFabricacao(LocalDate dataFabricacao) {
        if (dataFabricacao.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("A data de fabricação não pode ser posterior a atual.");
        }
        if (dataValidade != null && dataFabricacao.isAfter(dataValidade)) {
            throw new IllegalArgumentException("A data de fabricação não pode ser posterior à data de validade.");
        }

        this.dataFabricacao = dataFabricacao;
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

    public Funcionario getFuncionario() {
        return funcionario;
    }

    public void setFuncionario(Funcionario funcionario) {
        if (funcionario == null) {
            throw new IllegalArgumentException("O funcionário não pode ser nulo.");
        }
        this.funcionario = funcionario;
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

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        if (categoria == null) {
            throw new IllegalArgumentException("A categoria não pode ser nula.");
        }
        this.categoria = categoria;
    }
}
