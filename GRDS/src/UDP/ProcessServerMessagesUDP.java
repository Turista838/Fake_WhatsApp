package UDP;

import Data.ServerInfo;
import Data.ServerList;
import SharedClasses.GRDSServerMessageUDP;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

public class ProcessServerMessagesUDP extends Thread {

    private static final int MAX_SIZE = 10000;
    private ServerList serverList;
    private ArrayList<String> clientsAffectedBySGBDChanges;
    private ArrayList<String> filesList;
    private String message;
    private String serverIp;
    private int serverPort;


    public ProcessServerMessagesUDP(ServerList serverList, String serverIp, int serverPort, ArrayList clientsAffectedBySGBDChanges, String message) {
        this.serverList = serverList;
        this.serverIp = serverIp;
        this.serverPort = serverPort;
        this.clientsAffectedBySGBDChanges = clientsAffectedBySGBDChanges;
        this.message = message;
        sendServersUpdateClients();
    }

    public ProcessServerMessagesUDP(ArrayList filesList, ServerList serverList, String serverIp, int serverPort) {
        this.filesList = filesList;
        this.serverList = serverList;
        this.serverIp = serverIp;
        this.serverPort = serverPort;
        sendServersUpdateFiles();
    }

    public void sendServersUpdateClients(){

        try {
            System.out.println("Servidor enviou uma mensagem para transmitir a todos os servidores para estes avisarem os clientes para fazerem update");

            for(ServerInfo serverInfo : serverList.arrayServerList){

                if(serverInfo.getServerIP() != serverIp && serverInfo.getServerPort() != serverPort) {
                    InetAddress serverAddr = null;
                    String serverIP = serverInfo.getServerIP();
                    int serverPort = serverInfo.getServerPort();

                    serverAddr = InetAddress.getByName(serverIP);
                    DatagramSocket socketUDP = new DatagramSocket();

                    ByteArrayOutputStream bout = new ByteArrayOutputStream();
                    ObjectOutputStream oout = new ObjectOutputStream(bout);

                    GRDSServerMessageUDP grdsServerMessageUDP = new GRDSServerMessageUDP(true, false);
                    grdsServerMessageUDP.setClientsAffectedBySGBDChanges(clientsAffectedBySGBDChanges);
                    grdsServerMessageUDP.setMessage(message);
                    oout.writeUnshared(grdsServerMessageUDP);

                    //send
                    DatagramPacket packetUDP = new DatagramPacket(bout.toByteArray(), bout.size(), serverAddr, serverPort);
                    socketUDP.send(packetUDP);
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendServersUpdateFiles(){

        try {
            System.out.println("Servidor enviou uma mensagem para transmitir a todos os servidores para estes fazerem update aos ficheiros");

            for(ServerInfo serverInfo : serverList.arrayServerList){

                if(serverInfo.getServerIP() != serverIp && serverInfo.getServerPort() != serverPort) {
                    InetAddress serverAddr = null;
                    String serverIP = serverInfo.getServerIP();
                    int serverPort = serverInfo.getServerPort();

                    serverAddr = InetAddress.getByName(serverIP);
                    DatagramSocket socketUDP = new DatagramSocket();

                    ByteArrayOutputStream bout = new ByteArrayOutputStream();
                    ObjectOutputStream oout = new ObjectOutputStream(bout);

                    GRDSServerMessageUDP grdsServerMessageUDP = new GRDSServerMessageUDP(false, true);
                    grdsServerMessageUDP.setFilesList(filesList);
                    grdsServerMessageUDP.setServerTCPData(serverIp, this.serverPort);
                    oout.writeUnshared(grdsServerMessageUDP);

                    //send
                    DatagramPacket packetUDP = new DatagramPacket(bout.toByteArray(), bout.size(), serverAddr, serverPort);
                    socketUDP.send(packetUDP);
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
