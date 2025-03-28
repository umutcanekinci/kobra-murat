package common.graphics;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JPanel;

public class Panel extends JPanel {

    private GridBagLayout layout;
    public static GridBagConstraints constraints; // Https://docs.oracle.com/javase/tutorial/uiswing/layout/visual.html#gridbag
    
    public Panel() {
        super();
        layout = new GridBagLayout();
        setLayout(layout);
        setOpaque(false);
        initContraints();
    }

    private static void initContraints() {
        constraints = new GridBagConstraints();
        //constraints.insets = new Insets(50, 0, 0, 0);
        //constraints.fill = GridBagConstraints.HORIZONTAL;
    }

    public static void setInsets(int top, int left, int bottom, int right) {
        constraints.insets = new Insets(top, left, bottom, right);
    }

    public void add(Component comp, int x, int y, int width, int height) {
        
        constraints.gridx = x;
        constraints.gridy = y;
        
        constraints.gridwidth = width;
        constraints.gridheight = height;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        layout.setConstraints(comp, constraints);
        super.add(comp, constraints);   
    }

    @Override
    public void paintComponent(Graphics g) {
        
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

        super.paintComponent(g);
    }
}

