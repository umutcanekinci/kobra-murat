package game.player;
import java.awt.*;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

import game.Board;

public class SnakePart extends Point {

    public static final Point HIDDEN_POSITION = new Point(-1, -1);
    private BufferedImage image;
    Direction direction;

    public SnakePart(Point point, BufferedImage image) {
        this(point);
        setImage(image);
    }

    public SnakePart(Point point) {
        super(point);
    }

    public SnakePart() {
        super(HIDDEN_POSITION);
    }

    public void reset() {
        setLocation(HIDDEN_POSITION);
        setImage(null);
        setDirection(null);
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public Direction getDirection() {
        return direction;
    }

    public void draw(Graphics2D g, ImageObserver observer) {
        if(isHidden(this) || image == null)
            return;
        
        g.drawImage(image, x * Board.TILE_SIZE, y * Board.TILE_SIZE, observer);
    }

    public void drawHead(Graphics2D g, AffineTransformOp headTransform, ImageObserver observer) {
        if(image == null || headTransform == null)
            return;
        
        g.drawImage(headTransform.filter(image, null), x * Board.TILE_SIZE, y * Board.TILE_SIZE, observer);
    }

    public void drawCollider(Graphics2D g, Color color) {
        g.setColor(color);
        g.drawRect(x * Board.TILE_SIZE, y * Board.TILE_SIZE, Board.TILE_SIZE, Board.TILE_SIZE);
    }

    public boolean isHidden(Point point) {
        return point.equals(HIDDEN_POSITION);
    }

    @Override
    public String toString() {
        return "[" + x + ", " + y + "] " + direction;
    }
}
