package client;

import common.Window;
import common.Window.Mode;

/**
 * Main class for the client application.
 * This class is responsible for starting the client application and opening the window.
 * @version 1.0
 */
public class Main {
    /**
     * Main method to start the client application.
     * This method opens the window in client mode.
     * @param args command line arguments
     */
    public static void main(String[] args) {
        Window.open(Mode.CLIENT);
    }
}