package Relatorios;

import java.math.BigDecimal;
import java.util.Date;

public class classExtratoHistorico {
    private Date dtpagamento;
    private String descricao;
    private String referencia;
    private BigDecimal credito;
    private BigDecimal debito;
    private BigDecimal saldo;

    public classExtratoHistorico(Date dtpagamento, String descricao, String referencia, BigDecimal credito, BigDecimal debito, BigDecimal saldo) {
        this.dtpagamento = dtpagamento;
        this.descricao = descricao;
        this.referencia = referencia;
        this.credito = credito;
        this.debito = debito;
        this.saldo = saldo;
    }

    public Date getDtpagamento() {
        return dtpagamento;
    }

    public void setDtpagamento(Date dtpagamento) {
        this.dtpagamento = dtpagamento;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public BigDecimal getCredito() {
        return credito;
    }

    public void setCredito(BigDecimal credito) {
        this.credito = credito;
    }

    public BigDecimal getDebito() {
        return debito;
    }

    public void setDebito(BigDecimal debito) {
        this.debito = debito;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }    
}