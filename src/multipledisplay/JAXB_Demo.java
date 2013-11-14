/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package multipledisplay;

import clt.api.Displays;

public class JAXB_Demo {

    public static void main(String[] args) {
        Displays di = new Displays();

        //save java class to xml file
        for (int i = 0; i < 3; i++) {
            di.addDisplayItem((byte)0,"NetUart","192.168.1.233", "034234565");
        }
        di.saveXML("Displays.xml");

        //load xml file to be java class
//        di.loadXML("c:\\Displays.xml");
//        for (int i=0;i<di.displayItem.size();i++)
//        {
//            DisplayItem d = di.displayItem.get(i);
//            System.out.println(d.getID());
//        }
    }
}