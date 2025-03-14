package network.packet.apple;

import java.awt.Point;
import java.io.Serializable;

import game.Apple;

public class EatApplePacket implements Serializable{
    public Point position;

    public EatApplePacket(Apple apple) {
        position = apple.getPosition();
    }
}
