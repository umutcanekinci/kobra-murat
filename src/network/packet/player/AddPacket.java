package network.packet.player;

import network.packet.Packet;

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
