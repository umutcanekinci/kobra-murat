package client;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

import client.graphics.Draw;
import client.graphics.UI;
import common.Direction;
import common.Utils;
import common.packet.RotatePacket;
import server.Server;
import server.ServerListener;

public class Game extends JPanel implements ActionListener, KeyListener, ServerListener {

    //region ---------------------------------------- Variables ------------------------------------------

    public static Dimension SIZE;
    public static final int FPS = 60;
    public static final double DELTATIME = 1.0 / FPS;
    public static final boolean isHostInLocal = true;
    
    private static final int DELTATIME_MS = (int) (DELTATIME * 1000);
    private static final int PORT = 7777;
    private static final String HOST_IP = "192.168.1.7";
    private static final ArrayList<JButton> buttons = new ArrayList<>();

    private static boolean debugMode = false;
    private static GridBagConstraints layout; // Https://docs.oracle.com/javase/tutorial/uiswing/layout/visual.html#gridbag
    private static boolean isGameStarted = false;
    
    //endregion

    //region ---------------------------------------- INIT METHODS ----------------------------------------

    public Game() {
        super(new GridBagLayout());
        System.out.println(Direction.LEFT.getAngle());
        System.out.println(Direction.UP.getAngle());
        System.out.println(Direction.RIGHT.getAngle());
        System.out.println(Direction.DOWN.getAngle());

        
        Tilemap.loadSheet();
        Player.loadSpritesheet();

        setFullscreen();
        UI.init();
        initServer();
        initClient();
        initLayout();
        initWidgets();
        initTimer();
    }

    private void setFullscreen() {
        SIZE = Toolkit.getDefaultToolkit().getScreenSize();
        setPreferredSize(SIZE);
    }

    private void initServer() {
        Server.init(PORT);
        Server.setListener(this);
    }

    private static void initClient() {
        Client.setHost(isHostInLocal ? "localhost" : HOST_IP);
        Client.setPort(PORT);
    }

    private static void initLayout() {
        layout = new GridBagConstraints();
        layout.insets = new Insets(0, 10, 5, 0);
        layout.weighty = 1;
    }

    private void initWidgets() {
        addButton("Başla", e -> startGame());
        addButton("Bağlan", e -> onConnectButtonClick());
        addButton("Sunucu Aç", e -> onHostButtonClick());
        addButton("Çıkış", e -> exit());
    }

    private void addButton(String text, ActionListener listener) {
        JButton button = UI.newButton(text);
        button.addActionListener(listener);
        add(button, layout);
        buttons.add(button);
    }

    //region ---------------------------------------- BUTTON METHODS ----------------------------------------

    private static void startGame() {        
        isGameStarted = true;
        OfflinePlayerController.init();
        hideWidgets();
    }

    private static void hideWidgets() {
        for (JButton button : buttons) {
            if(button == null)
                continue;

            button.setVisible(false);
        }
    }

    private static void onConnectButtonClick() {
        disableConnectButton(); // Don't allow to spam clicks, maybe this not needed but just in case
        if(Client.isConnected()) {
            Client.disconnect();
            return;
        }
        connect();
    }

    private static void disableConnectButton() {
        buttons.get(1).setEnabled(false);
    }

    private static void connect() {        
        if(!Server.isRunning()) // Don't let open server if connected to another server
            disableHostButton();
        
        PlayerList.clear();
        AppleManager.clear();
        Client.start();
    }

    private static void onHostButtonClick() {
        disableHostButton(); // Don't allow to spam clicks, maybe this not needed but just in case
        if(Server.isRunning()) {
            Server.close();
            return;
        }
        Server.start();
    }

    private static void disableHostButton() {
        buttons.get(2).setEnabled(false);
    }

    public static void exit() {
        disconnect();
        Window.exit();
    }

    private static void disconnect() {
        if(Server.isRunning()) {
            Server.close(); // Server will close all clients so no need to close the client.
            return;
        }
        Client.disconnect();
    }

    public void onServerStateChange(Server.State state) {
        switch (state) {
            case CONNECTED:
                onServerOpened();
                break;
            case CLOSED:
                onServerClosed();
                break;
            case LISTENING:
                break;
        }
    }

    public static void onClientConnected() {
        JButton button = buttons.get(1);
        button.setText("Bağlantıyı Kes");
        button.setEnabled(true);
    }

    public static void onClientDisconnected() {
        JButton button = buttons.get(1);
        button.setText("Bağlan");
        button.setEnabled(true);
        buttons.get(2).setEnabled(true);
    }

    public static void onServerOpened() {
        JButton button = buttons.get(2);
        button.setText("Sunucuyu Kapat");
        button.setEnabled(true);
    }

    public static void onServerClosed() {
        JButton button = buttons.get(2);
        button.setText("Sunucu Aç");
        button.setEnabled(true);
        buttons.get(1).setEnabled(true);
    }

    //endregion

    private void initTimer() {
        new Timer(DELTATIME_MS, this).start();
    }

    //endregion

    //region ---------------------------------------- INPUT METHODS ---------------------------------------

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {

        if(e.getKeyCode() == KeyEvent.VK_F2)
            debugMode = !debugMode;

        if(!isGameStarted) {
            keyPressedMenu(e);
        }
        else {
            keyPressedGame(e);
        }
    }

    private static void keyPressedMenu(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
            exit();
    }

    private static void keyPressedGame(KeyEvent e) {
        if(!Client.isConnected())
            OfflinePlayerController.keyPressed(e);
        else
            updateDirection(e);
        
        switch (e.getKeyCode()) {
            case KeyEvent.VK_ESCAPE:
                openMenu();
                break;
        }
    }

    private static void updateDirection(KeyEvent e) {
        Direction direction = Utils.keyToDirection(e.getKeyCode());

        if(direction == null)
            return;

        sendDirection(direction);
    }

    private static void sendDirection(Direction direction) {
        Client.sendData(new RotatePacket(PlayerList.getCurrentPlayer().getId(), direction));
    }

    public static void openMenu() {
        isGameStarted = false;
        showWidgets();
    }

    private static void showWidgets() {
        for (JButton button : buttons) {
            if(button == null)
                continue;

            button.setVisible(true);
        }
    }

    //endregion

    @Override
    public void actionPerformed(ActionEvent e) {
        if(isGameStarted)
        {    
            if(!Client.isConnected())
                OfflinePlayerController.update();
        }
        repaint(); // Redraw the screen
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g); // Clear the screen
        Graphics2D g2d = (Graphics2D) g;
        //Camera.draw(g2d, this); // Set the camera
        Draw.all(g2d, this, isGameStarted, debugMode, this); // Draw everything
    }

}
