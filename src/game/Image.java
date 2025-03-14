package game;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;

public class Image {

    private BufferedImage image;

    public Image() {}

    public Image(File imageFile) {
        loadImage(imageFile);
    }

    private void loadImage(File imageFile) {
        image = Utils.loadImage(imageFile);
    }

    public BufferedImage get() {
        return image;
    }

    public void set(BufferedImage image) {
        this.image = image;
    }

    public void draw(Graphics2D g, Point position, ImageObserver observer) {
        // with the Point class, note that position.getX() returns a double, but 
        // position.x reliably returns an int. https://stackoverflow.com/a/30220114/4655368
        // this is also where we translate board grid position into a canvas pixel
        // position by multiplying by the tile size.

        g.drawImage(
                image,
                position.x * Board.TILE_SIZE,
                position.y * Board.TILE_SIZE,
                observer
        );
    }

    public void drawBorder(Graphics2D g, Color color, Point position) {
        g.setColor(color);
        this.drawBorder(g, position);
    }

    public void drawBorder(Graphics2D g, Point position) {
        g.drawRect(
                position.x * Board.TILE_SIZE,
                position.y * Board.TILE_SIZE,
                Board.TILE_SIZE,
                Board.TILE_SIZE
        );
    }

}
