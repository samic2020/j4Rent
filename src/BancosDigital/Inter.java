/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BancosDigital;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.util.Base64;
import java.util.stream.Collectors;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocketFactory;
import org.json.JSONArray;
import org.json.JSONException;

import org.json.JSONObject;

/**
 *
 * @author desenvolvimento-pc
 */
public class Inter {
    /**
    / Retono em caso de erro
    / Retorna na função insertBoleta
    **/
    private String codErro;
    private String msgErro;
    
    // Parametros Token 
    private String scope = "boleto-cobranca.read";
    private String grant_type = "client_credentials";
    
    public String getCodErro() { return codErro; }
    public String getMsgErro() { return msgErro; }
    
    public static void main(String[] args) throws Exception {
        //Inter c = new Inter();
      
        //Object[] msg = c.pdfBoleta(new String[] {"https://apis.bancointer.com.br/openbanking/v1/certificado/boletos/00904806611/pdf"}, "c:\\cert\\Inter_API_Certificado.crt", "c:\\cert\\Inter_API_Chave.key");
        
        // Token
        //Object[] msg = c.getToken("https://cdpj.partners.bancointer.com.br/oauth/v2/token", "c:\\cert\\Inter_API_Certificado.crt", "c:\\cert\\Inter_API_Chave.key");
        //Object[] msg = c.gToken(new String[] {"https://oauth2.googleapis.com/token","308580726145-odvbtc65m8tl5qco9gkt86e3ak8711j0.apps.googleusercontent.com","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"});
        //String codErr = msg[0].toString();
        //String msgErr = "";
        //try { 
        //    msgErr = (String)msg[1]; 
        //} catch (ClassCastException e) {            
        //    if (e.getMessage().toString().contains("[Ljava.lang.Object;")) {                            
        //        Object[] _msgErr = (Object[])msg[1]; 
        //        msgErr = ((JSONObject)_msgErr[0]).getString("access_token");
        //    //} else if (true) {
        //    //    msgErr = ((String[])msg[1])[0]; 
        //    } else {
        //        msgErr = "Não depurado";
        //    }            
        //}
        //System.out.println(codErr + "\n" + msgErr);
    }
    
    public Object[] insertBoleta(String[] dados, String path_crt, String path_key, String json_message) throws Exception{
        if (!new File(path_crt).exists()) return new Object[] {-1, new String[] {"Não achei o Certificado."}};
        if (!new File(path_key).exists()) return new Object[] {-1, new String[] {"Não achei a Chave Privada."}};
        
        File crtFile = new File(path_crt);
        File keyFile = new File(path_key);
        
        KeyStore keyStore = PEMImporter.createKeyStore(keyFile, crtFile, "samic");
        SSLContext sslContext = PEMImporter.createSSLFactory(keyFile, crtFile, "samic");
        SSLServerSocketFactory sslServerSocketFactory = sslContext.getServerSocketFactory();        
                
        byte[] postData = json_message.getBytes( StandardCharsets.UTF_8 );
        int postDataLength = postData.length;
        
        // Pega dados
        String url_ws = dados[0].toString().trim();
        String ccorrente = dados[1].toString().trim();
        String token = dados[2].toString().trim();

        URL url = new URL(url_ws);
        HttpsURLConnection uc = (HttpsURLConnection) url.openConnection();
        uc.setSSLSocketFactory(sslContext.getSocketFactory());
        // define que vai enviar dados da requisição
        uc.setDoOutput(true);
        uc.setRequestMethod("POST");
        uc.setRequestProperty("Content-Type", "application/json");
        uc.setRequestProperty("Accept", "application/json");
        uc.setRequestProperty("x-inter-conta-corrente", ccorrente);
        uc.setRequestProperty("charset", "utf-8");
        uc.setRequestProperty("Content-Length", Integer.toString( postDataLength ));
        uc.setRequestProperty("Authorization","Bearer " + token);

        try( DataOutputStream wr = new DataOutputStream( uc.getOutputStream())) {
           wr.write( postData );
        }        
//        OutputStream wr = uc.getOutputStream();
//        wr.write(json_message.getBytes());
//        wr.flush();
//        wr.close();

        int statusCode = uc.getResponseCode();
        BufferedReader br = new BufferedReader(new InputStreamReader(
                (statusCode == 200) ? uc.getInputStream() : uc.getErrorStream()
        ));
        String message = br.readLine();
        
        //String out;
        //while ((out = br.readLine()) != null) {
        //   System.out.println(out);
        //}

        String[] infoMessage = null; JSONObject jsonOb = null;
        if (statusCode != 200) {
            if (message == null) {
                infoMessage = new String[] {"Erro desconhecido."};
            } else {
                if (statusCode == 401) {
                    infoMessage = new String[] {"Erro de Autenticação."};
                } else if (statusCode == 500) {
                    infoMessage = new String[] {"Erro Interno no servidor."};
                } else if (statusCode == 400) {                    
                    JSONArray arrJson = null;
                    try {
                        jsonOb = new JSONObject(message);      
                        arrJson = jsonOb.getJSONArray("violacoes");
                    } catch (JSONException jex) {} finally {
                        if (arrJson != null) {
                            if (arrJson.length() != 0 ) {
                                infoMessage = new String[] {arrJson.getString(0)};                                
                            } else infoMessage = new String[] { jsonOb.getString("detail")};
                        } else {
                            infoMessage = new String[] { jsonOb.getString("detail")};
                        }                    
                    }
                } else {
                    infoMessage = new String[] {"Erro desconhecido."};
                }
            }
            codErro = String.valueOf(statusCode); msgErro = infoMessage != null ? infoMessage[0].toString() : null;
        } else {
            jsonOb = new JSONObject(message);
            String nossoNumero = myfunction(jsonOb,"nossoNumero").toString();
            String codigoBarras = myfunction(jsonOb,"codigoBarras").toString();
            String linhaDigitavel = myfunction(jsonOb,"linhaDigitavel").toString();
            infoMessage = new String[] {nossoNumero, codigoBarras, linhaDigitavel};
            
            codErro = null; msgErro = null;
        }       
       
       uc.disconnect();
       
       return new Object[] {statusCode, infoMessage};
    }

