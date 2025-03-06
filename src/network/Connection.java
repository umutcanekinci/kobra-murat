package network;

import game.App;
import network.server.Server;
import packet.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


public class Connection implements Runnable {
    
    public int id;
    private final Socket socket;
    private Server server;
    private final ObjectOutputStream output;
    private final ObjectInputStream input;

    public Connection(Socket socket) throws IOException {
        this.socket = socket;
        output = new ObjectOutputStream(socket.getOutputStream());
        input = new ObjectInputStream(socket.getInputStream());
    }

    public Connection(Socket socket, Server server) throws IOException {
        this(socket);
        this.server = server;
    }

    public void start() {
        new Thread(this).start();
    }

    @Override
    public void run() {
        while(socket.isConnected()) {
            try {
                readData();
            } catch (Exception e) {
                logError(e);
            }
        }
    }

    private void readData() throws IOException, ClassNotFoundException {
        Object p = input.readObject();

        if(server == null)
        { // Client side packets
            if(p instanceof SetIdPacket) {
                id = ((SetIdPacket) p).id;
            }
            else if(p instanceof AddPlayerPacket) {
                if(PlayerList.players.containsKey(((AddPlayerPacket) p).id))
                    return;
                PlayerList.addPlayer(this, (AddPlayerPacket) p);
            } 
            else if(p instanceof ServerClosedPacket) {
                App.exit();
            }
        }
        // Both client and server packets
        if(p instanceof RemovePlayerPacket) {
            PlayerList.removePlayer((RemovePlayerPacket) p);
            if(server != null) {
                server.sendData(p);
            }
        } else if(p instanceof UpdatePlayerPack) {
            PlayerList.updatePlayerTransform((UpdatePlayerPack) p);
            if(server != null) {
                server.sendData(p);
            }
        }
    }

    public void sendData(Object data){
        try {
            output.writeObject(data);
            output.flush();
        } catch (IOException e) {
            System.out.println("Failed to send data to client.");
            System.out.println(e.toString());
        }
    }

    public void close() throws IOException {
        input.close();
        output.close();
        socket.close();
    }

    public static void logError(Exception e) {

    }

}
