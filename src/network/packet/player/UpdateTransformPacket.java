package network.packet.player;
import java.awt.Point;
import java.util.ArrayList;

import game.player.Direction;
import game.player.NetPlayer;
import game.player.SnakePart;
import network.packet.Packet;

public class UpdateTransformPacket extends Packet {

    public final Point position;
    public final ArrayList<Point> parts = new ArrayList<>();
    public final Direction direction;
 
    public UpdateTransformPacket(int id, ArrayList<SnakePart> parts, Direction direction, Point position) {
        super(id);
        for(Point part : parts) {
            this.parts.add(new Point(part));
        }
        this.direction = direction;
        this.position = new Point(position);
    }

    public UpdateTransformPacket(NetPlayer player) {
        this(player.id, player.snake.parts, player.snake.getDirection(), player.getPos());
    }

    @Override
    public String toString() {
        return "PlayerTransformPacket{" +
                "id=" + id +
                ", direction=" + direction +
                '}';
    }

}
