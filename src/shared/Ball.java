package shared;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Ball extends Remote {

    void receiveBroadcast(BallRecord br) throws RemoteException;
}
