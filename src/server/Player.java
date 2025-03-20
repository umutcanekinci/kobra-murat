package server;

import java.io.Serializable;
import java.util.ArrayList;

import common.Utils;
import common.Direction;
import common.Position;

public class Player implements Serializable {

    public ArrayList<SnakePart> parts = new ArrayList<>();
    private static final Direction DEFAULT_DIRECTION = Direction.RIGHT;
    private Direction direction = DEFAULT_DIRECTION;
    public int tailIndex = 0;
    public int length;
    public final Position spawnPoint;
    public static final int DEFAULT_LENGTH = 12;

    public Player(Position spawnPoint) {
        this.spawnPoint = spawnPoint;
        reset();
    }

    public void setMap() {
        spawnPoint.setLocation(Tilemap.getSpawnPoint());
    }

    public void reset() {
        setLength(DEFAULT_LENGTH);
        goSpawnPosition();
        resetDirection();
    }

    private void goSpawnPosition() {
        if(spawnPoint == null)
            return;

        setPosition(spawnPoint);
    }

    private void setPosition(Position position) {
        tailIndex = 0;
        resetParts();
        getHead().setLocation(position);
    }

    public ArrayList<Position> getParts() {
        ArrayList<Position> points = new ArrayList<>();
        parts.forEach(part -> points.add(part));
        return points;
    }

    public SnakePart getHead() {
        return parts.get((tailIndex - 1 + length) % length);
    }

    public boolean isHead(SnakePart part) {
        return part.equals(getHead());
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
        
        Position position = Utils.clampPosition(Utils.moveTowards(head, direction));

        //if((doesCollide(position) && !isPointOnTail(position)) || Tilemap.doesCollide(position))
        //    return;
            
        tailIndex = (tailIndex + 1) % length;

        SnakePart newHead = getHead();
        newHead.setLocation(position);
    }

    public boolean doesCollide(Position point) {
        for(SnakePart part : parts) {
            if(part.equals(point))
                return true;
        }
        return false;
    }

    public boolean isPointOnTail(Position point) {
        return getTail().equals(point);
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

    public Position getPosition() {
        return getHead();
    }

    public void setParts(ArrayList<Position> parts) {
        this.parts.clear();
        parts.forEach(part -> this.parts.add(new SnakePart(part)));
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
