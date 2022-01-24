package SharedInterfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.ServerNotActiveException;

public interface ProcessRemoteMessagesRMIInterface  extends Remote
{
    public void addObserver(MainRemoteInterface observer) throws RemoteException;
    public void removeObserver(MainRemoteInterface observer) throws RemoteException;
    public void requestServerList(MainRemoteInterface observer) throws RemoteException;

}