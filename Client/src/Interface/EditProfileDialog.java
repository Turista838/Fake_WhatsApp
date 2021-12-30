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

import static Data.ClientManager.USER_EDIT_SUCCESSFUL;
import static Data.ClientManager.VIEW_CHANGED;

public class EditProfileDialog extends BorderPane {

    ClientManager clientManager;
    Stage stage;

    private Text title, editNameText, editUsernameText, oldPassText, passInfoText, newPassText;
    private TextField editNameField, editUsernameField, oldPassField, newPassField;

    private Button editProfileButton;

    private VBox mainBox;
    private HBox editNameBox;
    private HBox editUsernameBox;
    private HBox oldPassBox;
    private HBox newPassBox;

    public EditProfileDialog(ClientManager clientManager, Stage stage){
        this.clientManager = clientManager;
        this.stage = stage;
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

        editNameField.setText(clientManager.getClientName());
        editUsernameField.setText(clientManager.getUsername());

        editProfileButton.setOnAction(ev -> {
            if(!oldPassField.getText().isEmpty()) {
                if (newPassField.getText().isEmpty()) {
                    clientManager.editNameUsername(editNameField.getText(), editUsernameField.getText(), oldPassField.getText(), clientManager.getUsername());
                } else {
                    clientManager.editNameUsernamePassword(editNameField.getText(), editUsernameField.getText(), oldPassField.getText(), clientManager.getUsername(), newPassField.getText());
                }
            }
            else{
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Failed to edit Profile");
                alert.setHeaderText("You must enter your password");
                alert.show();
            }
        });

        clientManager.addPropertyChangeListener(USER_EDIT_SUCCESSFUL, evt->editSuccessful(stage));
    }

    private void editSuccessful(Stage stage) {
        stage.close();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Profile information edited");
        alert.setHeaderText("Profile information edited");
        alert.show();
    }
}
