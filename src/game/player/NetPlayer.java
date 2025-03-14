package game.player;
import network.Connection;
import network.PlayerList;

public class NetPlayer extends Player{

    public Connection connection;
    public final int id;

    public NetPlayer(int id) {
        super(new Snake());
        this.id = id;
    }

    public NetPlayer(int id, Snake snake, Connection connection) {
        this(id);
        this.connection = connection;
    }

    public boolean isHost() {
        return id == 0;
    }

    public boolean isCurrentPlayer() {
        return PlayerList.id == id;
    }

    public String toString() {
        String info = 
        "Player " + id +
        (isHost()          ? " (Host)" : "") +
        (isCurrentPlayer() ? " (You)"  : "") + "\n" +
        snake.toString();
        
        return info;
    }

}
