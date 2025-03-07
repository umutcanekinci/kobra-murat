//region Imports

package network.server;
import network.Connection;
import network.PlayerList;
import network.packet.AddPlayerPacket;
import network.packet.PacketHandler;
import network.packet.ServerClosedPacket;
import network.packet.SetIdPacket;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import game.Board;
import game.player.NetPlayer;
import java.util.logging.Level;
import java.util.logging.Logger;

//endregion

enum State {
    CLOSED,
    CONNECTED,
    LISTENING,
}

public class Server implements Runnable {

    private State state = State.CLOSED;
    public String ip;
    private int port;
    private ServerSocket serverSocket;
    public Board board;
    private static final Logger LOGGER = Logger.getLogger(Server.class.getName());
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

         PacketHandler.setServer(true);

        try {
            serverSocket = new ServerSocket(port);
            setState(State.CONNECTED);
        } catch (IOException e) {
            setState(State.CLOSED);
        }
    }

    @Override
    public void run() {
        addHostPlayer();
        setState(State.LISTENING);
        while (isRunning()) {
            listen();
        }
    }

    public boolean isRunning() {
        return state != State.CLOSED;
    }

    private void addHostPlayer() {
        PacketHandler.handle(new SetIdPacket(0), null);
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
            setState(State.CLOSED);
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

        int id = PlayerList.players.size();
        Connection newConnection = new Connection(socket);
        newConnection.start();
        
        newConnection.sendData(new SetIdPacket(id));

        // add new player to the player list
        PacketHandler.handle(new AddPlayerPacket(id), newConnection);

        // send the new player to all players
        AddPlayerPacket newPacket = new AddPlayerPacket(id);
        PlayerList.sendToAll(newPacket);

        sendPlayerList(newConnection);
    }

    private void sendPlayerList(Connection connection) {
        for(NetPlayer player : PlayerList.players.values()) {
            sendPlayer(player, connection);
        }
    }

    private void sendPlayer(NetPlayer player, Connection receiver) {
        if(player == null || receiver == null)
            return;
        
        AddPlayerPacket packet = new AddPlayerPacket(player.id);
        receiver.sendData(packet);
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
        if(serverSocket == null || serverSocket.isClosed() || state == State.CLOSED) {
            return;
        }

        try {
            PlayerList.sendToAll(new ServerClosedPacket());
            PlayerList.clear();
            serverSocket.close();
            
            setState(State.CLOSED);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to close server.", e);
        }
    }

}
