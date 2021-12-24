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

public class PendingInvitesDialog extends BorderPane {

    ClientManager clientManager;

    private Text title;

    private ListView pendingInvitesList;

    private Button acceptButton;
    private Button refuseButton;

    private VBox mainBox;
    private HBox buttonsBox;

    public PendingInvitesDialog(ClientManager clientManager){
        this.clientManager = clientManager;
        this.setWidth(122);
        this.setHeight(555);

        title = new Text("Pending Invites");

        pendingInvitesList = new ListView();

        acceptButton = new Button("Accept");
        refuseButton = new Button("Refuse");

        buttonsBox = new HBox();
        buttonsBox.getChildren().addAll(refuseButton, acceptButton);

        mainBox = new VBox();
        mainBox.getChildren().addAll(title, pendingInvitesList, buttonsBox);
        mainBox.setAlignment(Pos.CENTER);
        setCenter(mainBox);


//        registerButton.setOnAction(ev -> {
//            clientManager.register(nameField.getText(), usernameField.getText(), passwordField.getText());
//        });
    }
}