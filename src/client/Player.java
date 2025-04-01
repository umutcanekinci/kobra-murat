package client;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

import common.Constants;
import common.Position;
import common.Utils;
import common.Direction;

public class Player implements Serializable {
    
    private Direction direction = Constants.DEFAULT_DIRECTION;
    private ArrayList<SnakePart> parts = new ArrayList<>();
    private int tailIndex = 0;

    private int speed;
    private int length;

    public int getTailIndex() {
        return tailIndex;
    }

    public int getSpeed() {
        return speed;
    }

    public void setTailIndex(int tailIndex) {
        this.tailIndex = tailIndex;
    }

    public int getScore() {
        return Utils.getScore(length, 0);
    }

    public void spawn(Position spawnPoint, int length) {
        if(spawnPoint == null)
            throw new IllegalArgumentException("Spawn point cannot be null");

        setLength(length);
        resetParts();
        setPosition(new Position(spawnPoint));
        resetDirection();
        //OfflinePlayerController.enableRotation();
        updateHead();
        updateSpeed();
    }

    private void updateSpeed() {
        speed = Utils.calculateSpeed(length);
    }
    
    public ArrayList<Position> getParts() {
        ArrayList<Position> points = new ArrayList<>();
        parts.forEach(part -> points.add(part.getPosition()));
        return points;
    }

    public void setPosition(Position position) {
        if(position == null)
            throw new IllegalArgumentException("Position cannot be null");
            
        getHead().setPosition(position);
    }

    public SnakePart getHead() {
        if(parts.isEmpty())
            return null;
        
        return parts.get((tailIndex - 1 + length) % length);
    }

    public boolean isHead(SnakePart part) {
        if(part == null)
            throw new IllegalArgumentException("Part cannot be null");

        return part.getPosition().equals(getHead().getPosition());
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        if(direction == null)
            throw new IllegalArgumentException("Direction cannot be null");

        if(direction.isParallel(this.direction))
            return;
        
        this.direction = direction;
    }

    public Position getNextPosition() {
        return Utils.clampPosition(Utils.moveTowards(getHead().getPosition(), direction), Tilemap.getCols(), Tilemap.getRows());
    }

    public void stepTo(Position position) {
        if(position == null)
            throw new IllegalArgumentException("Position cannot be null");

        SnakePart head = getHead();
        head.setImage(getFrame(head.getDirection(), direction));

        tailIndex = (tailIndex + 1) % length;

        SnakePart newHead = getHead();
        newHead.setPosition(position);

        updateHead();
    }
    
    private BufferedImage getFrame(Direction dir, Direction newDir) {
        if(dir == null || newDir == null)
            throw new IllegalArgumentException("Direction cannot be null");

        int frame = dir == newDir ? 1 : 0;
        System.out.println("Frame: " + frame);
        return Utils.getRotatedImage(Constants.SPRITESHEET.getSprite(frame), newDir.getAngle(dir));
    }

    private BufferedImage getHeadFrame() {
        return Utils.getRotatedImage(Constants.SPRITESHEET.getSprite(3), direction.getAngle());
    }

    private void updateHead() {
        SnakePart head = getHead();
        head.setDirection(direction);
        head.setImage(getHeadFrame());
    }

    public boolean isPointOnTail(Position point) {
        if(point == null)
            throw new IllegalArgumentException("Point cannot be null");

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
        updateSpeed();
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
        direction = Constants.DEFAULT_DIRECTION;
    }

    public Position getPosition() {
        return getHead().getPosition();
    }

    public void setParts(ArrayList<Position> parts) {
        if(parts == null)
            throw new IllegalArgumentException("Parts cannot be null");

        this.parts.clear();
        parts.forEach(part -> this.parts.add(new SnakePart(part)));
        length = parts.size();
    }
    
    public boolean doesCollide(Position point) {
        if(point == null)
            throw new IllegalArgumentException("Point cannot be null");

        for(SnakePart part : parts) {
            if(part.doesCollide(point))
                return true;
        }
        return false;
    }

    public void drawCollider(Graphics2D g) {
        if(g == null)
            throw new IllegalArgumentException("Graphics cannot be null");

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
        if(g == null)
            throw new IllegalArgumentException("Graphics cannot be null");

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
