package SharedClasses;

import java.io.Serializable;
import java.util.ArrayList;

public class GRDSServerMessageUDP implements Serializable {

    public static final long serialVersionID = 4;
    private boolean updateBDconnection = false;
    private String message;
    private ArrayList clientsAffectedBySGBDChanges;

    public GRDSServerMessageUDP(Boolean updateBDconnection){
        this.updateBDconnection = updateBDconnection;
        clientsAffectedBySGBDChanges = new ArrayList<String>();
    }

    public void setMessage(String message) { this.message = message; }

    public String getMessage() { return message; }

    public boolean isUpdateBDconnection() { return updateBDconnection; }

    public void setClientsAffectedBySGBDChanges(ArrayList clientsAffectedBySGBDChanges) { this.clientsAffectedBySGBDChanges = clientsAffectedBySGBDChanges; }

    public ArrayList getClientsAffectedBySGBDChanges() { return clientsAffectedBySGBDChanges; }
}
