/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package multipledisplay;

import java.io.*;
import java.util.ArrayList;

import java.util.Vector;
import javax.swing.table.AbstractTableModel;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.xml.sax.SAXException;

/**
 *
 */
public final class XML_DOM extends AbstractTableModel {

    private Document doc;
    private String filename;
    private Vector data;
    private Vector columns;
    private final String DISPLAY_ITEM = "displayItem";

    public XML_DOM(String name) throws
            ParserConfigurationException {
        filename = name;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        File file = new File(name);

        if (file.exists()) {
            try {
                doc = builder.parse(name);
            } catch (SAXException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else {
            doc = builder.newDocument();
            //WriteSample("中文题目", "中文内容");
            saveXML();
        }
    }

    public void saveXML() {
        saveXML(filename);
    }

    public void saveXML(String targetFile) {
        try {

            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            DOMSource source = new DOMSource(doc);

            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            PrintWriter pw = new PrintWriter(new FileOutputStream(targetFile));
            StreamResult result = new StreamResult(pw);
            transformer.transform(source, result);
        } catch (TransformerException e) {
            e.printStackTrace();
        } catch (IOException exp) {
            exp.printStackTrace();
        }
    }

    public boolean updateNode(String checkField, String checkValue, String[][] newValue) {
        boolean res = false;
        if (doc == null) {
            return res;
        }
        NodeList nl = doc.getElementsByTagName(DISPLAY_ITEM);
        for (int i = 0; i < nl.getLength(); i++) {
            Element node = (Element) nl.item(i);//得列表中的每一个Node对象。
            String checkVal = node.getElementsByTagName(checkField).item(0).getFirstChild().getNodeValue();
            if (checkVal.equals(checkValue)) {
                for (int j = 0; j < newValue.length; j++) {
                    node.getElementsByTagName(newValue[j][0]).item(0).getFirstChild().setNodeValue(newValue[j][1]);
                    res = true;
                    break;
                }
            }
        }
        return res;
    }

    public void addNode(String[] Fields, Vector newValue) {

        Element root = doc.getDocumentElement();
            Element newNode = doc.createElement(DISPLAY_ITEM);

            for (int j = 0; j < newValue.size(); j++) {
                Node n = doc.createElement(Fields[j]);                   //Create Field
                Text txtValue = doc.createTextNode(newValue.get(j).toString());   //Create Value
                n.appendChild(txtValue);                                                //New Field add value
                newNode.appendChild(n);                                               //DisplayItem node add sub-node
            }
        
            root.appendChild(newNode);                                           //Root node add new field and values
    }

    public void deleteNode(String delField, String delValue) {

        NodeList nl = doc.getElementsByTagName(DISPLAY_ITEM);
        for (int i = 0; i < nl.getLength(); i++) {
            Element node = (Element) nl.item(i);
            String val = node.getElementsByTagName(delField).item(0).getFirstChild().getNodeValue();
            if (delValue.equals(delValue)) {
                Node n = nl.item(i);
                n.getParentNode().removeChild(n);  //getParentNode返回此节点的父节点。
                break;
            }
        }
    }

    public String readNode(String nodename) {
        Node node = doc.getElementsByTagName(nodename).item(0);
        return node.getTextContent();
    }

    public String readAttribute(String nodename, String itemname) {
        Node node = doc.getElementsByTagName(nodename).item(0);
        return node.getAttributes().getNamedItem(itemname).getTextContent();
    }

    public String parseString(String str, String nodename, String itemname) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dber;
        try {
            dber = factory.newDocumentBuilder();

            str = "<xroot" + str + "</xroot";

            ByteArrayInputStream bais = null;
            try {
                bais = new ByteArrayInputStream(str.getBytes("UTF-8"));
                Document docf;
                try {
                    docf = dber.parse(bais);

                    Node node = docf.getElementsByTagName(nodename).item(0);
                    return node.getAttributes().getNamedItem(itemname).getTextContent();
                } catch (IOException | SAXException ex) {

                    ex.printStackTrace();
                }

            } catch (UnsupportedEncodingException uee) {
                uee.printStackTrace();
            }
        } catch (ParserConfigurationException ex) {
            ex.printStackTrace();
        }
        return "";
    }

    public ArrayList<Vector> readToJTable(String[] fields) {
        ArrayList<Vector> v1 = new ArrayList<>();
        //data = new Vector();
        //columns = new Vector();
        String[][] data1 = new String[fields.length][fields.length];
        NodeList nl = doc.getElementsByTagName(DISPLAY_ITEM);

        for (int i = 0; i < nl.getLength(); i++) {
            Element node = (Element) nl.item(i);
            String line = "";
            String[] line2 = new String[fields.length];
            Vector v = new Vector();
            for (int j = 0; j < fields.length; j++) {
                //line += node.getElementsByTagName(fields[j]).item(0).getFirstChild().getNodeValue() + " ";
                String val = node.getElementsByTagName(fields[j]).item(0).getFirstChild().getNodeValue();
                if (val.equalsIgnoreCase("true") || val.equalsIgnoreCase("false")) {
                    v.addElement(Boolean.parseBoolean(val));
                } else {
                    v.addElement(val);
                }
            }
            v1.add(v);
        }
        return v1;
    }

//    public void readToJTable(String[] fields) {
//        data = new Vector();
//        columns = new Vector();
//
//        NodeList nl = doc.getElementsByTagName(DISPLAY_ITEM);
//
//        for (int i = 0; i < nl.getLength(); i++) {
//            Element node = (Element) nl.item(i);
//            String line = "";
//            for (int j = 0; j < fields.length; j++) {
//                line += node.getElementsByTagName(fields[j]).item(0).getFirstChild().getNodeValue() + " ";
//            }
//
//            StringTokenizer st2 = new StringTokenizer(line, " ");
//            while (st2.hasMoreTokens()) {
//                data.addElement(st2.nextToken());
//            }
//        }
//        for (int k = 0; k < fields.length; k++) {
//            columns.add(fields[k]);
//            columns.addElement(fields[k]);
//        }
//    }
    @Override
    public int getRowCount() {
        return data.size() / getColumnCount();
    }

    @Override
    public int getColumnCount() {
        return columns.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return (String) data.elementAt((rowIndex * getColumnCount())
                + columnIndex);
    }
}
