package Bancos;

import java.util.List;

public class cRetorno {
    private String banco;
    private String tipoInsc;
    private String inscr;
    private String tparquivo;
    private String datacredito;
    
    private List<cSegmentoT> segmentot;
    
    private String quantidadereg;
    private String quantidadesimples;
    private String quantidadevinc;
    private String valorvinc;
    
    private String codigolote;
    private String totalreg;

    public cRetorno(String banco, String tipoInsc, String inscr, String tparquivo, String datacredito, List<cSegmentoT> segmentot, String quantidadereg, String quantidadesimples, String quantidadevinc, String valorvinc, String codigolote, String totalreg) {
        this.banco = banco;
        this.tipoInsc = tipoInsc;
        this.inscr = inscr;
        this.tparquivo = tparquivo;
        this.datacredito = datacredito;
        this.segmentot = segmentot;
        this.quantidadereg = quantidadereg;
        this.quantidadesimples = quantidadesimples;
        this.quantidadevinc = quantidadevinc;
        this.valorvinc = valorvinc;
        this.codigolote = codigolote;
        this.totalreg = totalreg;
    }

    public String getBanco() { return banco; }
    public void setBanco(String banco) { this.banco = banco; }

    public String getTipoInsc() { return tipoInsc; }
    public void setTipoInsc(String tipoInsc) { this.tipoInsc = tipoInsc; }

    public String getInscr() { return inscr; }
    public void setInscr(String inscr) { this.inscr = inscr; }

    public String getTparquivo() { return tparquivo; }
    public void setTparquivo(String tparquivo) { this.tparquivo = tparquivo; }

    public String getDatacredito() { return datacredito; }
    public void setDatacredito(String datacredito) { this.datacredito = datacredito; }

    public List<cSegmentoT> getSegmentot() { return segmentot; }
    public void setSegmentot(List<cSegmentoT> segmentot) { this.segmentot = segmentot; }

    public String getQuantidadereg() { return quantidadereg; }
    public void setQuantidadereg(String quantidadereg) { this.quantidadereg = quantidadereg; }

    public String getQuantidadesimples() { return quantidadesimples; }
    public void setQuantidadesimples(String quantidadesimples) { this.quantidadesimples = quantidadesimples; }

    public String getQuantidadevinc() { return quantidadevinc; }
    public void setQuantidadevinc(String quantidadevinc) { this.quantidadevinc = quantidadevinc; }
    
    public String getValorvinc() { return valorvinc; }
    public void setValorvinc(String valorvinc) { this.valorvinc = valorvinc; }
    
    public String getCodigolote() { return codigolote; }
    public void setCodigolote(String codigolote) { this.codigolote = codigolote; }
    
    public String getTotalreg() { return totalreg; }
    public void setTotalreg(String totalreg) { this.totalreg = totalreg; }    
}