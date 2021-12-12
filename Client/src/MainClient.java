import Data.ClientStartup;
import Data.ClientManager;
import Interface.RootPane;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainClient extends Application {
    //public class MainClient{

    private static String arg0;
    private static String arg1;

//    public static void main(String[] args) {
//        if(args.length != 2){
//            System.out.println("Arguments needed: <IP GRDS> <PORT GRDS>");
//            return;
//        }
//        TextUserInterface ui = new TextUserInterface(new Client(args[0], args[1]));
//        ui.launch();
//    }


    public static void main(String[] args) {
        if(args.length != 2){
            System.out.println("Arguments needed: <IP GRDS> <PORT GRDS>");
            return;
        }
        else{
            arg0 = args[0];
            arg1 = args[1];
            launch(args); //TODO ?
        }
    }

    @Override
    public void start(Stage primaryStage) {

        ClientManager clientManager = new ClientManager(new ClientStartup(arg0, arg1));
        RootPane rootPane = new RootPane(clientManager, 1000, 600);

        Scene scene = new Scene(rootPane,1000,600);

        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Client App");

        primaryStage.setOnCloseRequest(ev -> Platform.exit());

        primaryStage.show();

    }


}
