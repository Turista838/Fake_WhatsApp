package Data;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class ServerList {

    public ArrayList<ServerInfo> arrayServerList;

    public ServerList(){
        arrayServerList = new ArrayList<>();
    }

    public void checkAddServer(String hostAddress, int port){

        if(arrayServerList.isEmpty()){
            ServerInfo svInfo = new ServerInfo(hostAddress, port);
            arrayServerList.add(svInfo);
            return;
        }

        for(ServerInfo svInfo : arrayServerList){
            if(svInfo.getServerIP().equals(hostAddress) && svInfo.getServerPort() == port)
                return;
        }

        ServerInfo svInfo = new ServerInfo(hostAddress, port);
        arrayServerList.add(svInfo);

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
}
