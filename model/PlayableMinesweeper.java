package model;

import notifier.IGameStateNotifier;

public interface PlayableMinesweeper {
    int getWidth();
    int getHeight();
    int getMines();
    boolean getFirstClick();
    void deactivateFirstTileRule();
    void click();
    AbstractTile getTile(int x, int y);
    void startNewGame(Difficulty level);
    void startNewGame(int row, int col, int explosionCount);
    void open(int x, int y);
    void toggleFlag(int x, int y);
    int getFlags();
    void setGameStateNotifier(IGameStateNotifier notifier);
}
