/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Movimento;

import Funcoes.Convert;
import Funcoes.Dates;
import Funcoes.Db;
import Funcoes.DbMain;
import Funcoes.FuncoesGlobais;
import Funcoes.Pad;
import Funcoes.StreamFile;
import Funcoes.VariaveisGlobais;
import Funcoes.jDirectory;
import Funcoes.jTableControl;
import Funcoes.toPreview;
import j4rent.Partida.Collections;
import java.awt.Color;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JFileChooser;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRPdfExporter;

/**
 *
 * @author supervisor
 */
public class jDimob extends javax.swing.JInternalFrame {
    DbMain conn = VariaveisGlobais.conexao;
    Db db = new Db();
    jTableControl tabela = new jTableControl(true);
    TableRowSorter<TableModel> sorter;
    
    /**
     * Creates new form jDimob
     */
    public jDimob() {
        initComponents();
        
        // Caso não exista cria tabela dimob
        conn.ExisteTabelaDimob();
        
        Collections gVar = VariaveisGlobais.dCliente;
        jCnpj.setText(gVar.get("cnpj"));
        jAno.setValue(Dates.iYear(new Date()));
        jrazao.setText(gVar.get("empresa"));
        jendereco.setText(gVar.get("endereco") + ", " + gVar.get("numero") + " " + gVar.get("complemento"));
        jestado.setText(gVar.get("estado"));

    }

    class ListaLoca extends Thread {
        public ListaLoca() {}
        public void run() { ListarLocatarios(jAno.getValue().toString().trim()); }
    }
    
    private void ListarLocatarios(String sAno) {
        //String sql = "SELECT DISTINCT a.rgprp, l.nome FROM auxiliar a, proprietarios l where (a.rgprp = l.rgprp) AND a.conta = 'REC' and InStr(a.campo,'01:1:') and Year(a.dtrecebimento) = '&1.' order by l.nome;";
        String sql = "SELECT DISTINCT a.rgprp, l.nome FROM auxiliar a, proprietarios l where (a.rgprp = l.rgprp) AND a.conta = 'REC' and InStr(a.campo,'01:1:') and Year(a.dtrecebimento) = '&1.' " + 
                     "union " +
                     "SELECT DISTINCT ae.rgprp, le.nome FROM jgeral_excluidos.auxiliar ae, jgeral_excluidos.proprietarios le where (ae.rgprp = le.rgprp) AND ae.conta = 'REC' and InStr(ae.campo,'01:1:') and Year(ae.dtrecebimento) = '&1.' " + 
                     "order by 2;";
        sql = FuncoesGlobais.Subst(sql, new String[] {sAno});

        ResultSet rs = conn.AbrirTabela(sql, ResultSet.CONCUR_READ_ONLY);
        Integer[] tam = {80,700,20};
        String[] col = {"contrato","locatario","tag"};
        Boolean[] edt = {false,false,true};
        String[] aln = {"L","L",""};
        Object[][] data = {};
        try {
            while (rs.next()) {
                String dcontrato = rs.getString("rgprp");
                String dnome = rs.getString("nome");
                int pos = dnome.indexOf(" - ");
                if (pos > -1) {
                    dnome = dnome.substring(0, pos);
                }
                Boolean dTag = false;
                
                Object[] dado = {dcontrato, dnome, dTag};
                data = tabela.insert(data, dado);
            }
        } catch (SQLException ex) {ex.printStackTrace();}

        DbMain.FecharTabela(rs);
        tabela.Show(aTable, data, tam, aln, col, edt);
        
        sorter = new TableRowSorter<TableModel>(aTable.getModel());
        aTable.setRowSorter(sorter);
        
    }
    
    class SimpleThread extends Thread {
        public SimpleThread() { }
        public void run() {
            ListaContratos(FuncoesGlobais.StrZero(jAno.getValue().toString().replace(".0", ""),4));
        }
    }
    
    class DirfThread extends Thread {
        public DirfThread() {}
        public void run() { 
            String nMes = "Todos";
            switch (jMes.getValue().toString().toUpperCase().trim()) {
                case "JANEIRO": nMes = "1"; break;
                case "FEVEREIRO": nMes = "2"; break;
                case "MARÇO": nMes = "3"; break;
                case "ABRIL": nMes = "4"; break;
                case "MAIO": nMes = "5"; break;
                case "JUNHO": nMes = "6"; break;
                case "JULHO": nMes = "7"; break;
                case "AGOSTO": nMes = "8"; break;
                case "SETEMBRO": nMes = "9"; break;
                case "OUTUBRO": nMes = "10"; break;
                case "NOVEMBRO": nMes = "11"; break;
                case "DEZEMBRO": nMes = "12"; break;
                default: nMes = "Todos";
            }
            ListaDirrf(nMes, FuncoesGlobais.StrZero(jAno.getValue().toString().replace(".0", ""),4));
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jCnpj = new javax.swing.JFormattedTextField();
        jLabel3 = new javax.swing.JLabel();
        jAno = new javax.swing.JSpinner();
        jRetifica = new javax.swing.JCheckBox();
        jLabel4 = new javax.swing.JLabel();
        jRecibo = new javax.swing.JFormattedTextField();
        jespecial = new javax.swing.JCheckBox();
        jLabel5 = new javax.swing.JLabel();
        jdtevento = new javax.swing.JFormattedTextField();
        jLabel6 = new javax.swing.JLabel();
        jcdsituacao = new javax.swing.JFormattedTextField();
        jLabel7 = new javax.swing.JLabel();
        jrazao = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jcpfresponsavel = new javax.swing.JFormattedTextField();
        jLabel9 = new javax.swing.JLabel();
        jendereco = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jestado = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jcdmunicipio = new javax.swing.JFormattedTextField();
        jfase = new javax.swing.JLabel();
        jbarra = new javax.swing.JProgressBar();
        jbtDimob = new javax.swing.JButton();
        jbtDirrf = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        aTable = new javax.swing.JTable();
        jLabel11 = new javax.swing.JLabel();
        cxTodos = new javax.swing.JCheckBox();
        jMes = new javax.swing.JSpinner();
        jLabel12 = new javax.swing.JLabel();

        setClosable(true);
        setIconifiable(true);
        setTitle(".:: DIMOB / DIRRF");
        setVisible(true);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "[DIMOB - Exportação] - Dados do Declarante", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(0, 45, 255))); // NOI18N
        jPanel1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jPanel1.setOpaque(false);

        jLabel2.setText("CNPJ:");

        jLabel3.setText("Ano:");

