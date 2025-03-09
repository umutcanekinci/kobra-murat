package network.packet.client;

import network.packet.Packet;

public class ServerClosedPacket extends Packet {

    @Override
    public String toString() {
        return "ServerClosedPacket{" +
                "id=" + id +
                '}';
    }
}
