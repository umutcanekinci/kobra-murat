package client;
public interface PlayerListListener {

    void onPlayerAdded();
    void onPlayerRemoved();
    void onPlayerUpdated();
    void onPlayerListCleared();

}
