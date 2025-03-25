package common;

import server.Server;

public interface ServerListener {
    void onServerStateChange(Server.State state);   
}
