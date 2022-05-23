/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * jImpBoletas.java
 *
 * Created on 01/04/2011, 15:00:54
 */

package Movimento;

import Funcoes.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.PatternSyntaxException;
import javax.swing.JOptionPane;
import javax.swing.RowFilter;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author supervisor
 */
public class jGeracao extends javax.swing.JInternalFrame {
    DbMain conn = VariaveisGlobais.conexao;
    final TableRowSorter<TableModel> sorter;

    /** Creates new form jImpBoletas */
    public jGeracao() throws SQLException {
        initComponents();

        //conn.CriarMySqlProcedures("ALL", "");
        
        jProgress.setVisible(false);

        // Seta Cabecario
        TableControl.header(jLista, new String[][] {{"rgprp","rgimv","contrato","nome","vencimento"},{"0","0","120","500","100"}});

        EmDia();
        
        if (VariaveisGlobais.gerMulSelect) {
            jLista.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        } else {
            jLista.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        }

        sorter = new TableRowSorter<TableModel>(jLista.getModel());
        jLista.setRowSorter(sorter);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        gCondicao = new javax.swing.ButtonGroup();
        jTipoEmail = new javax.swing.ButtonGroup();
        jGerar = new javax.swing.JButton();
        jSelectAll = new javax.swing.JButton();
        jProgress = new javax.swing.JProgressBar();
        jScrollPane1 = new javax.swing.JScrollPane();
        jLista = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jBusca = new javax.swing.JTextField();

        setClosable(true);
        setIconifiable(true);
        setTitle(".:: Geração de Vencimentos ::.");
        setVisible(true);

        jGerar.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        jGerar.setText("Gerar Vencimentos");
        jGerar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jGerarActionPerformed(evt);
            }
        });

        jSelectAll.setText("Selecionar Todos");
        jSelectAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jSelectAllActionPerformed(evt);
            }
        });

        jProgress.setFont(new java.awt.Font("Tahoma", 0, 8)); // NOI18N

        jLista.setAutoCreateRowSorter(true);
        jLista.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jLista.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        jScrollPane1.setViewportView(jLista);

        jLabel1.setText("Buscar:");

        jBusca.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBuscaActionPerformed(evt);
            }
        });
        jBusca.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jBuscaKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 763, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jGerar, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jProgress, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jBusca, javax.swing.GroupLayout.DEFAULT_SIZE, 292, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSelectAll)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jGerar, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE)
                    .addComponent(jProgress, javax.swing.GroupLayout.DEFAULT_SIZE, 28, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jBusca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jSelectAll, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel1)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jGerarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jGerarActionPerformed
        jGerar.setEnabled(false);
        int[] selRow = jLista.getSelectedRows();

        if (selRow.length != 0) {
            Object[] options = { "Sim", "Não" };
            int n = JOptionPane.showOptionDialog(null,
                    "Deseja gerar o(s) Vencimentos Agora ? ",
                    "Atenção", JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
            if (n == JOptionPane.YES_OPTION) {
                String sLst = "";
                String rgprp;
                String rgimv;
                String contrato = null;
                String vencto = null;

                String[] aLst = {};
                for (int i=0;i<=selRow.length - 1;i++) {
                    int nRow = selRow[i];
                    int modelRow = jLista.convertRowIndexToModel(nRow);
                    if (!jLista.getModel().getValueAt(modelRow, 4).toString().substring(0, 1).equalsIgnoreCase("B")) {
                        rgimv = jLista.getModel().getValueAt(modelRow, 1).toString();
                        aLst = FuncoesGlobais.ArrayAdd(aLst, "c.rgimv = '" + rgimv.trim() + "'");
                    }
                }

                if (aLst.length > 0) {sLst = FuncoesGlobais.join(aLst, " OR ");}

                if (aLst.length != 0) {
                    conn.CriarMySqlProcedures("GERAMOVTO", sLst);
                    conn.ExecutarComando("CALL GeraMovto()");

                    try {
                        //Auditor
                        conn.Auditor("GERACAO", sLst.substring(0, 255));
                    } catch (Exception e) {}
                    EmDia();
                }
            }
        } else {
            JOptionPane.showInternalMessageDialog(null, "Você deve selecionar primeiro!!!");
        }
        jGerar.setEnabled(true);
    }//GEN-LAST:event_jGerarActionPerformed

    private void jSelectAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jSelectAllActionPerformed
        jLista.selectAll();
    }//GEN-LAST:event_jSelectAllActionPerformed

    private void jBuscaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jBuscaKeyReleased
        if ("".equals(jBusca.getText().trim())) {
            sorter.setRowFilter(null);
        } else {
            try {
                sorter.setRowFilter(
                       RowFilter.regexFilter(jBusca.getText().trim()));
            } catch (PatternSyntaxException pse) {
                   System.err.println("Bad regex pattern");
            }
        }
    }//GEN-LAST:event_jBuscaKeyReleased

    private void jBuscaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBuscaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jBuscaActionPerformed

    public void EmDia() {
        String Sql = "select AtUnUpgrade(c.campo) AS upg, c.rgprp, c.rgimv, c.contrato, l.nomerazao, c.dtvencimento, l.boleta, (CountStr(c.campo,'*') = CountStr(c.campo,';') + 1) asupg from CARTEIRA c, locatarios l where c.contrato = l.contrato AND NOT IsNull(c.dtvencimento) AND (l.fiador1uf <> 'X' OR IsNull(l.fiador1uf)) order by l.nomerazao;";
                //"SELECT r.rgprp, r.rgimv, r.contrato, l.nomerazao, r.campo, r.dtvencimento, c.dtultrecebimento, l.boleta, 1 gerados FROM RECIBO r, locatarios l, CARTEIRA c where (r.tag <> 'X') AND (r.contrato = l.contrato and c.contrato = l.contrato) and (r.dtvencimento >= '" + Dates.DateFormata("yyyy-MM-dd", jInicial.getDate()) + "' AND r.dtvencimento <= '" + Dates.DateFormata("yyyy-MM-dd", jFinal.getDate()) + "') ORDER BY l.nomerazao;";
        ResultSet rs = conn.AbrirTabela(Sql, ResultSet.CONCUR_READ_ONLY);

        // Seta Cabecario
       TableControl.Clear(jLista);

        jProgress.setVisible(true);
        int b = 0;
        try {
            rs.last();
            int rcount = rs.getRow();
            rs.beforeFirst();
            while (rs.next()) {
                String trgprp = String.valueOf(rs.getInt("rgprp"));
                String trgimv = String.valueOf(rs.getInt("rgimv"));
                String tcontrato = rs.getString("contrato").toUpperCase();
                String tnome = rs.getString("nomerazao").trim();
                Boolean tcampo = rs.getBoolean("upg");
                String tvencto = "";
                try {
                    tvencto = (tcampo  ? "B " : "") + Dates.DateFormata("dd-MM-yyyy", Dates.StringtoDate(rs.getString("dtvencimento").toUpperCase(),"dd-MM-yyyy"));
                } catch (Exception ex) {
                    ex.printStackTrace();
                    System.out.println("P:" +trgprp + "\nL:" + trgimv + "\nC:" + tcontrato);
                    tvencto = null;
                }

                if (tvencto != null) {
                    TableControl.add(jLista, new String[][]{{trgprp, trgimv, tcontrato, tnome, tvencto},{"C","C","C","L","C"}}, true);
                }
                int pgs = ((b++ * 100) / rcount) + 1;

                jProgress.setValue(pgs);
            }
        } catch (SQLException ex) {}
        jProgress.setVisible(false);
        DbMain.FecharTabela(rs);

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup gCondicao;
    private javax.swing.JTextField jBusca;
    private javax.swing.JButton jGerar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JTable jLista;
    private javax.swing.JProgressBar jProgress;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton jSelectAll;
    private javax.swing.ButtonGroup jTipoEmail;
    // End of variables declaration//GEN-END:variables

}
