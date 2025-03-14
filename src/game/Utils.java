package game;

import javax.imageio.ImageIO;
import game.player.Direction;
import game.player.MoveKey;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class Utils {

    private static final Logger LOGGER = Logger.getLogger(Utils.class.getName());

    public static BufferedImage loadImage(File imageFile) {
        try {
            return ImageIO.read(imageFile);
        } catch (IOException exc) {
            LOGGER.log(Level.SEVERE, "Error opening image file: " + exc.getMessage(), exc);
            return null;
        }
    }

    public static Direction keyToDirection(int key) {
        int i = 0;
        for(MoveKey moveKey : MoveKey.values()) {
            if(moveKey.isEqual(key)) {
                return Direction.values()[i% Direction.values().length];
            }
            i++;
        }
        return null;
    }

    public static int[] getTextPosition(Graphics2D g2d, String text, Rectangle bounds, boolean center) {
        FontMetrics metrics = g2d.getFontMetrics();
        int x, y;
        if (center) {
            x = bounds.x + (bounds.width - metrics.stringWidth(text)) / 2;
            y = bounds.y + ((bounds.height - metrics.getHeight()) / 2) + metrics.getAscent();
        } else {
            x = bounds.x;
            y = bounds.y + metrics.getAscent();
        }
        return new int[]{x, y};
    }

    public static void drawText(Graphics2D g, String text, Color color, Rectangle bounds, boolean center) {

        // set the text color and font
        g.setColor(color);

        int[] pos = getTextPosition(g, text, bounds, center);
        for (String line : text.split("\n"))
            g.drawString(line, pos[0], pos[1] += g.getFontMetrics().getHeight());
    }

    public static AffineTransformOp getRotatedTransform(BufferedImage image, double angle) {
        double rotationRequired = Math.toRadians (angle);
        double locationX = (double) image.getWidth() / 2;
        double locationY = (double) image.getHeight() / 2;
        AffineTransform tx = AffineTransform.getRotateInstance(rotationRequired, locationX, locationY);
        return new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
    }

    public static String getLocalIp() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return "Unknown";
        }
    }

    public static Point moveTowards(Point position, Direction direction) {
        return new Point(position.x + direction.getX(), position.y + direction.getY());
    }

    public static Point clampPosition(Point position) {
        int x = (position.x + Board.COLUMNS) % Board.COLUMNS;
        int y = (position.y + Board.ROWS) % Board.ROWS;
        return new Point(x, y);
    }

    public static int calculateTextHeight(Graphics2D g, String text) {
        return g.getFontMetrics().getHeight() * text.split("\n").length;
    }

    public static Point getRandomPoint(int maxX, int maxY) {
        return new Point((int) (Math.random() * maxX), (int) (Math.random() * maxY));
    }

    public static Point getRandomMapPoint(int rows, int cols) {
        return getRandomPoint(cols, rows);
    }
}