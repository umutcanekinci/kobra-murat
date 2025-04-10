package editor;

import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.HashMap;

import common.graphics.image.SplashListener;
import common.graphics.panel.Menu;
import common.graphics.panel.Panel;
import common.graphics.panel.GridBagPanel;
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
        
        newPanel(container, page, components);
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

    @Override
    public void onSplashFinished() {
        MENU.openPage(Page.MAIN_MENU);
    }

    public static void initGraphics(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
    }

}