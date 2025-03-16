package common.packet.apple;

import java.awt.Point;
import common.packet.Packet;

public class EatApplePacket extends Packet{
    public Point position;

    public EatApplePacket(int id, Point apple) {
        super(id);
        position = apple;
    }

    public String toString() {
        return "EatApplePacket[" + id + "]: " + "(" + position.x + ", " + position.y + ")";
    }
}
