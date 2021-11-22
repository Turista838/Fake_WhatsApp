package Model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Client {

    public static final int MAX_SIZE = 10000;
    public static final String REQUEST_SV_ADRESS = "TIME";
    public static final int TIMEOUT = 10; //segundos

    InetAddress gbdsAddr = null;
    String gbdsIP;
    int gbdsPort;

    ByteArrayOutputStream bout; //enviar
    ObjectOutputStream oout; //enviar

    ByteArrayInputStream bin; //receber
    ObjectInputStream oin; //receber

    DatagramSocket socket = null;
    DatagramPacket packet = null;

    //DirectMessageTCP a = new DirectMe

    public Client(String args0, String args1){
        gbdsIP = args0; //IP GRDS
        gbdsPort = Integer.parseInt(args1); //Porto GRDS

    }

    public void connectSGBD(){


        try{ //connectar ao SGBD

            gbdsAddr = InetAddress.getByName(gbdsIP);
            //serverPort = Integer.parseInt(args1);

            socket = new DatagramSocket();
            //socket.setSoTimeout(TIMEOUT*1000);

            //Serializar a String TIME para um array de bytes encapsulado por bout
            bout = new ByteArrayOutputStream();
            oout = new ObjectOutputStream(bout);
            oout.writeUnshared(REQUEST_SV_ADRESS); //se fosse um objecto calendario (ou outro qq) seria oout.writeUnshared(canlendar);

            //Construir um datagrama UDP com o resultado da serialização
            packet = new DatagramPacket(bout.toByteArray(), bout.size(), gbdsAddr, gbdsPort);
            socket.send(packet);

            packet = new DatagramPacket(new byte[MAX_SIZE], MAX_SIZE);
            socket.receive(packet);

            //Deserializar o fluxo de bytes recebido para um array de bytes encapsulado por bin
            bin = new ByteArrayInputStream(packet.getData(), 0, packet.getLength());
            oin = new ObjectInputStream(bin);
            //response = (Calendar)oin.readObject(); //TODO strings ou objectos?

            //System.out.println("Hora indicada pelo servidor: " + response.getTime());

        }catch(Exception e){
            System.out.println("Problema:\n\t"+e);
        }finally{
            if(socket != null){
                socket.close();
            }
        }
    }

}
