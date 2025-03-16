package client;

import common.Connection;

import java.io.IOException;
import java.net.Socket;
import java.lang.Object;
import common.packet.player.DisconnectPacket;

public class Client {
    public Board board;
    private static String host;
    private static int port;
    public static State state = State.CLOSED;;

    public static String getInfo() {
        return "CLIENT: " + state;
    }

    private static void setState(State state) {
        Client.state = state;
        switch (state) {
            case CLOSED -> Board.onClientDisconnected();
            case CONNECTED -> Board.onClientConnected();
        }
    }

    public enum State {
        CLOSED,
        CONNECTED
    }

    private static Socket socket;
    private static Connection connection;

    public static void setHost(String host) {
        Client.host = host;
    }

    public static void setPort(int port) {
        Client.port = port;
    }

    public static void start() {
        new Thread() {
            public void run() {
                connect();
            }
        }.start();
    }
    private static void connect() {
        try {    
            socket = new Socket(host, port);
            connection = new Connection(socket, false);
            setState(State.CONNECTED);
        } catch (IOException e) {
            setState(State.CLOSED);
        }
    }

    public static void sendData(Object data) {
        if(!isConnected())
            return;

        connection.sendData(data);
    }

    public static boolean isConnected() {
        return !(connection == null || socket == null || socket.isClosed() || state == State.CLOSED);
    }

    public static void disconnect() {
        if(!isConnected())
            return;

        connection.sendData(new DisconnectPacket(PlayerList.getId()));
        close();
    }

    public static void close() {
        connection.close();
        setState(State.CLOSED);
    }
}