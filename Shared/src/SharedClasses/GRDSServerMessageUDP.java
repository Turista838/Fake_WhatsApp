package SharedClasses;

import java.io.Serializable;
import java.util.ArrayList;

public class GRDSServerMessageUDP implements Serializable {

    public static final long serialVersionID = 4;
    private boolean updateBDconnection = false;
    private boolean handleFiles = false;
    private String message;
    private ArrayList clientsAffectedBySGBDChanges;
    private ArrayList filesList;
    private String fileServerIp;
    private int fileServerPort;

    public GRDSServerMessageUDP(Boolean updateBDconnection, Boolean handleFiles){
        this.updateBDconnection = updateBDconnection;
        this.handleFiles = handleFiles;
        clientsAffectedBySGBDChanges = new ArrayList<String>();
        filesList = new ArrayList<String>();
    }

    public void setMessage(String message) { this.message = message; }

    public String getMessage() { return message; }

    public boolean isUpdateBDconnection() { return updateBDconnection; }

    public boolean notifyServersToDownloadFiles() { return handleFiles; }

    public void setClientsAffectedBySGBDChanges(ArrayList clientsAffectedBySGBDChanges) { this.clientsAffectedBySGBDChanges = clientsAffectedBySGBDChanges; }

    public ArrayList getClientsAffectedBySGBDChanges() { return clientsAffectedBySGBDChanges; }

    public void setFilesList(ArrayList filesList) { this.filesList = filesList; }

    public ArrayList getFilesList() { return filesList; }

    public void setServerTCPData(String fileServerIp, int fileServerPort) {
        this.fileServerIp = fileServerIp;
        this.fileServerPort = fileServerPort;
    }

    public String getFileServerIp() { return fileServerIp; }

    public int getFileServerPort() { return fileServerPort; }

}
