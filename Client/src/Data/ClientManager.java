package Data;

import SharedClasses.*;
import SharedClasses.Data.MessageList;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.*;
import java.util.ArrayList;
import java.util.Objects;


public class ClientManager extends Thread {

    private ClientStartup cs;

    private String name;
    private String username;
    private Boolean loggedIn = false;
    private Boolean selectedContactIsGroup = false;
    private Boolean selectedGroupIsAdmin = false;
    private String selectedContact = "";
    private ArrayList<String> contactList;
    private ArrayList<MessageList> msgList;
    private ArrayList<String> availableUsersList;
    private ArrayList<String> availableGroupsList;
    private ArrayList<String> selectedGroupMembersList;
    private ArrayList<String> pendingInvitesListUsers;
    private ArrayList<String[]> pendingInvitesListGroups;
    private int nBytes;
    private byte [] buffer = new byte[4096];

    private ObjectOutputStream oout; //enviar TCP
    private ObjectInputStream oin; //receber TCP

    public static final String FILES_FOLDER_PATH = "C:\\TempClient";

    private final PropertyChangeSupport propertyChangeSupport;
    public final static String VIEW_CHANGED = "View Changed";
    public final static String LOGIN_FAILED = "Login Failed";
    public final static String REGISTER_FAILED = "Register Failed";
    public final static String REGISTER_SUCCESS = "Register Success";
    public final static String FRIEND_REQUEST = "Friend Request";
    public final static String GROUP_REQUEST = "Group Request";
    public final static String UPDATE_REQUESTS = "Update Requests";
    public final static String VIEW_GROUP_MEMBERS = "View Users in Group";
    public final static String GROUP_EDIT_SUCCESSFUL = "Group name edited";
    public final static String GROUP_EDIT_NOT_SUCCESSFUL = "Group name not edites";
    public final static String GROUP_CREATING_SUCCESSFUL = "Group created";
    public final static String GROUP_CREATING_NOT_SUCCESSFUL = "Group nor created";
    public final static String GROUP_DELETING_SUCCESSFUL = "Group created";
    public final static String GROUP_EXCLUDING_SUCCESSFUL = "Group user excluded";
    public final static String USER_EDIT_SUCCESSFUL = "User name edited";
    public final static String USER_EDIT_NOT_SUCCESSFUL = "User name not edited";
    public final static String HIDE_SHOW_BUTTONS = "Hide or Show Buttons";

    public ClientManager(ClientStartup cs){
        this.cs = cs;
        oout = cs.getOout();
        oin = cs.getOin();
        contactList = new ArrayList<String>();
        msgList = new ArrayList<MessageList>();
        propertyChangeSupport = new PropertyChangeSupport(this);
        this.start();
    }

