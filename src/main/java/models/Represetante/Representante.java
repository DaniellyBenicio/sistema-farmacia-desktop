package models.Represetante;

import models.Fornecedor.Fornecedor;

public class Representante {
    private String nome;
    private String telefone;
    private Fornecedor fornecedor; 

    public Representante(String nome, String telefone, Fornecedor fornecedor) {
        this.nome = nome;
        this.telefone = telefone;
        this.fornecedor = fornecedor;
    }

    public Representante() {
        super();
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do representante não pode ser vazio.");
        }
        this.nome = nome;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        // Verificação de comprimento e se contém apenas dígitos
        if (telefone == null || !telefone.matches("\\d{11}")) {
            throw new IllegalArgumentException("Telefone inválido. Deve conter 11 dígitos numéricos.");
        }
        this.telefone = telefone;
    }

    public Fornecedor getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(Fornecedor fornecedor) {
        if (fornecedor == null) {
            throw new IllegalArgumentException("Fornecedor não pode ser nulo.");
        }
        this.fornecedor = fornecedor;
    }

    @Override
    public String toString() {
        return "Representante{" +
                "nome='" + nome + '\'' +
                ", telefone='" + telefone + '\'' +
                ", fornecedor=" + fornecedor.getNome() + 
                '}';
    }
}