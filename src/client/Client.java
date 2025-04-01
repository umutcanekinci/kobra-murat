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
import common.packet.basic.DisconnectPacket;
import common.packet.basic.StartPacket;

public class Client implements UIListener, ServerListener {
    
    //region ----------------------------------- Variables -----------------------------------
    
    private static Client INSTANCE = null;
    private static ClientState state = ClientState.CLOSED;
    private static String host;
    private static int port;
    private static Socket socket;
    private static Connection connection;
    private static final Logger LOGGER = Logger.getLogger(Client.class.getName());
    private static ArrayList<ClientListener> listeners = new ArrayList<>();

    //endregion

    //region ----------------------------------- Constructors -----------------------------------

    private Client() {}

    public static Client getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new Client();
        }
        return INSTANCE;
    }

    public static void addListener(ClientListener clientListener) {
        if(clientListener == null)
            throw new IllegalArgumentException("ClientListener cannot be null.");

        listeners.add(clientListener);
    }

    public void onServerConnected(String ip) {
        setHost(ip);
        start();
    }

    public void onServerClosed() {}

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

    //endregion

    //region ----------------------------------- Connection -----------------------------------

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
    public void onStartButtonClicked() {
        if(isConnected())
            return;

        sendData(new StartPacket(PlayerList.getId()));
    }

    public static void start() {
        if(isConnected())
            return;

        new Thread() {
            public void run() {
                connect();
            }
        }.start();
    }
    
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

    public static void sendData(Object data) {
        if(!isConnected())
            return;

        if(data == null)
            throw new IllegalArgumentException("Data cannot be null.");

        if(!(data instanceof Packet))
            throw new IllegalArgumentException("Data must be a Packet.");

        connection.sendData(data);
    }

    public static boolean isConnected() {
        return !(connection == null || socket == null || socket.isClosed() || state == ClientState.CLOSED);
    }

    public static void disconnect() {
        if(!isConnected())
            return;

        connection.sendData(new DisconnectPacket(PlayerList.getId()));
        close();
    }

    public static void close() {
        connection.close();
        setState(ClientState.CLOSED);
    }

    public static String getInfo() {
        return  "CLIENT: " + state + "\n" + 
                "(HOST: " + host + " PORT: " + port + ")\n";
    }

    //endregion
}