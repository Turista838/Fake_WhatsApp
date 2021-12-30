package UDP;

import SharedClasses.GRDSClientMessageUDP;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class ProcessClientMessagesUDP extends Thread {

    private DatagramSocket socket;
    private DatagramPacket packet;
    private GRDSClientMessageUDP clientMessageUDP;
    private String[] serverIpAndPort;


    public ProcessClientMessagesUDP(DatagramSocket socket, DatagramPacket packet, GRDSClientMessageUDP clientMessageUDP, String[] serverIpAndPort){
        this.socket = socket;
        this.packet = packet;
        this.clientMessageUDP = clientMessageUDP;
        this.serverIpAndPort = serverIpAndPort;
    }

    public void run(){

        clientMessageUDP.buildMessage(serverIpAndPort[0], serverIpAndPort[1]);

        try {

            ByteArrayOutputStream bout  = new ByteArrayOutputStream();
            ObjectOutputStream oout = new ObjectOutputStream(bout);

            oout.writeObject(clientMessageUDP);
            oout.flush(); //opcional

            packet.setData(bout.toByteArray());
            packet.setLength(bout.size());

            socket.send(packet);


        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
