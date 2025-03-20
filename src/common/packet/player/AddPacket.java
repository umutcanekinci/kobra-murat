package common.packet.player;

import common.Position;
import common.packet.Packet;

public class AddPacket extends Packet {

    private int length;
    private Position spawnPoint;

    public AddPacket(int id, int length, Position spawnPoint) {
        super(id);
        this.length = length;
        this.spawnPoint = spawnPoint;
    }

    public int getLength() {
        return length;
    }

    public Position getSpawnPoint() {
        return spawnPoint;
    }

    @Override
    public String toString() {
        return "AddPacket{" +
                "id=" + id +
                ", length=" + length +
                ", spawnPoint=" + spawnPoint +
                '}';
    }
}
