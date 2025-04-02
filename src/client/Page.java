package client;

import java.util.HashMap;

public enum Page {
    MAIN_MENU,
    CUSTOMIZE,
    PLAY_MODE, // Singleplayer or Multiplayer
    CONNECT_MODE, // Server or Client
    CONNECT, // Connect
    PAUSE,
    LOBBY,
    GAME;

    private static final HashMap<Page, Page> backPages = new HashMap<>() {{
        put(PLAY_MODE, MAIN_MENU);
        put(CONNECT_MODE, PLAY_MODE);
        put(CONNECT, CONNECT_MODE);
        
        put(GAME, PAUSE);
        put(PAUSE, GAME);
        
    }};

    public Page getBackPage() {
        return backPages.get(this);
    }
}