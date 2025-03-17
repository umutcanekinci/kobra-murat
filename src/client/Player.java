package client;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.io.File;
import java.awt.image.ImageObserver;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import common.Utils;
import common.Direction;
import common.Level;

public class Player implements Serializable {

    public ArrayList<SnakePart> parts = new ArrayList<>();
    public int tailIndex = 0;
    public int length;

    private static final File SPRITESHEET_FILE = new File("images/snake.png");
    private static Spritesheet spritesheet;
    private static final Direction DEFAULT_DIRECTION = Direction.RIGHT;
    private AffineTransformOp headTransform;
    private Direction direction = DEFAULT_DIRECTION;
    private static final int DEFAULT_LENGTH = 6;
    private final Point spawnPoint = new Point();

    public String toString() {
        String info =   "Length: " + length + "\n" +
                        "Direction: " + getDirection().name() + "\n";
        
        for(SnakePart part : parts)
            info += part + (isHead(part) ? " (Head)" : "") + "\n";

        return info;
    }

    public static void loadSpritesheet() {
        spritesheet = new SpritesheetBuilder().withColumns(3).withRows(3).withSpriteCount(9).withSheet(Utils.loadImage(SPRITESHEET_FILE)).build();
    }

    public int getScore() {
        return Utils.getScore(length, DEFAULT_LENGTH);
    }

    public void setSpawnPoint() {
        spawnPoint.setLocation(Tilemap.getSpawnPoint());
    }

    public void reset() {
        setLength(DEFAULT_LENGTH);
        resetParts();
        setPosition(spawnPoint);
        resetDirection();
        OfflinePlayerController.enableRotation();
        updateHead();
        rotateHeadTransform();
    }
    
    public ArrayList<Point> getParts() {
        ArrayList<Point> points = new ArrayList<>();
        parts.forEach(part -> points.add(part.getPosition()));
        return points;
    }

    public void setPosition(Point position) {
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

    public void step() {
        SnakePart head = getHead();
        
        Point position = Utils.clampPosition(Utils.moveTowards(head.getPosition(), direction));

        Boolean doesHitSelf = doesCollide(position) && !isPointOnTail(position);
        if(doesHitSelf || Tilemap.doesCollide(position))
        {
            //Board.onHit();
            return;
        }
            
        tailIndex = (tailIndex + 1) % length;

        SnakePart newHead = getHead();
        newHead.setPosition(position);

        head.setImage(spritesheet.getSprite(getFrame(head.getDirection(), direction)));

        updateHead();
        rotateHeadTransform();
    }
    
    private int getFrame(Direction dir, Direction newDir){
        if(dir == newDir){
            if(dir == Direction.UP || dir == Direction.DOWN)
                return 3; // same as 5
            else if(dir == Direction.RIGHT || dir == Direction.LEFT)
                return 1; // same as 7
        }
        else {
            if(newDir == Direction.UP && dir == Direction.RIGHT || newDir == Direction.LEFT && dir == Direction.DOWN)
                return 8;
            else if(newDir == Direction.RIGHT && dir == Direction.DOWN || newDir == Direction.UP && dir == Direction.LEFT)
                return 6;
            else if(newDir == Direction.DOWN && dir == Direction.LEFT || newDir == Direction.RIGHT && dir == Direction.UP)
                return 0;
            else if(newDir == Direction.LEFT && dir == Direction.UP || newDir == Direction.DOWN && dir == Direction.RIGHT)
                return 2;
        }
        return -1;
    }

    public void updateHead() {
        getHead().setDirection(direction);
        getHead().setImage(spritesheet.getSprite(4));
    }

    public boolean doesCollide(Point point) {
        for(SnakePart part : parts) {
            if(part.doesCollide(point))
                return true;
        }
        return false;
    }

    public boolean isPointOnTail(Point point) {
        return getTail().doesCollide(point);
    }

    private SnakePart getTail() {
        return parts.get(tailIndex);
    }

    public void setLength(int amount) {
        if(amount > Level.COLUMNS * Level.ROWS)
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

    public Point getPosition() {
        return getHead().getPosition();
    }

    public void setParts(ArrayList<Point> parts) {
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
        for(SnakePart part : parts) {
            part.draw(g, observer);
        }
    }

    public void rotateHeadTransform() {
        SnakePart head = getHead();
        
        if (head == null)
            return;

        BufferedImage headImage = head.getImage();

        if (headImage == null)
            return;

        headTransform = Utils.getRotatedTransform(headImage, direction.getAngle());
        head.setImage(headTransform.filter(head.getImage(), null));
    }
}
