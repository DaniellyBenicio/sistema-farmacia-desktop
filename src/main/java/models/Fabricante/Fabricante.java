package models.Fabricante;

public class Fabricante {
    private int id;
    private String nome;

    public Fabricante (int id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public Fabricante () {};

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
        if (nome == null || nome.trim().isEmpty() || !nome.matches("^[\\p{L}\\s]+$")){
            throw new IllegalArgumentException("O nome deve conter apenas letras e espaços, e não pode estar vazio.");
        }
        this.nome = nome;
    }
}
