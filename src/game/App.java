package game;

import network.Connection;
import network.client.Client;
import network.server.Server;

import javax.swing.*;
import java.io.IOException;
import java.net.ConnectException;

public class App extends JFrame {

    public static String TITLE = "Kobra Murat";
    private static final int PORT = 7777;
    private static final String HOST_IP = "192.168.1.8";
    private final Client client = new Client(HOST_IP, PORT);
    private final Server server = new Server(PORT);

    public App() {

        super(TITLE);

        if(!isThereServer()) {
            openServer();
        }

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Board stuffs
        Board board = new Board();
        add(board);
        addKeyListener(board);

        lock();
        pack();
        centerize();
        display();
        requestFocusInWindow();
    }

    private boolean isThereServer() {
        try {
            client.connectToServer();
            return true;
        } catch (ConnectException e) {
            return false;
        } catch (IOException e) {
            Connection.logError(e);
        }
        return false;
    }

    private void openServer() {
        try {
            server.open();
            server.start();
        } catch (IOException e) {
            Connection.logError(e);
        }
    }

    private void lock() {
        setResizable(false);
    }

    private void centerize() {
        setLocationRelativeTo(null);
    }

    private void display() {
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(App::new);
    }

}
