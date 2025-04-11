package common.packet;
import java.util.ArrayList;
import java.util.List;

import common.Direction;
import common.Position;

public class UpdateTransformPacket extends Packet {

    public final int tailIndex;
    public final Direction direction;
    private final List<Position> parts = new ArrayList<>();
 
    public UpdateTransformPacket(int id, List<Position> parts, Direction direction, int tailIndex) {
        super(id);
        this.tailIndex = tailIndex;
        this.parts.addAll(parts);
        this.direction = direction;
    }

    public UpdateTransformPacket(client.NetPlayer player) {
        this(player.getId(), player.getParts(), player.getDirection(), player.getTailIndex());
    }

    public UpdateTransformPacket(server.NetPlayer player) {
        this(player.getId(), player.getParts(), player.getDirection(), player.getTailIndex());
    }

    public int getTailIndex() {
        return tailIndex;
    }

    public List<Position> getParts() {
        return parts;
    }

    public Direction getDirection() {
        return direction;
    }

    @Override
    public String toString() {
        return super.toString() + "{" + "direction=" + direction + 
                ", tailIndex=" + tailIndex +
        '}';
    }
}
