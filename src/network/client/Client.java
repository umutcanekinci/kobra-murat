package network.client;
import network.Connection;

import network.PlayerList;
import network.server.NetPlayer;
import packet.RemovePlayerPacket;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class Client implements Runnable {

    private String host;
    private int port;
    private State state;

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
        try {
            socket = new Socket(host, port);
            connection = new Connection(socket);
            connection.start();
            state = State.CONNECTED;
            return true;
        } catch (IOException e) {
            state = State.CLOSED;
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

        try {
            connection.sendData(new RemovePlayerPacket(connection.id));
            connection.close();
        } catch (IOException e) {
            return;
        }
    }

    public boolean isConnected() {
        return state == State.CONNECTED;
    }
    
    public ArrayList<String> getDebugInfo() {
        ArrayList<String> info = new ArrayList<>();
        info.add("CLIENT [" + state + "]");
        info.add("Host: " + host);
        info.add("Port: " + port);
        info.add("");
        info.add("Player List:");
        for (NetPlayer player: PlayerList.players.values()) {
            if(player.id == connection.id)
                info.add("Player " + player.id + " (You)");
            else
                info.add("Player " + player.id);
        }

        return info;
    }


}