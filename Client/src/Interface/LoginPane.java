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
        registerText = new TextFlow( new Text("Ainda não tem conta?"), createAccount );
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
            clientManager.login(usernameField.getText(), passwordField.getText());
        });

        clientManager.addPropertyChangeListener(VIEW_CHANGED, evt->update());

        clientManager.addPropertyChangeListener(LOGIN_FAILED, evt->loginFailed());

        titleBox.setBackground(new Background(new BackgroundFill(Color.LIGHTCYAN,
                CornerRadii.EMPTY,
                Insets.EMPTY))); //TODO apagar (debug)
    }

    private Hyperlink showRegisterDialog(ClientManager clientManager) {
        Hyperlink register = new Hyperlink("Carregue aqui para registar");

        register.setOnAction(event -> {
            Stage stage = new Stage();
            stage.setScene(new Scene(new RegisterDialog(clientManager),200, 200));
            stage.initModality(Modality.APPLICATION_MODAL);
            //stage.setX(primaryStage.getX()+200+Math.random()*100);
            //stage.setY(primaryStage.getY()+200+Math.random()*100);
            stage.setResizable(false);
            //stage.setOnCloseRequest(ev -> Platform.exit());
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
