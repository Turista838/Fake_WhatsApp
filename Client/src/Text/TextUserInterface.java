package Text;
import Model.Client;
import java.util.Scanner;

public class TextUserInterface {

    private Client cli;

    public TextUserInterface(Client c) {
        cli = c;
    }

    private void uiMainMenu() {

        Scanner s = new Scanner(System.in);
        int value;

        System.out.println();
        System.out.println("******************************************");
        System.out.println("*************** 4 in Row *****************"); //TODO alterar isto tudo
        System.out.println("******************************************");
        System.out.println();
        System.out.println("0 - AI vs AI");
        System.out.println("1 - Human vs AI");
        System.out.println("2 - Human vs Human");
        System.out.println("3 - Carregar um jogo");
        System.out.println("4 - Exit");
        System.out.print("> ");

        while (!s.hasNextInt()) s.next(); //é para avançar só se pusermos um inteiro

        do{
            value = s.nextInt();
            if(value < 0 || value > 4)
                System.out.println("Introduza num 0 a 4");
            else{
                if(value == 3){
                    //loadGame();
                }
            }
        }while(value < 0 || value > 4);
    }


    public void authentication() {

        //TODO if já estiver autenticado:
        cli.connectSGBD();
        //uiMainMenu()
        //TODO else autenticar (autenticar só depois de estar ligado a um servidor)
        //register()

    }
}