    public Object[] selectBoleta(String[] dados, String path_crt, String path_key) throws Exception{
        if (!(new File(path_crt)).exists()) return new Object[] {-1, new String[] {"Não achei o Certificado."}};
        if (!(new File(path_key)).exists()) return new Object[] {-1, new String[] {"Não achei a Chave Privada."}};

        File crtFile = new File(path_crt);
        File keyFile = new File(path_key);

        KeyStore keyStore = PEMImporter.createKeyStore(keyFile, crtFile, "samic");
        SSLContext sslContext = PEMImporter.createSSLFactory(keyFile, crtFile, "samic");
        SSLServerSocketFactory sslServerSocketFactory = sslContext.getServerSocketFactory();        

        // Pega dados
        String url_ws = dados[0].toString().trim();
        String ccorrente = dados[1].toString().trim();
        String token = dados[2].toString().trim();
        
        URL url = new URL(url_ws);
        HttpsURLConnection uc = (HttpsURLConnection) url.openConnection();
        uc.setSSLSocketFactory(sslContext.getSocketFactory());
        // define que vai enviar dados da requisição
        uc.setDoOutput(true);
        uc.setRequestMethod("GET");
        uc.setRequestProperty("Content-Type", "application/json");
        uc.setRequestProperty("Accept", "application/json");
        uc.setRequestProperty("x-inter-conta-corrente", ccorrente);
        uc.setRequestProperty("data-raw", "");
        uc.setRequestProperty("Authorization","Bearer " + token);
        
        int statusCode = -1;
        BufferedReader br = null;
        
        try {
            statusCode = uc.getResponseCode();
            br = new BufferedReader(new InputStreamReader(
               (statusCode == 200) ? uc.getInputStream() : uc.getErrorStream()
            ));
        } catch (Exception e) { e.printStackTrace(); }
        
        String message = null;
        try {
            message = br.readLine();
        } catch (Exception ex) {}
        Object[] infoMessage = null;
        if (statusCode != 200) {
            if (message == null) {
                infoMessage = new String[] {"Erro desconhecido."};

                if (statusCode == 401) {
                    infoMessage = new String[] {"Erro de Autenticação."};
                } else if (statusCode == 500) {
                    infoMessage = new String[] {"Erro Interno no servidor."};
                } else if (statusCode == 400) {
                    JSONObject jsonOb = new JSONObject(message);      
                    JSONArray arrJson=jsonOb.getJSONArray("message");
                    infoMessage = new String[] {arrJson.getString(0)};
                } else {
                    infoMessage = new String[] {"Erro desconhecido."};
                }
            }
        } else {
            JSONObject jsonOb = new JSONObject(message);
            infoMessage = new Object[] {jsonOb};
        }       
       
       uc.disconnect();
       return new Object[] {statusCode, infoMessage};       
    }

