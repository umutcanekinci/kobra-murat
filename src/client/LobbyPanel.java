package client;

import common.graphics.Image;
import common.graphics.Panel;
import common.graphics.ui.Button;
import server.Server;

public class LobbyPanel extends Panel {

    private Button startButton;

    LobbyPanel(Button startButton) {
        super(Image.LOBBY_BACKGROUND);
        this.startButton = startButton;
    }

    @Override
    public void setVisible(boolean visible) {
        updateStartButton(visible);
        super.setVisible(visible);
    }

    private void updateStartButton(boolean visible) {
        if (!visible)
            return;

        boolean isHost = Server.isRunning();
        boolean isGameStarted = (PlayerList.doesCurrentPlayerExist() && PlayerList.getCurrentPlayer().getHead() != null);
        startButton.setText(isGameStarted ? "Oyuna dön" : isHost ? "Başlat" : "Hazır ol");
        startButton.setAction(e -> {
            if(isGameStarted) {
                Game.start();
                return;
            }

            if (isHost)
                UI.onStartButtonClicked();
            else
                UI.onReadyButtonClicked();
        });
    }

}
