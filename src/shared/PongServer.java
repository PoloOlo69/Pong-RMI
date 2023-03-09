package shared;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface PongServer extends Remote {
    String initClient() throws RemoteException;
    BallRecord getBall() throws RemoteException;
    void addForCallback (String id, Client client) throws RemoteException;
    void clientReady (String client)throws RemoteException;
    boolean client1ready() throws RemoteException;
    boolean client2ready() throws RemoteException;
    void addForBroadcast (String id, Ball ball) throws RemoteException;
    void broadcast() throws RemoteException;
    void getPos(String from) throws RemoteException;
    void callback(String from, Point pos) throws RemoteException;
    boolean matchmaking() throws RemoteException;
}