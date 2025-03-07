package game.player;

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

    public int getAngle() {
        return (int) Math.toDegrees(Math.atan2(y, x));
    }
}
