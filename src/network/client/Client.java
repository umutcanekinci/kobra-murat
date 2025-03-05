package network.client;
import network.Connection;

import network.PlayerHandler;
import network.server.NetPlayer;
import packet.RemovePlayerPacket;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class Client {

    private String host;
    private int port;

    private Socket socket;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private Connection connection;

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public ArrayList<String> getDebugInfo() {
        ArrayList<String> info = new ArrayList<>();
        for (NetPlayer player: PlayerHandler.players.values()) {
            info.add(player.name + " (" + player.id + ")");
        }

        if(!info.isEmpty())
            info.set(0, "-> " + info.getFirst());

        return info;
    }

    public boolean connectToServer() {
        try {
            socket = new Socket(host, port);
            System.out.println("Connected to the server with ip " + host + " on port " + port + ".");
            //connection = new Connection(socket);
            //connection.start();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public void sendData(Object data) {
        if(connection == null)
            return;

        connection.sendData(data);
    }

    public void disconnect() {
        try {
            if(connection == null)
                return;

            connection.sendData(new RemovePlayerPacket(connection.id));
            connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}