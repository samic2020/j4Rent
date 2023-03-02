/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BancosDigital;

import static BancosDigital.bancos.fmtCaracteres;
import static BancosDigital.bancos.fmtNumero;
import static BancosDigital.bancos.getOnlyDigits;
import Funcoes.Dates;

/**
 *
 * @author desenvolvimento-pc
 */
public class classInter {
    public final String inter_NUNDIASAGENDA_TRINTA = "TRINTA";
    public final String inter_NUNDIASAGENDA_SESSENTA = "SESSENTA";

    public final String inter_PAGADOR_TIPOPESSOA_FISICA = "FISICA";
    public final String inter_PAGADOR_TIPOPESSOA_JURIDICA = "JURIDICA";

    private static final int inter_PAGADOR_NOME_SIZE = 100;
    private static final int inter_PAGADOR_ENDERECO_SIZE = 90;
    private static final int inter_PAGADOR_NUMERO_SIZE = 10;
    private static final int inter_PAGADOR_COMPLEMENTO_SIZE = 30;
    private static final int inter_PAGADOR_BAIRRO_SIZE = 60;
    private static final int inter_PAGADOR_CIDADE_SIZE = 60;
    private static final int inter_PAGADOR_UF_SIZE = 2;
    private static final int inter_PAGADOR_CEP_SIZE = 8;
    private static final int inter_PAGADOR_CPFCNPJ_SIZE = 14;
    private static final int inter_PAGADOR_EMAIL_SIZE = 50;
    private static final int inter_PAGADOR_DDD_SIZE = 2;
    private static final int inter_PAGADOR_TELEFONE_SIZE = 9;

    private static final int inter_PAGADOR_MENSAGEM_SIZE = 78;
    private static final int inter_PAGADOR_SEUNUMERO = 15;

    public final String inter_DESCONTO_NAOTEMDESCONTO                   = "NAOTEMDESCONTO";
    public final String inter_DESCONTO_VALORFIXODATAINFORMADA           = "VALORFIXODATAINFORMADA";
    public final String inter_DESCONTO_PERCENTUALDATAINFORMADA          = "PERCENTUALDATAINFORMADA";
    public final String inter_DESCONTO_VALORANTECIPACAODIACORRIDO       = "VALORANTECIPACAODIACORRIDO";
    public final String inter_DESCONTO_VALORANTECIPACAODIAUTIL          = "VALORANTECIPACAODIAUTIL";
    public final String inter_DESCONTO_PERCENTUALVALORNOMINALDIACORRIDO = "PERCENTUALVALORNOMINALDIACORRIDO";
    public final String inter_DESCONTO_PERCENTUALVALORNOMINALDIAUTIL    = "PERCENTUALVALORNOMINALDIAUTIL";

    public final String inter_MULTA_NAOTEMMULTA = "NAOTEMMULTA";
    public final String inter_MULTA_VALORFIXO   = "VALORFIXO";
    public final String inter_MULTA_PERCENTUAL  = "PERCENTUAL";

    public final String inter_MORA_VALORDIA   = "VALORDIA";
    public final String inter_MORA_TAXAMENSAL = "TAXAMENSAL";
    public final String inter_MORA_ISENTO     = "ISENTO";

    private static final String inter_DATA_FORMAT = "yyyy-MM-dd";       
    
    private String tipopessoa;
    private String cnpjCpf;
    private String nome;
    private String endereco;
    private String numero;
    private String complemento;
    private String bairro;
    private String cidade;
    private String uf;
    private String cep;
    
    private String dataEmissao;
    private String seuNumero;
    private String dataLimite;
    private String dataVencimento;
    
    private String mensagem_linha1;
    private String mensagem_linha2;
    private String mensagem_linha3;
    private String mensagem_linha4;
    private String mensagem_linha5;
    
    private String desconto1_codigoDesconto;
    private String desconto1_taxa;
    private String desconto1_valor;
    private String desconto1_data;
    
    private String desconto2_codigoDesconto;
    private String desconto2_taxa;
    private String desconto2_valor;
    private String desconto2_data;
    
    private String desconto3_codigoDesconto;
    private String desconto3_taxa;
    private String desconto3_valor;
    private String desconto3_data;
    
    private String valorNominal;
    private String valorAbatimento;
    
    private String multa_data;
    private String multa_codigoMulta;
    private String multa_valor;
    private String multa_taxa;
    
