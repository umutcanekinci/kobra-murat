package server;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import common.packet.ServerClosedPacket;
import common.packet.player.AddPacket;
import common.packet.player.IdPacket;
import common.Connection;
import common.Utils;

public class Server {

    //region ------------------------------------ Variables ------------------------------------
    
    private static final Logger LOGGER = Logger.getLogger(Server.class.getName());
    private static String ip;
    private static int port;
    private static ServerSocket serverSocket;
    public enum State {
        CLOSED,
        CONNECTED,
        LISTENING,
    }
    public static State state = State.CLOSED;

    //endregion

    //region ------------------------------------ Constructors ------------------------------------

    public static String getInfo() {
        return "SERVER: " + state + "\n" +
        "(IP: " + ip + " PORT: " + port + ")\n" +
        Tilemap.getInfo() + "\n\n" +
        PlayerList.getInfo();
    }

    public static void init(int port) {
        setIp(Utils.getLocalIp());
        setPort(port);
        setLevel(common.Level.getRandomLevel());
        initApples();  
    }

    private static void setIp(String ip) {
        Server.ip = ip;
    }

    private static void setPort(int port) {
        Server.port = port;
    }

    private static void setLevel(int level) {
        Tilemap.load(level);
        AppleManager.setEmptyTiles(Tilemap.getEmptyTiles());
    }

    private static void initApples() {
        AppleManager.spawnAll();
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

    //endregion

    //region ------------------------------------ Methods ------------------------------------

    //region ------------------------------------ Socket ------------------------------------

    public static void start() {
        open();
        new GameManager().start();
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
        
        int id = PlayerList.size();
        
        Connection newConnection = new Connection(socket, true); // Create new connection and add to list
        Tilemap.sendLevel(newConnection); // Send current level to new player // Send current level to new player
        AppleManager.sendAllTo(newConnection);
        PlayerList.sendToAll(new AddPacket(id)); // Send new player to all clients
        PlayerList.addPlayer(newConnection, id); // Add player to list
        PlayerList.sendAllTo(newConnection); // Send all players to new client including itself
        newConnection.sendData(new IdPacket(id)); // Send id to new player so it can get its own player object from list
    }

    public static void close() {
        if(!isRunning())
            return;

        closeConnections();
        setState(State.CLOSED);
    }

    private static void closeConnections() {
        PlayerList.sendToAll(new ServerClosedPacket());
        PlayerList.clear();

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
        PlayerList.players.remove(id);
    }

    //endregion

}
