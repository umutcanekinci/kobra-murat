package client;

import common.Position;
import common.Direction;
import common.Object;
import common.Constants;

public class SnakePart extends Object {

    private Direction direction;

    public SnakePart() {
        super(Constants.HIDDEN_PART_POSITION);
    }

    public SnakePart(Position point) {
        super(point);
    }

    public void reset() {
        setPosition(Constants.HIDDEN_PART_POSITION);
        setImage(null);
        setDirection(Constants.DEFAULT_DIRECTION);
    }

    public void setDirection(Direction direction) {
        if (direction == null)
            throw new IllegalArgumentException("Direction cannot be null");

        this.direction = direction;
    }

    public Direction getDirection() {
        return direction;
    }

    public boolean isHidden(Position point) {
        if (point == null)
            throw new IllegalArgumentException("Position cannot be null");

        return point.equals(Constants.HIDDEN_PART_POSITION);
    }

}
