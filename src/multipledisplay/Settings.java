/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package multipledisplay;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

@XmlRootElement(name = "Settings")
public class Settings {

    String xml = "Settings.xml";
    @XmlElement
    public boolean useCOM = false;
    @XmlElement
    public boolean logStartWithBoot = false;
    @XmlElement
    public boolean logFan0Speed = false;
    @XmlElement
    public boolean logFan1Speed = false;
    @XmlElement
    public boolean logDigitalBrightnessLevel = true;
    @XmlElement
    public boolean logConnectStatus = true;
    @XmlElement
    public boolean logBacklightSatus = true;
    @XmlElement
    public boolean logInputSource = true;
    @XmlElement
    public boolean logMuteStatus = false;
    @XmlElement
    public boolean logContrast = false;
    @XmlElement
    public boolean logPhase = false;
    @XmlElement
    public boolean logColorTemperature = false;
    @XmlElement
    public boolean logBrightness = true;
    @XmlElement
    public boolean logVolume = true;
    private String COM = "COM1";

    @XmlElement
    public void setCOM(String com) {
        COM = com;
    }

    public String getCOM() {
        return COM;
    }
    private String BuadRate = "115200";

    @XmlElement
    public void setBaudRate(String baudRate) {
        BuadRate = baudRate;
    }

    public String getBaudRate() {
        return BuadRate;
    }
    private int StopBit = 1;

    @XmlElement
    public void setStopBit(int stopBit) {
        StopBit = stopBit;
    }

    public int getStopBit() {
        return StopBit;
    }
    private int DataBit = 8;

    @XmlElement
    public void setDataBit(int dataBit) {
        DataBit = dataBit;
    }

    public int getDataBit() {
        return DataBit;
    }
    private String Parity = "None";

    @XmlElement
    public void setParity(String parity) {
        Parity = parity;
    }

    public String getParity() {
        return Parity;
    }
    private String IP = "";

    @XmlElement
    public void setLocalIP(String ip) {
        IP = ip;
    }

    public String getLocalIP() {
        return IP;
    }
    private String Port = "23";

    @XmlElement
    public void setPort(String port) {
        Port = port;
    }

    public String getPort() {
        return Port;
    }
    private String LogFolder = "";

    @XmlElement
    public void setLogFolder(String logFolder) {
        LogFolder = logFolder;
    }

    public String getLogFolder() {
        return LogFolder;
    }
    private String UpdateFrequency = "10";

    @XmlElement
    public void setUpdateFrequency(String frequency) {
        UpdateFrequency = frequency;
    }

    public String getUpdateFrequency() {
        return UpdateFrequency;
    }

    public void SaveXML() {
        SaveXML(xml);
    }

    public void SaveXML(String xmlPath) {
        try {
            xml = xmlPath;
            File file = new File(xmlPath);//"C:\\file.xml"
            JAXBContext jaxbContext = JAXBContext.newInstance(Settings.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

            // output pretty printed  
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            jaxbMarshaller.marshal(this, file);
            jaxbMarshaller.marshal(this, System.out);

        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings( "unchecked")
    public static Settings LoadXML(String xmlPath) {
        Settings xmlSettings = new Settings();
        try {
            JAXBContext context;
            Source source = new StreamSource(new FileInputStream(xmlPath));
            context = JAXBContext.newInstance(Settings.class);
            Unmarshaller marshaller = context.createUnmarshaller();
            JAXBElement element = (JAXBElement) marshaller.unmarshal(source, Settings.class);
            // 讀取出根結點  
            xmlSettings = (Settings) element.getValue();
            xmlSettings.xml = xmlPath;
            return xmlSettings;
            //           System.out.println(name);  

            // 獲取另一子結點  
//            Rank rank = person.getRank();  
//              
//            String accountId = rank.getAccountId();  
//            double  money = rank.getMoney();  
//              
//            System.out.println(accountId);  
//            System.out.println(money);  
        } catch (FileNotFoundException | JAXBException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Settings() {
    }
}
