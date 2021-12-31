package Interface;

import Data.ClientManager;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;

import static Data.ClientManager.*;
import static Interface.Constants.*;

public class LoginPane extends BorderPane {

    private ClientManager clientManager;

    private Text title, usernameText, passwordText;
    private TextFlow registerText;
    private TextField usernameField, passwordField;
    private Button loginButton;

    private VBox mainBox;
    private HBox titleBox;
    private HBox usernameBox;
    private HBox passwordBox;
    private HBox loginBox;
    private HBox registerBox;

    public LoginPane(ClientManager clientManager, int width, int height){
        this.clientManager = clientManager;
        setWidth(width);
        setHeight(height);

        title = new Text("Fake Whatsapp");
        usernameText = new Text("Username");
        passwordText = new Text("Password");
        Hyperlink createAccount   = showRegisterDialog(clientManager);
        registerText = new TextFlow( new Text("New?"), createAccount );
        usernameField = new TextField();
        passwordField = new TextField();
        loginButton = new Button("Login");

        mainBox = new VBox(24);
        titleBox = new HBox();
        usernameBox = new HBox(8);
        passwordBox = new HBox(8);
        loginBox = new HBox();
        registerBox = new HBox();

        mainBox.getChildren().addAll(titleBox, usernameBox, passwordBox, loginBox, registerBox);
        mainBox.setAlignment(Pos.CENTER);
        setCenter(mainBox);

        titleBox.getChildren().add(title);
        titleBox.setAlignment(Pos.CENTER);

        usernameBox.getChildren().addAll(usernameText, usernameField);
        passwordBox.getChildren().addAll(passwordText, passwordField);
        loginBox.getChildren().add(loginButton);
        registerBox.getChildren().add(registerText);
        usernameBox.setAlignment(Pos.CENTER);
        passwordBox.setAlignment(Pos.CENTER);
        loginBox.setAlignment(Pos.CENTER);
        registerBox.setAlignment(Pos.CENTER);

        loginButton.setOnAction(ev -> {
            clientManager.login(usernameField.getText(), passwordField.getText());
        });

        clientManager.addPropertyChangeListener(VIEW_CHANGED, evt->update());

        clientManager.addPropertyChangeListener(LOGIN_FAILED, evt->loginFailed());

        mainBox.setBackground(new Background(new BackgroundFill(Color.DARKRED,
                CornerRadii.EMPTY,
                Insets.EMPTY)));

        registerBox.setBackground(new Background(new BackgroundFill(Color.NAVAJOWHITE,
                CornerRadii.EMPTY,
                Insets.EMPTY)));

        loginButton.setStyle(BUTTONSTYLE);
        title.setStyle(TITLESTYLE);
        usernameText.setStyle(SMALLTEXTSTYLE);
        passwordText.setStyle(SMALLTEXTSTYLE);

    }

    private Hyperlink showRegisterDialog(ClientManager clientManager) {
        Hyperlink register = new Hyperlink("Click here to sign up");

        register.setOnAction(event -> {
            Stage stage = new Stage();
            stage.setScene(new Scene(new RegisterDialog(clientManager, stage),200, 200));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.show();
        });

        return register;
    }

    private void update() {
        this.setVisible(!clientManager.getLoggedIn());
    }

    private void loginFailed() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Login Failed");
        alert.setHeaderText("Incorrect username and/or password");
        alert.show();
    }

}
