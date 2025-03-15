package network.packet.player;
import game.player.Direction;
import game.player.NetPlayer;
import network.packet.Packet;

public class StepPacket extends Packet {

    public final Direction direction;

    public StepPacket(int id, Direction direction) {
        super(id);
        this.direction = direction;
    }

    public StepPacket(NetPlayer player) {
        this(player.id, player.snake.getDirection());
    }

    @Override
    public String toString() {
        return "StepPacket{" +
                "id=" + id +
                ", direction=" + direction +
                '}';
    }

}
