package client;

import java.awt.Dimension;
import java.awt.Component;
import javax.swing.Box;

import common.graphics.Image;
import common.graphics.Panel;
import common.graphics.ui.Button;
import common.Constants;


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
    
    /*
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
        int totalRows        = windowSize.height / gridSize.height;
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

        add(Box.createVerticalStrut(leftSpace)        , 0                      , 0                                      , leftCols       , totalRows); // Top space
        for(int i=0; i<components.length; i++) {
            add(Box.createHorizontalStrut(topSpace) , 0                        , 0                , leftCols        , componentRows);
            add(components[i]                        , leftCols                  , topRows                , componentCols, componentRows);
            add(Box.createHorizontalStrut(botSpace), leftCols + componentCols    , topRows + componentRows                , rightCols    , componentRows);
        }
        add(Box.createVerticalStrut(rightSpace)        , 0                        , topRows + componentRows*components.length, rightCols       , totalRows); // Bottom space
        setVisible(false);
         
    }
        */

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
