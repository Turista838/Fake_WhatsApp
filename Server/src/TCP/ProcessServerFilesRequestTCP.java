package TCP;

import Data.ClientList;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


public class ProcessServerFilesRequestTCP extends Thread { //envia ficheiros aos restantes servidores

    private final String filesFolderPath;
    private final Socket socket;
    private ObjectInputStream oin;
    private ObjectOutputStream oout;

    public ProcessServerFilesRequestTCP(String filesFolderPath, ObjectInputStream in, ObjectOutputStream out, Socket socket){
        this.filesFolderPath = filesFolderPath;
        oin = in;
        oout = out;
        this.socket = socket;
    }

    public void run(){

        System.out.println("entrei no Run do ProcessServerFilesRequestTCP");

    }

}
