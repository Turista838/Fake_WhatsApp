import Data.ServerList;
import SharedClasses.*;
import UDP.*;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

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

            while(true){ //TODO tirar o true

                try{
                    packet = new DatagramPacket(new byte[MAX_SIZE], MAX_SIZE);
                    socket.receive(packet);

                    bin = new ByteArrayInputStream(packet.getData(), 0, packet.getLength());
                    oin = new ObjectInputStream(bin);

                    Object obj = oin.readObject();

                    if(obj instanceof GRDSClientMessageUDP){ //cliente só cai aqui na 1ª vez e quando perde ligação a 1 servidor
                        serverIpAndPort = serverList.returnAvailableServer();
                        ProcessClientMessagesUDP processClientMessages = new ProcessClientMessagesUDP(socket, packet, (GRDSClientMessageUDP) obj, serverIpAndPort);
                        processClientMessages.start();
                    }
                    if(obj instanceof GRDSServerMessageUDP){
                        if(((GRDSServerMessageUDP) obj).isUpdateBDconnection()){
                            new ProcessServerMessagesUDP(serverList, packet.getAddress().getHostAddress(), packet.getPort(), ((GRDSServerMessageUDP) obj).getClientsAffectedBySGBDChanges(), ((GRDSServerMessageUDP) obj).getMessage());
                            //processServerMessages.start();
                        }
                        else {
                            if(((GRDSServerMessageUDP) obj).notifyServersToDownloadFiles()){
                                System.out.println("recebi notifyServersToDownloadFiles");
                                new ProcessServerMessagesUDP(((GRDSServerMessageUDP) obj).getFilesList(), serverList, packet.getAddress().getHostAddress(), packet.getPort());
                                //processServerMessages.start();
                            }
                            else {
                                System.out.println("Porto do servidor: " + packet.getPort());
                                if (!serverList.checkAddServer(packet.getAddress().getHostAddress(), packet.getPort()))
                                    serverList.updateTimeServer(packet.getAddress().getHostAddress(), packet.getPort());
                            }
                        }
                    }
                    if(obj instanceof String){
                        String msgRecebida = (String)obj;
                        if (msgRecebida.compareTo("tcpPort")==0){
                            System.out.println("Recebido o ping do servidor Porto:"+packet.getPort()+" IP:"+packet.getAddress().getHostAddress()+
                            " Porto de escuta TCP:"+packet.getPort());
                            //TODO update ao time server
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
