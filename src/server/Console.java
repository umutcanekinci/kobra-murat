package server;

import common.Constants;
import common.ServerListener;

public class Console implements ServerListener {

    public void start() {
        Server.init(Constants.PORT);
        Server.start();
        Server.setListener(this);
    }

    public void onServerStateChange(Server.State state) {
        System.out.println(state);
    }

}
