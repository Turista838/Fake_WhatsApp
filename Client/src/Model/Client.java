package Model;
import SharedClasses.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Client {

    public static final int MAX_SIZE = 10000;
    public static final int TIMEOUT = 10; //segundos

    InetAddress gbdsAddr = null;
    String gbdsIP;
    String gbdsPort;

    ByteArrayOutputStream bout; //enviar
    ObjectOutputStream oout; //enviar

    ByteArrayInputStream bin; //receber
    ObjectInputStream oin; //receber

    DatagramSocket socket = null;
    DatagramPacket packet = null;



    public Client(String args0, String args1){
        gbdsIP = args0; //IP GRDS
        gbdsPort = args1; //Porto GRDS
    }


    public void connectSGBD(){

        GRDSClientMessageUDP grdsClientMessageUDP = new GRDSClientMessageUDP();

        try{ //connectar ao SGBD

            gbdsAddr = InetAddress.getByName(gbdsIP);

            socket = new DatagramSocket();
            //socket.setSoTimeout(TIMEOUT*1000);
            //encapsular a mensagem
            bout = new ByteArrayOutputStream();
            oout = new ObjectOutputStream(bout);
            oout.writeUnshared(grdsClientMessageUDP);
            //send
            packet = new DatagramPacket(bout.toByteArray(), bout.size(), gbdsAddr, Integer.parseInt(gbdsPort));
            socket.send(packet);
            //receive
            packet = new DatagramPacket(new byte[MAX_SIZE], MAX_SIZE);
            socket.receive(packet);
            //deserializar o fluxo de bytes recebido
            bin = new ByteArrayInputStream(packet.getData(), 0, packet.getLength());
            oin = new ObjectInputStream(bin);
            grdsClientMessageUDP = (GRDSClientMessageUDP)oin.readObject();


        }catch(Exception e){
            System.out.println("Problema:\n\t"+e);
        }finally{
            if(socket != null){
                socket.close();
            }
        }
    }
}
