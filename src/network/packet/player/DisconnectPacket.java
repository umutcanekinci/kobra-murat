package network.packet.player;
import network.packet.Packet;

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
