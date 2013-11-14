/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package multipledisplay;

import clt.api.Ncmd;
import clt.api.Unit;
import com.sun.jna.ptr.IntByReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

/**
 *
 * @author A000847
 */
public class Functions {

    static final String cfgXml = "Settings.xml";
    static final String displayXml = "Displays.xml";

    public static void showMsgDialog(String ss) {
        JOptionPane.showMessageDialog(null, ss);
    }

    public static boolean showYesNoDialog(String ss) {
        int i = JOptionPane.showConfirmDialog(null, ss, "Warning", JOptionPane.YES_NO_OPTION);
        if (i == 0) {
            return true;
        } else {
            return false;
        }
    }

    public static void showMsg(JTextArea textArea, String ss) {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        ss = sdf.format(date) + " " + ss + "\r\n";
        //ss = ss + "\r\n";
        textArea.append(ss);
        textArea.setSelectionStart(textArea.getText().length());

    }

    public static void Delay(int millSeconds) {
        try {
            Thread.sleep(millSeconds);
        } catch (InterruptedException exp) {
        }
    }

    public static int ShowSpecialInfo(int value, int minValue, int maxValue) {
        int val = 0;
        try {
            val = maxValue - ((maxValue - minValue) - value);
            //int mid = ((maxValue - minValue+1) / 2)+minValue;           //128

//        if (val == mid) {
//            val=mid;
//        }else if (val>mid){
//            val= maxValue-val;
//        }else{
//           val =(mid - val)+minValue;
//        }
        } catch (Exception exp) {
            exp.printStackTrace();
        }
        return val;
    }

    public static ArrayList<Ncmd.pIP210Node2> SearchDevice() {
        ArrayList<Ncmd.pIP210Node2> aryP210 = new ArrayList();
        try {
            IntByReference pThis = Ncmd.lib.initNCMD(-1, (short) 5000, (short) 2000);
            Ncmd.pIP210Node getP210 = Ncmd.lib.NcmdSrhDevice(pThis);
            if (getP210 == null) {
                Functions.showMsgDialog("No devices are searched.");
                return aryP210;
            }
            while (getP210 != null) {
                try {
                    Ncmd.pIP210Node2 p2 = new Ncmd.pIP210Node2();
                    //Device ID
                    p2.DeviceId = getP210.DeviceId;
                    System.out.println(getP210.DeviceId);

                    //Nickname
                    byte[] nickary = Unit.Bytes2BytesRedue0(getP210.Nickname);
                    p2.Nickname = Unit.Bytes2String(nickary);
                    System.out.println(Unit.Bytes2String(nickary));

                    //ProjectName
                    byte[] projectary = Unit.Bytes2BytesRedue0(getP210.ProjectName);
                    p2.ProjectName = Unit.Bytes2String(projectary);
                    System.out.println(Unit.Bytes2String(projectary));

                    //IP
                    byte[] ipary = getP210.IPAddr;
                    String ip = "";
                    for (byte b : ipary) {
                        ip += b & 0xFF;
                        ip += ".";
                    }
                    ip = ip.substring(0, ip.length() - 1);
                    p2.IPAddr = ip;
                    System.out.println(ip);

                    //Submask
                    byte[] suary = getP210.IPMask;
                    String su = "";
                    for (byte b : suary) {
                        su += b & 0xFF;
                        su += ".";
                    }
                    su = su.substring(0, su.length() - 1);
                    p2.IPMask = su;
                    System.out.println(su);

                    //GateWay
                    byte[] gwary = getP210.IPGateway;
                    String gw = "";
                    for (byte b : gwary) {
                        gw += b & 0xFF;
                        gw += ".";
                    }
                    gw = gw.substring(0, gw.length() - 1);
                    p2.IPGateway = gw;
                    System.out.println(gw);

                    //mac
                    byte[] macart = getP210.MACAddr;
                    String mac = "";
                    for (int i = 0; i < macart.length; i++) {
                        mac += Integer.toString((macart[i] & 0xff) + 0x100, 16).toUpperCase().substring(1) + "-";
                    }
                    mac = mac.substring(0, mac.length() - 1);
                    p2.MacAddr = mac;
                    System.out.println(mac);

                    aryP210.add(p2);
                    getP210 = getP210.next;
                } catch (Exception | StackOverflowError exp) {
                    exp.printStackTrace();
                    //       continue;
                }
            }
            return aryP210;

        } catch (Exception exp) {
            //Functions.showMsgDialog(exp.getMessage());
            return null;
        }
    }
}
