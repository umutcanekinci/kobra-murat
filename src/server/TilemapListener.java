package server;

import java.util.ArrayList;

import common.Position;

public interface  TilemapListener {
    void onMapLoaded(ArrayList<Position> emptyTiles);
}