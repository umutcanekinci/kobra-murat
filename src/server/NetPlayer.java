package server;

import java.util.ArrayList;

import common.Constants;
import common.Position;
import common.Connection;
import common.packet.EatApplePacket;
import common.packet.SpawnApplePacket;
import common.packet.StepPacket;

public class NetPlayer extends Player{

    private static final int speed = 3; // tiles/second
    private static double displacement = 0;
    private Connection connection;
    private final int id;

    public NetPlayer(Connection connection, int id, Position spawnPoint) {
        super(spawnPoint);
        this.connection = connection;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void send(Object packet) {
        connection.sendData(packet);
    }

    public void move() {
        displacement += speed * Constants.DELTATIME;

        if(displacement < 1)
            return;

        displacement = 0;

        StepPacket packet = new StepPacket(id, getDirection());
        PlayerList.playerStep(packet);
        PlayerList.sendToAll(packet);
        collectApples();
    }

    private void collectApples() {
        ArrayList<Position> collectedApples = AppleManager.getCollecteds(this);

        if(collectedApples.isEmpty())
            return;

        AppleManager.removeAll(collectedApples);
        collectedApples.forEach(apple -> PlayerList.sendToAll(new EatApplePacket(id, apple)));
        collectedApples.forEach(apple -> PlayerList.sendToAll(new SpawnApplePacket(AppleManager.spawn())));
        grow(collectedApples.size());
    }

    public void close() {
        if(connection == null)
            return;

        connection.close();
    }

    public String toString() {
        String info = 
        "Player " + id + "\n" +
        super.toString();
        return info;
    }

}
