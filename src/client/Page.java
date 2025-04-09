package client;

import common.graphics.image.BackgroundImage;
import common.graphics.image.SplashImage;

public enum Page {
    SPLASH,
    MAIN_MENU,
    GAME,
    PLAY_MODE,
    CONNECT_MODE,
    CUSTOMIZE,
    CONNECT,
    PAUSE,
    LOBBY;
    
    public BackgroundImage getBackgroundImage() {
        switch (this) {
            case SPLASH:
                return SplashImage.getInstance();
            case GAME:
                return null;
            case LOBBY:
                return BackgroundImage.LOBBY;
            default:
                return BackgroundImage.MAIN_MENU;
        }
    }
}