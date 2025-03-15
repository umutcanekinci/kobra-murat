package game.player;
import java.awt.*;
import java.awt.event.KeyEvent;
import game.Board;
import game.Utils;
import game.map.Tilemap;

public class Player {

    public final Snake snake;
    public final Point spawnPoint = new Point();
    public static final int DEFAULT_LENGTH = 6;
    public double displacement;
    private final int speed = 3; // tiles/second
    public boolean canRotate = true;

    public int getScore() {
        return (snake.length - DEFAULT_LENGTH) * 100;
    }

    public Point getPos() { return snake.getHead().getPosition(); }

    public Player(Snake snake) {
        this.snake = snake;
    }

    public void setMap() {
        spawnPoint.setLocation(Tilemap.getSpawnPoint());
    }

    public void reset() {
        snake.setLength(DEFAULT_LENGTH);
        goSpawnPosition();
        resetDirection();
    }

    public void resetDirection() {
        snake.resetDirection();
        canRotate = true;
        snake.updateHead();
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
        snake.getHead().setPosition(position);
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

    public void step() {
        if(displacement < 1)
            return;
        
        Board.onStep();
    }

}