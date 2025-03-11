package network.packet.apple;

import java.awt.Point;
import java.io.Serializable;

public class EatApplePacket implements Serializable{
    public Point position;

    public EatApplePacket(Point position) {
        this.position = position;
    }
}
