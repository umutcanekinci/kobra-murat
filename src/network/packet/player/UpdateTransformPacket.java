package network.packet.player;
import java.awt.Point;
import java.util.ArrayList;

import game.player.Direction;
import game.player.NetPlayer;
import network.packet.Packet;

public class UpdateTransformPacket extends Packet {

    public final Point position;
    public final ArrayList<Point> parts = new ArrayList<>();
    public final Direction direction;
 
    public UpdateTransformPacket(int id, ArrayList<Point> parts, Direction direction, Point position) {
        super(id);
        this.parts.addAll(parts);
        this.direction = direction;
        this.position = new Point(position);
    }

    public UpdateTransformPacket(NetPlayer player) {
        this(player.id, player.snake.getParts(), player.snake.getDirection(), player.getPos());
    }

    public UpdateTransformPacket(network.server.NetPlayer player) {
        this(player.id, player.snake.getParts(), player.snake.getDirection(), player.getPos());
    }

    @Override
    public String toString() {
        return "PlayerTransformPacket{" +
                "id=" + id +
                ", direction=" + direction +
                '}';
    }

}
