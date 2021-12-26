package SharedClasses;

import java.io.Serializable;
import java.util.ArrayList;

public class GroupManagementTCP implements Serializable {

    public static final long serialVersionID = 11;

    private String username;
    private String groupName;
    private String newGroupName;
    private String selectedUsername;
    private ArrayList groupMembersList;

    private boolean isCreating = false; //criar grupo
    private boolean isEditing = false; //editar nome do grupo
    private boolean isConsulting = false; //consultar os membros do grupo
    private boolean isExcluding = false; //excluir um membro do grupo
    private boolean isDeleting = false; //apagar o grupo

    private boolean creatingSuccess = false; //criar grupo
    private boolean editingSuccess = false; //editar nome do grupo
    private boolean excludingSuccess = false; //excluir um membro do grupo
    private boolean deletingSuccess = false; //apagar o grupo

    public GroupManagementTCP(String username, String groupName){
        this.username = username;
        this.groupName = groupName;
        groupMembersList = new ArrayList<String>();
    }

    public void setNewGroupName(String newGroupName) { this.newGroupName = newGroupName; }

    public void setSelectedUsername(String selectedUsername) { this.selectedUsername = selectedUsername; }

    public void setCreating(boolean creating) { isCreating = creating; }

    public void setEditing(boolean editing) { isEditing = editing; }

    public void setConsulting(boolean consulting) { isConsulting = consulting; }

    public void setExcluding(boolean excluding) { isExcluding = excluding; }

    public void setDeleting(boolean deleting) { isDeleting = deleting; }

    public boolean isCreating() { return isCreating; }

    public boolean isEditing() { return isEditing; }

    public boolean isConsulting() { return isConsulting; }

    public boolean isExcluding() { return isExcluding; }

    public boolean isDeleting() { return isDeleting; }

    public String getUsername() { return username; }

    public String getGroupName() { return groupName; }

    public String getNewGroupName() { return newGroupName; }

    public String getSelectedUsername() { return selectedUsername; }

    public void addGroupMember(String contact){
        groupMembersList.add(contact);
    }

    public ArrayList getgroupMembersList() { return groupMembersList; }

    public void setCreatingSuccess(boolean creatingSuccess) { this.creatingSuccess = creatingSuccess; }

    public void setEditingSuccess(boolean editingSuccess) { this.editingSuccess = editingSuccess; }

    public void setExcludingSuccess(boolean excludingSuccess) { this.excludingSuccess = excludingSuccess; }

    public void setDeletingSuccess(boolean deletingSuccess) { this.deletingSuccess = deletingSuccess; }

    public boolean getCreatingSuccess() { return creatingSuccess; }

    public boolean getEditingSuccess() { return editingSuccess; }

    public boolean getExcludingSuccess() { return excludingSuccess; }

    public boolean getDeletingSuccess() { return deletingSuccess; }

}
