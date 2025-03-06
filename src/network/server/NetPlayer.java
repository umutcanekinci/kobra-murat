package network.server;
import game.Player;
import game.Snake;
import network.Connection;
import packet.UpdatePlayerPack;

public class NetPlayer extends Player{

    public Connection connection;
    public int id;

    public NetPlayer(int id) {
        super(new Snake());
        this.id = id;
    }

    public void update(UpdatePlayerPack pack) {
        snake = pack.snake;
    }

}
