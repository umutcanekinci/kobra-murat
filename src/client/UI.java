package client;

import java.awt.Font;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.Box;

import common.Constants;
import common.Utils;
import common.graphics.Menu;
import common.graphics.Panel;
import common.graphics.SplashListener;
import common.graphics.ui.Button;
import common.graphics.ui.TextField;
import server.Server;

public class UI implements SplashListener, ClientListener {

    private static UI INSTANCE;
    public static final Menu<Page> MENU = new Menu<>(new HashMap<Page, Page>() {{
            put(Page.PLAY_MODE, Page.MAIN_MENU);
            put(Page.GAME, Page.PAUSE);
            put(Page.PAUSE, Page.GAME);
            put(Page.CONNECT_MODE, Page.PLAY_MODE);
            put(Page.CONNECT, Page.CONNECT_MODE);
            put(Page.CUSTOMIZE, Page.LOBBY);
    }});

    private static final Font DEFAULT_FONT = new Font("Lato", Font.BOLD, 25);

    private static TextField hostField = new TextField("localhost");
    private static TextField portField = new TextField(Constants.PORT + "");

    private static final ArrayList<UIListener> listeners = new ArrayList<>();

    private UI() {}

    public static UI getInstance() {
        if(INSTANCE == null)
            INSTANCE = new UI();
            
        return INSTANCE;
    }

    public static void addListener(UIListener listener) {
        listeners.add(listener);
    }

    public static void init(Container container) {
        initPanels(container);
    }

    private static void initPanels(Container container) {
        container.add(addPanel(new Panel(), Page.MAIN_MENU, new Component[] {
            new Button("Oyna", e -> MENU.openPage(Page.PLAY_MODE)),
            new Button("Çıkış", e -> Game.exit())
        }));
        
        container.add(addPanel(new Panel(), Page.PLAY_MODE, new Component[] {
            new Button("Tek oyunculu", e -> OfflinePlayerController.init()),
            new Button("Çok oyunculu", e -> MENU.openPage(Page.CONNECT_MODE))
        }));

        container.add(addPanel(new Panel(), Page.CONNECT_MODE, new Component[] {
            new Button("Sunucu Aç", e -> listeners.forEach(UIListener::onHostButtonClicked)),
            new Button("Bağlan", e -> MENU.openPage(Page.CONNECT))
        }));

        container.add(addPanel(new Panel(), Page.CONNECT, new Component[] {
            hostField,
            portField,
            new Button("Bağlan", e -> listeners.forEach(l -> l.onConnectButtonClicked(hostField.getText(), Integer.parseInt(portField.getText())))),
        }));

        Button exitButton = new Button("");
        container.add(addPanel(new PausePanel(exitButton), Page.PAUSE, new Component[] {
            new Button("Devam et", e -> Game.start()),
            exitButton
        }));
        
        Button startButton = new Button("");
        container.add(addPanel(new LobbyPanel(startButton), Page.LOBBY, new Component[] {
            startButton,
            new Button("Ayrıl", e -> onLeaveButtonClick())
        }));

        container.add(addPanel(new Panel(), Page.PAUSE_LOBBY, new Component[] {
            new Button("Devam et", e -> Game.start()),
            new Button("Ayrıl", e -> onLeaveButtonClick())
        }));

        container.add(addPanel(new Panel(), Page.CUSTOMIZE, new Component[] {}));
        container.add(addPanel(new Panel(), Page.GAME, new Component[] {}));
    }

    public static void onStartButtonClicked() {
        listeners.forEach(UIListener::onStartButtonClicked);
    }

    public static void onReadyButtonClicked() {
        listeners.forEach(UIListener::onReadyButtonClicked);
    }

    public static void onLeaveButtonClick() {
        if(Server.isRunning())
            Server.close();
        else if(Client.isConnected())
            Client.close();
    }

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

    private static Panel addPanel(Panel panel, Page page, Component[] components) {
        
        if(components == null || components.length == 0)
            return panel;

        Dimension gridSize      = Constants.GRID_SIZE;
        Dimension componentSize = new Dimension(Button.SIZE.width, Button.SIZE.height*2);
        Dimension windowSize    = Constants.DEFAULT_SIZE;

        int totalCols        = windowSize.width / gridSize.width;
        int componentRows    = componentSize.height / gridSize.height;
        int componentCols    = componentSize.width / gridSize.width;

        int leftCols = 10;
        int leftSpace = leftCols * gridSize.width;
        int rightSpace = windowSize.width - componentSize.width - leftSpace;
        int rightCols = rightSpace / gridSize.width;

        int botRows = 30 - (components.length - 1) * componentRows;
        int botSpace = botRows * gridSize.height;
        int topSpace = windowSize.height - componentSize.height * components.length - botSpace;
        int topRows = topSpace / gridSize.height;

        panel.add(Box.createVerticalStrut(topSpace)        , 0                        , 0                                      , totalCols       , topRows); // Top space
        for(int i=0; i<components.length; i++) {
            panel.add(Box.createHorizontalStrut(leftSpace) , 0                        , topRows + (componentRows)*i                , leftCols        , componentRows);
            panel.add(components[i]                        , leftCols                   , topRows + (componentRows)*i                , componentCols, componentRows);
            panel.add(Box.createHorizontalStrut(rightSpace), leftCols + componentCols, topRows + (componentRows)*i                , rightCols    , componentRows);
        }
        panel.add(Box.createVerticalStrut(botSpace)        , 0                        , topRows + componentRows*components.length, totalCols       , botRows); // Bottom space
        panel.setVisible(false);

        MENU.addPanel(page, panel);
        return panel;
    }
}
