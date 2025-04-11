package client;

/**
 * Interface for listening to client connection events.
 * This interface is used to notify when a client connects or disconnects.
 * @version 1.0
 */
public interface ClientListener {
    void onClientConnected();
    void onClientDisconnected();
}
