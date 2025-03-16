package common.packet.apple;

import java.awt.Point;
import java.io.Serializable;

import client.Apple;

public class SpawnApplePacket implements Serializable {
    public Point position;

    public SpawnApplePacket(Apple apple) {
        this(apple.getPosition());
    }

    public SpawnApplePacket(Point position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return "SpawnApplePacket{" +
                "position= [" + position.x + ", " + position.y + ']' +
                '}';
    }
}