    private String mora_data;
    private String mora_codigoMora;
    private String mora_valor;
    private String mora_taxa;
    
    private String cnpjCPFBeneficiario;
    private int numDiasAgenda;

    public classInter() {}
    
    public classInter(String tipopessoa, String cnpjCpf, String nome, String endereco, String numero, String complemento, String bairro, String cidade, String uf, String cep, String dataEmissao, String seuNumero, String dataLimite, String dataVencimento, String mensagem_linha1, String mensagem_linha2, String mensagem_linha3, String mensagem_linha4, String mensagem_linha5, String desconto1_codigoDesconto, String desconto1_taxa, String desconto1_valor, String desconto1_data, String desconto2_codigoDesconto, String desconto2_taxa, String desconto2_valor, String desconto2_data, String desconto3_codigoDesconto, String desconto3_taxa, String desconto3_valor, String desconto3_data, String valorNominal, String valorAbatimento, String multa_data, String multa_codigoMulta, String multa_valor, String multa_taxa, String mora_data, String mora_codigoMora, String mora_valor, String mora_taxa, String cnpjCPFBeneficiario, int numDiasAgenda) {
        this.tipopessoa = tipopessoa;
        this.cnpjCpf = cnpjCpf;
        this.nome = nome;
        this.endereco = endereco;
        this.numero = numero;
        this.complemento = complemento;
        this.bairro = bairro;
        this.cidade = cidade;
        this.uf = uf;
        this.cep = cep;
        this.dataEmissao = dataEmissao;
        this.seuNumero = seuNumero;
        this.dataLimite = dataLimite;
        this.dataVencimento = dataVencimento;
        this.mensagem_linha1 = mensagem_linha1;
        this.mensagem_linha2 = mensagem_linha2;
        this.mensagem_linha3 = mensagem_linha3;
        this.mensagem_linha4 = mensagem_linha4;
        this.mensagem_linha5 = mensagem_linha5;
        this.desconto1_codigoDesconto = desconto1_codigoDesconto;
        this.desconto1_taxa = desconto1_taxa;
        this.desconto1_valor = desconto1_valor;
        this.desconto1_data = desconto1_data;
        this.desconto2_codigoDesconto = desconto2_codigoDesconto;
        this.desconto2_taxa = desconto2_taxa;
        this.desconto2_valor = desconto2_valor;
        this.desconto2_data = desconto2_data;
        this.desconto3_codigoDesconto = desconto3_codigoDesconto;
        this.desconto3_taxa = desconto3_taxa;
        this.desconto3_valor = desconto3_valor;
        this.desconto3_data = desconto3_data;
        this.valorNominal = valorNominal;
        this.valorAbatimento = valorAbatimento;
        this.multa_data = multa_data;
        this.multa_codigoMulta = multa_codigoMulta;
        this.multa_valor = multa_valor;
        this.multa_taxa = multa_taxa;
        this.mora_data = mora_data;
        this.mora_codigoMora = mora_codigoMora;
        this.mora_valor = mora_valor;
        this.mora_taxa = mora_taxa;
        this.cnpjCPFBeneficiario = cnpjCPFBeneficiario;
        this.numDiasAgenda = numDiasAgenda;
    }

    public String getTipopessoa() {
        return tipopessoa;
    }

    public void setTipopessoa(String tipopessoa) {
        this.tipopessoa = tipopessoa;
    }

    public String getCnpjCpf() {
        return cnpjCpf;
    }

    public void setCnpjCpf(String cnpjCpf) {
        String tVar = getOnlyDigits(cnpjCpf);
        int tSize = (tVar.length() <= 11 ? 11 : 14);
        this.cnpjCpf = fmtNumero(tVar, tSize);
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = fmtCaracteres(nome, inter_PAGADOR_NOME_SIZE);
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = fmtCaracteres(endereco, inter_PAGADOR_ENDERECO_SIZE);
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = fmtCaracteres(numero, inter_PAGADOR_NUMERO_SIZE);
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = fmtCaracteres(complemento, inter_PAGADOR_COMPLEMENTO_SIZE);
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = fmtCaracteres(bairro, inter_PAGADOR_BAIRRO_SIZE);
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = fmtCaracteres(cidade, inter_PAGADOR_CIDADE_SIZE);
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = fmtCaracteres(uf, inter_PAGADOR_UF_SIZE);
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = fmtCaracteres(getOnlyDigits(cep), inter_PAGADOR_CEP_SIZE);
    }

