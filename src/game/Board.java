package game;

import network.client.Client;
import network.server.Server;
import packet.UpdatePlayerPack;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

interface GameListener {}

public class Board extends JPanel implements ActionListener, KeyListener, PlayerListener {

    //region ---------------------------------------- ATTRIBUTES ------------------------------------------

    private static final int PORT = 7777;
    private static final String HOST_IP = "192.168.1.7";

    private final Client client = new Client(HOST_IP, PORT);
    private final Server server = new Server(PORT);

    private static final Font DEFAULT_FONT = new Font("Lato", Font.BOLD, 25);
    private final Font TITLE_FONT = new Font("Arial", Font.BOLD, 100);

    private GridBagConstraints layout; // Https://docs.oracle.com/javase/tutorial/uiswing/layout/visual.html#gridbag

    // Events
    private final java.util.List<GameListener> listeners = new ArrayList<>();

    // DebugMode
    private static final Rectangle DEBUG_RECT = new Rectangle(20, 20, 350, 0);
    private boolean debugMode = true;
    private static final Color[] DEBUG_COLORS = {
            new Color(255, 0, 0),
            new Color(0, 255, 0)
    };

    // Player
    Player player = new Player();

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
    private final JButton[] buttons = new JButton[2];

    private boolean isHost = true;

    //endregion

    //region ---------------------------------------- INIT METHODS ----------------------------------------

    public Board() {
        super(new GridBagLayout());

        initConnection();
        initPlayer();
        initMap();
        initEventSystem();
        initLayout();
        initWidgets();
        initTimer();
    }

    private void initConnection() {
        if(!client.connectToServer()) {
            openServer();
        }
    }

    private void openServer() {
        server.open();
        server.start();
    }

    private void initPlayer() {
        player = new Player();
    }

    private void initMap() {
        setPreferredSize(SIZE);
        map = new Tilemap(Level.get(0));
        player.setMap(map);
    }

    private void initEventSystem() {
        player.addListener(this);
        addListener(player);
    }

    private void startGame() {
        isGameStarted = true;
        invokeEvents("onGameStart");
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
        startButton.addActionListener(e -> startGame());

        JButton exitButton = newButton("Çıkış");
        exitButton.addActionListener(e -> exit());

        buttons[0] = startButton;
        buttons[1] = exitButton;
    }

    private JButton newButton(String text) {
        JButton button = new JButton(text);
        button.setRequestFocusEnabled(false);
        button.setBackground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 20));
        button.setPreferredSize(new Dimension(500, 100));

        add(button, layout);
        return button;
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

    public void addListener(GameListener listener) {
        listeners.add(listener);
    }

    private void invokeEvents(String eventName) {
        for(GameListener listener : listeners) {
            try {
                listener.getClass().getMethod(eventName).invoke(listener);
            }
            catch (Exception e) {
                e.printStackTrace();
                exit();
            }
        }
    }

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
            debugMode = !debugMode;
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
        if(isGameStarted) {
            player.update();
            sendPlayerData();
        }

        repaint();
    }

    private void sendPlayerData() {
        if(client != null) {
            client.sendData(new UpdatePlayerPack(player.snake));
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

        if(debugMode) {
            drawDebugBackground(g2d);
            drawDebug(g2d);
        }

        Toolkit.getDefaultToolkit().sync(); // this smooths out animations on some systems
    }

    private void initGraphics(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);

    }

    //region Draw Game Methods

    private void drawGame(Graphics2D g) {

        if(!isGameStarted)
            return;

        setBackground(BACKGROUND_COLOR);
        g.setFont(DEFAULT_FONT);
        map.render(g);
        drawApples(g);
        drawPlayer(g);
        drawScore(g);
    }

    private void drawApples(Graphics2D g) {
        for (Apple apple : apples) {
            apple.draw(g, this);
        }
    }

    private void drawPlayer(Graphics2D g) {
        player.draw(g, this);
    }

    private void drawScore(Graphics2D g) {
        String text = "Score: " + player.getScore();
        Utils.drawText(g, text, Color.GREEN, SCORE_RECT, true);
    }

    private void drawDebug(Graphics2D g) {
        g.setFont(DEFAULT_FONT);

        DEBUG_RECT.y = 20;

        String[] text = {
                "DEBUG MODE ON",
                "FPS: " + FPS,
                "",
        };


        for (String s : text) {
            DEBUG_RECT.y = drawDebugText(g, s, DEBUG_COLORS[0]);
        }

        if(server.isRunning) {
            DEBUG_RECT.y = drawDebugText(g, "[SERVER]", DEBUG_COLORS[1]);
            for(String s : server.getDebugInfo()) {
                DEBUG_RECT.y = drawDebugText(g, s, DEBUG_COLORS[1]);
            }
        }
        else {
            DEBUG_RECT.y = drawDebugText(g, "[CLIENT]", DEBUG_COLORS[1]);
            for(String s : client.getDebugInfo()) {
                DEBUG_RECT.y = drawDebugText(g, s, DEBUG_COLORS[1]);
            }
        }

        if(!isGameStarted)
            return;

        for (String s : player.getDebugInfo()) {
            DEBUG_RECT.y = drawDebugText(g, s, DEBUG_COLORS[1]);
        }
    }

    private int drawDebugText(Graphics2D g, String text, Color color) {
        return Utils.drawText(g, text, color, DEBUG_RECT, false);
    }

    private void drawDebugBackground(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 150));

        g.fillRect(0, 0, DEBUG_RECT.width, DEBUG_RECT.y + 20);
    }

    //endregion

    private void drawMainMenu(Graphics2D g) {
        if(isGameStarted)
            return;

        setBackground(MENU_BACKGROUND_COLOR);
        drawTitle(g);
    }

    private void drawTitle(Graphics2D g) {
        g.setFont(TITLE_FONT);
        Utils.drawText(g, TITLE, Color.BLACK, new Rectangle(0, 100, SIZE.width, TILE_SIZE), true);
    }

    //endregion

    private void exit() {
        if(client != null)
            client.disconnect();

        if(server != null)
            server.close();
        App.exit();
    }
}
