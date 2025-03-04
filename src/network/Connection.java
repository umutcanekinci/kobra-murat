package network;

import network.server.EventListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Connection implements Runnable {

    private final Socket socket;
    private final ObjectOutputStream output;
    private final ObjectInputStream input;
    private final EventListener eventListener;

    public Connection(Socket socket) throws IOException {
        this.socket = socket;
        output = new ObjectOutputStream(socket.getOutputStream());
        input = new ObjectInputStream(socket.getInputStream());
        eventListener = new EventListener();
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
        eventListener.received(input.readObject());
    }

    public void sendData(Object data) throws IOException {
        output.writeObject(data);
        output.flush();
    }

    public void close() throws IOException {
        input.close();
        output.close();
        socket.close();
    }

    public static void logError(Exception e) {

    }

}