    public String getDataEmissao() {
        return dataEmissao;
    }

    public void setDataEmissao(String dataEmissao) {
        this.dataEmissao = Dates.StringtoString(dataEmissao, "dd-MM-yyyy", inter_DATA_FORMAT);
    }

    public String getSeuNumero() {
        return seuNumero;
    }

    public void setSeuNumero(String seuNumero) {
        this.seuNumero = fmtCaracteres(seuNumero, inter_PAGADOR_SEUNUMERO);
    }

    public String getDataLimite() {
        return dataLimite;
    }

    public void setDataLimite(String dataLimite) {
        this.dataLimite = fmtCaracteres(dataLimite, "SESSENTA".length());
    }

    public String getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(String dataVencimento) {
        this.dataVencimento = Dates.StringtoString(dataVencimento, "dd-MM-yyyy", inter_DATA_FORMAT);
    }

    public String getMensagem_linha1() {
        return mensagem_linha1;
    }

    public void setMensagem_linha1(String mensagem_linha1) {
        if (mensagem_linha1 == null || mensagem_linha1.equalsIgnoreCase("")) this.mensagem_linha1 = ""; else this.mensagem_linha1 = fmtCaracteres(mensagem_linha1, inter_PAGADOR_MENSAGEM_SIZE);
    }

    public String getMensagem_linha2() {
        return mensagem_linha2;
    }

    public void setMensagem_linha2(String mensagem_linha2) {
        if (mensagem_linha2 == null || mensagem_linha2.equalsIgnoreCase("")) this.mensagem_linha2 = ""; else this.mensagem_linha2 = fmtCaracteres(mensagem_linha2, inter_PAGADOR_MENSAGEM_SIZE);
    }

    public String getMensagem_linha3() {
        return mensagem_linha3;
    }

    public void setMensagem_linha3(String mensagem_linha3) {
        if (mensagem_linha3 == null || mensagem_linha3.equalsIgnoreCase("")) this.mensagem_linha3 = ""; else this.mensagem_linha3 = fmtCaracteres(mensagem_linha3, inter_PAGADOR_MENSAGEM_SIZE);
    }

    public String getMensagem_linha4() {
        return mensagem_linha4;
    }

    public void setMensagem_linha4(String mensagem_linha4) {
        if (mensagem_linha4 == null || mensagem_linha4.equalsIgnoreCase("")) this.mensagem_linha4 = ""; else this.mensagem_linha4 = fmtCaracteres(mensagem_linha4, inter_PAGADOR_MENSAGEM_SIZE);
    }

    public String getMensagem_linha5() {
        return mensagem_linha5;
    }

    public void setMensagem_linha5(String mensagem_linha5) {
        if (mensagem_linha5 == null || mensagem_linha5.equalsIgnoreCase("")) this.mensagem_linha5 = ""; else this.mensagem_linha5 = fmtCaracteres(mensagem_linha5, inter_PAGADOR_MENSAGEM_SIZE);
    }

    public String getDesconto1_codigoDesconto() {
        return desconto1_codigoDesconto;
    }

    public void setDesconto1_codigoDesconto(String desconto1_codigoDesconto) {
        this.desconto1_codigoDesconto = fmtCaracteres(desconto1_codigoDesconto, "PERCENTUALVALORNOMINALDIACORRIDO".length());
    }

    public String getDesconto1_taxa() {
        return desconto1_taxa;
    }

    public void setDesconto1_taxa(String desconto1_taxa) {
        this.desconto1_taxa = fmtCaracteres(desconto1_taxa, "PERCENTUALVALORNOMINALDIACORRIDO".length());
    }

    public String getDesconto1_valor() {
        return desconto1_valor;
    }

    public void setDesconto1_valor(String desconto1_valor) {
        this.desconto1_valor = fmtCaracteres(desconto1_valor, desconto1_valor.length());
    }

    public String getDesconto1_data() {
        return desconto1_data;
    }

    public void setDesconto1_data(String desconto1_data) {
        if (desconto1_data.equalsIgnoreCase("") || desconto1_data == null) this.desconto1_data = ""; else this.desconto1_data = Dates.StringtoString(desconto1_data, "dd-MM-yyyy", inter_DATA_FORMAT);
    }

    public String getDesconto2_codigoDesconto() {
        return desconto2_codigoDesconto;
    }

