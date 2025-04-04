package server;

import common.ServerListener;

public class Console implements ServerListener {

    public void start() {
        Server.addListener(this);
        Server.start();
    }

    @Override
    public void onServerConnected(String ip) {
        System.out.println("Server connected");
    }

    @Override
    public void onServerClosed() {
        System.out.println("Server closed");
    }

    @Override
    public void onServerStartedGame() {
        System.out.println("Server started game");
    }

}
