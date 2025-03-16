package common;

import javax.imageio.ImageIO;

import client.MoveKey;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.io.File;
import java.util.ArrayList;
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

    public static int getScore(int length, int defaultLength) {
        return (length - defaultLength) * 100;
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
        for (String line : text.split("\n")) {
            g.drawString(line, pos[0], pos[1] += g.getFontMetrics().getHeight());

            if(pos[1] > common.Level.SIZE.height - 20) {
                pos[0] += bounds.width;
                pos[1] = bounds.y + g.getFontMetrics().getHeight();
            }
        }
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
        int x = (position.x + common.Level.COLUMNS) % common.Level.COLUMNS;
        int y = (position.y + common.Level.ROWS) % common.Level.ROWS;
        return new Point(x, y);
    }

    

    public static int[] calculateTextSize(Graphics2D g, String text) {
        int width = calculateTextWidth(g, text) + 20;
        int height = calculateTextHeight(g, text) + 20;
        
        if(height >= common.Level.SIZE.height)
            width = width * (height / common.Level.SIZE.height + 1) + 20;

        return new int[]{Math.clamp(width + 20, 0, common.Level.SIZE.width), Math.clamp(height + 20, 0, common.Level.SIZE.height)};
    }

    public static int calculateTextWidth(Graphics2D g, String text) {
        int width = 0;
        for (String line : text.split("\n")) {
            int lineWidth = g.getFontMetrics().stringWidth(line);
            if (lineWidth > width) {
                width = lineWidth;
            }
        }
        return width;
    }

    public static int calculateTextHeight(Graphics2D g, String text) {
        return g.getFontMetrics().getHeight() * text.split("\n").length;
    }

    public static int getRandomInt(int min, int max) {
        return (int) (Math.random() * (max - min + 1) + min);
    }

    public static Point getRandomPoint(int maxX, int maxY) {
        return new Point(getRandomInt(0, maxX - 1), getRandomInt(0, maxY - 1));
    }

    public static Point getRandomMapPoint(int rows, int cols) {
        return getRandomPoint(cols, rows);
    }

    public static Point getRandomPointFrom(ArrayList<Point> points) {
        return points.get(getRandomInt(0, points.size() - 1));
    }

    public static int calculateFps(long lastTime, long currentTime) {
        return (int) (1000 / (currentTime - lastTime));
    }
}