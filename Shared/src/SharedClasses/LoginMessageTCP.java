package SharedClasses;

import java.io.Serializable;

public class LoginMessageTCP implements Serializable {

    public static final long serialVersionID = 6;
    String username;
    String password;

    public void setUsername(String username) {
        this.username = username;
    }
}