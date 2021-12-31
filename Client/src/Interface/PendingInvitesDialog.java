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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static Data.ClientManager.UPDATE_REQUESTS;
import static Data.ClientManager.VIEW_CHANGED;
import static Interface.Constants.*;

public class PendingInvitesDialog extends BorderPane {

    ClientManager clientManager;
    ArrayList<String[]> PendingInvitesListGroups;

    private Text title;

    private ListView pendingInvitesList;
    private ArrayList unformattedInvitesList;
    private ArrayList unformattedGroupsList;

    private Button acceptButton;
    private Button refuseButton;

    private VBox mainBox;
    private HBox buttonsBox;

    public PendingInvitesDialog(ClientManager clientManager){
        this.clientManager = clientManager;
        unformattedInvitesList = new ArrayList<String>();
        unformattedGroupsList = new ArrayList<String>();
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

        title.setStyle(BASICSECUNDARYTITLE);
        pendingInvitesList.setStyle(LISTVIEWSTYLE);
        acceptButton.setStyle(BASICBUTTON);
        refuseButton.setStyle(BASICBUTTON);

        clientManager.requestPendingInvitesList();

        clientManager.addPropertyChangeListener(UPDATE_REQUESTS, evt->updateRequests());

        acceptButton.setOnAction(ev -> {
            String selectedRequest = (String) pendingInvitesList.getSelectionModel().getSelectedItem();
            if(selectedRequest.contains("|User|")) //é um user
                clientManager.acceptFriendRequest((String) unformattedInvitesList.get(pendingInvitesList.getSelectionModel().getSelectedIndex()));
            else //é um grupo
                clientManager.acceptNewMember((String) unformattedInvitesList.get(pendingInvitesList.getSelectionModel().getSelectedIndex()), (String) unformattedGroupsList.get(pendingInvitesList.getSelectionModel().getSelectedIndex()));
        });

        refuseButton.setOnAction(ev -> {
            String selectedRequest = (String) pendingInvitesList.getSelectionModel().getSelectedItem();
            if(selectedRequest.contains("|User|")) //é um user
                clientManager.refuseFriendRequest((String) unformattedInvitesList.get(pendingInvitesList.getSelectionModel().getSelectedIndex()));
            else //é um grupo
                clientManager.refuseNewMember((String) unformattedInvitesList.get(pendingInvitesList.getSelectionModel().getSelectedIndex()), (String) unformattedGroupsList.get(pendingInvitesList.getSelectionModel().getSelectedIndex()));
        });
    }

    private void updateRequests() {
        pendingInvitesList.getItems().clear();
        unformattedInvitesList.clear();
        for (String user : clientManager.getPendingInvitesListUsers()) {
            unformattedInvitesList.add(user);
            pendingInvitesList.getItems().add(user + "|User|");
        }
        PendingInvitesListGroups =  clientManager.getPendingInvitesListGroups();
        for (String[] entry : PendingInvitesListGroups) {
            String member = entry[0];
            String group = entry[1];
            unformattedInvitesList.add(member);
            unformattedGroupsList.add(group);
            pendingInvitesList.getItems().add(member + " |Grupo: " + group + " | ");
        }

    }
}