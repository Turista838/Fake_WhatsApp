package SharedClasses;

import java.io.Serializable;

public class AbandonGroupTCP implements Serializable {

    public static final long serialVersionID = 17;
    private String username;
    private String groupName;

    public AbandonGroupTCP(String username, String groupName){
        this.username = username;
        this.groupName = groupName;
    }

    public String getUsername() { return username; }

    public String getGroupName() { return groupName; }
}
