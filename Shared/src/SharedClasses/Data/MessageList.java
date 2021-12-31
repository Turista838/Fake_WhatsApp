package SharedClasses.Data;

import  java.io.Serializable;
import java.sql.Timestamp;

public class MessageList implements Serializable {

    public static final long serialVersionID = 9;
    public String origin;
    public String message;
    public Timestamp timestamp;
    public boolean seen;
    public boolean file;

    public MessageList(String origin, String message, Timestamp timestamp, boolean seen, boolean file) {
        this.origin = origin;
        this.message = message;
        this.timestamp = timestamp;
        this.seen = seen;
        this.file = file;
    }
}
