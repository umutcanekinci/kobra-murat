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
import network.packet.client.*;
import game.Board;


//endregion

enum State {
    CLOSED,
    CONNECTED,
    LISTENING,
}

public class Server implements Runnable {

    public State state = State.CLOSED;
    public String ip;
    private int port;
    private ServerSocket serverSocket;
    public Board board;
    private static final Logger LOGGER = Logger.getLogger(Server.class.getName());
    HashMap<Integer, Connection> connections = new HashMap<>();
    //private ByteBuffer buffer;
    //private Selector selector;

    public void setBoard(Board board) {
        this.board = board;
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

    public Server(int port) {
        PacketHandler.init(this);
        setPort(port);
        setIp();
        //buffer = ByteBuffer.allocate(bufferSize);
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

    public void start() {
        open();
        new Thread(this).start();
    }

    private void open() {

        /*

        ServerSocketChannel serverChannel;
        serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);
        serverSocket = serverChannel.socket();

        InetSocketAddress address = new InetSocketAddress(port);
        serverSocket.bind(address);
        selector = Selector.open();
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);

         */

        try {
            serverSocket = new ServerSocket(port);
            setState(State.CONNECTED);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to open server socket.", e);
            close();
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
        /*
        Set<SelectionKey> keys = selector.selectedKeys();

        for (SelectionKey key : keys) {
            boolean isAccept = (key.readyOps() & SelectionKey.OP_ACCEPT) == SelectionKey.OP_ACCEPT;
            boolean isRead = (key.readyOps() & SelectionKey.OP_READ) == SelectionKey.OP_READ;

            if (isAccept) {
                acceptClient();
            } else if (isRead) {
                readData(key);
            }

            keys.clear();
        }


        */

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
            LOGGER.log(Level.SEVERE, "Failed to accept client.", e);   
        }
    }

    private void startConnection(Socket socket) {
        if(!isRunning())
            return;
        
        int id = connections.size();
        
        Connection newConnection = new Connection(socket, true); // Create new connection and add to list
        sendToAll(new AddPacket(id)); // Send new player to all clients
        connections.put(id, newConnection);
        sendAllPlayersTo(newConnection); // Send all players to new client not including itself
        newConnection.sendData(new IdPacket(id)); // Send id to new player so it can get its own player object from list
    }

    public void sendToAll(Object packet) {
        connections.values().forEach((connection) -> connection.sendData(packet));
    }

    public void sendAllPlayersTo(Connection connection) {
        connections.entrySet().forEach((set) -> connection.sendData(new AddPacket(set.getKey())));
    }
    
    /*

    private void acceptClient() throws IOException {
        Socket socket = serverSocket.accept();
        
        SocketChannel socketChannel = socket.getChannel();
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);
    }

    private void readData(SelectionKey key) throws IOException {
        SocketChannel channel = null;
        channel = (SocketChannel) key.channel();
        boolean connection = readData(channel, buffer);

        if (!connection) {
            key.cancel();
            Socket socket = null;
            socket = channel.socket();
            socket.close();
        }
    }

    private boolean readData(SocketChannel channel, ByteBuffer buffer) {
        return true;
    }

    */

    public void close() {
        if(state == State.CLOSED) {
            return;
        }

        closeConnections();
        setState(State.CLOSED);
    }

    private void closeConnections() {

        sendToAll(new ServerClosedPacket());
        connections.values().forEach((connection) -> closeConnection(connection));
        connections.clear();

        if(serverSocket == null || serverSocket.isClosed())
            return;

        try {
            serverSocket.close();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to close server socket.", e);
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

}
