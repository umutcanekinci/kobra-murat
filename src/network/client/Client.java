package network.client;

import network.Connection;
import network.PlayerList;
import network.packet.player.DisconnectPacket;
import game.Board;
import java.io.IOException;
import java.net.Socket;

public class Client implements Runnable {
    public Board board;
    private final String host;
    private final int port;
    public State state;

    public void setBoard(Board board) {
        PacketHandler.init(board, this);
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

    public enum State {
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

    private void connect() {
        try {    
            socket = new Socket(host, port);
            connection = new Connection(socket, false);
            setState(State.CONNECTED);
        } catch (IOException e) {
            setState(State.CLOSED);
        }
    }

    public void sendData(Object data) {
        if(!isConnected())
            return;

        connection.sendData(data);
    }

    public boolean isConnected() {
        return !(connection == null || socket == null || socket.isClosed() || state == State.CLOSED);
    }

    public void disconnect() {
        if(!isConnected())
            return;

        connection.sendData(new DisconnectPacket(PlayerList.id));
        close();
    }

    public void close() {
        connection.close();
        setState(State.CLOSED);
    }
}