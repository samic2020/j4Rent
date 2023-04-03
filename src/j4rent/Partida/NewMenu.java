/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * jSuperMenu.java
 *
 * Created on 25/01/2011, 16:08:28
 */

package j4rent.Partida;
import Funcoes.BackGroundDeskTopPane;
import Funcoes.CentralizaTela;
import Funcoes.Dates;
import Funcoes.DbMain;
import Funcoes.FuncoesGlobais;
import Funcoes.VariaveisGlobais;
import Funcoes.ResizeImageIcon;
import Funcoes.TableControl;
import static Funcoes.gmail.GmailAPI.ReadJSon;
import com.jtattoo.plaf.mint.MintLookAndFeel;
import java.awt.AWTKeyStroke;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashSet;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Dimension;
import java.util.Properties;
import mondrian.rolap.Test;
import org.json.JSONException;
/**
 *
 * @author supervisor
 */
public final class NewMenu extends javax.swing.JFrame {
    String regras = null;
    DbMain conn = VariaveisGlobais.conexao;
    
    /** Creates new form jSuperMenu */
    public NewMenu() throws SQLException {
        initComponents();

        Properties props = new Properties();            
        props.put("logoString", "Samic");
        MintLookAndFeel.setCurrentTheme(props);
                
//        // Autenticador mensal do software
//        int contadias = 0; int limitedias = 0; Date ultacesso = null; 
//        Date Hoje = Dates.StringtoDate(Dates.DateFormata("dd-MM-yyyy", new Date()), "dd-MM-yyyy");
//        Verifica cnx = new Verifica("samic.com.br","root","1a2b3c","j4rent");
//        if (cnx != null) {
//            ultacesso = Dates.StringtoDate(conn.LerParametros("ULTACESSO"),"dd-MM-yyyy");
//            if (ultacesso != null) {
//                if (ultacesso.before(Hoje)) {
//                    // verifica se pode
//                    conn.GravarParametros(new String[] {"CONTADIAS","0","NUMERICO"});
//                    conn.GravarParametros(new String[] {"ULTACESSO",Dates.DateFormata("dd-MM-yyyy", new Date()),"TEXTO"});
//                } 
//            } else {
//                    conn.GravarParametros(new String[] {"CONTADIAS","0","NUMERICO"});
//                    conn.GravarParametros(new String[] {"ULTACESSO",Dates.DateFormata("dd-MM-yyyy", new Date()),"TEXTO"});
//            }        
//        } else {
//            ultacesso = Dates.StringtoDate(conn.LerParametros("ULTACESSO"),"dd-MM-yyyy");
//            if (ultacesso != null) {
//                if (ultacesso.before(Hoje)) {
//                    limitedias = Integer.valueOf(conn.LerParametros("LIMITEDIAS"));
//                    contadias = Integer.valueOf(conn.LerParametros("CONTADIAS"));
//                    contadias++;
//                    if (contadias > limitedias) System.exit(0);
//                }            
//                conn.GravarParametros(new String[] {"CONTADIAS",String.valueOf(contadias),"NUMERICO"});
//            }
//            conn.GravarParametros(new String[] {"ULTACESSO",Dates.DateFormata("dd-MM-yyyy", new Date()),"TEXTO"});
//        }
        
        InicializaVariaveis();
        try {
            // Leitura dos parametros gmail
            ReadJSon();
        } catch (JSONException ex) {
            ex.printStackTrace();
        }

        // Setar menu
        //SetMenu();
        super.setJMenuBar(montaMenu()); 
        
        /**
         * maximiza a janela
         */
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);

        /**
         * Icone do modulo
         */
        ImageIcon icone = new ImageIcon("login.gif");
        setIconImage(icone.getImage());

        //Centraliza a janela.
        Dimension dimension = this.getToolkit().getScreenSize();
        int x = (int) (dimension.getWidth() - this.getSize().getWidth() ) / 2;
        int y = (int) (dimension.getHeight() - this.getSize().getHeight()) / 2;
        this.setLocation(x,y);

