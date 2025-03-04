package game;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.File;

public final class Utils {

    public static BufferedImage loadImage(File imageFile) {
        try {
            return ImageIO.read(imageFile);
        } catch (IOException exc) {
            System.out.println("Error opening image file: " + exc.getMessage());
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

    public static int drawText(Graphics2D g, String text, Color color, Rectangle bounds, boolean center) {

        // set the text color and font
        g.setColor(color);

        int[] pos = getTextPosition(g, text, bounds, center);
        g.drawString(text, pos[0], pos[1]);

        return pos[1]; // the y position (I think it is the bottom position)
    }

    public static AffineTransformOp getRotatedTransform(BufferedImage image, double angle) {
        double rotationRequired = Math.toRadians (angle);
        double locationX = (double) image.getWidth() / 2;
        double locationY = (double) image.getHeight() / 2;
        AffineTransform tx = AffineTransform.getRotateInstance(rotationRequired, locationX, locationY);
        return new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
    }

}