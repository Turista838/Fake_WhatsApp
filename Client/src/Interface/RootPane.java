package Interface;

import Data.ClientOBS;
import javafx.scene.layout.StackPane;

public class RootPane extends StackPane {

    private ClientOBS clientOBS;
    private LoginPane loginPane;
    private MainPane mainPane;

    public RootPane(ClientOBS clientOBS, int width, int height){
        this.clientOBS = clientOBS;
        setWidth(width);
        setHeight(height);

        loginPane = new LoginPane(clientOBS, width, height);
        mainPane = new MainPane(clientOBS, width, height);

        getChildren().addAll(loginPane, mainPane);

        update();

    }

    private void update() {
        loginPane.setVisible(true);
        mainPane.setVisible(false);
    }
}
