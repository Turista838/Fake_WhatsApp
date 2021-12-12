package SharedClasses;

import java.io.Serializable;
import java.net.InetAddress;

public class DirectMessageTCP implements Serializable {

    public static final long serialVersionID = 1;
    String sender;
    String chatMessage;
    String destination;

    public DirectMessageTCP(String sender, String chatMessage, String destination){
        this.sender = sender;
        this.chatMessage = chatMessage;
        this.destination = destination;
    }

    public String getChatMessage() {
        return chatMessage;
    }

    public String getSender() {
        return sender;
    }

    public String getDestination() {
        return destination;
    }
}
