package SharedClasses;

import java.io.Serializable;

public class UserManagementTCP implements Serializable {

    public static final long serialVersionID = 12;

    private String name;
    private String username;
    private String password;
    private String newPassword;
    private String oldUsername;
    private Boolean alteringPassword = false;
    private Boolean editSuccessful = false;

    public UserManagementTCP(String name, String username, String password, String oldUsername){
        this.name = name;
        this.username = username;
        this.password = password;
        this.oldUsername = oldUsername;
    }

    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }

    public void setAlteringPassword(Boolean alteringPassword) { this.alteringPassword = alteringPassword; }

    public String getUsername() { return username; }

    public String getName() { return name; }

    public String getOldUsername() { return oldUsername; }

    public String getPassword() { return password; }

    public String getNewPassword() { return newPassword; }

    public Boolean getAlteringPassword() { return alteringPassword; }

    public Boolean getEditSuccessful() { return editSuccessful; }

    public void setEditSuccessful(Boolean editSuccessful) { this.editSuccessful = editSuccessful; }
}
