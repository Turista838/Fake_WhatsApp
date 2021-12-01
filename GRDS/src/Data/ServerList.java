package Data;
import java.util.ArrayList;

public class ServerList {

    ArrayList<ServerInfo> arrayServerList;

    public ServerList(){
        arrayServerList = new ArrayList<ServerInfo>();
    }

    public boolean checkAddServer(String hostAddress, int port){

        if(arrayServerList.isEmpty()){
            ServerInfo svInfo = new ServerInfo(hostAddress, port);
            arrayServerList.add(svInfo);
            return true;
        }

        for(ServerInfo svInfo : arrayServerList){
            if(svInfo.getServerIP().equals(hostAddress) && svInfo.getServerPort() == port)
                return false;
        }

        ServerInfo svInfo = new ServerInfo(hostAddress, port);
        arrayServerList.add(svInfo);
        return true;
    }


    public void updateTimeServer(String hostAddress, int port) {
        for(ServerInfo svInfo : arrayServerList){
            if(svInfo.getServerIP().equals(hostAddress) && svInfo.getServerPort() == port)
                svInfo.updateTime();
        }
        //TODO Passados três períodos (20 em 20 segundos) sem receção de mensagens de um determinado servidor, este é “esquecido” pelo GRDS;
        //TODO isto provavelmente é uma thread à parte que adormece de 20 em 20 segundos
    }

    public String[] returnAvailableServer() {
        String[] serverIpAndPort = new String[2];

        ServerInfo svInfo = arrayServerList.get(0); //saca o primeiro da lista
        serverIpAndPort[0] = svInfo.getServerIP();
        serverIpAndPort[1] = String.valueOf(svInfo.getServerPort());

        arrayServerList.remove(0);
        arrayServerList.add(svInfo); //volta a adicionar, mas no fim da lista (escalonamento circular)

        return serverIpAndPort;
    }
}
