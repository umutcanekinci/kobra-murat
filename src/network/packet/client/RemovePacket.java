package network.packet.client;

import network.packet.Packet;

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
