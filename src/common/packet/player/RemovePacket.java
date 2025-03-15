package common.packet.player;

import common.packet.Packet;

public class RemovePacket extends Packet {
    public RemovePacket(int id) {
        super(id);
    }

    @Override
    public String toString() {
        return "RemovePacket{" +
                "id=" + id +
                '}';
    }
}
