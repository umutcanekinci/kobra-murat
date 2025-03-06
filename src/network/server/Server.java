//region Imports

package network.server;
import network.Connection;
import network.PlayerList;
import packet.AddPlayerPacket;
import packet.ServerClosedPacket;
import packet.SetIdPacket;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

//endregion

enum State {
    CLOSED,
    CONNECTED,
    LISTENING,
}

public class Server implements Runnable {

    private String ip;
    private final int port;
    private ServerSocket serverSocket;
    private State state = State.CLOSED;

    //private ByteBuffer buffer;
    //private Selector selector;

    public ArrayList<String> getDebugInfo() {
        ArrayList<String> info = new ArrayList<>();
        info.add("SERVER [" + state + "]");
        info.add("IP: " + ip);
        info.add("Port: " + port);
        info.add("");
        info.add("Player List:");
        for (NetPlayer player: PlayerList.players.values()) {
            if(player.id == 0)
                info.add("Player " + player.id + " (Host) (You)");
            else
                info.add("Player " + player.id);
        }
        return info;
    }

    public Server(int port) {
        this.port = port;
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            state = State.CLOSED;
        }
            
        //buffer = ByteBuffer.allocate(bufferSize);
    }

    public void open() {

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
            state = State.CONNECTED;
        } catch (IOException e) {
            state = State.CLOSED;
        }
    }

    public void start() {
        new Thread(this).start();
    }

    @Override
    public void run() {
        addHostPlayer();
        state = State.LISTENING;
        try {
            while (isRunning()) {
                listen();
            }
        } catch (IOException e) {
            Connection.logError(e);
        }
    }

    public boolean isRunning() {
        return state != State.CLOSED;
    }

    private void addHostPlayer() {
        PlayerList.addPlayer(null, 0);
    }

    private void listen() throws IOException, NullPointerException {
        state = State.LISTENING;

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
            state = State.CLOSED;
            return;
        }

        Socket socket = serverSocket.accept();
        startConnection(socket);
    }

    private void startConnection(Socket socket) throws IOException {
    
        int id = PlayerList.players.size();
        Connection newConnection = new Connection(socket, this);
        newConnection.start();
        
        newConnection.sendData(new SetIdPacket(id));

        // add new player to the player list
        PlayerList.addPlayer(newConnection, id);

        // send the new player to all players
        AddPlayerPacket newPacket = new AddPlayerPacket(id);
        sendData(newPacket);

        setPlayerList(newConnection);
    }

    private void setPlayerList(Connection connection) {
        for(NetPlayer oldPlayer : PlayerList.players.values()) {
            AddPlayerPacket oldPacket = new AddPlayerPacket(oldPlayer.id);
            connection.sendData(oldPacket);
        }
    }

    public void sendData(Object data) {
        for(NetPlayer player : PlayerList.players.values()) {
            if(player.connection == null)
                continue;
            player.connection.sendData(data);
        }
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
            sendData(new ServerClosedPacket());
            serverSocket.close();
            state = State.CLOSED;
        } catch (IOException e) {
            return;
        }
    }

}
