package client;

public class NetPlayer extends Player{

    private final int id;

    public String toString() {
        String info = 
        "Player " + id +
        (isHost()          ? " (Host)" : "") +
        (isCurrentPlayer() ? " (You)"  : "")
        + "\n" + super.toString();
        
        return info;
    }

    public NetPlayer(int id) {
        super();
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public boolean isHost() {
        return id == 0;
    }

    public boolean isCurrentPlayer() {
        return PlayerList.getId() == id;
    }

}
