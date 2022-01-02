import Data.ServerList;
import Data.ServerTimeController;
import SharedClasses.*;
import UDP.*;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;

public class MainGRDS {

    private static final int MAX_SIZE = 10000;

    public static void main(String[] args) {

        ServerList serverList = new ServerList();
        String[] serverIpAndPort = new String[2];
        int listeningPort;

        DatagramSocket socket = null;
        DatagramPacket packet;

        ByteArrayInputStream bin;
        ObjectInputStream oin;

        if(args.length != 1){
            System.out.println("Arguments needed: <LISTENING PORT>");
            return;
        }

        System.out.println("\n### GRDS initiated ###\n");

        try{

            listeningPort = Integer.parseInt(args[0]);
            socket = new DatagramSocket(listeningPort);
            new ServerTimeController(serverList);

            while(true){

                try{
                    packet = new DatagramPacket(new byte[MAX_SIZE], MAX_SIZE);
                    socket.receive(packet);

                    bin = new ByteArrayInputStream(packet.getData(), 0, packet.getLength());
                    oin = new ObjectInputStream(bin);

                    Object obj = oin.readObject();

                    if(obj instanceof GRDSClientMessageUDP){
                        serverIpAndPort = serverList.returnAvailableServer();
                        ProcessClientMessagesUDP processClientMessages = new ProcessClientMessagesUDP(socket, packet, (GRDSClientMessageUDP) obj, serverIpAndPort);
                        processClientMessages.start();
                    }
                    if(obj instanceof GRDSServerMessageUDP){
                        if(((GRDSServerMessageUDP) obj).isUpdateBDconnection()){
                            new ProcessServerMessagesUDP(serverList, packet.getAddress().getHostAddress(), packet.getPort(), ((GRDSServerMessageUDP) obj).getClientsAffectedBySGBDChanges(), ((GRDSServerMessageUDP) obj).getMessage());
                        }
                        else {
                            if(((GRDSServerMessageUDP) obj).notifyServersToDownloadFiles()){
                                new ProcessServerMessagesUDP(((GRDSServerMessageUDP) obj).getFilesList(), serverList, packet.getAddress().getHostAddress(), packet.getPort());
                            }
                            else {
                                serverList.checkAddServer(packet.getAddress().getHostAddress(), packet.getPort()); //sincronizar
                                serverList.warnServersForFileSynchronization();
                            }
                        }
                    }
                    if(obj instanceof String){
                        String msgRecebida = (String)obj;
                        if (msgRecebida.compareTo("tcpPort")==0){
                            System.out.println("Recebido o ping do servidor IP: "+packet.getAddress().getHostAddress()+ " Porto: "+packet.getPort());
                            serverList.updateTimeServer(packet.getAddress().getHostAddress(), packet.getPort()); //sincronizar
                        }
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
