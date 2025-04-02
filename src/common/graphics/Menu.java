package common.graphics;

import java.util.HashMap;

import client.Page;

public class Menu {
    
    private static Page currentPage;
    private static final HashMap<Page, Panel> panels = new HashMap<>();
    
    public static void addPanel(Page page, Panel panel) {
        if(page == null || panel == null)
            throw new IllegalArgumentException("Page and panel cannot be null.");

        panels.put(page, panel);
    }

    public static Page getCurrentPage() {
        return currentPage;
    }

    public static Panel getCurrentPanel() {
        return panels.get(currentPage);
    }

    public static void openPage(Page page) {
        if(page == null)
            return;

        currentPage = page;
        panels.forEach((p, panel) -> panel.setVisible(p == page));
    }

    public static void goBack() {
        if(currentPage == null || currentPage.getBackPage() == null)
            return;
        
        openPage(currentPage.getBackPage());
    }

    public static String getInfo() {
        return "Current page: " + currentPage;
    }

}
