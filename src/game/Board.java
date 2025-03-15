package game;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

import game.graphics.Draw;
import game.graphics.UI;
import game.map.Level;

import game.map.Tilemap;
import game.player.Direction;
import game.player.NetPlayer;

import network.client.Client;
import network.client.PlayerList;
import network.server.Server;
import network.packet.apple.EatApplePacket;
import network.packet.player.StepPacket;

public class Board extends JPanel implements ActionListener, KeyListener {

    //region ---------------------------------------- ATTRIBUTES ------------------------------------------
    
    private static boolean debugMode = true;

    // Colors
    private static final Color MENU_BACKGROUND_COLOR = Color.BLACK;
    private static final Color BACKGROUND_COLOR = Color.BLACK;

    public static final int PORT = 7777;
    public static final String HOST_IP = "192.168.1.7";
    public static String LOCAL_IP;
    public static final boolean isHostInLocal = true;

    private static GridBagConstraints layout; // Https://docs.oracle.com/javase/tutorial/uiswing/layout/visual.html#gridbag

    // Players
    private static NetPlayer player;
   
    // Timer
    public static final int FPS = 60;
    public static final double DELTATIME = 1.0 / FPS;
    public static final int DELTATIME_MS = (int) (DELTATIME * 1000);

    // Map
    public static Tilemap map;
    public static final int TILE_SIZE = 64;
    public static final int ROWS = 14;
    public static final int COLUMNS = 24;
    public static final Dimension SIZE = new Dimension(TILE_SIZE * COLUMNS, TILE_SIZE * ROWS);

    private static boolean isGameStarted = false;

    // UI
    private static final ArrayList<JButton> buttons = new ArrayList<>();

    //endregion

    //region ---------------------------------------- INIT METHODS ----------------------------------------

    public Board() {
        super(new GridBagLayout());
        UI.init();
        initClient();
        initServer();
        setLocalIp();
        setPreferredSize(SIZE);
        initLayout();
        initWidgets();
        initTimer();
    }

    private static void setLocalIp() {
        LOCAL_IP = Utils.getLocalIp();
    }

    private static void initServer() {
        Server.setPort(PORT);
    }

    private static void initClient() {
        Client.setHost(isHostInLocal ? "localhost" : HOST_IP);
        Client.setPort(PORT);
    }

    public static void setMap(int id) {
        Tilemap.load(Level.get(id));
    }

    private static void startGame() {
        isGameStarted = true;

        if(!Client.isConnected()) {
            setMap(0);
            PlayerList.addPlayer(null, 0);
            PlayerList.id = 0;
            Board.initPlayer();
            AppleManager.spawnApples();
            player.snake.setDirection(Direction.RIGHT);
            //player.snake.setParts();
            player.reset();
            player.getPos().setLocation(Tilemap.getSpawnPoint());
            player.snake.rotateHeadTransform();
        }
    
        hideWidgets();
    }

    private static void initLayout() {
        layout = new GridBagConstraints();
        layout.insets = new Insets(0, 10, 5, 0);
        layout.weighty = 1;
    }

    private void initWidgets() {
        addButton("Başla", e -> startGame());
        addButton("Bağlan", e -> connect());
        addButton("Sunucu Aç", e -> host());
        addButton("Çıkış", e -> exit());
    }

    private void addButton(String text, ActionListener listener) {
        JButton button = UI.newButton(text);
        button.addActionListener(listener);
        add(button, layout);
        buttons.add(button);
    }

    private static void host() {
        if(Server.isRunning())
            return;

        buttons.get(2).setEnabled(false); // Don't allow to spam clicks
        Server.start();
    }

    public static void onClientConnected() {
        JButton button = buttons.get(1);
        addListenerToButton(button, e -> Client.disconnect());
        button.setText("Bağlantıyı Kes");
        button.setEnabled(true);
    }

    public static void onClientDisconnected() {
        JButton button = buttons.get(1);
        
        for(ActionListener listener : button.getActionListeners()) {
            button.removeActionListener(listener);
        }
        button.addActionListener(e -> Board.connect());

        button.setText("Bağlan");
        button.setEnabled(true);
        buttons.get(2).setEnabled(true);
        PlayerList.clear();
        AppleManager.clear();
    }

    private static void connect() {
        if(Client.isConnected())
            return;


        buttons.get(1).setEnabled(false); // Don't allow to spam clicks
        
        if(!Server.isRunning()){ // Don't let open server if connected to another server
            buttons.get(2).setEnabled(false);
        }
            
        Client.start();
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
        for(ActionListener l : button.getActionListeners()) {
            button.removeActionListener(l);
        }
        button.addActionListener(listener);
    }

    public static void initPlayer() {
        player = PlayerList.getCurrentPlayer();

        if(player == null)
            return;
            
        player.setMap();
    }

    private static void hideWidgets() {
        for (JButton button : buttons) {
            if(button == null)
                continue;

            button.setVisible(false);
        }
    }

    private static void showWidgets() {
        for (JButton button : buttons) {
            if(button == null)
                continue;

            button.setVisible(true);
        }
    }

    private void initTimer() {
        // this timer will call the actionPerformed() method every DELTATIME ms
        // keep a reference to the timer object that triggers actionPerformed() in
        // case we need access to it in another method
        Timer timer = new Timer(DELTATIME_MS, this);
        timer.start();
    }

    //endregion

    //region ---------------------------------------- EVENT METHODS ---------------------------------------

    public static void onHit() {
        player.reset();
    }

    public static void onStep() {
        if(!Client.isConnected()) {
            player.snake.step();
            player.canRotate = true;
            player.displacement = 0;
            collectApples();
        }
        else
            sendStep();
    }
    
    private static void collectApples() {
        ArrayList<Apple> collectedApples = AppleManager.getCollecteds();

        if(collectedApples.isEmpty())
            return;

        AppleManager.removeAll(collectedApples);
        collectedApples.forEach(apple -> Client.sendData(new EatApplePacket(apple)));
        
        if(!Server.isRunning())
            AppleManager.spawnApples();
    }
    
    private static void sendStep() {
        Client.sendData(new StepPacket(player));
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
        keyPressedGame(e);

    }

    private static void keyPressedMenu(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
            exit();
    }

    private static void keyPressedGame(KeyEvent e) {
        if(!isGameStarted)
            return;

        if(player != null)
            player.keyPressed(e);

        switch (e.getKeyCode()) {
            case KeyEvent.VK_R:
                if(player != null)
                    player.reset();
                break;
            case KeyEvent.VK_ESCAPE:
                openMenu();
                break;
        }
    }

    public static void openMenu() {
        isGameStarted = false;
        showWidgets();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // react to key up events
    }

    //endregion

    //region ---------------------------------------- UPDATE METHODS --------------------------------------

    @Override
    public void actionPerformed(ActionEvent e) {
        // this method is called by the timer every DELTATIME ms.
        if(isGameStarted) {
            updatePlayer();
        }

        if(Server.isRunning() && !buttons.get(2).isEnabled()) {
            onServerOpened();
        }

        if(!Server.isRunning() && buttons.get(2).isEnabled()) {
            onServerClosed();
        }

        repaint();
    }

    private static void updatePlayer() {
        if(player == null)
            return;

        player.move();
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

    public static void exit() {
        onExit();
        Window.exit();
    }

    public static void onExit() {
        disconnect();
    }

    private static void disconnect() {
        disconnectClient();
        disconnectServer();
    }

    private static void disconnectClient() {
        if(!Client.isConnected())
            return;

        Client.disconnect();
    }

    private static void disconnectServer() {
        if(!Server.isRunning())
            return;

        Server.close();
    }
}
