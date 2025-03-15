package client;

import java.awt.*;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

import common.Direction;

public class SnakePart extends Object {

    public static final Point HIDDEN_POSITION = new Point(-1, -1);
    Direction direction;

    public SnakePart() {
        super(HIDDEN_POSITION);
    }

    public SnakePart(Point point) {
        super(point);
    }

    public SnakePart(Point point, BufferedImage image) {
        this(point);
        setImage(image);
    }

    public void reset() {
        setPosition(HIDDEN_POSITION);
        setImage(null);
        setDirection(null);
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public Direction getDirection() {
        return direction;
    }

    public void drawHead(Graphics2D g, AffineTransformOp headTransform, ImageObserver observer) {
        BufferedImage image = getImage();
        Point position = getPosition();
        
        if(g == null || headTransform == null || image == null || position == null)
            return;
        
        g.drawImage(headTransform.filter(image, null), position.x * common.Level.TILE_SIZE, position.y * common.Level.TILE_SIZE, observer);
    }

    public boolean isHidden(Point point) {
        return point.equals(HIDDEN_POSITION);
    }

}
