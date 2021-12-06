package SharedClasses.Data;

import java.io.Serializable;
import java.sql.Timestamp;

public class MessageList implements Serializable {

    public static final long serialVersionID = 9;
    public String message;
    public Timestamp timestamp;
    public boolean seen;
    public boolean file;

    public MessageList(String message, Timestamp timestamp, boolean seen, boolean file) {
        this.message = message;
        this.timestamp = timestamp;
        this.seen = seen;
        this.file = file;
    }
}
