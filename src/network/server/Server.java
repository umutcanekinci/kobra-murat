//region Imports

package network.server;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import network.Connection;
import network.packet.ServerClosedPacket;
import network.packet.SetMapPacket;
import network.packet.apple.SpawnApplePacket;
import network.packet.player.AddPacket;
import network.packet.player.IdPacket;
import game.Board;

//endregion

public class Server implements Runnable {

    public String ip;
    private int port;
    private ServerSocket serverSocket;
    public Board board;
    private static final Logger LOGGER = Logger.getLogger(Server.class.getName());
    final HashMap<Integer, Connection> connections = new HashMap<>();
    private int currentLevel;

    public enum State {
        CLOSED,
        CONNECTED,
        LISTENING,
    }
    public State state = State.CLOSED;

    //region ------------------------------------ Constructors ------------------------------------

    public Server(int port) {
        PacketHandler.init(this);
        setPort(port);
        setIp();
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    private void setPort(int port) {
        this.port = port;
    }

    private void setIp() {
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            ip = "Unknown";
        }
    }

    //endregion

    //region ------------------------------------ Methods ------------------------------------

    public void start() {
        setLevel(getRandomLevel());
        spawnApples();  
        open();
        new Thread(this).start();
    }

    private void setLevel(int id) {
        currentLevel = id;
    }

    private int getRandomLevel() {
        return (int) Math.random() * game.map.Level.levels.length;
    }

    private void spawnApples() {
        AppleManager.mapData = game.map.Level.levels[currentLevel];
        AppleManager.spawnApples();
    }


    private void open() {
        try {
            serverSocket = new ServerSocket(port);
            setState(State.CONNECTED);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to open server socket.\n", e);
            close();
        }
    }

    private void setState(State state) {
        this.state = state;
        notifyBoard();
    }

    private void notifyBoard() {
        if(board == null)
            return;
        
        switch (state) {
            case CLOSED -> board.onServerClosed();
            case CONNECTED -> board.onServerOpened();
            case LISTENING -> {}
        }
    }

    @Override
    public void run() {
        setState(State.LISTENING);
        while (isRunning()) {
            listen();
        }
    }

    public boolean isRunning() {
        return state != State.CLOSED;
    }

    private void listen() {
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

    private void startConnection(Socket socket) {
        if(!isRunning())
            return;
        
        int id = connections.size();
        
        Connection newConnection = new Connection(socket, true); // Create new connection and add to list
        newConnection.sendData(new SetMapPacket(currentLevel)); // Send current level to new player
        sendApplesTo(newConnection);
        sendToAll(new AddPacket(id)); // Send new player to all clients
        connections.put(id, newConnection);
        sendPlayersTo(newConnection); // Send all players to new client not including itself
        newConnection.sendData(new IdPacket(id)); // Send id to new player so it can get its own player object from list
    }

    public void sendToAll(Object packet) {
        connections.values().forEach((connection) -> connection.sendData(packet));
    }

    public void sendPlayersTo(Connection connection) {
        connections.forEach((key, value) -> connection.sendData(new AddPacket(key)));
    }
    
    public void sendApplesTo(Connection connection) {
        AppleManager.apples.forEach((apple) -> connection.sendData(new SpawnApplePacket(apple)));
    }

    public void close() {
        if(state == State.CLOSED) {
            return;
        }

        closeConnections();
        setState(State.CLOSED);
    }

    private void closeConnections() {

        sendToAll(new ServerClosedPacket());
        connections.values().forEach(this::closeConnection);
        connections.clear();

        if(serverSocket == null || serverSocket.isClosed())
            return;

        try {
            serverSocket.close();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to close server socket.\n", e);
        }
    }

    public void closeConnection(Connection connection) {
        if(connection == null)
            return;
        
        connection.close();
    }

    public void removeConnection(int id) {
        connections.remove(id);
    }

    //endregion

}
