package client.panel;

import client.Page;
import common.Constants;
import common.graphics.panel.GridBagPanel;
import common.graphics.ui.TextField;

public class ConnectPanel extends GridBagPanel {

    private TextField hostField;
    private TextField portField;

    public ConnectPanel(TextField hostField, TextField portField) {
        super();
        setBackgroundImage(Page.PAUSE.getBackgroundImage());
        this.hostField = hostField;
        this.portField = portField;
    }
}
