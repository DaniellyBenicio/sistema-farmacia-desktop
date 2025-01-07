package models.Funcionario;

import models.Cargo.Cargo;

public class Funcionario {
    private int id;
	private String nome;
    private String telefone;
    private String email;
    private Cargo cargo;
    private boolean status;
	
    public Funcionario(String nome, String telefone, String email, Cargo cargo, boolean status){
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.cargo = cargo;
        this.status = status;
    }

    public Funcionario(){
		super();
        this.status = true;
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
        if (nome == null || !nome.matches("^[A-Za-zÀ-ÿ\\s]+$")) {
            throw new IllegalArgumentException("O nome deve conter apenas letras e espaços.");
        }
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email == null || !email.matches("^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            throw new IllegalArgumentException("Email inválido.");
        }
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        if (telefone == null || !telefone.matches("\\d{11}")) {
            throw new IllegalArgumentException("Telefone inválido. Deve conter 11 dígitos numéricos.");
        }   
        this.telefone = telefone;
    }

    public Cargo getCargo() {
        return cargo;
    }

    public void setCargo(Cargo cargo) {
        if (cargo.getNome() == null || cargo.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Cargo deve ter um nome válido.");
        }
        
        this.cargo = cargo;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }


    @Override
    public String toString() {
        return "Funcionario{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", telefone='" + telefone + '\'' +
                ", cargo=" + cargo.getNome() +
                ", status=" + status +
                '}';
    }
}