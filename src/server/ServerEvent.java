package server;

import java.rmi.RemoteException;

public class ServerEvent implements Runnable{
    private Thread t;
    private PongServerImpl server;
    public ServerEvent(PongServerImpl server){
        this.server = server;
    }
    @Override
    public void run (){
        // gameloop
        double freq = 1000000000 / 90;
        double next = System.nanoTime() + freq;
        while(t != null){
            try{

                task();

                double remaining = (next - System.nanoTime())/1000000;

                if(remaining<0) remaining = 0;

                Thread.sleep((long)remaining);

                next+=freq;
            } catch(Exception e) {
                e.printStackTrace();
            }
        }

    }
    public void startTimer(){

        t = new Thread(this);
        t.start();

    }
    private void task() throws RemoteException{
        server.update();
    }
}
