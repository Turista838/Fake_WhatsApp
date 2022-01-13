package RMI;
import Data.ServerInfo;
import Data.ServerList;
import SharedInterfaces.*;
import java.rmi.RemoteException;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ProcessRemoteMessagesRMI extends UnicastRemoteObject implements ProcessRemoteMessagesRMIInterface {

    private List<MainRemoteInterface> remoteObservers;
    private ServerList serverList;

    public ProcessRemoteMessagesRMI(ServerList serverList, List<MainRemoteInterface> remoteObservers) throws RemoteException {
        this.remoteObservers = remoteObservers;
        this.serverList = serverList;
    }

    public synchronized void addObserver(MainRemoteInterface observer) throws RemoteException {
        if(!remoteObservers.contains(observer)){
            remoteObservers.add(observer);
            System.out.println("+ um observador.");

        }
    }

    public synchronized void removeObserver(MainRemoteInterface observer) throws RemoteException
    {
        if(remoteObservers.remove(observer))
            System.out.println("- um observador.");
    }

    public synchronized void requestServerList(MainRemoteInterface observer) throws RemoteException {
        ArrayList<String> results = new ArrayList<>();

        results.add("---------------------------------");
        results.add("Lista de Servidores activos: \n");

        for(ServerInfo svInfo : serverList.arrayServerList){
            results.add("## Servidor");
            if(svInfo.isActive()){
                results.add("IP: " + svInfo.getServerIP());
                results.add("PORTO: " + svInfo.getServerPort());
                results.add("Ultima vez online: " + svInfo.getLastTimeOnline().getTime().getHours() + ":" + svInfo.getLastTimeOnline().getTime().getMinutes() + ":" + svInfo.getLastTimeOnline().getTime().getSeconds());
            }
            results.add("");
        }
        results.add("---------------------------------");
        observer.operationResult(results);
    }

    public synchronized void serverAdded(String ip, int port) throws RemoteException {
        ArrayList<String> results = new ArrayList<>();

        for(int i = 0; i < remoteObservers.size(); i++){
            try{
                results.add("Servidor: ");
                results.add("IP: " + ip);
                results.add("PORTO: " + String.valueOf(port));
                results.add("Foi ADICIONADO à lista do GRDS");
                remoteObservers.get(i).operationResult(results);
                results.clear();
            }catch(RemoteException e){
                remoteObservers.remove(i--);
                System.out.println("- um observador (observador inacessivel).");
            }
        }
    }

    public synchronized void serverRemoved(String ip, int port) throws RemoteException {
        ArrayList<String> results = new ArrayList<>();

        for(int i = 0; i < remoteObservers.size(); i++){
            try{
                results.add("Servidor: ");
                results.add("IP: " + ip);
                results.add("PORTO: " + String.valueOf(port));
                results.add("Foi REMOVIDO da lista do GRDS");
                remoteObservers.get(i).operationResult(results);
                results.clear();
            }catch(RemoteException e){
                remoteObservers.remove(i--);
                System.out.println("- um observador (observador inacessivel).");
            }
        }
    }

    public synchronized void clientConnected(String ip, int port) throws RemoteException {
        ArrayList<String> results = new ArrayList<>();

        for(int i = 0; i < remoteObservers.size(); i++){
            try{
                results.add("Cliente: ");
                results.add("IP: " + ip);
                results.add("PORTO: " + String.valueOf(port));
                results.add("Conectou-se ao GRDS");
                remoteObservers.get(i).operationResult(results);
                results.clear();
            }catch(RemoteException e){
                remoteObservers.remove(i--);
                System.out.println("- um observador (observador inacessivel).");
            }
        }
    }


}
