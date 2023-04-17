package Relatorios;

import Funcoes.AutoCompletion;
import Funcoes.Dates;
import Funcoes.Db;
import Funcoes.DbMain;
import Funcoes.FuncoesGlobais;
import Funcoes.LerValor;
import Funcoes.StringManager;
import Funcoes.StringUtils;
import Funcoes.VariaveisGlobais;
import Funcoes.tempFile;
import Funcoes.toPreview;
import com.lowagie.text.Font;
import j4rent.Partida.Collections;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;

public class ExtratoHistorico extends javax.swing.JInternalFrame {
    Db db = new Db();
    boolean bExecNome = false, bExecCodigo = false;
    
    public ExtratoHistorico() {
        initComponents();
        
        FillCombos();
        AutoCompletion.enable(jRgprp);
        AutoCompletion.enable(jNomeProp);
     
        ehStatus.setText("");
        ehProgress.setIndeterminate(false);
        ehProgress.setMaximum(0);
        ehProgress.setMinimum(0);
        ehProgress.setValue(0);
    }

    private void FillCombos() {
        String sSql = "SELECT distinct p.rgprp, p.nome, '' AS tag FROM proprietarios p " +
                      "WHERE Upper(p.status) = 'ATIVO' UNION " +
                      "SELECT distinct pe.rgprp, pe.nome, '' AS tag FROM " +
                      "jgeral_excluidos.proprietarios pe WHERE Upper(pe.status) = 'ATIVO' " +
                      "ORDER BY 2;";
        ResultSet imResult = db.OpenTable(sSql, null);
        int nRecord = DbMain.RecordCount(imResult);
        
        List<Object[]> props = new ArrayList<Object[]>();
        try {
            while (imResult.next()) {
                if (!imResult.getString("tag").equalsIgnoreCase("X")) {
                    props.add(new Object[] {String.valueOf(imResult.getInt("rgprp")), imResult.getString("nome")});
                } else {
                    Object[][] aAvisos = null;
                    aAvisos = db.ReadFieldsTable(new String[] {"tag"}, "avisos", "rid = 0 and registro = '" + String.valueOf(imResult.getInt("rgprp")) + "' AND tag != 'X'");
                    if (aAvisos != null) {
                        props.add(new Object[] {String.valueOf(imResult.getInt("rgprp")), imResult.getString("nome")});
                    }
                }
            }
        } catch (Exception e) {e.printStackTrace();}
        DbMain.FecharTabela(imResult);
        
        // Deixa somente quem tem saldo  
        List<Object[]> props_final = new ArrayList<Object[]>();
        for (Object[] item : props) props_final.add(item);

        // Ordena Lista
        props_final.sort(new Comparator<Object[]>(){
            @Override
            public int compare(Object[] o1, Object[] o2)
            {
               return o1[1].toString().compareTo(o2[1].toString());
            }
        });
        
        jRgprp.removeAllItems();
        jNomeProp.removeAllItems();
        for (Object[] item : props_final) {
            jRgprp.addItem(item[0].toString());
            jNomeProp.addItem(item[1].toString());
        }
    }
    
