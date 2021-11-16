import Model.Client;
import Text.TextUserInterface;

public class Main {

    public static void main(String[] args) {

        TextUserInterface ui = new TextUserInterface(new Client(args[0], args[1]));
        ui.authentication();

    }
}
