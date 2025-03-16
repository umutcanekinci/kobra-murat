package common.packet.player;
import java.awt.Point;
import java.util.ArrayList;

import client.NetPlayer;
import common.Direction;
import common.packet.Packet;

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
        this(player.getId(), player.getParts(), player.getDirection(), player.getPosition());
    }

    public UpdateTransformPacket(server.NetPlayer player) {
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
