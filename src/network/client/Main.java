package network.client;

import network.Connection;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ConnectException;

public class Main {

    private static final int PORT = 7777;
    private static final String HOST_IP = "192.168.1.8";

    public static void main(String[] args) {

        Client client = new Client(HOST_IP, PORT);

        try {
            client.connectToServer();
        } catch (ConnectException e) {
            System.out.println("The client cannot connect to the server.");
        } catch (IOException e) {
            Connection.logError(e);
        }
    }

}
