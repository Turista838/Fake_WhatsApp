package Data;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class ServerInfo {

    private String serverIP;
    private int serverPort;
    private Calendar lastTimeOnline;
    private boolean active;

    public ServerInfo(String sIP, int sPort){
        active = true;
        serverIP = sIP;
        serverPort = sPort;
        lastTimeOnline = GregorianCalendar.getInstance();
    }

    public void setActive(boolean active) { this.active = active; }

    public boolean isActive() { return active; }

    public String getServerIP() {
        return serverIP;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void updateTime() { lastTimeOnline = GregorianCalendar.getInstance(); }

    public Calendar getLastTimeOnline() { return lastTimeOnline; }
}
