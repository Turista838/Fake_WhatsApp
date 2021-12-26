package SharedClasses;

import java.io.Serializable;

public class LoginMessageTCP implements Serializable {

    public static final long serialVersionID = 6;
    private String name;
    private String username;
    private String password;
    private boolean connected;

    public LoginMessageTCP(String username, String password){
        this.username = username;
        this.password = password;
        connected = false;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setConnected(boolean connected) { this.connected = connected; }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean getLoginStatus() {
        return connected;
    }
}