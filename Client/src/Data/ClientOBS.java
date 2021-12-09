package Data;

import SharedClasses.Data.MessageList;
import SharedClasses.LoginMessageTCP;
import SharedClasses.RegisterMessageTCP;
import SharedClasses.UpdateContactListTCP;
import SharedClasses.UpdateMessageListTCP;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Alert;
import javafx.util.Duration;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

//class ClientOBSThread extends Thread{
//
//    private Client client;
//    private final PropertyChangeSupport propertyChangeSupport;
//    private ObjectOutputStream ooutCli; //enviar
//    private ObjectInputStream oinCli; //receber
//
//
//    public ClientOBSThread(ObjectOutputStream oout, ObjectInputStream oin, Client client, PropertyChangeSupport propertyChangeSupport) {
//        this.client = client;
//        this.ooutCli = oout;
//        this.oinCli = oin;
//        this.propertyChangeSupport = propertyChangeSupport;
//    }
//
//    public void run(){
//        while(true){
//            try {
//                System.out.println("cenas");
//
//                Object obj = oinCli.readObject();
//
//                if (obj == null) { //EOF
//                    return;
//                }
//
//                if (obj instanceof LoginMessageTCP) { //Actualiza mensagens
//                    LoginMessageTCP loginMessageTCP = (LoginMessageTCP) oinCli.readObject();
//                    client.setLoggedIn(loginMessageTCP.getLoginStatus());
//                    if(client.getLoggedIn()) {
//                        client.setUsername(loginMessageTCP.getUsername());
//                        client.setLoggedIn(true);
//                        client.updateContactList();
//                        firePropertyChangeListener();
//                        //return true;
//                    }
//                    else{
//                        System.out.println(client.getLoggedIn());
//                        //return false;
//                    }
//                }
//
//                if (obj instanceof UpdateContactListTCP) { //Actualiza mensagens
//                    UpdateContactListTCP updateContactListTCP = (UpdateContactListTCP) oinCli.readObject();
//                    client.setContactList(updateContactListTCP.getContactList());
//                }
//
//                if (obj instanceof UpdateMessageListTCP) { //Actualiza mensagens
//                    UpdateMessageListTCP updateMessageListTCP = (UpdateMessageListTCP) obj;
//
//                    //if selected contact == atualizar, senão, por um asterisco no contacto?
//                    client.updateMsgList(updateMessageListTCP.getMessageList());
//                    firePropertyChangeListener();
//                    //msgList =
//                }
//
//            } catch (IOException | ClassNotFoundException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    private void firePropertyChangeListener() {
//        propertyChangeSupport.firePropertyChange(ClientOBS.VIEW_CHANGED, null, null);
//    }
//}

public class ClientOBS extends Thread {

    private Client client;
    //private ClientOBSThread clientOBSThread;
    private final PropertyChangeSupport propertyChangeSupport;
    public final static String VIEW_CHANGED = "View Changed";
    private ObjectOutputStream ooutCli; //enviar
    private ObjectInputStream oinCli; //receber

    public ClientOBS(Client cl){
        client = cl;
        propertyChangeSupport = new PropertyChangeSupport(client);
        //clientOBSThread = new ClientOBSThread(cl.getOout(), cl.getOin(), client, propertyChangeSupport);
        oinCli =  cl.getOin();
        //clientOBSThread.start();
        this.start();
    }

    public void run(){
        while(true){
            try {


                Object obj = oinCli.readObject();

                if (obj == null) { //EOF
                    return;
                }

                if (obj instanceof LoginMessageTCP) { //Actualiza mensagens
                    LoginMessageTCP loginMessageTCP = (LoginMessageTCP)obj;
                    client.setLoggedIn(loginMessageTCP.getLoginStatus());
                    System.out.println("entrei login message");
                    if(client.getLoggedIn()) {
                        client.setUsername(loginMessageTCP.getUsername());
                        client.setLoggedIn(true);
                        client.updateContactList();
                        //firePropertyChangeListener();
                        //return true;
                    }
                    else{
                        System.out.println(client.getLoggedIn());
                        //return false;
                    }
                }

                if (obj instanceof UpdateContactListTCP) { //Actualiza contactos
                    UpdateContactListTCP updateContactListTCP = (UpdateContactListTCP)obj;
                    client.setContactList(updateContactListTCP.getContactList());
                    //firePropertyChangeListener();
                }

                if (obj instanceof UpdateMessageListTCP) { //Actualiza mensagens
                    UpdateMessageListTCP updateMessageListTCP = (UpdateMessageListTCP) obj;
                    client.setSelectedContactIsGroup(updateMessageListTCP.getIsGroup());

                    //TODO if selected contact == atualizar, senão, por um asterisco no contacto?
                    client.updateMsgList(updateMessageListTCP.getMessageList());
                    //firePropertyChangeListener();
                    //msgList =
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

    public boolean getClientStatus() {
        return client.getLoggedIn();
    }

    public ArrayList<String> getContactList(){
        return client.getContactList();
    }

    public ArrayList<MessageList> getMessageList() { return client.getMessageList(); }

    public boolean getContactIsGroup() {
        return client.getContactIsGroup();
    }

    public void login(String username, String password) {
        if(!client.login(username, password)) {

        }
        else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText("Look, an Error Dialog");
            alert.setContentText("Incorrect Username and/or Password");
            alert.showAndWait();
        }
        //firePropertyChangeListener();
    }

    public void requestMessages(String selectedContact) {
        client.requestMessages(selectedContact);
        //firePropertyChangeListener();
    }

    public void sendDirectMessage(String message, String selectedContact) {
        client.sendDirectMessage(message, selectedContact);
        //firePropertyChangeListener();
    }


}
