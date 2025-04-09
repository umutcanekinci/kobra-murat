package common.graphics.panel;

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Color;

import common.graphics.image.BackgroundImage;

public class BackgroundPanel extends JPanel {

    private BackgroundImage backgroundImage = null;

    public BackgroundPanel() {
        super();
        setOpaque(true);
        setBackground(Color.BLACK);
    }

    public void setBackgroundImage(BackgroundImage backgroundImage) {
        if (backgroundImage == null)
            return;

        this.backgroundImage = backgroundImage;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage == null)
            return;
        
        backgroundImage.draw(g, this);
    }

}
