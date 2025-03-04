package network.server;

import network.Connection;

import java.io.IOException;

public class Main {

    private static final int PORT = 7777;
    //private static final int BUFFER_SIZE = 8;

    public static void main(String[] args) {

        Server server = new Server(PORT);

        try {
            server.open();
            server.start();
        } catch (IOException e) {
            Connection.logError(e);
        }

    }


}
