package UDP;

import Data.ServerInfo;
import Data.ServerList;
import SharedClasses.GRDSServerMessageUDP;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

public class ProcessServerMessagesUDP extends Thread {

    private static final int MAX_SIZE = 10000;
    private ServerList serverList;
    private ArrayList<String> clientsAffectedBySGBDChanges;
    private  String message;


    public ProcessServerMessagesUDP(ServerList serverList, ArrayList<String> clientsAffectedBySGBDChanges, String message) {
        this.serverList = serverList;
        this.clientsAffectedBySGBDChanges = clientsAffectedBySGBDChanges;
        this.message = message;
    }

    public void run(){

        try {
            System.out.println("Servidor enviou uma mensagem para transmitir a todos os servidores");

            for(ServerInfo serverInfo : serverList.arrayServerList){

                InetAddress serverAddr = null;
                String serverIP = serverInfo.getServerIP();
                int serverPort = serverInfo.getServerPort();

                serverAddr = InetAddress.getByName(serverIP);
                DatagramSocket socketUDP = new DatagramSocket();

                ByteArrayOutputStream bout = new ByteArrayOutputStream();
                ObjectOutputStream oout = new ObjectOutputStream(bout);

                GRDSServerMessageUDP grdsServerMessageUDP = new GRDSServerMessageUDP(true);
                grdsServerMessageUDP.setClientsAffectedBySGBDChanges(clientsAffectedBySGBDChanges);
                grdsServerMessageUDP.setMessage(message);
                oout.writeUnshared(grdsServerMessageUDP);

                //send
                DatagramPacket packetUDP = new DatagramPacket(bout.toByteArray(), bout.size(), serverAddr, serverPort);
                socketUDP.send(packetUDP);
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
