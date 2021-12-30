package Data;

import SharedClasses.*;
import java.io.*;
import java.net.*;

public class ClientStartup {

    public static final int MAX_SIZE = 10000;
    public static final int TIMEOUT = 10; //segundos

    private InetAddress gbdsAddr = null;
    private String gbdsIP;
    private int gbdsPort;

    private String serverIP;
    private int serverPort;

    private InetAddress serverAddr = null;
    private Socket serverSocket = null;

    private ByteArrayOutputStream bout; //enviar
    private ObjectOutputStream oout; //enviar

    private ByteArrayInputStream bin; //receber
    private ObjectInputStream oin; //receber

    private DatagramSocket socket = null;
    private DatagramPacket packet = null;


    public ClientStartup(String args0, String args1){
        gbdsIP = args0; //IP GRDS
        gbdsPort = Integer.parseInt(args1); //Porto GRDS
        connectGRDS();
    }


    public void connectGRDS(){

        GRDSClientMessageUDP grdsClientMessageUDP = new GRDSClientMessageUDP();

        try{ //connectar ao GRDS

            gbdsAddr = InetAddress.getByName(gbdsIP);

            socket = new DatagramSocket();
            //socket.setSoTimeout(TIMEOUT*1000);
            //encapsular a mensagem
            bout = new ByteArrayOutputStream();
            oout = new ObjectOutputStream(bout);
            oout.writeUnshared(grdsClientMessageUDP);
            //send
            packet = new DatagramPacket(bout.toByteArray(), bout.size(), gbdsAddr, gbdsPort);
            socket.send(packet);
            //receive
            packet = new DatagramPacket(new byte[MAX_SIZE], MAX_SIZE);
            socket.receive(packet);
            //deserializar o fluxo de bytes recebido
            bin = new ByteArrayInputStream(packet.getData(), 0, packet.getLength());
            oin = new ObjectInputStream(bin);
            grdsClientMessageUDP = (GRDSClientMessageUDP)oin.readObject();

            System.out.println("Recebi do GRDS, o seguinte servidor:");
            System.out.println("IP: " + grdsClientMessageUDP.getServerIP());
            System.out.println("Porto: " + grdsClientMessageUDP.getServerPort());

            serverIP = grdsClientMessageUDP.getServerIP();
            serverPort = grdsClientMessageUDP.getServerPort();

            connectServer(grdsClientMessageUDP.getServerIP(), grdsClientMessageUDP.getServerPort()); //connectServer


        }catch(Exception e){
            System.out.println("Problema:\n\t"+e);
        }finally{
            if(socket != null){
                socket.close();
            }
        }
    }

    public void connectServer(String serverIP, int serverPort) {

        try{

            serverAddr = InetAddress.getByName(serverIP);
            serverSocket = new Socket(serverAddr, serverPort); //criar socket TCP

            oout = new ObjectOutputStream(serverSocket.getOutputStream());
            oin = new ObjectInputStream(serverSocket.getInputStream());

            oout.writeObject("Client");
            oout.flush();

        }catch(UnknownHostException e){
            System.out.println("Destino desconhecido:\n\t"+e);
        }catch(NumberFormatException e){
            System.out.println("O porto do servidor deve ser um inteiro positivo.");
        }catch(SocketTimeoutException e){
            System.out.println("Nao foi recebida qualquer resposta:\n\t"+e);
        }catch(SocketException e){
            System.out.println("Ocorreu um erro ao nivel do mySocket TCP:\n\t"+e);
        }catch(IOException e){
            System.out.println("Ocorreu um erro no acesso ao mySocket:\n\t"+e);
        }
    }

    public Socket getServerSocket() {
        return serverSocket;
    }

    public ObjectOutputStream getOout() {
        return oout;
    }

    public ObjectInputStream getOin() {
        return oin;
    }

    public void closeServerSocket() {
        try {
            serverSocket.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}
