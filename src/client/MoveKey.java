package client;

import java.awt.event.KeyEvent;

/**
 * Enum representing the keys used for movement in the game.
 * This enum is used to map the keys to their respective key codes.
 * @version 1.0
 */
public enum MoveKey {
    LEFT(KeyEvent.VK_LEFT),
    UP(KeyEvent.VK_UP),
    RIGHT(KeyEvent.VK_RIGHT),
    DOWN(KeyEvent.VK_DOWN),

    A(KeyEvent.VK_A),
    W(KeyEvent.VK_W),
    D(KeyEvent.VK_D),
    S(KeyEvent.VK_S);

    private final int key;

    MoveKey(int key) {
        this.key = key;
    }

    public boolean isEqual(int value) {
        return key == value;
    }
}
