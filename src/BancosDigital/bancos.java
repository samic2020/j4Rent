/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BancosDigital;

import Funcoes.DbMain;
import Funcoes.FuncoesGlobais;
import Funcoes.StringManager;
import Funcoes.VariaveisGlobais;
import java.text.Normalizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author desenvolvimento-pc
 */
public class bancos {
    DbMain conn = VariaveisGlobais.conexao;
    
    private static String banco_id;
    private static String banco_NBANCO;
    private static String banco_NBANCODV;
    private static String banco_AGENCIA;
    private static String banco_AGENCIADV;
    private static String banco_CCORRENTE;
    private static String banco_OPERADOR;
    private static String banco_CARTEIRA;
    private static String banco_BENEFICIARIO;
    
    private static String banco_crtFile;
    private static String banco_keyFile;
    private static String banco_certPath;
    
    private static String banco_logo;

    private static String banco_nnumero;
    
    private static String banco_client_id;
    private static String banco_client_secret;
    
    public bancos(String banco) {
        // Ler dados do banco banco
        String dadosBco[][] = null;
            try {
                dadosBco = conn.LerCamposTabela(
                        new String[] {"id","nbanco","nbancodv","agencia","agenciadv","ccorrente","operador","crtfile","keyfile","path","nnumero","carteira","beneficiario","clientid","clientsecret"}, 
                        "bancos_digital", 
                        "Trim(nbanco) = '" + banco + "'"
                        );
            } catch (Exception e) {e.printStackTrace();}
            
            if (dadosBco != null) {
                setBanco_id(dadosBco[0][3].toString());
                setBanco_NBANCO(dadosBco[1][3].toString());
                setBanco_NBANCODV(dadosBco[2][3].toString());
                setBanco_AGENCIA(dadosBco[3][3].toString());
                setBanco_AGENCIADV(dadosBco[4][3].toString());
                setBanco_CCORRENTE(dadosBco[5][3].toString());
                setBanco_OPERADOR(dadosBco[6][3].toString());
                
                setBanco_CARTEIRA(dadosBco[11][3].toString());
                setBanco_BENEFICIARIO(dadosBco[12][3].toString());
                
                setBanco_CrtFile(dadosBco[7][3].toString());
                setBanco_KeyFile(dadosBco[8][3].toString());
                setBanco_CertPath(dadosBco[9][3].toString());
                setBanco_Nnumero(dadosBco[10][3].toString());
                setBanco_ClientId(dadosBco[13][3].toString());
                setBanco_ClientSecret(dadosBco[14][3].toString());
            } else {
                setBanco_id(null);
                setBanco_NBANCO(null);
                setBanco_NBANCODV(null);
                setBanco_AGENCIA(null);
                setBanco_AGENCIADV(null);
                setBanco_CCORRENTE(null);
                setBanco_OPERADOR(null);
                setBanco_CARTEIRA(null);
                setBanco_BENEFICIARIO(null);
                setBanco_CrtFile(null);
                setBanco_KeyFile(null);
                setBanco_CertPath(null);
                setBanco_Nnumero(null);
                setBanco_ClientId(null);
                setBanco_ClientSecret(null);
            }
    }

    public String getBanco_id() { return banco_id; }
    public  void setBanco_id(String banco_id) { bancos.banco_id = banco_id; }
    
    public  String getBanco_NBANCO() { return banco_NBANCO; }
    public  void setBanco_NBANCO(String banco_NBANCO) { bancos.banco_NBANCO = banco_NBANCO; }

    public String getBanco_NBANCODV() { return banco_NBANCODV; }
    public void setBanco_NBANCODV(String banco_NBANCODV) { bancos.banco_NBANCODV = banco_NBANCODV; }
    
    public  String getBanco_AGENCIA() { return banco_AGENCIA; }
    public  void setBanco_AGENCIA(String banco_AGENCIA) { bancos.banco_AGENCIA = banco_AGENCIA; }

