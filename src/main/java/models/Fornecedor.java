package models;

public class Fornecedor{
    private int id;
	private String nome;
    private String cnpj;
    private String email;
    private String telefone;
    private Funcionario funcionario; 
    private Representante representante;

    public Fornecedor(String nome, String cnpj, String email, String telefone, Funcionario funcionario) {
        if (funcionario == null || !"Gerente".equalsIgnoreCase(funcionario.getCargo().getNome())) {
            throw new IllegalArgumentException("Apenas Gerente pode cadastrar fornecedores.");
        }
        this.nome = nome;  
        this.cnpj = cnpj;   
        this.email = email;   
        this.telefone = telefone;
        this.funcionario = funcionario;
        this.representante = null; 
    }    

    public Fornecedor() {
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
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome não pode ser vazio.");
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

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        if (cnpj == null || cnpj.length() != 14 || !cnpj.matches("\\d{14}")) {
            throw new IllegalArgumentException("CNPJ inválido. Deve conter 14 dígitos numéricos.");
        }
        this.cnpj = cnpj;
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

    public Funcionario getFuncionario() {
        return funcionario;
    }

    public void setFuncionario(Funcionario funcionario) {
        if (funcionario == null) {
            throw new IllegalArgumentException("Funcionário não pode ser nulo.");
        }
        if (!"Gerente".equalsIgnoreCase(funcionario.getCargo().getNome())) {
            throw new IllegalArgumentException("Apenas Gerente pode cadastrar fornecedores.");
        }
        this.funcionario = funcionario;
    }

    public void setRepresentante(Representante representante) {
        if (representante == null) {
            throw new IllegalArgumentException("Representante não pode ser nulo.");
        }
        if (this.representante != null) {
            throw new IllegalArgumentException("Este fornecedor já tem um representante.");
        }
        representante.setFornecedor(this); 
        this.representante = representante;
    }

    public String getNomeRepresentante() {
        return this.representante != null ? this.representante.getNome() : null;
    }

    public String getTelefoneRepresentante() {
        return (this.representante != null) ? this.representante.getTelefone() : null;
    }

    @Override
    public String toString() {
        return "Fornecedor{" +
               "id=" + id +
               ", nome='" + nome + '\'' +
               ", cnpj='" + cnpj + '\'' +
               ", email='" + email + '\'' +
               ", telefone='" + telefone + '\'' +
               ", funcionario=" + funcionario.getNome() +
               '}';
    }
}