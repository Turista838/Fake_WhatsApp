package Interface;

import Data.ClientManager;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import static Data.ClientManager.*;

public class EditGroupDialog extends BorderPane {

    ClientManager clientManager;
    private String selectedGroup;
    private  Stage stage;

    private Text title, editGroupNameText;
    private TextField editGroupNameField;

    private ListView currentMembersList;

    private Button editGroupNameButton;
    private Button banUserButton;
    private Button deleteGroupButton;

    private VBox mainBox;
    private HBox editGroupNameBox;
    private HBox buttonsBox;

    public EditGroupDialog(ClientManager clientManager, String selectedGroup, Stage stage){
        this.clientManager = clientManager;
        this.selectedGroup = selectedGroup;
        this.stage = stage;

        title = new Text("Edit Group");
        editGroupNameText = new Text("New Group name:");
        editGroupNameField = new TextField();

        currentMembersList = new ListView();

        editGroupNameButton = new Button("Change Group Name");
        banUserButton = new Button("Exclude User");
        deleteGroupButton = new Button("Delete Group");

        editGroupNameBox = new HBox();
        editGroupNameBox.getChildren().addAll(editGroupNameText, editGroupNameField, editGroupNameButton);

        buttonsBox = new HBox();
        buttonsBox.getChildren().addAll(banUserButton, deleteGroupButton);

        mainBox = new VBox();
        mainBox.getChildren().addAll(title, editGroupNameBox, currentMembersList, buttonsBox);
        mainBox.setAlignment(Pos.CENTER);
        setCenter(mainBox);

        clientManager.requestGroupMembersList(selectedGroup);

        editGroupNameButton.setOnAction(ev -> {
            clientManager.editGroupName(selectedGroup, editGroupNameField.getText());
        });

        banUserButton.setOnAction(ev -> {
            String selectedUser = (String) currentMembersList.getSelectionModel().getSelectedItem();
            clientManager.banUserFromGroup(selectedGroup, selectedUser);
        });

        deleteGroupButton.setOnAction(ev -> {
            clientManager.deleteGroup(selectedGroup);
        });

        clientManager.addPropertyChangeListener(VIEW_GROUP_MEMBERS, evt->requestGroupUsers());

        clientManager.addPropertyChangeListener(GROUP_EDIT_SUCCESSFUL, evt->editSuccess());

        clientManager.addPropertyChangeListener(GROUP_EDIT_NOT_SUCCESSFUL, evt->editFailed());

        clientManager.addPropertyChangeListener(GROUP_CREATING_SUCCESSFUL, evt->creatingSuccess());

        clientManager.addPropertyChangeListener(GROUP_CREATING_NOT_SUCCESSFUL, evt->creatingFailed());

        clientManager.addPropertyChangeListener(GROUP_DELETING_SUCCESSFUL, evt->deletingSuccess(stage));

        clientManager.addPropertyChangeListener(GROUP_EXCLUDING_SUCCESSFUL, evt->excludingSuccess());

    }

    private void requestGroupUsers() {
        for (String members : clientManager.getSelectedGroupMembersList()) {
            currentMembersList.getItems().add(members);
        }
    }

    private void editFailed() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Edit Failed");
        alert.setHeaderText("You cannot change to a empty name or there is already a group with the same name that you are admin of");
        alert.show();
    }

    private void editSuccess() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Group name successfully edited");
        alert.show();
    }

    private void creatingFailed() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Failed to create Group");
        alert.setHeaderText("You create a group with an empty name or with the same name of a group that you are admin of");
        alert.show();
    }

    private void creatingSuccess() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Group successfully created");
        alert.show();
    }

    private void deletingSuccess(Stage stage) {
        stage.close();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Group successfully deleted");
        alert.show();
    }

    private void excludingSuccess() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("User successfully excluded");
        alert.show();
    }

}
