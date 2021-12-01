package SharedClasses;

import java.io.Serializable;
import java.net.InetAddress;

public class DirectMessageTCP implements Serializable {

    public static final long serialVersionID = 1;
    String chatMessage;
    String usernameDestination;

    public String getChatMessage() {
        return chatMessage;
    }

    public void setChatMessage(String chatMessage) {
        this.chatMessage = chatMessage;
    }
}
