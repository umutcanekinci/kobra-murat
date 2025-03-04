//region Imports

package network.server;
import network.Connection;
import packet.AddPlayerPacket;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

//endregion

public class Server implements Runnable {

    private final int port;
    private boolean isRunning = false;
    private ServerSocket serverSocket;
    private HashMap<Integer, Connection> connections;

    //private ByteBuffer buffer;
    //private Selector selector;

    public Server(int port) {
        this.port = port;
        connections = new HashMap<>();
        //buffer = ByteBuffer.allocate(bufferSize);
    }

    public void open() throws IOException {
        serverSocket = new ServerSocket(port);

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

        ClientHandler.players.put(0, new NetPlayer(0, "Anne Kobra"));

        System.out.println("Server is created successfully on host " + InetAddress.getLocalHost().getHostName() + " with IP address " + InetAddress.getLocalHost().getHostAddress() + " on port " + port + ".");
    }

    public void start() {
        new Thread(this).start();
    }

    @Override
    public void run() {
        isRunning = true;
        //System.out.println("Server started running.");

        while (isRunning) {
            try {
                listen();
            } catch (IOException e) {
                Connection.logError(e);
            }
        }
    }

    private void listen() throws IOException {
        //System.out.println("Server is listening port for clients to connect..");

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
        System.out.println(socket.getInetAddress().getHostName() + " with address " + socket.getInetAddress().getHostAddress() + " is connected with port " + socket.getPort() + ".");

        int id = ClientHandler.players.size();
        String name = "Kobra Murat " + id;
        AddPlayerPacket newPacket = new AddPlayerPacket(id, name);

        Connection newConnection = new Connection(socket);

        // add newConnection to the list
        connections.put(id, newConnection);

        newConnection.start();

        // send the new player to all players
        for(Connection connection : connections.values()) {
            connection.sendData(newPacket);
        }

        // send old players including server side to new player
        for(NetPlayer oldPlayer : ClientHandler.players.values()) {
            AddPlayerPacket oldPacket = new AddPlayerPacket(oldPlayer.id, oldPlayer.name);
            newConnection.sendData(oldPacket);
        }

        // add new player to the player list
        NetPlayer netPlayer = new NetPlayer(id, name);
        ClientHandler.players.put(id, netPlayer);

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


    public void close() throws IOException {
        if(!isRunning) {
            System.out.println("Server can't be closed when it is not working.");
            return;
        }

        isRunning = false;
        serverSocket.close();
        System.out.println("Server is closed successfully:");
    }

}
