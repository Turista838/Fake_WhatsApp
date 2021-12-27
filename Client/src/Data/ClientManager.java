package Data;

import SharedClasses.*;
import SharedClasses.Data.MessageList;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
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
    private HashMap pendingInvitesListGroups;

    private ObjectOutputStream oout; //enviar TCP
    private ObjectInputStream oin; //receber TCP

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
        while (true) {
            try {

                Object obj = oin.readObject();

                if (obj == null) { //EOF
                    return;
                }

                if (obj instanceof String) { //Única mensagem que não tem classe personalizada
                    if(obj.equals("Update Contacts")) {
                        System.out.println("Recebi STRING Update Contacts");
                        updateContactList();
                    }
                    if(obj.equals("Update Message")) {
                        System.out.println("Recebi STRING Update Message");
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
                    System.out.println("Sou " + username + " e cheguei aqui");
                    contactList = updateContactListTCP.getContactList();
                }

                if (obj instanceof UpdateMessageListTCP) { //Actualiza mensagens
                    UpdateMessageListTCP updateMessageListTCP = (UpdateMessageListTCP) obj;
                    selectedContactIsGroup = updateMessageListTCP.getIsGroup();
                    selectedGroupIsAdmin = updateMessageListTCP.getIsAdmin();
                    System.out.println("selected contact: " + selectedContact);
                    System.out.println("updateMessageListTCP.getContact(): " + updateMessageListTCP.getContact());
                    if(!selectedContact.isEmpty()) {
                        if (Objects.equals(selectedContact, updateMessageListTCP.getContact())) { //cliente está a ver as mensagens em directo
                            msgList = updateMessageListTCP.getMessageList();
                        }
                    }
                    addAsterisk(((UpdateMessageListTCP) obj).getContactsWithUnreadMessages());
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
                        if(groupManagementTCP.getEditingSuccess())
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
                    if(userManagementTCP.getEditSuccessful())
                        firePropertyChangeListener(USER_EDIT_SUCCESSFUL);
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

                System.out.println("Disparei View Changed");
                firePropertyChangeListener(VIEW_CHANGED);

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                return;
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

    public HashMap getPendingInvitesListGroups() { return pendingInvitesListGroups; }

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

    public void editNameUsername(String newName, String newUsername, String password) {
        if(newName.isEmpty()){
            newName = name;
        }
        if(newUsername.isEmpty()){
            newUsername = username;
        }
        try{
            UserManagementTCP userManagementTCP = new UserManagementTCP(newName, newUsername, password);
            oout.writeObject(userManagementTCP);
            oout.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void editNameUsernamePassword(String newName, String newUsername, String password, String newPassword) {
        if(newName.isEmpty()){
            newName = name;
        }
        if(newUsername.isEmpty()){
            newUsername = username;
        }
        try{
            UserManagementTCP userManagementTCP = new UserManagementTCP(newName, newUsername, password);
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

    public void acceptNewMember(String member) {
        try{
            AcceptOrRefuseRequestTCP acceptOrRefuseRequestTCP = new AcceptOrRefuseRequestTCP(username, member, true, true);
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

    public void refuseNewMember(String contact) {
        try{
            AcceptOrRefuseRequestTCP acceptOrRefuseRequestTCP = new AcceptOrRefuseRequestTCP(username, contact, true, false);
            oout.writeObject(acceptOrRefuseRequestTCP);
            oout.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addAsterisk(ArrayList<String> contacts) { //TODO meter cores em vez de asteriscos
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

    public void removeAsterisk(String selectedContact) { //TODO está a apagar os asteriscos todos, só devia apagar do contacto selecionado
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