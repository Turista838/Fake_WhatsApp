package SharedClasses;

import java.io.Serializable;

public class AcceptOrRefuseRequestTCP implements Serializable {

    public static final long serialVersionID = 14;

    private String username;
    private String request;
    private Boolean isGroup = false;
    private Boolean accept = false;

    public AcceptOrRefuseRequestTCP(String username, String request, Boolean isGroup, Boolean accept){
        this.username = username;
        this.request = request;
        this.isGroup = isGroup;
        this.accept = accept;
    }

    public String getUsername() { return username; }

    public String getRequest() { return request; }

    public Boolean getGroup() { return isGroup; }

    public Boolean getAccept() { return accept; }
}