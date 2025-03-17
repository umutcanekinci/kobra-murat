package client;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

import client.graphics.Draw;
import client.graphics.UI;
import server.Server;

public class Board extends JPanel implements ActionListener, KeyListener {

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

    private static void initServer() {
        Server.init(PORT);
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
        addButton("Sunucu Aç", e -> host());
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
        if(Client.isConnected()) {
            disconnect();
            return;
        }
        connect();
    }

    private static void connect() {
        buttons.get(1).setEnabled(false); // Don't allow to spam clicks
        
        if(!Server.isRunning()){ // Don't let open server if connected to another server
            buttons.get(2).setEnabled(false);
        }
        
        PlayerList.clear();
        AppleManager.clear();
        Client.start();
    }

    private static void host() {
        if(Server.isRunning())
            return;

        buttons.get(2).setEnabled(false); // Don't allow to spam clicks
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

        for(ActionListener listener : button.getActionListeners()) 
            button.removeActionListener(listener);
        button.addActionListener(e -> Server.close());

        button.setText("Sunucuyu Kapat");
        button.setEnabled(true);
    }

    public static void onServerClosed() {
        JButton button = buttons.get(2);
        addListenerToButton(button, e -> host());
        button.setText("Sunucu Aç");
        button.setEnabled(true);
        buttons.get(1).setEnabled(true);
    }

    private static void addListenerToButton(JButton button, ActionListener listener) {
        for(ActionListener l : button.getActionListeners()) 
            button.removeActionListener(l);
    
        button.addActionListener(listener);
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

    public static void onHit() {
        PlayerList.getCurrentPlayer().reset();
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
            return;
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
        Player player = PlayerList.getCurrentPlayer();

        if(player != null)
            OfflinePlayerController.keyPressed(e);

        switch (e.getKeyCode()) {
            case KeyEvent.VK_ESCAPE:
                openMenu();
                break;
        }
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

        if(Server.isRunning() && !buttons.get(2).isEnabled())
            onServerOpened();

        if(!Server.isRunning() && buttons.get(2).isEnabled())
            onServerClosed();

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
