package common.graphics.panel;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import javax.swing.Box;
import java.awt.Graphics;

import client.DebugLog;
import common.Constants;
import common.graphics.ui.Button;


public class GridBagPanel extends Panel {

    private GridBagLayout layout;
    public static GridBagConstraints constraints; // Https:docs.oracle.com/javase/tutorial/uiswing/layout/visual.html#gridbag
    public ArrayList<Component> components = new ArrayList<>();

    public GridBagPanel() {
        super();
        layout = new GridBagLayout();
        setLayout(layout);
        initContraints();
    }

    private static void initContraints() {
        constraints = new GridBagConstraints();
        constraints.insets = new Insets(20, 0, 20, 0);
        constraints.fill = GridBagConstraints.BOTH;
    }

    public static void setInsets(int top, int left, int bottom, int right) {
        constraints.insets = new Insets(top, left, bottom, right);
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

    @Override
    public Dimension getPreferredSize() {
        return Constants.DEFAULT_SIZE;
    }

    private void add(Component comp, int x, int y, int width, int height) {  
        components.add(comp);
        constraints.gridx = x;
        constraints.gridy = y;
        
        constraints.gridwidth = width;
        constraints.gridheight = height;
        layout.setConstraints(comp, constraints);
        super.add(comp, constraints);   
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (DebugLog.isOn())
            drawColliders(g);
    }

    private void drawColliders(Graphics g) {
        int[][] layoutDimensions = layout.getLayoutDimensions();

        g.setColor(GRID_COLOR);
        
        int x = 0;
        for (int add : layoutDimensions[0])
        {
            x += add;
            g.drawLine(x, 0, x, getHeight());
        }

        int y = 0;
        for (int add : layoutDimensions[1])
        {
            y += add;
            g.drawLine(0, y, getWidth(), y);
        }
            
    }

}

