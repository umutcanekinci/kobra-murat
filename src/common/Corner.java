package common;

public enum Corner {

    TOPLEFT(Direction.UP, Direction.RIGHT),
    TOPRIGHT(Direction.UP, Direction.LEFT),
    BOTTOMLEFT(Direction.DOWN, Direction.RIGHT),
    BOTTOMRIGHT(Direction.DOWN, Direction.LEFT);

    private final Direction dir1;
    private final Direction dir2;

    Corner(Direction dir1, Direction dir2) {
        this.dir1 = dir1;
        this.dir2 = dir2;
    }
}
