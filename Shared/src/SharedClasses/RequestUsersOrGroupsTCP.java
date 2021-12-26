package SharedClasses;

import java.io.Serializable;
import java.util.ArrayList;

public class RequestUsersOrGroupsTCP implements Serializable { //request for: All users in the system / All groups in the System / All contacts that can be invited in a group

    public static final long serialVersionID = 10;

    private String username;
    private ArrayList userOrGroupList;
    private boolean requestIsGroupList;

    public RequestUsersOrGroupsTCP(String username, boolean requestIsGroupList){
        this.username = username;
        userOrGroupList = new ArrayList<String>();
        this.requestIsGroupList = requestIsGroupList;
    }

    public String getUsername() { return username; }

    public void addUserOrGroupName(String userOrGroupName){
        userOrGroupList.add(userOrGroupName);
    }

    public boolean isRequestIsGroupList() { return requestIsGroupList; }

    public ArrayList getUserOrGroupList() { return userOrGroupList; }
}
