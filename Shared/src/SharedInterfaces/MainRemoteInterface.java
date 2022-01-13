package SharedInterfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface MainRemoteInterface extends Remote {

    public void operationResult(ArrayList<String> results) throws RemoteException;

}
