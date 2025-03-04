package game;

import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.Point;
import java.io.File;
import java.awt.Graphics2D;

public class Apple {

    // Image
    private static final File APPLE_IMAGE = new File("images/apple.png");
    private BufferedImage image;

    // Position
    private final Point pos;

    public Apple(Point pos) {
        loadImage();
        this.pos = pos;
    }

    private void loadImage() {
        image = Utils.loadImage(APPLE_IMAGE);
    }

    public void draw(Graphics2D g, ImageObserver observer) {
        // with the Point class, note that pos.getX() returns a double, but 
        // pos.x reliably returns an int. https://stackoverflow.com/a/30220114/4655368
        // this is also where we translate board grid position into a canvas pixel
        // position by multiplying by the tile size.

        g.drawImage(
                image,
                pos.x * Board.TILE_SIZE,
                pos.y * Board.TILE_SIZE,
                observer
        );
    }

    public boolean isCollide(Point position) {
        return pos.equals(position);
    }

}