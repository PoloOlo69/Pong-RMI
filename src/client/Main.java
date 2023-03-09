package client;

import shared.PongServer;

import javax.swing.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Main {

    public static void main(String... args) throws RemoteException, NotBoundException{
        System.out.println("Welcome " + System.getProperty("user.name") + "!");
        JFrame frame = new JFrame("Pong RMI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        Registry registry = LocateRegistry.getRegistry("localhost", 1099);
        PongServer server = (PongServer)registry.lookup("PongServer");

        PongPanel panel = new PongPanel(server);

        frame.add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        panel.startGame();
    }

}
