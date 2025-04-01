package common;

public interface ServerListener {
    void onServerConnected(String ip);
    void onServerClosed();
}
