package server;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import common.packet.apple.SpawnApplePacket;
import common.packet.ServerClosedPacket;
import common.packet.SetMapPacket;
import common.packet.player.AddPacket;
import common.packet.player.IdPacket;
import common.packet.player.UpdateTransformPacket;
import common.Connection;

public class Server {

    //region ------------------------------------ Variables ------------------------------------

    private static int port;
    private static ServerSocket serverSocket;
    private static final Logger LOGGER = Logger.getLogger(Server.class.getName());
    private static final HashMap<Integer, Connection> connections = new HashMap<>();

    public enum State {
        CLOSED,
        CONNECTED,
        LISTENING,
    }
    public static State state = State.CLOSED;

    //endregion

    //region ------------------------------------ Constructors ------------------------------------

    public static void init(int port) {
        setPort(port);
        setLevel(common.Level.getRandomLevel());
        initApples();  
    }

    private static void setPort(int port) {
        Server.port = port;
    }

    private static void setLevel(int level) {
        Tilemap.load(level);
    }

    private static void initApples() {
        AppleManager.spawnApples();
    }

    private static void setState(State state) {
        Server.state = state;
    }

    public static State getState() {
        return state;
    }

    public static boolean isRunning() {
        return state != State.CLOSED;
    }

    public static String getInfo() {
        return "SERVER: " + state + "\n"
        + Tilemap.getInfo() + "\n\n"
        + PlayerList.getInfo();
    }

    //endregion

    //region ------------------------------------ Methods ------------------------------------

    //region ------------------------------------ Socket ------------------------------------

    public static void start() {
        open();
        new Thread() {
            public void run() {
                while (isRunning())
                    listen();
            }
        }.start();
    }

    private static void open() {
        try {
            serverSocket = new ServerSocket(port);
            setState(State.CONNECTED);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to open server socket.\n", e);
            close();
        }
    }

    private static void listen() {
        setState(State.LISTENING);
        if(serverSocket == null || serverSocket.isClosed()) {
            close();
            return;
        }

        try {
            Socket socket = serverSocket.accept();
            startConnection(socket);
        } catch (IOException e) {
            if(!isRunning())
                return;
            LOGGER.log(Level.SEVERE, "Failed to accept client.\n", e);   
        }
    }

    //endregion

    private static void startConnection(Socket socket) {
        if(!isRunning())
            return;
        
        int id = connections.size();
        
        Connection newConnection = new Connection(socket, true); // Create new connection and add to list
        newConnection.sendData(new SetMapPacket(Tilemap.currentLevel)); // Send current level to new player
        sendApplesTo(newConnection);
        sendToAll(new AddPacket(id)); // Send new player to all clients
        connections.put(id, newConnection);
        PlayerList.addPlayer(newConnection, id); // Add player to list
        sendPlayersTo(newConnection); // Send all players to new client not including itself
        newConnection.sendData(new IdPacket(id)); // Send id to new player so it can get its own player object from list

    }

    public static void sendToAll(Object packet) {
        connections.values().forEach((connection) -> connection.sendData(packet));
    }

    public static void sendPlayersTo(Connection connection) {
        connections.forEach((key, value) -> connection.sendData(new AddPacket(key)));
        PlayerList.players.forEach((key, value) -> connection.sendData(new UpdateTransformPacket(value)));
    }
    
    public static void sendApplesTo(Connection connection) {
        AppleManager.apples.forEach((apple) -> connection.sendData(new SpawnApplePacket(apple)));
    }

    public static void close() {
        if(state == State.CLOSED) {
            return;
        }

        closeConnections();
        setState(State.CLOSED);
    }

    private static void closeConnections() {

        sendToAll(new ServerClosedPacket());
        connections.values().forEach(Server::closeConnection);
        connections.clear();

        if(serverSocket == null || serverSocket.isClosed())
            return;

        try {
            serverSocket.close();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to close server socket.\n", e);
        }
    }

    public static void closeConnection(Connection connection) {
        if(connection == null)
            return;
        
        connection.close();
    }

    public static void removeConnection(int id) {
        connections.remove(id);
    }

    //endregion

}
