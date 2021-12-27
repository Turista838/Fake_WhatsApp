package SharedClasses;

import java.io.Serializable;

public class UserManagementTCP implements Serializable {

    public static final long serialVersionID = 12;

    private String name;
    private String username;
    private String password;
    private String newPassword;
    private Boolean alteringPassword = false;
    private Boolean editSuccessful = false;

    public UserManagementTCP(String name, String username, String password){
        this.name = name;
        this.username = username;
        this.password = password;
    }

    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }

    public void setAlteringPassword(Boolean alteringPassword) { this.alteringPassword = alteringPassword; }

    public String getUsername() { return username; }

    public String getPassword() { return password; }

    public Boolean getAlteringPassword() { return alteringPassword; }

    public Boolean getEditSuccessful() { return editSuccessful; }

    public void setEditSuccessful(Boolean editSuccessful) { this.editSuccessful = editSuccessful; }
}
