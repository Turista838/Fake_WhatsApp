package Data;

import RMI.ProcessRemoteMessagesRMI;
import UDP.ProcessServerMessagesUDP;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class ServerTimeController extends Thread {

    private ProcessRemoteMessagesRMI processRemoteMessagesRMI;
    private ArrayList<Integer> indexes;
    private int index;
    private ServerList serverList;

    public ServerTimeController(ServerList serverList, ProcessRemoteMessagesRMI processRemoteMessagesRMI) throws RemoteException{
        indexes = new ArrayList<Integer>();
        this.serverList = serverList;
        this.processRemoteMessagesRMI = processRemoteMessagesRMI;
        start();
    }

    public void run(){

        while(true){

            try {

                if(!serverList.arrayServerList.isEmpty()){

                    Calendar time;
                    index = 0;
                    for(ServerInfo svInfo : serverList.arrayServerList){

                        time = GregorianCalendar.getInstance();
                        if(time.getTimeInMillis() - svInfo.getLastTimeOnline().getTimeInMillis() > 20000) // inativo
                        {
                            System.out.println("Inactivo: " + svInfo.getServerIP() + " | " + svInfo.getServerPort());
                            svInfo.setActive(false);
                        }
                        else {
                            if(!svInfo.isActive()){
                                svInfo.setActive(true);
                                new ProcessServerMessagesUDP(svInfo.getServerIP(), svInfo.getServerPort());
                            }
                        };

                        if(time.getTimeInMillis() - svInfo.getLastTimeOnline().getTimeInMillis() > 60000) // remove
                        {
                            System.out.println("Removi: " + svInfo.getServerIP() + " | " + svInfo.getServerPort());
                            processRemoteMessagesRMI.serverRemoved(svInfo.getServerIP(), svInfo.getServerPort());
                            indexes.add(index);
                        }
                        index++;
                    }

                }

                for(Integer i : indexes) {
                    System.out.println("removi index: " + i);
                    serverList.arrayServerList.remove(i.intValue());
                }

                indexes.clear();

                Thread.sleep(20000);

            } catch (InterruptedException | RemoteException e) {
                e.printStackTrace();
            }
        }
    }

}