    public String getBanco_AGENCIADV() { return banco_AGENCIADV; }
    public void setBanco_AGENCIADV(String banco_AGENCIADV) { bancos.banco_AGENCIADV = banco_AGENCIADV; }
    
    public  String getBanco_CCORRENTE() { return banco_CCORRENTE; }
    public  void setBanco_CCORRENTE(String banco_CCORRENTE) { bancos.banco_CCORRENTE = banco_CCORRENTE; }

    public  String getBanco_OPERADOR() { return banco_OPERADOR; }
    public  void setBanco_OPERADOR(String banco_OPERADOR) { bancos.banco_OPERADOR = banco_OPERADOR; }

    public String getBanco_CARTEIRA() { return banco_CARTEIRA; }
    public void setBanco_CARTEIRA(String banco_CARTEIRA) { bancos.banco_CARTEIRA = banco_CARTEIRA; }

    public String getBanco_BENEFICIARIO() { return banco_BENEFICIARIO; }
    public void setBanco_BENEFICIARIO(String banco_BENEFICIARIO) { bancos.banco_BENEFICIARIO = banco_BENEFICIARIO; }
    
    public  String getBanco_KeyFile() { return banco_keyFile; }
    public  void setBanco_KeyFile(String banco_keyFile) { bancos.banco_keyFile = banco_keyFile; }

    public  String getBanco_CertPath() { return banco_certPath; }
    public  void setBanco_CertPath(String banco_certPath) { bancos.banco_certPath = banco_certPath; }

    public  String getBanco_CrtFile() { return banco_crtFile; }
    public  void setBanco_CrtFile(String banco_crtFile) { bancos.banco_crtFile = banco_crtFile; }

    public  String getBanco_Nnumero() { return banco_nnumero; }
    public  void setBanco_Nnumero(String banco_nnumero) { bancos.banco_nnumero = banco_nnumero; }

    public String getBanco_ClientId() { return banco_client_id; }
    public void setBanco_ClientId(String client_id) { bancos.banco_client_id = client_id; }

    public String getBanco_ClientSecret() { return banco_client_secret; }
    public void setBanco_ClientSecret(String client_secret) { bancos.banco_client_secret = client_secret; }    
    
    public String getBanco_logo() { return "resources/logoBancos/" + bancos.banco_NBANCO + ".jpg"; }           
    
     public static String fmtNumero(String value, int size) {
        String zeros = FuncoesGlobais.StrZero("0",size);
        int tam = ((zeros + value.trim()).length() > size ? size : (zeros + value).trim().length());
        return StringManager.Right(zeros + getOnlyDigits(value), tam);
    }
    
     public static String fmtCaracteres(String value, int size) {
        String tvalue = value.trim().toUpperCase();
        String retValue = stripAccents(tvalue);
        int tam = (retValue.trim().length() > size ? size : retValue.trim().length());
        return retValue.substring(0, tam);
    }

     public static String rmvNumero(String value) {
        String ret = "";
        for (int i=0;i<value.length();i++) {
            if (value.substring(i, i + 1).equalsIgnoreCase(".") || value.substring(i, i + 1).equalsIgnoreCase("/") || value.substring(i, i + 1).equalsIgnoreCase("-") || value.substring(i, i + 1).equalsIgnoreCase(",") || value.substring(i, i + 1).equalsIgnoreCase(" ")){
                //
            } else {
                ret += value.substring(i, i + 1);
            }
        }
        return ret;
    }    
    
    public static String stripAccents(String input){
        String var = Normalizer.normalize(input, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");        
        return input == null ? null : Normalizer.normalize(var, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
    } 
    
    public static String getOnlyDigits(String s) {
    Pattern pattern = Pattern.compile("[^0-9]");
    Matcher matcher = pattern.matcher(s);
    String number = matcher.replaceAll("");
    return number;
    }
    
    public static String getOnlyStrings(String s) {
        Pattern pattern = Pattern.compile("[^a-z A-Z]");
        Matcher matcher = pattern.matcher(s);
        String number = matcher.replaceAll("");
        return number;
    }    
}