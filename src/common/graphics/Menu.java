package common.graphics;

import java.util.HashMap;
import java.awt.Graphics2D;
import java.awt.event.ActionListener;

public class Menu<T extends Enum<T>> {

    private T currentPage;
    private final HashMap<T, Panel> panels = new HashMap<>();
    private final HashMap<T, T> backPages;

    public Menu(HashMap<T, T> backPages) {
        if (backPages == null)
            throw new IllegalArgumentException("Back pages cannot be null.");

        this.backPages = backPages;
    }

    public void setBackPages(T page, T backPage) {
        if (page == null || backPage == null)
            throw new IllegalArgumentException("Page and backPage cannot be null.");

        backPages.put(page, backPage);
    }

    public void addPanel(T page, Panel panel) {
        if (page == null || panel == null)
            throw new IllegalArgumentException("Page and panel cannot be null.");

        panels.put(page, panel);
    }

    public T getCurrentPage() {
        return currentPage;
    }

    public Panel getCurrentPanel() {
        return panels.get(currentPage);
    }

    public void openPage(T page) {
        if (page == null)
            return;

        currentPage = page;
        panels.forEach((p, panel) -> panel.setVisible(p == page));
    }

    public void goBack(ActionListener onExit) {
        if (onExit == null)
            throw new IllegalArgumentException("onExit cannot be null.");

        if (currentPage == null) {
            onExit.actionPerformed(null);
            return;
        }

        T backPage = getBackPage(currentPage);
        if (backPage == null) {
            onExit.actionPerformed(null);
            return;
        }
            
        openPage(backPage);
    }

    private T getBackPage(T page) {
        if (page == null)
            throw new IllegalArgumentException("Page cannot be null.");

        if (!backPages.containsKey(page))
            return null;

        return backPages.get(page);
    }

    public void drawBackground(Graphics2D g) {
        Panel currentPanel = getCurrentPanel();
        if (currentPanel == null)
            return;
        
        currentPanel.drawBackground(g);
    }

    public String getInfo() {
        return "Current page: " + currentPage;
    }
}