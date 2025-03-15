package common.packet.player;

import common.packet.Packet;

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