    public Object[] baixaBoleta(String[] dados, String path_crt, String path_key, String codBaixa) throws Exception{
        if (!(new File(path_crt)).exists()) return new Object[] {-1, new String[] {"Não achei o Certificado."}};
        if (!(new File(path_key)).exists()) return new Object[] {-1, new String[] {"Não achei a Chave Privada."}};

        File crtFile = new File(path_crt);
        File keyFile = new File(path_key);

        KeyStore keyStore = PEMImporter.createKeyStore(keyFile, crtFile, "samic");
        SSLContext sslContext = PEMImporter.createSSLFactory(keyFile, crtFile, "samic");
        SSLServerSocketFactory sslServerSocketFactory = sslContext.getServerSocketFactory();        

        byte[] postData = codBaixa.getBytes( StandardCharsets.UTF_8 );
        int postDataLength = postData.length;
        
        // Pega dados
        String url_ws = dados[0].toString().trim();
        String ccorrente = dados[1].toString().trim();
        String token = dados[2].toString().trim();

        URL url = new URL(url_ws);
        HttpsURLConnection uc = (HttpsURLConnection) url.openConnection();
        uc.setSSLSocketFactory(sslContext.getSocketFactory());
        // define que vai enviar dados da requisição
        uc.setDoOutput(true);
        uc.setRequestMethod("POST");
        uc.setRequestProperty("Content-Type", "application/json");
        uc.setRequestProperty("Accept", "application/json");
        uc.setRequestProperty("x-inter-conta-corrente", ccorrente);
        uc.setRequestProperty("charset", "utf-8");
        uc.setRequestProperty("Content-Length", Integer.toString( postDataLength ));
        uc.setRequestProperty("Authorization","Bearer " + token);

        try( DataOutputStream wr = new DataOutputStream( uc.getOutputStream())) {
           wr.write( postData );
        }        
        
        int statusCode = uc.getResponseCode();
        BufferedReader br = new BufferedReader(new InputStreamReader(
                (statusCode == 204) ? uc.getInputStream() : uc.getErrorStream()
        ));
        String message = br.readLine();
        
        String out;
        while ((out = br.readLine()) != null) {
           System.out.println(out);
       }
        
        String[] infoMessage = null;
        if (statusCode != 204) {
            if (message == null) {
                infoMessage = new String[] {"Erro desconhecido."};
            } else {
                if (statusCode == 401) {
                    infoMessage = new String[] {"Erro de Autenticação."};
                } else if (statusCode == 500) {
                    infoMessage = new String[] {"Erro Interno no servidor."};
                } else if (statusCode == 400) {
                    JSONObject jsonOb = new JSONObject(message);      
                    JSONArray arrJson=jsonOb.getJSONArray("message");
                    infoMessage = new String[] {arrJson.getString(0)};
                } else {
                    infoMessage = new String[] {"Erro desconhecido."};
                }
            }
        } else {
            infoMessage = new String[] {"Sucesso"};
        }       
       
       uc.disconnect();
       return new Object[] {statusCode, infoMessage};  
    }

