package client.panel;
import client.Client;
import client.Page;
import client.UI;
import common.graphics.panel.Panel;
import common.graphics.ui.Button;

public class PausePanel extends Panel {

    private Button exitButton;

    public PausePanel(Button exitButton) {
        super();
        setBackgroundImage(Page.PAUSE.getBackgroundImage());
        this.exitButton = exitButton;
    }

    @Override
    public void setVisible(boolean visible) {
        updateExitButton(visible);
        super.setVisible(visible);
    }

    private void updateExitButton(boolean visible) {
        if (!visible)
            return;

        exitButton.setText(Client.isConnected() ? "Lobiye Dön" : "Ana Menüye Dön");
        exitButton.setAction(e -> UI.MENU.openPage(Client.isConnected() ? Page.LOBBY : Page.MAIN_MENU));
    }
}
