package models.ItemVenda;

import java.math.BigDecimal;
import models.Medicamento.Medicamento;
import models.Produto.Produto;

public class ItemVenda {
    private int id;
    private int venda_id;
    private Produto produto;
    private Medicamento medicamento;
    private int qnt;
    private BigDecimal precoUnit;
    private BigDecimal subtotal;
    private BigDecimal desconto;

    public ItemVenda() {}

    public ItemVenda(int venda_id, Produto produto, Medicamento medicamento, int qnt, BigDecimal precoUnit, BigDecimal desconto) {
        this.id = id;
        this.venda_id = venda_id;
        this.produto = produto;
        this.medicamento = medicamento;
        this.qnt = qnt;
        this.precoUnit = precoUnit;
        this.subtotal = precoUnit.multiply(BigDecimal.valueOf(qnt));
        this.desconto = desconto != null ? desconto : BigDecimal.ZERO;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("O id deve ser um valor positivo.");
        }
        this.id = id;
    }

    public int getVendaId() {
        return venda_id;
    }

    public void setVendaId(int venda_id) {
        if (venda_id <= 0) {
            throw new IllegalArgumentException("O id da venda deve ser um valor positivo.");
        }
        this.venda_id = venda_id;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        if (produto == null && medicamento == null) {
            throw new IllegalArgumentException("Pelo menos um dos campos (produto ou medicamento) deve ser preenchido.");
        }
        this.produto = produto;
    }

    public Medicamento getMedicamento() {
        return medicamento;
    }

    public void setMedicamento(Medicamento medicamento) {
        if (produto == null && medicamento == null) {
            throw new IllegalArgumentException("Pelo menos um dos campos (produto ou medicamento) deve ser preenchido.");
        }
        this.medicamento = medicamento;
    }
    
    public int getQnt() {
        return qnt;
    }

    public void setQnt(int qnt) {
        if (qnt <= 0) {
            throw new IllegalArgumentException("A quantidade deve ser um valor positivo.");
        }
        this.qnt = qnt;
        atualizarSubtotal();
    }  

    public BigDecimal getPrecoUnit() {
        return precoUnit;
    }

    public void setPrecoUnit(BigDecimal precoUnit) {
        if (precoUnit == null || precoUnit.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("O preço unitário não pode ser nulo ou negativo.");
        }
        this.precoUnit = precoUnit;
        atualizarSubtotal();
    }   

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public BigDecimal getDesconto() {
        return desconto;
    }

    public void setDesconto(BigDecimal desconto) {
        if (desconto == null || desconto.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("O desconto não pode ser nulo ou negativo.");
        }
        this.desconto = desconto;
        atualizarSubtotal();
    }   

    private void atualizarSubtotal() {
        BigDecimal precoComDesconto = precoUnit.multiply(BigDecimal.valueOf(qnt)).subtract(desconto);
        this.subtotal = precoComDesconto.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : precoComDesconto;
    }

}