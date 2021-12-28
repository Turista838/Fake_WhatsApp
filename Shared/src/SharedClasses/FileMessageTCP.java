package SharedClasses;

import java.io.Serializable;

public class FileMessageTCP implements Serializable {

    public static final long serialVersionID = 15;

    private long fileSize;
    private String filename;
    private String filePath;

    public FileMessageTCP(long fileSize, String fileName){
        this.fileSize = fileSize;
        this.filename = fileName;
    }

    public long getFileSize() { return fileSize; }

    public String getFilename() { return filename; }
}
