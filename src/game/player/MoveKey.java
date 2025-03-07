package game.player;

import java.awt.event.KeyEvent;

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
