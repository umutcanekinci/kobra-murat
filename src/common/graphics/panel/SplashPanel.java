package common.graphics.panel;

import common.graphics.image.SplashImage;

public class SplashPanel extends Panel {

    public SplashPanel() {
        super();        
    }

    @Override
    public void setVisible(boolean visible) {    
        if (visible)
            SplashImage.start();
            
        super.setVisible(visible);
    }
}
