package common;

public enum Direction {

    LEFT(-1, 0),
    UP(0, -1),
    RIGHT(1, 0),
    DOWN(0, 1);

    private final int x;
    private final int y;

    Direction(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isParallel(Direction other) {
        return this == other || isOppositeOf(other);
    }

    public boolean isOppositeOf(Direction other) {
        return other.x == -x && other.y == -y;
    }

    public int getAngle(Direction other) {
        if(other == Direction.UP && this == Direction.RIGHT || other == Direction.LEFT && this == Direction.DOWN)
            return 0;
        else if(other == Direction.RIGHT && this == Direction.DOWN || other == Direction.UP && this == Direction.LEFT)
            return 90;
        else if(other == Direction.DOWN && this == Direction.LEFT || other == Direction.RIGHT && this == Direction.UP)
            return 180;
        else if(other == Direction.LEFT && this == Direction.UP || other == Direction.DOWN && this == Direction.RIGHT)
            return -90;
        return getAngle();
    }

    public int getAngle() {
        return (int) Math.toDegrees(Math.atan2(y, x));
    }
}
