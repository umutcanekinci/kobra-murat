package network.server;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;

import game.Board;
import game.Utils;
import network.server.map.Tilemap;
import game.player.Direction;

public class Snake implements Serializable {

    public ArrayList<SnakePart> parts = new ArrayList<>();
    private static final Direction DEFAULT_DIRECTION = Direction.RIGHT;
    private Direction direction = DEFAULT_DIRECTION;
    public int tailIndex = 0;
    public int length;

    Snake() {
        grow(1);
    }

    public ArrayList<Point> getParts() {
        ArrayList<Point> points = new ArrayList<>();
        parts.forEach(part -> points.add(part.getPosition()));
        return points;
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
            System.out.println((doesCollide(position) && !isPointOnTail(position)) + " " + Tilemap.doesCollide(position));
            return;
        }
            
        tailIndex = (tailIndex + 1) % length;

        SnakePart newHead = getHead();
        newHead.setPosition(position);
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
