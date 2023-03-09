package client;

import shared.Ball;
import shared.BallRecord;
import shared.PongServer;

import java.awt.*;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class BallImpl implements Ball, Serializable {

    private final Color c = new Color(Color.WHITE.getRGB());
    private String id;
    public int xPos;
    public int yPos;
    public int vX;
    public int vY;

    public PongServer server;

    public BallImpl(String id, PongServer server) throws RemoteException{
        UnicastRemoteObject.exportObject(this,0);
        this.id = id;
        this.server = server;
        var b = server.getBall();
        receiveBroadcast(b);
    }
    @Override
    public void receiveBroadcast (BallRecord br) throws RemoteException{
        this.xPos = br.pos().x();
        this.yPos = br.pos().y();
        this.vX = br.vX();
        this.vY = br.vY();
    }
    public void draw(Graphics g) {
        g.setColor(c);
        g.drawRoundRect(xPos, yPos,32,32,32,32);
        g.fillRoundRect(xPos, yPos,32,32,32,32);
    }

    public void update(BallRecord br){
        this.xPos = br.pos().x();
        this.yPos = br.pos().y();
        this.vX = br.vX();
        this.vY = br.vY();
    }
    public void update() throws RemoteException{
        var b = server.getBall();
        update(b);
    }


}
