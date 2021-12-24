package Interface;

import Data.ClientManager;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class EditGroupDialog extends BorderPane {

    ClientManager clientManager;

    private Text title, editGroupNameText;
    private TextField editGroupNameField;

    private ListView currentUsersList;

    private Button editGroupNameButton;
    private Button banUserButton;
    private Button inviteUserButton;
    private Button deleteGroupButton;

    private VBox mainBox;
    private HBox editGroupNameBox;
    private HBox buttonsBox;

    public EditGroupDialog(ClientManager clientManager){
        this.clientManager = clientManager;
        this.setWidth(122);
        this.setHeight(555);

        title = new Text("Edit Group");
        editGroupNameText = new Text("New Group name:");
        editGroupNameField = new TextField();

        currentUsersList = new ListView();

        editGroupNameButton = new Button("Change Group Name");
        banUserButton = new Button("Exclude user");
        inviteUserButton = new Button("Invite User");
        deleteGroupButton = new Button("Delete Group");

        editGroupNameBox = new HBox();
        editGroupNameBox.getChildren().addAll(editGroupNameText, editGroupNameField, editGroupNameButton);

        buttonsBox = new HBox();
        buttonsBox.getChildren().addAll(banUserButton, inviteUserButton, deleteGroupButton);

        mainBox = new VBox();
        mainBox.getChildren().addAll(title, editGroupNameBox, currentUsersList, buttonsBox);
        mainBox.setAlignment(Pos.CENTER);
        setCenter(mainBox);


//        createButton.setOnAction(ev -> {
////            clientManager.register(nameField.getText(), usernameField.getText(), passwordField.getText());
//        });
    }
}
