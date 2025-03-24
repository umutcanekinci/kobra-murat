package common.packet;

import common.Position;
import client.Apple;

public class SpawnApplePacket extends Packet {
    private Position position;

    public SpawnApplePacket(Apple apple) {
        this(apple.getPosition());
    }

    public SpawnApplePacket(Position position) {
        this.position = position;
    }

    public Position getPosition() {
        return position;
    }
}
