package common.graphics.panel;

import javax.swing.JPanel;

import client.DebugLog;

import java.awt.Graphics;
import java.awt.Color;
import java.awt.Component;

import common.Constants;
import common.graphics.image.BackgroundImage;

public class Panel extends JPanel {

    protected static final Color GRID_COLOR = Color.BLUE;
    private BackgroundImage backgroundImage = null;

    public Panel() {
        super();
        setOpaque(false);
        setBackground(Color.BLACK);
    }

    protected void fill() {
        setPreferredSize(Constants.DEFAULT_SIZE);
    }

    public void setBackgroundImage(BackgroundImage backgroundImage) {
        if (backgroundImage == null)
            return;

        this.backgroundImage = backgroundImage;
    }

    public void addComponents(Component[] components) {
        if (components == null)
            throw new IllegalArgumentException("Panel and components cannot be null.");

        if (components.length == 0)
            return;

        for (Component component : components)
            add(component);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage == null)
            return;
        
        backgroundImage.draw(g, this);

        if(DebugLog.isOn())
            drawCollider(g);
    }

    private void drawCollider(Graphics g) {
        g.setColor(GRID_COLOR);
        g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
    }

}
