package client;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.io.File;
import java.awt.image.ImageObserver;

import common.Corner;
import common.Position;
import common.Utils;
import common.Direction;
import common.Spritesheet;
import common.SpritesheetBuilder;

public class Player implements Serializable {
    
    private static final File SPRITESHEET_FILE = new File("images/snake.png");
    private static final Direction DEFAULT_DIRECTION = Direction.RIGHT;
    private static Spritesheet spritesheet;
    
    private Direction direction = DEFAULT_DIRECTION;
    private ArrayList<SnakePart> parts = new ArrayList<>();
    private int tailIndex = 0;

    private int length;
    private final int defaultLength;
    private final Position spawnPoint;

    Player(int defaultLength, Position spawnPoint) {
        this.defaultLength = defaultLength;
        this.spawnPoint = spawnPoint;
        reset();
    }

    public int getTailIndex() {
        return tailIndex;
    }

    public void setTailIndex(int tailIndex) {
        this.tailIndex = tailIndex;
    }

    public static void loadSpritesheet() {
        spritesheet = new SpritesheetBuilder().withColumns(3).withRows(3).withSpriteCount(9).withSheet(Utils.loadImage(SPRITESHEET_FILE)).build();
    }

    public int getScore() {
        return Utils.getScore(length, defaultLength);
    }

    public void reset() {
        setLength(defaultLength);
        resetParts();
        setPosition(new Position(spawnPoint));
        resetDirection();
        //OfflinePlayerController.enableRotation();
        updateHead();
        rotateHead();
    }

    public void rotateHead() {
        SnakePart head = getHead();
        
        if (head == null || head.getImage() == null)
            return;

        head.setImage(Utils.getRotatedImage(head.getImage(), direction.getAngle()));
    }
    
    public ArrayList<Position> getParts() {
        ArrayList<Position> points = new ArrayList<>();
        parts.forEach(part -> points.add(part.getPosition()));
        return points;
    }

    public void setPosition(Position position) {
        if(position == null)
            return;
            
        getHead().setPosition(position);
    }

    public SnakePart getHead() {
        if(parts.isEmpty())
            return null;
        
        return parts.get((tailIndex - 1 + length) % length);
    }

    public boolean isHead(SnakePart part) {
        return part.getPosition().equals(getHead().getPosition());
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        if(direction != null && direction.isParallel(this.direction))
            return;
        
        this.direction = direction;
    }

    public Position getNextPosition() {
        return Utils.clampPosition(Utils.moveTowards(getHead().getPosition(), direction));
    }

    public void stepTo(Position position) {
        SnakePart head = getHead();
        
        tailIndex = (tailIndex + 1) % length;

        SnakePart newHead = getHead();
        newHead.setPosition(position);

        head.setImage(spritesheet.getSprite(getFrame(head.getDirection(), direction)));

        updateHead();
        rotateHead();
    }
    
    private int getFrame(Direction dir, Direction newDir){
        if(dir == newDir){
            if(dir == Direction.UP || dir == Direction.DOWN)
                return 3; // same as 5
            else if(dir == Direction.RIGHT || dir == Direction.LEFT)
                return 1; // same as 7
        }
        else { // Corner
            /*if(newDir == Direction.UP && dir == Direction.RIGHT || newDir == Direction.LEFT && dir == Direction.DOWN)
                return 8;
            else if(newDir == Direction.RIGHT && dir == Direction.DOWN || newDir == Direction.UP && dir == Direction.LEFT)
                return 6;
            else if(newDir == Direction.DOWN && dir == Direction.LEFT || newDir == Direction.RIGHT && dir == Direction.UP)
                return 0;
            else if(newDir == Direction.LEFT && dir == Direction.UP || newDir == Direction.DOWN && dir == Direction.RIGHT)
                return 2;
            */
            
        }
        return -1;
    }

    public void updateHead() {
        getHead().setDirection(direction);
        getHead().setImage(spritesheet.getSprite(4));
    }

    public boolean doesCollide(Position point) {
        for(SnakePart part : parts) {
            if(part.doesCollide(point))
                return true;
        }
        return false;
    }

    public boolean isPointOnTail(Position point) {
        return getTail().doesCollide(point);
    }

    private SnakePart getTail() {
        return parts.get(tailIndex);
    }

    public void setLength(int amount) {
        if(amount > Tilemap.getRows() * Tilemap.getCols())
            return;

        if(amount < 1)
            return;
        
        if(amount == length)
            return;

        if(amount > length) {
            grow(amount - length);
            return;
        }

        shrink(length - amount);
    }

    public void grow(int amount) {
        for(int i=0; i<amount; i++) {
            parts.add(tailIndex, new SnakePart()); // locating the new part to the head position, also it is tailIndex so it will become new head after move.
        }
        length += amount;
    }

    public void shrink(int amount) {
        for(int i=0; i<amount; i++) 
            parts.removeLast();
        
        length -= amount;
    }

    public void resetParts() {
        tailIndex = 0;
        parts.forEach(SnakePart::reset);
    }

    public void resetDirection() {
        direction = DEFAULT_DIRECTION;
    }

    public Position getPosition() {
        return getHead().getPosition();
    }

    public void setParts(ArrayList<Position> parts) {
        this.parts.clear();
        parts.forEach(part -> this.parts.add(new SnakePart(part)));
        length = parts.size();
    }

    public void drawCollider(Graphics2D g) {
        parts.forEach(part -> part.drawCollider(g, Color.RED));
        
        SnakePart head = getHead();
        if(head == null)
            return;
        head.drawCollider(g, Color.GREEN);

        SnakePart tail = getTail();
        if(tail == null)
            return;
        tail.drawCollider(g, Color.BLUE);
    }

    public void draw(Graphics2D g, ImageObserver observer) {
        parts.forEach(part -> part.draw(g, observer));
    }

    public String toString() {
        StringBuilder str = new StringBuilder(
                        "Length: "    + length                + "\n" +
                        "Direction: " + getDirection().name() + "\n");
        parts.forEach(part -> str.append(part).append(isHead(part) ? " (Head)" : "").append("\n"));

        return str.toString();
    }
}