    public Object[] pdfBoleta(String[] dados, String path_crt, String path_key, String nnumero) throws Exception{
        if (!(new File(path_crt)).exists()) return new Object[] {-1, new String[] {"Não achei o Certificado."}};
        if (!(new File(path_key)).exists()) return new Object[] {-1, new String[] {"Não achei a Chave Privada."}};

        File crtFile = new File(path_crt);
        File keyFile = new File(path_key);

        KeyStore keyStore = PEMImporter.createKeyStore(keyFile, crtFile, "samic");
        SSLContext sslContext = PEMImporter.createSSLFactory(keyFile, crtFile, "samic");
        SSLServerSocketFactory sslServerSocketFactory = sslContext.getServerSocketFactory();        
        
        byte[] postData = nnumero.getBytes( StandardCharsets.UTF_8 );
        int postDataLength = postData.length;
        
        // Pega dados
        String url_ws = dados[0].toString().trim();
        String ccorrente = dados[1].toString().trim();
        String token = dados[2].toString().trim();
        
        URL url = new URL(url_ws);
        HttpsURLConnection uc = (HttpsURLConnection) url.openConnection();
        uc.setSSLSocketFactory(sslContext.getSocketFactory());
        // define que vai enviar dados da requisição
        uc.setDoOutput(true);
        uc.setRequestMethod("GET");
        uc.setRequestProperty("Content-Type", "application/json");
        uc.setRequestProperty("Content-Type", "application/base64");
        uc.setRequestProperty("charset", "utf-8");
        uc.setRequestProperty("Content-Length", Integer.toString( postDataLength ));
        uc.setRequestProperty("x-inter-conta-corrente", ccorrente);
        uc.setRequestProperty("Authorization","Bearer " + token);

        int statusCode = uc.getResponseCode();
        BufferedReader br = new BufferedReader(new InputStreamReader(
                (statusCode == 200) ? uc.getInputStream() : uc.getErrorStream()
        ));
       
        String message = null; // = br.readLine();
        String out;
        while ((out = br.readLine()) != null) {
           message = message + out;
       }

        Object[] infoMessage = null;
        if (statusCode != 200) {
            if (message == null) {
                infoMessage = new String[] {"Erro desconhecido."};
            } else {
                if (statusCode == 401) {
                    infoMessage = new String[] {"Erro de Autenticação."};
                } else if (statusCode == 500) {
                    infoMessage = new String[] {"Erro Interno no servidor."};
                } else if (statusCode == 400) {
                    JSONObject jsonOb = new JSONObject(message);      
                    JSONArray arrJson=jsonOb.getJSONArray("message");
                    infoMessage = new String[] {arrJson.getString(0)};
                } else {
                    infoMessage = new String[] {"Erro desconhecido."};
                }
            }
        } else {
            InputStream inputStream = uc.getInputStream();
             String result = new BufferedReader(new InputStreamReader(inputStream))
            .lines().collect(Collectors.joining("\n"));
            String saveFilePath = "/cert/interBoleta.pdf";
            if (new File(saveFilePath).exists()) new File(saveFilePath).delete();

            byte[] data = null;
            try {
                data = Base64.getDecoder().decode(message);
            } catch (Exception ex) { ex.printStackTrace(); }
            
            if (data != null) {
                OutputStream stream = null;
                try { 
                    stream = new FileOutputStream(saveFilePath);
                    stream.write(data);
                } catch (Exception e) {
                   System.err.println("Couldn't write to file...");
                }
                stream.close();
                inputStream.close();
            }
        }
        
        uc.disconnect();

        return new Object[] {statusCode, infoMessage};
    }    
  
    public Object[] getToken(String[] dados, String path_crt, String path_key) throws Exception{
        if (!(new File(path_crt)).exists()) return new Object[] {-1, new String[] {"Não achei o Certificado."}};
        if (!(new File(path_key)).exists()) return new Object[] {-1, new String[] {"Não achei a Chave Privada."}};

        File crtFile = new File(path_crt);
        File keyFile = new File(path_key);

        KeyStore keyStore = PEMImporter.createKeyStore(keyFile, crtFile, "samic");
        SSLContext sslContext = PEMImporter.createSSLFactory(keyFile, crtFile, "samic");
        SSLServerSocketFactory sslServerSocketFactory = sslContext.getServerSocketFactory();        

        // Parametros Token
        String scope = "boleto-cobranca.read boleto-cobranca.write";
        String grant_type = "client_credentials";

        // Pega dados 
        String url_ws = dados[0].toString();
        String clientid = dados[1].toString();
        String clientsecret = dados[2].toString();
        
        String urlParameters = "client_id=" + clientid + "&client_secret=" + clientsecret + "&scope=" + scope + "&grant_type=" + grant_type;
        byte[] postData = urlParameters.getBytes( StandardCharsets.UTF_8 );
        int postDataLength = postData.length;
        
        URL url = new URL(url_ws);
        HttpsURLConnection uc = (HttpsURLConnection) url.openConnection();
        uc.setSSLSocketFactory(sslContext.getSocketFactory());
        // define que vai enviar dados da requisição
        uc.setDoOutput(true);
        uc.setInstanceFollowRedirects( false );
        uc.setRequestMethod("POST");
        
        uc.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); 
        uc.setRequestProperty( "charset", "utf-8");
        uc.setRequestProperty( "Content-Length", Integer.toString( postDataLength ));
        uc.setUseCaches( false );
        
        try( DataOutputStream wr = new DataOutputStream( uc.getOutputStream())) {
           wr.write( postData );
        }        
              
        int statusCode = -1;
        BufferedReader br = null;
        
        try {
            statusCode = uc.getResponseCode();
            br = new BufferedReader(new InputStreamReader(
               (statusCode == 200) ? uc.getInputStream() : uc.getErrorStream()
            ));
        } catch (Exception e) {} // e.printStackTrace(); }
        
