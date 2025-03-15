package common.packet.player;

import common.packet.Packet;

public class AddPacket extends Packet {
    public AddPacket(int id) {
        super(id);
    }

    @Override
    public String toString() {
        return "AddPacket{" +
                "id=" + id +
                '}';
    }
}