    public void run(){

        try {
            oout.writeObject("Cliente");
            oout.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }


        while (true) {
            try {

                Object obj = oin.readObject();

                if (obj == null) { //EOF
                    return;
                }

                if (obj instanceof String) { //Única mensagem que não tem classe personalizada
                    if(obj.equals("Update Contacts")) {
                        updateContactList();
                    }
                    if(obj.equals("Update Message")) {
                        requestMessages();
                    }
                }

                if (obj instanceof RegisterMessageTCP) { //Actualiza mensagens
                    RegisterMessageTCP registerMessageTCP = (RegisterMessageTCP)obj;
                    System.out.println("Registado: " + registerMessageTCP.getRegisteredStatus());
                    if(registerMessageTCP.getRegisteredStatus()){
                        firePropertyChangeListener(REGISTER_SUCCESS);
                    }
                    else{
                        firePropertyChangeListener(REGISTER_FAILED);
                    }
                }

                if (obj instanceof LoginMessageTCP) { //Actualiza mensagens
                    LoginMessageTCP loginMessageTCP = (LoginMessageTCP) obj;
                    loggedIn = loginMessageTCP.getLoginStatus();
                    if (loggedIn) {
                        name = loginMessageTCP.getName();
                        username = loginMessageTCP.getUsername();
                        updateContactList();
                        requestMessages();
                    }
                    else
                        firePropertyChangeListener(LOGIN_FAILED);
                }

                if (obj instanceof RequestUsersOrGroupsTCP) { //Recebe users registados, grupos ou contactos que possam integrar um grupo
                    RequestUsersOrGroupsTCP requestUsersOrGroupsTCP = (RequestUsersOrGroupsTCP) obj;
                    if(requestUsersOrGroupsTCP.isRequestIsPendingInvites()){
                        pendingInvitesListUsers = requestUsersOrGroupsTCP.getFriendsRequests();
                        pendingInvitesListGroups = requestUsersOrGroupsTCP.getGroupsRequests();
                        firePropertyChangeListener(UPDATE_REQUESTS);
                    }
                    else{
                        if (requestUsersOrGroupsTCP.isRequestIsGroupList()) { //Cliente pediu Grupos
                            availableGroupsList = requestUsersOrGroupsTCP.getUserOrGroupList();
                        }
                        else{ //Cliente pediu Users
                            availableUsersList = requestUsersOrGroupsTCP.getUserOrGroupList();
                        }
                    }
                }

                if (obj instanceof UpdateContactListTCP) { //Actualiza lista contactos
                    UpdateContactListTCP updateContactListTCP = (UpdateContactListTCP) obj;
                    contactList = updateContactListTCP.getContactList();
                }

                if (obj instanceof UpdateMessageListTCP) { //Actualiza mensagens
                    UpdateMessageListTCP updateMessageListTCP = (UpdateMessageListTCP) obj;
                    if(updateMessageListTCP.getIsGroup()) {
                        selectedContactIsGroup = updateMessageListTCP.getIsGroup();
                        selectedGroupIsAdmin = updateMessageListTCP.getIsAdmin();
                    }
                    else{
                        selectedContactIsGroup = false;
                        selectedGroupIsAdmin = false;
                    }
                    firePropertyChangeListener(HIDE_SHOW_BUTTONS);
                    if(!selectedContact.isEmpty()) {
                        if (Objects.equals(selectedContact, updateMessageListTCP.getContact())) {
                            msgList = updateMessageListTCP.getMessageList();
                        }
                    }
                    addAsterisk(((UpdateMessageListTCP) obj).getContactsWithUnreadMessages());
                }

                if (obj instanceof FileMessageTCP) { //Actualiza mensagens
                    long fileS = ((FileMessageTCP) obj).getFileSize();
                    int cont = 0;
                    InputStream in = cs.getServerSocket().getInputStream();
                    FileOutputStream localFileOutputStream = new FileOutputStream(FILES_FOLDER_PATH + "\\" + ((FileMessageTCP) obj).getFilename());

                    do {
                        nBytes = in.read(buffer);
                        cont = cont + nBytes;
                        if (nBytes > 0) { //porque pode vir nBytes = -1
                            localFileOutputStream.write(buffer, 0, nBytes);
                        }
                    } while (cont != fileS);

                    localFileOutputStream.close();
                }

                if (obj instanceof GroupManagementTCP) { //Actualiza Gestão de Grupo
                    GroupManagementTCP groupManagementTCP = (GroupManagementTCP) obj;
                    if(groupManagementTCP.isConsulting()){
                        selectedGroupMembersList = groupManagementTCP.getgroupMembersList();
                        firePropertyChangeListener(VIEW_GROUP_MEMBERS);
                    }
                    if(groupManagementTCP.isEditing()){
                        if(groupManagementTCP.getEditingSuccess())
                            firePropertyChangeListener(GROUP_EDIT_SUCCESSFUL);
                        else
                            firePropertyChangeListener(GROUP_EDIT_NOT_SUCCESSFUL);
                    }
                    if(groupManagementTCP.isCreating()){
                        if(groupManagementTCP.getCreatingSuccess())
                            firePropertyChangeListener(GROUP_CREATING_SUCCESSFUL);
                        else
                            firePropertyChangeListener(GROUP_CREATING_NOT_SUCCESSFUL);
                    }
                    if(groupManagementTCP.isDeleting()){
                        if(groupManagementTCP.getDeletingSuccess())
                            firePropertyChangeListener(GROUP_DELETING_SUCCESSFUL);
                    }
                    if(groupManagementTCP.isExcluding()){
                        if(groupManagementTCP.getExcludingSuccess())
                            firePropertyChangeListener(GROUP_EXCLUDING_SUCCESSFUL);
                    }
                }

                if (obj instanceof UserManagementTCP) { //Edita perfil
                    UserManagementTCP userManagementTCP = (UserManagementTCP) obj;
                    if(userManagementTCP.getEditSuccessful()) {
                        username = userManagementTCP.getUsername();
                        firePropertyChangeListener(USER_EDIT_SUCCESSFUL);
                    }
                    else
                        firePropertyChangeListener(USER_EDIT_NOT_SUCCESSFUL);
                }

                if (obj instanceof FriendOrGroupRequestTCP) { //Adicionou user ou grupo
                    FriendOrGroupRequestTCP friendOrGroupRequestTCP = (FriendOrGroupRequestTCP) obj;
                    if(friendOrGroupRequestTCP.getFriendRequest())
                        availableUsersList.remove(friendOrGroupRequestTCP.getFriend());
                    else
                        availableGroupsList.remove(friendOrGroupRequestTCP.getGroup());
                    requestUserList();
                    firePropertyChangeListener(FRIEND_REQUEST);
                }

                if (obj instanceof AcceptOrRefuseRequestTCP) { //Adicionou user ou grupo
                    AcceptOrRefuseRequestTCP acceptOrRefuseRequestTCP = (AcceptOrRefuseRequestTCP) obj;
                    //if(acceptOrRefuseRequestTCP.getAccept()){ //foi aceite
                    if(acceptOrRefuseRequestTCP.getGroup()) //num grupo
                        pendingInvitesListGroups.remove(acceptOrRefuseRequestTCP.getRequest());
                    else //num contacto
                        pendingInvitesListUsers.remove(acceptOrRefuseRequestTCP.getRequest());
                    //}
                    firePropertyChangeListener(UPDATE_REQUESTS);
                    updateContactList();
                    requestMessages();
                }

                firePropertyChangeListener(VIEW_CHANGED);

            } catch (IOException | ClassNotFoundException e) {
                cs.closeServerSocket();
                cs.connectGRDS();
                oout = cs.getOout();
                oin = cs.getOin();
            }
        }
    }

    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
    }

    private void firePropertyChangeListener(String view) {
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(100), evt -> {
            propertyChangeSupport.firePropertyChange(view, null, null);
        }));
        timeline.setCycleCount(1);
        timeline.play();
    }

    public void setSelectedContact(String selectedContact) { this.selectedContact = selectedContact; }

    public String getClientName() { return name; }

    public String getUsername() { return username; }

    public Boolean getLoggedIn() { return loggedIn; }

    public ArrayList<String> getContactList() { return contactList; }

    public ArrayList<MessageList> getMessageList() { return msgList; }

    public ArrayList<String> getAvailableUsersList() { return availableUsersList; }

    public ArrayList<String> getAvailableGroupsList() { return availableGroupsList; }

    public ArrayList<String> getSelectedGroupMembersList() { return selectedGroupMembersList; }

    public ArrayList<String> getPendingInvitesListUsers() { return pendingInvitesListUsers; }

    public ArrayList<String[]> getPendingInvitesListGroups() { return pendingInvitesListGroups; }

    public boolean getContactIsGroup() { return selectedContactIsGroup; }

    public boolean getSelectedGroupIsAdmin() { return selectedGroupIsAdmin; }

    public void register(String name, String username, String password) {
        try{
            RegisterMessageTCP registerMessageTCP = new RegisterMessageTCP(name, username, password);
            oout.writeObject(registerMessageTCP);
            oout.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void login(String username, String password) { //TODO evitar no servidor que possa haver 2 logins com o mesmo utilizador
        try{
            LoginMessageTCP loginMessageTCP = new LoginMessageTCP(username, password);
            oout.writeObject(loginMessageTCP);
            oout.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateContactList() {
        try{
            UpdateContactListTCP updateContactListTCP = new UpdateContactListTCP(getUsername());
            oout.writeObject(updateContactListTCP);
            oout.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void requestMessages() {
        try{
            UpdateMessageListTCP updateMessageListTCP = new UpdateMessageListTCP(username, selectedContact);
            oout.writeObject(updateMessageListTCP);
            oout.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendDirectMessage(String message) {
        try{
            DirectMessageTCP directMessageTCP = new DirectMessageTCP(username, message, selectedContact);
            oout.writeObject(directMessageTCP);
            oout.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendGroupMessage(String message) {
        try{
            GroupMessageTCP groupMessageTCP = new GroupMessageTCP(username, message, selectedContact);
            oout.writeObject(groupMessageTCP);
            oout.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteContact(String selectedContact) {
        try{
            DeleteContactTCP deleteContactTCP = new DeleteContactTCP(username, selectedContact);
            oout.writeObject(deleteContactTCP);
            oout.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void eraseMessage(int selectedIndex) {
        try{
            EraseMessageOrFileTCP eraseMessageOrFileTCP = new EraseMessageOrFileTCP(selectedIndex, username, selectedContact, selectedContactIsGroup, false);
            oout.writeObject(eraseMessageOrFileTCP);
            oout.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void eraseFile(String selectedFile, int selectedIndex) {
        try{
            EraseMessageOrFileTCP eraseMessageOrFileTCP = new EraseMessageOrFileTCP(selectedIndex, username, selectedContact, selectedContactIsGroup, true);
            eraseMessageOrFileTCP.setFileName(selectedFile);
            oout.writeObject(eraseMessageOrFileTCP);
            oout.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void leaveGroup(String selectedContact) {
        try{
            AbandonGroupTCP abandonGroupTCP = new AbandonGroupTCP( username, selectedContact);
            oout.writeObject(abandonGroupTCP);
            oout.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendDirectFile(File selectedFile) {
        byte []fileChunk = new byte[4096];
        int nBytes;
        try {
            FileMessageTCP fileMessageTCP = new FileMessageTCP(username, selectedContact, selectedFile.length(), selectedFile.getName());
            fileMessageTCP.setUploading(true);
            fileMessageTCP.setSelectedContactIsGroup(selectedContactIsGroup);

            oout.writeObject(fileMessageTCP);
            oout.flush();
            OutputStream fileOut = cs.getServerSocket().getOutputStream();
            FileInputStream fileInputStream = new FileInputStream(selectedFile.getCanonicalPath());
            do {
                nBytes = fileInputStream.read(fileChunk);
                System.out.println("Documento tem nBytes = " + nBytes);
                if (nBytes != -1) {// enquanto não é EOF
                    fileOut.write(fileChunk, 0, nBytes);
                    fileOut.flush();
                }
            } while (nBytes > 0);
            fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void downloadFile(String fileName) {
        try{
            FileMessageTCP fileMessageTCP = new FileMessageTCP(username, selectedContact, 0, fileName);
            fileMessageTCP.setDownload(true);
            oout.writeObject(fileMessageTCP);
            oout.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void requestUserList() {
        try{
            RequestUsersOrGroupsTCP requestUsersOrGroupsTCP = new RequestUsersOrGroupsTCP(username, false);
            oout.writeObject(requestUsersOrGroupsTCP);
            oout.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void requestGroupList() {
        try{
            RequestUsersOrGroupsTCP requestUsersOrGroupsTCP = new RequestUsersOrGroupsTCP(username, true);
            oout.writeObject(requestUsersOrGroupsTCP);
            oout.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void requestPendingInvitesList() {
        try{
            RequestUsersOrGroupsTCP requestUsersOrGroupsTCP = new RequestUsersOrGroupsTCP(username);
            oout.writeObject(requestUsersOrGroupsTCP);
            oout.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createGroup(String groupName) {
        try{
            GroupManagementTCP groupManagementTCP = new GroupManagementTCP(username, groupName);
            groupManagementTCP.setCreating(true);
            oout.writeObject(groupManagementTCP);
            oout.flush();
            updateContactList();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void editGroupName(String groupName, String newGroupName) {
        try{
            GroupManagementTCP groupManagementTCP = new GroupManagementTCP(username, groupName);
            groupManagementTCP.setEditing(true);
            groupManagementTCP.setNewGroupName(newGroupName);
            oout.writeObject(groupManagementTCP);
            oout.flush();
            updateContactList();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void banUserFromGroup(String groupName, String selectedUser) {
        try{
            GroupManagementTCP groupManagementTCP = new GroupManagementTCP(username, groupName);
            groupManagementTCP.setExcluding(true);
            groupManagementTCP.setSelectedUsername(selectedUser);
            oout.writeObject(groupManagementTCP);
            oout.flush();
            requestGroupMembersList(groupName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteGroup(String groupName) {
        try{
            GroupManagementTCP groupManagementTCP = new GroupManagementTCP(username, groupName);
            groupManagementTCP.setDeleting(true);
            oout.writeObject(groupManagementTCP);
            oout.flush();
            updateContactList();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void requestGroupMembersList(String groupName) {
        try{
            GroupManagementTCP groupManagementTCP = new GroupManagementTCP(username, groupName);
            groupManagementTCP.setConsulting(true);
            oout.writeObject(groupManagementTCP);
            oout.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void editNameUsername(String newName, String newUsername, String password, String oldUsername) {
        if(newName.isEmpty()){
            newName = name;
        }
        if(newUsername.isEmpty()){
            newUsername = username;
        }
        try{
            UserManagementTCP userManagementTCP = new UserManagementTCP(newName, newUsername, password, oldUsername);
            oout.writeObject(userManagementTCP);
            oout.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void editNameUsernamePassword(String newName, String newUsername, String password, String oldUsername, String newPassword) {
        if(newName.isEmpty()){
            newName = name;
        }
        if(newUsername.isEmpty()){
            newUsername = username;
        }
        try{
            UserManagementTCP userManagementTCP = new UserManagementTCP(newName, newUsername, password, oldUsername);
            userManagementTCP.setNewPassword(newPassword);
            userManagementTCP.setAlteringPassword(true);
            oout.writeObject(userManagementTCP);
            oout.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addUser(String selectedUser) {
        try{
            FriendOrGroupRequestTCP friendOrGroupRequest = new FriendOrGroupRequestTCP(username);
            friendOrGroupRequest.setFriend(selectedUser);
            friendOrGroupRequest.setFriendRequest(true);
            oout.writeObject(friendOrGroupRequest);
            oout.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void joinGroup(String selectedGroup) {
        try{
            FriendOrGroupRequestTCP friendOrGroupRequest = new FriendOrGroupRequestTCP(username);
            friendOrGroupRequest.setGroup(selectedGroup);
            friendOrGroupRequest.setGroupRequest(true);
            oout.writeObject(friendOrGroupRequest);
            oout.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void acceptFriendRequest(String contact) {
        try{
            AcceptOrRefuseRequestTCP acceptOrRefuseRequestTCP = new AcceptOrRefuseRequestTCP(username, contact, false, true);
            oout.writeObject(acceptOrRefuseRequestTCP);
            oout.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void acceptNewMember(String member, String groupName) {
        try{
            AcceptOrRefuseRequestTCP acceptOrRefuseRequestTCP = new AcceptOrRefuseRequestTCP(username, member, true, true);
            acceptOrRefuseRequestTCP.setGroupName(groupName);
            oout.writeObject(acceptOrRefuseRequestTCP);
            oout.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void refuseFriendRequest(String contact) {
        try{
            AcceptOrRefuseRequestTCP acceptOrRefuseRequestTCP = new AcceptOrRefuseRequestTCP(username, contact, false, false);
            oout.writeObject(acceptOrRefuseRequestTCP);
            oout.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void refuseNewMember(String member, String groupName) {
        try{
            AcceptOrRefuseRequestTCP acceptOrRefuseRequestTCP = new AcceptOrRefuseRequestTCP(username, member, true, false);
            acceptOrRefuseRequestTCP.setGroupName(groupName);
            oout.writeObject(acceptOrRefuseRequestTCP);
            oout.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addAsterisk(ArrayList<String> contacts) { //TODO grupo não fica com asterisco porquê?
        String temp;
        for(int i = 0; i < contactList.size(); i++){
            for(int j = 0; j < contacts.size(); j++){
                if(Objects.equals(contactList.get(i), contacts.get(j))){
                    temp = contactList.get(i);
                    temp = temp + "*";
                    contactList.set(i, temp);
                }
            }
        }
    }

    public void removeAsterisk(String selectedContact) {
        String temp;
        if(selectedContact.substring(selectedContact.length() - 1).equals("*")){ //remover o *
            this.selectedContact = selectedContact.substring(0, selectedContact.length() - 1);
        }
        for(int i = 0; i < contactList.size(); i++){
            if(contactList.get(i).substring(contactList.get(i).length() - 1).equals("*")) {
                temp = contactList.get(i).substring(0, contactList.get(i).length() - 1);
                contactList.set(i, temp);
            }
        }
    }

}