package SharedClasses;

import java.io.Serializable;
import java.net.InetAddress;

public class GRDSClientMessageUDP implements Serializable {

    public static final long serialVersionID = 3;
    private String serverIP;
    private int serverPort;

    public void buildMessage(String ip, String port) {
        serverIP = ip;
        serverPort = Integer.parseInt(port);
    }

    public String getServerIP() {
        return serverIP;
    }

    public int getServerPort() {
        return serverPort;
    }
}
