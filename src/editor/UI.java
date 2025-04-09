package editor;

import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.Box;

import common.Constants;
import common.graphics.Menu;
import common.graphics.image.SplashListener;
import common.graphics.panel.Panel;
import common.graphics.ui.Button;

public class UI implements SplashListener {

    private static UI INSTANCE;
    static final Menu<Page> MENU = new Menu<>(new HashMap<Page, Page>() {{
            put(Page.EDITOR, Page.MAIN_MENU);
    }});

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
        initWidgets(container);
    }

    private static void initWidgets(Container container) {
        add(container, Page.MAIN_MENU, new Component[] {
            new Button("Yeni", e -> listeners.forEach(listener -> listener.onNewButtonClicked())),
            new Button("Aç", e -> listeners.forEach(listener -> listener.onOpenButtonClicked())),
            new Button("Çıkış", e -> Editor.exit())
        });

        add(container, Page.EDITOR, new Component[] {});
    }

    private static void add(Container container, Page page, Component[] components) {
        if(page == null)
            throw new IllegalArgumentException("Page cannot be null");

        if(components == null)
            throw new IllegalArgumentException("Components cannot be null");
        
        container.add(addPanel(page, components));
    }

    @Override
    public void onSplashFinished() {
        MENU.openPage(Page.MAIN_MENU);
    }

    public static void initGraphics(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
    }

    private static Panel addPanel(Page page, Component[] components) {
        Panel panel = new Panel();
        
        if(components == null || components.length == 0)
            return panel;

        int gridWidth = 20; int gridHeight = 10;
        int totalCols = (int) Constants.SCREEN_SIZE.getWidth() / gridWidth;
        int componentWidth = 700; int componentHeight = 170;
        int componentRows = componentHeight / gridHeight; int componentColumns = componentWidth / gridWidth; // 35
        
        int leftCols = 10;
        int leftSpace = leftCols * gridWidth;
        int rightSpace = (int) Constants.SCREEN_SIZE.getWidth() - componentWidth - leftCols * gridWidth;
        int rightColumns = rightSpace / gridWidth;

        int botRows = 43 - (components.length - 1) * componentRows;
        int botSpace = botRows * gridHeight;
        int topSpace = (int) Constants.SCREEN_SIZE.getHeight() - componentHeight*components.length - botRows * gridHeight;
        int topRows = topSpace / gridHeight;

        panel.add(Box.createVerticalStrut(topSpace)        , 0                        , 0                                      , totalCols       , topRows); // Top space
        for(int i=0; i<components.length; i++) {
            panel.add(Box.createHorizontalStrut(leftSpace) , 0                        , topRows + (componentRows)*i                , leftCols        , componentRows);
            panel.add(components[i]                        , leftCols                   , topRows + (componentRows)*i                , componentColumns, componentRows);
            panel.add(Box.createHorizontalStrut(rightSpace), leftCols + componentColumns, topRows + (componentRows)*i                , rightColumns    , componentRows);
        }
        panel.add(Box.createVerticalStrut(botSpace)        , 0                        , topRows + componentRows*components.length, totalCols       , botRows); // Bottom space
        panel.setVisible(false);

        MENU.addPanel(page, panel);
        return panel;
    }
}