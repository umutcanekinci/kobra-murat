package client;

import java.io.IOException;
import java.net.Socket;
import java.lang.Object;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayList;

import common.Connection;
import common.ServerListener;
import common.packet.Packet;
import common.packet.RotatePacket;
import common.packet.basic.DisconnectPacket;
import common.packet.basic.ReadyPacket;
import common.Constants;
import common.Direction;

/**
 * The Client class is responsible for managing the client-side connection to the server.
 * It handles the connection, disconnection, and communication with the server.
 * It also implements the UIListener and ServerListener interfaces to handle UI events and server events.
 * @since 1.0
 * @see UIListener
 * @see ServerListener
 */
public class Client implements UIListener, ServerListener, GameListener {
    
    //region ----------------------------------- Variables -----------------------------------
    
    private static Client INSTANCE;
    private static ClientState state = ClientState.CLOSED;
    private static String host;
    private static int port;
    private static Socket socket;
    private static Connection connection;
    private static final Logger LOGGER = Logger.getLogger(Client.class.getName());
    private static ArrayList<ClientListener> listeners = new ArrayList<>();

    //endregion

    //region ----------------------------------- Constructors -----------------------------------

    /**
     * Private constructor to prevent instantiation.
     * @since 1.0
     */
    private Client() {}

    /**
     * @return The singleton instance of the Client class.
     * @since 1.0
     */
    public static Client getInstance() {
        if(INSTANCE == null)
            INSTANCE = new Client();
            
        return INSTANCE;
    }

    /**
     * Adds a listener to the client.
     * @param clientListener The listener to be added.
     * @throws IllegalArgumentException if the listener is null.
     * @since 1.0
     */
    public static void addListener(ClientListener clientListener) {
        if(clientListener == null)
            throw new IllegalArgumentException("ClientListener cannot be null.");

        listeners.add(clientListener);
    }

    /**
     * Sets the state of the client.
     * @param state The state to be set.
     * @throws IllegalArgumentException if the state is null.
     * @since 1.0
     */
    private static void setState(ClientState state) {
        if(state == null)
            throw new IllegalArgumentException("State cannot be null.");

        Client.state = state;
        switch (state) {
            case CONNECTED -> listeners.forEach(ClientListener::onClientConnected);
            case CLOSED -> listeners.forEach(ClientListener::onClientDisconnected);
        }
    }

    public static void setHost(String host) {
        if(host == null || host.isEmpty())
            throw new IllegalArgumentException("Host cannot be null or empty.");
        
        Client.host = host;
    }

    public static void setPort(int port) {
        if(port <= 0)
            throw new IllegalArgumentException("Port cannot be less than or equal to 0.");

        if(port > 65535)
            throw new IllegalArgumentException("Port cannot be greater than 65535.");
        
        Client.port = port;
    }

    @Override
    public void onServerConnected(String ip) {
        setHost(ip);
        setPort(Constants.PORT);
        start();
    }

    @Override
    public void onServerClosed() {}

    @Override
    public void onServerStartedGame() {}

    @Override
    public void onWindowReady() {}

    @Override
    public void onDirectionChanged(Direction direction) {
        if(!isConnected())
            return;

        sendData(new RotatePacket(PlayerList.getId(), direction));
    }

    @Override
    public void onConnectButtonClicked(String host, int port) {
        if(isConnected())
            return;

        setHost(host);
        setPort(port);

        if(host == null || host.isEmpty())
            throw new IllegalArgumentException("Host cannot be null or empty.");

        if(port <= 0)
            throw new IllegalArgumentException("Port cannot be less than or equal to 0.");

        start();
    }

    @Override
    public void onHostButtonClicked() {}

    @Override
    public void onReadyButtonClicked() {
        if(isConnected())
            return;

        sendData(new ReadyPacket(PlayerList.getId()));
    }

    @Override
    public void onStartButtonClicked() {}

    //endregion

    //region ----------------------------------- Connection -----------------------------------

    /**
     * Starts the client connection to the server in a new thread.
     * @throws IllegalStateException if the client is already connected.
     * @since 1.0
     * @see Client#connect()
     */
    public static void start() {
        if(isConnected())
            throw new IllegalStateException("Client is already connected.");

        new Thread() {
            public void run() {
                connect();
            }
        }.start();
    }
    
    /**
     * Connects the client to the server using the specified host and port.
     * @throws IllegalArgumentException if the host is null or empty, or if the port is less than or equal to 0.
     * @since 1.0
     * @see Socket#Socket(String, int)
     * @see Connection#Connection(Socket, boolean)
     */
    private static void connect() {
        if(isConnected())
            return;

        if(host == null || host.isEmpty())
            throw new IllegalArgumentException("Host cannot be null or empty.");

        if(port <= 0)
            throw new IllegalArgumentException("Port cannot be less than or equal to 0.");
        
        try {
            socket = new Socket(host, port);
            connection = new Connection(socket, false);
            setState(ClientState.CONNECTED);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.toString(), e);
            setState(ClientState.CLOSED);
        }
    }

    /**
     * Sends data to the server.
     * @param data The data to be sent.
     * @throws IllegalArgumentException if the data is null or not an instance of Packet.
     * @throws IllegalStateException if the connection is null or closed.
     * @since 1.0
     * @see Connection#sendData(Object)
     */
    public static void sendData(Object data) {
        if(data == null)
            throw new IllegalArgumentException("Data cannot be null.");

        if(!(data instanceof Packet))
            throw new IllegalArgumentException("Data must be a Packet.");

        if(connection == null)
            throw new IllegalStateException("Connection is null.");

        if(!isConnected())
            return;

        connection.sendData(data);
    }

    public static boolean isConnected() {
        return !(connection == null || socket == null || socket.isClosed() || state == ClientState.CLOSED);
    }

    /**
     * Disconnects the client from the server and closes the connection.
     * Sends a DisconnectPacket to the server before closing the connection.
     * @throws IllegalStateException if the connection is null.
     * @since 1.0
     * @see Connection#sendData(Object)
     * @see Connection#close()
     */
    public static void disconnect() {
        if(connection == null)
            throw new IllegalStateException("Connection is null.");

        if(!isConnected())
            return;

        connection.sendData(new DisconnectPacket(PlayerList.getId()));
        close();
    }

     /**
     * Closes the connection and socket.
     * @throws IllegalStateException if the connection or socket is null.
     * @since 1.0
     * @see Connection#close()
     * @see Socket#close()
     * @see ClientState#CLOSED
     */
    public static void close() {
        if(connection == null || socket == null)
            throw new IllegalStateException("Connection or socket is null.");

        connection.close();
        setState(ClientState.CLOSED);
    }

    /**
     * @return The current state of the client as a string.
     * @since 1.0
     */
    public static String getInfo() {
        return  "CLIENT: " + state + "\n" + 
                "(HOST: " + host + " PORT: " + port + ")\n";
    }

    //endregion
}