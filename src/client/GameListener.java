package client;

import common.Direction;

/**
 * Interface for listening to game events.
 * This interface is used to notify when the direction changes or when the window is ready.
 * @version 1.0
 */
public interface GameListener {
    void onDirectionChanged(Direction direction);
    void onWindowReady();
}
