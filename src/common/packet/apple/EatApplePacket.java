package common.packet.apple;

import java.awt.Point;
import java.io.Serializable;

import client.Apple;

public class EatApplePacket implements Serializable{
    public Point position;

    public EatApplePacket(Apple apple) {
        position = apple.getPosition();
    }
}
