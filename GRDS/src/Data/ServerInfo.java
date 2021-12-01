package Data;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class ServerInfo {

    private String serverIP;
    private int serverPort;
    private Calendar lastTimeOnline;

    public ServerInfo(String sIP, int sPort){
        serverIP = sIP;
        serverPort = sPort;
        lastTimeOnline = GregorianCalendar.getInstance();
    }

    public String getServerIP() {
        return serverIP;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void updateTime() {
        lastTimeOnline = GregorianCalendar.getInstance();
    }
}
