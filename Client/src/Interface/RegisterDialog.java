package Interface;

import Data.ClientManager;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import static Data.ClientManager.*;
import static Interface.Constants.*;


public class RegisterDialog extends BorderPane {

    ClientManager clientManager;
    private Stage stage;

    private Text title, nameText, usernameText, passwordText;
    private TextField nameField, usernameField, passwordField;

    private Button registerButton;

    private VBox mainBox;

    public RegisterDialog(ClientManager clientManager, Stage stage){
        this.clientManager = clientManager;
        this.stage = stage;
        this.setWidth(122);
        this.setHeight(555);

        title = new Text("Register");
        nameText = new Text("Name");
        usernameText = new Text("Username");
        passwordText = new Text("Password");

        nameField = new TextField();
        usernameField = new TextField();
        passwordField = new TextField();

        registerButton = new Button("Register");

        mainBox = new VBox(4);
        mainBox.getChildren().addAll(title, nameText, nameField, usernameText, usernameField, passwordText, passwordField, registerButton);
        mainBox.setAlignment(Pos.CENTER);
        setCenter(mainBox);

        title.setStyle(BASICSECUNDARYTITLE);
        nameText.setStyle(BASICSMALLTEXTSTYLE);
        usernameText.setStyle(BASICSMALLTEXTSTYLE);
        passwordText.setStyle(BASICSMALLTEXTSTYLE);
        registerButton.setStyle(BASICBUTTON);

        clientManager.addPropertyChangeListener(REGISTER_SUCCESS, evt->registerSuccess(stage));
        clientManager.addPropertyChangeListener(REGISTER_FAILED, evt->registerFailed());

        registerButton.setOnAction(ev -> {
            if(nameField.getText().isEmpty() || usernameField.getText().isEmpty() || passwordField.getText().isEmpty())
                registerFailedEmptySpaces();
            else
                clientManager.register(nameField.getText(), usernameField.getText(), passwordField.getText());
        });

    }

    private void registerFailedEmptySpaces() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Register Failed");
        alert.setHeaderText("You must fill all fields");
        alert.show();
    }

    private void registerSuccess(Stage stage) {
        stage.close();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Successfully Registered");
        alert.setHeaderText("User " + usernameField.getText() + " successfully registered");
        alert.show();
    }

    private void registerFailed() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Register Failed");
        alert.setHeaderText("The username " + usernameField.getText() + " is already taken");
        alert.show();
    }
}
