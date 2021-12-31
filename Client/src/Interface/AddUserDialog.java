package Interface;

import Data.ClientManager;
import SharedClasses.Data.MessageList;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import static Data.ClientManager.FRIEND_REQUEST;
import static Data.ClientManager.VIEW_CHANGED;
import static Interface.Constants.*;

public class AddUserDialog extends BorderPane {

    ClientManager clientManager;

    private Text title;
    private ListView availableUsersList;
    private Button addUserButton;

    private VBox mainBox;

    public AddUserDialog(ClientManager clientManager){
        this.clientManager = clientManager;

        title = new Text("Available Users List");
        availableUsersList = new ListView();
        addUserButton = new Button("Add User");

        mainBox = new VBox();
        mainBox.getChildren().addAll(title, availableUsersList, addUserButton);
        mainBox.setAlignment(Pos.CENTER);
        setCenter(mainBox);

        title.setStyle(BASICSECUNDARYTITLE);
        availableUsersList.setStyle(LISTVIEWSTYLE);
        addUserButton.setStyle(BASICBUTTON);

        clientManager.requestUserList();

        clientManager.addPropertyChangeListener(VIEW_CHANGED, evt->update());

        clientManager.addPropertyChangeListener(FRIEND_REQUEST, evt->friendRequestSent());

        addUserButton.setOnAction(ev -> {
            String selectedUser = (String) availableUsersList.getSelectionModel().getSelectedItem();
            if(selectedUser != null)
                clientManager.addUser(selectedUser);
        });
    }

    private void update() {
        availableUsersList.getItems().clear();
        for (String user : clientManager.getAvailableUsersList()) {
            availableUsersList.getItems().add(user);
        }
    }

    private void friendRequestSent() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Friend Request Sent");
        alert.setHeaderText("Request Sent");
        alert.show();
    }
}
