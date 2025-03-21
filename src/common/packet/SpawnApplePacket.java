package common.packet;

import common.Position;
import java.io.Serializable;

import client.Apple;

public class SpawnApplePacket implements Serializable {
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

    @Override
    public String toString() {
        return super.toString() + "{" + "position= " + position + '}';
    }
}
