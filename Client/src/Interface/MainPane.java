package Interface;

import Data.ClientManager;
import SharedClasses.Data.MessageList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;

import static Data.ClientManager.VIEW_CHANGED;

public class MainPane extends BorderPane {

    private ClientManager clientManager;

    private String selectedContact;

    private ListView usersList;
    private ListView conversationList;

    private Text username;
    private Text messageText;
    private TextField messageTextField;

    private VBox mainBox;
    private HBox menuBox;
    private VBox usersListBox;
    private VBox conversationListBox;
    private HBox readMessageBox;
    private HBox writeMessageBox;

    private Button addUserButton;
    private Button joinGroupButton;
    private Button createGroupButton;
    private Button editProfileButton;
    private Button pendingInvitesButton;

    private Button removeUserButton;
    private Button leaveGroupButton;
    private Button editGroupButton;
    private Button sendMessageButton;
    private Button sendFileButton;
    private Button removeMessageButton;

    public MainPane(ClientManager clientManager, int width, int height){
        this.clientManager = clientManager;
        setWidth(width);
        setHeight(height);

        usersList = new ListView();
        conversationList = new ListView();

        username = new Text();
        messageText = new Text("Message:");
        messageTextField = new TextField();

        //Top Buttons
        addUserButton = new Button("Add User");
        joinGroupButton = new Button("Join Group");
        createGroupButton = new Button("Create Group");
        editProfileButton = new Button("Edit Profile");
        pendingInvitesButton = new Button("Pending Invites");

        //Bottom Buttons
        removeUserButton = new Button("Remove User");
        leaveGroupButton = new Button("Leave Group");
        editGroupButton = new Button("Edit Group");
        sendMessageButton = new Button("Send");
        sendFileButton = new Button("Send File");
        removeMessageButton = new Button("Remove Message");

        mainBox = new VBox();
        menuBox = new HBox();
        usersListBox = new VBox();
        conversationListBox = new VBox();
        readMessageBox = new HBox();
        writeMessageBox = new HBox();

        mainBox.getChildren().addAll(menuBox, usersListBox, conversationListBox);
        mainBox.setAlignment(Pos.CENTER);
        setCenter(mainBox);

        menuBox.getChildren().addAll(username, addUserButton, joinGroupButton, createGroupButton, editProfileButton, pendingInvitesButton);
        menuBox.setAlignment(Pos.TOP_CENTER);
        setTop(menuBox);
        menuBox.setPadding(new Insets(10, 10, 10, 10));

        usersListBox.getChildren().addAll(usersList);
        usersListBox.setAlignment(Pos.CENTER_LEFT);
        setLeft(usersListBox);

        usersList.setPrefHeight(520);

        conversationListBox.getChildren().addAll(readMessageBox, writeMessageBox);
        conversationListBox.setAlignment(Pos.CENTER_RIGHT);
        setCenter(conversationListBox);

        readMessageBox.getChildren().add(conversationList);
        readMessageBox.setAlignment(Pos.CENTER);
        setCenter(readMessageBox);

        conversationList.setPrefWidth(760);

        writeMessageBox.getChildren().addAll(removeUserButton, leaveGroupButton, editGroupButton, messageText, messageTextField, sendMessageButton, sendFileButton, removeMessageButton);
        writeMessageBox.setAlignment(Pos.CENTER);
        setBottom(writeMessageBox);
        //writeMessageBox.setTranslateX(247.5);
        writeMessageBox.setPadding(new Insets(10, 10, 10, 10));

        addUserButton.setOnAction(ev -> {
            Stage stage = new Stage();
            stage.setScene(new Scene(new AddUserDialog(clientManager),400, 280));
            stage.initModality(Modality.NONE);
            stage.setResizable(false);
            stage.setTitle("Add User");
            stage.show();
        });

        joinGroupButton.setOnAction(ev -> {
            Stage stage = new Stage();
            stage.setScene(new Scene(new JoinGroupDialog(clientManager),400, 280));
            stage.initModality(Modality.NONE);
            stage.setResizable(false);
            stage.setTitle("Join Group");
            stage.show();
        });

        createGroupButton.setOnAction(ev -> {
            Stage stage = new Stage();
            stage.setScene(new Scene(new CreateGroupDialog(clientManager),300, 100));
            stage.initModality(Modality.NONE);
            stage.setResizable(false);
            stage.setTitle("Create Group");
            stage.show();
        });

        editProfileButton.setOnAction(ev -> {
            Stage stage = new Stage();
            stage.setScene(new Scene(new EditProfileDialog(clientManager),400, 180));
            stage.initModality(Modality.NONE);
            stage.setResizable(false);
            stage.setTitle("Edit Profile");
            stage.show();
        });

        pendingInvitesButton.setOnAction(ev -> {
            Stage stage = new Stage();
            stage.setScene(new Scene(new PendingInvitesDialog(clientManager),400, 280));
            stage.initModality(Modality.NONE);
            stage.setResizable(false);
            stage.setTitle("Pending Invites");
            stage.show();
        });

        editGroupButton.setOnAction(ev -> {
            Stage stage = new Stage();
            stage.setScene(new Scene(new EditGroupDialog(clientManager, selectedContact, stage),400, 280)); //selectedContact = group
            stage.initModality(Modality.NONE);
            stage.setResizable(false);
            stage.setTitle("Edit Group");
            stage.show();
        });

        sendMessageButton.setOnAction(ev -> {
            if(!messageTextField.getText().isEmpty()) {
                if (clientManager.getContactIsGroup()) {
                    clientManager.sendGroupMessage(messageTextField.getText());
                } else {
                    clientManager.sendDirectMessage(messageTextField.getText());
                }
            }
        });

        //clique nos contactos faz update da conversa desse contacto
        usersList.setOnMouseClicked(event -> {
            selectedContact = (String)usersList.getSelectionModel().getSelectedItem();
            if(selectedContact.substring(selectedContact.length() - 1).equals("*")){ //remover o *
                clientManager.removeAsterisk(selectedContact);
                selectedContact = selectedContact.substring(0, selectedContact.length() - 1);
            }
            clientManager.setSelectedContact(selectedContact);
            clientManager.requestMessages();
        });

        sendFileButton.setOnMouseClicked(event -> {
            Stage stage = new Stage();
            FileChooser fileChooser = new FileChooser();
            File selectedFile = fileChooser.showOpenDialog(stage);
            System.out.println(selectedFile);
            if (clientManager.getContactIsGroup()) {
                clientManager.sendGroupFile(selectedFile);
            } else {
                clientManager.sendDirectFile(selectedFile);
            }
        });


        clientManager.addPropertyChangeListener(VIEW_CHANGED, evt->update());


        menuBox.setBackground(new Background(new BackgroundFill(Color.BROWN,
                CornerRadii.EMPTY,
                Insets.EMPTY))); //TODO apagar (debug)
        conversationListBox.setBackground(new Background(new BackgroundFill(Color.TURQUOISE,
                CornerRadii.EMPTY,
                Insets.EMPTY))); //TODO apagar (debug)
        readMessageBox.setBackground(new Background(new BackgroundFill(Color.LIGHTGREEN,
        CornerRadii.EMPTY,
                Insets.EMPTY))); //TODO apagar (debug)
        writeMessageBox.setBackground(new Background(new BackgroundFill(Color.LIGHTPINK,
        CornerRadii.EMPTY,
                Insets.EMPTY))); //TODO apagar (debug)

    }

    private void update() {
        this.setVisible(clientManager.getLoggedIn());
        username.setText(clientManager.getUsername());
        usersList.getItems().clear();
        conversationList.getItems().clear();

        for (String contact : clientManager.getContactList()) {
            usersList.getItems().add(contact);
        }

        for (MessageList message : clientManager.getMessageList()) {
            conversationList.getItems().add(message.message);
        }
    }

}
