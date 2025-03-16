package common.packet.player;
import java.awt.Point;
import java.util.ArrayList;

import client.NetPlayer;
import common.Direction;
import common.packet.Packet;

public class UpdateTransformPacket extends Packet {

    public final int tailIndex;
    public final ArrayList<Point> parts = new ArrayList<>();
    public final Direction direction;
 
    public UpdateTransformPacket(int id, ArrayList<Point> parts, Direction direction, int tailIndex) {
        super(id);
        this.tailIndex = tailIndex;
        this.parts.addAll(parts);
        this.direction = direction;
    }

    public UpdateTransformPacket(NetPlayer player) {
        this(player.getId(), player.getParts(), player.getDirection(), player.tailIndex);
    }

    public UpdateTransformPacket(server.NetPlayer player) {
        this(player.id, player.getParts(), player.getDirection(), player.tailIndex);
    }

    @Override
    public String toString() {
        return "PlayerTransformPacket{" +
                "id=" + id +
                ", direction=" + direction +
                '}';
    }

}
