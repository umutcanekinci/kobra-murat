package client;

import common.Position;
import common.Direction;
import common.Object;
import common.Constants;

public class SnakePart extends Object {

    private Direction direction;

    public SnakePart() {
        super(Constants.HIDDEN_POSITION);
    }

    public SnakePart(Position point) {
        super(point);
    }

    public void reset() {
        setPosition(Constants.HIDDEN_POSITION);
        setImage(null);
        setDirection(null);
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public Direction getDirection() {
        return direction;
    }

    public boolean isHidden(Position point) {
        return point.equals(Constants.HIDDEN_POSITION);
    }

}
