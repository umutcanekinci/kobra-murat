package client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.HashMap;
import javax.swing.*;

import common.Constants;
import common.Direction;
import common.ServerListener;
import common.Utils;
import common.graphics.Image;
import common.graphics.ui.Button;
import common.packet.RotatePacket;
import common.packet.basic.StartPacket;
import client.graphics.Draw;
import client.graphics.UI;
import server.Server;

public class Game extends JPanel implements ActionListener, KeyListener, ServerListener {

    //region ---------------------------------------- Variables ------------------------------------------

    private static boolean isStarted = false;
    private static boolean debugMode = false;
    private static GridBagConstraints layout; // Https://docs.oracle.com/javase/tutorial/uiswing/layout/visual.html#gridbag

    enum Page {
        MAIN_MENU,
        CONNECT,
        GAME
    }
    private static Page page;
    private static final HashMap<Page, JPanel> pages = new HashMap<>();

    private static final HashMap<Page, Page> backPages = new HashMap<>() {{
        put(Page.CONNECT, Page.MAIN_MENU);
        put(Page.GAME, Page.MAIN_MENU);
    }};

    private static JTextField hostField;
    private static JTextField portField;
    
    //endregion

    //region ---------------------------------------- INIT METHODS ----------------------------------------

    public Game() {
        super(new GridBagLayout());

        Tilemap.loadSheet();
        Player.loadSpritesheet();

        setFullscreen();
        UI.init();
        initServer();
        initLayout();
        initWidgets();
        openPage(Page.MAIN_MENU);
        initTimer();
    }

    private void setFullscreen() {
        setPreferredSize(Constants.SIZE);
    }

    private void initServer() {
        Server.init(Constants.PORT);
        Server.setListener(this);
    }

    private static void initClient() {
        Client.setHost(hostField.getText());
        Client.setPort(Integer.parseInt(portField.getText()));
    }

    private static void initLayout() {
        layout = new GridBagConstraints();
        layout.insets = new Insets(0, 10, 5, 0);
        layout.weighty = 1;
    }

    private void initWidgets() {
        initMainMenu();
        initGame();
        initConnect();
        for (Page p : pages.keySet()) {
            add(pages.get(p), layout);
            pages.get(p).setBackground(Color.BLACK);
        }
    }

    private void initMainMenu() {
        JPanel mainMenu = new JPanel();
        pages.put(Page.MAIN_MENU, mainMenu);

        addImage(mainMenu, Image.MAIN_MENU_BACKGROUND, 0, 0);

        mainMenu.setLayout(new GridBagLayout());
        addButton(mainMenu, "Başla", e -> sendStart());
        addButton(mainMenu, "Bağlan", e -> openPage(Page.CONNECT));
        addButton(mainMenu, "Sunucu Aç", e -> onHostButtonClick());
        addButton(mainMenu, "Çıkış", e -> exit());
        
    }

    private void initConnect() {
        JPanel connect = new JPanel(new GridBagLayout());
        pages.put(Page.CONNECT, connect);

        addButton(connect, "Geri", e -> onBack());
        hostField = new JTextField("localhost");
        hostField.setPreferredSize(new Dimension(200, 60));
        connect.add(hostField, layout);
        portField = new JTextField("7777");
        portField.setPreferredSize(new Dimension(200, 60));
        connect.add(portField, layout);
        addButton(connect, "Bağlan", e -> onConnectButtonClick());
    }

    private void addImage(JPanel panel, BufferedImage image, int x, int y) {
        JLabel label = new JLabel(new ImageIcon(image));
        layout.gridx = x;
        layout.gridy = y;
        panel.add(label, layout);
    }

    private void addButton(JPanel panel, String text, ActionListener listener) {
        Button button = new Button(text, listener);
        panel.add(button, layout);
    }

    private void initGame() {
        JPanel game = new JPanel(new GridBagLayout());
        pages.put(Page.GAME, game);
    }

    private static void openPage(Page page) {
        Game.page = page;
        pages.forEach((p, panel) -> panel.setVisible(p == page));
    }

    //region ---------------------------------------- BUTTON METHODS ----------------------------------------

    private static void sendStart() {   
        if(Client.isConnected())
            sendStartPacket();
        else
            OfflinePlayerController.init();
    }

    public static void start() {
        isStarted = true;
        openPage(Page.GAME);
    }

    private static void sendStartPacket() {
        Client.sendData(new StartPacket(PlayerList.getId()));
    }

    private static void onConnectButtonClick() {
        if(Client.isConnected()) {
            Client.disconnect();
            return;
        }
        initClient();
        connect();
    }

    private static void connect() {
        PlayerList.clear();
        Client.start();
    }

    private static void onHostButtonClick() {
        if(Server.isRunning()) {
            Server.close();
            return;
        }
        Server.start();
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
    }

    public static void onClientDisconnected() {
    }

    public static void onServerOpened() {
    }

    public static void onServerClosed() {
    }

    //endregion

    private void initTimer() {
        new Timer(Constants.DELTATIME_MS, this).start();
    }

    //endregion

    //region ---------------------------------------- INPUT METHODS ---------------------------------------

    public static boolean isStarted() {
        return isStarted;
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {

        if(e.getKeyCode() == KeyEvent.VK_F2)
            debugMode = !debugMode;
        
        if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            onBack();
            return;
        }
            
        if(isStarted)
            keyPressedGame(e);
    }

    private static void onBack() {
        if(page != Page.MAIN_MENU)
            openPage(backPages.get(page));
        else
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
        isStarted = false;
        openPage(Page.MAIN_MENU);
    }

    //endregion

    @Override
    public void actionPerformed(ActionEvent e) {
        if(isStarted)
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
        Draw.all(g2d, this, isStarted, debugMode, this); // Draw everything
    }

}
