package client.panel;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import client.AppleManager;
import client.PlayerList;
import client.Tilemap;
import client.UI;
import common.graphics.panel.Panel;

public class GamePanel extends Panel {

    private static final long serialVersionUID = 1L;

    public GamePanel() {
        super();
        setBackground(Color.GRAY);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g.create();

        Tilemap.draw(g2d, this);
        AppleManager.draw(g2d, this);
        PlayerList.draw(g2d, this);            
        UI.drawPlayerBoard(g2d);
    }

}
