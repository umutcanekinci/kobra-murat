package client;

import java.awt.Font;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Rectangle;
import java.util.ArrayList;
import javax.swing.Box;

import common.Constants;
import common.ServerListener;
import common.Utils;
import common.graphics.Menu;
import common.graphics.Panel;
import common.graphics.SplashListener;
import common.graphics.ui.Button;
import common.graphics.ui.TextField;
import server.Server;

public class UI implements ServerListener, UIListener, SplashListener {

    private static UI INSTANCE = null;
    private static final Font DEFAULT_FONT = new Font("Lato", Font.BOLD, 25);

    private static TextField hostField = new TextField("localhost");
    private static TextField portField = new TextField(Constants.PORT + "");

    private static final ArrayList<UIListener> listeners = new ArrayList<>();

    private UI() {}

    public static UI getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new UI();
        }
        return INSTANCE;
    }

    public static void addListener(UIListener listener) {
        listeners.add(listener);
    }

    public static void init(Container container) {
        initWidgets(container);
    }

    private static void initWidgets(Container container) {
        add(container, Page.MAIN_MENU, new Component[] {
            new Button("Başla", e -> Menu.openPage(Page.PLAY_MODE)),
            new Button("Çıkış", e -> Game.exit())
        });
        
        add(container, Page.PLAY_MODE, new Component[] {
            new Button("Tek oyunculu", e -> OfflinePlayerController.init()),
            new Button("Çok oyunculu", e -> Menu.openPage(Page.CONNECT_MODE))
        });

        add(container, Page.CUSTOMIZE, new Component[] {});

        add(container, Page.CONNECT_MODE, new Component[] {
            new Button("Sunucu Aç", e -> listeners.forEach(UIListener::onHostButtonClicked)),
            new Button("Bağlan", e -> Menu.openPage(Page.CONNECT))
        });

        add(container, Page.CONNECT, new Component[] {
            hostField,
            portField,
            new Button("Bağlan", e -> listeners.forEach(listener -> listener.onConnectButtonClicked(hostField.getText(), Integer.parseInt(portField.getText())))),
        });

        add(container, Page.PAUSE, new Component[] {
            new Button("Devam et", e -> Game.start()),
            new Button("Ana menü", e -> Menu.openPage(Page.MAIN_MENU)),
        });

        Button leaveButton = new Button("Ayrıl", e -> onLeaveButtonClick());
        add(container, Page.LOBBY, new Component[] {
            new Button("Başlat", e -> listeners.forEach(UIListener::onStartButtonClicked)),
            leaveButton
        });

        add(container, Page.GAME, new Component[] {});
    }

    public static void onLeaveButtonClick() {
        if(Client.isConnected()) {
            Client.disconnect();
            return;
        }
        Server.close();
    }

    private static void add(Container container, Page page, Component[] components) {
        if(page == null)
            throw new IllegalArgumentException("Page cannot be null");

        if(components == null)
            throw new IllegalArgumentException("Components cannot be null");
        
        container.add(addPanel(page, components));
    }

    @Override
    public void onServerConnected(String ip) {
        Menu.openPage(Page.LOBBY);
    }

    @Override
    public void onServerClosed() {
        Menu.openPage(Page.CONNECT_MODE);
    }

    @Override
    public void onSplashFinished() {
        Menu.openPage(Page.MAIN_MENU);
    }

    @Override
    public void onConnectButtonClicked(String host, int ip) {}

    @Override
    public void onHostButtonClicked() {}

    @Override
    public void onStartButtonClicked() {
        Menu.openPage(Page.GAME);
    }

    public static void initGraphics(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
    }

    public static void drawPlayerBoard(Graphics2D g) {
        int width = Constants.PLAYER_BOARD_WIDTH;
        int x = Constants.MAX_SIZE.width - width;
        int y = 0;

        int count = PlayerList.getPlayerCount();
        int playerHeight = Utils.calculateTextHeight(g, "") + 15;
        int height = (count + 1) * playerHeight + 90;
        
        g.setFont(DEFAULT_FONT);
        g.setColor(Color.BLACK);
        System.out.println("x: " + x + ", y: " + y + ", width: " + width + ", height: " + height);
        g.fillRect(x, y, width, height);
        g.setColor(Color.WHITE);
        g.drawRect(x, y, width, height);

        String text = "PLAYERS";
        Utils.drawText(g, text, Color.WHITE, new Rectangle(x, y, width, playerHeight), true);
        g.drawLine(x, y + playerHeight + 60, x + width, y + playerHeight + 60);

        int i = 0;
        for (NetPlayer player : PlayerList.getPlayers()) {
            Utils.drawText(g, "Player " + player.getId(), Color.WHITE, new Rectangle(x, y + (i + 2) * playerHeight + 10, width, playerHeight), true);
            i++;
        }
    }

    private static Panel addPanel(Page page, Component[] components) {
        Panel panel = new Panel();
        
        if(components == null || components.length == 0)
            return panel;

        int gridWidth = 20; int gridHeight = 10;
        int totalCols = (int) Constants.SIZE.getWidth() / gridWidth;
        int componentWidth = 700; int componentHeight = 170;
        int componentRows = componentHeight / gridHeight; int componentColumns = componentWidth / gridWidth; // 35
        
        int leftCols = 10;
        int leftSpace = leftCols * gridWidth;
        int rightSpace = (int) Constants.SIZE.getWidth() - componentWidth - leftCols * gridWidth;
        int rightColumns = rightSpace / gridWidth;

        int botRows = 43 - (components.length - 1) * componentRows;
        int botSpace = botRows * gridHeight;
        int topSpace = (int) Constants.SIZE.getHeight() - componentHeight*components.length - botRows * gridHeight;
        int topRows = topSpace / gridHeight;

        panel.add(Box.createVerticalStrut(topSpace)        , 0                        , 0                                      , totalCols       , topRows); // Top space
        for(int i=0; i<components.length; i++) {
            panel.add(Box.createHorizontalStrut(leftSpace) , 0                        , topRows + (componentRows)*i                , leftCols        , componentRows);
            panel.add(components[i]                        , leftCols                   , topRows + (componentRows)*i                , componentColumns, componentRows);
            panel.add(Box.createHorizontalStrut(rightSpace), leftCols + componentColumns, topRows + (componentRows)*i                , rightColumns    , componentRows);
        }
        panel.add(Box.createVerticalStrut(botSpace)        , 0                        , topRows + componentRows*components.length, totalCols       , botRows); // Bottom space
        panel.setVisible(false);

        Menu.addPanel(page, panel);
        return panel;
    }
}
