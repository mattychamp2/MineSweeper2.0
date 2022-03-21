package test;

import model.AbstractTile;
import model.PlayableMinesweeper;

public interface TestableMinesweeper extends PlayableMinesweeper {

    void setWorld(AbstractTile[][] world);
    void open(int x, int y);
    void flag(int x, int y);
    void unflag(int x, int y);

    AbstractTile generateEmptyTile();
    AbstractTile generateExplosiveTile();
}
