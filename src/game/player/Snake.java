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
import game.map.Tilemap;

public class Snake implements Serializable {

    private AffineTransformOp headTransform;
    private static final Direction DEFAULT_DIRECTION = Direction.RIGHT;
    private Direction direction = DEFAULT_DIRECTION;
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

        if((doesCollide(position) && !isPointOnTail(position)) || Tilemap.doesCollide(position))
        {
            Board.onHit();
            return;
        }
            
        tailIndex = (tailIndex + 1) % length;

        SnakePart newHead = getHead();
        newHead.setPosition(position);

        
        head.setImage(spritesheet.getSprite(getFrame(head.getDirection(), direction)));
        updateHead();

        Board.onStep();
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
        for(int i=0; i<amount; i++) 
            parts.removeLast();
        
        length -= amount;
    }

    public void resetParts() {
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
            if(isHead(part)) {
                part.drawHead(g, headTransform, observer);
                continue;
            }

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
    }

    public String toString() {
        String info =
        "Length: " + length + "\n" +
        "Direction: " + getDirection().name() + "\n";
        
        for(SnakePart part : parts) {
            info += part + (isHead(part) ? " (Head)" : "") + "\n";
        }

        return info;
    }

}
