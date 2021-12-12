package Data;

import SharedClasses.*;
import SharedClasses.Data.MessageList;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Alert;
import javafx.util.Duration;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Objects;


public class ClientManager extends Thread {

    private ClientStartup cs;

    private Boolean loggedIn = false;
    private Boolean selectedContactIsGroup = false;
    private String selectedContact;
    private ArrayList<String> contactList;
    private ArrayList<MessageList> msgList;
    private String username;

    private ObjectOutputStream oout; //enviar TCP
    private ObjectInputStream oin; //receber TCP

    private final PropertyChangeSupport propertyChangeSupport;
    public final static String VIEW_CHANGED = "View Changed";

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
        while(true){
            try {

                Object obj = oin.readObject();

                if (obj == null) { //EOF
                    return;
                }

                if (obj instanceof LoginMessageTCP) { //Actualiza mensagens
                    LoginMessageTCP loginMessageTCP = (LoginMessageTCP)obj;
                    loggedIn = loginMessageTCP.getLoginStatus();
                    System.out.println("entrei login message");
                    if(loggedIn) {
                        username = loginMessageTCP.getUsername();
                        updateContactList();
                    }
                    else{
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error Dialog");
                        alert.setHeaderText("Look, an Error Dialog");
                        alert.setContentText("Incorrect Username and/or Password");
                        alert.showAndWait();
                    }
                }

                if (obj instanceof UpdateContactListTCP) { //Actualiza contactos
                    UpdateContactListTCP updateContactListTCP = (UpdateContactListTCP)obj;
                    contactList = updateContactListTCP.getContactList();
                }

                if (obj instanceof UpdateMessageListTCP) { //Actualiza mensagens
                    UpdateMessageListTCP updateMessageListTCP = (UpdateMessageListTCP) obj;
                    selectedContactIsGroup = updateMessageListTCP.getIsGroup();
//                    System.out.println("selected contact: " + selectedContact);
//                    System.out.println("getContact(): " +  updateMessageListTCP.getContact());
                    if(Objects.equals(selectedContact, updateMessageListTCP.getContact())){
                        msgList = updateMessageListTCP.getMessageList();
                    }
                    else{
                        addAsterisk(updateMessageListTCP.getContact());
                    }
                }

                Timeline timeline = new Timeline(new KeyFrame(Duration.millis(100), evt-> {
                    firePropertyChangeListener();
                }));
                timeline.setCycleCount(1);
                timeline.play();


            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
    }

    private void firePropertyChangeListener() {
        propertyChangeSupport.firePropertyChange(VIEW_CHANGED, null, null);
    }

    public void setSelectedContact(String selectedContact) { this.selectedContact = selectedContact; }

    public String getUsername() { return username; }

    public Boolean getLoggedIn() { return loggedIn; }

    public ArrayList<String> getContactList() { return contactList; }

    public ArrayList<MessageList> getMessageList() { return msgList; }

    public boolean getContactIsGroup() { return selectedContactIsGroup; }

    public void login(String username, String password) {
        try{
            LoginMessageTCP loginMessageTCP = new LoginMessageTCP(username, password);
            oout.writeObject(loginMessageTCP);
            oout.flush();
        } catch (IOException /*| ClassNotFoundException*/ e) {
            e.printStackTrace();
        }
    }

    public void updateContactList() {
        try{
            UpdateContactListTCP updateContactListTCP = new UpdateContactListTCP(getUsername());
            oout.writeObject(updateContactListTCP);
            oout.flush();
        } catch (IOException /*| ClassNotFoundException */ e) {
            e.printStackTrace();
        }
    }

    public void requestMessages() {
        try{
//            if(selectedContact.substring(selectedContact.length() - 1).equals("*")) //remover o *
//                removeAsterisk(selectedContact);
            UpdateMessageListTCP updateMessageListTCP = new UpdateMessageListTCP(username, selectedContact);
            oout.writeObject(updateMessageListTCP);
            oout.flush();
        } catch (IOException /*| ClassNotFoundException*/ e) {
            e.printStackTrace();
        }
    }

    public void sendDirectMessage(String message) {
        try{
            DirectMessageTCP directMessageTCP = new DirectMessageTCP(username, message, selectedContact);
            oout.writeObject(directMessageTCP);
            oout.flush();
        } catch (IOException /*| ClassNotFoundException */e) {
            e.printStackTrace();
        }
    }

    public void sendGroupMessage(String message) {
        try{
            GroupMessageTCP groupMessageTCP = new GroupMessageTCP(username, message, selectedContact);
            oout.writeObject(groupMessageTCP);
            oout.flush();
        } catch (IOException /*| ClassNotFoundException */e) {
            e.printStackTrace();
        }
    }


    public void addAsterisk(String contact) {
        String temp;
        for(int i = 0; i < contactList.size(); i++){
            if(Objects.equals(contactList.get(i), contact)){
                temp = contactList.get(i);
                temp = temp + "*";
                contactList.set(i, temp);
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