        String message = null;
        try {
            message = br.readLine();
        } catch (Exception ex) {}
        Object[] infoMessage = null;
        if (statusCode != 200) {
            if (message == null) {
                infoMessage = new String[] {"Erro desconhecido."};

                if (statusCode == 401) {
                    infoMessage = new String[] {"Erro de Autenticação."};
                } else if (statusCode == 403) {
                    infoMessage = new String[] {"Acesso negado."};
                } else if (statusCode == 500) {
                    infoMessage = new String[] {"Erro Interno no servidor."};
                } else if (statusCode == 400) {
                    JSONObject jsonOb = new JSONObject(message);      
                    JSONArray arrJson=jsonOb.getJSONArray("message");
                    infoMessage = new String[] {arrJson.getString(0)};
                } else {
                    infoMessage = new String[] {"Erro desconhecido."};
                }
            } else {
                infoMessage = new String[] {message};
            }
        } else {
            JSONObject jsonOb = new JSONObject(message);
            infoMessage = new Object[] {jsonOb};
        }       
       
       uc.disconnect();
       return new Object[] {statusCode, infoMessage};       
    }
    
    public Object myfunction(JSONObject x,String y) throws JSONException {
        Object finalresult = null;    
        JSONArray keys =  x.names();
        for(int i=0;i<keys.length();i++) {
            if(finalresult!=null) {
                return finalresult;                     //To kill the recursion
            }

            String current_key = keys.get(i).toString();
            if(current_key.equals(y)) {
                finalresult = x.get(current_key);
                return finalresult;
            }

            if(x.get(current_key).getClass().getName().equals("org.json.JSONObject")) {
                myfunction((JSONObject) x.get(current_key),y);
            }
            else if(x.get(current_key).getClass().getName().equals("org.json.JSONArray")) {
                for(int j=0;j<((JSONArray) x.get(current_key)).length();j++) {
                    if(((JSONArray) x.get(current_key)).get(j).getClass().getName().equals("org.json.JSONObject")) {
                        myfunction((JSONObject)((JSONArray) x.get(current_key)).get(j),y);
                    }
                }
            }
        }
        return null;
    }        
    
    
    public Object[] gToken(String[] dados) throws Exception{
        // Parametros Token
        String scope = "boleto-cobranca.read boleto-cobranca.write";
        String grant_type = "client_credentials";

        // Pega dados 
        String url_ws = dados[0].toString();
        String clientid = dados[1].toString();
        String clientsecret = dados[2].toString();
        
        String urlParameters = "client_id=" + clientid + "&client_secret=" + clientsecret + "&scope=" + scope + "&grant_type=" + grant_type;
        byte[] postData = urlParameters.getBytes( StandardCharsets.UTF_8 );
        int postDataLength = postData.length;
        
        URL url = new URL(url_ws);
        HttpsURLConnection uc = (HttpsURLConnection) url.openConnection();
        // define que vai enviar dados da requisição
        uc.setDoOutput(true);
        uc.setInstanceFollowRedirects( false );
        uc.setRequestMethod("POST");
        
        uc.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); 
        uc.setRequestProperty( "charset", "utf-8");
        uc.setRequestProperty( "Content-Length", Integer.toString( postDataLength ));
        uc.setUseCaches( false );
        
        try( DataOutputStream wr = new DataOutputStream( uc.getOutputStream())) {
           wr.write( postData );
        }        
              
        int statusCode = -1;
        BufferedReader br = null;
        
        try {
            statusCode = uc.getResponseCode();
            br = new BufferedReader(new InputStreamReader(
               (statusCode == 200) ? uc.getInputStream() : uc.getErrorStream()
            ));
        } catch (Exception e) {} // e.printStackTrace(); }
        
        String message = null;
        try {
            message = br.readLine();
        } catch (Exception ex) {}
        Object[] infoMessage = null;
        if (statusCode != 200) {
            if (message == null) {
                infoMessage = new String[] {"Erro desconhecido."};

                if (statusCode == 401) {
                    infoMessage = new String[] {"Erro de Autenticação."};
                } else if (statusCode == 403) {
                    infoMessage = new String[] {"Acesso negado."};
                } else if (statusCode == 500) {
                    infoMessage = new String[] {"Erro Interno no servidor."};
                } else if (statusCode == 400) {
                    JSONObject jsonOb = new JSONObject(message);      
                    JSONArray arrJson=jsonOb.getJSONArray("message");
                    infoMessage = new String[] {arrJson.getString(0)};
                } else {
                    infoMessage = new String[] {"Erro desconhecido."};
                }
            } else {
                infoMessage = new String[] {message};
            }
        } else {
            JSONObject jsonOb = new JSONObject(message);
            infoMessage = new Object[] {jsonOb};
        }       
       
       uc.disconnect();
       return new Object[] {statusCode, infoMessage};       
    }
    
}