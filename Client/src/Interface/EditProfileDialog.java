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

public class EditProfileDialog extends BorderPane {

    ClientManager clientManager;

    private Text title, editNameText, editUsernameText, oldPassText, passInfoText, newPassText;
    private TextField editNameField, editUsernameField, oldPassField, newPassField;

    private Button editProfileButton;

    private VBox mainBox;
    private HBox editNameBox;
    private HBox editUsernameBox;
    private HBox oldPassBox;
    private HBox newPassBox;

    public EditProfileDialog(ClientManager clientManager){
        this.clientManager = clientManager;
        this.setWidth(122);
        this.setHeight(555);

        title = new Text("Edit Profile");
        editNameText = new Text("Change name:");
        editNameField = new TextField();
        editUsernameText = new Text("New username:");
        editUsernameField = new TextField();
        oldPassText = new Text("Old password");
        oldPassField = new TextField();
        passInfoText = new Text("Leave field below in blank if you don't want to change your password");
        newPassText = new Text("New password:");
        newPassField = new TextField();

        editProfileButton = new Button("Edit");

        editNameBox = new HBox();
        editNameBox.getChildren().addAll(editNameText, editNameField);

        editUsernameBox = new HBox();
        editUsernameBox.getChildren().addAll(editUsernameText, editUsernameField);

        oldPassBox = new HBox();
        oldPassBox.getChildren().addAll(oldPassText, oldPassField);

        newPassBox = new HBox();
        newPassBox.getChildren().addAll(newPassText, newPassField);

        mainBox = new VBox();
        mainBox.getChildren().addAll(title, editNameBox, editUsernameBox, oldPassBox, passInfoText, newPassBox, editProfileButton);
        mainBox.setAlignment(Pos.CENTER);
        setCenter(mainBox);



        editProfileButton.setOnAction(ev -> {
            //clientManager.edit(nameField.getText(), usernameField.getText(), passwordField.getText());
        });
    }
}
