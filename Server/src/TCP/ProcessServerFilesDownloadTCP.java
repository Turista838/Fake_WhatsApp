package TCP;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class ProcessServerFilesDownloadTCP extends Thread {

    private final Socket socket;
    private ArrayList<String> filesList;


    public ProcessServerFilesDownloadTCP(ArrayList filesList, Socket socket) {
        this.filesList = filesList;
        this.socket = socket;
        run();
    }

    public void run(){ //TODO

        try{
            ObjectOutputStream fileOut = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream fileOin = new ObjectInputStream(socket.getInputStream());

            fileOut.writeObject("Server");
            fileOut.flush();

            System.out.println("Lista de ficheiros: ");
            ArrayList<String> teste = filesList;
            for(int i = 0; i < teste.size(); i++)
                System.out.println("Ficheiro: " + teste.get(i));


        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
