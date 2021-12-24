package Interface;

import Data.ClientManager;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

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

        addUserButton.setOnAction(ev -> { //TODO
            //clientManager.register(nameField.getText(), usernameField.getText(), passwordField.getText());
        });
    }
}
