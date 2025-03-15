package server;

import java.awt.*;

public class Player {

    public final Snake snake;
    public final Point spawnPoint = new Point();
    public static final int DEFAULT_LENGTH = 6;

    public Point getPos() { return snake.getHead(); }

    public Player(Snake snake) {
        this.snake = snake;
        reset();
    }

    public void setMap() {
        spawnPoint.setLocation(Tilemap.getSpawnPoint());
    }

    public void reset() {
        snake.setLength(DEFAULT_LENGTH);
        goSpawnPosition();
        snake.resetDirection();
    }

    private void goSpawnPosition() {
        if(spawnPoint == null)
            return;

        setPosition(spawnPoint);
    }

    private void setPosition(Point position) {
        snake.tailIndex = 0;
        snake.resetParts();
        snake.getHead().setLocation(position);
    }

}