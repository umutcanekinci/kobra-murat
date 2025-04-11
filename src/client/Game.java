package client;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.event.*;
import java.awt.Toolkit;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.Timer;

import common.Constants;
import common.Direction;
import common.Utils;
import common.Window;
import common.graphics.image.SplashImage;
import server.Server;

public class Game extends JPanel implements ActionListener, KeyListener {

    //region ---------------------------------------- Variables ------------------------------------------
    private static boolean isStarted = false;

    private static long currentTime = System.nanoTime();
    private static long lastTime = System.nanoTime();
    private static int frameCount = 0;    
    private static long totalTime = 0;
    private static int currentFps = 0;
    private static ArrayList<GameListener> listeners = new ArrayList<>();
    //endregion

    //region ---------------------------------------- INIT METHODS ----------------------------------------

    private static Game instance;
    public static Game getInstance() {
        if(instance == null)
            instance = new Game();

        return instance;
    }

    private Game() {
        super(new GridBagLayout()); // Without using a layout manager, there exist a space between the panel and jframe on the top.
        //setDoubleBuffered(true);
        UI.init(this);
        setBackground(Color.red);
        initListeners();
        
        listeners.forEach(GameListener::onWindowReady);
        
        initTimer();
    }

    private void initListeners() {
        addListener(UI.getInstance());
        UI.addListener(Server.getInstance());
        UI.addListener(Client.getInstance());
        Server.addListener(Client.getInstance());
        addMouseListener(SplashImage.getInstance());
        SplashImage.addListener(UI.getInstance());
        addListener(Client.getInstance());
        Client.addListener(UI.getInstance());
        Client.addListener(PlayerList.getInstance());
        Client.addListener(AppleManager.getInstance());
    }

    public static void addListener(GameListener listener) {
        if(listener == null)
            throw new IllegalArgumentException("Listener cannot be null");

        listeners.add(listener);
    }

    private void initTimer() {
        new Timer(Constants.DELTATIME_MS, this).start();
    }

    public static void start() {
        if(isStarted)
            return;

        isStarted = true;
        UI.MENU.openPage(Page.GAME);
    }

    public static void setPaused(boolean value) {
        isStarted = !value;
    }

    public static void exit() {
        Window.exit();
    }

    //endregion

    //region ---------------------------------------- INPUT METHODS ---------------------------------------

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
            onBack();

        if(isStarted)
            handleDirectionChange(e);

        DebugLog.keyPressed(e);
        SplashImage.keyPressed(e);
    }

    private void onBack() {
        Page currentPage = UI.MENU.getCurrentPage();

        if(currentPage == Page.GAME)
            setPaused(true);
        else if(currentPage == Page.PAUSE)
            setPaused(false);

        if(currentPage == Page.LOBBY || currentPage == Page.SPLASH)
            return;

        UI.MENU.goBack(e -> exit());
    }

    private static void handleDirectionChange(KeyEvent e) {
        if(e == null)
            throw new IllegalArgumentException("KeyEvent cannot be null");

        Direction direction = Utils.keyToDirection(e.getKeyCode());

        if(direction == null)
            return;

        if(!Client.isConnected())
            OfflinePlayerController.rotate(direction);
        else
            listeners.forEach(listener -> listener.onDirectionChanged(direction));
    }

    //endregion

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e == null)
            throw new IllegalArgumentException("ActionEvent cannot be null");

        currentTime = System.nanoTime();
        totalTime += currentTime - lastTime;
        lastTime = currentTime;
        
        update();
        repaint();

        frameCount++;

        if(totalTime >= 1_000_000_000) {
            totalTime = 0;
            currentFps = frameCount;
            frameCount = 0;
        }
    }

    private static void update() {
        if(isStarted && !Client.isConnected()) {
            OfflinePlayerController.update();
        }
    }

    @Override
    public void paint(Graphics g) {
        // When we use the gridbaglayout, paintcomponent method is not calling while we dont add any components to jpanel.
        // It should be becaouse of the layout managers smart enough to not draw hidden components.
        super.paint(g);
        draw((Graphics2D) g);
        Toolkit.getDefaultToolkit().sync();  // this smooths out animations on some systems
    }

    private static void draw(Graphics2D g) {
        if(g == null)
            throw new IllegalArgumentException("Graphics cannot be null");

        UI.initGraphics(g);
        DebugLog.draw(g);
    }

    public static String getInfo() {
        return "Current FPS: " + currentFps + "\n";
    }

}