package network.packet;
import game.player.Snake;

public class UpdatePlayerPack extends Packet {

    public Snake snake;

    public UpdatePlayerPack(Snake snake) {
        this.snake = snake;
    }
}
