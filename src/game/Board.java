package game;
import network.client.Client;
import network.packet.PacketHandler;
import network.packet.UpdatePlayerPack;
import network.server.Server;
import network.PlayerList;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import game.map.Level;
import game.map.Tilemap;
import game.player.NetPlayer;

import javax.swing.*;

public class Board extends JPanel implements ActionListener, KeyListener {

    //region ---------------------------------------- ATTRIBUTES ------------------------------------------

    private static final int PORT = 7777;
    private static String HOST_IP = "10.253.68.73";
    private static final boolean isHostInLocal = true;

    private Client client;
    private final Server server = new Server(PORT);;

    private static final Font DEFAULT_FONT = new Font("Lato", Font.BOLD, 25);
    private final Font TITLE_FONT = new Font("Arial", Font.BOLD, 100);

    private GridBagConstraints layout; // Https://docs.oracle.com/javase/tutorial/uiswing/layout/visual.html#gridbag

    // Players
    NetPlayer player;

    // Colors
    private static final Color MENU_BACKGROUND_COLOR = Color.GREEN;
    private static final Color BACKGROUND_COLOR = new Color(232, 232, 232);

    // Timer
    public static int FPS = 60;
    public static double DELTATIME = 1.0 / FPS;
    public static int DELTATIME_MS = (int) (DELTATIME * 1000);

    // Map
    private Tilemap map;
    public static final int TILE_SIZE = 64;
    public static final int ROWS = 12;
    public static final int COLUMNS = 18;
    public static Dimension SIZE = new Dimension(TILE_SIZE * COLUMNS, TILE_SIZE * ROWS);

    // Apples
    private final ArrayList<Apple> apples = new ArrayList<>();
    public static final int APPLE_COUNT = 5;
    private final Random rand = new Random();

    private boolean isGameStarted = false;

    // UI
    private final String TITLE = "Kobra Murat";
    private static final Rectangle SCORE_RECT = new Rectangle(0, TILE_SIZE * (ROWS - 1), TILE_SIZE * COLUMNS, TILE_SIZE);
    private final ArrayList<JButton> buttons = new ArrayList<>();

    //endregion

    //region ---------------------------------------- INIT METHODS ----------------------------------------

    public Board() {
        super(new GridBagLayout());
        PacketHandler.init(this);
        initDebugLog();
        initConnection();
        initMap();
        initLayout();
        initWidgets();
        initTimer();
    }

    private void initDebugLog() {
        DebugLog.toggle();
        DebugLog.debugText.add("DEBUG MODE ON - Press F2 to toggle");
        DebugLog.debugText.add("FPS: " + FPS);
        DebugLog.debugText.add("SIZE: " + SIZE.width + "x" + SIZE.height + " px");
        DebugLog.debugText.add("");
        DebugLog.debugText.add("CURRENT IP: " + server.ip);
        DebugLog.debugText.add("HOST_IP: " + HOST_IP + " (Local: " + isHostInLocal + ")");
        DebugLog.debugText.add("PORT: " + PORT);
    }

    private void initConnection() {
        client = new Client(isHostInLocal ? "localhost" : HOST_IP, PORT);
        client.setBoard(this);
        server.setBoard(this);
    }

    private void initMap() {
        setPreferredSize(SIZE);
        map = new Tilemap(Level.get(0));
        
    }

    private void startGame() {
        isGameStarted = true;
        player.onGameStart();
        spawnApples();
        hideWidgets();
    }

    private void initLayout() {
        layout = new GridBagConstraints();
        layout.insets = new Insets(0, 10, 5, 0);
        layout.weighty = 1;
    }

    private void initWidgets() {

        JButton startButton = newButton("Başla");
        startButton.setEnabled(false);
        startButton.addActionListener(e -> startGame());

        JButton connectButton = newButton("Bağlan");
        connectButton.addActionListener(e -> connect());

        JButton hostButton = newButton("Sunucu Aç");
        hostButton.addActionListener(e -> host());

        JButton exitButton = newButton("Çıkış");
        exitButton.addActionListener(e -> exit());
    }

