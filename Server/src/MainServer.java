import Data.ClientList;
import SharedClasses.*;
import TCP.ProcessClientMessagesTCP;

import java.io.*;
import java.net.*;

public class MainServer {

    public static final int MAX_SIZE = 10000;
    public static final int TIMEOUT = 10; //segundos
    public static final String MULTICAST_IP = "230.30.30.30";
    public static final int MULTICAST_PORT = 3030;

    public static void main(String[] args) {

        ClientList clientList = new ClientList();
        InetAddress gbdsAddr = null;
        String gbdsIP;
        String gbdsPort;

        ByteArrayOutputStream bout; //enviar
        ObjectOutputStream oout; //enviar

        ByteArrayInputStream bin; //receber
        ObjectInputStream oin; //receber

        DatagramSocket socketUDP = null;
        DatagramPacket packetUDP = null;

        ServerSocket serverSocketTCP = null;
        Socket clientSocketTCP = null;

        MulticastSocket multicastSocket = null;

        GRDSServerMessageUDP grdsServerMessageUDP = new GRDSServerMessageUDP();

        if(args.length == 2){

            gbdsIP = args[0]; //IP GRDS
            gbdsPort = args[1]; //Porto GRDS

            try{ //connectar ao GRDS UDP

                gbdsAddr = InetAddress.getByName(gbdsIP);
                socketUDP = new DatagramSocket();
                //socket.setSoTimeout(TIMEOUT*1000);
                //encapsular a mensagem
                bout = new ByteArrayOutputStream();
                oout = new ObjectOutputStream(bout);
                oout.writeUnshared(grdsServerMessageUDP);
                //send
                packetUDP = new DatagramPacket(bout.toByteArray(), bout.size(), gbdsAddr, Integer.parseInt(gbdsPort));
                socketUDP.send(packetUDP);
                //receive
                packetUDP = new DatagramPacket(new byte[MAX_SIZE], MAX_SIZE);
                socketUDP.receive(packetUDP);
                //deserializar o fluxo de bytes recebido
                bin = new ByteArrayInputStream(packetUDP.getData(), 0, packetUDP.getLength());
                oin = new ObjectInputStream(bin);
                grdsServerMessageUDP = (GRDSServerMessageUDP)oin.readObject();
                //TODO acho que o servidor tem de estar sempre atento porque pode vir mais mensagens do GRDS (tipo para irem à BD)

                System.out.println("Sou servidor e recebi pelo UDP isto:");
                System.out.println(grdsServerMessageUDP.testMsg);

                try{ //tratar de clientes TCP

                    System.out.println(socketUDP.getLocalPort()); //TODO melhorar?

                    serverSocketTCP = new ServerSocket(socketUDP.getLocalPort());
                    System.out.println("cheguei aqui 1");
                    while(true){ //TODO remover este true

                        try{
                            clientSocketTCP = serverSocketTCP.accept();
                            //clientSocketTCP.setSoTimeout(TIMEOUT);
                            ProcessClientMessagesTCP processClientMessagesTCP = new ProcessClientMessagesTCP(clientSocketTCP, clientList);
                            processClientMessagesTCP.start();
                        }
                        catch(IOException e){
                            System.out.println("Erro enquanto aguarda por um pedido");
                            return;
                        }
                        System.out.println("cheguei aqui 2");
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
