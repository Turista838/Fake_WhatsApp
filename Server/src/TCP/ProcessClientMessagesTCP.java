package TCP;

import Data.ClientInfo;
import Data.ClientList;
import SharedClasses.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;

public class ProcessClientMessagesTCP extends Thread {

    private final Socket socket;
    private String client;
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

                //TODO actualizar a lista de como esse utilizador está online

                if (obj instanceof RegisterMessageTCP) { //Processa Registo
                    System.out.println("cheguei RegisterMessageTCP. Recebi: ");
                    System.out.println("Username: " + ((RegisterMessageTCP) obj).getUsername());
                    rs = stmt.executeQuery("SELECT EXISTS(SELECT * from utilizador WHERE Username = \"" + ((RegisterMessageTCP) obj).getUsername() + "\");");
                    rs.next();
                    if(!rs.getBoolean(1)){ //se não encontrou, pode registar
                        stmt.executeUpdate("INSERT INTO utilizador VALUES (\"" + ((RegisterMessageTCP) obj).getUsername() + "\", \"" + ((RegisterMessageTCP) obj).getNome() + "\", \"" + ((RegisterMessageTCP) obj).getPassword() + "\", 0, current_timestamp())");
                        ((RegisterMessageTCP) obj).setRegistered(true);
                    }
                    oout.writeObject(obj);
                    oout.flush();
                }

                if (obj instanceof LoginMessageTCP) { //Processa Login
                    rs = stmt.executeQuery("SELECT EXISTS(SELECT * from utilizador WHERE Username = \"" + ((LoginMessageTCP) obj).getUsername() + "\" AND Password = \"" + ((LoginMessageTCP) obj).getPassword() + "\");");
                    rs.next();
                    ((LoginMessageTCP) obj).setConnected(rs.getBoolean(1));
                    if(rs.getBoolean(1)) {
                        rs = stmt.executeQuery("SELECT name from utilizador WHERE Username = \"" + ((LoginMessageTCP) obj).getUsername() + "\";");
                        rs.next();
                        ((LoginMessageTCP) obj).setName(rs.getString("Nome"));
                        clientList.addClientToClientList(((LoginMessageTCP) obj).getUsername(), oout);
                        client = ((LoginMessageTCP) obj).getUsername();
                    }
                    oout.writeObject(obj);
                    oout.flush();
                    //TODO actualizar a lista de como esse utilizador está online
                }

                if (obj instanceof RequestUsersOrGroupsTCP) { //Retorna Lista de Users ou Grupos para Adicionar
                    System.out.println("Recebi RequestUsersOrGroupsTCP");
                    if(((RequestUsersOrGroupsTCP) obj).isRequestIsGroupList()){ //query lista de Grupos
                        rs = stmt.executeQuery("SELECT grupo.Nome FROM grupo WHERE grupo.Nome NOT IN (SELECT grupo.Nome FROM grupo, inclui WHERE (inclui.Grupo_ID_Grupo = grupo.ID_Grupo) AND inclui.Utilizador_Username = \"" + ((RequestUsersOrGroupsTCP) obj).getUsername() + "\");");
                        while (rs.next()){
                            ((RequestUsersOrGroupsTCP) obj).addUserOrGroupName(rs.getString(1));
                        }
                    }
                    else{ //query lista de Users
                        rs = stmt.executeQuery("SELECT utilizador.Username FROM utilizador WHERE utilizador.Username NOT IN (SELECT Contacto from tem_o_contacto WHERE Username = \"" + ((RequestUsersOrGroupsTCP) obj).getUsername() + "\") AND NOT Username = \"" + ((RequestUsersOrGroupsTCP) obj).getUsername() + "\";");
                        while (rs.next()){
                            ((RequestUsersOrGroupsTCP) obj).addUserOrGroupName(rs.getString(1));
                        }
                    }
                    oout.writeObject(obj);
                    oout.flush();
                }

