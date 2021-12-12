package Interface;

import Data.ClientManager;
import javafx.scene.layout.StackPane;

public class RootPane extends StackPane {

    private ClientManager clientManager;
    private LoginPane loginPane;
    private MainPane mainPane;

    public RootPane(ClientManager clientManager, int width, int height){
        this.clientManager = clientManager;
        setWidth(width);
        setHeight(height);

        loginPane = new LoginPane(clientManager, width, height);
        mainPane = new MainPane(clientManager, width, height);

        getChildren().addAll(loginPane, mainPane);

        update();

    }

    private void update() {
        loginPane.setVisible(true);
        mainPane.setVisible(false);
    }
}
