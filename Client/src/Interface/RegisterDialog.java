package Interface;

import Data.ClientManager;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;


public class RegisterDialog extends BorderPane {

    ClientManager clientManager;

    private Text title, nameText, usernameText, passwordText;
    private TextField nameField, usernameField, passwordField;

    private Button registerButton;

    private VBox mainBox;

    public RegisterDialog(ClientManager clientManager){
        this.clientManager = clientManager;
        this.setWidth(122);
        this.setHeight(555);

        title = new Text("Register");
        nameText = new Text("Name");
        usernameText = new Text("Username");
        passwordText = new Text("Password");

        nameField = new TextField();
        usernameField = new TextField();
        passwordField = new TextField();

        registerButton = new Button("Registar");

        mainBox = new VBox();
        mainBox.getChildren().addAll(title, nameText, nameField, usernameText, usernameField, passwordText, passwordField, registerButton);
        mainBox.setAlignment(Pos.CENTER);
        setCenter(mainBox);


        registerButton.setOnAction(ev -> {
            clientManager.register(nameField.getText(), usernameField.getText(), passwordField.getText());
        });
    }
}
