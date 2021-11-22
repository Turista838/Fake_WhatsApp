package UDP;

import java.net.DatagramSocket;

public class ProcessClientMessagesUDP extends Thread {

    private DatagramSocket s;

    public ProcessClientMessagesUDP(DatagramSocket s){
        this.s = s;
    }

    public void run(){



    }
}
