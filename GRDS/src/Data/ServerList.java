package Data;
import RMI.ProcessRemoteMessagesRMI;
import UDP.ProcessServerMessagesUDP;

import java.rmi.RemoteException;
import java.util.ArrayList;

public class ServerList {

    public ArrayList<ServerInfo> arrayServerList;

    public ServerList(){
        arrayServerList = new ArrayList<>();
    }

    public void checkAddServer(String hostAddress, int port, ProcessRemoteMessagesRMI processRemoteMessagesRMI) throws RemoteException {

        if(arrayServerList.isEmpty()){
            ServerInfo svInfo = new ServerInfo(hostAddress, port);
            arrayServerList.add(svInfo);
            processRemoteMessagesRMI.serverAdded(svInfo.getServerIP(), svInfo.getServerPort());
            return;
        }

        for(ServerInfo svInfo : arrayServerList){
            if(svInfo.getServerIP().equals(hostAddress) && svInfo.getServerPort() == port)
                return;
        }

        ServerInfo svInfo = new ServerInfo(hostAddress, port);
        arrayServerList.add(svInfo);
        processRemoteMessagesRMI.serverAdded(svInfo.getServerIP(), svInfo.getServerPort());

    }


    public void updateTimeServer(String hostAddress, int port) {
        for(ServerInfo svInfo : arrayServerList){
            if(svInfo.getServerIP().equals(hostAddress) && svInfo.getServerPort() == port)
                svInfo.updateTime();
        }
    }

    public String[] returnAvailableServer() {
        String[] serverIpAndPort = new String[2];
        ServerInfo svInfo;

        do{

            svInfo = arrayServerList.get(0); //saca o primeiro da lista

            serverIpAndPort[0] = svInfo.getServerIP();
            serverIpAndPort[1] = String.valueOf(svInfo.getServerPort());

            arrayServerList.remove(0);
            arrayServerList.add(svInfo); //volta a adicionar, mas no fim da lista (escalonamento circular)

        }while(!svInfo.isActive());

        return serverIpAndPort;
    }

    public void warnServersForFileSynchronization() {

        String[] serverIpAndPort = new String[2];
        ServerInfo svInfo;
        int i = 0;

        do{

            svInfo = arrayServerList.get(i); //saca o primeiro da lista

            serverIpAndPort[0] = svInfo.getServerIP();
            serverIpAndPort[1] = String.valueOf(svInfo.getServerPort());

            i++;

        }while(!svInfo.isActive());

        new ProcessServerMessagesUDP(serverIpAndPort[0], Integer.parseInt(serverIpAndPort[1]));

    }
}
