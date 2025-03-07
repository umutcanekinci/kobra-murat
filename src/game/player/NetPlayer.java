package game.player;
import game.Board;
import network.Connection;
import network.packet.UpdatePlayerPack;
import network.PlayerList;
public class NetPlayer extends Player{

    public Connection connection;
    public int id;

    public NetPlayer(Board board, int id) {
        super(board, new Snake());
        this.id = id;
    }

    public NetPlayer(Board board, int id, Snake snake, Connection connection) {
        this(board, id);
        this.connection = connection;
    }

    public boolean isHost() {
        return id == 0;
    }

    public void update(UpdatePlayerPack pack) {
        snake = pack.snake;
    }

    public String[] getDebugInfo() {
        return new String[] {
                "Player " + id + (isHost() ? " (Host)" : "") + (PlayerList.isCurrentPlayer(this) ? " (You)" : ""),
                "Length: " + snake.length,
                "Direction: " + snake.direction.name(),
                "Position: " + getPos().x + ", " + getPos().y,
        };
    }

}
