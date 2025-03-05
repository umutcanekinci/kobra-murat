package network.server;

import game.Snake;
import network.Connection;
import packet.UpdatePlayerPack;

public class NetPlayer {

    public int id;
    public Connection connection;
    public String name;
    public Snake snake;

    public NetPlayer(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public void update(UpdatePlayerPack pack) {
        snake = pack.snake;
    }

}
