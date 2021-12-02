package Data;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

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

    public void login(String username, String password) {
        client.login(username, password);
        firePropertyChangeListener();
    }
}
