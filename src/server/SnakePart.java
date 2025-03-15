package server;

import java.awt.*;

import common.Direction;

public class SnakePart extends Point {

    public static final Point HIDDEN_POSITION = new Point(-1, -1);
    Direction direction;

    public SnakePart() {
        super(HIDDEN_POSITION);
    }

    public SnakePart(Point point) {
        super(point);
    }

    public void reset() {
        setLocation(HIDDEN_POSITION);
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

    public String toString() {
        return "[" + x + ", " + y + "]";
    }

}
