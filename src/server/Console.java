package server;

import common.ServerListener;

public class Console implements ServerListener {

    public void start() {
        Server.start();
        Server.addListener(this);
    }

    public void onServerConnected(String ip) {
        System.out.println("Server connected");
    }

    public void onServerClosed() {
        System.out.println("Server closed");
    }

}
