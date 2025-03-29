package models.Cliente;

import models.Funcionario.Funcionario;
import utils.Criptografia;

public class Cliente {
    private int id;
    private String nome;
    private String cpf;
    private String telefone;
    private String rua;
    private String numCasa;
    private String bairro;
    private String cidade;
    private String estado;
    private String pontoReferencia;
    private Funcionario funcionario;

    public Cliente(String nome, String cpf, String telefone, String rua, String numCasa, String bairro, String cidade,
            String estado, String pontoReferencia, Funcionario funcionario) throws Exception {
        this.nome = nome;
        this.cpf = Criptografia.criptografar(cpf);
        this.telefone = telefone;
        this.rua = rua;
        this.numCasa = numCasa;
        this.bairro = bairro;
        this.cidade = cidade;
        this.estado = estado;
        this.pontoReferencia = pontoReferencia;
        this.funcionario = funcionario;
    }

    public Cliente() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID inválido.");
        }
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        if (nome == null || nome.trim().isEmpty() || !nome.matches("[a-zA-ZÀ-ÿ\\s-]+")) {
            throw new IllegalArgumentException(
                    "Nome inválido! O nome não pode ser vazio e deve conter apenas letras, espaços e hífens.");
        }
        this.nome = nome;
    }

    public String getCpf() {
        try {
            return Criptografia.descriptografar(cpf);
        } catch (Exception e) {
            System.err.println("Erro ao descriptografar CPF: " + e.getMessage());
            throw new RuntimeException("Erro ao obter CPF: " + e.getMessage(), e);
        }
    }

    public void setCpf(String cpf) throws Exception {
        if (cpf == null || cpf.trim().isEmpty()) {
            throw new IllegalArgumentException("CPF não pode ser vazio.");
        }
        this.cpf = Criptografia.criptografar(cpf);
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        if (telefone == null || telefone.trim().isEmpty()) {
            throw new IllegalArgumentException("Telefone não pode ser vazio.");
        }
        if (!telefone.matches("\\d{11}")) {
            throw new IllegalArgumentException("Telefone inválido. Deve ter 11 números.");
        }
        this.telefone = telefone;
    }

    public String getRua() {
        return rua;
    }

    public void setRua(String rua) {
        if (rua == null || rua.trim().isEmpty()) {
            throw new IllegalArgumentException("A rua não pode ser nula ou vazia.");
        }
        if (!rua.matches("[a-zA-ZÀ-ÿ0-9\\s-]+")) {
            throw new IllegalArgumentException("A rua não pode conter caracteres especiais.");
        }
        this.rua = rua;
    }

    public String getNumCasa() {
        return numCasa;
    }

    public void setNumCasa(String numCasa) {
        this.numCasa = numCasa;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        if (bairro == null || bairro.trim().isEmpty()) {
            throw new IllegalArgumentException("Bairro não pode ser vazio.");
        }
        if (!bairro.matches("[a-zA-ZÀ-ÿ0-9\\s-]+")) {
            throw new IllegalArgumentException("O bairro não pode conter caracteres especiais.");
        }
        this.bairro = bairro;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        if (cidade == null || cidade.trim().isEmpty()) {
            throw new IllegalArgumentException("Cidade não pode ser vazia.");
        }
        if (!cidade.matches("[a-zA-ZÀ-ÿ0-9\\s-]+")) {
            throw new IllegalArgumentException("A cidade não pode conter caracteres especiais.");
        }
        this.cidade = cidade;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        if (estado == null || estado.trim().isEmpty()) {
            throw new IllegalArgumentException("Estado não pode ser vazio.");
        }
        this.estado = estado;
    }

    public String getPontoReferencia() {
        return pontoReferencia;
    }

    public void setPontoReferencia(String pontoReferencia) {
        this.pontoReferencia = pontoReferencia;
    }

    public Funcionario getFuncionario() {
        return funcionario;
    }

    public void setFuncionario(Funcionario funcionario) {
        if (funcionario == null) {
            throw new IllegalArgumentException("Funcionário não pode ser nulo.");
        }
        this.funcionario = funcionario;
    }

    @Override
    public String toString() {
        return "Cliente [id=" + id +
                ", nome=" + nome +
                ", cpf=" + cpf +
                ", telefone=" + telefone +
                ", numCasa=" + numCasa +
                ", bairro=" + bairro +
                ", cidade=" + cidade +
                ", estado=" + estado +
                ", pontoReferencia=" + pontoReferencia +
                ", funcionario=" + funcionario.getNome() +
                "]";
    }
}
