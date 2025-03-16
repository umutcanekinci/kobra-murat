package client.graphics;
import java.awt.*;
import java.io.File;
import javax.swing.BorderFactory;

import javax.swing.JButton;

import client.NetPlayer;
import client.PlayerList;
import common.Utils;
import common.Level;

import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

public class UI {

    private static final Color BUTTON_COLOR = new Color(84, 148, 20);
    private static final Color BUTTON_TEXT_COLOR = Color.WHITE;
    private static final Color BUTTON_BORDER_COLOR = Color.BLACK;

    // Fonts
    private static Rectangle SCORE_RECT;
    private static final Font DEFAULT_FONT = new Font("Lato", Font.BOLD, 25);
    
    // Title image file and buffered image
    private static final File TITLE_IMAGE = new File("images/title.png");
    private static BufferedImage titleImage;
    
    public static void init() {
        SCORE_RECT = new Rectangle(0, Level.TILE_SIZE * (Level.ROWS - 1), Level.TILE_SIZE * Level.COLUMNS, Level.TILE_SIZE);
        titleImage = Utils.loadImage(TITLE_IMAGE);
    }

    public static void initGraphics(Graphics2D g) {
        if (g == null) {
            return;
        }
        
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
    }

    public static JButton newButton(String text) {
        if(text == null) {
            return null;
        }
        
        JButton button = new JButton(text);
        button.setRequestFocusEnabled(false);
        button.setBackground(BUTTON_COLOR);
        button.setForeground(BUTTON_TEXT_COLOR);
        button.setBorder(BorderFactory.createLineBorder(BUTTON_BORDER_COLOR, 2));
        button.setFont(new Font("Arial", Font.BOLD, 20));
        button.setPreferredSize(new Dimension(200, 100));

        return button;
    }

    public static void drawTitle(Graphics2D g, int width, int y, ImageObserver observer) {
        if (g == null) {
            return;
        }
        
        g.drawImage(titleImage, (width - titleImage.getWidth()) / 2, y, observer);
    }

    public static void drawScore(Graphics2D g, int score) {
        if (g == null) {
            return;
        }
        
        g.setFont(DEFAULT_FONT);
        String text = "Score: " + score;
        Utils.drawText(g, text, Color.GREEN, SCORE_RECT, true);
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
