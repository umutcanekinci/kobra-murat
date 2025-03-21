package server;

import java.io.Serializable;
import java.util.ArrayList;

import common.Utils;
import common.Constants;
import common.Direction;
import common.Position;

public class Player implements Serializable {

    public ArrayList<SnakePart> parts = new ArrayList<>();
    private Direction direction = Constants.DEFAULT_DIRECTION;
    public int tailIndex = 0;
    public int length;
    public final Position spawnPoint;
    private int speed;

    public Player(Position spawnPoint) {
        this.spawnPoint = spawnPoint;
        reset();
    }

    public int getSpeed() {
        return speed;
    }

    public void setMap() {
        spawnPoint.setLocation(Tilemap.getSpawnPoint());
    }

    public void reset() {
        setLength(Constants.DEFAULT_LENGTH);
        goSpawnPosition();
        resetDirection();
        updateSpeed();
    }

    private void updateSpeed() {
        speed = Utils.calculateSpeed(length);
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

    public void stepTo(Position position) {
        tailIndex = (tailIndex + 1) % length;

        SnakePart newHead = getHead();
        newHead.setLocation(position);
        newHead.setDirection(direction);
    }

    public Position getNextPosition() {
        return Utils.clampPosition(Utils.moveTowards(getHead(), direction), Tilemap.getCols(), Tilemap.getRows());
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
        updateSpeed();
    }

    public void grow(int amount) {
        for(int i=0; i<amount; i++) {
            parts.add(tailIndex, new SnakePart()); // locating the new part to the head position, also it is snake.tailIndex so it will become new head after move.
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
        parts.forEach(SnakePart::reset);
    }

    public void resetDirection() {
        direction = Constants.DEFAULT_DIRECTION;
    }

    public Position getPosition() {
        return getHead();
    }

    public String toString() {
        StringBuilder str = new StringBuilder(
                        "Length: "    + length                + "\n" +
                        "Direction: " + getDirection().name() + "\n");
        parts.forEach(part -> str.append(part).append(isHead(part) ? " (Head)" : "").append("\n"));

        return str.toString();
    }

}
