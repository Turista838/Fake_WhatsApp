package UDP;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.*;


public class PingGRDSMessagesUDP extends Thread  {

    private static final int MAX_SIZE = 10000;
    private static final String PING="tcpPort";
    private  DatagramSocket socketUDP;
    private InetAddress gbdsAddr;
    private String grdsPort;

    public PingGRDSMessagesUDP(DatagramSocket socketUDP, InetAddress gbdsAddr, String grdsPort) {
        this.socketUDP = socketUDP;
        this.gbdsAddr = gbdsAddr;
        this.grdsPort = grdsPort;
    }

    public void run() {

        while (true){

            try {

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream out = new ObjectOutputStream(baos);
                out.writeObject(PING);

                byte[] bytesToSend = baos.toByteArray();

                DatagramPacket dp = new DatagramPacket(bytesToSend, bytesToSend.length, gbdsAddr, Integer.parseInt(grdsPort));
                socketUDP.send(dp);

                dp.setData(new byte[256]);
                dp.setLength(256);

                Thread.sleep(20000); //Sleep 20 segundos

            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        }
    }
}
