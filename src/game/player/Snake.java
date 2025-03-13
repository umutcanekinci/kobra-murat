package game.player;
import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.io.File;
import java.awt.image.ImageObserver;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import game.Board;
import game.Utils;

public class Snake implements Serializable {

    private AffineTransformOp headTransform;
    private static final Direction DEFAULT_DIRECTION = Direction.RIGHT;
    private static Direction currentDirection = DEFAULT_DIRECTION;
    public ArrayList<SnakePart> parts = new ArrayList<>();
    
    private static final File SPRITESHEET_FILE = new File("images/snake.png");
    private static Spritesheet spritesheet;
    public int tailIndex = 0;
    public int length;

    Snake() {
        grow(1);
        spritesheet = new SpritesheetBuilder().withColumns(3).withRows(3).withSpriteCount(9).withSheet(Utils.loadImage(SPRITESHEET_FILE)).build();
    }

    public SnakePart getHead() {
        return parts.get((tailIndex + 1 + length) % length);
    }

    public boolean isHead(Point point) {
        return point.equals(getHead());
    }

    public Direction getDirection() {
        return currentDirection;
        
        //SnakePart head = getHead();
        
        //if (head == null)
        //    return null;

        //return head.getDirection();
    }

    public void setDirection(Direction direction) {
        //Direction currentDirection = getDirection();
        
        if(currentDirection != null && currentDirection.isParallel(direction))
            return;
        
        //SnakePart head = getHead();

        //if (head == null)
        //    return;
    
        //head.setDirection(direction);

        currentDirection = direction;
    }

    public void step() {
        SnakePart head = getHead();
        
        Point newPosition = Utils.clampPosition(Utils.moveTowards(head, getDirection()));

        if(doesCollide(newPosition) || Board.map.isCollide(newPosition))
        {
            Board.onHit();
            return;
        }
            
        tailIndex = (tailIndex + 1) % length;

        SnakePart newHead = getHead();
        newHead.setLocation(newPosition);

        
        Direction dir = head.getDirection();
        Direction newDir = newHead.getDirection();
    
        if(dir == newDir)
            if(dir == Direction.UP || dir == Direction.DOWN)
                head.setImage(spritesheet.getSprite(3));
            else
                head.setImage(spritesheet.getSprite(1));
        else {
            int frame = 5;
            if(newDir == Direction.UP && dir == Direction.RIGHT || newDir == Direction.LEFT && dir == Direction.DOWN)
                frame = 0;
            else if(newDir == Direction.RIGHT && dir == Direction.DOWN || newDir == Direction.UP && dir == Direction.LEFT)
                frame = 2;
            else if(newDir == Direction.DOWN && dir == Direction.LEFT || newDir == Direction.RIGHT && dir == Direction.UP)
                frame = 8;
            else if(newDir == Direction.LEFT && dir == Direction.UP || newDir == Direction.DOWN && dir == Direction.RIGHT)
                frame = 6;
            head.setImage(spritesheet.getSprite(frame));
        }
        
        newHead.setImage(spritesheet.getSprite(4));
        newHead.setDirection(currentDirection);

        Board.onStep();
        rotateHeadTransform();
    }

    public boolean doesCollide(Point point) {
        return parts.contains(point) && !isPointOnTail(point);
    }

    public boolean isPointOnTail(Point point) {
        return point.equals(parts.get(tailIndex));
    }

    public void setLength(int amount) {
        if(amount > Board.COLUMNS * Board.ROWS)
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
            parts.add(tailIndex, new SnakePart()); // locating the new part to the head position, also it is snake.tailIndex so it will become new head after move.
        }
        length += amount;
    }

    public void shrink(int amount) {
        for(int i=0; i<amount; i++) {
            parts.removeLast();
        }
        length -= amount;
    }

    public void resetParts() {
        parts.forEach(SnakePart::reset);
    }

    public Point getPosition() {
        return parts.get((tailIndex + 1) % length);
    }

    public void setParts(ArrayList<Point> parts) {
        this.parts.clear();
        parts.forEach(part -> this.parts.add(new SnakePart(part)));
    }

    public void drawCollider(Graphics2D g) {
        parts.forEach(part -> part.drawCollider(g, Color.RED));
        SnakePart head = getHead();
        if(head == null)
            return;
        head.drawCollider(g, Color.GREEN);
    }

    public void draw(Graphics2D g, ImageObserver observer) {
        for(SnakePart part : parts) {
            if(isHead(part)) {
                part.drawHead(g, headTransform, observer);
                continue;
            }

            part.draw(g, observer);
        }
    }

    void rotateHeadTransform() {
        if (getDirection() == null)
            return;

        SnakePart head = getHead();
        
        if (head == null)
            return;

        BufferedImage headImage = head.getImage();

        if (headImage == null)
            return;

        headTransform = Utils.getRotatedTransform(headImage, getDirection().getAngle());
    }

}
