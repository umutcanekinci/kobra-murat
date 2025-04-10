package common.graphics.panel;

import common.Constants;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;

public class BorderPanel extends Panel {
    
    private Dimension size;

    public BorderPanel() {
        super();
        setLayout(new BorderLayout());
        size = Constants.DEFAULT_SIZE;
        //setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
    }

    public BorderPanel(int width, int height) {
        this();
        size = new Dimension(width, height);
    }

    @Override
    public Dimension getPreferredSize() {
        if (size == null)
            return Constants.DEFAULT_SIZE;
        
        return size;
    }

    @Override
    public void addComponents(Component[] components) {
        if (components == null)
            throw new IllegalArgumentException("Panel and components cannot be null.");

        if (components.length == 0)
            return;

        for (Component component : components)
            add(component, BorderLayout.CENTER);
    }

}
