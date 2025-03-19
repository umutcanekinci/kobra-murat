package common.packet.player;
import common.Direction;
import common.packet.Packet;

public class StepPacket extends Packet {

    /*
     * Used by server to notify the clients that a player has moved.
     * Also offline player controller uses this packet to simulate the server.
     */

    public final Direction direction;

    @Override
    public String toString() {
        return "StepPacket{" +
                "id=" + id +
                ", direction=" + direction +
                '}';
    }

    public StepPacket(int id, Direction direction) {
        super(id);
        this.direction = direction;
    }

    public StepPacket(client.NetPlayer player) {
        this(player.getId(), player.getDirection());
    }

    public StepPacket(server.NetPlayer player) {
        this(player.getId(), player.getDirection());
    }

}