        jAno.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jAnoStateChanged(evt);
            }
        });

        jRetifica.setText("Declaração retificadora");

        jLabel4.setText("Recibo Nº");

        jespecial.setText("Situação especial");

        jLabel5.setText("Data do evento:");

        try {
            jdtevento.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("##/##/####")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }

        jLabel6.setText("Código situação:");

        jLabel7.setText("Razão:");

        jLabel8.setText("CPF do responsável:");

        jLabel9.setText("End.:");

        jLabel1.setText("UF:");

        jLabel10.setText("Código do Município:");

        try {
            jcdmunicipio.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("####")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCnpj)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jAno, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRetifica)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRecibo, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(459, 459, 459)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel6)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jcpfresponsavel)
                    .addComponent(jcdsituacao)))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jespecial)
                        .addGap(55, 55, 55)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jdtevento, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel7)
                            .addComponent(jLabel9))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jrazao, javax.swing.GroupLayout.PREFERRED_SIZE, 394, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jendereco)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jestado, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel10)))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jcdmunicipio, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jCnpj, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(jAno, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jRetifica)
                    .addComponent(jLabel4)
                    .addComponent(jRecibo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jdtevento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(jcdsituacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(jespecial))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jrazao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(jcpfresponsavel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(jendereco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(jestado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10)
                    .addComponent(jcdmunicipio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jfase.setBackground(new java.awt.Color(0, 153, 0));
        jfase.setFont(new java.awt.Font("DejaVu Sans", 3, 12)); // NOI18N
        jfase.setForeground(new java.awt.Color(255, 255, 255));
        jfase.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jfase.setText("Fase 1/2");
        jfase.setOpaque(true);

        jbarra.setFocusable(false);
        jbarra.setStringPainted(true);

        jbtDimob.setText("Gerar Exportação");
        jbtDimob.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtDimobActionPerformed(evt);
            }
        });

        jbtDirrf.setText("DIRRF");
        jbtDirrf.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtDirrfActionPerformed(evt);
            }
        });

        aTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(aTable);

        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11.setText("DIRRF - SELECIONE OS LOCATARIOS");
        jLabel11.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        cxTodos.setText("Todos os locatários");
        cxTodos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cxTodosActionPerformed(evt);
            }
        });

        jMes.setModel(new javax.swing.SpinnerListModel(new String[] {"Todos", "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"}));

        jLabel12.setText("Mes:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(jfase, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(7, 7, 7)
                        .addComponent(jbarra, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbtDimob, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 25, Short.MAX_VALUE)
                        .addComponent(cxTodos, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(264, 264, 264)
                        .addComponent(jLabel12)
                        .addGap(3, 3, 3)
                        .addComponent(jMes, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbtDirrf, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jfase, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jbarra, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jbtDimob)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 229, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jbtDirrf)
                    .addComponent(cxTodos)
                    .addComponent(jMes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jbtDimobActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtDimobActionPerformed
        new SimpleThread().start();
    }//GEN-LAST:event_jbtDimobActionPerformed

    private void jbtDirrfActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtDirrfActionPerformed
        new DirfThread().start();
    }//GEN-LAST:event_jbtDirrfActionPerformed

    private void cxTodosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cxTodosActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cxTodosActionPerformed

    private void jAnoStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jAnoStateChanged
        new ListaLoca().start();
    }//GEN-LAST:event_jAnoStateChanged

    private void ListaDirrf(String sMes, String sAno) {
        jbtDirrf.setEnabled(false);
        
        String _where = "", _where2 = "";
        if (!cxTodos.isSelected()) {
            _where = " and (";
            for (int c=0; c< aTable.getRowCount(); c++) {
                int modelRow = aTable.convertRowIndexToModel(c);
                if ("true".equals(aTable.getModel().getValueAt(modelRow, 2).toString().toLowerCase())) {
                    _where += "rgprp = '" + aTable.getModel().getValueAt(modelRow, 0) + "' or ";
                }
            }
            _where = _where.substring(0, _where.length() - 4) + ")";
            _where2 = _where.replace("rgprp", "ae.rgprp");
            //if (_where.trim().equalsIgnoreCase("a (")) { 
            //    _where += ")";
            //} else {
            //    _where = "";
            //}
        }   
        
        String trgprp, trgimv, tcontrato, trecebimento, tvencimento, trcaut;
        float tAL = 0, tCM = 0, tDC = 0, tDF = 0, tIR = 0;
        Object[][] dimob = {};
        
        //String sql = "SELECT * FROM imposto where InStr(campo,'01:1:') and Year(dtrecebimento) = '&1.' order by contrato, dtrecebimento;";
        String sql = "";
        if (sMes.equalsIgnoreCase("TODOS")) {
            sql = "SELECT a.* FROM auxiliar a where a.conta = 'REC' and InStr(a.campo,'01:1:') and Year(a.dtrecebimento) = '&1.'" + _where + 
                  "union " +
                  "SELECT ae.* FROM jgeral_excluidos.auxiliar ae where ae.conta = 'REC' and InStr(ae.campo,'01:1:') and Year(ae.dtrecebimento) = '&1.'" + _where2 + 
                  " order by 5, 8;";
            sql = FuncoesGlobais.Subst(sql, new String[] {sAno});
        } else {
            sql = "SELECT a.* FROM auxiliar a where a.conta = 'REC' and InStr(a.campo,'01:1:') and Month(a.dtrecebimento) = '&1.' and Year(a.dtrecebimento) = '&2.'" + _where + 
                  "unioun " +
                  "SELECT ae.* FROM jgeral_excluidos.auxiliar ae where ae.conta = 'REC' and InStr(ae.campo,'01:1:') and Month(ae.dtrecebimento) = '&1.' and Year(ae.dtrecebimento) = '&2.'" + _where2 +   
                  " order by 5, 8;";
            sql = FuncoesGlobais.Subst(sql, new String[] {sMes, sAno});
        }
        ResultSet rs = conn.AbrirTabela(sql, ResultSet.CONCUR_READ_ONLY);
        try {
            jbarra.setValue(0);
            jfase.setText("Fase 1/2");
            jfase.setBackground(Color.getHSBColor(251, 100, 100));
            jfase.setForeground(Color.white);
            
            int eof = DbMain.RecordCount(rs); int pos = 1;
            rs.beforeFirst();
            while (rs.next()) {
                int br = ((pos++ * 100) / eof) + 1;
                jbarra.setValue(br);
                
                try { trgprp = rs.getString("rgprp"); } catch (SQLException e) { trgprp = null; }
                try { trgimv = rs.getString("rgimv"); } catch (SQLException e) { trgimv = null; }
                try { tcontrato = rs.getString("contrato"); } catch (SQLException e) { tcontrato = null; }
                try { trecebimento = rs.getString("dtrecebimento"); } catch (SQLException e) { trecebimento = null; }
                try { tvencimento = rs.getString("dtvencimento"); } catch (SQLException e) { tvencimento = null; }
                try { trcaut = rs.getString("rc_aut"); } catch (SQLException e) { trcaut = null; }
                
                if (tcontrato != null || trecebimento != null) {
                    String tcampo;
                    try { tcampo = rs.getString("campo"); } catch (SQLException e) { tcampo = null; }
                    if (tcampo != null) {
                        String[] acampo = tcampo.split(";");
                        if (acampo.length > 0) {
                            float vAL = 0, vCM = 0, vDA = 0, vDI = 0, vFA = 0, vFI = 0;
                            float vMU = 0, vJU = 0, vCO = 0, vEP = 0;
                            for (int i=0; i<acampo.length; i++) {
                                if (acampo[i].substring(0,2).equalsIgnoreCase("01")) {
                                    String tacampo[] = acampo[i].split(":");
                                    
                                    // Aluguel
                                    vAL = LerValor.LerValor.FloatNumber(tacampo[2],2);

                                    // Comissão
                                    int pCM = FuncoesGlobais.IndexOf(tacampo, "CM");
                                    if ( pCM > -1) {
                                        try {vCM = LerValor.LerValor.FloatNumber(tacampo[pCM].substring(2, 12),2);} catch (Exception ex) {}
                                    }
                                    
                                    // Multa
                                    int pMU = FuncoesGlobais.IndexOf(tacampo, "MU");
                                    if ( pMU > -1) {
                                        try{vMU = LerValor.LerValor.FloatNumber(tacampo[pMU].substring(2, 12),2);} catch (Exception ex) {}
                                    }
                                    
                                    // Juros
                                    int pJU = FuncoesGlobais.IndexOf(tacampo, "JU");
                                    if ( pJU > -1) {
                                        try{vJU = LerValor.LerValor.FloatNumber(tacampo[pJU].substring(2, 12),2);} catch (Exception ex) {}
                                    }
                                    
                                    // Correção
                                    int pCO = FuncoesGlobais.IndexOf(tacampo, "CO");
                                    if ( pCO > -1) {
                                        try{vCO = LerValor.LerValor.FloatNumber(tacampo[pCO].substring(2, 12),2);} catch (Exception ex) {}
                                    }
                                    
                                    // Expediente
                                    int pEP = FuncoesGlobais.IndexOf(tacampo, "EP");
                                    if ( pEP > -1) {
                                        try{vEP = LerValor.LerValor.FloatNumber(tacampo[pEP].substring(2, 12),2);} catch (Exception ex) {}
                                    }
                                } else if (acampo[i].substring(0,2).equalsIgnoreCase("DC")) {
                                    String tacampo[] = acampo[i].split(":");
                                    
                                    // É de aluguel
                                    int bAL = FuncoesGlobais.IndexOf(tacampo, "AL");
                                    
                                    // IR - IMPOSTO DE RENDA
                                    int bIR = FuncoesGlobais.IndexOf(tacampo, "DS7382");
                                    if (bIR > -1 && bAL == -1) {
                                        vDI = LerValor.LerValor.FloatNumber(tacampo[2],2);
                                    }
                                    
                                    if (bIR == -1 && bAL > -1) {
                                        vDA = LerValor.LerValor.FloatNumber(tacampo[2],2);
                                    }
                                } else if (acampo[i].substring(0,2).equalsIgnoreCase("DF")) {
                                    String tacampo[] = acampo[i].split(":");
                                    
                                    // É de aluguel
                                    int bAL = FuncoesGlobais.IndexOf(tacampo, "AL");
                                    
                                    // IR - IMPOSTO DE RENDA
                                    int bIR = FuncoesGlobais.IndexOf(tacampo, "DS7382");
                                    if (bIR > -1 && bAL == -1) {
                                        vFI = LerValor.LerValor.FloatNumber(tacampo[2],2);
                                    }
                                    
                                    if (bIR == -1 && bAL > -1) {
                                        vFA = LerValor.LerValor.FloatNumber(tacampo[2],2);
                                    }                                    
                                }
                            }
                            
//                            // Pega parte da ADM
//                            String asql = "SELECT * FROM auxiliar where conta = 'ADM' and Year(dtrecebimento) = '&1.' and contrato = '&2.' and dtvencimento = '&3.' and rc_aut = '&4.' order by contrato, dtrecebimento;";
//                            asql = FuncoesGlobais.Subst(asql, new String[] {sAno, tcontrato, tvencimento, trcaut});
//                            ResultSet ars = conn.AbrirTabela(asql, ResultSet.CONCUR_READ_ONLY);
//                            try {
//                                ars.beforeFirst();
//                                while (ars.next()) {
//                                    String tacampo;
//                                    try { tacampo = ars.getString("campo"); } catch (SQLException e) { tacampo = null; }
//                                    if (tacampo != null) {
//                                        String[] aacampo = tacampo.split(";");
//                                        if (aacampo.length > 0) {
//                                            for (int i=0; i<aacampo.length; i++) {
//                                                String atacampo[] = aacampo[i].split(":");
//
//                                                // Multa
//                                                int pMU = FuncoesGlobais.IndexOf(atacampo, "MU");
//                                                if ( pMU > -1) {
//                                                    vMU += LerValor.LerValor.FloatNumber(atacampo[pMU].substring(2, 12),2);
//                                                }
//
//                                                // Juros
//                                                int pJU = FuncoesGlobais.IndexOf(atacampo, "JU");
//                                                if ( pJU > -1) {
//                                                    vJU += LerValor.LerValor.FloatNumber(atacampo[pJU].substring(2, 12),2);
//                                                }
//
//                                                // Correção
//                                                int pCO = FuncoesGlobais.IndexOf(atacampo, "CO");
//                                                if ( pCO > -1) {
//                                                    vCO += LerValor.LerValor.FloatNumber(atacampo[pCO].substring(2, 12),2);
//                                                }
//
//                                                // Expediente
//                                                int pEP = FuncoesGlobais.IndexOf(atacampo, "EP");
//                                                if ( pEP > -1) {
//                                                    vEP += LerValor.LerValor.FloatNumber(atacampo[pEP].substring(2, 12),2);
//                                                }
//                                            }
//                                        }
//                                    }
//                                }
//                            } catch (Exception e) {}
//                            DbMain.FecharTabela(ars);
                            
                            int dpos = -1;
                            try {dpos = FuncoesGlobais.FindNinObjects(dimob, new int[] {0,1,2}, new String[] {trgprp,trgimv,tcontrato});} catch (Exception e) {dpos = -1;}
                            if (dpos == -1) {
                                dimob = FuncoesGlobais.ObjectsAdd(dimob,
                                        new Object[] {
                                        trgprp,
                                        trgimv,
                                        tcontrato,
                                            new Object[][] {
                                                {"01", (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0},
                                                {"02", (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0},
                                                {"03", (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0},
                                                {"04", (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0},
                                                {"05", (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0},
                                                {"06", (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0},
                                                {"07", (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0},
                                                {"08", (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0},
                                                {"09", (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0},
                                                {"10", (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0},
                                                {"11", (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0},
                                                {"12", (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0}                                               
                                            }
                                        });
                                Object[][] meses = (Object[][]) dimob[dimob.length - 1][3];
                                int mpos = FuncoesGlobais.FindinObjects(meses, 0, trecebimento.trim().substring(5, 7));
                                if (mpos > -1) {
                                    meses[mpos][1] = vAL;
                                    meses[mpos][2] = vCM;
                                    meses[mpos][3] = vDI;
                                    meses[mpos][4] = vMU;
                                    meses[mpos][5] = vJU;
                                    meses[mpos][6] = vCO;
                                    meses[mpos][7] = vEP;
                                    meses[mpos][8] = vDA;
                                    meses[mpos][9] = vFA;
                                }
                                dimob[dimob.length - 1][3] = meses;
                            } else {
                                Object[][] meses = (Object[][]) dimob[dpos][3];
                                int mpos = FuncoesGlobais.FindinObjects(meses, 0, trecebimento.trim().substring(5, 7));
                                if (mpos > -1) {
                                    meses[mpos][1] = Convert.toFloat(meses[mpos][1]) + vAL;
                                    meses[mpos][2] = Convert.toFloat(meses[mpos][2]) + vCM;
                                    meses[mpos][3] = Convert.toFloat(meses[mpos][3]) + vDI;
                                    meses[mpos][4] = Convert.toFloat(meses[mpos][4]) + vMU;
                                    meses[mpos][5] = Convert.toFloat(meses[mpos][5]) + vJU;
                                    meses[mpos][6] = Convert.toFloat(meses[mpos][6]) + vCO;
                                    meses[mpos][7] = Convert.toFloat(meses[mpos][7]) + vEP;
                                    meses[mpos][8] = Convert.toFloat(meses[mpos][8]) + vDA;
                                    meses[mpos][9] = Convert.toFloat(meses[mpos][9]) + vFA;
                                }
                                dimob[dpos][3] = meses;
                            }
                        }
                    }
                }
            }
        } catch (SQLException ex) {}
        DbMain.FecharTabela(rs);

        int eof = dimob.length; int pos = 1;
        jbarra.setValue(0);
        jfase.setText("Fase 2/2");
        jfase.setBackground(Color.getHSBColor(120, 100, 60));
        jfase.setForeground(Color.black);
        
        try {conn.ExecutarComando("DELETE FROM dimob;");} catch (Exception e) {e.printStackTrace();}
        
        boolean podeprint = false;
        for (int i=0;i<dimob.length;i++) {
            int br = ((pos++ * 100) / eof) + 1;
            jbarra.setValue(br);

            Object[][] dados_locador = null;
            Object[][] dados_locatario = null;
            Object[][] dados_imovel = null;
            Object[][] dados_carteira = null;
            Object[][] dados_auxiliar = null;
            
            //try {dados_locador = conn.LerCamposTabela(new String[] {"cpfcnpj","nome"}, "proprietarios", FuncoesGlobais.Subst("rgprp = '&1.'",new String[] {dimob[i][0].toString()}));} catch (Exception e) {}
            try {dados_locador = pegaDadosProprietario(dimob[i][0].toString(), new String[] {"cpfcnpj","nome"}); } catch (Exception e) {}
            if (dados_locador != null) {
                //try {dados_locatario = conn.LerCamposTabela(new String[] {"cpfcnpj","nomerazao","contrato"}, "locatarios", FuncoesGlobais.Subst("contrato = '&1.'",new String[] {dimob[i][2].toString()}));} catch (Exception e) {}                    
                try {dados_locatario = pegaDadosLocatarios(dimob[i][2].toString(), new String[] {"cpfcnpj","nomerazao","contrato"});} catch (Exception e) {}                    
                if (dados_locatario != null) {
                    //try {dados_imovel = conn.LerCamposTabela(new String[] {"tpurbrural","end","num","compl","cep","codcid","estado"}, "imoveis", FuncoesGlobais.Subst("rgimv = '&1.'",new String[] {dimob[i][1].toString()}));} catch (Exception e) {}
                    try {dados_imovel = pegaDadosImoveis(dimob[i][1].toString(), new String[] {"tpurbrural","end","num","compl","cep","codcid","estado"});} catch (Exception e) {}
                    if (dados_imovel != null) {
                        //try {dados_carteira = conn.LerCamposTabela(new String[] {"dtinicio"}, "CARTEIRA", FuncoesGlobais.Subst("contrato = '&1.'",new String[] {dimob[i][2].toString()}));} catch (Exception e) {}
                        try {dados_carteira = pegaDadosCarteira(dimob[i][2].toString(), new String[] {"dtinicio"});} catch (Exception e) {}
                        String dtInicio = "00000000";
                        if (dados_carteira != null) {
                            try {dtInicio = Dates.StringtoString(dados_carteira[0][3].toString(), "dd/MM/yyyy", "ddMMyyyy");} catch (Exception e) {}
                        } else {
                            //try {dados_auxiliar = conn.LerCamposTabela(new String[] {"campo"}, "auxiliar", FuncoesGlobais.Subst("conta = 'CAR' AND contrato = '&1.'",new String[] {dimob[i][2].toString()}));} catch (Exception e) {}
                            try {dados_auxiliar = pegaDadosAuxiliar(dimob[i][2].toString(),new String[] {"campo"});} catch (Exception e) {}
                            if (dados_auxiliar != null) {
                                String[] auxCampos = dados_auxiliar[0][3].toString().split(",");
                                try {dtInicio = Dates.StringtoString(auxCampos[1], "dd/MM/yyyy", "ddMMyyyy");} catch (Exception e) {}
                            }
                        }
                        String uSql = "INSERT INTO  dimob (rgprp, rgimv, contrato, " +
                        "cpfcnpjlocador, nomelocador, cpfcnpjlocatario, " +
                        "nomelocatario, numerocontrato, datacontrato, " +
                        "valorjan, comissaojan, impostojan, mujan, jujan, cojan, tejan, dfjan, dcjan, " +
                        "valorfev, comissaofev, impostofev, mufev, jufev, cofev, tefev, dffev, dcfev, " +
                        "valormar, comissaomar, impostomar, mumar, jumar, comar, temar, dfmar, dcmar, " +
                        "valorabr, comissaoabr, impostoabr, muabr, juabr, coabr, teabr, dfabr, dcabr, " +
                        "valormai, comissaomai, impostomai, mumai, jumai, comai, temai, dfmai, dcmai, " +
                        "valorjun, comissaojun, impostojun, mujun, jujun, cojun, tejun, dfjun, dcjun, " +
                        "valorjul, comissaojul, impostojul, mujul, jujul, cojul, tejul, dfjul, dcjul, " +
                        "valorago, comissaoago, impostoago, muago, juago, coago, teago, dfago, dcago, " +
                        "valorset, comissaoset, impostoset, muset, juset, coset, teset, dfset, dcset, " +
                        "valorout, comissaoout, impostoout, muout, juout, coout, teout, dfout, dcout, " +
                        "valornov, comissaonov, impostonov, munov, junov, conov, tenov, dfnov, dcnov, " +
                        "valordez, comissaodez, impostodez, mudez, judez, codez, tedez, dfdez, dcdez, " +
                        "tipoimovel, endimovel, cepimovel, codmunimovel, ufimovel) VALUES (" +
                         "'&1.', '&2.', '&3.', '&4.', '&5.', '&6.', " +
                          "'&7.', '&8.', '&9.','&10.','&11.','&12.','&13.','&14.','&15.'," +
                         "'&16.','&17.','&18.','&19.','&20.','&21.','&22.','&23.','&24.'," +
                         "'&25.','&26.','&27.','&28.','&29.','&30.','&31.','&32.','&33.'," + 
                         "'&34.','&35.','&36.','&37.','&38.','&39.','&40.','&41.','&42.'," + 
                         "'&43.','&44.','&45.','&46.','&47.','&48.','&49.','&50.','&51.'," +
                         "'&52.','&53.','&54.','&55.','&56.','&57.','&58.','&59.','&60.'," +
                         "'&61.','&62.','&63.','&64.','&65.','&66.','&67.','&68.','&69.'," +
                         "'&70.','&71.','&72.','&73.','&74.','&75.','&76.','&77.','&78.'," +
                         "'&79.','&80.','&81.','&82.','&83.','&84.','&85.','&86.','&87.'," +
                         "'&88.','&89.','&90.','&91.','&92.','&93.','&94.','&95.','&96.'," +
                         "'&97.','&98.','&99.','&100.','&101.','&102.','&103.','&104.','&105.'," +
                         "'&106.','&107.','&108.','&109.','&110.','&111.','&112.','&113.','&114.'," +
                         "'&115.','&116.','&117.','&118.','&119.','&120.','&121.','&122.')";

                        Object[][] meses = (Object[][]) dimob[i][3];
                        uSql = FuncoesGlobais.Subst(uSql, new String[] {
                            dimob[i][0].toString(), dimob[i][1].toString(), dimob[i][2].toString(),
                            FuncaoX(dados_locador[4][3].toString().replace(".", "").replace("-", "").replace("/", ""),14),
                            FuncaoX(dados_locador[6][3].toString(), 60).replace("'", "''"),
                            FuncaoX(dados_locatario[0][3].toString().replace(".", "").replace("-", "").replace("/", ""),14),
                            FuncaoX(dados_locatario[1][3].toString(), 60).replace("'", "''"),
                            FuncaoX(dados_locatario[2][3].toString(), 6),
                            dtInicio,

                            FuncaoR(meses[0][1]),
                            FuncaoR(meses[0][2]), 
                            FuncaoR(meses[0][3]),        
                            FuncaoR(meses[0][4]),
                            FuncaoR(meses[0][5]),
                            FuncaoR(meses[0][6]),
                            FuncaoR(meses[0][7]),
                            FuncaoR(meses[0][9]),
                            FuncaoR(meses[0][8]),

                            FuncaoR(meses[1][1]),
                            FuncaoR(meses[1][2]), 
                            FuncaoR(meses[1][3]),                               
                            FuncaoR(meses[1][4]),
                            FuncaoR(meses[1][5]),
                            FuncaoR(meses[1][6]),
                            FuncaoR(meses[1][7]),
                            FuncaoR(meses[1][9]),
                            FuncaoR(meses[1][8]),

                            FuncaoR(meses[2][1]),
                            FuncaoR(meses[2][2]), 
                            FuncaoR(meses[2][3]),                               
                            FuncaoR(meses[2][4]),
                            FuncaoR(meses[2][5]),
                            FuncaoR(meses[2][6]),
                            FuncaoR(meses[2][7]),
                            FuncaoR(meses[2][9]),
                            FuncaoR(meses[2][8]),

                            FuncaoR(meses[3][1]),
                            FuncaoR(meses[3][2]), 
                            FuncaoR(meses[3][3]),                               
                            FuncaoR(meses[3][4]),
                            FuncaoR(meses[3][5]),
                            FuncaoR(meses[3][6]),
                            FuncaoR(meses[3][7]),
                            FuncaoR(meses[3][9]),
                            FuncaoR(meses[3][8]),

                            FuncaoR(meses[4][1]),
                            FuncaoR(meses[4][2]), 
                            FuncaoR(meses[4][3]),                               
                            FuncaoR(meses[4][4]),
                            FuncaoR(meses[4][5]),
                            FuncaoR(meses[4][6]),
                            FuncaoR(meses[4][7]),
                            FuncaoR(meses[4][9]),
                            FuncaoR(meses[4][8]),

                            FuncaoR(meses[5][1]),
                            FuncaoR(meses[5][2]), 
                            FuncaoR(meses[5][3]),                               
                            FuncaoR(meses[5][4]),
                            FuncaoR(meses[5][5]),
                            FuncaoR(meses[5][6]),
                            FuncaoR(meses[5][7]),
                            FuncaoR(meses[5][9]),
                            FuncaoR(meses[5][8]),

                            FuncaoR(meses[6][1]),
                            FuncaoR(meses[6][2]), 
                            FuncaoR(meses[6][3]),                               
                            FuncaoR(meses[6][4]),
                            FuncaoR(meses[6][5]),
                            FuncaoR(meses[6][6]),
                            FuncaoR(meses[6][7]),
                            FuncaoR(meses[6][9]),
                            FuncaoR(meses[6][8]),

                            FuncaoR(meses[7][1]),
                            FuncaoR(meses[7][2]), 
                            FuncaoR(meses[7][3]),                               
                            FuncaoR(meses[7][4]),
                            FuncaoR(meses[7][5]),
                            FuncaoR(meses[7][6]),
                            FuncaoR(meses[7][7]),
                            FuncaoR(meses[7][9]),
                            FuncaoR(meses[7][8]),

                            FuncaoR(meses[8][1]),
                            FuncaoR(meses[8][2]), 
                            FuncaoR(meses[8][3]),                               
                            FuncaoR(meses[8][4]),
                            FuncaoR(meses[8][5]),
                            FuncaoR(meses[8][6]),
                            FuncaoR(meses[8][7]),
                            FuncaoR(meses[8][9]),
                            FuncaoR(meses[8][8]),

                            FuncaoR(meses[9][1]),
                            FuncaoR(meses[9][2]), 
                            FuncaoR(meses[9][3]),                               
                            FuncaoR(meses[9][4]),
                            FuncaoR(meses[9][5]),
                            FuncaoR(meses[9][6]),
                            FuncaoR(meses[9][7]),
                            FuncaoR(meses[9][9]),
                            FuncaoR(meses[9][8]),

                            FuncaoR(meses[10][1]),
                            FuncaoR(meses[10][2]), 
                            FuncaoR(meses[10][3]),                               
                            FuncaoR(meses[10][4]),
                            FuncaoR(meses[10][5]),
                            FuncaoR(meses[10][6]),
                            FuncaoR(meses[10][7]),
                            FuncaoR(meses[10][9]),
                            FuncaoR(meses[10][8]),

                            FuncaoR(meses[11][1]),
                            FuncaoR(meses[11][2]), 
                            FuncaoR(meses[11][3]),                               
                            FuncaoR(meses[11][4]),
                            FuncaoR(meses[11][5]),
                            FuncaoR(meses[11][6]),
                            FuncaoR(meses[11][7]),
                            FuncaoR(meses[11][9]),
                            FuncaoR(meses[11][8]),

                            FuncaoX(dados_imovel[0][3].toString(), 1),
                            FuncaoX(dados_imovel[1][3] + "," + dados_imovel[2][3] + " " + dados_imovel[3][3], 60).replace("'", "''"),
                            FuncaoN(dados_imovel[4][3].toString().replace("-", ""), 8),
                            FuncaoN(dados_imovel[5][3].toString(), 4),
                            FuncaoX(dados_imovel[6][3].toString(), 2)
                        });

                        try {conn.ExecutarComando(uSql);} catch (Exception e) {e.printStackTrace();}
                        podeprint = true;
                    }
                }
            }
        }
        
        if (podeprint) {
            try {
                Map parametros = new HashMap();
                parametros.put("anobase", "AnoBase: " + FuncaoN(sAno,4));

                String fileName = "reports/rDirrf2.jasper";
                JasperPrint print = JasperFillManager.fillReport(fileName, parametros, conn.conn);

                // Create a PDF exporter
                JRExporter exporter = new JRPdfExporter();

                new jDirectory("reports/Relatorios/" + Dates.iYear(new Date()) + "/" + Dates.Month(new Date()) + "/");
                String pathName = "reports/Relatorios/" + Dates.iYear(new Date()) + "/" + Dates.Month(new Date()) + "/";

                // Configure the exporter (set output file name and print object)
                String outFileName = pathName + "Dirrf2_" + Dates.DateFormata("ddMMyyyy", new Date()) + ".pdf";
                exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, outFileName);
                exporter.setParameter(JRExporterParameter.JASPER_PRINT, print);

                // Export the PDF file
                exporter.exportReport();

                new toPreview(outFileName);
            } catch (JRException e) {
                e.printStackTrace();
                System.exit(1);
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }                    
        }
       jbtDirrf.setEnabled(true);        
    }
    
    private void ListaContratos(String sAno) {
        jbtDimob.setEnabled(false);
        
        String trgprp, trgimv, tcontrato, trecebimento, tvencimento, trcaut;
        float tAL = 0, tCM = 0, tDC = 0, tDF = 0, tIR = 0;
        Object[][] dimob = {};
        
        //String sql = "SELECT * FROM auxiliar where conta = 'REC' and InStr(campo,'01:1:') and Year(dtrecebimento) = '&1.' order by contrato, dtrecebimento;";
        String sql = "SELECT * FROM auxiliar where conta = 'REC' and InStr(campo,'01:1:') and Year(dtrecebimento) = '&1.' " +
                     "union " +
                     "SELECT * FROM jgeral_excluidos.auxiliar where conta = 'REC' and InStr(campo,'01:1:') and Year(dtrecebimento) = '&2.' " +
                     "order by 5, 8;";
        sql = FuncoesGlobais.Subst(sql, new String[] {sAno, sAno});
        ResultSet rs = conn.AbrirTabela(sql, ResultSet.CONCUR_READ_ONLY);
        try {
            jbarra.setValue(0);
            jfase.setText("Fase 1/2");
            jfase.setBackground(Color.getHSBColor(251, 100, 100));
            jfase.setForeground(Color.white);
            
            int eof = DbMain.RecordCount(rs); int pos = 1;
            rs.beforeFirst();
            while (rs.next()) {
                int br = ((pos++ * 100) / eof) + 1;
                jbarra.setValue(br);
                
                try { trgprp = rs.getString("rgprp"); } catch (SQLException e) { trgprp = null; }
                try { trgimv = rs.getString("rgimv"); } catch (SQLException e) { trgimv = null; }
                try { tcontrato = rs.getString("contrato"); } catch (SQLException e) { tcontrato = null; }
                try { trecebimento = rs.getString("dtrecebimento"); } catch (SQLException e) { trecebimento = null; }
                try { tvencimento = rs.getString("dtvencimento"); } catch (SQLException e) { tvencimento = null; }
                try { trcaut = rs.getString("rc_aut"); } catch (SQLException e) { trcaut = null; }
                
                if (tcontrato != null || trecebimento != null) {
                    String tcampo;
                    try { tcampo = rs.getString("campo"); } catch (SQLException e) { tcampo = null; }
                    if (tcampo != null) {
                        String[] acampo = tcampo.split(";");
                        if (acampo.length > 0) {
                            float vAL = 0, vCM = 0, vDA = 0, vDI = 0, vFA = 0, vFI = 0;
                            float vMU = 0, vJU = 0, vCO = 0, vEP = 0;
                            for (int i=0; i<acampo.length; i++) {
                                if (acampo[i].substring(0,2).equalsIgnoreCase("01")) {
                                    String tacampo[] = acampo[i].split(":");
                                    
                                    // Aluguel
                                    vAL = LerValor.LerValor.FloatNumber(tacampo[2],2);

                                    // Comissão
                                    int pCM = FuncoesGlobais.IndexOf(tacampo, "CM");
                                    if ( pCM > -1) {
                                        vCM = LerValor.LerValor.FloatNumber(tacampo[pCM].substring(2, 12),2);
                                    }
                                    
                                    // Multa
                                    int pMU = FuncoesGlobais.IndexOf(tacampo, "MU");
                                    if ( pMU > -1) {
                                        //System.out.println("Contrato:" + tcontrato + "  Vencto:" + tvencimento + "  pMU: " + pMU + "  Multa:" + tacampo[pMU]);
                                        String tMU = "";
                                        try { 
                                            tMU = tacampo[pMU].substring(2, 12); 
                                            vMU = LerValor.LerValor.FloatNumber(tMU,2);
                                        } catch (Exception e) {vMU = 0;}                                        
                                    }
                                    
                                    // Juros
                                    int pJU = FuncoesGlobais.IndexOf(tacampo, "JU");
                                    if ( pJU > -1) {
                                        String tJU = "";
                                        try {
                                           tJU = tacampo[pJU].substring(2, 12);
                                           vJU = LerValor.LerValor.FloatNumber(tJU,2);
                                        } catch (Exception e) {vJU = 0;}                                        
                                    }
                                    
                                    // Correção
                                    int pCO = FuncoesGlobais.IndexOf(tacampo, "CO");
                                    if ( pCO > -1) {
                                        String tCO = "";
                                        try {
                                            tCO = tacampo[pCO].substring(2, 12);
                                            vCO = LerValor.LerValor.FloatNumber(tCO,2);
                                        } catch (Exception e) {vCO = 0;}
                                    }
                                    
                                    // Expediente
                                    int pEP = FuncoesGlobais.IndexOf(tacampo, "EP");
                                    if ( pEP > -1) {
                                        String tEP = "";
                                        try {
                                            tEP = tacampo[pEP].substring(2, 12);
                                            vEP = LerValor.LerValor.FloatNumber(tEP,2);
                                        } catch (Exception e) {vEP =0;}                                        
                                    }
                                } else if (acampo[i].substring(0,2).equalsIgnoreCase("DC")) {
                                    String tacampo[] = acampo[i].split(":");
                                    
                                    // É de aluguel
                                    int bAL = FuncoesGlobais.IndexOf(tacampo, "AL");
                                    
                                    // IR - IMPOSTO DE RENDA
                                    int bIR = FuncoesGlobais.IndexOf(tacampo, "DS7382");
                                    if (bIR > -1 && bAL == -1) {
                                        vDI = LerValor.LerValor.FloatNumber(tacampo[2],2);
                                    }
                                    
                                    if (bAL > -1) {
                                        vDA = LerValor.LerValor.FloatNumber(tacampo[2],2);
                                    }
                                } else if (acampo[i].substring(0,2).equalsIgnoreCase("DF")) {
                                    String tacampo[] = acampo[i].split(":");
                                    
                                    // É de aluguel
                                    int bAL = FuncoesGlobais.IndexOf(tacampo, "AL");
                                    
                                    // IR - IMPOSTO DE RENDA
                                    int bIR = FuncoesGlobais.IndexOf(tacampo, "DS7382");
                                    if (bIR > -1 && bAL == -1) {
                                        vFI = LerValor.LerValor.FloatNumber(tacampo[2],2);
                                    }
                                    
                                    if (bAL > -1) {
                                        vFA = LerValor.LerValor.FloatNumber(tacampo[2],2);
                                    }                                    
                                }
                            }
                            
                            // Pega parte da ADM
                            float avMU = 0, avJU = 0, avCO = 0, avEP = 0;
                            //String asql = "SELECT * FROM auxiliar where conta = 'ADM' and Year(dtrecebimento) = '&1.' and contrato = '&2.' and dtvencimento = '&3.' and rc_aut = '&4.' order by contrato, dtrecebimento, Length(campo) DESC LIMIT 1;";
                            String asql = "SELECT * FROM auxiliar where conta = 'ADM' and Year(dtrecebimento) = '&1.' and contrato = '&2.' and dtvencimento = '&3.' and rc_aut = '&4.' " +
                                           "union " +
                                           "SELECT * FROM jgeral_excluidos.auxiliar where conta = 'ADM' and Year(dtrecebimento) = '&5.' and contrato = '&6.' and dtvencimento = '&7.' and rc_aut = '&8.' " +
                                           "order by 5, 8, Length(6) DESC LIMIT 1;";
                            asql = FuncoesGlobais.Subst(asql, new String[] {sAno, tcontrato, tvencimento, trcaut, sAno, tcontrato, tvencimento, trcaut});
                            ResultSet ars = conn.AbrirTabela(asql, ResultSet.CONCUR_READ_ONLY);
                            try {
                                ars.beforeFirst();
                                while (ars.next()) {
                                    String tacampo;
                                    try { tacampo = ars.getString("campo"); } catch (SQLException e) { tacampo = null; }
                                    if (tacampo != null) {
                                        String[] aacampo = tacampo.split(";");
                                        if (aacampo.length > 0) {
                                            for (int i=0; i<aacampo.length; i++) {
                                                String atacampo[] = aacampo[i].split(":");

                                                // Multa
                                                int pMU = FuncoesGlobais.IndexOf(atacampo, "MU");
                                                if ( pMU > -1) {
                                                    avMU += LerValor.LerValor.FloatNumber(atacampo[pMU].substring(2, 12),2);
                                                }

                                                // Juros
                                                int pJU = FuncoesGlobais.IndexOf(atacampo, "JU");
                                                if ( pJU > -1) {
                                                    avJU += LerValor.LerValor.FloatNumber(atacampo[pJU].substring(2, 12),2);
                                                }

                                                // Correção
                                                int pCO = FuncoesGlobais.IndexOf(atacampo, "CO");
                                                if ( pCO > -1) {
                                                    avCO += LerValor.LerValor.FloatNumber(atacampo[pCO].substring(2, 12),2);
                                                }

                                                // Expediente
                                                int pEP = FuncoesGlobais.IndexOf(atacampo, "EP");
                                                if ( pEP > -1) {
                                                    avEP += LerValor.LerValor.FloatNumber(atacampo[pEP].substring(2, 12),2);
                                                }
                                            }
                                        }
                                    }
                                }
                            } catch (Exception e) {}
                            DbMain.FecharTabela(ars);
                            
                            int dpos = -1;
                            try {dpos = FuncoesGlobais.FindNinObjects(dimob, new int[] {0,1,2}, new String[] {trgprp,trgimv,tcontrato});} catch (Exception e) {dpos = -1;}
                            if (dpos == -1) {
                                dimob = FuncoesGlobais.ObjectsAdd(dimob,
                                        new Object[] {
                                        trgprp,
                                        trgimv,
                                        tcontrato,
                                            new Object[][] {
                                                {"01", (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0},
                                                {"02", (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0},
                                                {"03", (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0},
                                                {"04", (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0},
                                                {"05", (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0},
                                                {"06", (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0},
                                                {"07", (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0},
                                                {"08", (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0},
                                                {"09", (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0},
                                                {"10", (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0},
                                                {"11", (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0},
                                                {"12", (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0, (float)0}                                               
                                            }
                                        });
                                Object[][] meses = (Object[][]) dimob[dimob.length - 1][3];
                                int mpos = FuncoesGlobais.FindinObjects(meses, 0, trecebimento.trim().substring(5, 7));
                                if (mpos > -1) {
                                    meses[mpos][1] = vAL;
                                    meses[mpos][2] = vCM;
                                    meses[mpos][3] = vDI;
                                    meses[mpos][4] = vMU;
                                    meses[mpos][5] = vJU;
                                    meses[mpos][6] = vCO;
                                    meses[mpos][7] = vEP;
                                    meses[mpos][8] = vDA;
                                    meses[mpos][9] = vFA;

                                    meses[mpos][10] = avMU;
                                    meses[mpos][11] = avJU;
                                    meses[mpos][12] = avCO;
                                    meses[mpos][13] = avEP;
                                    
                                }
                                dimob[dimob.length - 1][3] = meses;
                            } else {
                                Object[][] meses = (Object[][]) dimob[dpos][3];
                                int mpos = FuncoesGlobais.FindinObjects(meses, 0, trecebimento.trim().substring(5, 7));
                                if (mpos > -1) {
                                    meses[mpos][1] = Convert.toFloat(meses[mpos][1]) + vAL;
                                    meses[mpos][2] = Convert.toFloat(meses[mpos][2]) + vCM;
                                    meses[mpos][3] = Convert.toFloat(meses[mpos][3]) + vDI;
                                    meses[mpos][4] = Convert.toFloat(meses[mpos][4]) + vMU;
                                    meses[mpos][5] = Convert.toFloat(meses[mpos][5]) + vJU;
                                    meses[mpos][6] = Convert.toFloat(meses[mpos][6]) + vCO;
                                    meses[mpos][7] = Convert.toFloat(meses[mpos][7]) + vEP;
                                    meses[mpos][8] = Convert.toFloat(meses[mpos][8]) + vDA;
                                    meses[mpos][9] = Convert.toFloat(meses[mpos][9]) + vFA;
                                    
                                    meses[mpos][10] = Convert.toFloat(meses[mpos][10]) + avMU;
                                    meses[mpos][11] = Convert.toFloat(meses[mpos][11]) + avJU;
                                    meses[mpos][12] = Convert.toFloat(meses[mpos][12]) + avCO;
                                    meses[mpos][13] = Convert.toFloat(meses[mpos][13]) + avEP;
                                    
                                }
                                dimob[dpos][3] = meses;
                            }
                        }
                    }
                }
            }
        } catch (SQLException ex) {}
        DbMain.FecharTabela(rs);

        JFileChooser fc = new JFileChooser();
        fc.showSaveDialog(this);
        try {
            String filename = fc.getSelectedFile().getName();
            String pathname = fc.getSelectedFile().getPath();

            int eof = dimob.length; int pos = 1;
            jbarra.setValue(0);
            jfase.setText("Fase 2/2");
            jfase.setBackground(Color.getHSBColor(120, 100, 60));
            jfase.setForeground(Color.black);
            
            StreamFile file = new StreamFile(new String[] {pathname + ".txt"});
            if (file.Open()) {
                // Registro tipo Header
                file.Print(
                        "DIMOB" + 
                        FuncoesGlobais.Space(369) + 
                        "\r\n"
                );
                
                // Reristro tipo 01
                file.Print(
                        "R01" +
                        jCnpj.getText().replace(".", "").replace("-", "").replace("/", "") +
                        FuncaoN(sAno,4) +
                        (jRetifica.isSelected() ? "1" : "0") +
                        FuncaoN(jRecibo.getText(), 10) +
                        (jespecial.isSelected() ? "1" : "0") +
                        (jdtevento.getText().trim().length() != 0 ? Dates.StringtoString(jdtevento.getText(), "dd/MM/yyyy", "ddMMyyyy") : "00000000") +
                        FuncaoN(jcdsituacao.getText(), 2) +
                        FuncaoX(jrazao.getText(), 60) +
                        jcpfresponsavel.getText().replace(".", "").replace("-", "").replace("/", "") + 
                        FuncaoX(jendereco.getText(), 120) +
                        FuncaoX(jestado.getText(), 2) +
                        FuncaoN(jcdmunicipio.getText(), 4) +
                        FuncoesGlobais.Space(20) +
                        FuncoesGlobais.Space(10) +
                        "\r\n"
                );
                
                for (int i=0;i<dimob.length;i++) {
                    int br = ((pos++ * 100) / eof) + 1;
                    jbarra.setValue(br);
                    
                    Object[][] dados_locador = null;
                    Object[][] dados_locatario = null;
                    Object[][] dados_imovel = null;
                    Object[][] dados_carteira = null;
                    Object[][] dados_auxiliar = null;
                    
                    //try {dados_locador = conn.LerCamposTabela(new String[] {"cpfcnpj","nome"}, "proprietarios", FuncoesGlobais.Subst("rgprp = '&1.'",new String[] {dimob[i][0].toString()}));} catch (Exception e) {}
                    try {dados_locador = pegaDadosProprietario(dimob[i][0].toString(), new String[] {"cpfcnpj","nome"}); } catch (Exception e) {}
                    if (dados_locador != null) {
                        //try {dados_locatario = conn.LerCamposTabela(new String[] {"cpfcnpj","nomerazao","contrato"}, "locatarios", FuncoesGlobais.Subst("contrato = '&1.'",new String[] {dimob[i][2].toString()}));} catch (Exception e) {}                    
                        try {dados_locatario = pegaDadosLocatarios(dimob[i][2].toString(), new String[] {"cpfcnpj","nomerazao","contrato"});} catch (Exception e) {}                    
                        if (dados_locatario != null) {
                            //try {dados_imovel = conn.LerCamposTabela(new String[] {"tpurbrural","end","num","compl","cep","codcid","estado"}, "imoveis", FuncoesGlobais.Subst("rgimv = '&1.'",new String[] {dimob[i][1].toString()}));} catch (Exception e) {}
                            try {dados_imovel = pegaDadosImoveis(dimob[i][1].toString(), new String[] {"tpurbrural","end","num","compl","cep","codcid","estado"});} catch (Exception e) {}
                            if (dados_imovel != null) {
                                //try {dados_carteira = conn.LerCamposTabela(new String[] {"dtinicio"}, "CARTEIRA", FuncoesGlobais.Subst("contrato = '&1.'",new String[] {dimob[i][2].toString()}));} catch (Exception e) {}
                                try {dados_carteira = pegaDadosCarteira(dimob[i][2].toString(), new String[] {"dtinicio"});} catch (Exception e) {}
                                String dtInicio = "01011998";
                                if (dados_carteira != null) {
                                    try {dtInicio = Dates.StringtoString(dados_carteira[0][3].toString(), "dd/MM/yyyy", "ddMMyyyy");} catch (Exception e) {}
                                }  else {
                                    //try {dados_auxiliar = conn.LerCamposTabela(new String[] {"campo"}, "auxiliar", FuncoesGlobais.Subst("conta = 'CAR' AND contrato = '&1.'",new String[] {dimob[i][2].toString()}));} catch (Exception e) {}
                                    try {dados_auxiliar = pegaDadosAuxiliar(dimob[i][2].toString(),new String[] {"campo"});} catch (Exception e) {}
                                    if (dados_auxiliar != null) {
                                        String[] auxCampos = dados_auxiliar[0][3].toString().split(",");
                                        try {dtInicio = Dates.StringtoString(auxCampos[1], "dd/MM/yyyy", "ddMMyyyy");} catch (Exception e) {}
                                    }
                                }
                                // REGISTRO TIPO R02
                                Object[][] meses = (Object[][]) dimob[i][3];
                                String cMeses = "";
                                //if (dimob[i][0].toString().equalsIgnoreCase("115800")) {
                                //    cMeses = "";
                                //}
                                
                                Boolean ePrinc = true;
                                String dados_divisao = null;
                                //try {dados_divisao = conn.LerCamposTabela(new String[] {"rgprp"}, "divisao", "rgimv = '" + dimob[i][1].toString() + "'")[0][3];} catch (Exception e) {}
                                try {dados_divisao = pegaDadosDivisao(dimob[i][1].toString(),new String[] {"rgprp"})[0][3].toString();} catch (Exception e) {}
                                if (dados_divisao != null) {
                                    ePrinc = dimob[i][0].toString().equalsIgnoreCase(dados_divisao);
                                } else ePrinc = false;
                                
                                for (int m=0; m<meses.length;m++) {
                                    cMeses += FuncaoR((float)meses[m][1] - (float)meses[m][8] + (float)meses[m][9]) + 
                                              FuncaoR(meses[m][2]) + 
                                              FuncaoR(meses[m][3]);
                                            
                                            /*
                                            FuncaoR(((Float)meses[m][1] + (float)meses[m][9] - (float)meses[m][8]) + (float)meses[m][10] + (float)meses[m][11] + (float)meses[m][12] + (ePrinc ? (float)meses[m][13] : 0f)) + 
                                              FuncaoR((float)meses[m][12] + (float)meses[m][4] + (float)meses[m][5] + (float)meses[m][6] + (ePrinc ? (float)meses[m][13] : 0f)) + 
                                              FuncaoR(meses[m][3]);
                                            */
                                }

                                String nomeProp = dados_locador[6][3].toString();                  
                                int xpos = nomeProp.indexOf(" - ");
                                if (xpos > -1) nomeProp = nomeProp.substring(0,xpos);
                                
                                String nomeLoca = dados_locatario[1][3].toString();
                                int lpos = nomeLoca.indexOf(" - ");
                                if (lpos > -1) nomeLoca = nomeLoca.substring(0,lpos);
                                
                                file.Print(
                                "R02" +
                                // Dados de cabeçário
                                jCnpj.getText().replace(".", "").replace("-", "").replace("/", "") +
                                FuncaoN(sAno,4) +
                                FuncaoN(String.valueOf(i + 1).trim().replace(".0", "").replace(".00", ""),5) +

                                FuncaoX(dados_locador[4][3].toString().replace(".", "").replace("-", "").replace("/", ""),14) +

                                        
                                FuncaoX(nomeProp, 60) +

                                FuncaoX(dados_locatario[0][3].toString().replace(".", "").replace("-", "").replace("/", ""),14) +

                                FuncaoX(nomeLoca, 60) +
                                FuncaoX(dados_locatario[2][3].toString(), 6) +
                                dtInicio +

                                cMeses +

                                // Dados do imóvel
                                FuncaoX(dados_imovel[0][3].toString(), 1) +
                                FuncaoX(dados_imovel[1][3] + "," + dados_imovel[2][3] + " " + dados_imovel[3][3], 60) +
                                FuncaoN(dados_imovel[4][3].toString().replace("-", ""), 8) +
                                FuncaoN(dados_imovel[5][3].toString(), 4) +
                                FuncoesGlobais.Space(20) +
                                FuncaoX(dados_imovel[6][3].toString(), 2) +
                                FuncoesGlobais.Space(10) +
                                "\r\n"
                                        );
                            }
                        }
                    }
                }
            }
            
            // Registro tipo T9
            file.Print(
                    "T9" +
                    FuncoesGlobais.Space(100) +
                    "\r\n"
            );
            
            file.Close();
        } catch (NullPointerException e) {e.printStackTrace();}
        jbtDimob.setEnabled(true);
    }
    
    private Object[][] pegaDadosProprietario(String rgprp, String[] campos) {
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
            }
        } catch (SQLException sqlEx) {}
        db.CloseTable(rs);
        return (retorno.length > 0 ? retorno : null);
    }
    
    private Object[][] pegaDadosImoveis(String rgimv, String[] campos) {
        Object[][] retorno = {};
        
        String selectSQL = "SELECT i.tpurbrural, i.`end` end, i.num, i.compl, " +
                           "i.cep, i.codcid, i.estado " +
                           "FROM imoveis i WHERE i.rgimv = :rgimv1 " +
                           "UNION SELECT ie.tpurbrural, ie.`end` end, ie.num, ie.compl, " +
                           "ie.cep, ie.codcid, ie.estado " +
                           "FROM jgeral_excluidos.imoveis ie " +
                           "WHERE ie.rgimv = :rgimv2 LIMIT 1;";
        ResultSet rs = db.OpenTable(selectSQL, new Object[][] {
            {"string", "rgimv1", rgimv},
            {"string", "rgimv2", rgimv}
        });
        
        try {                    
            String _tpurbrural = null, _end = null, _num = null, _compl = null;
            String _cep = null, _codcid = null, _estado = null;
            while (rs.next()) {
                try { _tpurbrural = rs.getString("tpurbrural"); } catch (SQLException ex) { _tpurbrural = null; }
                try { _end = rs.getString("end"); } catch (SQLException ex) { _end = null; }
                try { _num = rs.getString("num"); } catch (SQLException ex) { _num = null; }
                try { _compl = rs.getString("compl"); } catch (SQLException ex) { _compl = null; }
                try { _cep = rs.getString("cep"); } catch (SQLException ex) { _cep = null; }
                try { _codcid = rs.getString("codcid"); } catch (SQLException ex) { _codcid = null; }
                try { _estado = rs.getString("estado"); } catch (SQLException ex) { _estado = null; }
                
                Object[] tpurbrural = new Object[] {null, null, null, _tpurbrural};
                Object[] end = new Object[] {null, null, null, _end};
                Object[] num = new Object[] {null, null, null, _num};
                Object[] compl = new Object[] {null, null, null, _compl};
                Object[] cep = new Object[] {null, null, null, _cep};
                Object[] codcid = new Object[] {null, null, null, _codcid};
                Object[] estado = new Object[] {null, null, null, _estado};
                
                retorno = FuncoesGlobais.ObjectsAdd(retorno, tpurbrural);
                retorno = FuncoesGlobais.ObjectsAdd(retorno, end);
                retorno = FuncoesGlobais.ObjectsAdd(retorno, num);
                retorno = FuncoesGlobais.ObjectsAdd(retorno, compl);
                retorno = FuncoesGlobais.ObjectsAdd(retorno, cep);
                retorno = FuncoesGlobais.ObjectsAdd(retorno, codcid);
                retorno = FuncoesGlobais.ObjectsAdd(retorno, estado);
            }
        } catch (SQLException sqlEx) {}
        db.CloseTable(rs);
        return (retorno.length > 0 ? retorno : null);
    }
    
    private Object[][] pegaDadosCarteira(String contrato, String[] campos) {
        Object[][] retorno = {};
        
        String selectSQL = "select c.dtinicio from carteira c WHERE c.contrato = :contrato1 " +
                           "union " +
                           "select ce.dtinicio from jgeral_excluidos.carteira ce WHERE ce.contrato = :contrato2 " +
                           "LIMIT 1;";
        ResultSet rs = db.OpenTable(selectSQL, new Object[][] {
            {"string", "contrato1", contrato},
            {"string", "contrato2", contrato}
        });
        
        try {                    
            String _dtinicio = null;
            while (rs.next()) {
                try { _dtinicio = rs.getString("dtinicio"); } catch (SQLException ex) { _dtinicio = null; }
                
                Object[] dtinicio = new Object[] {null, null, null, _dtinicio};
                
                retorno = FuncoesGlobais.ObjectsAdd(retorno, dtinicio);
            }
        } catch (SQLException sqlEx) {}
        db.CloseTable(rs);
        return (retorno.length > 0 ? retorno : null);
    }
    
    private Object[][] pegaDadosLocatarios(String contrato, String[] campos) {
        Object[][] retorno = {};
        
        String selectSQL = "SELECT l.cpfcnpj, l.nomerazao, l.contrato FROM locatarios l " +
                           "WHERE l.contrato = :contrato1 UNION " +
                           "SELECT le.cpfcnpj, le.nomerazao, le.contrato FROM jgeral_excluidos.locatarios le " +
                           "WHERE le.contrato = :contrato2 LIMIT 1;";
        ResultSet rs = db.OpenTable(selectSQL, new Object[][] {
            {"string", "contrato1", contrato},
            {"string", "contrato2", contrato}
        });
        
        try {                    
            String _cpfcnpj = null, _nomerazao = null, _contrato = null;
            while (rs.next()) {
                try { _cpfcnpj = rs.getString("cpfcnpj"); } catch (SQLException ex) { _cpfcnpj = null; }
                try { _nomerazao = rs.getString("nomerazao"); } catch (SQLException ex) { _nomerazao = null; }
                try { _contrato = rs.getString("contrato"); } catch (SQLException ex) { _contrato = null; }
                
                Object[] cpfcnpj = new Object[] {null, null, null, _cpfcnpj};
                Object[] nomerazao = new Object[] {null, null, null, _nomerazao};
                Object[] tcontrato = new Object[] {null, null, null, _contrato};
                retorno = FuncoesGlobais.ObjectsAdd(retorno, cpfcnpj);
                retorno = FuncoesGlobais.ObjectsAdd(retorno, nomerazao);
                retorno = FuncoesGlobais.ObjectsAdd(retorno, tcontrato);
            }
        } catch (SQLException sqlEx) {}
        db.CloseTable(rs);
        return (retorno.length > 0 ? retorno : null);
    }

    private Object[][] pegaDadosAuxiliar(String contrato, String[] campos) {
        Object[][] retorno = {};
        
        String selectSQL = "select a.campo from auxiliar a WHERE a.conta = 'CAR' AND a.contrato = :contrato1 " +
                           "union " +
                           "select ae.campo from jgeral_excluidos.auxiliar ae WHERE ae.conta = 'CAR' AND ae.contrato = :contrato2 " +
                           "LIMIT 1;";
        ResultSet rs = db.OpenTable(selectSQL, new Object[][] {
            {"string", "contrato1", contrato},
            {"string", "contrato2", contrato}
        });
        
        try {                    
            String _campo = null;
            while (rs.next()) {
                try { _campo = rs.getString("campo"); } catch (SQLException ex) { _campo = null; }
                
                Object[] campo = new Object[] {null, null, null, _campo};
                
                retorno = FuncoesGlobais.ObjectsAdd(retorno, campo);
            }
        } catch (SQLException sqlEx) {}
        db.CloseTable(rs);
        return (retorno.length > 0 ? retorno : null);
    }
    
    private Object[][] pegaDadosDivisao(String rgimv, String[] campos) {
        Object[][] retorno = {};
        
        String selectSQL = "select d.rgprp from divisao d WHERE d.rgimv = :rgimv1 " +
                           "union " +
                           "select de.rgprp from jgeral_excluidos.divisao de WHERE de.rgimv = :rgimv2 " +
                           "LIMIT 1;";
        ResultSet rs = db.OpenTable(selectSQL, new Object[][] {
            {"string", "rgimv1", rgimv},
            {"string", "rgimv2", rgimv}
        });
        
        try {                    
            String _rgprp = null;
            while (rs.next()) {
                try { _rgprp = rs.getString("rgprp"); } catch (SQLException ex) { _rgprp = null; }
                
                Object[] rgprp = new Object[] {null, null, null, _rgprp};
                
                retorno = FuncoesGlobais.ObjectsAdd(retorno, rgprp);
            }
        } catch (SQLException sqlEx) {}
        db.CloseTable(rs);
        return (retorno.length > 0 ? retorno : null);
    }
    
    private String FuncaoN(String Value, int tam) {
        if (Value == null) return FuncoesGlobais.Repete("0", tam);
        return FuncoesGlobais.StrZero(Value, tam);
    }
    
    private String FuncaoX(String Value, int tam) {
        if (Value == null) return FuncoesGlobais.Space(tam);
        return FuncoesGlobais.myLetra(new Pad(Value, tam).RPad());
    }
    
    private String FuncaoR(Object Value) {
        String zeros = FuncoesGlobais.Repete("0", 12);
        String numero = LerValor.LerValor.FloatToString(Convert.toFloat(Value)).trim();
        int virgula = numero.indexOf(",");
        String parte1 = numero.substring(0,virgula);
        String parte2 = numero.substring(virgula + 1);
        if (parte2.length() == 1) parte2 += "0"; if(parte2.length() == 0) parte2 += "00";
        String rvalor1 = zeros + parte1;
        String rvalor2 =  parte2 + "00";
        
        //String rvalor = StringManager.Right(rvalor1, 12) + StringManager.Right(rvalor2, 2);
        
        String rvalor = rvalor1.substring(rvalor1.length() - 12,rvalor1.length()) + rvalor2.substring(0, 2);
        return rvalor;
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable aTable;
    private javax.swing.JCheckBox cxTodos;
    private javax.swing.JSpinner jAno;
    private javax.swing.JFormattedTextField jCnpj;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JSpinner jMes;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JFormattedTextField jRecibo;
    private javax.swing.JCheckBox jRetifica;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JProgressBar jbarra;
    private javax.swing.JButton jbtDimob;
    private javax.swing.JButton jbtDirrf;
    private javax.swing.JFormattedTextField jcdmunicipio;
    private javax.swing.JFormattedTextField jcdsituacao;
    private javax.swing.JFormattedTextField jcpfresponsavel;
    private javax.swing.JFormattedTextField jdtevento;
    private javax.swing.JTextField jendereco;
    private javax.swing.JCheckBox jespecial;
    private javax.swing.JTextField jestado;
    private javax.swing.JLabel jfase;
    private javax.swing.JTextField jrazao;
    // End of variables declaration//GEN-END:variables
}
