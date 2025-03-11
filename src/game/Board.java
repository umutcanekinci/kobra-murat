package game;
import network.client.Client;
import network.packet.apple.EatApplePacket;
import network.packet.player.StepPacket;
import network.server.Server;
import network.PlayerList;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
//import java.util.Random;
import game.map.Level;
import game.map.Tilemap;
import game.player.NetPlayer;

import javax.swing.*;

public class Board extends JPanel implements ActionListener, KeyListener {

    //region ---------------------------------------- ATTRIBUTES ------------------------------------------

    private static final int PORT = 7777;
    private static final String HOST_IP = "192.168.1.7";
    private static final boolean isHostInLocal = true;

    private Client client;
    private final Server server = new Server(PORT);

    private static final Font DEFAULT_FONT = new Font("Lato", Font.BOLD, 25);
    private final Font TITLE_FONT = new Font("Arial", Font.BOLD, 100);

    private GridBagConstraints layout; // Https://docs.oracle.com/javase/tutorial/uiswing/layout/visual.html#gridbag

    // Players
    NetPlayer player;

    // Colors
    private static final Color MENU_BACKGROUND_COLOR = Color.GREEN;
    private static final Color BACKGROUND_COLOR = new Color(232, 232, 232);

    // Timer
    public static final int FPS = 60;
    public static final double DELTATIME = 1.0 / FPS;
    public static final int DELTATIME_MS = (int) (DELTATIME * 1000);

    // Map
    public Tilemap map;
    public static final int TILE_SIZE = 64;
    public static final int ROWS = 12;
    public static final int COLUMNS = 18;
    public static final Dimension SIZE = new Dimension(TILE_SIZE * COLUMNS, TILE_SIZE * ROWS);

    // Apples
    private final ArrayList<Apple> apples = new ArrayList<>();

    private boolean isGameStarted = false;

    // UI
    private final String TITLE = "Kobra Murat";
    private static final Rectangle SCORE_RECT = new Rectangle(0, TILE_SIZE * (ROWS - 1), TILE_SIZE * COLUMNS, TILE_SIZE);
    private final ArrayList<JButton> buttons = new ArrayList<>();

    //endregion

    //region ---------------------------------------- INIT METHODS ----------------------------------------

    public Board() {
        super(new GridBagLayout());
        setPreferredSize(SIZE);
        initConnection();
        initDebugLog();
        initLayout();
        initWidgets();
        initTimer();
    }

    private void initDebugLog() {
        DebugLog.init(server, client);
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

    public void setMap(int id) {
        map = new Tilemap(Level.get(id));
    }

    private void startGame() {
        isGameStarted = true;
        PlayerList.players.values().forEach(p -> p.onGameStart());
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
        if(client.isConnected())
            return;

        buttons.get(1).setEnabled(false); // Don't allow to spam clicks

        if(!server.isRunning()){ // Don't let open server if connected to another server
            buttons.get(2).setEnabled(false);
        }
            
        client.start();
    }

    private void host() {
        if(client.isConnected() || server.isRunning())
            return;

        buttons.get(2).setEnabled(false); // Don't allow to spam clicks
        server.start();
    }

    public void onClientConnected() {
        buttons.get(0).setEnabled(true);
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
        buttons.get(0).setEnabled(false);
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
        buttons.get(0).setEnabled(false);
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
        if(!client.isConnected())
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

        player.keyPressed(e);

        if(e.getKeyCode() == KeyEvent.VK_R) {
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
            player.move();
        }

        repaint();
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
        apples.forEach(a -> a.draw(g, this));
    }
    
    private void drawPlayers(Graphics2D g) {
        PlayerList.players.values().forEach(p -> p.draw(g, this));
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

    public void exit() {
        if(client.isConnected())
            client.disconnect();

        if(server.isRunning())
            server.close();

        Window.exit();
    }
}
