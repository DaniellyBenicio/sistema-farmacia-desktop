package models.Venda;

import java.math.BigDecimal;
import java.time.LocalDateTime;

    public class Venda {
    private int id;
    private Integer cliente_id;
    private int funcionario_id; 
    private BigDecimal valorTotal;
    private BigDecimal desconto;
    private FormaPagamento formaPagamento;
    private LocalDateTime data;

    public Venda(){};

    public Venda(Integer cliente_id, int funcionario_id, BigDecimal valorTotal, BigDecimal desconto, FormaPagamento formaPagamento, LocalDateTime data) {
        this.cliente_id = cliente_id;
        this.funcionario_id = funcionario_id;
        this.valorTotal = valorTotal;
        this.desconto = desconto;
        this.formaPagamento = formaPagamento;
        this.data = data;
    }

    public enum FormaPagamento {
        DINHEIRO, CARTAO_CREDITO, CARTAO_DEBITO, PIX
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

    public int getCliente_id() {
        return cliente_id;
    }
    
    public void setClienteId(Integer cliente_id) {
        if (cliente_id != null && cliente_id <= 0) {
            throw new IllegalArgumentException("O id do cliente deve ser um valor positivo ou nulo.");
        }
        this.cliente_id = cliente_id;
    }

    public int getFuncionario_id() {
        return funcionario_id;
    }

    public void setFuncionarioId(int funcionario_id) {
        if (funcionario_id <= 0) {
            throw new IllegalArgumentException("ID do funcionário deve ser um valor positivo.");
        }
        this.funcionario_id = funcionario_id;
    }
    
    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        if (valorTotal == null || valorTotal.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Valor total não pode ser nulo ou negativo.");
        }
        this.valorTotal = valorTotal;
    }

    public BigDecimal getDesconto() {
        return desconto;
    }

    public void setDesconto(BigDecimal desconto) {
        if (desconto == null || desconto.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Desconto não pode ser nulo ou negativo.");
        }
        if (desconto.compareTo(valorTotal) > 0) {
            throw new IllegalArgumentException("Desconto não pode ser maior que o valor total.");
        }
        this.desconto = desconto;
    }

    public FormaPagamento getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(FormaPagamento formaPagamento) {
        if (formaPagamento == null) {
            throw new IllegalArgumentException("Forma de pagamento não pode ser nula.");
        }
        this.formaPagamento = formaPagamento;
    }     

    public LocalDateTime getData() {
        return data;
    }
    
    public void setData(LocalDateTime data) {
        if (data == null || data.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("Data não pode ser nula ou futura.");
        }
        this.data = data;
    }

}

