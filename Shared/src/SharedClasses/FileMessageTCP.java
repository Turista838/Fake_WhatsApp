package SharedClasses;

import java.io.Serializable;

public class FileMessageTCP implements Serializable {

    public static final long serialVersionID = 15;

    private long fileSize;
    private String filename;
    private String username;
    private String selectedContact;
    private Boolean uploading = false;
    private Boolean download = false;

    public FileMessageTCP(String username, String selectedContact, long fileSize, String fileName){
        this.username = username;
        this.selectedContact = selectedContact;
        this.fileSize = fileSize;
        this.filename = fileName;
    }

    public FileMessageTCP(long fileSize, String fileName) {
        this.fileSize = fileSize;
        this.filename = fileName;
    }

    public void setDownload(Boolean download) { this.download = download; }

    public void setUploading(Boolean uploading) { this.uploading = uploading; }

    public Boolean getDownload() { return download; }

    public Boolean getUploading() { return uploading; }

    public String getSender() { return username; }

    public String getDestination() { return selectedContact; }

    public long getFileSize() { return fileSize; }

    public String getFilename() { return filename; }
}
