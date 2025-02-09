package models.ProdutoCategoria;

public class produtoCategoria {
    private int produto_id;
    private int categoria_id;   

    public produtoCategoria(int produto_id, int categoria_id){
        this.produto_id = produto_id;
        this.categoria_id = categoria_id;
    }

    public int getProduto_id() {
        return produto_id;
    }

    public void setProduto_id(int produto_id) {
        if (produto_id <= 0) {
            throw new IllegalArgumentException("O ID do produto deve ser maior que zero.");
        }
        this.produto_id = produto_id;
    }

    public int getCategoria_id() {
        return categoria_id;
    }

    public void setCategoria_id(int categoria_id) {
        if (categoria_id <= 0) {
            throw new IllegalArgumentException("O ID da categoria deve ser maior que zero.");
        }
        this.categoria_id = categoria_id;
    } 
}
