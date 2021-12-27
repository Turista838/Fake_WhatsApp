import Data.ClientList;
import SharedClasses.*;
import TCP.ProcessClientMessagesTCP;
import UDP.ProcessGRDSMessagesUDP;

import java.io.*;
import java.net.*;
import java.sql.*;

public class MainServer {

    public static final int MAX_SIZE = 10000;
    public static final int TIMEOUT = 10; //segundos
    public static final String MULTICAST_IP = "230.30.30.30";
    public static final int MULTICAST_PORT = 3030;
    public static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    public static final String dbUrl = "jdbc:mysql://"+"127.0.0.1"+"/"+"pd_tp_final";

    public static void main(String[] args) {

        ClientList clientList = new ClientList();
        InetAddress gbdsAddr = null;
        String grdsIP;
        String grdsPort;

        ByteArrayOutputStream bout; //enviar
        ObjectOutputStream oout; //enviar

        ByteArrayInputStream bin; //receber
        ObjectInputStream oin; //receber

        DatagramSocket socketUDP = null;
        DatagramPacket packetUDP = null;

        ServerSocket serverSocketTCP = null;
        Socket clientSocketTCP = null;

        MulticastSocket multicastSocket = null;

        GRDSServerMessageUDP grdsServerMessageUDP = new GRDSServerMessageUDP(false);

        Connection conn = null;

        try {
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException ex) {
            System.out.println(ex);
        }

        try{
            conn = DriverManager.getConnection(dbUrl, "root", "123456");
        }
        catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        if(args.length == 2){

            grdsIP = args[0]; //IP GRDS
            grdsPort = args[1]; //Porto GRDS

            try{ //connectar ao GRDS UDP

                gbdsAddr = InetAddress.getByName(grdsIP);
                socketUDP = new DatagramSocket();
                //socket.setSoTimeout(TIMEOUT*1000);
                //encapsular a mensagem
                bout = new ByteArrayOutputStream();
                oout = new ObjectOutputStream(bout);
                oout.writeUnshared(grdsServerMessageUDP);
                //send
                packetUDP = new DatagramPacket(bout.toByteArray(), bout.size(), gbdsAddr, Integer.parseInt(grdsPort));
                socketUDP.send(packetUDP);

                ProcessGRDSMessagesUDP processGRDSMessagesUDP = new ProcessGRDSMessagesUDP(packetUDP, socketUDP, clientList);
                processGRDSMessagesUDP.start();

                try{ //tratar de clientes TCP

                    serverSocketTCP = new ServerSocket(socketUDP.getLocalPort());

                    while(true){ //TODO remover este true

                        try{
                            clientSocketTCP = serverSocketTCP.accept();
                            //clientSocketTCP.setSoTimeout(TIMEOUT);
                            ProcessClientMessagesTCP processClientMessagesTCP = new ProcessClientMessagesTCP(clientSocketTCP, clientList, conn, grdsIP, grdsPort);
                            processClientMessagesTCP.start();
                        }
                        catch(IOException e){
                            System.out.println("Erro enquanto aguarda por um pedido");
                            return;
                        }
                    }

                }catch(NumberFormatException e){
                    System.out.println("O porto de escuta deve ser um inteiro positivo.");
                }catch(IOException e){
                    System.out.println("Ocorreu um erro ao nivel do serverSocket de escuta:\n\t"+e);
                }finally{
                    if(serverSocketTCP!=null){
                        try {
                            serverSocketTCP.close();
                        } catch (IOException ex) {}
                    }
                }

            }catch(Exception e){ //TODO melhorar catches
                System.out.println("Problema:\n\t"+e);
            }finally{
                if(socketUDP != null){
                    socketUDP.close();
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
                    packetUDP = new DatagramPacket(bout.toByteArray(), bout.size(), gbdsAddr, MULTICAST_PORT);
                    multicastSocket.send(packetUDP);
                    //receive
                    packetUDP = new DatagramPacket(new byte[MAX_SIZE], MAX_SIZE);
                    multicastSocket.receive(packetUDP);
                    //deserializar o fluxo de bytes recebido
                    bin = new ByteArrayInputStream(packetUDP.getData(), 0, packetUDP.getLength());
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
