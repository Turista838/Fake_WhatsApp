package SharedClasses;

import SharedClasses.Data.MessageList;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;

public class UpdateMessageListTCP implements Serializable {

    public static final long serialVersionID = 8;

    private String username;
    private String contact;
    private Boolean isGroup;
    private ArrayList msgList;

    public UpdateMessageListTCP(String username, String contact){
        this.username = username;
        this.contact = contact;
        msgList = new ArrayList<MessageList>();
    }

    public String getUsername() {
        return username;
    }

    public String getContact() {
        return contact;
    }

    public void addMsgList(String message, Timestamp timestamp, boolean seen, boolean file) {
        msgList.add(new MessageList(message, timestamp, seen, file));
    }

    public ArrayList getMessageList() {
        return msgList;
    }

    public void setIsGroup(Boolean group) {
        isGroup = group;
    }

    public Boolean getIsGroup() {
        return isGroup;
    }
}