package client.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import javax.swing.Box;

import client.Game;
import client.NetPlayer;
import client.Page;
import client.PlayerList;
import client.PlayerListListener;
import client.UI;

import common.Direction;
import common.Object;
import common.Position;
import common.Utils;
import common.graphics.panel.BorderPanel;
import common.graphics.panel.Panel;
import common.graphics.ui.Button;

import server.Server;

public class LobbyPanel extends BorderPanel {

    private static final Dimension BUTTON_SIZE = new Dimension(450, 150);
    private Button startButton;
    private Button leaveButton;
    private Panel playerPanel;

    public LobbyPanel() {
        super();
        setBackgroundImage(Page.LOBBY.getBackgroundImage());

        add(Box.createVerticalStrut(150), BorderLayout.NORTH);

        // Player panel
        playerPanel = new Panel();
        playerPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 50, 20));
        
        add(playerPanel, BorderLayout.CENTER);

        // Button panel
        Panel buttonPanel = new Panel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 50, 40));
        
        Button customizeButton = new Button("Customize", e -> UI.MENU.openPage(Page.CUSTOMIZE));
        this.leaveButton = new Button("");
        this.startButton = new Button("");

        customizeButton.setFont(customizeButton.getFont().deriveFont(50f));
        leaveButton.setFont(customizeButton.getFont().deriveFont(50f));
        startButton.setFont(customizeButton.getFont().deriveFont(50f));
        
        customizeButton.setPreferredSize(BUTTON_SIZE);
        this.leaveButton.setPreferredSize(BUTTON_SIZE);
        this.startButton.setPreferredSize(BUTTON_SIZE);

        buttonPanel.add(leaveButton);
        buttonPanel.add(Box.createHorizontalStrut(350));
        buttonPanel.add(customizeButton);
        buttonPanel.add(startButton);
        
        add(buttonPanel, BorderLayout.SOUTH);

        PlayerList.addListener(new PlayerListListener() {
            @Override
            public void onPlayerAdded() {
                updatePlayers();
            }

            @Override
            public void onPlayerRemoved() {
                updatePlayers();
            }

            @Override
            public void onPlayerUpdated() {}

            @Override
            public void onPlayerListCleared() {}
        });
    }

    private void updatePlayers() {
        playerPanel.removeAll();
        PlayerList.getPlayers().forEach(player -> updatePlayer(player));
        playerPanel.revalidate();
        playerPanel.repaint();
    }

    private void updatePlayer(NetPlayer player) {
        Panel panel = new Panel() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setFont(getFont().deriveFont(50f));
                Utils.drawText(g2d, "Player " + player.getId(), player.isCurrentPlayer() ? Color.YELLOW : Color.WHITE, new Rectangle(0, 0, getWidth(), 0), true);
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(200, 400);
            }
        };
        panel.add(Box.createVerticalStrut(350), BorderLayout.NORTH);

        Object headObject = new Object();
        headObject.setImage(Utils.getRotatedImage(player.getHeadFrame(), Direction.DOWN.getAngle()));
        panel.add(headObject, BorderLayout.CENTER);
        playerPanel.add(panel);
    }

    @Override
    public void setVisible(boolean visible) {
        updateButtons(visible);
        super.setVisible(visible);
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

        leaveButton.setText(isHost ? "Lobiyi Dağıt" : "Ayrıl");
        leaveButton.setAction(e -> {
            if (isHost) {
                UI.onTerminateClicked();
                return;
            }

            UI.onLeaveButtonClick();
        });
    }

    @Override
    public void paintComponent(java.awt.Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2d = (Graphics2D) g;
        g.setColor(Color.BLACK);
        //5g.setFont(getFont().deriveFont(50f));
        g2d.drawString("Lobby", 100, 100);
    }
}
