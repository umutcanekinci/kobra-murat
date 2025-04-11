package common;

// Base class for all parts of snake. Server usng that but client uses a child with gui.
public class SnakePart extends Position {

    private Direction direction;

    public SnakePart() {
        super(Constants.HIDDEN_PART_POSITION);
    }

    public SnakePart(Position point) {
        super(point);
    }

    public void reset() {
        setLocation(Constants.HIDDEN_PART_POSITION);
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
