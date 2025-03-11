package network.packet.apple;

import java.awt.Point;
import java.io.Serializable;

public class SpawnApplePacket implements Serializable {
    public Point position;

    public SpawnApplePacket(Point position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return "SpawnApplePacket{" +
                "position=" + position +
                '}';
    }
}
