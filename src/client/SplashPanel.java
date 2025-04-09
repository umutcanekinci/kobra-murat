package client;

import java.awt.Color;

import common.graphics.Panel;
import common.graphics.SplashEffect;

public class SplashPanel extends Panel {

    public SplashPanel() {
        super();        
        setOpaque(true);
        setBackground(Color.BLACK);
        add(SplashEffect.getInstance(), 0, 0, 1, 1);
    }

    @Override
    public void setVisible(boolean visible) {    
        if (visible)
            SplashEffect.start();
            
        super.setVisible(visible);
    }
}
