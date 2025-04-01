package client;

public interface UIListener {
    void onConnectButtonClicked(String host, int port);
    void onHostButtonClicked();
    void onStartButtonClicked();
}
