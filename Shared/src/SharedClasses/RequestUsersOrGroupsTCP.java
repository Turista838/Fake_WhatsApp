package SharedClasses;

import java.io.Serializable;
import java.security.KeyPair;
import java.util.ArrayList;
import java.util.HashMap;

public class RequestUsersOrGroupsTCP implements Serializable { //request for: All users in the system / All groups in the System / All contacts that can be invited in a group

    public static final long serialVersionID = 10;

    private String username;
    private ArrayList userOrGroupList;
    private ArrayList friendsRequests;
    private HashMap groupsRequests;
    private boolean requestIsGroupList;
    private boolean requestIsPendingInvites;

    public RequestUsersOrGroupsTCP(String username, boolean requestIsGroupList){
        this.username = username;
        userOrGroupList = new ArrayList<String>();
        this.requestIsGroupList = requestIsGroupList;
    }

    public RequestUsersOrGroupsTCP(String username){ // Request Pending Invites List
        this.username = username;
        friendsRequests = new ArrayList<String>();
        groupsRequests = new HashMap<String, String>();
        requestIsPendingInvites = true;
    }

    public String getUsername() { return username; }

    public void addUserOrGroupName(String userOrGroupName){
        userOrGroupList.add(userOrGroupName);
    }

    public void addFriendsRequests(String user){
        friendsRequests.add(user);
    }

    public void addGroupsRequests(String user, String groupName){ groupsRequests.put(user, groupName); }

    public boolean isRequestIsGroupList() { return requestIsGroupList; }

    public boolean isRequestIsPendingInvites() { return requestIsPendingInvites; }

    public ArrayList getUserOrGroupList() { return userOrGroupList; }

    public ArrayList getFriendsRequests() { return friendsRequests; }

    public HashMap getGroupsRequests() { return groupsRequests; }
}
