import Data.ServerList;
import Data.ServerTimeController;
import RMI.ProcessRemoteMessagesRMI;
import SharedClasses.*;
import SharedInterfaces.MainRemoteInterface;
import UDP.*;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class MainGRDS {

    public static final String SERVICE_NAME = "GRDS_Service";
    private static final int MAX_SIZE = 10000;

    public static void main(String[] args) throws NoSuchObjectException {

        ProcessRemoteMessagesRMI processRemoteMessagesRMI = null;
        List<MainRemoteInterface> remoteObservers = new ArrayList<>();

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

            try{
                LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
            }catch(RemoteException e){
                System.out.println("Registry provavelmente jé em execução!");
            }

            processRemoteMessagesRMI = new ProcessRemoteMessagesRMI(serverList, remoteObservers);

            new ServerTimeController(serverList, processRemoteMessagesRMI);

            Naming.bind("rmi://localhost/" + SERVICE_NAME, processRemoteMessagesRMI);

            while(true){

                try{
                    packet = new DatagramPacket(new byte[MAX_SIZE], MAX_SIZE);
                    socket.receive(packet);

                    bin = new ByteArrayInputStream(packet.getData(), 0, packet.getLength());
                    oin = new ObjectInputStream(bin);

                    Object obj = oin.readObject();

                    if(obj instanceof GRDSClientMessageUDP){
                        serverIpAndPort = serverList.returnAvailableServer();
                        processRemoteMessagesRMI.clientConnected(packet.getAddress().getHostAddress(), packet.getPort());
                        ProcessClientMessagesUDP processClientMessages = new ProcessClientMessagesUDP(socket, packet, (GRDSClientMessageUDP) obj, serverIpAndPort);
                        processClientMessages.start();
                    }
                    if(obj instanceof GRDSServerMessageUDP){
                        if(((GRDSServerMessageUDP) obj).isUpdateBDconnection()){
                            new ProcessServerMessagesUDP(serverList, packet.getAddress().getHostAddress(), packet.getPort(), ((GRDSServerMessageUDP) obj).getClientsAffectedBySGBDChanges(), ((GRDSServerMessageUDP) obj).getMessage());
                            processRemoteMessagesRMI.serverNotifiedGRDS(packet.getAddress().getHostAddress(), packet.getPort());
                        }
                        else {
                            if(((GRDSServerMessageUDP) obj).notifyServersToDownloadFiles()){
                                new ProcessServerMessagesUDP(((GRDSServerMessageUDP) obj).getFilesList(), serverList, packet.getAddress().getHostAddress(), packet.getPort());
                                processRemoteMessagesRMI.serverNotifiedGRDS(packet.getAddress().getHostAddress(), packet.getPort());
                            }
                            else {
                                serverList.checkAddServer(packet.getAddress().getHostAddress(), packet.getPort(), processRemoteMessagesRMI);
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
        }catch(AlreadyBoundException e){
            System.out.println("Ocorreu um erro ao nivel remoto:\n\t"+e);
        } finally{
            if(socket!=null){
                socket.close();
            }
            UnicastRemoteObject.unexportObject(processRemoteMessagesRMI, true);
        }
    }
}
