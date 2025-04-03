package common;

import client.MoveKey;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

public final class Utils {

    public static int getScore(int length, int defaultLength) {
        return (length - defaultLength) * 100;
    }

    public static Direction keyToDirection(int key) {
        int i = 0;
        for(MoveKey moveKey : MoveKey.values()) {
            if(moveKey.isEqual(key))
                return Direction.values()[i% Direction.values().length];
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

            if(pos[1] > Constants.SIZE.height - 20) {
                pos[0] += bounds.width;
                pos[1] = bounds.y + g.getFontMetrics().getHeight();
            }
        }
    }

    public static BufferedImage getRotatedImage(BufferedImage image, double angle) {
        AffineTransformOp op = getRotatedTransform(image, angle);
        return op.filter(image, null);
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

    public static Position moveTowards(Position position, Direction direction) {
        return new Position(position.x + direction.getX(), position.y + direction.getY());
    }

    public static Position clampPosition(Position position, int cols, int rows) {
        int x = (position.x + cols) % cols;
        int y = (position.y + rows) % rows;
        return new Position(x, y);
    }

    public static int[] calculateTextSize(Graphics2D g, String text) {
        int width = calculateTextWidth(g, text) + 20;
        int height = calculateTextHeight(g, text) + 20;
        
        if(height >= Constants.SIZE.height)
            width = width * (height / Constants.SIZE.height + 1) + 20;

        return new int[]{Math.clamp(width + 20, 0, Constants.SIZE.width), Math.clamp(height + 20, 0, Constants.SIZE.height)};
    }

    public static int calculateTextWidth(Graphics2D g, String text) {
        int width = 0;
        for (String line : text.split("\n")) {
            int lineWidth = g.getFontMetrics().stringWidth(line);
            if (lineWidth > width)
                width = lineWidth;

        }
        return width;
    }

    public static int calculateTextHeight(Graphics2D g, String text) {
        return g.getFontMetrics().getHeight() * text.split("\n").length;
    }

    public static int getRandomInt(int min, int max) {
        return (int) (Math.random() * (max - min + 1) + min);
    }

    public static Position getRandomPoint(int maxX, int maxY) {
        return new Position(getRandomInt(0, maxX - 1), getRandomInt(0, maxY - 1));
    }

    public static Position getRandomMapPoint(int rows, int cols) {
        return getRandomPoint(cols, rows);
    }

    public static Position getRandomPointFrom(ArrayList<Position> points) {
        return points.get(getRandomInt(0, points.size() - 1));
    }
    
    public static String dataToString(int[][] data) {
        if(data == null)
            return "";

        StringBuilder str = new StringBuilder();
        for (int i = 0; i < data.length; i++) {
            int[] row = data[i];
            for (int j = 0; j < row.length; j++) {
                str.append(row[j]);

                if(j != row.length - 1)
                    str.append(" ");
            }
            if(i != data.length - 1)
                str.append("\n");
        }
        return str.toString();
    }

    public static int[][] stringToData(String str) {
        String[] lines = str.split("\n");
        int[][] data = new int[lines.length][lines[0].split(" ").length];

        for(int i=0; i<lines.length; i++) {
            String[] tiles = lines[i].split(" ");
            
            for(int j=0; j<tiles.length; j++) {
                try {
                    data[i][j] = Integer.parseInt(tiles[j]);
                } catch (NumberFormatException ex) {
                    continue;
                }
            }
        }
        return data;
    }

    public static int calculateSpeed(int length) {
        return Math.max(10, 20 - length);
    }

    public static Dimension scale(Dimension size) {
        return new Dimension((int) (size.width * Constants.SCALEW), (int) (size.height * Constants.SCALEH));
    }
}