    private JButton newButton(String text) {
        JButton button = new JButton(text);
        button.setRequestFocusEnabled(false);
        button.setBackground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 20));
        button.setPreferredSize(new Dimension(200, 100));

        add(button, layout);
        buttons.add(button);
        return button;
    }
    
    private void connect() {
        if(client.isConnected() || server.isRunning())
            return;
        buttons.get(1).setEnabled(false);
        buttons.get(2).setEnabled(false);
        buttons.get(1).setText("Bağlanılıyor...");
        client.start();
    }

    private void host() {
        if(client.isConnected() || server.isRunning())
            return;

        buttons.get(1).setEnabled(false);
        buttons.get(2).setEnabled(false);
        buttons.get(2).setText("Sunucu Açılıyor...");

        server.start();
    }

    public void onClientConnected() {
        JButton button = buttons.get(1);
        for(ActionListener listener : button.getActionListeners()) {
            button.removeActionListener(listener);
        }
        button.addActionListener(e -> client.disconnect());
        button.setText("Bağlantıyı Kes");
        button.setEnabled(true);
        DebugLog.connectionState = "Connected to Server";
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
        buttons.get(0).setEnabled(false);
        DebugLog.connectionState = "Disconnected";
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
        DebugLog.connectionState = "Server is running";
    }

    public void onServerClosed() {
        JButton button = buttons.get(2);
        for(ActionListener listener : button.getActionListeners()) {
            button.removeActionListener(listener);
        }
        button.addActionListener(e -> host());
        button.setText("Sunucu Aç");
        button.setEnabled(true);
        buttons.get(1).setEnabled(true);
        buttons.get(0).setEnabled(false);
        DebugLog.connectionState = "Server is closed";
        PlayerList.clear();
    }

    public void initPlayer() {
        player = PlayerList.getCurrentPlayer();
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

    private void spawnApples() {
        int amount = APPLE_COUNT - apples.size();
        for (int i = 0; i < amount; i++) {
            spawnApple();
        }
    }

    private void spawnApple() {
        Point position = new Point(rand.nextInt(COLUMNS), rand.nextInt(ROWS));
        Apple apple = new Apple(position);

        if(player.doesCollide(position) || map.isCollide(position)) {
            spawnApple();
            return;
        }

        if(apples.contains(apple))
            return;

        apples.add(apple);
    }

    //endregion

    //region ---------------------------------------- EVENT METHODS ---------------------------------------

    public void onHit() {
        restart();
    }

    private void restart() {
        player.reset();
        apples.clear();
        spawnApples();
    }

    public void onStep() {
        collectApples();
    }

    private void collectApples() {
        ArrayList<Apple> collectedApples = GetCollectedApples();

        if(collectedApples.isEmpty())
            return;

        apples.removeAll(collectedApples);
        spawnApples();
    }

    private ArrayList<Apple> GetCollectedApples() {
        ArrayList<Apple> collectedApples = new ArrayList<>();
        for (Apple apple : apples) {
            if (apple.isCollide(player.getPos())) {
                player.grow(1);
                collectedApples.add(apple);
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

        player.keyPressed(e);

        if(e.getKeyCode() == KeyEvent.VK_R) {
            restart();
        }
        else if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            openMenu();
        }
    }

    private void openMenu() {
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
        if(!isGameStarted)
            if(client.isConnected() || server.isRunning())
                buttons.get(0).setEnabled(true);

        if(isGameStarted) {
            player.update();
            sendPlayerData();
        }

        repaint();
    }

    private void sendPlayerData() {
        System.out.println("Sending player data. Direction: " + player.snake.direction);
        if(client.isConnected()) {
            client.sendData(new UpdatePlayerPack(player));
        }
        if(server.isRunning()) {
            PlayerList.sendToAll(new UpdatePlayerPack(player));
        }
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
        initGraphics(g2d);

        if(isGameStarted) {
            drawGame(g2d);
        }
        else {
            drawMainMenu(g2d);
        }

        DebugLog.draw(g2d);
        
        Toolkit.getDefaultToolkit().sync(); // this smooths out animations on some systems
    }

    private void initGraphics(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
    }

    private void drawGame(Graphics2D g) {
        if(!isGameStarted)
            return;

        setBackground(BACKGROUND_COLOR);
        map.render(g);
        drawApples(g);
        drawPlayers(g);
        drawScore(g);
    }

    private void drawApples(Graphics2D g) {
        for (Apple apple : apples) {
            apple.draw(g, this);
        }
    }

    private void drawPlayers(Graphics2D g) {
        for (NetPlayer p : PlayerList.players.values()) {
            p.draw(g, this);
        }
    }

    private void drawScore(Graphics2D g) {
        g.setFont(DEFAULT_FONT);
        String text = "Score: " + player.getScore();
        Utils.drawText(g, text, Color.GREEN, SCORE_RECT, true);
    }

    private void drawMainMenu(Graphics2D g) {
        if(isGameStarted)
            return;

        setBackground(MENU_BACKGROUND_COLOR);
        drawTitle(g);
    }

    private void drawTitle(Graphics2D g) {
        g.setFont(TITLE_FONT);
        Utils.drawText(g, TITLE, Color.BLACK, new Rectangle(0, 200, SIZE.width, TILE_SIZE), true);
    }

    //endregion

    private void exit() {
        if(client.isConnected())
            client.disconnect();

        if(server.isRunning())
            server.close();

        Window.exit();
    }
}