    public void setDesconto2_codigoDesconto(String desconto2_codigoDesconto) {
        this.desconto2_codigoDesconto = fmtCaracteres(desconto2_codigoDesconto, "PERCENTUALVALORNOMINALDIACORRIDO".length());
    }

    public String getDesconto2_taxa() {
        return desconto2_taxa;
    }

    public void setDesconto2_taxa(String desconto2_taxa) {
        this.desconto2_taxa = fmtCaracteres(desconto2_taxa, "PERCENTUALVALORNOMINALDIACORRIDO".length());
    }

    public String getDesconto2_valor() {
        return desconto2_valor;
    }

    public void setDesconto2_valor(String desconto2_valor) {
        this.desconto2_valor = fmtCaracteres(desconto2_valor, desconto2_valor.length());
    }

    public String getDesconto2_data() {
        return desconto2_data;
    }

    public void setDesconto2_data(String desconto2_data) {
        if (desconto2_data.equalsIgnoreCase("") || desconto2_data == null) this.desconto2_data = ""; else this.desconto2_data = Dates.StringtoString(desconto2_data, "dd-MM-yyyy", inter_DATA_FORMAT);
    }

    public String getDesconto3_codigoDesconto() {
        return desconto3_codigoDesconto;
    }

    public void setDesconto3_codigoDesconto(String desconto3_codigoDesconto) {
        this.desconto3_codigoDesconto = fmtCaracteres(desconto3_codigoDesconto, "PERCENTUALVALORNOMINALDIACORRIDO".length());
    }

    public String getDesconto3_taxa() {
        return desconto3_taxa;
    }

    public void setDesconto3_taxa(String desconto3_taxa) {
        this.desconto3_taxa = fmtCaracteres(desconto3_taxa, "PERCENTUALVALORNOMINALDIACORRIDO".length());
    }

    public String getDesconto3_valor() {
        return desconto3_valor;
    }

    public void setDesconto3_valor(String desconto3_valor) {
        this.desconto3_valor = fmtCaracteres(desconto3_valor, desconto3_valor.length());
    }

    public String getDesconto3_data() {
        return desconto3_data;
    }

    public void setDesconto3_data(String desconto3_data) {
        if (desconto3_data.equalsIgnoreCase("") || desconto3_data == null) this.desconto3_data = ""; else this.desconto3_data = Dates.StringtoString(desconto3_data, "dd-MM-yyyy", inter_DATA_FORMAT);
    }

    public String getValorNominal() {
        return valorNominal;
    }

    public void setValorNominal(String valorNominal) {
        this.valorNominal = fmtCaracteres(valorNominal, valorNominal.length());
    }

    public String getValorAbatimento() {
        return valorAbatimento;
    }

    public void setValorAbatimento(String valorAbatimento) {
        this.valorAbatimento = fmtCaracteres(valorAbatimento, valorAbatimento.length());
    }

    public String getMulta_data() {
        return multa_data;
    }

    public void setMulta_data(String multa_data) {
        this.multa_data = Dates.StringtoString(multa_data, "dd-MM-yyyy", inter_DATA_FORMAT);
    }

    public String getMulta_codigoMulta() {
        return multa_codigoMulta;
    }

    public void setMulta_codigoMulta(String multa_codigoMulta) {
        this.multa_codigoMulta = fmtCaracteres(multa_codigoMulta, "NAOTEMMULTA ".length());
    }

    public String getMulta_valor() {
        return multa_valor;
    }

    public void setMulta_valor(String multa_valor) {
        this.multa_valor = fmtCaracteres(multa_valor, multa_valor.length());
    }

    public String getMulta_taxa() {
        return multa_taxa;
    }

    public void setMulta_taxa(String multa_taxa) {
        this.multa_taxa = fmtCaracteres(multa_taxa, "PERCENTUALVALORNOMINALDIACORRIDO".length());
    }

    public String getMora_data() {
        return mora_data;
    }

    public void setMora_data(String mora_data) {
        this.mora_data = Dates.StringtoString(mora_data, "dd-MM-yyyy", inter_DATA_FORMAT);
    }

    public String getMora_codigoMora() {
        return mora_codigoMora;
    }

    public void setMora_codigoMora(String mora_codigoMora) {
        this.mora_codigoMora = fmtCaracteres(mora_codigoMora, "TAXAMENSAL ".length());
    }

    public String getMora_valor() {
        return mora_valor;
    }

    public void setMora_valor(String mora_valor) {
        this.mora_valor = fmtCaracteres(mora_valor, mora_valor.length());
    }

