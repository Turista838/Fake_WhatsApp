package Interface;

import Data.ClientManager;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import static Data.ClientManager.*;
import static Interface.Constants.*;

public class JoinGroupDialog extends BorderPane {

    ClientManager clientManager;

    private Text title;
    private ListView availableGroupsList;
    private Button joinGroupButton;

    private VBox mainBox;

    public JoinGroupDialog(ClientManager clientManager){
        this.clientManager = clientManager;
        this.setWidth(366);
        this.setHeight(555);

        title = new Text("Available Groups List");
        availableGroupsList = new ListView();
        joinGroupButton = new Button("Join Group");

        mainBox = new VBox();
        mainBox.getChildren().addAll(title, availableGroupsList, joinGroupButton);
        mainBox.setAlignment(Pos.CENTER);
        setCenter(mainBox);

        clientManager.requestGroupList();

        clientManager.addPropertyChangeListener(VIEW_CHANGED, evt->update());

        clientManager.addPropertyChangeListener(GROUP_REQUEST, evt->groupRequestSent());

        joinGroupButton.setOnAction(ev -> {
            String selectedGroup = (String) availableGroupsList.getSelectionModel().getSelectedItem();
            clientManager.joinGroup(selectedGroup);
        });
    }

    private void update() {
        availableGroupsList.getItems().clear();
        for (String group : clientManager.getAvailableGroupsList()) {
            availableGroupsList.getItems().add(group);
        }
    }

    private void groupRequestSent() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Friend Request Sent");
        alert.show();
    }
}