                if (obj instanceof UpdateContactListTCP) { //Update Lista de Contactos
                    System.out.println("Recebi UpdateContactListTCP");
                    rs = stmt.executeQuery("SELECT Contacto from tem_o_contacto WHERE Username = \"" + ((UpdateContactListTCP) obj).getUsername() + "\";");
                    while (rs.next()){ //query contactos
                        ((UpdateContactListTCP) obj).addContact(rs.getString(1));
                        System.out.println(rs.getString(1));
                    }
                    rs = stmt.executeQuery("SELECT grupo.Nome FROM grupo, inclui WHERE (inclui.Grupo_ID_Grupo = grupo.ID_Grupo) AND inclui.Utilizador_Username = \"" + ((UpdateContactListTCP) obj).getUsername() + "\";");
                    while (rs.next()){ //query grupos
                        ((UpdateContactListTCP) obj).addContact(rs.getString(1));
                        System.out.println(rs.getString(1));
                    }
                    oout.writeObject(obj);
                    oout.flush();
                }

                if (obj instanceof UpdateMessageListTCP) { //Update Lista de Mensagens (User ou Grupo)
                    System.out.println("Recebi UpdateContactListTCP. Contacto selecionado: " + ((UpdateMessageListTCP) obj).getContact());
                    boolean isGroup = false;
                    rs = stmt.executeQuery("SELECT * FROM grupo;");
                    while (rs.next()){
                        if(Objects.equals(rs.getString("Nome"), ((UpdateMessageListTCP) obj).getContact())) {
                            System.out.println("entrei");
                            isGroup = true;
                        }
                    }
                    if(isGroup){
                        ((UpdateMessageListTCP) obj).setIsGroup(true);
                        rs = stmt.executeQuery("SELECT * FROM mensagem_de_grupo WHERE Grupo = \"" + ((UpdateMessageListTCP) obj).getContact() + "\";");
                        while (rs.next()) {
                            ((UpdateMessageListTCP) obj).addMsgList(rs.getString("Texto"), rs.getTimestamp("Data"), rs.getBoolean("Visto"), rs.getBoolean("Ficheiro"));
                        }
                        rs = stmt.executeQuery("SELECT EXISTS(SELECT * from grupo WHERE User_Admin = \"" + ((UpdateMessageListTCP) obj).getUsername() + "\" AND Nome = \"" + ((UpdateMessageListTCP) obj).getContact() + "\");");
                        rs.next();
                        if(rs.getBoolean(1)){ //se user é admin do grupo, pode ter acesso a outros botões
                            ((UpdateMessageListTCP) obj).setIsAdmin(true);
                        }
                    }
                    else  {
                        ((UpdateMessageListTCP) obj).setIsGroup(false);
                        ((UpdateMessageListTCP) obj).setIsAdmin(false);
                        rs = stmt.executeQuery("SELECT * from mensagem_de_pares WHERE (Remetente = \"" + ((UpdateMessageListTCP) obj).getUsername() + "\" AND Destinatario = \"" + ((UpdateMessageListTCP) obj).getContact() + "\") OR (Remetente = \"" + ((UpdateMessageListTCP) obj).getContact() + "\" AND Destinatario = \"" + ((UpdateMessageListTCP) obj).getUsername() + "\");");
                        while (rs.next()) { //TODO devem ser alteradas as flags de mensagem vista
                            ((UpdateMessageListTCP) obj).addMsgList(rs.getString("Texto"), rs.getTimestamp("Data"), rs.getBoolean("Visto"), rs.getBoolean("Ficheiro"));
                        }
                    }
                    oout.writeObject(obj);
                    oout.flush();
                }

