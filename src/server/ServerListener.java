package server;

public interface ServerListener {

    void onServerStateChange(Server.State state);
    
}
