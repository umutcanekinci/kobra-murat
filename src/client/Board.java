package client;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

import client.graphics.Draw;
import client.graphics.UI;
import common.Direction;
import common.Utils;
import common.packet.player.RotatePacket;
import server.Server;
import server.ServerListener;

public class Board extends JPanel implements ActionListener, KeyListener, ServerListener {

    //region ---------------------------------------- Variables ------------------------------------------

    public static final int FPS = 60;
    public static final double DELTATIME = 1.0 / FPS;
    public static final int DELTATIME_MS = (int) (DELTATIME * 1000);
    public static final int PORT = 7777;
    public static final String HOST_IP = "192.168.1.7";
    public static final boolean isHostInLocal = true;
    public static Tilemap map;
    
    private static boolean debugMode = false;
    private static final Color MENU_BACKGROUND_COLOR = Color.BLACK;
    private static final Color BACKGROUND_COLOR = Color.BLACK;
    private static GridBagConstraints layout; // Https://docs.oracle.com/javase/tutorial/uiswing/layout/visual.html#gridbag
    private static boolean isGameStarted = false;
    private static final ArrayList<JButton> buttons = new ArrayList<>();

    //endregion

    //region ---------------------------------------- INIT METHODS ----------------------------------------

    public Board() {
        super(new GridBagLayout());
        setPreferredSize(common.Level.SIZE);

        Player.loadSpritesheet();
        UI.init();
        initServer();
        initClient();
        initLayout();
        initWidgets();
        initTimer();
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

    //region ---------------------------------------- EVENT METHODS ---------------------------------------

    public static void setMap(int id) {
        Tilemap.load(id);
        AppleManager.setEmptyTiles(Tilemap.getEmptyTiles());
    }

    public static void onIdSetted() {
        initPlayer();
    }

    private static void initPlayer() {
        NetPlayer player = PlayerList.getCurrentPlayer();

        if(player == null)
            return;
            
        player.setSpawnPoint();
        OfflinePlayerController.setPlayer(player);

        /*player.setDirection(Direction.RIGHT);
        player.reset();
        player.setPosition(Tilemap.getSpawnPoint());
        player.rotateHeadTransform();*/
    }

    //endregion

    //region ---------------------------------------- INPUT METHODS ---------------------------------------

    @Override
    public void keyTyped(KeyEvent e) {
        // this is not used but must be defined as part of the KeyListener interface
    }

    @Override
    public void keyPressed(KeyEvent e) {

        if(e.getKeyCode() == KeyEvent.VK_F2) {
            debugMode = !debugMode;
        }

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
        else {
            Direction direction = Utils.keyToDirection(e.getKeyCode());
            if(direction != null)
                sendDirection(direction);
        }
        
        switch (e.getKeyCode()) {
            case KeyEvent.VK_ESCAPE:
                openMenu();
                break;
        }
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

    @Override
    public void keyReleased(KeyEvent e) {
        // react to key up events
    }

    //endregion

    //region ---------------------------------------- UPDATE METHODS --------------------------------------

    @Override
    public void actionPerformed(ActionEvent e) {
        if(isGameStarted)
            OfflinePlayerController.update();

        repaint();
    }

    //endregion

    //region ---------------------------------------- DRAW METHODS ----------------------------------------

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawBackground();
        Draw.all(g, isGameStarted, debugMode, this);
    }

    private void drawBackground() {
        setBackground(isGameStarted ? BACKGROUND_COLOR : MENU_BACKGROUND_COLOR);
    }

    //endregion

}
