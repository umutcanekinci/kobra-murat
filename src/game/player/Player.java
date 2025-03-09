package game.player;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;

import game.Board;
import game.Utils;
import game.map.Tilemap;

public class Player {

    //region ---------------------------------------- ATTRIBUTES -----------------------------------------

    public Snake snake;
    private Tilemap map;
    public Point spawnPoint = new Point(0, 0);
    private Color bodyColor = new Color(0, 0, 0);

    // Default
    public static final int DEFAULT_LENGTH = 3;
    public static final Direction DEFAULT_DIRECTION = Direction.RIGHT;

    // Image
    private static final File HEAD_IMAGE = new File("images/head.png");
    private BufferedImage headImage;
    private AffineTransformOp headTransform;

    // Movement
    private double displacement;
    private final int speed = 3; // tiles/second
    public Point pos = new Point(0, 0);
    private boolean canRotate = true;

    private Board board;

    //endregion

    public boolean doesCollide(Point position) {
        return snake.doesCollide(position);
    }

    //region ---------------------------------------- EVENT METHODS ---------------------------------------

    public void onGameStart() {
        reset();
    }

    //endregion

    //region ---------------------------------------- GET METHODS ---------------------------------------

    public String getScore() {
        return String.valueOf((snake.length - DEFAULT_LENGTH) * 100);
    }

    public Point getPos() { return pos; }

    //endregion

    //region ---------------------------------------- INIT METHODS ---------------------------------------

    public Player(Board board, Snake snake) {
        this.board = board;
        this.snake = snake;
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
        snake.setLength(DEFAULT_LENGTH);
        updateColor();
        goSpawnPosition();
    }

    public void setDirection(Direction direction) {
        if(direction == null)
            return;
        snake.direction = direction;
        canRotate = true;
        rotateHeadTransform();
    }

    public void grow(int amount) {
        snake.grow(amount);
        updateColor();
    }

    private void updateColor() {
        bodyColor = new Color(0, (255 - snake.length*2)%255, 0);
    }

    private void goSpawnPosition() {
        if(spawnPoint == null)
            return;

        setPosition(spawnPoint);
    }

    private void setPosition(Point position) {
        pos.setLocation(position);
        snake.tailIndex = 0;
        snake.resetParts();
        setHeadPosition(position);
    }

    private void setHeadPosition(Point position) {
        snake.parts.get(snake.tailIndex).setLocation(position);
        snake.tailIndex++;
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

        if(newDirection.isParallel(snake.direction))
            return;

        snake.direction = newDirection;
        canRotate = false;
    }

    //endregion

    //region ---------------------------------------- UPDATE METHODS -------------------------------------

    public void move() {
        displacement += speed * Board.DELTATIME;

        if(displacement < 1)
            return;

        step();
        clampPosition();
        
        if(snake.doesCollide(pos) || map.isCollide(pos)) {
            board.onHit();
        }

        displacement = 0;
        snake.stepTo(pos);
        board.onStep();
        rotateHeadTransform();
    }

    public void update() {
        rotateHeadTransform();
    }

    public void step() {
        pos.move(pos.x + snake.direction.getX(), pos.y + snake.direction.getY());
        canRotate = true;
    }

    private void clampPosition() {
        pos.x %= Board.COLUMNS;
        pos.x += pos.x < 0 ? Board.COLUMNS : 0;
        pos.y %= Board.ROWS;
        pos.y += pos.y < 0 ? Board.ROWS : 0;
    }

    //endregion

    //region ---------------------------------------- DRAW METHODS ---------------------------------------

    public void draw(Graphics2D g, ImageObserver observer) {

        for(Point part : snake.parts) {

            if(snake.isHidden(part))
                continue;

            if(part.equals(pos)) {
                drawHead(g, observer);
                continue;
            }

            drawBody(g, part, bodyColor);
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
        if (snake.direction == null)
            return;

        headTransform = Utils.getRotatedTransform(headImage, snake.direction.getAngle());
    }

    //endregion

}