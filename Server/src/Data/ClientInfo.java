package Data;

import java.util.Calendar;

public class ClientInfo {

    private String username;
    private String serverIP;
    private int serverPort;

    public ClientInfo(String usr, String sIP, int sPort){
        username = usr;
        serverIP = sIP;
        serverPort = sPort;
    }

    public String getUsername() {
        return username;
    }

    public String getServerIP() {
        return serverIP;
    }

    public int getServerPort() {
        return serverPort;
    }
}
