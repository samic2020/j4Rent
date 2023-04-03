/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package j4rent.Partida;

import Funcoes.FuncoesGlobais;
import Funcoes.LerValor;
import Funcoes.Settings;
import Funcoes.VariaveisGlobais;
import Funcoes.CriptografiaUtil;
import Funcoes.CriticaExtrato;
import Funcoes.Dates;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

/**
 *
 * @author supervisor
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        Timer t = new Timer();
        t.schedule(new RemindTask(), 60 * 1000);
        
        try {
            //UIManager.setLookAndFeel("com.nilo.plaf.nimrod.NimRODLookAndFeel");
            UIManager.setLookAndFeel("com.jtattoo.plaf.mint.MintLookAndFeel");
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(jLogin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(jLogin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(jLogin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(jLogin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
    
        LerSettings();
        t.cancel();

        if (!VariaveisGlobais.Marca.equalsIgnoreCase("samic")) {
            Date fimData = new Date(123,03,30);
            if (Dates.DateDiff(Dates.DIA, new Date(), fimData) <= 0) {
                JOptionPane.showMessageDialog(null, "Avaliação expridada!\n\nChame a SAMIC para contratar o serviço.\nTel.: 21 2701-0261 / 21 98552-1405");
                System.out.println("Avaliação expridada!\n\nChame a SAMIC para contratar o serviço.\nTel.: 21 2701-0261 / 21 98552-1405");
                System.exit(0);
            }
        }
        
        // Criação da Senha
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("AJUDA")) {
                System.out.println("User o comando CRITICA <ENTER> para gerar e processar a tabela critica.");
                System.out.println("");
                System.out.println("Use o comando LIMPA conforme sintaxe:");
                System.out.println("LIMPA <ENTER> Para limpar recibos sem carteira e Arquivo de CRITICA.");
                System.out.println("LIMPA RECIBO <ENTER> Para limpar recibos sem carteira.");
                System.out.println("LIMPA CRITICA <ENTER> Para limpar critica sem carteira.");
            } else if (args[0].equalsIgnoreCase("SAMIC")) {
                if (args[1].equalsIgnoreCase("PWD")) {
                    String crypSenha = "";
                    try {
                        crypSenha = CriptografiaUtil.encrypt(args[2].toString(), CriptografiaUtil.ALGORITMO_AES,CriptografiaUtil.ALGORITMO_AES);
                    } catch (Exception e) {}
                    System.out.println("Senha: " + crypSenha);
                    System.exit(0);
                }
            } else if (args.length >= 1) {
                if (args[0].equalsIgnoreCase("CRITICA")) {
                    new CriticaExtrato(0);
                } else if (args[0].equalsIgnoreCase("LIMPA")) {
                    if (args.length == 2) {
                        if (args[1].equalsIgnoreCase("RECIBO")) {
                            new CriticaExtrato(2);
                        } else if (args[1].equalsIgnoreCase("CRITICA")) {
                            new CriticaExtrato(3);
                        } else if (args[1].equalsIgnoreCase("HELP")) {
                            System.out.println("Use o comando LIMPA conforme sintaxe:");
                            System.out.println("");
                            System.out.println("LIMPA <ENTER> Para limpar recibos sem carteira e Arquivo de CRITICA.");
                            System.out.println("LIMPA RECIBO <ENTER> Para limpar recibos sem carteira.");
                            System.out.println("LIMPA CRITICA <ENTER> Para limpar critica sem carteira.");
                        }
                    } else {
                        new CriticaExtrato(1);
                    }
                }
            } 
        }
                        
        (new jLogin(null, true)).main(new String[] {""});        
    }
    
    private static class RemindTask extends TimerTask {
        public void run() {
            System.exit(0);
        }
    }

    private static void LerSettings() {
        // Settings
        new Settings();

        VariaveisGlobais.LerConf();
        
        String[] _host = null;
        if (!"".equals(VariaveisGlobais.unidade)) {
            _host = VariaveisGlobais.unidade.split(",");
            if (_host.length > 1) {
                VariaveisGlobais.unidade = _host[0];
                VariaveisGlobais.dbnome = _host[1];
                VariaveisGlobais.dbsenha = Boolean.valueOf(_host[2]);
            }
        }
        
        if (!"".equals(VariaveisGlobais.unidade)) VariaveisGlobais.unidades = FuncoesGlobais.ObjectsAdd(VariaveisGlobais.unidades, new Object[]{VariaveisGlobais.unidade,VariaveisGlobais.dbnome,VariaveisGlobais.dbsenha});
        for (int w=1;w<=99;w++) {
            VariaveisGlobais.remoto1 = System.getProperty("remoto" + LerValor.FormatPattern(String.valueOf(w), "#0"), "");
            
            String[] _host1 = null;
            if (!"".equals(VariaveisGlobais.remoto1)) {
                _host1 = VariaveisGlobais.remoto1.split(",");
                if (_host1.length > 1) {
                    VariaveisGlobais.remoto1  = _host1[0];
                    VariaveisGlobais.dbnome1  = _host1[1];
                    VariaveisGlobais.dbsenha1 = Boolean.valueOf(_host1[2]);
                }
            }
            if (!"".equals(VariaveisGlobais.remoto1)) VariaveisGlobais.unidades = FuncoesGlobais.ObjectsAdd(VariaveisGlobais.unidades, new Object[]{VariaveisGlobais.remoto1,VariaveisGlobais.dbnome1,VariaveisGlobais.dbsenha1});            
        }
    }
}
