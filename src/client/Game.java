package client;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.event.*;
import javax.swing.JPanel;
import javax.swing.Timer;

import common.Constants;
import common.Direction;
import common.ServerListener;
import common.Utils;
import common.graphics.Image;
import common.graphics.Panel;
import common.packet.RotatePacket;
import common.packet.basic.StartPacket;
import client.graphics.Draw;
import client.graphics.UI;
import server.Server;
import common.graphics.ui.Button;
import common.graphics.ui.TextField;
import client.graphics.UI.Page;

public class Game extends JPanel implements ActionListener, KeyListener, ServerListener {

    //region ---------------------------------------- Variables ------------------------------------------

    public static boolean isStarted = false;
    public static boolean debugMode = false;

    private static TextField hostField = new TextField(Constants.PORT + "");;
    private static TextField portField = new TextField("localhost");
    
    //endregion

    //region ---------------------------------------- INIT METHODS ----------------------------------------

    public Game() {
        super();
        setFullscreen();
        initServer();
        initWidgets();
        UI.openPage(Page.MAIN_MENU);
        initTimer();
    }

    private void setFullscreen() {
        setPreferredSize(Constants.SIZE);
    }

    private void initServer() {
        Server.init(Constants.PORT);
        Server.setListener(this);
    }

    private static void initClient() {
        Client.setHost(hostField.getText());
        Client.setPort(Integer.parseInt(portField.getText()));
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
            new Button("Sunucu", e -> onHostButtonClick()),
            new Button("Bağlan", e -> UI.openPage(Page.CONNECT))
        });

        
        hostField = new TextField("localhost");
        addPanel(Page.CONNECT, new Component[] {
            hostField, portField, new Button("Bağlan", e -> onConnectButtonClick())
        });

        addPanel(Page.PAUSE, new Component[] {
            new Button("Devam et", e -> UI.openPage(Page.GAME)),
            new Button("Ana menü", e -> UI.openPage(Page.MAIN_MENU)),
            new Button("Çıkış", e -> exit())
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

    public static void start() {
        isStarted = true;
        UI.openPage(Page.GAME);
    }

    private static void sendStartPacket() {
        Client.sendData(new StartPacket(PlayerList.getId()));
    }

    private static void onConnectButtonClick() {
        if(Client.isConnected()) {
            Client.disconnect();
            return;
        }
        initClient();
        connect();
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
        new Timer(Constants.DELTATIME_MS, this).start();
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

        if(e.getKeyCode() == KeyEvent.VK_F2)
            debugMode = !debugMode;
        
        if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            onBack();
        }
            
        if(isStarted)
            keyPressedGame(e);
    }

    private static void onBack() {
        if(UI.getCurrentPage() != Page.MAIN_MENU)
            UI.goBack();
        else
            exit();
    }

    private static void keyPressedGame(KeyEvent e) {
        if(!Client.isConnected())
            OfflinePlayerController.keyPressed(e);
        else
            updateDirection(e);
        
        switch (e.getKeyCode()) {
            case KeyEvent.VK_ESCAPE:
                openMenu();
                break;
        }
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

    public static void openMenu() {
        isStarted = false;
        UI.openPage(Page.MAIN_MENU);
    }

    //endregion

    @Override
    public void actionPerformed(ActionEvent e) {
        if(isStarted)
        {    
            if(!Client.isConnected())
                OfflinePlayerController.update();
        }
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if(!isStarted)
            Image.MAIN_MENU_BACKGROUND.draw((Graphics2D) g, 0, 0, this);

        Panel currentPanel = UI.getCurrentPanel();
        if (currentPanel == null)
            return;
        Draw.all((Graphics2D) g, currentPanel, isStarted, debugMode, currentPanel); // Draw everything
    }

}
