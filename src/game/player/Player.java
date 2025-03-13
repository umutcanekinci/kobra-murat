package game.player;
import java.awt.*;
import java.awt.event.KeyEvent;
import game.Board;
import game.Utils;
import game.map.Tilemap;

public class Player {

    public final Snake snake;
    public final Point spawnPoint = new Point(10, 10);
    public static final int DEFAULT_LENGTH = 3;
    public static final Direction DEFAULT_DIRECTION = Direction.RIGHT;
    public double displacement;
    private final int speed = 1; // tiles/second
    private boolean canRotate = true;


    public void onGameStart() {
        reset();
    }

    public int getScore() {
        return (snake.length - DEFAULT_LENGTH) * 100;
    }

    public Point getPos() { return snake.getHead(); }

    public Player(Snake snake) {
        this.snake = snake;
    }

    public void setMap(Tilemap map) {
        spawnPoint.setLocation(map.getSpawnPoint());
    }

    public void reset() {
        snake.setLength(DEFAULT_LENGTH);
        goSpawnPosition();
        setDirection(DEFAULT_DIRECTION);
    }

    public void setDirection(Direction direction) {
        if(direction == null)
            return;

        snake.setDirection(direction);
        canRotate = true;
        snake.rotateHeadTransform();
    }

    private void goSpawnPosition() {
        if(spawnPoint == null)
            return;

        setPosition(spawnPoint);
    }

    private void setPosition(Point position) {
        snake.tailIndex = 0;
        snake.resetParts();
        setHeadPosition(position);
    }

    private void setHeadPosition(Point position) {
        snake.getHead().setLocation(position);
    }

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        Rotate(key);
    }

    private void Rotate(int key) {
        if(!canRotate)
            return;

        Direction newDirection = Utils.keyToDirection(key);

        if(newDirection == null)
            return;

        if(newDirection.isParallel(snake.getDirection()))
            return;

        snake.setDirection(newDirection);
        canRotate = false;
    }

    public void move() {
        displacement += speed * Board.DELTATIME;
        step();
    }

    public void update() {
        snake.rotateHeadTransform();
    }

    public void step() {
        if(displacement < 1)
            return;
        
        canRotate = true;
        displacement = 0;
        snake.step();
    }

}