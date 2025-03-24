package client;

public class NetPlayer extends Player{

    private final int id;

    public NetPlayer(int id, int defaultLength) {
        super(defaultLength);
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String toString() {
        String info = 
        "Player " + id +
        (isHost()          ? " (Host)" : "") +
        (isCurrentPlayer() ? " (You)"  : "")
        + "\n" + super.toString();
        
        return info;
    }

    public boolean isHost() {
        return id == 0;
    }

    public boolean isCurrentPlayer() {
        return PlayerList.getId() == id;
    }

}
