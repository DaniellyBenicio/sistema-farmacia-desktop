package models.Cargo;

public class Cargo {
    private int id;
    private String nome;

    public Cargo(int id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public Cargo() {}
    
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
        if (nome == null || nome.trim().isEmpty() || !nome.matches("^[A-Za-zÀ-ÿ]+(\\s[A-Za-zÀ-ÿ]+)*$")) {
            throw new IllegalArgumentException("O nome deve conter apenas letras e espaços, e não pode estar vazio.");
        }
        this.nome = nome;
    }

    @Override
    public String toString() {
        return "Cargo{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                '}';
    }
}
