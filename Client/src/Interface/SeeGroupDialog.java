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

import static Data.ClientManager.*;
import static Interface.Constants.*;

public class SeeGroupDialog extends BorderPane {

    ClientManager clientManager;
    private String selectedGroup;

    private Text title;

    private ListView currentMembersList;


    private VBox mainBox;

    public SeeGroupDialog(ClientManager clientManager, String selectedGroup){
        this.clientManager = clientManager;
        this.selectedGroup = selectedGroup;

        title = new Text("Group Info: " + selectedGroup);

        currentMembersList = new ListView();

        mainBox = new VBox();
        mainBox.getChildren().addAll(title, currentMembersList);
        mainBox.setAlignment(Pos.CENTER);
        setCenter(mainBox);

        clientManager.requestGroupMembersList(selectedGroup);

        clientManager.addPropertyChangeListener(VIEW_GROUP_MEMBERS, evt->requestGroupUsers());


    }

    private void requestGroupUsers() {
        currentMembersList.getItems().clear();
        for (String members : clientManager.getSelectedGroupMembersList()) {
            currentMembersList.getItems().add(members);
        }
    }

}
