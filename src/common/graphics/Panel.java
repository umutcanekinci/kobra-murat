package common.graphics;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.util.ArrayList;

import javax.swing.JPanel;

import common.Constants;

public class Panel extends JPanel {

    private GridBagLayout layout;
    public static GridBagConstraints constraints; // Https://docs.oracle.com/javase/tutorial/uiswing/layout/visual.html#gridbag
    public ArrayList<Component> components = new ArrayList<>();
    public Image backgroundImage = null;

    public Panel() {
        super();
        layout = new GridBagLayout();
        setLayout(layout);
        setOpaque(false);
        initContraints();
        setPreferredSize(Constants.DEFAULT_SIZE);
        this.backgroundImage = Image.BACKGROUND;
    }

    public Panel(Image backgroundImage) {
        this();
        this.backgroundImage = backgroundImage;
    }

    private static void initContraints() {
        constraints = new GridBagConstraints();
        constraints.insets = new Insets(20, 0, 20, 0);
        constraints.fill = GridBagConstraints.BOTH;
    }

    public static void setInsets(int top, int left, int bottom, int right) {
        constraints.insets = new Insets(top, left, bottom, right);
    }

    public void add(Component comp, int x, int y, int width, int height) {
        components.add(comp);
        constraints.gridx = x;
        constraints.gridy = y;
        
        constraints.gridwidth = width;
        constraints.gridheight = height;
        layout.setConstraints(comp, constraints);
        super.add(comp, constraints);   
    }

    public void drawBackground(Graphics2D g) {
        if (backgroundImage == null)
            return;

        backgroundImage.draw(g, new Point(), this);
    }

    public void drawColliders(Graphics g) { 
        g.setColor(Color.RED);
        
        int[][] dims = layout.getLayoutDimensions();
        g.setColor(Color.BLUE);
        int x = 0;
        for (int add : dims[0])
        {
            x += add;
            g.drawLine(x, 0, x, getHeight());
        }
        int y = 0;
        for (int add : dims[1])
        {
            y += add;
            g.drawLine(0, y, getWidth(), y);
        }
    }
}

