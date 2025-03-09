package network;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import network.packet.client.PacketHandler;

public class Connection implements Runnable {
    
    private static final Logger LOGGER = Logger.getLogger(Connection.class.getName());
    private final Socket socket;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private boolean isServer;

    public Connection(Socket socket, boolean isServer) {
        this.isServer = isServer;
        this.socket = socket;
        
        try {
            output = new ObjectOutputStream(socket.getOutputStream());
            input = new ObjectInputStream(socket.getInputStream());
            start();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to create connection.", e);
        }
    }

    public void start() {
        new Thread(this).start();
    }

    @Override
    public void run() {
        while(socket.isConnected()) {
            readData();
        }
    }
    
    private void readData() {
        Object packet;
        try {
            packet = input.readObject();

            if(isServer)
                network.server.PacketHandler.handle(packet, this);
            else
                PacketHandler.handle(packet, this);
        } catch (Exception e) {
            if(socket.isClosed())
                return;

            LOGGER.log(Level.SEVERE, "Failed to read data from " + (isServer ? "server." : "client."), e);
        }
    }

    public void sendData(Object data){
        if(!socket.isConnected())
            return;

        try {
            synchronized (output) {
                output.writeObject(data);
                output.flush();
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to send data to " + (isServer ? "server." : "client."), e);
        }
    }

    public void close() {
        try {
            input.close();
            output.close();

            if(socket.isClosed())
                return;

            socket.close();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to close connection.", e); 
        }
    }
}
