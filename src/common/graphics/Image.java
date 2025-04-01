package common.graphics;

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
import common.Utils;

public class Image {

    public static final Image BACKGROUND_IMAGE     = new Image("images/main-menu-background.png");
    public static final Image TITLE                = new Image("images/title.png");
    public static final Image SPRITESHEET          = new Image("images/honored.png");
    public static final Image TILESHEET            = new Image("images/wall.png");
    public static final Image SPLASH               = new Image("images/splash.png");
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
        //set(image.getScaledInstance(getWidth(), getHeight(), BufferedImage.SCALE_SMOOTH));
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

    public void draw(Graphics2D g, Point position, ImageObserver observer) {
        draw(g, position.x * Constants.TILE_SIZE, position.y * Constants.TILE_SIZE, observer);
    }

    public void draw(Graphics2D g, int x, int y, ImageObserver observer) {
        g.drawImage(image, x, y, observer);
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
