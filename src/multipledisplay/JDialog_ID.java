/*
 *Purpose: The file is shown how to  add/update/search ID List .
 * Last Modified: 2013/09/26
 * Author: Terence Lee
 * 
 */
package multipledisplay;

import clt.api.Display;
import clt.api.DisplayEventListener;
import clt.api.DisplayItem;
import clt.api.Displays;
import clt.api.PacketFrame;
import clt.api.Unit;
import clt.socket.RS232;
import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import clt.api.Ncmd;
import clt.api.Ncmd.pIP210Node2;

/**
 *
 * @author A000847
 */
public class JDialog_ID extends javax.swing.JDialog {

    // <editor-fold defaultstate="collapsed" desc="Define">
    DefaultTableModel dtSearch = null;
    DefaultTableModel dtID_List = null;
    Display display = null;
    Displays displays = null;
    Settings set = null;
    boolean changed = false;
    // </editor-fold>

    public JDialog_ID(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
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
        jPanel1 = new javax.swing.JPanel();
        jrbRS232 = new javax.swing.JRadioButton();
        jrbEthernet = new javax.swing.JRadioButton();
        jcobCOM = new javax.swing.JComboBox();
        btnScan = new javax.swing.JButton();
        jcobIP = new javax.swing.JComboBox();
        jlblAddToList = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jtableSearch = new javax.swing.JTable();
        jlblSearchDisplays = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jtxtIP = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jtxtMac = new javax.swing.JTextField();
        jbtnAdd = new javax.swing.JButton();
        spID = new javax.swing.JSpinner();
        jtxtNickname = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jtableList = new javax.swing.JTable();
        jlblSaveListAs = new javax.swing.JLabel();
        jlblReload = new javax.swing.JLabel();
        jlblSaveList = new javax.swing.JLabel();
        jlblRemove = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("ID List");
        setPreferredSize(new java.awt.Dimension(690, 480));
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Connection Type", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("微軟正黑體", 0, 12))); // NOI18N
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jrbRS232.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jrbRS232.setSelected(true);
        jrbRS232.setText("RS232");
        jPanel1.add(jrbRS232, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 23, -1, -1));

