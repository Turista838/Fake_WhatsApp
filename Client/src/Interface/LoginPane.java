package Interface;

import Data.ClientOBS;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import static Data.ClientOBS.VIEW_CHANGED;

public class LoginPane extends BorderPane {

    private ClientOBS clientOBS;

    private Text title, usernameText, passwordText, registerText;
    private TextField usernameField, passwordField;
    private Button loginButton;

    private VBox mainBox;
    private HBox titleBox;
    private HBox usernameBox;
    private HBox passwordBox;
    private HBox loginBox;
    private HBox registerBox;

    public LoginPane(ClientOBS clientOBS, int width, int height){
        this.clientOBS = clientOBS;
        setWidth(width);
        setHeight(height);

        title = new Text("Fake Whatsapp");
        usernameText = new Text("Username");
        passwordText = new Text("Password");
        registerText = new Text("Clique aqui para Registar");
        usernameField = new TextField();
        passwordField = new TextField();
        loginButton = new Button("Login");

        mainBox = new VBox();
        titleBox = new HBox();
        usernameBox = new HBox();
        passwordBox = new HBox();
        loginBox = new HBox();
        registerBox = new HBox();

        //this.getChildren().addAll(titleBox, usernameBox, passwordBox, registerBox);

        mainBox.getChildren().addAll(titleBox, usernameBox, passwordBox, loginBox, registerBox);
        mainBox.setAlignment(Pos.CENTER);
        setCenter(mainBox);

        titleBox.getChildren().add(title);
        titleBox.setAlignment(Pos.CENTER);



        usernameBox.getChildren().addAll(usernameText, usernameField);
        passwordBox.getChildren().addAll(passwordText, passwordField);
        loginBox.getChildren().add(loginButton);
        registerBox.getChildren().add(registerText);

        loginButton.setOnAction(ev -> {
            clientOBS.login(usernameField.getText(), passwordField.getText());
        });

        clientOBS.addPropertyChangeListener(VIEW_CHANGED, evt->update());

        titleBox.setBackground(new Background(new BackgroundFill(Color.LIGHTCYAN,
                CornerRadii.EMPTY,
                Insets.EMPTY))); //TODO apagar (debug)
    }

    private void update() {
        this.setVisible(!clientOBS.getClientStatus());
    }

}
