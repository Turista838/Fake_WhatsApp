import SharedInterfaces.*;

import java.io.IOException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MainRemote extends UnicastRemoteObject implements MainRemoteInterface
{

    public MainRemote() throws RemoteException {}

    public void operationResult(ArrayList<String> results) throws RemoteException
    {
        for(String msg : results)
            System.out.println(msg);
        System.out.println();
    }

    public static void main(String[] args) {

        int value;
        Scanner s = new Scanner(System.in);

        try{
            MainRemote observer = new MainRemote();

            if(args.length != 2){
                System.out.println("Arguments needed: <IP GRDS> <RMI SERVICE NAME>");
                System.exit(1);
            }

            String objectUrl = "rmi://" + args[0] + "/" + args[1]; //rmiregistry on localhost

            ProcessRemoteMessagesRMIInterface processRemoteMessagesRMI = (ProcessRemoteMessagesRMIInterface) Naming.lookup(objectUrl);

            processRemoteMessagesRMI.addObserver(observer);

            System.out.println();
            System.out.println("\n### MainRemore initiated ###\n");
            System.out.println();
            System.out.println("Escolha uma Operação:");
            System.out.println("1 - Listar Servidores Activos");
            System.out.println("2 - Sair");
            System.out.print("> ");

            while (!s.hasNextInt()) s.next(); //é para avançar só se pusermos um inteiro

            do{
                value = s.nextInt();
                if(value < 1 || value > 2)
                    System.out.println("Introduza num 1 a 2");
                else{
                    if(value == 1){
                        processRemoteMessagesRMI.requestServerList(observer);
                    }
                    if(value == 2) {
                        processRemoteMessagesRMI.removeObserver(observer);
                        System.exit(0);
                    }
                }
            }while(value != 2);

        }catch(RemoteException e){
            e.printStackTrace();
            System.exit(2);
        }catch(IOException | NotBoundException e){
            e.printStackTrace();
            System.exit(3);
        }

    }

}
