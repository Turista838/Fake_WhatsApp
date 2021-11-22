package SharedClasses;

import java.io.Serializable;
import java.net.InetAddress;

public class GRDSClientMessageUDP implements Serializable {

    public static final long serialVersionID = 3;
    //InetAddress serverAddr = null;
    String serverIP;
    int serverPort;

}
