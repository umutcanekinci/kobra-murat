package network.packet;
import java.awt.Point;
import java.util.ArrayList;

import game.player.Direction;
import game.player.NetPlayer;

public class PlayerTransformPacket extends Packet {

    public Point position;
    public ArrayList<Point> parts = new ArrayList<>();
    public Direction direction;
 
    public PlayerTransformPacket(int id, ArrayList<Point> parts, Direction direction, Point position) {
        super(id);
        for(Point part : parts) {
            this.parts.add(new Point(part));
        }
        this.direction = direction;
        this.position = new Point(position);
    }

    public PlayerTransformPacket(NetPlayer player) {
        this(player.id, player.snake.parts, player.snake.direction, player.getPos());
    }

    @Override
    public String toString() {
        return "PlayerTransformPacket{" +
                "id=" + id +
                ", direction=" + direction +
                '}';
    }

}
