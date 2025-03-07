package network;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import network.packet.PacketHandler;

public class Connection implements Runnable {
    
    private static final Logger LOGGER = Logger.getLogger(Connection.class.getName());
    private final Socket socket;
    private ObjectOutputStream output;
    private ObjectInputStream input;

    public Connection(Socket socket) {
        this.socket = socket;
        
        try {
            output = new ObjectOutputStream(socket.getOutputStream());
            input = new ObjectInputStream(socket.getInputStream());
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
            try {
                readData();
            } catch (Exception e) {
                if(socket.isClosed())
                    break;
                LOGGER.log(Level.SEVERE, "Failed to read data from client.", e);
            }
        }
    }
    
    private void readData() {
        try {
            Object packet = input.readObject();
            PacketHandler.handle(packet, this);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to read data from client.", e);
        }
    }

    public void sendData(Object data){
        try {
            synchronized (output) {
                output.writeObject(data);
                output.flush();
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to send data to client.", e);
        }
    }

    public void close() {
        try {
            input.close();
            output.close();
            socket.close();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to close connection.", e); 
        }
    }
}
