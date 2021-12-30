package TCP;

import Data.ClientList;
import SharedClasses.FileMessageTCP;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;


public class ProcessServerFilesRequestTCP extends Thread { //envia ficheiros aos restantes servidores

    private final String filesFolderPath;
    private final Socket socket;
    private ObjectInputStream oin;
    private ObjectOutputStream oout;
    private ArrayList<String> filesList;
    private int nBytes;

    public ProcessServerFilesRequestTCP(String filesFolderPath, ArrayList<String> filesList, ObjectInputStream in, ObjectOutputStream out, Socket socket){
        this.filesFolderPath = filesFolderPath;
        this.filesList = filesList;
        oin = in;
        oout = out;
        this.socket = socket;
    }

    public void run(){

        try{

            for(int i = 0; i < filesList.size(); i++){

                //enviar ao servidor o tamanho
                byte []fileChunk = new byte[4096];
                Path path = Paths.get(filesFolderPath + "\\" + filesList.get(i));
                FileMessageTCP fileMessageTCP = null;
                fileMessageTCP = new FileMessageTCP(Files.size(path), filesList.get(i));
                fileMessageTCP.setDownload(true);
                oout.writeObject(fileMessageTCP);
                oout.flush();

                //enviar ficheiro
                OutputStream fileOut = socket.getOutputStream();
                FileInputStream fileInputStream = new FileInputStream(filesFolderPath + "\\" + filesList.get(i));
                do {
                    nBytes = fileInputStream.read(fileChunk);
                    if (nBytes != -1) {// enquanto não é EOF
                        fileOut.write(fileChunk, 0, nBytes);
                        fileOut.flush();
                    }
                } while (nBytes > 0);
                fileInputStream.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }

    }

}
