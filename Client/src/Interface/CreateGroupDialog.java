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

public class CreateGroupDialog extends BorderPane {

    ClientManager clientManager;

    private Text title, groupNameText, infoText;
    private TextField groupNameField;

    private Button createButton;

    private VBox mainBox;
    private HBox groupNameBox;

    public CreateGroupDialog(ClientManager clientManager){
        this.clientManager = clientManager;
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


        createButton.setOnAction(ev -> {
            if(groupNameField.getText().isEmpty()){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Group creation Failed");
                alert.setHeaderText("Group name cannot be empty or there is already a Group with that name");
                alert.show();
            }
            else
                clientManager.createGroup(groupNameField.getText());
        });
    }
}
