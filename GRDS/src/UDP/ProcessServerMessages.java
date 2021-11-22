package UDP;

import Data.ServerList;

import java.net.DatagramSocket;
import java.net.Socket;

public class ProcessServerMessages extends Thread {

    private DatagramSocket s;

    public ProcessServerMessages(DatagramSocket s){
        this.s = s;
    }

    public void run(){



    }

}
