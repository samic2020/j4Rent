/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BancosDigital;

import Funcoes.Autenticacao;
import Funcoes.CentralizaTela;
import Funcoes.Dates;
import Funcoes.Db;
import Funcoes.DbMain;
import Funcoes.FuncoesGlobais;
import Funcoes.LerValor;
import Funcoes.Outlook;
import Funcoes.StringManager;
import Funcoes.TableControl;
import Funcoes.VariaveisGlobais;
import Funcoes.gmail.GmailAPI;
import static Funcoes.gmail.GmailOperations.createEmailWithAttachment;
import static Funcoes.gmail.GmailOperations.createMessageWithEmail;
import Funcoes.jDirectory;
import Funcoes.jTableControl;
import Funcoes.toPreview;
import Funcoes.toPrint2;
import Movimento.BoletasCentral.BancosBoleta;
import Movimento.BoletasCentral.BoletaTreeTableModel;
import Movimento.BoletasCentral.PessoasBoleta;
import Protocolo.Calculos;
import Protocolo.DepuraCampos;
import Protocolo.DivideCC;
import Protocolo.ReCalculos;
import boleta.Boleta;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import com.toedter.calendar.JTextFieldDateEditor;
import j4rent.Partida.Collections;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.File;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.mail.internet.MimeMessage;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.INFORMATION_MESSAGE;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.undo.UndoManager;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import org.jdesktop.swingx.JXTreeTable;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author desenvolvimento-pc
 */
public class BancosDigital extends javax.swing.JInternalFrame {
    private JEditorPane _htmlPane = new JEditorPane();

    private final UndoManager undoManager = new UndoManager();
    private final UndoAction undoAction = new UndoAction();
    private final RedoAction redoAction = new RedoAction();

    private JXTreeTable treeTable;
    private List<BancosBoleta> bancosBoleta;

    private JXTreeTable treeTablePrinted;
    
    private JTable treeTableErros;
    private List<BancosErros> bancosBoletaErros;

    DbMain conn = VariaveisGlobais.conexao;
    Db db = new Db();

    String[] month;
    int[] dmonth;

    private String codErro;
    private String msgErro;

    jTableControl tabela = new jTableControl(true);    
    
    boolean bExecNome = false, bExecCodigo = false;
    
    private String token = null;
    private Date token_hora;
    
    public BancosDigital() {
        initComponents();

        jSemEnvio.setVisible(VariaveisGlobais.funcao.equalsIgnoreCase("sys"));

        lbl_Status.setText("");
        VencBoleto.setMinSelectableDate(Calendar.getInstance().getTime());  
        VencBoleto.setDate(new Date());
        JTextFieldDateEditor editor = (JTextFieldDateEditor) VencBoleto.getDateEditor();
        editor.setEditable(false);        
        
        this.month = new String[]{"Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"};
        this.dmonth = new int[]{31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        MesRef.setValue(this.month[(new Date()).getMonth()]);
        AnoRef.setValue(1900 + new Date().getYear());
        
        EditorEmail();
        _htmlPane.setText("Prezado(a) Sr(a),\n\nSegue em anexo boleto mensal com vencimento próximo.\n\nSem demais,\nAdministração.");
        /*
        / Setar dataspara consulta de boletos
        */
        {
            FillBancos();
            conDataInicial.setDate(Dates.primeiraDataMes(new Date()));
            conDataFinal.setDate(Dates.ultimoDataMes(new Date()));
            jBtnBaixar.setEnabled(false);
            jBtnBaixar.setText("Baixar [000]");
        }
        
        /*
        / Avulsas
        */
        {
            Clean();
            CleanHist();
            FillOpcao();
            LerMsgAv();

            opcprop.setSelected(true);
        }
        if (!VariaveisGlobais.usuario.toLowerCase().equals("samic")) jBtnBaixar.setVisible(false);
    }

    private void LerMsgAv() {
        String avsMSG01 = ""; try { avsMSG01 = conn.LerParametros("avsMSG01"); } catch (Exception e) {}
        String avsMSG02 = ""; try { avsMSG02 = conn.LerParametros("avsMSG02"); } catch (Exception e) {}
        String avsMSG03 = ""; try { avsMSG03 = conn.LerParametros("avsMSG03"); } catch (Exception e) {}
        String avsMSG04 = ""; try { avsMSG04 = conn.LerParametros("avsMSG04"); } catch (Exception e) {}
        String avsMSG05 = ""; try { avsMSG05 = conn.LerParametros("avsMSG05"); } catch (Exception e) {}
        String avsMSG06 = ""; try { avsMSG06 = conn.LerParametros("avsMSG06"); } catch (Exception e) {}
        String avsMSG07 = ""; try { avsMSG07 = conn.LerParametros("avsMSG07"); } catch (Exception e) {}
        String avsMSG08 = ""; try { avsMSG08 = conn.LerParametros("avsMSG08"); } catch (Exception e) {}
        String avsMSG09 = ""; try { avsMSG09 = conn.LerParametros("avsMSG09"); } catch (Exception e) {}
        
        jAvMsg01.setText(avsMSG01); jAvMsg02.setText(avsMSG02);
        jAvMsg03.setText(avsMSG03); jAvMsg04.setText(avsMSG04);
        jAvMsg05.setText(avsMSG05); jAvMsg06.setText(avsMSG06);
        jAvMsg07.setText(avsMSG07); jAvMsg08.setText(avsMSG08);
        jAvMsg09.setText(avsMSG09);
    }
    
    private void FillOpcao() {
        btLimpar.setVisible(false);
        jcodigo.setEnabled(true);
        jnome.setEnabled(true);
        
        int opc = -1;
        if (opcprop.isSelected()) {
            Enabled(false);
            opc = 0;
        } else if (opcloca.isSelected()) {
            Enabled(false);
            opc = 1;
        } else {
            opc = -1;
            btLimpar.setVisible(true);
            jcodigo.setEnabled(false);
            jnome.setEnabled(false);
            Enabled(true);
            Clean();
            pnome.requestFocus();
        }
        
        if (opc == 0 || opc == 1) {
            String sql = null;
            if (opc == 0) { 
                sql = "SELECT rgprp AS codigo, Upper(nome) AS nome FROM proprietarios ORDER BY nome;"; 
            } else {
                sql = "SELECT contrato AS codigo, Upper(nomerazao) AS nome FROM locatarios ORDER BY nomerazao;"; 
            }
            ResultSet rs = conn.AbrirTabela(sql, ResultSet.CONCUR_READ_ONLY);
            jcodigo.removeAllItems();
            jnome.removeAllItems();
            try {
                while (rs.next()) {
                    jcodigo.addItem(rs.getString("codigo"));
                    jnome.addItem(rs.getString("nome"));
                }
            } catch (Exception e) {}
            DbMain.FecharTabela(rs);
            
            jcodigo.requestFocus();
        }
    }
    
    private void FillBancos() {
        String sql = "SELECT codigo nbanco, nome FROM bancos WHERE EXISTS(SELECT * FROM bancos_digital WHERE codigo = nbanco);";
        ResultSet rs = conn.AbrirTabela(sql, ResultSet.CONCUR_READ_ONLY);
        try {
            jbanco.removeAllItems();
            jcbConsultaBancos.removeAllItems();
            while (rs.next()) {
                jbanco.addItem(rs.getString("nbanco") + " - " + rs.getString("nome"));
                jcbConsultaBancos.addItem(rs.getString("nbanco") + " - " + rs.getString("nome"));
            }
        } catch (Exception e) {e.printStackTrace();}
        DbMain.FecharTabela(rs);
    }
    
    private void Clean() {
        pnome.setText(null);
        pdocumento.setText(null);
        pendereco.setText(null);
        pnumero.setText(null);
        pcomplto.setText(null);
        pbairro.setText(null);
        pcidade.setText(null);
        pestado.setText(null);
        pcep.setText(null);
    }
    
    private void CleanHist() {
        phistorico01.setText("");
        jvencto.setDate(new Date());
        jvalor.setText("0,00");
    }
    
    private void Enabled(boolean endi) {
        pnome.setEditable(endi);
        pdocumento.setEditable(endi);
        pendereco.setEditable(endi);
        pnumero.setEditable(endi);
        pcomplto.setEditable(endi);
        pbairro.setEditable(endi);
        pcidade.setEditable(endi);
        pestado.setEditable(endi);
        pcep.setEditable(endi);
    }
    
    private void Setar() {
        if (jcodigo.getSelectedItem() == null) return;
        
        String sql = null;
        if (opcprop.isSelected()) {
            sql = "SELECT nome, end, num, compl, bairro, cidade, estado, cep, cpfcnpj FROM proprietarios WHERE rgprp = '" + jcodigo.getSelectedItem().toString() + "';";
        } else if (opcloca.isSelected()) {
            sql = "SELECT l.nomerazao AS nome, i.end, i.num, i.compl, i.bairro, i.cidade, i.estado, i.cep, l.cpfcnpj FROM locatarios l, imoveis i WHERE (l.rgprp = i.rgprp AND l.rgimv = i.rgimv) AND l.contrato = '" + jcodigo.getSelectedItem().toString() + "';";
        }
        
        Clean();
        ResultSet rs = conn.AbrirTabela(sql, ResultSet.CONCUR_READ_ONLY);
        try {
            while (rs.next()) {
                pnome.setText(rs.getString("nome"));
                pdocumento.setText(rs.getString("cpfcnpj"));
                pendereco.setText(rs.getString("end"));
                pnumero.setText(rs.getString("num"));
                pcomplto.setText(rs.getString("compl"));
                pbairro.setText(rs.getString("bairro"));
                pcidade.setText(rs.getString("cidade"));
                pestado.setText(rs.getString("estado"));
                pcep.setText(rs.getString("cep"));
            }
        } catch (Exception e) {}
        DbMain.FecharTabela(rs);
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        filler7 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));
        buttonGroup2 = new javax.swing.ButtonGroup();
        jAletas = new javax.swing.JTabbedPane();
        jBoletos = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jemdia = new javax.swing.JRadioButton();
        jatrasados = new javax.swing.JRadioButton();
        jtodos = new javax.swing.JRadioButton();
        jperiodo = new javax.swing.JRadioButton();
        jPanel15 = new javax.swing.JPanel();
        VencBoleto = new com.toedter.calendar.JDateChooser("dd/MM/yyyy", "##/##/#####", '_');
        jLabel8 = new javax.swing.JLabel();
        jPeriodo = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jInicial = new com.toedter.calendar.JDateChooser("dd/MM/yyyy", "##/##/#####", '_');
        jLabel3 = new javax.swing.JLabel();
        jFinal = new com.toedter.calendar.JDateChooser("dd/MM/yyyy", "##/##/#####", '_');
        jPanel4 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jContrato = new javax.swing.JFormattedTextField();
        jListar = new javax.swing.JButton();
        jGerar = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        MesRef = new javax.swing.JSpinner();
        AnoRef = new javax.swing.JSpinner();
        lbl_Status = new javax.swing.JLabel();
        jProgress = new javax.swing.JProgressBar();
        jProgress1 = new javax.swing.JProgressBar();
        jProgress2 = new javax.swing.JProgressBar();
        jLabel6 = new javax.swing.JLabel();
        ListaBancosPessoasImpressas = new javax.swing.JScrollPane();
        jLabel7 = new javax.swing.JLabel();
        ListaBancosPessoas = new javax.swing.JScrollPane();
        jLabel14 = new javax.swing.JLabel();
        ListaBancosPessoasErros = new javax.swing.JScrollPane();
        jEnvio = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        tblEmails = new javax.swing.JTable();
        jPanel10 = new javax.swing.JPanel();
        jProgressEmail = new javax.swing.JProgressBar();
        btnEnviarTodos = new javax.swing.JButton();
        btnEnviarSelecao = new javax.swing.JButton();
        btnEditarCadastro = new javax.swing.JButton();
        btnListarBoletasEmail = new javax.swing.JButton();
        jSemEnvio = new javax.swing.JCheckBox();
        jPanel11 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        edtSubJect = new javax.swing.JTextField();
        jPanel12 = new javax.swing.JPanel();
        FontName = new javax.swing.JComboBox();
        FontSize = new javax.swing.JComboBox();
        FontBold = new javax.swing.JToggleButton();
        FontItalic = new javax.swing.JToggleButton();
        FontUnderLine = new javax.swing.JToggleButton();
        jSeparator3 = new javax.swing.JSeparator();
        TextAlignLeft = new javax.swing.JToggleButton();
        TextAlignCenter = new javax.swing.JToggleButton();
        TextAlignRigth = new javax.swing.JToggleButton();
        TextAlignJustified = new javax.swing.JToggleButton();
        FontColor = new javax.swing.JButton();
        FontBackColor = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        pScroll = new javax.swing.JScrollPane();
        jConsulta = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jTipoListagem = new javax.swing.JComboBox<>();
        jLabel10 = new javax.swing.JLabel();
        conDataInicial = new com.toedter.calendar.JDateChooser("dd/MM/yyyy", "##/##/#####", '_');
        jLabel11 = new javax.swing.JLabel();
        conDataFinal = new com.toedter.calendar.JDateChooser("dd/MM/yyyy", "##/##/#####", '_');
        conBtnListar = new javax.swing.JButton();
        jLabel13 = new javax.swing.JLabel();
        jcbConsultaBancos = new javax.swing.JComboBox<>();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        jPanel5 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        conLista = new javax.swing.JTable();
        jProgressListaBoletasConsulta = new javax.swing.JProgressBar();
        jProgressListaBoletasConsulta1 = new javax.swing.JProgressBar();
        jBtnBaixar = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        jPanel8 = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        baiQuantidade = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        baiValor = new javax.swing.JTextField();
        filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        jPanel13 = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        recQuantidade = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        recValor = new javax.swing.JTextField();
        filler5 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        jPanel14 = new javax.swing.JPanel();
        jLabel20 = new javax.swing.JLabel();
        expQuantidade = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        expValor = new javax.swing.JTextField();
        filler6 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        jPanel16 = new javax.swing.JPanel();
        jLabel22 = new javax.swing.JLabel();
        preQuantidade = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        preValor = new javax.swing.JTextField();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        jPanel19 = new javax.swing.JPanel();
        jLabel32 = new javax.swing.JLabel();
        canQuantidade = new javax.swing.JTextField();
        jLabel33 = new javax.swing.JLabel();
        canValor = new javax.swing.JTextField();
        jPanel17 = new javax.swing.JPanel();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel20 = new javax.swing.JPanel();
        opcprop = new javax.swing.JRadioButton();
        opcloca = new javax.swing.JRadioButton();
        opcavulsa = new javax.swing.JRadioButton();
        jnome = new javax.swing.JComboBox();
        jcodigo = new javax.swing.JComboBox();
        jLabel15 = new javax.swing.JLabel();
        pnome = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        pdocumento = new javax.swing.JTextField();
        jLabel25 = new javax.swing.JLabel();
        pendereco = new javax.swing.JTextField();
        pnumero = new javax.swing.JTextField();
        pcomplto = new javax.swing.JTextField();
        jLabel26 = new javax.swing.JLabel();
        pbairro = new javax.swing.JTextField();
        jLabel27 = new javax.swing.JLabel();
        pcidade = new javax.swing.JTextField();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        btImprimir = new javax.swing.JButton();
        jvalor = new javax.swing.JFormattedTextField();
        jvencto = new com.toedter.calendar.JDateChooser("dd/MM/yyyy", "##/##/#####", '_');
        pcep = new javax.swing.JFormattedTextField();
        pestado = new javax.swing.JFormattedTextField();
        btLimpar = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel36 = new javax.swing.JLabel();
        jbanco = new javax.swing.JComboBox<>();
        jPanel18 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        phistorico01 = new javax.swing.JEditorPane();
        jPanel21 = new javax.swing.JPanel();
        jLabel37 = new javax.swing.JLabel();
        jAvMsg01 = new javax.swing.JTextField();
        jAvMsg02 = new javax.swing.JTextField();
        jAvMsg03 = new javax.swing.JTextField();
        jAvMsg04 = new javax.swing.JTextField();
        jAvMsg05 = new javax.swing.JTextField();
        jAvMsg06 = new javax.swing.JTextField();
        jAvMsg07 = new javax.swing.JTextField();
        jLabel38 = new javax.swing.JLabel();
        jAvMsg08 = new javax.swing.JTextField();
        jAvMsg09 = new javax.swing.JTextField();
        jmsgGravar = new javax.swing.JButton();
        jLabel39 = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        jLabel45 = new javax.swing.JLabel();
        jLabel46 = new javax.swing.JLabel();

        setClosable(true);
        setIconifiable(true);
        setTitle(".:: Bancos Digitais (Impressão e Controle de Boletos)");
        setAlignmentX(0.0F);
        setAlignmentY(0.0F);
        setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        setMaximumSize(new java.awt.Dimension(925, 648));
        setMinimumSize(new java.awt.Dimension(925, 648));
        setNormalBounds(new java.awt.Rectangle(0, 0, 0, 0));
        setPreferredSize(new java.awt.Dimension(925, 648));
        setVerifyInputWhenFocusTarget(false);
        setVisible(true);

        jAletas.setAlignmentX(0.0F);
        jAletas.setAlignmentY(0.0F);
        jAletas.setFocusable(false);
        jAletas.setMaximumSize(new java.awt.Dimension(853, 650));
        jAletas.setMinimumSize(new java.awt.Dimension(853, 650));

        jPanel2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Condição"));
        jPanel3.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N

