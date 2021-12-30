package SharedClasses;

import java.io.Serializable;

public class DeleteContactTCP implements Serializable {

    public static final long serialVersionID = 5;

    private String username;
    private String selectedContact;

    public DeleteContactTCP(String username, String selectedContact){
        this.username = username;
        this.selectedContact = selectedContact;
    }

    public String getUsername() { return username; }

    public String getSelectedContact() { return selectedContact; }

}
