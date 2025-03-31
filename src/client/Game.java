package client;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.Timer;

import common.Constants;
import common.Direction;
import common.ServerListener;
import common.Utils;
import common.Window;
import common.graphics.Image;
import common.graphics.Panel;
import common.graphics.SplashEffect;
import common.graphics.SplashListener;
import common.packet.RotatePacket;
import common.packet.basic.StartPacket;
import client.graphics.Draw;
import client.graphics.UI;
import server.Server;
import common.graphics.ui.Button;
import common.graphics.ui.TextField;
import client.graphics.UI.Page;

public class Game extends JPanel implements ActionListener, KeyListener, ServerListener, SplashListener {

    //region ---------------------------------------- Variables ------------------------------------------

    private static boolean isStarted = false;

    private static TextField hostField = new TextField(Constants.PORT + "");;
    private static TextField portField = new TextField("localhost");
    
    private static long currentTime = System.nanoTime();
    private static long lastTime = System.nanoTime();
    private static int frameCount = 0;    
    private static long totalTime = 0;
    private static int currentFps = 0;
    private static Timer timer;

    private static final ArrayList<GameListener> listeners = new ArrayList<>();

    public static void addListener(GameListener listener) {
        listeners.add(listener);
    }

    //endregion

    //region ---------------------------------------- INIT METHODS ----------------------------------------

    public Game() {
        super();
        setDoubleBuffered(true);
        setFullscreen();
        setBackground(Color.BLACK);
        initSplash();
        initServer();
        initWidgets();
        
        listeners.forEach(GameListener::onWindowReady);
        initTimer();
    }

    private void initSplash() {
        SplashEffect splashEffect = new SplashEffect();
        SplashEffect.setListener(this);
        addListener(splashEffect);
        addMouseListener(splashEffect);
        //addKeyListener(splashEffect);
    }

    @Override
    public void onSplashFinished() {
        UI.openPage(Page.MAIN_MENU);
    }

    private void setFullscreen() {
        setPreferredSize(Constants.SIZE);
    }

    private void initServer() {
        Server.init(Constants.PORT);
        Server.setListener(this);
    }

    private void initWidgets() {
        addPanel(Page.MAIN_MENU, new Component[] {
            new Button("Başla", e -> UI.openPage(Page.PLAY_MODE)),
            new Button("Çıkış", e -> exit())
        });
        
        addPanel(Page.PLAY_MODE, new Component[] {
            new Button("Tek oyunculu", e -> sendStart()),
            new Button("Çok oyunculu", e -> UI.openPage(Page.CONNECT_MODE))
        });

        addPanel(Page.CONNECT_MODE, new Component[] {
            new Button("Sunucu Aç", e -> onHostButtonClick()),
            new Button("Bağlan", e -> UI.openPage(Page.CONNECT))
        });

        addPanel(Page.CONNECT, new Component[] {
            hostField, portField, new Button("Bağlan", e -> onConnectButtonClick())
        });

        addPanel(Page.PAUSE, new Component[] {
            new Button("Devam et", e -> UI.openPage(Page.GAME)),
            new Button("Ana menü", e -> UI.openPage(Page.MAIN_MENU)),
        });

        addPanel(Page.LOBBY, new Component[] {
            new Button("Başla", e -> sendStart()),
            new Button("Ana menü", e -> UI.openPage(Page.MAIN_MENU)),
            new Button("Çıkış", e -> exit())
        });

        addPanel(Page.GAME, new Component[] {});
    }

    private void addPanel(Page page, Component[] components) {
        add(UI.addPanel(page, components));
    }

    //region ---------------------------------------- BUTTON METHODS ----------------------------------------

    private static void sendStart() {   
        if(Client.isConnected())
            sendStartPacket();
        else
            OfflinePlayerController.init();
    }

    private static void sendStartPacket() {
        Client.sendData(new StartPacket(PlayerList.getId()));
    }

    public static void start() {
        if(isStarted)
            return;

        isStarted = true;
        UI.openPage(Page.GAME);
    }

    public static void setPaused(boolean value) {
        isStarted = !value;
    }

    private static void onConnectButtonClick() {
        if(Client.isConnected()) {
            Client.disconnect();
            return;
        }
        updateClient();
        connect();
    }

    private static void updateClient() {
        Client.setHost(hostField.getText());
        Client.setPort(Integer.parseInt(portField.getText()));
    }

    private static void connect() {
        PlayerList.clear();
        Client.start();
    }

    private static void onHostButtonClick() {
        if(Server.isRunning()) {
            Server.close();
            return;
        }
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

    public void onServerStateChange(Server.State state) {
        switch (state) {
            case CONNECTED:
                onServerOpened();
                break;
            case CLOSED:
                onServerClosed();
                break;
            case LISTENING:
                break;
        }
    }

    public static void onClientConnected() {
    }

    public static void onClientDisconnected() {
    }

    public static void onServerOpened() {
        connect();
    }

    public static void onServerClosed() {
    }

    //endregion

    private void initTimer() {
        timer = new Timer(Constants.DELTATIME_MS, this);
        timer.start();
    }

    //endregion

    //region ---------------------------------------- INPUT METHODS ---------------------------------------

    public static boolean isStarted() {
        return isStarted;
    }

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
        if(!Client.isConnected())
            OfflinePlayerController.keyPressed(e);
        else
            updateDirection(e);
    }

    private static void updateDirection(KeyEvent e) {
        Direction direction = Utils.keyToDirection(e.getKeyCode());

        if(direction == null)
            return;

        sendDirection(direction);
    }

    private static void sendDirection(Direction direction) {
        Client.sendData(new RotatePacket(PlayerList.getCurrentPlayer().getId(), direction));
    }

    //endregion

    @Override
    public void actionPerformed(ActionEvent e) {
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
        
        if(SplashEffect.isPlaying()) {
            SplashEffect.draw((Graphics2D) g, this);
            return;
        }

        if(!isStarted)
            Image.BACKGROUND_IMAGE.draw((Graphics2D) g, 0, 0, this);

        Panel currentPanel = UI.getCurrentPanel();
        Draw.all((Graphics2D) g, currentPanel, isStarted, currentPanel); // Draw everything
        
        //g.dispose();
    }

    public static String getInfo() {
        return "Current FPS: " + currentFps + "\n";
    }

}
