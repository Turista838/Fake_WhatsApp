package TCP;

import Data.ClientList;
import SharedClasses.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class ProcessClientMessagesTCP extends Thread {

    private final Socket socket;
    private ClientList clientList;

    public ProcessClientMessagesTCP(Socket socket, ClientList clientList){
        this.socket = socket;
        this.clientList = clientList;
    }

    public void run(){

        ObjectInputStream oin;
        ObjectOutputStream oout;

       try{
            oout = new ObjectOutputStream(socket.getOutputStream());
            oin = new ObjectInputStream(socket.getInputStream());

            while(true) {
                Object obj = oin.readObject();

                if (obj == null) { //EOF
                    return;
                }

                if (obj instanceof String) { //primeira mensagem
                    //TODO
                    System.out.println("Recebi String");
                }

                if (obj instanceof LoginMessageTCP) { //se é uma mensagem de login
                    //TODO
                    System.out.println("Recebi LoginMessageTCP");
                }

                if (obj instanceof DirectMessageTCP) { //se é uma mensagem individual
                    //TODO então mas
                    // servidor recebe uma mensagem do cliente
                    // pega na mensagem e cola na base de dados
                    // pega na mensagem e envia TCP ao outro cliente
                    // então mas assim tem de saber o IP e porto do outro cliente, onde o vai buscar?
                    // é que disse que não era preciso guardar os IPs dos clientes na BD
                    // e quando o cliente está conectado noutro servidor?
                    //System.out.println(((DirectMessageTCP) obj).getChatMessage()); //só para testar
                    //clientList.checkAddClient(((DirectMessageTCP) obj).getChatMessage(), "testar", 1);
                    //clientList.teste();
                    //oout.writeObject(new Time(calendar.get(GregorianCalendar.HOUR_OF_DAY), calendar.get(GregorianCalendar.MINUTE), calendar.get(GregorianCalendar.SECOND)));
                    //oout.flush();
                    System.out.println("Recebi DirectMessageTCP");
                }

                if (obj instanceof GroupMessageTCP) { //se é uma mensagem de grupo
                    System.out.println("Recebi GroupMessageTCP");
                    //TODO
                }
            }

        }catch(Exception e){
            System.out.println("Problema na comunicação com o cliente " +
                    socket.getInetAddress().getHostAddress() + ":" +
                    socket.getPort()+"\n\t" + e);
        }finally{
            try{
                socket.close();
            }catch(IOException e){}
        }
    }
}
