package network.packet.player;

import network.packet.Packet;

public class IdPacket extends Packet {
    public IdPacket(int id) {
        super(id);
    }

    @Override
    public String toString() {
        return "IdPacket{" +
                "id=" + id +
                '}';
    }
}
