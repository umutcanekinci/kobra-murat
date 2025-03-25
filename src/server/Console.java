package server;

import common.ServerListener;

public class Console implements ServerListener {

    public void start() {
        Server.start();
        Server.setListener(this);
    }

    public void onServerStateChange(Server.State state) {
        System.out.println(state);
    }

}
