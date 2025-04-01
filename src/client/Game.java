package client;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.*;
import java.awt.image.ImageObserver;

import javax.swing.JPanel;
import javax.swing.Timer;

import common.Constants;
import common.Direction;
import common.Utils;
import common.Window;
import common.graphics.Image;
import common.graphics.SplashEffect;
import common.graphics.SplashListener;
import common.packet.RotatePacket;
import java.awt.Toolkit;

import client.graphics.UI;
import server.Server;

import client.graphics.UI.Page;

public class Game extends JPanel implements ActionListener, KeyListener, SplashListener {

    //region ---------------------------------------- Variables ------------------------------------------

    private static boolean isStarted = false;
    
    private static long currentTime = System.nanoTime();
    private static long lastTime = System.nanoTime();
    private static int frameCount = 0;    
    private static long totalTime = 0;
    private static int currentFps = 0;
    private static Timer timer;

    //endregion

    //region ---------------------------------------- INIT METHODS ----------------------------------------

    public Game() {
        super();
        setDoubleBuffered(true);
        setBackground(Color.BLACK);
        UI.init(this);
        initListeners();    
        initTimer();
    }

    private void initListeners() {
        Server.addListener(UI.getInstance());
        
        addMouseListener(SplashEffect.getInstance());
        SplashEffect.addListener(this);
        SplashEffect.addListener(UI.getInstance());
        SplashEffect.start();
    }

    @Override
    public void onSplashFinished() {
        setBackground(Constants.BACKGROUND_COLOR);
    }

    public static void onLeaveButtonClick() {
        if(Client.isConnected()) {
            Client.disconnect();
            return;
        }
        Server.close();
    }

    //region ---------------------------------------- BUTTON METHODS ----------------------------------------

    public static void start() {
        if(isStarted)
            return;

        isStarted = true;
        UI.openPage(Page.GAME);
    }

    public static void setPaused(boolean value) {
        isStarted = !value;
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

    //endregion

    private void initTimer() {
        timer = new Timer(Constants.DELTATIME_MS, this);
        timer.start();
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
            keyPressedGame(e);
    }

    private void onBack() {
        Page currentPage = UI.getCurrentPage();
        
        if(currentPage == null || currentPage == Page.MAIN_MENU)
            exit();

        if(currentPage == Page.GAME)
            setPaused(true);
        else if(currentPage == Page.PAUSE)
            setPaused(false);

        UI.goBack();
    }

    private static void keyPressedGame(KeyEvent e) {
        if(e == null)
            throw new IllegalArgumentException("KeyEvent cannot be null");

        if(!Client.isConnected())
            OfflinePlayerController.keyPressed(e);
        else
            updateDirection(e);
    }

    private static void updateDirection(KeyEvent e) {
        if(e == null)
            throw new IllegalArgumentException("KeyEvent cannot be null");

        Direction direction = Utils.keyToDirection(e.getKeyCode());

        if(direction == null)
            return;

        sendDirection(direction);
    }

    private static void sendDirection(Direction direction) {
        if(direction == null)
            throw new IllegalArgumentException("Direction cannot be null");

        Client.sendData(new RotatePacket(PlayerList.getCurrentPlayer().getId(), direction));
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
        draw((Graphics2D) g, UI.getCurrentPanel());
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
        
        if(!isStarted) {
            Image.BACKGROUND_IMAGE.draw(g, 0, 0, observer);
        }
        else {
            Tilemap.draw(g, observer);
            AppleManager.draw(g, observer);
            PlayerList.draw(g, observer);
            UI.drawPlayerBoard(g);
            
            if(DebugLog.isOn())
                PlayerList.drawColliders(g);
                Tilemap.drawColliders(g);
                AppleManager.drawColliders(g);

        }

        DebugLog.draw(g);
        Toolkit.getDefaultToolkit().sync();  // this smooths out animations on some systems
        //g.dispose();
    }

    public static String getInfo() {
        return "Current FPS: " + currentFps + "\n";
    }

}