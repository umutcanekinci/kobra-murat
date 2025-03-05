//region Imports

package network.server;
import network.Connection;
import network.PlayerHandler;
import packet.AddPlayerPacket;
import packet.ServerClosedPacket;
import packet.SetIdPacket;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

//endregion

public class Server implements Runnable {

    private final int port;
    public boolean isRunning = false;
    private ServerSocket serverSocket;

    //private ByteBuffer buffer;
    //private Selector selector;

    public ArrayList<String> getDebugInfo() {
        ArrayList<String> info = new ArrayList<>();
        for (NetPlayer player: PlayerHandler.players.values()) {

            info.add(player.name + " (" + player.id + ")");
        }
        info.set(0, "-> " + info.getFirst());
        return info;
    }

    public Server(int port) {
        this.port = port;
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
            System.out.println("Server is created successfully on host " + InetAddress.getLocalHost().getHostName() + " with IP address " + InetAddress.getLocalHost().getHostAddress() + " on port " + port + ".");
            PlayerHandler.addPlayer(null, 0, "Anne Kobra");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        new Thread(this).start();
    }

    @Override
    public void run() {
        isRunning = true;
        //System.out.println("Server started running.");

        try {
            while (isRunning) {
                listen();
            }
        } catch (IOException e) {
            Connection.logError(e);
        }
    }

    private void listen() throws IOException {
        //System.out.println("Server is listening port for clients to connect.");

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
        //System.out.println("Server stopped listening port for clients to connect.");

        */

        Socket socket = serverSocket.accept();
        startConnection(socket);
    }

    private void startConnection(Socket socket) throws IOException {
        //System.out.println(socket.getInetAddress().getHostName() + " with address " + socket.getInetAddress().getHostAddress() + " is connected with port " + socket.getPort() + ".");

        int id = PlayerHandler.players.size();
        String name = "Kobra Murat " + id;
        AddPlayerPacket newPacket = new AddPlayerPacket(id, name);

        Connection newConnection = new Connection(socket);
        newConnection.start();
        newConnection.sendData(new SetIdPacket(id));

        // add new player to the player list
        PlayerHandler.addPlayer(newConnection, id, name);

        // send the new player to all players
        sendData(newPacket);

        // send old players including server side to new player
        for(NetPlayer oldPlayer : PlayerHandler.players.values()) {
            AddPlayerPacket oldPacket = new AddPlayerPacket(oldPlayer.id, oldPlayer.name);
            newConnection.sendData(oldPacket);
        }

    }

    public void sendData(Object data) {
        for(NetPlayer player : PlayerHandler.players.values()) {
            if(player.connection == null)
                continue;
            player.connection.sendData(data);
        }
    }

    /*

    private void acceptClient() throws IOException {
        Socket socket = serverSocket.accept();
        System.out.println(socket + " is connected.");
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
        try {
            if(serverSocket == null || serverSocket.isClosed()) {
                return;
            }

            sendData(new ServerClosedPacket());

            isRunning = false;
            serverSocket.close();

            System.out.println("Server is closed successfully:");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
