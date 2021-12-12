package SharedClasses;

import java.io.Serializable;

public class GroupMessageTCP implements Serializable {

    public static final long serialVersionID = 2;
    String sender;
    String chatMessage;
    String group;

    public GroupMessageTCP(String sender, String chatMessage, String group){
        this.sender = sender;
        this.chatMessage = chatMessage;
        this.group = group;
    }

    public String getChatMessage() {
        return chatMessage;
    }

    public String getSender() {
        return sender;
    }

    public String getGroup() {
        return group;
    }
}
