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
    private Server server;
    private final Socket socket;
    private final ObjectOutputStream output;
    private final ObjectInputStream input;

    public Connection(Socket socket) throws IOException {
        this.socket = socket;
        output = new ObjectOutputStream(socket.getOutputStream());
        input = new ObjectInputStream(socket.getInputStream());
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
        //System.out.println("Connection is waiting data to be sent..");
        Object p = input.readObject();

        if(p instanceof SetIdPacket) {
            id = ((SetIdPacket) p).id;
        }
        else if(p instanceof AddPlayerPacket) {
            if(PlayerHandler.players.containsKey(((AddPlayerPacket) p).id))
                return;
            PlayerHandler.addPlayer(this, (AddPlayerPacket) p);
        } else if(p instanceof RemovePlayerPacket) {
            if(server != null)
                server.sendData(p);
            PlayerHandler.removePlayer((RemovePlayerPacket) p);
        } else if(p instanceof UpdatePlayerPack) {
            PlayerHandler.updatePlayerTransform((UpdatePlayerPack) p);
        } else if(p instanceof ServerClosedPacket) {
            App.exit();
        }
    }

    public void sendData(Object data){
        try {
            output.writeObject(data);
            output.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
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