        jrbEthernet.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jrbEthernet.setText("Ethernet");
        jPanel1.add(jrbEthernet, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 53, -1, -1));

        jcobCOM.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jcobCOM.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "COM1" }));
        jcobCOM.setName(""); // NOI18N
        jPanel1.add(jcobCOM, new org.netbeans.lib.awtextra.AbsoluteConstraints(87, 23, 90, 24));

        btnScan.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        btnScan.setText("Scan");
        btnScan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnScanActionPerformed(evt);
            }
        });
        jPanel1.add(btnScan, new org.netbeans.lib.awtextra.AbsoluteConstraints(219, 23, 63, 55));

        jcobIP.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jcobIP.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "255.255.255.255" }));
        jcobIP.setName(""); // NOI18N
        jPanel1.add(jcobIP, new org.netbeans.lib.awtextra.AbsoluteConstraints(87, 53, 122, -1));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 290, 90));

        jlblAddToList.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jlblAddToList.setIcon(new javax.swing.ImageIcon(getClass().getResource("/multipledisplay/addlist.png"))); // NOI18N
        jlblAddToList.setText("<HTML><U>Add to ID List<U><HTML>");
        jlblAddToList.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jlblAddToList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jlblAddToListMouseClicked(evt);
            }
        });
        getContentPane().add(jlblAddToList, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 100, 110, -1));

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Search Result"));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jtableSearch.setAutoCreateRowSorter(true);
        jtableSearch.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jtableSearch.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "", "ID", "Nick Name", "IP", "MAC"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Boolean.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                true, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jtableSearch.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jScrollPane2.setViewportView(jtableSearch);

        jPanel3.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 19, 278, 274));

        getContentPane().add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 130, 290, 300));

        jlblSearchDisplays.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jlblSearchDisplays.setIcon(new javax.swing.ImageIcon(getClass().getResource("/multipledisplay/search.png"))); // NOI18N
        jlblSearchDisplays.setText("<HTML><U>Search Display<U><HTML>");
        jlblSearchDisplays.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jlblSearchDisplays.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jlblSearchDisplaysMouseClicked(evt);
            }
        });
        getContentPane().add(jlblSearchDisplays, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 100, 120, -1));

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Create New Display", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("微軟正黑體", 0, 12))); // NOI18N
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jLabel1.setText("ID");
        jPanel4.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 25, 18, 20));

        jLabel2.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jLabel2.setText("IP");
        jPanel4.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 54, 18, 20));

        jtxtIP.setFont(new java.awt.Font("微軟正黑體", 0, 10)); // NOI18N
        jPanel4.add(jtxtIP, new org.netbeans.lib.awtextra.AbsoluteConstraints(38, 55, 88, -1));

        jLabel3.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jLabel3.setText("MAC");
        jPanel4.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 54, 29, 20));

        jtxtMac.setFont(new java.awt.Font("微軟正黑體", 0, 10)); // NOI18N
        jtxtMac.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtxtMacActionPerformed(evt);
            }
        });
        jPanel4.add(jtxtMac, new org.netbeans.lib.awtextra.AbsoluteConstraints(163, 55, 90, -1));

        jbtnAdd.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jbtnAdd.setText("Add/Update");
        jbtnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnAddActionPerformed(evt);
            }
        });
        jPanel4.add(jbtnAdd, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 20, 110, 60));

        spID.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        spID.setModel(new javax.swing.SpinnerNumberModel(1, 0, 19, 1));
        jPanel4.add(spID, new org.netbeans.lib.awtextra.AbsoluteConstraints(38, 24, -1, -1));

        jtxtNickname.setFont(new java.awt.Font("微軟正黑體", 0, 10)); // NOI18N
        jtxtNickname.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtxtNicknameActionPerformed(evt);
            }
        });
        jPanel4.add(jtxtNickname, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 24, 110, -1));

        jLabel4.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jLabel4.setText("Name");
        jPanel4.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 25, 40, 20));

        getContentPane().add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 10, 380, 90));

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("ID List"));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jtableList.setAutoCreateRowSorter(true);
        jtableList.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jtableList.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "", "ID", "Nick Name", "IP", "MAC"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Boolean.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                true, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jtableList.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jtableList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jtableListMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(jtableList);

        jPanel2.add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 19, 360, 274));

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 130, 380, 300));

        jlblSaveListAs.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jlblSaveListAs.setIcon(new javax.swing.ImageIcon(getClass().getResource("/multipledisplay/save.png"))); // NOI18N
        jlblSaveListAs.setText("<HTML><U>Save List As<U><HTML>");
        jlblSaveListAs.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jlblSaveListAs.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jlblSaveListAsMouseClicked(evt);
            }
        });
        getContentPane().add(jlblSaveListAs, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 100, 100, -1));

        jlblReload.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jlblReload.setIcon(new javax.swing.ImageIcon(getClass().getResource("/multipledisplay/reload.png"))); // NOI18N
        jlblReload.setText("<HTML><U>Reload<U><HTML>");
        jlblReload.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jlblReload.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jlblReloadMouseClicked(evt);
            }
        });
        jlblReload.addAncestorListener(new javax.swing.event.AncestorListener() {
            public void ancestorMoved(javax.swing.event.AncestorEvent evt) {
            }
            public void ancestorAdded(javax.swing.event.AncestorEvent evt) {
                jlblReloadAncestorAdded(evt);
            }
            public void ancestorRemoved(javax.swing.event.AncestorEvent evt) {
            }
        });
        getContentPane().add(jlblReload, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 100, 80, -1));

        jlblSaveList.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jlblSaveList.setIcon(new javax.swing.ImageIcon(getClass().getResource("/multipledisplay/save.png"))); // NOI18N
        jlblSaveList.setText("<HTML><U>Save List<U><HTML>");
        jlblSaveList.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jlblSaveList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jlblSaveListMouseClicked(evt);
            }
        });
        jlblSaveList.addAncestorListener(new javax.swing.event.AncestorListener() {
            public void ancestorMoved(javax.swing.event.AncestorEvent evt) {
            }
            public void ancestorAdded(javax.swing.event.AncestorEvent evt) {
                jlblSaveListAncestorAdded(evt);
            }
            public void ancestorRemoved(javax.swing.event.AncestorEvent evt) {
            }
        });
        getContentPane().add(jlblSaveList, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 100, 80, -1));

        jlblRemove.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jlblRemove.setIcon(new javax.swing.ImageIcon(getClass().getResource("/multipledisplay/remove.png"))); // NOI18N
        jlblRemove.setText("<HTML><U>Remove<U><HTML>");
        jlblRemove.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jlblRemove.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jlblRemoveMouseClicked(evt);
            }
        });
        getContentPane().add(jlblRemove, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 100, 90, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        this.initial();
    }//GEN-LAST:event_formWindowOpened

    private void initial() {
        try {
            this.buttonGroup1.add(this.jrbRS232);
            this.buttonGroup1.add(this.jrbEthernet);

            //you need  set the Jtable attribues  "AutosizeMode " to OFF
            jtableSearch.getColumnModel().getColumn(0).setPreferredWidth(15);
            jtableSearch.getColumnModel().getColumn(1).setPreferredWidth(20);
            jtableSearch.getColumnModel().getColumn(2).setPreferredWidth(80);
            jtableSearch.getColumnModel().getColumn(3).setPreferredWidth(90);
            jtableSearch.getColumnModel().getColumn(4).setPreferredWidth(140);
            dtSearch = (DefaultTableModel) jtableSearch.getModel();
            dtSearch.setRowCount(0);

            jtableList.getColumnModel().getColumn(0).setPreferredWidth(15);
            jtableList.getColumnModel().getColumn(1).setPreferredWidth(20);
            jtableList.getColumnModel().getColumn(2).setPreferredWidth(80);
            jtableList.getColumnModel().getColumn(3).setPreferredWidth(90);
            jtableList.getColumnModel().getColumn(4).setPreferredWidth(140);
            dtID_List = (DefaultTableModel) jtableList.getModel();

            dtID_List.setRowCount(0);

            this.btnScanActionPerformed(null);

            //LoadSetting
            loadXML();

            if (set.useCOM) {
                this.jrbRS232.setSelected(true);
            } else {
                this.jrbEthernet.setSelected(true);
            }

            this.loadID_List();
        } catch (Exception exp) {
            Functions.showMsgDialog(exp.getMessage());
        }
    }

    private void loadXML() {

        try {
            File f = new File(Functions.cfgXml);
            if (f.exists()) {
                set = Settings.LoadXML(Functions.cfgXml);
            } else {
                set = new Settings();
                set.SaveXML(Functions.cfgXml);
            }

            dtID_List.setRowCount(0);
            File file = new File(Functions.displayXml);
            if (file.exists()) {
                displays = Displays.loadXML(Functions.displayXml);
            } else {
                displays = new Displays();
                displays.saveXML(Functions.displayXml);
            }

        } catch (Exception exp) {
            Functions.showMsgDialog(exp.getMessage());
        }

    }

    private void loadID_List() {
        dtID_List.setRowCount(0);
        for (DisplayItem item : displays.getDisplayItems()) {
            Vector v = new Vector();
            v.add(false);
            v.add(item.getID());
            v.add(item.getName());
            v.add(item.getIP());
            v.add(item.getMac());

            dtID_List.addRow(v);
        }
    }

    private void btnScanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnScanActionPerformed

        //Scan all RS232 device
        jcobCOM.removeAllItems();
        String[] aryComs = RS232.ListCommPorts();
        for (String element : aryComs) {
            jcobCOM.addItem(element);
        }
        //Scan all Etherent interface 
        jcobIP.removeAllItems();
        String ip = Unit.listHostAddress();
        if (ip != "") {
            jcobIP.addItem(ip);
        }

    }//GEN-LAST:event_btnScanActionPerformed

    private void searchRS232() {
        try {
            String comName = (String) jcobCOM.getSelectedItem();

            display = new Display(comName);
            if (!display.IsConnected) {
                //   ShowMsg("Connection timed out.");
                display = null;
                return;
            }
            display.addListener(new DisplayEventListener() {
                @Override
                public synchronized void OnConnected(Object source) {
                    //check power status
                    DefaultTableModel dtm = (DefaultTableModel) jtableSearch.getModel();
                    dtm.setRowCount(0);     //clear search jtable
                    display.GetPowerStatus((byte) 0);

                }

                @Override
                public synchronized void OnResponse(Object source, PacketFrame pf) {
                    if (Arrays.equals(pf.Command, Unit.CMD_POWER_CONTROL)) {
                        Vector v = new Vector();
                        v.add(false);
                        v.add(pf.IDT);
                        v.add("");
                        v.add("");
                        v.add("");
                        dtSearch.addRow(v);
                    }
                }

                @Override
                public void OnDisconnected(Object evt) {
                    //  throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public void OnMessage(Object evt, String Msg) {
                    // throw new UnsupportedOperationException("Not supported yet.");
                }
            });
            display.run();

        } catch (Exception exp) {
            display = null;
            Functions.showMsgDialog(exp.getMessage());
            exp.printStackTrace();
        }
    }

    private void searchNetwork() {

        try {
            dtSearch.setRowCount(0);
            ArrayList<pIP210Node2> getP210 = Functions.SearchDevice();
            for (pIP210Node2 p2 : getP210) {
                Vector v = new Vector();
                v.add(true);
                v.add(p2.DeviceId);
                v.add(p2.Nickname);
                v.add(p2.IPAddr);
                v.add(p2.MacAddr);

                dtSearch.addRow(v);
            }
        } catch (Exception exp) {
            Functions.showMsgDialog(exp.getMessage());
        }

    }

    private void addUpdateID_List(byte id, String nickName, String ip, String mac) {
        boolean isExist = false;

        if (this.jrbRS232.isSelected()) {
            for (DisplayItem di : displays.getDisplayItems()) {

                if (di.getID() == id) {
                    isExist = true;
                    if (Functions.showYesNoDialog("The MAC address is already exist. overwrite it?")) {
                        displays.updateDisplayItemByID(id, ip, mac);

                        for (int i = 0; i < dtID_List.getRowCount(); i++) {
                            byte oID = (byte) dtID_List.getValueAt(i, 1);
                            if (oID == id) {

                                dtID_List.setValueAt(nickName, i, 2); //nickname
                                dtID_List.setValueAt(ip, i, 3); //IP
                                dtID_List.setValueAt(mac, i, 4); //MAC
                            }
                        }
                    }
                }
            }
        } else {   //Ethernet
            for (DisplayItem di : displays.getDisplayItems()) {
                if (di.getMac().equals(mac)) {
                    isExist = true;

                    if (Functions.showYesNoDialog("The MAC address is already exist. overwrite it?")) {
                        displays.updateDisplayItemByMAC(id, ip, mac);

                        for (int i = 0; i < dtID_List.getRowCount(); i++) {
                            String oMAC = (String) dtID_List.getValueAt(i, 4);
                            if (oMAC.equals(mac)) {
                                dtID_List.setValueAt(id, i, 1); //ID
                                dtID_List.setValueAt(nickName, i, 2); //Nickname
                                dtID_List.setValueAt(ip, i, 3); //IP
                            }
                        }
                        this.loadID_List();
                    }
                }
            }
        }

        if (!isExist) //update
        {
            displays.addDisplayItem(id, nickName, ip, mac);

            this.loadID_List();
        }

    }

    private void jlblSearchDisplaysMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlblSearchDisplaysMouseClicked
        // TODO add your handling code here:

        try {
            jlblSearchDisplays.setEnabled(false);
            jlblSearchDisplays.setBackground(Color.GRAY);

            if (jrbRS232.isSelected()) {
                String comName = (String) jcobCOM.getSelectedItem();
                if (!comName.equals("")) {
                    this.searchRS232();
                    try {
                        for (int i = 0; i < 30; i++) {
                            Thread.sleep(100);//睡眠100毫秒
                        }
                        if (display != null) {
                            display.stop();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                try {
                    String ip = (String) jcobIP.getSelectedItem();
                    if (!ip.equals("")) {
                        this.searchNetwork();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } finally {
            jlblSearchDisplays.setEnabled(true);
            jlblSearchDisplays.setBackground(Color.BLACK);
        }
    }//GEN-LAST:event_jlblSearchDisplaysMouseClicked

    private void jlblAddToListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlblAddToListMouseClicked
        boolean isExist = false;

        if (dtSearch.getRowCount() == 0) {
            return;
        }

        for (int i = 0; i < dtSearch.getRowCount(); i++) {
            boolean isAdd = (boolean) dtSearch.getValueAt(i, 0);
            if (isAdd) {
                byte id = Byte.parseByte(dtSearch.getValueAt(i, 1).toString());
                String name = (String) dtSearch.getValueAt(i, 2);
                String ip = (String) dtSearch.getValueAt(i, 3);
                String mac = (String) dtSearch.getValueAt(i, 4);
                addUpdateID_List(id, name, ip, mac);
            }
        }
    }//GEN-LAST:event_jlblAddToListMouseClicked

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:
        try {
            if (this.jrbRS232.isSelected()) {
                set.useCOM = true;
                String comName = (String) jcobCOM.getSelectedItem();
                if (comName != null && !comName.equals("")) {
                    set.setCOM(comName);
                }
            } else {
                set.useCOM = false;
                String ip = (String) jcobIP.getSelectedItem();
                if (ip != null && !ip.equals("")) {
                    set.setLocalIP(ip);
                }
            }
            set.SaveXML();
        } catch (Exception exp) {
            Functions.showMsgDialog(exp.getMessage());
        }
    }//GEN-LAST:event_formWindowClosing

    private void jlblSaveListAsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlblSaveListAsMouseClicked
        // TODO add your handling code here:
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("XML file", "xml");
        chooser.setFileFilter(filter);
        int option = chooser.showSaveDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            File f = chooser.getSelectedFile();
            String filePath = f.getPath();
            if (!filePath.toLowerCase().endsWith(".xml")) {
                f = new File(filePath + ".xml");
            }
            try {
                displays.saveXML(f.getPath());
                changed = true;
                Functions.showMsgDialog("ID list has been saved.");
            } catch (Exception exp) {
                Functions.showMsgDialog(exp.getMessage());
            }
        }

    }//GEN-LAST:event_jlblSaveListAsMouseClicked

    private void jlblReloadMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlblReloadMouseClicked
        loadXML();
        loadID_List();
    }//GEN-LAST:event_jlblReloadMouseClicked

    private void jlblSaveListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlblSaveListMouseClicked
        if (displays != null) {
            displays.saveXml();
            changed = true;
            Functions.showMsgDialog("ID list has been saved.");
        }
    }//GEN-LAST:event_jlblSaveListMouseClicked

    private void jlblRemoveMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlblRemoveMouseClicked

        for (int i = 0; i < dtID_List.getRowCount(); i++) {
            Boolean chbox = Boolean.parseBoolean(dtID_List.getValueAt(i, 0).toString());
            if (chbox.booleanValue()) {
                byte id = (byte) dtID_List.getValueAt(i, 1);
                String nickname = dtID_List.getValueAt(i, 2).toString().trim();
                String ip = dtID_List.getValueAt(i, 3).toString().trim();
                String mac = dtID_List.getValueAt(i, 4).toString().trim();
                displays.removeDisplayItem(id, ip, mac);
                dtID_List.removeRow(i);
                i--;
            }
        }
    }//GEN-LAST:event_jlblRemoveMouseClicked

    private void jtableListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jtableListMouseClicked
        // TODO add your handling code here:
        int curIndex = this.jtableList.getSelectedRow();

        this.spID.setValue(jtableList.getValueAt(curIndex, 1));
        this.jtxtNickname.setText(jtableList.getValueAt(curIndex, 2).toString());
        this.jtxtIP.setText(jtableList.getValueAt(curIndex, 3).toString());
        this.jtxtMac.setText(jtableList.getValueAt(curIndex, 4).toString());

    }//GEN-LAST:event_jtableListMouseClicked

    private void jbtnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnAddActionPerformed
        byte id = Byte.parseByte(spID.getValue().toString());
        addUpdateID_List(id, "NetUart", this.jtxtIP.getText().trim(), this.jtxtMac.getText().trim());
    }//GEN-LAST:event_jbtnAddActionPerformed

    private void jtxtMacActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtxtMacActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jtxtMacActionPerformed

    private void jlblReloadAncestorAdded(javax.swing.event.AncestorEvent evt) {//GEN-FIRST:event_jlblReloadAncestorAdded
        // TODO add your handling code here:
    }//GEN-LAST:event_jlblReloadAncestorAdded

    private void jlblSaveListAncestorAdded(javax.swing.event.AncestorEvent evt) {//GEN-FIRST:event_jlblSaveListAncestorAdded
        // TODO add your handling code here:
    }//GEN-LAST:event_jlblSaveListAncestorAdded

    private void jtxtNicknameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtxtNicknameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jtxtNicknameActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Metal".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;




                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(JDialog_ID.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(JDialog_ID.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(JDialog_ID.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JDialog_ID.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                JDialog_ID dialog = new JDialog_ID(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnScan;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JButton jbtnAdd;
    private javax.swing.JComboBox jcobCOM;
    private javax.swing.JComboBox jcobIP;
    private javax.swing.JLabel jlblAddToList;
    private javax.swing.JLabel jlblReload;
    private javax.swing.JLabel jlblRemove;
    private javax.swing.JLabel jlblSaveList;
    private javax.swing.JLabel jlblSaveListAs;
    private javax.swing.JLabel jlblSearchDisplays;
    private javax.swing.JRadioButton jrbEthernet;
    private javax.swing.JRadioButton jrbRS232;
    private javax.swing.JTable jtableList;
    private javax.swing.JTable jtableSearch;
    private javax.swing.JTextField jtxtIP;
    private javax.swing.JTextField jtxtMac;
    private javax.swing.JTextField jtxtNickname;
    private javax.swing.JSpinner spID;
    // End of variables declaration//GEN-END:variables
}