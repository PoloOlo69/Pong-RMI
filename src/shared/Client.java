package shared;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;


public interface Client extends Remote {
    void play () throws UnsupportedAudioFileException, IOException, LineUnavailableException;

    void setScore(String score) throws RemoteException;

    void sendPos() throws RemoteException;

    void receiveCallback(Point pos) throws RemoteException;

}
