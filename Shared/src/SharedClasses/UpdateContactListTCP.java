package SharedClasses;

import java.io.Serializable;
import java.util.ArrayList;

public class UpdateContactListTCP implements Serializable {

    public static final long serialVersionID = 7;

    private String username;
    private ArrayList contactList;

    public UpdateContactListTCP(String username){
        this.username = username;
        contactList = new ArrayList<String>();
    }

    public String getUsername() {
        return username;
    }

    public void addContact(String contact){
        contactList.add(contact);
    }

    public ArrayList<String> getContactList() {
        return contactList;
    }
}