    private void HistoricoPreview(Date dtini, Date dtfim, String rgprp) {
        jGerar.setEnabled(false);

        Collections gVar = VariaveisGlobais.dCliente;
        List<classExtratoHistorico> lista = new ArrayList<>();
        
        List<Date> dataExtHist = retDatas(dtini, dtfim, rgprp);
        if (dataExtHist == null) {
            JOptionPane.showMessageDialog(this, "Não há movimento neste período de datas.");

            jGerar.setEnabled(true);

            ehStatus.setText("");
            ehProgress.setIndeterminate(false);
            ehProgress.setMaximum(0);
            ehProgress.setMinimum(0);
            ehProgress.setValue(0);
            
            return;
        }
        
        String selectSQL = "select 'EXT' tipo, e.rgprp, e.rgimv, e.contrato, e.campo, e.dtvencimento, e.dtrecebimento, " +
        "e.tag, e.rc_aut, e.et_aut, e.pr_sdant from extrato e WHERE (e.rgprp = :rgprp1) AND e.tag != 'B' AND " +
        "e.dtrecebimento BETWEEN :dtini1 AND :dtfim1 " +
        
        "union SELECT 'EXT' tipo, ee.rgprp, ee.rgimv, ee.contrato, ee.campo, ee.dtvencimento, ee.dtrecebimento, " +
        "ee.tag, ee.rc_aut, ee.et_aut, ee.pr_sdant from jgeral_excluidos.extrato ee WHERE (ee.rgprp = :rgprp2) AND ee.tag != 'B' AND " +
        "ee.dtrecebimento BETWEEN :dtini2 AND :dtfim2 " +                

        "union SELECT 'AVI' tipo, a.registro rgprp, null rgimv, null contrato, a.campo, " +
        "RetAvDataRid2(a.campo) dtvencimento, RetAvDataRid2(a.campo) dtrecebimento, " +
        "a.tag, a.autenticacao rc_aut, a.et_aut, 0 pr_sdant FROM avisos a " +
        "where (a.registro = :registro1) AND a.rid = 0 and a.tag != 'B' AND " + 
        "RetAvDataRid2(a.campo) BETWEEN :dtini3 AND :dtfim3 " + 

        "union SELECT 'AVI' tipo, ae.registro rgprp, null rgimv, null contrato, ae.campo, " +
        "RetAvDataRid2(ae.campo) dtvencimento, RetAvDataRid2(ae.campo) dtrecebimento, " +
        "ae.tag, ae.autenticacao rc_aut, ae.et_aut, 0 pr_sdant FROM jgeral_excluidos.avisos ae " +
        "where (ae.registro = :registro2) AND ae.rid = 0 and ae.tag != 'B' AND " + 
        "RetAvDataRid2(ae.campo) BETWEEN :dtini4 AND :dtfim4 " + 
                
        "union SELECT 'AUX' tipo, x.contrato rgprp, null rgimv, null contrato, x.campo, "  +
        "x.dtvencimento, x.dtrecebimento, ' ' tag, x.rc_aut, x.rc_aut et_aut, 0 pr_sdant " +
        "FROM auxiliar x WHERE x.conta = 'EXT' AND x.contrato = :auxiliar1 AND " +
        "x.dtrecebimento BETWEEN :dtini5 AND :dtfim5 " +
                
        "union SELECT 'AUX' tipo, xe.contrato rgprp, null rgimv, null contrato, xe.campo, "  +
        "xe.dtvencimento, xe.dtrecebimento, ' ' tag, xe.rc_aut, xe.rc_aut et_aut, 0 pr_sdant " +
        "FROM jgeral_excluidos.auxiliar xe WHERE xe.conta = 'EXT' AND xe.contrato = :auxiliar2 AND " +
        "xe.dtrecebimento BETWEEN :dtini6 AND :dtfim6 " +
                
        "ORDER BY 7;";

        ehStatus.setText("Montando o relatório...  [AGUARDE!]");
        ehProgress.setIndeterminate(false);
        ehProgress.setMaximum(dataExtHist.size());
        ehProgress.setMinimum(0);
        ehProgress.setValue(0);
        int posPg = 0;
        
        BigDecimal saldo = new BigDecimal(0);
        for (Date data : dataExtHist) {
            ehStatus.setText("Montando o relatório...  [AGUARDE!]");
            ehProgress.setValue(posPg++);
            
            // Saldo de Abertura
            BigDecimal saldoAbertura = pegaSaldoAnterior(dataExtHist.get(0), rgprp);
            if (saldoAbertura != null) {
                if (saldoAbertura.signum() < 0) {
                    saldo = saldo.subtract(saldoAbertura);
                    lista.add(new classExtratoHistorico(data, "Saldo Abertura", null, null, null, saldoAbertura));
                } else {
                    saldo = saldo.add(saldoAbertura);
                    lista.add(new classExtratoHistorico(data, "Saldo Abertura", null, null, null, saldoAbertura));
                }
            }

            ResultSet rs = db.OpenTable(selectSQL, new Object[][] {
                {"string", "rgprp1", rgprp},
                {"string", "rgprp2", rgprp},
                {"string", "registro1", rgprp},
                {"string", "registro2", rgprp},
                {"string", "auxiliar1", rgprp},
                {"string", "auxiliar2", rgprp},
                {"date", "dtini1", data},
                {"date", "dtfim1", data},
                {"date", "dtini2", data},
                {"date", "dtfim2", data},
                {"date", "dtini3", data},
                {"date", "dtfim3", data},
                {"date", "dtini4", data},
                {"date", "dtfim4", data},
                {"date", "dtini5", data},
                {"date", "dtfim5", data},
                {"date", "dtini6", data},
                {"date", "dtfim6", data}
            });
            
            try {
                String _tipo = null, _rgprp = null, _rgimv = null, _contrato = null;
                while (rs.next()) {
                    try { _tipo = rs.getString("tipo"); } catch (SQLException e) { _tipo = null; }
                    try { _rgprp = rs.getString("rgprp"); } catch (SQLException e) { _rgprp = null; }
                    try { _rgimv = rs.getString("rgimv"); } catch (SQLException e) { _rgimv = null; }
                    try { _contrato = rs.getString("contrato"); } catch (SQLException e) { _contrato = null; }
                    
                    if (_tipo.equalsIgnoreCase("AVI")) {
                        // Aviso
                        String tmpCampo = "" + rs.getString("campo");
                        String tmpAuten = "" + rs.getString("rc_aut");
                        String[][] rCampos = FuncoesGlobais.treeArray(tmpCampo, false);
                        String sinq = FuncoesGlobais.DecriptaNome(rCampos[0][10]) + " - " + rCampos[0][7].substring(0, 2) + "/" + rCampos[0][7].substring(2,4) + "/" + rCampos[0][7].substring(4) + " - " + tmpAuten;
                        if (!"".equals(sinq.trim())) {
                            java.awt.Font ft = new java.awt.Font("Arial",Font.NORMAL,9);
                            List aLinhas = StringUtils.wrap(sinq, getFontMetrics(ft), 220);
                            for (Iterator linha = aLinhas.iterator(); linha.hasNext();) { lista.add(new classExtratoHistorico(data, (String)linha.next(), null, null, null, null)); }
                            int posicaoValor = lista.size() - 1;
                            if ("CRE".equals(rCampos[0][8])) {
                                saldo = saldo.add(new BigDecimal(LerValor.FormatNumber(rCampos[0][2],2).replace(".", "").replace(",", ".")));
                                lista.get(posicaoValor).setCredito(new BigDecimal(LerValor.FormatNumber(rCampos[0][2],2).replace(".", "").replace(",", ".")));
                            } else {
                                saldo = saldo.subtract(new BigDecimal(LerValor.FormatNumber(rCampos[0][2],2).replace(".", "").replace(",", ".")));
                                lista.get(posicaoValor).setDebito(new BigDecimal(LerValor.FormatNumber(rCampos[0][2],2).replace(".", "").replace(",", ".")));
                            }
                            lista.get(posicaoValor).setSaldo(saldo);
                        }
                    } if (_tipo.equalsIgnoreCase("EXT")) {
                        // Extrato
                        String tmpCampo = rs.getString("campo");
                        String[][] rCampos = FuncoesGlobais.treeArray(tmpCampo, true);

                        for (int j = 0; j<rCampos.length; j++) {

                            if (LerValor.StringToFloat(LerValor.FormatNumber(rCampos[j][2],2)) == 0) continue;

                            String tpCampo = rCampos[j][rCampos[j].length - 1];
                            String tpCota = "";
                            if (VariaveisGlobais.bShowCotaParcelaExtrato) {
                                String spart1 = "", spart2 = "", scotaparc = "";
                                if (!"".equals(rCampos[j][3].trim())) {
                                    spart1 = rCampos[j][3].trim().substring(0, 2);
                                    spart2 = rCampos[j][3].trim().substring(2);
                                } else {
                                    spart1 = "00"; spart2 = "0000";
                                }
                                if (!"00".equals(spart1) && "0000".equals(spart2)) {
                                    spart1 = "00";
                                } else if ("00".equals(spart1) && !"0000".equals(spart2)) {
                                    spart2 = "0000";
                                }
                                scotaparc = spart1 + spart2;
                                tpCota = ("0000".equals(scotaparc) || "000000".equals(scotaparc) || "".equals(scotaparc) ? "       " : scotaparc.substring(0,2) + "/" + scotaparc.substring(2));
                            }
                            boolean bRetc = (FuncoesGlobais.IndexOf(rCampos[j], "RT") > -1) || (FuncoesGlobais.IndexOf(rCampos[j], "AT") > -1);
                            if ("AL".equals(rCampos[j][4])) {
                                if (LerValor.isNumeric(rCampos[j][0])) {
                                    Object[][] hBusca = pegaDadosImoveis(rs.getString("rgimv"), new String[] {"end", "num", "compl"});

                                    java.awt.Font ft = new java.awt.Font("Arial",Font.NORMAL,8);
                                    String imv = rs.getString("rgimv").trim() + " - " + hBusca[0][3].toString().trim() + ", " + hBusca[1][3].toString().trim() + " " + hBusca[2][3].toString().trim();
                                    List aLinhas = StringUtils.wrap(imv, getFontMetrics(ft), 257);
                                    for (Iterator linha = aLinhas.iterator(); linha.hasNext();) { lista.add(new classExtratoHistorico(data,  StringManager.ConvStr((String) linha.next()).replace("ò", " "), null, null, null, null)); }

                                    String loc = pegaDadosLocatarios(rs.getString("contrato"), new String[] {"nomerazao"})[0][3].toString();
                                    aLinhas = null;
                                    aLinhas = StringUtils.wrap(loc, getFontMetrics(ft), 257);
                                    for (Iterator linha = aLinhas.iterator(); linha.hasNext();) { lista.add(new classExtratoHistorico(data, StringManager.ConvStr((String) linha.next()).replace("ò", " "), null, null, null, null)); }

                                    int posValor = lista.size() - 1;
                                    lista.get(posValor).setReferencia("[" + Dates.DateFormata("dd-MM-yyyy", rs.getDate("dtvencimento")) + "] - " + rs.getString("rc_aut"));
                                    
                                    saldo = saldo.add(new BigDecimal(LerValor.FormatNumber(rCampos[j][2],2).replace(".", "").replace(",", ".")));
                                    if (bRetc) saldo = saldo.subtract(new BigDecimal(LerValor.FormatNumber(rCampos[j][2],2).replace(".", "").replace(",", ".")));
                                    lista.add(new classExtratoHistorico(data, tpCampo, tpCota, new BigDecimal(LerValor.FormatNumber(rCampos[j][2],2).replace(".", "").replace(",", ".")), (bRetc ? new BigDecimal(LerValor.FormatNumber(rCampos[j][2],2).replace(".", "").replace(",", ".")) : null), saldo));

                                    int nPos = FuncoesGlobais.IndexOf(rCampos[j], "CM");
                                    if (nPos > -1) {
                                        saldo = saldo.subtract(new BigDecimal(LerValor.FormatNumber(rCampos[j][nPos].substring(2),2).replace(".", "").replace(",", ".")));
                                        lista.add(new classExtratoHistorico(data, gVar.get("CM"), null, null, new BigDecimal(LerValor.FormatNumber(rCampos[j][nPos].substring(2),2).replace(".", "").replace(",", ".")), saldo));
                                    }

                                    nPos = FuncoesGlobais.IndexOf(rCampos[j], "AD");
                                    if (nPos > -1) {
                                        if (LerValor.StringToFloat(LerValor.FormatNumber(rCampos[j][nPos].substring(9),2)) > 0) {
                                            String wAD = rCampos[j][nPos].split("@")[1];
                                            saldo = saldo.subtract(new BigDecimal(LerValor.FormatNumber(wAD,2).replace(".", "").replace(",", ".")));
                                            lista.add(new classExtratoHistorico(data, "Adiantamento", null, null, new BigDecimal(LerValor.FormatNumber(wAD,2).replace(".", "").replace(",", ".")), saldo));
                                        }
                                    }

                                    nPos = FuncoesGlobais.IndexOf(rCampos[j], "MU");
                                    if (nPos > -1) {
                                        if (LerValor.StringToFloat(LerValor.FormatNumber(rCampos[j][nPos].substring(2),2)) > 0) {
                                            saldo = saldo.add(new BigDecimal(LerValor.FormatNumber(rCampos[j][nPos].substring(2),2).replace(".", "").replace(",", ".")));
                                            lista.add(new classExtratoHistorico(data, gVar.get("MU"), null, new BigDecimal(LerValor.FormatNumber(rCampos[j][nPos].substring(2),2).replace(".", "").replace(",", ".")), null, saldo));
                                        }
                                    }

                                    nPos = FuncoesGlobais.IndexOf(rCampos[j], "JU");
                                    if (nPos > -1) {
                                        if (LerValor.StringToFloat(LerValor.FormatNumber(rCampos[j][nPos].substring(2),2)) > 0) {
                                            saldo = saldo.add(new BigDecimal(LerValor.FormatNumber(rCampos[j][nPos].substring(2),2).replace(".", "").replace(",", ".")));
                                            lista.add(new classExtratoHistorico(data, gVar.get("JU"), null, new BigDecimal(LerValor.FormatNumber(rCampos[j][nPos].substring(2),2).replace(".", "").replace(",", ".")), null, saldo));
                                        }
                                    }

                                    nPos = FuncoesGlobais.IndexOf(rCampos[j], "CO");
                                    if (nPos > -1) {
                                        if (LerValor.StringToFloat(LerValor.FormatNumber(rCampos[j][nPos].substring(2),2)) > 0) {
                                            saldo = saldo.add(new BigDecimal(LerValor.FormatNumber(rCampos[j][nPos].substring(2),2).replace(".", "").replace(",", ".")));
                                            lista.add(new classExtratoHistorico(data, gVar.get("CO"), null, new BigDecimal(LerValor.FormatNumber(rCampos[j][nPos].substring(2),2).replace(".", "").replace(",", ".")), null, saldo));
                                        }
                                    }

                                    nPos = FuncoesGlobais.IndexOf(rCampos[j], "EP");
                                    if (nPos > -1) {
                                        if (LerValor.StringToFloat(LerValor.FormatNumber(rCampos[j][nPos].substring(2),2)) > 0) {
                                            saldo = saldo.add(new BigDecimal(LerValor.FormatNumber(rCampos[j][nPos].substring(2),2).replace(".", "").replace(",", ".")));
                                            lista.add(new classExtratoHistorico(data, gVar.get("EP"), null, new BigDecimal(LerValor.FormatNumber(rCampos[j][nPos].substring(2),2).replace(".", "").replace(",", ".")), null, saldo));
                                        }
                                    }
                                } else {
                                    saldo = saldo.add(new BigDecimal(LerValor.FormatNumber(rCampos[j][2],2).replace(".", "").replace(",", ".")));
                                    if (bRetc) saldo = saldo.subtract(new BigDecimal(LerValor.FormatNumber(rCampos[j][2],2).replace(".", "").replace(",", ".")));
                                    lista.add(new classExtratoHistorico(data, tpCampo, null, new BigDecimal(LerValor.FormatNumber(rCampos[j][2],2).replace(".", "").replace(",", ".")), (bRetc ? new BigDecimal(LerValor.FormatNumber(rCampos[j][2],2).replace(".", "").replace(",", ".")) : null), saldo));
                                }
                            } else if (FuncoesGlobais.IndexOf(rCampos[j], "AD") > -1) {
                                int nPos = FuncoesGlobais.IndexOf(rCampos[j], "AD");
                                if (LerValor.StringToFloat(LerValor.FormatNumber(rCampos[j][nPos].split("@")[1],2)) > 0) {
                                    String wAD = rCampos[j][nPos].split("@")[1];
                                    saldo = saldo.subtract(new BigDecimal(LerValor.FormatNumber(wAD,2).replace(".", "").replace(",", ".")));
                                    lista.add(new classExtratoHistorico(data, "Adiantamento - " + rCampos[j][nPos].split("@")[0].substring(2), null, null, new BigDecimal(LerValor.FormatNumber(wAD,2).replace(".", "").replace(",", ".")), saldo));
                                }                        
                            } else if ("DC".equals(rCampos[j][4])) {
                                saldo = saldo.subtract(new BigDecimal(LerValor.FormatNumber(rCampos[j][2],2).replace(".", "").replace(",", ".")));
                                if (bRetc) saldo = saldo.add(new BigDecimal(LerValor.FormatNumber(rCampos[j][2],2).replace(".", "").replace(",", ".")));
                                lista.add(new classExtratoHistorico(data, tpCampo, null, (bRetc ? new BigDecimal(LerValor.FormatNumber(rCampos[j][2],2).replace(".", "").replace(",", ".")) : null), new BigDecimal(LerValor.FormatNumber(rCampos[j][2],2).replace(".", "").replace(",", ".")), saldo));
                            } else if ("DF".equals(rCampos[j][4])) {
                                saldo = saldo.add(new BigDecimal(LerValor.FormatNumber(rCampos[j][2],2).replace(".", "").replace(",", ".")));
                                if (bRetc) saldo = saldo.subtract(new BigDecimal(LerValor.FormatNumber(rCampos[j][2],2).replace(".", "").replace(",", ".")));
                                lista.add(new classExtratoHistorico(data, tpCampo, null, new BigDecimal(LerValor.FormatNumber(rCampos[j][2],2).replace(".", "").replace(",", ".")), (bRetc ? new BigDecimal(LerValor.FormatNumber(rCampos[j][2],2).replace(".", "").replace(",", ".")) : null), saldo));
                            } else if ("SG".equals(rCampos[j][4])) {
                                saldo = saldo.add(new BigDecimal(LerValor.FormatNumber(rCampos[j][2],2).replace(".", "").replace(",", ".")));
                                if (bRetc) saldo = saldo.subtract(new BigDecimal(LerValor.FormatNumber(rCampos[j][2],2).replace(".", "").replace(",", ".")));
                                lista.add(new classExtratoHistorico(data, tpCampo, null, new BigDecimal(LerValor.FormatNumber(rCampos[j][2],2).replace(".", "").replace(",", ".")), (bRetc ? new BigDecimal(LerValor.FormatNumber(rCampos[j][2],2).replace(".", "").replace(",", ".")) : null), saldo));
                            } else {
                                saldo = saldo.add(new BigDecimal(LerValor.FormatNumber(rCampos[j][2],2).replace(".", "").replace(",", ".")));
                                if (bRetc) saldo = saldo.subtract(new BigDecimal(LerValor.FormatNumber(rCampos[j][2],2).replace(".", "").replace(",", ".")));
                                lista.add(new classExtratoHistorico(data, tpCampo, null, new BigDecimal(LerValor.FormatNumber(rCampos[j][2],2).replace(".", "").replace(",", ".")), (bRetc ? new BigDecimal(LerValor.FormatNumber(rCampos[j][2],2).replace(".", "").replace(",", ".")) : null), saldo));
                            }
                        }
                    } else if (_tipo.equalsIgnoreCase("AUX")) {
                        // AUXILIAR
                        String tmpCampo = "" + rs.getString("campo");
                        String tmpAuten = "" + rs.getString("rc_aut");
                        String tmpValor = LerValor.FormatNumber(tmpCampo.substring(5,15),2);
                        lista.add(new classExtratoHistorico(data, "Retirada", tmpAuten, null, null, null)); 
                        int posicaoValor = lista.size() - 1;
                        saldo = saldo.subtract(new BigDecimal(tmpValor.replace(".", "").replace(",", ".")));
                        lista.get(posicaoValor).setDebito(new BigDecimal(tmpValor.replace(".", "").replace(",", ".")));
                        lista.get(posicaoValor).setSaldo(saldo);
                    }
                }
            } catch (SQLException sqlEx) {}
            db.CloseTable(rs);
        }

        ehStatus.setText("Relatório pronto...   [Visualização!]");
        ehProgress.setIndeterminate(false);
        ehProgress.setMaximum(0);
        ehProgress.setMinimum(0);
        ehProgress.setValue(0);
        
        Object dados_prop[][] = pegaDadosProprietario(rgprp, new String[] {"banco", "agencia", "conta", "favorecido","cpfcnpj","saldoant","nome"});
        String nomeProp = "";
        if (dados_prop != null) {
            nomeProp = dados_prop[6][3].toString();
        }
        
        JRBeanCollectionDataSource jrds = new JRBeanCollectionDataSource(lista);

        String sFileName = new tempFile("pdf").getsPathNameExt();
        String docName = new tempFile().getTempFileName(sFileName);
        String pathName = new tempFile().getTempPath();
        String FileNamePdf = pathName + docName;

        try {
            Map parametros = new HashMap();
            parametros.put("usuario", VariaveisGlobais.usuario);
            parametros.put("logo", "resources/logos/extrato/" + VariaveisGlobais.icoExtrato);
            parametros.put("proprietario", rgprp.trim() + " - " + nomeProp.trim());
            parametros.put("dtini", dtini);
            parametros.put("dtfim", dtfim);

            String nameReport = "ExtratoHistorico.jasper"; // Aqui vai o nome do relatório
            String fileName = "reports\\" + nameReport;
            JasperPrint print = JasperFillManager.fillReport(fileName, parametros, jrds);

            // Create a PDF exporter
            JRExporter exporter = new JRPdfExporter();

            // Configure the exporter (set output file name and print object)
            String outFileName = FileNamePdf;
            exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, outFileName);
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, print);

