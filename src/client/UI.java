package client;

import java.awt.Font;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import client.panel.ConnectPanel;
import client.panel.GamePanel;
import client.panel.LobbyPanel;
import client.panel.PausePanel;
import common.Constants;
import common.Direction;
import common.Utils;
import common.graphics.image.SplashListener;
import common.graphics.panel.Menu;
import common.graphics.panel.Panel;
import common.graphics.panel.GridBagPanel;
import common.graphics.panel.SplashPanel;
import common.graphics.ui.Button;
import common.graphics.ui.TextField;
import server.Server;

public class UI implements GameListener, SplashListener, ClientListener {

    private static UI instance;
    public static final Menu<Page> MENU = new Menu<>(new HashMap<Page, Page>() {{
            put(Page.PLAY_MODE, Page.MAIN_MENU);
            put(Page.GAME, Page.PAUSE);
            put(Page.PAUSE, Page.GAME);
            put(Page.CONNECT_MODE, Page.PLAY_MODE);
            put(Page.CONNECT, Page.CONNECT_MODE);
            put(Page.CUSTOMIZE, Page.LOBBY);
    }});

    private static final Font DEFAULT_FONT = new Font("Lato", Font.BOLD, 25);
    private static final List<UIListener> listeners = new ArrayList<>();
   
    private UI() {}

    public static UI getInstance() {
        if(instance == null)
            instance = new UI();
            
        return instance;
    }

    public static void addListener(UIListener listener) {
        listeners.add(listener);
    }

    public static void init(Container container) {
        initPanels(container);
    }

    private static void initPanels(Container container) {
        initSplash(container);
        initMainMenu(container);
        initPlayMode(container);
        initConnectMode(container);
        initConnect(container);
        initPause(container);
        initLobby(container);
        initCustomize(container);
        initGame(container);
    }
    
    public static void initSplash(Container container) {
        container.add(addPanel(new SplashPanel(), Page.SPLASH));
    }

    private static void initMainMenu(Container container) {
        newPanel(container, Page.MAIN_MENU, new Component[] {
            new Button("Oyna", e -> MENU.openPage(Page.PLAY_MODE)),
            new Button("Çıkış", e -> Game.exit())
        });
    }

    private static void initPlayMode(Container container) {
        newPanel(container, Page.PLAY_MODE, new Component[] {
            new Button("Tek oyunculu", e -> OfflinePlayerController.init()),
            new Button("Çok oyunculu", e -> MENU.openPage(Page.CONNECT_MODE))
        });
    }
    
    private static void initConnectMode(Container container) {
        newPanel(container, Page.CONNECT_MODE, new Component[] {
            new Button("Sunucu Aç", e -> listeners.forEach(UIListener::onHostButtonClicked)),
            new Button("Bağlan", e -> MENU.openPage(Page.CONNECT))
        });
    }

    private static void initGame(Container container) {
        container.add(addPanel(new GamePanel(), Page.GAME));
    }

    private static void initConnect(Container container) {
        TextField hostField = new TextField("localhost");
        TextField portField = new TextField(Constants.PORT + ""); 
        container.add(addPanel(new ConnectPanel(hostField, portField), Page.PAUSE, new Component[] {
            new Button("Devam et", e -> Game.start()),
            hostField,
            portField,
            new Button("Bağlan", e -> listeners.forEach(l -> l.onConnectButtonClicked(hostField.getText(), Integer.parseInt(portField.getText())))),
        }));
    }

    private static void initPause(Container container) {
        Button exitButton = new Button("");
        container.add(addPanel(new PausePanel(exitButton), Page.PAUSE, new Component[] {
            new Button("Devam et", e -> Game.start()),
            exitButton
        }));
    }

    private static void initLobby(Container container) {   
        container.add(addPanel(new LobbyPanel(), Page.LOBBY));
    }

    private static void initCustomize(Container container) {
        newPanel(container,Page.CUSTOMIZE, new Component[] {
            new Button("Geri", e -> MENU.openPage(Page.MAIN_MENU)),
            new Button("Oyna", e -> listeners.forEach(UIListener::onStartButtonClicked))
        });
    }

    private static void newPanel(Container container, Page page, Component[] components) {
        container.add(addPanel(new GridBagPanel(), page, components));
    }

    private static Panel addPanel(Panel panel, Page page, Component[] components) {
        panel.addComponents(components);    
        return addPanel(panel, page);
    }

    private static Panel addPanel(Panel panel, Page page) {
        panel.setBackgroundImage(page.getBackgroundImage());
        MENU.addPanel(page, panel);
        return panel;
    }

    public static void onStartButtonClicked() {
        listeners.forEach(UIListener::onStartButtonClicked);
    }

    public static void onReadyButtonClicked() {
        listeners.forEach(UIListener::onReadyButtonClicked);
    }

    public static void onTerminateClicked() {
        Server.terminateLobby();
    }

    public static void onLeaveButtonClick() {
        if(Server.isRunning())
            Server.close();
        else if(Client.isConnected())
            Client.close();
    }

    @Override
    public void onWindowReady() {
        MENU.openPage(Page.SPLASH);
    }
    
    @Override
    public void onDirectionChanged(Direction direction) {}

    @Override
    public void onSplashFinished() {
        MENU.openPage(Page.MAIN_MENU);
    }

    @Override
    public void onClientConnected() {
        MENU.openPage(Page.LOBBY);
    }

    @Override
    public void onClientDisconnected() {
        MENU.openPage(Page.CONNECT_MODE);
    }

    public static void initGraphics(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
    }

    public static void drawPlayerBoard(Graphics2D g) {
        if(!Client.isConnected())
            return;

        int width = Constants.PLAYER_BOARD_WIDTH;
        int x = Constants.DEFAULT_SIZE.width - width;
        int y = 0;

        int count = PlayerList.getPlayerCount();
        int playerHeight = Utils.calculateTextHeight(g, "") + 15;
        int height = (count + 1) * playerHeight + 90;
        
        g.setFont(DEFAULT_FONT);
        g.setColor(Color.BLACK);
    
        g.fillRect(x, y, width, height);
        g.setColor(Color.WHITE);
        g.drawRect(x, y, width, height);

        String text = "PLAYERS";
        Utils.drawText(g, text, Color.WHITE, new Rectangle(x, y, width, playerHeight), true);
        g.drawLine(x, y + playerHeight + 60, x + width, y + playerHeight + 60);

        int i = 0;
        for (NetPlayer player : PlayerList.getPlayers()) {
            
            Utils.drawText(g, "Player " + player.getId(), player.isCurrentPlayer() ? Color.YELLOW : Color.WHITE, new Rectangle(x, y + (i + 2) * playerHeight + 10, width, playerHeight), true);
            i++;
        }
    }
}
