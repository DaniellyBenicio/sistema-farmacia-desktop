package models.Medicamento;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
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
    private List<Fabricante> fabricantes; 
    private List<Fornecedor> fornecedores; 
    private List<Funcionario> funcionarios;

    public Medicamento(){};

    public Medicamento(String nome, String dosagem, String formaFarmaceutica, double valorUnit, LocalDate dataValidade, LocalDate dataFabricacao, TipoReceita tipoReceita, int qnt, Tipo tipo, Categoria categoria, List<Fabricante> fabricantes, List<Fornecedor> fornecedores, List<Funcionario> funcionarios){
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
        this.fabricantes = fabricantes != null ? fabricantes : new ArrayList<>();
        this.fornecedores = fornecedores != null ? fornecedores : new ArrayList<>();
        this.funcionarios = funcionarios != null ? funcionarios : new ArrayList<>();
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

    public List<Fabricante> getFabricantes() {
        return fabricantes;
    }

    public void setFabricantes(List<Fabricante> fabricantes) {
        if (fabricantes != null) {
            this.fabricantes = fabricantes;
        } else {
            throw new IllegalArgumentException("A lista de fabricantes não pode ser nula.");
        }
    }

    public List<Fornecedor> getFornecedores() {
        return fornecedores;
    }

    public void setFornecedores(List<Fornecedor> fornecedores) {
        if (fornecedores != null) {
            this.fornecedores = fornecedores;
        } else {
            throw new IllegalArgumentException("A lista de fornecedores não pode ser nula.");
        }
    }

    public List<Funcionario> getFuncionarios() {
        return funcionarios;
    }

    public void setFuncionarios(List<Funcionario> funcionarios) {
        if (funcionarios != null) {
            this.funcionarios = funcionarios;
        } else {
            throw new IllegalArgumentException("A lista de funcionários não pode ser nula.");
        }
    }
}