        // Colocando enter para pular de campo
        HashSet conj = new HashSet(this.getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS));
        conj.add(AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_ENTER, 0));
        this.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, conj);

        VariaveisGlobais.jPanePrin = jDesktopPane1;

        ID();
        
        jDesktopPane1.requestFocus();
        jBuscaGlobalizada.setVisible(false);
    }

    private JMenuBar montaMenu() {
        //Cria a barra  
        JMenuBar barraMenu = new JMenuBar();  
        barraMenu.setBackground(new java.awt.Color(153, 153, 255));
        barraMenu.setMaximumSize(new java.awt.Dimension(146, 146));

        SetarMenu(barraMenu);
        
        //Cria o menu  
        JMenu menuSair = new JMenu("Sair");  
        
        // Aqui vão os itens
        JMenuItem itemSair = new javax.swing.JMenuItem();
        itemSair.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.CTRL_MASK));
        itemSair.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Figuras/stop.png"))); // NOI18N
        itemSair.setText("Logout");
        itemSair.setToolTipText("Logout do sistema...");
        itemSair.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dispose();
                try {
                    (new Main()).main(new String[]{""});
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        menuSair.add(itemSair);

        // Aqui vão os itens
        JMenuItem itemFechar = new javax.swing.JMenuItem();
        itemFechar.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.CTRL_MASK));
        itemFechar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Figuras/ok.png"))); // NOI18N
        itemFechar.setText("Encerrar o Sistema");
        itemFechar.setToolTipText("Sai completamente do programa...");
        itemFechar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                System.exit(0);
            }
        });
        menuSair.add(itemFechar);

        //Adiciona o menu na barra  
        barraMenu.add(menuSair);

        return barraMenu;
    }

    private boolean Setar(String[] _nodes, String _chave) {
        if (VariaveisGlobais.protocolomenu.trim().equalsIgnoreCase("")) return true;
        
        for (int z=0;z<_nodes.length;z++) {
            int pos = FuncoesGlobais.OcourCount(_nodes[z], ":", 2);
            String _node = _nodes[z].substring(0, pos);
            boolean _setar = (_chave.trim().equalsIgnoreCase(_node.trim()));
            if (_setar) {
                String[] _acesso = _nodes[z].split(":");
                if (_acesso[2].equalsIgnoreCase("1")) return true;
            }        
        }
        return false;
    }
    private void SetarMenu(JMenuBar barraMenu) {
        String protocolomenu = VariaveisGlobais.protocolomenu;
        String _tree = protocolomenu.replaceAll(";", ",");
        String[] _nodes = _tree.split(",");
        
        ResultSet mnu = conn.AbrirTabela("SELECT * FROM menu ORDER BY tipo,cod;", ResultSet.CONCUR_READ_ONLY);
        try {
            JMenu menuItem = null; JMenuItem item = null;
            while (mnu.next()) {
                int cod = mnu.getInt("cod");
                int tip = mnu.getInt("tipo");
                String nome = mnu.getString("nome");
                final String rotina = mnu.getString("rotina");
                String icone = mnu.getString("icone");
                final int _senha = mnu.getInt("senha");
                
                String _chave = tip + ":" + cod;
                
                if (cod == 0) {
                    //Cria o menu  
                    menuItem = new JMenu(nome);  
                    menuItem.setVisible(Setar(_nodes, _chave));
                } else {
                    // Adiciona o item ao menu
                    barraMenu.add(menuItem);
                    
                    // Aqui vão os itens
                    item = new javax.swing.JMenuItem();
                    //item.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.CTRL_MASK));
                    // Setar Icones
                    //ImageIcon ico = new ResizeImageIcon("I", "loca.png", 16, 16).getImg();
                    try{
                        item.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Figuras/" + icone ))); // NOI18N
                    } catch (Exception e) {}
                    item.setText(nome);
                    //item.setToolTipText("Sai completamente do programa...");
                    item.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                            Class classe = null;  
                            try {  
                                boolean pode = true;
                                if (_senha > 0) {
                                    jAutoriza oAut = new jAutoriza(null, true);
                                    oAut.setVisible(true);
                                    pode = oAut.pode;
                                }
                                
                                if (pode) {
                                    classe = Class.forName(rotina);  
                                    JInternalFrame frame = (JInternalFrame) classe.newInstance();  
                                    jDesktopPane1.add(frame);
                                    CentralizaTela.setCentro(frame, jDesktopPane1, 0, 0);

                                    jDesktopPane1.getDesktopManager().activateFrame(frame);
                                    frame.requestFocus();
                                    frame.setSelected(true);
                                    frame.setVisible(true);  
                                }
                            } catch (Exception ex) {  
                                ex.printStackTrace();  
                            }  
                        }
                    });
                    
                    item.setVisible(Setar(_nodes,_chave));
                    menuItem.add(item);
                }
            }
        } catch (Exception e) {e.printStackTrace();}
        DbMain.FecharTabela(mnu);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jDesktopPane1 = new BackGroundDeskTopPane((new File(VariaveisGlobais.myLogo).exists() ? (Object)VariaveisGlobais.myLogo : "/Figuras/login.jpg"));
        jBuscaGlobalizada = new javax.swing.JInternalFrame();
        jPanel2 = new javax.swing.JPanel();
        jrbProp = new javax.swing.JRadioButton();
        jrbLoca = new javax.swing.JRadioButton();
        jrbImoveis = new javax.swing.JRadioButton();
        jrbFiadores = new javax.swing.JRadioButton();
        jcbHeuristica = new javax.swing.JCheckBox();
        jInativos = new javax.swing.JCheckBox();
        jrbboletas = new javax.swing.JRadioButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jtbBuscas = new javax.swing.JTable();
        jStatus = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jBuscar = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jUsuario = new javax.swing.JLabel();
        jInicio = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jOS = new javax.swing.JLabel();
        jLocal = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jBaseDados = new javax.swing.JLabel();
        jtermica = new javax.swing.JLabel();
        jRelogio = new javax.swing.JLabel();
        jSeparator8 = new javax.swing.JSeparator();
        jIMG = new javax.swing.JLabel();
        jSeparator9 = new javax.swing.JSeparator();
        jSeparator10 = new javax.swing.JSeparator();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle(".:: j4Rent - Programa de Administração de Imobiliárias");
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
        });
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jDesktopPane1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        jBuscaGlobalizada.setClosable(true);
        jBuscaGlobalizada.setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
        jBuscaGlobalizada.setTitle(".:: Busca Globalizada");
        jBuscaGlobalizada.setVisible(true);
        jBuscaGlobalizada.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jBuscaGlobalizadaFocusLost(evt);
            }
        });

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Critérios da busca:"));
        jPanel2.setFont(new java.awt.Font("Tahoma", 0, 8)); // NOI18N

        buttonGroup1.add(jrbProp);
        jrbProp.setSelected(true);
        jrbProp.setText("Proprietários");
        jrbProp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jrbPropActionPerformed(evt);
            }
        });

        buttonGroup1.add(jrbLoca);
        jrbLoca.setText("Locatários");
        jrbLoca.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jrbLocaActionPerformed(evt);
            }
        });

        buttonGroup1.add(jrbImoveis);
        jrbImoveis.setText("Imóveis");
        jrbImoveis.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jrbImoveisActionPerformed(evt);
            }
        });

        buttonGroup1.add(jrbFiadores);
        jrbFiadores.setText("Fiadores");
        jrbFiadores.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jrbFiadoresActionPerformed(evt);
            }
        });

        jcbHeuristica.setSelected(true);
        jcbHeuristica.setText("Buscar em qualquer posição");
        jcbHeuristica.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcbHeuristicaActionPerformed(evt);
            }
        });

        jInativos.setText("Desativados");
        jInativos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jInativosActionPerformed(evt);
            }
        });

        buttonGroup1.add(jrbboletas);
        jrbboletas.setText("Boletas");
        jrbboletas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jrbboletasActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jInativos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jcbHeuristica, javax.swing.GroupLayout.PREFERRED_SIZE, 337, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jrbProp)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jrbLoca)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jrbImoveis)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jrbFiadores)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jrbboletas, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jcbHeuristica)
                    .addComponent(jInativos))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jrbProp)
                    .addComponent(jrbLoca)
                    .addComponent(jrbImoveis)
                    .addComponent(jrbFiadores)
                    .addComponent(jrbboletas)))
        );

        jtbBuscas.setAutoCreateRowSorter(true);
        jtbBuscas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jtbBuscas.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jtbBuscas.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jtbBuscas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jtbBuscasMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jtbBuscas);

        javax.swing.GroupLayout jBuscaGlobalizadaLayout = new javax.swing.GroupLayout(jBuscaGlobalizada.getContentPane());
        jBuscaGlobalizada.getContentPane().setLayout(jBuscaGlobalizadaLayout);
        jBuscaGlobalizadaLayout.setHorizontalGroup(
            jBuscaGlobalizadaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jBuscaGlobalizadaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jBuscaGlobalizadaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 536, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jBuscaGlobalizadaLayout.setVerticalGroup(
            jBuscaGlobalizadaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jBuscaGlobalizadaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 118, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6))
        );

        jDesktopPane1.add(jBuscaGlobalizada);
        jBuscaGlobalizada.setBounds(380, 40, 570, 250);

        jStatus.setBackground(new java.awt.Color(254, 254, 254));
        jStatus.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Figuras/img004.jpg"))); // NOI18N
        jLabel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel1MouseClicked(evt);
            }
        });

        jBuscar.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(40, 2, 204), 1, true));
        jBuscar.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jBuscarFocusGained(evt);
            }
        });
        jBuscar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jBuscarKeyReleased(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Ubuntu", 1, 12)); // NOI18N
        jLabel3.setText("Usuário:");

        jLabel4.setFont(new java.awt.Font("Ubuntu", 1, 12)); // NOI18N
        jLabel4.setText("Logado em:");

        jUsuario.setFont(new java.awt.Font("Ubuntu", 0, 8)); // NOI18N
        jUsuario.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jInicio.setFont(new java.awt.Font("Ubuntu", 0, 8)); // NOI18N
        jInicio.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel5.setFont(new java.awt.Font("Ubuntu", 1, 12)); // NOI18N
        jLabel5.setText("OS:");

        jLabel6.setFont(new java.awt.Font("Ubuntu", 1, 12)); // NOI18N
        jLabel6.setText("Local:");

        jOS.setFont(new java.awt.Font("Ubuntu", 0, 8)); // NOI18N
        jOS.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLocal.setFont(new java.awt.Font("Ubuntu", 0, 8)); // NOI18N
        jLocal.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel7.setFont(new java.awt.Font("Ubuntu", 1, 12)); // NOI18N
        jLabel7.setText("Impressoras:");

        jLabel8.setFont(new java.awt.Font("Ubuntu", 1, 12)); // NOI18N
        jLabel8.setText("BasedeDados");

        jBaseDados.setFont(new java.awt.Font("Ubuntu", 0, 8)); // NOI18N
        jBaseDados.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jtermica.setBackground(new java.awt.Color(255, 255, 255));
        jtermica.setFont(new java.awt.Font("Ubuntu", 0, 8)); // NOI18N
        jtermica.setForeground(new java.awt.Color(0, 153, 0));
        jtermica.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jtermica.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jtermicaMouseReleased(evt);
            }
        });

        jRelogio.setFont(new java.awt.Font("Ubuntu", 1, 24)); // NOI18N
        jRelogio.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jRelogio.setText("00:00.00");
        jRelogio.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jSeparator8.setOrientation(javax.swing.SwingConstants.VERTICAL);

        jSeparator9.setOrientation(javax.swing.SwingConstants.VERTICAL);

        jSeparator10.setOrientation(javax.swing.SwingConstants.VERTICAL);

        javax.swing.GroupLayout jStatusLayout = new javax.swing.GroupLayout(jStatus);
        jStatus.setLayout(jStatusLayout);
        jStatusLayout.setHorizontalGroup(
            jStatusLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jStatusLayout.createSequentialGroup()
                .addComponent(jIMG, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addComponent(jSeparator8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addGroup(jStatusLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jStatusLayout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jStatusLayout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jInicio, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jStatusLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jStatusLayout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                    .addGroup(jStatusLayout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addGap(21, 21, 21)))
                .addGroup(jStatusLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jOS, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLocal, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jStatusLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jStatusLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jBaseDados, javax.swing.GroupLayout.DEFAULT_SIZE, 95, Short.MAX_VALUE)
                    .addComponent(jtermica, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, 0)
                .addComponent(jSeparator9, javax.swing.GroupLayout.PREFERRED_SIZE, 6, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jRelogio)
                .addGap(0, 0, 0)
                .addComponent(jSeparator10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jBuscar, javax.swing.GroupLayout.DEFAULT_SIZE, 188, Short.MAX_VALUE)
                .addContainerGap())
        );
        jStatusLayout.setVerticalGroup(
            jStatusLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jStatusLayout.createSequentialGroup()
                .addGroup(jStatusLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jUsuario, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jOS, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jtermica, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(jStatusLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jInicio, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, 18, Short.MAX_VALUE)
                    .addComponent(jBaseDados, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLocal, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
            .addComponent(jSeparator8)
            .addComponent(jIMG, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jStatusLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jSeparator10, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(jBuscar)
            .addComponent(jSeparator9, javax.swing.GroupLayout.Alignment.TRAILING)
            .addComponent(jRelogio, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jDesktopPane1.add(jStatus);
        jStatus.setBounds(0, 0, 958, 38);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jDesktopPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 958, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jDesktopPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 869, Short.MAX_VALUE)
                .addGap(0, 3, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jrbPropActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jrbPropActionPerformed
        TableControl.header(jtbBuscas, new String[][] {{"rgprp", "nome", "telefone", "cpf/cnpj", "endereço de correspondência"},{"80","500","120","150","500"}});
        jBuscar.requestFocus();
        Buscar();
    }//GEN-LAST:event_jrbPropActionPerformed

    private void jrbLocaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jrbLocaActionPerformed
        TableControl.header(jtbBuscas, new String[][] {{"contrato", "rgimv", "cpf/cnpj", "nome", "endereço", "endereço de correspondência"},{"80","80","150","500","500","500"}});
        jBuscar.requestFocus();
        Buscar();
    }//GEN-LAST:event_jrbLocaActionPerformed

    private void Buscar() {
        String[] gbProp = {"p.rgprp", "p.nome", "p.tel", "p.cpfcnpj", "p.cor_end"};
        String[] gbLoca = {"l.contrato", "l.rgimv", "l.cpfcnpj", "l.nomerazao", "l.end", "l.cor_end"};
        String[] gbImvl = {"i.rgprp", "i.rgimv", "i.end", "i.num", "i.compl", "i.situacao", "i.matriculas"};
        String[] gbFiad = {"f.nomerazao", "f.contrato", "f.rgimv", "f.cpfcnpj"};
        //String[] gbBole = {"b.nnumero", "b.contrato", "b.nome","b.vencimento","b.valor"};
        String[] gbBole = {"r.nnumero", "r.contrato", "(SELECT l.nomerazao FROM locatarios l WHERE l.contrato = r.contrato) AS nomerazao", "r.dtvencimento", "r.tag"};
        String cSql = "";
        String sBusca = jBuscar.getText().trim().toLowerCase();

        if(sBusca.equals("")) {
            TableControl.Clear(jtbBuscas);
            return;
        } 
            
        if (jrbProp.isSelected()) {
            String sInativos = "";
            if (!jInativos.isSelected()) {
                sInativos = "(Lower(Trim(p.status)) = 'ativo') AND (";
                VariaveisGlobais.IProp = false;
            } else {
                sInativos = "(Lower(Trim(p.status)) <> 'ativo') AND (";
                VariaveisGlobais.IProp = true;
            }
            String jgbProp = FuncoesGlobais.join(gbProp, ",");
            cSql =  "SELECT " + jgbProp + " FROM proprietarios p WHERE " + sInativos + gbProp[0].toLowerCase() + (jcbHeuristica.isSelected() ? " LIKE '%" : " LIKE '") + sBusca + "%' OR " +
            gbProp[1].toLowerCase() + (jcbHeuristica.isSelected() ? " LIKE '%" : " LIKE '") + sBusca + "%' OR " + gbProp[2].toLowerCase() + (jcbHeuristica.isSelected() ? " LIKE '%" : " LIKE '") + sBusca + "%' OR " +
            gbProp[3].toLowerCase() + (jcbHeuristica.isSelected() ? " LIKE '%" : " LIKE '") + sBusca + "%' OR " + gbProp[4].toLowerCase() + (jcbHeuristica.isSelected() ? " LIKE '%" : " LIKE '") + sBusca + "%')";

            TableControl.header(jtbBuscas, new String[][] {{"rgprp", "nome", "telefone", "cpf/cnpj", "endereço de correspondência"},{"80","500","120","150","500"}});
            ResultSet pResult = conn.AbrirTabela(cSql, ResultSet.CONCUR_READ_ONLY);
            try {
                while (pResult.next()) {
                    String trgprp = String.valueOf(pResult.getInt("p.rgprp"));
                    String tnome = pResult.getString("p.nome").toUpperCase();

                    String ttel = "";                    
                    String[] telef = null;
                    try { telef = pResult.getString("p.tel").split(";"); } catch (Exception e) {}
                    if (telef != null) {
                        for (int w=0;w<telef.length;w++) {
                            if (!telef[w].trim().equalsIgnoreCase("")) {
                                String[] tmptel = telef[w].split(",");

                                String teltmp = tmptel[0];
                                if (tmptel.length > 1) teltmp += " * " + tmptel[1];
                                ttel += teltmp + " / ";
                            }
                        }
                        if (ttel.length() > 0) ttel = ttel.substring(0, ttel.length() -3);
                    }

                    String tcpfcnpj = pResult.getString("p.cpfcnpj");
                    String tend = pResult.getString("p.cor_end");
                    TableControl.add(jtbBuscas, new String[][]{{trgprp, tnome, ttel, tcpfcnpj, tend},{"C","L","C","C","L"}},true);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            DbMain.FecharTabela(pResult);

        } else if (jrbLoca.isSelected()) {
            String sInativos = "";
            if (!jInativos.isSelected()) {
                sInativos = "(l.fiador1uf <> 'X' OR IsNull(l.fiador1uf)) AND (";
                VariaveisGlobais.Iloca = false;
            } else {
                sInativos = "(l.fiador1uf = 'X') AND (";
                VariaveisGlobais.Iloca = true;
            }
            cSql = "SELECT " + FuncoesGlobais.join(gbLoca,",") + " FROM locatarios l WHERE " + sInativos + gbLoca[0].toLowerCase() + (jcbHeuristica.isSelected() ? " LIKE '%" : " LIKE '") + sBusca + "%' OR " +
            gbLoca[1].toLowerCase() + (jcbHeuristica.isSelected() ? " LIKE '%" : " LIKE '") + sBusca + "%' OR " + gbLoca[2].toLowerCase() + (jcbHeuristica.isSelected() ? " LIKE '%" : " LIKE '") + sBusca + "%' OR " +
            gbLoca[3].toLowerCase() + (jcbHeuristica.isSelected() ? " LIKE '%" : " LIKE '") + sBusca + "%' OR " + gbLoca[4].toLowerCase() + (jcbHeuristica.isSelected() ? " LIKE '%" : " LIKE '") + sBusca + "%' OR " +
            gbLoca[5].toLowerCase() + (jcbHeuristica.isSelected() ? " LIKE '%" : " LIKE '") + sBusca + "%')";

            TableControl.header(jtbBuscas, new String[][] {{"contrato", "rgimv", "cpf/cnpj", "nome", "endereço", "endereço de correspondência"},{"80","80","150","500","500","500"}});
            ResultSet pResult = this.conn.AbrirTabela(cSql, ResultSet.CONCUR_READ_ONLY);
            try {
                while (pResult.next()) {
                    String tcontrato = String.valueOf(pResult.getInt("l.contrato"));
                    String trgimv = String.valueOf(pResult.getInt("l.rgimv"));
                    String tcpfcnpj = pResult.getString("l.cpfcnpj");
                    String tnome = pResult.getString("l.nomerazao").toUpperCase();
                    String tend = pResult.getString("l.end").toUpperCase().trim();
                    String tendcor = pResult.getString("l.cor_end").toUpperCase().trim();
                    TableControl.add(jtbBuscas, new String[][]{{tcontrato, trgimv, tcpfcnpj, tnome, tend, tendcor},{"C","C","C","L","L","L"}},true);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            DbMain.FecharTabela(pResult);

        } else if (jrbImoveis.isSelected()) {
            String sInativos = "";
            cSql = "SELECT " + FuncoesGlobais.join(gbImvl,",") + " FROM imoveis i WHERE " + gbImvl[0].toLowerCase() + (jcbHeuristica.isSelected() ? " LIKE '%" : " LIKE '") + sBusca + "%' OR " +
            gbImvl[1].toLowerCase() + (jcbHeuristica.isSelected() ? " LIKE '%" : " LIKE '") + sBusca + "%' OR " + gbImvl[2].toLowerCase() + (jcbHeuristica.isSelected() ? " LIKE '%" : " LIKE '") + sBusca +
                    "%' OR " + gbImvl[6].toLowerCase() + (jcbHeuristica.isSelected() ? " LIKE '%" : " LIKE '") + sBusca + "%'";

            TableControl.header(jtbBuscas, new String[][] {{"rgprp", "rgimv", "endereço"},{"80","80","500"}});
            ResultSet pResult = this.conn.AbrirTabela(cSql, ResultSet.CONCUR_READ_ONLY);
            try {
                while (pResult.next()) {
                    String trgprp = String.valueOf(pResult.getInt("i.rgprp"));
                    String trgimv = String.valueOf(pResult.getInt("i.rgimv"));
                    String tend = pResult.getString("i.end").toUpperCase().trim() + ", " + pResult.getString("i.num").toUpperCase().trim() + " " + pResult.getString("i.compl").toUpperCase().trim();
                    String tsit = pResult.getString("i.situacao");
                    
                    if (jInativos.isSelected()) {
                        if (tsit.equalsIgnoreCase("DESATIVADO")) TableControl.add(jtbBuscas, new String[][]{{trgprp, trgimv, tend},{"C","C","L"}},true);
                    } else {
                        if (!tsit.equalsIgnoreCase("DESATIVADO")) TableControl.add(jtbBuscas, new String[][]{{trgprp, trgimv, tend},{"C","C","L"}},true);
                    }
                    
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            DbMain.FecharTabela(pResult);

        } else if (jrbFiadores.isSelected()) {
            cSql = "SELECT " + FuncoesGlobais.join(gbFiad,",") + " FROM fiadores f WHERE Lower(" + gbFiad[0].toLowerCase() + (jcbHeuristica.isSelected() ? ") LIKE '%" : ") LIKE '") + sBusca + "%' OR Lower(" +
            gbFiad[1].toLowerCase() + (jcbHeuristica.isSelected() ? ") LIKE '%" : ") LIKE '") + sBusca + "%' OR Lower(" + gbFiad[2].toLowerCase() + (jcbHeuristica.isSelected() ? ") LIKE '%" : ") LIKE '") + sBusca +
            "%' OR Lower(" + gbFiad[3].toLowerCase() + (jcbHeuristica.isSelected() ? ") LIKE '%" : ") LIKE '") + sBusca + "%'";

            TableControl.header(jtbBuscas, new String[][] {{"contrato", "rgimv", "nome", "cpf/cnpj"},{"80","80","500","150"}});
            ResultSet pResult = this.conn.AbrirTabela(cSql, ResultSet.CONCUR_READ_ONLY);
            try {
                while (pResult.next()) {
                    String tcontrato = String.valueOf(pResult.getInt("f.contrato"));
                    String trgimv = String.valueOf(pResult.getInt("f.rgimv"));
                    String tnome = pResult.getString("f.nomerazao").toUpperCase().trim();
                    String tcpfcnpj = pResult.getString("f.cpfcnpj").toUpperCase().trim();
                    TableControl.add(jtbBuscas, new String[][]{{tcontrato, trgimv, tnome, tcpfcnpj},{"C","C","L","C"}},true);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            DbMain.FecharTabela(pResult);
        } else if (jrbboletas.isSelected()) {        
            //cSql = "SELECT " + FuncoesGlobais.join(gbBole,",") + " FROM recibo r WHERE (r.contrato = l.contrato) AND Trim(r.nnumero) <> '' AND " + 
            //       "Lower(" + gbBole[0].toLowerCase() + (jcbHeuristica.isSelected() ? ") LIKE '%" : ") LIKE '") + sBusca + "%' OR Lower(" +
            //gbBole[1].toLowerCase() + (jcbHeuristica.isSelected() ? ") LIKE '%" : ") LIKE '") + sBusca + "%' ORDER BY r.dtvencimento DESC LIMIT 50;";

            cSql = "SELECT r.nnumero as nnumero, r.contrato as contrato, (SELECT l.nomerazao FROM locatarios l WHERE l.contrato = r.contrato) AS nome, " +
                   "r.dtvencimento as vencimento, r.tag as recebido FROM recibo r WHERE " +
                   "Trim(r.nnumero) <> '' AND " +
                   "Lower(r.nnumero) " + (jcbHeuristica.isSelected() ? " LIKE '%" : " LIKE '") + sBusca + "%' OR " +
                   "Lower(r.contrato) " + (jcbHeuristica.isSelected() ? " LIKE '%" : " LIKE '") + sBusca + "%' " +
                   "ORDER BY r.dtvencimento DESC LIMIT 50;";
            //cSql = "SELECT nnumero, contrato, nome, vencimento, valor FROM bloquetos WHERE nnumero LIKE '%" + sBusca + "%' ORDER BY vencimento DESC LIMIT 50;";
            TableControl.header(jtbBuscas, new String[][] {{"nnumero", "contrato", "nome", "vencimento","tag"},{"120","70","250","90","80"}});
            ResultSet pResult = this.conn.AbrirTabela(cSql, ResultSet.CONCUR_READ_ONLY);
            try {
                while (pResult.next()) {
                    String tnnumero = pResult.getString("nnumero");
                    String tcontrato = pResult.getString("contrato");
                    String tnome = pResult.getString("nome").toUpperCase().trim();
                    String tvecto = Dates.DateFormata("dd/MM/yyyy", pResult.getDate("vencimento"));
                    String tvalor = pResult.getString("recebido").toUpperCase().trim();
                    TableControl.add(jtbBuscas, new String[][]{{tnnumero, tcontrato, tnome, tvecto, tvalor},{"C","C","L","C","C"}},true);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            DbMain.FecharTabela(pResult);
       }
    }
    
    private void jrbImoveisActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jrbImoveisActionPerformed
        TableControl.header(jtbBuscas, new String[][] {{"rgprp", "rgimv", "endereço"},{"80","80","500"}});
        Buscar();
        jBuscar.requestFocus();
    }//GEN-LAST:event_jrbImoveisActionPerformed

    private void jrbFiadoresActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jrbFiadoresActionPerformed
        TableControl.header(jtbBuscas, new String[][] {{"contrato", "rgimv", "nome", "cpf/cnpj"},{"80","80","500","150"}});
        Buscar();
        jBuscar.requestFocus();
    }//GEN-LAST:event_jrbFiadoresActionPerformed
    
    private void jtbBuscasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jtbBuscasMouseClicked
        if (evt.getClickCount() == 2) {
            int selRow = jtbBuscas.getSelectedRow();
            Object valor = null;
            if (selRow > -1) valor = jtbBuscas.getModel().getValueAt(selRow, 0);
            String _class = ""; String _method = ""; String[] _args = {};
            if (jrbProp.isSelected() || jrbImoveis.isSelected()) {
                _class = "j4rent.jProprietarios";
                _method = "MoveToProp";
                _args = new String[] {"rgprp", valor.toString()};
            } else if (jrbLoca.isSelected() || jrbFiadores.isSelected()) {
                _class = "j4rent.Locatarios.jLocatarios";
                _method = "MoveToLoca";
                _args = new String[] {"contrato", valor.toString()};
            }
            try {
                Class classe = null;
                classe = Class.forName(_class);  
                JInternalFrame frame = (JInternalFrame) classe.newInstance();  
                
                Class[] args1 = new Class[2];
                args1[0] = String.class;
                args1[1] = String.class;
                Method mtd = classe.getMethod(_method, args1);
                mtd.invoke(frame, _args);
                
                jDesktopPane1.add(frame);
                CentralizaTela.setCentro(frame, jDesktopPane1, 0, 0);

                jDesktopPane1.getDesktopManager().activateFrame(frame);
                frame.requestFocus();
                frame.setSelected(true);
                frame.setVisible(true);  
            } catch (Exception e) {e.printStackTrace();}
            
            jBuscar.setText("");
            jBuscaGlobalizada.setVisible(false);
        }
    }//GEN-LAST:event_jtbBuscasMouseClicked

    private void ID() {
        ImageIcon img = new ResizeImageIcon("E", "resources/logos/boleta/" + VariaveisGlobais.marca.toLowerCase().trim() + ".gif", jIMG.getWidth(), jIMG.getHeight()).getImg();
        jIMG.setIcon(img);
        
        jUsuario.setText(VariaveisGlobais.usuario);
        if (VariaveisGlobais.local) {
            jUsuario.setForeground(Color.BLACK);
        } else {
            jUsuario.setForeground(Color.red);
        }
        
        jInicio.setText(new Date().toString());

        // 12-04-2012
        jOS.setText(System.getProperty("os.name") + " - " + System.getProperty("os.version"));
        jLocal.setText(VariaveisGlobais.unidade);
        jBaseDados.setText(VariaveisGlobais.dbnome);
        //jtermica.setText(VariaveisGlobais.DefaultThermalPort);
        jtermica.setText(VariaveisGlobais.statPrinter ? "Ligada" : "Desligada");
        
        ActionListener action = new ActionListener() {
        public void actionPerformed(@SuppressWarnings("unused") java.awt.event.ActionEvent e) {
                jRelogio.setText(Dates.DateFormata("HH:mm.ss", new Date()));
            }
        };
        Timer t = new Timer(1000, action);
        t.start();        
    }
    
    private void jBuscarFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jBuscarFocusGained
        if (jBuscar.getText().trim().equals("")) TableControl.Clear(jtbBuscas);
        int xpos = jDesktopPane1.getWidth() - jBuscaGlobalizada.getWidth();
        int ypos = 40;
        jBuscaGlobalizada.setLocation(xpos, ypos);
        jBuscaGlobalizada.setVisible(true);
        jBuscar.requestFocus();
    }//GEN-LAST:event_jBuscarFocusGained

    private void jBuscarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jBuscarKeyReleased
        if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
            jBuscaGlobalizada.setVisible(false);
            return;
        }

        Buscar();
        jBuscaGlobalizada.setVisible(true);
    }//GEN-LAST:event_jBuscarKeyReleased

    private void jcbHeuristicaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbHeuristicaActionPerformed
        Buscar();
        jBuscar.requestFocus();
    }//GEN-LAST:event_jcbHeuristicaActionPerformed

    private void jInativosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jInativosActionPerformed
        Buscar();
        jBuscar.requestFocus();
    }//GEN-LAST:event_jInativosActionPerformed

    private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized
        jStatus.setBounds(0, 0, jDesktopPane1.getWidth(), 38);
    }//GEN-LAST:event_formComponentResized

    private void jBuscaGlobalizadaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jBuscaGlobalizadaFocusLost
        jBuscaGlobalizada.setVisible(false);
    }//GEN-LAST:event_jBuscaGlobalizadaFocusLost

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
    }//GEN-LAST:event_formWindowClosing

    private void jrbboletasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jrbboletasActionPerformed
        TableControl.header(jtbBuscas, new String[][] {{"contrato", "rgimv", "nome", "cpf/cnpj", "vencimento", "tag"},{"80","80","500","150","80","10"}});
        Buscar();
        jBuscar.requestFocus();
    }//GEN-LAST:event_jrbboletasActionPerformed

    private void jtermicaMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jtermicaMouseReleased
        //jtermica.setEnabled(!jtermica.isEnabled());
        jtermica.setForeground(!VariaveisGlobais.statPrinter ? new java.awt.Color(0, 153, 0) : Color.RED);
        VariaveisGlobais.statPrinter = !VariaveisGlobais.statPrinter;
        jtermica.setText(VariaveisGlobais.statPrinter ? "Ligada" : "Desligada");
    }//GEN-LAST:event_jtermicaMouseReleased

    private void jLabel1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel1MouseClicked
        Test.main(new String[] {});
    }//GEN-LAST:event_jLabel1MouseClicked

    
    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new NewMenu().setVisible(true);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    public void InicializaVariaveis() throws SQLException {
        DbMain conn = VariaveisGlobais.conexao;
        VariaveisGlobais.dCliente.add("empresa", conn.LerParametros("EMPRESA"));
        VariaveisGlobais.dCliente.add("endereco", conn.LerParametros("ENDERECO"));
        VariaveisGlobais.dCliente.add("numero", conn.LerParametros("NUMERO"));
        VariaveisGlobais.dCliente.add("complemento", conn.LerParametros("COMPLEMENTO"));
        VariaveisGlobais.dCliente.add("bairro", conn.LerParametros("BAIRRO"));
        VariaveisGlobais.dCliente.add("cidade", conn.LerParametros("CIDADE"));
        VariaveisGlobais.dCliente.add("estado", conn.LerParametros("ESTADO"));
        VariaveisGlobais.dCliente.add("cep", conn.LerParametros("CEP"));
        VariaveisGlobais.dCliente.add("cnpj", conn.LerParametros("CNPJ"));
        VariaveisGlobais.dCliente.add("tipodoc", conn.LerParametros("TIPODOC"));
        VariaveisGlobais.dCliente.add("inscricao", conn.LerParametros("INSCRICAO"));
        VariaveisGlobais.dCliente.add("tipoinsc", conn.LerParametros("TIPOINSC"));
        VariaveisGlobais.dCliente.add("marca", conn.LerParametros("MARCA"));
        VariaveisGlobais.dCliente.add("telefone", conn.LerParametros("TELEFONE"));
        VariaveisGlobais.dCliente.add("hpage", conn.LerParametros("HPAGE"));
        VariaveisGlobais.dCliente.add("email", conn.LerParametros("EMAIL"));
        VariaveisGlobais.dCliente.add("recibo", conn.LerParametros("RECIBO"));

        VariaveisGlobais.cContas.add("PR", "00");  // Proprietario
        VariaveisGlobais.cContas.add("LC", "01");  // Locatarios
        VariaveisGlobais.cContas.add("PC", "02");  // Passagem Caixa
        VariaveisGlobais.cContas.add("CX", "03");  // Fechamento Caixa
        VariaveisGlobais.cContas.add("NT", "04");  // Taxas
        VariaveisGlobais.cContas.add("AL", "05");  // Alugueres
        VariaveisGlobais.cContas.add("EN", "06");  // Encargos
        VariaveisGlobais.cContas.add("CM", "07");  // Comissão
        VariaveisGlobais.cContas.add("SO", "08");  // Sócio
        VariaveisGlobais.cContas.add("RT", "09");  // Retenção
        VariaveisGlobais.cContas.add("SD", "10");  // Saldo
        VariaveisGlobais.cContas.add("EP", "12");  // Expediente
        VariaveisGlobais.cContas.add("SG", "13");  // Seguro
        VariaveisGlobais.cContas.add("DC", "14");  // Desconto
        VariaveisGlobais.cContas.add("DF", "15");  // Diferença
        VariaveisGlobais.cContas.add("GG", "16");  // Adm Valores
        VariaveisGlobais.cContas.add("CA", "17");  // Contas da Adn
        VariaveisGlobais.cContas.add("AT", "18");  // Antecipações

        VariaveisGlobais.marca = conn.LerParametros("MARCA").toLowerCase().trim();
        // logomarcas
        VariaveisGlobais.icoBoleta = System.getProperty("icoBoleta", VariaveisGlobais.marca + ".gif");
        VariaveisGlobais.icoExtrato = System.getProperty("icoExtrato", VariaveisGlobais.marca + ".gif");

        // Nomes das contas do sistema
        String sSql = "SELECT CODIGO, DESCR FROM ADM;";
        ResultSet hresult = conn.AbrirTabela(sSql, ResultSet.CONCUR_READ_ONLY);
        while (hresult.next()) {
            String campo = hresult.getString("DESCR");
            String chave =  hresult.getString("CODIGO");
            VariaveisGlobais.dCliente.add(chave,campo);
        }
        DbMain.FecharTabela(hresult);
        
        try {regras = conn.LerParametros("REGRAS");} catch (Exception ex) {}

        try {VariaveisGlobais.bcobol = conn.LerParametros("BCOBOL");} catch (Exception ex) {VariaveisGlobais.bcobol = "itau";}
        
        // site
        try {VariaveisGlobais.siteIP = conn.LerParametros("siteIP");} catch (Exception e) {VariaveisGlobais.siteIP = "";}
        try {VariaveisGlobais.siteUser = conn.LerParametros("siteUser");} catch (Exception e) {VariaveisGlobais.siteUser = "";}
        try {VariaveisGlobais.sitePwd = conn.LerParametros("sitePwd");} catch (Exception e) {VariaveisGlobais.sitePwd = "";}
        try {VariaveisGlobais.siteDbName = conn.LerParametros("siteDbName");} catch (Exception e) {VariaveisGlobais.siteDbName = "";}
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel jBaseDados;
    private javax.swing.JInternalFrame jBuscaGlobalizada;
    private javax.swing.JTextField jBuscar;
    private javax.swing.JDesktopPane jDesktopPane1;
    private javax.swing.JLabel jIMG;
    private javax.swing.JCheckBox jInativos;
    private javax.swing.JLabel jInicio;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLocal;
    private javax.swing.JLabel jOS;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JLabel jRelogio;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator10;
    private javax.swing.JSeparator jSeparator8;
    private javax.swing.JSeparator jSeparator9;
    private javax.swing.JPanel jStatus;
    private javax.swing.JLabel jUsuario;
    private javax.swing.JCheckBox jcbHeuristica;
    private javax.swing.JRadioButton jrbFiadores;
    private javax.swing.JRadioButton jrbImoveis;
    private javax.swing.JRadioButton jrbLoca;
    private javax.swing.JRadioButton jrbProp;
    private javax.swing.JRadioButton jrbboletas;
    private javax.swing.JTable jtbBuscas;
    private javax.swing.JLabel jtermica;
    // End of variables declaration//GEN-END:variables

}
