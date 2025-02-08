package models.Categoria;

public class Categoria {
    private int id;
    private String nome;

    public Categoria (int id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public Categoria () {};

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
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome não pode estar vazio.");
        }
    
        if (!nome.matches("^[\\p{L}\\s-()\\/]+$")) {
            throw new IllegalArgumentException("O nome deve conter apenas letras, espaços e caracteres especiais permitidos, e não pode conter números.");
        }
        
        this.nome = nome;
    }
}
