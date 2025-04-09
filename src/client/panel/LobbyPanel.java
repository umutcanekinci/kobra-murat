package client.panel;

import java.awt.Dimension;
import java.awt.Component;
import javax.swing.Box;

import client.Game;
import client.Page;
import client.PlayerList;
import client.UI;
import common.graphics.panel.Panel;
import common.graphics.ui.Button;
import common.Constants;


import server.Server;

public class LobbyPanel extends Panel {

    private Button startButton;
    private Button leaveButton;

    public LobbyPanel() {
        super();
        setBackgroundImage(Page.LOBBY.getBackgroundImage());

        this.startButton = new Button("");
        this.leaveButton = new Button("");

        addComponents(new Component[] {
            startButton,
            leaveButton
        });
    }

    @Override
    public void setVisible(boolean visible) {
        updateButtons(visible);
        super.setVisible(visible);
    }

    @Override
    public void addComponents(Component[] components) {
        if(components == null)
            throw new IllegalArgumentException("Panel and components cannot be null.");

        if(components.length == 0)
            return;

        Dimension gridSize      = Constants.GRID_SIZE;
        Dimension componentSize = new Dimension(Button.SIZE.width, Button.SIZE.height*2);
        Dimension windowSize    = Constants.DEFAULT_SIZE;

        int totalCols        = windowSize.width / gridSize.width;
        int componentRows    = componentSize.height / gridSize.height;
        int componentCols    = componentSize.width / gridSize.width;

        int leftCols = 10;
        int leftSpace = leftCols * gridSize.width;
        int rightSpace = windowSize.width - componentSize.width - leftSpace;
        int rightCols = rightSpace / gridSize.width;

        int botRows = 30 - (components.length - 1) * componentRows;
        int botSpace = botRows * gridSize.height;
        int topSpace = windowSize.height - componentSize.height * components.length - botSpace;
        int topRows = topSpace / gridSize.height;

        add(Box.createVerticalStrut(topSpace)        , 0                        , 0                                      , totalCols       , topRows); // Top space
        for(int i=0; i<components.length; i++) {
            add(Box.createHorizontalStrut(leftSpace) , 0                        , topRows + (componentRows)*i                , leftCols        , componentRows);
            add(components[i]                        , leftCols                   , topRows + (componentRows)*i                , componentCols, componentRows);
            add(Box.createHorizontalStrut(rightSpace), leftCols + componentCols, topRows + (componentRows)*i                , rightCols    , componentRows);
        }
        add(Box.createVerticalStrut(botSpace)        , 0                        , topRows + componentRows*components.length, totalCols       , botRows); // Bottom space
    }

    private void updateButtons(boolean visible) {
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

        leaveButton.setText(isHost ? "Oyunu İptal Et" : "Ayrıl");
        leaveButton.setAction(e -> {
            if (isHost) {
                UI.onTerminateClicked();
                return;
            }

            UI.onLeaveButtonClick();
        });
    }

}
