package SharedClasses;

import java.io.Serializable;
import java.sql.Timestamp;


public class EraseMessageOrFileTCP implements Serializable {

    public static final long serialVersionID = 16;

    private Timestamp messageDate;
    private String username;
    private String contact;
    private Boolean isGroup;
    private Boolean isFile;
    private String fileName;

    public EraseMessageOrFileTCP(Timestamp messageIndex, String username, String contact, Boolean isGroup, Boolean isFile){
        this.messageDate = messageIndex;
        this.username = username;
        this.contact = contact;
        this.isGroup = isGroup;
        this.isFile = isFile;
    }

    public Timestamp getMessageDate() { return messageDate; }

    public String getUsername() { return username; }

    public String getContact() { return contact; }

    public Boolean getGroup() { return isGroup; }

    public Boolean getFile() { return isFile; }

    public String getFileName() { return fileName; }

    public void setFileName(String fileName) { this.fileName = fileName; }
}
