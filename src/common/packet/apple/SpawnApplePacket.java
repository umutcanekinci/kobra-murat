package common.packet.apple;

import common.Position;
import java.io.Serializable;

import client.Apple;

public class SpawnApplePacket implements Serializable {
    public Position position;

    public SpawnApplePacket(Apple apple) {
        this(apple.getPosition());
    }

    public SpawnApplePacket(Position position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return "SpawnApplePacket{" +
                "position= [" + position.x + ", " + position.y + ']' +
                '}';
    }
}
