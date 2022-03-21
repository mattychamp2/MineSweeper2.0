package model;

import notifier.ITileStateNotifier;
import test.TestableTile;

public abstract class AbstractTile implements TestableTile {
    protected ITileStateNotifier viewNotifier;
    public abstract boolean open();
    public abstract void flag();
    public abstract void unflag();
    public abstract void toggledFlag();
    public abstract void setSafe();
    public abstract boolean isFlagged();
    public abstract boolean isOpened();
    public abstract void setExplosiveCount(int explosiveCount);
    public abstract int getExplosiveCount();
    public final void setTileNotifier(ITileStateNotifier notifier) {
        this.viewNotifier = notifier;
    }
}
