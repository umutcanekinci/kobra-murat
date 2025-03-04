package game;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

interface PlayerListener {}

public class Player implements GameListener {

    //region ---------------------------------------- ATTRIBUTES -----------------------------------------

    public Point spawnPoint = new Point(0, 0);
    private Color bodyColor = new Color(0, 0, 0);

    // Default
    public static final Point HIDDEN_PART_POSITION = new Point(-1, -1);
    public static final int DEFAULT_LENGTH = 3;
    public static final Direction DEFAULT_DIRECTION = Direction.RIGHT;

    // Events
    private final List<PlayerListener> listeners = new ArrayList<>();

    // Image
    private static final File HEAD_IMAGE = new File("images/head.png");
    private BufferedImage headImage;
    private AffineTransformOp headTransform;

    // Movement
    private final ArrayList<Point> snakeParts = new ArrayList<>();
    private double displacement;
    private final int speed = 5; // tiles/second
    private final Point pos = new Point(0, 0);
    private int length;
    private Direction direction;
    private int tailIndex = 0;
    private boolean canRotate = true;

    private Tilemap map;

    //endregion

    public boolean isCollide(Point position) {
        return snakeParts.contains(position);
    }

    public String[] getDebugInfo() {
        return new String[] {
                "PLAYER DEBUG INFO",
                "Length: " + length,
                "Direction: " + direction.name(),
                "Position: " + pos.x + ", " + pos.y,
                String.format("Displacement: %.2f", displacement),
                "Can Rotate: " + canRotate
        };
    }

    //region ---------------------------------------- EVENT METHODS ---------------------------------------


    public void addListener(PlayerListener listener) {
        listeners.add(listener);
    }

    private void invokeEvents(String eventName) {
        for(PlayerListener listener : listeners) {
            try {
                listener.getClass().getMethod(eventName).invoke(listener);
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
    }

    public void onGameStart() {
        reset();
    }

    //endregion

    //region ---------------------------------------- GET METHODS ---------------------------------------

    public String getScore() {
        return String.valueOf((length - DEFAULT_LENGTH) * 100);
    }

    public Point getPos() { return pos; }

    //endregion

    //region ---------------------------------------- INIT METHODS ---------------------------------------

    public Player() {
        loadImages();
    }

    public void setMap(Tilemap map) {
        this.map = map;
        spawnPoint.setLocation(map.getSpawnPoint());
    }

    private void loadImages() {
        headImage = Utils.loadImage(HEAD_IMAGE);
        rotateHeadTransform();
    }

    public void reset() {
        setDirection(DEFAULT_DIRECTION);
        setLength(DEFAULT_LENGTH);
        updateColor();
        goSpawnPosition();
    }

    public void setDirection(Direction direction) {
        if(direction == null)
            return;
        this.direction = direction;
        canRotate = true;
        rotateHeadTransform();
    }

    public void setLength(int length) {
        if(length > Board.COLUMNS * Board.ROWS)
            return;

        if(length < 1)
            return;

        if(length == this.length)
            return;

        if(length > this.length) {
            grow(length - this.length);
            return;
        }

        shrink(this.length - length);
    }

    public void grow(int amount) {
        for(int i=0; i<amount; i++) {
            snakeParts.add(tailIndex, new Point(-1, -1)); // locating the new part to the head position, also it is tailIndex so it will become new head after move.
        }
        length += amount;
        updateColor();
    }

    private void updateColor() {
        bodyColor = new Color(0, (255 - length*2)%255, 0);
    }

    public void shrink(int amount) {
        for(int i=0; i<amount; i++) {
            snakeParts.removeLast();
        }
        length -= amount;
    }

    private void goSpawnPosition() {
        if(spawnPoint == null)
            return;

        setPosition(spawnPoint);
    }

    private void setPosition(Point position) {
        pos.setLocation(position);
        tailIndex = 0;
        resetSnakeParts();
        setHeadPosition(position);
    }

    private void resetSnakeParts() {
        for(Point snakePart : snakeParts) {
            snakePart.setLocation(HIDDEN_PART_POSITION);
        }
    }

    private void setHeadPosition(Point position) {
        snakeParts.get(tailIndex).setLocation(position);
        tailIndex++;
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

        Direction newDirection = Utils.keyToDirection(key);

        if(newDirection == null)
            return;

        if(newDirection.isParallel(direction))
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

        if(doesHit(pos) || map.isCollide(pos)) {
            invokeEvents("onHit");
        }

        updateSnakeParts();
        displacement = 0;
        rotateHeadTransform();

    }

    private boolean doesHit(Point point) {
        for(Point snakePart : snakeParts) {
            if(snakePart.equals(point) && !isPointOnTail(snakePart)) {
                return true;
            }
        }
        return false;
    }

    private boolean isPointOnTail(Point point) {
        return point.equals(snakeParts.get(tailIndex));
    }

    private void step() {
        pos.move(pos.x + direction.getX(), pos.y + direction.getY());
        canRotate = true;
        invokeEvents("onStep");
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

    public void draw(Graphics2D g, ImageObserver observer) {

        for(Point snakePart : snakeParts) {

            if(snakePart.equals(HIDDEN_PART_POSITION)) {
                continue;
            }

            if(snakePart.equals(pos)) {
                drawHead(g, observer);
                continue;
            }

            drawBody(g, snakePart, bodyColor);
        }
    }

    private void drawHead(Graphics2D g, ImageObserver observer) {
        drawSnakePart(g, headTransform.filter(headImage, null), pos, observer);
    }

    private void drawBody(Graphics2D g, Point snakePart, Color color) {
        drawBodyRect(g, snakePart, color);
    }

    private void drawBodyRect(Graphics2D g, Point snakePart, Color color) {
        g.setColor(color);
        g.fillRect(
                snakePart.x * Board.TILE_SIZE, snakePart.y * Board.TILE_SIZE,
                Board.TILE_SIZE, Board.TILE_SIZE
        );
    }

    private void drawSnakePart(Graphics2D g, BufferedImage image, Point snakePart, ImageObserver observer) {
        g.drawImage(image, snakePart.x * Board.TILE_SIZE, snakePart.y * Board.TILE_SIZE, observer);
    }

    private void rotateHeadTransform() {
        if (direction == null)
            return;

        headTransform = Utils.getRotatedTransform(headImage, direction.getAngle());
    }

    //endregion

}