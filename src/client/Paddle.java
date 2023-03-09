package client;

import shared.Client;
import shared.Point;
import shared.PongServer;

import javax.sound.sampled.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.net.URL;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Objects;

public class Paddle implements Client, Serializable {

    private static final Color c = new Color(Color.BLACK.getRGB());
    private static final Color w = new Color(Color.WHITE.getRGB());
    private static Clip clip;
    private URL url;
    private static final Font f = new Font("Arial", Font.BOLD, 40);
    private String id;
    private PongServer server;
    private PongPanel panel;
    private int x;
    private int y;
    private int velocity = 7;
    private String score = "0";

    private boolean ready = false;
    public Paddle(String i, PongServer s, PongPanel p, Point pos) throws RemoteException{
        UnicastRemoteObject.exportObject(this,0);
        id = i;
        server = s;
        panel = p;
        x = pos.x();
        y = pos.y();
        soundSetup();
    }

    private void soundSetup(){
        url = Objects.requireNonNull(getClass().getResource("/client/tennisball.wav"));
    }

    @Override
    public void play (){
        try
        {
            AudioInputStream input = AudioSystem.getAudioInputStream(url);
            clip = AudioSystem.getClip();
            clip.open(input);
            clip.start();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void setScore (String score) throws RemoteException{
        this.score = score;
    }

    public void update(){

        if(panel.keyL.r)
            try{ready = true; server.clientReady(id);} catch(Exception e){e.printStackTrace();}

        if(panel.keyL.up&&y-velocity>=0){
            y-=velocity;
        }
        else if(panel.keyL.down&&y+velocity+128<=512){
            y+=velocity;
        }
    }

    public void fetchPos() throws RemoteException{
        server.getPos(id);
    }

    @Override
    public void sendPos() throws RemoteException{
        var pos = record();
        server.callback(id, pos);
    }

    public Point record(){
        return new Point(x,y);
    }

    @Override
    public void receiveCallback (Point pos) throws RemoteException{
        this.x = pos.x();
        this.y = pos.y();
    }

    public void draw(Graphics g) {
        g.setColor(c);
        g.fillRect(x,y,32,128);

        drawScore(g);
    }
    public void drawScore(Graphics g){
        g.setFont(f);
        g.setColor(w);
        Rectangle2D r = g.getFontMetrics().getStringBounds(score, g);
        if(score.equals("YOU WON")) g.setColor(Color.GREEN);
        else if(score.equals("YOU LOST")) g.setColor(Color.RED);

        if(id.equals("client1p1")||id.equals("client2p2"))
        {
            g.drawString(score, (int)((panel.SCREEN_WIDTH-r.getWidth())/4), 32);
        }
        if(id.equals("client2p1")||id.equals("client1p2"))
        {
            g.drawString(score, (int)((panel.SCREEN_WIDTH-r.getWidth())/2+panel.SCREEN_WIDTH/4), 32);
        }
        try
        {
            if(server.client1ready() && server.client2ready()) return;

            g.drawString("Press R to start game!", 32,panel.SCREEN_HEIGHT - 32);
            if(!server.client1ready()) g.setColor(Color.RED);
            else g.setColor(Color.GREEN);
            g.drawString("ready", (int) ((panel.SCREEN_WIDTH-r.getWidth())/4-128), 32);

            if(!server.client2ready()) g.setColor(Color.RED);
            else g.setColor(Color.GREEN);
            g.drawString("ready", (int) ((panel.SCREEN_WIDTH-r.getWidth())/2+panel.SCREEN_WIDTH/4)-128, 32);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
