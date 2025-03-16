package client;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Font;

import common.Utils;
import server.Server;

public class DebugLog {

    private static final Font FONT = new Font("Lato", Font.BOLD, 20);
    private static final Color COLOR = Color.WHITE;
    private static final Color BACKGROUND_COLOR = new Color(0, 0, 0, 150);
    private static final Rectangle RECT = new Rectangle(20, 0, 410, 0);
    
    public static void draw(Graphics2D g) {
        g.setFont(FONT);
        String text = getText();
        int[] size = Utils.calculateTextSize(g, text);
        drawBackground(g, size[0], size[1]);
        drawText(g, text);
    }

    private static String getText() {
        return  "DEBUG MODE ON - Press F2 to toggle\n\n" +
                "FPS: " + Board.FPS + "\n" +
                "Connect to Local Mode: " + Board.isHostInLocal + "\n\n" +        
                Client.getInfo() + "\n" +
                Tilemap.getInfo() + "\n\n" +
                PlayerList.getInfo() + "\n" +
                //AppleManager.getInfo() + "\n\n" +
                Server.getInfo() + "\n";
    }

    private static void drawBackground(Graphics2D g, int width, int height) {
        g.setColor(BACKGROUND_COLOR);
        g.fillRect(0,0, width, height);

        g.setColor(COLOR);
        g.drawRect(0, 0, width, height);
    }

    private static void drawText(Graphics2D g, String text) {
        Utils.drawText(g, text, COLOR, RECT, false);
    }

}
