package BancosDigital;

import Funcoes.Dates;
import java.util.Date;

public class classBaixar {
    private String seuNumero = "";
    private String nome = "";
    private Date vencimento = null;
    private Date pagamento = null;
    private String nossoNumero = "";
    private float multa = 0;
    private float juros = 0;
    private float valor = 0;
    private String atrasado = "";

    public classBaixar() {}
    public classBaixar(String seuNumero, String nome, Date vencimento, Date pagamento,
                       String nossoNumero, float multa, float juros, float valor) {
        this.seuNumero = seuNumero;
        this.nome = nome;
        this.vencimento = vencimento;
        this.pagamento = pagamento;
        this.nossoNumero = nossoNumero;
        this.multa = multa;
        this.juros = juros;
        this.valor = valor;
    }

    public String getSeuNumero() { return seuNumero; }
    public void setSeuNumero(String seuNumero) { this.seuNumero = seuNumero; }

    public Date getVencimento() { return vencimento; }
    public void setVencimento(Date vencimento) { this.vencimento = vencimento; }

    public Date getPagamento() { return pagamento; }
    public void setPagamento(Date pagamento) { this.pagamento = pagamento; }

    public String getNossoNumero() { return nossoNumero; }
    public void setNossoNumero(String nossoNumero) { this.nossoNumero = nossoNumero; }

    public float getMulta() { return multa; }
    public void setMulta(float multa) { this.multa = multa; }

    public float getJuros() { return juros; }
    public void setJuros(float juros) { this.juros = juros; }

    public float getValor() { return valor; }
    public void setValor(float valor) { this.valor = valor; }     

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }        

    public void setAtrasado() { 
        if (Dates.DateDiff(Dates.DIA,this.vencimento, this.pagamento) > 5) {
            this.atrasado = "*";
        } else {
            this.atrasado = "";
        }
    }        
    
    public String getAtrasado() { return atrasado; }
}