        buttonGroup1.add(jemdia);
        jemdia.setFont(new java.awt.Font("Dialog", 1, 9)); // NOI18N
        jemdia.setSelected(true);
        jemdia.setText("Em Dia");
        jemdia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jemdiaActionPerformed(evt);
            }
        });

        buttonGroup1.add(jatrasados);
        jatrasados.setFont(new java.awt.Font("Dialog", 1, 9)); // NOI18N
        jatrasados.setText("Atrasados");
        jatrasados.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jatrasadosActionPerformed(evt);
            }
        });

        buttonGroup1.add(jtodos);
        jtodos.setFont(new java.awt.Font("Dialog", 1, 9)); // NOI18N
        jtodos.setText("Todos");
        jtodos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtodosActionPerformed(evt);
            }
        });

        buttonGroup1.add(jperiodo);
        jperiodo.setFont(new java.awt.Font("Dialog", 1, 9)); // NOI18N
        jperiodo.setText("Por período");
        jperiodo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jperiodoActionPerformed(evt);
            }
        });

        VencBoleto.setDate(new java.util.Date(-2208977612000L));
        VencBoleto.setEnabled(false);
        VencBoleto.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N

        jLabel8.setText("Vencimento Boleto");

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addComponent(jLabel8)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(VencBoleto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(VencBoleto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jatrasados)
                    .addComponent(jtodos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jperiodo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jemdia, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jemdia)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jatrasados)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtodos)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jperiodo))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(13, 13, 13)
                        .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPeriodo.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Período"));
        jPeriodo.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N

        jLabel2.setFont(new java.awt.Font("Dialog", 1, 9)); // NOI18N
        jLabel2.setText("Inicio:");

        jInicial.setDate(new java.util.Date(-2208977612000L));
        jInicial.setEnabled(false);
        jInicial.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N

        jLabel3.setFont(new java.awt.Font("Dialog", 1, 9)); // NOI18N
        jLabel3.setText("Final:");

        jFinal.setDate(new java.util.Date(-2208977612000L));
        jFinal.setEnabled(false);
        jFinal.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N

        javax.swing.GroupLayout jPeriodoLayout = new javax.swing.GroupLayout(jPeriodo);
        jPeriodo.setLayout(jPeriodoLayout);
        jPeriodoLayout.setHorizontalGroup(
            jPeriodoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPeriodoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPeriodoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPeriodoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jInicial, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jFinal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPeriodoLayout.setVerticalGroup(
            jPeriodoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPeriodoLayout.createSequentialGroup()
                .addGroup(jPeriodoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jInicial, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPeriodoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jFinal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true), "Unitário"));
        jPanel4.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N

        jLabel4.setFont(new java.awt.Font("Dialog", 1, 9)); // NOI18N
        jLabel4.setText("Contrato:");

        jContrato.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        jContrato.setMinimumSize(new java.awt.Dimension(6, 20));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jContrato, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jContrato, javax.swing.GroupLayout.DEFAULT_SIZE, 20, Short.MAX_VALUE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(5, 5, 5))
        );

        jListar.setFont(new java.awt.Font("Dialog", 1, 9)); // NOI18N
        jListar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Figuras/next.png"))); // NOI18N
        jListar.setText("Listar Locatários");
        jListar.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        jListar.setIconTextGap(5);
        jListar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jListarActionPerformed(evt);
            }
        });

        jGerar.setFont(new java.awt.Font("Dialog", 1, 9)); // NOI18N
        jGerar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Figuras/ok.png"))); // NOI18N
        jGerar.setText("Gerar Boletos");
        jGerar.setToolTipText("");
        jGerar.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jGerar.setIconTextGap(5);
        jGerar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jGerarActionPerformed(evt);
            }
        });

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true), "Mês de Referência"));
        jPanel6.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N

        MesRef.setModel(new javax.swing.SpinnerListModel(new String[] {"Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"}));

        AnoRef.setModel(new javax.swing.SpinnerNumberModel(2018, 2010, 2030, 1));

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(MesRef, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(AnoRef)
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(MesRef, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(AnoRef, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        lbl_Status.setFont(new java.awt.Font("Tahoma", 2, 11)); // NOI18N
        lbl_Status.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        jProgress.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        jProgress.setStringPainted(true);

        jProgress1.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        jProgress1.setStringPainted(true);

        jProgress2.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        jProgress2.setStringPainted(true);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPeriodo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jListar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jGerar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jProgress, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jProgress1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jProgress2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_Status, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPeriodo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jListar, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jProgress2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbl_Status, javax.swing.GroupLayout.PREFERRED_SIZE, 13, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jGerar, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jProgress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jProgress1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel6.setBackground(new java.awt.Color(153, 204, 255));
        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(0, 51, 255));
        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icones/Asp.net_0031_color1_16x16.gif"))); // NOI18N
        jLabel6.setText("Boletos já impressos, para reimprimi-los ultilize a função de 2ª via no menu de Relatórios");
        jLabel6.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jLabel6.setIconTextGap(8);
        jLabel6.setOpaque(true);

        ListaBancosPessoasImpressas.setBackground(new java.awt.Color(255, 255, 255));

        jLabel7.setBackground(new java.awt.Color(153, 204, 255));
        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(0, 51, 255));
        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icones/plus.png"))); // NOI18N
        jLabel7.setText("Selecione na Lista os locatários para impressão do boleto.");
        jLabel7.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jLabel7.setIconTextGap(8);
        jLabel7.setOpaque(true);

        ListaBancosPessoas.setBackground(new java.awt.Color(255, 255, 255));

        jLabel14.setBackground(new java.awt.Color(153, 204, 255));
        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(0, 51, 255));
        jLabel14.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icones/Asp.net_0031_color1_16x16.gif"))); // NOI18N
        jLabel14.setText("Boletos rejeitados pelo banco. Corrija o erro e faça a reemissão.");
        jLabel14.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jLabel14.setIconTextGap(8);
        jLabel14.setOpaque(true);

        ListaBancosPessoasErros.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout jBoletosLayout = new javax.swing.GroupLayout(jBoletos);
        jBoletos.setLayout(jBoletosLayout);
        jBoletosLayout.setHorizontalGroup(
            jBoletosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jBoletosLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addGroup(jBoletosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel14, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(ListaBancosPessoas, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(ListaBancosPessoasImpressas, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ListaBancosPessoasErros, javax.swing.GroupLayout.Alignment.LEADING))
                .addContainerGap())
        );
        jBoletosLayout.setVerticalGroup(
            jBoletosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jBoletosLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jBoletosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jBoletosLayout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addGap(0, 0, 0)
                        .addComponent(ListaBancosPessoasImpressas, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ListaBancosPessoas, javax.swing.GroupLayout.PREFERRED_SIZE, 249, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ListaBancosPessoasErros)))
                .addGap(11, 11, 11))
        );

        jAletas.addTab("Geração de Boletos", jBoletos);

        tblEmails.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Contrato", "Nome", "Vencimento", "Boleta", "nnumero", "FileName"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane5.setViewportView(tblEmails);

        jPanel10.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jProgressEmail.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        jProgressEmail.setStringPainted(true);

        btnEnviarTodos.setText("Enviar Todas");
        btnEnviarTodos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEnviarTodosActionPerformed(evt);
            }
        });

        btnEnviarSelecao.setText("Enviar Selecionada(s)");
        btnEnviarSelecao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEnviarSelecaoActionPerformed(evt);
            }
        });

        btnEditarCadastro.setText("Editar Cadastro");
        btnEditarCadastro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditarCadastroActionPerformed(evt);
            }
        });

        btnListarBoletasEmail.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Figuras/previous.png"))); // NOI18N
        btnListarBoletasEmail.setText("Listar Boletas para E-Mail");
        btnListarBoletasEmail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnListarBoletasEmailActionPerformed(evt);
            }
        });

        jSemEnvio.setText("Sem envio - Só Gerente");

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jProgressEmail, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnEnviarTodos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnEnviarSelecao, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnEditarCadastro, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnListarBoletasEmail, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jSemEnvio, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnListarBoletasEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSemEnvio)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnEnviarTodos, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnEnviarSelecao, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnEditarCadastro, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jProgressEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel11.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel5.setText("Assunto:");

        edtSubJect.setText("Boleto com vencimento proximo");

        FontName.setToolTipText("Tipo da Fonte");

        FontSize.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "6", "7", "8", "10", "12", "14", "16", "18", "20", "22", "24", "26", "32", "64", "72" }));
        FontSize.setToolTipText("Tamanho da Fonte");

        FontBold.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icones/webmaster_2532_text_bold.png"))); // NOI18N
        FontBold.setToolTipText("Negrito");

        FontItalic.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icones/webmaster_2533_text_italic.png"))); // NOI18N
        FontItalic.setToolTipText("Itálico");

        FontUnderLine.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icones/webmaster_2536_text_underline.png"))); // NOI18N
        FontUnderLine.setToolTipText("Sublinhado");

        TextAlignLeft.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icones/webmaster_2531_text_align_left.png"))); // NOI18N
        TextAlignLeft.setSelected(true);
        TextAlignLeft.setToolTipText("Alinhamento a Esquerda");

        TextAlignCenter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icones/webmaster_2531_text_align_center.png"))); // NOI18N
        TextAlignCenter.setToolTipText("Alinhamento ao Centro");

        TextAlignRigth.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icones/webmaster_2531_text_align_right.png"))); // NOI18N
        TextAlignRigth.setToolTipText("Alinhamento a Direita");

        TextAlignJustified.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icones/webmaster_2531_text_align_justify.png"))); // NOI18N
        TextAlignJustified.setToolTipText("Alinhamento Justificado");

        FontColor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icones/editor_0304_text_foregroundcolor.png"))); // NOI18N
        FontColor.setToolTipText("Cor da Fonte");

        FontBackColor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icones/editor_0304_text_backgroundcolor.png"))); // NOI18N
        FontBackColor.setToolTipText("Cor do Fundo");

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addComponent(FontName, javax.swing.GroupLayout.PREFERRED_SIZE, 214, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(FontSize, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(FontBold, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(FontItalic, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(FontUnderLine, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(TextAlignLeft, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(TextAlignCenter, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(TextAlignRigth, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(TextAlignJustified, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addComponent(FontColor, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(FontBackColor, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(FontBold)
                        .addComponent(FontItalic)
                        .addComponent(FontUnderLine))
                    .addComponent(jSeparator3)
                    .addComponent(TextAlignCenter, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(TextAlignRigth, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(TextAlignLeft, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(TextAlignJustified, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(FontBackColor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(FontColor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(FontSize)
                    .addComponent(FontName)
                    .addComponent(jSeparator1))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(edtSubJect))
                    .addComponent(pScroll)
                    .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(edtSubJect, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pScroll, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 593, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jPanel11, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(31, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jEnvioLayout = new javax.swing.GroupLayout(jEnvio);
        jEnvio.setLayout(jEnvioLayout);
        jEnvioLayout.setHorizontalGroup(
            jEnvioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
            .addGroup(jEnvioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jEnvioLayout.setVerticalGroup(
            jEnvioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
            .addGroup(jEnvioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jEnvioLayout.createSequentialGroup()
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        jAletas.addTab("Envio", jEnvio);

        jConsulta.setAlignmentX(0.0F);
        jConsulta.setAlignmentY(0.0F);
        jConsulta.setMaximumSize(new java.awt.Dimension(854, 500));
        jConsulta.setMinimumSize(new java.awt.Dimension(584, 500));
        jConsulta.setPreferredSize(new java.awt.Dimension(854, 500));
        jConsulta.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel1.setMaximumSize(new java.awt.Dimension(723, 50));
        jPanel1.setMinimumSize(new java.awt.Dimension(723, 50));

        jLabel9.setText("Listar:");

        jTipoListagem.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "TODOS", "PAGO", "EXPIRADO", "VENCIDO", "EMABERTO", "CANCELADO" }));
        jTipoListagem.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTipoListagemFocusGained(evt);
            }
        });

        jLabel10.setText("Periodo:");

        conDataInicial.setDate(new java.util.Date(-2208977612000L));
        conDataInicial.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                conDataInicialFocusGained(evt);
            }
        });

        jLabel11.setText("até");

        conDataFinal.setDate(new java.util.Date(-2208977612000L));
        conDataFinal.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                conDataFinalFocusGained(evt);
            }
        });

        conBtnListar.setText("Listar");
        conBtnListar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                conBtnListarActionPerformed(evt);
            }
        });

        jLabel13.setText("Banco:");

        jcbConsultaBancos.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jcbConsultaBancosFocusGained(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jcbConsultaBancos, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTipoListagem, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(conDataInicial, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(conDataFinal, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(filler1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(conBtnListar, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(conDataFinal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(conDataInicial, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jTipoListagem, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(conBtnListar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(22, 22, 22)
                                .addComponent(filler1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jcbConsultaBancos, javax.swing.GroupLayout.Alignment.LEADING))))
                .addContainerGap())
        );

        jConsulta.add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 11, 850, -1));

        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel5.setMaximumSize(new java.awt.Dimension(456, 361));
        jPanel5.setMinimumSize(new java.awt.Dimension(456, 361));

        jLabel12.setBackground(new java.awt.Color(153, 204, 255));
        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(0, 51, 255));
        jLabel12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icones/Asp.net_0031_color1_16x16.gif"))); // NOI18N
        jLabel12.setText("Boletos no sistema do Banco.");
        jLabel12.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jLabel12.setIconTextGap(8);
        jLabel12.setOpaque(true);

        conLista.setAutoCreateRowSorter(true);
        conLista.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Emissão", "Vencimento", "Pagamento", "SeuNumero", "NossoNumero", "CnpjCpf", "Sacado", "lMulta", "Juros", "Valor", "Situação", "B"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        conLista.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        conLista.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                conListaMouseClicked(evt);
            }
        });
        conLista.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                conListaKeyPressed(evt);
            }
        });
        jScrollPane6.setViewportView(conLista);
        conLista.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        if (conLista.getColumnModel().getColumnCount() > 0) {
            conLista.getColumnModel().getColumn(5).setMinWidth(0);
            conLista.getColumnModel().getColumn(5).setPreferredWidth(0);
            conLista.getColumnModel().getColumn(5).setMaxWidth(0);
        }

        jProgressListaBoletasConsulta.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        jProgressListaBoletasConsulta.setMaximumSize(new java.awt.Dimension(146, 14));
        jProgressListaBoletasConsulta.setMinimumSize(new java.awt.Dimension(146, 14));
        jProgressListaBoletasConsulta.setStringPainted(true);

        jProgressListaBoletasConsulta1.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        jProgressListaBoletasConsulta1.setStringPainted(true);

        jBtnBaixar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icones/DownArrow.gif"))); // NOI18N
        jBtnBaixar.setText("Baixar no Sistema");
        jBtnBaixar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnBaixarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jProgressListaBoletasConsulta1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jProgressListaBoletasConsulta, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane6)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jBtnBaixar, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(jBtnBaixar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 357, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(76, 76, 76)
                .addComponent(jProgressListaBoletasConsulta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jProgressListaBoletasConsulta1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jConsulta.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 72, 850, 390));

        jPanel7.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        jPanel7.setMaximumSize(new java.awt.Dimension(834, 115));
        jPanel7.setMinimumSize(new java.awt.Dimension(834, 115));
        jPanel7.setLayout(new javax.swing.BoxLayout(jPanel7, javax.swing.BoxLayout.LINE_AXIS));
        jPanel7.add(filler3);

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 204), 1, true), " [ Vencidos ] ", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 0, 12), new java.awt.Color(0, 0, 102))); // NOI18N
        jPanel8.setMaximumSize(new java.awt.Dimension(198, 89));
        jPanel8.setMinimumSize(new java.awt.Dimension(198, 89));
        jPanel8.setPreferredSize(new java.awt.Dimension(148, 81));

        jLabel16.setText("Quantidade:");

        baiQuantidade.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        baiQuantidade.setForeground(new java.awt.Color(0, 51, 204));
        baiQuantidade.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        baiQuantidade.setText("000");

        jLabel17.setText("Valor:");

        baiValor.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        baiValor.setForeground(new java.awt.Color(0, 51, 204));
        baiValor.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        baiValor.setText("0,00");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jLabel17)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(baiValor))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jLabel16)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(baiQuantidade, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(55, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16)
                    .addComponent(baiQuantidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(baiValor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel7.add(jPanel8);
        jPanel7.add(filler4);

        jPanel13.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(51, 204, 0), 1, true), " [ Pagos ] ", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 0, 12), new java.awt.Color(51, 204, 0))); // NOI18N
        jPanel13.setMaximumSize(new java.awt.Dimension(148, 89));
        jPanel13.setMinimumSize(new java.awt.Dimension(148, 89));
        jPanel13.setPreferredSize(new java.awt.Dimension(150, 81));

        jLabel18.setText("Quantidade:");

        recQuantidade.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        recQuantidade.setForeground(new java.awt.Color(51, 153, 0));
        recQuantidade.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        recQuantidade.setText("000");

        jLabel19.setText("Valor:");

        recValor.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        recValor.setForeground(new java.awt.Color(51, 153, 0));
        recValor.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        recValor.setText("0,00");

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addComponent(jLabel19)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(recValor))
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addComponent(jLabel18)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(recQuantidade, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18)
                    .addComponent(recQuantidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19)
                    .addComponent(recValor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel7.add(jPanel13);
        jPanel7.add(filler5);

        jPanel14.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 0, 0), 1, true), " [ Expirados ] ", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 0, 12), new java.awt.Color(255, 0, 0))); // NOI18N
        jPanel14.setMaximumSize(new java.awt.Dimension(148, 89));
        jPanel14.setMinimumSize(new java.awt.Dimension(148, 89));
        jPanel14.setPreferredSize(new java.awt.Dimension(148, 81));

        jLabel20.setText("Quantidade:");

        expQuantidade.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        expQuantidade.setForeground(new java.awt.Color(255, 0, 0));
        expQuantidade.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        expQuantidade.setText("000");

        jLabel21.setText("Valor:");

        expValor.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        expValor.setForeground(new java.awt.Color(255, 0, 0));
        expValor.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        expValor.setText("0,00");

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addComponent(jLabel21)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(expValor))
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addComponent(jLabel20)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(expQuantidade, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(7, Short.MAX_VALUE))
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20)
                    .addComponent(expQuantidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21)
                    .addComponent(expValor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel7.add(jPanel14);
        jPanel7.add(filler6);

        jPanel16.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 204, 204), 1, true), " [ Em Abertos ] ", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 0, 12), new java.awt.Color(0, 204, 204))); // NOI18N
        jPanel16.setMaximumSize(new java.awt.Dimension(198, 89));
        jPanel16.setMinimumSize(new java.awt.Dimension(198, 89));
        jPanel16.setPreferredSize(new java.awt.Dimension(148, 81));

        jLabel22.setText("Quantidade:");

        preQuantidade.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        preQuantidade.setForeground(new java.awt.Color(0, 204, 204));
        preQuantidade.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        preQuantidade.setText("000");

        jLabel23.setText("Valor:");

        preValor.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        preValor.setForeground(new java.awt.Color(0, 204, 204));
        preValor.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        preValor.setText("0,00");

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel16Layout.createSequentialGroup()
                        .addComponent(jLabel23)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(preValor))
                    .addGroup(jPanel16Layout.createSequentialGroup()
                        .addComponent(jLabel22)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(preQuantidade, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(56, Short.MAX_VALUE))
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel22)
                    .addComponent(preQuantidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel23)
                    .addComponent(preValor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel7.add(jPanel16);
        jPanel7.add(filler2);

        jPanel19.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true), " [ Cancelados ] "));
        jPanel19.setMaximumSize(new java.awt.Dimension(148, 89));
        jPanel19.setMinimumSize(new java.awt.Dimension(148, 89));

        jLabel32.setText("Quantidade:");

        canQuantidade.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        canQuantidade.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        canQuantidade.setText("000");

        jLabel33.setText("Valor:");

        canValor.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        canValor.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        canValor.setText("0,00");

        javax.swing.GroupLayout jPanel19Layout = new javax.swing.GroupLayout(jPanel19);
        jPanel19.setLayout(jPanel19Layout);
        jPanel19Layout.setHorizontalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel19Layout.createSequentialGroup()
                        .addComponent(jLabel33)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(canValor))
                    .addGroup(jPanel19Layout.createSequentialGroup()
                        .addComponent(jLabel32)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(canQuantidade)))
                .addContainerGap())
        );
        jPanel19Layout.setVerticalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel32)
                    .addComponent(canQuantidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel33)
                    .addComponent(canValor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel7.add(jPanel19);

        jConsulta.add(jPanel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 470, 850, -1));

        jAletas.addTab("Consulta/Baixa de Boletos", jConsulta);

        jTabbedPane2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTabbedPane2MousePressed(evt);
            }
        });

        jPanel20.setBackground(new java.awt.Color(204, 255, 204));
        jPanel20.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel20.setForeground(new java.awt.Color(204, 255, 204));

        opcprop.setBackground(new java.awt.Color(204, 255, 204));
        buttonGroup2.add(opcprop);
        opcprop.setSelected(true);
        opcprop.setText("Proprietário");
        opcprop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                opcpropActionPerformed(evt);
            }
        });

        opcloca.setBackground(new java.awt.Color(204, 255, 204));
        buttonGroup2.add(opcloca);
        opcloca.setText("Locatarios");
        opcloca.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                opclocaActionPerformed(evt);
            }
        });

        opcavulsa.setBackground(new java.awt.Color(204, 255, 204));
        buttonGroup2.add(opcavulsa);
        opcavulsa.setText("Avulsa");
        opcavulsa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                opcavulsaActionPerformed(evt);
            }
        });

        jnome.setEditable(true);
        jnome.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jnome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jnomeActionPerformed(evt);
            }
        });

        jcodigo.setEditable(true);
        jcodigo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jcodigo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcodigoActionPerformed(evt);
            }
        });

        jLabel15.setText("Nome:");

        pnome.setText("jTextField1");

        jLabel24.setText("CPF/CNPJ:");

        pdocumento.setText("jTextField2");

        jLabel25.setText("Endereço:");

        pendereco.setText("jTextField3");

        pnumero.setText("jTextField4");

        pcomplto.setText("jTextField5");

        jLabel26.setText("Bairro:");

        pbairro.setText("jTextField6");

        jLabel27.setText("Cidade:");

        pcidade.setText("jTextField7");

        jLabel28.setText("Estado:");

        jLabel29.setText("Cep:");

        jLabel30.setText("Vencimento:");

        jLabel31.setText("Valor:");

        btImprimir.setText("Gerar");
        btImprimir.setToolTipText("Esta boleta não tem efeito contábil no sistema, devendo a entrada do valor ser feita após o recebimento desta manualmente na conta apropriada.");
        btImprimir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btImprimirActionPerformed(evt);
            }
        });

        jvalor.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        jvalor.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jvalor.setText("0,00");
        jvalor.setCaretColor(new java.awt.Color(51, 51, 255));
        jvalor.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N

        jvencto.setDate(new java.util.Date(-2208977612000L));

        try {
            pcep.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("#####-###")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }

        pestado.setText("jFormattedTextField1");

        btLimpar.setText("Limpar");
        btLimpar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btLimparActionPerformed(evt);
            }
        });

        jLabel36.setText("Banco:");

        jPanel18.setBackground(new java.awt.Color(255, 255, 204));
        jPanel18.setBorder(javax.swing.BorderFactory.createTitledBorder(" [ Histórico ] "));

        jScrollPane1.setViewportView(phistorico01);

        javax.swing.GroupLayout jPanel18Layout = new javax.swing.GroupLayout(jPanel18);
        jPanel18.setLayout(jPanel18Layout);
        jPanel18Layout.setHorizontalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1)
        );
        jPanel18Layout.setVerticalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1)
        );

        javax.swing.GroupLayout jPanel20Layout = new javax.swing.GroupLayout(jPanel20);
        jPanel20.setLayout(jPanel20Layout);
        jPanel20Layout.setHorizontalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator2)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel20Layout.createSequentialGroup()
                        .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel20Layout.createSequentialGroup()
                                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jcodigo, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(opcprop))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel20Layout.createSequentialGroup()
                                        .addComponent(opcloca)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(opcavulsa))
                                    .addComponent(jnome, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel20Layout.createSequentialGroup()
                                .addComponent(jLabel15)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(pnome, javax.swing.GroupLayout.PREFERRED_SIZE, 517, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel24)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(pdocumento, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel20Layout.createSequentialGroup()
                                .addComponent(jLabel25)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(pendereco)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(pnumero, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(pcomplto, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel20Layout.createSequentialGroup()
                                .addComponent(jLabel26)
                                .addGap(9, 9, 9)
                                .addComponent(pbairro, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel27)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(pcidade)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel28)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(pestado, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel29)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(pcep, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jPanel18, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel20Layout.createSequentialGroup()
                                .addComponent(jLabel36)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jbanco, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btImprimir, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel20Layout.createSequentialGroup()
                                .addComponent(btLimpar)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel30)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jvencto, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel31)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jvalor, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(17, 17, 17)))
                .addContainerGap())
        );
        jPanel20Layout.setVerticalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(opcprop)
                    .addComponent(opcloca)
                    .addComponent(opcavulsa))
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel20Layout.createSequentialGroup()
                        .addComponent(jnome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pdocumento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pcomplto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pcep, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel20Layout.createSequentialGroup()
                        .addComponent(jcodigo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel15)
                            .addComponent(pnome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel24))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel25)
                            .addComponent(pendereco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(pnumero, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel26)
                            .addComponent(pbairro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel27)
                            .addComponent(pcidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel29)
                            .addComponent(jLabel28)
                            .addComponent(pestado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btLimpar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel20Layout.createSequentialGroup()
                        .addComponent(jvalor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jvencto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel31, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel30, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel36)
                    .addComponent(jbanco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btImprimir))
                .addGap(12, 12, 12))
        );

        jTabbedPane2.addTab("Boleta", jPanel20);

        jPanel21.setBackground(new java.awt.Color(240, 237, 153));

        jLabel37.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        jLabel37.setText("Menssagens Superiores:");

        jLabel38.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        jLabel38.setText("Menssagens Inferiores:");

        jAvMsg08.setBackground(new java.awt.Color(160, 203, 254));
        jAvMsg08.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        jAvMsg08.setForeground(new java.awt.Color(0, 17, 255));
        jAvMsg08.setToolTipText("APÓS O DIA [VENCIMENTO] MULTA DE 2% + ENCARGOS DE 0,333% AO DIA DE ATRASO.");
        jAvMsg08.setSelectedTextColor(new java.awt.Color(0, 255, 114));

        jAvMsg09.setBackground(new java.awt.Color(160, 203, 254));
        jAvMsg09.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        jAvMsg09.setForeground(new java.awt.Color(0, 17, 255));
        jAvMsg09.setToolTipText("NÃO RECEBER APÓS 30 DIAS DO VENCIMENTO.");
        jAvMsg09.setSelectedTextColor(new java.awt.Color(0, 255, 114));

        jmsgGravar.setText("Gravar");
        jmsgGravar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmsgGravarActionPerformed(evt);
            }
        });

        jLabel39.setFont(new java.awt.Font("Ubuntu", 1, 10)); // NOI18N
        jLabel39.setText("Legenda: (linha 08)");

        jLabel40.setFont(new java.awt.Font("Ubuntu", 0, 10)); // NOI18N
        jLabel40.setText("[VENCIMENTO] - Data de Vencimento do Boleto.");

        jLabel41.setFont(new java.awt.Font("Ubuntu", 0, 10)); // NOI18N
        jLabel41.setText("[CARENCIA] - Data de Vencimento acrescido de dias de carência.");

        jLabel42.setFont(new java.awt.Font("Ubuntu", 0, 10)); // NOI18N
        jLabel42.setText("[MULTA] - Multa padrão de 10%.");

        jLabel43.setFont(new java.awt.Font("Ubuntu", 0, 10)); // NOI18N
        jLabel43.setText("[ENCARGOS] - Encargos padrão de 0,333%.");

        jLabel44.setText("L01:");

        jLabel45.setText("L08:");

        jLabel46.setText("L09:");

        javax.swing.GroupLayout jPanel21Layout = new javax.swing.GroupLayout(jPanel21);
        jPanel21.setLayout(jPanel21Layout);
        jPanel21Layout.setHorizontalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jAvMsg02)
                    .addComponent(jAvMsg03)
                    .addComponent(jAvMsg04)
                    .addComponent(jAvMsg05)
                    .addComponent(jAvMsg06)
                    .addComponent(jAvMsg07)
                    .addGroup(jPanel21Layout.createSequentialGroup()
                        .addComponent(jLabel44)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jAvMsg01))
                    .addGroup(jPanel21Layout.createSequentialGroup()
                        .addComponent(jLabel46)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jAvMsg09))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel21Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jmsgGravar, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel21Layout.createSequentialGroup()
                        .addComponent(jLabel45)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jAvMsg08))
                    .addGroup(jPanel21Layout.createSequentialGroup()
                        .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel37)
                            .addComponent(jLabel38)
                            .addComponent(jLabel40)
                            .addComponent(jLabel41)
                            .addComponent(jLabel42)
                            .addComponent(jLabel43)
                            .addComponent(jLabel39, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 507, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel21Layout.setVerticalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel37)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jAvMsg01, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel44))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jAvMsg02, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jAvMsg03, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jAvMsg04, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jAvMsg05, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jAvMsg06, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jAvMsg07, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel38)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jAvMsg08, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel45))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel39)
                .addGap(2, 2, 2)
                .addComponent(jLabel40)
                .addGap(0, 0, 0)
                .addComponent(jLabel41)
                .addGap(0, 0, 0)
                .addComponent(jLabel42)
                .addGap(0, 0, 0)
                .addComponent(jLabel43)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jAvMsg09, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel46))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jmsgGravar)
                .addContainerGap(42, Short.MAX_VALUE))
        );

        jTabbedPane2.addTab("Menssagem", jPanel21);

        javax.swing.GroupLayout jPanel17Layout = new javax.swing.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 850, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane2)
                .addContainerGap())
        );

        jAletas.addTab("Avulsas", jPanel17);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jAletas, javax.swing.GroupLayout.PREFERRED_SIZE, 872, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jAletas, javax.swing.GroupLayout.PREFERRED_SIZE, 597, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jemdiaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jemdiaActionPerformed
        jInicial.setEnabled(false);
        jFinal.setEnabled(false);
        VencBoleto.setEnabled(false);
    }//GEN-LAST:event_jemdiaActionPerformed

    private void jatrasadosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jatrasadosActionPerformed
        jInicial.setEnabled(false);
        jFinal.setEnabled(false);
        VencBoleto.setEnabled(true);
        VencBoleto.requestFocus();
    }//GEN-LAST:event_jatrasadosActionPerformed

    private void jtodosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtodosActionPerformed
        jInicial.setEnabled(false);
        jFinal.setEnabled(false);
        VencBoleto.setEnabled(true);
        VencBoleto.requestFocus();
    }//GEN-LAST:event_jtodosActionPerformed

    private void jperiodoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jperiodoActionPerformed
        jInicial.setEnabled(true);
        jFinal.setEnabled(true);
        VencBoleto.setEnabled(true);
        VencBoleto.requestFocus();
    }//GEN-LAST:event_jperiodoActionPerformed

    private void jListarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jListarActionPerformed
        jListar.setEnabled(false);

        int iAnoRef = Dates.iYear(new Date());
        int iMesRef = Dates.iMonth(new Date());
        int iDiaRef = 01;
        Date iDataRef = new Date(iAnoRef - 1900, iMesRef - 1, iDiaRef);
        
        int iAnoMax = Integer.valueOf(AnoRef.getValue().toString());
        int iMesMax = FuncoesGlobais.IndexOf(this.month, this.MesRef.getValue().toString());
        int iDiaMax = 01;
        Date iDataMax = new Date(iAnoMax - 1900, iMesMax, iDiaMax);
        int DiasNoMes = this.dmonth[iMesRef - 1];
        
        int difData = Dates.DiffDate(iDataMax, iDataRef);
        if (difData > DiasNoMes) {
            JOptionPane.showMessageDialog(this, 
                    "Você só pode gerar boletas até hum mês acima do Mês atual.");
            jListar.setEnabled(true);
            return;
        }
        
        if (jemdia.isSelected()) {
            EmDia();
        } else if (jatrasados.isSelected()) {
            Atrasados();
        } else if (jtodos.isSelected()) {
            Todos();
        } else {
            PorPeriodo();
        }

        jListar.setEnabled(true);
    }//GEN-LAST:event_jListarActionPerformed

    public void EmDia() {
        lbl_Status.setText("Criando Lista...");
        jProgress2.setValue(0);
        
        String sContrato = "";
        if (!"".equals(jContrato.getText().trim())) { sContrato = " AND r.contrato = '" + jContrato.getText().trim() + "' "; }
        int iAnoRef = Integer.valueOf(AnoRef.getValue().toString());
        int iDiaRef = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        Date iDataRef = new Date(iAnoRef - 1900, FuncoesGlobais.IndexOf(this.month, this.MesRef.getValue().toString()), iDiaRef);
        if (Dates.DiffDate(iDataRef, new Date()) > 0) {
            iDiaRef = 1;
            iDataRef = new Date(iAnoRef - 1900, FuncoesGlobais.IndexOf(this.month, this.MesRef.getValue().toString()), iDiaRef);
        }
        String Sql = "SELECT r.rgprp, r.rgimv, r.contrato, l.nomerazao, r.campo, r.dtvencimento, r.nnumero, c.dtultrecebimento, " + 
                     "l.boleta, 1 gerados, l.envio, l.bcobol, b.nome as bconome FROM RECIBO r, locatarios l, CARTEIRA c, bancos b " + 
                     "where l.boleta = -1 AND (r.tag <> 'X') AND (l.fiador1uf is null or l.fiador1uf = '') AND (r.contrato = l.contrato and c.contrato = " + 
                     "l.contrato) and (r.dtvencimento >= '" + 
                     Dates.DateFormata("yyyy-MM-dd", iDataRef) + 
                     "' and r.dtvencimento <= '" + Dates.DateFormata("yyyy-MM-dd", new Date(iAnoRef - 1900, FuncoesGlobais.IndexOf(this.month, 
                     this.MesRef.getValue().toString()), this.dmonth[FuncoesGlobais.IndexOf(this.month, this.MesRef.getValue().toString())])) + 
                     "') AND (Year(r.dtvencimento) = " + AnoRef.getValue() + ") " + sContrato + " AND (b.codigo = l.bcobol) AND " + 
                     "EXISTS(SELECT * FROM bancos_digital bd WHERE b.codigo = bd.nbanco LIMIT 1) ORDER BY l.bcobol, l.nomerazao;";
        ResultSet rs = conn.AbrirTabela(Sql, ResultSet.CONCUR_READ_ONLY);
        
        bancosBoleta = new ArrayList<BancosBoleta>();
        List<PessoasBoleta> pessoasBoleta = new ArrayList<PessoasBoleta>();
        
        // Bancos Erros
        List<BancosErros> bancosErros = new ArrayList<BancosErros>();

        // Boletos Printed
        List<BancosBoleta> bancosBoletaPrinted = new ArrayList<BancosBoleta>();
        List<PessoasBoleta> pessoasBoletaPrinted = new ArrayList<PessoasBoleta>();
        
        int b = 1;
        
        // Atribuições
        String trgprp = "";
        String trgimv = "";
        String tcontrato = "";
        String tnome = "";
        String tvencto = "";
        String tnnumero = "";
        String tbcobol = "";
        String tbcobolnome = "";
        int tpenvio = 0;
        int rcount = 0;
        try {
            String oldBco = ""; String oldBcoNome = "";
            rcount = DbMain.RecordCount(rs);
            while (rs.next()) {
                trgprp = String.valueOf(rs.getInt("rgprp"));
                trgimv = String.valueOf(rs.getInt("rgimv"));
                tcontrato = rs.getString("contrato").toUpperCase();
                tnome = rs.getString("nomerazao").trim();
                tvencto = Dates.DateFormata("dd-MM-yyyy", Dates.StringtoDate(rs.getString("dtvencimento").toUpperCase(),"yyyy-MM-dd"));
                tnnumero = rs.getString("nnumero");
                tbcobol = rs.getString("bcobol");
                tbcobolnome = rs.getString("bconome");
                tpenvio = rs.getInt("envio");
                String tenvio = "";
                if (tpenvio == 0) tenvio = "EM MÃOS";
                if (tpenvio == 1) tenvio = "EMAIL";
                if (tpenvio == 2) tenvio = "CORREIO";
                
                if (oldBco.equalsIgnoreCase("")) { oldBco = tbcobol; oldBcoNome = tbcobolnome; }
                if (!oldBco.equalsIgnoreCase(tbcobol)) {
                    if (!pessoasBoleta.isEmpty()) bancosBoleta.add(new BancosBoleta(oldBco, oldBcoNome, pessoasBoleta, new Boolean(false)));
                    if (!pessoasBoletaPrinted.isEmpty()) bancosBoletaPrinted.add(new BancosBoleta(oldBco, oldBcoNome, pessoasBoletaPrinted));
                    pessoasBoleta = new ArrayList<PessoasBoleta>();
                    pessoasBoletaPrinted = new ArrayList<PessoasBoleta>();
                }
                
                if (tnnumero == null || "".equals(tnnumero)) {
                    pessoasBoleta.add(new PessoasBoleta(tcontrato, tnome, tvencto, tnnumero, tenvio, trgprp, trgimv, new Boolean(false)));        
                } else {
                    pessoasBoletaPrinted.add(new PessoasBoleta(tcontrato, tnome, tvencto, tnnumero, tenvio, trgprp, trgimv, new Boolean(false)));        
                }
                
                oldBco = tbcobol; oldBcoNome = tbcobolnome;
                
                int pgs = ((b++ * 100) / rcount) + 1;
                jProgress2.setValue(pgs);
                try { Thread.sleep(20); } catch (InterruptedException ex) { }
            }
        } catch (SQLException ex) {}
        DbMain.FecharTabela(rs);

        if (!pessoasBoleta.isEmpty()) {
            bancosBoleta.add(new BancosBoleta(tbcobol, tbcobolnome, pessoasBoleta, new Boolean(false)));
        }      
        if (!pessoasBoletaPrinted.isEmpty()) {
            bancosBoletaPrinted.add(new BancosBoleta(tbcobol, tbcobolnome, pessoasBoletaPrinted));
        }               
        
        // Boletas não impressas
        {
            BoletaTreeTableModel boletaTreeTableModel = new BoletaTreeTableModel(bancosBoleta);
            treeTable = new JXTreeTable(boletaTreeTableModel);
            SetDisplayParameters(treeTable);
            ListaBancosPessoas.setViewportView(treeTable);
            ListaBancosPessoas.repaint();
        }

        // Boletas impressas
        {
            BoletaTreeTableModel boletaTreeTableModelPrinted = new BoletaTreeTableModel(bancosBoletaPrinted);
            treeTablePrinted = new JXTreeTable(boletaTreeTableModelPrinted);       
            SetDisplayParametersPrinted(treeTablePrinted);
            ListaBancosPessoasImpressas.setViewportView(treeTablePrinted);
            ListaBancosPessoasImpressas.repaint();
        }
        
        // Boletas Erros
        {
            ErrosTreeTableModel boletaTreeTableModelErros = new ErrosTreeTableModel(bancosErros);
            treeTableErros = new JTable(boletaTreeTableModelErros);       
            SetDisplayParametersErros(treeTableErros);
            ListaBancosPessoasErros.setViewportView(treeTableErros);
            ListaBancosPessoasErros.repaint();
        }
        
        lbl_Status.setText("Listagem pronta.");
    }
    
    public void Atrasados() {
        lbl_Status.setText("Criando Lista...");
        jProgress2.setValue(0);
        
        String sContrato = "";
        if (!"".equals(jContrato.getText().trim())) { sContrato = " AND r.contrato = '" + jContrato.getText().trim() + "' "; }
        String Sql = "SELECT r.rgprp, r.rgimv, r.contrato, l.nomerazao, r.campo, r.dtvencimento, r.nnumero, c.dtultrecebimento, " + 
                     "l.boleta, 1 gerados, l.envio, l.bcobol, b.nome as bconome FROM RECIBO r, locatarios l, CARTEIRA c, bancos b " + 
                     "where l.boleta = -1 AND (r.tag <> 'X') AND (l.fiador1uf is null) AND (r.contrato = l.contrato and c.contrato = " + 
                     "l.contrato) and (r.dtvencimento < '" + 
                     Dates.DateFormata("yyyy-MM-dd", new Date()) + "') AND (Year(r.dtvencimento) = " + 
                     AnoRef.getValue() + ") " + sContrato + " AND (b.codigo = l.bcobol) and " + 
                    "EXISTS(SELECT * FROM bancos_digital bd WHERE b.codigo = bd.nbanco LIMIT 1) ORDER BY l.bcobol, l.nomerazao;";
        ResultSet rs = conn.AbrirTabela(Sql, ResultSet.CONCUR_READ_ONLY);
        
        bancosBoleta = new ArrayList<BancosBoleta>();
        List<PessoasBoleta> pessoasBoleta = new ArrayList<PessoasBoleta>();

        // Bancos Erros
        List<BancosErros> bancosErros = new ArrayList<BancosErros>();
        
        // Boletos Printed
        List<BancosBoleta> bancosBoletaPrinted = new ArrayList<BancosBoleta>();
        List<PessoasBoleta> pessoasBoletaPrinted = new ArrayList<PessoasBoleta>();
        
        int b = 1;
        
        // Atribuições
        String trgprp = "";
        String trgimv = "";
        String tcontrato = "";
        String tnome = "";
        String tvencto = "";
        String tnnumero = "";
        String tbcobol = "";
        String tbcobolnome = "";
        int tpenvio = 0;
        int rcount = 0;
        try {
            String oldBco = ""; String oldBcoNome = "";
            rcount = DbMain.RecordCount(rs);
            while (rs.next()) {
                trgprp = String.valueOf(rs.getInt("rgprp"));
                trgimv = String.valueOf(rs.getInt("rgimv"));
                tcontrato = rs.getString("contrato").toUpperCase();
                tnome = rs.getString("nomerazao").trim();
                tvencto = Dates.DateFormata("dd-MM-yyyy", Dates.StringtoDate(rs.getString("dtvencimento").toUpperCase(),"yyyy-MM-dd"));
                tnnumero = rs.getString("nnumero");
                tbcobol = rs.getString("bcobol");
                tbcobolnome = rs.getString("bconome");
                tpenvio = rs.getInt("envio");
                String tenvio = "";
                if (tpenvio == 0) tenvio = "EM MÃOS";
                if (tpenvio == 1) tenvio = "EMAIL";
                if (tpenvio == 2) tenvio = "CORREIO";
                
                if (oldBco.equalsIgnoreCase("")) { oldBco = tbcobol; oldBcoNome = tbcobolnome; }
                if (!oldBco.equalsIgnoreCase(tbcobol)) {
                    if (!pessoasBoleta.isEmpty()) bancosBoleta.add(new BancosBoleta(oldBco, oldBcoNome, pessoasBoleta, new Boolean(false)));
                    if (!pessoasBoletaPrinted.isEmpty()) bancosBoletaPrinted.add(new BancosBoleta(oldBco, oldBcoNome, pessoasBoletaPrinted));
                    pessoasBoleta = new ArrayList<PessoasBoleta>();
                    pessoasBoletaPrinted = new ArrayList<PessoasBoleta>();
                }
                
                String DataBoleto = "";
                if (Dates.DateDiff(Dates.DIA, Dates.StringtoDate(tvencto, "dd/MM/yyyy"), new Date()) > 0) DataBoleto = Dates.DateFormata("dd/MM/yyyy", VencBoleto.getDate());
                if (tnnumero == null || "".equals(tnnumero)) {
                    pessoasBoleta.add(new PessoasBoleta(tcontrato, tnome, tvencto, DataBoleto, tenvio, trgprp, trgimv, new Boolean(false)));        
                } else {
                    pessoasBoletaPrinted.add(new PessoasBoleta(tcontrato, tnome, tvencto, tnnumero, tenvio, trgprp, trgimv, new Boolean(false)));        
                }
                
                oldBco = tbcobol; oldBcoNome = tbcobolnome;
                
                int pgs = ((b++ * 100) / rcount) + 1;
                jProgress2.setValue(pgs);
                try { Thread.sleep(20); } catch (InterruptedException ex) { }
            }
        } catch (SQLException ex) {}
        DbMain.FecharTabela(rs);

        if (!pessoasBoleta.isEmpty()) {
            bancosBoleta.add(new BancosBoleta(tbcobol, tbcobolnome, pessoasBoleta, new Boolean(false)));
        }      
        if (!pessoasBoletaPrinted.isEmpty()) {
            bancosBoletaPrinted.add(new BancosBoleta(tbcobol, tbcobolnome, pessoasBoletaPrinted));
        }               
        
        // Boletas não impressas
        {
            BoletaTreeTableModel boletaTreeTableModel = new BoletaTreeTableModel(bancosBoleta);
            treeTable = new JXTreeTable(boletaTreeTableModel);
            SetDisplayParameters(treeTable);
            ListaBancosPessoas.setViewportView(treeTable);
            ListaBancosPessoas.repaint();
        }

        // Boletas impressas
        {
            BoletaTreeTableModel boletaTreeTableModelPrinted = new BoletaTreeTableModel(bancosBoletaPrinted);
            treeTablePrinted = new JXTreeTable(boletaTreeTableModelPrinted);       
            SetDisplayParametersPrinted(treeTablePrinted);
            ListaBancosPessoasImpressas.setViewportView(treeTablePrinted);
            ListaBancosPessoasImpressas.repaint();
        }
        
        // Boletas Erros
        {
            ErrosTreeTableModel boletaTreeTableModelErros = new ErrosTreeTableModel(bancosErros);
            treeTableErros = new JTable(boletaTreeTableModelErros);       
            SetDisplayParametersErros(treeTableErros);
            ListaBancosPessoasErros.setViewportView(treeTableErros);
            ListaBancosPessoasErros.repaint();
        }

        lbl_Status.setText("Listagem pronta.");
    }

    public void Todos() {
        lbl_Status.setText("Criando Lista...");
        jProgress2.setValue(0);
        
        String sContrato = "";
        if (!"".equals(jContrato.getText().trim())) { sContrato = " AND r.contrato = '" + jContrato.getText().trim() + "' "; }
        String Sql = "SELECT r.rgprp, r.rgimv, r.contrato, l.nomerazao, r.campo, r.dtvencimento, r.nnumero, c.dtultrecebimento, " + 
                     "l.boleta, 1 gerados, l.envio, l.bcobol, b.nome as bconome FROM RECIBO r, locatarios l, CARTEIRA c, bancos b " + 
                     "where l.boleta = -1 AND (r.tag <> 'X') AND (l.fiador1uf is null) AND (r.contrato = l.contrato and c.contrato = " + 
                     "l.contrato) " + sContrato + " AND (b.codigo = l.bcobol) AND " + 
                     "EXISTS(SELECT * FROM bancos_digital bd WHERE b.codigo = bd.nbanco LIMIT 1) ORDER BY l.bcobol, l.nomerazao;";
        ResultSet rs = conn.AbrirTabela(Sql, ResultSet.CONCUR_READ_ONLY);
        
        bancosBoleta = new ArrayList<BancosBoleta>();
        List<PessoasBoleta> pessoasBoleta = new ArrayList<PessoasBoleta>();

        // Bancos Erros
        List<BancosErros> bancosErros = new ArrayList<BancosErros>();

        // Boletos Printed
        List<BancosBoleta> bancosBoletaPrinted = new ArrayList<BancosBoleta>();
        List<PessoasBoleta> pessoasBoletaPrinted = new ArrayList<PessoasBoleta>();
        
        int b = 1;
        
        // Atribuições
        String trgprp = "";
        String trgimv = "";
        String tcontrato = "";
        String tnome = "";
        String tvencto = "";
        String tnnumero = "";
        String tbcobol = "";
        String tbcobolnome = "";
        int tpenvio = 0;
        int rcount = 0;
        try {
            String oldBco = ""; String oldBcoNome = "";
            rcount = DbMain.RecordCount(rs);
            while (rs.next()) {
                trgprp = String.valueOf(rs.getInt("rgprp"));
                trgimv = String.valueOf(rs.getInt("rgimv"));
                tcontrato = rs.getString("contrato").toUpperCase();
                tnome = rs.getString("nomerazao").trim();
                tvencto = Dates.DateFormata("dd-MM-yyyy", Dates.StringtoDate(rs.getString("dtvencimento").toUpperCase(),"yyyy-MM-dd"));
                tnnumero = rs.getString("nnumero");
                tbcobol = rs.getString("bcobol");
                tbcobolnome = rs.getString("bconome");
                tpenvio = rs.getInt("envio");
                String tenvio = "";
                if (tpenvio == 0) tenvio = "EM MÃOS";
                if (tpenvio == 1) tenvio = "EMAIL";
                if (tpenvio == 2) tenvio = "CORREIO";
                
                if (oldBco.equalsIgnoreCase("")) { oldBco = tbcobol; oldBcoNome = tbcobolnome; }
                if (!oldBco.equalsIgnoreCase(tbcobol)) {
                    if (!pessoasBoleta.isEmpty()) bancosBoleta.add(new BancosBoleta(oldBco, oldBcoNome, pessoasBoleta, new Boolean(false)));
                    if (!pessoasBoletaPrinted.isEmpty()) bancosBoletaPrinted.add(new BancosBoleta(oldBco, oldBcoNome, pessoasBoletaPrinted));
                    pessoasBoleta = new ArrayList<PessoasBoleta>();
                    pessoasBoletaPrinted = new ArrayList<PessoasBoleta>();
                }
                
                String DataBoleto = "";
                if (Dates.DateDiff(Dates.DIA, Dates.StringtoDate(tvencto, "dd/MM/yyyy"), new Date()) > 0) DataBoleto = Dates.DateFormata("dd/MM/yyyy", VencBoleto.getDate());
                if (tnnumero == null || "".equals(tnnumero)) {
                    pessoasBoleta.add(new PessoasBoleta(tcontrato, tnome, tvencto, DataBoleto, tenvio, trgprp, trgimv, new Boolean(false)));        
                } else {
                    pessoasBoletaPrinted.add(new PessoasBoleta(tcontrato, tnome, tvencto, tnnumero, tenvio, trgprp, trgimv, new Boolean(false)));        
                }
                
                oldBco = tbcobol; oldBcoNome = tbcobolnome;
                
                int pgs = ((b++ * 100) / rcount) + 1;
                jProgress2.setValue(pgs);
                try { Thread.sleep(20); } catch (InterruptedException ex) { }
            }
        } catch (SQLException ex) {}
        DbMain.FecharTabela(rs);

        if (!pessoasBoleta.isEmpty()) {
            bancosBoleta.add(new BancosBoleta(tbcobol, tbcobolnome, pessoasBoleta, new Boolean(false)));
        }      
        if (!pessoasBoletaPrinted.isEmpty()) {
            bancosBoletaPrinted.add(new BancosBoleta(tbcobol, tbcobolnome, pessoasBoletaPrinted));
        }               
        
        // Boletas não impressas
        {
            BoletaTreeTableModel boletaTreeTableModel = new BoletaTreeTableModel(bancosBoleta);
            treeTable = new JXTreeTable(boletaTreeTableModel);
            SetDisplayParameters(treeTable);
            ListaBancosPessoas.setViewportView(treeTable);
            ListaBancosPessoas.repaint();
        }

        // Boletas impressas
        {
            BoletaTreeTableModel boletaTreeTableModelPrinted = new BoletaTreeTableModel(bancosBoletaPrinted);
            treeTablePrinted = new JXTreeTable(boletaTreeTableModelPrinted);       
            SetDisplayParametersPrinted(treeTablePrinted);
            ListaBancosPessoasImpressas.setViewportView(treeTablePrinted);
            ListaBancosPessoasImpressas.repaint();
        }
        
        // Boletas Erros
        {
            ErrosTreeTableModel boletaTreeTableModelErros = new ErrosTreeTableModel(bancosErros);
            treeTableErros = new JTable(boletaTreeTableModelErros);       
            SetDisplayParametersErros(treeTableErros);
            ListaBancosPessoasErros.setViewportView(treeTableErros);
            ListaBancosPessoasErros.repaint();
        }

        lbl_Status.setText("Listagem pronta.");
    }    
    
    public void PorPeriodo() {
        lbl_Status.setText("Criando Lista...");
        jProgress2.setValue(0);
        
        String sContrato = "";
        if (!"".equals(jContrato.getText().trim())) { sContrato = " AND r.contrato = '" + jContrato.getText().trim() + "' "; }
        String Sql = "SELECT r.rgprp, r.rgimv, r.contrato, l.nomerazao, r.campo, r.dtvencimento, r.nnumero, c.dtultrecebimento, " + 
                     "l.boleta, 1 gerados, l.envio, l.bcobol, b.nome as bconome FROM RECIBO r, locatarios l, CARTEIRA c, bancos b " + 
                     "where l.boleta = -1 AND (r.tag <> 'X') AND (l.fiador1uf is null) AND (r.contrato = l.contrato and c.contrato = " + 
                     "l.contrato) and (r.dtvencimento >= '" + 
                     Dates.DateFormata("yyyy-MM-dd", jInicial.getDate()) + "' AND r.dtvencimento <= '" + 
                     Dates.DateFormata("yyyy-MM-dd", jFinal.getDate()) + "') " + 
                     sContrato + " AND (b.codigo = l.bcobol) AND " + 
                     "EXISTS(SELECT * FROM bancos_digital bd WHERE b.codigo = bd.nbanco LIMIT 1) ORDER BY l.bcobol, l.nomerazao;";
        ResultSet rs = conn.AbrirTabela(Sql, ResultSet.CONCUR_READ_ONLY);
        
        bancosBoleta = new ArrayList<BancosBoleta>();
        List<PessoasBoleta> pessoasBoleta = new ArrayList<PessoasBoleta>();

        // Bancos Erros
        List<BancosErros> bancosErros = new ArrayList<BancosErros>();

        // Boletos Printed
        List<BancosBoleta> bancosBoletaPrinted = new ArrayList<BancosBoleta>();
        List<PessoasBoleta> pessoasBoletaPrinted = new ArrayList<PessoasBoleta>();
        
        int b = 1;
        
        // Atribuições
        String trgprp = "";
        String trgimv = "";
        String tcontrato = "";
        String tnome = "";
        String tvencto = "";
        String tnnumero = "";
        String tbcobol = "";
        String tbcobolnome = "";
        int tpenvio = 0;
        int rcount = 0;
        try {
            String oldBco = ""; String oldBcoNome = "";
            rcount = DbMain.RecordCount(rs);
            while (rs.next()) {
                trgprp = String.valueOf(rs.getInt("rgprp"));
                trgimv = String.valueOf(rs.getInt("rgimv"));
                tcontrato = rs.getString("contrato").toUpperCase();
                tnome = rs.getString("nomerazao").trim();
                tvencto = Dates.DateFormata("dd-MM-yyyy", Dates.StringtoDate(rs.getString("dtvencimento").toUpperCase(),"yyyy-MM-dd"));
                tnnumero = rs.getString("nnumero");
                tbcobol = rs.getString("bcobol");
                tbcobolnome = rs.getString("bconome");
                tpenvio = rs.getInt("envio");
                String tenvio = "";
                if (tpenvio == 0) tenvio = "EM MÃOS";
                if (tpenvio == 1) tenvio = "EMAIL";
                if (tpenvio == 2) tenvio = "CORREIO";
                
                if (oldBco.equalsIgnoreCase("")) { oldBco = tbcobol; oldBcoNome = tbcobolnome; }
                if (!oldBco.equalsIgnoreCase(tbcobol)) {
                    if (!pessoasBoleta.isEmpty()) bancosBoleta.add(new BancosBoleta(oldBco, oldBcoNome, pessoasBoleta, new Boolean(false)));
                    if (!pessoasBoletaPrinted.isEmpty()) bancosBoletaPrinted.add(new BancosBoleta(oldBco, oldBcoNome, pessoasBoletaPrinted));
                    pessoasBoleta = new ArrayList<PessoasBoleta>();
                    pessoasBoletaPrinted = new ArrayList<PessoasBoleta>();
                }
                
                String DataBoleto = "";
                if (Dates.DateDiff(Dates.DIA, Dates.StringtoDate(tvencto, "dd/MM/yyyy"), new Date()) > 0) DataBoleto = Dates.DateFormata("dd/MM/yyyy", VencBoleto.getDate());                
                if (tnnumero == null || "".equals(tnnumero)) {
                    pessoasBoleta.add(new PessoasBoleta(tcontrato, tnome, tvencto, DataBoleto, tenvio, trgprp, trgimv, new Boolean(false)));        
                } else {
                    pessoasBoletaPrinted.add(new PessoasBoleta(tcontrato, tnome, tvencto, tnnumero, tenvio, trgprp, trgimv, new Boolean(false)));        
                }
                
                oldBco = tbcobol; oldBcoNome = tbcobolnome;
                
                int pgs = ((b++ * 100) / rcount) + 1;
                jProgress.setValue(pgs);
                try { Thread.sleep(20); } catch (InterruptedException ex) { }
            }
        } catch (SQLException ex) {}
        DbMain.FecharTabela(rs);

        if (!pessoasBoleta.isEmpty()) {
            bancosBoleta.add(new BancosBoleta(tbcobol, tbcobolnome, pessoasBoleta, new Boolean(false)));
        }      
        if (!pessoasBoletaPrinted.isEmpty()) {
            bancosBoletaPrinted.add(new BancosBoleta(tbcobol, tbcobolnome, pessoasBoletaPrinted));
        }               
        
        // Boletas não impressas
        {
            BoletaTreeTableModel boletaTreeTableModel = new BoletaTreeTableModel(bancosBoleta);
            treeTable = new JXTreeTable(boletaTreeTableModel);
            SetDisplayParameters(treeTable);
            ListaBancosPessoas.setViewportView(treeTable);
            ListaBancosPessoas.repaint();
        }

        // Boletas impressas
        {
            BoletaTreeTableModel boletaTreeTableModelPrinted = new BoletaTreeTableModel(bancosBoletaPrinted);
            treeTablePrinted = new JXTreeTable(boletaTreeTableModelPrinted);       
            SetDisplayParametersPrinted(treeTablePrinted);
            ListaBancosPessoasImpressas.setViewportView(treeTablePrinted);
            ListaBancosPessoasImpressas.repaint();
        }
        
        // Boletas Erros
        {
            ErrosTreeTableModel boletaTreeTableModelErros = new ErrosTreeTableModel(bancosErros);
            treeTableErros = new JTable(boletaTreeTableModelErros);       
            SetDisplayParametersErros(treeTableErros);
            ListaBancosPessoasErros.setViewportView(treeTableErros);
            ListaBancosPessoasErros.repaint();
        }

        lbl_Status.setText("Listagem pronta.");
    }    

        private void SetDisplayParameters(JXTreeTable table) {
            table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            table.setRootVisible(false);
            table.setBounds(0, 0, ListaBancosPessoas.getWidth(), ListaBancosPessoas.getHeight());
            table.setVisible(true);
            //table.setRowHeight(50);

            table.getColumnModel().getColumn(0).setWidth(104);
            table.getColumnModel().getColumn(0).setPreferredWidth(104);
            table.getColumnModel().getColumn(0).setMinWidth(104);
            table.getColumnModel().getColumn(0).setMaxWidth(104);

            table.getColumnModel().getColumn(1).setWidth(215);
            table.getColumnModel().getColumn(1).setPreferredWidth(215);
//            table.getColumnModel().getColumn(1).setMinWidth(215);
//            table.getColumnModel().getColumn(1).setMaxWidth(215);

            table.getColumnModel().getColumn(2).setWidth(75);
            table.getColumnModel().getColumn(2).setPreferredWidth(75);
            table.getColumnModel().getColumn(2).setMinWidth(75);
            table.getColumnModel().getColumn(2).setMaxWidth(75);

            table.getColumnModel().getColumn(3).setWidth(75);
            table.getColumnModel().getColumn(3).setPreferredWidth(75);
            table.getColumnModel().getColumn(3).setMinWidth(75);
            table.getColumnModel().getColumn(3).setMaxWidth(75);

            table.getColumnModel().getColumn(4).setWidth(75);
            table.getColumnModel().getColumn(4).setPreferredWidth(75);
            table.getColumnModel().getColumn(4).setMinWidth(75);
            table.getColumnModel().getColumn(4).setMaxWidth(75);

            table.getColumnModel().getColumn(5).setWidth(0);
            table.getColumnModel().getColumn(5).setPreferredWidth(0);
            table.getColumnModel().getColumn(5).setMinWidth(0);
            table.getColumnModel().getColumn(5).setMaxWidth(0);

            table.getColumnModel().getColumn(6).setWidth(0);
            table.getColumnModel().getColumn(6).setPreferredWidth(0);
            table.getColumnModel().getColumn(6).setMinWidth(0);
            table.getColumnModel().getColumn(6).setMaxWidth(0);

            table.getColumnModel().getColumn(7).setWidth(30);
            table.getColumnModel().getColumn(7).setPreferredWidth(30);
            table.getColumnModel().getColumn(7).setMinWidth(30);
            table.getColumnModel().getColumn(7).setMaxWidth(30);
            table.expandAll();
    }
    
    private void SetDisplayParametersPrinted(JXTreeTable table) {
            table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            table.setRootVisible(false);
            table.setBounds(0, 0, ListaBancosPessoasImpressas.getWidth(), ListaBancosPessoasImpressas.getHeight());
            table.setVisible(true);
            //table.setRowHeight(50);

            table.getColumnModel().getColumn(0).setWidth(102);
            table.getColumnModel().getColumn(0).setPreferredWidth(102);
            table.getColumnModel().getColumn(0).setMinWidth(102);
            table.getColumnModel().getColumn(0).setMaxWidth(102);

            table.getColumnModel().getColumn(1).setWidth(247);
            table.getColumnModel().getColumn(1).setPreferredWidth(247);
            table.getColumnModel().getColumn(1).setMinWidth(247);
            table.getColumnModel().getColumn(1).setMaxWidth(247);

            table.getColumnModel().getColumn(2).setWidth(75);
            table.getColumnModel().getColumn(2).setPreferredWidth(75);
            table.getColumnModel().getColumn(2).setMinWidth(75);
            table.getColumnModel().getColumn(2).setMaxWidth(75);

            table.getColumnModel().getColumn(3).setWidth(75);
            table.getColumnModel().getColumn(3).setPreferredWidth(75);
            table.getColumnModel().getColumn(3).setMinWidth(75);
            table.getColumnModel().getColumn(3).setMaxWidth(75);

            table.getColumnModel().getColumn(4).setWidth(75);
            table.getColumnModel().getColumn(4).setPreferredWidth(75);
            table.getColumnModel().getColumn(4).setMinWidth(75);
            table.getColumnModel().getColumn(4).setMaxWidth(75);

            table.getColumnModel().getColumn(5).setWidth(0);
            table.getColumnModel().getColumn(5).setPreferredWidth(0);
            table.getColumnModel().getColumn(5).setMinWidth(0);
            table.getColumnModel().getColumn(5).setMaxWidth(0);

            table.getColumnModel().getColumn(6).setWidth(0);
            table.getColumnModel().getColumn(6).setPreferredWidth(0);
            table.getColumnModel().getColumn(6).setMinWidth(0);
            table.getColumnModel().getColumn(6).setMaxWidth(0);

            table.getColumnModel().getColumn(7).setWidth(0);
            table.getColumnModel().getColumn(7).setPreferredWidth(0);
            table.getColumnModel().getColumn(7).setMinWidth(0);
            table.getColumnModel().getColumn(7).setMaxWidth(0);
            table.expandAll();
    }

    private void SetDisplayParametersErros(JTable table) {
            table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            table.setBounds(0, 0, ListaBancosPessoasErros.getWidth(), ListaBancosPessoasErros.getHeight());
            table.setVisible(true);
            
            DefaultTableCellRenderer esquerda = new DefaultTableCellRenderer();
            DefaultTableCellRenderer centralizado = new DefaultTableCellRenderer();
            DefaultTableCellRenderer direita = new DefaultTableCellRenderer();
            esquerda.setHorizontalAlignment(SwingConstants.LEFT);
            centralizado.setHorizontalAlignment(SwingConstants.CENTER);
            direita.setHorizontalAlignment(SwingConstants.RIGHT);
            
            table.getColumnModel().getColumn(0).setWidth(75);
            table.getColumnModel().getColumn(0).setPreferredWidth(75);
            table.getColumnModel().getColumn(0).setMinWidth(75);
            table.getColumnModel().getColumn(0).setMaxWidth(75);
            table.getColumnModel().getColumn(0).setCellRenderer(centralizado);

            table.getColumnModel().getColumn(1).setWidth(247);
            table.getColumnModel().getColumn(1).setPreferredWidth(247);
            table.getColumnModel().getColumn(1).setMinWidth(247);
            table.getColumnModel().getColumn(1).setMaxWidth(700);
            table.getColumnModel().getColumn(1).setCellRenderer(esquerda);

            table.getColumnModel().getColumn(2).setWidth(80);
            table.getColumnModel().getColumn(2).setPreferredWidth(80);
            table.getColumnModel().getColumn(2).setMinWidth(80);
            table.getColumnModel().getColumn(2).setMaxWidth(80);
            table.getColumnModel().getColumn(2).setCellRenderer(centralizado);
            
            table.getColumnModel().getColumn(3).setWidth(75);
            table.getColumnModel().getColumn(3).setPreferredWidth(75);
            table.getColumnModel().getColumn(3).setMinWidth(75);
            table.getColumnModel().getColumn(3).setMaxWidth(75);
            table.getColumnModel().getColumn(3).setCellRenderer(centralizado);

            table.getColumnModel().getColumn(4).setWidth(250);
            table.getColumnModel().getColumn(4).setPreferredWidth(200);
            table.getColumnModel().getColumn(4).setMinWidth(200);
            table.getColumnModel().getColumn(4).setMaxWidth(700);
            table.getColumnModel().getColumn(4).setCellRenderer(esquerda);
    }

    private void jGerarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jGerarActionPerformed
        if (!AnalisaGeracao()) return;

        jGerar.setEnabled(false);

        String rgprp;
        String rgimv;
        String contrato = null;
        String vencto = null;
        String vectoBol = null;

        // Bancos Erros
        List<BancosErros> bancosErros = new ArrayList<BancosErros>();
        
        int pg1 = 1; int rcount1 = bancosBoleta.size();
        
        // Limpa lista de email
        for (int b = 0; b <= bancosBoleta.size() - 1; b++) {
            int pg2 = 1; int rcount2 = bancosBoleta.get(b).getPessoasBoleta().size();
            
            for (PessoasBoleta p : bancosBoleta.get(b).getPessoasBoleta()) {
                if (p.getTag()) {
                    rgprp = p.getRgprp();
                    rgimv = p.getRgimv();
                    contrato = p.getContrato();
                    vencto = p.getVencimentoRec();
                    vectoBol = p.getVencimentoBol();
                    if (vectoBol != null) {
                        if (vectoBol.trim().equalsIgnoreCase("")) vectoBol = null;
                    }

                    Boleta Bean1 = null;
                    try {
                        Bean1 = CreateBoleta(bancosBoleta.get(b).getBanco(), rgprp, rgimv, contrato, vencto, vectoBol);
                        
                        if (Bean1 != null) {
                            // Gravar no arquivo Boletas
                            String cSql = "INSERT INTO bloquetos (`rgprp`,`rgimv`,`contrato`," +
                            "`nome`,`vencimento`,`valor`,`nnumero`,`remessa`) " +
                            "VALUES (\"&1.\",\"&2.\",\"&3.\",\"&4.\",\"&5.\",\"&6.\",\"&7.\",\"&8.\")";
                            cSql = FuncoesGlobais.Subst(cSql, new String[] {
                                rgprp,
                                rgimv,
                                contrato,
                                Bean1.getsacDadosNome(),
                                Dates.StringtoString(vencto,"dd/MM/yyyy","yyyy-MM-dd"),
                                Bean1.getbolDadosVrdoc(),
                                Bean1.getbolDadosNnumero(),
                                "S"
                            });
                            try {
                                if (conn.ExisteTabelaBloquetos()) conn.ExecutarComando(cSql);
                            } catch (Exception e) {e.printStackTrace();}

                            List<Boleta> lista = new ArrayList<Boleta>();
                            lista.add(Bean1);

                            JRDataSource jrds = new JRBeanCollectionDataSource(lista);
                            try {
                                String fileName = "reports/Boletos.jasper";
                                JasperPrint print = JasperFillManager.fillReport(fileName, null, jrds);

                                // Create a PDF exporter
                                JRExporter exporter = new JRPdfExporter();

                                new jDirectory("reports/BoletasDigital/" + Dates.iYear(new Date()) + "/" + Dates.Month(new Date()) + "/");
                                String pathName = "reports/BoletasDigital/" + Dates.iYear(new Date()) + "/" + Dates.Month(new Date()) + "/";

                                // Configure the exporter (set output file name and print object)
                                String outFileName = pathName + contrato + "_" + Bean1.getsacDadosNome() + "_" + vencto + "_" + Bean1.getbolDadosNnumero().substring(0,11) + ".pdf";
                                exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, outFileName);
                                exporter.setParameter(JRExporterParameter.JASPER_PRINT, print);

                                try {
                                    String recUpdSql = "UPDATE RECIBO SET boletapath = '" + outFileName + "' WHERE contrato = '" + contrato + "' AND " +
                                    "dtvencimento = '" + Dates.DateFormata("yyyy-MM-dd", Dates.StringtoDate(vencto, "dd/MM/yyyy")) + "';";
                                    conn.ExecutarComando(recUpdSql);
                                } catch (Exception err) {err.printStackTrace();}

                                // Export the PDF file
                                exporter.exportReport();
                            } catch (JRException e) { 
                                e.printStackTrace();
                                System.exit(1);
                            } catch (Exception e) {
                                e.printStackTrace();
                                System.exit(1);
                            }
                        } else {
                            bancosErros.add(new BancosErros(p.getContrato(), p.getNome(), p.getVencimentoRec(), codErro, msgErro));
                        }
                    } catch (SQLException ex) {}
                } 
                
                int pgs2 = ((pg2++ * 100) / rcount1) + 1;
                jProgress1.setValue(pgs2);
                try { Thread.sleep(20); } catch (InterruptedException ex) { }
            }
            
            int pgs = ((pg1++ * 100) / rcount1) + 1;
            jProgress.setValue(pgs);            
            try { Thread.sleep(20); } catch (InterruptedException ex) { }
        } 
        jListar.doClick();

        // Boletas Erros
        {
            ErrosTreeTableModel boletaTreeTableModelErros = new ErrosTreeTableModel(bancosErros);
            treeTableErros = new JTable(boletaTreeTableModelErros);       
            SetDisplayParametersErros(treeTableErros);
            ListaBancosPessoasErros.setViewportView(treeTableErros);
            ListaBancosPessoasErros.repaint();
            
            // ToolTips
            treeTableErros.addMouseMotionListener(new MouseMotionAdapter(){
                public void mouseMoved(MouseEvent e){
                    Point p = e.getPoint(); 
                    int row = treeTableErros.rowAtPoint(p);
                    int modelRow = treeTableErros.convertRowIndexToModel(row);
                    int column = treeTableErros.columnAtPoint(p);
                    treeTableErros.setToolTipText(String.valueOf(treeTableErros.getModel().getValueAt(modelRow,column)));
                 }
            });            
        }        

        JOptionPane.showMessageDialog(this, "Processo finalizado.\n\nCheque na lista abaixo se houve algum erro na geração.");
               
        jGerar.setEnabled(true);
    }//GEN-LAST:event_jGerarActionPerformed

    private void btnEnviarTodosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEnviarTodosActionPerformed
        btnEnviarTodos.setEnabled(false);

        // Seleciona todos
        tblEmails.selectAll();
        int selRows = tblEmails.getSelectedRowCount();
        if (selRows <= 0) {
            btnEnviarTodos.setEnabled(true);
            return;
        }

        if (edtSubJect.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "O campo Assunto não pode ser vazio!");
            btnEnviarTodos.setEnabled(true);
            edtSubJect.requestFocus();
            return;
        }

        String htmlText = "";
        try {htmlText = _htmlPane.getDocument().getText(0, _htmlPane.getDocument().getEndPosition().getOffset());} catch (BadLocationException ba) {}
        if (htmlText.trim().equalsIgnoreCase("")) {
            JOptionPane.showMessageDialog(null, "O campo Mensagem não pode ser vazio!");
            btnEnviarTodos.setEnabled(true);
            _htmlPane.requestFocus();
            return;
        }

        int[] selRow = tblEmails.getSelectedRows();

        String contrato = null;
        String vencto = "";
        String filename = "";
        String status = "";

        int b = 1; int rcount = selRow.length;
        jProgressEmail.setValue(0);
        
        for (int i=0;i<=selRow.length - 1;i++) {
            int nRow = selRow[i];
            int modelRow = tblEmails.convertRowIndexToModel(nRow);

            contrato = tblEmails.getModel().getValueAt(modelRow, 0).toString();
            String[][] EmailLocaDados = null;
            try {
                EmailLocaDados = conn.LerCamposTabela(new String[] {"nomerazao","email"}, "locatarios", "contrato = '" + contrato + "'");
            } catch (SQLException e) {}
            
            String EmailLoca = null;
            if (EmailLocaDados != null) EmailLoca = EmailLocaDados[1][3].toLowerCase();
            
            vencto = tblEmails.getModel().getValueAt(modelRow, 2).toString();
            try {filename = tblEmails.getModel().getValueAt(modelRow, 5).toString();} catch (Exception ex) {}
            String[] attachMent;
            if (filename.isEmpty()) {
                attachMent = new String[]{};
            } else {
                attachMent = new String[]{System.getProperty("user.dir") + "/" + filename};
            }
            status = tblEmails.getModel().getValueAt(modelRow, 6).toString();

            if (!status.equalsIgnoreCase("OK")) {
                try {            
                    String To = EmailLoca.trim().toLowerCase();
                    String Subject = edtSubJect.getText().trim();
                    String Body = _htmlPane.getText();
                    String[] Attachments = attachMent;

                    Gmail service = GmailAPI.getGmailService();
                    MimeMessage Mimemessage = createEmailWithAttachment(To,"me",Subject,Body,new File(System.getProperty("user.dir") + "/" + filename));
                    Message message = createMessageWithEmail(Mimemessage);
                    message = service.users().messages().send("me", message).execute();

                    //System.out.println("Message id: " + message.getId());
                    //System.out.println(message.toPrettyString());
                    if (message.getId() == null) {
                        JOptionPane.showMessageDialog(null, "Erro ao enviar!!!\n\nTente novamente...", "Atenção", JOptionPane.ERROR_MESSAGE);
                    } else {
                        conn.ExecutarComando("UPDATE recibo SET emailbol = 'S' WHERE contrato = '" + contrato + "' AND dtvencimento = '" +
                            Dates.StringtoString(vencto, "dd/MM/yyyy", "yyyy/MM/dd") + "';");
                        //JOptionPane.showMessageDialog(null, "Enviado com sucesso!!!", "Atenção", JOptionPane.INFORMATION_MESSAGE);
                    }
                    tblEmails.getModel().setValueAt(message.getId() != null ? "Ok" : "Err", modelRow, 6);
                } catch (Exception ex) { ex.printStackTrace(); }
            }
            
            int pgs = ((b++ * 100) / rcount) + 1;
            jProgressEmail.setValue(pgs);
            try { Thread.sleep(20); } catch (InterruptedException ex) { }
        }

        tblEmails.clearSelection();
        btnEnviarTodos.setEnabled(true);
    }//GEN-LAST:event_btnEnviarTodosActionPerformed

    private void btnEnviarSelecaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEnviarSelecaoActionPerformed
        btnEnviarSelecao.setEnabled(false);
        int selRows = tblEmails.getSelectedRowCount();
        if (selRows <= 0) {
            btnEnviarSelecao.setEnabled(true);
            return;
        }

        if (edtSubJect.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "O campo Assunto não pode ser vazio!");
            btnEnviarSelecao.setEnabled(true);
            edtSubJect.requestFocus();
            return;
        }

        String htmlText = "";
        try {htmlText = _htmlPane.getDocument().getText(0, _htmlPane.getDocument().getEndPosition().getOffset());} catch (BadLocationException ba) {}
        if (htmlText.trim().equalsIgnoreCase("")) {
            JOptionPane.showMessageDialog(null, "O campo Mensagem não pode ser vazio!");
            btnEnviarSelecao.setEnabled(true);
            _htmlPane.requestFocus();
            return;
        }

        int[] selRow = tblEmails.getSelectedRows();

        String contrato = null;
        String vencto = "";
        String filename = "";
        String status = "";

        int b = 1; int rcount = selRow.length;
        jProgressEmail.setValue(0);
        
        for (int i=0;i<=selRow.length - 1;i++) {
            int nRow = selRow[i];
            int modelRow = tblEmails.convertRowIndexToModel(nRow);

            contrato = tblEmails.getModel().getValueAt(modelRow, 0).toString();
            String[][] EmailLocaDados = null;
            try {
                EmailLocaDados = conn.LerCamposTabela(new String[] {"nomerazao","email"}, "locatarios", "contrato = '" + contrato + "'");
            } catch (SQLException e) {}
            
            String EmailLoca = null;
            if (EmailLocaDados != null) EmailLoca = EmailLocaDados[1][3].toLowerCase();
            
            vencto = tblEmails.getModel().getValueAt(modelRow, 2).toString();
            try {filename = tblEmails.getModel().getValueAt(modelRow, 5).toString();} catch (Exception ex) {}
            String[] attachMent;
            if (filename.isEmpty()) {
                attachMent = new String[]{};
            } else {
                attachMent = new String[]{System.getProperty("user.dir") + "/" + filename};
            }
            status = tblEmails.getModel().getValueAt(modelRow, 6).toString();

            if (!status.equalsIgnoreCase("OK")) {
                Outlook email = new Outlook(true);
                try {            
                    String To = EmailLoca.trim().toLowerCase();
                    String Subject = edtSubJect.getText().trim();
                    String Body = _htmlPane.getText();
                    String[] Attachments = attachMent;
                    email.Send(To, null, Subject, Body, Attachments);
                    if (!email.isSend()) {
                        //JOptionPane.showMessageDialog(null, "Erro ao enviar!!!\n\nTente novamente...", "Atenção", JOptionPane.ERROR_MESSAGE);
                    } else {
                        conn.ExecutarComando("UPDATE recibo SET emailbol = 'S' WHERE contrato = '" + contrato + "' AND dtvencimento = '" +
                            Dates.StringtoString(vencto, "dd/MM/yyyy", "yyyy/MM/dd") + "';");
                        //JOptionPane.showMessageDialog(null, "Enviado com sucesso!!!", "Atenção", JOptionPane.INFORMATION_MESSAGE);
                    }
                    tblEmails.getModel().setValueAt(email.isSend() ? "Ok" : "Err", modelRow, 6);
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    email = null;
                }
            }
            
            int pgs = ((b++ * 100) / rcount) + 1;
            jProgressEmail.setValue(pgs);            
            try { Thread.sleep(20); } catch (InterruptedException ex) { }
        }

        tblEmails.clearSelection();
        btnEnviarSelecao.setEnabled(true);
    }//GEN-LAST:event_btnEnviarSelecaoActionPerformed

    private void btnEditarCadastroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditarCadastroActionPerformed
        int selRows = tblEmails.getSelectedRowCount();
        if (selRows <= 0) {
            return;
        }

        int[] selRow = tblEmails.getSelectedRows();

        String contrato = null;
        for (int i=0;i<=0;i++) {
            int nRow = selRow[i];
            int modelRow = tblEmails.convertRowIndexToModel(nRow);

            contrato = tblEmails.getModel().getValueAt(modelRow, 0).toString();
            String _class = ""; String _method = ""; String[] _args = {};
            _class = "Sici.Locatarios.jLocatarios";
            _method = "MoveToLoca";
            _args = new String[] {"contrato", contrato};

            try {
                Class classe = null;
                classe = Class.forName(_class);
                JInternalFrame frame = (JInternalFrame) classe.newInstance();

                Class[] args1 = new Class[2];
                args1[0] = String.class;
                args1[1] = String.class;
                Method mtd = classe.getMethod(_method, args1);
                mtd.invoke(frame, _args);

                VariaveisGlobais.jPanePrin.add(frame);
                CentralizaTela.setCentro(frame, VariaveisGlobais.jPanePrin, 0, 0);

                VariaveisGlobais.jPanePrin.getDesktopManager().activateFrame(frame);
                frame.requestFocus();
                frame.setSelected(true);
                frame.setVisible(true);
            } catch (Exception e) {e.printStackTrace();}

        }
    }//GEN-LAST:event_btnEditarCadastroActionPerformed

    private void btnListarBoletasEmailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnListarBoletasEmailActionPerformed
        String Sql = "SELECT r.rgprp, r.rgimv, r.contrato, l.nomerazao, r.campo, r.dtvencimento, r.nnumero, c.dtultrecebimento, " +
        "l.boleta, 1 gerados, l.envio, l.bcobol, b.nome as bconome, r.dtvencbol, r.boletapath FROM RECIBO r, locatarios l, CARTEIRA c, bancos b " +
        "where l.boleta = -1 AND (r.tag <> 'X') AND (l.fiador1uf is null) AND (r.contrato = l.contrato and c.contrato = " +
        "l.contrato) AND (r.remessa = 'S' AND r.emailbol = 'N' AND l.envio = '1') AND (b.codigo = l.bcobol) AND " + 
        "EXISTS(SELECT * FROM bancos_digital bd WHERE b.codigo = bd.nbanco LIMIT 1) ORDER BY l.nomerazao;";
        ResultSet rs = conn.AbrirTabela(Sql, ResultSet.CONCUR_READ_ONLY);

        TableControl.header(tblEmails, new String[][] {{"contrato","nome","vencimento","Boleta","nnumero","fileName","Status"},{"60","200","70","70","120","0","30"}});
        TableControl.Clear(tblEmails);

        int b = 1;
        jProgressEmail.setValue(0);
        
        // Atribuições
        String tcontrato = "";
        String tnome = "";
        String tvencto = "";
        String tvenctoBol = "";
        String tnnumero = "";
        String tfilename = "";
        int rcount = 0;
        try {
            rcount = DbMain.RecordCount(rs);
            while (rs.next()) {
                tcontrato = rs.getString("contrato").toUpperCase();
                tnome = rs.getString("nomerazao").trim();
                tvencto = Dates.DateFormata("dd-MM-yyyy", Dates.StringtoDate(rs.getString("dtvencimento").toUpperCase(),"yyyy-MM-dd"));
                tvenctoBol = null;
                try {tvenctoBol = rs.getString("dtvencbol");} catch (SQLException e) {}
                if (tvenctoBol != null) tvenctoBol = Dates.DateFormata("dd-MM-yyyy", Dates.StringtoDate(tvenctoBol.toUpperCase(),"yyyy-MM-dd"));
                tnnumero = rs.getString("nnumero");
                tfilename = "";
                try {tfilename = rs.getString("boletapath");} catch (SQLException ex) {}
                TableControl.add(tblEmails, new String[][]{{tcontrato, tnome, tvencto, tvenctoBol, tnnumero, tfilename, ""},{"C","L","C","C","C","L","C"}}, true);

                int pgs = ((b++ * 100) / rcount) + 1;
                jProgressEmail.setValue(pgs);
                try { Thread.sleep(20); } catch (InterruptedException ex) { }
            }
        } catch (SQLException ex) {}
        DbMain.FecharTabela(rs);
    }//GEN-LAST:event_btnListarBoletasEmailActionPerformed

    private void conBtnListarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_conBtnListarActionPerformed
        try {
            ListaBoletasBanco();
        } catch (Exception ex) {
            System.out.println("Problema de comunicação com o banco ou sem Internet.");
        }
    }//GEN-LAST:event_conBtnListarActionPerformed

    private void TokenExpiration() {
        // Geração do Token
        if (this.token == null) {
            bancos bco = new bancos(jcbConsultaBancos.getSelectedItem().toString().substring(0,3));
            final String path = bco.getBanco_CertPath();
            final String path_crt = path + bco.getBanco_CrtFile();
            final String path_key = path + bco.getBanco_KeyFile();
            final String clientid = bco.getBanco_ClientId().trim();
            final String clientsecret = bco.getBanco_ClientSecret().trim();
            final String tokenUrl = "https://cdpj.partners.bancointer.com.br/oauth/v2/token";
            try {
                Inter c  = new Inter();
                Object[] msg = c.getToken(new String[] {tokenUrl,clientid,clientsecret}, path_crt, path_key);
                String codErr = msg[0].toString();
                String msgErr = "";
                try { 
                    msgErr = (String)msg[1]; 
                } catch (ClassCastException e) {            
                    if (e.getMessage().toString().contains("[Ljava.lang.Object;")) {                            
                        Object[] _msgErr = (Object[])msg[1]; 
                        this.token = msgErr = ((JSONObject)_msgErr[0]).getString("access_token");
                        
                        long time = System.currentTimeMillis();
                        this.token_hora = new Date();
                    } else {
                        this.token = null;
                        System.out.println("API Inter erro não depurado");
                        return;
                    }            
                }
            } catch (Exception e) {}
        } else {
            Date agora = new Date();
            long diffMs =  this.token_hora.getTime() - agora.getTime();
            long diffSec = diffMs / 1000;
            long min = (diffSec / 60) * -1;
            if (min >= 60) {
                this.token = null;
                TokenExpiration();
            }
        }        
    }
    
    private void conListaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_conListaMouseClicked
        final int selRow = conLista.getSelectedRow();
        if (selRow <= -1) return;
        String situacao = conLista.getValueAt(selRow, 10).toString().toUpperCase();
        String baixado =  conLista.getValueAt(selRow, 11).toString().toUpperCase();
        String meunumero = conLista.getValueAt(selRow, 03).toString().toUpperCase();
        
        if (evt.getButton() != MouseEvent.BUTTON3) return;
        if (!"EMABERTO;PAGO".contains(situacao)) return;
        if (situacao.equalsIgnoreCase("PAGO") && baixado.equalsIgnoreCase("S")) return;
        if (situacao.equalsIgnoreCase("PAGO") && baixado.equalsIgnoreCase("N") && meunumero.equalsIgnoreCase("AVULSA")) return;
        
        final int selCol = 4;
        if (selRow == -1) return;
                
        bancos bco = new bancos(jcbConsultaBancos.getSelectedItem().toString().substring(0,3));
        final String path = bco.getBanco_CertPath();
        final String path_crt = path + bco.getBanco_CrtFile();
        final String path_key = path + bco.getBanco_KeyFile();
        final String ccorrente = bco.getBanco_CCORRENTE();
        final String nNumero = conLista.getValueAt(selRow, selCol).toString();

        // Pega token e verifica se expirou
        TokenExpiration();
        final String token = this.token;

        JMenuItem item1 = new JMenuItem("Baixado a pedido do cliente");
        item1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    String url_baixa = "https://cdpj.partners.bancointer.com.br/cobranca/v2/boletos/&1./cancelar";
                    url_baixa = FuncoesGlobais.Subst(url_baixa, new String[] {nNumero});
                    String codBaixa = "{\"motivoCancelamento\": \"APEDIDODOCLIENTE\"}";
                    Inter c  = new Inter();
                    Object[] baixa = c.baixaBoleta(new String[] {url_baixa, ccorrente,token}, path_crt, path_key, codBaixa);
                    int statusCode = (int)baixa[0];
                    Object[] infoMessage = (Object[])baixa[1];
                    if ((int)baixa[0] != 204) {            
                        System.out.println("Codigo do Erro:" + statusCode + " - " + infoMessage[0]);
                    } else {
                        try {
                            ListaBoletasBanco();
                        } catch (Exception ex) {
                            System.out.println("Problema de comunicação com o banco ou sem Internet.");
                        }
                        System.out.println(infoMessage[0]);
                    }
                } catch (Exception ex) {}
            }
        });

        JMenuItem item2 = new JMenuItem("Copiar NossoNumero para área de transferência");
        item2.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                final int selRow = conLista.getSelectedRow();
                try {
                    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(conLista.getValueAt(selRow, 4).toString()), null);
                } catch (Exception ex) {}
            }
        });

