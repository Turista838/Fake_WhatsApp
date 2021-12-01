package UDP;

import Data.ServerList;
import SharedClasses.GRDSServerMessageUDP;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;

public class ProcessServerMessagesUDP extends Thread {

    private static final int MAX_SIZE = 10000;
    private DatagramSocket socket;
    private DatagramPacket packet;
    private GRDSServerMessageUDP serverMessageUDP;


    public ProcessServerMessagesUDP(DatagramSocket socket, DatagramPacket packet, GRDSServerMessageUDP serverMessageUDP){
        this.socket = socket;
        this.packet = packet;
        this.serverMessageUDP = serverMessageUDP;
    }

    public void run(){

        serverMessageUDP.testMsg = "O GDRS enviou esta classe";

        try {

            ByteArrayOutputStream bout  = new ByteArrayOutputStream();
            ObjectOutputStream oout = new ObjectOutputStream(bout);

            oout.writeObject(serverMessageUDP);
            oout.flush(); //opcional

            packet.setData(bout.toByteArray());
            packet.setLength(bout.size());

            socket.send(packet);


        } catch (IOException e) { //TODO melhorar try catch
            e.printStackTrace();
        }





    }

}
