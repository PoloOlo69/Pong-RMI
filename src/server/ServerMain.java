package server;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServerMain {

    public static void main(String... args) throws RemoteException, AlreadyBoundException{

        System.out.println("Welcome " + System.getProperty("user.name") + "!");

        var server = new PongServerImpl();

        Registry registry = LocateRegistry.createRegistry(1099);

        registry.bind("PongServer", server);

    }
}