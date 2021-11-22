import Data.ServerList;
import SharedClasses.*;
import UDP.*;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;

public class MainGRDS {

    private ProcessClientMessages processClientMessages;
    private ProcessServerMessages processServerMessages;
    private ServerList serverList;
    private static final int MAX_SIZE = 10000;

    public static void main(String[] args) {

        int listeningPort;
        DatagramSocket socket = null;
        DatagramPacket packet;

        ByteArrayInputStream bin;
        ObjectInputStream oin;

        ByteArrayOutputStream bout;
        ObjectOutputStream oout;

        if(args.length != 1){
            System.out.println("Arguments needed: <LISTENING PORT>");
            return;
        }

        System.out.println("\n### GRDS initiated ###\n");

        try{

            listeningPort = Integer.parseInt(args[0]);
            socket = new DatagramSocket(listeningPort);

            while(true){

                try{
                    packet = new DatagramPacket(new byte[MAX_SIZE], MAX_SIZE);
                    socket.receive(packet);

                    bin = new ByteArrayInputStream(packet.getData(), 0, packet.getLength());
                    oin = new ObjectInputStream(bin);

                    Object obj = oin.readObject();

                    if(obj instanceof GRDSClientMessageUDP){
                        //ProcessClientMessages processClientMessages = new ProcessClientMessages(socket);
                        System.out.println("entrei aqui ProcessClientMessages"); //TODO apagar
                        //processClientMessages.start();
                    }
                    if(obj instanceof GRDSServerMessageUDP){
                            //TODO add to server list
                            System.out.println("entrei aqui ProcessServerMessages"); //TODO apagar
                            //ProcessServerMessages processServerMessages = new ProcessServerMessages(socket);
                            //processServerMessages.start();
                    }
                }
                catch(IOException e){
                    System.out.println("Erro enquanto aguarda por um pedido");
                    return;
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }catch(NumberFormatException e){
            System.out.println("O porto de escuta deve ser um inteiro positivo.");
        }catch(IOException e){
            System.out.println("Ocorreu um erro ao nivel do socket de escuta:\n\t"+e);
        }finally{
            if(socket!=null){
                socket.close();
            }
        }

    }
}
