package Bancos;

public class cSegmentoU {
    private String codocor;
    private String jurousmulta;
    private String desconto;
    private String abatimento;
    private String valorcred;
    private String valorlanc;
    private String dataocorr;
    private String datacredito;
    private String ocorrpagador;
    private String dataocor;
    private String valorocorr;

    public cSegmentoU(String codocor, String jurousmulta, String desconto, String abatimento, String valorcred, String valorlanc, String dataocorr, String datacredito, String ocorrpagador, String dataocor, String valorocorr) {
        this.codocor = codocor;
        this.jurousmulta = jurousmulta;
        this.desconto = desconto;
        this.abatimento = abatimento;
        this.valorcred = valorcred;
        this.valorlanc = valorlanc;
        this.dataocorr = dataocorr;
        this.datacredito = datacredito;
        this.ocorrpagador = ocorrpagador;
        this.dataocor = dataocor;
        this.valorocorr = valorocorr;
    }

    public String getCodocor() { return codocor; }
    public void setCodocor(String codocor) { this.codocor = codocor; }

    public String getJurousmulta() { return jurousmulta; }
    public void setJurousmulta(String jurousmulta) { this.jurousmulta = jurousmulta; }

    public String getDesconto() { return desconto; }
    public void setDesconto(String desconto) { this.desconto = desconto; }

    public String getAbatimento() { return abatimento; }
    public void setAbatimento(String abatimento) { this.abatimento = abatimento; }

    public String getValorcred() { return valorcred; }
    public void setValorcred(String valorcred) { this.valorcred = valorcred; }

    public String getValorlanc() { return valorlanc; }
    public void setValorlanc(String valorlanc) { this.valorlanc = valorlanc; }

    public String getDataocorr() { return dataocorr; }
    public void setDataocorr(String dataocorr) { this.dataocorr = dataocorr; }

    public String getDatacredito() { return datacredito; }
    public void setDatacredito(String datacredito) { this.datacredito = datacredito; }

    public String getOcorrpagador() { return ocorrpagador; }
    public void setOcorrpagador(String ocorrpagador) { this.ocorrpagador = ocorrpagador; }

    public String getDataocor() { return dataocor; }
    public void setDataocor(String dataocor) { this.dataocor = dataocor; }

    public String getValorocorr() { return valorocorr;  }
    public void setValorocorr(String valorocorr) { this.valorocorr = valorocorr; }                
}
