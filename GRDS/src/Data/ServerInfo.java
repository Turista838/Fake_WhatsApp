package Data;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class ServerInfo {

    private String serverIP;
    private String serverPort;
    private Calendar lastTimeOnline;

    public ServerInfo(String sIP, String sPort){
        lastTimeOnline = GregorianCalendar.getInstance();
    }

}
