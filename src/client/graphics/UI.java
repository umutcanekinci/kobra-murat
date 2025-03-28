package client.graphics;

import java.awt.image.ImageObserver;
import java.awt.Font;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Rectangle;

import common.Utils;
import common.graphics.Image;
import client.NetPlayer;
import client.PlayerList;

public class UI {

    // Fonts
    //private static Rectangle SCORE_RECT;
    private static final Font DEFAULT_FONT = new Font("Lato", Font.BOLD, 25);
        
    public static void init() {
        //SCORE_RECT = new Rectangle(0, Constants.TILE_SIZE * (Level.ROWS - 1), Constants.TILE_SIZE * Level.COLUMNS, Constants.TILE_SIZE);
    }

    public static void initGraphics(Graphics2D g) {
        if (g == null) {
            return;
        }
        
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
    }

    public static void drawTitle(Graphics2D g, int width, int y, ImageObserver observer) {
        if (g == null) {
            return;
        }
        
        g.drawImage(Image.TITLE, (width - Image.TITLE.getWidth()) / 2, y, observer);
    }

    public static void drawScore(Graphics2D g, int score) {
        if (g == null) {
            return;
        }
        
        g.setFont(DEFAULT_FONT);
        //String text = "Score: " + score;
        //Utils.drawText(g, text, Color.GREEN, SCORE_RECT, true);
    }

    public static void drawPlayerBoard(Graphics2D g, int x, int y, int width) {
        if (g == null) {
            return;
        }

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
            Utils.drawText(g, "Player " + player.getId(), Color.WHITE, new Rectangle(x, y + (i + 2) * playerHeight + 10, width, playerHeight), true);
        }
    }
}
