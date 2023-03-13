package Bancos;

public class cSegmentoT {
    private String codocor;
    private String nnumero;
    private String dacnnumero;
    private String seunumero;
    private String vencimento;
    private String vrtitulo;
    private String agbaixa;
    private String dacagbaixa;
    private String tpinscpagador;
    private String inscpagador;
    private String nomepagador;
    private String tarifa;
    private String errosrejeicao;
    private String codliquidacao;

    private cSegmentoU segmentou;

    public cSegmentoT(String codocor, String nnumero, String dacnnumero, String seunumero, String vencimento, String vrtitulo, String agbaixa, String dacagbaixa, String tpinscpagador, String inscpagador, String nomepagador, String tarifa, String errosrejeicao, String codliquidacao, cSegmentoU segmentou) {
        this.codocor = codocor;
        this.nnumero = nnumero;
        this.dacnnumero = dacnnumero;
        this.seunumero = seunumero;
        this.vencimento = vencimento;
        this.vrtitulo = vrtitulo;
        this.agbaixa = agbaixa;
        this.dacagbaixa = dacagbaixa;
        this.tpinscpagador = tpinscpagador;
        this.inscpagador = inscpagador;
        this.nomepagador = nomepagador;
        this.tarifa = tarifa;
        this.errosrejeicao = errosrejeicao;
        this.codliquidacao = codliquidacao;
        this.segmentou = segmentou;
    }

    public String getCodocor() { return codocor; }
    public void setCodocor(String codocor) { this.codocor = codocor; }

    public String getNnumero() { return nnumero; }
    public void setNnumero(String nnumero) { this.nnumero = nnumero; }

    public String getDacnnumero() { return dacnnumero; }
    public void setDacnnumero(String dacnnumero) { this.dacnnumero = dacnnumero; }

    public String getSeunumero() { return seunumero; }
    public void setSeunumero(String seunumero) { this.seunumero = seunumero; }

    public String getVencimento() { return vencimento; }
    public void setVencimento(String vencimento) { this.vencimento = vencimento; }

    public String getVrtitulo() { return vrtitulo; }
    public void setVrtitulo(String vrtitulo) { this.vrtitulo = vrtitulo; }

    public String getAgbaixa() { return agbaixa; }
    public void setAgbaixa(String agbaixa) { this.agbaixa = agbaixa; }

    public String getDacagbaixa() { return dacagbaixa; }
    public void setDacagbaixa(String dacagbaixa) { this.dacagbaixa = dacagbaixa; }

    public String getTpinscpagador() { return tpinscpagador; }
    public void setTpinscpagador(String tpinscpagador) { this.tpinscpagador = tpinscpagador; }

    public String getInscpagador() { return inscpagador; }
    public void setInscpagador(String inscpagador) { this.inscpagador = inscpagador; }

    public String getNomepagador() { return nomepagador; }
    public void setNomepagador(String nomepagador) { this.nomepagador = nomepagador; }

    public String getTarifa() { return tarifa; }
    public void setTarifa(String tarifa) { this.tarifa = tarifa; }

    public String getErrosrejeicao() { return errosrejeicao; }
    public void setErrosrejeicao(String errosrejeicao) { this.errosrejeicao = errosrejeicao; }

    public String getCodliquidacao() { return codliquidacao; }
    public void setCodliquidacao(String codliquidacao) { this.codliquidacao = codliquidacao; }

    public cSegmentoU getSegmentou() { return segmentou; }
    public void setSegmentou(cSegmentoU segmentou) { this.segmentou = segmentou; }        
}
