package common.packet;

import common.Position;

public class SpawnPacket extends Packet {
    private Position spawnPoint;

    public SpawnPacket(client.NetPlayer player) {
        this(player.getId(), player.getPosition());
    }

    public SpawnPacket(int id, Position spawnPoint) {
        super(id);
        this.spawnPoint = spawnPoint;
    }

    public Position getSpawnPoint() {
        return spawnPoint;
    }
}
