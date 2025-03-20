package common.packet.apple;

import common.Position;
import common.packet.Packet;

public class EatApplePacket extends Packet{
    public Position position;

    public EatApplePacket(int id, Position apple) {
        super(id);
        position = apple;
    }

    public String toString() {
        return "EatApplePacket[" + id + "]: " + "(" + position.x + ", " + position.y + ")";
    }
}
