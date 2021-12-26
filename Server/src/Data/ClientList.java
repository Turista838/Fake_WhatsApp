package Data;

import java.io.*;
import java.util.ArrayList;

public class ClientList {

    ArrayList<ClientInfo> arrayClientList;

    public ClientList(){
        arrayClientList = new ArrayList<ClientInfo>();
    }

    public void addClientToClientList(String username, ObjectOutputStream oout) {
        arrayClientList.add(new ClientInfo(username, oout));
        System.out.println("Fui adicionado");
    }

    public ObjectOutputStream getClientOout (String username){
        for(ClientInfo c : arrayClientList){
            if(c.getUsername().equals(username)) {
                System.out.println("encontrei " + c.getUsername());
                return c.getOout();
            }
        }
        return null;
    }

    public ArrayList<ClientInfo> getArrayClientList() { return arrayClientList; }
}
