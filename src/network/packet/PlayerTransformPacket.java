package network.packet;
import java.awt.Point;
import java.util.ArrayList;

import game.player.Direction;
import game.player.NetPlayer;

public class PlayerTransformPacket extends Packet {

    public ArrayList<Point> parts;
    public Direction direction;
 
    public PlayerTransformPacket(int id, ArrayList<Point> parts, Direction direction) {
        super(id);
        this.parts = parts;
        this.direction = direction;
    }

    public PlayerTransformPacket(NetPlayer player) {
        this(player.id, player.snake.parts, player.snake.direction);
    }

}
