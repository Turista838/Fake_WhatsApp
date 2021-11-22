import SharedClasses.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class MainServer {

    public static final int MAX_SIZE = 10000;
    public static final int TIMEOUT = 10; //segundos
    public static final String MULTICAST_IP = "230.30.30.30";
    public static final int MULTICAST_PORT = 3030;

    public static void main(String[] args) {

        InetAddress gbdsAddr = null;
        String gbdsIP;
        String gbdsPort;

        ByteArrayOutputStream bout; //enviar
        ObjectOutputStream oout; //enviar

        ByteArrayInputStream bin; //receber
        ObjectInputStream oin; //receber

        DatagramSocket socket = null;
        DatagramPacket packet = null;

        MulticastSocket multicastSocket = null;

        GRDSServerMessageUDP grdsServerMessageUDP = new GRDSServerMessageUDP();

        if(args.length == 2){

            gbdsIP = args[0]; //IP GRDS
            gbdsPort = args[1]; //Porto GRDS

            try{ //connectar ao SGBD

                gbdsAddr = InetAddress.getByName(gbdsIP);
                socket = new DatagramSocket();
                //socket.setSoTimeout(TIMEOUT*1000);
                //encapsular a mensagem
                bout = new ByteArrayOutputStream();
                oout = new ObjectOutputStream(bout);
                oout.writeUnshared(grdsServerMessageUDP);
                //send
                packet = new DatagramPacket(bout.toByteArray(), bout.size(), gbdsAddr, Integer.parseInt(gbdsPort));
                socket.send(packet);
                //receive
                packet = new DatagramPacket(new byte[MAX_SIZE], MAX_SIZE);
                socket.receive(packet);
                //deserializar o fluxo de bytes recebido
                bin = new ByteArrayInputStream(packet.getData(), 0, packet.getLength());
                oin = new ObjectInputStream(bin);
                grdsServerMessageUDP = (GRDSServerMessageUDP)oin.readObject();

                //TODO escutar por ligações cliente TCP e lançar threads

            }catch(Exception e){ //TODO melhorar catches
                System.out.println("Problema:\n\t"+e);
            }finally{
                if(socket != null){
                    socket.close();
                }
            }
        }
        else{
            System.out.println("Arguments needed: <IP GRDS> <PORT GRDS>");
            System.out.println("Attempting Multicast communication...");

            boolean succesfullConnection = false;
            int attemps = 0;

            while(attemps < 3 || !succesfullConnection) {
                System.out.println("Attempt number " + (attemps + 1));
                attemps++;
                try {
                    gbdsAddr = InetAddress.getByName(MULTICAST_IP);
                    multicastSocket = new MulticastSocket(MULTICAST_PORT);

                    bout = new ByteArrayOutputStream();
                    oout = new ObjectOutputStream(bout);
                    oout.writeUnshared(grdsServerMessageUDP);
                    //send
                    packet = new DatagramPacket(bout.toByteArray(), bout.size(), gbdsAddr, MULTICAST_PORT);
                    multicastSocket.send(packet);
                    //receive
                    packet = new DatagramPacket(new byte[MAX_SIZE], MAX_SIZE);
                    multicastSocket.receive(packet);
                    //deserializar o fluxo de bytes recebido
                    bin = new ByteArrayInputStream(packet.getData(), 0, packet.getLength());
                    oin = new ObjectInputStream(bin);
                    grdsServerMessageUDP = (GRDSServerMessageUDP) oin.readObject();
                    succesfullConnection = true;

                    //TODO escutar por ligações cliente TCP e lançar threads

                } catch (Exception e) { //TODO melhorar catches
                    System.out.println("Problema:\n\t" + e);
                } finally {
                    if (multicastSocket != null) {
                        multicastSocket.close();
                    }
                }
            }
        }



    }
}
