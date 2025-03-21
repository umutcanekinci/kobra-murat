package common.packet;
import common.Direction;

public class StepPacket extends Packet {

    /*
     * Used by server to notify the clients that a player has moved.
     * Also offline player controller uses this packet to simulate the server.
     */

    public final Direction direction;

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

    @Override
    public String toString() {
        return super.toString() + "{" + "direction=" + direction + '}';
    }
}
