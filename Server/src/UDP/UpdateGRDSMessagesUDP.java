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
    private String grdsPort;
    private String message;
    private ArrayList<String> clientsAffectedBySGBDChanges;
    private ArrayList<String> storedFilesList;

    public UpdateGRDSMessagesUDP(DatagramSocket socketUDP, InetAddress grdsIP, String grdsPort, String message, ArrayList clientsAffectedBySGBDChanges){
        this.message = message;
        this.clientsAffectedBySGBDChanges = clientsAffectedBySGBDChanges;
        this.socketUDP = socketUDP;
        this.grdsIP = grdsIP;
        this.grdsPort = grdsPort;
        this.runUpdateClients();
    }

    public UpdateGRDSMessagesUDP(ArrayList storedFilesList, DatagramSocket socketUDP, InetAddress grdsIP, String grdsPort) {
        this.storedFilesList = storedFilesList;
        this.socketUDP = socketUDP;
        this.grdsIP = grdsIP;
        this.grdsPort = grdsPort;
        this.runRequestFiles();
    }

    public void runUpdateClients(){ //envia ao GRDS

        GRDSServerMessageUDP grdsServerMessageUDP = new GRDSServerMessageUDP(true, false, false);
        grdsServerMessageUDP.setMessage(message);

        try{
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

    public void runRequestFiles(){ //envia ao GRDS


        GRDSServerMessageUDP grdsServerMessageUDP = new GRDSServerMessageUDP(false, true, false);
        grdsServerMessageUDP.setFilesList(storedFilesList);

        try{
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
