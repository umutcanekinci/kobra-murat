package client;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.Font;

import common.Constants;
import common.Utils;
import server.Server;

public class DebugLog {

    private static boolean debugMode = false;
    private static final Font FONT = new Font("Lato", Font.BOLD, 20);
    private static final Color COLOR = Color.WHITE;
    private static final Color BACKGROUND_COLOR = new Color(0, 0, 0, 150);
    private static final Rectangle RECT = new Rectangle(20, 0, 410, 0);
    
    public static boolean isOn() {
        return debugMode;
    }

    public static void draw(Graphics2D g) {
        if (g == null)
            throw new NullPointerException("Graphics2D object is null");

        if (!debugMode)
            return;

        g.setFont(FONT);
        String text = getInfo();
        int[] size = Utils.calculateTextSize(g, text);
        drawBackground(g, size[0], size[1]);
        drawText(g, text);
    }

    private static String getInfo() {
        return  "DEBUG MODE ON - Press F2 to toggle\n\n" +
                "FPS: " + Constants.FPS + "\n" +   
                Game.getInfo() + "\n" +
                UI.MENU.getInfo() + "\n" +
                Client.getInfo() + "\n" +
                Tilemap.getInfo() + "\n\n" +
                PlayerList.getInfo() + "\n" +
                AppleManager.getInfo() + "\n\n" +
                Server.getInfo() + "\n";
    }

    private static void drawBackground(Graphics2D g, int width, int height) {
        if (g == null)
            throw new NullPointerException("Graphics2D object is null");

        if (width <= 0 || height <= 0)
            throw new IllegalArgumentException("Width and height must be greater than 0");

        g.setColor(BACKGROUND_COLOR);
        g.fillRect(0,0, width, height);

        g.setColor(COLOR);
        g.drawRect(0, 0, width, height);
    }

    private static void drawText(Graphics2D g, String text) {
        if (g == null)
            throw new NullPointerException("Graphics2D object is null");

        if (text == null || text.isEmpty())
            throw new IllegalArgumentException("Text cannot be null or empty");

        Utils.drawText(g, text, COLOR, RECT, false);
    }

    static void keyPressed(KeyEvent e) {
        if (e == null)
            throw new NullPointerException("KeyEvent object is null");

        if (e.getKeyCode() == KeyEvent.VK_F2)
            debugMode = !debugMode;
    }
}
