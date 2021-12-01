import Model.Client;
import Text.TextUserInterface;

public class MainClient {

    public static void main(String[] args) {

        if(args.length != 2){
            System.out.println("Arguments needed: <IP GRDS> <PORT GRDS>");
            return;
        }

        TextUserInterface ui = new TextUserInterface(new Client(args[0], args[1]));
        ui.launch();

    }
}
