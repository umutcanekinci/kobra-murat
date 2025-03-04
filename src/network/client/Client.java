package network.client;
import network.Connection;

import packet.AddPlayerPacket;
import packet.RemovePlayerPacket;
import network.server.EventListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client {

    private String host;
    private int port;

    private Socket socket;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private EventListener eventListener;
    private Connection connection;

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void connectToServer() throws IOException {
        socket = new Socket(host, port);

        System.out.println("Connected to the server with ip " + host + " on port " + port + ".");

        connection = new Connection(socket);
        connection.start();
    }

    public void enterGame(AddPlayerPacket packet) throws IOException {
        connection.sendData(packet);
    }

    public void disconnect() throws IOException {
        RemovePlayerPacket packet = new RemovePlayerPacket();
        connection.sendData(packet);
        connection.close();
    }
}