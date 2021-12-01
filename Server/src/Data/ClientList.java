package Data;

import java.util.ArrayList;

public class ClientList {

    ArrayList<ClientInfo> arrayClientList;

    public ClientList(){
        arrayClientList = new ArrayList<ClientInfo>();
    }

    public boolean checkAddClient(String username, String hostAddress, int port){

//        if(arrayServerList.isEmpty()){ //TODO copy paste do ServerList (não sei se será preciso checks)
//            ServerInfo svInfo = new ServerInfo(hostAddress, port);
//            arrayServerList.add(svInfo);
//            return true;
//        }
//
//        for(ServerInfo svInfo : arrayServerList){
//            if(svInfo.getServerIP().equals(hostAddress) && svInfo.getServerPort() == port)
//                return false;
//        }
//
//        ServerInfo svInfo = new ServerInfo(hostAddress, port);
//        arrayServerList.add(svInfo);

        ClientInfo clInfo = new ClientInfo(username, hostAddress, port);
        arrayClientList.add(clInfo);

        return true;
    }

    public void teste(){
        System.out.println("sou teste e fui chamado");
        for(ClientInfo clInfo : arrayClientList){
            System.out.println(clInfo.getUsername());
        }
    }
}
