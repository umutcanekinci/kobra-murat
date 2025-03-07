package network.packet;
import game.player.NetPlayer;
import game.player.Snake;

public class UpdatePlayerPack extends Packet {

    public Snake snake;

    public UpdatePlayerPack(NetPlayer player) {
        super(player.id);
        this.snake = player.snake;
    }
}
