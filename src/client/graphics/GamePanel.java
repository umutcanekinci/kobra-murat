package client.graphics;

import java.awt.Graphics;
import common.graphics.Panel;

public class GamePanel extends Panel{
    public GamePanel() {
        super();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        //Draw.all((Graphics2D) g, this, Game.isStarted, Game.debugMode, this); // Draw everything
    }
    
}
