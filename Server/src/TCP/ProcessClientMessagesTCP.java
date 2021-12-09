package TCP;

import Data.ClientList;
import SharedClasses.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class ProcessClientMessagesTCP extends Thread {

    private final Socket socket;
    private ClientList clientList;
    private Statement stmt;
    private ResultSet rs;

    public ProcessClientMessagesTCP(Socket socket, ClientList clientList, Statement stmt){
        this.socket = socket;
        this.clientList = clientList;
        this.stmt = stmt;
        rs = null;
    }

    public void run(){

        ObjectInputStream oin;
        ObjectOutputStream oout;

       try{
            oout = new ObjectOutputStream(socket.getOutputStream());
            oin = new ObjectInputStream(socket.getInputStream());

            while(true) { //TODO alterar este true
                Object obj = oin.readObject();

                if (obj == null) { //EOF
                    return;
                }

                if (obj instanceof RegisterMessageTCP) { //Processa Registo
                    System.out.println("cheguei RegisterMessageTCP. Recebi: ");
                    System.out.println("Username: " + ((LoginMessageTCP) obj).getUsername());
                    rs = stmt.executeQuery("SELECT EXISTS(SELECT * from utilizador WHERE Username = \"" + ((RegisterMessageTCP) obj).getUsername() + "\");");
                    rs.next();
                    if(!rs.getBoolean(1)){ //se não encontrou, pode registar
                        stmt.executeUpdate("INSERT INTO utilizador VALUES (\"" + ((RegisterMessageTCP) obj).getUsername() + "\", \"" + ((RegisterMessageTCP) obj).getNome() + "\", \"" + ((RegisterMessageTCP) obj).getPassword() + "\", 0, current_timestamp())");
                    } //TODO NÃO TESTADO - nem foi criado ainda no CliOBS para enviar
                    ((RegisterMessageTCP) obj).setRegistered(rs.getBoolean(1));
                    oout.writeObject(obj);
                    oout.flush();
                }

                if (obj instanceof LoginMessageTCP) { //Processa Login
                    System.out.println("cheguei LoginMessageTCP. Recebi: ");
                    System.out.println("Username: " + ((LoginMessageTCP) obj).getUsername());
                    System.out.println("Password: " + ((LoginMessageTCP) obj).getPassword());
                    rs = stmt.executeQuery("SELECT EXISTS(SELECT * from utilizador WHERE Username = \"" + ((LoginMessageTCP) obj).getUsername() + "\" AND Password = \"" + ((LoginMessageTCP) obj).getPassword() + "\");");
                    rs.next();
                    ((LoginMessageTCP) obj).setConnected(rs.getBoolean(1));
                    if(rs.getBoolean(1)) {
                        System.out.println("entrei aqui");
                        clientList.addClientToClientList(((LoginMessageTCP) obj).getUsername(), oout);
                    }
                    System.out.println(((LoginMessageTCP) obj).getLoginStatus());
                    oout.writeObject(obj);
                    oout.flush();
                    //TODO actualizar a lista de como esse utilizador está online
                }

                if (obj instanceof UpdateContactListTCP) { //Update Lista de Contactos
                    System.out.println("Recebi UpdateContactListTCP");
                    rs = stmt.executeQuery("SELECT Contacto from tem_o_contacto WHERE Username = \"" + ((UpdateContactListTCP) obj).getUsername() + "\";");
                    while (rs.next()){
                        ((UpdateContactListTCP) obj).addContact(rs.getString(1));
                    }
                    oout.writeObject(obj);
                    oout.flush();
                }

                if (obj instanceof UpdateMessageListTCP) { //Update Lista de Mensagens
                    System.out.println("Recebi UpdateContactListTCP");
                    boolean isGroup = false;
                    rs = stmt.executeQuery("SELECT * from mensagem_de_grupo;");
                    while (rs.next()){
                        if(rs.getString("Nome") == ((UpdateMessageListTCP) obj).getContact()){
                            isGroup = true;
                            //TODO query mensagem de grupo
                        }
                    }
                    if(isGroup){
                        ((UpdateMessageListTCP) obj).setIsGroup(true);
                    }
                    else  {
                        ((UpdateMessageListTCP) obj).setIsGroup(false);
                        rs = stmt.executeQuery("SELECT * from mensagem_de_pares WHERE (Remetente = \"" + ((UpdateMessageListTCP) obj).getUsername() + "\" AND Destinatario = \"" + ((UpdateMessageListTCP) obj).getContact() + "\") OR (Remetente = \"" + ((UpdateMessageListTCP) obj).getContact() + "\" AND Destinatario = \"" + ((UpdateMessageListTCP) obj).getUsername() + "\");");
                        while (rs.next()) { //TODO devem ser alteradas as flags de mensagem vista
                            ((UpdateMessageListTCP) obj).addMsgList(rs.getString("Texto"), rs.getTimestamp("Data"), rs.getBoolean("Visto"), rs.getBoolean("Ficheiro"));
                        }
                    }
                    oout.writeObject(obj);
                    oout.flush();
                }

                if (obj instanceof DirectMessageTCP) { //se é uma mensagem individual
                    System.out.println("Recebi DirectMessageTCP");
                    System.out.println(((DirectMessageTCP) obj).getChatMessage());
                    System.out.println(((DirectMessageTCP) obj).getSender());
                    System.out.println(((DirectMessageTCP) obj).getdestination());
                    //inserção da mensagem na BD
                    //System.out.println("INSERT INTO mensagem_de_pares VALUES (0, 0, current_timestamp(), \"" + ((DirectMessageTCP) obj).getChatMessage() + "\", \"" + ((DirectMessageTCP) obj).getSender() +"\", \"" + ((DirectMessageTCP) obj).getdestination() + "\");");
                    stmt.executeUpdate("INSERT INTO mensagem_de_pares VALUES (0, 0, current_timestamp(), \"" + ((DirectMessageTCP) obj).getChatMessage() + "\", \"" + ((DirectMessageTCP) obj).getSender() +"\", \"" + ((DirectMessageTCP) obj).getdestination() + "\");");
                    //enviar o histórico de mensagens de volta
                    UpdateMessageListTCP updateMessageListTCP = new UpdateMessageListTCP(((DirectMessageTCP) obj).getSender(), ((DirectMessageTCP) obj).getdestination());
                    updateMessageListTCP.setIsGroup(false); //!!!
                    rs = stmt.executeQuery("SELECT * from mensagem_de_pares WHERE (Remetente = \"" + ((DirectMessageTCP) obj).getSender() + "\" AND Destinatario = \"" + ((DirectMessageTCP) obj).getdestination() + "\") OR (Remetente = \"" + ((DirectMessageTCP) obj).getdestination() + "\" AND Destinatario = \"" + ((DirectMessageTCP) obj).getSender() + "\");");
                    while (rs.next()) {
                        updateMessageListTCP.addMsgList(rs.getString("Texto"), rs.getTimestamp("Data"), rs.getBoolean("Visto"), rs.getBoolean("Ficheiro"));
                    }
                    oout.writeObject(updateMessageListTCP);
                    oout.flush();
                    //TODO e se o cliente estiver noutro servidor? - fazer
                    ObjectOutputStream ooutDest = clientList.getClientOout(((DirectMessageTCP) obj).getdestination());
                    if(ooutDest != null){
                        ooutDest.writeObject(updateMessageListTCP);
                        ooutDest.flush();
                    }
                }

                if (obj instanceof GroupMessageTCP) { //se é uma mensagem de grupo
                    System.out.println("Recebi GroupMessageTCP");

                }

                //TODO enviar mensagens a ao GRDS a dizer que houve uma alteração na BD
                //TODO enviar ao cliente afetado (destinatário para atualizar a sua vista)

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
