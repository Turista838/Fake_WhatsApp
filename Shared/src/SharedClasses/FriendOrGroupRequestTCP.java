package SharedClasses;

import java.io.Serializable;

public class FriendOrGroupRequestTCP implements Serializable {

    public static final long serialVersionID = 13;

    private String username;
    private String friend;
    private String group;
    private Boolean friendRequest = false;
    private Boolean groupRequest = false;

    public FriendOrGroupRequestTCP(String username){
        this.username = username;
    }

    public String getUsername() { return username; }

    public String getFriend() { return friend; }

    public void setFriend(String friend) { this.friend = friend; }

    public String getGroup() { return group; }

    public void setGroup(String group) { this.group = group; }

    public Boolean getFriendRequest() { return friendRequest; }

    public void setFriendRequest(Boolean friendRequest) { this.friendRequest = friendRequest; }

    public Boolean getGroupRequest() { return groupRequest; }

    public void setGroupRequest(Boolean groupRequest) { this.groupRequest = groupRequest; }
}
