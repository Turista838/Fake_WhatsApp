package Data;

import SharedClasses.Data.MessageList;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;

public class ClientOBS {

    private Client client;
    private final PropertyChangeSupport propertyChangeSupport;
    public final static String STATE_CHANGED = "visibility state changed";

    public ClientOBS(Client cl){
        client = cl;
        propertyChangeSupport = new PropertyChangeSupport(client);
    }

    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
    }

    private void firePropertyChangeListener() {
        propertyChangeSupport.firePropertyChange(STATE_CHANGED, null, null);
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
        client.login(username, password);
        firePropertyChangeListener();
    }

    public void requestMessages(String selectedContact) {
        client.requestMessages(selectedContact);
        firePropertyChangeListener();
    }

    public void sendDirectMessage(String message, String selectedContact) {
        client.sendDirectMessage(message, selectedContact);
        firePropertyChangeListener();
    }


}