            // Export the PDF file
            exporter.exportReport();
        } catch (JRException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        if (!FileNamePdf.isEmpty()) new toPreview(FileNamePdf);
        
        ehStatus.setText("");
        ehProgress.setIndeterminate(false);
        ehProgress.setMaximum(0);
        ehProgress.setMinimum(0);
        ehProgress.setValue(0);

        jGerar.setEnabled(true);
    }
    
    private Object[][] pegaDadosProprietario(String rgprp, String[] campos) {
        ehStatus.setText("Pegando dados proprietário... [Aguarde]");
        Object[][] retorno = {};
        
        String selectSQL = "SELECT p.banco, p.agencia, p.conta, p.favorecido, " +
                           "p.cpfcnpj, p.saldoant, p.nome, p.email FROM proprietarios p " +
                           "WHERE p.rgprp = :rgprp1 UNION " +
                           "SELECT pe.banco, pe.agencia, pe.conta, pe.favorecido, " +
                           "pe.cpfcnpj, pe.saldoant, pe.nome, pe.email FROM " +
                           "jgeral_excluidos.proprietarios pe WHERE pe.rgprp = :rgprp2";
        ResultSet rs = db.OpenTable(selectSQL, new Object[][] {
            {"string", "rgprp1", rgprp},
            {"string", "rgprp2", rgprp}
        });
        
        int recCount = db.RecordCount(rs);
        ehProgress.setIndeterminate(false);
        ehProgress.setMaximum(recCount);
        ehProgress.setMinimum(0);
        ehProgress.setValue(0);
                
        try {                    
            int posPg = 0;
            String _banco = null, _agencia = null, _favorecido = null, _cpfcnpj = null;
            String _conta = null, _nome = null; int _saldoant = -1;
            while (rs.next()) {
                try { _banco = rs.getString("banco"); } catch (SQLException ex) { _banco = null; }
                try { _agencia = rs.getString("agencia"); } catch (SQLException ex) { _agencia = null; }
                try { _conta = rs.getString("conta"); } catch (SQLException ex) { _conta = null; }
                try { _favorecido = rs.getString("favorecido"); } catch (SQLException ex) { _favorecido = null; }
                try { _cpfcnpj = rs.getString("cpfcnpj"); } catch (SQLException ex) { _cpfcnpj = null; }
                try { _nome = rs.getString("nome"); } catch (SQLException ex) { _nome = null; }
                try { _saldoant = rs.getInt("saldoant"); } catch (SQLException ex) { _saldoant = -1; }
                
                Object[] banco = new Object[] {null, null, null, _banco};
                Object[] agencia = new Object[] {null, null, null, _agencia};
                Object[] conta = new Object[] {null, null, null, _conta};
                Object[] favorecido = new Object[] {null, null, null, _favorecido};
                Object[] cpfcnpj = new Object[] {null, null, null, _cpfcnpj};
                Object[] nome = new Object[] {null, null, null, _nome};
                Object[] saldoant = new Object[] {null, null, null, _saldoant};
                
                retorno = FuncoesGlobais.ObjectsAdd(retorno, banco);
                retorno = FuncoesGlobais.ObjectsAdd(retorno, agencia);
                retorno = FuncoesGlobais.ObjectsAdd(retorno, conta);
                retorno = FuncoesGlobais.ObjectsAdd(retorno, favorecido);
                retorno = FuncoesGlobais.ObjectsAdd(retorno, cpfcnpj);
                retorno = FuncoesGlobais.ObjectsAdd(retorno, saldoant);
                retorno = FuncoesGlobais.ObjectsAdd(retorno, nome);
                ehProgress.setValue(posPg++);
            }
        } catch (SQLException sqlEx) {}
        db.CloseTable(rs);
        return (retorno.length > 0 ? retorno : null);
    }
    
    private Object[][] pegaDadosImoveis(String rgimv, String[] campos) {
        ehStatus.setText("Pegando dados Imóveis... [Aguarde]");
        Object[][] retorno = {};
        
        String selectSQL = "SELECT i.`end` end, i.num, i.compl " +
                           "FROM imoveis i WHERE i.rgimv = :rgimv1 " +
                           "UNION SELECT ie.`end` end, ie.num, ie.compl " +
                           "FROM jgeral_excluidos.imoveis ie " +
                           "WHERE ie.rgimv = :rgimv2 LIMIT 1;";
        ResultSet rs = db.OpenTable(selectSQL, new Object[][] {
            {"string", "rgimv1", rgimv},
            {"string", "rgimv2", rgimv}
        });
        
        int recCount = db.RecordCount(rs);
        ehProgress.setIndeterminate(false);
        ehProgress.setMaximum(recCount);
        ehProgress.setMinimum(0);
        ehProgress.setValue(0);
                
        try {                    
            int posPg = 0;
            String _end = null, _num = null, _compl = null;
            while (rs.next()) {
                try { _end = rs.getString("end"); } catch (SQLException ex) { _end = null; }
                try { _num = rs.getString("num"); } catch (SQLException ex) { _num = null; }
                try { _compl = rs.getString("compl"); } catch (SQLException ex) { _compl = null; }
                
                Object[] end = new Object[] {null, null, null, _end};
                Object[] num = new Object[] {null, null, null, _num};
                Object[] compl = new Object[] {null, null, null, _compl};
                retorno = FuncoesGlobais.ObjectsAdd(retorno, end);
                retorno = FuncoesGlobais.ObjectsAdd(retorno, num);
                retorno = FuncoesGlobais.ObjectsAdd(retorno, compl);
                ehProgress.setValue(posPg++);
            }
        } catch (SQLException sqlEx) {}
        db.CloseTable(rs);
        return (retorno.length > 0 ? retorno : null);
    }
    
    private Object[][] pegaDadosLocatarios(String contrato, String[] campos) {
        ehStatus.setText("Pegando dados Locatários... [Aguarde]");
        Object[][] retorno = {};
        
        String selectSQL = "SELECT l.nomerazao FROM locatarios l " +
                           "WHERE l.contrato = :contrato1 UNION " +
                           "SELECT le.nomerazao FROM locatarios le " +
                           "WHERE le.contrato = :contrato2 LIMIT 1;";
        ResultSet rs = db.OpenTable(selectSQL, new Object[][] {
            {"string", "contrato1", contrato},
            {"string", "contrato2", contrato}
        });
        
        int recCount = db.RecordCount(rs);
        ehProgress.setIndeterminate(false);
        ehProgress.setMaximum(recCount);
        ehProgress.setMinimum(0);
        ehProgress.setValue(0);
                
        try {                    
            int posPg = 0;
            String _nomerazao = null;
            while (rs.next()) {
                try { _nomerazao = rs.getString("nomerazao"); } catch (SQLException ex) { _nomerazao = null; }
                
                Object[] nomerazao = new Object[] {null, null, null, _nomerazao};
                retorno = FuncoesGlobais.ObjectsAdd(retorno, nomerazao);
                ehProgress.setValue(posPg++);
            }
        } catch (SQLException sqlEx) {}
        db.CloseTable(rs);
        return (retorno.length > 0 ? retorno : null);
    }
    
    private BigDecimal pegaSaldoAnterior(Date data, String rgprp) {
        ehStatus.setText("Pegando saldo anterior... [Aguarde]");

        BigDecimal retorno = null;
        
        String selectSQL = "select e.pr_sdant from extrato e WHERE " +
                           "(e.rgprp = :rgprp1) AND e.tag != 'B' AND " +
                           "e.dtrecebimento BETWEEN :dtini1 AND :dtfim1 " +
                
                           "union select ex.pr_sdant from jgeral_excluidos.extrato ex " +
                           "WHERE (ex.rgprp = :rgprp2) AND ex.tag != 'B' AND " +
                           "ex.dtrecebimento BETWEEN :dtini2 AND :dtfim2 " +
                           "LIMIT 1 ";
        ResultSet rs = db.OpenTable(selectSQL, new Object[][] {
            {"string", "rgprp1", rgprp},
            {"string", "rgprp2", rgprp},
            {"date", "dtini1", data},
            {"date", "dtfim1", data},
            {"date", "dtini2", data},
            {"date", "dtfim2", data}
        });

        int recCount = db.RecordCount(rs);
        ehProgress.setIndeterminate(false);
        ehProgress.setMaximum(recCount);
        ehProgress.setMinimum(0);
        ehProgress.setValue(0);

        int posPg = 0;
        try {
            BigDecimal saldoAnterior = null;        
            while (rs.next()) {
                try { saldoAnterior = rs.getBigDecimal("pr_sdant"); } catch (SQLException ex) { saldoAnterior = null; }
                
                ehProgress.setValue(posPg++);
            }
        } catch (SQLException sqlEx) {}
        db.CloseTable(rs);
        return retorno;
    }
    
    private List<Date> retDatas(Date dtini, Date dtfim, String rgprp) {
        ehStatus.setText("Preparando informações... [Aguarde]");

        List<Date> retorno = new ArrayList<>();
        String selectSQL = "select distinct e.dtrecebimento from extrato e " +
        "WHERE (e.rgprp = :rgprp1) AND e.tag != 'B' AND " +
        "e.dtrecebimento BETWEEN :dtini1 AND :dtfim1 " +
                
        "union select distinct ex.dtrecebimento from jgeral_excluidos.extrato ex " +
        "WHERE (ex.rgprp = :rgprp2) AND ex.tag != 'B' AND " +
        "ex.dtrecebimento BETWEEN :dtini2 AND :dtfim2 " +
                
        "union SELECT distinct RetAvDataRid2(a.campo) dtrecebimento " +
        "FROM avisos a WHERE (a.registro = :registro1) AND a.rid = 0 and a.tag != 'B' AND " +
        "RetAvDataRid2(a.campo) BETWEEN :dtini3 AND :dtfim3 " + 
                
        "union SELECT distinct RetAvDataRid2(ax.campo) dtrecebimento " +
        "FROM jgeral_excluidos.avisos ax WHERE (ax.registro = :registro2) AND ax.rid = 0 and ax.tag != 'B' AND " +
        "RetAvDataRid2(ax.campo) BETWEEN :dtini4 AND :dtfim4 " + 
                
        "union SELECT distinct x.dtrecebimento FROM auxiliar x WHERE x.conta = 'EXT' AND " +
        "x.contrato = :auxiliar1 AND x.dtrecebimento BETWEEN :dtini5 AND :dtfim5 " +

        "union SELECT distinct xx.dtrecebimento FROM jgeral_excluidos.auxiliar xx WHERE xx.conta = 'EXT' AND " +
        "xx.contrato = :auxiliar2 AND xx.dtrecebimento BETWEEN :dtini6 AND :dtfim6 ORDER BY 1;";

        ResultSet rs = db.OpenTable(selectSQL, new Object[][] {
            {"string", "rgprp1", rgprp},
            {"string", "rgprp2", rgprp},
            {"string", "registro1", rgprp},
            {"string", "registro2", rgprp},
            {"string", "auxiliar1", rgprp},
            {"string", "auxiliar2", rgprp},
            {"date", "dtini1", dtini},
            {"date", "dtfim1", dtfim},
            {"date", "dtini2", dtini},
            {"date", "dtfim2", dtfim},
            {"date", "dtini3", dtini},
            {"date", "dtfim3", dtfim},
            {"date", "dtini4", dtini},
            {"date", "dtfim4", dtfim},
            {"date", "dtini5", dtini},
            {"date", "dtfim5", dtfim},
            {"date", "dtini6", dtini},
            {"date", "dtfim6", dtfim}
        });
        
        int recCount = db.RecordCount(rs);
        ehProgress.setIndeterminate(false);
        ehProgress.setMaximum(recCount);
        ehProgress.setMinimum(0);
        ehProgress.setValue(0);
                
        try {                    
            Date datas = null; int posPg = 0;
            while (rs.next()) {
                try { datas = rs.getDate("dtrecebimento"); } catch (SQLException ex) { datas = null; }
                if (datas != null) retorno.add(datas);
                
                ehProgress.setValue(posPg++);
            }
        } catch (SQLException sqlEx) {}
        db.CloseTable(rs);
        return retorno.size() > 0 ? retorno : null;
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jGerar = new javax.swing.JButton();
        jDtIni = new com.toedter.calendar.JDateChooser("dd/MM/yyyy", "##/##/#####", '_');
        jLabel2 = new javax.swing.JLabel();
        jDtFim = new com.toedter.calendar.JDateChooser("dd/MM/yyyy", "##/##/#####", '_');
        jLabel3 = new javax.swing.JLabel();
        jRgprp = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        jNomeProp = new javax.swing.JComboBox();
        ehStatus = new javax.swing.JLabel();
        ehProgress = new javax.swing.JProgressBar();

        setClosable(true);
        setMaximumSize(new java.awt.Dimension(656, 157));
        setMinimumSize(new java.awt.Dimension(656, 157));

        jLabel1.setText("Data:");

        jGerar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icones/Actions-document-print-preview-icon.png"))); // NOI18N
        jGerar.setText("Preview");
        jGerar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jGerarActionPerformed(evt);
            }
        });

        jLabel2.setText("Até");

        jLabel3.setText("Rgprp:");

        jRgprp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRgprpActionPerformed(evt);
            }
        });

        jLabel4.setText("Nome:");

        jNomeProp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jNomePropActionPerformed(evt);
            }
        });

        ehStatus.setText("Progresso...");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ehStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jRgprp, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jNomeProp, 0, 430, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jDtIni, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jDtFim, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jGerar))
                    .addComponent(ehProgress, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jRgprp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(jNomeProp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jGerar, javax.swing.GroupLayout.Alignment.CENTER)
                        .addComponent(jDtFim, javax.swing.GroupLayout.Alignment.CENTER, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jDtIni, javax.swing.GroupLayout.Alignment.CENTER, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(2, 2, 2)))
                .addGap(13, 13, 13)
                .addComponent(ehStatus)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ehProgress, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jDtFim, jDtIni});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jGerarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jGerarActionPerformed
        if (!Dates.isDateValid(Dates.DateFormata("dd-MM-yyyy", jDtIni.getDate()), "dd-MM-yyyy")) {
            JOptionPane.showMessageDialog(this, "Data inicial vazia ou inválida!", "Atenção", JOptionPane.ERROR_MESSAGE);
            jDtIni.requestFocus();
            return;
        }

        if (!Dates.isDateValid(Dates.DateFormata("dd-MM-yyyy", jDtFim.getDate()), "dd-MM-yyyy")) {
            JOptionPane.showMessageDialog(this, "Data final vazia ou inválida!", "Atenção", JOptionPane.ERROR_MESSAGE);
            jDtFim.requestFocus();
            return;
        }

        new SimpleThread().start();
    }//GEN-LAST:event_jGerarActionPerformed

    class SimpleThread extends Thread {
        public SimpleThread() {
        }
        public void run() {
            HistoricoPreview(jDtIni.getDate(), jDtFim.getDate(), jRgprp.getSelectedItem().toString());
        }
    }
    
    private void jRgprpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRgprpActionPerformed
        if (!bExecNome) {
            int pos = jRgprp.getSelectedIndex();
            if (jNomeProp.getItemCount() > 0) {bExecCodigo = true; jNomeProp.setSelectedIndex(pos); bExecCodigo = false;}
        }
    }//GEN-LAST:event_jRgprpActionPerformed

    private void jNomePropActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jNomePropActionPerformed
        if (!bExecCodigo) {
            int pos = jNomeProp.getSelectedIndex();
            if (jRgprp.getItemCount() > 0) {bExecNome = true; jRgprp.setSelectedIndex(pos); bExecNome = false; }
        }
    }//GEN-LAST:event_jNomePropActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JProgressBar ehProgress;
    private javax.swing.JLabel ehStatus;
    private com.toedter.calendar.JDateChooser jDtFim;
    private com.toedter.calendar.JDateChooser jDtIni;
    private javax.swing.JButton jGerar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JComboBox jNomeProp;
    private javax.swing.JComboBox jRgprp;
    // End of variables declaration//GEN-END:variables
}
