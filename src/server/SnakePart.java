package server;

import common.Direction;
import common.Position;

public class SnakePart extends Position {

    public static final Position HIDDEN_POSITION = new Position(-1, -1);
    Direction direction;

    public SnakePart() {
        super(HIDDEN_POSITION);
    }

    public SnakePart(Position point) {
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

    public boolean isHidden(Position point) {
        return point.equals(HIDDEN_POSITION);
    }
}
