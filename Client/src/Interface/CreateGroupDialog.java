package Interface;

import Data.ClientManager;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import static Data.ClientManager.GROUP_CREATING_NOT_SUCCESSFUL;
import static Data.ClientManager.GROUP_CREATING_SUCCESSFUL;

public class CreateGroupDialog extends BorderPane {

    ClientManager clientManager;
    private Stage stage;

    private Text title, groupNameText, infoText;
    private TextField groupNameField;

    private Button createButton;

    private VBox mainBox;
    private HBox groupNameBox;

    public CreateGroupDialog(ClientManager clientManager, Stage stage){
        this.clientManager = clientManager;
        this.stage = stage;
        this.setWidth(122);
        this.setHeight(555);

        title = new Text("Create Group");
        groupNameText = new Text("Group Name:");
        infoText = new Text("You can only invite members after group creation");

        groupNameField = new TextField();

        createButton = new Button("Create");

        groupNameBox = new HBox();
        groupNameBox.getChildren().addAll(groupNameText, groupNameField);

        mainBox = new VBox();
        mainBox.getChildren().addAll(title, groupNameBox, infoText, createButton);
        mainBox.setAlignment(Pos.CENTER);
        setCenter(mainBox);

        clientManager.addPropertyChangeListener(GROUP_CREATING_SUCCESSFUL, evt->creatingSuccess(stage));

        clientManager.addPropertyChangeListener(GROUP_CREATING_NOT_SUCCESSFUL, evt->creatingFailed());

        createButton.setOnAction(ev -> {
            clientManager.createGroup(groupNameField.getText());
        });
    }

    private void creatingFailed() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Failed to create Group");
        alert.setHeaderText("You create a group with an empty name or with the same name of a group that you are admin of");
        alert.show();
    }

    private void creatingSuccess(Stage stage) {
        stage.close();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Group successfully created");
        alert.setHeaderText("Group successfully created");
        alert.show();
    }
}
