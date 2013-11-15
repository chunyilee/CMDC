/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package multipledisplay;

import clt.api.Display;
import clt.api.Display.DisplayType;
import clt.api.DisplayEventListener;
import clt.api.DisplayItem;
import clt.api.Displays;
import clt.api.PacketFrame;
import clt.api.Unit;
import java.awt.Component;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import clt.api.Ncmd;
import clt.api.Display.DisplayType;

/**
 *
 * @author A000847
 */
public class CMD extends javax.swing.JFrame {

    // <editor-fold defaultstate="collapsed" desc="Define">
    final int tableDefaultWidth = 80;
    final int tableDefaultHeight = 25;
    final int tmrIDListCheckSec = 30;   //save id list by 30 sconeds 
    final String SERVICEMSG = "Logging Service: ";
    boolean firstLogging = true;
    int tmrLoggingMin = 10;   //by mins
    ArrayList<Display> netDisplays = new ArrayList<>();
    ArrayList<Displays> logDisplays = new ArrayList<>();
    Display comDisplay = null;
    Displays displayItems = null;
    ImageIcon imgPowerOn = null;
    ImageIcon imgPowerOff = null;
    ImageIcon imgConnect = null;
    ImageIcon imgDisconnect = null;
    Settings set = null;
    String todayLogFolder = "";
    Timer tmrIDListCheck = new Timer();    //一段時間儲存Displays到XML,以秒為單位
    Timer tmrLogging = new Timer();    //一段時儲存每個 ID的資料, 以分為單位
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    //</editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Power Control Tab">
    DefaultTableModel dtPower = null;

    private void initialPowerControl() {

        if (dtPower != null) {
            dtPower = null;
        }
        //you need  set the Jtable attribues  "AutosizeMode " to OFF
        jtablePower.getColumnModel().getColumn(0).setPreferredWidth(20);
        jtablePower.getColumnModel().getColumn(1).setPreferredWidth(20);

        if (set.useCOM) {
            //hide IP Field        
            tableHideColumn(jtablePower, 2);
        } else {
            tableShowColumn(jtablePower, 2, 90);
        }

        tableShowColumn(jtablePower, 3, 30);
        jtablePower.getColumnModel().getColumn(3).setCellRenderer(new ImagePowerRenderer());

        if (set.logConnectStatus) {
            tableShowColumn(jtablePower, 4, 30);
            jtablePower.getColumnModel().getColumn(4).setCellRenderer(new ImageConnRenderer());
        } else {

            tableHideColumn(jtablePower, 4);
        }

        if (set.logBacklightSatus) {
            tableShowColumn(jtablePower, 5, tableDefaultWidth);
        } else {
            tableHideColumn(jtablePower, 5);
        }

        if (set.logBrightness) {
            tableShowColumn(jtablePower, 6, tableDefaultWidth);
        } else {
            tableHideColumn(jtablePower, 6);
        }

        if (set.logColorTemperature) {
            tableShowColumn(jtablePower, 7, tableDefaultWidth);
        } else {
            tableHideColumn(jtablePower, 7);
        }
        if (set.logContrast) {
            tableShowColumn(jtablePower, 8, tableDefaultWidth);
        } else {
            tableHideColumn(jtablePower, 8);
        }
        if (set.logDigitalBrightnessLevel) {
            tableShowColumn(jtablePower, 9, tableDefaultWidth);
        } else {
            tableHideColumn(jtablePower, 9);
        }

        if (set.logFan0Speed) {
            tableShowColumn(jtablePower, 10, tableDefaultWidth);
        } else {
            tableHideColumn(jtablePower, 10);
        }
        if (set.logFan1Speed) {
            tableShowColumn(jtablePower, 11, tableDefaultWidth);
        } else {
            tableHideColumn(jtablePower, 11);
        }
        if (set.logInputSource) {
            tableShowColumn(jtablePower, 12, tableDefaultWidth);
        } else {
            tableHideColumn(jtablePower, 12);
        }
        if (set.logMuteStatus) {
            tableShowColumn(jtablePower, 13, tableDefaultWidth);
        } else {
            tableHideColumn(jtablePower, 13);
        }
        if (set.logPhase) {
            tableShowColumn(jtablePower, 14, tableDefaultWidth);
        } else {
            tableHideColumn(jtablePower, 14);
        }
        if (set.logVolume) {
            tableShowColumn(jtablePower, 15, tableDefaultWidth);
        } else {
            tableHideColumn(jtablePower, 15);

        }
        jtablePower.setRowHeight(tableDefaultHeight);
        dtPower = (DefaultTableModel) jtablePower.getModel();
        dtPower.setRowCount(0);

        loadPowerControlTable();
    }

    private void loadPowerControlTable() {
        if (dtPower == null) {
            return;
        }
        dtPower.setRowCount(0);
        //   for (DisplayItem di : displayItems.getDisplayItems()) {
        for (int i = 0; i < displayItems.getDisplayItems().size(); i++) {
            try {
                displayItems.displayItem.get(i).isConnected = false;
                DisplayItem di = displayItems.displayItem.get(i);

                Vector v = new Vector();
                v.add(false);
                v.add(di.getID());
                v.add(di.getIP());

                v.add(di.getPowerOn());

                //Conencted field  is false when the app opend
                v.add(di.isConnected);
                v.add(di.getBacklightOn());
                v.add(di.getBL_Brightness());
                v.add(di.getColorTemperature());
                v.add(di.getContrast());
                v.add(di.getDigitalBrightnessLevel());
                v.add(di.getFan0Speed());
                v.add(di.getFan1Speed());
                v.add(Unit.SOURCE_TYPE[di.getInputSource()]);
                v.add(di.getMuteOn());
                v.add(di.getPhase());
                v.add(di.getVolume());

                dtPower.addRow(v);
            } catch (Exception exp) {
                continue;
            }
        }
    }

    private void updatePowerControlTable(DisplayItem di) {
        if (di == null) {
            return;
        }

        for (int i = 0; i < dtPower.getRowCount(); i++) {
            Byte id = Byte.parseByte(dtPower.getValueAt(i, 1).toString());
            String ip = dtPower.getValueAt(i, 2).toString();
            if (id == di.getID()) {
                if (!set.useCOM && !ip.equals(di.getIP())) {
                    continue;
                }
                try {
                    dtPower.setValueAt(di.getPowerOn(), i, 3);
                    dtPower.setValueAt(di.isConnected, i, 4);
                    dtPower.setValueAt(di.getBacklightOn(), i, 5);
                    dtPower.setValueAt(di.getBL_Brightness(), i, 6);
                    dtPower.setValueAt(di.getColorTemperature(), i, 7);
                    dtPower.setValueAt(di.getContrast(), i, 8);
                    dtPower.setValueAt(di.getDigitalBrightnessLevel(), i, 9);
                    dtPower.setValueAt(di.getFan0Speed(), i, 10);
                    dtPower.setValueAt(di.getFan1Speed(), i, 11);
                    dtPower.setValueAt(Unit.SOURCE_TYPE[di.getInputSource()], i, 12);
                    dtPower.setValueAt(di.getMuteOn(), i, 13);
                    dtPower.setValueAt(di.getPhase(), i, 14);
                    dtPower.setValueAt(di.getVolume(), i, 15);
                } catch (Exception exp) {
                    continue;
                }
            }
        }
    }
//</editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Input Source Tab">
    DefaultTableModel dtInput = null;

    private void initialInputSource() {
        //you need  set the Jtable attribues  "AutosizeMode " to OFF
        jtableInput.getColumnModel().getColumn(0).setPreferredWidth(20);
        jtableInput.getColumnModel().getColumn(1).setPreferredWidth(20);
        if (set.useCOM) {
            tableHideColumn(jtableInput, 2);

        } else {
            tableShowColumn(jtableInput, 2, 90);
        }
        jtableInput.getColumnModel().getColumn(3).setPreferredWidth(30);
        jtableInput.getColumnModel().getColumn(3).setCellRenderer(new ImagePowerRenderer());
        jtableInput.getColumnModel().getColumn(4).setPreferredWidth(150);
        jtableInput.setRowHeight(tableDefaultHeight);
        dtInput = (DefaultTableModel) jtableInput.getModel();
        dtInput.setRowCount(0);
        loadInputSourceTable();
    }

    private void loadInputSourceTable() {
        if (dtInput == null) {
            return;
        }
        dtInput.setRowCount(0);
        for (DisplayItem di : displayItems.getDisplayItems()) {
            try {
                Vector v = new Vector();
                v.add(false);
                v.add(di.getID());
                v.add(di.getIP());
                v.add(di.getPowerOn());
                v.add(Unit.SOURCE_TYPE[di.getInputSource()]);

                dtInput.addRow(v);
            } catch (Exception exp) {
                continue;
            }
        }
    }

    private void loadInputSourceTable(DisplayItem di) {
        if (di == null) {
            return;
        }

        for (int i = 0; i < dtPower.getRowCount(); i++) {
            Byte id = Byte.parseByte(dtPower.getValueAt(i, 1).toString());
            String ip = dtPower.getValueAt(i, 2).toString();
            if (id == di.getID()) {
                if (!set.useCOM && ip != di.getIP()) {
                    continue;
                }
                try {
                    dtInput.setValueAt(di.getPowerOn(), i, 3);
                    dtInput.setValueAt(Unit.SOURCE_TYPE[di.getInputSource()], i, 4);
                } catch (Exception exp) {
                    continue;
                }

            }
        }
    }
// </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="PIP Tab">
    DefaultTableModel dtPIP = null;

    private void initialPIP() {
        //you need  set the Jtable attribues  "AutosizeMode " to OFF
        jtablePIP.getColumnModel().getColumn(0).setPreferredWidth(20);
        jtablePIP.getColumnModel().getColumn(1).setPreferredWidth(20);
        if (set.useCOM) {
            tableHideColumn(jtablePIP, 2);

        } else {
            tableShowColumn(jtablePIP, 2, 90);
        }

        jtablePIP.getColumnModel().getColumn(3).setPreferredWidth(30);
        jtablePIP.getColumnModel().getColumn(3).setCellRenderer(new ImagePowerRenderer());
        jtablePIP.getColumnModel().getColumn(4).setPreferredWidth(tableDefaultWidth);
        jtablePIP.getColumnModel().getColumn(5).setPreferredWidth(tableDefaultWidth);
        jtablePIP.getColumnModel().getColumn(6).setPreferredWidth(tableDefaultWidth);
        jtablePIP.setRowHeight(tableDefaultHeight);
        dtPIP = (DefaultTableModel) jtablePIP.getModel();
        dtPIP.setRowCount(0);
        loadPIP_Table();
    }

    private void loadPIP_Table() {
        if (dtPIP == null) {
            return;
        }
        dtPIP.setRowCount(0);
        for (DisplayItem di : displayItems.getDisplayItems()) {
            try {

                Vector v = new Vector();
                v.add(false);
                v.add(di.getID());
                v.add(di.getIP());
                v.add(di.getPowerOn());
                v.add(Unit.SOURCE_TYPE[di.getPIP_SourceSelection()]);
                v.add(Unit.PIP_SIZE[di.getPIP_Adjust()]);
                v.add(Unit.PIP_Postion[di.getPIP_Position()]);
                dtPIP.addRow(v);
            } catch (Exception exp) {
                continue;
            }
        }
    }