//        JMenuItem item3 = new JMenuItem("Baixa por acertos");
//        item3.addActionListener(new ActionListener() {
//
//            public void actionPerformed(ActionEvent e) {
//                
//            }
//        });
//
//        JMenuItem item4 = new JMenuItem("Baixado por ter sido protestado");
//        item4.addActionListener(new ActionListener() {
//
//            public void actionPerformed(ActionEvent e) {
//                
//            }
//        });
//
//        JMenuItem item5 = new JMenuItem("Baixado para devolução");
//        item5.addActionListener(new ActionListener() {
//
//            public void actionPerformed(ActionEvent e) {
//                
//            }
//        });
//
//        JMenuItem item6 = new JMenuItem("Baixado por protesto após baixa");
//        item6.addActionListener(new ActionListener() {
//
//            public void actionPerformed(ActionEvent e) {
//                
//            }
//        });
//
//        JMenuItem item7 = new JMenuItem("Baixado, pago direto ao cliente");
//        item7.addActionListener(new ActionListener() {
//
//            public void actionPerformed(ActionEvent e) {
//                
//            }
//        });
//
//        JMenuItem item8 = new JMenuItem("Baixado por substituição");
//        item8.addActionListener(new ActionListener() {
//
//            public void actionPerformed(ActionEvent e) {
//                
//            }
//        });

        JPopupMenu popup = new JPopupMenu();
        if (situacao.contains("EMABERTO")) popup.add(item1);
        if (situacao.contains("PAGO")) popup.add(item2);
