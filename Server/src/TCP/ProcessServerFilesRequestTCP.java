package TCP;

import Data.ClientList;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


public class ProcessServerFilesRequestTCP extends Thread {

    private final Socket socket;
    private ObjectInputStream oin;
    private ObjectOutputStream oout;

    public ProcessServerFilesRequestTCP(ObjectInputStream in, ObjectOutputStream out, Socket socket){
        oin = in;
        oout = out;
        this.socket = socket;
    }

    public void run(){

        System.out.println("entrei no Run do ProcessServerFilesRequestTCP");

    }

}
