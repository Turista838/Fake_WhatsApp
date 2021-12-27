package UDP;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Arrays;

public class PingGRDSMessagesUDP  {

    private static final int MAX_SIZE = 10000;
    private static final String PING="tcpPort";
    private int grdsPort;
    private String grdsIp;

    public PingGRDSMessagesUDP(int grdsPort, String grdsIp) {
        this.grdsPort = grdsPort;
        this.grdsIp = grdsIp;
    }

    public void run() throws IOException {

        while (true){

            DatagramSocket ds = new DatagramSocket();
            ds.setSoTimeout(1000);

            ByteArrayOutputStream baos= new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(baos);
            out.writeObject(PING);
            //out.flush(); caso n apreça a mensagem toda, despeija o buff
            byte[] bytesToSend= baos.toByteArray();

            InetAddress ip =InetAddress.getByName(grdsIp);

            DatagramPacket dp= new DatagramPacket(bytesToSend,bytesToSend.length,ip,grdsPort);
            ds.send(dp);
            System.out.println("Send to " + ip.getHostAddress() + ":" + grdsPort + " - " + PING);

            dp.setData(new byte[256]);
            dp.setLength(256);


            // a thread vai fazer um sleep de 20 segundos
            try {
                Thread.sleep(20000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String toString() {
        return "PingGRDSMessagesUDP{" +
                "grdsPort=" + grdsPort +
                ", grdsIp='" + grdsIp + '\'' +
                '}';
    }
}