//        popup.add(item3);
//        popup.add(item4);
//        popup.add(item5);
//        popup.add(item6);
//        popup.add(item7);
//        popup.add(item8);

        //mostra na tela
        int xPos = conLista.getMousePosition().x;
        int yPos = conLista.getMousePosition().y;
        popup.show(conLista, xPos, yPos);        
    }//GEN-LAST:event_conListaMouseClicked

    private void conListaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_conListaKeyPressed
//        final int selRow = conLista.getSelectedRow();
//        if (selRow <= -1) return;
//        String situacao = conLista.getValueAt(selRow, 10).toString().toUpperCase();
//        String baixado =  conLista.getValueAt(selRow, 11).toString().toUpperCase();
//        if (!"PAGO".contains(situacao)) conLista.setValueAt(false, selRow, 12);
//        if ("PAGO".contains(situacao)) {
//            if (conLista.getValueAt(selRow, 11).toString().equalsIgnoreCase("S")) {
//                conLista.setValueAt(false, selRow,12);
//                conLista.repaint();
//            }
//        }
    }//GEN-LAST:event_conListaKeyPressed

    private void jBtnBaixarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnBaixarActionPerformed
        List<classBaixar> listas = new ArrayList<>();
        Map<String, Object> param = new HashMap();
        
        if (conLista.getSelectedRows().length == 0) {
            for (int i = 0; i < conLista.getRowCount(); i++) {
                String avulso =   conLista.getValueAt(i, 3).toString().toUpperCase();
                if ("AVULSO".contains(avulso)) continue;

                String situacao = conLista.getValueAt(i, 10).toString().toUpperCase();
                String baixado =  conLista.getValueAt(i, 11).toString().toUpperCase();
                if ("PAGO".contains(situacao) && "N".contains(baixado)) {
                    String seuNumero = conLista.getValueAt(i, 3).toString();
                    String seuNome = conLista.getValueAt(i, 6).toString();
                    String vencimento = conLista.getValueAt(i, 1).toString();
                    String pagamento = conLista.getValueAt(i, 2).toString();
                    String nossoNumero = conLista.getValueAt(i, 4).toString();
                    float multa = LerValor.StringToFloat(conLista.getValueAt(i, 7).toString());
                    float juros = LerValor.StringToFloat(conLista.getValueAt(i, 8).toString());
                    float valor = LerValor.StringToFloat(conLista.getValueAt(i, 9).toString());

                    Date dvencimento = null; Date dpagamento = null;
                    try { dvencimento = Dates.StringtoDate(vencimento, "dd-MM-yyyy"); } catch (Exception e) {}
                    try { dpagamento = Dates.StringtoDate(pagamento, "dd-MM-yyyy"); } catch (Exception e) {}

                    classBaixar tbaixar = new classBaixar(seuNumero, seuNome, dvencimento, dpagamento, nossoNumero, multa, juros, valor);
                    tbaixar.setAtrasado();
                    listas.add(tbaixar);
                } 
            }
            param.put("banco", jcbConsultaBancos.getSelectedItem().toString());
            param.put("logado", VariaveisGlobais.usuario.toUpperCase().trim());
            param.put("status", "NÃO BAXADOS");
        } else {
            for (int i = 0; i < conLista.getSelectedRows().length; i++) {
                int selecRow = conLista.getSelectedRows()[i];
                String avulso = conLista.getValueAt(selecRow, 3).toString().toUpperCase();
                if ("AVULSO".contains(avulso)) continue;

                String situacao = conLista.getValueAt(selecRow, 10).toString().toUpperCase();
                String baixado =  conLista.getValueAt(selecRow, 11).toString().toUpperCase();
                if ("PAGO".contains(situacao) && "N".contains(baixado)) {
                    String seuNumero = conLista.getValueAt(selecRow, 3).toString();
                    String seuNome = conLista.getValueAt(selecRow, 6).toString();
                    String vencimento = conLista.getValueAt(selecRow, 1).toString();
                    String pagamento = conLista.getValueAt(selecRow, 2).toString();
                    String nossoNumero = conLista.getValueAt(selecRow, 4).toString();
                    float multa = LerValor.StringToFloat(conLista.getValueAt(selecRow, 7).toString());
                    float juros = LerValor.StringToFloat(conLista.getValueAt(selecRow, 8).toString());
                    float valor = LerValor.StringToFloat(conLista.getValueAt(selecRow, 9).toString());

                    Date dvencimento = null; Date dpagamento = null;
                    try { dvencimento = Dates.StringtoDate(vencimento, "dd-MM-yyyy"); } catch (Exception e) {}
                    try { dpagamento = Dates.StringtoDate(pagamento, "dd-MM-yyyy"); } catch (Exception e) {}

                    classBaixar tbaixar = new classBaixar(seuNumero, seuNome, dvencimento, dpagamento, nossoNumero, multa, juros, valor);
                    tbaixar.setAtrasado();
                    listas.add(tbaixar);
                } 
            }
            param.put("banco", jcbConsultaBancos.getSelectedItem().toString());
            param.put("logado", VariaveisGlobais.usuario.toUpperCase().trim());
            param.put("status", "");
        }
        
        if (listas.size() <= 0) {
            JOptionPane.showInternalMessageDialog(this, "Não há vencimentos a baixar no sistema.");
        } else {
            try {
                if (conLista.getSelectedRows().length == 0) {
                    String fileName = "reports/BolBaixadas.jasper";
                    JRDataSource jrds = new JRBeanCollectionDataSource(listas);
                    JasperPrint print = JasperFillManager.fillReport(fileName, param, jrds);

                    // Create a PDF exporter
                    JRExporter exporter = new JRPdfExporter();

                    new jDirectory("reports/Relatorios/" + Dates.iYear(new Date()) + "/" + Dates.Month(new Date()) + "/");
                    String pathName = "reports/Relatorios/" + Dates.iYear(new Date()) + "/" + Dates.Month(new Date()) + "/";

                    // Configure the exporter (set output file name and print object)
                    String outFileName = pathName + "BolBaixadas_" + Dates.DateFormata("ddMMyyyyHHmmss", new Date()) + ".pdf";
                    exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, outFileName);
                    exporter.setParameter(JRExporterParameter.JASPER_PRINT, print);

                    // Export the PDF file
                    exporter.exportReport();
                    new toPreview(outFileName);
                } else {
                    if (JOptionPane.showConfirmDialog(this, "Consolida as Boletas?", "Consolidação",JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                        // Baixa as boletas
                        for (int i = 0; i < conLista.getSelectedRows().length; i++) {
                            int selecRow = conLista.getSelectedRows()[i];
                            String avulso = conLista.getValueAt(selecRow, 3).toString().toUpperCase();
                            if ("AVULSO".contains(avulso)) continue;

                            String situacao = conLista.getValueAt(selecRow, 10).toString().toUpperCase();
                            String baixado =  conLista.getValueAt(selecRow, 11).toString().toUpperCase();
                            if ("PAGO".contains(situacao) && "N".contains(baixado)) {
                                String seuNumero = conLista.getValueAt(selecRow, 3).toString();
                                String seuNome = conLista.getValueAt(selecRow, 6).toString();
                                String vencimento = conLista.getValueAt(selecRow, 1).toString();
                                String pagamento = conLista.getValueAt(selecRow, 2).toString();
                                String nossoNumero = conLista.getValueAt(selecRow, 4).toString();
                                float multa = LerValor.StringToFloat(conLista.getValueAt(selecRow, 7).toString());
                                float juros = LerValor.StringToFloat(conLista.getValueAt(selecRow, 8).toString());
                                float valor = LerValor.StringToFloat(conLista.getValueAt(selecRow, 9).toString());

                                Date dvencimento = null; Date dpagamento = null;
                                try { dvencimento = Dates.StringtoDate(vencimento, "dd-MM-yyyy"); } catch (Exception e) {}
                                try { dpagamento = Dates.StringtoDate(pagamento, "dd-MM-yyyy"); } catch (Exception e) {}

                                // Faz a baixa da boleta
                                boolean bSucesso = Distribuicao(selecRow);
                                if (!bSucesso) {
                                    Object obj = conLista.getSelectedRows()[selecRow];
                                    listas.remove(listas.indexOf(obj));
                                } 
                            } 
                        }
                        
                        // Imprime na impressora jato/laser configurada
                        String fileName = "reports/BolBaixadas.jasper";
                        JRDataSource jrds = new JRBeanCollectionDataSource(listas);
                        JasperPrint print = JasperFillManager.fillReport(fileName, param, jrds);

                        // Create a PDF exporter
                        JRExporter exporter = new JRPdfExporter();

                        new jDirectory("reports/Relatorios/" + Dates.iYear(new Date()) + "/" + Dates.Month(new Date()) + "/");
                        String pathName = "reports/Relatorios/" + Dates.iYear(new Date()) + "/" + Dates.Month(new Date()) + "/";

                        // Configure the exporter (set output file name and print object)
                        String outFileName = pathName + "BolBaixadas_" + Dates.DateFormata("ddMMyyyyHHmmss", new Date()) + ".pdf";
                        exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, outFileName);
                        exporter.setParameter(JRExporterParameter.JASPER_PRINT, print);

                        // Export the PDF file
                        exporter.exportReport();
                        new toPrint2(outFileName, 3);
                    }
                }                    
            } catch (JRException e) {
                e.printStackTrace();
                System.exit(1);
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }        
            
        }
    }//GEN-LAST:event_jBtnBaixarActionPerformed

    private void opcpropActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_opcpropActionPerformed
        FillOpcao();
    }//GEN-LAST:event_opcpropActionPerformed

    private void opclocaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_opclocaActionPerformed
        FillOpcao();
    }//GEN-LAST:event_opclocaActionPerformed

    private void opcavulsaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_opcavulsaActionPerformed
        FillOpcao();
    }//GEN-LAST:event_opcavulsaActionPerformed

    private void jnomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jnomeActionPerformed
        if (!bExecCodigo) {
            int pos = jnome.getSelectedIndex();
            if (jcodigo.getItemCount() > 0) {bExecNome = true; jcodigo.setSelectedIndex(pos); bExecNome = false;}
        }
        Setar();
    }//GEN-LAST:event_jnomeActionPerformed

    private void jcodigoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcodigoActionPerformed
        if (!bExecNome) {
            int pos = jcodigo.getSelectedIndex();
            if (jnome.getItemCount() > 0) {bExecCodigo = true; jnome.setSelectedIndex(pos); bExecCodigo = false;}
        }
        Setar();
    }//GEN-LAST:event_jcodigoActionPerformed

    private void btImprimirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btImprimirActionPerformed
        List<Boleta> lista = new ArrayList<Boleta>();
        Boleta Bean1 = null;
        try {
            Bean1 = CreateBoletaAvulsa(jbanco.getSelectedItem().toString().substring(0,3));
            if (Bean1 != null) {
                lista.add(Bean1);

                JRDataSource jrds = new JRBeanCollectionDataSource(lista);

                try {
                    String fileName = "reports/Boletos_avulsas.jasper";
                    JasperPrint print = JasperFillManager.fillReport(fileName, null, jrds);

                    // Create a PDF exporter
                    JRExporter exporter = new JRPdfExporter();

                    new jDirectory("reports/BoletasDigital/" + Dates.iYear(new Date()) + "/" + Dates.Month(new Date()) + "/");
                    String pathName = "reports/BoletasDigital/" + Dates.iYear(new Date()) + "/" + Dates.Month(new Date()) + "/";

                    String tCodigo = "";
                    if (opcavulsa.isSelected()) {
                        tCodigo = "000000";
                    } else {
                        tCodigo = jcodigo.getSelectedItem().toString();                        
                    }
                    // Configure the exporter (set output file name and print object)
                    String outFileName = pathName + tCodigo + "_" + Bean1.getsacDadosNome() + "_" + 
                            Dates.DateFormata("ddMMyyyy", jvencto.getDate()) + "_" + 
                            Bean1.getbolDadosNnumero().substring(0,11) + "_AVULSA.pdf";
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
                JOptionPane.showMessageDialog(this, "Boleta Gerada com sucesso.\n\n" + 
                        "Use <Relatórios><Visualizar/Imprimir> documentos para Imprimir ou enviar via e-mail.", 
                        "Sucesso!.", INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "CodErro: " + codErro + " - " + msgErro, "Erro ao enviar para o banco." , ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        
        Clean();
        CleanHist();

    }//GEN-LAST:event_btImprimirActionPerformed

    private Boleta CreateBoletaAvulsa(String numBanco) throws SQLException {
        bancos bco = new bancos(numBanco);
        classInter pagador = new classInter();
        
        Collections gVar = VariaveisGlobais.dCliente;

        Boleta bean1 = new Boleta();
        bean1.setempNome(gVar.get("empresa").toUpperCase().trim());
        bean1.setempEndL1(gVar.get("endereco") + ", " + gVar.get("numero") + gVar.get("complemento") + " - " + gVar.get("bairro"));
        bean1.setempEndL2(gVar.get("cidade") + " - " + gVar.get("estado") + " - CEP " + gVar.get("cep"));
        bean1.setempEndL3("Tel/Fax.: " + gVar.get("telefone"));
        bean1.setempEndL4(gVar.get("hpage") + " / " + gVar.get("email"));

        // Logo da Imobiliaria
        bean1.setlogoLocation("resources/logos/boleta/" + VariaveisGlobais.icoBoleta);

        bean1.setlocaMsgL01("");
        pagador.setMensagem_linha4("");

        bean1.setlocaMsgL02("");
        pagador.setMensagem_linha1("");

        // Logo do Banco
        bean1.setlogoBanco(bco.getBanco_logo());
        
        bean1.setnumeroBanco(bco.getBanco_NBANCO() + "-" + bco.getBanco_NBANCODV());

        try { bean1.setlocaDescL01(phistorico01.getText()); } catch (Exception ex) {}

        bean1.setbolDadosVencimento(Dates.DateFormata("dd-MM-yyyy", jvencto.getDate()));
        pagador.setDataVencimento(Dates.DateFormata("dd-MM-yyyy", jvencto.getDate()));
        
        String cValor = Bancos.bancos.Valor4Boleta(jvalor.getText());  // valor da boleta
        bean1.setbolDadosVrdoc(jvalor.getText());
        pagador.setValorNominal(jvalor.getText().replace(".", "").replace(",", "."));
        pagador.setValorAbatimento("0");

        String showCarteira = bco.getBanco_CARTEIRA(); String NossoNumero = bco.getBanco_AGENCIA() + bco.getBanco_AGENCIADV() + "/" + bco.getBanco_CARTEIRA() + "/" + "0070438957-8"; 
        String cdBarras = ""; String lnDig = "";

        bean1.setbolDadosAgcodced(bco.getBanco_AGENCIA() + bco.getBanco_AGENCIADV() + "/" + bco.getBanco_BENEFICIARIO().trim());
        
        bean1.setbolDadosNumdoc("AVULSA");
        pagador.setSeuNumero("AVULSA");

        bean1.setsacDadosNome(pnome.getText().toUpperCase());  // Nome do Sacado
        pagador.setNome(pnome.getText().toUpperCase());
        bean1.setsacDadosCpfcnpj("CNPJ/CPF: " + pdocumento.getText().trim());  // Cpf ou Cnpj do Sacado
        pagador.setCnpjCpf(pdocumento.getText().trim());
        pagador.setTipopessoa(pagador.getCnpjCpf().length() == 11 ? pagador.inter_PAGADOR_TIPOPESSOA_FISICA : pagador.inter_PAGADOR_TIPOPESSOA_JURIDICA);

        bean1.setsacDadosEndereco(pendereco.getText().trim().toUpperCase());
        pagador.setEndereco(pendereco.getText().trim().toUpperCase());
        bean1.setsacDadosNumero(pnumero.getText().trim());
        pagador.setNumero(pnumero.getText().trim());
        bean1.setsacDadosCompl(pcomplto.getText().trim().toUpperCase());
        pagador.setComplemento(pcomplto.getText().trim().toUpperCase());
        bean1.setsacDadosBairro(pbairro.getText().trim().toUpperCase());
        pagador.setBairro(pbairro.getText().trim().toUpperCase());
        bean1.setsacDadosCidade(pcidade.getText().trim().toUpperCase());
        pagador.setCidade(pcidade.getText().trim().toUpperCase());
        bean1.setsacDadosEstado(pestado.getText().toLowerCase().toUpperCase());
        pagador.setUf(pestado.getText().trim().toUpperCase());
        
        pagador.setCep(pcep.getText().trim());
        bean1.setsacDadosCep(pagador.getCep());        

        bean1.setbcoMsgL01("PAGAVEL EM QUALQUER BANCO OU PELA INTERNET.");
        bean1.setbcoMsgL02("");
        bean1.setbolDadosEspeciedoc("OU");
               
        bean1.setbolDadosCedente(gVar.get("cnpj") + " - " + gVar.get("empresa").toUpperCase());
        pagador.setCnpjCPFBeneficiario(gVar.get("cnpj"));                    
        bean1.setbolDadosDatadoc(Dates.DatetoString(new Date()));
        pagador.setDataEmissao(Dates.DatetoString(new Date()));
        bean1.setbolDadosAceite("N");
        bean1.setbolDadosDtproc(Dates.DatetoString(new Date()));
        bean1.setbolDadosUsobco("");
        bean1.setbolDadosCarteira(showCarteira);
        bean1.setbolDadosEspecie("R$");
        bean1.setbolDadosQtde("");
        bean1.setbolDadosValor("");
 
        String msgBol01 = jAvMsg01.getText(); if (msgBol01 == null) msgBol01 = "";
        String msgBol02 = jAvMsg02.getText(); if (msgBol02 == null) msgBol02 = "";
        String msgBol03 = jAvMsg03.getText(); if (msgBol03 == null) msgBol03 = "";
        String msgBol04 = jAvMsg04.getText(); if (msgBol04 == null) msgBol04 = "";
        String msgBol05 = jAvMsg05.getText(); if (msgBol05 == null) msgBol05 = "";
        String msgBol06 = jAvMsg06.getText(); if (msgBol06 == null) msgBol06 = "";
        String msgBol07 = jAvMsg07.getText(); if (msgBol07 == null) msgBol07 = "";
        String msgBol08 = jAvMsg08.getText(); if (msgBol08 == null) msgBol08 = "";
        String msgBol09 = jAvMsg09.getText(); if (msgBol09 == null) msgBol09 = "";

        bean1.setbolDadosMsg01(msgBol01);
        bean1.setbolDadosMsg02(msgBol02);
        pagador.setMensagem_linha2(msgBol04);
        bean1.setbolDadosMsg03(msgBol03);
        pagador.setMensagem_linha3(msgBol05);
        bean1.setbolDadosMsg04(msgBol04);
        bean1.setbolDadosMsg05(msgBol05);
        bean1.setbolDadosMsg06(msgBol06);
        bean1.setbolDadosMsg07(msgBol07);

        Date tvecto = jvencto.getDate();
        String carVecto = Dates.DateFormata("dd/MM/yyyy", 
                        Dates.DateAdd(Dates.DIA, 0, tvecto));

        String ln08 = "";
        bean1.setbolDadosMsg08(ln08);
        pagador.setMensagem_linha4(msgBol08);
        bean1.setbolDadosMsg09("".equals(msgBol09) ? "NÃO RECEBER APÓS 30 DIAS DO VENCIMENTO." : msgBol09);
        pagador.setMensagem_linha5(msgBol09);
        
        // Descontos
        bean1.setbolDadosDesconto("");
        pagador.setDesconto1_codigoDesconto(pagador.inter_DESCONTO_NAOTEMDESCONTO);
        pagador.setDesconto1_data("");
        pagador.setDesconto1_taxa("0");
        pagador.setDesconto1_valor("0");
        
        pagador.setDesconto2_codigoDesconto(pagador.inter_DESCONTO_NAOTEMDESCONTO);
        pagador.setDesconto2_data("");
        pagador.setDesconto2_taxa("0");
        pagador.setDesconto2_valor("0");

        pagador.setDesconto3_codigoDesconto(pagador.inter_DESCONTO_NAOTEMDESCONTO);
        pagador.setDesconto3_data("");
        pagador.setDesconto3_taxa("0");
        pagador.setDesconto3_valor("0");
        
        bean1.setbolDadosMora("");
        
        // Multa
        pagador.setMulta_codigoMulta(pagador.inter_MULTA_PERCENTUAL);
        pagador.setMulta_data(Dates.DateFormata("dd/MM/yyyy", 
                        Dates.DateAdd(Dates.DIA, 1, tvecto)));
        
        pagador.setMulta_taxa(String.valueOf(10));
        pagador.setMulta_valor("0");
        
        // Mora
        pagador.setMora_codigoMora(pagador.inter_MORA_TAXAMENSAL);
        pagador.setMora_data(Dates.DateFormata("dd/MM/yyyy", 
                        Dates.DateAdd(Dates.DIA, 1, tvecto)));
        pagador.setMora_taxa(String.valueOf(1));
        pagador.setMora_valor("0");
        
        bean1.setbolDadosVrcobrado("");

        pagador.setDataLimite(pagador.inter_NUNDIASAGENDA_TRINTA);
        //pagador.setNumDiasAgenda(pagador.inter_NUNDIASAGENDA_TRINTA);
        pagador.setNumDiasAgenda(30);

        /*
        / Registrar boleta no banco
        */                
        String url_ws = "https://cdpj.partners.bancointer.com.br/cobranca/v2/boletos";
        String path = bco.getBanco_CertPath();
        String path_crt = path + bco.getBanco_CrtFile();
        String path_key = path + bco.getBanco_KeyFile();
        final String ccorrente = bco.getBanco_CCORRENTE();

        // Pega token e verifica se expirou
        TokenExpiration();
        final String token = this.token;
        
        boolean pegueiBanco = true;
        Inter c  = new Inter();
        Object[] message = null;
        try {
            message = c.insertBoleta(new String[] {url_ws, ccorrente, token}, path_crt, path_key, pagador.getJSONBoleto());    
        } catch (Exception e) { pegueiBanco = false; }
        
        codErro = c.getCodErro();
        msgErro = c.getMsgErro();
        
        if (!pegueiBanco) return null;
        
        int statusCode = (int)message[0];
        String[] infoMessage = (String[])message[1];
        if ((int)message[0] != 200) {            
            System.out.println("Codigo do Erro:" + statusCode + " - " + (infoMessage != null ? infoMessage[0] : ""));
            //JOptionPane.showMessageDialog(null, "Codigo do Erro:" + statusCode + " - " + (infoMessage != null ? infoMessage[0] : ""));
            return null;
        } else {
            NossoNumero = infoMessage[0];
            cdBarras = infoMessage[1];
            lnDig = infoMessage[2];
        }

        // Pegar no openbanking
        //
        bean1.setbolDadosNnumero(NossoNumero);
        bean1.setcodDadosDigitavel(lnDig);
        bean1.setcodDadosBarras(cdBarras);

        return bean1;
    }
    
    private void btLimparActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btLimparActionPerformed
        if (opcprop.isSelected() || opcloca.isSelected()) return;

        Clean();
        CleanHist();
    }//GEN-LAST:event_btLimparActionPerformed

    private void jmsgGravarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmsgGravarActionPerformed
        String avsMSG01 = jAvMsg01.getText(); try { conn.GravarParametros(new String[] {"avsMSG01",avsMSG01,"TEXTO"}); } catch (Exception e) {}
        String avsMSG02 = jAvMsg02.getText(); try { conn.GravarParametros(new String[] {"avsMSG02",avsMSG02,"TEXTO"}); } catch (Exception e) {}
        String avsMSG03 = jAvMsg03.getText(); try { conn.GravarParametros(new String[] {"avsMSG03",avsMSG03,"TEXTO"}); } catch (Exception e) {}
        String avsMSG04 = jAvMsg04.getText(); try { conn.GravarParametros(new String[] {"avsMSG04",avsMSG04,"TEXTO"}); } catch (Exception e) {}
        String avsMSG05 = jAvMsg05.getText(); try { conn.GravarParametros(new String[] {"avsMSG05",avsMSG05,"TEXTO"}); } catch (Exception e) {}
        String avsMSG06 = jAvMsg06.getText(); try { conn.GravarParametros(new String[] {"avsMSG06",avsMSG06,"TEXTO"}); } catch (Exception e) {}
        String avsMSG07 = jAvMsg07.getText(); try { conn.GravarParametros(new String[] {"avsMSG07",avsMSG07,"TEXTO"}); } catch (Exception e) {}
        String avsMSG08 = jAvMsg08.getText(); try { conn.GravarParametros(new String[] {"avsMSG08",avsMSG08,"TEXTO"}); } catch (Exception e) {}
        String avsMSG09 = jAvMsg09.getText(); try { conn.GravarParametros(new String[] {"avsMSG09",avsMSG09,"TEXTO"}); } catch (Exception e) {}
    }//GEN-LAST:event_jmsgGravarActionPerformed

    private void jTabbedPane2MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTabbedPane2MousePressed
        LerMsgAv();
    }//GEN-LAST:event_jTabbedPane2MousePressed

    private void conDataFinalFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_conDataFinalFocusGained
        jBtnBaixar.setEnabled(false);
        jBtnBaixar.setText("Baixar [000]");
    }//GEN-LAST:event_conDataFinalFocusGained

    private void conDataInicialFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_conDataInicialFocusGained
        jBtnBaixar.setEnabled(false);
        jBtnBaixar.setText("Baixar [000]");
    }//GEN-LAST:event_conDataInicialFocusGained

    private void jTipoListagemFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTipoListagemFocusGained
        jBtnBaixar.setEnabled(false);
        jBtnBaixar.setText("Baixar [000]");
    }//GEN-LAST:event_jTipoListagemFocusGained

    private void jcbConsultaBancosFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jcbConsultaBancosFocusGained
        jBtnBaixar.setEnabled(false);
        jBtnBaixar.setText("Baixar [000]");
    }//GEN-LAST:event_jcbConsultaBancosFocusGained

    private void ListaBoletasBanco() throws Exception {
        Integer[] tam = {0,80,80,70,100,0,240,70,70,80,80,30};
        String[] col = {"Emissão","Vencimento","Pagamento","SeuNumero","NossoNumero","CnpjCpf","Sacado","Multa","Juros","Valor","Situação","B"};
        Boolean[] edt = {false,false,false,false,false,false,false,false,false,false,false,false};
        String[] aln = {"C","C","C","C","C","C","L","R","R","R","C","C"};
        Object[][] data = {};
        
        int bPagasENaoBaixadas = 0;
        int a = 1; int b = 1;
        jProgressListaBoletasConsulta.setValue(0);
        
        Inter c  = new Inter();
        
        bancos bco = new bancos(jcbConsultaBancos.getSelectedItem().toString().substring(0,3));
        String path = bco.getBanco_CertPath();
        String path_crt = path + bco.getBanco_CrtFile();
        String path_key = path + bco.getBanco_KeyFile();
        final String ccorrente = bco.getBanco_CCORRENTE();

        // Pega token e verifica se expirou
        TokenExpiration();
        final String token = this.token;
        
        String tipoListagem = jTipoListagem.getSelectedItem().toString();
        tipoListagem = (tipoListagem.equalsIgnoreCase("TODOS") ? "" : tipoListagem);
        
        String conDataIni = Dates.DateFormata("yyyy-MM-dd", conDataInicial.getDate());
        String conDataFim = Dates.DateFormata("yyyy-MM-dd", conDataFinal.getDate());
        /*
        / Inicializa variavel para jtable
        */
        List<classInterConsulta> pessoasBoleta = new ArrayList<classInterConsulta>();
        
        String url_consulta = "https://cdpj.partners.bancointer.com.br/cobranca/v2/boletos/sumario" +
                "?dataInicial=&1.&dataFinal=&2.&situacao=&3.";
        url_consulta = FuncoesGlobais.Subst(url_consulta, new String[] {conDataIni, conDataFim, tipoListagem});
        
        Object[] consulta = c.selectBoleta(new String[] {url_consulta, ccorrente, token}, path_crt, path_key);
        int statusCode = (int)consulta[0];
        Object[] infoMessage = (Object[])consulta[1];
        if ((int)consulta[0] != 200) {            
            System.out.println("Codigo do Erro:" + statusCode + " - " + infoMessage[0]);
        } else {
            JSONObject jsonOb = (JSONObject)infoMessage[0];

            String abertos = c.myfunction(jsonOb,"abertos").toString();
            JSONObject aItens = new JSONObject(abertos);
            String previstosQuantidade = c.myfunction(aItens,"quantidade").toString();
            preQuantidade.setText(previstosQuantidade);
            String previstosValor = c.myfunction(aItens,"valor").toString();
            preValor.setText(new DecimalFormat("#,##0.00").format(new BigDecimal(previstosValor)));
            
            String vencidos = c.myfunction(jsonOb,"vencidos").toString();
            JSONObject vItens = new JSONObject(vencidos);
            String baixadosQuantidade = c.myfunction(vItens,"quantidade").toString();
            baiQuantidade.setText(baixadosQuantidade);
            String baixadosValor = c.myfunction(vItens,"valor").toString();
            baiValor.setText(new DecimalFormat("#,##0.00").format(new BigDecimal(baixadosValor)));

            String expirados = c.myfunction(jsonOb,"expirados").toString();
            JSONObject eItens = new JSONObject(expirados);
            String expiradosQuantidade = c.myfunction(eItens,"quantidade").toString();
            expQuantidade.setText(expiradosQuantidade);
            String expiradosValor = c.myfunction(eItens,"valor").toString();
            expValor.setText(new DecimalFormat("#,##0.00").format(new BigDecimal(expiradosValor)));

            String pagos = c.myfunction(jsonOb,"pagos").toString();
            JSONObject pItens = new JSONObject(pagos);
            String recebidosQuantidade = c.myfunction(pItens,"quantidade").toString();
            recQuantidade.setText(recebidosQuantidade);
            String recebidosValor = c.myfunction(pItens,"valor").toString();
            recValor.setText(new DecimalFormat("#,##0.00").format(new BigDecimal(recebidosValor)));

            String cancelados = c.myfunction(jsonOb,"cancelados").toString();
            JSONObject cItens = new JSONObject(cancelados);
            String canceladosQuantidade = c.myfunction(cItens,"quantidade").toString();
            canQuantidade.setText(canceladosQuantidade);
            String canceladosValor = c.myfunction(cItens,"valor").toString();
            canValor.setText(new DecimalFormat("#,##0.00").format(new BigDecimal(canceladosValor)));

            int emitidosQuantidade = Integer.valueOf(baixadosQuantidade) +
                                     Integer.valueOf(recebidosQuantidade) +
                                     Integer.valueOf(expiradosQuantidade) +
                                     Integer.valueOf(previstosQuantidade) +
                                     Integer.valueOf(canceladosQuantidade);;
            int _paginas = (emitidosQuantidade / 20) + (emitidosQuantidade % 20 > 0 ? 1 : 0);
              int totalPages = _paginas;

            if (totalPages > 0) {
                int arc = totalPages;
                for (int i=0; i <= totalPages - 1; i++) {
                    String url_Paginas = "https://cdpj.partners.bancointer.com.br/cobranca/v2/boletos" +
                "?dataInicial=&1.&dataFinal=&2.&filtrarDataPor=&3.&itensPorPagina=20&paginaAtual=&4.&ordenarPor=DATAVENCIMENTO&tipoOrdenacao=ASC&situacao=&5.";
                    url_Paginas = FuncoesGlobais.Subst(url_Paginas, new String[] {conDataIni, conDataFim,"VENCIMENTO",String.valueOf(i),tipoListagem});
                    
                    consulta = c.selectBoleta(new String[] {url_Paginas, ccorrente, token}, path_crt, path_key);
                    statusCode = (int)consulta[0];
                    infoMessage = (Object[])consulta[1];
                    if ((int)consulta[0] != 200) {            
                        System.out.println("Codigo do Erro:" + statusCode + " - " + infoMessage[0]);
                    } else {
                        jsonOb = (JSONObject)infoMessage[0];
                        String jsonContent = c.myfunction(jsonOb,"content").toString();
                        
                        JSONArray jsonObContent = new JSONArray(jsonContent);
                        
                        int brc = jsonObContent.length();
                        for(int n=0; n < jsonObContent.length(); n++) {
                            JSONObject lista = new JSONObject(jsonObContent.getString(n));
                            Date dataEmissao = Dates.StringtoDate(c.myfunction(lista,"dataEmissao").toString(),"yyyy-MM-dd");
                            Date dataVencimento = Dates.StringtoDate(c.myfunction(lista,"dataVencimento").toString(),"yyyy-MM-dd");
                            Date dataPagamento = null;
                            if (c.myfunction(lista, "situacao").equals("PAGO")) dataPagamento = Dates.StringtoDate(c.myfunction(lista, "dataHoraSituacao").toString(), "yyyy-MM-dd");
                            String seuNumero = c.myfunction(lista,"seuNumero").toString();
                            String nossoNumero = c.myfunction(lista,"nossoNumero").toString();
                            String cnpjCpfSacado = c.myfunction(lista,"cnpjCpfBeneficiario").toString();
                            
                            String pagador = c.myfunction(lista,"pagador").toString();
                            JSONObject pagadorList = new JSONObject(pagador);
                            
                            String nomeSacado = c.myfunction(pagadorList,"nome").toString();
                            
                            BigDecimal valorMulta = new BigDecimal("0"); //new BigDecimal(c.myfunction(lista,"valorMulta").toString());
                            BigDecimal valorJuros = new BigDecimal("0"); //new BigDecimal(c.myfunction(lista,"valorJuros").toString());
                            BigDecimal valorRecebido = new BigDecimal("0");
                            try { valorRecebido = new BigDecimal(c.myfunction(lista, "valorTotalRecebimento").toString()); } catch (Exception e) {}
                            BigDecimal valorNominal = new BigDecimal(c.myfunction(lista,"valorNominal").toString());
                            String situacao = c.myfunction(lista,"situacao").toString();
                            boolean baixado = isBaixado(nossoNumero);
                            if (situacao.equalsIgnoreCase("PAGO") && !baixado) bPagasENaoBaixadas++;
                            
                            // Calculo - Adicionado em 07-06-2022
                            if (valorRecebido.floatValue() != 0) valorMulta = valorRecebido.subtract(valorNominal);
                            
                            pessoasBoleta.add(new classInterConsulta(dataEmissao, dataVencimento, dataPagamento, seuNumero, nossoNumero, cnpjCpfSacado, nomeSacado, valorMulta, valorJuros, valorNominal, situacao, baixado));
                            
                            int pgs2 = ((b++ * 100) / brc) + 1;
                            jProgressListaBoletasConsulta1.setValue(pgs2);     
                            try { Thread.sleep(20); } catch (InterruptedException ex) { }
                        }                                    
                    }
                    int pgs1 = ((a++ * 100) / arc) + 1;
                    jProgressListaBoletasConsulta.setValue(pgs1);          
                    try { Thread.sleep(20); } catch (InterruptedException ex) { }
                }
            }
        }
        
        if (pessoasBoleta.size() > 0) {
            for (classInterConsulta item : pessoasBoleta) { 
                String mData = "";
                try { mData = Dates.DateFormata("dd-MM-yyyy", item.getDataPagamento().getValue()); } catch (Exception ex) {}
                Object[] dado = {
                        Dates.DateFormata("dd-MM-yyyy", item.getDataEmissao().getValue()), 
                        Dates.DateFormata("dd-MM-yyyy", item.getDataVencimento().getValue()), 
                        mData, 
                        item.getSeuNumero().getValue().toString(), 
                        item.getNossoNumero().getValue().toString(), 
                        item.getCnpjCpfSacado().getValue().toString(), 
                        item.getNomeSacado().getValue().toString(), 
                        new DecimalFormat("#,##0.00").format(item.getValorMulta().getValue()),
                        new DecimalFormat("#,##0.00").format(item.getValorJuros().getValue()),
                        new DecimalFormat("#,##0.00").format(item.getValorNominal().getValue()),
                        item.getSituacao().getValue().toString(),
                        item.getBaixado().getValue() ? "S" : "N"
                };
                data = tabela.insert(data, dado);
            }
            
            tabela.Show(conLista, data, tam, aln, col, edt);            
        } 
        if (bPagasENaoBaixadas != 0) {
            jBtnBaixar.setEnabled(true);
            jBtnBaixar.setText("Baixar [" + FuncoesGlobais.StrZero(String.valueOf(bPagasENaoBaixadas), 3) + "]");            
        } else {
            jBtnBaixar.setEnabled(false);
            jBtnBaixar.setText("Baixar [000]");            
        }
    }

    private boolean isBaixado(String nnumero) {
        String sSql = "SELECT NNUMERO FROM RECIBO WHERE autenticacao != 0 AND Mid(NNUMERO,4) = '" + nnumero + "';";
        ResultSet imResult = conn.AbrirTabela(sSql, ResultSet.CONCUR_READ_ONLY);
        boolean isDown = false;
        try {
            while (imResult.next()) {
                isDown = true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        DbMain.FecharTabela(imResult);
        return isDown;
    }
    
    private boolean AnalisaGeracao() {
        boolean retorno = true; boolean selecao = false;
        if (bancosBoleta != null) {
            for (int b = 0; b <= bancosBoleta.size() - 1; b++) {
                for (PessoasBoleta p : bancosBoleta.get(b).getPessoasBoleta()) {
                    if (p.getTag()) {
                        selecao = true;
                        if (Dates.DateDiff(Dates.DIA, Dates.StringtoDate(p.getVencimentoRec(), "dd/MM/yyyy"), new Date()) > 0) {
                           if (p.getVencimentoBol().equalsIgnoreCase("")) {
                                JOptionPane.showMessageDialog(this, "Para vencimentos menor que a data atual,\n" + 
                                                                     "o campo <boleto> não pode ser banco.");
                                retorno = false;
                                treeTable.scrollRowToVisible(b);
                                return retorno;
                            }

                            if (Dates.DateDiff(Dates.DIA, new Date(), Dates.StringtoDate(p.getVencimentoBol(), "dd/MM/yyyy")) < 0) {
                                JOptionPane.showMessageDialog(this,"A data do campo <boleto> não pode ser menor que a data atual e " + 
                                                                        "nem estar em branco.");
                                retorno = false;
                                treeTable.scrollRowToVisible(b);
                                return retorno;
                            }
                        }
                    }
                }
            }
            if (!selecao) {
                JOptionPane.showMessageDialog(this, "Você deve selecionar ao menos 1(hum( locatário!");
                return false;
            }
        } else {
            JOptionPane.showMessageDialog(this, "Você deve primeiro Lista e depois selecionar os locatários para impressão.");
            retorno = false;
        }

        return retorno;
    }

    private String ChecaTermino(String contrato) {
        String msg = "";        
        String[][] campos = null;
        try {
            campos = conn.LerCamposTabela(new String[] {"dtinicio","dttermino","dtadito","dtseguro",
                    FuncoesGlobais.Subst("((Month(StrDate(dttermino)) = &1. AND Year(StrDate(dttermino)) = &2.)) AS pinta",
                    new String[] {String.valueOf(Dates.iMonth(new Date())), String.valueOf(Dates.iYear(new Date()))})}, "CARTEIRA", 
                    FuncoesGlobais.Subst("contrato = '" + contrato + "' AND ((Month(StrDate(dtinicio)) >= &1. AND " + 
                    "Year(StrDate(dttermino)) >= &2.) OR (Month(StrDate(dttermino)) >= &1. AND Year(StrDate(dttermino)) = &2.))",
                    new String[] {String.valueOf(Dates.iMonth(new Date())), String.valueOf(Dates.iYear(new Date()))}));
        } catch (Exception e) {}
        if (campos != null) {
            String tmesanor = campos[3][3]; if (tmesanor == null) tmesanor = "";
            String tdtini = campos[0][3];
            Date _inic = Dates.StringtoDate(tdtini,"dd/MM/yyyy");
            Date _comp = new Date();
            if (_inic.getYear() != _comp.getYear()) {   
                if (tmesanor.isEmpty()) {
                    String meses = null;
                    try {meses = conn.LerParametros("REAJNUM");} catch (Exception e) {meses = "1";}
                    if (_inic.getMonth() - 1 >= _comp.getMonth() - 1) {
                        if (((_inic.getMonth() - 1) - (_comp.getMonth() - 1)) <= Integer.valueOf(meses)) {
                            if (((_inic.getMonth() - 1) - (_comp.getMonth() - 1)) > 0) {
                                msg = "Faltam " + ((_inic.getMonth() - 1) - (_comp.getMonth() - 1)) + " Mes(es) para o reajuste!!!";
                            } else msg = "";
                        }
                    } else if (_inic.getMonth() == _comp.getMonth()) {
                        msg = "Este é o mês do reajuste!!!";
                        if (campos[4][3].equals("1")) msg += "    Termino de contrato!!!";
                    }
                } else {
                    boolean reaj = Integer.valueOf(tmesanor.substring(3,7)) >= Dates.iYear(new Date());
                    if (!reaj) {
                        if (_inic.getMonth() - 1 > _comp.getMonth() - 1) {
                            String meses = null;
                            try {meses = conn.LerParametros("REAJNUM");} catch (Exception e) {meses = "1";}
                            msg = "Faltam " + meses + " Mes(es) para o reajuste!!!";
                        } else if (_inic.getMonth() == _comp.getMonth()) {
                            msg = "Este é o mês do reajuste!!!";
                            if (campos[4][3].equals("1")) msg += "    Termino de contrato!!!";
                        }
                    }
                }
            }

        }
        return msg;
    }
    
    private Boleta CreateBoleta(String numBanco, String rgprp, String rgimv, String contrato, String vencto, String dataBol) throws SQLException {
        bancos bco = new bancos(numBanco);
        classInter pagador = new classInter();
        
        Collections gVar = VariaveisGlobais.dCliente;

        Boleta bean1 = new Boleta();
        bean1.setempNome(gVar.get("empresa").toUpperCase().trim());
        bean1.setempEndL1(gVar.get("endereco") + ", " + gVar.get("numero") + gVar.get("complemento") + " - " + gVar.get("bairro"));
        bean1.setempEndL2(gVar.get("cidade") + " - " + gVar.get("estado") + " - CEP " + gVar.get("cep"));
        bean1.setempEndL3("Tel/Fax.: " + gVar.get("telefone"));
        bean1.setempEndL4(gVar.get("hpage") + " / " + gVar.get("email"));

        // Logo da Imobiliaria
        bean1.setlogoLocation("resources/logos/boleta/" + VariaveisGlobais.icoBoleta);

        String[][] msgboleta = null;
        try {
            msgboleta = conn.LerCamposTabela(new String[] {"msgboleta","dtnasc"}, "locatarios", "contrato = '" + contrato + "'");
        } catch (SQLException e) {}
        
        String mbol = "" + conn.LerParametros("MSGBOL10");
        if (msgboleta != null) {
            if (!msgboleta[0][3].isEmpty()) mbol = msgboleta[0][3];
        }
        
        bean1.setlocaMsgL01(mbol);
        pagador.setMensagem_linha4(mbol);

        String txtAniversario = "";
        if ("TRUE".equals(conn.LerParametros("ANIVERSARIO").toUpperCase())) {
        txtAniversario = "" + conn.LerParametros("MSGANIVERSARIO");
        } else {
        if (("TRUE".equals(conn.LerParametros("FERIADOS").toUpperCase()))) { }
        }

        String msg = ChecaTermino(contrato);
        if (!msg.trim().equals("")) {
            bean1.setlocaMsgL02(msg);
            pagador.setMensagem_linha1(msg);
        } else {
            if (msgboleta != null) {
                if (msgboleta[1][3] != null) {
                    if (Dates.DateFormata("MM", Dates.StringtoDate(msgboleta[1][3].substring(0, 10), "yyyy-MM-dd")).equals(Dates.DateFormata("MM", Dates.StringtoDate(vencto,"dd-MM-yyyy")))) {
                        bean1.setlocaMsgL02(txtAniversario);
                        pagador.setMensagem_linha1(txtAniversario);
                    }
                }
            }
        }

        // 13-11-2017 - Reimpressão com nova data de boleta
        if (dataBol != null) {
            bean1.setlocaMsgL02("Calculos feitos em referencia a data original de Vencimento " + vencto);
            pagador.setMensagem_linha1("Calculos feitos em referencia a data original de Vencimento " + vencto);
        }

        // Logo do Banco
        bean1.setlogoBanco(bco.getBanco_logo());
        
        bean1.setnumeroBanco(bco.getBanco_NBANCO() + "-" + bco.getBanco_NBANCODV());

        String[][] linhas = Recalcula(rgprp, rgimv, contrato, vencto);
        float[] totais = CalcularRecibo(rgprp, rgimv, contrato, vencto);

        // exp, mul, jur, cor
        //float expediente = totais[0], multa = totais[1], juros = totais[2], correcao = totais[3];
        float expediente = 0, multa = 0, juros = 0, correcao = 0;
        
        if (VariaveisGlobais.boletoEP || VariaveisGlobais.boletoSomaEP) expediente = totais[0];
        if (VariaveisGlobais.boletoMU) { multa = totais[1]; } else { totais[4] -= totais[1]; }
        if (VariaveisGlobais.boletoJU) { juros = totais[2]; } else { totais[4] -= totais[2]; }
        if (VariaveisGlobais.boletoCO) { correcao = totais[3]; } else { totais[4] -= totais[3]; }
        float tRecibo = totais[4];
        
        // Atualizar Recibo parando MU/JU/CO/EP nesta data
        AlteraMUJUCOEP(contrato,vencto,multa,juros,correcao,expediente);
        
        DecimalFormat df = new DecimalFormat("#,##0.00");
        df.format(multa);

        if ((VariaveisGlobais.boletoEP && expediente > 0) && !VariaveisGlobais.boletoSomaEP) {
            int pos = AchaVazio(linhas);
            if (pos > -1) {
                linhas[pos][0] = gVar.get("EP");
                linhas[pos][1] = "-";
                linhas[pos][2] = df.format(expediente);
            }
        } else if (VariaveisGlobais.boletoEP && VariaveisGlobais.boletoSomaEP) {
            float alrec = LerValor.StringToFloat(linhas[0][2]);
            linhas[0][2] = LerValor.floatToCurrency(alrec + expediente, 2);
            expediente = 0;
        } else if (!VariaveisGlobais.boletoEP && !VariaveisGlobais.boletoSomaEP) {
            tRecibo -= totais[0];
            expediente = 0;
        }

        if (multa > 0) {
            int pos = AchaVazio(linhas);
            if (pos > -1) {
                linhas[pos][0] = gVar.get("MU");
                linhas[pos][1] = "-";
                linhas[pos][2] = df.format(multa);
            }
        }

        if (juros > 0) {
            int pos = AchaVazio(linhas);
            if (pos > -1) {
                linhas[pos][0] = gVar.get("JU");
                linhas[pos][1] = "-";
                linhas[pos][2] = df.format(juros);
            }
        }

        if (correcao > 0) {
            int pos = AchaVazio(linhas);
            if (pos > -1) {
                linhas[pos][0] = gVar.get("CO");
                linhas[pos][1] = "-";
                linhas[pos][2] = df.format(correcao);
            }
        }

        try {
            bean1.setlocaDescL01(linhas[0][0]);
            bean1.setlocaCpL01(linhas[0][1]);
            bean1.setlocaVrL01(linhas[0][2]);
            bean1.setlocaDescL02(linhas[1][0]); 
            bean1.setlocaCpL02(linhas[1][1]);
            bean1.setlocaVrL02(linhas[1][2]);
            bean1.setlocaDescL03(linhas[2][0]);
            bean1.setlocaCpL03(linhas[2][1]);
            bean1.setlocaVrL03(linhas[2][2]);
            bean1.setlocaDescL04(linhas[3][0]);
            bean1.setlocaCpL04(linhas[3][1]);
            bean1.setlocaVrL04(linhas[3][2]);
            bean1.setlocaDescL05(linhas[4][0]);
            bean1.setlocaCpL05(linhas[4][1]);
            bean1.setlocaVrL05(linhas[4][2]);
            bean1.setlocaDescL06(linhas[5][0]);
            bean1.setlocaCpL06(linhas[5][1]);
            bean1.setlocaVrL06(linhas[5][2]);
            bean1.setlocaDescL07(linhas[6][0]);
            bean1.setlocaCpL07(linhas[6][1]);
            bean1.setlocaVrL07(linhas[6][2]);
            bean1.setlocaDescL08(linhas[7][0]);
            bean1.setlocaCpL08(linhas[7][1]);
            bean1.setlocaVrL08(linhas[7][2]);
            bean1.setlocaDescL09(linhas[8][0]);
            bean1.setlocaCpL09(linhas[8][1]);
            bean1.setlocaVrL09(linhas[8][2]);
            bean1.setlocaDescL10(linhas[9][0]);
            bean1.setlocaCpL10(linhas[9][1]);
            bean1.setlocaVrL10(linhas[9][2]);
            bean1.setlocaDescL11(linhas[10][0]);
            bean1.setlocaCpL11(linhas[10][1]);
            bean1.setlocaVrL11(linhas[10][2]);
            bean1.setlocaDescL12(linhas[11][0]);
            bean1.setlocaCpL12(linhas[11][1]);
            bean1.setlocaVrL12(linhas[11][2]);
            bean1.setlocaDescL13(linhas[12][0]);
            bean1.setlocaCpL13(linhas[12][1]);
            bean1.setlocaVrL13(linhas[12][2]);
            bean1.setlocaDescL14(linhas[13][0]);
            bean1.setlocaCpL14(linhas[13][1]);
            bean1.setlocaVrL14(linhas[13][2]);
        } catch (Exception ex) {}

        if (dataBol != null) {
            bean1.setbolDadosVencimento(dataBol);
            pagador.setDataVencimento(dataBol);
        } else {
            bean1.setbolDadosVencimento(vencto);
            pagador.setDataVencimento(vencto);
        }
        
        String cValor = Bancos.bancos.Valor4Boleta(LerValor.floatToCurrency(tRecibo,2));  // valor da boleta
        bean1.setbolDadosVrdoc(df.format(tRecibo));
        pagador.setValorNominal(new DecimalFormat("#.##").format(tRecibo).replace(",", "."));
        pagador.setValorAbatimento("0");

//        String banco = "";
//        if (Bancos.bancos.getBanco().equalsIgnoreCase("104")) {
//            banco = "cef";
//        } else if (Bancos.bancos.getBanco().equalsIgnoreCase("341")) {
//            banco = "itau";
//        } else if (Bancos.bancos.getBanco().equalsIgnoreCase("033")) {
//            banco = "santander";
//        } else if (Bancos.bancos.getBanco().equalsIgnoreCase("001")) {
//            banco = "bb";
//        } else if (Bancos.bancos.getBanco().equalsIgnoreCase("237")) {
//            banco = "bd";
//        } else banco = "";
        
        String showCarteira = bco.getBanco_CARTEIRA(); String NossoNumero = bco.getBanco_AGENCIA() + bco.getBanco_AGENCIADV() + "/" + bco.getBanco_CARTEIRA() + "/" + "0070438957-8"; 
        String cdBarras = ""; String lnDig = "";

        bean1.setbolDadosAgcodced(bco.getBanco_AGENCIA() + bco.getBanco_AGENCIADV() + "/" + bco.getBanco_BENEFICIARIO().trim());
        
        bean1.setbolDadosNumdoc(rgprp + "/" + contrato);
        pagador.setSeuNumero(contrato);

        String[][] dadosSac = conn.LerCamposTabela(new String[] {"nomerazao", "cpfcnpj"}, "locatarios", "contrato = '" + contrato + "'");

        bean1.setsacDadosNome(dadosSac[0][3]);  // Nome do Sacado
        pagador.setNome(dadosSac[0][3]);
        bean1.setsacDadosCpfcnpj("CNPJ/CPF: " + dadosSac[1][3]);  // Cpf ou Cnpj do Sacado
        pagador.setCnpjCpf(dadosSac[1][3]);
        pagador.setTipopessoa(pagador.getCnpjCpf().length() == 11 ? pagador.inter_PAGADOR_TIPOPESSOA_FISICA : pagador.inter_PAGADOR_TIPOPESSOA_JURIDICA);

        String[][] endSac = conn.LerCamposTabela(new String[] {"end", "num", "compl", "bairro", "cidade", "estado", "cep"}, "imoveis", "rgimv = '" + rgimv + "'");
        bean1.setsacDadosEndereco(endSac[0][3]);
        pagador.setEndereco(endSac[0][3]);
        bean1.setsacDadosNumero(endSac[1][3]);
        pagador.setNumero(endSac[1][3]);
        bean1.setsacDadosCompl(endSac[2][3]);
        pagador.setComplemento(endSac[2][3]);
        bean1.setsacDadosBairro(endSac[3][3]);
        pagador.setBairro(endSac[3][3]);
        bean1.setsacDadosCidade(endSac[4][3]);
        pagador.setCidade(endSac[4][3]);
        bean1.setsacDadosEstado(endSac[5][3]);
        pagador.setUf(endSac[5][3]);
        
        pagador.setCep(endSac[6][3]);
        bean1.setsacDadosCep(pagador.getCep());        

        bean1.setbcoMsgL01("PAGAVEL EM QUALQUER BANCO OU PELA INTERNET.");
        bean1.setbcoMsgL02("");
        bean1.setbolDadosEspeciedoc("OU");
               
        bean1.setbolDadosCedente(gVar.get("cnpj") + " - " + gVar.get("empresa").toUpperCase());
        pagador.setCnpjCPFBeneficiario(gVar.get("cnpj"));                    
        bean1.setbolDadosDatadoc(Dates.DatetoString(new Date()));
        pagador.setDataEmissao(Dates.DatetoString(new Date()));
        bean1.setbolDadosAceite("N");
        bean1.setbolDadosDtproc(Dates.DatetoString(new Date()));
        bean1.setbolDadosUsobco("");
        bean1.setbolDadosCarteira(showCarteira);
        bean1.setbolDadosEspecie("R$");
        bean1.setbolDadosQtde("");
        bean1.setbolDadosValor("");
 
        String msgBol01 = conn.LerParametros("MSGBOL1"); if (msgBol01 == null) msgBol01 = "";
        String msgBol02 = conn.LerParametros("MSGBOL2"); if (msgBol02 == null) msgBol02 = "";
        String msgBol03 = conn.LerParametros("MSGBOL3"); if (msgBol03 == null) msgBol03 = "";
        String msgBol04 = conn.LerParametros("MSGBOL4"); if (msgBol04 == null) msgBol04 = "";
        String msgBol05 = conn.LerParametros("MSGBOL5"); if (msgBol05 == null) msgBol05 = "";
        String msgBol06 = conn.LerParametros("MSGBOL6"); if (msgBol06 == null) msgBol06 = "";
        String msgBol07 = conn.LerParametros("MSGBOL7"); if (msgBol07 == null) msgBol07 = "";
        String msgBol08 = conn.LerParametros("MSGBOL8"); if (msgBol08 == null) msgBol08 = "";
        String msgBol09 = conn.LerParametros("MSGBOL9"); if (msgBol09 == null) msgBol09 = "";

        bean1.setbolDadosMsg01(msgBol01);
        bean1.setbolDadosMsg02(msgBol02);
        pagador.setMensagem_linha2(msgBol04);
        bean1.setbolDadosMsg03(msgBol03);
        pagador.setMensagem_linha3(msgBol05);
        bean1.setbolDadosMsg04(msgBol04);
        bean1.setbolDadosMsg05(msgBol05);
        bean1.setbolDadosMsg06(msgBol06);
        bean1.setbolDadosMsg07(msgBol07);

        Calculos rc = new Calculos();
        try {
            rc.Inicializa(rgprp, rgimv, contrato);
        } catch (SQLException ex) {}

        Date tvecto = Dates.StringtoDate(dataBol != null ? dataBol : vencto,"dd/MM/yyyy");
        String carVecto = Dates.DateFormata("dd/MM/yyyy", 
                        Dates.DateAdd(Dates.DIA, (int)rc.dia_mul, tvecto));

        String ln08 = "";
        if ("".equals(msgBol08)) {
            ln08 = "APÓS O DIA " + carVecto + " MULTA DE 2% + ENCARGOS DE 0,333% AO DIA DE ATRASO.";
        } else {
            // [VENCIMENTO] - Mostra Vencimento
            // [CARENCIA] - Mostra Vencimento + Carencia
            // [MULTA] - Mostra Juros
            // [ENCARGOS] - Mostra Encargos
            ln08 = msgBol08.replace("[VENCIMENTO]", Dates.DateFormata("dd/MM/yyyy", tvecto));
            ln08 = ln08.replace("[CARENCIA]", carVecto);
            ln08 = ln08.replace("[MULTA]", String.valueOf(rc.TipoImovel().equalsIgnoreCase("RESIDENCIAL") ? rc.multa_res : rc.multa_com).replace(".0", "") + "%");
            ln08 = ln08.replace("[ENCARGOS]", "0,033%");
        }
        bean1.setbolDadosMsg08(ln08);
        pagador.setMensagem_linha4(msgBol08);
//        bean1.setbolDadosMsg08("".equals(msgBol08) ? "APÓS O DIA " + carVecto + 
//            " MULTA DE " + (rc.TipoImovel().equalsIgnoreCase("RESIDENCIAL") ? rc.multa_res : rc.multa_com) + "% + ENCARGOS DE 0,333% AO DIA DE ATRASO." : "" + msgBol08);
        bean1.setbolDadosMsg09("".equals(msgBol09) ? "NÃO RECEBER APÓS 30 DIAS DO VENCIMENTO." : msgBol09);
        pagador.setMensagem_linha5(msgBol09);
        
        // Descontos
        bean1.setbolDadosDesconto("");
        pagador.setDesconto1_codigoDesconto(pagador.inter_DESCONTO_NAOTEMDESCONTO);
        pagador.setDesconto1_data("");
        pagador.setDesconto1_taxa("0");
        pagador.setDesconto1_valor("0");
        
        pagador.setDesconto2_codigoDesconto(pagador.inter_DESCONTO_NAOTEMDESCONTO);
        pagador.setDesconto2_data("");
        pagador.setDesconto2_taxa("0");
        pagador.setDesconto2_valor("0");

        pagador.setDesconto3_codigoDesconto(pagador.inter_DESCONTO_NAOTEMDESCONTO);
        pagador.setDesconto3_data("");
        pagador.setDesconto3_taxa("0");
        pagador.setDesconto3_valor("0");
        
        bean1.setbolDadosMora("");
        
        // Multa
        pagador.setMulta_codigoMulta(pagador.inter_MULTA_PERCENTUAL);
        pagador.setMulta_data(Dates.DateFormata("dd/MM/yyyy", 
                        Dates.DateAdd(Dates.DIA, (int)rc.dia_mul + 1, tvecto)));
        
        pagador.setMulta_taxa(String.valueOf(rc.TipoImovel().equalsIgnoreCase("RESIDENCIAL") ? rc.multa_res : rc.multa_com));
        pagador.setMulta_valor("0");
        
        // Mora
        pagador.setMora_codigoMora(pagador.inter_MORA_TAXAMENSAL);
        pagador.setMora_data(Dates.DateFormata("dd/MM/yyyy", 
                        Dates.DateAdd(Dates.DIA, (int)rc.dia_mul + 1, tvecto)));
        pagador.setMora_taxa(String.valueOf(rc.cor_per));
        pagador.setMora_valor("0");
        
        bean1.setbolDadosVrcobrado("");

        pagador.setDataLimite(pagador.inter_NUNDIASAGENDA_TRINTA);
        //pagador.setNumDiasAgenda(pagador.inter_NUNDIASAGENDA_TRINTA);
        pagador.setNumDiasAgenda(30);

        /*
        / Registrar boleta no banco
        */                
        String url_ws = "https://cdpj.partners.bancointer.com.br/cobranca/v2/boletos";
        String path = bco.getBanco_CertPath();
        String path_crt = path + bco.getBanco_CrtFile();
        String path_key = path + bco.getBanco_KeyFile();
        final String ccorrente = bco.getBanco_CCORRENTE();

        // Pega token e verifica se expirou
        TokenExpiration();
        final String token = this.token;
        
        boolean pegueiBanco = true;
        Inter c  = new Inter();
        Object[] message = null;
        try {
            message = c.insertBoleta(new String[] {url_ws, ccorrente, token}, path_crt, path_key, pagador.getJSONBoleto());    
        } catch (Exception e) { pegueiBanco = false; }
        
        codErro = c.getCodErro();
        msgErro = c.getMsgErro();
        
        if (!pegueiBanco) return null;
        
        int statusCode = (int)message[0];
        String[] infoMessage = (String[])message[1];
        if ((int)message[0] != 200) {            
            System.out.println("Codigo do Erro:" + statusCode + " - " + (infoMessage != null ? infoMessage[0] : ""));
            //JOptionPane.showMessageDialog(null, "Codigo do Erro:" + statusCode + " - " + (infoMessage != null ? infoMessage[0] : ""));
            return null;
        } else {
            NossoNumero = infoMessage[0];
            cdBarras = infoMessage[1];
            lnDig = infoMessage[2];
        }

        // Pegar no openbanking
        //
        bean1.setbolDadosNnumero(NossoNumero);
        bean1.setcodDadosDigitavel(lnDig);
        bean1.setcodDadosBarras(cdBarras);
                
        /***************************************************
         * Gravar Nosso Numero no Arquivo de RECIBO para
         * posterior baixa. e Avisar que remessa = 'N'
         */
        try {
            String recUpdSql = "UPDATE RECIBO SET NNUMERO = '" + bco.getBanco_NBANCO() + StringManager.Right(NossoNumero,12) + 
                            "', REMESSA = 'S', dtvencbol = "  + (dataBol == null ? dataBol + ", ": "'" + Dates.StringtoString(dataBol, "dd/MM/yyyy","yyyy/MM/dd") + "', ") + 
                            "emailbol = 'N' WHERE contrato = '" + contrato + "' AND " +
                            "dtvencimento = '" + Dates.DateFormata("yyyy-MM-dd",
                            Dates.StringtoDate(vencto, "dd/MM/yyyy")) + "';";
            conn.ExecutarComando(recUpdSql);
        } catch (Exception err) {err.printStackTrace();}
                
        return bean1;
    }
    
    private void AlteraMUJUCOEP(String contrato, String vecto, float multa,float juros,float correcao,float expediente) {

        String auxCpo = VariaveisGlobais.ccampos;
        
        String vMulta = ""; String vJuros = ""; String vCorrecao = ""; String vTaxa = "";
        
        // Multa
        if (multa > 0) {
            vMulta = "MU" + FuncoesGlobais.GravaValor(LerValor.FloatToString(multa));
        } else {
            vMulta = "MU";
        }
        
        // Juros
        if (juros > 0) {
            vJuros = "JU" + FuncoesGlobais.GravaValor(LerValor.FloatToString(juros));
        } else {
            vJuros = "JU";
        }
        
        // Correção
        if (correcao > 0) {
            vCorrecao = "CO" + FuncoesGlobais.GravaValor(LerValor.FloatToString(correcao));
        } else {
            vCorrecao = "CO";
        }
        
        // Taxa
        if (expediente > 0) {
            vTaxa = "EP" + FuncoesGlobais.GravaValor(LerValor.FloatToString(expediente));
        } else {
            vTaxa = "EP";
        }
        
        // Limpar Ocorrencias
        String oldMU = BuscaXX(auxCpo, "MU");
        String oldJU = BuscaXX(auxCpo, "JU");
        String oldCO = BuscaXX(auxCpo, "CO");
        String oldEP = BuscaXX(auxCpo, "EP");
        
        auxCpo = auxCpo.replace(oldMU, "");
        auxCpo = auxCpo.replace(oldJU, "");
        auxCpo = auxCpo.replace(oldCO, "");
        auxCpo = auxCpo.replace(oldEP, "");
        
        // Grava Novas Ocorrencias
        String mujucoep = "";
        if (!"".equals(vMulta)) {
            mujucoep += vMulta + ":";
        }
        if (!"".equals(vJuros)) {
            mujucoep += vJuros + ":";
        }
        if (!"".equals(vCorrecao)) {
            mujucoep += vCorrecao + ":";
        }
        if (!"".equals(vTaxa)) {
            mujucoep += vTaxa + ":";
        }
        
        if (mujucoep.equalsIgnoreCase("MU:JU:CO:EP:") == false) {
            int pos = auxCpo.indexOf("AL:");
            auxCpo = auxCpo.substring(0, pos + 3) + mujucoep + auxCpo.substring(pos + 3);
        
            String SQLtxt = "UPDATE RECIBO SET CAMPO = '" + auxCpo + "' WHERE CONTRATO = '" + contrato +
                        "' AND DTVENCIMENTO = '" + Dates.DateFormata("yyyy/MM/dd", Dates.StringtoDate(vecto,"dd/MM/yyyy")) + "';";

            conn.ExecutarComando(SQLtxt);
        }
    }

    private String BuscaXX(String zcampo, String oque) {
        String zRet = "";
        int i = zcampo.indexOf(oque);
        if (i >= 0) {
            // Achou
            String auxCpo = zcampo.substring(i + 2);
            if (auxCpo.length() > 0) {
                if (":".equals(auxCpo.substring(0,1))) {
                    zRet = oque + ":";
                } else {
                    zRet = oque + auxCpo.substring(0,10) + ":";
                }
            } else zRet = oque + ":";
        }
        
        return zRet;
    }
    
    private int AchaVazio(String[][] value) {
        int r = -1;

        for (int i=0;i<value.length;i++) {
            if (value[i][0] == null || "".equals(value[i][0])) {r = i; break;}
        }

        return r;
    }

    public String[][] Recalcula(String rgprp, String rgimv, String contrato, String vencimento) {
        String[][] linhas = null;
        try {
            linhas = MontaTela(rgprp, rgimv, contrato, vencimento);
        } catch (Exception ex) {} 

        return linhas;
    }

    private String IPTU(String vecto, String campo, String rgimv) {
        String pIptu = ""; Integer pant = 0;
        try { pIptu = conn.LerParametros("IPTU"); } catch (Exception e) {}
        try { pant = Integer.valueOf(conn.LerParametros("IPTUANT"));  } catch (Exception e) {}
        if (pIptu == null || pIptu.equalsIgnoreCase("")) return campo;
        
        Boolean eiptu = campo.contains(pIptu + ":");
        if (eiptu) return campo;
        
        String[] meses = {"","jan","fev","mar","abr","mai","jun","jul","ago","set","out","nov","dez"};
        String ddia = Dates.StringtoString(vecto, "dd-MM-yyyy", "dd-MM-yyyy").substring(0, 2);
        String dmes = Dates.StringtoString(vecto, "dd-MM-yyyy", "dd-MM-yyyy").substring(3, 5);
        String dano  = Dates.StringtoString(vecto, "dd-MM-yyyy", "dd-MM-yyyy").substring(6, 10);
        String umes = Dates.DateFormata("dd-MM-yyyy", new Date()).substring(3, 5);
        String uano = Dates.DateFormata("dd-MM-yyyy", new Date()).substring(6, 10);
        
        String wmes = ""; String wValor = "0000000000";
        
        String wSql = "SELECT p.* FROM iptu p, imoveis i WHERE InStr(i.matriculas,p.matricula) > 0 AND p.ano = '" + dano + "' AND i.rgimv = '" + rgimv + "';";
        ResultSet ws = conn.AbrirTabela(wSql, ResultSet.CONCUR_READ_ONLY);
        try {
            while (ws.next()) {
                wmes = ws.getString(meses[Integer.valueOf(dmes)]);
                String[] avar = wmes.split(";");
                if (avar.length > 0) {
                    for (int i=0;i<avar.length;i++) {
                        String[] rvar = avar[i].split(",");
                        if (!rvar[0].trim().equalsIgnoreCase("")) {
                            if (Dates.DateDiff(Dates.DIA, new Date(), Dates.DateAdd(Dates.DIA, (pant * -1), Dates.StringtoDate(rvar[0], "dd-MM-yyyy"))) > 0) {
                                wValor = rvar[1];
                                break;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {}
        return campo + ";" + (!wValor.equalsIgnoreCase("0000000000") ? pIptu + ":2:" + wValor + ":0000:NT:RZ:ET:IP" : "");
    }
    
    public String[][] MontaTela(String rgprp, String rgimv, String contrato, String vecto) throws SQLException, ParseException {
        String sql = "SELECT * FROM RECIBO WHERE contrato = '" + contrato + "' AND dtvencimento = '" + Dates.DateFormata("yyyy-MM-dd", Dates.StringtoDate(vecto, "dd/MM/yyyy")) + "';";
        ResultSet pResult = conn.AbrirTabela(sql, ResultSet.CONCUR_UPDATABLE);

        String[][] linhas = null; 
        if (pResult.first()) {
            String tcampo = IPTU(vecto, pResult.getString("campo"), rgimv);
            DepuraCampos a = new DepuraCampos(tcampo);
            VariaveisGlobais.ccampos = tcampo;

//            DepuraCampos a = new DepuraCampos(pResult.getString("campo"));
//            VariaveisGlobais.ccampos = pResult.getString("campo");
            linhas = new String[14][3];

            a.SplitCampos();
            // Ordena Matriz
            Arrays.sort(a.aCampos, new Comparator() {
                private int pos1 = 3;
                private int pos2 = 4;
                @Override
                public int compare(Object o1, Object o2) {
                    String p1 = ((String)o1).substring(pos1, pos2);
                    String p2 = ((String)o2).substring(pos1, pos2);
                    return p1.compareTo(p2);
                }
            });

            // Monta campos
            for (int i=0; i<= a.length() - 1; i++) {
                String[] Campo = a.Depurar(i);
                if (Campo.length > 0) {
                    //MontaCampos(Campo, i);
                    linhas[i][0] = Campo[0];
                    linhas[i][1] = ("00/00".equals(Campo[3]) || "00/0000".equals(Campo[3]) || "".equals(Campo[3]) || "99/00".equals(Campo[3]) || "99/0000".equals(Campo[3]) ? "-" : Campo[3]) ;
                    linhas[i][2] = Campo[1];
                }
            }
        }

        DbMain.FecharTabela(pResult);

        return linhas;
    }

    private float[] CalcularRecibo(String rgprp, String rgimv, String contrato, String vecto) {
        if ("".equals(vecto.trim())) { return null; }

        Calculos rc = new Calculos();
        try {
            rc.Inicializa(rgprp, rgimv, contrato);
        } catch (SQLException ex) {}

        String campo = ""; String rcampo = ""; boolean mCartVazio = false;
        String sql = "SELECT * FROM RECIBO WHERE contrato = '" + contrato + "' AND dtvencimento = '" + Dates.DateFormata("yyyy-MM-dd", Dates.StringtoDate(vecto, "dd/MM/yyyy")) + "';";
        ResultSet pResult = conn.AbrirTabela(sql, ResultSet.CONCUR_UPDATABLE);
        try {
            if (pResult.first()) {
                campo = pResult.getString("campo");

                // Aqui recebe o IPTU
                campo = IPTU(vecto, campo, rgimv);
                
                rcampo = campo;
                mCartVazio = ("".equals(rcampo.trim()));
            }
        } catch (SQLException ex) {
            rcampo = "";
        }
        DbMain.FecharTabela(pResult);

        float exp = 0, multa = 0, juros = 0, correcao = 0;

        try {
            exp = rc.TaxaExp(campo);
        } catch (SQLException ex) {}

        try {
            multa = rc.Multa(campo, vecto, false);
        } catch (SQLException ex) {}

        try {
            juros = rc.Juros(campo, vecto);
        } catch (SQLException ex) {}

        try {
            correcao = rc.Correcao(campo, vecto);
        } catch (SQLException ex) {}

        float tRecibo = 0;
        tRecibo = Calculos.RetValorCampos(campo);
        tRecibo += exp + multa + juros + correcao;

        float[] retorno = new float[5];
        retorno[0] = exp; retorno[1] = multa; retorno[2] = juros; retorno[3] = correcao; retorno[4] = tRecibo;
        return retorno;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSpinner AnoRef;
    private javax.swing.JButton FontBackColor;
    private javax.swing.JToggleButton FontBold;
    private javax.swing.JButton FontColor;
    private javax.swing.JToggleButton FontItalic;
    private javax.swing.JComboBox FontName;
    private javax.swing.JComboBox FontSize;
    private javax.swing.JToggleButton FontUnderLine;
    private javax.swing.JScrollPane ListaBancosPessoas;
    private javax.swing.JScrollPane ListaBancosPessoasErros;
    private javax.swing.JScrollPane ListaBancosPessoasImpressas;
    private javax.swing.JSpinner MesRef;
    private javax.swing.JToggleButton TextAlignCenter;
    private javax.swing.JToggleButton TextAlignJustified;
    private javax.swing.JToggleButton TextAlignLeft;
    private javax.swing.JToggleButton TextAlignRigth;
    private com.toedter.calendar.JDateChooser VencBoleto;
    private javax.swing.JTextField baiQuantidade;
    private javax.swing.JTextField baiValor;
    private javax.swing.JButton btImprimir;
    private javax.swing.JButton btLimpar;
    private javax.swing.JButton btnEditarCadastro;
    private javax.swing.JButton btnEnviarSelecao;
    private javax.swing.JButton btnEnviarTodos;
    private javax.swing.JButton btnListarBoletasEmail;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JTextField canQuantidade;
    private javax.swing.JTextField canValor;
    private javax.swing.JButton conBtnListar;
    private com.toedter.calendar.JDateChooser conDataFinal;
    private com.toedter.calendar.JDateChooser conDataInicial;
    private javax.swing.JTable conLista;
    private javax.swing.JTextField edtSubJect;
    private javax.swing.JTextField expQuantidade;
    private javax.swing.JTextField expValor;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler4;
    private javax.swing.Box.Filler filler5;
    private javax.swing.Box.Filler filler6;
    private javax.swing.Box.Filler filler7;
    private javax.swing.JTabbedPane jAletas;
    private javax.swing.JTextField jAvMsg01;
    private javax.swing.JTextField jAvMsg02;
    private javax.swing.JTextField jAvMsg03;
    private javax.swing.JTextField jAvMsg04;
    private javax.swing.JTextField jAvMsg05;
    private javax.swing.JTextField jAvMsg06;
    private javax.swing.JTextField jAvMsg07;
    private javax.swing.JTextField jAvMsg08;
    private javax.swing.JTextField jAvMsg09;
    private javax.swing.JPanel jBoletos;
    private javax.swing.JButton jBtnBaixar;
    private javax.swing.JPanel jConsulta;
    private javax.swing.JFormattedTextField jContrato;
    private javax.swing.JPanel jEnvio;
    private com.toedter.calendar.JDateChooser jFinal;
    private javax.swing.JButton jGerar;
    private com.toedter.calendar.JDateChooser jInicial;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JButton jListar;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel21;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JPanel jPeriodo;
    private javax.swing.JProgressBar jProgress;
    private javax.swing.JProgressBar jProgress1;
    private javax.swing.JProgressBar jProgress2;
    private javax.swing.JProgressBar jProgressEmail;
    private javax.swing.JProgressBar jProgressListaBoletasConsulta;
    private javax.swing.JProgressBar jProgressListaBoletasConsulta1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JCheckBox jSemEnvio;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JComboBox<String> jTipoListagem;
    private javax.swing.JRadioButton jatrasados;
    private javax.swing.JComboBox<String> jbanco;
    private javax.swing.JComboBox<String> jcbConsultaBancos;
    private javax.swing.JComboBox jcodigo;
    private javax.swing.JRadioButton jemdia;
    private javax.swing.JButton jmsgGravar;
    private javax.swing.JComboBox jnome;
    private javax.swing.JRadioButton jperiodo;
    private javax.swing.JRadioButton jtodos;
    private javax.swing.JFormattedTextField jvalor;
    private com.toedter.calendar.JDateChooser jvencto;
    private javax.swing.JLabel lbl_Status;
    private javax.swing.JRadioButton opcavulsa;
    private javax.swing.JRadioButton opcloca;
    private javax.swing.JRadioButton opcprop;
    private javax.swing.JScrollPane pScroll;
    private javax.swing.JTextField pbairro;
    private javax.swing.JFormattedTextField pcep;
    private javax.swing.JTextField pcidade;
    private javax.swing.JTextField pcomplto;
    private javax.swing.JTextField pdocumento;
    private javax.swing.JTextField pendereco;
    private javax.swing.JFormattedTextField pestado;
    private javax.swing.JEditorPane phistorico01;
    private javax.swing.JTextField pnome;
    private javax.swing.JTextField pnumero;
    private javax.swing.JTextField preQuantidade;
    private javax.swing.JTextField preValor;
    private javax.swing.JTextField recQuantidade;
    private javax.swing.JTextField recValor;
    private javax.swing.JTable tblEmails;
    // End of variables declaration//GEN-END:variables

    // Editor
    private void EditorEmail() {
        Action boldAction = new BoldAction();
        boldAction.putValue(Action.NAME, "");
        Action italicAction = new ItalicAction();
        italicAction.putValue(Action.NAME, "");
        Action underlineAction = new UnderlineAction();
        underlineAction.putValue(Action.NAME, "");

        Action alignleftAction = new AlignLeftAction();
        Action alignrightAction = new AlignRightAction();
        Action aligncenterAction = new AlignCenterAction();
        Action alignjustifiedAction = new AlignJustifiedAction();

        Action fontsizeAction = new FontSizeAction();
        Action foregroundAction = new ForegroundAction();
        Action backgroundAction = new BackgroundAction();
        Action formatTextAction = new FontAndSizeAction();

        FontBold.setAction(boldAction);
        FontBold.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icones/webmaster_2532_text_bold.png")));
        FontItalic.setAction(italicAction);
        FontItalic.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icones/webmaster_2533_text_italic.png")));
        FontUnderLine.setAction(underlineAction);
        FontUnderLine.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icones/webmaster_2536_text_underline.png")));

        TextAlignLeft.setAction(alignleftAction);
        TextAlignLeft.setText(null);
        TextAlignLeft.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icones/webmaster_2531_text_align_left.png")));
        TextAlignLeft.setToolTipText("Alinhamento a Esquerda");

        TextAlignRigth.setAction(alignrightAction);
        TextAlignRigth.setText(null);
        TextAlignRigth.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icones/webmaster_2531_text_align_right.png")));
        TextAlignRigth.setToolTipText("Alinhamento a Direita");

        TextAlignCenter.setAction(aligncenterAction);
        TextAlignCenter.setText(null);
        TextAlignCenter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icones/webmaster_2531_text_align_center.png")));
        TextAlignCenter.setToolTipText("Alinhamento ao Centro");

        TextAlignJustified.setAction(alignjustifiedAction);
        TextAlignJustified.setText(null);
        TextAlignJustified.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icones/webmaster_2531_text_align_justify.png")));
        TextAlignJustified.setToolTipText("Alinhamento Justificado");

        ListarFontes();
        FontName.setAction(fontsizeAction);
        FontName.setToolTipText("Estilo da Fonte");
        FontSize.setAction(fontsizeAction);
        FontSize.setToolTipText("Tamanho da Fonte");

        FontColor.setAction(foregroundAction);
        FontColor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icones/editor_0304_text_foregroundcolor.png")));
        FontColor.setToolTipText("Cor da Fonte");

        FontBackColor.setAction(backgroundAction);
        FontBackColor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icones/editor_0304_text_backgroundcolor.png")));
        FontBackColor.setToolTipText("Cor do Fundo");

        _htmlPane.setEditable(true);
        pScroll.setViewportView(_htmlPane);

        HTMLEditorKit htmlEditor = new HTMLEditorKit();
        _htmlPane.setEditorKit(htmlEditor);
        String aSubject = ""; String aBody = "";
        //try { aBody = ManipuladorArquivo.leitor("reports/EmailBody.txt"); } catch (IOException ex) {}
        //try { aSubject = ManipuladorArquivo.leitor("reports/EmailSubJect.txt"); } catch (IOException ex) {}
        edtSubJect.setText(aSubject);
        String htmlString = aBody; 
        Document doc=htmlEditor.createDefaultDocument();
        _htmlPane.setDocument(doc);
        _htmlPane.setText(htmlString);
    }

    
    class FontSizeAction extends StyledEditorKit.StyledTextAction {
        private static final long serialVersionUID = 584531387732416339L;
        private String family;
        private float fontSize;

        public FontSizeAction() {
            super("Font and Size");
        }

        public String toString() {
            return "Font and Size";
        }

        public void actionPerformed(ActionEvent e) {
            JEditorPane editor = (JEditorPane) getEditor(e);
            family = (String) FontName.getSelectedItem();
            fontSize = Float.parseFloat(FontSize.getSelectedItem().toString());
            MutableAttributeSet attr = null;
            if (editor != null) {
                attr = new SimpleAttributeSet();
                StyleConstants.setFontFamily(attr, family);
                StyleConstants.setFontSize(attr, (int) fontSize);
                setCharacterAttributes(editor, attr, false);
                editor.requestFocus();
            }
        }
    }

    class FontCellRenderer extends DefaultListCellRenderer {
        public Component getListCellRendererComponent(
            JList list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus) {
            JLabel label = (JLabel)super.getListCellRendererComponent(
                list,value,index,isSelected,cellHasFocus);
            Font font = new Font((String)value, Font.PLAIN, 20);
            label.setFont(font);
            return label;
        }
    }

    private void ListarFontes() {
        GraphicsEnvironment ge = GraphicsEnvironment.
            getLocalGraphicsEnvironment();
        String[] fonts = ge.getAvailableFontFamilyNames();
        for (String f : fonts) {
            FontName.addItem(f);
        }
        FontName.setRenderer(new FontCellRenderer());
        //FontName.setSelectedItem(family);
    }

    class AlignJustifiedAction extends StyledEditorKit.StyledTextAction {
        private static final long serialVersionUID = 1749670038684056758L;

        public AlignJustifiedAction() {
            super("align-justified");
        }

        public String toString() {
            return "AlignJustified";
        }

        public void actionPerformed(ActionEvent e) {
            JEditorPane editor = getEditor(e);
            if (editor != null) {
                StyledEditorKit kit = getStyledEditorKit(editor);
                MutableAttributeSet attr = kit.getInputAttributes();
                SimpleAttributeSet sas = new SimpleAttributeSet();
                StyleConstants.setAlignment(sas, StyleConstants.ALIGN_JUSTIFIED);
                setParagraphAttributes(editor, sas, false);
                editor.requestFocus();
            }
        }
    }

    class AlignCenterAction extends StyledEditorKit.StyledTextAction {
        private static final long serialVersionUID = 1749670038684056758L;

        public AlignCenterAction() {
            super("align-center");
        }

        public String toString() {
            return "AlignCenter";
        }

        public void actionPerformed(ActionEvent e) {
            JEditorPane editor = getEditor(e);
            if (editor != null) {
                StyledEditorKit kit = getStyledEditorKit(editor);
                MutableAttributeSet attr = kit.getInputAttributes();
                SimpleAttributeSet sas = new SimpleAttributeSet();
                StyleConstants.setAlignment(sas, StyleConstants.ALIGN_CENTER);
                setParagraphAttributes(editor, sas, false);
                editor.requestFocus();
            }
        }
    }

    class AlignRightAction extends StyledEditorKit.StyledTextAction {
        private static final long serialVersionUID = 1749670038684056758L;

        public AlignRightAction() {
            super("align-right");
        }

        public String toString() {
            return "AlignRight";
        }

        public void actionPerformed(ActionEvent e) {
            JEditorPane editor = getEditor(e);
            if (editor != null) {
                StyledEditorKit kit = getStyledEditorKit(editor);
                MutableAttributeSet attr = kit.getInputAttributes();
                SimpleAttributeSet sas = new SimpleAttributeSet();
                StyleConstants.setAlignment(sas, StyleConstants.ALIGN_RIGHT);
                setParagraphAttributes(editor, sas, false);
                editor.requestFocus();
            }
        }
    }

    class AlignLeftAction extends StyledEditorKit.StyledTextAction {
        private static final long serialVersionUID = 1749670038684056758L;

        public AlignLeftAction() {
            super("align-left");
        }

        public String toString() {
            return "AlignLeft";
        }

        public void actionPerformed(ActionEvent e) {
            JEditorPane editor = getEditor(e);
            if (editor != null) {
                StyledEditorKit kit = getStyledEditorKit(editor);
                MutableAttributeSet attr = kit.getInputAttributes();
                SimpleAttributeSet sas = new SimpleAttributeSet();
                StyleConstants.setAlignment(sas, StyleConstants.ALIGN_LEFT);
                setParagraphAttributes(editor, sas, false);
                editor.requestFocus();
            }
        }
    }

    class UnderlineAction extends StyledEditorKit.StyledTextAction {
        private static final long serialVersionUID = 1794670038684056758L;

        public UnderlineAction() {
            super("font-underline");
        }

        public String toString() {
            return "Underline";
        }

        public void actionPerformed(ActionEvent e) {
            JEditorPane editor = getEditor(e);
            if (editor != null) {
                StyledEditorKit kit = getStyledEditorKit(editor);
                MutableAttributeSet attr = kit.getInputAttributes();
                boolean underline = (StyleConstants.isUnderline(attr)) ? false : true;
                SimpleAttributeSet sas = new SimpleAttributeSet();
                StyleConstants.setUnderline(sas, underline);
                setCharacterAttributes(editor, sas, false);
                editor.requestFocus();
            }
        }
    }

    class BoldAction extends StyledEditorKit.StyledTextAction {
        private static final long serialVersionUID = 9174670038684056758L;

        public BoldAction() {
            super("font-bold");
        }

        public String toString() {
            return "Bold";
        }

        public void actionPerformed(ActionEvent e) {
            JEditorPane editor = getEditor(e);
            if (editor != null) {
                StyledEditorKit kit = getStyledEditorKit(editor);
                MutableAttributeSet attr = kit.getInputAttributes();
                boolean bold = (StyleConstants.isBold(attr)) ? false : true;
                SimpleAttributeSet sas = new SimpleAttributeSet();
                StyleConstants.setBold(sas, bold);
                setCharacterAttributes(editor, sas, false);
                editor.requestFocus();
            }
        }
    }

    class ItalicAction extends StyledEditorKit.StyledTextAction {
        private static final long serialVersionUID = -1428340091100055456L;

        public ItalicAction() {
            super("font-italic");
        }

        public String toString() {
            return "Italic";
        }

        public void actionPerformed(ActionEvent e) {
            JEditorPane editor = getEditor(e);
            if (editor != null) {
                StyledEditorKit kit = getStyledEditorKit(editor);
                MutableAttributeSet attr = kit.getInputAttributes();
                boolean italic = (StyleConstants.isItalic(attr)) ? false : true;
                SimpleAttributeSet sas = new SimpleAttributeSet();
                StyleConstants.setItalic(sas, italic);
                setCharacterAttributes(editor, sas, false);
                editor.requestFocus();
            }
        }
    }

    class ForegroundAction extends StyledEditorKit.StyledTextAction {
        private static final long serialVersionUID = 6384632651737400352L;
        JColorChooser colorChooser = new JColorChooser();
        JDialog dialog = new JDialog();
        boolean noChange = false;
        boolean cancelled = false;

        public ForegroundAction() {
            super("");
        }

        public void actionPerformed(ActionEvent e) {
            JEditorPane editor = (JEditorPane) getEditor(e);
            if (editor == null) {
                JOptionPane.showMessageDialog(null,
                    "You need to select the editor pane before you can change the color.", "Error",
                JOptionPane.ERROR_MESSAGE);
                return;
            }
            int p0 = editor.getSelectionStart();
            StyledDocument doc = getStyledDocument(editor);
            Element paragraph = doc.getCharacterElement(p0);
            AttributeSet as = paragraph.getAttributes();
            fg = StyleConstants.getForeground(as);
            if (fg == null) {
                fg = Color.BLACK;
            }
            colorChooser.setColor(fg);
            JButton accept = new JButton("OK");
            accept.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    fg = colorChooser.getColor();
                    dialog.dispose();
                }
            });
            JButton cancel = new JButton("Cancel");
            cancel.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                   cancelled = true;
                    dialog.dispose();
                }
            });
            JButton none = new JButton("None");
            none.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    noChange = true;
                    dialog.dispose();
                }
            });
            JPanel buttons = new JPanel();
            buttons.add(accept);
            buttons.add(none);
            buttons.add(cancel);
            dialog.getContentPane().setLayout(new BorderLayout());
            dialog.getContentPane().add(colorChooser, BorderLayout.CENTER);
            dialog.getContentPane().add(buttons, BorderLayout.SOUTH);
            dialog.setModal(true);
            dialog.pack();
            dialog.setVisible(true);
            if (!cancelled) {
                MutableAttributeSet attr = null;
                if (editor != null) {
                    if (fg != null && !noChange) {
                        attr = new SimpleAttributeSet();
                        StyleConstants.setForeground(attr, fg);
                        setCharacterAttributes(editor, attr, false);
                    }
                }
            }// end if color != null
            noChange = false;
            cancelled = false;
        }
        private Color fg;
    }

    class FontAndSizeAction extends StyledEditorKit.StyledTextAction {
        private static final long serialVersionUID = 584531387732416339L;
        private String family;
        private float fontSize;
        JDialog formatText;
        private boolean accept = false;
        JComboBox fontFamilyChooser;
        JComboBox fontSizeChooser;

        public FontAndSizeAction() {
            super("Font and Size");
        }

        public String toString() {
            return "Font and Size";
        }

        public void actionPerformed(ActionEvent e) {
            JEditorPane editor = (JEditorPane) getEditor(e);
            int p0 = editor.getSelectionStart();
            StyledDocument doc = getStyledDocument(editor);
            Element paragraph = doc.getCharacterElement(p0);
            AttributeSet as = paragraph.getAttributes();
            family = StyleConstants.getFontFamily(as);
            fontSize = StyleConstants.getFontSize(as);
            formatText = new JDialog(new JFrame(), "Font and Size", true);
            formatText.getContentPane().setLayout(new BorderLayout());
            JPanel choosers = new JPanel();
            choosers.setLayout(new GridLayout(2, 1));
            JPanel fontFamilyPanel = new JPanel();
            fontFamilyPanel.add(new JLabel("Font"));
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            String[] fontNames = ge.getAvailableFontFamilyNames();
            fontFamilyChooser = new JComboBox();
            for (int i = 0; i < fontNames.length; i++) {
                fontFamilyChooser.addItem(fontNames[i]);
            }
            fontFamilyChooser.setSelectedItem(family);
            fontFamilyPanel.add(fontFamilyChooser);
            choosers.add(fontFamilyPanel);
            JPanel fontSizePanel = new JPanel();
            fontSizePanel.add(new JLabel("Size"));
            fontSizeChooser = new JComboBox();
            fontSizeChooser.setEditable(true);
            fontSizeChooser.addItem(new Float(4));
            fontSizeChooser.addItem(new Float(8));
            fontSizeChooser.addItem(new Float(12));
            fontSizeChooser.addItem(new Float(16));
            fontSizeChooser.addItem(new Float(20));
            fontSizeChooser.addItem(new Float(24));
            fontSizeChooser.setSelectedItem(new Float(fontSize));
            fontSizePanel.add(fontSizeChooser);
            choosers.add(fontSizePanel);
            JButton ok = new JButton("OK");
            ok.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    accept = true;
                    formatText.dispose();
                    family = (String) fontFamilyChooser.getSelectedItem();
                    fontSize = Float.parseFloat(fontSizeChooser.getSelectedItem().toString());
                }
            });
            JButton cancel = new JButton("Cancel");
            cancel.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    formatText.dispose();
                }
            });
            JPanel buttons = new JPanel();
            buttons.add(ok);
            buttons.add(cancel);
            formatText.getContentPane().add(choosers, BorderLayout.CENTER);
            formatText.getContentPane().add(buttons, BorderLayout.SOUTH);
            formatText.pack();
            formatText.setVisible(true);
            MutableAttributeSet attr = null;
            if (editor != null && accept) {
                attr = new SimpleAttributeSet();
                StyleConstants.setFontFamily(attr, family);
                StyleConstants.setFontSize(attr, (int) fontSize);
                setCharacterAttributes(editor, attr, false);
            }
        }
    }

    class UndoListener implements UndoableEditListener {
        public void undoableEditHappened(UndoableEditEvent e) {
            undoManager.addEdit(e.getEdit());
            undoAction.update();
            redoAction.update();
        }   
    }

    class UndoAction extends AbstractAction {
        public UndoAction() {
            this.putValue(Action.NAME, undoManager.getUndoPresentationName());
            this.setEnabled(false);
        }

        public void actionPerformed(ActionEvent e) {
            if (this.isEnabled()) {
                undoManager.undo();
                undoAction.update();
                redoAction.update();
            }
        }

        public void update() {
            this.putValue(Action.NAME, undoManager.getUndoPresentationName());
            this.setEnabled(undoManager.canUndo());
        }
    }

    class RedoAction extends AbstractAction {
        public RedoAction() {
            this.putValue(Action.NAME, undoManager.getRedoPresentationName());
            this.setEnabled(false);
        }

        public void actionPerformed(ActionEvent e) {
            if (this.isEnabled()) {
                undoManager.redo();
                undoAction.update();
                redoAction.update();
            }
        }

        public void update() {
            this.putValue(Action.NAME, undoManager.getRedoPresentationName());
            this.setEnabled(undoManager.canRedo());
        }
    }

    class BackgroundAction extends StyledEditorKit.StyledTextAction {
        private static final long serialVersionUID = 3684632651737400352L;
        JColorChooser colorChooser = new JColorChooser();
        JDialog dialog = new JDialog();
        boolean noChange = false;
        boolean cancelled = false;

        public BackgroundAction() {
            super("");
        }

        public void actionPerformed(ActionEvent e) {
            JEditorPane editor = (JEditorPane) getEditor(e);
            if (editor == null) {
                JOptionPane.showMessageDialog(null,
                    "You need to select the editor pane before you can change the color.", "Error",
                JOptionPane.ERROR_MESSAGE);
                return;
            }
            int p0 = editor.getSelectionStart();
            StyledDocument doc = getStyledDocument(editor);
            Element paragraph = doc.getCharacterElement(p0);
            AttributeSet as = paragraph.getAttributes();
            fg = StyleConstants.getBackground(as);
            if (fg == null) {
                fg = Color.BLACK;
            }
            colorChooser.setColor(fg);
            JButton accept = new JButton("OK");
            accept.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    fg = colorChooser.getColor();
                    dialog.dispose();
                }
            });
            JButton cancel = new JButton("Cancel");
            cancel.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                   cancelled = true;
                    dialog.dispose();
                }
            });
            JButton none = new JButton("None");
            none.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    noChange = true;
                    dialog.dispose();
                }
            });
            JPanel buttons = new JPanel();
            buttons.add(accept);
            buttons.add(none);
            buttons.add(cancel);
            dialog.getContentPane().setLayout(new BorderLayout());
            dialog.getContentPane().add(colorChooser, BorderLayout.CENTER);
            dialog.getContentPane().add(buttons, BorderLayout.SOUTH);
            dialog.setModal(true);
            dialog.pack();
            dialog.setVisible(true);
            if (!cancelled) {
                MutableAttributeSet attr = null;
                if (editor != null) {
                    if (fg != null && !noChange) {
                        attr = new SimpleAttributeSet();
                        StyleConstants.setBackground(attr, fg);
                        setCharacterAttributes(editor, attr, false);
                    }
                }
            }// end if color != null
            noChange = false;
            cancelled = false;
        }
        private Color fg;
    }
    
    private boolean Distribuicao(int selRow) throws SQLException {
        String nnumero = conLista.getValueAt(selRow, 4).toString();
        // Pega MU; CO; recebidos no boleto
        String BMU = FuncoesGlobais.GravaValor(conLista.getValueAt(selRow, 7).toString());
        String BCO = FuncoesGlobais.GravaValor(conLista.getValueAt(selRow, 8).toString());
        
        // Pega dados do recibo
        Object[][] aDados = null;
        try {
            aDados = db.ReadFieldsTable(new String[] {"rgprp", "rgimv", "contrato", "dtvencimento", "campo"}, "recibo", "TRIM(nnumero) LIKE '%" + nnumero + "%'");
        } catch (SQLException sqlEx) {}
        if (aDados == null) {
            JOptionPane.showMessageDialog(this, "Erro ao localizar ou NossoNumero apagado!");
            return false;
        }
        String rgprp = aDados[0][3].toString();
        String rgimv = aDados[1][3].toString();
        String contrato = aDados[2][3].toString();
        String vencto = Dates.StringtoString(aDados[3][3].toString(),"yyyy-MM-dd","dd-MM-yyyy");
        String campo= aDados[4][3].toString();

        // Autenticacao
        float nAut = (float)Autenticacao.getAut();
        if (!Autenticacao.setAut((double)nAut, 1)) {
            JOptionPane.showMessageDialog(null, "Erro ao gravar autenticacão!!!\nChane o suporte técnico...", "Atenção", JOptionPane.INFORMATION_MESSAGE);
            return false;
        }
        float rc = LerValor.StringToFloat("0,00"); // acertar

        boolean sucesso = true;
        try {
            conn.LancarCaixa(new String[] {rgprp, rgimv, contrato}, new String[][] {{"","","","","0.00","CT","CRE","RC","BOLETA"}}, String.valueOf((int)nAut).replace(".0", ""));
        } catch (Exception e) { sucesso = false; }
        
        // Calcula o recibo
        new Calculos().Inicializa(rgprp, rgimv, contrato);
        
        DepuraCampos zCampos; String[] zCampo;

        String[][] aCC = DivideCC.Divisao(rgimv);
        String[][] jCampo;
        String[] gmpCampo, difCampo;
        String[][] tmpCampo;
        String[][] divaCampo = {};
        String[] admValores = {};
        String[][] gCampo = {};

        String[] admPer = null;
        admPer = new Calculos().percADM(rgprp, rgimv);

        String[][] dvCampo;

        float fDC = 0, fDF = 0;

        float[] aComissao = null;
        try {aComissao = new Calculos().percComissao2(rgprp, rgimv);} catch (Exception e) {}
        float fComissao = aComissao[0]; 
        String sComissao = FuncoesGlobais.GravaValores(String.valueOf(fComissao).replace(".", ","), 3);

        String[] sVenctos = {vencto};

        int i = 0;
        String tCampo = CriticaCampo(rgprp, rgimv, contrato, sVenctos[i], campo);

        // Checa IPTU Automático
        //tCampo = IPTU(rgimv, sVenctos[i], tCampo);

        // Aqui coloca a MU e CO do boleto e Apaga o JU
        int intMPos = tCampo.indexOf("MU");
        if (intMPos != 0) {
            String part1 = tCampo.substring(0, intMPos + 2);
            String part2 = tCampo.substring(intMPos + 12);
            tCampo = part1 + BMU + part2;
        }
        int intCPos = tCampo.indexOf("CO");
        if (intCPos != 0) {
            String part1 = tCampo.substring(0, intCPos + 2);
            String part2 = tCampo.substring(intCPos + 12);
            tCampo = part1 + BCO + part2;
        }
        int intJPos = tCampo.indexOf("JU");
        if (intJPos != 0) {
            String part1 = tCampo.substring(0, intJPos + 2);
            String part2 = tCampo.substring(intJPos + 12);
            tCampo = part1 + "0000000000" + part2;
        }
        
        String[] aCampo = tCampo.split(";");

        // Captura descontas/diferencas
        jCampo = FuncoesGlobais.treeArray(FuncoesGlobais.join(aCampo, ";"), false);
        gmpCampo = null; fDC = 0; 
        difCampo = null; fDF = 0;
        for (int w=0;w<jCampo.length;w++) {
            gmpCampo = null;
            gmpCampo = FuncoesGlobais.treeSeekArray2(jCampo, "DC", w, w);
            if (Integer.valueOf(gmpCampo[0]) > -1 && Integer.valueOf(gmpCampo[1]) > -1) {
                fDC += ("LQ".equals(gmpCampo[4]) ? LerValor.StringToFloat(gmpCampo[3]) : 0);
            } else fDC += 0;

            difCampo = null;
            difCampo = FuncoesGlobais.treeSeekArray2(jCampo, "DF", w, w);                        
            if (Integer.valueOf(difCampo[0]) > -1 && Integer.valueOf(difCampo[1]) > -1) {
                fDF += ("LQ".equals(difCampo[4]) ? LerValor.StringToFloat(difCampo[3]) : 0);
            } else fDF += 0;
        }
        // ------------- FIM Descontos/Diferenças

        for (int j=0; j < aCampo.length; j++) {
            int k = aCampo[j].indexOf("AL");
            if (k > -1) {
                //zCampos = new DepuraCampos(aCampo[j]);
                zCampos = new DepuraCampos(tCampo);
                VariaveisGlobais.ccampos = aCampo[j];
                zCampos.SplitCampos();
                zCampo = zCampos.Depurar(j); 

                if ("AL".equals(aCampo[j].split(":")[4])) {
                    float calc1 = LerValor.StringToFloat(zCampo[1]) + fDF - fDC;
                    float calc2 = fComissao / 100;
                    float reslt = calc1 * calc2;

                    String valor = "";
                    if (fComissao != 0) {
                        valor = String.valueOf(reslt).replace(".", ",");
                    }
                    aCampo[j] += ":CM" + FuncoesGlobais.GravaValores(valor, 2);
                }
                tmpCampo = FuncoesGlobais.treeArray(FuncoesGlobais.join(aCampo, ";"),false);

                // Ja foi adiantado
                boolean bad = (FuncoesGlobais.treeSeekArray(tmpCampo, "AD", 0)[0].equalsIgnoreCase("-1") ? false : true);

                if (!"DC".equals(aCampo[j].split(":")[4]) || !"DF".equals(aCampo[j].split(":")[4]) || !"SG".equals(aCampo[j].split(":")[4])) {
                    admValores = new String[] {};
                    // Separa valores da ADM
                    // Separa Multa
                    gmpCampo = FuncoesGlobais.treeSeekArray(tmpCampo, "MU", 0);
                    if (Integer.valueOf(gmpCampo[0]) > -1 && Integer.valueOf(gmpCampo[1]) > -1) {
                        float valor1 = LerValor.StringToFloat(gmpCampo[3]) - (LerValor.StringToFloat(gmpCampo[3]) * (LerValor.StringToFloat((bad ? "100" : admPer[0])) / 100));
                        String valor = String.valueOf(valor1).replace(".", ",");
                        if (valor1 > 0) {
                            tmpCampo[Integer.valueOf(gmpCampo[0])][Integer.valueOf(gmpCampo[1])] = "MU" + FuncoesGlobais.GravaValores(valor, 2);
                        } else {
                            tmpCampo = FuncoesGlobais.ArraysDelSub(tmpCampo, Integer.valueOf(gmpCampo[0]), Integer.valueOf(gmpCampo[1]));
                        }
                        float valor3 = (LerValor.StringToFloat(gmpCampo[3]) * (LerValor.StringToFloat((bad ? "100" : admPer[0])) / 100));
                        String valor_mu = String.valueOf(valor3).replace(".", ",");
                        admValores = FuncoesGlobais.ArrayAdd(admValores, valor_mu);
                    } else {
                        admValores = FuncoesGlobais.ArrayAdd(admValores, "0");
                    }

                    // Juros
                    gmpCampo = FuncoesGlobais.treeSeekArray(tmpCampo, "JU", 0);
                    if (Integer.valueOf(gmpCampo[0]) > -1 && Integer.valueOf(gmpCampo[1]) > -1) {
                        float valor1 = LerValor.StringToFloat(gmpCampo[3]) - (LerValor.StringToFloat(gmpCampo[3]) * (LerValor.StringToFloat((bad ? "100" : admPer[1])) / 100));
                        String valor = String.valueOf(valor1).replace(".", ",");
                        if (valor1 > 0) {
                            tmpCampo[Integer.valueOf(gmpCampo[0])][Integer.valueOf(gmpCampo[1])] = "JU" + FuncoesGlobais.GravaValores(valor, 2);
                        } else {
                            tmpCampo = FuncoesGlobais.ArraysDelSub(tmpCampo, Integer.valueOf(gmpCampo[0]), Integer.valueOf(gmpCampo[1]));
                        }
                        float valor3 = (LerValor.StringToFloat(gmpCampo[3]) * (LerValor.StringToFloat((bad ? "100" : admPer[1])) / 100));
                        String valor_ju = String.valueOf(valor3).replace(".", ",");
                        admValores = FuncoesGlobais.ArrayAdd(admValores, valor_ju);
                    } else {
                        admValores = FuncoesGlobais.ArrayAdd(admValores, "0");
                    }

                    // Correção
                    gmpCampo = FuncoesGlobais.treeSeekArray(tmpCampo, "CO", 0);
                    if (Integer.valueOf(gmpCampo[0]) > -1 && Integer.valueOf(gmpCampo[1]) > -1) {
                        float valor1 = LerValor.StringToFloat(gmpCampo[3]) - (LerValor.StringToFloat(gmpCampo[3]) * (LerValor.StringToFloat((bad ? "100" : admPer[2])) / 100));
                        String valor = String.valueOf(valor1).replace(".", ",");
                        if (valor1 > 0) {
                            tmpCampo[Integer.valueOf(gmpCampo[0])][Integer.valueOf(gmpCampo[1])] = "CO" + FuncoesGlobais.GravaValores(valor, 2);
                        } else {
                            tmpCampo = FuncoesGlobais.ArraysDelSub(tmpCampo, Integer.valueOf(gmpCampo[0]), Integer.valueOf(gmpCampo[1]));
                        }
                        float valor3 = (LerValor.StringToFloat(gmpCampo[3]) * (LerValor.StringToFloat((bad ? "100" : admPer[2])) / 100));
                        String valor_co = String.valueOf(valor3).replace(".", ",");
                        admValores = FuncoesGlobais.ArrayAdd(admValores, valor_co);
                    } else admValores = FuncoesGlobais.ArrayAdd(admValores, "0");

                    // Expediente
                    gmpCampo = FuncoesGlobais.treeSeekArray(tmpCampo, "EP", 0);
                    if (Integer.valueOf(gmpCampo[0]) > -1 && Integer.valueOf(gmpCampo[1]) > -1) {
                        float valor1 = LerValor.StringToFloat(gmpCampo[3]) - (LerValor.StringToFloat(gmpCampo[3]) * (LerValor.StringToFloat((bad ? "100" : admPer[3])) / 100));
                        String valor = String.valueOf(valor1).replace(".", ",");
                        if (valor1 > 0) {
                            tmpCampo[Integer.valueOf(gmpCampo[0])][Integer.valueOf(gmpCampo[1])] = "EP" + FuncoesGlobais.GravaValores(valor, 2);
                        } else {
                            tmpCampo = FuncoesGlobais.ArraysDelSub(tmpCampo, Integer.valueOf(gmpCampo[0]), Integer.valueOf(gmpCampo[1]));
                        }
                        float valor3 = (LerValor.StringToFloat(gmpCampo[3]) * (LerValor.StringToFloat((bad ? "100" : admPer[3])) / 100));
                        String valor_ep = String.valueOf(valor3).replace(".", ",");
                        admValores = FuncoesGlobais.ArrayAdd(admValores, valor_ep);
                    } else admValores = FuncoesGlobais.ArrayAdd(admValores, "0");

                    // Separa comissão
                    gmpCampo = FuncoesGlobais.treeSeekArray(tmpCampo, "CM", 0);
                    if (Integer.valueOf(gmpCampo[0]) > -1 && Integer.valueOf(gmpCampo[1]) > -1) {
                        admValores = FuncoesGlobais.ArrayAdd(admValores, gmpCampo[3]);
                    } else admValores = FuncoesGlobais.ArrayAdd(admValores, "0");

                    aCampo[j] = FuncoesGlobais.join(tmpCampo[j], ":");

                    // Envio dos Valores da ADM
                    if (LerValor.StringToFloat(admValores[0]) + LerValor.StringToFloat(admValores[1]) + LerValor.StringToFloat(admValores[2]) + LerValor.StringToFloat(admValores[3]) > 0) {
                        gCampo = FuncoesGlobais.ArraysAdd(gCampo, new String[] {admValores[0], admValores[1], admValores[2], admValores[3], sVenctos[i]});
                    }
                }
            }
        }

        // Clona Variavel
        String[] oCampo = aCampo;

        for (int l=0;l<aCC.length;l++) {
            dvCampo = FuncoesGlobais.treeArray(FuncoesGlobais.join(aCampo, ";"), false);
            for (int m=0;m<dvCampo.length;m++){
                //';
                float part1 = LerValor.StringToFloat(LerValor.FormatNumber(dvCampo[m][2], 2));
                float part2 = LerValor.StringToFloat(aCC[l][1].replace(".", ",")) / 100;
                if (FuncoesGlobais.IndexOf(dvCampo[m], "AL") < 0) part2 = 1;

                // 15-12-2016 12h30m
                if (l==0) {
                    if (FuncoesGlobais.IndexOf(dvCampo[m], "AL") < 0) part2 = 1;
                } else {
                    if (FuncoesGlobais.IndexOf(dvCampo[m], "AL") < 0) {
                        part2 = 0;
                    } else part2 = 1;
                }

                String vrfinal = LerValor.FloatToString(part1 * part2);

                //dvCampo[m][2] = winger.GravarValor(winger.LerValor(dvCampo[m][2]) * If(FuncoesGlobais.ArraFind(dvCampo[m], "AL") < 0, 1, (aCC[l][1] / 100)));

                if (aCC[l][2].trim().toUpperCase().equalsIgnoreCase("TRUE")) {
                    //'; divide tudo
                    float fValor1 = LerValor.StringToFloat(LerValor.FormatNumber(dvCampo[m][2], 2));
                    float fValor2 = Float.valueOf(aCC[l][1].replace(",", ".")) / 100;
                    dvCampo[m][2] = FuncoesGlobais.GravaValores(LerValor.FloatToString(fValor1 * fValor2),2);
                } else {
                    // 06/01/2017 8h10m
                    if ("AL".equals(dvCampo[m][4])) {
                        float fValor1 = LerValor.StringToFloat(LerValor.FormatNumber(dvCampo[m][2], 2));
                        float fValor2 = Float.valueOf(aCC[l][1].replace(",", ".")) / 100;
                        dvCampo[m][2] = FuncoesGlobais.GravaValores(LerValor.FloatToString(fValor1 * fValor2),2);
                    } else {
                        dvCampo[m][2] = FuncoesGlobais.GravaValores(vrfinal, 2);                    
                    }
                }

                if ("AL".equals(dvCampo[m][4])) {
                    // Separa Multa
                    gmpCampo = new String[] {};
                    gmpCampo = FuncoesGlobais.treeSeekArray(dvCampo, "MU", 0);
                    if (Integer.valueOf(gmpCampo[0]) > -1 && Integer.valueOf(gmpCampo[1]) > -1) {
                        float valor1 = (LerValor.StringToFloat(gmpCampo[3]) * (LerValor.StringToFloat(aCC[l][1]) / 100));
                        String valor = String.valueOf(valor1).replace(".", ",");
                        dvCampo[Integer.valueOf(gmpCampo[0])][Integer.valueOf(gmpCampo[1])] = "MU" + FuncoesGlobais.GravaValores(valor, 2);
                    }


                    // Separa juros
                    gmpCampo = new String[] {};
                    gmpCampo = FuncoesGlobais.treeSeekArray(dvCampo, "JU", 0);
                    if (Integer.valueOf(gmpCampo[0]) > -1 && Integer.valueOf(gmpCampo[1]) > -1) {
                        float valor1 = (LerValor.StringToFloat(gmpCampo[3]) * (LerValor.StringToFloat(aCC[l][1]) / 100));
                        String valor = String.valueOf(valor1).replace(".", ",");
                        dvCampo[Integer.valueOf(gmpCampo[0])][Integer.valueOf(gmpCampo[1])] = "JU" + FuncoesGlobais.GravaValores(valor, 2);
                    }

                    // Separa Correção
                    gmpCampo = new String[] {};
                    gmpCampo = FuncoesGlobais.treeSeekArray(dvCampo, "CO", 0);
                    if (Integer.valueOf(gmpCampo[0]) > -1 && Integer.valueOf(gmpCampo[1]) > -1) {
                        float valor1 = (LerValor.StringToFloat(gmpCampo[3]) * (LerValor.StringToFloat(aCC[l][1]) / 100));
                        String valor = String.valueOf(valor1).replace(".", ",");
                        dvCampo[Integer.valueOf(gmpCampo[0])][Integer.valueOf(gmpCampo[1])] = "CO" + FuncoesGlobais.GravaValores(valor, 2);
                    }

                    // Separa Expediente
                    gmpCampo = new String[] {};
                    gmpCampo = FuncoesGlobais.treeSeekArray(dvCampo, "EP", 0);
                    if (Integer.valueOf(gmpCampo[0]) > -1 && Integer.valueOf(gmpCampo[1]) > -1) {
                        float valor1 = (LerValor.StringToFloat(gmpCampo[3]) * (LerValor.StringToFloat(aCC[l][1]) / 100));
                        String valor = String.valueOf(valor1).replace(".", ",");
                        dvCampo[Integer.valueOf(gmpCampo[0])][Integer.valueOf(gmpCampo[1])] = "EP" + FuncoesGlobais.GravaValores(valor, 2);
                    }

                    // Separa Comissao
                    gmpCampo = new String[] {};
                    gmpCampo = FuncoesGlobais.treeSeekArray(dvCampo, "CM", 0);
                    if (Integer.valueOf(gmpCampo[0]) > -1 && Integer.valueOf(gmpCampo[1]) > -1) {
                        float valor1 = (LerValor.StringToFloat(gmpCampo[3]) * (LerValor.StringToFloat(aCC[l][1].replace(".", ",")) / 100));
                        String valor = String.valueOf(valor1).replace(".", ",");
                        dvCampo[Integer.valueOf(gmpCampo[0])][Integer.valueOf(gmpCampo[1])] = "CM" + FuncoesGlobais.GravaValores(valor, 2);
                    }
                }
            }

            //String[] tpCampo = {contrato.trim(), aCC[l][0], rgimv, FuncoesGlobais.SuperJoin(dvCampo, l == 0)};
            String[] tpCampo = {contrato.trim(), aCC[l][0], rgimv, FuncoesGlobais.SuperJoin(dvCampo, true)};
            divaCampo = FuncoesGlobais.ArraysAdd(divaCampo, tpCampo);
        }

        for (int l=0; l<aCC.length; l++) {
            aCampo = null; aCampo = divaCampo[l][3].split(";");

            String[] rCampo = {};
            String[] eCampo = {};
            String[] iCampo = {};

            String[] lCampo = {};
            String[] xCampo = {};
            String[] pCampo = {};
            String[] sCampo = {};
            String[] hCampo = {};

            for (int j=0; j<aCampo.length; j++) {
                String[] wCampo = aCampo[j].split(":");

                int k = aCampo[j].indexOf("AL");
                if (k > -1) lCampo = FuncoesGlobais.ArrayAdd(lCampo, aCampo[j]);

                // Retenção
                k = aCampo[j].indexOf("RT");
                if (k > -1) sCampo = FuncoesGlobais.ArrayAdd(sCampo, aCampo[j]);

                // Antecipados
                k = aCampo[j].indexOf("AT");
                if (k > -1) hCampo = FuncoesGlobais.ArrayAdd(hCampo, aCampo[j]);

                // Expediente
                k = FuncoesGlobais.IndexOf(wCampo, "EP");
                if (k > -1) pCampo = FuncoesGlobais.ArrayAdd(pCampo, "EP" + wCampo[k].substring(wCampo[k].length() - 10, wCampo[k].length()) + ":0000:EP");

                // Taxas -------------
                k = aCampo[j].indexOf("AL");
                if (k < 0) xCampo = FuncoesGlobais.ArrayAdd(xCampo, aCampo[j]);

                // Campos Razao
                k = aCampo[j].indexOf("RZ");
                if (k > -1) rCampo = FuncoesGlobais.ArrayAdd(rCampo, aCampo[j]);

                k = aCampo[j].indexOf("ET");
                if (k > -1) eCampo = FuncoesGlobais.ArrayAdd(eCampo, aCampo[j]);

                k = aCampo[j].indexOf("IP");
                if (k > -1) iCampo = FuncoesGlobais.ArrayAdd(iCampo, aCampo[j]);
            }

            //String[] tpCampo = {lCampo, pCampo, xCampo, rCampo, eCampo, iCampo, sCampo};
            String slCampo = "", spCampo = "", sxCampo = "", srCampo = "", seCampo = "", siCampo = "", ssCampo = "", waCampo = "";
            try {slCampo = FuncoesGlobais.join(lCampo, ";");} catch (Exception ex) {slCampo = "";}
            try {spCampo = FuncoesGlobais.join(pCampo, ";");} catch (Exception ex) {spCampo = "";}
            try {sxCampo = FuncoesGlobais.join(rCampo, ";");} catch (Exception ex) {sxCampo = "";}
            try {srCampo = FuncoesGlobais.join(rCampo, ";");} catch (Exception ex) {srCampo = "";}
            try {seCampo = FuncoesGlobais.join(eCampo, ";");} catch (Exception ex) {seCampo = "";}
            try {siCampo = FuncoesGlobais.join(iCampo, ";");} catch (Exception ex) {siCampo = "";}
            try {ssCampo = FuncoesGlobais.join(sCampo, ";");} catch (Exception ex) {ssCampo = "";}

            // 29/09/2014 11h
            try {waCampo = FuncoesGlobais.join(hCampo, ";");} catch (Exception ex) {waCampo = "";}
            String finalCampos = slCampo + "," + spCampo + "," + sxCampo + "," + srCampo + "," + seCampo + "," + siCampo + "," + ssCampo; // + "," + waCampo;
            divaCampo[l] = FuncoesGlobais.ArrayAdd(divaCampo[l], finalCampos);
        }

        sucesso = true;
        for (int l=0;l<gCampo.length;l++) {
            String sADMCPOS = "";
            for (int p=0;p<gCampo[l].length - 1;p++) {
                if (LerValor.StringToFloat(gCampo[l][p]) != 0) {
                    String sSql = "INSERT INTO razao (rgprp, campo, dtvencimento, dtrecebimento, rc_aut, tag) VALUES ('&1.','&2.','&3.','&4.','&5.',' ')";

                    String par1 = "GG";
                    String par2 = "GG:9:" +
                                  FuncoesGlobais.GravaValores(gCampo[l][p],2) +
                                  ":000000:GG:ET:" +
                                  FuncoesGlobais.StrZero(String.valueOf((int)nAut).replace(".0", "").trim(), 6) +
                                  VariaveisGlobais.cContas.get("GG") +
                                  FuncoesGlobais.Choose(p + 1,new String[] {"", "MU", "JU", "CO", "EP"}) +
                                  ":" + Dates.DateFormata("yyyyMMdd", new Date()) +
                                  ":CRE:DN:656877:" +
                                  VariaveisGlobais.usuario;
                    String par3 = Dates.DateFormata("yyyy-MM-dd", Dates.StringtoDate(sVenctos[i], "dd/MM/yyyy"));
                    String par4 = Dates.DateFormata("yyyy-MM-dd", new Date());
                    String par5 = String.valueOf((int)nAut).replace(".0", "");
                    sSql = FuncoesGlobais.Subst(sSql, new String[] {par1, par2, par3, par4, par5});

                    try {
                        // Grava no Razao
                        conn.ExecutarComando(sSql);
                    } catch (Exception e) { sucesso = false; }

                    sADMCPOS += FuncoesGlobais.Choose(p + 1, new String[] {"", "MU", "JU", "CO", "EP"}) + FuncoesGlobais.GravaValores(gCampo[l][p].replace(".", ","),2) + ":";
                }
            }
            if (!"".equals(sADMCPOS)) {
                sADMCPOS = sADMCPOS.substring(0, sADMCPOS.length() - 1);
                conn.CreateArqAux();
                String sSql = "INSERT INTO auxiliar (contrato, rgprp, rgimv, campo, dtvencimento, dtrecebimento, rc_aut, conta) VALUES ('&1.','&2.','&3.','&4.','&5.','&6.','&7.','&8.')";
                String[] variavel = new String[] {contrato, rgprp, rgimv, sADMCPOS,
                       Dates.DateFormata("yyyy-MM-dd",Dates.StringtoDate(gCampo[l][gCampo[l].length - 1],"dd/MM/yyyy")),
                       Dates.DateFormata("yyyy-MM-dd", new Date()),
                       String.valueOf((int)nAut).replace(".0", ""),
                       "ADM"};
                sSql = FuncoesGlobais.Subst(sSql, variavel);

                try {
                    conn.ExecutarComando(sSql);
                } catch (Exception e) { sucesso = false; }
            }
        }

        for (int l=0;l<divaCampo.length;l++) {
            // Splitar Itens do DivaCampos Elemento (4)
            String[] divaSplit = divaCampo[l][4].split(",");

            // gravar no arquivo auxiliar
            conn.CreateArqAux();
            String sSql = "INSERT INTO auxiliar (contrato, rgprp, rgimv, campo, dtvencimento, dtrecebimento, rc_aut, conta) VALUES ('&1.','&2.','&3.','&4.','&5.','&6.','&7.','&8.')";
            sSql = FuncoesGlobais.Subst(sSql, new String[] {
                   divaCampo[l][0],
                   divaCampo[l][1],
                   divaCampo[l][2],
                   divaCampo[l][3],
                   Dates.DateFormata("yyyy-MM-dd", Dates.StringtoDate(sVenctos[i], "dd/MM/yyyy")),
                   Dates.DateFormata("yyyy-MM-dd", new Date()), String.valueOf((int)nAut).replace(".0", ""), "REC"});

            try {
                conn.ExecutarComando(sSql);
            } catch (Exception e) { sucesso = false; }

            // gravar gravar razao na tabela
            if (!"".equals(divaSplit[3].trim())) {
                sSql = "INSERT INTO razao (contrato, rgprp, rgimv, campo, dtvencimento, dtrecebimento, rc_aut, tag) VALUES ('&1.','&2.','&3.','&4.','&5.','&6.','&7.', ' ')";
                String[] par1 = {divaCampo[l][0],
                                 divaCampo[l][1],
                                 divaCampo[l][2],
                                 divaSplit[3],
                                 Dates.DateFormata("yyyy-MM-dd", Dates.StringtoDate(sVenctos[i], "dd/MM/yyyy")),
                                 Dates.DateFormata("yyyy-MM-dd", new Date()),
                                 String.valueOf((int)nAut).replace(".0", "")};
                sSql = FuncoesGlobais.Subst(sSql, par1);

                try {
                    conn.ExecutarComando(sSql);
                } catch (Exception e) { sucesso = false; }
            }

            // gravar extrato na tabela
            if (!"".equals(divaSplit[4].trim())) {
                sSql = "INSERT INTO extrato (contrato, rgprp, rgimv, campo, dtvencimento, dtrecebimento, rc_aut, tag) VALUES ('&1.','&2.','&3.','&4.','&5.','&6.','&7.', ' ')";
                String[] par1 = {divaCampo[l][0],
                                 divaCampo[l][1],
                                 divaCampo[l][2],
                                 divaSplit[4],
                                 Dates.DateFormata("yyyy-MM-dd", Dates.StringtoDate(sVenctos[i], "dd/MM/yyyy")),
                                 Dates.DateFormata("yyyy-MM-dd", new Date()),
                                 String.valueOf((int)nAut).replace(".0", "")};
                sSql = FuncoesGlobais.Subst(sSql, par1);

                try {
                    conn.ExecutarComando(sSql);
                } catch (Exception e) { sucesso = false; }
            }

            // Grava imposto na tabela
            if (divaSplit.length >= 6) {
                    if (!"".equals(divaSplit[5].trim())) {
                        sSql = "INSERT INTO imposto (contrato, rgprp, rgimv, campo, dtvencimento, dtrecebimento, rc_aut, tag) VALUES ('&1.','&2.','&3.','&4.','&5.','&6.','&7.',' ')";
                        String[] par1 = {divaCampo[l][0],
                                         divaCampo[l][1],
                                         divaCampo[l][2],
                                         divaSplit[5],
                                         Dates.DateFormata("yyyy-MM-dd", Dates.StringtoDate(sVenctos[i], "dd/MM/yyyy")),
                                         Dates.DateFormata("yyyy-MM-dd", new Date()),
                                         String.valueOf((int)nAut).replace(".0", "")};
                        sSql = FuncoesGlobais.Subst(sSql, par1);

                        try {
                            conn.ExecutarComando(sSql);
                        } catch (Exception e) { sucesso = false; }
                }
            }

            // Grava Retenções na tabela
            if (divaSplit.length > 6) {
                String[] retencaoSplit = divaSplit[6].split(";");
                for (int n=0;n<retencaoSplit.length;n++) {
                    sSql = "INSERT INTO retencao (contrato, rgprp, rgimv, campo, vencimento, rc_aut, tag, gat, rt_aut) VALUES ('&1.','&2.','&3.','&4.','&5.','&6.','&7.','&8.','&9.')";
                    String[] par1 = {divaCampo[l][0],
                                     divaCampo[l][1],
                                     divaCampo[l][2],
                                     retencaoSplit[n],
                                     Dates.DateFormata("yyyy-MM-dd", Dates.StringtoDate(sVenctos[i], "dd/MM/yyyy")),
                                     String.valueOf((int)nAut).replace(".0", ""),"0"," ","0"};
                    sSql = FuncoesGlobais.Subst(sSql, par1);

                    try {
                        conn.ExecutarComando(sSql);
                    } catch (Exception e) { sucesso = false; }
                }
            }
        }

        // Atualiza Antecipados 29/09/2014
        for (int h=0;h<jCampo.length;h++) {
            if (jCampo[h][jCampo[h].length - 1].equalsIgnoreCase("AT")) {
                if (Dates.isDateValid(jCampo[h][4], "ddMMyyyy")) {
                    String sSql = "UPDATE ANTECIPADOS SET dtrecebimento = '&1.', rc_aut = '&2.' WHERE contrato = '&3.' AND dtvencimento = '&4.' AND Mid(campo,1,2) = '&5.';";
                    sSql = FuncoesGlobais.Subst(sSql, new String[] {
                            Dates.DateFormata("yyyy-MM-dd", new Date()),
                        String.valueOf((int)nAut).replace(".0", ""), contrato, Dates.DateFormata("yyyy-MM-dd", Dates.StringtoDate(jCampo[h][4], "ddMMyyyy")), jCampo[h][0]});

                    System.out.println(sSql);
                    try {
                        conn.ExecutarComando(sSql);
                    } catch (Exception e) { sucesso = false; }
                }
            }
        }

        if (nAut > -1 && sucesso) {
            // Elimina os recibos
            String sql = "UPDATE RECIBO SET tag = 'X', AUTENTICACAO = '" + String.valueOf((int)nAut).replace(".0", "") + "' WHERE contrato = '" + contrato + "' AND dtvencimento = '" + Dates.DateFormata("yyyy-MM-dd", Dates.StringtoDate(vencto, "dd/MM/yyyy")) + "';";
            try {
                conn.ExecutarComando(sql);
            } catch (Exception e) { sucesso = false; }

            if (!sucesso) { 
                ExtornaRecibo(String.valueOf((int)nAut).replace(".0", "")); 
            } 

            try {
                sql = "UPDATE CARTEIRA SET dtultrecebimento = '" + Dates.DateFormata("dd-MM-yyyy",Dates.StringtoDate(vencto,"dd-MM-yyyy")) + "' WHERE contrato = '" + contrato + "';";
                conn.ExecutarComando(sql);
            } catch (Exception e) { 
                sucesso = false;
                e.printStackTrace(); 
            }
        } 

        return sucesso;
    }    
    
    private void ExtornaRecibo(String nAut) {
        try {
            String sql = "";
            sql = FuncoesGlobais.Subst("DELETE FROM Cheques WHERE ch_autenticacao = '&1.'", new String[] {nAut});
            conn.ExecutarComando(sql);

            sql = FuncoesGlobais.Subst("DELETE FROM razao WHERE rc_aut = '&1.'", new String[] {nAut});
            conn.ExecutarComando(sql);

            sql = FuncoesGlobais.Subst("DELETE FROM retencao WHERE rc_aut = '&1.'", new String[] {nAut});
            conn.ExecutarComando(sql);

            sql = FuncoesGlobais.Subst("DELETE FROM imposto WHERE rc_aut = '&1.'", new String[] {nAut});
            conn.ExecutarComando(sql);

            sql = FuncoesGlobais.Subst("DELETE FROM extrato WHERE rc_aut = '&1.'", new String[] {nAut});
            conn.ExecutarComando(sql);

            sql = FuncoesGlobais.Subst("UPDATE RECIBO SET TAG = ' ', AUTENTICACAO = '0' WHERE AUTENTICACAO = '&1.'", new String[] {nAut});
            conn.ExecutarComando(sql);        
        } catch (Exception e) {}
    }
    
    private String CriticaCampo(String rgprp, String rgimv, String contrato, String recto, String campo) throws SQLException {
        String[] aCampo; int tCampo = 0; String ctCampo = "";
        float tbTaxa, tbMulta, tbJurosxt, tbCorrecao = 0;

        String tmpCampo = campo.trim();
        if (!"".equalsIgnoreCase(tmpCampo)) {
            aCampo = tmpCampo.split(";");
            aCampo = FuncoesGlobais.OrdenaMatriz(aCampo, 3, 1, true);
            tCampo = aCampo.length - 1;

            ReCalculos rc = new ReCalculos();
            rc.Inicializa(rgprp, rgimv, contrato);
            rc.setCalcDate(Dates.StringtoDate(recto, "dd/MM/yyyy"));      
            tbTaxa = rc.TaxaExp(tmpCampo);
            tbMulta = rc.Multa(tmpCampo, recto, false);
            tbJurosxt = rc.Juros(tmpCampo, recto);
            tbCorrecao = rc.Correcao(tmpCampo, recto);

            for (int nConta=0; nConta <= tCampo; nConta++) {
                ctCampo = aCampo[nConta];
                String[] rCampos = ctCampo.split(":");

                if ("AL".equals(rCampos[4])) {
                    int iMulta = FuncoesGlobais.IndexOf(rCampos,"MU");
                    if (iMulta > -1) {
                        rCampos[iMulta] = "MU" + FuncoesGlobais.GravaValores(String.valueOf(tbMulta).replace(".", ","), 2);
                    } else {
                        rCampos = FuncoesGlobais.ArrayAdd(rCampos, "MU" + FuncoesGlobais.GravaValores(String.valueOf(tbMulta).replace(".", ","), 2));
                    }

                    int iJuros = FuncoesGlobais.IndexOf(rCampos,"JU");
                    if (iJuros > -1) {
                        rCampos[iJuros] = "JU" + FuncoesGlobais.GravaValores(String.valueOf(tbJurosxt).replace(".", ","), 2);
                    } else {
                        rCampos = FuncoesGlobais.ArrayAdd(rCampos, "JU" + FuncoesGlobais.GravaValores(String.valueOf(tbJurosxt).replace(".", ","), 2));
                    }

                    int iCorrecao = FuncoesGlobais.IndexOf(rCampos,"CO");
                    if (iCorrecao > -1) {
                        rCampos[iCorrecao] = "CO" + FuncoesGlobais.GravaValores(String.valueOf(tbCorrecao).replace(".", ","), 2);
                    } else {
                        rCampos = FuncoesGlobais.ArrayAdd(rCampos, "CO" + FuncoesGlobais.GravaValores(String.valueOf(tbCorrecao).replace(".", ","), 2));
                    }

                    int iExp = FuncoesGlobais.IndexOf(rCampos,"EP");
                    if (iExp > -1) {
                        rCampos[iExp] = "EP" + FuncoesGlobais.GravaValores(String.valueOf(tbTaxa).replace(".", ","), 2);
                    } else {
                        rCampos = FuncoesGlobais.ArrayAdd(rCampos, "EP" + FuncoesGlobais.GravaValores(String.valueOf(tbTaxa).replace(".", ","), 2));
                    }
                }
                aCampo[nConta] = FuncoesGlobais.join(rCampos, ":");
            }
            tmpCampo = FuncoesGlobais.join(aCampo, ";");
        }
        return tmpCampo;
    }
    
    private String AlteraMUJUCOEP(String campos, String[][] mujucoep) {
        String auxCpo = campos;
        String tmujucoep = "";        
        String vMulta = ""; String vJuros = ""; String vCorrecao = ""; String vExpediente = "";
        String jMulta = ""; String jJuros = ""; String jCorrecao = ""; String jExpediente = "";
        for (String[] a : mujucoep) {
            if (a[0].toString().equalsIgnoreCase("MU")) {
                jMulta = a[1].toString();
                if (LerValor.StringToFloat(jMulta) > 0) {
                    vMulta = "MU" + FuncoesGlobais.GravaValor(jMulta);
                } else {
                    vMulta = "MU";
                }
                String oldMU = BuscaXX(auxCpo, "MU");
                auxCpo = auxCpo.replace(oldMU, "");
                tmujucoep += vMulta + ":";
            } else if (a[0].toString().equalsIgnoreCase("JU")) {
                jJuros = a[1].toString();
                if (LerValor.StringToFloat(jJuros) > 0) {
                    vJuros = "JU" + FuncoesGlobais.GravaValor(jJuros);
                } else {
                    vJuros = "JU";
                }
                String oldJU = BuscaXX(auxCpo, "JU");
                auxCpo = auxCpo.replace(oldJU, "");
                tmujucoep += vJuros + ":"; 
            } else if (a[0].toString().equalsIgnoreCase("CO")) {
                jCorrecao = a[1].toString();
                if (LerValor.StringToFloat(jCorrecao) > 0) {
                    vCorrecao = "CO" + FuncoesGlobais.GravaValor(jCorrecao);
                } else {
                    vCorrecao = "CO";
                }
                String oldCO = BuscaXX(auxCpo, "CO");
                auxCpo = auxCpo.replace(oldCO, "");
                tmujucoep += vCorrecao + ":"; 
            } else if (a[0].toString().equalsIgnoreCase("EP")) {
                jExpediente = a[1].toString();
                if (LerValor.StringToFloat(jExpediente) > 0) {
                    vExpediente = "EP" + FuncoesGlobais.GravaValor(jExpediente);
                } else {
                    vExpediente = "EP";
                }
                String oldEP = BuscaXX(auxCpo, "EP");
                auxCpo = auxCpo.replace(oldEP, "");
                tmujucoep += vExpediente + ":"; 
            }
        }

        int pos = auxCpo.indexOf("AL:");
        auxCpo = auxCpo.substring(0, pos + 3) + tmujucoep + auxCpo.substring(pos + 3);

        return auxCpo;
    }
}
