package network.client;
import network.Connection;
import network.PlayerList;
import network.packet.PacketHandler;
import network.packet.RemovePlayerPacket;
import game.Board;
import java.io.IOException;
import java.net.Socket;
public class Client implements Runnable {

    public Board board;
    private String host;
    private int port;
    private State state;

    public void setBoard(Board board) {
        this.board = board;
    }

    private void setState(State state) {
        this.state = state;
        if(board != null)
            switch (state) {
                case CLOSED -> board.onClientDisconnected();
                case CONNECTED -> board.onClientConnected();
            }
    }

    private enum State {
        CLOSED,
        CONNECTED
    }

    private Socket socket;
    private Connection connection;

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
        state = State.CLOSED;
    }

    public void start() {
        new Thread(this).start();
    }

    @Override
    public void run() {
        connect();
    }

    private boolean connect() {
        
        PacketHandler.setServer(false);
        
        try {    
            socket = new Socket(host, port);
            connection = new Connection(socket);
            connection.start();
            setState(State.CONNECTED);
            return true;
        } catch (IOException e) {
            setState(State.CLOSED);
            return false;
        }
    }

    public void sendData(Object data) {
        if(connection == null)
            return;

        connection.sendData(data);
    }

    public void disconnect() {
        if(connection == null || socket.isClosed() || state == State.CLOSED)
            return;
        System.out.println("Disconnecting from the server.");
        connection.sendData(new RemovePlayerPacket(PlayerList.id));
        connection.close();
        setState(State.CLOSED);
    }

    public boolean isConnected() {
        return state == State.CONNECTED;
    }
}