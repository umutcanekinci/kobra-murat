package common.graphics.image;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import common.Constants;
import common.Position;
import common.Utils;

public class Image {

    public static final Image SPRITESHEET          = new Image("images/honored.png");
    public static final Image TILESHEET            = new Image("images/tilesheet.png");
    public static final Image APPLE                = new Image("images/apple.png");

    private static final Logger LOGGER = Logger.getLogger(Utils.class.getName());
    private BufferedImage image;

    public Image() {}

    public Image(String path) {
        this(new File(path));
    }

    public Image(File imageFile) {
        load(imageFile);
    }

    private void load(File imageFile) {
        set(readImageFile(imageFile));
    }

    private static BufferedImage readImageFile(File imageFile) {
        try {
            return ImageIO.read(imageFile);
        } catch (IOException exc) {
            LOGGER.log(Level.SEVERE, "Error opening image file: " + exc.getMessage(), exc);
            return null;
        }
    }

    public void set(BufferedImage image) {
        this.image = image;
    }

    public BufferedImage get() {
        return image;
    }

    public int getWidth() {
        return image.getWidth();
    }

    public int getHeight() {
        return image.getHeight();
    }

    public void draw(Graphics2D g, Position screenPosition, ImageObserver observer) {
        g.drawImage(image, screenPosition.x, screenPosition.y, observer);
    }

    public void drawBorder(Graphics2D g, Color color, Point position) {
        g.setColor(color);
        this.drawBorder(g, position);
    }

    public void drawBorder(Graphics2D g, Point position) {
        g.drawRect(
                position.x * Constants.TILE_SIZE,
                position.y * Constants.TILE_SIZE,
                Constants.TILE_SIZE,
                Constants.TILE_SIZE
        );
    }

}
