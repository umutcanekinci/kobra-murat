package common.packet;

import common.Position;

public class EatApplePacket extends Packet{
    private Position position;

    public EatApplePacket(int id, Position apple) {
        super(id);
        position = apple;
    }

    public Position getPosition() {
        return position;
    }

    public String toString() {
        return super.toString() + ": " + position;
    }
}
