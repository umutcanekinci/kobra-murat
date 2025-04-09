package server;

import common.Direction;
import common.Position;
import common.Constants;

public class SnakePart extends Position {

    Direction direction;

    public SnakePart() {
        super(Constants.HIDDEN_PART_POSITION);
    }

    public SnakePart(Position point) {
        super(point);
    }

    public void reset() {
        setLocation(Constants.HIDDEN_PART_POSITION);
        setDirection(null);
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public Direction getDirection() {
        return direction;
    }

    public boolean isHidden(Position point) {
        return point.equals(Constants.HIDDEN_PART_POSITION);
    }
}
