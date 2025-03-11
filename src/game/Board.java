package game;
import network.client.Client;
import network.client.PacketHandler;
import network.packet.SetMapPacket;
import network.packet.apple.EatApplePacket;
import network.packet.apple.SpawnApplePacket;
import network.packet.player.AddPacket;
import network.packet.player.IdPacket;
import network.packet.player.StepPacket;
import network.server.Server;
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

    // Colors
    private static final Color MENU_BACKGROUND_COLOR = Color.GREEN;
    private static final Color BACKGROUND_COLOR = new Color(232, 232, 232);

    private static final int PORT = 7777;
    private static final String HOST_IP = "192.168.1.7";
    private static String LOCAL_IP;
    private static final boolean isHostInLocal = true;

    private Client client;
    private Server server;

    private GridBagConstraints layout; // Https://docs.oracle.com/javase/tutorial/uiswing/layout/visual.html#gridbag

    // Players
    NetPlayer player;
   
    // Timer
    public static final int FPS = 60;
    public static final double DELTATIME = 1.0 / FPS;
    public static final int DELTATIME_MS = (int) (DELTATIME * 1000);

    // Map
    public Tilemap map;
    public static final int TILE_SIZE = 64;
    public static final int ROWS = 14;
    public static final int COLUMNS = 24;
    public static final Dimension SIZE = new Dimension(TILE_SIZE * COLUMNS, TILE_SIZE * ROWS);

    // Apples
    private final ArrayList<Apple> apples = new ArrayList<>();

    private boolean isGameStarted = false;

    // UI
    
    private final ArrayList<JButton> buttons = new ArrayList<>();

    //endregion

    //region ---------------------------------------- INIT METHODS ----------------------------------------

    public Board() {
        super(new GridBagLayout());
        UI.init(TILE_SIZE, COLUMNS, ROWS);
        setLocalIp();
        setPreferredSize(SIZE);
        initDebugLog();
        initLayout();
        initWidgets();
        initTimer();
    }

    private void setLocalIp() {
        LOCAL_IP = Utils.getLocalIp();
    }
    
    private void initDebugLog() {
        DebugLog.toggle();
        DebugLog.debugText.add("DEBUG MODE ON - Press F2 to toggle");
        DebugLog.debugText.add("FPS: " + FPS);
        DebugLog.debugText.add("SIZE: " + SIZE.width + "x" + SIZE.height + " px");
        DebugLog.debugText.add("");
        DebugLog.debugText.add("LOCAL IP: " + LOCAL_IP);
        DebugLog.debugText.add("HOST IP: " + HOST_IP + " (Local: " + isHostInLocal + ")");
        DebugLog.debugText.add("PORT: " + PORT);
    }

    private void initServer() {
        server = new Server(PORT);
        server.setBoard(this);
        DebugLog.init(server, client);
    }

    private void initClient() {
        client = new Client(isHostInLocal ? "localhost" : HOST_IP, PORT);
        client.setBoard(this);
        DebugLog.init(server, client);
    }

    public void setMap(int id) {
        map = new Tilemap(Level.get(id));
    }

    private void startGame() {
        isGameStarted = true;

        if(!Utils.isClientConnected(client)) {
            PacketHandler.init(this, client);
            PacketHandler.handle(new SetMapPacket(0), null);
            PacketHandler.handle(new AddPacket(0), null);
            PacketHandler.handle(new IdPacket(0), null);
            AppleManager.apples.forEach(a -> PacketHandler.handle(new SpawnApplePacket(a), null));
        }
    
        PlayerList.players.values().forEach(p -> p.onGameStart());
        hideWidgets();
    }

    private void initLayout() {
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
    
    private void connect() {
        if(client == null)
            initClient();

        if(client.isConnected())
            return;

        buttons.get(1).setEnabled(false); // Don't allow to spam clicks

        if(!server.isRunning()){ // Don't let open server if connected to another server
            buttons.get(2).setEnabled(false);
        }
            
        client.start();
    }

    private void host() {
        if(server == null)
            initServer();

        if(server.isRunning())
            return;

        buttons.get(2).setEnabled(false); // Don't allow to spam clicks
        server.start();
    }

    public void onClientConnected() {
        JButton button = buttons.get(1);
        addListenerToButton(button, e -> client.disconnect());
        button.setText("Bağlantıyı Kes");
        button.setEnabled(true);
    }

    public void onClientDisconnected() {
        JButton button = buttons.get(1);
        
        for(ActionListener listener : button.getActionListeners()) {
            button.removeActionListener(listener);
        }
        button.addActionListener(e -> connect());

        button.setText("Bağlan");
        button.setEnabled(true);
        buttons.get(2).setEnabled(true);
        PlayerList.clear();
    }

    public void onServerOpened() {
        JButton button = buttons.get(2);
        for(ActionListener listener : button.getActionListeners()) {
            button.removeActionListener(listener);
        }
        button.addActionListener(e -> server.close());
        button.setText("Sunucuyu Kapat");
        button.setEnabled(true);
    }

    public void onServerClosed() {
        JButton button = buttons.get(2);
        addListenerToButton(button, e -> host());
        button.setText("Sunucu Aç");
        button.setEnabled(true);
        buttons.get(1).setEnabled(true);
    }

    private void addListenerToButton(JButton button, ActionListener listener) {
        for(ActionListener l : button.getActionListeners()) {
            button.removeActionListener(l);
        }
        button.addActionListener(listener);
    }

    public void initPlayer() {
        player = PlayerList.getCurrentPlayer();

        if(player == null)
            return;
            
        player.setMap(map);
    }

    private void hideWidgets() {
        for (JButton button : buttons) {
            if(button == null)
                continue;

            button.setVisible(false);
        }
    }

    private void showWidgets() {
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

    public void spawnApple(Point position) {
        apples.add(new Apple(position));
    }

    public void onHit() {
        player.reset();
    }

    public void onStep() {
        collectApples();
        sendTransform();
    }

    private void sendTransform() {
        if(!Utils.isClientConnected(client))
            return;
        
        //client.sendData(new PlayerTransformPacket(player));   
        client.sendData(new StepPacket(player));
    }

    
    private void collectApples() {
        ArrayList<Apple> collectedApples = GetCollectedApples();

        if(collectedApples.isEmpty())
            return;

        apples.removeAll(collectedApples);
        collectedApples.forEach(a -> client.sendData(new EatApplePacket(a.pos)));
        //spawnApples();
    }

    private ArrayList<Apple> GetCollectedApples() {
        ArrayList<Apple> collectedApples = new ArrayList<>();
        for (Apple apple : apples) {
            for(NetPlayer player : PlayerList.players.values()) {
                if (apple.isCollide(player.getPos())) {
                    player.grow(1);
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
            DebugLog.toggle();
        }

        if(!isGameStarted) {
            keyPressedMenu(e);
            return;
        }
        keyPressedGame(e);

    }

    private void keyPressedMenu(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            exit();
        }
    }

    private void keyPressedGame(KeyEvent e) {
        if(!isGameStarted)
            return;

        if(player != null)
            player.keyPressed(e);

        if(e.getKeyCode() == KeyEvent.VK_R) {
            if(player != null)
                player.reset();
        }
        else if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            openMenu();
        }
    }

    public void openMenu() {
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

    private void updatePlayer() {
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

        DebugLog.draw(g2d);
        Toolkit.getDefaultToolkit().sync(); // this smooths out animations on some systems
    }

    private void drawBackground() {
        setBackground(isGameStarted ? BACKGROUND_COLOR : MENU_BACKGROUND_COLOR);
    }

    private void drawGame(Graphics2D g) {
        drawBackground();
        InGame.drawMap(map, g);
        InGame.drawApples(this, apples, g);
        InGame.drawPlayers(this, g);
        UI.drawScore((player == null ? 0 : player.getScore()), g);
    }

    private void drawMainMenu(Graphics2D g) {
        UI.drawTitle(SIZE.width, 100, g, this);
    }

    //endregion

    public void exit() {
        onExit();
        Window.exit();
    }

    public void onExit() {
        disconnect();
    }

    private void disconnect() {
        disconnectClient();
        disconnectServer();
    }

    private void disconnectClient() {
        if(!Utils.isClientConnected(client))
            return;

        client.disconnect();
    }

    private void disconnectServer() {
        if(!Utils.isServerRunning(server))
            return;

        server.close();
    }
}