                if (obj instanceof DirectMessageTCP) { //se é uma mensagem individual
                     //inserção da mensagem na BD
                    stmt.executeUpdate("INSERT INTO mensagem_de_pares VALUES (0, 0, current_timestamp(), \"" + ((DirectMessageTCP) obj).getChatMessage() + "\", \"" + ((DirectMessageTCP) obj).getSender() +"\", \"" + ((DirectMessageTCP) obj).getDestination() + "\");");
                    //enviar o histórico de mensagens de volta
                    UpdateMessageListTCP updateMessageListTCP = new UpdateMessageListTCP(((DirectMessageTCP) obj).getSender(), ((DirectMessageTCP) obj).getDestination());
                    updateMessageListTCP.setIsGroup(false); //!!!
                    rs = stmt.executeQuery("SELECT * from mensagem_de_pares WHERE (Remetente = \"" + ((DirectMessageTCP) obj).getSender() + "\" AND Destinatario = \"" + ((DirectMessageTCP) obj).getDestination() + "\") OR (Remetente = \"" + ((DirectMessageTCP) obj).getDestination() + "\" AND Destinatario = \"" + ((DirectMessageTCP) obj).getSender() + "\");");
                    while (rs.next()) {
                        updateMessageListTCP.addMsgList(rs.getString("Texto"), rs.getTimestamp("Data"), rs.getBoolean("Visto"), rs.getBoolean("Ficheiro"));
                    }
                    oout.writeObject(updateMessageListTCP);
                    oout.flush();
                    sendUpdateMessageToClients();
                }

                if (obj instanceof GroupMessageTCP) { //se é uma mensagem de grupo
                    stmt.executeUpdate("INSERT INTO mensagem_de_grupo VALUES (0, 0, current_timestamp(), \"" + ((GroupMessageTCP) obj).getChatMessage() + "\", \"" + ((GroupMessageTCP) obj).getSender() + "\", \"" + ((GroupMessageTCP) obj).getGroup() + "\");");
                    //enviar o histórico de mensagens de volta
                    UpdateMessageListTCP updateMessageListTCP = new UpdateMessageListTCP(((GroupMessageTCP) obj).getSender(), ((GroupMessageTCP) obj).getGroup());
                    updateMessageListTCP.setIsGroup(true); //!!!
                    rs = stmt.executeQuery("SELECT * FROM mensagem_de_grupo WHERE grupo = \"" + ((GroupMessageTCP) obj).getGroup() + "\";");
                    while (rs.next()) {
                        updateMessageListTCP.addMsgList(rs.getString("Texto"), rs.getTimestamp("Data"), rs.getBoolean("Visto"), rs.getBoolean("Ficheiro"));
                    }
                    oout.writeObject(updateMessageListTCP);
                    oout.flush();
                    sendUpdateMessageToClients();
                }

