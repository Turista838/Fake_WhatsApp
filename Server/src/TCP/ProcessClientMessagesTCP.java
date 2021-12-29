package TCP;

import Data.ClientInfo;
import Data.ClientList;
import SharedClasses.*;
import UDP.UpdateGRDSMessagesUDP;

import java.io.*;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;


public class ProcessClientMessagesTCP extends Thread {

    private final String FILES_FOLDER_PATH;
    private final Socket socket;
    private ObjectInputStream oin;
    private ObjectOutputStream oout;
    private String client;
    private ClientList clientList;
    private Statement stmt;
    private ResultSet rs;
    private ArrayList clientsAffectedBySGBDChanges;
    private InetAddress grdsIP;
    private String grdsPort;
    private DatagramSocket socketUDP;
    private int nBytes;
    private byte [] buffer = new byte[4096];
    private ArrayList storedFilesList;

    public final static String UPDATE_CONTACTS = "Update Contacts";
    public final static String UPDATE_MESSAGES = "Update Message";

    public ProcessClientMessagesTCP(String files_folder_path, ArrayList<String> storedFilesList, ObjectInputStream in, ObjectOutputStream out, Socket socket, ClientList clientList, Connection conn, DatagramSocket socketUDP, InetAddress grdsIP, String grdsPort){
        oin = in;
        oout = out;
        this.socket = socket;
        this.clientList = clientList;
        this.socketUDP = socketUDP;
        this.storedFilesList = storedFilesList;
        this.grdsIP = grdsIP;
        this.grdsPort = grdsPort;
        FILES_FOLDER_PATH = files_folder_path;
        clientsAffectedBySGBDChanges = new ArrayList<String>();
        rs = null;
        try{
            stmt = conn.createStatement(); //é a partir deste statement que se faz os comandos
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void run(){

       try{

            while(true) { //TODO alterar este true
                Object obj = oin.readObject();

                if (obj == null) { //EOF
                    return;
                }

                //TODO actualizar a lista de como esse utilizador está online

                if (obj instanceof RegisterMessageTCP) { //Processa Registo
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
                        rs = stmt.executeQuery("SELECT Nome from utilizador WHERE Username = \"" + ((LoginMessageTCP) obj).getUsername() + "\";");
                        rs.next();
                        ((LoginMessageTCP) obj).setName(rs.getString("Nome"));
                        clientList.addClientToClientList(((LoginMessageTCP) obj).getUsername(), oout);
                        client = ((LoginMessageTCP) obj).getUsername();
                    }
                    System.out.println("Cliente " + ((LoginMessageTCP) obj).getUsername() + " [" + socket.getPort() + "] " + " logado neste servidor");
                    oout.writeObject(obj);
                    oout.flush();
                    //TODO actualizar a lista de como esse utilizador está online
                }

                if (obj instanceof RequestUsersOrGroupsTCP) { //Retorna Lista de Users ou Grupos para Adicionar
                    if(((RequestUsersOrGroupsTCP) obj).isRequestIsPendingInvites()){ //query pending invites
                        rs = stmt.executeQuery("SELECT Username from tem_o_contacto WHERE Contacto = \"" + ((RequestUsersOrGroupsTCP) obj).getUsername() + "\" AND Adicionado = 0;");
                        while (rs.next()){ //query friends requests
                            ((RequestUsersOrGroupsTCP) obj).addFriendsRequests(rs.getString(1));
                        }
                        rs = stmt.executeQuery("SELECT Grupo_ID_Grupo, Utilizador_Username, Nome FROM inclui LEFT JOIN grupo ON inclui.Grupo_ID_Grupo = grupo.ID_Grupo WHERE Grupo_ID_Grupo = (SELECT ID_Grupo FROM grupo WHERE User_Admin = \"" + ((RequestUsersOrGroupsTCP) obj).getUsername() + "\") AND adicionado = 0;");
                        while (rs.next()){ //query group requests
                            ((RequestUsersOrGroupsTCP) obj).addGroupsRequests(rs.getString(2), rs.getString(3));
                        }
                    }
                    else {
                        if (((RequestUsersOrGroupsTCP) obj).isRequestIsGroupList()) { //query lista de Grupos
                            rs = stmt.executeQuery("SELECT grupo.Nome FROM grupo WHERE grupo.Nome NOT IN (SELECT grupo.Nome FROM grupo, inclui WHERE (inclui.Grupo_ID_Grupo = grupo.ID_Grupo) AND inclui.Utilizador_Username = \"" + ((RequestUsersOrGroupsTCP) obj).getUsername() + "\");");
                            while (rs.next()) {
                                ((RequestUsersOrGroupsTCP) obj).addUserOrGroupName(rs.getString(1));
                            }
                        } else { //query lista de Users
                            rs = stmt.executeQuery("SELECT utilizador.Username FROM utilizador WHERE utilizador.Username NOT IN (SELECT Contacto from tem_o_contacto WHERE Username = \"" + ((RequestUsersOrGroupsTCP) obj).getUsername() + "\") AND NOT Username = \"" + ((RequestUsersOrGroupsTCP) obj).getUsername() + "\";");
                            while (rs.next()) {
                                ((RequestUsersOrGroupsTCP) obj).addUserOrGroupName(rs.getString(1));
                            }
                        }
                    }
                    oout.writeObject(obj);
                    oout.flush();
                }

                if (obj instanceof UpdateContactListTCP) { //Update Lista de Contactos
                    System.out.println("Sou " + ((UpdateContactListTCP) obj).getUsername() + " e entrei no UpdateContact");
                    rs = stmt.executeQuery("SELECT Contacto from tem_o_contacto WHERE Username = \"" + ((UpdateContactListTCP) obj).getUsername() + "\" AND Adicionado = 1;");
                    while (rs.next()){ //query contactos
                        ((UpdateContactListTCP) obj).addContact(rs.getString(1));
                        System.out.println(((UpdateContactListTCP) obj).getUsername() + " adicionou à lista: " + rs.getString(1));
                    }
                    rs = stmt.executeQuery("SELECT grupo.Nome FROM grupo, inclui WHERE (inclui.Grupo_ID_Grupo = grupo.ID_Grupo) AND inclui.Utilizador_Username = \"" + ((UpdateContactListTCP) obj).getUsername() + "\" AND Adicionado = 1;");
                    while (rs.next()){ //query grupos
                        ((UpdateContactListTCP) obj).addContact(rs.getString(1));
                        System.out.println(rs.getString(1));
                    }
                    oout.writeObject(obj);
                    oout.flush();
                }

                if (obj instanceof UpdateMessageListTCP) { //Update Lista de Mensagens (User ou Grupo)
                    if(!((UpdateMessageListTCP) obj).getContact().isEmpty()) {
                        boolean isGroup = false;
                        rs = stmt.executeQuery("SELECT * FROM grupo;");
                        while (rs.next()) {
                            if (Objects.equals(rs.getString("Nome"), ((UpdateMessageListTCP) obj).getContact())) {
                                isGroup = true;
                            }
                        }
                        if (isGroup) {
                            ((UpdateMessageListTCP) obj).setIsGroup(true);
                            rs = stmt.executeQuery("SELECT * FROM mensagem_de_grupo WHERE Grupo = \"" + ((UpdateMessageListTCP) obj).getContact() + "\";");
                            while (rs.next()) { //TODO devem ser alteradas as flags de mensagem vista
                                ((UpdateMessageListTCP) obj).addMsgList(rs.getString("Texto"), rs.getTimestamp("Data"), rs.getBoolean("Visto"), rs.getBoolean("Ficheiro"));
                            }
                            rs = stmt.executeQuery("SELECT EXISTS(SELECT * from grupo WHERE User_Admin = \"" + ((UpdateMessageListTCP) obj).getUsername() + "\" AND Nome = \"" + ((UpdateMessageListTCP) obj).getContact() + "\");");
                            rs.next();
                            if (rs.getBoolean(1)) { //se user é admin do grupo, pode ter acesso a outros botões
                                ((UpdateMessageListTCP) obj).setIsAdmin(true);
                            }
                        } else {
                            ((UpdateMessageListTCP) obj).setIsGroup(false);
                            ((UpdateMessageListTCP) obj).setIsAdmin(false);
                            rs = stmt.executeQuery("SELECT * from mensagem_de_pares WHERE (Remetente = \"" + ((UpdateMessageListTCP) obj).getUsername() + "\" AND Destinatario = \"" + ((UpdateMessageListTCP) obj).getContact() + "\") OR (Remetente = \"" + ((UpdateMessageListTCP) obj).getContact() + "\" AND Destinatario = \"" + ((UpdateMessageListTCP) obj).getUsername() + "\") ORDER BY Data;");
                            while (rs.next()) {
                                ((UpdateMessageListTCP) obj).addMsgList(rs.getString("Texto"), rs.getTimestamp("Data"), rs.getBoolean("Visto"), rs.getBoolean("Ficheiro"));
                            }
                            stmt.executeUpdate("UPDATE mensagem_de_pares SET Visto = 1 WHERE Remetente = \"" + ((UpdateMessageListTCP) obj).getContact() + "\" AND Destinatario = \"" + ((UpdateMessageListTCP) obj).getUsername() + "\";");
                        }
                    }
                    rs = stmt.executeQuery("SELECT DISTINCT Remetente from mensagem_de_pares WHERE Destinatario = \"" + ((UpdateMessageListTCP) obj).getUsername() + "\" AND Visto = 0;");
                    while (rs.next()) {
                        ((UpdateMessageListTCP) obj).addContactsWithUnreadMessages(rs.getString(1));
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
                    rs = stmt.executeQuery("SELECT * from mensagem_de_pares WHERE (Remetente = \"" + ((DirectMessageTCP) obj).getSender() + "\" AND Destinatario = \"" + ((DirectMessageTCP) obj).getDestination() + "\") OR (Remetente = \"" + ((DirectMessageTCP) obj).getDestination() + "\" AND Destinatario = \"" + ((DirectMessageTCP) obj).getSender() + "\") ORDER BY Data;");
                    while (rs.next()) {
                        updateMessageListTCP.addMsgList(rs.getString("Texto"), rs.getTimestamp("Data"), rs.getBoolean("Visto"), rs.getBoolean("Ficheiro"));
                    }
                    oout.writeObject(updateMessageListTCP);
                    oout.flush();
                    clientsAffectedBySGBDChanges.add(((DirectMessageTCP) obj).getDestination());
                    sendUpdateMessageToServerClients(UPDATE_MESSAGES, clientsAffectedBySGBDChanges);
                }

                if (obj instanceof GroupMessageTCP) { //se é uma mensagem de grupo
                    stmt.executeUpdate("INSERT INTO mensagem_de_grupo VALUES (0, 0, current_timestamp(), \"" + ((GroupMessageTCP) obj).getChatMessage() + "\", \"" + ((GroupMessageTCP) obj).getSender() + "\", \"" + ((GroupMessageTCP) obj).getGroup() + "\");");
                    //enviar o histórico de mensagens de volta
                    UpdateMessageListTCP updateMessageListTCP = new UpdateMessageListTCP(((GroupMessageTCP) obj).getSender(), ((GroupMessageTCP) obj).getGroup());
                    updateMessageListTCP.setIsGroup(true); //!!!
                    rs = stmt.executeQuery("SELECT * FROM mensagem_de_grupo WHERE grupo = \"" + ((GroupMessageTCP) obj).getGroup() + "\";");
                    while (rs.next()) {
                        updateMessageListTCP.addMsgList(rs.getString("Texto"), rs.getTimestamp("Data"), rs.getBoolean("Visto"), rs.getBoolean("Ficheiro"));
                        //clientsAffectedBySGBDChanges.add(((DirectMessageTCP) obj).getDestination()); //TODO por aqui os membros afectados
                    }
                    oout.writeObject(updateMessageListTCP);
                    oout.flush();
                    sendUpdateMessageToServerClients(UPDATE_MESSAGES, clientsAffectedBySGBDChanges);
                }

                if (obj instanceof FileMessageTCP){
                    if(((FileMessageTCP) obj).getUploading()) {
                        long fileS = ((FileMessageTCP) obj).getFileSize();
                        int cont = 0;
                        InputStream in = socket.getInputStream();
                        FileOutputStream localFileOutputStream = new FileOutputStream(FILES_FOLDER_PATH + "\\" + ((FileMessageTCP) obj).getFilename());

                        do {
                            nBytes = in.read(buffer);
                            cont = cont + nBytes;
                            if (nBytes > 0) { //porque pode vir nBytes = -1
                                localFileOutputStream.write(buffer, 0, nBytes);
                            }
                        } while (cont != fileS);

                        storedFilesList.add(((FileMessageTCP) obj).getFilename());
                        Collections.sort(storedFilesList);
                        localFileOutputStream.close();

                        stmt.executeUpdate("INSERT INTO mensagem_de_pares VALUES (0, 1, current_timestamp(), \"#Ficheiro: " + ((FileMessageTCP) obj).getFilename() + "\", \"" + ((FileMessageTCP) obj).getSender() + "\", \"" + ((FileMessageTCP) obj).getDestination() + "\");");
                        oout.writeObject(UPDATE_MESSAGES);
                        oout.flush();

                        sendUpdateMessageToServerClients(UPDATE_MESSAGES, clientsAffectedBySGBDChanges);
                        new UpdateGRDSMessagesUDP(storedFilesList, socketUDP, grdsIP, grdsPort);
                    }
                    if(((FileMessageTCP) obj).getDownload()){
                        byte []fileChunk = new byte[4096];
                        Path path = Paths.get(FILES_FOLDER_PATH + "\\" + ((FileMessageTCP) obj).getFilename());
                        FileMessageTCP fileMessageTCP = new FileMessageTCP(Files.size(path), ((FileMessageTCP) obj).getFilename());
                        fileMessageTCP.setDownload(true);
                        oout.writeObject(fileMessageTCP);
                        oout.flush();

                        OutputStream fileOut = socket.getOutputStream();
                        FileInputStream fileInputStream = new FileInputStream(FILES_FOLDER_PATH + "\\" + ((FileMessageTCP) obj).getFilename());
                        do {
                            nBytes = fileInputStream.read(fileChunk);
                            if (nBytes != -1) {// enquanto não é EOF
                                fileOut.write(fileChunk, 0, nBytes);
                                fileOut.flush();
                            }
                        } while (nBytes > 0);
                        fileInputStream.close();
                    }
                }

                if (obj instanceof UserManagementTCP) { //Editar Perfil
                    rs = stmt.executeQuery("SELECT EXISTS(SELECT * from utilizador WHERE Username = \"" + ((UserManagementTCP) obj).getUsername() + "\" AND Password = \"" + ((UserManagementTCP) obj).getPassword() + "\");");
                    rs.next();
                    if(rs.getBoolean(1)) {
                        if (((UserManagementTCP) obj).getAlteringPassword()) { //é para alterar também a pass
                            //stmt.executeUpdate("");
                        } else { //é só para alterar o username

                           //stmt.executeUpdate("UPDATE utilizador SET Nome = \"" + ((GroupManagementTCP)obj).getNewGroupName() + "\" WHERE (User_Admin = \"" + ((GroupManagementTCP)obj).getUsername() + "\" AND Nome = \"" + ((GroupManagementTCP)obj).getGroupName() + "\");");
                        }
                        //é preciso alterar nas mensagens e na tabela de grupo
                        sendUpdateMessageToServerClients(UPDATE_CONTACTS, clientsAffectedBySGBDChanges);
                    }
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
                            //TODO update a todos os utilizadores desse grupo
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
                            stmt.executeUpdate("INSERT INTO inclui values ((SELECT ID_Grupo FROM grupo WHERE Nome = \"" + ((GroupManagementTCP)obj).getGroupName() + "\"), \"" + ((GroupManagementTCP)obj).getUsername() + "\", 1);");
                            ((GroupManagementTCP)obj).setCreatingSuccess(true);
                        }
                        oout.writeObject(obj);
                        oout.flush();
                    }
                    if(((GroupManagementTCP)obj).isDeleting()){
                        System.out.println("Recebi GroupManagementTCP - isDeleting");
                        //TODO query de todos os membros desse grupo
                        //TODO clientsAffectedBySGBDChanges
                        stmt.executeUpdate("DELETE FROM inclui WHERE Grupo_ID_Grupo = (SELECT ID_Grupo FROM grupo WHERE Nome = \"" + ((GroupManagementTCP)obj).getGroupName() + "\");");
                        stmt.executeUpdate("DELETE FROM grupo WHERE User_Admin = \"" + ((GroupManagementTCP)obj).getUsername() + "\" AND Nome = \"" + ((GroupManagementTCP)obj).getGroupName() + "\";");
                        ((GroupManagementTCP)obj).setDeletingSuccess(true);
                        oout.writeObject(obj);
                        oout.flush();
                    }
                    if(((GroupManagementTCP)obj).isExcluding()){
                        System.out.println("Recebi GroupManagementTCP - isExcluding");
                        //TODO query de todos os membros desse grupo
                        //TODO clientsAffectedBySGBDChanges
                        stmt.executeUpdate("DELETE FROM inclui WHERE Grupo_ID_Grupo = (SELECT ID_Grupo FROM grupo WHERE Nome = \"" + ((GroupManagementTCP)obj).getGroupName() + "\") AND Utilizador_Username = \"" + ((GroupManagementTCP)obj).getSelectedUsername() + "\";");
                        ((GroupManagementTCP)obj).setExcludingSuccess(true);
                        oout.writeObject(obj);
                        oout.flush();
                    }
                    sendUpdateMessageToServerClients(UPDATE_CONTACTS, clientsAffectedBySGBDChanges);
                }

                if (obj instanceof FriendOrGroupRequestTCP) { //Friend ou Group Request
                    if(((FriendOrGroupRequestTCP) obj).getFriendRequest()){
                        stmt.executeUpdate("INSERT INTO tem_o_contacto VALUES (\"" + ((FriendOrGroupRequestTCP) obj).getUsername() + "\", \"" + ((FriendOrGroupRequestTCP) obj).getFriend() + "\", 0);");
                    }
                    if(((FriendOrGroupRequestTCP) obj).getGroupRequest()){
                        stmt.executeUpdate("INSERT INTO inclui values ((SELECT ID_Grupo FROM grupo WHERE Nome = \"" + ((FriendOrGroupRequestTCP) obj).getGroup() + "\"), \"" + ((FriendOrGroupRequestTCP) obj).getUsername() + "\", 0);");
                    }
                    oout.writeObject(obj);
                    oout.flush();
//                    clientsAffectedBySGBDChanges.add(((AcceptOrRefuseRequestTCP) obj).getRequest());
//                    sendUpdateMessageToClients(UPDATE_CONTACTS, clientsAffectedBySGBDChanges);
                }

                if (obj instanceof AcceptOrRefuseRequestTCP) { //Accept or Refuse Friend ou Group
                    if(((AcceptOrRefuseRequestTCP) obj).getAccept()) { //é para aceitar
                        if (((AcceptOrRefuseRequestTCP) obj).getGroup()) { //aceite num grupo
                            stmt.executeUpdate("UPDATE inclui SET Adicionado = 1 WHERE Utilizador_Username = \"" + ((AcceptOrRefuseRequestTCP) obj).getRequest() + "\" AND Grupo_ID_Grupo = (SELECT ID_Grupo FROM grupo WHERE Nome = \"" + ((AcceptOrRefuseRequestTCP) obj).getGroupName() + "\");");
                        } else { //aceite contacto
                            stmt.executeUpdate("DELETE FROM tem_o_contacto WHERE Username = \"" + ((AcceptOrRefuseRequestTCP) obj).getUsername() + "\" AND Contacto = \"" + ((AcceptOrRefuseRequestTCP) obj).getRequest() + "\";");
                            stmt.executeUpdate("DELETE FROM tem_o_contacto WHERE Username = \"" + ((AcceptOrRefuseRequestTCP) obj).getRequest() + "\" AND Contacto = \"" + ((AcceptOrRefuseRequestTCP) obj).getUsername() + "\";");
                            stmt.executeUpdate("INSERT INTO tem_o_contacto VALUES (\"" + ((AcceptOrRefuseRequestTCP) obj).getUsername() + "\", \"" + ((AcceptOrRefuseRequestTCP) obj).getRequest() + "\", 1);");
                            stmt.executeUpdate("INSERT INTO tem_o_contacto VALUES (\"" + ((AcceptOrRefuseRequestTCP) obj).getRequest() + "\", \"" + ((AcceptOrRefuseRequestTCP) obj).getUsername() + "\", 1);");
                            clientsAffectedBySGBDChanges.add(((AcceptOrRefuseRequestTCP) obj).getRequest());
                        }
                    }
                    else{ //é para recusar
                        if(((AcceptOrRefuseRequestTCP) obj).getGroup()){ //recusado num grupo
                            stmt.executeUpdate("DELETE inclui WHERE Utilizador_Username = \"" + ((AcceptOrRefuseRequestTCP) obj).getRequest() + "\" AND Grupo_ID_Grupo = (SELECT ID_Grupo FROM grupo WHERE Nome = \"" + ((AcceptOrRefuseRequestTCP) obj).getGroupName() + "\");");
                        }
                        else{ //recusado num contacto
                            stmt.executeUpdate("DELETE FROM tem_o_contacto WHERE Username = \"" + ((AcceptOrRefuseRequestTCP) obj).getUsername() + "\" AND Contacto = \"" + ((AcceptOrRefuseRequestTCP) obj).getRequest() + "\";");
                            stmt.executeUpdate("DELETE FROM tem_o_contacto WHERE Username = \"" + ((AcceptOrRefuseRequestTCP) obj).getRequest() + "\" AND Contacto = \"" + ((AcceptOrRefuseRequestTCP) obj).getUsername() + "\";");
                            clientsAffectedBySGBDChanges.add(((AcceptOrRefuseRequestTCP) obj).getRequest());
                        }
                    }

                    oout.writeObject(obj);
                    oout.flush();
                    sendUpdateMessageToServerClients(UPDATE_CONTACTS, clientsAffectedBySGBDChanges);
                }

                clientsAffectedBySGBDChanges.clear();

            }

        }catch(Exception e){
           //TODO remover da lista e colocá-lo como offline
            e.printStackTrace();
            System.out.println("Problema na comunicação com o cliente " +
                    socket.getInetAddress().getHostAddress() + ":" +
                    socket.getPort()+"\n\t");
        }finally{
            try{
                socket.close();
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    private void sendUpdateMessageToServerClients(String message, ArrayList clientsAffectedBySGBDChanges) {

        new UpdateGRDSMessagesUDP(socketUDP, grdsIP, grdsPort, message, clientsAffectedBySGBDChanges);

        ArrayList<ClientInfo> cList = clientList.getArrayClientList();
        for(ClientInfo c : cList){
            if(!Objects.equals(c.getUsername(), client)) {
                ObjectOutputStream ooutDest = c.getOout();
                try {
                    ooutDest.writeObject(message);
                    ooutDest.flush();
                } catch (Exception IOException) {
                    //TODO remover da lista e colocá-lo como offline
                }
            }
        }
    }


}
