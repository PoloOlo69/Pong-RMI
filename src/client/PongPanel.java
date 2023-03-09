package client;

import shared.PongServer;
import shared.Point;

import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;

public class PongPanel extends JPanel implements Runnable {
    public final int SCREEN_WIDTH = 1048;
    public final int SCREEN_HEIGHT = 512;
    private Thread t;
    public KeyL keyL;
    public BallImpl ball;
    public Paddle player;
    public Paddle opponent;
    public String id;



    public PongPanel(PongServer server) throws RemoteException{
        super();
        initPong(server);
        panelSetup();

    }
    private void panelSetup(){
        keyL = new KeyL();
        addKeyListener(keyL);
        setBackground(Color.DARK_GRAY);
        setPreferredSize(new Dimension(SCREEN_WIDTH,SCREEN_HEIGHT));
        setDoubleBuffered(true);
        setFocusable(true);
    }

    private void initPong (PongServer server) throws RemoteException {
        id = server.initClient();
        if(id.equals("client1")){
            player = new Paddle("client1p1", server, this, new Point(16,SCREEN_HEIGHT/2-64));
            opponent = new Paddle("client1p2", server, this, new Point(SCREEN_WIDTH-48,SCREEN_HEIGHT/2-64));
            ball = new BallImpl("client1",server);
            server.addForBroadcast("client1", ball);
            server.addForCallback("client1p1", player);
            server.addForCallback("client1p2", opponent);
        }
        if(id.equals("client2")){
            player = new Paddle("client2p1", server, this, new Point(SCREEN_WIDTH-48,SCREEN_HEIGHT/2-64));
            opponent = new Paddle("client2p2", server, this, new Point(16,SCREEN_HEIGHT/2-64));
            ball = new BallImpl("client2",server);
            server.addForBroadcast("client2", ball);
            server.addForCallback("client2p1", player);
            server.addForCallback("client2p2", opponent);
        }

        while(true){
            if(server.matchmaking()==false)break;
        }

    }

    @Override
    public void run (){
        // gameloop
        double freq = 1000000000 / 60;
        double next = System.nanoTime() + freq;
        while(t != null){
            try{

                update();

                repaint();

                double remaining = (next - System.nanoTime())/1000000;

                if(remaining<0) remaining = 0;

                Thread.sleep((long)remaining);

                next+=freq;

            } catch(Exception e) {
                e.printStackTrace();
            }
        }

    }
    
    private void update () throws RemoteException{
        player.update();
        opponent.fetchPos();
    }

    public void startGame(){

        t = new Thread(this);
        t.start();

    }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Toolkit.getDefaultToolkit().sync();
        player.drawScore(g);
        player.draw(g);
        opponent.draw(g);
        opponent.drawScore(g);
        ball.draw(g);
    }
}
