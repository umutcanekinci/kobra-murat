package client;

import java.io.IOException;
import java.net.Socket;
import java.lang.Object;
import java.util.logging.Level;
import java.util.logging.Logger;

import common.Connection;
import common.packet.basic.DisconnectPacket;

public class Client {
    
    //region ----------------------------------- Variables -----------------------------------
    
    private enum State {
        CLOSED,
        CONNECTED
    }
    private static State state = State.CLOSED;
    private static String host;
    private static int port;
    private static Socket socket;
    private static Connection connection;
    private static final Logger LOGGER = Logger.getLogger(Client.class.getName());

    //endregion

    //region ----------------------------------- Constructors -----------------------------------

    public static String getInfo() {
        return  "CLIENT: " + state + "\n" + 
                "(HOST: " + host + " PORT: " + port + ")\n";
    }

    private static void setState(State state) {
        Client.state = state;
        switch (state) {
            case CLOSED -> Game.onClientDisconnected();
            case CONNECTED -> Game.onClientConnected();
        }
    }

    public static void setHost(String host) {
        Client.host = host;
    }

    public static void setPort(int port) {
        Client.port = port;
    }

    //endregion

    //region ----------------------------------- Connection -----------------------------------

    public static void start() {
        new Thread() {
            public void run() {
                connect();
            }
        }.start();
    }
    
    private static void connect() {
        try {    
            socket = new Socket(host, port);
            connection = new Connection(socket, false);
            setState(State.CONNECTED);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.toString(), e);
            setState(State.CLOSED);
        }
    }

    public static void sendData(Object data) {
        if(!isConnected())
            return;

        connection.sendData(data);
    }

    public static boolean isConnected() {
        return !(connection == null || socket == null || socket.isClosed() || state == State.CLOSED);
    }

    public static void disconnect() {
        if(!isConnected())
            return;

        connection.sendData(new DisconnectPacket(PlayerList.getId()));
        close();
    }

    public static void close() {
        connection.close();
        setState(State.CLOSED);
    }

    //endregion
}