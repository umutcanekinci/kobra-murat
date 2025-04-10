package editor;

import common.graphics.image.BackgroundImage;

public enum Page {
    MAIN_MENU,
    EDITOR;

    public BackgroundImage getBackgroundImage() {
        switch (this) {
            case MAIN_MENU:
                return BackgroundImage.MAIN_MENU;
            case EDITOR:
                return null;
            default:
                throw new IllegalArgumentException("Unknown page: " + this);
        }
    }
}