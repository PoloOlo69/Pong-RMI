package server;

import shared.*;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class PongServerImpl implements PongServer {

    public static final int SCREEN_WIDTH = 1024;
    public static final int SCREEN_HEIGHT = 512;
    public final ConcurrentMap<String, Client> clients = new ConcurrentHashMap<>();
    public final ConcurrentMap<String, Point> positions = new ConcurrentHashMap<>();
    public final ConcurrentMap<String, Ball> balls = new ConcurrentHashMap<>();


    public ServerEvent event = new ServerEvent(this);
    public BallRecord ball;
    boolean client1connected = false;
    boolean client2connected = false;
    boolean client1ready = false;
    boolean client2ready = false;
    boolean matchmaking = true;

    int client1score = 0;

    int client2score = 0;

    public PongServerImpl() throws RemoteException{
        UnicastRemoteObject.exportObject(this,0);
        ball = new BallRecord(new Point((SCREEN_WIDTH/2)-16,(SCREEN_HEIGHT/2)-16),0,0);
        System.out.println(ball);
    }

    @Override
    public BallRecord getBall(){
        return ball;
    }

    @Override
    public String initClient () throws RemoteException{

        if(!client1connected)
        {
            client1connected = true;
            System.out.println("hello, client1");
            return "client1";
        }
        else if(!client2connected)
        {
            client2connected = true;
            System.out.println("hello, client2");
            return "client2";
        }
        else
        {
            throw new RemoteException("Server occupied!");
        }
    }

    @Override
    public void addForCallback (String id, Client client) throws RemoteException{
        clients.put(id,client);
        if(clients.size() >= 4){
            broadcast();
            matchmaking=false;
            event.startTimer();
        }
    }
    @Override
    public void clientReady (String client){
        if(client.equals("client1p1")) client1ready = true;
        if(client.equals("client2p1")) client2ready = true;
        if(client1ready && client2ready) newBall();
    }
    @Override
    public boolean client1ready(){
        return client1ready;
    }
    @Override
    public boolean client2ready(){
        return client2ready;
    }
    @Override
    public void addForBroadcast (String id, Ball ball){
        balls.put(id,ball);
    }
    @Override
    public void broadcast() throws RemoteException{
        balls.forEach((k, v)->{
            try
            {
                v.receiveBroadcast(ball);
            }
            catch(RemoteException e)
            {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void getPos(String from)throws RemoteException{
        clients.get(tmm(from)).sendPos();
    }

    @Override
    public void callback (String from, Point pos) throws RemoteException{
        positions.put(from, pos);
        clients.get(tmm(from)).receiveCallback(pos);
    }

    private String tmm(String from) {
        if(from.equals("client1p1")) return "client2p2";
        else if(from.equals("client1p2")) return "client2p1";
        else if(from.equals("client2p1")) return "client1p2";
        else if(from.equals("client2p2")) return "client1p1";
        return "";
    }

    private void score(String client) throws RemoteException{
        if(client.equals("client1p1")) {
            client1score+=1;
            String score = String.valueOf(client1score);
            clients.get(client).setScore(score);
            clients.get("client2p2").setScore(score);
            newBall();
        }
        if(client.equals("client2p1")) {
            client2score+=1;
            String score = String.valueOf(client2score);
            clients.get(client).setScore(score);
            clients.get("client1p2").setScore(score);
            newBall();
        }
        if(client1score >= 10) {
            clients.get("client1p1").setScore("YOU WON");
            clients.get("client1p2").setScore("-");
            clients.get("client2p1").setScore("YOU LOST");
            clients.get("client2p2").setScore("-");
            ball = new BallRecord(new Point((SCREEN_WIDTH/2)-16,(SCREEN_HEIGHT/2)-16),0,0);
        }
        if(client2score>=10) {
            clients.get("client2p1").setScore("YOU WON");
            clients.get("client2p2").setScore("-");
            clients.get("client1p1").setScore("YOU LOST");
            clients.get("client1p2").setScore("-");
            ball = new BallRecord(new Point((SCREEN_WIDTH/2)-16,(SCREEN_HEIGHT/2)-16),0,0);
        }
    }

    private void checkCollision() throws RemoteException{

        Point p1 = positions.get("client1p1");
        Point p2 = positions.get("client2p1");

        if(ball.pos().x()<=0)
            score("client2p1");
        if(ball.pos().x()+16>=SCREEN_WIDTH)
            score("client1p1");
        if(ball.pos().y()<=0)
            ball = new BallRecord(ball.pos(), ball.vX(), -ball.vY());
        if(ball.pos().y()+32>=SCREEN_HEIGHT)
            ball = new BallRecord(ball.pos(), ball.vX(), -ball.vY());

        int x1 = p1.x()+32;
        int y1 = p1.y();
        int y1Max = p1.y()+128;
        if(ball.pos().x() <= x1 && ball.pos().y()>=y1 && ball.pos().y() + 32 <= y1Max)
        {
            ball=new BallRecord(new Point(ball.pos().x()+3, ball.pos().y()), -ball.vX(), ball.vY());
            try{
            clients.get("client1p1").play();
            clients.get("client2p2").play();
            } catch (Exception e) {}
        }
        int x2 = p2.x();
        int y2 = p2.y();
        int y2Max = p2.y()+128;
        if(ball.pos().x()+32 >= x2 && ball.pos().y() >= y2 && ball.pos().y() + 32 <= y2Max){
            ball = new BallRecord(new Point(ball.pos().x()-3,ball.pos().y()),-ball.vX(), ball.vY());
            try{
                clients.get("client2p1").play();
                clients.get("client1p2").play();
            } catch (Exception e) {}
        }
    }

    public void update () throws RemoteException{
        moveBall();
        checkCollision();
        broadcast();
    }

    private void moveBall(){
        int x = ball.pos().x()+ball.vX();
        int y = ball.pos().y()+ball.vY();
        int vX = ball.vX();
        int vY = ball.vY();
        ball = new BallRecord(new Point(x,y),vX,vY);
    }

    private void newBall(){
        Random rand = new Random();
        int vX = rand.nextInt(3,6);
        int vY = rand.nextInt(2,4);
        int x = (SCREEN_WIDTH/2)-16;
        int y = (SCREEN_HEIGHT/2)-16;
        if(rand.nextBoolean()) vX*=-1;
        if(rand.nextBoolean()) vY*=-1;
        ball = new BallRecord(new Point(x,y),vX,vY);
    }

    @Override
    public boolean matchmaking () throws RemoteException{
        return matchmaking;
    }

}