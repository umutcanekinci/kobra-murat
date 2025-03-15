package common.packet.player;
import common.packet.Packet;

public class DisconnectPacket extends Packet {

    public DisconnectPacket(int id) {
        super(id);
    }

    @Override
    public String toString() {
        return "DisconnectPacket{" +
                "id=" + id +
                '}';
    }
}
