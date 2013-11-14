/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package multipledisplay;

/**
 *
 * @author 7000006 Current use the DOM to parse the xml format (because size is
 * samll. we can use dom4j in the feature if xml is larger
 */
import java.io.*;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import org.xml.sax.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;

public class XMLInJTable extends AbstractTableModel {

    Vector data;
    Vector columns;

    public XMLInJTable(String xml) {
        try {
            File f = new File(xml);
            if (!f.exists()) {
                return;
            }
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(f);

            data = new Vector();
            columns = new Vector();
            String data1 = "", data2 = "", data3 = "";
            NodeList nl = doc.getElementsByTagName("Displays");
            int Rowscount = nl.getLength();
            int Colscount = nl.item(0).getAttributes().getLength();
            for (int i = 0; i < nl.getLength(); i++) {
                Element node = (Element) nl.item(i);//得列表中的每一个Node对象。
                line = node.getElementsByTagName("NAME").item(0).getFirstChild().getNodeValue()
                System.out.println();
                System.out.println(node.getElementsByTagName("ADDRESS").item(0).getFirstChild().getNodeValue());
                System.out.println(node.getElementsByTagName("TEL").item(0).getFirstChild().getNodeValue());
                System.out.println(node.getElementsByTagName("FAX").item(0).getFirstChild().getNodeValue());
                System.out.println(node.getElementsByTagName("EMAIL").item(0).getFirstChild().getNodeValue());
                StringTokenizer st2 = new StringTokenizer(line, " ");
                while (st2.hasMoreTokens()) {
                    data.addElement(st2.nextToken());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

//                        NodeList n2 = doc.getElementsByTagName("Address");
//                        NodeList n3 = doc.getElementsByTagName("ContactNo");
//                        NodeList listOfPersons = doc.getElementsByTagName("Person");
//                        int Rowscount=listOfPersons.getLength();
//                        int Colscount=listOfPersons.item(0).getAttributes().getLength();
//                        String data1 = "", data2 = "", data3 = "";
//                        
//                        data = new Vector();
//                        columns = new Vector();
//                        for (int i = 0; i < listOfPersons.getLength(); i++) {
//                                data1 = nl.item(i).getFirstChild().getNodeValue();
//                                data2 = n2.item(i).getFirstChild().getNodeValue();
//                                data3 = n3.item(i).getFirstChild().getNodeValue();
//                                String line = data1 + " " + data2 + " " + data3;
//                                StringTokenizer st2 = new StringTokenizer(line, " ");
//                                while (st2.hasMoreTokens())
//                                        data.addElement(st2.nextToken());
//                        }
//                        columns.add("Name");
//                        columns.add("Address");
//                        columns.add("ContactNo");

    }

    public int getRowCount() {
        return data.size() / getColumnCount();
    }

    public int getColumnCount() {
        return columns.size();
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        return (String) data.elementAt((rowIndex * getColumnCount())
                + columnIndex);
    }
//        public static void main(String argv[]) throws Exception {
//                XMLInJTable t = new XMLInJTable();
//                JTable table = new JTable();
//                table.setModel(t);
//                JScrollPane scrollpane = new JScrollPane(table);
//                JPanel panel = new JPanel();
//                panel.add(scrollpane);
//                JFrame frame = new JFrame();
//                frame.add(panel, "Center");
//                frame.pack();
//                frame.setVisible(true);
//        }
}