    public String getMora_taxa() {
        return mora_taxa;
    }

    public void setMora_taxa(String mora_taxa) {
        this.mora_taxa = fmtCaracteres(mora_taxa, mora_taxa.length());
    }

    public String getCnpjCPFBeneficiario() {
        return cnpjCPFBeneficiario;
    }

    public void setCnpjCPFBeneficiario(String cnpjCPFBeneficiario) {
        String tVar = getOnlyDigits(cnpjCPFBeneficiario);
        int tSize = (tVar.length() <= 11 ? 11 : 14);
        this.cnpjCPFBeneficiario = fmtNumero(tVar, tSize);
    }

    public int getNumDiasAgenda() {
        return numDiasAgenda;
    }

    public void setNumDiasAgenda(int numDiasAgenda) {
        this.numDiasAgenda = numDiasAgenda; //fmtCaracteres(numDiasAgenda, "SESSENTA".length());
    }
        
    public String getJSONBoleto() {
        String jsonBoleto = new StringBuilder()
            .append("{")
            .append("\"pagador\":{")
            .append("\"tipoPessoa\":\"" + getTipopessoa() + "\",")
            .append("\"cpfCnpj\":\"" + getCnpjCpf() + "\",")
            .append("\"nome\":\"" + getNome() + "\",")
            .append("\"endereco\":\"" + getEndereco() + "\",")
            .append("\"numero\":\"" + getNumero() + "\",")
            .append("\"complemento\":\"" + getComplemento() + "\",")
            .append("\"bairro\":\"" + getBairro() + "\",")
            .append("\"cidade\":\""+ getCidade() + "\",")
            .append("\"uf\":\"" + getUf() + "\",")
            .append("\"cep\":\"" + getCep() + "\"")
            .append("},")

            .append("\"dataEmissao\":\"" + getDataEmissao() + "\",")
            .append("\"seuNumero\":\"" + getSeuNumero() + "\",")
            .append("\"dataLimite\":\"" + getDataLimite() + "\",")
            .append("\"dataVencimento\":\"" + getDataVencimento() + "\",")

            .append("\"mensagem\":{")
            .append("\"linha1\":\"" + getMensagem_linha1() + "\",")
            .append("\"linha2\":\"" + getMensagem_linha2() + "\",")
            .append("\"linha3\":\"" + getMensagem_linha3() + "\",")
            .append("\"linha4\":\"" + getMensagem_linha4() + "\",")
            .append("\"linha5\":\"" + getMensagem_linha5() + "\"")
            .append("},")

            .append("\"desconto1\":{")
            .append("\"codigoDesconto\":\"NAOTEMDESCONTO\",")
            .append("\"taxa\":0,")
            .append("\"valor\":0,")
            .append("\"data\":\"\"")
            .append("},")

            .append("\"desconto2\":{")
            .append("\"codigoDesconto\":\"NAOTEMDESCONTO\",")
            .append("\"taxa\":0,")
            .append("\"valor\":0,")
            .append("\"data\":\"\"")
            .append("},")

            .append("\"desconto3\":{")
            .append("\"codigoDesconto\":\"NAOTEMDESCONTO\",")
            .append("\"taxa\":0,")
            .append("\"valor\":0,")
            .append("\"data\":\"\"")
            .append("},")

            .append("\"valorNominal\":" + getValorNominal() + ",")
            .append("\"valorAbatimento\":" + getValorAbatimento() + ",")

            .append("\"multa\":{")
            .append("\"data\":\"" + getMulta_data() + "\",")
            .append("\"codigoMulta\":\"" + getMulta_codigoMulta() + "\",")
            .append("\"valor\":" + getMulta_valor() + ",")
            .append("\"taxa\":" + getMulta_taxa())
            .append("},")

            .append("\"mora\":{")
            .append("\"data\":\"" + getMora_data() + "\",")
            .append("\"codigoMora\":\"" + getMora_codigoMora() + "\",")
            .append("\"valor\":" + getMora_valor() + ",")
            .append("\"taxa\":" + getMora_taxa())
            .append("},")

            .append("\"cnpjCPFBeneficiario\":\"" + getCnpjCPFBeneficiario() + "\",")
            .append("\"numDiasAgenda\":\"" + getNumDiasAgenda() + "\"")               

            .append("}")
            .toString();
        
        return jsonBoleto;
    }
}