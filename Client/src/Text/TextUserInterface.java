package Text;
import Model.Client;
import SharedClasses.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Scanner;

public class TextUserInterface {

    private Client cli;
    ObjectOutputStream oout; //enviar
    ObjectInputStream oin; //receber

    public TextUserInterface(Client c) {
        cli = c;
    }

    private void uiMainMenu() {

        Scanner s = new Scanner(System.in);
        int value;

        System.out.println();
        System.out.println("************  Bem vindo Cliente  ***************");
        System.out.println();
        System.out.println("1 - Enviar uma LoginMessage");
        System.out.println("2 - Enviar uma DirectMessage");
        System.out.println("3 - Enviar uma GroupMessage");
        System.out.println("4 - Sair");
        System.out.print("> ");

        while (!s.hasNextInt()) s.next(); //é para avançar só se pusermos um inteiro

        do{
            value = s.nextInt();
            if(value < 1 || value > 4)
                System.out.println("Introduza num 1 a 3");
            else{
                if(value == 1){
                    LoginMessageTCP loginMessageTCP = new LoginMessageTCP();
                    //directMessageTCP.setChatMessage(nome);
                    try{
                        cli.getOout().writeObject(loginMessageTCP);
                        cli.getOout().flush();
                    }
                   catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(value == 2){
                    DirectMessageTCP directMessageTCP = new DirectMessageTCP();
                    //directMessageTCP.setChatMessage(nome);
                    try{
                        cli.getOout().writeObject(directMessageTCP);
                        cli.getOout().flush();
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(value == 3){
                    GroupMessageTCP groupMessageTCP = new GroupMessageTCP();
                    //directMessageTCP.setChatMessage(nome);
                    try{
                        cli.getOout().writeObject(groupMessageTCP);
                        cli.getOout().flush();
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }while(value > 0 || value < 4);
    }


    public void launch() {
        
        cli.connectGRDS();
        //cli.connectServer(cli.getServerIP(), cli.getServerPort());
        uiMainMenu();
        //TODO else autenticar (autenticar só depois de estar ligado a um servidor)
        //register()

    }
}
