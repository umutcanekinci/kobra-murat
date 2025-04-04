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

        startButton.setText(Server.isRunning() ? "Başlat" : "Hazır ol");
        startButton.setAction(e -> {
            if (Server.isRunning())
                UI.onStartButtonClicked();
            else
                UI.onReadyButtonClicked();
        });
    }

}
