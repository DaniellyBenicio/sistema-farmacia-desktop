package models.Venda;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Venda {
    private Integer id; 
    private Integer clienteId;
    private Integer funcionarioId;
    private BigDecimal valorTotal;
    private BigDecimal desconto;
    private LocalDateTime data;

    public Venda() {}

    public Venda(Integer clienteId, Integer funcionarioId, BigDecimal valorTotal, BigDecimal desconto, LocalDateTime data) {
        this.clienteId = clienteId;
        this.funcionarioId = funcionarioId;
        this.valorTotal = valorTotal;
        this.desconto = desconto;
        this.data = data;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        if (id != null && id <= 0) {
            throw new IllegalArgumentException("O id deve ser um valor positivo.");
        }
        this.id = id;
    }

    public Integer getClienteId() {
        return clienteId;
    }

    public void setClienteId(Integer clienteId) {
        if (clienteId != null && clienteId <= 0) {
            throw new IllegalArgumentException("O ID do cliente deve ser um valor positivo.");
        }
        this.clienteId = clienteId;
    }

    public Integer getFuncionarioId() {
        return funcionarioId;
    }

    public void setFuncionarioId(Integer funcionarioId) {
        if (funcionarioId != null && funcionarioId <= 0) {
            throw new IllegalArgumentException("O ID do funcionário deve ser um valor positivo.");
        }
        this.funcionarioId = funcionarioId;
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
        if (valorTotal != null && desconto.compareTo(valorTotal) > 0) {
            throw new IllegalArgumentException("Desconto não pode ser maior que o valor total.");
        }
        this.desconto = desconto;
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        if (data == null) {
            this.data = LocalDateTime.now();
        } else if (data.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("Data não pode ser futura.");
        } else {
            this.data = data;
        }
    }
}