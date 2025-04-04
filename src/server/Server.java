package server;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import client.UIListener;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import common.packet.SpawnPacket;
import common.packet.basic.AddPacket;
import common.packet.basic.IdPacket;
import common.packet.basic.ServerClosedPacket;
import common.packet.basic.StartPacket;
import common.Connection;
import common.Constants;
import common.ServerListener;
import common.ServerState;
import common.Utils;

public class Server implements UIListener {

    //region ------------------------------------ Variables ------------------------------------
    
    private static Server INSTANCE;
    private static final Logger LOGGER = Logger.getLogger(Server.class.getName());
    private static String ip = Utils.getLocalIp();
    private static ServerSocket serverSocket;

    private static ServerState state = ServerState.CLOSED;
    private static ArrayList<ServerListener> listeners = new ArrayList<>();

    //endregion

    //region ------------------------------------ Constructors ------------------------------------

    private Server() {}

    public static Server getInstance() {
        if(INSTANCE == null)
            INSTANCE = new Server();
            
        return INSTANCE;
    }

    @Override
    public void onConnectButtonClicked(String host, int port) {}

    @Override
    public void onHostButtonClicked() {
        start();
    }

    @Override
    public void onStartButtonClicked() {
        PlayerList.players.values().forEach(p -> PlayerList.sendToAll(new SpawnPacket(p.getId(), Tilemap.getSpawnPoint(), Constants.DEFAULT_LENGTH)));
        PlayerList.players.values().forEach(p -> p.spawn(Tilemap.getSpawnPoint()));
        PlayerList.sendToAll(new StartPacket());

        listeners.forEach(listener -> listener.onServerStartedGame());
    }

    @Override
    public void onReadyButtonClicked() {}

    public static void addListener(ServerListener listener) {
        if(listener == null)
            return;
        
        Server.listeners.add(listener);
    }

    private static void setState(ServerState state) {
        Server.state = state;
    
        if(state == ServerState.CONNECTED)
            listeners.forEach(listener -> listener.onServerConnected(ip));
        else if(state == ServerState.CLOSED)
            listeners.forEach(listener -> listener.onServerClosed());

    }

    public static boolean isRunning() {
        return state != ServerState.CLOSED;
    }

    public static String getIp() {
        return ip;
    }

    //endregion

    //region ------------------------------------ Methods ------------------------------------

    //region ------------------------------------ Socket ------------------------------------

    public static void start() {
        addListener(Tilemap.getInstance());
        Tilemap.addListener(AppleManager.getInstance());
        addListener(GameManager.getInstance());

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
            serverSocket = new ServerSocket(Constants.PORT);
            setState(ServerState.CONNECTED);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to open server socket.\n", e);
            close();
        }
    }

    private static void listen() {
        setState(ServerState.LISTENING);
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
        Tilemap.sendMap(newConnection); // Send current level to new player // Send current level to new player
        AppleManager.sendAllTo(newConnection);
        PlayerList.sendToAll(new AddPacket(id));// Send new player to all clients
        PlayerList.addPlayer(newConnection, id); // Add player to list
        PlayerList.sendAllTo(newConnection); // Send all players to new client including itself
        newConnection.sendData(new IdPacket(id)); // Send id to new player so it can get its own player object from list
    }

    public static void close() {
        if(!isRunning())
            return;

        closeConnections();
        setState(ServerState.CLOSED);
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

    public static String getInfo() {
        return "SERVER: " + state + "\n" +
        "(IP: " + ip + " PORT: " + Constants.PORT + ")\n" +
        Tilemap.getInfo() + "\n\n" +
        PlayerList.getInfo();
    }

    //endregion

}
