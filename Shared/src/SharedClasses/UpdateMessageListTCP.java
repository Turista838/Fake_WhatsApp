package SharedClasses;

import SharedClasses.Data.MessageList;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;

public class UpdateMessageListTCP implements Serializable {

    public static final long serialVersionID = 9;

    private String username;
    private String contact;
    private Boolean isGroup;
    private Boolean isAdmin;
    private ArrayList msgList;
    private ArrayList contactsWithUnreadMessages;

    public UpdateMessageListTCP(String username, String contact){
        this.username = username;
        this.contact = contact;
        msgList = new ArrayList<MessageList>();
        contactsWithUnreadMessages = new ArrayList<String>();
    }

    public void setUsername(String username) { this.username = username; }

    public void setContact(String contact) { this.contact = contact; }

    public String getUsername() {
        return username;
    }

    public String getContact() {
        return contact;
    }

    public void addMsgList(String origin, String message, Timestamp timestamp, boolean seen, boolean file) {
        msgList.add(new MessageList(origin, message, timestamp, seen, file));
    }

    public void addContactsWithUnreadMessages(String contact) {
        contactsWithUnreadMessages.add(contact);
    }

    public ArrayList getMessageList() {
        return msgList;
    }

    public ArrayList getContactsWithUnreadMessages() { return contactsWithUnreadMessages; }

    public void setIsGroup(Boolean group) {
        isGroup = group;
    }

    public Boolean getIsGroup() {
        return isGroup;
    }

    public void setIsAdmin(Boolean admin) {
        isAdmin = admin;
    }

    public Boolean getIsAdmin() {
        return isAdmin;
    }
}