                if (obj instanceof GroupManagementTCP) { //Actualiza Gestão de Grupo (Apenas admin tem acesso)
                    System.out.println("Recebi GroupManagementTCP");
                    if(((GroupManagementTCP)obj).isConsulting()){ //retorna lista de membros desse grupo
                        System.out.println("Recebi GroupManagementTCP - isConsulting");
                        rs = stmt.executeQuery("SELECT DISTINCT inclui.Utilizador_Username FROM inclui, grupo WHERE Grupo_ID_Grupo = (SELECT ID_Grupo FROM grupo WHERE User_Admin = \"" + ((GroupManagementTCP)obj).getUsername() + "\" AND Nome = \"" + ((GroupManagementTCP)obj).getGroupName() + "\");");
                        while (rs.next()) {
                            ((GroupManagementTCP)obj).addGroupMember(rs.getString("Utilizador_Username"));
                        }
                        oout.writeObject(obj);
                        oout.flush();
                    }
                    if(((GroupManagementTCP)obj).isEditing()){ //retorna novo nome do grupo
                        rs = stmt.executeQuery("SELECT EXISTS(SELECT * from grupo WHERE Nome = \"" + ((GroupManagementTCP)obj).getNewGroupName() + "\");");
                        rs.next();
                        if(rs.getBoolean(1)){ //se user é efetivamente admin do grupo, altera o nome
                            stmt.executeUpdate("UPDATE grupo SET Nome = \"" + ((GroupManagementTCP)obj).getNewGroupName() + "\" WHERE (User_Admin = \"" + ((GroupManagementTCP)obj).getUsername() + "\" AND Nome = \"" + ((GroupManagementTCP)obj).getGroupName() + "\");");
                            ((GroupManagementTCP)obj).setEditingSuccess(true);
                            //TODO alterar o nome em todas as mensagens de grupo
                        }
                        else{
                            ((GroupManagementTCP)obj).setEditingSuccess(false);
                        }
                        oout.writeObject(obj);
                        oout.flush();
                    }
                    if(((GroupManagementTCP)obj).isCreating()){ //retorna confirmação de criação com sucesso ou não
                        System.out.println("Recebi GroupManagementTCP - isCreating");
                        rs = stmt.executeQuery("SELECT EXISTS(SELECT * from grupo WHERE Nome = \"" + ((GroupManagementTCP)obj).getGroupName() + "\");");
                        rs.next();
                        if(rs.getBoolean(1)){ //se grupo já existe não cria
                            ((GroupManagementTCP)obj).setCreatingSuccess(false);
                        }
                        else{
                            stmt.executeUpdate("INSERT INTO grupo (Nome, User_Admin) VALUES (\"" + ((GroupManagementTCP)obj).getGroupName() + "\", \"" + ((GroupManagementTCP)obj).getUsername() + "\");");
                            stmt.executeUpdate("INSERT INTO inclui values ((SELECT ID_Grupo FROM grupo WHERE Nome = \"" + ((GroupManagementTCP)obj).getGroupName() + "\"), \"" + ((GroupManagementTCP)obj).getUsername() + "\");");
                            ((GroupManagementTCP)obj).setCreatingSuccess(true);
                        }
                        oout.writeObject(obj);
                        oout.flush();
                    }
                    if(((GroupManagementTCP)obj).isDeleting()){
                        System.out.println("Recebi GroupManagementTCP - isDeleting");
                        stmt.executeUpdate("DELETE FROM inclui WHERE Grupo_ID_Grupo = (SELECT ID_Grupo FROM grupo WHERE Nome = \"" + ((GroupManagementTCP)obj).getGroupName() + "\");");
                        stmt.executeUpdate("DELETE FROM grupo WHERE User_Admin = \"" + ((GroupManagementTCP)obj).getUsername() + "\" AND Nome = \"" + ((GroupManagementTCP)obj).getGroupName() + "\";");
                        ((GroupManagementTCP)obj).setDeletingSuccess(true);
                        oout.writeObject(obj);
                        oout.flush();
                    }
                    if(((GroupManagementTCP)obj).isExcluding()){
                        System.out.println("Recebi GroupManagementTCP - isExcluding");
                        stmt.executeUpdate("DELETE FROM inclui WHERE Grupo_ID_Grupo = (SELECT ID_Grupo FROM grupo WHERE Nome = \"" + ((GroupManagementTCP)obj).getGroupName() + "\") AND Utilizador_Username = \"" + ((GroupManagementTCP)obj).getSelectedUsername() + "\";");
                        ((GroupManagementTCP)obj).setExcludingSuccess(true);
                        oout.writeObject(obj);
                        oout.flush();
                    }
                    sendUpdateMessageToClients();
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
            }catch(IOException e){

            }
        }
    }

    private void sendUpdateMessageToClients() {
        ArrayList<ClientInfo> cList = clientList.getArrayClientList();

        for(ClientInfo c : cList){
            if(!Objects.equals(c.getUsername(), client)) {
                ObjectOutputStream ooutDest = c.getOout();
                try {
                    ooutDest.writeObject("Update");
                    ooutDest.flush();
                } catch (Exception IOException) {
                    //TODO remover da lista e colocá-lo como offline
                }
            }
        }
    }
}
