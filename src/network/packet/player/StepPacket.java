package network.packet.player;
import game.player.Direction;
import game.player.NetPlayer;
import network.packet.Packet;

import java.awt.Point;

public class StepPacket extends Packet {

    public final Point position;
    public final Direction direction;

    public StepPacket(int id, Point position, Direction direction) {
        super(id);
        this.position = new Point(position);
        this.direction = direction;
    }

    public StepPacket(NetPlayer player) {
        this(player.id, player.getPos(), player.snake.getDirection());
    }

    @Override
    public String toString() {
        return "StepPacket{" +
                "id=" + id +
                ", direction=" + direction +
                ", position=" + position +
                '}';
    }

}
