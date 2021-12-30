package TCP;

import SharedClasses.FileMessageTCP;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ProcessServerFilesDownloadTCP extends Thread {

    private final Socket socket;
    private ArrayList<String> filesList;
    private String filesFolderPath;
    byte []buffer = new byte[4096];
    int nBytes;

    public ProcessServerFilesDownloadTCP(String filesFolderPath, ArrayList filesList, Socket socket) {
        this.filesList = filesList;
        this.socket = socket;
        this.filesFolderPath = filesFolderPath;
        run();
    }

    public void run(){


        try{

            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            out.writeObject("Server");
            out.flush();

            for(int i = 0; i < filesList.size(); i++){

                Object obj = in.readObject();

                if (obj == null) { //EOF
                    return;
                }

                if (obj instanceof FileMessageTCP){

                    long fileS = ((FileMessageTCP) obj).getFileSize();
                    int cont = 0;
                    InputStream fileIn = socket.getInputStream();
                    FileOutputStream localFileOutputStream = new FileOutputStream(filesFolderPath + "\\" + ((FileMessageTCP) obj).getFilename());

                    do {
                        nBytes = fileIn.read(buffer);
                        cont = cont + nBytes;
                        if (nBytes > 0) {
                            localFileOutputStream.write(buffer, 0, nBytes);
                        }
                    } while (cont != fileS);

                    localFileOutputStream.close();
                }

            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }


    }
}
