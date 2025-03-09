package game.player;
import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;

import game.Board;

public class Snake implements Serializable {

    public static final Point HIDDEN_PART_POSITION = new Point(-1, -1);
    public Direction direction;
    public int tailIndex = 0;
    public ArrayList<Point> parts = new ArrayList<>();
    public int length;

    Snake() {
        this.direction = Direction.RIGHT;
    }

    public boolean isHead(Point point) {
        return point.equals(parts.get(tailIndex));
    }

    public void stepTo(Point position) {

        /*

        I have got two snake.parts position algorithm.
        1. Shift right and add to first -> Move all values in list to the right and update first value to head position.
        2. Circular Buffer -> Insert the head position to the oldest value with using i%length.
        3. LinkedList -> Best way(gpt)

        Algorithm 1 - Worst way
        for(int i=snake.parts.size()-1; i>0; i--) {
            snake.parts.get(i).setLocation(snake.parts.get(i-1));
        }
        snake.parts.getFirst().setLocation(pos);

        */

        parts.get(tailIndex).setLocation(position);
        tailIndex = (tailIndex + 1) % length;

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
            parts.add(tailIndex, new Point(-1, -1)); // locating the new part to the head position, also it is snake.tailIndex so it will become new head after move.
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
        for(Point snakePart : parts) {
            snakePart.setLocation(HIDDEN_PART_POSITION);
        }
    }

    public boolean isHidden(Point point) {
        return point.equals(HIDDEN_PART_POSITION);
    }

    public Point getPosition() {
        return parts.get((tailIndex + 1) % length);
    }

}
