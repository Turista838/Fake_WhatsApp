package UDP;

import SharedClasses.GRDSServerMessageUDP;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

public class UpdateGRDSMessagesUDP extends Thread {

    private String grdsIP;
    private  String grdsPort;
    private String message;
    private ArrayList<String> clientsAffectedBySGBDChanges;

    public UpdateGRDSMessagesUDP(String grdsIP, String grdsPort, String message, ArrayList clientsAffectedBySGBDChanges){
        this.message = message;
        this.clientsAffectedBySGBDChanges = clientsAffectedBySGBDChanges;
        this.grdsIP = grdsIP;
        this.grdsPort = grdsPort;
        this.run();
    }

    public void run(){

        GRDSServerMessageUDP grdsServerMessageUDP = new GRDSServerMessageUDP(true);
        grdsServerMessageUDP.setMessage(message);

        try{
            InetAddress gbdsAddr = InetAddress.getByName(grdsIP);
            DatagramSocket socketUDP = new DatagramSocket();

            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            ObjectOutputStream oout = new ObjectOutputStream(bout);
            oout.writeUnshared(grdsServerMessageUDP);

            DatagramPacket packetUDP = new DatagramPacket(bout.toByteArray(), bout.size(), gbdsAddr, Integer.parseInt(grdsPort));
            socketUDP.send(packetUDP);
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

}
