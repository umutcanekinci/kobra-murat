package game;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;

import network.server.Server;
import network.client.Client;
import network.client.PlayerList;

import java.awt.Font;

public class DebugLog {

    private static final Font FONT = new Font("Lato", Font.BOLD, 20);
    private static final Color COLOR = Color.WHITE;
    private static final Color BACKGROUND_COLOR = new Color(0, 0, 0, 150);
    
    private static final Rectangle DEBUG_RECT = new Rectangle(20, 0, 410, 0);
    public static ArrayList<String> playerList = new ArrayList<>();
    
    public static void draw(Graphics2D g) {
        drawDebug(g);
    }

    private static void drawDebug(Graphics2D g) {
        g.setFont(FONT);

        String text = 
        "DEBUG MODE ON - Press F2 to toggle\n" +
        "FPS: " + Board.FPS + "\n" +
        "SIZE: " + Board.SIZE.width + "x" + Board.SIZE.height + " px\n" +
        "LOCAL IP: " + Board.LOCAL_IP + "\n" +
        "HOST IP: " + Board.HOST_IP + " (Local: " + Board.isHostInLocal + ")\n" +
        "PORT: " + Board.PORT + "\n" +
        "\n" +
        PlayerList.getDebugInfo() + "\n" +
        Server.getString() + "\n" +
        Client.getState();

        DEBUG_RECT.height = Utils.calculateTextHeight(g, text) + 40;
        drawDebugBackground(g);
        drawText(g, text);
    }

    private static void drawDebugBackground(Graphics2D g) {
        g.setColor(BACKGROUND_COLOR);
        g.fillRect(0, DEBUG_RECT.y, DEBUG_RECT.width, DEBUG_RECT.height);
    }

    private static void drawText(Graphics2D g, String text) {
        Utils.drawText(g, text, COLOR, DEBUG_RECT, false);
    }

}
