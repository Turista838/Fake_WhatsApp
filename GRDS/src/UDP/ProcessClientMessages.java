package UDP;

import java.net.DatagramSocket;

public class ProcessClientMessages extends Thread {

    private DatagramSocket s;

    public ProcessClientMessages(DatagramSocket s){
        this.s = s;
    }

    public void run(){



    }
}
