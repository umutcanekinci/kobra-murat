package common.packet.player;
import client.NetPlayer;
import common.Direction;
import common.packet.Packet;

public class StepPacket extends Packet {

    public final Direction direction;

    public StepPacket(int id, Direction direction) {
        super(id);
        this.direction = direction;
    }

    public StepPacket(NetPlayer player) {
        this(player.getId(), player.snake.getDirection());
    }

    @Override
    public String toString() {
        return "StepPacket{" +
                "id=" + id +
                ", direction=" + direction +
                '}';
    }

}
