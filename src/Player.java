import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

interface GameListener {
    void onGameOver();
}

public class Player {

    //region ---------------------------------------- ATTRIBUTES -----------------------------------------

    // Default
    public static final Point SPAWN_POSITION = new Point(0, 0);
    public static final int DEFAULT_LENGTH = 3;
    public static final Direction DEFAULT_DIRECTION = Direction.RIGHT;

    // Events
    private final List<GameListener> listeners = new ArrayList<>();

    // Image
    private static final File SNAKE_IMAGE = new File("images/snake.png");
    private BufferedImage image;

    // Movement
    private final ArrayList<Point> snakeParts = new ArrayList<>();;
    private double displacement;
    private final int speed = 5; // move speed per second in tiles
    private final Point pos = new Point(0, 0);
    private int length;
    private Direction direction;
    private int tailIndex = 0;
    private boolean canRotate = true;

    //endregion

    //region ---------------------------------------- GET METHODS ---------------------------------------

    public String getScore() {
        return String.valueOf((length - DEFAULT_LENGTH) * 100);
    }

    public Point getPos() { return pos; }

    public ArrayList<Point> getSnakeParts() { return snakeParts; }

    public int getLength() { return length; }

    //endregion

    //region ---------------------------------------- INIT METHODS ---------------------------------------

    public void addListener(GameListener listener) {
        listeners.add(listener);
    }

    public Player() {
        loadImage();
        reset();
    }

    private void loadImage() {
        image = Utils.LoadImage(SNAKE_IMAGE);
    }

    public void reset() {
        pos.setLocation(SPAWN_POSITION);
        direction = DEFAULT_DIRECTION;
        tailIndex = 0;
        clearSnakeParts();
        grow(DEFAULT_LENGTH);
    }

    private void clearSnakeParts() {
        snakeParts.clear();
        length = 0;
    }

    public void grow(int amount) {
        for(int i=0; i<amount; i++) {
            snakeParts.add(tailIndex, new Point(pos)); // locating the new part to the head position, also it is tailIndex so it will become new head after move.
        }
        length += amount;
    }

    //endregion

    //region ---------------------------------------- INPUT METHODS --------------------------------------

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        Rotate(key);
    }

    private void Rotate(int key) {
        if(!canRotate)
            return;

        Direction newDirection = Utils.KeyToDirection(key);

        if(newDirection == null)
            return;

        if(newDirection.isOppositeOf(direction))
            return;

        direction = newDirection;
        canRotate = false;
    }

    //endregion

    //region ---------------------------------------- UPDATE METHODS -------------------------------------

    public void update() {
        move();
    }

    private void move() {
        displacement += speed * Board.DELTATIME;

        if(displacement < 1) {
            return;
        }

        step();
        clampPosition();

        if(doesHit()) {
            onHit();
        }
        updateSnakeParts();
        displacement = 0;
    }

    private boolean doesHit() {
        for(Point snakePart : snakeParts) {
            if(snakePart.equals(pos)) {
                return true;
            }
        }
        return false;
    }

    private void onHit() {
        for(GameListener listener : listeners) {
            listener.onGameOver();
        }
    }

    private void step() {
        pos.move(pos.x + direction.getX(), pos.y + direction.getY());
        canRotate = true;
    }

    private void clampPosition() {
        pos.x %= Board.COLUMNS;
        pos.x += pos.x < 0 ? Board.COLUMNS : 0;
        pos.y %= Board.ROWS;
        pos.y += pos.y < 0 ? Board.ROWS : 0;
    }

    private void updateSnakeParts() {

        /*

        I have got two snakeParts position algorithm.
        1. Shift right and add to first -> Move all values in list to the right and update first value to head position.
        2. Circular Buffer -> Insert the head position to the oldest value with using i%length.
        3. LinkedList -> Best way(gpt)

        Algorithm 1 - Worst way
        for(int i=snakeParts.size()-1; i>0; i--) {
            snakeParts.get(i).setLocation(snakeParts.get(i-1));
        }
        snakeParts.getFirst().setLocation(pos);

        */

        snakeParts.get(tailIndex).setLocation(pos);
        tailIndex = (tailIndex + 1) % length;

    }

    //endregion

    //region ---------------------------------------- DRAW METHODS ---------------------------------------

    public void draw(Graphics g, ImageObserver observer) {
        // with the Point class, note that pos.getX() returns a double, but
        // pos.x reliably returns an int. https://stackoverflow.com/a/30220114/4655368

        for(Point snakePart : snakeParts) {
            g.drawImage(image, snakePart.x * Board.TILE_SIZE, snakePart.y * Board.TILE_SIZE, observer);
        }
    }

    //endregion

}