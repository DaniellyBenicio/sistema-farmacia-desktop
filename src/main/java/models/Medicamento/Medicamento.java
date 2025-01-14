package models.Medicamento;

import java.time.LocalDate;

import models.Funcionario.Funcionario;

//basico de getters e setters, falta validar, ver a questão do desconto, data de fabricação e o enum

public class Medicamento {
    private int id;
    private String nome;
    private double valorUnit;
    private LocalDate dataValidade;
    private TipoReceita tipoReceita;
    private String formaFarmaceutica;
    private String dosagem;
    private int qnt;
    private Funcionario funcionario;

    public Medicamento(){};

    public Medicamento(String nome, double valorUnit, LocalDate dataValidade, TipoReceita tipoReceita, String formaFarmaceutica, String dosagem, int qnt, Funcionario funcionario){
        this.nome = nome;
        this.valorUnit = valorUnit;
        this.dataValidade = dataValidade;
        this.tipoReceita = tipoReceita;
        this.formaFarmaceutica = formaFarmaceutica;
        this.dosagem = dosagem;
        this.qnt = qnt;
        this.funcionario = funcionario;
    };
    
    public enum TipoReceita {
        AZUL, COMUM, ESPECIAL;
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

    public double getValorUnit() {
        return valorUnit;
    }

    public void setValorUnit(double valorUnit) {
        this.valorUnit = valorUnit;
    }

    public LocalDate getDataValidade() {
        return dataValidade;
    }

    public void setDataValidade(LocalDate dataValidade) {
        this.dataValidade = dataValidade;
    }

    public TipoReceita getTipoReceita() {
        return tipoReceita;
    }

    public void setTipoReceita(TipoReceita tipoReceita) {
        this.tipoReceita = tipoReceita;
    }

    public String getFormaFarmaceutica() {
        return formaFarmaceutica;
    }

    public void setFormaFarmaceutica(String formaFarmaceutica) {
        this.formaFarmaceutica = formaFarmaceutica;
    }

    public String getDosagem() {
        return dosagem;
    }

    public void setDosagem(String dosagem) {
        this.dosagem = dosagem;
    }

    public int getQnt() {
        return qnt;
    }

    public void setQnt(int qnt) {
        this.qnt = qnt;
    }

    public Funcionario getFuncionario() {
        return funcionario;
    }

    public void setFuncionario(Funcionario funcionario) {
        this.funcionario = funcionario;
    }
}