    private void loadPIP_Table(DisplayItem di) {
        if (di == null) {
            return;
        }

        for (int i = 0; i < dtPower.getRowCount(); i++) {
            Byte id = Byte.parseByte(dtPower.getValueAt(i, 1).toString());
            String ip = dtPower.getValueAt(i, 2).toString();
            if (id == di.getID()) {
                if (!set.useCOM && ip != di.getIP()) {
                    continue;
                }
                try {
                    dtPIP.setValueAt(di.getPowerOn(), i, 3);
                    dtPIP.setValueAt(Unit.SOURCE_TYPE[di.getPIP_SourceSelection()], i, 4);
                    dtPIP.setValueAt(Unit.PIP_SIZE[di.getPIP_Adjust()], i, 5);
                    dtPIP.setValueAt(Unit.PIP_Postion[di.getPIP_Position()], i, 6);

                } catch (Exception exp) {
                    continue;
                }
            }

        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Adjust Tab">
    DefaultTableModel dtAdjustment = null;

    private void initialAdjustment() {
        //you need  set the Jtable attribues  "AutosizeMode " to OFF
        jtableAdjustment.getColumnModel().getColumn(0).setPreferredWidth(20);
        jtableAdjustment.getColumnModel().getColumn(1).setPreferredWidth(20);
        if (set.useCOM) {
            tableHideColumn(jtableAdjustment, 2);

        } else {
            tableShowColumn(jtableAdjustment, 2, 90);
        }
        jtableAdjustment.getColumnModel().getColumn(3).setPreferredWidth(30);
        jtableAdjustment.getColumnModel().getColumn(3).setCellRenderer(new ImagePowerRenderer());
        jtableAdjustment.getColumnModel().getColumn(4).setPreferredWidth(tableDefaultWidth);
        jtableAdjustment.getColumnModel().getColumn(5).setPreferredWidth(tableDefaultWidth);
        jtableAdjustment.getColumnModel().getColumn(6).setPreferredWidth(tableDefaultWidth);
        jtableAdjustment.getColumnModel().getColumn(7).setPreferredWidth(tableDefaultWidth);
        jtableAdjustment.getColumnModel().getColumn(8).setPreferredWidth(tableDefaultWidth);
        jtableAdjustment.getColumnModel().getColumn(9).setPreferredWidth(tableDefaultWidth);
        jtableAdjustment.getColumnModel().getColumn(10).setPreferredWidth(tableDefaultWidth);
        jtableAdjustment.getColumnModel().getColumn(11).setPreferredWidth(tableDefaultWidth);
        jtableAdjustment.getColumnModel().getColumn(12).setPreferredWidth(tableDefaultWidth);
        jtableAdjustment.getColumnModel().getColumn(13).setPreferredWidth(tableDefaultWidth);
        jtableAdjustment.getColumnModel().getColumn(14).setPreferredWidth(tableDefaultWidth);
        jtableAdjustment.getColumnModel().getColumn(15).setPreferredWidth(tableDefaultWidth);
        jtableAdjustment.getColumnModel().getColumn(16).setPreferredWidth(tableDefaultWidth);
        jtableAdjustment.getColumnModel().getColumn(17).setPreferredWidth(tableDefaultWidth);
        jtableAdjustment.getColumnModel().getColumn(18).setPreferredWidth(tableDefaultWidth);
        jtableAdjustment.getColumnModel().getColumn(19).setPreferredWidth(tableDefaultWidth);
        jtableAdjustment.getColumnModel().getColumn(20).setPreferredWidth(tableDefaultWidth);
        jtableAdjustment.setRowHeight(tableDefaultHeight);
        dtAdjustment = (DefaultTableModel) jtableAdjustment.getModel();
        dtAdjustment.setRowCount(0);
        loadAdjustmentTable();
    }

    private void loadAdjustmentTable() {
        if (dtAdjustment == null) {
            return;
        }
        dtAdjustment.setRowCount(0);
        for (DisplayItem di : displayItems.getDisplayItems()) {
            try {

                Vector v = new Vector();
                v.add(false);
                v.add(di.getID());
                v.add(di.getIP());
                v.add(di.getPowerOn());

                //start list adjustment value
                v.add(di.getBL_Brightness());
                v.add(di.getDigitalBrightnessLevel());
                v.add(di.getContrast());
                v.add(di.getHue());
                v.add(di.getStaturation());
                v.add(di.getPhase());
                v.add(di.getClock());
                v.add(di.getSharpness());
                v.add(di.getPowerOnDelay());
                v.add(Unit.GAMMA_TYPE[di.getGamma()]);
                v.add(Unit.COLOR_TEMP[di.getColorTemperature()]);

                v.add(di.getR_Gain());
                v.add(di.getG_Gain());
                v.add(di.getB_Gain());
                v.add(di.getR_Offset());
                v.add(di.getG_Offset());
                v.add(di.getB_Offset());

                dtAdjustment.addRow(v);
            } catch (Exception exp) {
                continue;
            }
        }
    }

    private void loadAdjustmentTable(DisplayItem di) {
        if (di == null) {
            return;
        }

        for (int i = 0; i < dtPower.getRowCount(); i++) {
            Byte id = Byte.parseByte(dtPower.getValueAt(i, 1).toString());
            String ip = dtPower.getValueAt(i, 2).toString();
            if (id == di.getID()) {
                if (!set.useCOM && ip != di.getIP()) {
                    continue;
                }
                try {
                    dtAdjustment.setValueAt(di.getPowerOn(), i, 3);

                    //start list adjustment value
                    dtAdjustment.setValueAt(di.getBL_Brightness(), i, 4);
                    dtAdjustment.setValueAt(di.getDigitalBrightnessLevel(), i, 5);
                    dtAdjustment.setValueAt(di.getContrast(), i, 6);
                    dtAdjustment.setValueAt(di.getHue(), i, 7);
                    dtAdjustment.setValueAt(di.getStaturation(), i, 8);
                    dtAdjustment.setValueAt(di.getPhase(), i, 9);
                    dtAdjustment.setValueAt(di.getClock(), i, 10);
                    dtAdjustment.setValueAt(di.getSharpness(), i, 11);
                    dtAdjustment.setValueAt(di.getPowerOnDelay(), i, 12);
                    dtAdjustment.setValueAt(Unit.GAMMA_TYPE[di.getGamma()], i, 13);
                    dtAdjustment.setValueAt(Unit.COLOR_TEMP[di.getColorTemperature()], i, 14);

                    dtAdjustment.setValueAt(di.getR_Gain(), i, 15);
                    dtAdjustment.setValueAt(di.getG_Gain(), i, 16);
                    dtAdjustment.setValueAt(di.getB_Gain(), i, 17);
                    dtAdjustment.setValueAt(di.getR_Offset(), i, 18);
                    dtAdjustment.setValueAt(di.getG_Offset(), i, 19);
                    dtAdjustment.setValueAt(di.getB_Offset(), i, 20);
                } catch (Exception exp) {
                    continue;
                }
            }

        }
    }
    //</editor-fold>
    // <editor-fold defaultstate="collapsed" desc="TimeTab">
    DefaultTableModel dtTime = null;

    private void initialTime() {
        //you need  set the Jtable attribues  "AutosizeMode " to OFF
        jtableTime.getColumnModel().getColumn(0).setPreferredWidth(20);
        jtableTime.getColumnModel().getColumn(1).setPreferredWidth(20);
        if (set.useCOM) {
            tableHideColumn(jtableTime, 2);

        } else {
            tableShowColumn(jtableTime, 2, 90);
        }
        jtableTime.getColumnModel().getColumn(3).setPreferredWidth(30);
        jtableTime.getColumnModel().getColumn(3).setCellRenderer(new ImagePowerRenderer());
        jtableTime.getColumnModel().getColumn(4).setPreferredWidth(tableDefaultWidth);
        jtableTime.getColumnModel().getColumn(5).setPreferredWidth(tableDefaultWidth);
        jtableTime.getColumnModel().getColumn(6).setPreferredWidth(tableDefaultWidth);
        jtableTime.getColumnModel().getColumn(7).setPreferredWidth(tableDefaultWidth);
        jtableTime.getColumnModel().getColumn(8).setPreferredWidth(tableDefaultWidth);
        jtableTime.getColumnModel().getColumn(9).setPreferredWidth(tableDefaultWidth);
        jtableTime.getColumnModel().getColumn(10).setPreferredWidth(tableDefaultWidth);
        jtableTime.getColumnModel().getColumn(11).setPreferredWidth(tableDefaultWidth);
        jtableTime.getColumnModel().getColumn(12).setPreferredWidth(tableDefaultWidth);
        jtableTime.getColumnModel().getColumn(13).setPreferredWidth(tableDefaultWidth);
        jtableTime.getColumnModel().getColumn(14).setPreferredWidth(tableDefaultWidth);
        jtableTime.getColumnModel().getColumn(15).setPreferredWidth(tableDefaultWidth);
        jtableTime.getColumnModel().getColumn(16).setPreferredWidth(tableDefaultWidth);
        jtableTime.getColumnModel().getColumn(17).setPreferredWidth(tableDefaultWidth);
        jtableTime.getColumnModel().getColumn(18).setPreferredWidth(tableDefaultWidth);

        jtableTime.setRowHeight(tableDefaultHeight);
        dtTime = (DefaultTableModel) jtableTime.getModel();
        dtTime.setRowCount(0);
        loadTimeTable();
    }

    private void loadTimeTable() {
        if (dtTime == null) {
            return;
        }
        dtTime.setRowCount(0);
        for (DisplayItem di : displayItems.getDisplayItems()) {
            try {

                Vector v = new Vector();
                v.add(false);
                v.add(di.getID());
                v.add(di.getIP());
                v.add(di.getPowerOn());

                //start list adjustment value
                v.add(Unit.TIME_MODE[di.getTimeMode()]);
                v.add(di.getMondayOn());
                v.add(di.getMondayOff());
                v.add(di.getTuesdayOn());
                v.add(di.getTuesdayOff());
                v.add(di.getWednesdayOn());
                v.add(di.getWednesdayOff());
                v.add(di.getThursdayOn());
                v.add(di.getThursdayOff());
                v.add(di.getFridayOn());
                v.add(di.getFridayOff());
                v.add(di.getSaturdayOn());
                v.add(di.getSaturdayOff());
                v.add(di.getSundayOn());
                v.add(di.getSundayOff());

                dtTime.addRow(v);
            } catch (Exception exp) {
                continue;
            }
        }
    }

    private void loadTimeTable(DisplayItem di) {
        if (di == null) {
            return;
        }

        for (int i = 0; i < dtPower.getRowCount(); i++) {
            Byte id = Byte.parseByte(dtPower.getValueAt(i, 1).toString());
            String ip = dtPower.getValueAt(i, 2).toString();
            if (id == di.getID()) {
                if (!set.useCOM && ip != di.getIP()) {
                    continue;
                }
                try {
                    dtAdjustment.setValueAt(di.getPowerOn(), i, 3);

                    //start list adjustment value
                    dtAdjustment.setValueAt(Unit.TIME_MODE[di.getTimeMode()], i, 4);
                    dtAdjustment.setValueAt(di.getMondayOn(), i, 5);
                    dtAdjustment.setValueAt(di.getMondayOff(), i, 6);
                    dtAdjustment.setValueAt(di.getTuesdayOn(), i, 7);
                    dtAdjustment.setValueAt(di.getTuesdayOff(), i, 8);

                    dtAdjustment.setValueAt(di.getWednesdayOn(), i, 9);
                    dtAdjustment.setValueAt(di.getWednesdayOff(), i, 10);
                    dtAdjustment.setValueAt(di.getThursdayOn(), i, 11);
                    dtAdjustment.setValueAt(di.getThursdayOff(), i, 12);
                    dtAdjustment.setValueAt(di.getFridayOn(), i, 13);
                    dtAdjustment.setValueAt(di.getFridayOff(), i, 14);

                    dtAdjustment.setValueAt(di.getSaturdayOn(), i, 15);
                    dtAdjustment.setValueAt(di.getSaturdayOff(), i, 16);
                    dtAdjustment.setValueAt(di.getSundayOn(), i, 17);
                    dtAdjustment.setValueAt(di.getSundayOff(), i, 18);

                } catch (Exception exp) {
                    continue;
                }
            }

        }
    }
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Logging">
    private void runLoggingTimer() {
        tmrLogging.schedule(new TimerTask() {
            @Override
            public void run() {

                //update Time
                Date ScanTime = new Date();
                lblLastLoggingTime.setText("<HTML><U>Last update : " + sdf.format(ScanTime) + "<U><HTML>");

                //Check  the  new dayt
                checkLogFolder();

                //save data to log after second scan
                if (!firstLogging) {
                    for (DisplayItem di : displayItems.displayItem) {
                        int index = displayItems.getDisplayItemIndexByID(di.getID());
                        for (Displays dis : logDisplays) {
                            // 未來power on 才紀錄 if (di.getPowerOn() && Byte.toString(di.getID()).equals(dis.name)) {
                            if (Byte.toString(di.getID()).equals(dis.name)) {
                                displayItems.displayItem.get(index).setLastUpdateTime(new Date());
                                dis.LastLoggingTime = ScanTime;
                                dis.addDisplayItem(displayItems.displayItem.get(index));
                                dis.saveXml();
                                break;
                            }
                        }
                    }
                }
                firstLogging = false;

                if (set.useCOM) {
                    logRS232();
                } else {
                    logEthernet();
                }
            }
        }, 30000, 30000); //Start logging after 60 seconds.  tmrLoggingMin
        //  }, 60 * 1000, tmrLoggingMin * 60 * 1000); //Start logging after 60 seconds.  tmrLoggingMin
    }

    private void logRS232() {

        if (!comDisplay.IsConnected) {
            comDisplay.Connect();
        }
        if (!comDisplay.IsConnected) {
            for (int i = 0; i < displayItems.displayItem.size(); i++) {
                displayItems.displayItem.get(i).isConnected = false;  //Mark each displayitem Isconnected  is false
            }
            return;
        }

        for (int i = 0; i < displayItems.displayItem.size(); i++) {
            try {
                DisplayItem di = displayItems.displayItem.get(i);

                displayItems.displayItem.get(i).isConnected = true;

                //send command  if  PowerOn preperty is true
                if (!di.getPowerOn()) {
                    continue;
                }

                logSendCommand(comDisplay, di.getID());
            } catch (Exception exp) {
                //Functions.showMsg(jTextAreaInfo, exp.getMessage());
                continue;

            }
        }

    }

    private void logEthernet() {

        for (Display display : netDisplays) {
            try {
                int index = displayItems.getDisplayItemIndexByIP(display.ip);
                if (index == -1) {
                    continue;
                }
                logNetSendCommand(display, index);
            } catch (Exception exp) {
                //Functions.showMsg(jTextAreaInfo, exp.getMessage());
                continue;
            }
        }
    }

    public void logNetSendCommand(final Display display, final int index) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                if (!display.IsConnected) {
                    display.Connect();
                }
                if (!display.IsConnected) {
                    displayItems.displayItem.get(index).isConnected = false;  //Mark each displayitem Isconnected  is false
                    return;
                }
                displayItems.displayItem.get(index).isConnected = true;  //Mark each displayitem Isconnected  is true
                if (!displayItems.displayItem.get(index).getPowerOn()) {
                    return;
                }

                logSendCommand(display, displayItems.displayItem.get(index).getID());
            }
        }, 100);

    }

    public void logSendCommand(Display display, byte id) {

        if (set.logVolume) {
            display.GetVolume(id);

        }

        if (set.logBacklightSatus) {
            display.GetBackLightStatus(id);

        }

        if (set.logBrightness) {
            display.GetBL_Brightness(id);

        }

        if (set.logDigitalBrightnessLevel) {
            display.GetDigitalBrightnessLevel(id);

        }

        if (set.logColorTemperature) {
            display.GetColorTemperature(id);

        }

        if (set.logContrast) {
            display.GetContrast(id);

        }

        if (set.logFan0Speed) {
            display.GetFan0Speed(id);
        }

        if (set.logFan1Speed) {
            display.GetFan1Speed(id);

        }

        if (set.logInputSource) {
            display.GetInputSource(id);

        }

        if (set.logMuteStatus) {
            display.GetMuteStatus(id);
        }

        if (set.logPhase) {
            display.GetPhase(id);
        }

    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Get Status">
    public void GetNetInputStatus(final Display display, final int index) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                if (!display.IsConnected) {
                    display.Connect();
                    if (display.IsConnected) {
                        displayAddListener(display);
                    } else {
                        Functions.showMsg(jTextAreaInfo, "Connect to Display[" + display.ip + "] is  time out.");
                    }
                }
                if (!display.IsConnected) {
                    displayItems.displayItem.get(index).isConnected = false;  //Mark each displayitem Isconnected  is false
                    return;
                }
                displayItems.displayItem.get(index).isConnected = true;  //Mark each displayitem Isconnected  is true
                if (!displayItems.displayItem.get(index).getPowerOn()) {
                    return;
                }
                display.GetInputSource(displayItems.displayItem.get(index).getID());
            }
        }, 100);
    }

    public void GetNetAdjustmentStatus(final Display display, final int index) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                if (!display.IsConnected) {
                    display.Connect();
                    if (display.IsConnected) {
                        displayAddListener(display);
                    } else {
                        Functions.showMsg(jTextAreaInfo, "Connect to Display[" + display.ip + "] is  time out.");
                    }
                }
                if (!display.IsConnected) {
                    displayItems.displayItem.get(index).isConnected = false;  //Mark each displayitem Isconnected  is false
                    return;
                }
                displayItems.displayItem.get(index).isConnected = true;  //Mark each displayitem Isconnected  is true
                if (!displayItems.displayItem.get(index).getPowerOn()) {
                    return;
                }
                GetAdjustmentStatus(display, displayItems.displayItem.get(index).getID());
            }
        }, 100);
    }

    private void GetAdjustmentStatus(Display display, byte id) {
        //General
        display.GetBL_Brightness(id);
        display.GetDigitalBrightnessLevel(id);
        display.GetContrast(id);
        display.GetHue(id);
        display.GetSaturation(id);
        display.GetPhase(id);
        display.GetClock(id);
        display.GetSharpness(id);
        display.GetPowerOnDelay(id);

        //Color
        display.GetColorTemperature(id);
        display.GetGamma(id);
        display.GetRedGain(id);
        display.GetGreenGain(id);
        display.GetBlueGain(id);
        display.GetRedOffset(id);
        display.GetGreenOffset(id);
        display.GetBlueOffset(id);
    }

    public void GetNetPIP_Status(final Display display, final int index) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                if (!display.IsConnected) {
                    display.Connect();
                    if (display.IsConnected) {
                        displayAddListener(display);
                    } else {
                        Functions.showMsg(jTextAreaInfo, "Connect to Display[" + display.ip + "] is  time out.");
                    }
                }
                if (!display.IsConnected) {
                    displayItems.displayItem.get(index).isConnected = false;  //Mark each displayitem Isconnected  is false
                    return;
                }
                displayItems.displayItem.get(index).isConnected = true;  //Mark each displayitem Isconnected  is true
                if (!displayItems.displayItem.get(index).getPowerOn()) {
                    return;
                }
                GetPIPStatus(display, displayItems.displayItem.get(index).getID());
            }
        }, 100);
    }

    private void GetPIPStatus(Display display, byte id) {
        display.GetPIP_Adjust(id);
        display.GetPIP_Position(id);
        display.GetPIP_SourceSelect(id);
    }

    public void GetNetTimeStatus(final Display display, final int index) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                if (!display.IsConnected) {
                    display.Connect();
                    if (display.IsConnected) {
                        displayAddListener(display);
                    } else {
                        Functions.showMsg(jTextAreaInfo, "Connect to Display[" + display.ip + "] is  time out.");
                    }
                }
                if (!display.IsConnected) {
                    displayItems.displayItem.get(index).isConnected = false;  //Mark each displayitem Isconnected  is false
                    return;
                }
                displayItems.displayItem.get(index).isConnected = true;  //Mark each displayitem Isconnected  is true
                if (!displayItems.displayItem.get(index).getPowerOn()) {
                    return;
                }
                GetTimeStatus(display, displayItems.displayItem.get(index).getID());
            }
        }, 100);
    }

    private void GetTimeStatus(Display display, byte id) {
        display.GetTimeMdoe(id);
        display.GetMondayTime(id);
        display.GetTuesdayTime(id);
        display.GetWednesdayTime(id);
        display.GetThursdayTime(id);
        display.GetFridayTime(id);
        display.GetSaturdayTime(id);
        display.GetSundayTime(id);
    }
    //</editor-fold>

    /**
     * Creates new form CMD
     */
    public CMD() {
        initComponents();
    }

    protected class ImagePowerRenderer extends DefaultTableCellRenderer {

        JLabel lbl = new JLabel();

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            lbl.setHorizontalAlignment(CENTER);
            if (Boolean.parseBoolean(value.toString())) {
                lbl.setIcon(imgPowerOn);
            } else {
                lbl.setIcon(imgPowerOff);
            }

            return lbl;
        }
    }

    protected class ImageConnRenderer extends DefaultTableCellRenderer {

        JLabel lbl = new JLabel();

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            lbl.setHorizontalAlignment(CENTER);
            if (Boolean.parseBoolean(value.toString())) {
                lbl.setIcon(imgConnect);
            } else {
                lbl.setIcon(imgDisconnect);
            }

            return lbl;
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

        tabSource = new javax.swing.JTabbedPane();
        tabPowerControl = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jtablePower = new javax.swing.JTable();
        jbtnPowerSelectAll = new javax.swing.JButton();
        jbtnPowerUnselectAll = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel36 = new javax.swing.JLabel();
        jsliderVolume = new javax.swing.JSlider();
        valVolume = new javax.swing.JLabel();
        jbtnVolume = new javax.swing.JButton();
        jbtnMuteOn = new javax.swing.JButton();
        jbtnMuteOff = new javax.swing.JButton();
        jLabel37 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        jbtnPowerOn = new javax.swing.JButton();
        jbtnPowerOff = new javax.swing.JButton();
        jbtnBL_On = new javax.swing.JButton();
        jbtnBL_Off = new javax.swing.JButton();
        jLabel39 = new javax.swing.JLabel();
        jbtnAutoAdjust = new javax.swing.JButton();
        jbtnUnlockKey = new javax.swing.JButton();
        jbtnLockKey = new javax.swing.JButton();
        jbtnPowerRefresh = new javax.swing.JButton();
        jbtnReconnect = new javax.swing.JButton();
        lblLastLoggingTime = new javax.swing.JLabel();
        jbtnOpenLogFolder = new javax.swing.JButton();
        jbtnWOD_On = new javax.swing.JButton();
        jbtnWOD_Off = new javax.swing.JButton();
        valBacklight = new javax.swing.JLabel();
        jsliderBacklight = new javax.swing.JSlider();
        jbtnBacklight = new javax.swing.JButton();
        lblServiceType = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        tabInputSource = new javax.swing.JPanel();
        jbtnInputSelectAll = new javax.swing.JButton();
        jbtnInputUnselectAll = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        jtableInput = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jbtnComposite2 = new javax.swing.JButton();
        jbtnHDMI1 = new javax.swing.JButton();
        jbtnSVideo = new javax.swing.JButton();
        jbtnHDMI2 = new javax.swing.JButton();
        jbtnVGA = new javax.swing.JButton();
        jbtnDVI = new javax.swing.JButton();
        jbtnComponent1 = new javax.swing.JButton();
        jbtnDisplayPort = new javax.swing.JButton();
        jbtnComposite1 = new javax.swing.JButton();
        jbtnComponent2 = new javax.swing.JButton();
        jbtnHDMI3 = new javax.swing.JButton();
        jbtnHDMI4 = new javax.swing.JButton();
        jbtnHDSDI1 = new javax.swing.JButton();
        jbtnHDSDI2 = new javax.swing.JButton();
        jbtnInputRefresh = new javax.swing.JButton();
        tabDisplayAdjustment = new javax.swing.JPanel();
        jbtnAdjustSelectAll = new javax.swing.JButton();
        jbtnAdjustUnselectAll = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        jtableAdjustment = new javax.swing.JTable();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        tabAdjustment = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jsliderBrightness = new javax.swing.JSlider();
        valBrightness = new javax.swing.JLabel();
        jbtnBrightness = new javax.swing.JButton();
        jbtnDBrightness = new javax.swing.JButton();
        valDBrightness = new javax.swing.JLabel();
        jsliderDBrightness = new javax.swing.JSlider();
        jsliderContrast = new javax.swing.JSlider();
        jsliderHue = new javax.swing.JSlider();
        jsliderSaturation = new javax.swing.JSlider();
        jsliderPhase = new javax.swing.JSlider();
        jsliderClock = new javax.swing.JSlider();
        jsliderSharpness = new javax.swing.JSlider();
        valContrast = new javax.swing.JLabel();
        valHue = new javax.swing.JLabel();
        valSaturation = new javax.swing.JLabel();
        valPhase = new javax.swing.JLabel();
        valClock = new javax.swing.JLabel();
        valSharpness = new javax.swing.JLabel();
        jbtnHue = new javax.swing.JButton();
        jbtnContrast = new javax.swing.JButton();
        jbtnSaturation = new javax.swing.JButton();
        jbtnPhase = new javax.swing.JButton();
        jbtnClock = new javax.swing.JButton();
        jbtnSharpness = new javax.swing.JButton();
        jLabel48 = new javax.swing.JLabel();
        jsliderPowerOnDelay = new javax.swing.JSlider();
        valPowerOnDelay = new javax.swing.JLabel();
        jbtnPowerOnDelay = new javax.swing.JButton();
        tabScaling = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jsliderColorTemp = new javax.swing.JSlider();
        jsliderGamma = new javax.swing.JSlider();
        jsliderRGain = new javax.swing.JSlider();
        jsliderGGain = new javax.swing.JSlider();
        jsliderBGain = new javax.swing.JSlider();
        jsliderROffset = new javax.swing.JSlider();
        jsliderGOffset = new javax.swing.JSlider();
        jsliderBOffset = new javax.swing.JSlider();
        valGamma = new javax.swing.JLabel();
        valColorTemp = new javax.swing.JLabel();
        valRGain = new javax.swing.JLabel();
        valGGain = new javax.swing.JLabel();
        valBGain = new javax.swing.JLabel();
        valROffset = new javax.swing.JLabel();
        valBOffset = new javax.swing.JLabel();
        valGOffset = new javax.swing.JLabel();
        jbtnBOffset = new javax.swing.JButton();
        jbtnGamma = new javax.swing.JButton();
        jbtnColorTemp = new javax.swing.JButton();
        jbtnRGain = new javax.swing.JButton();
        jbtnGGain = new javax.swing.JButton();
        jbtnBGain = new javax.swing.JButton();
        jbtnROffset = new javax.swing.JButton();
        jbtnGOffset = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JSeparator();
        jPanel4 = new javax.swing.JPanel();
        jLabel34 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jbtnNative = new javax.swing.JButton();
        jbtnFill = new javax.swing.JButton();
        jbtnLetterBox = new javax.swing.JButton();
        jbtnPillarBox = new javax.swing.JButton();
        jbtnZoomIn = new javax.swing.JButton();
        jbtnZoomOut = new javax.swing.JButton();
        jbtnSport = new javax.swing.JButton();
        jbtnUser = new javax.swing.JButton();
        jbtnGame = new javax.swing.JButton();
        jbtnVivid = new javax.swing.JButton();
        jbtnCinema = new javax.swing.JButton();
        jbtnAdjustRefresh = new javax.swing.JButton();
        tabPIP = new javax.swing.JPanel();
        jbtnPipSelectAll = new javax.swing.JButton();
        jbtnPipUnselectAll = new javax.swing.JButton();
        jScrollPane5 = new javax.swing.JScrollPane();
        jtablePIP = new javax.swing.JTable();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        tabPipSource = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jbtnVGA1 = new javax.swing.JButton();
        jbtnDVI1 = new javax.swing.JButton();
        jbtnDisplayPort1 = new javax.swing.JButton();
        jbtnSVideo1 = new javax.swing.JButton();
        jbtnComposite11 = new javax.swing.JButton();
        jbtnComposite12 = new javax.swing.JButton();
        jbtnComponent12 = new javax.swing.JButton();
        jbtnComponent11 = new javax.swing.JButton();
        jbtnHDMI11 = new javax.swing.JButton();
        jbtnHDMI12 = new javax.swing.JButton();
        jbtnHDMI14 = new javax.swing.JButton();
        jbtnHDMI13 = new javax.swing.JButton();
        jbtnHDSDI11 = new javax.swing.JButton();
        jbtnHDSDI12 = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jbtnSwap = new javax.swing.JButton();
        tabPipSize = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jbtnOff = new javax.swing.JButton();
        jbtnSmall = new javax.swing.JButton();
        jbtnMedium = new javax.swing.JButton();
        jbtnLarge = new javax.swing.JButton();
        jbtnSideBySide = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jbtnButtonLeft = new javax.swing.JButton();
        jbtnButtonRight = new javax.swing.JButton();
        jbtnTopRight = new javax.swing.JButton();
        jbtnTopLeft = new javax.swing.JButton();
        jbtnPipRefresh = new javax.swing.JButton();
        tabTime = new javax.swing.JPanel();
        jbtnTimeSelectAll = new javax.swing.JButton();
        jbtnTimeUnselectAll = new javax.swing.JButton();
        jScrollPane6 = new javax.swing.JScrollPane();
        jtableTime = new javax.swing.JTable();
        jPanel5 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jbtnCheckTime = new javax.swing.JButton();
        jcobTimeMode = new javax.swing.JComboBox();
        jLabel33 = new javax.swing.JLabel();
        jbtnTimeApply = new javax.swing.JButton();
        jpanTimeMode = new javax.swing.JPanel();
        jpanSAT = new javax.swing.JPanel();
        jcbSat = new javax.swing.JCheckBox();
        jsSatOn = new javax.swing.JSpinner();
        jLabel40 = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        jsSatOff = new javax.swing.JSpinner();
        jpanMon = new javax.swing.JPanel();
        jcbMon = new javax.swing.JCheckBox();
        jsMonOn = new javax.swing.JSpinner();
        jLabel42 = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();
        jsMonOff = new javax.swing.JSpinner();
        jpanTUE = new javax.swing.JPanel();
        jcbTue = new javax.swing.JCheckBox();
        jsTueOn = new javax.swing.JSpinner();
        jLabel44 = new javax.swing.JLabel();
        jLabel45 = new javax.swing.JLabel();
        jsTueOff = new javax.swing.JSpinner();
        jpanWED = new javax.swing.JPanel();
        jcbWed = new javax.swing.JCheckBox();
        jsWedOn = new javax.swing.JSpinner();
        jLabel46 = new javax.swing.JLabel();
        jLabel47 = new javax.swing.JLabel();
        jsWedOff = new javax.swing.JSpinner();
        jpanFRI = new javax.swing.JPanel();
        jcbFri = new javax.swing.JCheckBox();
        jsFriOn = new javax.swing.JSpinner();
        jLabel49 = new javax.swing.JLabel();
        jLabel50 = new javax.swing.JLabel();
        jsFriOff = new javax.swing.JSpinner();
        jpanTHU = new javax.swing.JPanel();
        jcbThu = new javax.swing.JCheckBox();
        jsThuOn = new javax.swing.JSpinner();
        jLabel51 = new javax.swing.JLabel();
        jLabel52 = new javax.swing.JLabel();
        jsThuOff = new javax.swing.JSpinner();
        jpanSUN = new javax.swing.JPanel();
        jcbSun = new javax.swing.JCheckBox();
        jsSunOn = new javax.swing.JSpinner();
        jLabel53 = new javax.swing.JLabel();
        jLabel54 = new javax.swing.JLabel();
        jsSunOff = new javax.swing.JSpinner();
        jbtnTimeRefresh = new javax.swing.JButton();
        jToolBar2 = new javax.swing.JToolBar();
        jLabel2 = new javax.swing.JLabel();
        btnID_List = new javax.swing.JButton();
        jbtnSetting = new javax.swing.JButton();
        jlblNote = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextAreaInfo = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("CHILIN Multiple Display Control (CMDC)");
        setBackground(new java.awt.Color(102, 102, 102));
        setIconImages(null);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        tabSource.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        tabSource.setPreferredSize(new java.awt.Dimension(705, 461));
        tabSource.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                tabSourceStateChanged(evt);
            }
        });

        tabPowerControl.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jScrollPane1.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N

        jtablePower.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jtablePower.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "", "ID", "IP", "PWR", "Link", "Backlight Switch", "Brightness", "Color temp.", "Contrast", "Backlight", "Fan 0 Speed", "Fan 1 Speed", "Input", "Mute", "Phase", "Volume"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jtablePower.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jtablePower.setRowHeight(20);
        jScrollPane1.setViewportView(jtablePower);

        tabPowerControl.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 45, 690, 180));

        jbtnPowerSelectAll.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jbtnPowerSelectAll.setText("Select All");
        jbtnPowerSelectAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnPowerSelectAllActionPerformed(evt);
            }
        });
        tabPowerControl.add(jbtnPowerSelectAll, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, -1, -1));

        jbtnPowerUnselectAll.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jbtnPowerUnselectAll.setText("Clear All");
        jbtnPowerUnselectAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnPowerUnselectAllActionPerformed(evt);
            }
        });
        tabPowerControl.add(jbtnPowerUnselectAll, new org.netbeans.lib.awtextra.AbsoluteConstraints(103, 10, -1, -1));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Power"));
        jPanel1.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        tabPowerControl.add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(314, 248, 210, -1));

        jLabel36.setBackground(new java.awt.Color(0, 51, 102));
        jLabel36.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        jLabel36.setForeground(new java.awt.Color(255, 255, 255));
        jLabel36.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel36.setText("Back Light");
        jLabel36.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel36.setOpaque(true);
        tabPowerControl.add(jLabel36, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 300, 210, 26));

        jsliderVolume.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jsliderVolumeStateChanged(evt);
            }
        });
        tabPowerControl.add(jsliderVolume, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 308, 120, -1));

        valVolume.setText("50");
        tabPowerControl.add(valVolume, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 310, 20, 20));

        jbtnVolume.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jbtnVolume.setText("OK");
        jbtnVolume.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PowerControlActionPerformed(evt);
            }
        });
        tabPowerControl.add(jbtnVolume, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 305, 60, -1));

        jbtnMuteOn.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jbtnMuteOn.setText("Mute ON");
        jbtnMuteOn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PowerControlActionPerformed(evt);
            }
        });
        tabPowerControl.add(jbtnMuteOn, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 270, 90, 25));

        jbtnMuteOff.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jbtnMuteOff.setText("Mute OFF");
        jbtnMuteOff.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PowerControlActionPerformed(evt);
            }
        });
        tabPowerControl.add(jbtnMuteOff, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 270, 100, 25));

        jLabel37.setBackground(new java.awt.Color(0, 51, 102));
        jLabel37.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        jLabel37.setForeground(new java.awt.Color(255, 255, 255));
        jLabel37.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel37.setText("Volume");
        jLabel37.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel37.setOpaque(true);
        tabPowerControl.add(jLabel37, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 230, 210, 26));

        jLabel38.setBackground(new java.awt.Color(0, 51, 102));
        jLabel38.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        jLabel38.setForeground(new java.awt.Color(255, 255, 255));
        jLabel38.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel38.setText("Other");
        jLabel38.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel38.setOpaque(true);
        tabPowerControl.add(jLabel38, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 230, 200, 26));

        jbtnPowerOn.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jbtnPowerOn.setText("Power ON");
        jbtnPowerOn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PowerControlActionPerformed(evt);
            }
        });
        tabPowerControl.add(jbtnPowerOn, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 270, 100, 25));

        jbtnPowerOff.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jbtnPowerOff.setText("Power OFF");
        jbtnPowerOff.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PowerControlActionPerformed(evt);
            }
        });
        tabPowerControl.add(jbtnPowerOff, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 270, 100, 25));

        jbtnBL_On.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jbtnBL_On.setText("BL ON");
        jbtnBL_On.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PowerControlActionPerformed(evt);
            }
        });
        tabPowerControl.add(jbtnBL_On, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 330, 90, 25));

        jbtnBL_Off.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jbtnBL_Off.setText("BL OFF");
        jbtnBL_Off.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PowerControlActionPerformed(evt);
            }
        });
        tabPowerControl.add(jbtnBL_Off, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 330, 100, 25));

        jLabel39.setBackground(new java.awt.Color(0, 51, 102));
        jLabel39.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        jLabel39.setForeground(new java.awt.Color(255, 255, 255));
        jLabel39.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel39.setText("Power");
        jLabel39.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel39.setOpaque(true);
        tabPowerControl.add(jLabel39, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 230, 210, 26));

        jbtnAutoAdjust.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jbtnAutoAdjust.setText("Auto Adjustment");
        jbtnAutoAdjust.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PowerControlActionPerformed(evt);
            }
        });
        tabPowerControl.add(jbtnAutoAdjust, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 330, 200, 25));

        jbtnUnlockKey.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jbtnUnlockKey.setText("Unlock Key");
        jbtnUnlockKey.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PowerControlActionPerformed(evt);
            }
        });
        tabPowerControl.add(jbtnUnlockKey, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 270, 100, 25));

        jbtnLockKey.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jbtnLockKey.setText("Lock Key");
        jbtnLockKey.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PowerControlActionPerformed(evt);
            }
        });
        tabPowerControl.add(jbtnLockKey, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 270, 90, 25));

        jbtnPowerRefresh.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jbtnPowerRefresh.setText("Get Status");
        jbtnPowerRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnPowerRefreshActionPerformed(evt);
            }
        });
        tabPowerControl.add(jbtnPowerRefresh, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 340, 100, -1));

        jbtnReconnect.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jbtnReconnect.setText("Reconnect");
        jbtnReconnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnReconnectActionPerformed(evt);
            }
        });
        tabPowerControl.add(jbtnReconnect, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 10, 100, -1));

        lblLastLoggingTime.setBackground(new java.awt.Color(204, 204, 204));
        lblLastLoggingTime.setFont(new java.awt.Font("Vrinda", 0, 14)); // NOI18N
        lblLastLoggingTime.setForeground(new java.awt.Color(255, 255, 255));
        lblLastLoggingTime.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblLastLoggingTime.setText("<HTML><U>Last Update:<U><HTML>");
        tabPowerControl.add(lblLastLoggingTime, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 20, 260, 20));
        lblLastLoggingTime.getAccessibleContext().setAccessibleName("<HTML><U>Add to ID List<U><HTML>");

        jbtnOpenLogFolder.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jbtnOpenLogFolder.setText("Oepn Log Folder");
        jbtnOpenLogFolder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnOpenLogFolderPowerControlActionPerformed(evt);
            }
        });
        tabPowerControl.add(jbtnOpenLogFolder, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 360, 200, 25));

        jbtnWOD_On.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jbtnWOD_On.setText("WOD ON");
        jbtnWOD_On.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PowerControlActionPerformed(evt);
            }
        });
        tabPowerControl.add(jbtnWOD_On, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 300, 90, 25));

        jbtnWOD_Off.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jbtnWOD_Off.setText("WOD OFF");
        jbtnWOD_Off.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PowerControlActionPerformed(evt);
            }
        });
        tabPowerControl.add(jbtnWOD_Off, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 300, 100, 25));

        valBacklight.setText("50");
        tabPowerControl.add(valBacklight, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 360, 20, 20));

        jsliderBacklight.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jsliderBacklightStateChanged(evt);
            }
        });
        tabPowerControl.add(jsliderBacklight, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 360, 110, -1));

        jbtnBacklight.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jbtnBacklight.setText("OK");
        jbtnBacklight.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PowerControlActionPerformed(evt);
            }
        });
        tabPowerControl.add(jbtnBacklight, new org.netbeans.lib.awtextra.AbsoluteConstraints(389, 360, 60, -1));

        lblServiceType.setBackground(new java.awt.Color(204, 204, 204));
        lblServiceType.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        lblServiceType.setForeground(new java.awt.Color(255, 255, 255));
        lblServiceType.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblServiceType.setText("Logging Service: Stop");
        lblServiceType.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblServiceType.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblServiceTypeMouseClicked(evt);
            }
        });
        tabPowerControl.add(lblServiceType, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 5, 260, 20));

        jPanel2.setBackground(new java.awt.Color(102, 102, 102));
        jPanel2.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 264, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 31, Short.MAX_VALUE)
        );

        tabPowerControl.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 5, 270, 37));

        tabSource.addTab("Power Control", new javax.swing.ImageIcon(getClass().getResource("/multipledisplay/power_control.png")), tabPowerControl); // NOI18N

        jbtnInputSelectAll.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jbtnInputSelectAll.setText("Select All");
        jbtnInputSelectAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnInputSelectAllActionPerformed(evt);
            }
        });

        jbtnInputUnselectAll.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jbtnInputUnselectAll.setText("Clear All");
        jbtnInputUnselectAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnInputUnselectAllActionPerformed(evt);
            }
        });

        jtableInput.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "", "ID", "IP", "PWR", "Input"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
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
        jtableInput.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jScrollPane3.setViewportView(jtableInput);

        jPanel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel3.setLayout(null);

        jLabel1.setBackground(new java.awt.Color(0, 51, 102));
        jLabel1.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Choose Input Source");
        jLabel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel1.setOpaque(true);
        jPanel3.add(jLabel1);
        jLabel1.setBounds(10, 10, 290, 26);

        jbtnComposite2.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jbtnComposite2.setText("Composite 2");
        jbtnComposite2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnInputActionPerformed(evt);
            }
        });
        jPanel3.add(jbtnComposite2);
        jbtnComposite2.setBounds(165, 120, 115, 25);

        jbtnHDMI1.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jbtnHDMI1.setText("HDMI1");
        jbtnHDMI1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnInputActionPerformed(evt);
            }
        });
        jPanel3.add(jbtnHDMI1);
        jbtnHDMI1.setBounds(40, 180, 115, 25);

        jbtnSVideo.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jbtnSVideo.setText("S-Video");
        jbtnSVideo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnInputActionPerformed(evt);
            }
        });
        jPanel3.add(jbtnSVideo);
        jbtnSVideo.setBounds(40, 90, 115, 25);

        jbtnHDMI2.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jbtnHDMI2.setText("HDMI2");
        jbtnHDMI2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnInputActionPerformed(evt);
            }
        });
        jPanel3.add(jbtnHDMI2);
        jbtnHDMI2.setBounds(165, 180, 115, 25);

        jbtnVGA.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jbtnVGA.setText("VGA");
        jbtnVGA.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnInputActionPerformed(evt);
            }
        });
        jPanel3.add(jbtnVGA);
        jbtnVGA.setBounds(40, 60, 115, 25);

        jbtnDVI.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jbtnDVI.setText("DVI");
        jbtnDVI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnInputActionPerformed(evt);
            }
        });
        jPanel3.add(jbtnDVI);
        jbtnDVI.setBounds(165, 60, 115, 25);

        jbtnComponent1.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jbtnComponent1.setText("Component 1");
        jbtnComponent1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnInputActionPerformed(evt);
            }
        });
        jPanel3.add(jbtnComponent1);
        jbtnComponent1.setBounds(40, 150, 115, 25);

        jbtnDisplayPort.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jbtnDisplayPort.setText("Display Port");
        jbtnDisplayPort.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnInputActionPerformed(evt);
            }
        });
        jPanel3.add(jbtnDisplayPort);
        jbtnDisplayPort.setBounds(165, 90, 115, 25);

        jbtnComposite1.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jbtnComposite1.setText("Composite 1");
        jbtnComposite1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnInputActionPerformed(evt);
            }
        });
        jPanel3.add(jbtnComposite1);
        jbtnComposite1.setBounds(40, 120, 115, 25);

        jbtnComponent2.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jbtnComponent2.setText("Component 2");
        jbtnComponent2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnInputActionPerformed(evt);
            }
        });
        jPanel3.add(jbtnComponent2);
        jbtnComponent2.setBounds(165, 150, 115, 25);

        jbtnHDMI3.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jbtnHDMI3.setText("HDMI3");
        jbtnHDMI3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnInputActionPerformed(evt);
            }
        });
        jPanel3.add(jbtnHDMI3);
        jbtnHDMI3.setBounds(40, 210, 115, 25);

        jbtnHDMI4.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jbtnHDMI4.setText("HDMI4");
        jbtnHDMI4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnInputActionPerformed(evt);
            }
        });
        jPanel3.add(jbtnHDMI4);
        jbtnHDMI4.setBounds(165, 210, 115, 25);

        jbtnHDSDI1.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jbtnHDSDI1.setText("HDSDI 1");
        jbtnHDSDI1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnInputActionPerformed(evt);
            }
        });
        jPanel3.add(jbtnHDSDI1);
        jbtnHDSDI1.setBounds(40, 240, 115, 25);

        jbtnHDSDI2.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jbtnHDSDI2.setText("HDSDI 2");
        jbtnHDSDI2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnInputActionPerformed(evt);
            }
        });
        jPanel3.add(jbtnHDSDI2);
        jbtnHDSDI2.setBounds(165, 240, 115, 25);

        jbtnInputRefresh.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jbtnInputRefresh.setText("Get Status");
        jbtnInputRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnInputRefreshActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout tabInputSourceLayout = new javax.swing.GroupLayout(tabInputSource);
        tabInputSource.setLayout(tabInputSourceLayout);
        tabInputSourceLayout.setHorizontalGroup(
            tabInputSourceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabInputSourceLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabInputSourceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(tabInputSourceLayout.createSequentialGroup()
                        .addComponent(jbtnInputSelectAll)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jbtnInputUnselectAll)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jbtnInputRefresh))
                    .addGroup(tabInputSourceLayout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 340, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 316, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(34, Short.MAX_VALUE))
        );
        tabInputSourceLayout.setVerticalGroup(
            tabInputSourceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabInputSourceLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabInputSourceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jbtnInputSelectAll)
                    .addComponent(jbtnInputUnselectAll)
                    .addComponent(jbtnInputRefresh))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(tabInputSourceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 339, Short.MAX_VALUE)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );

        tabSource.addTab("Input Source", new javax.swing.ImageIcon(getClass().getResource("/multipledisplay/input_source.png")), tabInputSource); // NOI18N

        jbtnAdjustSelectAll.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jbtnAdjustSelectAll.setText("Select All");
        jbtnAdjustSelectAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnAdjustSelectAllActionPerformed(evt);
            }
        });

        jbtnAdjustUnselectAll.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jbtnAdjustUnselectAll.setText("Clear All");
        jbtnAdjustUnselectAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnAdjustUnselectAllActionPerformed(evt);
            }
        });

        jtableAdjustment.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jtableAdjustment.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "", "ID", "IP", "Power", "Brightness", "Backlight", "Contrast", "Hue", "Saturation", "Phase", "Clock", "Sharpness", "PowerOnDelay", "Gamma", "Color Temp", "R Gain", "G Gain", "B Gain", "R Offset", "G Offset", "B Offset"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jtableAdjustment.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jScrollPane4.setViewportView(jtableAdjustment);

        jTabbedPane1.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N

        tabAdjustment.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        tabAdjustment.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel3.setBackground(new java.awt.Color(0, 51, 102));
        jLabel3.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("General");
        jLabel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel3.setOpaque(true);
        tabAdjustment.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(19, 9, 290, 26));
        jLabel3.getAccessibleContext().setAccessibleName("");

        jLabel7.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jLabel7.setText("Brightness");
        tabAdjustment.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 50, -1, -1));

        jLabel8.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jLabel8.setText("Backlight");
        tabAdjustment.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 80, 90, -1));

        jLabel9.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jLabel9.setText("Contrast");
        tabAdjustment.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 110, 60, -1));

        jLabel10.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jLabel10.setText("Hue");
        tabAdjustment.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 140, -1, -1));

        jLabel11.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jLabel11.setText("Saturation");
        tabAdjustment.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 170, -1, -1));
        tabAdjustment.add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 290, 309, 0));

        jLabel14.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jLabel14.setText("Clock");
        tabAdjustment.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 230, -1, -1));

        jLabel15.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jLabel15.setText("Phase");
        tabAdjustment.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 200, -1, -1));

        jLabel16.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jLabel16.setText("Sharpness");
        tabAdjustment.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 260, -1, -1));

        jsliderBrightness.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jsliderBrightnessStateChanged(evt);
            }
        });
        tabAdjustment.add(jsliderBrightness, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 50, 110, -1));

        valBrightness.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        valBrightness.setText("50");
        tabAdjustment.add(valBrightness, new org.netbeans.lib.awtextra.AbsoluteConstraints(225, 50, 30, 18));

        jbtnBrightness.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jbtnBrightness.setText("Set");
        jbtnBrightness.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnAdjustActionPerformed(evt);
            }
        });
        tabAdjustment.add(jbtnBrightness, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 50, 60, 20));

        jbtnDBrightness.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jbtnDBrightness.setText("Set");
        jbtnDBrightness.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnAdjustActionPerformed(evt);
            }
        });
        tabAdjustment.add(jbtnDBrightness, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 80, 60, 20));

        valDBrightness.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        valDBrightness.setText("50");
        tabAdjustment.add(valDBrightness, new org.netbeans.lib.awtextra.AbsoluteConstraints(225, 80, 30, 18));

        jsliderDBrightness.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jsliderDBrightnessStateChanged(evt);
            }
        });
        tabAdjustment.add(jsliderDBrightness, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 80, 110, -1));

        jsliderContrast.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jsliderContrastStateChanged(evt);
            }
        });
        tabAdjustment.add(jsliderContrast, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 110, 110, -1));

        jsliderHue.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jsliderHueStateChanged(evt);
            }
        });
        tabAdjustment.add(jsliderHue, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 140, 110, -1));

        jsliderSaturation.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jsliderSaturationStateChanged(evt);
            }
        });
        tabAdjustment.add(jsliderSaturation, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 170, 110, -1));

        jsliderPhase.setMaximum(63);
        jsliderPhase.setValue(31);
        jsliderPhase.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jsliderPhaseStateChanged(evt);
            }
        });
        tabAdjustment.add(jsliderPhase, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 200, 110, -1));

        jsliderClock.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jsliderClockStateChanged(evt);
            }
        });
        tabAdjustment.add(jsliderClock, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 230, 110, -1));

        jsliderSharpness.setMaximum(24);
        jsliderSharpness.setValue(12);
        jsliderSharpness.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jsliderSharpnessStateChanged(evt);
            }
        });
        tabAdjustment.add(jsliderSharpness, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 260, 110, -1));

        valContrast.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        valContrast.setText("50");
        tabAdjustment.add(valContrast, new org.netbeans.lib.awtextra.AbsoluteConstraints(225, 110, 30, 18));

        valHue.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        valHue.setText("50");
        tabAdjustment.add(valHue, new org.netbeans.lib.awtextra.AbsoluteConstraints(225, 140, 30, 18));

        valSaturation.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        valSaturation.setText("50");
        tabAdjustment.add(valSaturation, new org.netbeans.lib.awtextra.AbsoluteConstraints(225, 170, 30, 18));

        valPhase.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        valPhase.setText("31");
        tabAdjustment.add(valPhase, new org.netbeans.lib.awtextra.AbsoluteConstraints(225, 200, 30, 18));

        valClock.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        valClock.setText("50");
        tabAdjustment.add(valClock, new org.netbeans.lib.awtextra.AbsoluteConstraints(225, 230, 30, 18));

        valSharpness.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        valSharpness.setText("12");
        tabAdjustment.add(valSharpness, new org.netbeans.lib.awtextra.AbsoluteConstraints(225, 260, 30, 18));

        jbtnHue.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jbtnHue.setText("Set");
        jbtnHue.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnAdjustActionPerformed(evt);
            }
        });
        tabAdjustment.add(jbtnHue, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 140, 60, 20));

        jbtnContrast.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jbtnContrast.setText("Set");
        jbtnContrast.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnAdjustActionPerformed(evt);
            }
        });
        tabAdjustment.add(jbtnContrast, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 110, 60, 20));

        jbtnSaturation.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jbtnSaturation.setText("Set");
        jbtnSaturation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnAdjustActionPerformed(evt);
            }
        });
        tabAdjustment.add(jbtnSaturation, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 170, 60, 20));

        jbtnPhase.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jbtnPhase.setText("Set");
        jbtnPhase.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnAdjustActionPerformed(evt);
            }
        });
        tabAdjustment.add(jbtnPhase, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 200, 60, 20));

        jbtnClock.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jbtnClock.setText("Set");
        jbtnClock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnAdjustActionPerformed(evt);
            }
        });
        tabAdjustment.add(jbtnClock, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 230, 60, 20));

        jbtnSharpness.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jbtnSharpness.setText("Set");
        jbtnSharpness.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnAdjustActionPerformed(evt);
            }
        });
        tabAdjustment.add(jbtnSharpness, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 260, 60, 20));

        jLabel48.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jLabel48.setText("Power on Delay");
        tabAdjustment.add(jLabel48, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 290, -1, -1));

        jsliderPowerOnDelay.setMaximum(30);
        jsliderPowerOnDelay.setValue(15);
        jsliderPowerOnDelay.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jsliderPowerOnDelayStateChanged(evt);
            }
        });
        tabAdjustment.add(jsliderPowerOnDelay, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 290, 110, -1));

        valPowerOnDelay.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        valPowerOnDelay.setText("15");
        tabAdjustment.add(valPowerOnDelay, new org.netbeans.lib.awtextra.AbsoluteConstraints(225, 290, 30, 18));

        jbtnPowerOnDelay.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jbtnPowerOnDelay.setText("Set");
        jbtnPowerOnDelay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnAdjustActionPerformed(evt);
            }
        });
        tabAdjustment.add(jbtnPowerOnDelay, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 290, 60, 20));

        jTabbedPane1.addTab("General", tabAdjustment);

        tabScaling.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        tabScaling.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel17.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jLabel17.setText("Gamma");
        tabScaling.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 100, -1, -1));

        jLabel18.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jLabel18.setText("Color Temp.");
        tabScaling.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 50, -1, -1));

        jLabel19.setBackground(new java.awt.Color(0, 51, 102));
        jLabel19.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(255, 255, 255));
        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel19.setText("Color Adjustment");
        jLabel19.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel19.setOpaque(true);
        tabScaling.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(19, 10, 290, 26));

        jLabel20.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jLabel20.setText("R Gain");
        tabScaling.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 130, -1, -1));
        jLabel20.getAccessibleContext().setAccessibleName("");

        jLabel21.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jLabel21.setText("G Gain");
        tabScaling.add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 160, -1, -1));

        jLabel22.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jLabel22.setText("B Gain");
        tabScaling.add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 190, -1, -1));

        jLabel23.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jLabel23.setText("R Offset");
        tabScaling.add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 220, -1, -1));

        jLabel24.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jLabel24.setText("G Offset");
        tabScaling.add(jLabel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 250, -1, -1));

        jLabel25.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jLabel25.setText("B Offset");
        tabScaling.add(jLabel25, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 280, -1, -1));

        jsliderColorTemp.setMaximum(9);
        jsliderColorTemp.setValue(0);
        jsliderColorTemp.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jsliderColorTempStateChanged(evt);
            }
        });
        tabScaling.add(jsliderColorTemp, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 50, 100, -1));

        jsliderGamma.setMaximum(4);
        jsliderGamma.setValue(0);
        jsliderGamma.setEnabled(false);
        jsliderGamma.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jsliderGammaStateChanged(evt);
            }
        });
        tabScaling.add(jsliderGamma, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 100, 120, -1));

        jsliderRGain.setMaximum(255);
        jsliderRGain.setValue(127);
        jsliderRGain.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jsliderRGainStateChanged(evt);
            }
        });
        tabScaling.add(jsliderRGain, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 130, 120, -1));

        jsliderGGain.setMaximum(255);
        jsliderGGain.setValue(127);
        jsliderGGain.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jsliderGGainStateChanged(evt);
            }
        });
        tabScaling.add(jsliderGGain, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 160, 120, -1));

        jsliderBGain.setMaximum(255);
        jsliderBGain.setValue(127);
        jsliderBGain.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jsliderBGainStateChanged(evt);
            }
        });
        tabScaling.add(jsliderBGain, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 190, 120, -1));

        jsliderROffset.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jsliderROffsetStateChanged(evt);
            }
        });
        tabScaling.add(jsliderROffset, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 220, 120, -1));

        jsliderGOffset.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jsliderGOffsetStateChanged(evt);
            }
        });
        tabScaling.add(jsliderGOffset, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 250, 120, -1));

        jsliderBOffset.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jsliderBOffsetStateChanged(evt);
            }
        });
        tabScaling.add(jsliderBOffset, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 280, 120, -1));

        valGamma.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        valGamma.setText("OFF");
        tabScaling.add(valGamma, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 100, 30, 18));

        valColorTemp.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        valColorTemp.setText("User");
        tabScaling.add(valColorTemp, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 50, 60, 18));

        valRGain.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        valRGain.setText("0");
        tabScaling.add(valRGain, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 130, 30, 18));

        valGGain.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        valGGain.setText("0");
        tabScaling.add(valGGain, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 160, 30, 18));

        valBGain.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        valBGain.setText("0");
        tabScaling.add(valBGain, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 190, 30, 18));

        valROffset.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        valROffset.setText("0");
        tabScaling.add(valROffset, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 220, 30, 18));

        valBOffset.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        valBOffset.setText("0");
        tabScaling.add(valBOffset, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 280, 30, 18));

        valGOffset.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        valGOffset.setText("0");
        tabScaling.add(valGOffset, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 250, 30, 18));

        jbtnBOffset.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jbtnBOffset.setText("Set");
        jbtnBOffset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnAdjustActionPerformed(evt);
            }
        });
        tabScaling.add(jbtnBOffset, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 280, 60, 20));

        jbtnGamma.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jbtnGamma.setText("Set");
        jbtnGamma.setEnabled(false);
        jbtnGamma.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnAdjustActionPerformed(evt);
            }
        });
        tabScaling.add(jbtnGamma, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 100, 60, 20));

        jbtnColorTemp.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jbtnColorTemp.setText("Set");
        jbtnColorTemp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnAdjustActionPerformed(evt);
            }
        });
        tabScaling.add(jbtnColorTemp, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 48, 60, 20));

        jbtnRGain.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jbtnRGain.setText("Set");
        jbtnRGain.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnAdjustActionPerformed(evt);
            }
        });
        tabScaling.add(jbtnRGain, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 130, 60, 20));

        jbtnGGain.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jbtnGGain.setText("Set");
        jbtnGGain.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnAdjustActionPerformed(evt);
            }
        });
        tabScaling.add(jbtnGGain, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 160, 60, 20));

        jbtnBGain.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jbtnBGain.setText("Set");
        jbtnBGain.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnAdjustActionPerformed(evt);
            }
        });
        tabScaling.add(jbtnBGain, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 190, 60, 20));

        jbtnROffset.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jbtnROffset.setText("Set");
        jbtnROffset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnAdjustActionPerformed(evt);
            }
        });
        tabScaling.add(jbtnROffset, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 220, 60, 20));

        jbtnGOffset.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jbtnGOffset.setText("Set");
        jbtnGOffset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnAdjustActionPerformed(evt);
            }
        });
        tabScaling.add(jbtnGOffset, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 250, 60, 20));
        tabScaling.add(jSeparator2, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 80, 300, -1));

        jTabbedPane1.addTab("Color", tabScaling);

        jPanel4.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N

        jLabel34.setBackground(new java.awt.Color(0, 51, 102));
        jLabel34.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        jLabel34.setForeground(new java.awt.Color(255, 255, 255));
        jLabel34.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel34.setText("Scaling");
        jLabel34.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel34.setOpaque(true);

        jLabel35.setBackground(new java.awt.Color(0, 51, 102));
        jLabel35.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        jLabel35.setForeground(new java.awt.Color(255, 255, 255));
        jLabel35.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel35.setText("Scheme");
        jLabel35.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel35.setOpaque(true);

        jbtnNative.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jbtnNative.setText("Native");
        jbtnNative.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnAdjustActionPerformed(evt);
            }
        });

        jbtnFill.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jbtnFill.setText("Fill");
        jbtnFill.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnAdjustActionPerformed(evt);
            }
        });

        jbtnLetterBox.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jbtnLetterBox.setText("Letter Box");
        jbtnLetterBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnAdjustActionPerformed(evt);
            }
        });

        jbtnPillarBox.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jbtnPillarBox.setText("PILLAR Box");
        jbtnPillarBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnAdjustActionPerformed(evt);
            }
        });

        jbtnZoomIn.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jbtnZoomIn.setText("Zoom In");
        jbtnZoomIn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnAdjustActionPerformed(evt);
            }
        });

        jbtnZoomOut.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jbtnZoomOut.setText("Zoom Out");
        jbtnZoomOut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnAdjustActionPerformed(evt);
            }
        });

        jbtnSport.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jbtnSport.setText("Sport");
        jbtnSport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnAdjustActionPerformed(evt);
            }
        });

        jbtnUser.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jbtnUser.setText("User");
        jbtnUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnAdjustActionPerformed(evt);
            }
        });

        jbtnGame.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jbtnGame.setText("Game");
        jbtnGame.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnAdjustActionPerformed(evt);
            }
        });

        jbtnVivid.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jbtnVivid.setText("Vivid");
        jbtnVivid.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnAdjustActionPerformed(evt);
            }
        });

        jbtnCinema.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jbtnCinema.setText("Cinema");
        jbtnCinema.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnAdjustActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap(32, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jbtnNative, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(10, 10, 10)
                                .addComponent(jbtnFill, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jbtnPillarBox, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(10, 10, 10)
                                .addComponent(jbtnLetterBox, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jbtnZoomIn, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(10, 10, 10)
                                .addComponent(jbtnZoomOut, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(35, 35, 35))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jbtnUser, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(10, 10, 10)
                                .addComponent(jbtnSport, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jbtnGame, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jbtnCinema, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jbtnVivid, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(35, 35, 35))))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jbtnNative)
                    .addComponent(jbtnFill))
                .addGap(5, 5, 5)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jbtnPillarBox)
                    .addComponent(jbtnLetterBox))
                .addGap(5, 5, 5)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jbtnZoomIn)
                    .addComponent(jbtnZoomOut))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 29, Short.MAX_VALUE)
                .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jbtnUser)
                    .addComponent(jbtnSport))
                .addGap(5, 5, 5)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jbtnGame)
                    .addComponent(jbtnCinema))
                .addGap(5, 5, 5)
                .addComponent(jbtnVivid)
                .addGap(16, 16, 16))
        );

        jTabbedPane1.addTab("Scaling & Scheme", jPanel4);

        jbtnAdjustRefresh.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jbtnAdjustRefresh.setText("Get Status");
        jbtnAdjustRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnAdjustRefreshActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout tabDisplayAdjustmentLayout = new javax.swing.GroupLayout(tabDisplayAdjustment);
        tabDisplayAdjustment.setLayout(tabDisplayAdjustmentLayout);
        tabDisplayAdjustmentLayout.setHorizontalGroup(
            tabDisplayAdjustmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, tabDisplayAdjustmentLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(tabDisplayAdjustmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(tabDisplayAdjustmentLayout.createSequentialGroup()
                        .addComponent(jbtnAdjustSelectAll)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jbtnAdjustUnselectAll)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jbtnAdjustRefresh))
                    .addGroup(tabDisplayAdjustmentLayout.createSequentialGroup()
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 340, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 345, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(114, 114, 114))
        );
        tabDisplayAdjustmentLayout.setVerticalGroup(
            tabDisplayAdjustmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabDisplayAdjustmentLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabDisplayAdjustmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jbtnAdjustSelectAll)
                    .addComponent(jbtnAdjustUnselectAll)
                    .addComponent(jbtnAdjustRefresh))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(tabDisplayAdjustmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTabbedPane1)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );

        tabSource.addTab("Adjustment", new javax.swing.ImageIcon(getClass().getResource("/multipledisplay/imageSize.png")), tabDisplayAdjustment); // NOI18N

        jbtnPipSelectAll.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jbtnPipSelectAll.setText("Select All");
        jbtnPipSelectAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnPipSelectAllActionPerformed(evt);
            }
        });

        jbtnPipUnselectAll.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jbtnPipUnselectAll.setText("Clear All");
        jbtnPipUnselectAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnPipUnselectAllActionPerformed(evt);
            }
        });

        jtablePIP.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jtablePIP.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "", "ID", "IP", "PWR", "PIP Source", "PIP Size", "PIP Position"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                true, false, false, true, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jtablePIP.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jScrollPane5.setViewportView(jtablePIP);

        jTabbedPane2.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N

        tabPipSource.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        tabPipSource.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        tabPipSource.add(jPanel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(325, 202, -1, -1));

        jbtnVGA1.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jbtnVGA1.setText("VGA");
        jbtnVGA1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPIPActionPerformed(evt);
            }
        });
        tabPipSource.add(jbtnVGA1, new org.netbeans.lib.awtextra.AbsoluteConstraints(45, 51, 115, 25));

        jbtnDVI1.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jbtnDVI1.setText("DVI");
        jbtnDVI1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPIPActionPerformed(evt);
            }
        });
        tabPipSource.add(jbtnDVI1, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 50, 115, 25));

        jbtnDisplayPort1.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jbtnDisplayPort1.setText("Display Port");
        jbtnDisplayPort1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPIPActionPerformed(evt);
            }
        });
        tabPipSource.add(jbtnDisplayPort1, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 84, 115, 25));

        jbtnSVideo1.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jbtnSVideo1.setText("S-Video");
        jbtnSVideo1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPIPActionPerformed(evt);
            }
        });
        tabPipSource.add(jbtnSVideo1, new org.netbeans.lib.awtextra.AbsoluteConstraints(45, 84, 115, 25));

        jbtnComposite11.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jbtnComposite11.setText("Composite 1");
        jbtnComposite11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPIPActionPerformed(evt);
            }
        });
        tabPipSource.add(jbtnComposite11, new org.netbeans.lib.awtextra.AbsoluteConstraints(45, 113, 115, 25));

        jbtnComposite12.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jbtnComposite12.setText("Composite 2");
        jbtnComposite12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPIPActionPerformed(evt);
            }
        });
        tabPipSource.add(jbtnComposite12, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 110, 115, 25));

        jbtnComponent12.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jbtnComponent12.setText("Component 2");
        jbtnComponent12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPIPActionPerformed(evt);
            }
        });
        tabPipSource.add(jbtnComponent12, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 140, 115, 25));

        jbtnComponent11.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jbtnComponent11.setText("Component 1");
        jbtnComponent11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPIPActionPerformed(evt);
            }
        });
        tabPipSource.add(jbtnComponent11, new org.netbeans.lib.awtextra.AbsoluteConstraints(45, 142, 115, 25));

        jbtnHDMI11.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jbtnHDMI11.setText("HDMI1");
        jbtnHDMI11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPIPActionPerformed(evt);
            }
        });
        tabPipSource.add(jbtnHDMI11, new org.netbeans.lib.awtextra.AbsoluteConstraints(45, 171, 115, 25));

        jbtnHDMI12.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jbtnHDMI12.setText("HDMI2");
        jbtnHDMI12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPIPActionPerformed(evt);
            }
        });
        tabPipSource.add(jbtnHDMI12, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 171, 115, 25));

        jbtnHDMI14.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jbtnHDMI14.setText("HDMI4");
        jbtnHDMI14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPIPActionPerformed(evt);
            }
        });
        tabPipSource.add(jbtnHDMI14, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 200, 115, 25));

        jbtnHDMI13.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jbtnHDMI13.setText("HDMI3");
        jbtnHDMI13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPIPActionPerformed(evt);
            }
        });
        tabPipSource.add(jbtnHDMI13, new org.netbeans.lib.awtextra.AbsoluteConstraints(45, 200, 115, 25));

        jbtnHDSDI11.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jbtnHDSDI11.setText("HDSDI 1");
        jbtnHDSDI11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPIPActionPerformed(evt);
            }
        });
        tabPipSource.add(jbtnHDSDI11, new org.netbeans.lib.awtextra.AbsoluteConstraints(45, 230, 115, 25));

        jbtnHDSDI12.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jbtnHDSDI12.setText("HDSDI 2");
        jbtnHDSDI12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPIPActionPerformed(evt);
            }
        });
        tabPipSource.add(jbtnHDSDI12, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 230, 115, 25));

        jLabel4.setBackground(new java.awt.Color(0, 51, 102));
        jLabel4.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("Choose PIP Source");
        jLabel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel4.setOpaque(true);
        tabPipSource.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 290, 26));

        jbtnSwap.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jbtnSwap.setText("PIP / Main Swap");
        jbtnSwap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPIPActionPerformed(evt);
            }
        });
        tabPipSource.add(jbtnSwap, new org.netbeans.lib.awtextra.AbsoluteConstraints(45, 265, 240, 25));

        jTabbedPane2.addTab("PIP Source", tabPipSource);

        tabPipSize.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel5.setBackground(new java.awt.Color(0, 51, 102));
        jLabel5.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("Choose PIP Size");
        jLabel5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel5.setOpaque(true);
        tabPipSize.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 290, 26));

        jbtnOff.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jbtnOff.setText("OFF");
        jbtnOff.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPIPActionPerformed(evt);
            }
        });
        tabPipSize.add(jbtnOff, new org.netbeans.lib.awtextra.AbsoluteConstraints(45, 50, 115, 25));

        jbtnSmall.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jbtnSmall.setText("Small");
        jbtnSmall.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPIPActionPerformed(evt);
            }
        });
        tabPipSize.add(jbtnSmall, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 50, 115, 25));

        jbtnMedium.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jbtnMedium.setText("Medium");
        jbtnMedium.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPIPActionPerformed(evt);
            }
        });
        tabPipSize.add(jbtnMedium, new org.netbeans.lib.awtextra.AbsoluteConstraints(45, 80, 115, 25));

        jbtnLarge.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jbtnLarge.setText("Large");
        jbtnLarge.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPIPActionPerformed(evt);
            }
        });
        tabPipSize.add(jbtnLarge, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 80, 115, 25));

        jbtnSideBySide.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jbtnSideBySide.setText("Side by Side");
        jbtnSideBySide.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPIPActionPerformed(evt);
            }
        });
        tabPipSize.add(jbtnSideBySide, new org.netbeans.lib.awtextra.AbsoluteConstraints(45, 110, 115, 25));

        jLabel6.setBackground(new java.awt.Color(0, 51, 102));
        jLabel6.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("Choose PIP Position");
        jLabel6.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel6.setOpaque(true);
        tabPipSize.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 170, 290, 26));

        jbtnButtonLeft.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jbtnButtonLeft.setText("Button-Left");
        jbtnButtonLeft.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPIPActionPerformed(evt);
            }
        });
        tabPipSize.add(jbtnButtonLeft, new org.netbeans.lib.awtextra.AbsoluteConstraints(45, 240, 115, 25));

        jbtnButtonRight.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jbtnButtonRight.setText("Button-Right");
        jbtnButtonRight.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPIPActionPerformed(evt);
            }
        });
        tabPipSize.add(jbtnButtonRight, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 240, 115, 25));

        jbtnTopRight.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jbtnTopRight.setText("Top-Right");
        jbtnTopRight.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPIPActionPerformed(evt);
            }
        });
        tabPipSize.add(jbtnTopRight, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 210, 115, 25));

        jbtnTopLeft.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jbtnTopLeft.setText("Top-Left");
        jbtnTopLeft.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPIPActionPerformed(evt);
            }
        });
        tabPipSize.add(jbtnTopLeft, new org.netbeans.lib.awtextra.AbsoluteConstraints(45, 210, 115, 25));

        jTabbedPane2.addTab("PIP Size & Position", tabPipSize);

        jbtnPipRefresh.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jbtnPipRefresh.setText("Get Status");
        jbtnPipRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnPipRefreshActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout tabPIPLayout = new javax.swing.GroupLayout(tabPIP);
        tabPIP.setLayout(tabPIPLayout);
        tabPIPLayout.setHorizontalGroup(
            tabPIPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabPIPLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabPIPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(tabPIPLayout.createSequentialGroup()
                        .addComponent(jbtnPipSelectAll)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jbtnPipUnselectAll)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jbtnPipRefresh)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(tabPIPLayout.createSequentialGroup()
                        .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 340, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTabbedPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 336, Short.MAX_VALUE)))
                .addContainerGap())
        );
        tabPIPLayout.setVerticalGroup(
            tabPIPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, tabPIPLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabPIPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jbtnPipSelectAll)
                    .addComponent(jbtnPipUnselectAll)
                    .addComponent(jbtnPipRefresh))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(tabPIPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTabbedPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 339, Short.MAX_VALUE)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );

        jTabbedPane2.getAccessibleContext().setAccessibleName("PIP Source & Position");

        tabSource.addTab("PIP", new javax.swing.ImageIcon(getClass().getResource("/multipledisplay/PIP.png")), tabPIP); // NOI18N

        jbtnTimeSelectAll.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jbtnTimeSelectAll.setText("Select All");
        jbtnTimeSelectAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnTimeSelectAllActionPerformed(evt);
            }
        });

        jbtnTimeUnselectAll.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jbtnTimeUnselectAll.setText("Clear All");
        jbtnTimeUnselectAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnTimeUnselectAllActionPerformed(evt);
            }
        });

        jtableTime.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jtableTime.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "", "ID", "IP", "PWR", "Time Mode", "MON On", "MON Off", "TUE On", "TUE Off", "WED On", "WED Off", "THU On", "THU Off", "FRI On", "FRI Off", "SAT On", "SAT Off", "SUN On", "SUN Off"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jtableTime.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jScrollPane6.setViewportView(jtableTime);

        jPanel5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel12.setBackground(new java.awt.Color(0, 51, 102));
        jLabel12.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel12.setText("Choose Time Mode");
        jLabel12.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel12.setOpaque(true);
        jPanel5.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(23, 11, 290, 26));

        jbtnCheckTime.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jbtnCheckTime.setText("Check Time");
        jbtnCheckTime.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTimeactionPerformed(evt);
            }
        });
        jPanel5.add(jbtnCheckTime, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 305, 300, 30));

        jcobTimeMode.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jcobTimeMode.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "All Day", "Wrok Days", "User" }));
        jcobTimeMode.setName(""); // NOI18N
        jcobTimeMode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcobTimeModeActionPerformed(evt);
            }
        });
        jPanel5.add(jcobTimeMode, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 50, 100, 20));

        jLabel33.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jLabel33.setText("Time Mode:");
        jPanel5.add(jLabel33, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 50, -1, -1));

        jbtnTimeApply.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jbtnTimeApply.setText("Apply");
        jbtnTimeApply.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTimeactionPerformed(evt);
            }
        });
        jPanel5.add(jbtnTimeApply, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 50, 90, 20));

        jpanTimeMode.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jpanTimeMode.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jpanSAT.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jcbSat.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jcbSat.setText("SAT");
        jcbSat.setName("32"); // NOI18N
        jpanSAT.add(jcbSat, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 2, -1, -1));

        jsSatOn.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jsSatOn.setModel(new javax.swing.SpinnerDateModel(new java.util.Date(1381881600000L), null, null, java.util.Calendar.MINUTE));
        jsSatOn.setEditor(new javax.swing.JSpinner.DateEditor(jsSatOn, "hh:mm  a"));
        jpanSAT.add(jsSatOn, new org.netbeans.lib.awtextra.AbsoluteConstraints(85, 4, -1, -1));

        jLabel40.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jLabel40.setText("ON");
        jpanSAT.add(jLabel40, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 6, -1, -1));

        jLabel41.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jLabel41.setText("OFF");
        jpanSAT.add(jLabel41, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 6, -1, -1));

        jsSatOff.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jsSatOff.setModel(new javax.swing.SpinnerDateModel(new java.util.Date(1381914000000L), null, null, java.util.Calendar.MINUTE));
        jsSatOff.setEditor(new javax.swing.JSpinner.DateEditor(jsSatOff, "hh:mm a"));
        jpanSAT.add(jsSatOff, new org.netbeans.lib.awtextra.AbsoluteConstraints(198, 4, 80, -1));

        jpanTimeMode.add(jpanSAT, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 155, 280, 30));

        jpanMon.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jcbMon.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jcbMon.setText("MON");
        jcbMon.setName("1"); // NOI18N
        jpanMon.add(jcbMon, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 2, -1, -1));

        jsMonOn.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jsMonOn.setModel(new javax.swing.SpinnerDateModel(new java.util.Date(1381881600000L), null, null, java.util.Calendar.MINUTE));
        jsMonOn.setEditor(new javax.swing.JSpinner.DateEditor(jsMonOn, "hh:mm  a"));
        jpanMon.add(jsMonOn, new org.netbeans.lib.awtextra.AbsoluteConstraints(85, 4, -1, -1));

        jLabel42.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jLabel42.setText("ON");
        jpanMon.add(jLabel42, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 6, -1, -1));

        jLabel43.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jLabel43.setText("OFF");
        jpanMon.add(jLabel43, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 6, -1, -1));

        jsMonOff.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jsMonOff.setModel(new javax.swing.SpinnerDateModel(new java.util.Date(1381827600000L), null, null, java.util.Calendar.MINUTE));
        jsMonOff.setEditor(new javax.swing.JSpinner.DateEditor(jsMonOff, "hh:mm a"));
        jpanMon.add(jsMonOff, new org.netbeans.lib.awtextra.AbsoluteConstraints(198, 4, 80, -1));

        jpanTimeMode.add(jpanMon, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 5, 280, 30));

        jpanTUE.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jcbTue.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jcbTue.setText("TUE");
        jcbTue.setName("2"); // NOI18N
        jpanTUE.add(jcbTue, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 2, -1, -1));

        jsTueOn.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jsTueOn.setModel(new javax.swing.SpinnerDateModel(new java.util.Date(1381881600000L), null, null, java.util.Calendar.MINUTE));
        jsTueOn.setEditor(new javax.swing.JSpinner.DateEditor(jsTueOn, "hh:mm  a"));
        jpanTUE.add(jsTueOn, new org.netbeans.lib.awtextra.AbsoluteConstraints(85, 4, -1, -1));

        jLabel44.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jLabel44.setText("ON");
        jpanTUE.add(jLabel44, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 6, -1, -1));

        jLabel45.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jLabel45.setText("OFF");
        jpanTUE.add(jLabel45, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 6, -1, -1));

        jsTueOff.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jsTueOff.setModel(new javax.swing.SpinnerDateModel(new java.util.Date(1381827600000L), null, null, java.util.Calendar.MINUTE));
        jsTueOff.setEditor(new javax.swing.JSpinner.DateEditor(jsTueOff, "hh:mm a"));
        jpanTUE.add(jsTueOff, new org.netbeans.lib.awtextra.AbsoluteConstraints(198, 4, 80, -1));

        jpanTimeMode.add(jpanTUE, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 35, 280, 30));

        jpanWED.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jcbWed.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jcbWed.setText("WED");
        jcbWed.setName("4"); // NOI18N
        jpanWED.add(jcbWed, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 2, -1, -1));

        jsWedOn.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jsWedOn.setModel(new javax.swing.SpinnerDateModel(new java.util.Date(1381881600000L), null, null, java.util.Calendar.MINUTE));
        jsWedOn.setEditor(new javax.swing.JSpinner.DateEditor(jsWedOn, "hh:mm  a"));
        jpanWED.add(jsWedOn, new org.netbeans.lib.awtextra.AbsoluteConstraints(85, 4, -1, -1));

        jLabel46.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jLabel46.setText("ON");
        jpanWED.add(jLabel46, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 6, -1, -1));

        jLabel47.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jLabel47.setText("OFF");
        jpanWED.add(jLabel47, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 6, -1, -1));

        jsWedOff.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jsWedOff.setModel(new javax.swing.SpinnerDateModel(new java.util.Date(1381914000000L), null, null, java.util.Calendar.MINUTE));
        jsWedOff.setEditor(new javax.swing.JSpinner.DateEditor(jsWedOff, "hh:mm a"));
        jpanWED.add(jsWedOff, new org.netbeans.lib.awtextra.AbsoluteConstraints(198, 4, 80, -1));

        jpanTimeMode.add(jpanWED, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 65, 280, 30));

        jpanFRI.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jcbFri.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jcbFri.setText("FRI");
        jcbFri.setName("16"); // NOI18N
        jpanFRI.add(jcbFri, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 2, -1, -1));

        jsFriOn.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jsFriOn.setModel(new javax.swing.SpinnerDateModel(new java.util.Date(1381881600000L), null, null, java.util.Calendar.MINUTE));
        jsFriOn.setEditor(new javax.swing.JSpinner.DateEditor(jsFriOn, "hh:mm  a"));
        jpanFRI.add(jsFriOn, new org.netbeans.lib.awtextra.AbsoluteConstraints(85, 4, -1, -1));

        jLabel49.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jLabel49.setText("ON");
        jpanFRI.add(jLabel49, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 6, -1, -1));

        jLabel50.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jLabel50.setText("OFF");
        jpanFRI.add(jLabel50, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 6, -1, -1));

        jsFriOff.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jsFriOff.setModel(new javax.swing.SpinnerDateModel(new java.util.Date(1381827600000L), null, null, java.util.Calendar.MINUTE));
        jsFriOff.setEditor(new javax.swing.JSpinner.DateEditor(jsFriOff, "hh:mm a"));
        jpanFRI.add(jsFriOff, new org.netbeans.lib.awtextra.AbsoluteConstraints(198, 4, 80, -1));

        jpanTimeMode.add(jpanFRI, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 125, 280, 30));

        jpanTHU.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jcbThu.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jcbThu.setText("THU");
        jcbThu.setName("8"); // NOI18N
        jpanTHU.add(jcbThu, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 2, -1, -1));

        jsThuOn.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jsThuOn.setModel(new javax.swing.SpinnerDateModel(new java.util.Date(1381881600000L), null, null, java.util.Calendar.MINUTE));
        jsThuOn.setEditor(new javax.swing.JSpinner.DateEditor(jsThuOn, "hh:mm  a"));
        jpanTHU.add(jsThuOn, new org.netbeans.lib.awtextra.AbsoluteConstraints(85, 4, -1, -1));

        jLabel51.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jLabel51.setText("ON");
        jpanTHU.add(jLabel51, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 6, -1, -1));

        jLabel52.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jLabel52.setText("OFF");
        jpanTHU.add(jLabel52, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 6, -1, -1));

        jsThuOff.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jsThuOff.setModel(new javax.swing.SpinnerDateModel(new java.util.Date(1381914000000L), null, null, java.util.Calendar.MINUTE));
        jsThuOff.setEditor(new javax.swing.JSpinner.DateEditor(jsThuOff, "hh:mm a"));
        jpanTHU.add(jsThuOff, new org.netbeans.lib.awtextra.AbsoluteConstraints(198, 4, 80, -1));

        jpanTimeMode.add(jpanTHU, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 95, 280, 30));

        jpanSUN.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jcbSun.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jcbSun.setText("SUN");
        jcbSun.setName("64"); // NOI18N
        jpanSUN.add(jcbSun, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 2, -1, -1));

        jsSunOn.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jsSunOn.setModel(new javax.swing.SpinnerDateModel(new java.util.Date(1381881600000L), null, null, java.util.Calendar.MINUTE));
        jsSunOn.setEditor(new javax.swing.JSpinner.DateEditor(jsSunOn, "hh:mm  a"));
        jpanSUN.add(jsSunOn, new org.netbeans.lib.awtextra.AbsoluteConstraints(85, 4, -1, -1));

        jLabel53.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jLabel53.setText("ON");
        jpanSUN.add(jLabel53, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 6, -1, -1));

        jLabel54.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jLabel54.setText("OFF");
        jpanSUN.add(jLabel54, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 6, -1, -1));

        jsSunOff.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jsSunOff.setModel(new javax.swing.SpinnerDateModel(new java.util.Date(1381914000000L), null, null, java.util.Calendar.MINUTE));
        jsSunOff.setEditor(new javax.swing.JSpinner.DateEditor(jsSunOff, "hh:mm a"));
        jpanSUN.add(jsSunOff, new org.netbeans.lib.awtextra.AbsoluteConstraints(198, 4, 80, -1));

        jpanTimeMode.add(jpanSUN, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 185, 280, 30));

        jPanel5.add(jpanTimeMode, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 80, 300, 220));

        jbtnTimeRefresh.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jbtnTimeRefresh.setText("Get Status");
        jbtnTimeRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnTimeRefreshActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout tabTimeLayout = new javax.swing.GroupLayout(tabTime);
        tabTime.setLayout(tabTimeLayout);
        tabTimeLayout.setHorizontalGroup(
            tabTimeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabTimeLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabTimeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(tabTimeLayout.createSequentialGroup()
                        .addComponent(jbtnTimeSelectAll)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jbtnTimeUnselectAll)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jbtnTimeRefresh)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(tabTimeLayout.createSequentialGroup()
                        .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 338, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 330, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        tabTimeLayout.setVerticalGroup(
            tabTimeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabTimeLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabTimeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jbtnTimeSelectAll)
                    .addComponent(jbtnTimeUnselectAll)
                    .addComponent(jbtnTimeRefresh))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(tabTimeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, 343, Short.MAX_VALUE)
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );

        tabSource.addTab("Time", new javax.swing.ImageIcon(getClass().getResource("/multipledisplay/clock_time.png")), tabTime); // NOI18N

        jToolBar2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jToolBar2.setRollover(true);

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial Black", 1, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(0, 102, 255));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("CHILIN Solutions");
        jLabel2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jToolBar2.add(jLabel2);
        jLabel2.getAccessibleContext().setAccessibleParent(jToolBar2);

        btnID_List.setIcon(new javax.swing.ImageIcon(getClass().getResource("/multipledisplay/id_list.png"))); // NOI18N
        btnID_List.setToolTipText("ID List");
        btnID_List.setFocusable(false);
        btnID_List.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnID_List.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnID_List.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnID_ListActionPerformed(evt);
            }
        });
        jToolBar2.add(btnID_List);

        jbtnSetting.setIcon(new javax.swing.ImageIcon(getClass().getResource("/multipledisplay/option.png"))); // NOI18N
        jbtnSetting.setToolTipText("Setting");
        jbtnSetting.setFocusable(false);
        jbtnSetting.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jbtnSetting.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jbtnSetting.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnSettingActionPerformed(evt);
            }
        });
        jToolBar2.add(jbtnSetting);

        jlblNote.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        jlblNote.setIcon(new javax.swing.ImageIcon(getClass().getResource("/multipledisplay/notification.png"))); // NOI18N
        jlblNote.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jlblNoteMouseClicked(evt);
            }
        });

        jTextAreaInfo.setColumns(20);
        jTextAreaInfo.setRows(5);
        jTextAreaInfo.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jScrollPane2.setViewportView(jTextAreaInfo);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jlblNote, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(tabSource, javax.swing.GroupLayout.PREFERRED_SIZE, 711, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jToolBar2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jToolBar2, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tabSource, javax.swing.GroupLayout.PREFERRED_SIZE, 456, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jlblNote, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );

        tabSource.getAccessibleContext().setAccessibleName("Input Source");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void loadXML() {

        try {
            File file = new File(Functions.displayXml);
            if (file.exists()) {
                displayItems = Displays.loadXML(Functions.displayXml);
            } else {

                displayItems = new Displays();
                displayItems.saveXML(Functions.displayXml);
            }
            file = null;
            lblLastLoggingTime.setText("<HTML><U>Last update : " + sdf.format(displayItems.LastLoggingTime) + "<U><HTML>");

            //LoadSetting
            file = new File(Functions.cfgXml);
            if (file.exists()) {
                set = Settings.LoadXML(Functions.cfgXml);
            } else {
                set = new Settings();
                set.SaveXML(Functions.cfgXml);
            }

            this.tmrLoggingMin = Integer.parseInt(set.getUpdateFrequency());

        } catch (Exception exp) {
            Functions.showMsgDialog(exp.getMessage());
        }
    }

    private void updateIDListIP() {
        boolean isChange = false;
        try {
            Functions.showMsg(jTextAreaInfo, "Update all Devices IP from the local network...");
            ArrayList<Ncmd.pIP210Node2> getP210 = Functions.SearchDevice();
            for (Ncmd.pIP210Node2 p2 : getP210) {
                int index = displayItems.getDisplayItemIndexByMAC(p2.MacAddr);
                if (index == -1) {
                    continue;
                }
                if (!displayItems.displayItem.get(index).getIP().equals(p2.IPAddr)) {
                    displayItems.displayItem.get(index).setIP(p2.IPAddr);
                    isChange = true;
                }
            }
            if (isChange) {
                loadPowerControlTable();
                displayItems.saveXml();
            }
        } catch (Exception exp) {
            Functions.showMsgDialog(exp.getMessage());
        } finally {
            Functions.showMsg(jTextAreaInfo, "Update ID List finished.");
        }
    }

    private void checkLogFolder() {

        Date d = new Date();
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
        todayLogFolder = set.getLogFolder() + "\\" + sdfDate.format(d);
        File file = new File(todayLogFolder);
        if (!file.exists()) {
            file.mkdirs();  //create today folder;
            logDisplays.clear();
        }
        file = null;
        logDisplays.clear();
        for (DisplayItem di : displayItems.displayItem) {
            file = new File(todayLogFolder + "\\" + di.getID() + ".xml");
            Displays logDis;
            if (!file.exists()) {
                logDis = new Displays();
                logDis.name = Byte.toString(di.getID());
                logDis.saveXML(file.getPath());
            } else {
                logDis = Displays.loadXML(file.getPath());
            }
            logDisplays.add(logDis);
        }
    }

    private void connectDisplay() {
        Functions.showMsg(jTextAreaInfo, "Connect Displays...");
        if (displayItems == null || displayItems.getDisplayItems().size() <= 0) {
            return;
        }
        netDisplays.clear();
        comDisplay = null;

        if (set.useCOM) {   //RS232
            comDisplay = new Display(set.getCOM());
            if (comDisplay.IsConnected) {
                displayAddListener(comDisplay);
                //check all display power on status after connected .
                for (int i = 0; i < displayItems.displayItem.size(); i++) {
                    displayItems.displayItem.get(i).setPowerOn(false);
                    comDisplay.GetPowerStatus(displayItems.displayItem.get(i).getID());
                }
            } else {
                Functions.showMsg(jTextAreaInfo, "Connect Display[" + set.getCOM() + "is time out.");
            }
        } else {    //Etherent
            runUpdateIDListIP();
        }
    }

    private void runUpdateIDListIP() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                updateIDListIP();
                Functions.showMsg(jTextAreaInfo, "Connect all Display...");
                for (int i = 0; i < displayItems.getDisplayItems().size(); i++) {
                    displayItems.displayItem.get(i).setPowerOn(false); //load進來就設為false
                    DisplayItem di = displayItems.displayItem.get(i);
                    runNetConnect(di.getIP(), Integer.parseInt(set.getPort()), di.getID());
                }
            }
        }, 100);

    }

    private void runNetConnect(final String ip, final int port, final byte id) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Display display = new Display(ip, port);
                netDisplays.add(display);

                if (display.IsConnected) {
                    displayAddListener(display);
                    display.GetPowerStatus(id);
                } else {
                    Functions.showMsg(jTextAreaInfo, "[" + Unit.Byte2HexString(id) + "] Connect to Display[" + ip + "] is  time out.");
                }
            }
        }, 100);

    }

    private void disconnectDisplay() {
        try {
            Functions.showMsg(jTextAreaInfo, "Disconnecting all Displays...");
            if (set.useCOM) {
                if (comDisplay != null && comDisplay.IsConnected) {
                    comDisplay.stop();
                }
            } else {
                for (Display dis : netDisplays) {
                    if (comDisplay != null && comDisplay.IsConnected) {
                        comDisplay.stop();
                    }
                }
            }
        } catch (Exception exp) {
            Functions.showMsg(jTextAreaInfo, exp.getMessage());
        }
    }

    private void displayAddListener(Display display) {
        display.addListener(new DisplayEventListener() {
            @Override
            public synchronized void OnConnected(Object source) {
                Display dis = (Display) source;
                Functions.showMsg(jTextAreaInfo, "Connect to the Display [" + dis.showSource() + "] sucessfully.");
                updateConnectState(dis, true);

            }

            @Override
            public synchronized void OnDisconnected(Object source) {
                Display dis = (Display) source;
                Functions.showMsg(jTextAreaInfo, "Disconnect to the Display [" + dis.showSource() + "]. ");
                updateConnectState(dis, true);
            }

            @Override
            public synchronized void OnMessage(Object source, String msg) {
                //ShowMsg("API Internal Message: " + msg);
            }

            @Override
            public synchronized void OnResponse(Object source, PacketFrame pf) {
                Display dis = (Display) source;
                boolean isUpdate = false;
                int index = -1;
                byte id = pf.IDT;
                String MsgID = "";
                if (set.useCOM) {
                    index = displayItems.getDisplayItemIndexByID(id);
                    MsgID = "[" + Unit.Byte2HexString(id) + "] ";
                } else {
                    index = displayItems.getDisplayItemIndexByIP(dis.ip);
                    MsgID = dis.ip + "[ " + Unit.Byte2HexString(id) + "]: ";
                }
                if (index == -1) {
                    return;
                }
                if (pf.Value[0] == -1) {
                    Functions.showMsg(jTextAreaInfo, MsgID + "Return -1");
                    return;
                }
                // <editor-fold defaultstate="collapsed" desc="PowerControl">
                if (Arrays.equals(pf.Command, Unit.CMD_POWER_CONTROL)) {
                    Functions.showMsg(jTextAreaInfo, MsgID + "Power is " + Unit.ON_OFF_TYPE[pf.Value[0]]);
                    displayItems.displayItem.get(index).setPowerOn(pf.Value[0] != 0);
                    isUpdate = true;
                } else if (Arrays.equals(pf.Command, Unit.CMD_MUTE)) {
                    Functions.showMsg(jTextAreaInfo, MsgID + "Mute is " + Unit.ON_OFF_TYPE[pf.Value[0]]);
                    displayItems.displayItem.get(index).setMuteOn(pf.Value[0] != 0);
                    isUpdate = true;
                } else if (Arrays.equals(pf.Command, Unit.CMD_VOLUME)) {
                    Functions.showMsg(jTextAreaInfo, MsgID + "Volume is " + pf.Value[0]);
                    displayItems.displayItem.get(index).setVolume(pf.Value[0]);
                    isUpdate = true;
                } else if (Arrays.equals(pf.Command, Unit.CMD_BACKLIGHT)) {
                    Functions.showMsg(jTextAreaInfo, MsgID + "Backlight is " + Unit.ON_OFF_TYPE[pf.Value[0]]);
                    displayItems.displayItem.get(index).setBacklightOn(pf.Value[0] != 0);
                    isUpdate = true;
                } else if (Arrays.equals(pf.Command, Unit.CMD_LOCK_KEYS)) {
                    Functions.showMsg(jTextAreaInfo, MsgID + "Lock Keys is " + Unit.ON_OFF_TYPE[pf.Value[0]]);
                    displayItems.displayItem.get(index).setLockKey(pf.Value[0] != 0);
                    isUpdate = true;
                } else if (Arrays.equals(pf.Command, Unit.CMD_WOD_MODE)) {
                    Functions.showMsg(jTextAreaInfo, MsgID + "WOD Mode is " + Unit.ON_OFF_TYPE[pf.Value[0]]);
                    displayItems.displayItem.get(index).setLockKey(pf.Value[0] != 0);
                    isUpdate = true;
                } else if (Arrays.equals(pf.Command, Unit.CMD_AUTO_ADJUST)) {
                    Functions.showMsg(jTextAreaInfo, MsgID + "Set Auto Adjustment");
                } // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="InputSource">
                else if (Arrays.equals(pf.Command, Unit.CMD_INPUT_SOURCE)) {
                    Functions.showMsg(jTextAreaInfo, MsgID + "Source is " + Unit.SOURCE_TYPE[pf.Value[0]] + ".");
                    displayItems.displayItem.get(index).setInputSource(pf.Value[0]);
                    isUpdate = true;
                } // </editor-fold> 
                // <editor-fold defaultstate="collapsed" desc="Adjustment">
                else if (Arrays.equals(pf.Command, Unit.CMD_BACKLIGHT_BRIGHTNESS)) {
                    Functions.showMsg(jTextAreaInfo, MsgID + "Backlight value is " + pf.Value[0] + ".");
                    displayItems.displayItem.get(index).setBL_Brightness(pf.Value[0]);
                    isUpdate = true;
                } else if (Arrays.equals(pf.Command, Unit.CMD_DIGITAL_BRIGHTNESS_LEVEL)) {
                    Functions.showMsg(jTextAreaInfo, MsgID + "Brightness value is " + pf.Value[0] + ".");
                    displayItems.displayItem.get(index).setDigitalBrightnessLevel(pf.Value[0]);
                    isUpdate = true;
                } else if (Arrays.equals(pf.Command, Unit.CMD_CONTRAST)) {
                    Functions.showMsg(jTextAreaInfo, MsgID + "Contrast value is " + pf.Value[0] + ".");
                    displayItems.displayItem.get(index).setContrast(pf.Value[0]);
                    isUpdate = true;
                } else if (Arrays.equals(pf.Command, Unit.CMD_HUE)) {
                    Functions.showMsg(jTextAreaInfo, MsgID + "Hue value is " + pf.Value[0] + ".");
                    displayItems.displayItem.get(index).setHue(pf.Value[0]);
                    isUpdate = true;
                } else if (Arrays.equals(pf.Command, Unit.CMD_SATURATION)) {
                    Functions.showMsg(jTextAreaInfo, MsgID + "Saturation value is " + pf.Value[0] + ".");
                    displayItems.displayItem.get(index).setStaturation(pf.Value[0]);
                    isUpdate = true;
                } else if (Arrays.equals(pf.Command, Unit.CMD_PHASE)) {
                    Functions.showMsg(jTextAreaInfo, MsgID + "Phase value is " + pf.Value[0] + ".");
                    displayItems.displayItem.get(index).setPhase(pf.Value[0]);
                    isUpdate = true;
                } else if (Arrays.equals(pf.Command, Unit.CMD_CLOCK)) {
                    Functions.showMsg(jTextAreaInfo, MsgID + "Clock " + id + "is " + pf.Value[0] + ".");
                    displayItems.displayItem.get(index).setClock(pf.Value[0]);
                    isUpdate = true;
                } else if (Arrays.equals(pf.Command, Unit.CMD_SHARPNESS)) {
                    Functions.showMsg(jTextAreaInfo, MsgID + "Sharpness value is " + pf.Value[0] + ".");
                    displayItems.displayItem.get(index).setSharpness(pf.Value[0]);
                    isUpdate = true;
                } else if (Arrays.equals(pf.Command, Unit.CMD_POWERONDELAY_INTEGRAL)) {
                    Functions.showMsg(jTextAreaInfo, MsgID + "Power on Delay value is " + pf.Value[0] + ".");
                    displayItems.displayItem.get(index).setPowerOnDelay(pf.Value[0]);
                    isUpdate = true;
                } else if (Arrays.equals(pf.Command, Unit.CMD_COLOR_TEMP)) {
                    Functions.showMsg(jTextAreaInfo, MsgID + "Color Temperature value is " + Unit.COLOR_TEMP[pf.Value[0]] + ".");
                    displayItems.displayItem.get(index).setColorTemperature(pf.Value[0]);
                    isUpdate = true;
                } else if (Arrays.equals(pf.Command, Unit.CMD_GAMMA)) {
                    Functions.showMsg(jTextAreaInfo, MsgID + "Gamma value is " + Unit.GAMMA_TYPE[pf.Value[0]] + ".");
                    displayItems.displayItem.get(index).setGamma(pf.Value[0]);
                    isUpdate = true;
                } else if (Arrays.equals(pf.Command, Unit.CMD_R_GAIN)) {
                    Functions.showMsg(jTextAreaInfo, MsgID + "R Gain value is " + Functions.ShowSpecialInfo(pf.Value[0], 128, 383) + ".");
                    displayItems.displayItem.get(index).setR_Gain(Functions.ShowSpecialInfo(pf.Value[0], 128, 383));
                    isUpdate = true;
                } else if (Arrays.equals(pf.Command, Unit.CMD_G_GAIN)) {
                    Functions.showMsg(jTextAreaInfo, MsgID + "G Gain value is " + Functions.ShowSpecialInfo(pf.Value[0], 128, 383) + ".");
                    displayItems.displayItem.get(index).setG_Gain(Functions.ShowSpecialInfo(pf.Value[0], 128, 383));
                    isUpdate = true;
                } else if (Arrays.equals(pf.Command, Unit.CMD_B_GAIN)) {
                    Functions.showMsg(jTextAreaInfo, MsgID + "B Gain value is " + Functions.ShowSpecialInfo(pf.Value[0], 128, 383) + ".");
                    displayItems.displayItem.get(index).setB_Gain(Functions.ShowSpecialInfo(pf.Value[0], 128, 383));
                    isUpdate = true;
                } else if (Arrays.equals(pf.Command, Unit.CMD_R_OFFSET)) {
                    Functions.showMsg(jTextAreaInfo, MsgID + "R Offset value is " + Functions.ShowSpecialInfo(pf.Value[0], -50, 50) + ".");
                    displayItems.displayItem.get(index).setR_Offset(Functions.ShowSpecialInfo(pf.Value[0], -50, 50));
                    isUpdate = true;
                } else if (Arrays.equals(pf.Command, Unit.CMD_G_OFFSET)) {
                    Functions.showMsg(jTextAreaInfo, MsgID + "G Offset value is " + Functions.ShowSpecialInfo(pf.Value[0], -50, 50) + ".");
                    displayItems.displayItem.get(index).setR_Offset(Functions.ShowSpecialInfo(pf.Value[0], -50, 50));
                    isUpdate = true;
                } else if (Arrays.equals(pf.Command, Unit.CMD_B_OFFSET)) {
                    Functions.showMsg(jTextAreaInfo, MsgID + "B Offset value is " + Functions.ShowSpecialInfo(pf.Value[0], -50, 50) + ".");
                    displayItems.displayItem.get(index).setB_Offset(Functions.ShowSpecialInfo(pf.Value[0], -50, 50));
                    isUpdate = true;
                } else if (Arrays.equals(pf.Command, Unit.CMD_SCALING_ZOOM)) {
                    Functions.showMsg(jTextAreaInfo, MsgID + "Zoom is " + Unit.ZOOM_TYPE[pf.Value[0]] + ".");
                    displayItems.displayItem.get(index).setScheme(pf.Value[0]);
                    isUpdate = true;
                } else if (Arrays.equals(pf.Command, Unit.CMD_SCALING)) {
                    Functions.showMsg(jTextAreaInfo, MsgID + "Scaling value is " + Unit.SCALING_TYPE[pf.Value[0]] + ".");
                    displayItems.displayItem.get(index).setScaling(pf.Value[0]);
                    isUpdate = true;
                } else if (Arrays.equals(pf.Command, Unit.CMD_SCHEME_SELECTION)) {
                    Functions.showMsg(jTextAreaInfo, MsgID + "Scheme vaule is " + Unit.SCHEME_TYPE[pf.Value[0]] + ".");
                    displayItems.displayItem.get(index).setScheme(pf.Value[0]);
                    isUpdate = true;
                } // </editor-fold>   
                // <editor-fold defaultstate="collapsed" desc="PIP">
                else if (Arrays.equals(pf.Command, Unit.CMD_PIP_SOURCE_SELECTION)) {
                    Functions.showMsg(jTextAreaInfo, MsgID + "PIP source is " + Unit.SOURCE_TYPE[pf.Value[0]] + ".");
                    displayItems.displayItem.get(index).setPIP_SourceSelection(pf.Value[0]);
                    isUpdate = true;
                } else if (Arrays.equals(pf.Command, Unit.CMD_PIP_Adjust)) {
                    Functions.showMsg(jTextAreaInfo, MsgID + "PIP Size is " + Unit.PIP_SIZE[pf.Value[0]] + ".");
                    displayItems.displayItem.get(index).setPIP_Adjust(pf.Value[0]);
                    isUpdate = true;
                } else if (Arrays.equals(pf.Command, Unit.CMD_PIP_POSITION)) {
                    Functions.showMsg(jTextAreaInfo, MsgID + " PIP Position is " + Unit.PIP_Postion[pf.Value[0]] + ".");
                    displayItems.displayItem.get(index).setPIP_Position(pf.Value[0]);
                    isUpdate = true;
                } else if (Arrays.equals(pf.Command, Unit.CMD_PIP_MAIN_SWAP)) {
                    Functions.showMsg(jTextAreaInfo, MsgID + "PIP Main Swap");
                }
                // </editor-fold>

                if (isUpdate) {
                    updateToTable(displayItems.displayItem.get(index));
                }
            }
        });
        display.run();
    }

    private void updateConnectState(Display dis, boolean state) {

        //Update each Id  connection state is true if  Display  type is RS-232
        if (dis.displayType == DisplayType.RS232) {
            for (int i = 0; i < displayItems.displayItem.size(); i++) {
                if (displayItems.displayItem.get(i).getCOM().equals(dis.comPort)) {
                    displayItems.displayItem.get(i).isConnected = state;
                    dtPower.setValueAt(state, i, 4);  //connection field
                    displayItems.unSaved = true;
                }
            }
            //Update the id  connection state is ture if Display ip equals the displayitem
        } else if (dis.displayType == DisplayType.Ethernet) {
            for (int i = 0; i < displayItems.displayItem.size(); i++) {
                if (displayItems.displayItem.get(i).getIP().equals(dis.ip)) {
                    displayItems.displayItem.get(i).isConnected = state;
                    dtPower.setValueAt(state, i, 4);   //connection field exists power control only.
                    displayItems.unSaved = true;
                }
            }
        }
    }

    private void updateToTable(DisplayItem di) {
        if (displayItems != null) {

            displayItems.unSaved = true;  //交由Timer去存至檔案

            switch (tabSource.getSelectedIndex()) {
                case 0: //Power control
                    updatePowerControlTable(di);
                    break;
                case 1://Inpurt source
                    loadInputSourceTable(di);
                    break;
                case 2: //Display Adjustment
                    loadAdjustmentTable(di);
                    break;
                case 3:  //PIP
                    loadPIP_Table(di);
                    break;
                case 4:  //Time
                    loadTimeTable(di);
                    break;
            }

        }
    }

    private void tableItemSelect(DefaultTableModel dt, boolean isSelect) {
        if (dt != null) {
            for (int i = 0; i < dt.getRowCount(); i++) {
                dt.setValueAt(isSelect, i, 0);
            }
        }
    }

    private void tableItemSend(DefaultTableModel dt, java.awt.event.ActionEvent evt) {

        for (int i = 0; i < dt.getRowCount(); i++) {
            boolean selected = Boolean.parseBoolean(dt.getValueAt(i, 0).toString());
            if (selected) {
                byte id = Byte.parseByte(dt.getValueAt(i, 1).toString());
                String ip = dt.getValueAt(i, 2).toString();
                this.btnSendCommand(id, ip, evt.getSource());
            }
        }
    }

    private void btnSendCommand(byte id, String ip, Object sender) {
        //ArrayList<Display> netDisplays = new ArrayList<>();
        //Display comDisplay = null;

        if (set.useCOM) {
            if (!comDisplay.IsConnected) {
                comDisplay.Connect();
                if (comDisplay.IsConnected) {
                    displayAddListener(comDisplay);
                } else {
                    Functions.showMsg(jTextAreaInfo, "Connect to Display[" + comDisplay.comPort + "] is  time out.");
                }
            }
            btnSendCommandSub(comDisplay, sender, id);

        } else {
            for (Display dis : netDisplays) {
                //Re-connect when Display doesn't connect.
                if (!dis.ip.equals(ip)) {
                    continue;
                }

                if (!dis.IsConnected) {
                    dis.Connect();
                    if (dis.IsConnected) {
                        displayAddListener(dis);
                    } else {
                        Functions.showMsg(jTextAreaInfo, "Connect to Display[" + ip + "] is  time out.");
                    }
                }
                btnNetSendCommandSub(dis, sender, id);
                break;
            }
        }

    }

    private void btnNetSendCommandSub(final Display display, final Object button, final byte id) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                int index = displayItems.getDisplayItemIndexByIP(display.ip);
                if (!display.IsConnected) {
                    display.Connect();
                    if (display.IsConnected) {
                        displayAddListener(display);
                    } else {
                        Functions.showMsg(jTextAreaInfo, "Connect to Display[" + display.ip + "] is  time out.");
                    }
                }

                btnSendCommandSub(display, button, id);
            }
        }, 100);
    }

    private boolean btnSendCommandSub(Display display, Object button, byte id) {
        boolean isExcute = false;

        if (!display.IsConnected) {
            return false;
        }

        try {

            if (button.equals(jbtnPowerOn)) {
                display.SetPower(id, (byte) 0x1);
                return true;
            } else if (button.equals(jbtnPowerOff)) {
                display.SetPower(id, (byte) 0x0);
                return true;
            }
            DisplayItem di;
            if (set.useCOM) {
                di = displayItems.getDisplayItemByID(id);
                if (!di.getPowerOn()) {
                    Functions.showMsg(jTextAreaInfo, "Can not send command to Display[" + id + "] (Power Off). ");
                    return false; //Dont send command if the displayitem power status is off.
                }
            } else {
                di = displayItems.getDisplayItemByIP(display.ip);
                if (!di.getPowerOn()) {
                    Functions.showMsg(jTextAreaInfo, "Can not send command to Display (" + display.ip + ")[" + id + "] (Power Off). ");
                    return false; //Dont send command if the displayitem power status is off.
                }
            }
            // <editor-fold defaultstate="collapsed" desc="Power Control">
            if (button.equals(jbtnVolume)) {
                display.SetVolume(id, Byte.parseByte(this.valVolume.getText()));
                isExcute = true;
            } else if (button.equals(jbtnMuteOn)) {
                display.SetMuteOn(id);
                isExcute = true;
            } else if (button.equals(jbtnMuteOff)) {
                display.SetMuteOff(id);
                isExcute = true;
            } else if (button.equals(jbtnWOD_On)) {
                display.SetWOD_On(id);
                isExcute = true;
            } else if (button.equals(jbtnWOD_Off)) {
                display.SetWOD_Off(id);
                isExcute = true;
            } else if (button.equals(jbtnBL_On)) {
                display.SetBackLightOn(id);
                isExcute = true;
            } else if (button.equals(jbtnBL_Off)) {
                display.SetBackLightOff(id);
                isExcute = true;
            } else if (button.equals(this.jbtnBacklight)) {
                display.SetDigitalBrightnessLevel(id, Byte.parseByte(this.valBacklight.getText()));
                isExcute = true;
            } else if (button.equals(jbtnLockKey)) {
                display.SetLockKey(id);
                isExcute = true;
            } else if (button.equals(jbtnUnlockKey)) {
                display.SetUnlockKey(id);
                isExcute = true;
            } else if (button.equals(jbtnAutoAdjust)) {
                display.AutoAdjust(id);
                isExcute = true;
            } // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Input Source">
            else if (button.equals(jbtnVGA)) {
                display.SetInputSource(id, Unit.PARM_VGA);
                isExcute = true;
            } else if (button.equals(jbtnDVI)) {
                display.SetInputSource(id, Unit.PARM_DVI);
                isExcute = true;
            } else if (button.equals(jbtnSVideo)) {
                display.SetInputSource(id, Unit.PARM_SVIDEO);
                isExcute = true;
            } else if (button.equals(jbtnComposite1)) {
                display.SetInputSource(id, Unit.PARM_COMPOSITE1);
                isExcute = true;
            } else if (button.equals(jbtnComponent1)) {
                display.SetInputSource(id, Unit.PARM_COMPONENT1);
                isExcute = true;
            } else if (button.equals(jbtnHDSDI1)) {
                display.SetInputSource(id, Unit.PARM_HDSDI1);
                isExcute = true;
            } else if (button.equals(jbtnHDSDI2)) {
                display.SetInputSource(id, Unit.PARM_HDSDI2);
                isExcute = true;
            } else if (button.equals(jbtnComposite2)) {
                display.SetInputSource(id, Unit.PARM_COMPOSITE2);
                isExcute = true;
            } else if (button.equals(jbtnComponent2)) {
                display.SetInputSource(id, Unit.PARM_COMPONENT2);
                isExcute = true;
            } else if (button.equals(jbtnHDMI1)) {
                display.SetInputSource(id, Unit.PARM_HDMI1);
                isExcute = true;
            } else if (button.equals(jbtnHDMI2)) {
                display.SetInputSource(id, Unit.PARM_HDMI2);
                isExcute = true;
            } else if (button.equals(jbtnHDMI3)) {
                display.SetInputSource(id, Unit.PARM_HDMI3);
                isExcute = true;
            } else if (button.equals(jbtnHDMI4)) {
                display.SetInputSource(id, Unit.PARM_HDMI4);
                isExcute = true;
            } else if (button.equals(jbtnDisplayPort)) {
                display.SetInputSource(id, Unit.PARM_DISPLAYPORT);
                isExcute = true;
            } // </editor-fold >
            // <editor-fold defaultstate="collapsed" desc="Adjustment">
            else if (button.equals(jbtnBrightness)) {
                display.SetBL_Brightness(id, (byte) jsliderBrightness.getValue());
                isExcute = true;
            } else if (button.equals(jbtnDBrightness)) {
                display.SetDigitalBrightnessLevel(id, (byte) jsliderDBrightness.getValue());
                isExcute = true;
            } else if (button.equals(jbtnContrast)) {
                display.SetContrast(id, (byte) jsliderContrast.getValue());
                isExcute = true;
            } else if (button.equals(jbtnHue)) {
                display.SetHue(id, (byte) jsliderHue.getValue());
                isExcute = true;
            } else if (button.equals(jbtnSaturation)) {
                display.SetSaturation(id, (byte) jsliderSaturation.getValue());
                isExcute = true;
            } else if (button.equals(jbtnPhase)) {
                display.SetPhase(id, (byte) jsliderPhase.getValue());
                isExcute = true;
            } else if (button.equals(jbtnClock)) {
                display.SetClock(id, (byte) jsliderClock.getValue());
                isExcute = true;
            } else if (button.equals(jbtnSharpness)) {
                display.SetSharpness(id, (byte) jsliderSharpness.getValue());
                isExcute = true;
            } else if (button.equals(jbtnPowerOnDelay)) {
                display.SetPowerOnDelay(id, (byte) jsliderPowerOnDelay.getValue());
                isExcute = true;
            } else if (button.equals(jbtnColorTemp)) {
                display.SetColorTemperature(id, (byte) jsliderColorTemp.getValue());
                isExcute = true;
            } else if (button.equals(jbtnGamma)) {
                display.SetGamma(id, (byte) jsliderGamma.getValue());
                isExcute = true;
            } else if (button.equals(jbtnRGain)) {
                display.SetRedGain(id, (byte) jsliderRGain.getValue());
                isExcute = true;
            } else if (button.equals(jbtnGGain)) {
                display.SetGreenGain(id, (byte) jsliderGGain.getValue());
                isExcute = true;
            } else if (button.equals(jbtnBGain)) {
                display.SetBlueGain(id, (byte) jsliderBGain.getValue());
                isExcute = true;
            } else if (button.equals(jbtnROffset)) {
                display.SetRedOffset(id, (byte) jsliderROffset.getValue());
                isExcute = true;
            } else if (button.equals(jbtnGOffset)) {
                display.SetGreenOffset(id, (byte) jsliderGOffset.getValue());
                isExcute = true;
            } else if (button.equals(jbtnBOffset)) {
                display.SetBlueOffset(id, (byte) jsliderBOffset.getValue());
                isExcute = true;
            } else if (button.equals(jbtnNative)) {
                display.SetScaling(id, Unit.PARM_NATIVE);
                isExcute = true;
            } else if (button.equals(jbtnFill)) {
                display.SetScaling(id, Unit.PARM_FILL);
                isExcute = true;
            } else if (button.equals(jbtnPillarBox)) {
                display.SetScaling(id, Unit.PARM_PILLAR_BOX);
                isExcute = true;
            } else if (button.equals(jbtnLetterBox)) {
                display.SetScaling(id, Unit.PARM_LETTER_BOX);
                isExcute = true;
            } else if (button.equals(jbtnZoomIn)) {
                display.SetZoomIn(id);
                isExcute = true;
            } else if (button.equals(jbtnZoomOut)) {
                display.SetZoomOut(id);
                isExcute = true;
            } else if (button.equals(jbtnUser)) {
                display.SetSchemeSelection(id, Unit.PARM_USER);
                isExcute = true;
            } else if (button.equals(jbtnSport)) {
                display.SetSchemeSelection(id, Unit.PARM_SPORT);
                isExcute = true;
            } else if (button.equals(jbtnGame)) {
                display.SetSchemeSelection(id, Unit.PARM_GAME);
                isExcute = true;
            } else if (button.equals(jbtnCinema)) {
                display.SetSchemeSelection(id, Unit.PARM_CINEMA);
                isExcute = true;
            } else if (button.equals(jbtnVivid)) {
                display.SetSchemeSelection(id, Unit.PARM_VIVID);
                isExcute = true;
            } // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="PIP">
            else if (button.equals(jbtnVGA1)) {
                display.SetPIP_SourceSelect(id, Unit.PARM_VGA);
                isExcute = true;
            } else if (button.equals(jbtnDVI1)) {
                display.SetPIP_SourceSelect(id, Unit.PARM_DVI);
                isExcute = true;
            } else if (button.equals(jbtnSVideo1)) {
                display.SetPIP_SourceSelect(id, Unit.PARM_SVIDEO);
                isExcute = true;
            } else if (button.equals(jbtnComposite11)) {
                display.SetPIP_SourceSelect(id, Unit.PARM_COMPOSITE1);
                isExcute = true;
            } else if (button.equals(jbtnComponent11)) {
                display.SetPIP_SourceSelect(id, Unit.PARM_COMPONENT1);
                isExcute = true;
            } else if (button.equals(jbtnHDSDI11)) {
                display.SetPIP_SourceSelect(id, Unit.PARM_HDSDI1);
                isExcute = true;
            } else if (button.equals(jbtnHDSDI12)) {
                display.SetPIP_SourceSelect(id, Unit.PARM_HDSDI2);
                isExcute = true;
            } else if (button.equals(jbtnComposite12)) {
                display.SetPIP_SourceSelect(id, Unit.PARM_COMPOSITE2);
                isExcute = true;
            } else if (button.equals(jbtnComponent12)) {
                display.SetPIP_SourceSelect(id, Unit.PARM_COMPONENT2);
                isExcute = true;
            } else if (button.equals(jbtnHDMI11)) {
                display.SetPIP_SourceSelect(id, Unit.PARM_HDMI1);
                isExcute = true;
            } else if (button.equals(jbtnHDMI12)) {
                display.SetPIP_SourceSelect(id, Unit.PARM_HDMI2);
                isExcute = true;
            } else if (button.equals(jbtnHDMI13)) {
                display.SetPIP_SourceSelect(id, Unit.PARM_HDMI3);
                isExcute = true;
            } else if (button.equals(jbtnHDMI14)) {
                display.SetPIP_SourceSelect(id, Unit.PARM_HDMI4);
                isExcute = true;
            } else if (button.equals(jbtnDisplayPort1)) {
                display.SetPIP_SourceSelect(id, Unit.PARM_DISPLAYPORT);
                isExcute = true;
            } else if (button.equals(jbtnSwap)) {
                display.SetPIP_Swap(id);
                isExcute = true;
            } else if (button.equals(jbtnOff)) {
                display.SetPIP_Adjust(id, Unit.PARM_PIP_OFF);
                isExcute = true;
            } else if (button.equals(jbtnSmall)) {
                display.SetPIP_Adjust(id, Unit.PARM_PIP_SMALL);
                isExcute = true;
            } else if (button.equals(jbtnMedium)) {
                display.SetPIP_Adjust(id, Unit.PARM_PIP_MEDIUM);
                isExcute = true;
            } else if (button.equals(jbtnLarge)) {
                display.SetPIP_Adjust(id, Unit.PARM_PIP_LARGE);
                isExcute = true;
            } else if (button.equals(jbtnSideBySide)) {
                display.SetPIP_Adjust(id, Unit.PARM_PIP_SIDE_BY_SIDE);
                isExcute = true;
            } else if (button.equals(jbtnButtonLeft)) {
                display.SetPIP_Position(id, Unit.PARM_PIP_BOTTOM_LEFT);
                isExcute = true;
            } else if (button.equals(jbtnButtonRight)) {
                display.SetPIP_Position(id, Unit.PARM_PIP_BOTTOM_RIGHT);
                isExcute = true;
            } else if (button.equals(jbtnTopLeft)) {
                display.SetPIP_Position(id, Unit.PARM_PIP_TOP_LEFT);
                isExcute = true;
            } else if (button.equals(jbtnTopRight)) {
                display.SetPIP_Position(id, Unit.PARM_PIP_TOP_RIGHT);
                isExcute = true;
            } // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Time">
            else if (button.equals(jbtnCheckTime)) {
                display.SetRealTime(id, Calendar.getInstance());
                isExcute = true;
            } else if (button.equals(jbtnTimeApply)) {
                sendCommandForTime(id, display);
                isExcute = true;
            }
            // </editor-fold>
        } catch (Exception exp) {
            exp.printStackTrace();
        }
        return isExcute;
    }

    private void sendCommandForTime(byte id, Display dis) {

        int mode = jcobTimeMode.getSelectedIndex();
        int enableNum = 0;
        Date dMonOn = (Date) jsMonOn.getValue();
        Date dMonOff = (Date) jsMonOff.getValue();
        Date dSatOn = (Date) jsSatOn.getValue();
        Date dSatOff = (Date) jsSatOff.getValue();
        Date dSunOn = (Date) jsSunOn.getValue();
        Date dSunOff = (Date) jsSunOff.getValue();
        //Set Time Mode
        dis.SetTimeMode(id, (byte) mode);

        if (this.jcbMon.isSelected()) {  //Applicable all time mode

            dis.SetMondayTime(id, (byte) dMonOn.getHours(),
                    (byte) dMonOn.getMinutes(), (byte) dMonOff.getHours(), (byte) dMonOff.getMinutes());
            enableNum = Unit.PARM_ALARM_MONDAY;
        }

        if (mode == 1) {  //working days :mon, sat and sun
            if (this.jcbSat.isSelected()) {
                dis.SetSaturdayTime(id, (byte) dSatOn.getHours(),
                        (byte) dSatOn.getMinutes(), (byte) dSatOff.getHours(), (byte) dSatOff.getMinutes());
                enableNum += Unit.PARM_ALARM_SATURDAY;
            }
            if (this.jcbSun.isSelected()) {
                dis.SetSundayTime(id, (byte) dSunOn.getHours(),
                        (byte) dSunOn.getMinutes(), (byte) dSunOff.getHours(), (byte) dSunOff.getMinutes());
                enableNum += Unit.PARM_ALARM_SUNDAY;
            }
        } else if (mode == 2) { //user
            Date dTueOn = (Date) jsTueOn.getValue();
            Date dTueOff = (Date) jsTueOff.getValue();
            Date dWedOn = (Date) jsWedOn.getValue();
            Date dWedOff = (Date) jsWedOff.getValue();
            Date dThuOn = (Date) jsThuOn.getValue();
            Date dThuOff = (Date) jsThuOff.getValue();
            Date dFriOn = (Date) jsFriOn.getValue();
            Date dFriOff = (Date) jsFriOff.getValue();
            if (this.jcbTue.isSelected()) {
                dis.SetTuesdayTime(id, (byte) dTueOn.getHours(),
                        (byte) dTueOn.getMinutes(), (byte) dTueOff.getHours(), (byte) dTueOff.getMinutes());
                enableNum += Unit.PARM_ALARM_TUESDAY;
            }
            if (this.jcbWed.isSelected()) {
                dis.SetWednesdayTime(id, (byte) dWedOn.getHours(),
                        (byte) dWedOn.getMinutes(), (byte) dWedOff.getHours(), (byte) dWedOff.getMinutes());
                enableNum += Unit.PARM_ALARM_WEDNESDAY;
            }
            if (this.jcbThu.isSelected()) {
                dis.SetThursdayTime(id, (byte) dThuOn.getHours(),
                        (byte) dThuOn.getMinutes(), (byte) dThuOff.getHours(), (byte) dThuOff.getMinutes());
                enableNum += Unit.PARM_ALARM_THURSDAY;
            }
            if (this.jcbFri.isSelected()) {
                dis.SetFridayTime(id, (byte) dFriOn.getHours(),
                        (byte) dFriOn.getMinutes(), (byte) dFriOff.getHours(), (byte) dFriOff.getMinutes());
                enableNum += Unit.PARM_ALARM_FRIDAY;
            }
            if (this.jcbSat.isSelected()) {
                dis.SetSaturdayTime(id, (byte) dSatOn.getHours(),
                        (byte) dSatOn.getMinutes(), (byte) dSatOff.getHours(), (byte) dSatOff.getMinutes());
                enableNum += Unit.PARM_ALARM_SATURDAY;
            }
            if (this.jcbSun.isSelected()) {
                dis.SetSundayTime(id, (byte) dSunOn.getHours(),
                        (byte) dSunOn.getMinutes(), (byte) dSunOff.getHours(), (byte) dSunOff.getMinutes());
                enableNum += Unit.PARM_ALARM_SUNDAY;
            }
        }

        //Enabled the Alarm
        dis.SetAlarmEnable(id, (byte) enableNum);

        //Displabed  other days Alarm ( compsite field 1,2,4,8,16,32,64
        dis.SetAlarmDisable(id, (byte) (127 - enableNum));  //disabled orhter schedule 

    }

    private void runIDListCheckTimer() {
        this.tmrIDListCheck.schedule(new TimerTask() {
            @Override
            public void run() {
                if (displayItems.unSaved) {
                    displayItems.saveXml();
                }
            }
        }, tmrIDListCheckSec * 1000, tmrIDListCheckSec * 1000);
    }

    private void initialTables() {
        try {

            initialPowerControl();
            initialInputSource();
            initialPIP();
            initialAdjustment();
            initialTime();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void tableHideColumn(JTable table, int column) {
        TableColumn tc = table.getTableHeader().getColumnModel().getColumn(column);
        tc.setMaxWidth(0);
        tc.setPreferredWidth(0);
        tc.setWidth(0);
        tc.setMinWidth(0);
        table.getTableHeader().getColumnModel().getColumn(column).setMaxWidth(0);
        table.getTableHeader().getColumnModel().getColumn(column).setMinWidth(0);
    }

    private void tableShowColumn(JTable table, int column, int width) {
        TableColumn tc = table.getColumnModel().getColumn(column);
        tc.setMaxWidth(width);
        tc.setPreferredWidth(width);
        tc.setWidth(width);
        tc.setMinWidth(width);
        table.getTableHeader().getColumnModel().getColumn(column).setMaxWidth(width);
        table.getTableHeader().getColumnModel().getColumn(column).setMinWidth(width);
    }

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened

        // TODO add your handling code here:
        //set App icon 
        try {
            //set the table icon (power on/off, connect on/off)
            java.net.URL pwron = ClassLoader.getSystemResource("multipledisplay/poweron.png");
            java.net.URL pwroff = ClassLoader.getSystemResource("multipledisplay/poweroff.png");
            java.net.URL connon = ClassLoader.getSystemResource("multipledisplay/connected.png");
            java.net.URL connoff = ClassLoader.getSystemResource("multipledisplay/disconnect.png");
            imgPowerOn = new ImageIcon(pwron);
            imgPowerOff = new ImageIcon(pwroff);
            imgConnect = new ImageIcon(connon);
            imgDisconnect = new ImageIcon(connoff);

            //set application icon
            java.net.URL url = ClassLoader.getSystemResource("multipledisplay/multiple_display.png");
            Toolkit kit = Toolkit.getDefaultToolkit();
            Image img = kit.createImage(url);
            setIconImage(img);

            //set program form in the middle of the Screen
            this.setLocationRelativeTo(null);

            jcobTimeMode.setSelectedIndex(0);

            //2013/10/17 計劃要寫每個table 的 refresh 功能, 目前先建好按鈕,及傳送command的函式
            //tabRefreshSend , 其餘尚未進行
            jbtnPowerRefresh.setVisible(false);
//            jbtnInputRefresh.setVisible(false);
//            jbtnAdjustRefresh.setVisible(false);
//            jbtnPipRefresh.setVisible(false);
//            jbtnTimeRefresh.setVisible(false);

            loadXML();

            initialTables();

            connectDisplay();

            //間隔一段時儲存displsys 資料
            this.runIDListCheckTimer();

            //是否開啟Log 的timer 更新資料
            if (set.logStartWithBoot) {
                this.lblServiceType.setText(SERVICEMSG + "Start");
                this.runLoggingTimer();
            } else {
                this.lblServiceType.setText(SERVICEMSG + "Stop");
            }
            //this.getContentPane().setBackground(new Color(120,120,120));
            //this.setUndecorated(true);   //Redue the 
            //  this.setOpacity(0.5f); need to set the undecorated =true;
        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }//GEN-LAST:event_formWindowOpened

    private void btnID_ListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnID_ListActionPerformed

        try {
            // Close all timer wait for ID setup finished.
            this.tmrIDListCheck.cancel();
            this.tmrLogging.cancel();     //Have to stop the time because  all connected will be disconntect.
            this.disconnectDisplay();

            //Show ID List dialog
            JDialog_ID d = new JDialog_ID(this, true);
            d.setLocationRelativeTo(this);
            d.setVisible(true);
            if (d.changed) {
                loadXML();
                initialTables();
            }

            connectDisplay();

            //間隔一段時儲存displsys 資料
            this.tmrIDListCheck = new Timer();
            this.runIDListCheckTimer();

            //是否開啟Log 的timer 更新資料
            if (set.logStartWithBoot) {
                this.tmrLogging = new Timer();
                this.runLoggingTimer();
            }
        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }//GEN-LAST:event_btnID_ListActionPerformed

    private void jsliderVolumeStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jsliderVolumeStateChanged
        this.valVolume.setText(String.valueOf(jsliderVolume.getValue()));
    }//GEN-LAST:event_jsliderVolumeStateChanged

    private void jbtnPowerUnselectAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnPowerUnselectAllActionPerformed
        tableItemSelect(dtPower, false);
    }//GEN-LAST:event_jbtnPowerUnselectAllActionPerformed

    private void jbtnPowerSelectAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnPowerSelectAllActionPerformed
        tableItemSelect(dtPower, true);
    }//GEN-LAST:event_jbtnPowerSelectAllActionPerformed

    private void PowerControlActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PowerControlActionPerformed
        tableItemSend(dtPower, evt);
    }//GEN-LAST:event_PowerControlActionPerformed

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        // TODO add your handling code here:
        this.tmrIDListCheck.cancel();
        this.tmrLogging.cancel();
        disconnectDisplay();
    }//GEN-LAST:event_formWindowClosed

    private void jbtnInputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnInputActionPerformed
        tableItemSend(dtInput, evt);
    }//GEN-LAST:event_jbtnInputActionPerformed

    private void tabSourceStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_tabSourceStateChanged

        switch (tabSource.getSelectedIndex()) {
            case 0: //power contronl
                this.loadPowerControlTable();
                break;
            case 1:
                this.loadInputSourceTable();
                break;
            case 2:
                this.loadAdjustmentTable();
                break;
            case 3:
                this.loadPIP_Table();
                break;
            case 4:
                this.loadTimeTable();
                break;
        }

    }//GEN-LAST:event_tabSourceStateChanged

    private void jbtnInputSelectAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnInputSelectAllActionPerformed
        tableItemSelect(dtInput, true);
    }//GEN-LAST:event_jbtnInputSelectAllActionPerformed

    private void jbtnInputUnselectAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnInputUnselectAllActionPerformed
        tableItemSelect(dtInput, false);
    }//GEN-LAST:event_jbtnInputUnselectAllActionPerformed

    private void btnPIPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPIPActionPerformed
        tableItemSend(dtPIP, evt);
    }//GEN-LAST:event_btnPIPActionPerformed

    private void jbtnPipSelectAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnPipSelectAllActionPerformed
        tableItemSelect(dtPIP, true);
    }//GEN-LAST:event_jbtnPipSelectAllActionPerformed

    private void jbtnPipUnselectAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnPipUnselectAllActionPerformed
        tableItemSelect(dtPIP, false);
    }//GEN-LAST:event_jbtnPipUnselectAllActionPerformed

    private void jsliderColorTempStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jsliderColorTempStateChanged
        int value = jsliderColorTemp.getValue();
        this.valColorTemp.setText(Unit.COLOR_TEMP[value]);

        jsliderGamma.setEnabled(value != 0);
        jbtnGamma.setEnabled(value != 0);

        jsliderRGain.setEnabled(value == 0);
        jsliderGGain.setEnabled(value == 0);
        jsliderBGain.setEnabled(value == 0);
        jsliderROffset.setEnabled(value == 0);
        jsliderGOffset.setEnabled(value == 0);
        jsliderBOffset.setEnabled(value == 0);

        jbtnRGain.setEnabled(value == 0);
        jbtnGGain.setEnabled(value == 0);
        jbtnBGain.setEnabled(value == 0);
        jbtnROffset.setEnabled(value == 0);
        jbtnGOffset.setEnabled(value == 0);
        jbtnBOffset.setEnabled(value == 0);

    }//GEN-LAST:event_jsliderColorTempStateChanged

    private void jsliderGammaStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jsliderGammaStateChanged
        this.valGamma.setText(Unit.GAMMA_TYPE[jsliderGamma.getValue()]);
    }//GEN-LAST:event_jsliderGammaStateChanged

    private void jsliderRGainStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jsliderRGainStateChanged
        this.valRGain.setText(String.valueOf(Functions.ShowSpecialInfo(jsliderRGain.getValue(), 128, 383)));
    }//GEN-LAST:event_jsliderRGainStateChanged

    private void jsliderGGainStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jsliderGGainStateChanged
        this.valGGain.setText(String.valueOf(Functions.ShowSpecialInfo(jsliderGGain.getValue(), 128, 383)));
    }//GEN-LAST:event_jsliderGGainStateChanged

    private void jsliderBGainStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jsliderBGainStateChanged
        this.valBGain.setText(String.valueOf(Functions.ShowSpecialInfo(jsliderBGain.getValue(), 128, 383)));
    }//GEN-LAST:event_jsliderBGainStateChanged

    private void jsliderROffsetStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jsliderROffsetStateChanged
        this.valROffset.setText(String.valueOf(Functions.ShowSpecialInfo(jsliderROffset.getValue(), -50, 50)));
    }//GEN-LAST:event_jsliderROffsetStateChanged

    private void jsliderGOffsetStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jsliderGOffsetStateChanged
        this.valGOffset.setText(String.valueOf(Functions.ShowSpecialInfo(jsliderGOffset.getValue(), -50, 50)));
    }//GEN-LAST:event_jsliderGOffsetStateChanged

    private void jsliderBOffsetStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jsliderBOffsetStateChanged
        this.valBOffset.setText(String.valueOf(Functions.ShowSpecialInfo(jsliderBOffset.getValue(), -50, 50)));
    }//GEN-LAST:event_jsliderBOffsetStateChanged

    private void jsliderBrightnessStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jsliderBrightnessStateChanged

        this.valBrightness.setText(String.valueOf(jsliderBrightness.getValue()));
    }//GEN-LAST:event_jsliderBrightnessStateChanged

    private void jsliderDBrightnessStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jsliderDBrightnessStateChanged
        this.valDBrightness.setText(String.valueOf(jsliderDBrightness.getValue()));
    }//GEN-LAST:event_jsliderDBrightnessStateChanged

    private void jsliderContrastStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jsliderContrastStateChanged
        this.valContrast.setText(String.valueOf(jsliderContrast.getValue()));
    }//GEN-LAST:event_jsliderContrastStateChanged

    private void jsliderHueStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jsliderHueStateChanged
        this.valHue.setText(String.valueOf(jsliderHue.getValue()));
    }//GEN-LAST:event_jsliderHueStateChanged

    private void jsliderSaturationStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jsliderSaturationStateChanged
        this.valSaturation.setText(String.valueOf(jsliderSaturation.getValue()));
    }//GEN-LAST:event_jsliderSaturationStateChanged

    private void jsliderPhaseStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jsliderPhaseStateChanged
        this.valPhase.setText(String.valueOf(jsliderPhase.getValue()));
    }//GEN-LAST:event_jsliderPhaseStateChanged

    private void jsliderClockStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jsliderClockStateChanged
        this.valClock.setText(String.valueOf(jsliderClock.getValue()));
    }//GEN-LAST:event_jsliderClockStateChanged

    private void jsliderSharpnessStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jsliderSharpnessStateChanged
        this.valSharpness.setText(String.valueOf(jsliderSharpness.getValue()));
    }//GEN-LAST:event_jsliderSharpnessStateChanged

    private void jsliderPowerOnDelayStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jsliderPowerOnDelayStateChanged
        this.valPowerOnDelay.setText(String.valueOf(jsliderPowerOnDelay.getValue()));
    }//GEN-LAST:event_jsliderPowerOnDelayStateChanged

    private void jbtnAdjustSelectAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnAdjustSelectAllActionPerformed
        tableItemSelect(dtAdjustment, true);
    }//GEN-LAST:event_jbtnAdjustSelectAllActionPerformed

    private void jbtnAdjustUnselectAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnAdjustUnselectAllActionPerformed
        tableItemSelect(dtAdjustment, false);
    }//GEN-LAST:event_jbtnAdjustUnselectAllActionPerformed

    private void jbtnAdjustActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnAdjustActionPerformed
        tableItemSend(dtAdjustment, evt);
    }//GEN-LAST:event_jbtnAdjustActionPerformed

    private void jcobTimeModeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcobTimeModeActionPerformed
        switch (jcobTimeMode.getSelectedIndex()) {
            case 0:
                jpanTUE.setVisible(false);
                jpanWED.setVisible(false);
                jpanTHU.setVisible(false);
                jpanFRI.setVisible(false);
                jpanSAT.setVisible(false);
                jpanSUN.setVisible(false);
                break;
            case 1:
                jpanTUE.setVisible(false);
                jpanWED.setVisible(false);
                jpanTHU.setVisible(false);
                jpanFRI.setVisible(false);
                jpanSAT.setVisible(true);
                jpanSUN.setVisible(true);
                break;
            case 2:
                jpanTUE.setVisible(true);
                jpanWED.setVisible(true);
                jpanTHU.setVisible(true);
                jpanFRI.setVisible(true);
                jpanSAT.setVisible(true);
                jpanSUN.setVisible(true);
                break;
        }
    }//GEN-LAST:event_jcobTimeModeActionPerformed

    private void jbtnTimeUnselectAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnTimeUnselectAllActionPerformed
        tableItemSelect(dtTime, false);
    }//GEN-LAST:event_jbtnTimeUnselectAllActionPerformed

    private void jbtnTimeSelectAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnTimeSelectAllActionPerformed
        tableItemSelect(dtTime, true);
    }//GEN-LAST:event_jbtnTimeSelectAllActionPerformed

    private void btnTimeactionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTimeactionPerformed
        tableItemSend(dtTime, evt);
    }//GEN-LAST:event_btnTimeactionPerformed

    private void jbtnPowerRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnPowerRefreshActionPerformed
    }//GEN-LAST:event_jbtnPowerRefreshActionPerformed

    private void jbtnInputRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnInputRefreshActionPerformed
        for (int i = 0; i < dtInput.getRowCount(); i++) {
            boolean selected = Boolean.parseBoolean(dtInput.getValueAt(i, 0).toString());
            if (!selected) {
                continue;
            }

            byte id = Byte.parseByte(dtInput.getValueAt(i, 1).toString());
            String ip = dtInput.getValueAt(i, 2).toString();

            if (set.useCOM) {
                if (!comDisplay.IsConnected) {
                    comDisplay.Connect();
                    if (comDisplay.IsConnected) {
                        displayAddListener(comDisplay);
                    } else {
                        Functions.showMsg(jTextAreaInfo, "Connect to Display[" + comDisplay.comPort + "] is  time out.");
                        return;
                    }
                }
                comDisplay.GetInputSource(id);
            } else {
                for (int j = 0; j < netDisplays.size(); j++) {

                    //Re-connect when Display doesn't connect.
                    if (!netDisplays.get(j).ip.equals(ip)) {
                        continue;
                    }
                    this.GetNetInputStatus(netDisplays.get(j), j);
                    break;
                }
            }
        }
    }//GEN-LAST:event_jbtnInputRefreshActionPerformed

    private void jbtnAdjustRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnAdjustRefreshActionPerformed
        for (int i = 0; i < dtAdjustment.getRowCount(); i++) {
            boolean selected = Boolean.parseBoolean(dtAdjustment.getValueAt(i, 0).toString());
            if (!selected) {
                continue;
            }

            byte id = Byte.parseByte(dtAdjustment.getValueAt(i, 1).toString());
            String ip = dtAdjustment.getValueAt(i, 2).toString();

            if (set.useCOM) {
                if (!comDisplay.IsConnected) {
                    comDisplay.Connect();
                    if (comDisplay.IsConnected) {
                        displayAddListener(comDisplay);
                    } else {
                        Functions.showMsg(jTextAreaInfo, "Connect to Display[" + comDisplay.comPort + "] is  time out.");
                        return;
                    }
                }
                GetAdjustmentStatus(comDisplay, id);
            } else {
                for (int j = 0; j < netDisplays.size(); j++) {
                    //Re-connect when Display doesn't connect.
                    if (!netDisplays.get(j).ip.equals(ip)) {
                        continue;
                    }
                    this.GetNetAdjustmentStatus(netDisplays.get(j), j);
                    break;
                }
            }
        }
    }//GEN-LAST:event_jbtnAdjustRefreshActionPerformed

    private void jbtnPipRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnPipRefreshActionPerformed
        for (int i = 0; i < dtPIP.getRowCount(); i++) {
            boolean selected = Boolean.parseBoolean(dtPIP.getValueAt(i, 0).toString());
            if (!selected) {
                continue;
            }

            byte id = Byte.parseByte(dtPIP.getValueAt(i, 1).toString());
            String ip = dtPIP.getValueAt(i, 2).toString();

            if (set.useCOM) {
                if (!comDisplay.IsConnected) {
                    comDisplay.Connect();
                    if (comDisplay.IsConnected) {
                        displayAddListener(comDisplay);
                    } else {
                        Functions.showMsg(jTextAreaInfo, "Connect to Display[" + comDisplay.comPort + "] is  time out.");
                        return;
                    }
                    GetPIPStatus(comDisplay, id);
                }
            } else {
                for (int j = 0; j < netDisplays.size(); j++) {
                    //Re-connect when Display doesn't connect.
                    if (!netDisplays.get(j).ip.equals(ip)) {
                        continue;
                    }
                    GetNetPIP_Status(netDisplays.get(j), j);
                    break;
                }
            }
        }

    }//GEN-LAST:event_jbtnPipRefreshActionPerformed
    private void jbtnTimeRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnTimeRefreshActionPerformed

        for (int i = 0; i < dtTime.getRowCount(); i++) {
            boolean selected = Boolean.parseBoolean(dtTime.getValueAt(i, 0).toString());
            if (!selected) {
                continue;
            }

            byte id = Byte.parseByte(dtTime.getValueAt(i, 1).toString());
            String ip = dtTime.getValueAt(i, 2).toString();

            if (set.useCOM) {
                if (!comDisplay.IsConnected) {
                    comDisplay.Connect();
                    if (comDisplay.IsConnected) {
                        displayAddListener(comDisplay);
                    } else {
                        Functions.showMsg(jTextAreaInfo, "Connect to Display[" + comDisplay.comPort + "] is  time out.");
                        return;
                    }
                }
                GetTimeStatus(comDisplay, id);

            } else {
                for (int j = 0; j < netDisplays.size(); j++) {
                    //Re-connect when Display doesn't connect.
                    if (!netDisplays.get(j).ip.equals(ip)) {
                        continue;
                    }
                    GetNetTimeStatus(comDisplay, id);
                    break;
                }
            }
        }
    }//GEN-LAST:event_jbtnTimeRefreshActionPerformed

    private void jbtnReconnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnReconnectActionPerformed
        try {
            disconnectDisplay();
            Thread.sleep(1000);

        } catch (InterruptedException ex) {
            Logger.getLogger(CMD.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        connectDisplay();
    }//GEN-LAST:event_jbtnReconnectActionPerformed

    private void jbtnSettingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnSettingActionPerformed
        try {
            tmrLogging.cancel();

            JDialogSetting d = new JDialogSetting(this, true);
            d.setLocationRelativeTo(this);
            d.setVisible(true);

            if (d.saveChanged) {
                loadXML();
                initialPowerControl();   //re-arranage the power control field
            }
            if (set.logStartWithBoot) {
                tmrLogging = new Timer();
                this.runLoggingTimer();
            }
        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }//GEN-LAST:event_jbtnSettingActionPerformed

    private void jlblNoteMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlblNoteMouseClicked
        if (jTextAreaInfo.getText().equals("")) {
            return;
        }

        if (Functions.showYesNoDialog("Would you like to clean Notication area?")) {
            this.jTextAreaInfo.setText("");
        }
    }//GEN-LAST:event_jlblNoteMouseClicked

    private void jbtnOpenLogFolderPowerControlActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnOpenLogFolderPowerControlActionPerformed
        final Runtime rt = Runtime.getRuntime();
        try {
            SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
            todayLogFolder = set.getLogFolder() + "\\" + sdfDate.format(new Date());
            File file = new File(todayLogFolder);
            if (!file.exists()) {
                file.mkdirs();  //create today folder;
            }
            rt.exec("explorer.exe /select," + todayLogFolder);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }//GEN-LAST:event_jbtnOpenLogFolderPowerControlActionPerformed

    private void jsliderBacklightStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jsliderBacklightStateChanged
        this.valBacklight.setText(String.valueOf(jsliderBacklight.getValue()));
    }//GEN-LAST:event_jsliderBacklightStateChanged

    private void lblServiceTypeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblServiceTypeMouseClicked
        if (set.logStartWithBoot) {
            tmrLogging.cancel();

            this.lblServiceType.setText(SERVICEMSG + "Stop");
            set.logStartWithBoot = false;
        } else {
            this.lblServiceType.setText(SERVICEMSG + "Start");
            tmrLogging = new Timer();
            this.runLoggingTimer();
            set.logStartWithBoot = true;
        }
    }//GEN-LAST:event_lblServiceTypeMouseClicked

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
            java.util.logging.Logger.getLogger(CMD.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CMD.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CMD.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CMD.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new CMD().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnID_List;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
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
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
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
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JTextArea jTextAreaInfo;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JButton jbtnAdjustRefresh;
    private javax.swing.JButton jbtnAdjustSelectAll;
    private javax.swing.JButton jbtnAdjustUnselectAll;
    private javax.swing.JButton jbtnAutoAdjust;
    private javax.swing.JButton jbtnBGain;
    private javax.swing.JButton jbtnBL_Off;
    private javax.swing.JButton jbtnBL_On;
    private javax.swing.JButton jbtnBOffset;
    private javax.swing.JButton jbtnBacklight;
    private javax.swing.JButton jbtnBrightness;
    private javax.swing.JButton jbtnButtonLeft;
    private javax.swing.JButton jbtnButtonRight;
    private javax.swing.JButton jbtnCheckTime;
    private javax.swing.JButton jbtnCinema;
    private javax.swing.JButton jbtnClock;
    private javax.swing.JButton jbtnColorTemp;
    private javax.swing.JButton jbtnComponent1;
    private javax.swing.JButton jbtnComponent11;
    private javax.swing.JButton jbtnComponent12;
    private javax.swing.JButton jbtnComponent2;
    private javax.swing.JButton jbtnComposite1;
    private javax.swing.JButton jbtnComposite11;
    private javax.swing.JButton jbtnComposite12;
    private javax.swing.JButton jbtnComposite2;
    private javax.swing.JButton jbtnContrast;
    private javax.swing.JButton jbtnDBrightness;
    private javax.swing.JButton jbtnDVI;
    private javax.swing.JButton jbtnDVI1;
    private javax.swing.JButton jbtnDisplayPort;
    private javax.swing.JButton jbtnDisplayPort1;
    private javax.swing.JButton jbtnFill;
    private javax.swing.JButton jbtnGGain;
    private javax.swing.JButton jbtnGOffset;
    private javax.swing.JButton jbtnGame;
    private javax.swing.JButton jbtnGamma;
    private javax.swing.JButton jbtnHDMI1;
    private javax.swing.JButton jbtnHDMI11;
    private javax.swing.JButton jbtnHDMI12;
    private javax.swing.JButton jbtnHDMI13;
    private javax.swing.JButton jbtnHDMI14;
    private javax.swing.JButton jbtnHDMI2;
    private javax.swing.JButton jbtnHDMI3;
    private javax.swing.JButton jbtnHDMI4;
    private javax.swing.JButton jbtnHDSDI1;
    private javax.swing.JButton jbtnHDSDI11;
    private javax.swing.JButton jbtnHDSDI12;
    private javax.swing.JButton jbtnHDSDI2;
    private javax.swing.JButton jbtnHue;
    private javax.swing.JButton jbtnInputRefresh;
    private javax.swing.JButton jbtnInputSelectAll;
    private javax.swing.JButton jbtnInputUnselectAll;
    private javax.swing.JButton jbtnLarge;
    private javax.swing.JButton jbtnLetterBox;
    private javax.swing.JButton jbtnLockKey;
    private javax.swing.JButton jbtnMedium;
    private javax.swing.JButton jbtnMuteOff;
    private javax.swing.JButton jbtnMuteOn;
    private javax.swing.JButton jbtnNative;
    private javax.swing.JButton jbtnOff;
    private javax.swing.JButton jbtnOpenLogFolder;
    private javax.swing.JButton jbtnPhase;
    private javax.swing.JButton jbtnPillarBox;
    private javax.swing.JButton jbtnPipRefresh;
    private javax.swing.JButton jbtnPipSelectAll;
    private javax.swing.JButton jbtnPipUnselectAll;
    private javax.swing.JButton jbtnPowerOff;
    private javax.swing.JButton jbtnPowerOn;
    private javax.swing.JButton jbtnPowerOnDelay;
    private javax.swing.JButton jbtnPowerRefresh;
    private javax.swing.JButton jbtnPowerSelectAll;
    private javax.swing.JButton jbtnPowerUnselectAll;
    private javax.swing.JButton jbtnRGain;
    private javax.swing.JButton jbtnROffset;
    private javax.swing.JButton jbtnReconnect;
    private javax.swing.JButton jbtnSVideo;
    private javax.swing.JButton jbtnSVideo1;
    private javax.swing.JButton jbtnSaturation;
    private javax.swing.JButton jbtnSetting;
    private javax.swing.JButton jbtnSharpness;
    private javax.swing.JButton jbtnSideBySide;
    private javax.swing.JButton jbtnSmall;
    private javax.swing.JButton jbtnSport;
    private javax.swing.JButton jbtnSwap;
    private javax.swing.JButton jbtnTimeApply;
    private javax.swing.JButton jbtnTimeRefresh;
    private javax.swing.JButton jbtnTimeSelectAll;
    private javax.swing.JButton jbtnTimeUnselectAll;
    private javax.swing.JButton jbtnTopLeft;
    private javax.swing.JButton jbtnTopRight;
    private javax.swing.JButton jbtnUnlockKey;
    private javax.swing.JButton jbtnUser;
    private javax.swing.JButton jbtnVGA;
    private javax.swing.JButton jbtnVGA1;
    private javax.swing.JButton jbtnVivid;
    private javax.swing.JButton jbtnVolume;
    private javax.swing.JButton jbtnWOD_Off;
    private javax.swing.JButton jbtnWOD_On;
    private javax.swing.JButton jbtnZoomIn;
    private javax.swing.JButton jbtnZoomOut;
    private javax.swing.JCheckBox jcbFri;
    private javax.swing.JCheckBox jcbMon;
    private javax.swing.JCheckBox jcbSat;
    private javax.swing.JCheckBox jcbSun;
    private javax.swing.JCheckBox jcbThu;
    private javax.swing.JCheckBox jcbTue;
    private javax.swing.JCheckBox jcbWed;
    private javax.swing.JComboBox jcobTimeMode;
    private javax.swing.JLabel jlblNote;
    private javax.swing.JPanel jpanFRI;
    private javax.swing.JPanel jpanMon;
    private javax.swing.JPanel jpanSAT;
    private javax.swing.JPanel jpanSUN;
    private javax.swing.JPanel jpanTHU;
    private javax.swing.JPanel jpanTUE;
    private javax.swing.JPanel jpanTimeMode;
    private javax.swing.JPanel jpanWED;
    private javax.swing.JSpinner jsFriOff;
    private javax.swing.JSpinner jsFriOn;
    private javax.swing.JSpinner jsMonOff;
    private javax.swing.JSpinner jsMonOn;
    private javax.swing.JSpinner jsSatOff;
    private javax.swing.JSpinner jsSatOn;
    private javax.swing.JSpinner jsSunOff;
    private javax.swing.JSpinner jsSunOn;
    private javax.swing.JSpinner jsThuOff;
    private javax.swing.JSpinner jsThuOn;
    private javax.swing.JSpinner jsTueOff;
    private javax.swing.JSpinner jsTueOn;
    private javax.swing.JSpinner jsWedOff;
    private javax.swing.JSpinner jsWedOn;
    private javax.swing.JSlider jsliderBGain;
    private javax.swing.JSlider jsliderBOffset;
    private javax.swing.JSlider jsliderBacklight;
    private javax.swing.JSlider jsliderBrightness;
    private javax.swing.JSlider jsliderClock;
    private javax.swing.JSlider jsliderColorTemp;
    private javax.swing.JSlider jsliderContrast;
    private javax.swing.JSlider jsliderDBrightness;
    private javax.swing.JSlider jsliderGGain;
    private javax.swing.JSlider jsliderGOffset;
    private javax.swing.JSlider jsliderGamma;
    private javax.swing.JSlider jsliderHue;
    private javax.swing.JSlider jsliderPhase;
    private javax.swing.JSlider jsliderPowerOnDelay;
    private javax.swing.JSlider jsliderRGain;
    private javax.swing.JSlider jsliderROffset;
    private javax.swing.JSlider jsliderSaturation;
    private javax.swing.JSlider jsliderSharpness;
    private javax.swing.JSlider jsliderVolume;
    private javax.swing.JTable jtableAdjustment;
    private javax.swing.JTable jtableInput;
    private javax.swing.JTable jtablePIP;
    private javax.swing.JTable jtablePower;
    private javax.swing.JTable jtableTime;
    private javax.swing.JLabel lblLastLoggingTime;
    private javax.swing.JLabel lblServiceType;
    private javax.swing.JPanel tabAdjustment;
    private javax.swing.JPanel tabDisplayAdjustment;
    private javax.swing.JPanel tabInputSource;
    private javax.swing.JPanel tabPIP;
    private javax.swing.JPanel tabPipSize;
    private javax.swing.JPanel tabPipSource;
    private javax.swing.JPanel tabPowerControl;
    private javax.swing.JPanel tabScaling;
    private javax.swing.JTabbedPane tabSource;
    private javax.swing.JPanel tabTime;
    private javax.swing.JLabel valBGain;
    private javax.swing.JLabel valBOffset;
    private javax.swing.JLabel valBacklight;
    private javax.swing.JLabel valBrightness;
    private javax.swing.JLabel valClock;
    private javax.swing.JLabel valColorTemp;
    private javax.swing.JLabel valContrast;
    private javax.swing.JLabel valDBrightness;
    private javax.swing.JLabel valGGain;
    private javax.swing.JLabel valGOffset;
    private javax.swing.JLabel valGamma;
    private javax.swing.JLabel valHue;
    private javax.swing.JLabel valPhase;
    private javax.swing.JLabel valPowerOnDelay;
    private javax.swing.JLabel valRGain;
    private javax.swing.JLabel valROffset;
    private javax.swing.JLabel valSaturation;
    private javax.swing.JLabel valSharpness;
    private javax.swing.JLabel valVolume;
    // End of variables declaration//GEN-END:variables
}
