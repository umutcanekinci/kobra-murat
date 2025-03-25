package common.packet;

import common.Position;

public class SpawnPacket extends Packet {
    private Position spawnPoint;
    private int length;

    public SpawnPacket(int id, Position spawnPoint, int length) {
        super(id);
        this.spawnPoint = spawnPoint;
        this.length = length;
    }

    public Position getSpawnPoint() {
        return spawnPoint;
    }

    public int getLength() {
        return length;
    }

    @Override
    public String toString() {
        return super.toString() + "{" +
                "spawnPoint=" + spawnPoint +
                ", length=" + length +
            '}';
    }
}
