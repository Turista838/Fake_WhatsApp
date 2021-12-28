package Data;

import java.io.ObjectOutputStream;
import java.util.Calendar;

public class ClientInfo {

    private String username;
    private ObjectOutputStream oout;

    public ClientInfo(String username, ObjectOutputStream oout){
        this.username = username;
        this.oout = oout;
    }

    public String getUsername() {
        return username;
    }

    public ObjectOutputStream getOout() { return oout; }
}
