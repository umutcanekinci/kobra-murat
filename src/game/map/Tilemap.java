package game.map;

import java.awt.*;

public class Tilemap {

    private int[][] data;
    private Tile[][] tiles;
    private int cols, rows;
    private final Point spawnPoint = new Point(0, 0);

    public Tilemap() {}

    public Tilemap(int[][] mapData) {
        loadMap(mapData);
    }

    public boolean isReady() {
        return tiles != null;
    }

    private void loadMap(int[][] mapData) {

        if(mapData == null)
            return;

        if(mapData.length == 0 || mapData[0].length == 0)
            return;

        data = mapData;
        cols = data[0].length;
        rows = data.length;
        tiles = new Tile[rows][cols];

        for(int row = 0; row < data.length; row++) {
            for(int col = 0; col < data[0].length; col++) {
                tiles[row][col] = new Tile(data[row][col], row, col);

                if(data[row][col] == 2)
                    spawnPoint.setLocation(col, row);
            }
        }
    }

    public Point getSpawnPoint() {
        return spawnPoint;
    }

    public Tile getTile(int row, int col) {

        if(row < 0 || row >= rows || col < 0 || col >= cols)
            return null;

        return tiles[row][col];
    }

    public boolean isCollide(Point position) {
        for(Tile[] row : tiles) {
            for(Tile tile : row) {
                if(tile.isCollide(position))
                    return true;
            }
        }
        return false;
    }

    public void render(Graphics2D renderer) {
        if(tiles == null)
            return;

        if(renderer == null)
            return;

        for (Tile[] row : tiles) {
            for (Tile tile : row) {
                tile.render(renderer);
            }
        }
    }
}
