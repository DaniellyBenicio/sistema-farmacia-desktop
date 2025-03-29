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
        this.nome = nome;
    }
}
