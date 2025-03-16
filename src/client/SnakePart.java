package client;

import java.awt.*;
import java.awt.image.BufferedImage;

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

    public boolean isHidden(Point point) {
        return point.equals(HIDDEN_POSITION);
    }

}
