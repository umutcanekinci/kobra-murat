package client;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.*;
import java.awt.image.ImageObserver;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.Timer;

import common.Constants;
import common.Direction;
import common.Utils;
import common.Window;
import common.graphics.Panel;
import common.graphics.SplashEffect;
import common.graphics.SplashListener;
import java.awt.Toolkit;

import server.Server;

public class Game extends JPanel implements ActionListener, KeyListener, SplashListener {

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

    public Game() {
        super();
        setDoubleBuffered(true);
        UI.init(this);
        initListeners();
        SplashEffect.start();    
        initTimer();
    }

    private void initListeners() {
        UI.addListener(Server.getInstance());
        UI.addListener(Client.getInstance());
        Server.addListener(Client.getInstance());
        addMouseListener(SplashEffect.getInstance());
        SplashEffect.addListener(this);
        SplashEffect.addListener(UI.getInstance());
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

    @Override
    public void onSplashFinished() {
        setBackground(Constants.BACKGROUND_COLOR);
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
        DebugLog.keyPressed(e);
        SplashEffect.keyPressed(e);

        if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
            onBack();
            
        if(isStarted)
            handleDirectionChange(e);
    }

    private void onBack() {
        Page currentPage = UI.MENU.getCurrentPage();
        
        if(currentPage == Page.GAME)
            setPaused(true);
        else if(currentPage == Page.PAUSE)
            setPaused(false);

        if(currentPage == Page.LOBBY)
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

    public static void update() {
        if(isStarted) {
            if(!Client.isConnected())
                OfflinePlayerController.update();
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);    
        draw((Graphics2D) g, UI.MENU.getCurrentPanel());
    }

    public static void draw(Graphics2D g, ImageObserver observer) {
        if(g == null)
            throw new IllegalArgumentException("Graphics cannot be null");

        g.scale(Constants.SCALEW, Constants.SCALEH);

        if(SplashEffect.isPlaying()) {
            SplashEffect.draw(g, observer);
            return;
        }

        UI.initGraphics(g);

        if(!isStarted)
            UI.MENU.drawBackground(g);
        else {
            Tilemap.draw(g, observer);
            AppleManager.draw(g, observer);
            PlayerList.draw(g, observer);            
            UI.drawPlayerBoard(g);
            PlayerList.drawColliders(g);
            Tilemap.drawColliders(g);
            AppleManager.drawColliders(g);
        }
        
        if(DebugLog.isOn()) {
            Panel panel = UI.MENU.getCurrentPanel();

            if(panel != null)
                panel.drawColliders(g);
        }

        DebugLog.draw(g);
        Toolkit.getDefaultToolkit().sync();  // this smooths out animations on some systems
        g.scale(1 / Constants.SCALEW, 1 / Constants.SCALEH); // reset scale
        //g.dispose();
        
    }

    public static String getInfo() {
        return "Current FPS: " + currentFps + "\n";
    }

}