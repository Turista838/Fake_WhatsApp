package SharedClasses;

import java.io.Serializable;

public class EraseMessageOrFileTCP implements Serializable {

    public static final long serialVersionID = 16;

    private int messageIndex;
    private String username;
    private String contact;
    private Boolean isGroup;
    private Boolean isFile;
    private String fileName;

    public EraseMessageOrFileTCP(int messageIndex, String username, String contact, Boolean isGroup, Boolean isFile){
        this.messageIndex = messageIndex;
        this.username = username;
        this.contact = contact;
        this.isGroup = isGroup;
        this.isFile = isFile;
    }

    public int getMessageIndex() {
        return messageIndex;
    }

    public String getUsername() {
        return username;
    }

    public String getContact() {
        return contact;
    }

    public Boolean getGroup() {
        return isGroup;
    }

    public Boolean getFile() {
        return isFile;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
