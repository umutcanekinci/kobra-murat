package common.packet;
import java.util.ArrayList;

import common.Direction;
import common.Position;

public class UpdateTransformPacket extends Packet {

    public final int tailIndex;
    public final ArrayList<Position> parts = new ArrayList<>();
    public final Direction direction;
 
    public UpdateTransformPacket(int id, ArrayList<Position> parts, Direction direction, int tailIndex) {
        super(id);
        this.tailIndex = tailIndex;
        this.parts.addAll(parts);
        this.direction = direction;
    }

    public UpdateTransformPacket(client.NetPlayer player) {
        this(player.getId(), player.getParts(), player.getDirection(), player.getTailIndex());
    }

    public UpdateTransformPacket(server.NetPlayer player) {
        this(player.getId(), player.getParts(), player.getDirection(), player.tailIndex);
    }

    @Override
    public String toString() {
        return super.toString() + "{" + "direction=" + direction + '}';
    }
}
