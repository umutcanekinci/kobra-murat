package game;
import network.client.Client;
import network.packet.SetMapPacket;
import network.packet.apple.EatApplePacket;
import network.packet.apple.SpawnApplePacket;
import network.packet.player.AddPacket;
import network.packet.player.IdPacket;
import network.packet.player.StepPacket;
import network.server.Server;
import network.PacketHandler;
import network.PlayerList;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import game.graphics.InGame;
import game.graphics.UI;
import game.map.Level;
import game.map.Tilemap;
import game.player.NetPlayer;
import javax.swing.*;

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

    // Apples
    private static final ArrayList<Apple> apples = new ArrayList<>();

    private static boolean isGameStarted = false;

    // UI
    private static final ArrayList<JButton> buttons = new ArrayList<>();

    //endregion

    //region ---------------------------------------- INIT METHODS ----------------------------------------

    public Board() {
        super(new GridBagLayout());
        UI.init(TILE_SIZE, COLUMNS, ROWS);
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
            PacketHandler.handle(new SetMapPacket(2), null);
            PacketHandler.handle(new AddPacket(0), null);
            PacketHandler.handle(new IdPacket(0), null);
            AppleManager.apples.forEach(a -> PacketHandler.handle(new SpawnApplePacket(a), null));
        }
    
        PlayerList.players.values().forEach(p -> p.onGameStart());
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
        for(ActionListener listener : button.getActionListeners()) {
            button.removeActionListener(listener);
        }
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

    public static void spawnApple(Point position) {
        apples.add(new Apple(position));
    }

    public static void onHit() {
        player.reset();
    }

    public static void onStep() {
        collectApples();
        sendTransform();
    }

    private static void sendTransform() {
        if(!Client.isConnected())
            return;
        
        Client.sendData(new StepPacket(player));
    }

    
    private static void collectApples() {
        ArrayList<Apple> collectedApples = GetCollectedApples();

        if(collectedApples.isEmpty())
            return;

        apples.removeAll(collectedApples);
        collectedApples.forEach(a -> Client.sendData(new EatApplePacket(a.pos)));
        //spawnApples();
    }

    private static ArrayList<Apple> GetCollectedApples() {
        ArrayList<Apple> collectedApples = new ArrayList<>();
        for (Apple apple : apples) {
            for(NetPlayer player : PlayerList.players.values()) {
                if (apple.isCollide(player.getPos())) {
                    player.snake.grow(1);
                    collectedApples.add(apple);
                    break;
                }
            }
        }
        return collectedApples;
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

    //endregion -

    //region ---------------------------------------- UPDATE METHODS --------------------------------------

    @Override
    public void actionPerformed(ActionEvent e) {
        // this method is called by the timer every DELTATIME ms.
        // so this is the mainloop of the game
        if(isGameStarted) {
            updatePlayer();
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

        /*
         when calling g.drawImage() we can use "this" for the ImageObserver
         because Component implements the ImageObserver interface, and JPanel
         extends from Component. So "this" Board instance, as a Component, can
         react to imageUpdate() events triggered by g.drawImage()
        */

        Graphics2D g2d = (Graphics2D) g; // we need to cast the Graphics to Graphics2D to draw nicer text
        UI.initGraphics(g2d);
        drawBackground();

        if(isGameStarted) {
            drawGame(g2d);
        }
        else {
            drawMainMenu(g2d);
        }

        if(debugMode) {
            DebugLog.draw(g2d);
            if(isGameStarted)
                PlayerList.players.values().forEach(p -> p.snake.drawCollider(g2d));
        }

        Toolkit.getDefaultToolkit().sync(); // this smooths out animations on some systems
    }

    private void drawBackground() {
        setBackground(isGameStarted ? BACKGROUND_COLOR : MENU_BACKGROUND_COLOR);
    }

    private void drawGame(Graphics2D g) {
        drawBackground();
        InGame.drawMap(g);
        InGame.drawApples(apples, g);
        InGame.drawPlayers(g);
        UI.drawScore((player == null ? 0 : player.getScore()), g);
    }

    private static void drawMainMenu(Graphics2D g) {
        UI.drawTitle(SIZE.width, 100, g, null);
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
