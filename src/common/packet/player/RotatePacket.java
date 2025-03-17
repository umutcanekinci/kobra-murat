package common.packet.player;

import common.Direction;
import common.packet.Packet;

public class RotatePacket extends Packet {

    public final Direction direction;

    public RotatePacket(int id, Direction direction) {
        super(id);
        this.direction = direction;
    }

}
