package models.Pagamento;

import java.math.BigDecimal;

public class Pagamento {
    private int id;
    private int venda_id;
    private FormaPagamento formaPagamento;
    private BigDecimal valorPago;

    public enum FormaPagamento {
        DINHEIRO, CARTAO_CREDITO, CARTAO_DEBITO, PIX
    }

    public Pagamento() {
    }

    public Pagamento(int venda_id, FormaPagamento formaPagamento, BigDecimal valorPago) {
        setVendaId(venda_id); 
        setFormaPagamento(formaPagamento);
        setValorPago(valorPago);
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

    public FormaPagamento getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(FormaPagamento formaPagamento) {
        if (formaPagamento == null) {
            throw new IllegalArgumentException("A forma de pagamento não pode ser nula.");
        }
        this.formaPagamento = formaPagamento;
    }

    public BigDecimal getValorPago() {
        return valorPago;
    }

    public void setValorPago(BigDecimal valorPago) {
        if (valorPago == null) {
            throw new IllegalArgumentException("O valor pago não pode ser nulo.");
        }
        if (valorPago.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor pago deve ser maior que zero.");
        }
        this.valorPago = valorPago;
    }
}