package UDP;

import SharedClasses.GRDSServerMessageUDP;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

public class UpdateGRDSMessagesUDP extends Thread {

    private DatagramSocket socketUDP;
    private InetAddress grdsIP;
    private  String grdsPort;
    private String message;
    private ArrayList<String> clientsAffectedBySGBDChanges;

    public UpdateGRDSMessagesUDP(DatagramSocket socketUDP, InetAddress grdsIP, String grdsPort, String message, ArrayList clientsAffectedBySGBDChanges){
        this.message = message;
        this.clientsAffectedBySGBDChanges = clientsAffectedBySGBDChanges;
        this.socketUDP = socketUDP;
        this.grdsIP = grdsIP;
        this.grdsPort = grdsPort;
        this.runUpdateClients();
    }

    public UpdateGRDSMessagesUDP(DatagramSocket socketUDP, InetAddress grdsIP, String grdsPort) {
        System.out.println("ENTREI NO CONSTRUTOR");
        this.socketUDP = socketUDP;
        this.grdsIP = grdsIP;
        this.grdsPort = grdsPort;
        this.runRequestFiles();
    }

    public void runUpdateClients(){

        GRDSServerMessageUDP grdsServerMessageUDP = new GRDSServerMessageUDP(true, false);
        grdsServerMessageUDP.setMessage(message);

        try{
            //InetAddress gbdsAddr = InetAddress.getByName(grdsIP);
            //DatagramSocket socketUDP = new DatagramSocket();

            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            ObjectOutputStream oout = new ObjectOutputStream(bout);
            oout.writeUnshared(grdsServerMessageUDP);

            DatagramPacket packetUDP = new DatagramPacket(bout.toByteArray(), bout.size(), grdsIP, Integer.parseInt(grdsPort));
            socketUDP.send(packetUDP);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void runRequestFiles(){

        System.out.println("vou correr runRequestFiles()");

        GRDSServerMessageUDP grdsServerMessageUDP = new GRDSServerMessageUDP(false, true);

        try{
            //InetAddress gbdsAddr = InetAddress.getByName(grdsIP);
            //DatagramSocket socketUDP = new DatagramSocket();

            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            ObjectOutputStream oout = new ObjectOutputStream(bout);
            oout.writeUnshared(grdsServerMessageUDP);

            DatagramPacket packetUDP = new DatagramPacket(bout.toByteArray(), bout.size(), grdsIP, Integer.parseInt(grdsPort));
            socketUDP.send(packetUDP);
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

}
