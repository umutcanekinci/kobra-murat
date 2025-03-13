package game;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import network.PlayerList;
import network.server.Server;
import network.client.Client;
import java.awt.Font;

public class DebugLog {

    private static final Color BACKGROUND_COLOR = new Color(0, 0, 0, 150);
    private static final Font FONT = new Font("Lato", Font.BOLD, 20);
    private static final Rectangle DEBUG_RECT = new Rectangle(20, 20, 410, 0);
    private static final Color[] DEBUG_COLORS = {
            new Color(255, 0, 0),
            new Color(0, 255, 0),
            new Color(50, 120, 255),
    };
    public static final ArrayList<String> debugText = new ArrayList<>();
    public static ArrayList<String> playerList = new ArrayList<>();
    
    public static void draw(Graphics2D g) {
        drawDebugBackground(g);
        drawDebug(g);
    }

    private static void drawDebugBackground(Graphics2D g) {
        g.setColor(BACKGROUND_COLOR);
        g.fillRect(0, 0, DEBUG_RECT.width, DEBUG_RECT.y + 20);
    }

    private static void drawDebug(Graphics2D g) {
        g.setFont(FONT);
        DEBUG_RECT.y = 20;
        drawDebugText(g, "Player List");
        drawPlayerList(g);
        DEBUG_RECT.y = drawText(g, Server.getState(), DEBUG_COLORS[1]);
        DEBUG_RECT.y = drawText(g, Client.getState(), DEBUG_COLORS[1]);
    }

    private static int drawText(Graphics2D g, String text, Color color) {
        return Utils.drawText(g, text, color, DEBUG_RECT, false);
    }

    private static void drawDebugText(Graphics2D g, String text) {
        for (String s : debugText) {
            DEBUG_RECT.y = drawText(g, s, DEBUG_COLORS[0]);
        }   
    }

    private static void drawPlayerList(Graphics2D g) {
        for (String s : PlayerList.getDebugInfo()) {
            DEBUG_RECT.y = drawText(g, s, DEBUG_COLORS[2]);
        }
    }
}
