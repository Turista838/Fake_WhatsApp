package UDP;

import Data.ServerList;

import java.net.DatagramSocket;
import java.net.Socket;

public class ProcessServerMessagesUDP extends Thread {

    private DatagramSocket s;

    public ProcessServerMessagesUDP(DatagramSocket s){
        this.s = s;
    }

    public void run(){



    }

}
