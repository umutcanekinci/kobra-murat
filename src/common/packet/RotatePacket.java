package common.packet;

import common.Direction;

public class RotatePacket extends Packet {

    public final Direction direction;

    public RotatePacket(int id, Direction direction) {
        super(id);
        this.direction = direction;
    }

    public Direction getDirection() {
        return direction;
    }

    @Override
    public String toString() {
        return super.toString() + "{direction=" + direction +'}';
    }
}
