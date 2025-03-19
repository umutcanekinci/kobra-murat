package common.packet.player;
import java.awt.Point;
import java.util.ArrayList;

import common.Direction;
import common.packet.Packet;

public class UpdateTransformPacket extends Packet {

    public final int tailIndex;
    public final ArrayList<Point> parts = new ArrayList<>();
    public final Direction direction;
 
    @Override
    public String toString() {
        return "PlayerTransformPacket{" +
                "id=" + id +
                ", direction=" + direction +
                '}';
    }


    public UpdateTransformPacket(int id, ArrayList<Point> parts, Direction direction, int tailIndex) {
        super(id);
        this.tailIndex = tailIndex;
        this.parts.addAll(parts);
        this.direction = direction;
    }

    public UpdateTransformPacket(client.NetPlayer player) {
        this(player.getId(), player.getParts(), player.getDirection(), player.tailIndex);
    }

    public UpdateTransformPacket(server.NetPlayer player) {
        this(player.getId(), player.getParts(), player.getDirection(), player.tailIndex);
    }
}
