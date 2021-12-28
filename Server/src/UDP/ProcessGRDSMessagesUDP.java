package UDP;

import Data.ClientInfo;
import Data.ClientList;
import SharedClasses.GRDSServerMessageUDP;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

public class ProcessGRDSMessagesUDP extends Thread {

    public static final int MAX_SIZE = 10000;
    private DatagramPacket packetUDP;
    private DatagramSocket socketUDP;
    private ClientList clientList;

    public ProcessGRDSMessagesUDP(DatagramPacket packetUDP, DatagramSocket socketUDP, ClientList clientList) {
        this.packetUDP = packetUDP;
        this.socketUDP = socketUDP;
        this.clientList = clientList;
    }

    public void run(){

        while(true){

            try{
                packetUDP = new DatagramPacket(new byte[MAX_SIZE], MAX_SIZE);
                socketUDP.receive(packetUDP);
                ByteArrayInputStream bin = new ByteArrayInputStream(packetUDP.getData(), 0, packetUDP.getLength());
                ObjectInputStream oin = new ObjectInputStream(bin);
                System.out.println("recebi mensagem e processei na thread ProcessGRDSMessagesUDP");
                GRDSServerMessageUDP grdsServerMessageUDP = (GRDSServerMessageUDP)oin.readObject();

                if(grdsServerMessageUDP.isUpdateBDconnection()){
                    System.out.println("vou enviar aos clientes");
                    ArrayList<ClientInfo> cList = clientList.getArrayClientList();
                    for(ClientInfo c : cList){
                        ObjectOutputStream ooutDest = c.getOout();
                        try {
                            String m = grdsServerMessageUDP.getMessage();
                            System.out.println("m = " + m);
                            ooutDest.writeObject(m);
                            ooutDest.flush();
                            System.out.println("enviei ao cliente " + c.getUsername());
                        } catch (Exception IOException) {
                            IOException.printStackTrace();
                        }
                    }
                }

                if(grdsServerMessageUDP.notifyServersToDownloadFiles()){

                    InetAddress serverAddr = InetAddress.getByName(grdsServerMessageUDP.getFileServerIp());
                    System.out.println("IP: " + grdsServerMessageUDP.getFileServerIp());
                    System.out.println("Porto: " + grdsServerMessageUDP.getFileServerPort());
                    Socket socket = new Socket(serverAddr, grdsServerMessageUDP.getFileServerPort()); //criar socket TCP

                    ObjectOutputStream fileOut = new ObjectOutputStream(socket.getOutputStream());
                    ObjectInputStream fileOin = new ObjectInputStream(socket.getInputStream());

                    fileOut.writeObject("Server");
                    fileOut.flush();
                }


            }
            catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }


        }
    }

}
