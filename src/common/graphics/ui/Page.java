package common.graphics.ui;

import java.awt.Graphics;
import java.awt.GridBagLayout;

import javax.swing.JPanel;

import common.graphics.Image;

public class Page extends JPanel {

    private static final GridBagLayout layout = new GridBagLayout();
    private Image background;

    public Page(Image background) {
        super(layout);
        this.background = background;   
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(background.get(), 0, 0, null);
    }

}
